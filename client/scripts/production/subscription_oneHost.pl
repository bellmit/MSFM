#!/usr/bin/perl -w
# Input: mdcas.log
# Output 1: csv timestamp,user,product,interface,# of duplicate subscriptions
# Output 2: csv file listing user,class subscribed to

# -------------------- Constants --------------------

$DEBUG = 0;
$SEP = "\001";
chomp( $PROG = `basename $0` );
chomp( $PROGDIR = `dirname $0` );
if ($PROGDIR eq ".")
{
    chomp( $PROGDIR = `pwd` );
}

# -------------------- Subroutines --------------------

# Funny thing about split: if you have a delimiter at the very
# start and very end of the string, the first delimiter creates
# an empty first element, but the last delimiter does NOT create
# an empty last element.

sub dumpSubscription
{
    my $user;
    for $user (sort keys %Subscription)
    {
        print "  User $user\n";
        my $pi;
        for $pi (sort keys %{ $Subscription{$user} } )
        {
            my @parts = split /$SEP/o, $Subscription{$user}->{$pi};
            shift @parts;
            my $nparts = @parts;
            print "    $pi -> [$nparts]";
            my $s;
            for ($s = 0; $s < $nparts; ++$s)
            {
                print " $parts[$s]";
            }
            print "\n";
        }
    }
}

#low information 2006/8/4 5:25:48.02 mdcas01v2File Prodmdcas01v2mdcas98 MDCAS "RemoteCASMarketDataServiceHome:7993935>>> Sub:oid for user:DC2 session:com.cboe.remoteApplication.session.RemoteCASSessionManagerImpl@9f6ece com.cboe.application.supplier.proxy.overlayProxy.CurrentMarketV3ConsumerOverlayProxy@8fba304a sessionName:W_MAIN classKey:157704765" 0 
sub addSubscription
{
    # Extract data from input line

    my ($timestamp, $user, $userSession, $tradingSession,
        $product, $class, $interface);
    /^\S+ \S+ (\S+ \S+)/ && ($timestamp = $1);
    /user:(\S+)/ && ($user = $1);
    /session:[^@]+@(\S+) ([^@]+)/ && ($userSession = $1, $interface = $2);
    /sessionName:(\S+)/ && ($tradingSession = $1);
    /classKey:(\d+)/ && ($class = $1);
    $product = "c$class"; # in case line has no productKey
    /productKey:(\d+)/ && ($product = $1);
    $product = "$tradingSession:$product";

    # Remove package names from the name of the interface object
    while ($interface =~ /\./)
    {
        $interface =~ s/^.*\.//;
    }

    # Record subscription, count concurrent subscriptions

    my $pi = "$product,$interface";

    print "SUBSCRIBE $user" . "[$pi] -> $userSession,$timestamp\n"
      if $DEBUG gt 0;

    $Subscription{$user}->{$pi} = $SEP unless defined $Subscription{$user}->{$pi};
    $Subscription{$user}->{$pi} .= "$userSession,$timestamp$SEP";
    &dumpSubscription if $DEBUG gt 1;
    my @parts = split /$SEP/, $Subscription{$user}->{$pi};
    shift @parts;
    my $nDups = (scalar @parts) - 1;
    print JOURNAL "'$timestamp',$user,$pi,$nDups\n";

    # Report 2: for each user, list classes subscribed to

    $key = "$user,$class";
    $ClassSub{$key} = 1;

    # Additional info: how many separate users logged in?
    $UserLogin{$user} = 1;

    # Additional info: how many subscriptions of each type were made?
    ++ $InterfaceSubCount{$interface};
}

#low information 2006/8/4 5:25:34.11 mdcas01v2File Prodmdcas01v2mdcas98 MDCAS "RemoteCASMarketDataServiceHome:7993935>>> UnSub:oid for user:DC2 session:com.cboe.remoteApplication.session.RemoteCASSessionManagerImpl@9f6ece com.cboe.application.supplier.proxy.ExpectedOpeningPriceConsumerProxy@d47f4736 sessionName:W_MAIN classKey:157704765" 0 
sub endSubscription
{
    my ($user, $userSession, $tradingSession, $product, $class, $interface);
    /user:(\S+)/ && ($user = $1);
    /session:[^@]+@(\S+) ([^@]+)/ && ($userSession = $1, $interface = $2);
    /sessionName:(\S+)/ && ($tradingSession = $1);
    /classKey:(\d+)/ && ($class = $1);
    $product = "c$class"; # in case line has no productKey
    /productKey:(\d+)/ && ($product = $1);
    $product = "$tradingSession:$product";

    while ($interface =~ /\./)
    {
        $interface =~ s/^.*\.//;
    }

    # Subscription might be xxxConsumerProxy or xxxConsumerOverlayProxy
    # or xxxConsumerFlushProxy, but unsubscription is xxxConsumerProxy.
    # If we can't find unsubscription as presented, try modifying it to
    # one of the others.
    if (!exists $Subscription{$user}->{"$product,$interface"})
    {
        $interface =~ s/ConsumerProxy/ConsumerOverlayProxy/;
        if (!exists $Subscription{$user}->{"$product,$interface"})
        {
            $interface =~ s/ConsumerOverlayProxy/ConsumerFlushProxy/;
            if (!exists $Subscription{$user}->{"$product,$interface"})
            {
                $interface =~ s/ConsumerFlushProxy/ConsumerProxy/;
                print "==== Can't find Subscription{$user}->{$product,$interface}\n";
                return;
            }
        }
    }

    my $subs = $Subscription{$user}->{"$product,$interface"};
    if ($subs =~ /$SEP($userSession,[^$SEP]+)$SEP/)
    {
        # have a subscription in this list
        my $matched = $1;
        $subs =~ s/$SEP$userSession,[^$SEP]+$SEP/$SEP/;
        $Subscription{$user}->{"$product,$interface"} = $subs;
        print "UNSUBSCRIBE $user" . "[$product,$interface] -> $matched\n"
           if $DEBUG gt 0;
        &dumpSubscription if $DEBUG gt 1;
        return;
    }

    # Can't find subscription to remove. This commonly happens when logout is
    # processed while unsubscribe calls are still in queue.
}

#low information 2006/8/4 6:58:12.90 mdcas01v2File Prodmdcas01v2mdcas98 MDCAS "RemoteCASSessionManagerHome:<anonymous>>>> calling publishLogout for sessionManager:user:SZF session:com.cboe.remoteApplication.session.RemoteCASSessionManagerImpl@153e9cf" 0 
sub endUserSession
{
    my ($user, $userSession);
    /user:(\S+)/ && ($user = $1);
    / session:[^@]+@([a-fA-F0-9]+)/ && ($userSession = $1);

    print "LOGOUT user[$user] userSession[$userSession]\n"
      if $DEBUG gt 0;

    my $pi; # product, interface
    for $pi (sort keys %{ $Subscription{$user} } )
    {
        my $subs = $Subscription{$user}->{$pi};
        if ($subs =~ /$SEP($userSession,[^$SEP]+)$SEP/)
        {
            my $matched = $1;
            # have a subscription in this list
            $subs =~ s/$SEP$userSession,[^$SEP]+$SEP/$SEP/;
            $Subscription{$user}->{$pi} = $subs;
            print "   removing subscription $pi $matched\n"
              if $DEBUG > 0;
        }
    }
    &dumpSubscription if $DEBUG > 1;
}

# -------------------- Main Program --------------------

die "Usage: $PROG subscriptionJournal userClassReport\n" if $ARGV[1] eq "";

$SubJournal = $ARGV[0];
$UserClassReport = $ARGV[1];

%Subscription = ();
%ClassSub = ();
%UserLogin = ();
%InterfaceSubCount = ();

open JOURNAL, ">$SubJournal";
print JOURNAL "timestamp,user,product,interface,redundantSubs\n";
while (<STDIN>)
{
    &addSubscription if / Sub:oid/ ;
    &endSubscription if /UnSub:oid/ ;
    &endUserSession  if /publishLogout/ ;
}
print JOURNAL "#-----\nnUsers\n";
print JOURNAL (scalar keys %UserLogin) . "\n";
print JOURNAL "#-----\ninterface,nSubscriptions\n";
for $interface (sort keys %InterfaceSubCount)
{
    print JOURNAL "$interface,$InterfaceSubCount{$interface}\n";
}
print JOURNAL "#-----\n";
print JOURNAL "#Produced by script $PROGDIR/$PROG\n";
close JOURNAL;

# No header for this report: it's used as input to another script.
open CLASS,">$UserClassReport";
for $key (sort keys %ClassSub)
{
    print CLASS "$key\n";
}
close CLASS;
