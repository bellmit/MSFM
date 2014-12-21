package com.cboe.cfix.fix.fix42.field;

/**
 * AdditionalUserField.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * This file implements FIX Protocol's Field AdditionalUserFields.
 *
 */

import com.cboe.cfix.interfaces.*;
import com.cboe.client.util.*;

public final class AdditionalUserField implements FixFieldIF
{
    public int    tagID;
    public String value;
    public static final String TagName = "AdditionalUserField";

    public AdditionalUserField(int tag)
    {
        tagID = tag;
    }

    public AdditionalUserField(int tag, char[] array, int offset, int length)
    {
        setTag(tag);
        create(array, offset, length);
    }

    public void create(char[] array, int offset, int length)
    {
        value = new String(array, offset, length);
    }

    public final void setTag(int tag)
    {
        tagID = tag;
    }

    public final int getTag()
    {
        return tagID;
    }

    public final String getTagAsString()
    {
        return StringHelper.intToString(tagID);
    }

    public final String getTagName()
    {
        return TagName;
    }

    public String getValue()
    {
        return value;
    }

    public boolean hasValue()
    {
        return value != null;
    }

    public String getValueDescription()
    {
        return getValue();
    }

    public String toString()
    {
        StringBuilder result = new StringBuilder(TagName.length()+value.length()+20);
        result.append(TagName).append("{").append(tagID).append("} [").append(value).append("]");
        return result.toString();
    }

    public void accept(FixMessageBuilderIF fixMessageBuilder)
    {
        fixMessageBuilder.append(getTagAsString(), getValue());
    }
}
