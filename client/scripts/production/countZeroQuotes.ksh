#!/bin/ksh
# Report how many acceptQuote* calls were used to actually remove quotes.
# Send e-mail.

# Allow for running from crontab or from command line
STARTDIR=`pwd`
if [ -z $RUN_DIR ] ; then
    cd
    . ~/.profile >/dev/null 2>&1
    cd $STARTDIR
fi
SCRIPTDIR=$( dirname $0 )
cd $SCRIPTDIR
SCRIPTDIR=$( pwd )
cd $STARTDIR

# -------------------- Configuration --------------------

TIMEOUT_SECONDS=3
MAILLIST="cboedirectclientreports@cboe.com"

# -------------------- Constants --------------------

TMPDIR=$RUN_DIR/tmp/$LOGNAME.countZeroQuotes.`date +%Y%m%d_%H%M%S`.$$
REPORTFILE=$TMPDIR/zeroQuotes.txt

DATEFORMAT="+%Y-%m-%d %T"
SSH="ssh -q -o Batchmode=yes "
TODAY=$( date +%A )
ReportDate=$( date +%Y-%m-%d )

GREP_UNQUOTE="grep 'QuoteCache.*numCancels'"
GZGREP_UNQUOTE="gzgrep 'QuoteCache.*numCancels'"

# -------------------- Subroutines --------------------

setPastDay()
{
    typeset -l day="$1"
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

# Create $host.$engine.$suffix files from input dir/dir/$host.something file
# @param 1 directory prefix such as v2fixcas or v2cfix
# @param 2 path to file, output of grep
# @param 3 suffix for output files
filePerEngine()
{
    typeset engineType_=$1
    typeset inpath_=$2
    typeset suffix_=$3
    typeset indir_=`dirname $inpath_`
    typeset host_=`basename $inpath_`
    host_=${host_%\.[a-z]*}
    typeset engine_list=$( cut -d: -f1 <$inpath_ \
        | sed -e 's/.*'$engineType_// | cut -d/ -f1 | sort -u )
    typeset engine_=
    for engine_ in $engine_list ; do
        grep $engineType_$engine_/ $inpath_ \
        | sed -e 's/.*\.log://' >$indir_/$host_.$engine_$suffix_
    done
}

printReportHeader()
{
    echo "host.engine, nMessages, avgCancels"
}

printReportLine()
{
    typeset engine=$1
    typeset nLines=$2
    typeset avgCancels=$3
    printf "%-11s, %9d, %s\n" $engine $nLines $avgCancels >> $REPORTFILE
}

cleanupAndExit()
{
    cd
    rm -rf $TMPDIR
}

# -------------------- Main Program --------------------

if [ -n "$1" ] ; then
    setPastDay $1
    shift
    PASTDAY=" - $CASDAY"
fi

unalias rm
trap cleanupAndExit 1 2 3
mkdir -p $TMPDIR

. $SCRIPTDIR/setenv-scripts
. $SCRIPTDIR/hostsUtils

# Scan log files for all CAS boxes
date "$DATEFORMAT CAS hosts"
getHosts "cas" # set ALLHOST to all cas
checkHosts $TIMEOUT_SECONDS
date "$DATEFORMAT Scanning CAS log files"
for i in $ALIVEHOST ; do
    if [ -z "$CASDAY" ] ; then
        $SSH $i $GREP_UNQUOTE v2cas01/log/cas.log >$TMPDIR/$i.unquote &
    else
        $SSH $i $GZGREP_UNQUOTE v2cas01/log/$CASDAY/$i/cas.log* >$TMPDIR/$i.unquote &
    fi
done
wait

# Scan log files for FIXCAS boxes
date "$DATEFORMAT FIX hosts"
getHosts "fix" # set ALLHOST to all fix
checkHosts $TIMEOUT_SECONDS
date "$DATEFORMAT Scanning FIXCAS log files"
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
    if [ -z "$FIXDAY" ] ; then
        # Add /dev/null to the search list to force grep to include file name
        # in its output
        $SSH $i $GREP_UNQUOTE $HOMEDIR'v2fixcas*/log/cas.log /dev/null' >$TMPDIR/$i.fixlog &
    else
        $SSH $i $GZGREP_UNQUOTE $HOMEDIR'v2fixcas*/log/$FIXDAY/cas.log* /dev/null' \
        | grep -v /dev/null >$TMPDIR/$i.fixlog &
    fi
done
wait

date "$DATEFORMAT Separating FIXCAS engine results"
for i in $TMPDIR/*.fixlog ; do
    filePerEngine v2fixcas $i .unquote
done

# Generate report

date "$DATEFORMAT Generating report"
echo "# Today is $ReportDate" > $REPORTFILE
printReportHeader >> $REPORTFILE

for quoteFile in $TMPDIR/*.unquote ; do
    typeset engine=${quoteFile%.unquote}
    engine=$( basename $engine )
    integer sum=0
    integer nLines=0
    cat $quoteFile | sed -e 's/.*numCancels://;s/ .*//' | while read line
    do
        integer count=$line
        sum=$sum+$count
        nLines=$nLines+1
    done
    if [ $nLines -ne 0 ] ; then
        # format average with 1 decimal place
        typeset avg=$( perl -e 'printf "%.1f"'", $sum/$nLines" )
    else
        # avoid division by 0, just supply a 0 value
        typeset avg=0
    fi
    printReportLine $engine $nLines $avg
done

echo >> $REPORTFILE
echo "# Produced by script $SCRIPTDIR/`basename $0`" >> $REPORTFILE

mailx -s "[$REPORT_ENVIRONMENT] Quote messages used to cancel quotes$PASTDAY" $MAILLIST <$REPORTFILE

cd
date "$DATEFORMAT Finished"
