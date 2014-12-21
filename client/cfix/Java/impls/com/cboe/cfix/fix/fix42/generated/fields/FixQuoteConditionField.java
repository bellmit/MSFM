package com.cboe.cfix.fix.fix42.generated.fields;

/**
 * FixQuoteConditionField.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * This GENERATED file implements FIX Protocol's Field Tag [276] (known as QuoteCondition).
 *
 * WARNING: This entire file is completely generated by XSLT stylesheets from CboeFIX42.xml file.
 *
 */

import com.cboe.cfix.interfaces.*;
import com.cboe.cfix.util.*;
import com.cboe.client.util.*;

public abstract class FixQuoteConditionField implements FixFieldIF
{   
    public static final int    TagID         =  276;
    public static final String TagIDAsString = "276";
    public static final char[] TagIDAsChars  = {'2','7','6'};
    public static final String TagName       = "QuoteCondition";
    public static final String TagFixType    = "MULTIPLEVALUESTRING";

    public static final char    OpenActive                    = 'A';
    public static final String  string_OpenActive             = "A";
    public static final String  tagged_OpenActive             = TagIDAsString + EQUALS + string_OpenActive + SOH;
    public static final char[]  taggedchars_OpenActive        = {'2','7','6', EQUALSchar, 'A', SOHchar};
    public static final int     bitmask_OpenActive            = 1 << 1; public static final String text_OpenActive             = "OpenActive";
    public static final char    ClosedInactive                = 'B';
    public static final String  string_ClosedInactive         = "B";
    public static final String  tagged_ClosedInactive         = TagIDAsString + EQUALS + string_ClosedInactive + SOH;
    public static final char[]  taggedchars_ClosedInactive    = {'2','7','6', EQUALSchar, 'B', SOHchar};
    public static final int     bitmask_ClosedInactive        = 1 << 2; public static final String text_ClosedInactive         = "ClosedInactive";
    public static final char    ExchangeBest                  = 'C';
    public static final String  string_ExchangeBest           = "C";
    public static final String  tagged_ExchangeBest           = TagIDAsString + EQUALS + string_ExchangeBest + SOH;
    public static final char[]  taggedchars_ExchangeBest      = {'2','7','6', EQUALSchar, 'C', SOHchar};
    public static final int     bitmask_ExchangeBest          = 1 << 3; public static final String text_ExchangeBest           = "ExchangeBest";
    public static final char    ConsolidatedBest              = 'D';
    public static final String  string_ConsolidatedBest       = "D";
    public static final String  tagged_ConsolidatedBest       = TagIDAsString + EQUALS + string_ConsolidatedBest + SOH;
    public static final char[]  taggedchars_ConsolidatedBest  = {'2','7','6', EQUALSchar, 'D', SOHchar};
    public static final int     bitmask_ConsolidatedBest      = 1 << 4; public static final String text_ConsolidatedBest       = "ConsolidatedBest";
    public static final char    Locked                        = 'E';
    public static final String  string_Locked                 = "E";
    public static final String  tagged_Locked                 = TagIDAsString + EQUALS + string_Locked + SOH;
    public static final char[]  taggedchars_Locked            = {'2','7','6', EQUALSchar, 'E', SOHchar};
    public static final int     bitmask_Locked                = 1 << 5; public static final String text_Locked                 = "Locked";
    public static final char    Crossed                       = 'F';
    public static final String  string_Crossed                = "F";
    public static final String  tagged_Crossed                = TagIDAsString + EQUALS + string_Crossed + SOH;
    public static final char[]  taggedchars_Crossed           = {'2','7','6', EQUALSchar, 'F', SOHchar};
    public static final int     bitmask_Crossed               = 1 << 6; public static final String text_Crossed                = "Crossed";
    public static final char    Depth                         = 'G';
    public static final String  string_Depth                  = "G";
    public static final String  tagged_Depth                  = TagIDAsString + EQUALS + string_Depth + SOH;
    public static final char[]  taggedchars_Depth             = {'2','7','6', EQUALSchar, 'G', SOHchar};
    public static final int     bitmask_Depth                 = 1 << 7; public static final String text_Depth                  = "Depth";
    public static final char    FastTrading                   = 'H';
    public static final String  string_FastTrading            = "H";
    public static final String  tagged_FastTrading            = TagIDAsString + EQUALS + string_FastTrading + SOH;
    public static final char[]  taggedchars_FastTrading       = {'2','7','6', EQUALSchar, 'H', SOHchar};
    public static final int     bitmask_FastTrading           = 1 << 8; public static final String text_FastTrading            = "FastTrading";
    public static final char    NonFirm                       = 'I';
    public static final String  string_NonFirm                = "I";
    public static final String  tagged_NonFirm                = TagIDAsString + EQUALS + string_NonFirm + SOH;
    public static final char[]  taggedchars_NonFirm           = {'2','7','6', EQUALSchar, 'I', SOHchar};
    public static final int     bitmask_NonFirm               = 1 << 9; public static final String text_NonFirm                = "NonFirm";

    private static FixQuoteConditionField flyweightOpenActive;
    public static final FixQuoteConditionField flyweightOpenActive()
    {
        if (flyweightOpenActive == null)
        {
            synchronized(FixQuoteConditionField.class)
            {
                if (flyweightOpenActive == null)
                {
                    flyweightOpenActive  = new FixQuoteConditionField()
                    {public boolean hasValue()                        {return true;}
                     public String  getValue()                        {return string_OpenActive;}
                     public String  getValueDescription()             {return text_OpenActive;}
                     public String  toString()                        {return new StringBuilder(64).append(TagName).append("{").append(TagIDAsString).append("} [").append(string_OpenActive).append("|").append(text_OpenActive).append("]").toString();}
                     public boolean isOpenActive()                    {return true;}
                     public void    accept(FixMessageBuilderIF fixMessageBuilder) {fixMessageBuilder.append(taggedchars_OpenActive);}
                    };
                }
            }
        }

        return flyweightOpenActive;
    }

    private static FixQuoteConditionField flyweightClosedInactive;
    public static final FixQuoteConditionField flyweightClosedInactive()
    {
        if (flyweightClosedInactive == null)
        {
            synchronized(FixQuoteConditionField.class)
            {
                if (flyweightClosedInactive == null)
                {
                    flyweightClosedInactive  = new FixQuoteConditionField()
                    {public boolean hasValue()                        {return true;}
                     public String  getValue()                        {return string_ClosedInactive;}
                     public String  getValueDescription()             {return text_ClosedInactive;}
                     public String  toString()                        {return new StringBuilder(64).append(TagName).append("{").append(TagIDAsString).append("} [").append(string_ClosedInactive).append("|").append(text_ClosedInactive).append("]").toString();}
                     public boolean isClosedInactive()                {return true;}
                     public void    accept(FixMessageBuilderIF fixMessageBuilder) {fixMessageBuilder.append(taggedchars_ClosedInactive);}
                    };
                }
            }
        }

        return flyweightClosedInactive;
    }

    private static FixQuoteConditionField flyweightExchangeBest;
    public static final FixQuoteConditionField flyweightExchangeBest()
    {
        if (flyweightExchangeBest == null)
        {
            synchronized(FixQuoteConditionField.class)
            {
                if (flyweightExchangeBest == null)
                {
                    flyweightExchangeBest  = new FixQuoteConditionField()
                    {public boolean hasValue()                        {return true;}
                     public String  getValue()                        {return string_ExchangeBest;}
                     public String  getValueDescription()             {return text_ExchangeBest;}
                     public String  toString()                        {return new StringBuilder(64).append(TagName).append("{").append(TagIDAsString).append("} [").append(string_ExchangeBest).append("|").append(text_ExchangeBest).append("]").toString();}
                     public boolean isExchangeBest()                  {return true;}
                     public void    accept(FixMessageBuilderIF fixMessageBuilder) {fixMessageBuilder.append(taggedchars_ExchangeBest);}
                    };
                }
            }
        }

        return flyweightExchangeBest;
    }

    private static FixQuoteConditionField flyweightConsolidatedBest;
    public static final FixQuoteConditionField flyweightConsolidatedBest()
    {
        if (flyweightConsolidatedBest == null)
        {
            synchronized(FixQuoteConditionField.class)
            {
                if (flyweightConsolidatedBest == null)
                {
                    flyweightConsolidatedBest  = new FixQuoteConditionField()
                    {public boolean hasValue()                        {return true;}
                     public String  getValue()                        {return string_ConsolidatedBest;}
                     public String  getValueDescription()             {return text_ConsolidatedBest;}
                     public String  toString()                        {return new StringBuilder(64).append(TagName).append("{").append(TagIDAsString).append("} [").append(string_ConsolidatedBest).append("|").append(text_ConsolidatedBest).append("]").toString();}
                     public boolean isConsolidatedBest()              {return true;}
                     public void    accept(FixMessageBuilderIF fixMessageBuilder) {fixMessageBuilder.append(taggedchars_ConsolidatedBest);}
                    };
                }
            }
        }

        return flyweightConsolidatedBest;
    }

    private static FixQuoteConditionField flyweightLocked;
    public static final FixQuoteConditionField flyweightLocked()
    {
        if (flyweightLocked == null)
        {
            synchronized(FixQuoteConditionField.class)
            {
                if (flyweightLocked == null)
                {
                    flyweightLocked  = new FixQuoteConditionField()
                    {public boolean hasValue()                        {return true;}
                     public String  getValue()                        {return string_Locked;}
                     public String  getValueDescription()             {return text_Locked;}
                     public String  toString()                        {return new StringBuilder(64).append(TagName).append("{").append(TagIDAsString).append("} [").append(string_Locked).append("|").append(text_Locked).append("]").toString();}
                     public boolean isLocked()                        {return true;}
                     public void    accept(FixMessageBuilderIF fixMessageBuilder) {fixMessageBuilder.append(taggedchars_Locked);}
                    };
                }
            }
        }

        return flyweightLocked;
    }

    private static FixQuoteConditionField flyweightCrossed;
    public static final FixQuoteConditionField flyweightCrossed()
    {
        if (flyweightCrossed == null)
        {
            synchronized(FixQuoteConditionField.class)
            {
                if (flyweightCrossed == null)
                {
                    flyweightCrossed  = new FixQuoteConditionField()
                    {public boolean hasValue()                        {return true;}
                     public String  getValue()                        {return string_Crossed;}
                     public String  getValueDescription()             {return text_Crossed;}
                     public String  toString()                        {return new StringBuilder(64).append(TagName).append("{").append(TagIDAsString).append("} [").append(string_Crossed).append("|").append(text_Crossed).append("]").toString();}
                     public boolean isCrossed()                       {return true;}
                     public void    accept(FixMessageBuilderIF fixMessageBuilder) {fixMessageBuilder.append(taggedchars_Crossed);}
                    };
                }
            }
        }

        return flyweightCrossed;
    }

    private static FixQuoteConditionField flyweightDepth;
    public static final FixQuoteConditionField flyweightDepth()
    {
        if (flyweightDepth == null)
        {
            synchronized(FixQuoteConditionField.class)
            {
                if (flyweightDepth == null)
                {
                    flyweightDepth  = new FixQuoteConditionField()
                    {public boolean hasValue()                        {return true;}
                     public String  getValue()                        {return string_Depth;}
                     public String  getValueDescription()             {return text_Depth;}
                     public String  toString()                        {return new StringBuilder(64).append(TagName).append("{").append(TagIDAsString).append("} [").append(string_Depth).append("|").append(text_Depth).append("]").toString();}
                     public boolean isDepth()                         {return true;}
                     public void    accept(FixMessageBuilderIF fixMessageBuilder) {fixMessageBuilder.append(taggedchars_Depth);}
                    };
                }
            }
        }

        return flyweightDepth;
    }

    private static FixQuoteConditionField flyweightFastTrading;
    public static final FixQuoteConditionField flyweightFastTrading()
    {
        if (flyweightFastTrading == null)
        {
            synchronized(FixQuoteConditionField.class)
            {
                if (flyweightFastTrading == null)
                {
                    flyweightFastTrading  = new FixQuoteConditionField()
                    {public boolean hasValue()                        {return true;}
                     public String  getValue()                        {return string_FastTrading;}
                     public String  getValueDescription()             {return text_FastTrading;}
                     public String  toString()                        {return new StringBuilder(64).append(TagName).append("{").append(TagIDAsString).append("} [").append(string_FastTrading).append("|").append(text_FastTrading).append("]").toString();}
                     public boolean isFastTrading()                   {return true;}
                     public void    accept(FixMessageBuilderIF fixMessageBuilder) {fixMessageBuilder.append(taggedchars_FastTrading);}
                    };
                }
            }
        }

        return flyweightFastTrading;
    }

    private static FixQuoteConditionField flyweightNonFirm;
    public static final FixQuoteConditionField flyweightNonFirm()
    {
        if (flyweightNonFirm == null)
        {
            synchronized(FixQuoteConditionField.class)
            {
                if (flyweightNonFirm == null)
                {
                    flyweightNonFirm  = new FixQuoteConditionField()
                    {public boolean hasValue()                        {return true;}
                     public String  getValue()                        {return string_NonFirm;}
                     public String  getValueDescription()             {return text_NonFirm;}
                     public String  toString()                        {return new StringBuilder(64).append(TagName).append("{").append(TagIDAsString).append("} [").append(string_NonFirm).append("|").append(text_NonFirm).append("]").toString();}
                     public boolean isNonFirm()                       {return true;}
                     public void    accept(FixMessageBuilderIF fixMessageBuilder) {fixMessageBuilder.append(taggedchars_NonFirm);}
                    };
                }
            }
        }

        return flyweightNonFirm;
    }


    public boolean isOpenActive()         {return false;}
    public boolean isClosedInactive()     {return false;}
    public boolean isExchangeBest()       {return false;}
    public boolean isConsolidatedBest()   {return false;}
    public boolean isLocked()             {return false;}
    public boolean isCrossed()            {return false;}
    public boolean isDepth()              {return false;}
    public boolean isFastTrading()        {return false;}
    public boolean isNonFirm()            {return false;}

    public final int    getTag()                                      {return TagID;}
    public final String getTagAsString()                              {return TagIDAsString;}
    public final char[] getTagAsChars()                               {return TagIDAsChars;}
    public final String getTagName()                                  {return TagName;}
    public       String getValueDescription()                         {return getValue();}
    public       void   accept(FixMessageBuilderIF fixMessageBuilder) {fixMessageBuilder.append(TagIDAsChars, getValue());}
    public       String toString()                                    {return new StringBuilder(64).append(TagName).append("{").append(TagIDAsString).append("} [").append(getValue()).append(']').toString();}

    public static FixQuoteConditionField create(char[] array, int offset, int length)
    {
        return create(new String(array, offset, length));
    }

    public static FixQuoteConditionField create(final String multiparam)
    {
        int bitmask = 0;

        for (int i = 0; i < multiparam.length(); i++)
        {
            switch (multiparam.charAt(i))
            {                                                                                           
                case OpenActive:
                    if (multiparam.length() == 1) return flyweightOpenActive();
                    bitmask |= bitmask_OpenActive;
                    break;
                case ClosedInactive:
                    if (multiparam.length() == 1) return flyweightClosedInactive();
                    bitmask |= bitmask_ClosedInactive;
                    break;
                case ExchangeBest:
                    if (multiparam.length() == 1) return flyweightExchangeBest();
                    bitmask |= bitmask_ExchangeBest;
                    break;
                case ConsolidatedBest:
                    if (multiparam.length() == 1) return flyweightConsolidatedBest();
                    bitmask |= bitmask_ConsolidatedBest;
                    break;
                case Locked:
                    if (multiparam.length() == 1) return flyweightLocked();
                    bitmask |= bitmask_Locked;
                    break;
                case Crossed:
                    if (multiparam.length() == 1) return flyweightCrossed();
                    bitmask |= bitmask_Crossed;
                    break;
                case Depth:
                    if (multiparam.length() == 1) return flyweightDepth();
                    bitmask |= bitmask_Depth;
                    break;
                case FastTrading:
                    if (multiparam.length() == 1) return flyweightFastTrading();
                    bitmask |= bitmask_FastTrading;
                    break;
                case NonFirm:
                    if (multiparam.length() == 1) return flyweightNonFirm();
                    bitmask |= bitmask_NonFirm;
                    break;
                default:
                    return null;
            }
        }

        if (bitmask == 0)
        {
            return null;
        }

        final int bits = bitmask;

        return new FixQuoteConditionField()
        {       public int value = bits;
                public boolean hasValue()                        {return bits != 0;}
                public String  getValue()                        {return this.toMultipleValueString(value);}
        };
    }                                                                                               
    public static String toMultipleValueString(int bitmask)
    {
        StringBuilder buffer = new StringBuilder();
                                                                                                        
        if (BitHelper.isBitMaskSet(bitmask, bitmask_OpenActive)) buffer.append(string_OpenActive).append(' ');
        if (BitHelper.isBitMaskSet(bitmask, bitmask_ClosedInactive)) buffer.append(string_ClosedInactive).append(' ');
        if (BitHelper.isBitMaskSet(bitmask, bitmask_ExchangeBest)) buffer.append(string_ExchangeBest).append(' ');
        if (BitHelper.isBitMaskSet(bitmask, bitmask_ConsolidatedBest)) buffer.append(string_ConsolidatedBest).append(' ');
        if (BitHelper.isBitMaskSet(bitmask, bitmask_Locked)) buffer.append(string_Locked).append(' ');
        if (BitHelper.isBitMaskSet(bitmask, bitmask_Crossed)) buffer.append(string_Crossed).append(' ');
        if (BitHelper.isBitMaskSet(bitmask, bitmask_Depth)) buffer.append(string_Depth).append(' ');
        if (BitHelper.isBitMaskSet(bitmask, bitmask_FastTrading)) buffer.append(string_FastTrading).append(' ');
        if (BitHelper.isBitMaskSet(bitmask, bitmask_NonFirm)) buffer.append(string_NonFirm).append(' ');

        if (buffer.charAt(buffer.length() - 1) == ' ') buffer.setLength(buffer.length() - 1);

        return buffer.toString();
    }                                                                                                   
}