#!/bin/ksh
# Check a log file for SSH errors, report any we find

if [ -z $RUN_DIR ] ; then
    STARTDIR=`pwd`
    . ~/.profile >/dev/null 2>&1
    cd $STARTDIR
fi

# -------------------- Constants --------------------

PROG=`basename $0`
TMPFILE=$RUN_DIR/tmp/$PROG.`date "+%Y%m%d.%H%M%S"`
RM=/usr/bin/rm
MAILLIST="CBOEDirectClientSupport@cboent.cboe.com"

# -------------------- Main program --------------------

INPUT=$1

LINES=`nl -v0 -ba $INPUT |grep 'Host key verification failed' |awk '{print $1}'`
for i in $LINES ; do
    tail +$i $INPUT | head -1
done | sort -u \
  | sed 's/changed.*/changed/;s/Connecting to/Connect interactively to/;s/\.\.//' >$TMPFILE

if [ -s $TMPFILE ] ; then
    echo "" >> $TMPFILE
    echo 'Please change files in ~'$LOGNAME'/.ssh as noted in '$INPUT \
        >> $TMPFILE
    mailx -s "Need ssh changes on `hostname`" <$TMPFILE $MAILLIST
fi

$RM $TMPFILE
