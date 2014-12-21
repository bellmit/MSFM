//
// -----------------------------------------------------------------------------------
// Source file: PropertyDefinitionImpl.java
//
// PACKAGE: com.cboe.domain.property;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.property;

import java.util.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.cboe.exceptions.NotFoundException;

import com.cboe.interfaces.domain.property.PropertyDefinition;

import com.cboe.util.ExceptionBuilder;

/**
 *  The definition for a Property.  This class contains all the elements
 *  that define what a specific property can do.  The name of the definition
 *  is specified in the property, to tie them together.
 */
public class PropertyDefinitionImpl implements PropertyDefinition
{
    protected String defaultValue;
    protected Class  dataType;
    protected List   displayValues;
    protected List   possibleValues;
    protected String displayName;
    protected String definitionName;
    protected boolean defaultAllowed;

    /**
     *  Construct a property definition with all the components.
     */
    public PropertyDefinitionImpl(String defaultValue, Class dataType, List displayValues, List possibleValues,
                                  String displayName, String definitionName)
    {
        setDefaultAllowed(false);       // it is set to true later if the property key mask says so
        setDefaultValue(defaultValue);
        setDataType(dataType);
        setDisplayName(displayName);
        setDefinitionName(definitionName);

        setDisplayValueList(displayValues);
        setPossibleValues(possibleValues);
    }

    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }
        if(!(o instanceof PropertyDefinition))
        {
            return false;
        }

        final PropertyDefinition propertyDefinition = (PropertyDefinition) o;

        if(getDataType() != null ?
           !getDataType().equals(propertyDefinition.getDataType()) : propertyDefinition.getDataType() != null)
        {
            return false;
        }
        if(getDefaultValue() != null ?
           !getDefaultValue().equals(propertyDefinition.getDefaultValue()) :
           propertyDefinition.getDefaultValue() != null)
        {
            return false;
        }
        if(getDefinitionName() != null ?
           !getDefinitionName().equals(propertyDefinition.getDefinitionName()) :
           propertyDefinition.getDefinitionName() != null)
        {
            return false;
        }
        if(getDisplayName() != null ?
           !getDisplayName().equals(propertyDefinition.getDisplayName()) : propertyDefinition.getDisplayName() != null)
        {
            return false;
        }
        if(!getDisplayValues().equals(propertyDefinition.getDisplayValues()))
        {
            return false;
        }
        if(!getPossibleValues().equals(propertyDefinition.getPossibleValues()))
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return (definitionName != null ? definitionName.hashCode() : 0);
    }

    /**
     *  Get the list of display values for the possible values.  The order
     *  of display values and possible values must match.
     *
     *  @return The list of display values.
     */
    public List getDisplayValues() 
    {
        List newList = new ArrayList(displayValues.size());
        newList.addAll(displayValues);
        return newList;
    }

    /**
     *  Set the list of display values.  The display values are the values
     *  that should be displayed for the possible values.  The order of the
     *  lists must be corrdinated.
     *
     *  @param displayValues  The list of display values.
     */
    public void setDisplayValueList(List displayValues) 
    {
        ArrayList newList = new ArrayList();

        if(displayValues != null)
        {
            newList.ensureCapacity(displayValues.size());
            newList.addAll(displayValues);
        }
        this.displayValues = newList;
    }

    /**
     *  Get the default value
     *
     *  @return the default value
     */
    public String getDefaultValue() 
    {
        return defaultValue;
    }

    /**
     * Gets the default value, instantiated as the DataType. Takes of creating an Object that is the Class returned from
     * getDataType(), that represents the value of getDefaultValue().
     * @return defaultValue represented as the Class from getDataType().
     * @throws InstantiationException May be thrown if dataType is not set, or the Class represented by dataType cannot
     * represent the defaultValue.
     * @throws NotFoundException will be thrown if the default value is not set.
     */
    public Object getDefaultValueAsDataType() throws InstantiationException, NotFoundException
    {
        if(getDefaultValue() == null || getDefaultValue().length() == 0)
        {
            throw ExceptionBuilder.notFoundException("DefaultValue is not set.", 0);
        }
        Object returnValue = convertObjectToDataType(getDefaultValue(), "DefaultValue");
        return returnValue;
    }

    /**
     *  Set the default property
     *
     *  @param defaultValue The default value
     */
    public void setDefaultValue(String defaultValue) 
    {
        this.defaultValue = defaultValue;
    }

    /**
     *  Get the data type.
     *
     *  @return The data type
     */
    public Class getDataType() 
    {
        return dataType;
    }

    /**
     *  Set the data type.
     *  @param dataType a class that represents the data type
     */
    public void setDataType(Class dataType) 
    {
        this.dataType = dataType;
    }

    /**
     *  Set the data type.  The string is converted to a class, if the class
     *  is not found, null is set.
     *
     *  @param dataTypeName a String that provides a fully qualified name of the class that represents the data type
     */
    public void setDataType(String dataTypeName) 
    {
        if(dataTypeName == null)
        {
            dataType = null;
        }
        else if(dataTypeName.length() > 0)
        {
            try
            {
                dataType = Class.forName(dataTypeName);
            }
            catch(ClassNotFoundException e)
            {
                dataType = null;
            }
        }
        else
        {
            dataType = null;
        }
    }

    /**
     * Gets the list of possible values, were each element is instantiated as the DataType. Takes of creating an Object
     * that is the Class returned from getDataType(), that represents each possible value.
     * @return list of possible values were each element is represented as the Class from getDataType().
     * @throws InstantiationException May be thrown if dataType is not set, or the Class represented by dataType cannot
     * represent one of the possible values.
     */
    public List getPossibleValuesAsDataType() throws InstantiationException
    {
        List newList = new ArrayList(possibleValues.size());
        for(Iterator iterator = possibleValues.iterator(); iterator.hasNext();)
        {
            Object element = iterator.next();
            newList.add(convertObjectToDataType(element, "PossibleValue"));

        }
        return newList;
    }

    /**
     *  Get the list of possible values.  The values in this
     *  list should all be represented by a display value
     *  in the display value list.
     *  
     *  @return The list of possible values.
     */
    public List getPossibleValues() 
    {
        List newList = new ArrayList(possibleValues.size());
        newList.addAll(possibleValues);
        return newList;
    }

    /**
     *  Set the list of possible values.  The values in this
     *  list should all be represented by a display value
     *  in the display value list.
     *  
     *  @param possibleValues The list of possible values.
     */
    public void setPossibleValues(List possibleValues) 
    {
        ArrayList newList = new ArrayList();

        if(possibleValues != null)
        {
            newList.ensureCapacity(possibleValues.size());
            newList.addAll(possibleValues);
        }
        this.possibleValues = newList;
    }

    /**
     *  Get the display name for the name of the property.
     *
     *  @return The name to display.
     */
    public String getDisplayName() 
    {
        return displayName;
    }

    /**
     *  Set the name to display for properties tied to this defintion.
     *
     *  @param displayName The display name.
     */
    public void setDisplayName(String displayName) 
    {
        this.displayName = displayName;
    }

    /**
     *  Get the name of the property defintion.  This name will be used
     *  to tie the defintion to the properties.
     *
     *  @return the name
     */
    public String getDefinitionName() 
    {
        return definitionName;
    }

    /**
     *  Set the name of the property definition.  The name will be used
     *  to tie the definition to the properties.
     *
     *  @param definitionName of this property definition
     */
    public void setDefinitionName(String definitionName)
    {
        this.definitionName = definitionName;
    }

    /**
     *  Set one part of the definition.
     *
     *  @param value The value to set.
     *  @param type The type to set.
     */
    public void setDefinitionValue(String value, String type)
    {
        if (type.equals(DEFAULT_VALUE_NAME))
        {
            setDefaultValue(value);
        }
        else if (type.equals(DATA_TYPE_NAME))
        {
            setDataType(value);
        }
        else if (type.equals(DISPLAY_VALUES_NAME))
        {
            if(value != null && value.length() > 0)
            {
                List valueList = BasicPropertyParser.parseList(value);
                setDisplayValueList(valueList);
            }
        }
        else if (type.equals(POSSIBLE_VALUES_NAME))
        {
            if(value != null && value.length() > 0)
            {
                List valueList = BasicPropertyParser.parseList(value);
                setPossibleValues(valueList);
            }
        }
        else if (type.equals(DISPLAY_NAME_NAME))
        {
            setDisplayName(value);
        }
        else if (type.equals(DEFINITION_NAME_NAME))
        {
            setDefinitionName(value);
        }
    }

    public Object getDisplayValueForPossibleValue(Object possibleValue) throws NotFoundException
    {
        Object returnValue = null;

        boolean foundValue = false;
        int i = 0;
        for(Iterator iterator = possibleValues.iterator(); iterator.hasNext(); i++)
        {
            Object myPossibleValue = iterator.next();
            if(myPossibleValue.toString().equals(possibleValue.toString()))
            {
                foundValue = true;
                break;
            }
        }
        if(foundValue)
        {
            if(displayValues.size() > i)
            {
                returnValue = displayValues.get(i);
            }
            else
            {
                throw ExceptionBuilder.notFoundException("Display Value did not exist for Possible Value.", 0);
            }
        }
        else
        {
            if(!possibleValues.isEmpty())
            {
                throw ExceptionBuilder.notFoundException("Possible Value did not exist for passed value.", 0);
            }
        }

        return returnValue;
    }

    private Object convertObjectToDataType(Object value, String valueType) throws InstantiationException
    {
        Object returnValue = value;

        if(value != null)
        {
            Class dataType = getDataType();
            if(dataType != null)
            {
                if(!dataType.isInstance(value))
                {
                    Class[] parameterTypes = {value.getClass()};
                    try
                    {
                        Constructor constructor = dataType.getConstructor(parameterTypes);
                        if(constructor != null)
                        {
                            Object[] parameters = {value};
                            returnValue = constructor.newInstance(parameters);
                        }
                        else
                        {
                            InstantiationException exception =
                                    new InstantiationException("Could not find constructor from DataType that accepts " +
                                                               valueType + '.');
                            throw exception;
                        }
                    }
                    catch(NoSuchMethodException e)
                    {
                        InstantiationException exception =
                                new InstantiationException("Could not find constructor from DataType that accepts " +
                                                           valueType + '.');
                        exception.initCause(e);
                        throw exception;
                    }
                    catch(IllegalAccessException e)
                    {
                        InstantiationException exception =
                                new InstantiationException("Could not construct Object from DataType.");
                        exception.initCause(e);
                        throw exception;
                    }
                    catch(InvocationTargetException e)
                    {
                        InstantiationException exception =
                                new InstantiationException("Could not construct Object from DataType.");
                        exception.initCause(e);
                        throw exception;
                    }
                }
            }
            else
            {
                throw new InstantiationException("DataType is not set for this PropertyDefinition.");
            }
        }
        return returnValue;
    }

    public void setDefaultAllowed(boolean defaultAllowed)
    {
        this.defaultAllowed = defaultAllowed;
    }

    public boolean isDefaultAllowed()
    {
        return defaultAllowed;
    }

    public boolean hasDefaultValue()
    {
        return isDefaultAllowed()  &&  getDefaultValue() != null  &&  getDefaultValue().length() > 0;
    }

    public boolean isDefaultValue(Object value)
    {
        return value != null
           &&  hasDefaultValue()
           &&  (value.equals(getDefaultValue())  ||  value.toString().equals(getDefaultValue().toString()));
    }


    @Override
    public String toString()
    {
        String defString = "for defName=[" + getDefinitionName() + "] dispName=["
                         + getDisplayName() + "] dataType=[" + getDataType()
                         + "] defValue=[" + getDefaultValue() + "]";
        defString += "\n\t\t possible values=[";
        int count = 0;
        for(Object val : getPossibleValues())
        {
            if (count++ > 0)
            {
                defString += ", ";
            }
            defString += val.toString();
        }
        defString += "]";

        return defString;
    }
}
