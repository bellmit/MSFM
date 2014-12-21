#
# generateCollectionItem.pl
#
#    used to generate CommonFacilities' com/cboe/util/collections Java classes
#
# author: Dmitry Volpyansky
#

use strict;

my $KEYTYPE = shift;
my $VALTYPE = shift;
my $IMPORTS = shift;

my $KEYNAME;
my $VALNAME;
my $KEYEMPTY;
my $VALEMPTY;
my $KEYSUBTYPE;
my $VALSUBTYPE;

my $imports;

my %primitiveMap = (
    "int"  => "Integer",
    "long" => "Long",
    "char" => "Char",
    "byte" => "Byte");

if ($KEYTYPE =~ /^(float|double)$/)
{
    print STDERR "Can't have '$1' as a key\n";
    exit 0;
}

if ($KEYTYPE =~ /^(int|long|char|byte)$/)
{
    $KEYNAME    = ucfirst $1;
    $KEYEMPTY   = 0;
    $KEYSUBTYPE = 'primitive';
}
elsif ($KEYTYPE =~ /^(Comparable|String)$/)
{
    $KEYNAME    = $1;
    $KEYEMPTY   = 'null';
    $KEYSUBTYPE = 'comparable';
}
else
{
    $KEYNAME    = $KEYTYPE;
    $KEYEMPTY   = 'null';
    $KEYSUBTYPE = 'object';
}

if ($VALTYPE =~ /^(int|long|char|byte|float|double)$/)
{
    $VALNAME    = ucfirst $1;
    $VALEMPTY   = 0;
    $VALSUBTYPE = 'primitive';
}
elsif ($VALTYPE =~ /^(Comparable|String)$/)
{
    $VALNAME    = $1;
    $VALEMPTY   = 'null';
    $VALSUBTYPE = 'comparable';
}
else
{
    $VALNAME    = $VALTYPE;
    $VALEMPTY   = 'null';
    $VALSUBTYPE = 'object';
}

if ($IMPORTS)
{
    for my $x (split(/[;: ]/, $IMPORTS))
    {
        if (length($x))
        {
            $imports .= "import ${x};\n";
        }
    }
}

my $directory = "../Java/com/cboe/util/collections";

&process( "KeyValueMap.template",                   "${directory}/${KEYNAME}${VALNAME}Map.java"                      );
&process( "KeyValueMapVisitor.template",            "${directory}/${KEYNAME}${VALNAME}MapVisitor.java"               );
&process( "KeyValueMapVisitorImpl.template",        "${directory}/${KEYNAME}${VALNAME}MapVisitorImpl.java"           );
&process( "KeyValueMapArrayHolder.template",        "${directory}/${KEYNAME}${VALNAME}MapArrayHolder.java"           );
&process( "KeyValueMapArrayHolderImpl.template",    "${directory}/${KEYNAME}${VALNAME}MapArrayHolderImpl.java"       );
&process( "KeyValueMapPolicy.template",             "${directory}/${KEYNAME}${VALNAME}MapPolicy.java"                );
&process( "KeyValueMapPolicyImpl.template",         "${directory}/${KEYNAME}${VALNAME}MapPolicyImpl.java"            );
&process( "KeyValueMapModifyPolicy.template",       "${directory}/${KEYNAME}${VALNAME}MapModifyPolicy.java"          );
&process( "KeyValueMapModifyPolicyImpl.template",   "${directory}/${KEYNAME}${VALNAME}MapModifyPolicyImpl.java"      );
&process( "TypedArrayHolder.template",              "${directory}/${KEYNAME}ArrayHolder.java"                        );
&process( "TypedArrayHolderImpl.template",          "${directory}/${KEYNAME}ArrayHolderImpl.java"            ,  "y"  );
&process( "TypedVisitor.template",                  "${directory}/${KEYNAME}Visitor.java"                            );
&process( "TypedVisitorImpl.template",              "${directory}/${KEYNAME}VisitorImpl.java"                        );

sub process()
{
    my $infile             = shift;
    my $outfile            = shift;
    my $swapKeyValue       = shift;

    my $localKEYNAME       = $KEYNAME;
    my $localKEYTYPE       = $KEYTYPE;
    my $localVALNAME       = $VALNAME;
    my $localVALTYPE       = $VALTYPE;
    my $localKEYEMPTY      = $KEYEMPTY;
    my $localVALEMPTY      = $VALEMPTY;
    my $localKEYSUBTYPE    = $KEYSUBTYPE;
    my $localVALSUBTYPE    = $VALSUBTYPE;

    my $duplicateState     = "false";
    my $syncText           = "";
    my $unsyncText         = "";
    my $interfaceText      = "";
    my $className          = "";
    my $primitiveValueOnly = "";

    open(OUTFILE, "> ${outfile}") || die "no such file ${outfile}\n";
    open(INFILE,  "< ${infile}")  || die "no such file ${infile}\n";
    while (<INFILE>)
    {
        if (/IMPORTS/)
        {
            if ($imports)
            {
                print OUTFILE $imports;
            }
            else
            {
                $_ = <INFILE>;
            }

            next;
        }

        if (/VALINVALID/)
        {
            if (${localVALSUBTYPE} =~ /primitive/i)
            {
                s/VALINVALID/$primitiveMap{$localVALTYPE}.MIN_VALUE/g;
            }
            elsif (${localVALSUBTYPE} =~ /object|comparable/i)
            {
                s/VALINVALID/null/g;
            }
        }

        if (/KEYINVALID/)
        {
            if (${localKEYSUBTYPE} =~ /primitive/i)
            {
                s/KEYINVALID/$primitiveMap{$localKEYTYPE}.MIN_VALUE/g;
            }
            elsif (${localKEYSUBTYPE} =~ /object|comparable/i)
            {
                s/KEYINVALID/null/g;
            }
        }

        if (/KEYEQUALS/)
        {
            if (${localKEYSUBTYPE} =~ /primitive/i)
            {
                s/KEYEQUALS/key == keys[keyIndex]/g;
            }
            elsif (${localKEYSUBTYPE} =~ /object|comparable/i)
            {
                s/KEYEQUALS/key != null \&\& key.equals(keys[keyIndex])/g;
            }
        }

        if (/VALEQUALS/)
        {
            if (${localVALSUBTYPE} =~ /primitive/i)
            {
                s/VALEQUALS/value == values[keyIndex]/g;
            }
            elsif (${localVALSUBTYPE} =~ /object|comparable/i)
            {
                s/VALEQUALS/value != null \&\& value.equals(values[keyIndex])/g;
            }
        }

        if (/KEYINDEX/)
        {
            if (${localKEYSUBTYPE} =~ /primitive/i)
            {
                s/KEYINDEX/(int) (key \& highestBucketIndex)/g;
            }
            elsif (${localKEYSUBTYPE} =~ /object|comparable/i)
            {
                s/KEYINDEX/(int) (key.hashCode() \& highestBucketIndex)/g;
            }
        }

        if (/KEYFIND/)
        {
            if (${localKEYSUBTYPE} =~ /primitive|comparable/i)
            {
                s/KEYFIND/return MapHelper.binarySearch(keys, key, keyCount);/g;
            }
            elsif (${localKEYSUBTYPE} =~ /object/i)
            {
                my $x = <<EOF;
            if (key == null)
            {
                for (int keyIndex = 0; keyIndex < keyCount; keyIndex++)
                {
                    if (keys[keyIndex] == null)
                    {
                        return keyIndex;
                    }
                }
            }
            else
            {
                for (int keyIndex = 0; keyIndex < keyCount; keyIndex++)
                {
                    if (key.equals(keys[keyIndex]))
                    {
                        return keyIndex;
                    }
                }
            }

            return -1;
EOF
                s/KEYFIND/$x/g;
            }
        }

        if (/KEYarray/)
        {
            if (${localKEYSUBTYPE} =~ /primitive/i || ${localKEYTYPE} eq "String")
            {
                s/KEYarray/array/g;
#                s/KEYarray/MapHelper.array/g;
            }
            else
            {
                s/KEYarray/array/g;
            }
        }

        if (/VALarray/)
        {
            if (${localVALSUBTYPE} =~ /primitive/i || ${localVALTYPE} eq "String")
            {
#                s/VALarray/MapHelper.array/g;
                s/VALarray/array/g;
            }
            else
            {
                s/VALarray/array/g;
            }
        }

        if (/ARRAYCLONE/)
        {
            $_ = "";

#            if (${localKEYSUBTYPE} !~ /primitive/i && ${localKEYTYPE} ne "String")
            {
                $_ .= &arrayclone(${localKEYTYPE});
            }

#            if (${localVALSUBTYPE} !~ /primitive/i && ${localVALTYPE} ne "String")
            {
                if (${localKEYTYPE} ne ${localVALTYPE})
                {
                    $_ .= &arrayclone(${localVALTYPE});
                }
            }

            next if length($_) == 0;
        }

        s/KEYNAME/${localKEYNAME}/g;
        s/KEYTYPE/${localKEYTYPE}/g;
        s/VALNAME/${localVALNAME}/g;
        s/VALTYPE/${localVALTYPE}/g;
        s/KEYEMPTY/${localKEYEMPTY}/g;
        s/VALEMPTY/${localVALEMPTY}/g;

        if (/DUPLICATE_WITH_SYNCHRONIZED_START/)
        {
            $duplicateState = "true";
            next;
        }

        if (/DUPLICATE_WITH_SYNCHRONIZED_END/)
        {
            $duplicateState = "false";

            $interfaceText .= "    }\n";

            print OUTFILE "// bucket interface\n";
            print OUTFILE $interfaceText;
            print OUTFILE "// unsynchronized bucket \n";
            print OUTFILE $unsyncText;
            print OUTFILE "// synchronized bucket\n";
            print OUTFILE $syncText;

            $syncText      = "";
            $unsyncText    = "";
            $interfaceText = "";

            next;
        }

        if ($duplicateState eq "true")
        {
            my $temp = $_;

            if ($temp =~ /protected class\s+(\S+)\s*$/)
            {
                $className = $1;

                my $xxx = $temp;
                $xxx =~ s/^(\s+protected class )(.*)\s*/\1Synchronized\2 implements $className\n/;
                $syncText .= $xxx;

                $xxx = $temp;
                $xxx =~ s/^(\s+protected class )(.*)\s*/\1Unsynchronized\2 implements $className\n/;
                $unsyncText .= $xxx;

                $interfaceText .= "    public interface $className\n    {\n";

                next;
            }

            if ($temp =~ /public\s+\S+\(/) # constructors
            {
                my $xxx = $temp;
                $xxx =~ s/CLASSNAME/Synchronized${className}/;
                $syncText .= $xxx;

                $xxx = $temp;
                $xxx =~ s/CLASSNAME/Unsynchronized${className}/;
                $unsyncText .= $xxx;

                next;
            }

            if ($temp =~ /^(\s+public )(.*\(.*\))\s*$/)
            {
                my $xxx = $temp;
                $xxx =~ s/^(\s+public)(.*)\s*$/\1 synchronized\2\n/;
                $syncText .= $xxx;

                $xxx = $temp;
                $unsyncText .= $xxx;

                $xxx = $temp;
                $xxx =~ s/^\s+(public) (.*)\s*$/        \1 \2;\n/;
                $interfaceText .= $xxx;

                next;
            }

            $syncText   .= $temp;
            $unsyncText .= $temp;

            next;
        }

        print OUTFILE $_;
    }
    close(INFILE);
    close(OUTFILE);
}

sub arrayclone()
{
    my $TYPE = shift;

return <<EOF;

        private ${TYPE}[] arrayclone(${TYPE} from)
        {
            ${TYPE}[] to = new ${TYPE}[1];

            to[0] = from;

            return to;
        }

        private ${TYPE}[] arrayclone(${TYPE}[] from)
        {
            if (from == null)
            {
                return null;
            }

            return (${TYPE}[]) from.clone();
        }

        private ${TYPE}[] arrayclone(${TYPE}[] from, int toSize)
        {
            if (from == null)
            {
                return null;
            }

            ${TYPE}[] to = new ${TYPE}[toSize];
            System.arraycopy(from, 0, to, 0, from.length);
            return to;
        }

        private ${TYPE}[] arrayclone(${TYPE}[] from, int fromOffset, int fromSize, int toSize)
        {
            if (from == null)
            {
                return null;
            }

            ${TYPE}[] to = new ${TYPE}[toSize];
            System.arraycopy(from, fromOffset, to, 0, fromSize);
            return to;
        }

        private ${TYPE}[] arraycloneCombine(${TYPE}[] from, int startOffset, int endOffset, int toSize)
        {
            if (from == null)
            {
                return null;
            }

            ${TYPE}[] to = new ${TYPE}[toSize];
            int firstPortion = from.length - startOffset;
            System.arraycopy(from, startOffset, to, 0,            firstPortion);
            System.arraycopy(from, 0,           to, firstPortion, endOffset);
            return to;
        }

        private ${TYPE}[] arraycloneExpandGap(${TYPE}[] from, int fromOffset, int fromSize, int toSize, int gapOffset, int gapLength)
        {
            if (from == null)
            {
                return null;
            }

            ${TYPE}[] to = new ${TYPE}[toSize];
            int gap = gapOffset + gapLength;
            System.arraycopy(from, fromOffset, to, 0,   gapOffset);
            System.arraycopy(from, gapOffset,  to, gap, fromSize - gapOffset);
            return to;
        }
EOF
}
