#!/bin/ksh
# script to clean up log file under $RUN_DIR/log and $V2RUN_DIR/log
# Usage: cleanLogFiles.ksh boxname

# -------------------- Constants --------------------
HOST=$1
LOGFILE=/sbt/prod/cas/log/$( date +%a )/cleanLogFiles.log
# -------------------- Subroutine --------------------
usage()
{
     echo "usage:cleanLogFiles.ksh boxname"
}
# -------------------- Main Program ----------------------------
if [ $# = 0 ] ; then
    usage
    exit
fi
echo cleanLogFiles.ksh $HOST
cd /sbt/prod/cas/bin/once
case $HOST in
    fix11[ab]|fix20[ab]|fix2[12])
        ## remove all fix log files 
        allclient -r $HOST ls infra/v2fixcas??/log/@(Mon|Tue|Wed|Thu|Fri|Sat|Sun)/* | tee $LOGFILE
        ## remove all infra log files
        allclient -r $HOST ls infra/run_dir/log/@(Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday)/$HOST/* | tee -a $LOGFILE
        allclient -r $HOST ls infra/run_dir/log/yesterday/* | tee -a $LOGFILE
    ;;

    fix*)
        ## remove all fix log files
        allclient -r $HOST ls v2fixcas??/log/@(Mon|Tue|Wed|Thu|Fri|Sat|Sun)/* | tee $LOGFILE
        ## remove all infra log files
        allclient -r $HOST ls run_dir/log/@(Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday)/$HOST/* | tee -a $LOGFILE
        allclient -r $HOST ls run_dir/log/yesterday/$HOST/* | tee -a $LOGFILE
      ;;

    cas*)
        ## clean v2cas01 log directory
        allclient -r $HOST ls v2cas01/log/@((Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday)/$HOST/* | tee $LOGFILE
        allclient -r $HOST ls v2cas01/log/yesterday/* | tee -a $LOGFILE
        ## clean run_dir/log directory
        allclient -r $HOST ls run_dir/log/@(Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday)/$HOST/* | tee -a $LOGFILE
        allclient -r $HOST ls run_dir/log/yesterday/* | tee -a $LOGFILE
    ;;

    sa*)
        ## clean v2cas01 log directory
        allclient -r $HOST ls v2sacas01/log/@((Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday)/$HOST/* | tee $LOGFILE
        allclient -r $HOST ls v2sacas01/log/yesterday/* | tee -a $LOGFILE

        ## clean run_dir/log directory
        allclient -r $HOST ls run_dir/log/@(Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday)/$HOST/* | tee -a $LOGFILE
        allclient -r $HOST ls run_dir/log/yesterday/* | tee -a $LOGFILE
    ;;
    *)
    ;;
esac
echo complete
