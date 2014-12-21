#!/bin/ksh
# runStatusReport.sh - run CAS process status 

export PATH=/usr/local/bin:/usr/bin:$PATH
export OUTDIR=$HOME/CASInstall/bin.new

#export MAILLIST="weiw@cboent.cboe.com"
export MAILLIST="danaher@cboe.com labres@cboe.com CBOEDirectClientSupport@cboent.cboe.com APITestingGroupInternetPublic@cboent.cboe.com"
export MAILSUBJECT="CAS Status For API / CERT / ATG"
TMPFILE=/tmp/$LOGNAME.$$.tmp
OUTFILE=/tmp/$LOGNAME.$$.out

. $OUTDIR/setRestartContext

>$OUTFILE
>$TMPFILE

apiFormatLines ()
{
    SERVER=$1
    LOGIN=$2
    export LINEOUT=$(rsh $SERVER -l $LOGIN -n "ksh ~/apitestPWrequest.sh $LOGIN |egrep -e \"^[scfm]|SMS|v20\" |egrep -v \"POA|showCur|set\" |sort -r +5.1 |sed -e 's/^.*down/<font color=red>&<\/font>/g'")
}

standardFormatLines ()
{
    SERVER=$1
    LOGIN=$2
    export LINEOUT=$(rsh $SERVER -l $LOGIN -n ". .profile >/dev/null 2>&1;pwadmin -request showCurrentList |egrep -e \"^[scfm]|SMS|v20\" |egrep -v \"POA|showCur|set\" |sort -r +5.1 |sed -e 's/^.*down/<font color=red>&<\/font>/g'")
}

# ATG and API hosts
cd $OUTDIR
REMOTEHOST=${REMOTEHOST-$CASHOST}
[[ $REMOTEHOST == "" ]] && setHostList

CASHOST=$REMOTEHOST
print CASHOST=$REMOTEHOST

for i in $REMOTEHOST
do
    ftp $i <<!
cd /tmp
get ..log $i.status 
del ..log
!
    INFILE=$i.status
    RETURN=$(perl -n -e 'if (/FAIL/) {print $_};' $INFILE)
    [[ $RETURN == "" ]] || STATUS="<font size=\"-1\" color=red>DOWN</font size=\"+1\">"

    [[ $RETURN == "" ]] && STATUS="<font size=\"-1\" color=brown>UNABLE TO START<font size\"=+1\">" 
    RETURN=$(perl -n -e 'if (/SUCCESSFUL/) {print $_};' $INFILE)
    [[ $RETURN == "" ]] || STATUS="<font size=\"-1\" color=brown>UP<font  size\"=+1\">"

    FORMATTED=$(grep -v SUCCESSFUL $INFILE |sed '1,$s/ [A-Z][A-Z]T 20[0-9][0-9]//g' |nawk 'BEGIN{RS=FS="";OFS=", "} $1=$1')

    print "export ${i}_status=\"$STATUS\"" >>$TMPFILE
    print "export ${i}_log=\"$FORMATTED\"" >>$TMPFILE
done

# get file system usage >50%
export REMOTEHOST=$CASHOST
FSUSAGE=$(ksh $OUTDIR/topUsage.ksh)

print "export FSUSAGE=\"$FSUSAGE\"" >>$TMPFILE

for i in apitest1 apitest2 
do
    apiFormatLines atgsvr2 $i
    print "export $(echo $i |tr "[:lower:]" "[:upper:]")=\"$LINEOUT\"" >>$TMPFILE
done 

standardFormatLines certapi infrap
print "export APICERT=\"$LINEOUT\"" >>$TMPFILE

for i in atgtest1 atgtest2
do
    standardFormatLines atgqagc1 $i
    print "export $(echo $i |tr "[:lower:]" "[:upper:]")=\"$LINEOUT\"" >>$TMPFILE
done 

for i in atgtest3 atgtest4
do
    standardFormatLines atgqagc2 $i
    print "export $(echo $i |tr "[:lower:]" "[:upper:]")=\"$LINEOUT\"" >>$TMPFILE
done 

print "cat - <<!" >>$TMPFILE
cat status.template.htm >>$TMPFILE
print "!" >>$TMPFILE
ksh $TMPFILE >>$OUTFILE 

cd javamail-1.3.3_01/demo;ksh sendHtmlUtil.sh $TMPFILE; cd -
\rm -f /tmp/*.$$.* *.status

exit 0
