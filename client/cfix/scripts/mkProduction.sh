#!/usr/bin/ksh
#
# Author: Dmitry Volpyansky
#

outdir=/vobs/dte/client/cfix/tmp

mkdir -p ${outdir}

chmod 777 ${outdir}

### PRODUCTION BOXES
echo "Making PROD boxes"

for i in 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 98 99
do
   ksh mkCfix.sh -h mdcas${i} -e CF1M${i} CF2M${i} -t prod -d ${outdir} -a -n -g -p W_MAIN
done

for i in 1 2
do
   ENG=""
   for eng in 01 02 03 04 ; do
      ENG="$ENG CF${eng}M0${i}"
   done
   ksh mkCfix.sh -h prdcfix${i} -e $ENG -t prod -d ${outdir} -a -n -g -p W_MAIN
done

### DR BOXES
echo "Making DR boxes"
for box in 01 02 03 04
do
   ENG=""
   for eng in 01 02 03 04 ; do
      ENG="$ENG CF${eng}DR${box}"
   done
   ksh mkCfix.sh -h drcfix${box} -e $ENG -t dr -d ${outdir} -a -n -g -p W_MAIN
done

### ATG BOXES
echo "Making ATG boxes"
for i in 1 2 3 4 5 6 7 8 9 
do
   ksh mkCfix.sh -h atgmd${i} -e CF1M01 CF2M01 -t atg -d ${outdir} -a -n -g -p W_MAIN
done

### API BOXES
echo "Making API boxes"
for i in 1 2 3
do
   ksh mkCfix.sh -h apimd${i} -e CF1A0${i} CF2A0${i} -t api -d ${outdir} -s -a -n -g -p W_MAIN
done

### CERT BOXES
for i in 1 2
do
   ksh mkCfix.sh -h certmdcas${i} -e CF1A0${i} CF2A0${i} -t cert -d ${outdir} -s -a -n -g -p W_MAIN
done

### PERF BOXES
echo "Making PERF boxes"
for i in 01 02 03 04 05 06 07 08 09
do
   ksh mkCfix.sh -h sbtmdc${i} -e CF1M01 -t perf -d ${outdir} -a -n -g -p W_MAIN
   ksh mkCfix.sh -h perfmdcas${i} -e CF1M01 -t perf -d ${outdir} -a -n -g -p W_MAIN
done

### DEV BOXES
for i in 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25
do
   ksh mkCfix.sh -h dev${i}mdc -e CF1M01 -t dev -d ${outdir} -a -n -g -p W_MAIN
done
