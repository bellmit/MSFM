#!/usr/ksh

function showUsage {
    echo " "
    echo "Usage: $0 [-s startTime] [-e endTime] [-m mailingList] -h"
    echo "   -s: Mandatory.  Specify start time.  format example: [hh]:[mm]"
    echo "   -e: Mandatory.  Specify end time.    format example: [hh]:[mm]"
    echo "   -m: Optional.   Specify mailing list.    format example: xxxx@cboe.com,xxxx@cboe.com"
    echo "   -h: help."
}
if [ $# -eq 0 ]; then
    showUsage;
    exit 0
fi
while getopts :s:e:m:h c
do
    case $c in
    s) STARTTIME=$OPTARG
                 ;;
    e) ENDTIME=$OPTARG
               ;;
    m) MAILLIST=$OPTARG
               ;;
    h) showUsage
       exit 0
       ;;
    ?) echo "Unrecognized parameter::$1"
       exit 0
       ;;
    esac
done
if [[ -z ${STARTTIME} ]]; then
    echo "Unspecified STARTTIME"
    exit 0
fi

if [[ -z ${ENDTIME} ]]; then
    echo "Unspecified ENDTIME"
    exit 0
fi
    grep 'CurrentMarketEventConsumerInterceptor/acceptCurrentMarketsForClass' ../run_dir/log/*mdcas01*MI.log > CM_IEC.log; grep 'ProdCurrentMarket' ../run_dir/log/*mdcas01*QI.log > CM_QI.log
    sed -n "/${STARTTIME}:/,/${ENDTIME}:/p" CM_IEC.log|awk -F"," '{print $1, ($3-lastCall), ($5-lastMethodTime); lastCall=$3; lastMethodTime=$5}'>CM_MI
    sed -n "/${STARTTIME}:/,/${ENDTIME}:/p" CM_QI.log|awk -F"," '{print $1, ($4-last)/30; last=$4}'>CM_MsgRate
    paste CM_MI CM_MsgRate > mdRate
    sed '/ 0 /d' mdRate | awk -F" " '{print $1, $2, ($4-lastMethodTime)/($3-lastCalls), $6, $7; $3=lastCalls; $4=lastMethodTime}' > Rate
    awk '{total+=$3} END {print "avg IEC method time:", total/NR }' Rate >> Rate
    rm CM_QI.log CM_IEC.log CM_MI CM_MsgRate
cat Rate
