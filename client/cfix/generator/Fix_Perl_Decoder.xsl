<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fix="http://www.cboe.com/fix/xslt"
                >

<xsl:output method="text" encoding="UTF-8" indent="no" name="text"/>

<xsl:strip-space elements="*"/>

<xsl:template match="text()"/>

<xsl:template match="/">

<xsl:result-document href="file:///d:/scripts/dvFix.pl" format="text">

<xsl:call-template name="main"/>
sub generate_tags()
{<xsl:text/>
<xsl:apply-templates/>
}
</xsl:result-document>

</xsl:template>

<xsl:template match="/fix/fields/field">
    $tags{'<xsl:value-of select="@number"/>'}="<xsl:value-of select="@name"/>(<xsl:value-of select="@number"/>)";<xsl:text/>
<xsl:for-each select="value">
    $value_tags{'<xsl:value-of select="../@number"/>_<xsl:value-of select="@enum"/>'}="<xsl:value-of select="../@name"/>(<xsl:value-of select="../@number"/>)=<xsl:value-of select="@description"/>(<xsl:value-of select="@enum"/>)";<xsl:text/>
</xsl:for-each>
<xsl:if test="@type='MULTIPLEVALUESTRING'">
    $multiple_values{'<xsl:value-of select="@number"/>'}="<xsl:value-of select="@name"/>(<xsl:value-of select="@number"/>)=";<xsl:text/>
<xsl:for-each select="value">
    $multiple_values{'<xsl:value-of select="../@number"/>_<xsl:value-of select="@enum"/>'}="<xsl:value-of select="@description"/>(<xsl:value-of select="@enum"/>)";<xsl:text/>
</xsl:for-each>
</xsl:if>
</xsl:template>

<xsl:template name="main">
<xsl:text/>#!/bin/perl
#
# Author: Dmitry Volpyansky
#
# Version: 04/27/2004 10:26
#
# Usage: perl dvFix.pl &lt; fixengine.log
#
# To show firm ABC only                  : perl dvFix.pl -firm ABC &lt; fixengine.log
# To show between 8:30:00 and 12:59:59   : perl dvFix.pl -from 8:30:00 -to 12:59:59 &lt; fixengine.log
# To hide Heartbeats/Resends/Resequences : perl dvFix.pl -nohb &lt; fixengine.log
#
# All of the above                       : perl dvFix.pl -firm ABC -from 8:30:00 -to 12:59:59 -nohb &lt; fixengine.log
#
# Note: This file was generated from /vob/client/cfix/generator/Fix_Perl_Decoder.xls for FIX4.2
#

while (@ARGV)
{
    if ($ARGV[0] =~ /-from/i)
    {
        $from = &amp;makeStringMillis($ARGV[1]);

        shift;
    }
    elsif ($ARGV[0] =~ /-to/i)
    {
        $to = &amp;makeStringMillis($ARGV[1]);

        shift;
    }
    elsif ($ARGV[0] =~ /-firm/i)
    {
        $firm = $ARGV[1];

        shift;
    }
    elsif ($ARGV[0] =~ /-(noheartbeat|nohb)/i)
    {
        $noheartbeat = "1";
    }
    elsif ($ARGV[0] =~ /-login/i)
    {
        $login = "1";
    }

    shift;
}

&amp;generate_tags();

while (&lt;>)
{
    next unless (/8=FIX\.4\./);

    s/[ \r\n]+$//;

    $line = $_;

    undef $time;

    if (/ (\d\d?):(\d\d):(\d\d).(\d\d) / || ### CAS-Style
        / (\d\d?):(\d\d):(\d\d):(\d\d\d) /) ### FIXCAS-Style
    {
        ($time = $&amp;) =~ s/ //g;
    }

    $_ = $line;

    if (defined($from) || defined($to))
    {
        if (/ (\d\d?):(\d\d):(\d\d).(\d\d) / || ### CAS-Style
            / (\d\d?):(\d\d):(\d\d):(\d\d\d) /) ### FIXCAS-Style
        {
            $millis = &amp;makeMillis($1, $2, $3, $4);
        }
        else
        {
            next;
        }

        $_ = $line;

        next if defined($from) &amp;&amp; $millis &lt; $from;
        last if defined($to)   &amp;&amp; $millis > $to;
    }

    if (defined($firm))
    {
        next unless ($line =~ /[\001](49|56)=$firm[\001]/);
    }

    if (defined($noheartbeat))
    {
        next if ($line =~ /[\001]35=[0124][\001]/);
    }

    $_ = $line;

    while (! /[\001]10=\d{3}[\001]/)
    {
        $x = &lt;>;

        $x =~ s/[ \r\n]+$//;

        last if !defined($x);

        $_ .= $x;
    }

    s/^.*?(8=FIX\.4\..*?[\001]10=\d{3}[\001]).*$/\1/g;

    if (defined($time))
    {
        print "\n[$time] $_\n";
    }
    else
    {
        print "\n$_\n";
    }

    foreach $token (split(/[\001]/))
    {
        ($tag, $value) = split(/=/, $token);

        next unless length($tag) > 0;

        $x = $value_tags{$tag . '_' . $value};

        if ($x)
        {
            print "   $x\n";
        }
        else
        {
            $x = $multiple_values{$tag};

            if ($x)
            {
                $x = $multiple_values{$tag} . "=";

                foreach $val (split(/ /, $value))
                {
                    $x = $x . $multiple_values{$tag . "_" . $val} . ",";
                }

                chop($x);

                print "   $x\n";
            }
            else
            {
                $x = $tags{$tag};
                if ($x)
                {
                    print "   $x=$value\n";
                }
                else
                {
                    print "   $tag($tag)=$value\n";
                }
            }
        }
    }
}

sub makeStringMillis()
{
    $time = shift(@_);

    if ($time =~ /^(\d{1,2}):?$/)
    {
        return makeMillis($1, 0, 0, 0);
    }

    if ($time =~ /^(\d{1,2}):(\d{1,2}):?$/)
    {
        return makeMillis($1, $2, 0, 0);
    }

    if ($time =~ /^(\d{1,2}):(\d{1,2}):(\d{1,2})\[.:]?$/)
    {
        return makeMillis($1, $2, $3, 0);
    }

    if ($time =~ /^(\d{1,2}):(\d{1,2}):(\d{1,2})\[.:](\d{1,3})$/)
    {
        return makeMillis($1, $2, $3, $4);
    }

    return 0;
}

sub makeMillis()
{
    $millis  = shift(@_) * 24 * 60 * 60 * 1000;
    $millis += shift(@_) * 60 * 60 * 1000;
    $millis += shift(@_) * 60 * 1000;
    $x = shift(@_);
    if (length($x) == 2)
    {
        $millis += $x * 10;
    }
    elsif (length($x) == 3)
    {
        $millis += $x;
    }
    else
    {
        $millis += $x * 100;
    }

    return $millis;
}

</xsl:template>

</xsl:stylesheet>
