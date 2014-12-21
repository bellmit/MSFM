#!/bin/ksh
# Report on subscriptions made across all MDCAS boxes:
# user, item subscribed for, any duplications
# user, number of MDCASes subscribed to, number of products/classes

# Allow for running from crontab or from command line
if [ -z $RUN_DIR ] ; then
    cd
    . ./.profile >/dev/null 2>&1
    cd -
fi

# -------------------- Configuration --------------------

PW_HOST=prdgc2a  # Host that runs ProcessWatcher
TIMEOUT_SECONDS=3
MAILLIST="lyncht@cboe.com"

# -------------------- Constants --------------------

TMPDIR=$RUN_DIR/tmp/subscriptionStats.`date +%Y%m%d.%H%M%S`.$LOGNAME.$$
PWFILE=$TMPDIR/pwList.log
DATEFORMAT="+%Y-%m-%d %T"
TODAY=`date +%Y%m%d`
RESULTFILE=subscription.$TODAY.tar
SSH="ssh -q -o Batchmode=yes "

# list everything that ProcessWatcher is watching
GETPWLIST="pwadmin -request showCurrentList"

# -------------------- Main Program --------------------

SCRIPTDIR=`dirname $0`
if [ $SCRIPTDIR = "." ] ; then
    SCRIPTDIR=`pwd`
fi

. $SCRIPTDIR/checkHosts
mkdir -p $TMPDIR

date "$DATEFORMAT Getting list from ProcessWatcher"
if [ `hostname` == $PW_HOST ] ; then
    $GETPWLIST
else
    $SSH $PW_HOST ". ~/.profile >/dev/null 2>&1; $GETPWLIST"
fi >$PWFILE

# Scan log files for all mdcas boxes

date "$DATEFORMAT Scanning MDCAS log files"
ALLHOST=`awk '$3 ~ /^mdcas/ {print $3}' <$PWFILE | sort -u`
checkHosts $TIMEOUT_SECONDS
for i in $ALIVEHOST ; do
    $SSH $i grep 'Sub:oid v2mdcas??/log/mdcas*.log' >$TMPDIR/$i.log
done
wait

# Convert log files to data files

date "$DATEFORMAT Processing log extracts"
for i in $ALIVEHOST ; do
    BOXFILE=$TMPDIR/$i.subsc.csv
    USERFILE=$TMPDIR/$i.user.csv
    $SCRIPTDIR/subscription_oneHost.pl <$TMPDIR/$i.log $BOXFILE $USERFILE
done

# Create composite 
date "$DATEFORMAT Creating user summary"
$SCRIPTDIR/subscription_userSummary.pl $TMPDIR/*.user.csv >$TMPDIR/users.subsc.csv

cd $TMPDIR
date "$DATEFORMAT Creating tarfile"
tar cvf $RESULTFILE *.subsc.csv
date "$DATEFORMAT Compressing tarfile"
gzip $RESULTFILE
date "$DATEFORMAT Mailing compressed tarfile"
uuencode $RESULTFILE.gz <$RESULTFILE.gz \
   | mailx -s "Subscription summaries" $MAILLIST

date "$DATEFORMAT Echoing contents of tarfile to log"
for i in *.subsc.csv ; do
    echo ".... FILE $i ...."
    cat $i
done

cd
\rm -rf $TMPDIR
date "$DATEFORMAT Done"
