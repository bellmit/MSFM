#!/bin/ksh
# Get configuration data from Client hosts
# -----
# Each day that this script runs, it destroys
# and recreates $STAGING_DIR.

if [ -z $RUN_DIR ] ; then
    STARTDIR=$( pwd )
    . ~/.profile >/dev/null 2>&1
    cd $STARTDIR
fi

# -------------------- Configuration --------------------

STAGING_ROOT=ProdConfig
TIMEOUT_SECONDS=3

# -------------------- Constants --------------------

SCRIPTDIR=$( dirname $0 )
PROG=$( basename $0 )
HOSTNAME=$( hostname )
DATEFORMAT="+%Y-%m-%d %T"
SSH="ssh -q -o Batchmode=yes "

TMPDIR=$RUN_DIR/tmp/$PROG.$$
FTP_SCRIPT=ftp_script

# -------------------- Main program --------------------

unalias rm
. $SCRIPTDIR/setenv-scripts
. $SCRIPTDIR/hostsUtils
mkdir -m777 -p $TMPDIR
STAGING_DIR=$STAGING_ROOT/$REPORT_ENVIRONMENT

date "$DATEFORMAT Checking clients"
# Get config information from every live Client box
getHosts "cas,fix,sacas,mdcas,cfix,mdx" # set ALLHOST, ALIVEHOST
checkHosts $TIMEOUT_SECONDS

date "$DATEFORMAT Getting client configuration files"
cd $TMPDIR
for i in $ALIVEHOST ; do
    case $i in
    fix2[12])
        # Old FIXCAS disk structure discontinued after 2008.
        CONFIG=infra/config
        ;;
    *)
        CONFIG=config
        ;;
    esac
    mkdir $i.dir
    cd $i.dir
    cat - >$FTP_SCRIPT <<_ftp_
get $CONFIG/bin/setContext
get $CONFIG/bin/setContext.v2*
bye
_ftp_
    sftp -b $FTP_SCRIPT $i &
    cd ..
done
wait

date "$DATEFORMAT Renaming client configuration files"
for i in *.dir ; do
    HOST=${i%.dir}
    cd $i
    rm $FTP_SCRIPT
    for file in * ; do
        mv $file ../$HOST.$file
    done
    cd ..
    rmdir $i
done

date "$DATEFORMAT Creating empty target directory"
$SSH ${USER_WEB}@$HOST_WEB '\rm -rf '$STAGING_DIR'; mkdir -p '$STAGING_DIR

date "$DATEFORMAT Delivering client configuration files"
cd $TMPDIR
scp * ${USER_WEB}@${HOST_WEB}:$STAGING_DIR

cd
rm -rf $TMPDIR
date "$DATEFORMAT Done"
