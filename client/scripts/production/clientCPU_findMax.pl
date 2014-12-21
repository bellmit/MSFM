#!/usr/bin/perl -w
#
# Analyze run_dir/log/monitorInfra.log to find maximum CPU usage on a host

# -------------------- Constants --------------------

$FIRST_HOUR = 7;
$LAST_HOUR = 15;
$HOST = $ARGV[0];
$YES = 1;
$NO = 0;

# -------------------- Subroutines --------------------

sub report
{
    if (defined $recordTime)
    {
        print "$HOST,$recordTime," . (sprintf "%4.1f", $recordUsage) . "%\n";
    }
    else
    {
        print "$HOST,,\n";
    }
}

sub getTime
{
    my $time;
    /^\S+\s+\S+\s+\d+\s+(\S+)\s+\d+/ && ($time = $1);

    # Don't know why this happens, but sometimes it does
    return if !defined $time;

    # Check for maximum CPU in each hour from 7:00 to 15:30
    # (being lazy, make it 15:59)
    my $hour;
    $time =~ /^(\d\d)/ && ($hour = 0 + $1);
    return if $hour < $FIRST_HOUR;
    if ($hour != $CurrentHour)
    {
        &report if $CurrentHour >= $FIRST_HOUR && $CurrentHour <= $LAST_HOUR;
        $recordUsage = 0.0;
        undef $recordTime;
        $CurrentHour = $hour;
    }

    $Timestamp = $time;
}

sub getCPU
{
    if (/All Processors/)
    {
        # If we see this string, ignore lines that don't have this string
        $RequireAll = $YES;
    }
    elsif ($RequireAll == $YES)
    {
        # Read only "All Processors" line, and this line isn't it
        return;
    }

    my $Idle;
    /(\S+)% idle/ && ($Idle = $1);
    my $newUsage = 100.0 - $Idle;
    if ($newUsage > $recordUsage)
    {
        $recordTime = $Timestamp;
        $recordUsage = $newUsage;
    }
}

# -------------------- Main program --------------------

$RequireAll = $NO;
$recordUsage = 0.0;
$CurrentHour = 0;
while (<STDIN>)
{
    /pstats/ && (&getTime);
    /CPU States/ && (&getCPU);
}

&report if $CurrentHour <= $LAST_HOUR;
