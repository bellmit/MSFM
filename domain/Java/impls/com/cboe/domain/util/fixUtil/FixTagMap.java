package com.cboe.domain.util.fixUtil;

import java.util.HashMap;

/**
 * Provides convenience methods for put and get.  The key is the FixTag's 
 * tagNumber and value is the FixTag.
 * 
 * Date: Sep 1, 2004
 */
public class FixTagMap extends HashMap
{
    public FixTagMap(int initialCapacity)
    {
        super(initialCapacity);
    }

    public Object put(FixTag tag)
    {
        return put(Integer.valueOf(tag.getTagNumber()), tag);
    }

    public FixTag get(int tagNumber)
    {
        return (FixTag)get(Integer.valueOf(tagNumber));
    }
}
