#!/bin/ksh
#
# Survey all Client boxes after processes are shut down, to report which
# processes had threads incorrectly still running at end of day.
#

if [ -z $RUN_DIR ] ; then
    STARTDIR=`pwd`
    cd
    . ~/.profile >/dev/null 2>&1
    cd $STARTDIR
fi

# -------------------- Configuration --------------------

DEBUG=${DEBUG-0} # set DEBUG=1 to run a short test

TIMEOUT_SECONDS=3
MAILLIST="CBOEDirectClientSupport@cboent.cboe.com CBOEDirectSupport@cboent.cboe.com"
if [ $DEBUG -eq 1 ] ; then
   MAILLIST="mageem@cboe.com"
fi

# -------------------- Constants --------------------

SCRIPTDIR=`dirname $0`
if [ $SCRIPTDIR = "." ] ; then
    SCRIPTDIR=`pwd`
fi
TODAY=`date +%a`
PROC=`basename $0`
REPORTFILE=/tmp/$PROC.txt
DATEFORMAT="+%Y-%m-%d %T"

# -------------------- Main program --------------------

. $SCRIPTDIR/setenv-scripts
. $SCRIPTDIR/hostsUtils

# get list of all cas, sacas, fix, mdcas, mdx, and cfix hosts
getHosts "cas,sacas,fix,mdcas,mdx,cfix"
checkHosts $TIMEOUT_SECONDS

[[ $DEBUG == 1 ]] && {
    # take lf out of ALLHOST for DEBUG logging
    myAllHost=$(echo $ALLHOST |sed -e 's/\n/ /g')
    print "ALLHOST: \"$myAllHost\""
}
[[ $DEBUG == 1 ]] && print "ALIVEHOST: \"$ALIVEHOST\""

# Create or truncate the output file
> $REPORTFILE
for host in $ALIVEHOST ; do
    case $host in
    fix2[12])
        DIR=infra/run_dir/tmp
        ;;
    *)
        DIR=run_dir/tmp
        ;;
    esac
    ssh $host 'cat '$DIR'/threads.*.'"$TODAY.log 2>/dev/null" >> $REPORTFILE
done

# Make a report if the result file is non-empty
if [ -s $REPORTFILE ] ; then
    echo "#" >> $REPORTFILE
    echo "# Produced by script $0" >> $REPORTFILE
    mailx -s "[$REPORT_ENVIRONMENT] Unfinished threads on Client hosts" $MAILLIST <$REPORTFILE
fi

if [[ $DEBUG == 1 ]] ; then 
    print "keeping REPORTFILE: $REPORTFILE"
else
    \rm -f $REPORTFILE 2>/dev/null
fi
