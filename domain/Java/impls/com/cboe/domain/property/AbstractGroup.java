package com.cboe.domain.property;
//
// -----------------------------------------------------------------------------------
// Source file: AbstractGroupImpl
//
// PACKAGE: com.cboe.domain.property;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import com.cboe.interfaces.domain.property.Group;

/**
 *  Base class for a group.
 */
public abstract class AbstractGroup implements Group
{
    protected String category;
    protected String key;
    protected int    version;
    protected Map    pairs;

    /**
     * Returns the category for the property group
     * @return
     */
    public String getCategory()
    {
        return category;
    }

    /**
     * Sets the category of the property group
     * @param value
     */
    public void setCategory(String category)
    {
        this.category = category;
    }

    /**
     * Returns the key of the property group.
     * @return
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Sets the key of the property group.
     * @param value
     */
    public void setKey(String key)
    {
        this.key = key;
    }

    /**
     * Returns the version of the property group.
     * @return version
     */
    public int getVersion()
    {
        return version;
    }

    /**
     * Sets the version
     * @param version
     */
    public void setVersion(int version)
    {
        this.version = version;
    }

    /**
     *  Get the list of name value pairs in the group.
     *
     *  @return The list of name value pairs.
     */
    public Map getPairs()
    {
        if (pairs == null)
        {
            pairs = new HashMap();
        }
        return pairs;
    }

    /**
     *  Set the list of name value pairs in the group.
     *
     *  @param map The list of name value pairs.
     */
    public void setPairs(Map pairs)
    {
        this.pairs = pairs;
    }

    /**
     *  Add a name value pair to the pairs map.
     */
    public void addPair(String name, Object value)
    {
        pairs.put(name,value);
    }

    /**
     *  Get a specific pair value by name.
     */
    public Object getPairValue(String name)
    {
        return getPairs().get(name);
    }

    /**
     *  Remove a pair from the group.
     *
     *  @param name The name of the pair.
     */
    public void removePair(String name)
    {
        getPairs().remove(name);        
    }

    protected void dumpPairs(StringBuffer buffer)
    {
        buffer.append("AbstractGroup::dumpPairs Pair Dump follows\n");
        Map pairs = getPairs();    
        Iterator iterator = pairs.keySet().iterator();
        while (iterator.hasNext())
        {
            Object key = iterator.next();
            Object value = pairs.get(key);
            buffer.append("key=").append(key);
            buffer.append("  ");
            buffer.append("value=").append(value);
            buffer.append("\n");
        }

    }
}
