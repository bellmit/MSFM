package com.cboe.cfix.fix.fix42.generated.fields;

/**
 * FixBasisPxTypeField.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * This GENERATED file implements FIX Protocol's Field Tag [419] (known as BasisPxType).
 *
 * WARNING: This entire file is completely generated by XSLT stylesheets from CboeFIX42.xml file.
 *
 */

import com.cboe.cfix.interfaces.*;
import com.cboe.cfix.util.*;
import com.cboe.client.util.*;

public abstract class FixBasisPxTypeField implements FixFieldIF, HasCharValueIF
{   
    public static final int    TagID         =  419;
    public static final String TagIDAsString = "419";
    public static final char[] TagIDAsChars  = {'4','1','9'};
    public static final String TagName       = "BasisPxType";
    public static final String TagFixType    = "CHAR";

    public static FixBasisPxTypeField create(final char[] array, final int offset, final int length)
    {
        if (length == 1)
        {
            return create(array[offset]);
        }

        return null;
    }

    public static FixBasisPxTypeField create(final char param)
    {
        return new FixBasisPxTypeField()
        {   public final char value = param;
            public boolean hasValue()                                    {return true;}
            public String  getValue()                                    {return StringHelper.charToString(value);}
            public char    charValue()                                   {return value;}
            public void    accept(FixMessageBuilderIF fixMessageBuilder) {fixMessageBuilder.append(TagIDAsChars, value);}
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