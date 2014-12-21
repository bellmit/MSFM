#!/bin/ksh

FOUND=$( which ping )
if [[ "$FOUND" = no?ping?in* ]] ; then
    . ~/.profile >/dev/null 2>&1
fi
unset FOUND

# -------------------- Configuration --------------------

MAILLIST="lyncht@cboe.com"

# -------------------- Constants --------------------

PROG=$( basename $0 )
NOW=$( date "+%Y%m%d_%H%M%S" )
HOSTNAME=$( hostname )
WORK_DIR=/tmp/$PROG.$NOW.$$
REPORTFILE=$WORK_DIR/report.txt
TMPPWLIST=$WORK_DIR/pwlist.txt

TIMEOUT_SECONDS=3
DATEFORMAT="+%Y-%m-%d %T"

SCRIPTDIR=$( dirname $0 )
cd $SCRIPTDIR
SCRIPTDIR=$( pwd )

# Error codes from errno.h
ENOENT=2    # No such file or directory
EINTR=4     # interrupted system call

# -------------------- Subroutines --------------------

# Set MDXB and MDXC with lists of MDX hosts
getHosts()
{
    # Try to use list from ProcessWatcher
    if [ "$HOSTNAME" = "$HOST_INFRAGC" ] ; then
        pwadmin -request showCurrentList > $TMPPWLIST
    else
        ssh $HOST_INFRAGC -n > $TMPPWLIST \
            '. ~/.profile >/dev/null 2>&1; pwadmin -request showCurrentList'
    fi
    MDXB=$( awk '$1 ~ /^mdx/ {print $3}' < $TMPPWLIST | grep 'b$' | sort -u )
    MDXC=$( awk '$1 ~ /^mdx/ {print $3}' < $TMPPWLIST | grep 'c$' | sort -u )

    if [ -z "$MDXB" ] || [ -z "$MDXC" ] ; then
        print -u2 "$PROG: Insufficient list of MDX hosts. $MDXB $MDXC"
        cleanup
        exit $ENOENT
    fi
}

cleanup()
{
    rm -rf $WORK_DIR
}

cleanupAndExit()
{
    cleanup
    exit $EINTR
}

# -------------------- Main program --------------------

. $SCRIPTDIR/checkHosts
. $SCRIPTDIR/setenv-scripts

trap cleanupAndExit 1 2 3
unalias rm
mkdir -m777 $WORK_DIR

date "$DATEFORMAT Checking MDX B and C hosts"

getHosts
ALLHOST="$MDXB"
checkHosts $TIMEOUT_SECONDS
ALIVE_MDXB="$ALIVEHOST"

ALLHOST="$MDXC"
checkHosts $TIMEOUT_SECONDS
ALIVE_MDXC="$ALIVEHOST"

date "$DATEFORMAT Starting remote jobs"

for host in $ALIVE_MDXB ; do
    ssh $host kstat '"e1000g:4:statistics:Tx Reschedule"' >$WORK_DIR/$host.log 2>&1 &
done

for host in $ALIVE_MDXC ; do
    ssh $host kstat '"e1000g:8:statistics:Tx Reschedule"' >$WORK_DIR/$host.log 2>&1 &
done

wait
date "$DATEFORMAT Finished remote jobs, constructing report"

cd $WORK_DIR
for file in *.log ; do
    host=${file%.log}
    echo ".... $host ...." >> $REPORTFILE
    cat $WORK_DIR/$file >> $REPORTFILE
done
echo "########## Produced by script $SCRIPTDIR/$PROG" >> $REPORTFILE
cd

date "$DATEFORMAT Mailing report"
mailx -s "[$REPORT_ENVIRONMENT] TCP too busy to transmit" $MAILLIST <$REPORTFILE

cleanup
date "$DATEFORMAT $PROG done"
