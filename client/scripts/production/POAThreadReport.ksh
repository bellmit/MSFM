#!/bin/ksh
#
# Survey all Client boxes after processes are shut down, to report which
# processes had threads incorrectly still running at end of day.
#
# Usage: [ DEBUG=1 ] $0

if [ -z $RUN_DIR ] ; then
    STARTDIR=`pwd`
    cd
    . ~/.profile >/dev/null 2>&1
    cd $STARTDIR
fi

# -------------------- Configuration --------------------

TIMEOUT_SECONDS=3
THRESHOLD=60
MAILLIST="cboedirectclientreports@cboe.com"
[[ $DEBUG == 1 ]] && {
    THRESHOLD=2
    MAILLIST="mageem@cboe.com"
}
# -------------------- Constants --------------------

SCRIPTDIR=`dirname $0`
if [ $SCRIPTDIR = "." ] ; then
    SCRIPTDIR=`pwd`
fi
TODAY=`date +%a`
PROC=`basename $0`
TMPDIR=/tmp/$PROC.$LOGNAME.$$
REPORTFILE=$TMPDIR/POAthreadlimit.rpt
DATEFORMAT="+%Y-%m-%d %T"

# -------------------- Main program --------------------

. $SCRIPTDIR/setenv-scripts
. $SCRIPTDIR/hostsUtils
unalias rm

# get list of all cas, fix, sacas, mdcas, cfix and mdx hosts
getHosts "cas,fix,sacas,mdcas,cfix,mdx" # Set ALLHOST to list of Clients
checkHosts $TIMEOUT_SECONDS
[[ $DEBUG == 1 ]] && print "ALLHOST: \"$ALLHOST\""

mkdir -p $TMPDIR

# Create script to collect data on each host

cat - << EOF > thread.cmdfile
#!/bin/ksh
POA_LOG=/tmp/poa.\$\$.log
POA_TIMES=/tmp/poa.\$\$.times
hostname=\`uname -n|cut -c1-3\`
if [ \$hostname == "fix" ]; then
    for engine in 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25
    do
        awk -F, '/POA/ { if ( \$6 == $THRESHOLD ) printf( "overlimit.%s %s  %-28s : %2d / %d  \n",substr(FILENAME,index(FILENAME,"v2")-2,2), \$1,\$2,\$3,\$4) }' log/Prodfixcas"\$engine"v2fix*MonitorTPI.log | grep -v POATPAggregated | head -1 | tee \$POA_LOG
        awk -F, '/POA/ { if ( \$3 == $THRESHOLD ) printf( "overlimit.%s %s  %-28s : %2d / %d  \n",substr(FILENAME,index(FILENAME,"v2")-2,2), \$1,\$2,\$3,\$4) }' log/Prodfixcas"\$engine"v2fix*MonitorTPI.log | grep -v POATPAggregated | tee -a \$POA_LOG
        awk '/overlimit/ { print \$2 " " substr(\$3,1,5)":..,POA"}' \$POA_LOG > \$POA_TIMES
        awk '/overlimit/ { print substr(\$4,index(\$4,"/")+1,length(\$4)-index(\$4,"/"))}' \$POA_LOG | read matchstring
        if [  -s \$POA_TIMES ]; then
            egrep -f \$POA_TIMES log/Prodfixcas"$engine"v2fix*MonitorQI.log|grep -v W_MAIN|grep $matchstring|sed "s/^/queue for \`uname -n\`.$engine: /"
        fi
    done
else
    awk -F,  '/POA/ { if (\$6 == $THRESHOLD ) printf( "overlimit %s  %-28s : %2d / %d / %d / %d / %d / %d/ %d/ %d/ %d/ %d/ %d/ %d /%d\n", \$1,\$2,\$3,\$4,\$5,\$6,\$7,\$8,\$9,\$10,\$11,\$12,\$13,\$14,\$15) }' log/Prod*DefaultMonitorTPI.log | grep -v POATPAggregated | head -1 | tee \$POA_LOG
    awk -F,  '/POA/ { if (\$3 == $THRESHOLD ) printf( "overlimit %s  %-28s : %2d / %d / %d / %d / %d / %d/ %d/ %d/ %d/ %d/ %d/ %d /%d\n", \$1,\$2,\$3,\$4,\$5,\$6,\$7,\$8,\$9,\$10,\$11,\$12,\$13,\$14,\$15) }' log/Prod*DefaultMonitorTPI.log | grep -v POATPAggregated | tee -a \$POA_LOG
    awk '/overlimit/ { print \$2 " " substr(\$3,1,5)":..,POA"}' \$POA_LOG > \$POA_TIMES
    awk '/overlimit/ { print substr(\$4,index(\$4,"/")+1,length(\$4)-index(\$4,"/"))}' \$POA_LOG | read matchstring
    egrep -f \$POA_TIMES log/Prod*DefaultMonitorQI.log |grep -v W_MAIN|grep $matchstring|sed "s/^/queue for \`uname -n\`: /"
fi
unalias rm
rm -f \$POA_LOG \$POA_TIMES
EOF

date "$DATEFORMAT Collecting data"

# Create or truncate the output file
> $REPORTFILE
[[ $DEBUG == 1 ]] && print "ALIVEHOST: \"$ALIVEHOST\""
for host in $ALIVEHOST ; do
    ssh $host < thread.cmdfile 2> /dev/null > $TMPDIR/$host.thread &
done
wait

date "$DATEFORMAT Creating report"

echo "POA Thread Limit Exceeded for Following Servers" > $REPORTFILE
echo "===========================================" >> $REPORTFILE
grep overlimit $TMPDIR/*.thread|sed s!$TMPDIR'/\(.*\).thread:overlimit!\1 :!' >> $REPORTFILE

echo  >> $REPORTFILE
echo "POA Queue Depth Details" >> $REPORTFILE
echo "=======================" >> $REPORTFILE
echo "time,name,enqueued,dequeued,flushed,overlaid,hwm,size,status,overallhwm,overallhwmtime,enqwaits,deqwaits,deqtos,userdata" >> $REPORTFILE
echo "--------------------------------------------------------------------------------------------------------------------------" >> $REPORTFILE
grep -h "queue for"  $TMPDIR/*.thread|sed 's/queue for//' >> $REPORTFILE

echo "#" >> $REPORTFILE
echo "# Produced by script $0" >> $REPORTFILE
mailx -s "[$REPORT_ENVIRONMENT] CAS POA Thread Report" $MAILLIST < $REPORTFILE

[[ $DEBUG == 1 ]] || {
    rm -rf $TMPDIR
    rm thread.cmdfile
}

date "$DATEFORMAT Finished"
