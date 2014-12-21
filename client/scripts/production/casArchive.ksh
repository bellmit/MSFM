#!/usr/bin/ksh
# ---
# Archiving for Client hosts
# ---
# --- Constants --- #
PROG=$( basename $0 )
HOSTNAME=$( hostname )
INFRA_HOME=${HOME%/infra}/infra # valid for fixcas in all environments
EINVAL=22 # Invalid argument

DATED_PATH=$(date +%Y/%m/%d/%A) # e.g.  2010/11/05/Friday
SHORT_DAY=$(date +%a)           # e.g.  Fri
LONG_DAY=$(date +%A)            # e.g.  Friday
TIMESTAMP=$(date +%Y%m%d.%H%M)  # e.g.  20101105.0935
YMD=$(date +%Y%m%d)             # YMD   YearMonthDay    e.g.  20101105
TOD=$(date +%H%M%S)             # TOD   TimeOfDay (including seconds)   e.g.  093127

REMOTE_USER="infra"
FREEZER_HOME="/sbt/test/infra"
PROGLOGDIR=$INFRA_HOME/v2run_dir/tmp
PROGLOGFILE=${PROG%.*}.${TIMESTAMP}.log
GUARDFILE=${PROG%.*}.${YMD}.guard
ERASEFILE=${PROG%.*}.${YMD}.erase
PROGLOG=$PROGLOGDIR/$PROGLOGFILE
PROGGUARD=$PROGLOGDIR/$GUARDFILE
PROGERASE=$PROGLOGDIR/$ERASEFILE
DELAYEDERASE_SCRIPT=$PROGLOGDIR/${PROG%.*}DelayedErase.ksh
DELAYEDERASE_BATCH=${DELAYEDERASE_SCRIPT%.*}.batch
SEMAPHORESCRIPT=$INFRA_HOME/run_dir/bin/RunWithSem.pl

KEEP_LOCAL_FILES="NO"
FIRST_RUN="NO"  
ERASE_LATER="NO"
ERASE_DELAY=300

# note - INFRA,  FIX, CAS, SACAS do not need separate
# _SAVE _ARCHIVE _KEEP _ERASE_LATER settings, but for simplicity's sake will be set below
# note _ERASE_LATER trumps _KEEP - i.e. if the same extension is in _KEEP and _ERASE_LATER
# the result is it is left alone at the time of the run, but scheduled for a later erase

INFRA_FILE_EXT_TO_SAVE="log debug out err dat"
INFRA_FILE_EXT_TO_YESTERDAY="log debug out err"
INFRA_FILE_EXT_TO_KEEP="dat"
INFRA_FILE_EXT_TO_ERASE_LATER=""

FIX_FILE_EXT_TO_SAVE="log rej ratesFile debug out err"
FIX_FILE_EXT_TO_YESTERDAY="log rej ratesFile debug out err"
FIX_FILE_EXT_TO_KEEP="db5 debug"
FIX_FILE_EXT_TO_ERASE_LATER="debug"

CAS_FILE_EXT_TO_SAVE="log debug out err"
CAS_FILE_EXT_TO_YESTERDAY="log debug out err"
CAS_FILE_EXT_TO_KEEP="debug"
CAS_FILE_EXT_TO_ERASE_LATER="debug"

SACAS_FILE_EXT_TO_SAVE="log debug out err"
SACAS_FILE_EXT_TO_YESTERDAY="log debug out err"
SACAS_FILE_EXT_TO_KEEP="debug"
SACAS_FILE_EXT_TO_ERASE_LATER="debug"

# --- Constants --- #

usage() {
    print ""
    print "Usage: $PROG [ -t [ local | remote ] ] [ -T hostname ] [-d] [-k] [-e ] [-E minutes] [-n] [engine list]"
    print "    where -t (if specified) can override setContext settings"
    print "        'local' means to archive client logging on the local host"
    print "        'remote' means to archive client logging to a remote host"
    print "            (requires a '-T hostname' to be specified" 
    print "    where -T (if specified) is an alternate archive host target"
    print "    where -d specifies to run in debug mode"
    print "    where -k specifies to \"keep\" local files"
    print "            (default is to erase or truncate files after remote archving)" 
    print "    where -e specifies to erase local files later"
    print "            (only applies if -k is NOT specified)" 
    print "    where -E specifies the number of minutes to delay local erase"
    print "            (only applies if -k is NOT specified)" 
    print "            (implies -e setting)" 
    print "            (-e without -E defaults to 300 minute delay)"
    print "    where -n specifies to treat this as the first run of the day"
    print ""
    print "    where 'engine list' (if omitted) defaults to host ENGINE_LIST"
    print "    Note: 'engine list' only refers to appropriate  client_types"
    print "          e.g. fixcas"
    print ""
}

myPrint() {
    print -u2 "$1"
    print "$1" >> $PROGLOG
}

readCommandLine() {
    myPrint "\n$PROG: readCommandLine() Start: [$#] $*"
    MY_ARCHIVE_TYPE="" 
    MY_ARCHIVE_HOST=""
    while getopts dkt:T:eE:nh parm ; do
        case $parm in 
            d)  set -x
                myPrint "$PROG: debug on"
                ;;
            t)  if [[ -z $OPTARG ]] || [[ ${OPTARG#-} != $OPTARG ]] ; then
                    myPrint "$PROG: Missing type after -t switch"
                    usage
                    exit $EINVAL
                fi
                MY_ARCHIVE_TYPE=$OPTARG
                if [[ "$MY_ARCHIVE_TYPE" != "local" ]] && [[ "$MY_ARCHIVE_TYPE" != "remote" ]] ; then
                    myPrint "$PROG: Invalid type '$MY_ARCHIVE_TYPE' after -t switch"
                    myPrint "       type must be 'remote' or 'local'"
                    usage
                    exit $EINVAL
                fi
                myPrint "$PROG: $MY_ARCHIVE_TYPE archiving on"
                ;;
            T)  if [[ -z $OPTARG ]] || [[ ${OPTARG#-} != $OPTARG ]] ; then
                    myPrint "$PROG: Missing hostname after -T switch"
                    usage
                    exit $EINVAL
                fi
                MY_ARCHIVE_HOST=$OPTARG
                myPrint "$PROG: remote host: $MY_ARCHIVE_HOST"
                ;;
            k)  KEEP_LOCAL_FILES="YES"
                myPrint "$PROG: Keep Local Files: ON"
                ;;
            e)  ERASE_LATER="YES"
                ;;
            E)  if [[ -z $OPTARG ]] || [[ ${OPTARG#-} != $OPTARG ]] ; then
                    myPrint "$PROG: Missing delay setting after -E switch"
                    usage
                    exit $EINVAL
                fi
                ERASE_LATER="YES"
                DIGITCOUNT="$(echo $OPTARG | sed 's/[[:digit:]]//g')"
                if [[ -z $DIGITCOUNT ]] ; then 
                    myPrint "PROG: -E value not numeric"
                    usage
                    exit $EINVAL
                fi
                ERASE_DELAY=$OPTARG
                ;;
            n)  myPrint "$PROG: -n flag set: FIRST RUN OF DAY behavior requested"
                rm -f $GUARDFILE
                FIRST_RUN="YES"
                # remove previous run logs and guard files
                rm -f $PROGLOGDIR/${PROG%.*}.20*log
                rm -f $PROGLOGDIR/${PROG%.*}.*guard
                rm -f $PROGLOGDIR/${PROG%.*}.*erase
                ;;
            h) usage
                exit 0
                ;;
            \?) usage
                exit 0
                ;;
        esac
    done
    shift $((OPTIND - 1))

    if [[ "$MY_ARCHIVE_TYPE" == "remote" ]] ; then
        if [[ -z $MY_ARCHIVE_HOST ]] ; then 
            myPrint "$PROG: remote archiving selected - no remote host specified"
            usage 
            exit $EINVAL
        fi
    fi
    if [[ ! -z $MY_ARCHIVE_HOST ]] ; then
        if [[ -z $MY_ARCHIVE_TYPE ]] || [[ "$MY_ARCHIVE_TYPE" == "local" ]]; then 
            myPrint "$PROG: remote host specified - remote archiving not selected"
            usage 
            exit $EINVAL
        fi
    fi
    if [[ 0 -eq $# ]] ; then
        MY_ENGINE_LIST=$ENGINE_LIST
    else
        MY_ENGINE_LIST=$*
    fi
    myPrint "$PROG: local host: $HOSTNAME"
    myPrint "$PROG: delayed local erase: $ERASE_LATER"
    if [[ "$ERASE_LATER" == "YES" ]] ; then
        myPrint "$PROG: delayed local erase value: $ERASE_DELAY minutes"
    fi
    myPrint "$PROG: readCommandLine() End: [$#] $*"
}

getFIXengineList() {
    myPrint "\n$PROG: getFIXengineList() [$#] $* Start"
    if [[ "$*" == "$ENGINE_LIST" ]] ; then
        myPrint "$PROG: using default ENGINE_LIST: $ENGINE_LIST"
        myPrint "$PROG: getFIXengineList() [$#] $* End"
        return
    fi
    for eng in $* ; do
        # myPrint "$PROG: validating engine '$eng'"
        unset VALID
        for valid in $ENGINE_LIST ; do
           if [[ "$eng" == "$valid" ]] ; then 
               # myPrint "$PROG: engine $eng valid"
               VALID=1
           fi
        done
        if [[ -z $VALID ]] ; then
            myPrint "$PROG: engine $eng INVALID"
            exit $EINVAL
        fi
    done
    # myPrint "$PROG: getFIXengineList() [$#] $* valid "
    >/tmp/$PROG.raw.$$
    for eng in $MY_ENGINE_LIST ; do
        echo "$eng" >> /tmp/$PROG.raw.$$
    done
    $(sort -u < /tmp/$PROG.raw.$$ > /tmp/$PROG.u.$$)
    MY_ENGINE_LIST=""
    while read ENGINE
    do
        # myPrint "$PROG: reading unique engine $ENGINE"
        if [[ -z $MY_ENGINE_LIST ]] ; then
            MY_ENGINE_LIST=$ENGINE
        else
            MY_ENGINE_LIST="$MY_ENGINE_LIST $ENGINE"
        fi
    done < /tmp/$PROG.u.$$
    \rm /tmp/$PROG.raw.$$ /tmp/$PROG.u.$$
    myPrint "$PROG: using specified engine list: $MY_ENGINE_LIST"
    myPrint "$PROG: getFIXengineList() [$#] $* End"
}

setupArchive() {
    myPrint "\n$PROG: setupArchive() [$#] $* Start"
    myPrint "$PROG: CLIENT_TYPE: $CLIENT_TYPE"
    # test $INFRA_HOME/config/bin/setContext vars
    case $CLIENT_TYPE in 
        fixcas)
            setupInfraArchive
            getFIXengineList $MY_ENGINE_LIST
            setupFIXArchive $MY_ENGINE_LIST
            ;;
        cas)
            setupInfraArchive
            setupCASArchive 
            ;;
        sacas)
            setupInfraArchive
            setupSACASArchive
            ;;
        mdcas)
            setupMDCASArchive 
            ;;
        mdx)
            setupMDXArchive
            ;;
        cfix)
            setupCFIXArchive
            ;;
        *)
            myPrint "$PROG Unknown CLIENT_TYPE: $CLIENT_TYPE"
            myPrint "\tArchiving aborted"
            exit $EINVAL
            ;;
    esac
    myPrint "$PROG: setupArchive() [$#] $* End"
}

setupInfraArchive() {
    INFRASTART=$(date +%Y_%m_%d.%H:%M:%S)
    myPrint "\n$PROG: setupInfraArchive() [$#] $* Start $INFRASTART"
    export BACKUP_HOST=$MY_ARCHIVE_HOST
    export BACKUP_DIR_BASE=$FREEZER_HOME/Freezer
    export FILE_EXT_TO_SAVE=$INFRA_FILE_EXT_TO_SAVE
    export FILE_EXT_TO_KEEP=$INFRA_FILE_EXT_TO_KEEP
    export FILE_EXT_TO_YESTERDAY=$INFRA_FILE_EXT_TO_YESTERDAY
    export FILE_EXT_TO_ERASE_LATER=$INFRA_FILE_EXT_TO_ERASE_LATER
    SOURCE_DIR_BASE=$RUN_DIR/log
    . $RUN_DIR/bin/setContext 

    # test for override 
    if [[ "$MY_ARCHIVE_TYPE" == "remote" ]] ; then
        myPrint "\tsetting ARCHIVE HOST from $ARCHIVE_HOST to $MY_ARCHIVE_HOST"
        ARCHIVE_HOST=$MY_ARCHIVE_HOST
        ARCHIVE_DIR_BASE=$BACKUP_DIR_BASE
    fi

    if [[ "$ARCHIVE_HOST" == "localhost" ]] ; then
        MY_ARCHIVE_TYPE="local" 
        ARCHIVE_TARGET_DIR=${SOURCE_DIR_BASE}/${LONG_DAY}
    else
        MY_ARCHIVE_TYPE="remote" 
        ARCHIVE_TARGET_DIR=${ARCHIVE_DIR_BASE}/${DATED_PATH}/${HOSTNAME}
    fi
    INFRASTART=$(date +%Y_%m_%d.%H:%M:%S)
    myPrint "$PROG: Infra (RUN_DIR) Archiving Start $INFRASTART"
    myPrint "$PROG: archive from host: $HOSTNAME - location: $SOURCE_DIR_BASE to host: $ARCHIVE_HOST - location: $ARCHIVE_TARGET_DIR"
    doArchive $MY_ARCHIVE_TYPE $HOSTNAME $SOURCE_DIR_BASE $ARCHIVE_HOST $ARCHIVE_TARGET_DIR
    INFRAEND=$(date +%Y_%m_%d.%H:%M:%S)
    myPrint "$PROG: Infra (RUN_DIR) Archiving End $INFRAEND"
    myPrint "$PROG: setupInfraArchive() [$#] $* End $INFRAEND"
}

setupFIXArchive() {
    myPrint "\n$PROG: setupFIXArchive() [$#] $* Start"
    export BACKUP_HOST=$MY_ARCHIVE_HOST
    export BACKUP_DIR_BASE=$FREEZER_HOME/Freezer
    export PROGARCHIVEDIR=${BACKUP_DIR_BASE}/fix/${DATED_PATH}/${HOSTNAME}
    export FILE_EXT_TO_SAVE=$FIX_FILE_EXT_TO_SAVE
    export FILE_EXT_TO_KEEP=$FIX_FILE_EXT_TO_KEEP
    export FILE_EXT_TO_YESTERDAY=$FIX_FILE_EXT_TO_YESTERDAY
    export FILE_EXT_TO_ERASE_LATER=$FIX_FILE_EXT_TO_ERASE_LATER

    for engine in $* ; do
        SOURCE_DIR_BASE=$INFRA_HOME/v2fixcas${engine}/log
        . $INFRA_HOME/v2fixcas${engine}/bin/setContext 

        # test for override 
        if [[ "$MY_ARCHIVE_TYPE" == "remote" ]] ; then
            myPrint "\tsetting ARCHIVE HOST from $ARCHIVE_HOST to $MY_ARCHIVE_HOST"
            ARCHIVE_HOST=$MY_ARCHIVE_HOST
            ARCHIVE_DIR_BASE=$BACKUP_DIR_BASE/fix
        fi

        if [[ "$ARCHIVE_HOST" == "localhost" ]] ; then
            MY_ARCHIVE_TYPE="local" 
            ARCHIVE_TARGET_DIR=${SOURCE_DIR_BASE}/${SHORT_DAY}
        else
            MY_ARCHIVE_TYPE="remote" 
            ARCHIVE_TARGET_DIR=${ARCHIVE_DIR_BASE}/${DATED_PATH}/${HOSTNAME}/fix${engine}
        fi
        FIXSTART=$(date +%Y_%m_%d.%H:%M:%S)
        myPrint "$PROG: FIX ($engine) Archiving Start $FIXSTART"
        myPrint "$PROG: archive from host: $HOSTNAME - location: $SOURCE_DIR_BASE to host: $ARCHIVE_HOST - location: $ARCHIVE_TARGET_DIR"
        doArchive $MY_ARCHIVE_TYPE $HOSTNAME $SOURCE_DIR_BASE $ARCHIVE_HOST $ARCHIVE_TARGET_DIR
        FIXEND=$(date +%Y_%m_%d.%H:%M:%S)
        myPrint "$PROG: FIX ($engine) Archiving End $FIXEND"
    done 
    myPrint "$PROG: setupFIXArchive() [$#] $* End"
}

setupCASArchive() {
    CASSTART=$(date +%Y_%m_%d.%H:%M:%S)
    myPrint "\n$PROG: setupCASArchive() [$#] $* Start $CASSTART"
    export BACKUP_HOST=$MY_ARCHIVE_HOST
    export BACKUP_DIR_BASE=$FREEZER_HOME/Freezer
    export PROGARCHIVEDIR=${BACKUP_DIR_BASE}/cas/${DATED_PATH}/${HOSTNAME}
    export FILE_EXT_TO_SAVE=$CAS_FILE_EXT_TO_SAVE
    export FILE_EXT_TO_KEEP=$CAS_FILE_EXT_TO_KEEP
    export FILE_EXT_TO_YESTERDAY=$CAS_FILE_EXT_TO_YESTERDAY
    export FILE_EXT_TO_ERASE_LATER=$CAS_FILE_EXT_TO_ERASE_LATER
    . $INFRA_HOME/config/bin/setContext
    . $RUN_DIR_LIST/bin/setContext 
    SOURCE_DIR_BASE=$RUN_DIR_LIST/log

    # test for override 
    if [[ "$MY_ARCHIVE_TYPE" == "remote" ]] ; then
        myPrint "\tsetting ARCHIVE HOST from $ARCHIVE_HOST to $MY_ARCHIVE_HOST"
        ARCHIVE_HOST=$MY_ARCHIVE_HOST
        ARCHIVE_DIR_BASE=$BACKUP_DIR_BASE/cas
    fi
    if [[ "$ARCHIVE_HOST" == "localhost" ]] ; then
        MY_ARCHIVE_TYPE="local" 
        ARCHIVE_TARGET_DIR=${SOURCE_DIR_BASE}/${SHORT_DAY}
    else
        MY_ARCHIVE_TYPE="remote" 
        ARCHIVE_TARGET_DIR=${ARCHIVE_DIR_BASE}/${DATED_PATH}/${HOSTNAME}
    fi
    myPrint "$PROG: CAS (v2run_dir) Archiving Start $CASSTART"
    myPrint "$PROG: archive from host: $HOSTNAME - location: $SOURCE_DIR_BASE to host: $ARCHIVE_HOST - location: $ARCHIVE_TARGET_DIR"
    doArchive $MY_ARCHIVE_TYPE $HOSTNAME $SOURCE_DIR_BASE $ARCHIVE_HOST $ARCHIVE_TARGET_DIR
    CASEND=$(date +%Y_%m_%d.%H:%M:%S)
    myPrint "$PROG: CAS (v2run_dir) Archiving End $CASEND"
    myPrint "$PROG: setupCASArchive() [$#] $* End $CASEND"
}

setupSACASArchive() {
    SACASSTART=$(date +%Y_%m_%d.%H:%M:%S)
    myPrint "\n$PROG: setupSACASArchive() [$#] $* Start $SACASSTART"
    export BACKUP_HOST=$MY_ARCHIVE_HOST
    export BACKUP_DIR_BASE=$FREEZER_HOME/Freezer
    export PROGARCHIVEDIR=${BACKUP_DIR_BASE}/sacas/${DATED_PATH}/${HOSTNAME}
    export FILE_EXT_TO_SAVE=$SACAS_FILE_EXT_TO_SAVE
    export FILE_EXT_TO_KEEP=$SACAS_FILE_EXT_TO_KEEP
    export FILE_EXT_TO_YESTERDAY=$SACAS_FILE_EXT_TO_YESTERDAY
    export FILE_EXT_TO_ERASE_LATER=$SACAS_FILE_EXT_TO_ERASE_LATER
    . $INFRA_HOME/config/bin/setContext
    . $RUN_DIR_LIST/bin/setContext 
    SOURCE_DIR_BASE=$RUN_DIR_LIST/log

    if [[ "$ARCHIVE_HOST" == "localhost" ]] ; then
        MY_ARCHIVE_TYPE="local" 
        ARCHIVE_TARGET_DIR=${SOURCE_DIR_BASE}/${SHORT_DAY}
        ARCHIVE_DIR_BASE=$BACKUP_DIR_BASE/sacas
    else
        MY_ARCHIVE_TYPE="remote" 
        ARCHIVE_TARGET_DIR=${ARCHIVE_DIR_BASE}/${DATED_PATH}/${HOSTNAME}
    fi
    myPrint "$PROG: SACAS (v2run_dir) Archiving Start $SACASSTART"
    myPrint "$PROG: archive from host: $HOSTNAME - location: $SOURCE_DIR_BASE to host: $ARCHIVE_HOST - location: $ARCHIVE_TARGET_DIR"
    doArchive $MY_ARCHIVE_TYPE $HOSTNAME $SOURCE_DIR_BASE $ARCHIVE_HOST $ARCHIVE_TARGET_DIR
    SACASEND=$(date +%Y_%m_%d.%H:%M:%S)
    myPrint "$PROG: SACAS (v2run_dir) Archiving End $SACASEND"
    myPrint "$PROG: setupSACASArchive() [$#] $* End $SACASEND"
}

setupMDCASArchive() {
    myPrint "\n$PROG: setupMDCASArchive() [$#] $*"
    myPrint "\tCLIENT_TYPE $CLIENT_TYPE Currently Not Supported"
}

setupMDXArchive() {
    myPrint "\n$PROG: setupMDXArchive() [$#] $*"
    myPrint "\tCLIENT_TYPE $CLIENT_TYPE Currently Not Supported"
}

setupCFIXArchive() {
    myPrint "\n$PROG: setupCFIXArchive() [$#] $*"
    myPrint "\tCLIENT_TYPE $CLIENT_TYPE Currently Not Supported"
}

doArchive() {
    # parms: $MY_ARCHIVE_TYPE $HOSTNAME $SOURCE_DIR_BASE $ARCHIVE_HOST $ARCHIVE_TARGET_DIR
    myPrint "\n$PROG: doArchive() [$#] $* Start"
    # first - deal with "yesterday"
    if [[ ! -d ${3}/yesterday ]] ; then 
        mkdir -p ${3}/yesterday
    fi
    saveToYesterday ${3}
    if [[ "$1" == "local" ]] ; then
        doLocalArchive $3 $5
    else
        doRemoteArchive $3 $4 $5
    fi
    myPrint "$PROG: doArchive() [$#] $* End"
}

saveToYesterday() {
    myPrint "\n$PROG: saveToYesterday() [$#] $* Start"
    typeset Dir=$1
    if [[ "$FIRST_RUN" == "YES" ]] ; then
        myPrint "$PROG: erasing contents of $1/yesterday"
        rm -rf $Dir/yesterday/*
    else
        myPrint "$PROG: adding to contents of $1/yesterday"
    fi
    myPrint "\tSaving $HOSTNAME:$Dir--> $HOSTNAME:$Dir/yesterday"

    for ext in $FILE_EXT_TO_YESTERDAY ; do
        filesInDir=$(ls -1 $Dir)
        for file2test in $filesInDir ; do
            if [[ -f $Dir/$file2test ]] && [[ ! -L $Dir/$file2test ]] ; then
                file2copy=${file2test%.*}.$ext
                if [[ "$file2copy" == "$file2test" ]] ; then 
                    if [[ "$FIRST_RUN" == "YES" ]] ; then
                        myPrint "\t\tSaving $HOSTNAME:$Dir $file2copy --> yesterday"
                        cp -p $Dir/$file2copy $Dir/yesterday/$file2copy
                    else
                        TOD=$(date +%H%M%S) # TOD   TimeOfDay (including seconds)   e.g.  093127
                        myPrint "\t\tSaving $HOSTNAME:$Dir $file2copy --> yesterday/$file2copy.$TOD"
                        cp -p $Dir/$file2copy $Dir/yesterday/$file2copy.$TOD
                    fi
                fi
            fi
        done 
    done
    myPrint "$PROG: saveToYesterday() [$#] $* End"
}

doLocalArchive() {
    myPrint "\n$PROG: doLocalArchive() [$#] $*"
    myPrint "\tNot written yet"
}

doRemoteArchive() {
    myPrint "\n$PROG: doRemoteArchive() [$#] $* Start"
    typeset localDir=$1
    typeset remoteHost=$2
    typeset remoteDir=$3
    REXEC="ssh -l $REMOTE_USER $remoteHost"
    MKDIR="mkdir -p"
    CP="scp -p"
    
    if ! $REXEC $MKDIR $remoteDir ; then
        echo "CANNOT CREATE DIR $remoteDir on $remoteHost"
        exit 1
    fi
    myPrint "\tSaving $HOSTNAME:$localDir --> $remote $remoteHost:$remoteDir"
    for ext in $FILE_EXT_TO_SAVE ; do
        filesInDir=$(ls -1 $localDir)
        for file2test in $filesInDir ; do
            if [[ -f $localDir/$file2test ]] && [[ ! -L $localDir/$file2test ]] ; then
                file2copy=${file2test%.*}.$ext
                if [[ "$file2copy" == "$file2test" ]] ; then 
                    if [[ "$FIRST_RUN" == "YES" ]] ; then
                        myPrint "\t\tSaving $HOSTNAME:$localDir/$file2copy \n\t\t --> $remote $remoteHost:$remoteDir/$file2copy"
                        $CP $localDir/$file2copy $REMOTE_USER@$remoteHost:$remoteDir/$file2copy
                    else
                        TOD=$(date +%H%M%S) # TOD   TimeOfDay (including seconds)   e.g.  093127
                        myPrint "\t\tSaving $HOSTNAME:$localDir/$file2copy \n\t\t --> $remote $remoteHost:$remoteDir/$file2copy.$TOD"
                        $CP $localDir/$file2copy $REMOTE_USER@$remoteHost:$remoteDir/$file2copy.$TOD
                    fi
                fi
            fi
        done 
    done

    myPrint "$PROG: KEEP_LOCAL_FILES: $KEEP_LOCAL_FILES"
    if [[ "$KEEP_LOCAL_FILES" == "YES" ]] ; then
        myPrint "$PROG: KEEP_LOCAL_FILES is set: $localDir not cleaned up"
    else
        cleanupArchive $localDir
    fi
    myPrint "$PROG: doRemoteArchive() [$#] $* End"
}


cleanupArchive() {
    myPrint "\n$PROG: cleanupArchive() [$#] $* Start"
    myPrint "\tFILE_EXT_TO_KEEP: $FILE_EXT_TO_KEEP"
    typeset localDir=$1
    filesInDir=$(ls -1 $localDir)
    if [[ "$FILE_EXT_TO_KEEP" == "" ]] ; then
        myPrint "\tErasing all files in $localDir"
        for file2test in $filesInDir ; do
           if [[ -f $localDir/$file2test ]] ; then
                cleanupFile $localDir/$file2test
           else
                myPrint "\t\t$HOSTNAME:$localDir/$file2test not a file - skipping erase"
           fi
        done
    else
        myPrint "Erasing selected files in $localDir"
        for file2test in $filesInDir ; do
           if [[ -f $localDir/$file2test ]] ; then
                KEEP_FILE="No"
                for ext in $FILE_EXT_TO_KEEP ; do 
                    file2keep=${file2test%%.$ext}.$ext
                    if [[ "$file2keep" == "$file2test" ]] ;then
                        KEEP_FILE="Yes"
                        break;
                    fi
                done
                if [[ "$KEEP_FILE" == "No" ]] ; then 
                    cleanupFile $localDir/$file2test
                else 
                    myPrint "\tKeeping $HOSTNAME:$localDir/$file2test"
                fi
           else
               myPrint "\t\t$HOSTNAME:$localDir/$file2test not a file - skipping erase"
           fi
        done
    fi
    myPrint "\t\t$HOSTNAME:$localDir - cronArchive.log cronArchive.err compatibility"
    touch $localDir/cronArchive.err
    touch $localDir/cronArchive.log
    if [[ "$ERASE_LATER" == "YES" ]] ; then
        if [[ -z $FILE_EXT_TO_ERASE_LATER ]] ; then
            myPrint "\t$HOSTNAME:$localDir delayed erase set - but no delayed erase files specified"
        else
            listDelayedEraseFiles $localDir
        fi
    fi
    myPrint "$PROG: cleanupArchive() [$#] $* End"
}

cleanupFile() {
    typeset localFile=$1
    FUSER_PID=`fuser $localFile`
    if [[ -n $FUSER_PID ]] ; then 
         myPrint "\tTruncating $localFile"
         > $localFile
    fi
    if [[ -z $FUSER_PID ]] ; then 
        myPrint "\tErasing $localFile"
        rm -f $localFile
    fi
}

listDelayedEraseFiles() {
    myPrint "\n$PROG: listDelayedEraseFiles() [$#] $* Start"
    myPrint "\tProcessing $HOSTNAME:$localDir"
    myPrint "\tFILE_EXT_TO_ERASE_LATER: $FILE_EXT_TO_ERASE_LATER"
    touch $PROGERASE
    typeset localFile=$1
    filesInDir=$(ls -1 $localDir)
    for file2test in $filesInDir ; do
       if [[ -f $localDir/$file2test ]] ; then
            ERASE_FILE="No"
            for ext in $FILE_EXT_TO_ERASE_LATER ; do 
                file2erase=${file2test%%.$ext}.$ext
                if [[ "$file2erase" == "$file2test" ]] ;then
                    ERASE_FILE="Yes"
                    break;
                fi
            done
            if [[ "$ERASE_FILE" == "Yes" ]] ; then 
                myPrint "\tListing $HOSTNAME:$localDir/$file2test for delayed erase"
                echo "$localDir/$file2test" >> $PROGERASE
            fi
       else
           myPrint "\t\t$HOSTNAME:$localDir/$file2test not a file - skipping erase"
       fi
    done
    myPrint "$PROG: listDelayedEraseFiles() [$#] $* End"
}

writeEraseScript() {
\rm -f $DELAYEDERASE_SCRIPT
cat >> $DELAYEDERASE_SCRIPT << ERASE_SCRIPT_EOF
#!/usr/bin/ksh
PROG=\$( basename $DELAYEDERASE_SCRIPT )
TIMESTAMP=\$(date +'%m/%d/%Y %T')
INFRAHOME=${HOME%/infra}/infra
DATEDPATH=$(date +%Y/%m/%d/%A)
LOCALHOST=$( hostname )

INPUT=$1
REMOTEUSER=infra
REMOTEHOST=${2:-prdcaslog02}
REMOTEDIR=\$INFRAHOME/Freezer/\$DATEDPATH/\$LOCALHOST
LOGFILE=$1.log

myPrint() {
    print -u2 "(\$PROG \$TIMESTAMP) \$1"
    print "(\$PROG \$TIMESTAMP) \$1" >> \$LOGFILE
}

if [[ -e $1 ]] ; then
    myPrint "Processing $1 Start"
    while read ERASE_TARGET ; do
        FUSER_PID=\`fuser \$ERASE_TARGET\`
        if [[ -n \$FUSER_PID ]] ; then
            myPrint "\tTruncating \$ERASE_TARGET"
            >\$ERASE_TARGET
        else
            myPrint "\tErasing \$ERASE_TARGET"
            rm -f \$ERASE_TARGET
        fi
    done < $1
    myPrint "Processing $1 Done"
else
    myPrint "$1 not found to process"
fi

case $3 in
    fixcas)
        REMOTECLIENTDIR=\$INFRAHOME/Freezer/fix/\$DATEDPATH/\$LOCALHOST
        ;;
    cas)
        REMOTECLIENTDIR=\$INFRAHOME/Freezer/cas/\$DATEDPATH/\$LOCALHOST
        ;;
    *)
        REMOTECLIENTDIR=\$REMOTEDIR
        ;;
esac

SHORTLOG=\$( basename \$LOGFILE )
myPrint "scp -p \$SHORTLOG \$REMOTEUSER@\$REMOTEHOST:\$REMOTEDIR/\$SHORTLOG"
scp -p \$SHORTLOG \$REMOTEUSER@\$REMOTEHOST:\$REMOTEDIR/\$SHORTLOG
myPrint "scp -p \$SHORTLOG \$REMOTEUSER@\$REMOTEHOST:\$REMOTECLIENTDIR/\$SHORTLOG"
scp -p \$SHORTLOG \$REMOTEUSER@\$REMOTEHOST:\$REMOTECLIENTDIR/\$SHORTLOG
ERASE_SCRIPT_EOF
chmod 775 $DELAYEDERASE_SCRIPT
}

scheduleErase () {
\rm -f $DELAYEDERASE_BATCH
cat >> $DELAYEDERASE_BATCH << ERASE_BATCH_EOF
$SEMAPHORESCRIPT $DELAYEDERASE_SCRIPT $PROGERASE $MY_ARCHIVE_HOST 
ERASE_BATCH_EOF
chmod 775 $DELAYEDERASE_BATCH
myPrint "$PROG: Delayed Erase Scheduled for the following files:"
while read ERASE_TARGET ; do
    myPrint "\t$ERASE_TARGET"
done < $PROGERASE

at -f $DELAYEDERASE_BATCH now + $ERASE_DELAY minutes 2>&1|tee -a $PROGLOG
}

# --- main --- #
# --- setup to run from crontab ---#
if [ -z $RUN_DIR ] ; then
    # Running from crontab; run .profile to get definitions
    STARTDIR=`pwd`
    cd
    . ./.profile >/dev/null 2>&1
    cd $STARTDIR
fi
# --- setup to run from crontab ---#

RUN_START=$(date +%Y_%m_%d.%H:%M:%S)
myPrint "$PROG: start $RUN_START"
if [[ ! -e $PROGGUARD ]] ; then 
    myPrint "$PROG: file $PROGGUARD does not exist" 
    FIRST_RUN="YES"
    # remove previous run logs and guard files
    rm -f $PROGLOGDIR/${PROG%.*}.20*log
    rm -f $PROGLOGDIR/${PROG%.*}.*guard
    rm -f $PROGLOGDIR/${PROG%.*}.*erase
else
    myPrint "$PROG: file $PROGGUARD exists"
    FIRST_RUN="NO"
fi
touch $PROGGUARD
myPrint "$PROG: first run today: $FIRST_RUN"

readCommandLine $*

setupArchive 

if [[ -e  $PROGERASE ]] ; then
    chmod +x $PROGERASE
    writeEraseScript $PROGERASE $MY_ARCHIVE_HOST $CLIENT_TYPE
    scheduleErase 
fi

RUN_END=$(date +%Y_%m_%d.%H:%M:%S)
myPrint "\n$PROG: success $RUN_END"

if [[ "$MY_ARCHIVE_TYPE" == "remote" ]] ; then
    scp -p $PROGLOG $REMOTE_USER@$ARCHIVE_HOST:$PROGARCHIVEDIR/$PROGLOGFILE
    scp -p $PROGLOG $REMOTE_USER@$ARCHIVE_HOST:${BACKUP_DIR_BASE}/${DATED_PATH}/${HOSTNAME}/$PROGLOGFILE
fi
