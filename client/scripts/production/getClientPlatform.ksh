#!/bin/ksh
# Get Client platform information from /etc/release
# Usage: [ DEBUG=1 ] $0

if [ -z $RUN_DIR ] ; then
    STARTDIR=`pwd`
    cd
    . ~/.profile >/dev/null 2>&1
    cd $STARTDIR
fi

# -------------------- Configuration --------------------

REPORTFILE=$RUN_DIR/tmp/ClientPlatform.txt
if [ -n "$DEBUG" ] ; then
    REPORTFILE=/tmp/ClientPlatform.$$.txt
fi

WEB_DIR=public_html/$( date +%A )

# -------------------- Constants --------------------

SCRIPTDIR=$( dirname $0 )
PROG=$( basename $0 )
HOSTNAME=$( hostname )
DATEFORMAT="+%Y-%m-%d %T"
TIMEOUT_SECONDS=3
SSH="ssh -q -o Batchmode=yes "
TAB="	"

TMPDIR=$RUN_DIR/tmp/$PROG.$$.$USER
CASFILE=$TMPDIR/pwlist.txt

# This is a filename prefix; full name is $PLAT.$host
PLATFILE=$TMPDIR/platform

# -------------------- Main program --------------------

unalias rm
. $SCRIPTDIR/hostsUtils
. $SCRIPTDIR/setenv-scripts
mkdir -m777 -p $TMPDIR

# Get information from every live Client box

getHosts "cas,fix,sacas,mdcas,cfix,mdx" # Set ALLHOST to list of Clients
checkHosts $TIMEOUT_SECONDS

date "$DATEFORMAT Getting platform data from each Client host"
for i in $ALIVEHOST ; do
    $SSH $i "head -1 /etc/release | sed 's/^ *//'" >$PLATFILE.$i &
done
wait

# Produce report

date "$DATEFORMAT Creating report"
date "+%Y-%m-%d  Client host platform data" >$REPORTFILE
echo "" >>$REPORTFILE

for i in $ALIVEHOST ; do
    echo "$i$TAB"$( cat $PLATFILE.$i ) >>$REPORTFILE
done

echo "" >>$REPORTFILE
echo "Produced by script $SCRIPTDIR/$PROG" >>$REPORTFILE

rm -rf $TMPDIR
if [ -z $DEBUG ] ; then  # Don't do this in debugging mode
    base=$( basename $REPORTFILE )
    head=${base%.*}
    tail=${base##*.}
    REMOTE_REPORT_FILE=$head.$REPORT_ENVIRONMENT.$tail
    scp -q $REPORTFILE $USER_WEB@$HOST_WEB:$WEB_DIR/$REMOTE_REPORT_FILE
fi #DEBUG

date "$DATEFORMAT Finished"
