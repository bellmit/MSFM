#!/bin/ksh

if [ -z $RUN_DIR ] ; then
    STARTDIR=`pwd`
    cd
    . ~/.profile >/dev/null 2>&1
    cd $STARTDIR
fi

SCRIPTDIR=`dirname $0`
if [ $SCRIPTDIR = "." ] ; then
    SCRIPTDIR=`pwd`
fi
# -------------------- Configuration  --------------------

. $SCRIPTDIR/setenv-scripts
. $SCRIPTDIR/hostsUtils

# -------------------- Constants --------------------

DEBUG=${DEBUG:-0}

TMPDIR=$RUN_DIR/tmp
TMPFILE=$TMPDIR/`basename $0`.$$.tmp
STAT_F=$TMPDIR/`basename $0`.stats
MAILLIST="cboedirectclientreports@cboe.com"

getHosts "cas,sacas,fix,mdcas,mdx,cfix" # set ALLHOST 
[[ $DEBUG == 1 ]] && print "ALLHOST :\"$ALLHOST\""

# Cognizant Added Following variable
SUMMARYRPT=$TMPDIR/summary.report


if [ $DEBUG == 1 ] ; then
    # HOSTS=cas0015
    # MAILLIST="simkin@cboe.com"
    MAILLIST="mageem@cboe.com"
fi

# -------------------- Main program --------------------

\rm -f $STAT_F.* >/dev/null 2>&1

echo "host,timestamp,maxUsage" > $TMPFILE
echo '#'`date +%Y-%m-%d`",produced by script `pwd`/`basename $0`" >> $TMPFILE
for cas in $ALLHOST
do
    case $cas in
    fix2[12])
        # Old FIXCAS disk structure discontinued after 2008.
        DIR=infra/run_dir
        ;;
    *)
        # Standard disk structure, also for FIXCAS starting in 2009.
        DIR=run_dir
        ;;
    esac
    ssh $cas "egrep 'pstats|CPU States' $DIR/log/monitorInfra.log" | $SCRIPTDIR/clientCPU_findMax.pl $cas >$STAT_F.$cas &
done
wait

cat $STAT_F.* >> $TMPFILE
\rm -f $STAT_F.*

awk -F, -f /sbt/prod/cas/bin/clientCPU_report.awk $TMPFILE > $SUMMARYRPT
cat $SUMMARYRPT $TMPFILE > $TMPFILE.new
mailx -s "[$REPORT_ENVIRONMENT] CPU usages" <$TMPFILE.new $MAILLIST

scp -q $TMPFILE \
 $USER_WEB@$HOST_WEB:public_html/`date +%A`/clientCPU.$REPORT_ENVIRONMENT.txt

\rm -f $TMPFILE $TMPFILE.new
date "+%Y-%m-%d %H:%M:%S Finished"
