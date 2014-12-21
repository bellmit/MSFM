#!/bin/ksh
# CAS_STARTTIMES.ksh
#
# get Client (CAS, FIX, SACAS...) engine startup times, e.g. $0 TODAY 1
#         Usage: $0 [TODAY,Monday ...] Iteration(1,2,3,...)
#
# Important side-effect: creates file $RUN_DIR/tmp/pwProcesses.lst
#
# Needs:
# $RUN_DIR/tmp/CASINFO.lst produced by get_cas_info.ksh

if [ -z $RUN_DIR ] ; then
    STARTDIR=`pwd`
    cd
    . ~/.profile >/dev/null 2>&1
    cd $STARTDIR
fi

# -------------------- Configuration --------------------

DEBUG=${DEBUG-0} # set DEBUG=1 to run a short test

TIMEOUT_SECONDS=3

# mail list 
MAILLIST="cboedirectclientreports@cboe.com ccs@cboe.com blaho@cboe.com tomkowm@cboe.com suppserv2@cboent.cboe.com sendung@cboeent.cboe.com"
[[ $DEBUG == 1 ]] && { 
    MAILLIST="mageem@cboe.com "
}

SLEEP_MINUTES=120
SLEEP_SECONDS=$(( $SLEEP_MINUTES * 60 ))

# -------------------- Constants --------------------

SCRIPTDIR=`dirname $0`
if [ $SCRIPTDIR = "." ] ; then
    SCRIPTDIR=`pwd`
fi
PROG=`basename $0`
SBTDIR=$HOME/bin
TMPDIR=$RUN_DIR/tmp/$LOGNAME.CAS_STARTTIMES.`date +%Y%m%d_%H%M%S`.$$
TMPFILE=$TMPDIR/tmpfile
DATADIR="$TMPDIR/data"
PINGFILE=$TMPDIR/ping.tmp
FTPCMD=$TMPDIR/ftp.tmp
DATEFORMAT="+%Y-%m-%d %T"

cd $SBTDIR
# export TODAY (such as Wednesday), ReportDate (such as 03/25/98)
export TODAY=`date +%A`
export ReportDate=`date +%m/%d/%y`
# eval `/sbt/prod/infra/run_dir/bin/EffectiveDate`
MAILFILE=$TMPDIR/CAS_STARTTIMES.$TODAY
CMDFILE=CAS_STARTTIMES.remotehelper.ksh

export PATH=$RUN_DIR/bin:$SBTDIR:$PATH
export GETFEINFO=$RUN_DIR/tmp/CASINFO.lst
export PWFILE=$RUN_DIR/tmp/pwProcesses.lst
export SSH="ssh -q -o Batchmode=yes "

# list everything that ProcessWatcher is watching
GETPWLIST="pwadmin -request showCurrentList"

# -------------------- Subroutines --------------------

getTimeValue()
{
        time_val=$*
        #echo "time_val=${time_val}"
        dateVal=`echo ${time_val}|cut -d" " -f2`
        hh=`echo ${dateVal}|cut -d ":" -f1`
        min=`echo ${dateVal}|cut -d ":" -f2`
        ss=`echo ${dateVal}|cut -d ":" -f3`
        #echo "hh=${hh}, min=${min}, ss=${ss}"
        ((time_val=${hh} * 3600.0 + ${min} * 60.0 + ${ss}))

        return time_val
}

usage() 
{
    [[ $1 == "" || $1 == "-h" || $2 == "" ]] && {
         echo  "Usage: $0 [TODAY,Monday ...] Iteration(1,2,3,...) "
         exit 1
    }
}

parseCommandLine()
{
    export DateIncluded=$1
    export Repeat=$2
    Iteration=""
    i=1
    while [[ $i -le "$Repeat" ]] ; do
        Iteration="$Iteration  $i"
        i=$(( $i + 1 ))
    done

    export Iteration=$Iteration
}

grepNonFixCas()
{
    case $DateIncluded in
        TODAY*)
            export LOGDIR="."
            export FILES="*cas.debug"
            export LOGFILES="cas.log"
            export CATCMD="cat"
            ;;
        *)
            export LOGDIR="$DateIncluded/*cas*"
            export FILES="*cas.debug*Z"
            export LOGFILES="cas.log*Z"
            export CATCMD="gzcat"
            ;;
    esac

    IPATH=/sbt/prod/infra

    cat - <<EOF >$CMDFILE
cd $IPATH/v2*cas01/log
$CATCMD $LOGDIR/$FILES |egrep -e "Log.service.initializ|Re-Initializ|All.initi" |sed 's#high systemNotification##g;s#SubscriptionManagerHome:[0-9]*##g;s#FileClient##g'
 $CATCMD $LOGDIR/$LOGFILES |  egrep 'getProductsByClass called' | tail -1
    $CATCMD $LOGDIR/$LOGFILES |egrep "getProductsByClass called" | wc -l
EOF

    cat - <<FTP >$FTPCMD
cd /tmp
put $CMDFILE
FTP

    for i in $ALIVEHOST
    do
        sftp -b $FTPCMD $i 2>&1 | egrep -v 'sftp> |Uploading '$CMDFILE
        $SSH $i "ksh /tmp/$CMDFILE; 'rm' /tmp/$CMDFILE" >$DATADIR/$i.TIMES &
    done
    wait
    \rm -f $CMDFILE $FTPCMD
}

grepCfix()
{
    case $DateIncluded in
        TODAY*)
            export LOGDIR="."
            export FILES="cfix.debug"
            export CATCMD="cat"
            ;;
        *)
            export LOGDIR="$DateIncluded"
            export FILES="cfix.debug*Z"
            export CATCMD="gzcat"
            ;;
    esac

    IPATH=/sbt/prod/infra

    for i in $ALIVEHOST
    do
        cat - <<! >$CMDFILE
engine=\$1
if [ -f /sbt/prod/infra/v2cfix\${engine}/log/$LOGDIR/$FILES ] ; then
    $CATCMD /sbt/prod/infra/v2cfix\${engine}/log/$LOGDIR/$FILES |egrep -e "Log.service.initializ|Re-Initializ|All.initi" |sed 's#high systemNotification##g;s#SubscriptionManagerHome:[0-9]*##g;s#FileClient##g' | grep -v "Cfix CAS Re-Initialization is Complete."
fi
!
        scp -Bq $CMDFILE $i:/tmp

        ENGINELIST=`$SSH $i "ls -d $IPATH/v2cfix?? 2>/dev/null" | sed 's/^.*v2cfix//g'`
        for engine in $ENGINELIST
        do
            echo "_INFO: (v2: $engine of $i): checking ... "
            $SSH $i ksh /tmp/$CMDFILE $engine >> $DATADIR/$i.$engine.OUTFILE &
        done
    done
    wait
    \rm -f $CMDFILE
}

grepFix()
{
    case $DateIncluded in
        TODAY*)
            export LOGDIR="."
            export FILES="cas.debug"
            export LOGFILES="cas.log"
            export CATCMD="cat"
            ;;
        *)
            export LOGDIR="$DateIncluded"
            export FILES="cas.debug*Z"
            export LOGFILES="cas.log*Z"
            export CATCMD="gzcat"
            ;;
    esac

    for i in $ALIVEHOST
    do
        case $i in
        fix2[12])
            # FIXCAS disk structure discontinued after 2008
            IPATH=/apps/fixcas/prod/infra
            ;;
        *)
            IPATH=/sbt/prod/infra
            ;;
        esac
        cat - <<! >$CMDFILE
engine=\$1
if [ -f $IPATH/v2fixcas\${engine}/log/$LOGDIR/$FILES ] ; then
    $CATCMD $IPATH/v2fixcas\${engine}/log/$LOGDIR/$FILES |egrep -e "Log.service.initializ|Re-Initializ|All.initi" |sed 's#high systemNotification##g;s#SubscriptionManagerHome:[0-9]*##g;s#FileClient##g'
    $CATCMD $IPATH/v2fixcas\${engine}/log/$LOGDIR/$LOGFILES |  egrep 'getProductsByClass called' | tail -1
    $CATCMD $IPATH/v2fixcas\${engine}/log/$LOGDIR/$LOGFILES |egrep "getProductsByClass called" | wc -l
fi
!
        scp -Bq $CMDFILE $i:/tmp

        ENGINELIST=`$SSH $i "ls -d $IPATH/v2fixcas?? 2>/dev/null" | sed 's/^.*v2fixcas//g'`
        for engine in $ENGINELIST
        do
            echo "_INFO: (v2: $engine of $i): checking ... "
            $SSH $i ksh /tmp/$CMDFILE $engine >> $DATADIR/$i.$engine.OUTFILE &
        done
    done
    wait
    \rm -f $CMDFILE
}

grepMdcas()
{
    case $DateIncluded in
        TODAY*)
            export LOGDIR="."
            export FILES="*.debug"
            export CATCMD="cat"
            ;;
        *)
            export LOGDIR="$DateIncluded"
            export FILES="*.debug*Z"
            export CATCMD="gzcat"
            ;;
    esac
    IPATH=/sbt/prod/infra

    for i in $ALIVEHOST
    do
        ENGINELIST=`$SSH $i "ls -d $IPATH/v2*[0-9] 2>/dev/null" | sed 's/^.*v2fixcas//g'`
        for engine in $ENGINELIST
        do
            case $engine in
            *mdcas*)
                printname=`echo $engine | sed 's/.*v2mdcas//'`
                ;;
            *cfix*)
                printname=`echo $engine | sed 's/.*v2cfix/c/'`
                ;;
            *) # unknown
                printname=`echo $engine | sed 's,.*/,,'`
                ;;
            esac

            echo "checking $i.$printname..."
            # Main program ignores output files *.OUTFILE of 0 length. If the
            # log file we're looking at has any content at all, the echo
            # command in $ECMDFILE will ensure that the output file has non-0
            # length so that the main program includes a line for this log
            # file, even if egrep can't find anything of interest there.
            ECMDFILE=$CMDFILE.$printname
            cat - <<! >$ECMDFILE
if [ -s $engine/log/$LOGDIR/$FILES ] ; then
    echo ""
    $CATCMD $engine/log/$LOGDIR/$FILES |egrep -e "Log.service.initializ|Re-Initializ|All.initi" |sed 's#high systemNotification##g;s#SubscriptionManagerHome:[0-9]*##g;s#FileClient##g'
fi
!
            cat - <<FTP >$FTPCMD
cd /tmp
put $ECMDFILE
FTP
            sftp -b $FTPCMD $i 2>&1 | egrep -v 'sftp> |Uploading '$ECMDFILE
            $SSH $i "ksh /tmp/$ECMDFILE; 'rm' /tmp/$ECMDFILE" >> $DATADIR/$i.$printname.OUTFILE &
            \rm -f $ECMDFILE
        done
    done
    wait
    \rm -f $FTPCMD
}

grepMdx()
{
    case $DateIncluded in
        TODAY*)
            export LOGDIR="."
            export FILES="mdx*.debug"
            export CATCMD="cat"
            ;;
        *)
            export LOGDIR="$DateIncluded/*mdx*"
            export FILES="mdx*.debug*Z"
            export CATCMD="gzcat"
            ;;
    esac

    IPATH=/sbt/prod/infra

cat - <<EOF >$CMDFILE

cd $IPATH/v2mdx01/log
$CATCMD $LOGDIR/$FILES |egrep -e "Log.service.initializ|Re-Initializ|All.initi" |sed 's#high systemNotification##g;s#SubscriptionManagerHome:[0-9]*##g;s#FileClient##g'
EOF

    cat - <<FTP >$FTPCMD
cd /tmp
put $CMDFILE
FTP

    for i in $ALIVEHOST
    do
        sftp -b $FTPCMD $i 2>&1 | egrep -v 'sftp> |Uploading '$CMDFILE
        $SSH $i ksh /tmp/$CMDFILE >$DATADIR/$i.TIMES &
    done
    wait
    \rm -f $CMDFILE $FTPCMD
}

catOutputLine () 
{
    HEADER=$1
    TIME=$2
    LINE="$2 $3"
###    [[ $TIME = "" ]] || CAS="${CAS} | $HEADER: $LINE "
    [[ $TIME = "" ]] || \
        { CAS="${CAS} | $HEADER: $LINE "
                case $HEADER in
                BEGIN )
                        counterbegin=`expr $counterbegin + 1`
                        ;;
                FF)
                        counterffinit=`expr $counterffinit + 1`
                        ;;
                RE)
                        counterreinit=`expr $counterreinit + 1`
                        ;;
                *)
                        ;;
                esac
        }

}

processDetailLines () 
{
    export counterbegin=0
    export counterffinit=0
    export counterreinit=0
    export starttime=0
    export endtime=0
    export pingflag=0
    export msg=""
   export classes=0 
   export products=0
   export product_count=0

    MYCAS=$1
    ENGINE=$2

    [[ $ENGINE == "" ]] && { 
        INPUTFILE=$MYCAS.TIMES
        DELIMIT=" "
    }
    [[ $ENGINE == "" ]] || {
        INPUTFILE=$MYCAS.$ENGINE.OUTFILE
        DELIMIT="."
    }
    export CAS=$(grep -w $MYCAS $GETFEINFO |cut -d" " -f4)

    if [ -f $DATADIR/$INPUTFILE ] ; then
        # Scan log file and build report line
        while read line
        do
            catOutputLine "BEGIN"   $(echo $line |perl -n -e 'if (/initialized/) { print $_};' |awk '{print $1, $2}')
            # second pattern for FF is for Infra 13 and later
            catOutputLine "FF" $(echo $line |perl -n -e 'if (/\.initialize |initialize Event/) { print $_};' |awk '{print $4}')
            catOutputLine "RE" $(echo $line |perl -n -e 'if (/Re-Initialization/) { print $_};' |awk '{print $2}')

	    timeinput=""; time_val=""
	   
            timeinput=`echo $line |perl -n -e 'if (/initialized/) { print $_};' |awk '{print $1, $2}' `
            if [ ! -z $timeinput  ]
            then
                 getTimeValue $timeinput; starttime=$time_val;   
            fi
            timeinput=""; time_val=""
	    servertype=`echo $MYCAS|grep mdx`
	    if [ -z $servertype ]
            then
                timeinput=`echo $line |perl -n -e 'if (/Re-Initialization/) { print $_};' |awk '{print $1, $2}' `
            else
                timeinput=`echo $line |perl -n -e 'if (/\.initialize /) { print $_};' |awk '{print $4}' `
            fi
            if [ ! -z $timeinput  ]
            then
                getTimeValue $timeinput; endtime=$time_val;   ##echo endtime = $endtime
            fi
            
        done <$DATADIR/$INPUTFILE 
       #Added to Check Class and Products count
       #export temp_name=`echo $MYCAS|grep ^fix`
      export temp_name="xxx" 
       if [ ! -z $temp_name ]; then
            product_count=0
            product_count=`grep "getProductsByClass called" $DATADIR/$INPUTFILE | wc -l | tr -d " "`
            if [ product_count -eq 0 ]; then
               products=0
               classes=0
               catOutputLine "P" 0
               catOutputLine "C" 0
            else
               products=`grep "getProductsByClass called" $DATADIR/$INPUTFILE |  tr -d " " | cut -d "\"" -f 2 | cut -d ":" -f6 | cut -d "=" -f 2`
               classes=`tail -1 $DATADIR/$INPUTFILE | tr -d " "`
               catOutputLine "P" $products
               catOutputLine "C" $classes
            fi
       else
        product_count=0 
       fi
    else
        # Couldn't communicate with this host
        eval err='$ERR_'$MYCAS
        catOutputLine "COMMS" "$err"
	pingflag=1
    fi
    if [ $counterbegin -eq 0 -a $counterffinit -eq 0 -a $counterreinit -eq 0 ]
    then
        msg="No INIT found"
	[[ $pingflag -eq 0 ]] || msg="No Connection"
    else
      	[[ $counterbegin -eq 1 ]] || msg="Exception:Multiple BEGIN's  "
       	[[ $counterffinit -eq 1 ]] || msg="Exception:Multiple FF-INIT's "
	if [ -z $servertype ]
           then
		if [ $counterreinit -eq 0 ];then
			msg="Exception:NO RE-INIT"
		fi
		if [ $counterreinit -gt 1 ];then
			msg="Exception:Multiple RE-INIT's"
		fi
                ###[[ $counterreinit -eq 1 ]] || msg="Exception:Multiple RE-INIT's "
           else
                  if [ $MYCAS != "prdmdx01a" -a $MYCAS != "prdmdx02a" -a $MYCAS != "prdmdx20a" -a $MYCAS != "prdmdx25a" -a $MY                  CAS != "prdmdx96a" -a $MYCAS != "prdmdx26a" -a $MYCAS != "prdmdxbka" ]
                  then
                          if [ $counterffinit -eq 0 ];then
                             msg="Exception:NO FF-INIT"
                          fi
                          if [ $counterffinit -gt 1 ];then
                             msg="Exception:Multiple FF-INIT's"
                          fi
                  else
                      echo "Not a good MDX"
                  fi
        fi
    fi

    elapsedtime_sec=`expr $endtime - $starttime `

    echo "CAS_NAME:${MYCAS}${DELIMIT}$ENGINE | $CAS |Time=$elapsedtime_sec sec"
    if [ -z $msg ]
    then
        echo $elapsedtime_sec >> /tmp/timecount.tmp
    else
        echo "$msg for CAS_NAME:${MYCAS}${DELIMIT}$ENGINE" >> /tmp/errmailfile
    fi
}

# -------------------- Main Program --------------------

usage $*
parseCommandLine $*
. $SCRIPTDIR/setenv-scripts
. $SCRIPTDIR/hostsUtils

mkdir -p $TMPDIR
mkdir -p $DATADIR
chmod -f 666 $PWFILE

for loop in $Iteration
do
    date "$DATEFORMAT Starting Iteration $Iteration"

    #reset 
    \rm -f $DATADIR/*
    >$MAILFILE

    ### ---------------- cas, sacas ---------------------------------------------- ###
    date "$DATEFORMAT Iteration $Iteration getting cas,sacas"
    getHosts "cas,sacas" # set ALLHOST to all cas,sacas
    [[ $DEBUG == 1 ]] && print "ALLHOST cas,sacas:\"$ALLHOST\""
# for DEBUG we want 1 good CAS, 1 CAS that doesn't respond, and 1 not in DNS
    #[[ $DEBUG == 1 ]] && export ALLHOST="cas0001 cas0004 cas5 sacas2"
    checkHosts $TIMEOUT_SECONDS
    date "$DATEFORMAT Checked hosts: cas, sacas"
    grepNonFixCas

    rm /tmp/timecount.tmp /tmp/errmailfile
    echo "EXCEPTION SUMMARY REPORT" >> /tmp/errmailfile
    echo "--------------------------------------------------------------------" >> /tmp/errmailfile

    for i in $ALLHOST 
    do
        processDetailLines $i
    done >>$MAILFILE

    mintime=`cat /tmp/timecount.tmp|awk '{printf("%04d\n",$0)}' |sort|head -1`
    maxtime=`cat /tmp/timecount.tmp|awk '{printf("%04d\n",$0)}' |sort|tail -1`
    echo Time for initialization NonFixCas ranges from $mintime sec to $maxtime sec >> /tmp/errmailfile
    echo  >> /tmp/errmailfile
    ### ---------------- cas, sacas ---------------------------------------------- ###

    ### ---------------- mdx ----------------------------------------------------- ###
    date "$DATEFORMAT Iteration $Iteration getting mdx"
    getHosts "mdx" # set ALLHOST to all mdx
    [[ $DEBUG == 1 ]] && print "ALLHOST mdx:\"$ALLHOST\""
# for DEBUG we want 1 good box, 1 that doesn't respond, and 1 not in DNS
    #[[ $DEBUG == 1 ]] && export ALLHOST="prdmdx21a prdmdxbka nosuchmdx"
    checkHosts $TIMEOUT_SECONDS
    date "$DATEFORMAT Checked hosts: mdx"
    grepMdx
    rm /tmp/timecount.tmp 

    for i in $ALLHOST
    do
        processDetailLines $i
    done >>$MAILFILE

    mintime=`cat /tmp/timecount.tmp|awk '{printf("%04d\n",$0)}' |sort|head -1`
    maxtime=`cat /tmp/timecount.tmp|awk '{printf("%04d\n",$0)}' |sort|tail -1`
    echo Time for initialization MDX ranges from $mintime sec to $maxtime sec >> /tmp/errmailfile
    echo  >> /tmp/errmailfile
    ### ---------------- mdx ----------------------------------------------------- ###

    ### ---------------- mdcas --------------------------------------------------- ###
    date "$DATEFORMAT Iteration $Iteration getting mdcas"
    getHosts "mdcas" # set ALLHOST to all mdcas
    [[ $DEBUG == 1 ]] && print "ALLHOST mdcas:\"$ALLHOST\""
# for DEBUG we want 1 good MDCAS
    #[[ $DEBUG == 1 ]] && export ALLHOST="mdcas02"
    checkHosts $TIMEOUT_SECONDS
    date "$DATEFORMAT Checked hosts: mdcas"
    grepMdcas
    rm /tmp/timecount.tmp

    for i in $ALLHOST
    do
        for datafile in $DATADIR/$i.*.OUTFILE
        do
            if [ -s $datafile ]
            then
                engine=`echo $datafile |sed -e "s#.*/$i.##;s#.OUTFILE##"`
                processDetailLines $i $engine
            fi
        done
    done >>$MAILFILE

    mintime=`cat /tmp/timecount.tmp|awk '{printf("%04d\n",$0)}' |sort|head -1`
    maxtime=`cat /tmp/timecount.tmp|awk '{printf("%04d\n",$0)}' |sort|tail -1`
    echo Time for initialization MDCAS ranges from $mintime sec to $maxtime sec >> /tmp/errmailfile
    echo  >> /tmp/errmailfile
    ### ---------------- mdcas --------------------------------------------------- ###

    ### ---------------- fixcas -------------------------------------------------- ###
    date "$DATEFORMAT Iteration $Iteration getting fixcas"
    getHosts "fix" # set ALLHOST to all fix
    [[ $DEBUG == 1 ]] && print "ALLHOST fix:\"$ALLHOST\""
    #[[ $DEBUG == 1 ]] && export ALLHOST="fix11b fix3c"
    checkHosts $TIMEOUT_SECONDS
    date "$DATEFORMAT Checked hosts: fixcas"
    grepFix
    rm /tmp/timecount.tmp

    for i in $ALLHOST
    do
        for file in $DATADIR/$i.*.OUTFILE
        do
            engine=${file#$DATADIR/$i.}
            engine=${engine%.OUTFILE}
            [[ -s $DATADIR/$i.$engine.OUTFILE ]] && processDetailLines $i $engine
        done 
    done >>$MAILFILE

    mintime=`cat /tmp/timecount.tmp|awk '{printf("%04d\n",$0)}' |sort|head -1`
    maxtime=`cat /tmp/timecount.tmp|awk '{printf("%04d\n",$0)}' |sort|tail -1`
    echo Time for initialization FIX ranges from $mintime sec to $maxtime sec >> /tmp/errmailfile
    echo  >> /tmp/errmailfile
    ### ---------------- fixcas -------------------------------------------------- ###

    ### ---------------- cfix ---------------------------------------------------- ###
    date "$DATEFORMAT Iteration $Iteration getting cfix"
    getHosts "cfix" # set ALLHOST to all cfix
    [[ $DEBUG == 1 ]] && print "ALLHOST cfix:\"$ALLHOST\""
    #[[ $DEBUG == 1 ]] && export ALLHOST="prdcfix1"
    checkHosts $TIMEOUT_SECONDS
    date "$DATEFORMAT Checked hosts: prdcfix"
    grepCfix
    rm /tmp/timecount.tmp

    for i in $ALLHOST
    do
        for file in $DATADIR/$i.*.OUTFILE
        do
            engine=${file#$DATADIR/$i.}
            engine=${engine%.OUTFILE}
            [[ -s $DATADIR/$i.$engine.OUTFILE ]] && processDetailLines $i $engine
        done
    done >>$MAILFILE

    mintime=`cat /tmp/timecount.tmp|awk '{printf("%04d\n",$0)}' |sort|head -1`
    maxtime=`cat /tmp/timecount.tmp|awk '{printf("%04d\n",$0)}' |sort|tail -1`
    echo Time for initialization CFIX ranges from $mintime sec to $maxtime sec >> /tmp/errmailfile
    echo  >> /tmp/errmailfile
    ### ---------------- cfix ---------------------------------------------------- ###

    echo "--------------------------------" >> /tmp/errmailfile
    echo "END OF EXCEPTION SUMMARY REPORT" >> /tmp/errmailfile
    echo "--------------------------------" >> /tmp/errmailfile
    echo " DETAILED REPORT" >> /tmp/errmailfile
    echo "-----------------" >> /tmp/errmailfile
    grep -v prdmdx /tmp/errmailfile > /tmp/errmailfile.mdxfilter

    sort $MAILFILE >$TMPFILE
    cat /tmp/errmailfile.mdxfilter $TMPFILE > $MAILFILE
    ### mv $TMPFILE $MAILFILE

    grep "NO RE-INIT" $MAILFILE > /tmp/noinitfile
    if [ $? -eq 0 ]; then
	MAILSUB="[$REPORT_ENVIRONMENT] ERROR!! NO RE-INIT FOUND for CAS/FIX engine"
	cat /tmp/noinitfile $MAILFILE > $TMPFILE
	mv $TMPFILE $MAILFILE
    else
	MAILSUB="[$REPORT_ENVIRONMENT] CAS & FIX engine RE-INITs pass #$loop of $Repeat (sleep $SLEEP_MINUTES mins)" 
    fi

    [[ "$loop" == "$Repeat" ]] && ksh $SCRIPTDIR/topUsage.ksh $ALIVEHOST >>$MAILFILE
    [[ -s $MAILFILE ]] && echo "\nProduced by script $SCRIPTDIR/$PROG" | cat $MAILFILE - |mailx -s "$MAILSUB" $MAILLIST
    [[ "$loop" != "$Repeat" ]] && sleep $SLEEP_SECONDS
done

[[ $DEBUG == 1 ]] || {
    # archive
    ftp -n $HOST_DEEPFRZ <<!
user infra infra
lcd `dirname $MAILFILE`
put `basename $MAILFILE`
!
}

#clean-up
\rm -rf $TMPDIR
date "$DATEFORMAT $PROG Run complete"
