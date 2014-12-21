#!/bin/ksh
#
if [ -z $RUN_DIR ] ; then
    STARTDIR=`pwd`
    cd
    . ~/.profile >/dev/null 2>&1
    cd $STARTDIR
fi


#------------------- Define Constants ----------------------
DATEFORMAT="+%Y-%m-%d %T"
CMDFILE=/tmp/ftp.cmdfile
FIXCMDFILE=/tmp/fixftp.cmdfile
SUMMARYFILE=/tmp/GCstats.summary
FTPCMD=/tmp/xfer.cmdfile
TIMEOUT_SECONDS=3
SCRIPTDIR=`dirname $0`
if [ $SCRIPTDIR = "." ] ; then
    SCRIPTDIR=`pwd`
fi
MAILLIST="cboedirectclientreports@cboe.com"
[[ $DEBUG == 1 ]] && {
    MAILLIST="mageem@cboe.com"
}
\rm -rf /tmp/GCstats.*

#-------------------- Create ssh and ftp command files --------
cat - <<EOF >$CMDFILE
#cd /sbt/prod/infra/v2*cas01/log
cd /sbt/prod/infra/v2run_dir/log
LOG_INIT=\`grep "Log service initialized!" *.debug|tail -1\`
if [[ \${LOG_INIT} != "" ]]; then
    timestamp=\`echo \${LOG_INIT} | cut -d" " -f4\`
else
    echo "GCRPT:Did not get log or log service initialized string for CAS \`uname -n\`"
fi
procrun=\`ps -ef|grep PrintCASGCTime|grep -c -v "grep PrintCASGCTime"\`
if [ \$procrun -lt 1 ];then
/tmp/PrintCASGCTime -v timestamp=\${timestamp} -v process=\`uname -n\` -v maxLimit=0 /sbt/prod/infra/v2*cas01/log/startsh*log  /sbt/prod/infra/v2run_dir/log/startsh*out
else
	echo PrintGCTime Already Running on \`uname -n\`
fi
\rm -rf /tmp/PrintCASGCTime 2> /dev/null
EOF

cat - <<EOF >$FIXCMDFILE
IPATH=/apps/fixcas/prod/infra
if [ ! -d \$IPATH ] ; then
    # Old FIXCAS disk structure discontinued after 2008. Use new path.
    IPATH=/sbt/prod/infra
fi
cd \$IPATH
for i in v2fixcas*
do
cd \$IPATH/\$i/log
engine=\`echo \$i|sed 's/v2fixcas//'\`
LOG_INIT=\`grep "Log service initialized!" *.debug 2> /dev/null|tail -1\`
if [[ \${LOG_INIT} != "" ]]; then
    timestamp=\`echo \${LOG_INIT} | cut -d" " -f4\`
    procrun=\`ps -ef|grep PrintCASGCTime|grep -c -v "grep PrintCASGCTime"\`
    if [ \$procrun -lt 1 ];then
    /tmp/PrintCASGCTime -v timestamp=\${timestamp} -v process=\`uname -n\`.\$engine -v maxLimit=0 startsh*log
        echo got here
    else
            echo PrintGCTime Already Running on \`uname -n\`
    fi
fi
done
\rm -rf /tmp/PrintCASGCTime 2> /dev/null
EOF


cat - <<FTP >$FTPCMD
cd /tmp
lcd /sbt/prod/cas/bin
put PrintCASGCTime
chmod 744 PrintCASGCTime
FTP

# -------------------  Main program begins ----------------------

. $SCRIPTDIR/setenv-scripts
. $SCRIPTDIR/hostsUtils
# get list of all fix hosts
date "$DATEFORMAT getting all fix hosts"
getHosts "fix" # set ALLHOST to all fix
checkHosts $TIMEOUT_SECONDS
FIXALIVEHOST=$ALIVEHOST

# get list of all cas,sacas,mdcas,mdx  hosts
date "$DATEFORMAT getting all cas,sacas,mdcas,mdx hosts"
getHosts "cas,sacas,mdcas,mdx" # set ALLHOST to all cas,sacas,mdcas,mdx 
checkHosts $TIMEOUT_SECONDS
CASALIVEHOST=$ALIVEHOST

for server in $FIXALIVEHOST; do
        (sftp -b $FTPCMD $server 2>/dev/null > /dev/null;\
          ssh -q $server < $FIXCMDFILE |grep "GC[RPT SUM]" 2> /dev/null > /tmp/GCstats.$server;\
          echo Completed GC stats for $server...\
         ) &
done


for server in $CASALIVEHOST; do
	(sftp -b $FTPCMD $server 2>/dev/null > /dev/null;\
          ssh -q $server < $CMDFILE |grep "GC[RPT SUM]" 2> /dev/null > /tmp/GCstats.$server;\
          echo Completed GC stats for $server...\
         ) &
done

wait
echo "GC Stats Obtained for All CAS servers"
\rm -rf /tmp/GCstats_all
\rm -rf /tmp/GC.rpt
cat /tmp/GCstats.* > /tmp/GCstats_all
grep -v GCSUM /tmp/GCstats_all > /tmp/GCstats_all.detail
echo "GC Summary Daytime - 8:00 to 15:15 (Time in ms):" > $SUMMARYFILE
echo "================================================" >> $SUMMARYFILE
printf "%-22s %4s %5s %5s %5s %6s %6s %6s %6s %6s\n" "Server " "0-20 "  "21-30" "31-40" "41-50" "51-100" "101<=200" "201-500" ">=501"  "Full_GC" >> $SUMMARYFILE
echo "--------------------------------------------------------------------------------------------------" >> $SUMMARYFILE

grep GCSUMDAY /tmp/GCstats_all |sed 's/GCSUMDAY: //'>> $SUMMARYFILE
echo >> $SUMMARYFILE
echo >> $SUMMARYFILE
echo "GC Summary Startup - 0:00 to 8:00 (Time in ms):" >> $SUMMARYFILE
echo "================================================" >> $SUMMARYFILE
grep GCSUMSTART /tmp/GCstats_all |sed 's/GCSUMSTART: //'>> $SUMMARYFILE

awk '
BEGIN { print "\t\tCAS GC Report"
print "============================================"
print "Server \t Duration(sec)\t Time of Occurence"
}
/Full GC/ {
split($12,timearr,":")
hour=timearr[1]
min=timearr[2]
sec=timearr[3]
printf("%s     %2.4f        %02d:%02d:%02d (%s)\n",$5,$9,hour,min,sec,$13)
}
{
  split($6,timearr,":")
  hour=timearr[1]
  min=timearr[2]
  sec=timearr[3]
        printf("%s     %2.4f        %02d:%02d:%02d\n",$2,$4,hour,min,sec)
}
' /tmp/GCstats_all.detail > /tmp/GC.rpt

mv /tmp/GC.rpt /sbt/prod/cas/log/`date +\%a`/CASGCReport.details
echo >> $SUMMARYFILE
echo "Detailed Report Available at prdcaslog01:/sbt/prod/cas/log/`date +\%a`/CASGCReport.details" >> $SUMMARYFILE

mailx -s"[$REPORT_ENVIRONMENT] CAS/FIX GC Report" $MAILLIST < $SUMMARYFILE
### \rm -rf /tmp/GCstats.*
