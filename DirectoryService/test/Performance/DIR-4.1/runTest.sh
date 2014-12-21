#/bin/ksh

#Template test program

#Fill in your test program
JAVAPROG="-version"
PROGARG=""

CMD="java"

LOGFILE="run.log"
CURDATE=`date +%m%d%Y`

if [ $# -lt 1 ]; then
            TEST=${CURDATE}.1
else
            TEST=${CURDATE}.$1
fi

if [ -d $TEST ]; then
          echo "Using existing directory: $TEST"
          cleartool co -nc $TEST > /dev/null 2> /dev/null
          cleartool co -nc ./$TEST/results.txt > /dev/null 2> /dev/null
          echo "result:    pass / fail" > ./$TEST/results.txt
          echo "comments: " >> ./$TEST/results.txt 
          cleartool ci -nc ./$TEST/results.txt > /dev/null 2> /dev/null
else
          cleartool co -nc .
          echo "Creating directory: $TEST"
##        mkdir $TEST
          cleartool mkelem -eltype directory -nc $TEST > /dev/null 2> /dev/null
          echo "result:    pass / fail" > ./$TEST/results.txt
          echo "comments: " >> ./$TEST/results.txt
          cleartool ci -nc .
fi

cleartool mkelem -eltype text_file -ci -nc ./$TEST/results.txt > /dev/null 2> /dev/null
cleartool ci -nc $TEST > /dev/null 2> /dev/null
cleartool unco -rm ./$TEST/results.txt > /dev/null 2> /dev/null

(cd $TEST; $CMD $JAVAPROG $PROGARG > $LOGFILE 2>&1)
