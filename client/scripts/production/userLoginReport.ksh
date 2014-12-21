#!/bin/ksh
# Report which users logged in to which boxes. Send e-mail and
# update a web-accessible file.

# Allow for running from crontab or from command line
if [ -z $RUN_DIR ] ; then
    STARTDIR=`pwd`
    cd
    . ~/.profile >/dev/null 2>&1
    cd $STARTDIR
fi

# -------------------- Configuration --------------------

WEB_DIR=public_html
TIMEOUT_SECONDS=3
MAILLIST="vliet@cboe.com danaher@cboe.com zakeri@cboe.com hasanm@cboe.com cboedirectclientreports@cboe.com"
if [ $DEBUG -eq 1 ] ; then
   MAILLIST="mageem@cboe.com"
else 
   DEBUG=0
fi

# -------------------- Constants --------------------

TMPDIR=$RUN_DIR/tmp/$LOGNAME.userLoginReport.`date +%Y%m%d_%H%M%S`.$$
REPORTFILE=$TMPDIR/userReport.txt

# Cognizant Added Following variable
SUMMARYRPT=/tmp/summary.report
SUBSRPT=/tmp/subs.report

DATEFORMAT="+%Y-%m-%d %T"
SSH="ssh -q -o Batchmode=yes "
TODAY=$(date +%A)
ReportDate=$(date +%m/%d/%y)

LOGIN_INFRA_5="Authenticated..pwd"
LOGIN_INFRA_13="Authentication..pwd..is.successful"
GREP_LOGIN="egrep '$LOGIN_INFRA_5|$LOGIN_INFRA_13'"

# Get ID from login line, works for Infra 5, Infra 12 and Infra 13
# perl -n -e '/ ([^ "]+)" (\d+ )?$/ && (print $1, "\n");'
EXTRACT_ID="perl -n -e '"'/ ([^ "]+)" (\d+ )?$/ && (print $1, "\n");'"'"

# -------------------- Subroutines --------------------

# Create $host.$engine.$suffix files from input dir/dir/$host.something file
# @param 1 directory prefix such as v2fixcas or v2cfix
# @param 2 path to file, output of grep
# @param 3 suffix for output files
filePerEngine()
{
    engineType_=$1
    inpath_=$2
    suffix_=$3
    indir_=`dirname $inpath_`
    host_=`basename $inpath_`
    host_=${host_%\.[a-z]*}
    engine_list=`cut -d: -f1 <$inpath_ | sed -e 's/.*'$engineType_// | cut -d/ -f1 | sort -u`
    for engine_ in $engine_list ; do
        grep $engineType_$engine_/ $inpath_ | sed -e 's/.*\.log://' >$indir_/$host_.$engine_$suffix_
    done
}

printReportHeader()
{
    echo "host.engine,nAcronyms, user acronyms, connection error"
    FIRST2FIELDS=21 # chars before 2nd comma in header, for alignment
}

printReportLine()
{
    host_=$1
    nAcronyms_=$2
    acronyms_=$3
    err_=$4

    hostchars_=${#host_}
    hostchars_=$(( $hostchars_ + 1 )) # comma after hostname
    countchars_=$(( $FIRST2FIELDS - $hostchars_ ))
    spacecount_=$( printf "%$countchars_"s $nAcronyms_ )
    if [ ! -z "$err_" ] ; then
        acronyms_=", $err_"
    fi
    echo "$host_,$spacecount_, $acronyms_"
}

checkEOP()
{
echo 
echo List of Users with subscription apart from EOP
echo Hostname    User List 
echo =============================================== 
for hostcas in /tmp/*.subs
do
    userlist=""
    for user in `cut -d: -f6 $hostcas|cut -d" " -f1|sort -u`
    do
        count=`grep "Sub:oid for user:$user" $hostcas|grep -c -v ExpectedOpeningPrice`
        if [[ $count -ne 0 ]];then userlist="$userlist $user"; fi
    done
    if [[ -z $userlist ]]; then
        echo "`basename $hostcas .subs`   None" 
    else
        echo "`basename $hostcas .subs`  $userlist"
    fi
done
echo "End of User EOP Report"
echo "=============================================="
echo 
}
# -------------------- Main Program --------------------

SCRIPTDIR=`dirname $0`
if [ $SCRIPTDIR = "." ] ; then
    SCRIPTDIR=`pwd`
fi
mkdir -p $TMPDIR

. $SCRIPTDIR/setenv-scripts
. $SCRIPTDIR/hostsUtils

# Scan log files for all CAS and SACAS boxes
date "$DATEFORMAT Scanning CAS and SACAS log files"
getHosts "cas,sacas" # set ALLHOST to all cas, sacas 
checkHosts $TIMEOUT_SECONDS
ALLDEADHOST=$DEADHOST
for i in $ALIVEHOST ; do
    $SSH $i $GREP_LOGIN 'v2*cas01/log/*cas.log' >$TMPDIR/$i.login &
done
wait

# Scan log files for FIXCAS boxes
date "$DATEFORMAT Scanning FIXCAS log files"
getHosts "fix" # set ALLHOST to all fix
checkHosts $TIMEOUT_SECONDS
ALLDEADHOST="$ALLDEADHOST $DEADHOST"
for i in $ALIVEHOST ; do
    case $i in
    fix2[12])
        # Old FIXCAS disk structure discontinued after 2008.
        HOMEDIR="infra/"
        ;;
    *)
        # Standard disk structure, also for FIXCAS starting in 2009.
        HOMEDIR=
        ;;
    esac
    # Add /dev/null to the search list to force grep to include file name
    # in its output
    $SSH $i $GREP_LOGIN $HOMEDIR'v2fixcas*/log/cas.log /dev/null' >$TMPDIR/$i.fixlog &
done
wait

for i in $TMPDIR/*.fixlog ; do
    filePerEngine v2fixcas $i .login
done

# Scan log files for all MDCAS and MDX boxes
# Produce *.ids file with list of IDs, but no *.login file so that
# these logins don't get counted in the total
date "$DATEFORMAT Scanning MDCAS and MDX log files"
getHosts "mdx,mdcas" # set ALLHOST to all mdx mdcas
checkHosts $TIMEOUT_SECONDS
ALLDEADHOST="$ALLDEADHOST $DEADHOST"
for i in $ALIVEHOST ; do
    $SSH $i >$TMPDIR/$i.ids \
      'grep v2 run_dir/log/*QI.log | cut -d, -f2  | sed -e '"'"'s!@.*!!;s!.*/!!'"'"' | sort -u|tee /tmp/user.ids' 
    $SSH $i >/tmp/$i.subs \
    'fgrep -f /tmp/user.ids /sbt/prod/infra/v2run_dir/log/mdcas.log ' &
done
wait

checkEOP | grep -v prdmdx > $SUBSRPT

# Scan log files for all CFIX engines
date "$DATEFORMAT Scanning CFIX log files"
getHosts "cfix" # set ALLHOST to all cfix
checkHosts $TIMEOUT_SECONDS
ALLDEADHOST="$ALLDEADHOST $DEADHOST"
for i in $ALIVEHOST ; do
    # Add /dev/null to the search list to force grep to include file name
    # in its output
    $SSH $i $GREP_LOGIN 'v2cfix*/log/cfix.log /dev/null' >$TMPDIR/$i.cfixlog &
done
wait

for i in $TMPDIR/*.cfixlog ; do
    filePerEngine v2cfix $i .cfixlogin
done

# Generate report

echo "Today is $ReportDate" > $REPORTFILE
printReportHeader >> $REPORTFILE
for i in $ALLDEADHOST ; do
    eval err='$ERR_'$i
    if [ "$err" = ssh:* ] ; then
        printReportLine "$i" "0" "" "$err" >> $REPORTFILE
    fi
done
for i in $ALLDEADHOST ; do
    eval err='$ERR_'$i
    if [ "$err" != ssh:* ] ; then
        printReportLine "$i" "0" "" "$err" >> $REPORTFILE
    fi
done

for loginFile in $TMPDIR/*.login ; do
    idfile=${loginFile%.login}.ids
    eval $EXTRACT_ID <$loginFile | sort -u >$idfile
done
for loginFile in $TMPDIR/*.cfixlogin ; do
    if [ -e $loginFile ] ; then
        idfile=${loginFile%.cfixlogin}.ids
        eval $EXTRACT_ID <$loginFile | sort -u >$idfile
    fi
done

totalNames=0
for idfile in $TMPDIR/*.ids ; do
    logins=`cat $idfile | tr '\n' ' '`
    logcount=`wc -l $idfile | awk '{print $1}'`
    if [ -f ${idfile%.ids}.login ] ; then
        totalNames=$(( $totalNames + $logcount ))
    fi

    hostname=`basename ${idfile%.ids}`
    printReportLine "$hostname" "$logcount" "$logins" "" >> $REPORTFILE
done
echo >> $REPORTFILE
echo " Total: $totalNames (excluding MDCAS, CFIX and MDX)" >> $REPORTFILE
echo " Produced by script $SCRIPTDIR/`basename $0`" >> $REPORTFILE

# Cognizant Added Following two lines of code

awk -F, -f /sbt/prod/cas/bin/userLoginReport.awk $REPORTFILE > $SUMMARYRPT
echo "Detailed User Report" >> $SUBSRPT 
echo "===================================" >> $SUBSRPT
cat $SUMMARYRPT $SUBSRPT $REPORTFILE > $REPORTFILE.new

mailx -s "[$REPORT_ENVIRONMENT] User Login Report (SACAS, CAS, FIX, MDCAS, MDX)" $MAILLIST <$REPORTFILE.new

if [ $DEBUG -eq 0 ] ; then
    base=`basename $REPORTFILE`
    head=${base%.*}
    tail=${base##*.}
    REMOTE_REPORT_FILE=$head.$REPORT_ENVIRONMENT.$tail
    scp -q $REPORTFILE $USER_WEB@$HOST_WEB:$WEB_DIR/$TODAY/$REMOTE_REPORT_FILE
fi


\rm -rf $TMPDIR
date "$DATEFORMAT Finished"
