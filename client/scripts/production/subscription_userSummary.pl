#!/usr/bin/perl -w
# Usage: $0 mdcas-file-1 mdcas-file-2 ... >some-result.csv
# Count subscriptions and active mdcas boxes per user. Input files are
# secondary output of script subscription_oneHost.pl

$PROG=`basename $0`;
chomp $PROG;

%UserBox = ();
for $infile (@ARGV)
{
    open INFILE, "<$infile";
    while (<INFILE>)
    {
        my ($user, $product) = split /,/;
        my $key = "$user,$infile";
        # Keep track of which files had subscriptions for this user, and
        # which products the user subscribed to
        $UserBox{$user}->{$infile} = 1;
        $UserSub{$user}->{$product} = 1;
    }
    close INFILE;
}

print "name,nHosts,nProducts\n";
for $user (sort keys %UserBox)
{
    my $nboxes = scalar keys %{ $UserBox{$user} };
    my $nproducts = scalar keys %{ $UserSub{$user} };
    print "$user,$nboxes,$nproducts\n";
}
print "#Produced by script $PROG\n";
