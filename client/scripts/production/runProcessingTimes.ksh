#!/bin/ksh
# runProcessingTimes.ksh - generate quote and order info jars for CAS's
# Usage: runProcessingTimes.ksh [ day-of-week ]
#
# set -x 

if [ -z $RUN_DIR ] ; then
    STARTDIR=`pwd`
    cd
    . ~/.profile >/dev/null 2>&1
    cd $STARTDIR
fi
SCRIPTDIR=`dirname $0`

. $SCRIPTDIR/setenv-scripts
. $SCRIPTDIR/hostsUtils

# -------------------- Configuration --------------------

BIGTMP=/sbt/prod/cas/bigtmp

# -------------------- Constants --------------------

export SSH="ssh -q -o Batchmode=yes "
DATEFORMAT="+%Y-%m-%d %T"
TIMEOUT_SECONDS=3
# export TODAY (such as Wednesday), ReportDate (such as 03/25/98)
export TODAY=`date +%A`
export ReportDate=`date +%m/%d/%y`

LOCAL_WORKDIR=/tmp
ORDER_SCRIPT=CASLogReaderOrder.ksh
ORDER_CANCEL_SCRIPT=statsOrderCancel.ksh
QUOTE_SCRIPT=CASLogReaderQuote.ksh
QUOTE_CANCEL_SCRIPT=statsQuoteCancel.ksh
FTP_SCRIPT=$LOCAL_WORKDIR/ftpscript.$$.txt

# Error codes from /usr/include/errno.h
EINVAL=22   # Invalid argument

# -------------------- Subroutines --------------------

setPastDay()
{
    day=$( echo $1 | tr '[A-Z]' '[a-z]' )
    case $day in
    su*)
        CASDAY=Sunday
        FIXDAY=Sun
        ;;
    mo*)
        CASDAY=Monday
        FIXDAY=Mon
        ;;
    tu*)
        CASDAY=Tuesday
        FIXDAY=Tue
        ;;
    we*)
        CASDAY=Wednesday
        FIXDAY=Wed
        ;;
    th*)
        CASDAY=Thursday
        FIXDAY=Thu
        ;;
    fr*)
        CASDAY=Friday
        FIXDAY=Fri
        ;;
    sa*)
        CASDAY=Saturday
        FIXDAY=Sat
        ;;
    *)
        echo "Unknown day-of-week $1"
        exit $EINVAL
        ;;
    esac
    date "$DATEFORMAT Running for past day $CASDAY"
}

setDirectories()
{
    if [ -z "$CASDAY" ] ; then
        JAR_BASEDIR=$BIGTMP
    else
        JAR_BASEDIR=$BIGTMP/$CASDAY
        mkdir -p $JAR_BASEDIR
    fi

    QUOTE_JARDIR=QuoteTimes/data
    ORDER_JARDIR=OrderTimes/data
    QUOTE_CANCEL_JARDIR=QuoteCancelTimes/data
    ORDER_CANCEL_JARDIR=OrderCancelTimes/data
    QUOTE_DATADIR=$JAR_BASEDIR/$QUOTE_JARDIR
    ORDER_DATADIR=$JAR_BASEDIR/$ORDER_JARDIR
    QUOTE_CANCEL_DATADIR=$JAR_BASEDIR/$QUOTE_CANCEL_JARDIR
    ORDER_CANCEL_DATADIR=$JAR_BASEDIR/$ORDER_CANCEL_JARDIR
}

# Set IPATH and FIXCAS based on host name
# @param 1 host name
setIpathFixcas()
{
    case $1 in 
    fix2[12])
        # FIXCAS disk structure discontinued after 2008
        IPATH="/apps/fixcas/prod/infra"
        FIXCAS="yes"
        ;;
    fix*)
        IPATH="/sbt/prod/infra"
        FIXCAS="yes"
        ;;
    *)
        IPATH="/sbt/prod/infra"
        FIXCAS="no"
        ;;
    esac
}

makeScripts()
{
cat >$LOCAL_WORKDIR/$ORDER_SCRIPT << '_ORDER_'
#
# Usage: <script> <cas log file>
#

CASLOG=$1
if [[ $CASLOG = *.Z ]] ; then
    COMPRESS=compress
    uncompress $CASLOG
    CASLOG=${CASLOG%.Z}
elif [[ $CASLOG = *.gz ]] ; then
    COMPRESS=gzip
    gunzip $CASLOG
    CASLOG=${CASLOG%.gz}
fi

#####
# We used to have some hosts logging times in milliseconds
#   # Millisecond input, nanosecond output
#   CVT='sub cvt { my $ms = 0 + shift; return 1000000 * $ms; }'
# Now all hosts log times in nanoseconds
#####

# Nanosecond input, nanosecond output
CVT='sub cvt { my $ns = 0 + shift; return $ns; }'


# Note: userAssignedId (UAID) may contain characters that we interpret as
# field delimiters, so we replace that value in the line we're parsing.
# Note: userAcronym (UA) shows up as part of of sessionId and again right
# after UAID as part of OrderStruct; we don't want that second value so
# we change its label in the line we're parsing.

echo "DAY,TIME,CAS,CAS_PRCS_TIME,USER,CLASS_KEY,PROD_KEY,SESSION,SVR_PRCS_TIME,BRANCH,SEQUENCE,FIRM,ENTITY_ID,TYPE,FIX_TIME"
egrep 'accept[a-zA-Z]*Order.*returning' $CASLOG | \
grep -v OrderCancel | perl -n \
    -e "$CVT" \
    -e 'chomp;' \
    -e 's/ UAID:.* UA:/ UAID:x UAx:/;' \
    -e '@f = split " ";' \
    -e 'm/"(.*)"/ && ($line = $1);' \
    -e '$line =~ s/.*returning //;' \
    -e '%hash = split /[: ]/, $line;' \
    -e '$firm = $hash{'CF'};' \
    -e '$firm = $hash{'EF'} if exists $hash{'EF'}; ' \
    -e '$fixtime = "0"; ' \
    -e '$fixtime = &cvt($hash{'FT'}) if exists $hash{'FT'}; ' \
    -e '$cas = $f[5];' \
    -e '$cas = $f[4] if $f[4] =~ "cas" ; ' \
    -e '$s = join ",", $f[2],$f[3],$cas,&cvt($hash{'TT'}),$hash{'UA'},$hash{'CK'},$hash{'PK'},$hash{'SESN'},&cvt($hash{'ST'}),$hash{'BR'},$hash{'SEQ'},$firm,$hash{'EID'},$hash{'TYPE'},$fixtime;' \
    -e 'print "$s\n";'

if [ -n "$COMPRESS" ] ; then
    $COMPRESS $CASLOG
fi
_ORDER_


cat >$LOCAL_WORKDIR/$ORDER_CANCEL_SCRIPT << '_ORDER_CANCEL_'
#
# Usage: <script> <cas log file>
#

CASLOG=$1
if [[ $CASLOG = *.Z ]] ; then
    COMPRESS=compress
    uncompress $CASLOG
    CASLOG=${CASLOG%.Z}
elif [[ $CASLOG = *.gz ]] ; then
    COMPRESS=gzip
    gunzip $CASLOG
    CASLOG=${CASLOG%.gz}
fi

# Nanosecond input, nanosecond output
CVT='sub cvt { my $ns = 0 + shift; return $ns; }'

# Note: userAssignedId (UAID) may contain characters that we interpret as
# field delimiters, so we replace that value in the line we're parsing.

echo "DAY,TIME,CAS,CAS_PRCS_TIME,USER,CLASS_KEY,PROD_KEY,SESSION,SVR_PRCS_TIME,BRANCH,SEQUENCE,FIRM,ENTITY_ID,TYPE,FIX_TIME"
egrep 'accept[a-zA-Z]*OrderCancel*.*returning' $CASLOG | perl -n \
    -e "$CVT" \
    -e 'chomp;' \
    -e 's/ UAID:.* CF:/ UAID:x CF:/;' \
    -e '@f = split " ";' \
    -e 'm/"(.*)"/ && ($line = $1);' \
    -e '$line =~ s/.*returning //;' \
    -e '%hash = split /[: ]/, $line;' \
    -e '$firm = $hash{'CF'}; ' \
    -e '$firm = $hash{'EF'} if exists $hash{'EF'}; ' \
    -e '$fixtime = "0"; ' \
    -e '$fixtime = &cvt($hash{'FT'}) if exists $hash{'FT'}; ' \
    -e '$cas = $f[5];' \
    -e '$cas = $f[4] if $f[4] =~ "cas" ; ' \
    -e '$s = join ",", $f[2],$f[3],$cas,&cvt($hash{'TT'}),$hash{'UA'},$hash{'CK'},$hash{'PK'},$hash{'SESN'},&cvt($hash{'ST'}),$hash{'BR'},$hash{'SEQ'},$firm,$hash{'EID'},$hash{'TYPE'},$fixtime;' \
    -e 'print "$s\n";'

if [ -n "$COMPRESS" ] ; then
    $COMPRESS $CASLOG
fi
_ORDER_CANCEL_


cat >$LOCAL_WORKDIR/$QUOTE_SCRIPT << '_QUOTE_'
#
# Usage: <script> <cas log file>
#

CASLOG=$1
if [[ $CASLOG = *.Z ]] ; then
    COMPRESS=compress
    uncompress $CASLOG
    CASLOG=${CASLOG%.Z}
elif [[ $CASLOG = *.gz ]] ; then
    COMPRESS=gzip
    gunzip $CASLOG
    CASLOG=${CASLOG%.gz}
fi

# Nanosecond input, nanosecond output
CVT='sub cvt { my $ns = 0 + shift; return $ns; }'

# Note: userAssignedId (UAID) may contain characters that we interpret as
# field delimiters, so we replace that value in the line we're parsing.

echo "DAY,TIME,CAS,CAS_PRCS_TIME,USER,CLASS_KEY,BLK_SZ,SESSION,SVR_PRCS_TIME,CLASSWAIT,CLASSHOLD,CACHEWAIT,CACHEHOLD,ENTITY_ID,FIX_TIME,CCL"
grep 'acceptQuote.*returning' $CASLOG | perl -n \
    -e "$CVT" \
    -e 'chomp;' \
    -e 's/Prodcas..v2//;' \
    -e 's/Prod//;' \
    -e 's/ UAID:.* CK:/ UAID:x CK:/;' \
    -e '@f = split " ";' \
    -e 'm/"(.*)"/ && ($line = $1);' \
    -e '$line =~ s/.*returning //;' \
    -e '%hash = split /[: ]/, $line;' \
    -e '$fixtime = "0"; ' \
    -e '$fixtime = &cvt($hash{'FT'}) if exists $hash{'FT'}; ' \
    -e '$cas = $f[5];' \
    -e '$cas = $f[4] if $f[4] =~ "cas" ; ' \
    -e '$s = join ",", $f[2],$f[3],$cas,&cvt($hash{'TT'}),$hash{'UA'},$hash{'CK'},$hash{'SZ'},$hash{'SESN'},&cvt($hash{'ST'}),&cvt($hash{'ClsLWT'}),&cvt($hash{'ClsLHT'}),&cvt($hash{'CacheLWT'}),&cvt($hash{'CacheLHT'}),$hash{'EID'},$fixtime,$hash{'CCL'};' \
    -e  'print "$s\n";'

if [ -n "$COMPRESS" ] ; then
    $COMPRESS $CASLOG
fi
_QUOTE_


cat >$LOCAL_WORKDIR/$QUOTE_CANCEL_SCRIPT << '_QUOTE_CANCEL_'
#
# Usage: <script> <cas log file>
#

CASLOG=$1
if [[ $CASLOG = *.Z ]] ; then
    COMPRESS=compress
    uncompress $CASLOG
    CASLOG=${CASLOG%.Z}
elif [[ $CASLOG = *.gz ]] ; then
    COMPRESS=gzip
    gunzip $CASLOG
    CASLOG=${CASLOG%.gz}
fi

# Nanosecond input, nanosecond output
CVT='sub cvt { my $ns = 0 + shift; return $ns; }'

# Note: userAssignedId (UAID) may contain characters that we interpret as
# field delimiters, so we replace that value in the line we're parsing.

echo "DAY,TIME,CAS,CAS_PRCS_TIME,USER,CLASS_KEY,BLK_SZ,SESSION,SVR_PRCS_TIME,CLASSWAIT,CLASSHOLD,CACHEWAIT,CACHEHOLD,ENTITY_ID,FIX_TIME"
grep 'cancelQuote.*returning' $CASLOG | perl -n \
    -e "$CVT" \
    -e 'chomp;' \
    -e 's/ UAID:.* UA:/ UAID:x UA:/;' \
    -e '@f = split " ";' \
    -e 'm/"(.*)"/ && ($line = $1);' \
    -e '$line =~ s/.*returning //;' \
    -e '%hash = split /[: ]/, $line;' \
    -e '$blkSz = 1;' \
    -e '$classKey = $hash{'PK'}; ' \
    -e '$classKey = $hash{'CK'} if exists $hash{'CK'}; ' \
    -e '$fixtime = "0"; ' \
    -e '$fixtime = &cvt($hash{'FT'}) if exists $hash{'FT'}; ' \
    -e '$cas = $f[5];' \
    -e '$cas = $f[4] if $f[4] =~ "cas" ; ' \
    -e '$s = join ",", $f[2],$f[3],$cas,&cvt($hash{'TT'}),$hash{'UA'},$classKey,$blkSz,$hash{'SESN'},&cvt($hash{'ST'}),&cvt($hash{'ClsLWT'}),&cvt($hash{'ClsLHT'}),&cvt($hash{'CacheLWT'}),&cvt($hash{'CacheLHT'}),$hash{'EID'},$fixtime;' \
    -e 'print "$s\n";'

if [ -n "$COMPRESS" ] ; then
    $COMPRESS $CASLOG
fi
_QUOTE_CANCEL_
} # makeScripts

removeScripts()
{
    \rm -f $LOCAL_WORKDIR/$ORDER_SCRIPT $LOCAL_WORKDIR/$ORDER_CANCEL_SCRIPT \
           $LOCAL_WORKDIR/$QUOTE_SCRIPT $LOCAL_WORKDIR/$QUOTE_CANCEL_SCRIPT \
           $FTP_SCRIPT
}

# -------------------- Main program --------------------

if [ -n "$1" ] ; then
    setPastDay $1
    shift
fi

setDirectories

makeScripts
mkdir -p $QUOTE_DATADIR $QUOTE_CANCEL_DATADIR
cd $SCRIPTDIR

getHosts "cas,fix" # set ALLHOST to all cas,fix
checkHosts $TIMEOUT_SECONDS
date "$DATEFORMAT Requesting Quote datafiles"
for i in $ALIVEHOST
do 
    echo $i
    setIpathFixcas $i
    REMOTE_WORKDIR=$IPATH/run_dir/tmp

    cat >$FTP_SCRIPT <<!
    cd $IPATH/run_dir/tmp
    lcd $LOCAL_WORKDIR
    put $QUOTE_SCRIPT
    put $QUOTE_CANCEL_SCRIPT
    put $ORDER_SCRIPT
    put $ORDER_CANCEL_SCRIPT
    chmod 777 $QUOTE_SCRIPT
    chmod 777 $QUOTE_CANCEL_SCRIPT
    chmod 777 $ORDER_SCRIPT
    chmod 777 $ORDER_CANCEL_SCRIPT
    bye
!
    sftp -b $FTP_SCRIPT $i
 
    if [ $FIXCAS == "no" ] ; then
        if [ -z "$CASDAY" ] ; then
            LOGFILE="$IPATH/v2cas*/log/cas.log"
        else
            LOGFILE="$IPATH/v2cas*/log/$CASDAY/$i/cas.log*"
        fi
        $SSH $i "$REMOTE_WORKDIR/$QUOTE_SCRIPT $LOGFILE 2>/dev/null" >$QUOTE_DATADIR/${i}.quotes &
    else
        if [ -z "$FIXDAY" ] ; then
            LOGFILE=$IPATH/v2fixcas*/log/cas.log*
        else
            LOGFILE=$IPATH/v2fixcas*/log/$FIXDAY/cas.log*
        fi
        for j in `$SSH $i ls $LOGFILE`
        do
            k=`echo $j | sed -e 's!.*/v2fixcas!!;s!/.*!!;'`
            $SSH $i "$REMOTE_WORKDIR/$QUOTE_SCRIPT $j 2>/dev/null" >$QUOTE_DATADIR/${i}.${k}.quotes &
        done
    fi
done
date "$DATEFORMAT Waiting for Quote datafiles"
wait
date "$DATEFORMAT Created all Quote datafiles"

date "$DATEFORMAT Requesting QuoteCancel datafiles"
for i in $ALIVEHOST
do 
    echo $i
    setIpathFixcas $i
    REMOTE_WORKDIR=$IPATH/run_dir/tmp

    if [ $FIXCAS == "no" ] ; then
        if [ -z "$CASDAY" ] ; then
            LOGFILE="$IPATH/v2cas*/log/cas.log"
        else
            LOGFILE="$IPATH/v2cas*/log/$CASDAY/$i/cas.log*"
        fi
        $SSH $i "$REMOTE_WORKDIR/$QUOTE_CANCEL_SCRIPT $LOGFILE 2>/dev/null" >$QUOTE_CANCEL_DATADIR/${i}.quotecancels &
    else
        if [ -z "$FIXDAY" ] ; then
            LOGFILE=$IPATH/v2fixcas*/log/cas.log*
        else
            LOGFILE=$IPATH/v2fixcas*/log/$FIXDAY/cas.log*
        fi
        for j in `$SSH $i ls $LOGFILE`
        do
            k=`echo $j | sed -e 's!.*/v2fixcas!!;s!/.*!!;'`
            $SSH $i "$REMOTE_WORKDIR/$QUOTE_CANCEL_SCRIPT $j 2>/dev/null" >$QUOTE_CANCEL_DATADIR/${i}.${k}.quotecancels &
        done
    fi
done
date "$DATEFORMAT Waiting for QuoteCancel datafiles"
wait
date "$DATEFORMAT Created all QuoteCancel datafiles"

mkdir -p $ORDER_DATADIR $ORDER_CANCEL_DATADIR
cd $SCRIPTDIR

date "$DATEFORMAT Requesting Order datafiles"
for i in $ALIVEHOST
do 
    echo $i
    setIpathFixcas $i
    REMOTE_WORKDIR=$IPATH/run_dir/tmp

    if [ $FIXCAS == "no" ] ; then
        if [ -z "$CASDAY" ] ; then
            LOGFILE="$IPATH/v2cas*/log/cas.log"
        else
            LOGFILE="$IPATH/v2cas*/log/$CASDAY/$i/cas.log*"
        fi
        $SSH $i "$REMOTE_WORKDIR/$ORDER_SCRIPT $LOGFILE 2>/dev/null" >$ORDER_DATADIR/${i}.orders &
    else
        if [ -z "$FIXDAY" ] ; then
            LOGFILE=$IPATH/v2fixcas*/log/cas.log*
        else
            LOGFILE=$IPATH/v2fixcas*/log/$FIXDAY/cas.log*
        fi
        for j in `$SSH $i ls $LOGFILE`
        do
            k=`echo $j | sed -e 's!.*/v2fixcas!!;s!/.*!!;'`
            $SSH $i "$REMOTE_WORKDIR/$ORDER_SCRIPT $j 2>/dev/null" >$ORDER_DATADIR/${i}.${k}.orders &
        done
    fi
done
date "$DATEFORMAT Waiting for Order datafiles"
wait
date "$DATEFORMAT Created all Order datafiles"

date "$DATEFORMAT Requesting OrderCancel datafiles"
for i in $ALIVEHOST
do 
    echo $i
    setIpathFixcas $i
    REMOTE_WORKDIR=$IPATH/run_dir/tmp

    if [ $FIXCAS == "no" ] ; then
        if [ -z "$CASDAY" ] ; then
            LOGFILE="$IPATH/v2cas*/log/cas.log"
        else
            LOGFILE="$IPATH/v2cas*/log/$CASDAY/$i/cas.log*"
        fi
        $SSH $i "$REMOTE_WORKDIR/$ORDER_CANCEL_SCRIPT $LOGFILE 2>/dev/null" >$ORDER_CANCEL_DATADIR/${i}.ordercancels &
    else
        if [ -z "$FIXDAY" ] ; then
            LOGFILE=$IPATH/v2fixcas*/log/cas.log*
        else
            LOGFILE=$IPATH/v2fixcas*/log/$FIXDAY/cas.log*
        fi
        for j in `$SSH $i ls $LOGFILE`
        do
            k=`echo $j | sed -e 's!.*/v2fixcas!!;s!/.*!!;'`
            $SSH $i "$REMOTE_WORKDIR/$ORDER_CANCEL_SCRIPT $j 2>/dev/null" >$ORDER_CANCEL_DATADIR/${i}.${k}.ordercancels &
        done
    fi
done
date "$DATEFORMAT Waiting for OrderCancel datafiles"
wait
date "$DATEFORMAT Created all OrderCancel datafiles"

#---- casArchiving ----#
getHosts "cas,sacas,fix" # set ALLHOST to all cas,sacas,fix
checkHosts $TIMEOUT_SECONDS
date "$DATEFORMAT Starting  Remote Archiving Scheduling"
let SCHEDULE_SPREAD=30
let INITIAL_DELAY=10
let HOST_COUNT=0
for i in $ALIVEHOST
do 
    echo $i
    setIpathFixcas $i
    let HOST_COUNT=$HOST_COUNT+1
    let LOCAL_DELAY=$HOST_COUNT%$SCHEDULE_SPREAD
    let DELAY=$INITIAL_DELAY+$LOCAL_DELAY    
    $SSH $i "at -f $IPATH/v2run_dir/tmp/casArchive_at.txt now + $DELAY minutes" 
done
date "$DATEFORMAT Completed Remote Archiving Scheduling"
#---- casArchiving ----#

date "$DATEFORMAT Creating jar files"
cd $JAR_BASEDIR
jar cf QuoteTimes.jar $QUOTE_JARDIR
touch QuoteTimes.jar.DONE
date "$DATEFORMAT Created QuoteTimes.jar"
jar cf QuoteCancelTimes.jar $QUOTE_CANCEL_JARDIR
touch QuoteCancelTimes.jar.DONE
date "$DATEFORMAT Created QuoteCancelTimes.jar"
jar cf OrderTimes.jar $ORDER_JARDIR
touch OrderTimes.jar.DONE
date "$DATEFORMAT Created OrderTimes.jar"
jar cf OrderCancelTimes.jar $ORDER_CANCEL_JARDIR
touch OrderCancelTimes.jar.DONE
date "$DATEFORMAT Created OrderCancelTimes.jar"

if [ -n "$CASDAY" ] ; then
    TODAY=$CASDAY
fi
ssh infra@$HOST_DEEPFRZ mkdir -p $DEEPFRZ_TARGET/$TODAY
cat >$FTP_SCRIPT <<!
cd $DEEPFRZ_TARGET/$TODAY
put QuoteTimes.jar 
put QuoteTimes.jar.DONE
put QuoteCancelTimes.jar 
put QuoteCancelTimes.jar.DONE
put OrderTimes.jar 
put OrderTimes.jar.DONE
put OrderCancelTimes.jar 
put OrderCancelTimes.jar.DONE
bye
!
sftp -b $FTP_SCRIPT infra@$HOST_DEEPFRZ

\rm -f $QUOTE_DATADIR/*.quotes $ORDER_DATADIR/*.orders \
       $QUOTE_CANCEL_DATADIR/*.quotecancels \
       $ORDER_CANCEL_DATADIR/*.ordercancels
rmdir -p $QUOTE_JARDIR $QUOTE_CANCEL_JARDIR $ORDER_JARDIR $ORDER_CANCEL_JARDIR
removeScripts
date "$DATEFORMAT Finished"
