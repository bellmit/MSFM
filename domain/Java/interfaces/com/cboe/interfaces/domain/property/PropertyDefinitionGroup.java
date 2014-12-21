package com.cboe.interfaces.domain.property;

import java.util.Map;

//
// -----------------------------------------------------------------------------------
// Source file: PropertyDefinitionGroup
//
// PACKAGE: com.cboe.interfaces.domain.property;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

/**
 *  This interface defines the methods for a property definition group.  A property
 *  defintion group holds a set of property defintions for a specific category.
 */
public interface PropertyDefinitionGroup extends Group
{
    /**
     *  Get the list of name value pairs in the group.
     *
     *  @return The list of name value pairs.
     */
    public Map getDefinitions();

    /**
     *  Set the list of name value pairs in the group.
     *
     *  @param map The list of name value pairs.
     */
    public void setDefinitions(Map definitions);

    /**
     *  Add a definition to the current list.
     *
     *  @param name The name of the definition.
     *  @param value The value of the definition.
     */
    public void addDefinition(PropertyDefinition definition);

    /**
     *  Get a specific property by name.
     */
    public PropertyDefinition getDefinition(String name);

    /**
     *  Remove a property from the group.
     *
     *  @param The property to remove
     */
    public void removeDefinition(PropertyDefinition definition);

    /**
     *  Remove a property from the group.
     *
     *  @param The encoded name of the property to remove
     */
    public void removeDefinition(String propertyName);

}
