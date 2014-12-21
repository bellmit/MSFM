package com.cboe.cfix.fix.fix42.generated.fields;

/**
 * FixMDEntrySizeField.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * This GENERATED file implements FIX Protocol's Field Tag [271] (known as MDEntrySize).
 *
 * WARNING: This entire file is completely generated by XSLT stylesheets from CboeFIX42.xml file.
 *
 */

import com.cboe.cfix.interfaces.*;
import com.cboe.cfix.util.*;

public abstract class FixMDEntrySizeField implements FixFieldIF, HasDoubleValueIF
{   
    public static final int    TagID         =  271;
    public static final String TagIDAsString = "271";
    public static final char[] TagIDAsChars  = {'2','7','1'};
    public static final String TagName       = "MDEntrySize";
    public static final String TagFixType    = "DOUBLE";
    public static final char[]  taggedchars_value0 = {'2','7','1', EQUALSchar, ZEROchar, SOHchar};
    public static final char[]  taggedchars_value1 = {'2','7','1', EQUALSchar, ONEchar,  SOHchar};

    public static FixMDEntrySizeField create(final char[] array, final int offset, final int length)
    {
        return create(Double.parseDouble(new String(array, offset, length)));
    }

    public static FixMDEntrySizeField create(final double param)
    {
        return new FixMDEntrySizeField()
        {   public final double value = param;
            public boolean hasValue()                        {return true;}
            public String  getValue()                        {return Double.toString(value);}
            public double  doubleValue()                     {return value;}
        };
    }
    
    public final int    getTag()                                      {return TagID;}
    public final String getTagAsString()                              {return TagIDAsString;}
    public final char[] getTagAsChars()                               {return TagIDAsChars;}
    public final String getTagName()                                  {return TagName;}
    public       String getValueDescription()                         {return getValue();}
    public       void   accept(FixMessageBuilderIF fixMessageBuilder) {fixMessageBuilder.append(TagIDAsChars, getValue());}
    public       String toString()                                    {return new StringBuilder(64).append(TagName).append("{").append(TagIDAsString).append("} [").append(getValue()).append(']').toString();}
}