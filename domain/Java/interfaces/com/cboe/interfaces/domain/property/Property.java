package com.cboe.interfaces.domain.property;

import java.util.List;

import com.cboe.interfaces.domain.Delimeter;

//
// -----------------------------------------------------------------------------------
// Source file: Property
//
// PACKAGE: com.cboe.interfaces.domain.property;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

public interface Property
{
    public static final String VALUE_FIELD_DEFINITION_QUALIFIER = "@";
    public static final char DELIMETER = Delimeter.PROPERTY_DELIMETER;

    /**
     *  Get the name as represented by a String.
     *
     *  @return a String representation of the name.
     */
    public String getName();

    /**
     * Get the list of names.
     *
     * @return The list of names
     */
    public List getNameList();
    /**
     *  Set the name list.
     */
    public void setNameList(List nameList);
    /**
     *  Get the value.
     *
     *  @return The value for this property.
     */
    public String getValue();
    /**
     * Sets the Value for the property.
     * @param value
     */
    public void setValue(String value);
    /**
     *  Get the property definition for this property.
     *
     *  @return The PropertyDefinition for this property, null if one is not set.
     */
    public PropertyDefinition getPropertyDefinition();

    /**
     * Sets the property definition for this property.
     * @param propertyDefinition The defintion for this property.
     */
    public void setPropertyDefinition(PropertyDefinition propertyDefinition);

    /**
     * Get the list of values.
     * @return The list of values
     */
    public List getValueList();
}
