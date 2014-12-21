package com.cboe.interfaces.domain.property;

import java.util.Map;

//
// -----------------------------------------------------------------------------------
// Source file: Group
//
// PACKAGE: com.cboe.interfaces.domain.property;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

public interface Group
{
    /**
     * Returns the category for the property group
     * @return
     */
    public String getCategory();

    /**
     * Sets the category of the property group
     * @param value
     */
    public void setCategory(String category);

    /**
     * Returns the key of the property group.
     * @return
     */
    public String getKey();

    /**
     * Sets the key of the property group.
     * @param value
     */
    public void setKey(String key);

    /**
     * Returns the version of the property group.
     * @return version
     */
    public int getVersion();

    /**
     * Sets the version
     * @param version
     */
    public void setVersion(int version);

    /**
     *  Get the list of name value pairs in the group.
     *
     *  @return The list of name value pairs.
     */
    public Map getPairs();

    /**
     *  Set the list of name value pairs in the group.
     *
     *  @param map The list of name value pairs.
     */
    public void setPairs(Map pairs);

    /**
     *  Add a property to the current list.
     *
     *  @param name The name.
     *  @param value The value.
     */
    public void addPair(String name, Object value);

    /**
     *  Get a specific property by name.
     */
    public Object getPairValue(String name);
    /**
     *  Remove a pair from the group.
     *
     *  @param The name of the pair to remove
     */
    public void removePair(String name);

}
