package com.cboe.domain.property;
//
// -----------------------------------------------------------------------------------
// Source file: PropertyDefinitionGroupImpl
//
// PACKAGE: com.cboe.domain.property;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
import java.util.Map;
import java.util.HashMap;

import com.cboe.interfaces.domain.property.PropertyDefinition;
import com.cboe.interfaces.domain.property.PropertyDefinitionGroup;

/**
 *  This interface defines the methods for a property definition group.  A property
 *  defintion group holds a set of property defintions for a specific category.
 */
public class PropertyDefinitionGroupImpl extends AbstractGroup implements PropertyDefinitionGroup
{
    /**
     *  Constructor for a blank group.
     */
    public PropertyDefinitionGroupImpl(String category, String key)
    {
        setCategory(category);
        setKey(key);
        setVersion(0);
        setPairs(new HashMap());
    }

    /**
     *  Get the list of name value pairs in the group.
     *
     *  @return The list of name value pairs.
     */
    public Map getDefinitions()
    {
        return getPairs();
    }

    /**
     *  Set the list of name value pairs in the group.
     *
     *  @param map The list of name value pairs.
     */
    public void setDefinitions(Map definitions)
    {
        setPairs(definitions);
    }

    /**
     *  Add a definition to the current list.
     *
     *  @param name The name of the definition.
     *  @param value The value of the definition.
     */
    public void addDefinition(PropertyDefinition definition)
    {
        addPair(definition.getDefinitionName(),definition);
    }

    /**
     *  Get a specific property by name.
     */
    public PropertyDefinition getDefinition(String name)
    {
        return (PropertyDefinition) getPairValue(name);
    }

    /**
     *  Remove a property from the group.
     *
     *  @param The property to remove
     */
    public void removeDefinition(PropertyDefinition definition)
    {
        removePair(definition.getDefinitionName());
    }

    /**
     *  Remove a property from the group.
     *
     *  @param The encoded name of the property to remove
     */
    public void removeDefinition(String definitionName)
    {
        removePair(definitionName);
    }

}
