package com.cboe.cfix.fix.fix42.generated.fields;

/**
 * FixResetSeqNumFlagField.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * This GENERATED file implements FIX Protocol's Field Tag [141] (known as ResetSeqNumFlag).
 *
 * WARNING: This entire file is completely generated by XSLT stylesheets from CboeFIX42.xml file.
 *
 */

import com.cboe.cfix.interfaces.*;
import com.cboe.cfix.util.*;

public abstract class FixResetSeqNumFlagField implements FixFieldIF, HasBooleanValueIF
{   
    public static final int    TagID         =  141;
    public static final String TagIDAsString = "141";
    public static final char[] TagIDAsChars  = {'1','4','1'};
    public static final String TagName       = "ResetSeqNumFlag";
    public static final String TagFixType    = "BOOLEAN";

    public static final boolean YesResetSequenceNumbers              = true;
    public static final String  string_YesResetSequenceNumbers       = FIX_YES;
    public static final String  tagged_YesResetSequenceNumbers       = TagIDAsString + EQUALS + string_YesResetSequenceNumbers + SOH;
    public static final char    char_YesResetSequenceNumbers         = 'Y';
    public static final char[]  taggedchars_YesResetSequenceNumbers  = {'1','4','1', EQUALSchar, 'Y', SOHchar};
    public static final String  text_YesResetSequenceNumbers         = "YesResetSequenceNumbers";
    public static final boolean No                                   = false;
    public static final String  string_No                            = FIX_NO;
    public static final String  tagged_No                            = TagIDAsString + EQUALS + string_No + SOH;
    public static final char    char_No                              = 'N';
    public static final char[]  taggedchars_No                       = {'1','4','1', EQUALSchar, 'N', SOHchar};
    public static final String  text_No                              = "No";

    private static FixResetSeqNumFlagField flyweightYesResetSequenceNumbers;
    public static final FixResetSeqNumFlagField flyweightYesResetSequenceNumbers()
    {
        if (flyweightYesResetSequenceNumbers == null)
        {
            synchronized(FixResetSeqNumFlagField.class)
            {
                if (flyweightYesResetSequenceNumbers == null)
                {
                    flyweightYesResetSequenceNumbers  = new FixResetSeqNumFlagField()
                    {public boolean hasValue()                        {return true;}
                     public String  getValue()                        {return string_YesResetSequenceNumbers;}
                     public String  getValueDescription()             {return text_YesResetSequenceNumbers;}
                     public String  toString()                        {return new StringBuilder(64).append(TagName).append("{").append(TagIDAsString).append("} [").append(string_YesResetSequenceNumbers).append("|").append(text_YesResetSequenceNumbers).append("]").toString();}
                     public boolean isYesResetSequenceNumbers()       {return true;}
                     public boolean booleanValue()                    {return YesResetSequenceNumbers;}
                     public void    accept(FixMessageBuilderIF fixMessageBuilder) {fixMessageBuilder.append(taggedchars_YesResetSequenceNumbers);}
                    };
                }
            }
        }

        return flyweightYesResetSequenceNumbers;
    }

    private static FixResetSeqNumFlagField flyweightNo;
    public static final FixResetSeqNumFlagField flyweightNo()
    {
        if (flyweightNo == null)
        {
            synchronized(FixResetSeqNumFlagField.class)
            {
                if (flyweightNo == null)
                {
                    flyweightNo  = new FixResetSeqNumFlagField()
                    {public boolean hasValue()                        {return true;}
                     public String  getValue()                        {return string_No;}
                     public String  getValueDescription()             {return text_No;}
                     public String  toString()                        {return new StringBuilder(64).append(TagName).append("{").append(TagIDAsString).append("} [").append(string_No).append("|").append(text_No).append("]").toString();}
                     public boolean isNo()                            {return true;}
                     public boolean booleanValue()                    {return No;}
                     public void    accept(FixMessageBuilderIF fixMessageBuilder) {fixMessageBuilder.append(taggedchars_No);}
                    };
                }
            }
        }

        return flyweightNo;
    }


    public boolean isYesResetSequenceNumbers()   {return false;}
    public boolean isNo()                        {return false;}

    public final int    getTag()                                      {return TagID;}
    public final String getTagAsString()                              {return TagIDAsString;}
    public final char[] getTagAsChars()                               {return TagIDAsChars;}
    public final String getTagName()                                  {return TagName;}
    public       String getValueDescription()                         {return getValue();}
    public       void   accept(FixMessageBuilderIF fixMessageBuilder) {fixMessageBuilder.append(TagIDAsChars, getValue());}
    public       String toString()                                    {return new StringBuilder(64).append(TagName).append("{").append(TagIDAsString).append("} [").append(getValue()).append(']').toString();}

    public static FixResetSeqNumFlagField create(char[] array, int offset, int length)
    {
        if (length == 1)
        {                                                                                           
            if (array[offset] == char_YesResetSequenceNumbers) return flyweightYesResetSequenceNumbers();
            if (array[offset] == char_No)                      return flyweightNo();
        }
        return null;
    }

    public static final char[] truefalse_Flyweight  = new char[] {FixFieldIF.FIX_YESchar, FixFieldIF.FIX_NOchar};
    public static FixResetSeqNumFlagField create(boolean param)
    {
        return create(truefalse_Flyweight, param ? 0 : 1, 1);
    }                                                                                               
}