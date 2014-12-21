#!/bin/ksh
# Gather 'limit exceeded' error messages from all CAS and FIXCAS hosts,
# and produce a report

if [ -z $RUN_DIR ] ; then
    STARTDIR=`pwd`
    cd
    . ~/.profile >/dev/null 2>&1
    cd $STARTDIR
fi

# -------------------- Configuration --------------------

DEBUG=0

DEEPFREEZE_USER=infra
WEB_DIR=public_html
MAILLIST="cboedirectclientreports@cboe.com novakm@cboe.com piwnicki@cboe.com ciciora@cboe.com oconnell@cboe.com curt@cboe.com winters@cboe.com"

TIMEOUT_SECONDS=3

if [ $DEBUG == 1 ] ; then
    MAILLIST="mageem@cboe.com"
fi

# -------------------- Constants --------------------

SCRIPTDIR=`dirname $0`
if [ $SCRIPTDIR = "." ] ; then
    SCRIPTDIR=`pwd`
fi
TMPDIR=$RUN_DIR/tmp/$LOGNAME.`date "+%Y%m%d.%H%M%S"`.rateLimitExceeded.$$

PINGFILE=$TMPDIR/ping.tmp
DATEFORMAT="+%Y-%m-%d %T"

GREPOUTFILE=$TMPDIR/allexceed.txt
REPORTFILE=$TMPDIR/quotereport.txt
if [ $DEBUG == 1 ] ; then
    GREPOUTFILE=/sbt/prod/cas/log/`date "+%a"`/exceeded-grep.tmp
    REPORTFILE=/sbt/prod/cas/log/`date "+%a"`/exceeded-quotereport.tmp
fi
MAKE_REPORT=$TMPDIR/rateLimitExceeded_table.pl

# -------------------- Subroutines --------------------

makeReportScript()
{
    cat >$MAKE_REPORT <<'_Report_Script_'
#!/usr/bin/perl -w

# .................... Constants ....................

@TIME_BUCKETS = (
  "07:15", "08:15", "09:15", "10:15", "11:15",
  "12:15", "13:15", "14:15", "15:15" );

# Partial header lines, also need @TIME_BUCKETS
@CAS_HEADER_LINE = ("Host", "ExceedType");
@USER_HEADER_LINE = ("Host", "User", "ExceedType");

# For neat printouts, use equal-length strings for all the
# members of @EXCEED_TYPES and the corresponding titles in
# @CAS_HEADER_LINE and @USER_HEADER_LINE.
$QUOTE_COUNT="quoteRate ";
$QUOTE_CALL ="quoteCall ";
$ORDER_CALL ="orderCall ";
@EXCEED_TYPES = ($QUOTE_COUNT, $QUOTE_CALL, $ORDER_CALL);

$YES=1;
$NO=0;

# .................... Subroutines ....................

sub usage
{
    die "Usage: $0 {-o|-q} <cas.debug\n";
}

sub parseCommandLine
{
    $Orders = $NO;
    $Quotes = $NO;
    my $i = 0;
    while ($i <= $#ARGV)
    {
        if ($ARGV[$i] eq "-o")
        {
            $Orders = $YES;
        }
        elsif ($ARGV[$i] eq "-q")
        {
            $Quotes = $YES;
        }
        else
        {
            &usage;
        }
        ++ $i;
    }
    &usage if $Orders != $YES && $Quotes != $YES;
}

# Convert HH:MM to an integer index
# @param 1 24-hour time, HH:MM
# @return 0..$#TIME_BUCKETS
sub timeBucket()
{
    my $time = shift;
    my $i;
    for ($i = 1; $i <= $#TIME_BUCKETS; ++$i)
    {
        return $i-1 if $TIME_BUCKETS[$i] gt $time;
    }
    return $#TIME_BUCKETS;
}

# Parse exception line indicating some limit exceeded
# Uses $_
# @return array (timeBucket, host, user)
sub parseInput
{
    my @words = split;
    my $time = $words[3];
    my $host = $words[5];
    my $engineType = $words[6];
    my $user;
    / UA:(\S+) / && ($user = $1);

    $time =~ s/:\d+\.\d+$//;  # keep only HH:MM
    $time = "0$time" if (length $time) < $CountWidth;
    if ($engineType eq "FIXCAS")
    {
        # $host looks like Prodfixcas13v2fix61bk
        my ($engine, $hostname);
        $host =~ /fixcas(\d+)v2(.+)/ && ($engine=$1, $hostname=$2);
        $host = "$hostname.$engine";
    }
    else
    {
        # $host looks like Prodcas01v2cas0016
        $host =~ s/.*cas01v2//;   # keep only host name
    }
    return (&timeBucket($time), $host, $user);
}

sub quoteCalls
{
    my ($timeBucket, $host, $user) = &parseInput;
    return if $timeBucket eq "";

    ++ $Cas{$host}->{$timeBucket}->{$QUOTE_CALL};
    ++ $Cas{$host}->{$user}->{$timeBucket}->{$QUOTE_CALL};
    ++ $TotalExceptions;
}

sub quoteRate
{
    my ($timeBucket, $host, $user) = &parseInput;
    return if $timeBucket eq "";

    ++ $Cas{$host}->{$timeBucket}->{$QUOTE_COUNT};
    ++ $Cas{$host}->{$user}->{$timeBucket}->{$QUOTE_COUNT};
    ++ $TotalExceptions;
}

sub orderCalls
{
    my ($timeBucket, $host, $user) = &parseInput;
    return if $timeBucket eq "";

    ++ $Cas{$host}->{$timeBucket}->{$ORDER_CALL};
    ++ $Cas{$host}->{$user}->{$timeBucket}->{$ORDER_CALL};
    ++ $TotalExceptions;
}

sub padRight
{
    my $value = shift;
    my $width = shift;
    $value = "" unless defined $value;
    return $value . (" " x ($width - length "$value"));
}

sub padLeft
{
    my $value = shift;
    my $width = shift;
    $value = "" unless defined $value;
    return +(" " x ($width - length "$value")) . $value;
}

sub printLine
{
    print +(join ',', @_) . "\n";
}

# .................... Main Program ....................

%Cas = ();
$CountWidth = length $TIME_BUCKETS[0];
$TotalExceptions = 0;
&parseCommandLine;

# Read and tally all input

while (<STDIN>)
{
    next unless /limit exceeded/;
    next if /^com\.cboe\.exceptions\./;

    my $count = 1;
    /(\d+) duplicates suppressed/ && ($count = 1 + $1);

    while ($count > 0)
    {
        &quoteCalls if $Quotes == $YES && /Quote.*Call limit exceeded/;
        &quoteRate  if $Quotes == $YES && /Quote rate limit exceeded/;
        &orderCalls if $Orders == $YES && /Order.*Call limit exceeded/;
        --$count;
    }
}
if ($TotalExceptions == 0)
{
    print "No exceptions found\n";
    exit;
}

# Determine a couple of column widths based on input

$CasWidth = length $CAS_HEADER_LINE[0];
$NameWidth = length $USER_HEADER_LINE[1];
for $cas (sort keys %Cas)
{
    $CasWidth = length $cas if (length $cas) > $CasWidth;
    my $name;
    foreach $name (sort keys %{ $Cas{$cas} })
    {
        $NameWidth = length $name if (length $name) > $NameWidth;
    }
}
++$CasWidth;
++$NameWidth;
$CAS_HEADER_LINE[0] = &padRight($CAS_HEADER_LINE[0], $CasWidth);
$USER_HEADER_LINE[0] = &padRight($USER_HEADER_LINE[0], $CasWidth);
$USER_HEADER_LINE[1] = &padRight($USER_HEADER_LINE[1], $NameWidth);

# Find widest number in per-CAS table, so we can set column size

$maxCount = 0;
for $exceedType (@EXCEED_TYPES)
{
    my $cas;
    for $cas (sort keys %Cas)
    {
        my $timeBucket;
        for $timeBucket ("0" .. "$#TIME_BUCKETS")
        {
            if (exists $Cas{$cas}->{$timeBucket}->{$exceedType})
            {
                my $n = $Cas{$cas}->{$timeBucket}->{$exceedType};
                $maxCount = $n if $n > $maxCount;
            }
        }
    }
}
@PaddedBuckets = (@TIME_BUCKETS);
$CountWidth = length $TIME_BUCKETS[0];
$CountWidth = length $maxCount if (length $maxCount) > $CountWidth;
for $bucket (@PaddedBuckets)
{
    $bucket = &padLeft($bucket, $CountWidth);
}

# Print the per-CAS summary lines

@CAS_HEADER_LINE = (@CAS_HEADER_LINE, @PaddedBuckets);
&printLine(@CAS_HEADER_LINE);

for $exceedType (@EXCEED_TYPES)
{
    for $cas (sort keys %Cas)
    {
        my $nExceeds = 0;
        my $timeBucket;
        my @list = (&padRight($cas, $CasWidth), $exceedType);
        for $timeBucket ("0" .. "$#TIME_BUCKETS")
        {
            push @list, &padLeft($Cas{$cas}->{$timeBucket}->{$exceedType}, $CountWidth);
            if (exists $Cas{$cas}->{$timeBucket}->{$exceedType})
            {
                $nExceeds += 1*$Cas{$cas}->{$timeBucket}->{$exceedType};
            }
        } # each timeBucket this CAS
        if ($nExceeds > 0)
        {
            &printLine(@list);
        }
    }
}

# Find widest number in user-detail table, so we can set column size

$maxCount = 0;
for $exceedType (@EXCEED_TYPES)
{
    my $cas;
    for $cas (sort keys %Cas)
    {
        my $name;
        foreach $name (sort keys %{ $Cas{$cas} })
        {
            my $timeBucket;
            for $timeBucket ("0" .. "$#TIME_BUCKETS")
            {
                if (exists $Cas{$cas}->{$name}->{$timeBucket}->{$exceedType})
                {
                    my $n = $Cas{$cas}->{$name}->{$timeBucket}->{$exceedType};
                    $maxCount = $n if $n > $maxCount;
                }
            }
        }
    }
}
@PaddedBuckets = (@TIME_BUCKETS);
$CountWidth = length $TIME_BUCKETS[0];
$CountWidth = length $maxCount if (length $maxCount) > $CountWidth;
for $bucket (@PaddedBuckets)
{
    $bucket = &padLeft($bucket, $CountWidth);
}

# Print the user detail lines

print "\nExceptions by User\n";
@USER_HEADER_LINE = (@USER_HEADER_LINE, @PaddedBuckets);
&printLine(@USER_HEADER_LINE);
for $exceedType (@EXCEED_TYPES)
{
    for $cas (sort keys %Cas)
    {
        my $name;
        foreach $name (sort keys %{ $Cas{$cas} })
        {
            my $nExceeds = 0;
            my $timeBucket;
            my @list = (&padRight($cas, $CasWidth), &padRight($name, $NameWidth), $exceedType);
            for $timeBucket ("0" .. "$#TIME_BUCKETS")
            {
                push @list, &padLeft($Cas{$cas}->{$name}->{$timeBucket}->{$exceedType}, $CountWidth);
                if (exists $Cas{$cas}->{$name}->{$timeBucket}->{$exceedType})
                {
                    $nExceeds += 1*$Cas{$cas}->{$name}->{$timeBucket}->{$exceedType};
                }
            } # each timeBucket this CAS
            if ($nExceeds > 0)
            {
                &printLine(@list);
            }
        }
    }
}
_Report_Script_
    chmod 777 $MAKE_REPORT
} # makeReportScript

# -------------------- Main Program --------------------

. $SCRIPTDIR/setenv-scripts
. $SCRIPTDIR/hostsUtils
mkdir -p $TMPDIR
makeReportScript

# Set variable TODAY 
TODAY=`date +%A`
ReportDate=`date +%m/%d/%y`

# Filter FIXCAS logfiles
date "$DATEFORMAT Scanning FIXCAS debug files"
getHosts "fix"
checkHosts $TIMEOUT_SECONDS
for i in $ALIVEHOST ; do
    case $i in
    fix2[12])
        # Old FIXCAS disk structure discontinued after 2008.
        HOMEDIR="infra/"
        ;;
    *)
        # Standard disk structure, also for FIXCAS starting in 2009.
        HOMEDIR=
        ;;
    esac
    ssh $i "grep 'UA:.*limit exceeded' "$HOMEDIR"v2fixcas*/log/cas.debug" > $TMPDIR/$i.exceeded.txt &
done
wait
DEADFIX=$DEADHOST

# Filter CAS logfiles
date "$DATEFORMAT Scanning CAS debug files"
getHosts "cas"
checkHosts $TIMEOUT_SECONDS
for i in $ALIVEHOST ; do
    ssh $i "grep 'UA:.*limit exceeded' v2cas01/log/cas.debug" > $TMPDIR/$i.exceeded.txt &
done
wait
DEADCAS=$DEADHOST
NOCONNHOST="$DEADFIX $DEADCAS"

date "$DATEFORMAT Producing e-mail"
cat $TMPDIR/*.exceeded.txt >$GREPOUTFILE

cat - >$REPORTFILE <<__ORDERHEADER__
Today is $ReportDate.

----- ORDER rate limits exceeded -----

__ORDERHEADER__

$MAKE_REPORT -o <$GREPOUTFILE >>$REPORTFILE

cat - >>$REPORTFILE <<__QUOTEHEADER__

----- QUOTE rate limits exceeded -----

__QUOTEHEADER__
$MAKE_REPORT -q <$GREPOUTFILE >>$REPORTFILE

cat - >>$REPORTFILE <<__ENDQUOTE__

List of unreachable hosts: [$NOCONNHOST]

Recent reports at http://ccserv1/~cas

Produced by script $0
__ENDQUOTE__

date "$DATEFORMAT Sending e-mail"
mailx <$REPORTFILE -s"[$REPORT_ENVIRONMENT] Order/Quote Limit Exceeded detail report" $MAILLIST

if [[ $DEBUG != 1 ]] ; then
    date "$DATEFORMAT Copying report for non-production access"
    ftp -n -v $HOST_DEEPFRZ <<__FREEZE__
user $DEEPFREEZE_USER $DEEPFREEZE_USER
put $REPORTFILE $TODAY.lst
__FREEZE__

    remoteFile=$WEB_DIR/$TODAY/rateLimitExceeded.$REPORT_ENVIRONMENT.txt
    scp -q $REPORTFILE $USER_WEB@$HOST_WEB:$remoteFile
fi

date "$DATEFORMAT Cleaning up"
\rm -rf $TMPDIR

date "$DATEFORMAT Finished"
