package com.cboe.domain.property;
//
// -----------------------------------------------------------------------------------
// Source file: PropertyGroupImpl
//
// PACKAGE: com.cboe.domain.property;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import com.cboe.idl.property.PropertyStruct;
import com.cboe.idl.property.PropertyGroupStruct;

import com.cboe.interfaces.domain.property.Property;
import com.cboe.interfaces.domain.property.PropertyDefinition;
import com.cboe.interfaces.domain.property.PropertyServiceProperty;
import com.cboe.interfaces.domain.property.PropertyDefinitionGroup;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;

public class PropertyServicePropertyGroupImpl extends AbstractGroup implements PropertyServicePropertyGroup
{
    /**
     *  Create a new blank group by category and key.
     *
     *  @param category Category for the new group.
     *  @param key Key for the new group.
     */
    public PropertyServicePropertyGroupImpl(String category, String key)
    {
        setCategory(category);
        setKey(key);
        setVersion(0);
        setPairs(new HashMap());
    }

    /**
     *  Create a new property group based on a struct.
     *
     *  @param struct Struct that contains the property group.
     */
    public PropertyServicePropertyGroupImpl(PropertyGroupStruct struct)
    {
        setStruct(struct, true);
    }

    
    /**
     *  Create a new property group based on a struct.
     *
     *  @param struct Struct that contains the property group.
     */
    public PropertyServicePropertyGroupImpl(PropertyGroupStruct struct, boolean loadPropertyDefinitions)
    {
        setStruct(struct, loadPropertyDefinitions);
    }

    /**
     *  Get the list of name value pairs in the group.
     *
     *  @return The list of name value pairs.
     */
    public Map getProperties()
    {
        return getPairs();
    }

    /**
     *  Set the list of name value pairs in the group.
     *
     *  @param map The list of name value pairs.
     */
    public void setProperties(Map properties)
    {
        setPairs(properties);
    }

    /**
     *  Set (replace) a property in the group.
     *
     *  @param The property to set
     */
    public void setProperty(Property property)
    {
        removeProperty( property.getName() );
        addProperty( property );
    }
    
    /**
     *  Add a Property to the current list.
     *  @param property to add
     */
    public void addProperty(Property property)
    {
        addPair(property.getName(), property);
    }

    /**
     *  Get a specific property by name.
     */
    public Property getProperty(String name)
    {
        return (Property) getPairValue(name);
    }

    /**
     *  Get the struct that represents this property.
     */
    public PropertyGroupStruct getStruct()
    {
        return buildStruct();
    }

    /**
     *  Remove a property from the group.
     *
     *  @param name The name of the property.
     */
    public void removeProperty(String name)
    {
        removePair(name);
    }

    /**
     *  Remove a property from the group.
     *
     *  @param name The property to remove
     */
    public void removeProperty(Property property)
    {
        removePair(property.getName());
    }

    /**
     *  Set the object to the values in this struct.
     */
    public void setStruct(PropertyGroupStruct struct,  boolean loadPropertyDefinitions)
    {
        setCategory(struct.category);
        setKey(struct.propertyKey);
        setVersion(struct.versionNumber);
        setProperties(new HashMap());

        // Lets get the definition group for the group, if the group is not a defintion group
        PropertyDefinitionGroup definitions = null;
        
        if(loadPropertyDefinitions)
        {
            if (!(getKey().equals(PropertyDefinition.PROPERTY_DEFINITION_KEY)))
            {
                definitions = PropertyDefinitionCache.getInstance().getCategoryDefinitions(getCategory());
            }
        }
        
        for (int i=0;i<struct.preferenceSequence.length ; i++)
        {
            addProperty(PropertyFactory.createProperty(struct.preferenceSequence[i],definitions));
        }
    }

    /**
     *  Build a struct based on the current attributes.
     */
    protected PropertyGroupStruct buildStruct()
    {
        PropertyGroupStruct struct = new PropertyGroupStruct();

        struct.propertyKey = key;
        struct.category = category;
        struct.versionNumber = version;
        struct.preferenceSequence = buildSequence();

        return struct;
    }

    /** 
     *  Build a struct sequence representing the list
     *  of properties.
     */
    protected PropertyStruct[] buildSequence()
    {
        int i =0;
        PropertyStruct[] structs = new PropertyStruct[getProperties().size()];
        
        Map properties = getProperties();

        Iterator iterator = properties.values().iterator();
        while (iterator.hasNext())
        {
            Object obj = iterator.next();
            PropertyServiceProperty property = (PropertyServiceProperty) obj;
            structs[i++] = property.getStruct();    
        }

        return structs;
    }

    public void dumpGroup(StringBuffer buffer)
    {
        buffer.append("PropertyServiceProprtyGroupImpl::category=").append(category).append("\n");
        buffer.append("PropertyServiceProprtyGroupImpl::key=").append(key).append("\n");
        buffer.append("PropertyServiceProprtyGroupImpl::version=").append(version).append("\n");

        Iterator iterator = getProperties().values().iterator();
        while (iterator.hasNext())
        {
            PropertyServicePropertyImpl property = (PropertyServicePropertyImpl) iterator.next();
            property.dumpProperty(buffer);
        }

    }

}
