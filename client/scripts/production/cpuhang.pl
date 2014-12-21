#!/usr/bin/perl -w

# -------------------- Configuration --------------------

@REMOTEHOST = ("mdcas02" , "mdcas04",
  "prdmdx04a", "prdmdx06a", "prdmdx09a", "prdmdx10a", "prdmdx21a", "prdmdx22a",
  "prdmdx30a", "prdmdx95a", "prdmdx98a", "prdmdx99a", "prdmdxnoa", "prdmdxbka"
);
@DAYLIST = (""); # empty string for today and/or 2-digit day-of-month strings
$MAILLIST = "lyncht\@cboe.com simkin\@cboe.com";
$MAXGAP = 63;
$INFRA = "infrap";

# -------------------- Constants --------------------

$Now = `date "+%Y%m%d.%H%M%S"`;
chomp $Now;
$TMPFILE = "/tmp/cpuhang.$Now.tmp";
$REPORTFILE = "/tmp/cpuhang.$Now.report";

$YES = 1;
$NO = 0;

# -------------------- Subroutines --------------------

sub findGaps()
{
    my $filename = shift;
    my ($host, $date);
    my $firstRead = $YES;
    my ($prevTime, $prevDayseconds);
    open IN, "<$filename";
    while (<IN>)
    {
        if (m!\d\d/\d\d/\d\d!)
        {
            # SunOS mdcas02 5.10 Generic_Patch i86pc    07/10/2006
            my @parts = split;
            $host = $parts[1];
            $date = $parts[5];
            print REPORT "HOST $host DATE $date\n";
        }
        next unless /\d\d:\d\d:\d\d/;
        # 00:01:00       0       0       0     100

        my $time = (split)[0];
        my ($hour, $minute, $second) = split /:/, $time;
        my $dayseconds = ((0 + $hour) * 60 + $minute) * 60 + $second;
        if ($firstRead != $YES)
        {
            if (($dayseconds - $prevDayseconds) > $MAXGAP)
            {
                print REPORT "   gap $prevTime -" . " $time\n";
            }
        }
        $prevDayseconds = $dayseconds;
        $prevTime = $time;
        $firstRead = $NO;
    }
    close IN;
}


# -------------------- Main Program --------------------

open REPORT, ">$REPORTFILE";

for $Host (@REMOTEHOST)
{
    for $Day (@DAYLIST)
    {
        my $args = ($Day eq "") ? "" : "-f /var/adm/sa/sa$Day";
        system "ssh $INFRA"."@"."$Host \"sar $args\" >$TMPFILE";
        &findGaps($TMPFILE);
    } # $day
}

print REPORT "\nProduced by script $0\n";

unlink $TMPFILE;
system "mailx <$REPORTFILE -s 'V40z CPU hangs' $MAILLIST";
unlink $REPORTFILE;
