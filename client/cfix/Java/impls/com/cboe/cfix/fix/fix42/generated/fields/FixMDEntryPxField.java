package com.cboe.cfix.fix.fix42.generated.fields;

/**
 * FixMDEntryPxField.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * This GENERATED file implements FIX Protocol's Field Tag [270] (known as MDEntryPx).
 *
 * WARNING: This entire file is completely generated by XSLT stylesheets from CboeFIX42.xml file.
 *
 */

import com.cboe.cfix.interfaces.*;
import com.cboe.cfix.util.*;
import com.cboe.client.util.*;
import com.cboe.idl.cmiUtil.*;

public abstract class FixMDEntryPxField implements FixFieldIF, HasPriceStructValueIF
{   
    public static final int    TagID         =  270;
    public static final String TagIDAsString = "270";
    public static final char[] TagIDAsChars  = {'2','7','0'};
    public static final String TagName       = "MDEntryPx";
    public static final String TagFixType    = "PRICE";
    public static final char[]  taggedchars_value0 = {'2','7','0', EQUALSchar, ZEROchar, SOHchar};
    public static final char[]  taggedchars_value1 = {'2','7','0', EQUALSchar, ONEchar,  SOHchar};

    public static FixMDEntryPxField create(final char[] array, final int offset, final int length)
    {
        return create(PriceHelper.createPriceStruct(array, offset, length));
    }

    public static FixMDEntryPxField create(final PriceStruct param)
    {
        return new FixMDEntryPxField()
        {   public final PriceStruct value = param;
            public boolean hasValue()                        {return true;}
            public String  getValue()                        {return PriceHelper.toString(value);}
            public PriceStruct priceStructValue()            {return value;}
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