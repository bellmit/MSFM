//
// -----------------------------------------------------------------------------------
// Source file: PropertyServicePropertyImpl.java
//
// PACKAGE: com.cboe.domain.property;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.property;

import java.util.List;
import java.util.ArrayList;

import com.cboe.idl.property.PropertyStruct;

import com.cboe.interfaces.domain.property.PropertyDefinition;
import com.cboe.interfaces.domain.property.PropertyServiceProperty;
import com.cboe.interfaces.domain.property.PropertyDefinitionGroup;
import com.cboe.interfaces.domain.property.Property;

public class PropertyServicePropertyImpl implements PropertyServiceProperty
{
    protected List nameList;

    protected List valueList;
    protected PropertyDefinition propertyDefinition;

    /**
     *  Build a blank property.
     */
    public PropertyServicePropertyImpl()
    {
        super();
    }

    /**
     *  Build a property with an encoded version of the name and value.
     */
    public PropertyServicePropertyImpl(String encodedName, String encodedValue)
    {
        this();
        decodeName(encodedName);
        decodeValue(encodedValue);
    }

    /**
     *  Build a property with a namelist, propertyDefinition and valuelist.
     */
    public PropertyServicePropertyImpl(List nameList, List valueList, PropertyDefinition propertyDefinition)
    {
        this();
        this.nameList = nameList;
        this.propertyDefinition = propertyDefinition;
        this.valueList = valueList;
    }

    /**
     *  Build a property from a struct.
     *
     */
    public PropertyServicePropertyImpl(PropertyStruct struct, PropertyDefinitionGroup definitionGroup)
    {
        this();
        decodeName(struct.name);
        decodeValue(struct.value, definitionGroup);
    }

    /**
     *  Build a property from a struct.
     *
     */
    public PropertyServicePropertyImpl(PropertyStruct struct)
    {
        this(struct, null);
    }

    public boolean equals(Object otherObject)
    {
        boolean isEqual = super.equals(otherObject);

        if(!isEqual)
        {
            if(otherObject instanceof Property)
            {
                Property castedObject = (Property) otherObject;
                if(getName().equals(castedObject.getName()) &&
                   getValue().equals(castedObject.getValue()))
                {
                    if(getPropertyDefinition() == null)
                    {
                        if(castedObject.getPropertyDefinition() == null)
                        {
                            isEqual = true;
                        }
                    }
                    else if(getPropertyDefinition().equals(castedObject.getPropertyDefinition()))
                    {
                        isEqual = true;
                    }
                }
            }
        }

        return isEqual;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(150);
        buffer = dumpProperty(buffer);
        return buffer.toString();
    }

    /**
     *  Get a string representation of the name.
     *
     *  @return a string representation of the name.
     */
    public String getName()
    {
        return getEncodedName();
    }

    /**
     *  Get the list of elements in the name.
     */
    public List getNameList()
    {
        return nameList;
    }

    /**
     *  Set the name list.
     */
    public void setNameList(List nameList)
    {
        this.nameList = nameList;
    }

    /**
     *  Get the list of values.
     *
     *  @return The list of values for this property.
     */
    public List getValueList()
    {
        return valueList;
    }

    /**
     * Sets the Value List collection of elements.
     * @param valueList
     */
    public void setValueList(List valueList)
    {
        this.valueList = valueList;
    }

    /**
     *  Get the value for the property.  If the value list contains more than
     *  one value, the values are separated by the Delimiter.
     *  @return The value
     */
    public String getValue()
    {
        String value;
        if (valueList.size() == 1)
        {
            value = String.valueOf(valueList.get(0));
        }
        else
        {
            // get the encoded value without the appended property definition name
            value = getEncodedValue(false);
        }
        return value;
    }

    /** 
     *  Set the value of the property.
     *  @param value The new value
     */
    public void setValue(String value)
    {
        valueList = new ArrayList();
        valueList.add(value);
    }

    /**
     *  Get the struct this data represents.
     *
     *  @return A struct representing this property.
     */
    public PropertyStruct getStruct()
    {
        return buildStruct();
    }

    /**
     *  Set the object to the values in this struct.
     */
    public void setStruct(PropertyStruct struct)
    {
        // Set the base values, then calculate the rest.
        decodeName(struct.name);
        decodeValue(struct.value);
    }

    /**
     *  Get the property definition.
     *
     *  @return the property definition, null if none is set.
     */
    public PropertyDefinition getPropertyDefinition()
    {
        return propertyDefinition;
    }

    /**
     * Sets the property definition for this property.
     * @param propertyDefinition The defintion for this property.
     */
    public void setPropertyDefinition(PropertyDefinition propertyDefinition)
    {
        this.propertyDefinition = propertyDefinition;
    }

    /**
     *  Build a struct represented by this property.
     */
    protected PropertyStruct buildStruct()
    {
        PropertyStruct propertyStruct = new PropertyStruct();
        propertyStruct.name = getEncodedName();
        propertyStruct.value = getEncodedValue();

        return propertyStruct;
    }

    /**
     *  Rebuild the name derived values based on the unparsed name field.
     */
    protected void decodeName(String name)
    {
        nameList = BasicPropertyParser.parseList(name);    
    }

    /**
     *  Rebuild the value derived values based on the unparsed value field.
     *  The unparsed value field is assumed to have the following fields in
     *  order.
     *     0*) valueList
     *     1)  propertyDefinitionName
     */
    protected void decodeValue(String valueString)
    {
        decodeValue(valueString, null);
    }

    /**
     * Rebuild the value derived values based on the unparsed value field.
     * The unparsed value field is assumed to have the following fields in
     * order.
     *    0*) valueList
     *    1)  propertyDefinitionName
     * The last field parsed will only be assumed to be a propertyDefinitionName if the value begins with "@".
     * In addition, if the property definition group is not null, the method
     * will try to attach a property definition to this property.
     */
    protected void decodeValue(String valueString, PropertyDefinitionGroup definitions)
    {
        List decodedList = BasicPropertyParser.parseList(valueString);
        String propertyDefinitionName = null;

        // Next comes the list of actual values
        ArrayList actualValues = new ArrayList();

        if(decodedList.size() > 0)
        {
            for (int i = 0; i < decodedList.size() - 1; i++)
            {
                actualValues.add(decodedList.get(i));
            }
            String fieldValue = (String) decodedList.get(decodedList.size() - 1);
            if(fieldValue.startsWith(VALUE_FIELD_DEFINITION_QUALIFIER))
            {
                propertyDefinitionName = fieldValue.substring(1);
            }
            else
            {
                actualValues.add(fieldValue);
            }
        }

        this.valueList = actualValues;

        if(definitions != null && propertyDefinitionName != null)
        {
            attachDefinition(definitions, propertyDefinitionName);
        }
    }

    /**
     *  Encode the name into a String, based on the list.
     *  @return Encoded String version of the list
     */
    protected String getEncodedName()
    {
        String name = BasicPropertyParser.buildCompoundString(nameList.toArray());

        return name;
    }

    /**
     *  Encode all of the value elements into a String.
     *
     */
    protected String getEncodedValue()
    {
        return getEncodedValue(true);
    }

    /**
     *  Encode all of the value elements into a String.
     *
     *  @param appendDefinition Flag for determining if the propertyDefinition name should be appended.
     */
    protected String getEncodedValue(boolean appendDefinition)
    {
        StringBuffer value = new StringBuffer();

        value.append(BasicPropertyParser.buildCompoundString(valueList.toArray()));

        if (appendDefinition)
        {
            // Add the propertyDefinitionName
            value.append(DELIMETER).append(VALUE_FIELD_DEFINITION_QUALIFIER);
            if (propertyDefinition != null)
            {
                value.append(propertyDefinition.getDefinitionName());
            }
            else
            {
                value.append(PropertyDefinition.NO_DEFINITION);
            }
        }
        return value.toString();
    }

    /**
     *  Seek the definitions for one that matches the name.  If one matches, then
     *  attach it to this property.
     *  @param definitions The definitions for this category
     *  @param definitionName Name of the definition
     */
    protected void attachDefinition(PropertyDefinitionGroup definitions, String definitionName)
    {
        setPropertyDefinition(definitions.getDefinition(definitionName));
    }

    public StringBuffer dumpProperty(StringBuffer buffer)
    {
        buffer.append("PropertyServiceProprtyImpl::name=").append(getNameList()).append("\n");
        buffer.append("PropertyServiceProprtyImpl::value=").append(getValueList()).append("\n");
        buffer.append("PropertyServiceProprtyImpl::encodedName=").append(getEncodedName()).append("\n");
        buffer.append("PropertyServiceProprtyImpl::encodedValue=").append(getEncodedValue()).append("\n");
        buffer.append("PropertyServiceProprtyImpl::propertyDefinitionName=").append(getPropertyDefinition()).append("\n");
        return buffer;
    }
}
