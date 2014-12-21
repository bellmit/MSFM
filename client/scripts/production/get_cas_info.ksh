#!/bin/ksh
# Get CAS information: FE connection, INFRA version, CBOEDIR version
# Usage: $0 outputfile

if [ -z $RUN_DIR ] ; then
    STARTDIR=`pwd`
    cd
    . ~/.profile >/dev/null 2>&1
    cd $STARTDIR
fi

# -------------------- Configuration --------------------

OUTFILE=$RUN_DIR/tmp/CASINFO.lst
if [ -n "$DEBUG" ] ; then
    OUTFILE=/tmp/casinfo.$$
fi

WEB_DIR=public_html/$( date +%A )

# -------------------- Constants --------------------

SCRIPTDIR=$( dirname $0 )
PROG=$( basename $0 )
HOSTNAME=$( hostname )
DATEFORMAT="+%Y-%m-%d %T"
TIMEOUT_SECONDS=3
SSH="ssh -q -o Batchmode=yes "

TMPDIR=$RUN_DIR/tmp/$PROG.$$.$USER
CASFILE=$TMPDIR/pwlist.txt

# These are filename prefixes; full names are $FEFILE.$host, $LSFILE.$host
FEFILE=$TMPDIR/fe
LSFILE=$TMPDIR/ls

# -------------------- Main program --------------------

unalias rm
. $SCRIPTDIR/setenv-scripts
. $SCRIPTDIR/hostsUtils
mkdir -m777 -p $TMPDIR

# Get remotehost information from every live Client box

getHosts "cas,fix,sacas,mdcas,cfix,mdx" # Set ALLHOST to list of Clients
checkHosts $TIMEOUT_SECONDS

date "$DATEFORMAT Getting FE data from each Client host"
for i in $ALIVEHOST ; do
    # grep finds line like FRONTEND_IDENTIFIER=PRDFE01
    # output file has line like PRDFE01v20
    case $i in
    fix*)
        # Old fix boxes (2008 and earlier) have infra subdirectory
        # New fix boxes (2009 and later) log in to infra directory
        $SSH $i 'grep -h FRONTEND_IDENTIFIER infra/config/bin/setContext.* config/bin/setContext.* 2>/dev/null' \
        | sed 's/.*=//;s/$/v20/' | sort -u >$FEFILE.$i &
        ;;
    prdmdx*)
        $SSH $i 'grep -h FE_IDENTIFIER config/bin/setContext' \
        | sed 's/.*=//;s/$/v20/' | sort -u >$FEFILE.$i &
        ;;
    *)
        $SSH $i 'grep -h FRONTEND_IDENTIFIER config/bin/setContext.*' \
        | sed 's/.*=//;s/$/v20/' | sort -u >$FEFILE.$i &
        ;;
    esac
done
wait

date "$DATEFORMAT Getting software version data from each Client host"
for i in $ALIVEHOST ; do
    case $i in
    fix2[12])
        InfraHome=/apps/fixcas/prod/infra
        ;;
    *)
        InfraHome=/sbt/prod/infra
        ;;
    esac
    $SSH $i "egrep '^export JAVA_HOME=' $InfraHome/.setenv; ls -ld $InfraHome/run_dir $InfraHome/v2*01" >$LSFILE.$i &
done
wait

# Produce report

echo "CAS Servers -> FE         JDK         INFRA            SBT              [RELEASED ON]" >$OUTFILE

for i in $ALIVEHOST ; do
    INFRA_DATE=$( grep /run_dir $LSFILE.$i |sort| awk '{print $6,$7,$8}' )
    CAS_DATE=$(   grep /v2      $LSFILE.$i |sort| awk '{print $6,$7,$8}' )
    FEID=$( cat $FEFILE.$i )
    VERSIONS=$( cut <$LSFILE.$i -d'>' -f2 | sed 's#.*/local/##;s#../cas/##' )
    # VERSIONS has each item on its own line, followed by \n.
    # We don't want that \n character; turn it into a space.
    VERSIONS=$( echo $VERSIONS | tr '\n' ' ' )
    echo "CAS $i -> $FEID $VERSIONS[$INFRA_DATE $CAS_DATE]" >>$OUTFILE
done

rm -rf $TMPDIR
if [ -z $DEBUG ] ; then  # Don't do this in debugging mode
    base=$( basename $OUTFILE )
    head=${base%.*}
    tail=${base##*.}
    REMOTE_REPORT_FILE=$head.$REPORT_ENVIRONMENT.$tail
    scp -q $OUTFILE $USER_WEB@$HOST_WEB:$WEB_DIR/$TODAY/$REMOTE_REPORT_FILE
fi #DEBUG

date "$DATEFORMAT Finished"
