#!/bin/ksh
# Collect TTE data files for Clients
# Usage: $0 [ Sunday | Monday | ... | Saturday ]
#
# set -x 

if [ -z $RUN_DIR ] ; then
    STARTDIR=$( pwd )
    cd
    . ~/.profile >/dev/null 2>&1
    cd $STARTDIR
fi
SCRIPTDIR=$( dirname $0 )

. $SCRIPTDIR/setenv-scripts
. $SCRIPTDIR/hostsUtils

# -------------------- Configuration --------------------

# Should these definitions be set in setenv-scripts?
USER_DEEPFRZ=infra
USER_DEVSTOR=infra

BIGTMP=/sbt/prod/cas/bigtmp
TARGET_HOST=$HOST_DEVSTOR
TARGET_USER=$USER_DEVSTOR
TARGET_ROOTDIR=/sbt/test/infra/Freezer

# -------------------- Constants --------------------

DATEFORMAT="+%Y-%m-%d %T"
TIMEOUT_SECONDS=3
NOW=$( date +%Y%m%d_%H%M%S )

export TODAY=$( date +%A )              # Example: Wednesday
export WebADate=$( date +%Y/%m/%d )     # Example: 1993/03/25

# Error codes from /usr/include/errno.h
EINVAL=22   # Invalid argument

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

setDirectories()
{
    if [ -z "$CASDAY" ] ; then
        JAR_BASEDIR=$BIGTMP
        CASTTE_TARDIR=CASTTE
        TARGET_DIR=$TARGET_ROOTDIR/$WebADate/$TODAY
    else
        JAR_BASEDIR=$BIGTMP/$CASDAY
        mkdir -p $JAR_BASEDIR
        CASTTE_TARDIR=CASTTE_$NOW
        typeset OtherDate=$( $SCRIPTDIR/prevday $CASDAY )
        TARGET_DIR=$TARGET_ROOTDIR/$OtherDate/$CASDAY
    fi

    CASTTE_DATADIR=$JAR_BASEDIR/$CASTTE_TARDIR
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

# -------------------- Main program --------------------

if [ -n "$1" ] ; then
    setPastDay $1
    shift
fi

unalias rm

setDirectories

cd $SCRIPTDIR

getHosts "cas,fix" # set ALLHOST to all cas,fix
checkHosts $TIMEOUT_SECONDS

## copy TTE files to this host

date "$DATEFORMAT Requesting CASTTE datafiles"
rm -rf $CASTTE_DATADIR
mkdir -p $CASTTE_DATADIR
for i in $ALIVEHOST
do 
    date "$DATEFORMAT ... host $i"
    setIpathFixcas $i
    mkdir -p $CASTTE_DATADIR/$i
    # Directory structure under run_dir/log is the same, FIXCAS or other box
    if [ -z "$CASDAY" ] ; then
        LOGFILE="$IPATH/run_dir/log/*T*.dat"
    else
        LOGFILE="$IPATH/run_dir/log/$CASDAY/$i/*T*.dat*"
    fi
    scp infrap@$i:$LOGFILE $CASTTE_DATADIR/$i/
done


date "$DATEFORMAT Transferring data files to ${TARGET_HOST}:$TARGET_DIR"

cd $CASTTE_DATADIR; cd ..
tar cf - $CASTTE_TARDIR \
  | ssh ${TARGET_USER}@${TARGET_HOST} "cd $TARGET_DIR; tar xvf -"

if [ -z "$CASDAY" ] ; then
    # If doing today's logs, create "done" marker file
    DONEFILE=${CASTTE_TARDIR}.tar.DONE
    ssh ${USER_DEEPFRZ}@$HOST_DEEPFRZ \
        "cd $DEEPFRZ_TARGET; mkdir -p $TODAY; touch $TODAY/$DONEFILE"
fi

cd
rm -rf $CASTTE_TARDIR
date "$DATEFORMAT Finished"
exit 0
