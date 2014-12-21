package com.cboe.domain.property;
//
// -----------------------------------------------------------------------------------
// Source file: PropertyFactory
//
// PACKAGE: com.cboe.domain.property;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
import java.util.*;

import com.cboe.idl.property.PropertyGroupStruct;
import com.cboe.idl.property.PropertyStruct;

import com.cboe.interfaces.domain.property.Property;
import com.cboe.interfaces.domain.property.PropertyDefinition;
import com.cboe.interfaces.domain.property.PropertyDefinitionGroup;
import com.cboe.interfaces.domain.property.PropertyGroup;
import com.cboe.interfaces.domain.property.PropertyServiceProperty;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;

/**
 *  Factory that contains factory methods for creatign classes for the property service.
 */
public class PropertyFactory
{
    /**
     *  Create a property from a struct.
     *
     *  @param struct Struct containing the property data.
     *  @param propertyDefinitionGroup The property defintion group that applies to the category the property is in.
     *  @return Property The newly created property
     */
    public static PropertyServiceProperty createProperty(PropertyStruct struct, PropertyDefinitionGroup definitionGroup)
    {
        PropertyServicePropertyImpl property = new PropertyServicePropertyImpl(struct,definitionGroup);

        return property;
    }

    /**
     * Create a property from its component parts.
     *
     * @param nameList The list of names for the property.
     * @param valueList The list of values.
     * @param propertyDefinition The definition for this property
     * @return The newly created property.
     */
    public static PropertyServiceProperty createProperty(List nameList, List valueList, PropertyDefinition propertyDefinition)
    {
        PropertyServicePropertyImpl property = new PropertyServicePropertyImpl(nameList,valueList,propertyDefinition);

        return property;
    }

    public static PropertyServiceProperty createProperty(String name, String value)
    {
        PropertyServicePropertyImpl property = new PropertyServicePropertyImpl(name, value);
        return property;
    }

    /**
     *  Create a property group sequence from a struct sequence.
     *
     *  @param struct[] A struct[] representation of a property groups.
     *  @return The property group[].
     */
    public static PropertyServicePropertyGroup[] createPropertyGroup(PropertyGroupStruct[] struct)
    {
        PropertyServicePropertyGroup[] group = new PropertyServicePropertyGroup[ struct.length ];
        
        for ( int i = 0; i < struct.length; i++ )
        {
            group[ i ] = createPropertyGroup( struct[ i ] );
        }
        
        return group;
    }

    /**
     *  Create a property group from a struct.
     *
     *  @param struct A struct representation of a property group.
     *  @retrun The property group.
     */
    public static PropertyServicePropertyGroup createPropertyGroup(PropertyGroupStruct struct)
    {
        PropertyServicePropertyGroupImpl group = new PropertyServicePropertyGroupImpl(struct);

        return group;
    }

    /**
     *  Create a property group from a category and key.  This group will be blank and have
     *  no properties.
     *
     *  @param category The group category.
     *  @param key The key for the group.
     *  @return A property group.
     */
    public static PropertyServicePropertyGroup createPropertyGroup(String category, String key)
    {
        PropertyServicePropertyGroupImpl group = new PropertyServicePropertyGroupImpl(category,key);
        return group;
    }

    /**
     *  Create a property definition group from a category and key.  This group will be blank and have
     *  no properties.
     *
     *  @param category The group category.
     *  @param key The key for the group.
     *  @return A property group.
     */
    public static PropertyDefinitionGroup createPropertyDefinitionGroup(String category, String key)
    {
        PropertyDefinitionGroupImpl group = new PropertyDefinitionGroupImpl(category,key);
        return group;
    }

    /**
     *  Create a property definition from all the component parts.
     *
     *  @param defaultValue Default value for properties using this definition
     *  @param dataType Data type for properties using this defintion.
     *  @param possibleValues The list of possible values
     *  @param displayValues The list of values to display for the possible values
     *  @param displayName The name of the property defined by this definition
     *  @param definitionName The name of the definition.
     *  @return A property definition represented by the component parts.
     */
    public static PropertyDefinition createPropertyDefinition(String defaultValue, Class dataType, List possibleValues, 
                                                       List displayValues, String displayName, String definitionName)
    {
        PropertyDefinitionImpl definition = new PropertyDefinitionImpl(defaultValue, dataType, displayValues,
                                                                       possibleValues, displayName, definitionName);
        return definition;
    }

    /**
     *  Create a property group that represents a property definition group.  The
     *  group is created by parsing the property names and grouping them into
     *  PropertyDefinition objects.
     *
     *  @param struct The group representing the property defintion group.
     *  @return a property group that represents one defintion group.
     */
    public static PropertyDefinitionGroup createPropertyDefinitionGroup(PropertyGroup propertyGroup)
    {
        // Create a new property group
        PropertyDefinitionGroup definitionGroup = createPropertyDefinitionGroup(propertyGroup.getCategory(), propertyGroup.getKey());

        // Get a list of properties
        Map properties = propertyGroup.getProperties();
        Iterator propertyIterator = properties.values().iterator();
        // loop through the properties
        while (propertyIterator.hasNext())
        {
            // Get the property
            Property property = (Property) propertyIterator.next();
            // Get the definition Name
            String definitionName = BasicPropertyParser.parseDefinitionBaseName(property.getName());
            // If the name is already built, great, otherwise make one
            PropertyDefinition definition = definitionGroup.getDefinition(definitionName);
            if (definition == null)
            {
                definition = createPropertyDefinition(null,null,null,null,null,definitionName);
                definitionGroup.addDefinition(definition);
            }
            // Get the definitionType
            String definitionType = BasicPropertyParser.parseDefinitionType(property.getName());
            // Set the value on the definition
            definition.setDefinitionValue(property.getValue(),definitionType); 
        }

        return definitionGroup;
    }

    /**
     * Create a property service property group that represents a property definition group.
     * The group is created by parsing the property definition and grouping them into PropertyGroup objects.
     * @return a property service property group that represents one defintion group.
     */
    public static PropertyServicePropertyGroup createPropertyDefinitionGroup(PropertyDefinitionGroup propertyDefinitionGroup)
    {
        // Create a new property service property group
        PropertyServicePropertyGroup propertyServicePropertyGroup = createPropertyGroup(propertyDefinitionGroup.getCategory(), propertyDefinitionGroup.getKey());

        // go thur all definitions and build up the properties that make each up
        Map propertyDefinitions = propertyDefinitionGroup.getDefinitions();
        Iterator iter = propertyDefinitions.values().iterator();
        PropertyDefinition propertyDefinition = null;
        while( iter.hasNext() )
        {
            propertyDefinition = (PropertyDefinition)iter.next();

            // for each possible definition "type" create a property and add it to the group
            String[] fullDefinitionComponents = new String[2];
            fullDefinitionComponents[0] = propertyDefinition.getDefinitionName();

            fullDefinitionComponents[1] = PropertyDefinition.DEFAULT_VALUE_NAME;
            String fullDefinitionName = BasicPropertyParser.buildCompoundString(fullDefinitionComponents);
            propertyServicePropertyGroup.addProperty(createProperty(fullDefinitionName, propertyDefinition.getDefaultValue()));

            fullDefinitionComponents[1] = PropertyDefinition.DATA_TYPE_NAME;
            fullDefinitionName = BasicPropertyParser.buildCompoundString(fullDefinitionComponents);
            String className = "";
            if (propertyDefinition.getDataType() != null)
            {
                className = propertyDefinition.getDataType().getName();
            }
            propertyServicePropertyGroup.addProperty(createProperty(fullDefinitionName, className));

            fullDefinitionComponents[1] = PropertyDefinition.DISPLAY_VALUES_NAME;
            fullDefinitionName = BasicPropertyParser.buildCompoundString(fullDefinitionComponents);
            List list = propertyDefinition.getDisplayValues();
            String[] listStrings = new String[ list.size() ];
            listStrings = (String[]) list.toArray(listStrings);
            propertyServicePropertyGroup.addProperty(createProperty(fullDefinitionName, BasicPropertyParser.buildCompoundString(listStrings)));

            fullDefinitionComponents[1] = PropertyDefinition.POSSIBLE_VALUES_NAME;
            fullDefinitionName = BasicPropertyParser.buildCompoundString(fullDefinitionComponents);
            list = propertyDefinition.getPossibleValues();
            listStrings = new String[list.size()];
            listStrings = (String[]) list.toArray(listStrings);
            propertyServicePropertyGroup.addProperty(createProperty(fullDefinitionName, BasicPropertyParser.buildCompoundString(listStrings)));

            fullDefinitionComponents[1] = PropertyDefinition.DISPLAY_NAME_NAME;
            fullDefinitionName = BasicPropertyParser.buildCompoundString(fullDefinitionComponents);
            propertyServicePropertyGroup.addProperty(createProperty(fullDefinitionName, propertyDefinition.getDisplayName()));

            fullDefinitionComponents[1] = PropertyDefinition.DEFINITION_NAME_NAME;
            fullDefinitionName = BasicPropertyParser.buildCompoundString(fullDefinitionComponents);
            propertyServicePropertyGroup.addProperty(createProperty(fullDefinitionName, propertyDefinition.getDefinitionName()));
        }

        return propertyServicePropertyGroup;
    }
}
