package com.cboe.interfaces.domain.property;

import java.util.Map;

//
// -----------------------------------------------------------------------------------
// Source file: PropertyGroup
//
// PACKAGE: com.cboe.interfaces.domain.property;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

public interface PropertyGroup extends Group
{
    /**
     *  Get the list of name value pairs in the group.
     *
     *  @return The list of name value pairs.
     */
    public Map getProperties();

    /**
     *  Set (replace) a property in the group.
     *
     *  @param The property to set
     */
    public void setProperty(Property property);
    
    /**
     *  Set the list of name value pairs in the group.
     *
     *  @param map The list of name value pairs.
     */
    public void setProperties(Map properties);

    /**
     * Add a Property to the current list.
     * @param property to add
     */
    public void addProperty(Property property);

    /**
     *  Get a specific property by name.
     */
    public Property getProperty(String name);
    /**
     *  Remove a property from the group.
     *
     *  @param The property to remove
     */
    public void removeProperty(Property property);
    /**
     *  Remove a property from the group.
     *
     *  @param The encoded name of the property to remove
     */
    public void removeProperty(String propertyName);

}
