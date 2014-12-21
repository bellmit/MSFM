//
// -----------------------------------------------------------------------------------
// Source file: PropertyDefinition.java
//
// PACKAGE: com.cboe.interfaces.domain.property;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.property;

import java.util.List;

import com.cboe.exceptions.NotFoundException;

/**
 *  The definition for a Property.  This class contains all the elements
 *  that define what a specific property can do.  The name of the definition
 *  is specified in the property, to tie them together.
 */
public interface PropertyDefinition
{
    // The key value used to store the property definitions in each category.
    // This key cannot be used by a normal property.
    String  PROPERTY_DEFINITION_KEY = "propertyDefinitionKey";

    // This contstant is used to identify that the property does not have a 
    // definition.
    String  NO_DEFINITION           = "NONE";
    
    // These constants are used to append to the name for specifying the type of defintion
    String DEFAULT_VALUE_NAME   = "default";
    String DATA_TYPE_NAME       = "dataType";
    String DISPLAY_VALUES_NAME  = "displayValues";
    String POSSIBLE_VALUES_NAME = "possibleValues";
    String DISPLAY_NAME_NAME    = "displayName";
    String DEFINITION_NAME_NAME = "definitionName";

    /**
     *  Get the list of display values for the possible values.  The order
     *  of display values and possible values must match.
     *
     *  @return The list of display values.
     */
    List getDisplayValues();

    /**
     *  Set the list of display values.  The display values are the values
     *  that should be displayed for the possible values.  The order of the
     *  lists must be corrdinated.
     *
     *  @param displayValues  The list of display values.
     */
    void setDisplayValueList(List displayValues);

    /**
     *  Get the default value
     *
     *  @return the default value
     */
    String getDefaultValue();

    /**
     * Gets the default value, instantiated as the DataType. Takes of creating an Object that is the Class returned
     * from getDataType(), that represents the value of getDefaultValue().
     * @return defaultValue represented as the Class from getDataType().
     * @throws InstantiationException May be thrown if dataType is not set, or the Class represented by dataType
     * cannot represent the defaultValue.
     * @throws NotFoundException may be thrown if the default value is not set.
     */
    Object getDefaultValueAsDataType() throws InstantiationException, NotFoundException;

    /**
     *  Set the default property
     *
     *  @param defaultValue The default value
     */
    void setDefaultValue(String defaultValue);

    /**
     *  Get the data type.
     *
     *  @return The data type
     */
    Class getDataType();

    /**
     *  Set the data type.
     *
     *  @param the data type.
     */
    void setDataType(Class dataType);

    /**
     * Gets the list of possible values, were each element is instantiated as the DataType. Takes of creating an
     * Object that is the Class returned from getDataType(), that represents each possible value.
     * @return list of possible values were each element is represented as the Class from getDataType().
     * @throws InstantiationException May be thrown if dataType is not set, or the Class represented by dataType cannot
     * represent one of the possible values.
     */
    List getPossibleValuesAsDataType() throws InstantiationException;

    /**
     *  Get the list of possible values.  The values in this
     *  list should all be represented by a display value
     *  in the display value list.
     *  
     *  @return The list of possible values.
     */
    List getPossibleValues();

    /**
     *  Set the list of possible values.  The values in this
     *  list should all be represented by a display value
     *  in the display value list.
     *  
     *  @param possibleValues The list of possible values.
     */
    void setPossibleValues(List possibleValues);

    /**
     *  Get the display name for the name of the property.
     *
     *  @return The name to display.
     */
    String getDisplayName();

    /**
     *  Set the name to display for properties tied to this defintion.
     *
     *  @param displayName The display name.
     */
    void setDisplayName(String displayName);

    /**
     *  Get the name of the property defintion.  This name will be used
     *  to tie the defintion to the properties.
     *
     *  @return the name
     */
    String getDefinitionName();

    /**
     *  Set the name of the property definition.  The name will be used
     *  to tie the definition to the properties.
     *  @param name the property definition name.
     */
    void setDefinitionName(String name);

    /**
     *  Set a part of the definition.
     *
     *  @param value The value to set
     *  @param type The type of definition field to set.
     */
    void setDefinitionValue(String value, String type);

    Object getDisplayValueForPossibleValue(Object possibleValue)
            throws NotFoundException;

    void setDefaultAllowed(boolean defaultAllowed);

    boolean isDefaultAllowed();

    boolean hasDefaultValue();

    boolean isDefaultValue(Object value);
}
