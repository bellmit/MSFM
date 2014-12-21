package com.cboe.domain.routingProperty;
// -----------------------------------------------------------------------------------
// Source file: AbstractBaseProperty
//
// PACKAGE: com.cboe.domain.routingProperty
// 
// Created: Jun 20, 2006 2:34:02 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import com.cboe.idl.cmiErrorCodes.NotFoundCodes;

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;

import com.cboe.interfaces.domain.property.Property;
import com.cboe.interfaces.domain.property.PropertyDefinition;
import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;

import com.cboe.util.ExceptionBuilder;

import com.cboe.domain.property.BasicPropertyParser;
import com.cboe.domain.property.PropertyDefinitionCache;
import com.cboe.domain.property.PropertyFactory;
import com.cboe.domain.routingProperty.validation.ValidationSupport;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

abstract public class AbstractBaseProperty implements BaseProperty
{
    private String propertyCategory;
    private String propertyName;

    protected BasePropertyKey key;
    protected PropertyChangeSupport propertyChangeSupport;
    private BasePropertyType type;

    private Map<String, PropertyDefinition> propertyDefinitionMap;
    private boolean definitionsModified;

    private BeanInfo myBeanInfo;
    private PropertyDescriptor[] myPropertyDescriptors;
    private PropertyDescriptor defaultFieldPropertyDescriptor;

    private boolean isOptional;
    private ValidationSupport validationSupport;

    protected AbstractBaseProperty(String propertyCategory, String propertyName, BasePropertyKey key, BasePropertyType type)
    {
        this(propertyCategory, propertyName, key, type, null);
    }

    protected AbstractBaseProperty(String propertyCategory, String propertyName, BasePropertyKey key,
                                   BasePropertyType type, List<Validator> vals)
    {
        this.propertyCategory = propertyCategory;
        this.propertyName = propertyName;
        this.key = key;
        this.type = type;

        initialize();
        setValidators(vals);
    }

    public BasePropertyKey getPropertyKey()
    {
        return key;
    }

    public String getPropertyName()
    {
        return propertyName;
    }

    public int hashCode()
    {
        return getPropertyName().hashCode();
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(45);
        buffer.append(getPropertyName()).append('=');
        buffer.append(getProperty().getValue());

        return buffer.toString();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Overridden to check names, frim, exchangeAcroynm, propertyName and type
     */
    public boolean equals(Object otherObject)
    {
        boolean isEqual = super.equals(otherObject);
        if(isEqual)
        {
            if(otherObject instanceof AbstractBaseProperty)
            {
                AbstractBaseProperty casted = (AbstractBaseProperty) otherObject;

                isEqual = (this.key.equals(casted.key) &&
                           this.getPropertyType().equals(casted.getPropertyType()) &&
                           this.getPropertyName().equals(casted.getPropertyName()));
            }
            else
            {
                isEqual = false;
            }
        }

        return isEqual;
    }

    public Object clone() throws CloneNotSupportedException
    {
        AbstractBaseProperty property = (AbstractBaseProperty) super.clone();
        property.key = (BasePropertyKey) key.clone();
        property.isOptional = isOptional;

        property.validationSupport = validationSupport.clone();

        return property;
    }

    public void setProperty(Property property) throws DataValidationException
    {
        if(property != null)
        {
            String stringValue = property.getValue();
            if(stringValue != null)
            {
                try
                {
                    decodeValue(stringValue);
                }
                catch(NumberFormatException e)
                {
                    throw ExceptionBuilder
                            .dataValidationException("Invalid property value: property name='" + property.getName() +
                                                     "' property value='" + property.getValue() + "'", 0);
                }
            }
            else
            {
                throw ExceptionBuilder
                        .dataValidationException("Invalid property value: property name='" + property.getName() +
                                                 "' property value='" + property.getValue() + "'", 0);
            }
        }
        else
        {
            throw ExceptionBuilder.dataValidationException("Property may not be null.", 0);
        }
    }

    public Property getProperty()
    {
        List<String> nameList = new ArrayList<String>(1);
        nameList.add(getPropertyName());

        List valueList = getEncodedValuesAsStringList();

        Property newProperty = PropertyFactory.createProperty(nameList, valueList, null);

        return newProperty;
    }

    public BasePropertyType getPropertyType()
    {
        return type;
    }

    /**
     * Gets the Class that implements the GUI representation of this BaseProperty.
     * @return May return null if this BaseProperty does not support its own customizer.
     * @throws java.beans.IntrospectionException that could be returned from the Introspector
     */
    public Class getCustomizerClass() throws IntrospectionException
    {
        BeanInfo myBeanInfo = getMyBeanInfo();
        return myBeanInfo.getBeanDescriptor().getCustomizerClass();
    }

    /**
     * Gets the PropertyDescriptor's for this BaseProperty.
     * @return The PropertyDescriptor's that are returned from the BeanInfo
     * @throws java.beans.IntrospectionException that could be returned from the Introspector
     */
    public PropertyDescriptor[] getPropertyDescriptors() throws IntrospectionException
    {
        if(myPropertyDescriptors == null)
        {
            BeanInfo myBeanInfo = getMyBeanInfo();

            PropertyDescriptor[] descriptors = myBeanInfo.getPropertyDescriptors();
            if(descriptors != null)
            {
                myPropertyDescriptors = new PropertyDescriptor[descriptors.length];
                System.arraycopy(descriptors, 0, myPropertyDescriptors, 0, descriptors.length);
                Arrays.sort(myPropertyDescriptors, getPropertyDescriptorSortComparator());
            }
            else
            {
                myPropertyDescriptors = new PropertyDescriptor[0];
            }
        }
        PropertyDescriptor[] newArray = new PropertyDescriptor[myPropertyDescriptors.length];
        System.arraycopy(myPropertyDescriptors, 0, newArray, 0, myPropertyDescriptors.length);
        return newArray;
    }

    /**
     * Gets the default property descriptor that represents the key field for this BaseProperty.
     * @return default field property descriptor. May return null if one is not defined.
     * @throws java.beans.IntrospectionException that could be returned from the Introspector
     */
    public PropertyDescriptor getDefaultPropertyDescriptor() throws IntrospectionException
    {
        if(defaultFieldPropertyDescriptor == null)
        {
            BeanInfo myBeanInfo = getMyBeanInfo();
            int defaultPropIndex = myBeanInfo.getDefaultPropertyIndex();
            PropertyDescriptor[] descriptors = myBeanInfo.getPropertyDescriptors();
            if(defaultPropIndex > -1 && descriptors != null && descriptors.length > defaultPropIndex)
            {
                defaultFieldPropertyDescriptor = descriptors[defaultPropIndex];
            }
        }

        return defaultFieldPropertyDescriptor;
    }

    /**
     * Gets the actual value for the propertyDescriptor from this Object
     * @param propertyDescriptor for field in this Object
     * @return value returned from read method in propertyDescriptor from this Object
     */
    public Object getFieldValue(PropertyDescriptor propertyDescriptor)
            throws IllegalAccessException, InvocationTargetException
    {
        Object returnValue;
        Method readMethod = propertyDescriptor.getReadMethod();
        returnValue = readMethod.invoke(this, null);
        return returnValue;
    }

    /**
     * Sets the actual value for the propertyDescriptor to this Object
     * @param propertyDescriptor for field in this Object
     * @param value Object value to set with
     */
    public void setFieldValue(PropertyDescriptor propertyDescriptor, Object value)
            throws IllegalAccessException, InvocationTargetException
    {
        Method writeMethod = propertyDescriptor.getWriteMethod();
        Object[] values = {value};
        writeMethod.invoke(this, values);
    }

    /**
     * Sets all field values to their default value. This default may be defined by a property definition or some other
     * method.
     */
    public void initializeDefaultValues() throws IntrospectionException
    {
        PropertyDescriptor[] descriptors = getPropertyDescriptors();
        for(int i = 0; i < descriptors.length; i++)
        {
            PropertyDescriptor descriptor = descriptors[i];
            try
            {
                PropertyDefinition definition = getPropertyDefinition(descriptor.getName());
                if(definition != null)
                {
                    Object defaultValue = definition.getDefaultValueAsDataType();
                    setFieldValue(descriptor, defaultValue);
                }
            }
            catch(NotFoundException e)
            {
                //normal flow, just don't set default value
            }
            catch(InstantiationException e)
            {
                Log.exception("Could not set default value for:" + descriptor.getName(), e);
            }
            catch(IllegalAccessException e)
            {
                Log.exception("Could not set default value for:" + descriptor.getName(), e);
            }
            catch(InvocationTargetException e)
            {
                Log.exception("Could not set default value for:" + descriptor.getName(), e);
            }
        }
    }

    /**
     * Gets the PropertyDefinition for the passed fieldName that defines what is allowed for that field.
     * @param fieldName to get PropertyDefinition for. This fieldName will be as defined by the JavaBeans
     * specification.
     * @return defines the allowed values, display values, etc. for the passed fieldName.
     */
    public PropertyDefinition getPropertyDefinition(String fieldName) throws NotFoundException
    {
        Object[] elements = {getPropertyType().getName(), fieldName};
        String name = BasicPropertyParser.buildCompoundString(elements);
        PropertyDefinition definition = propertyDefinitionMap.get(name);
        if(definition == null)
        {
            definition =
                    PropertyDefinitionCache.getInstance().getPropertyDefinition(propertyCategory, name);

            if(definition != null)
            {
                propertyDefinitionMap.put(name, definition);
                return definition;
            }
            else
            {
                throw ExceptionBuilder.notFoundException("Property Definition does not exist for: " + fieldName,
                                                         NotFoundCodes.RESOURCE_DOESNT_EXIST);
            }
        }
        else
        {
            return definition;
        }
    }

    /**
     * Used to compare another BaseProperty to this one.
     * @param object to compare this with.
     * @return as defined in java.lang.Comparable
     */
    public abstract int compareTo(Object object);


    // to have no validators, set it to an empty list
    public void setValidators(List<Validator> newValidators)
    {
        if (newValidators == null)
        {
            newValidators = getDefaultValidators();
        }
        validationSupport.addValidators(newValidators);
    }

    public List<Validator> getValidators()
    {
        return validationSupport.getValidators();
    }

    public void setOptional(boolean optional)
    {
        isOptional = optional;
    }

    public boolean isOptional()
    {
        return isOptional;
    }

    protected List<Validator> getDefaultValidators()
    {
        return new ArrayList<Validator>(2);
    }

    protected void firePropertyChange(PropertyChangeEvent evt)
    {
        propertyChangeSupport.firePropertyChange(evt);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue)
    {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Allows the Trading Property to determine the order of the PropertyDescriptor's. This implementation will just
     * return a comparator that will sort by property name.
     * @return comparator to use for sorting the returned PropertyDescriptor's from getPropertyDescriptors().
     */
    protected Comparator getPropertyDescriptorSortComparator()
    {
        return new ForcedPropertyDescriptorComparator(null);
    }

    /**
     * Parses the field values from the passed String
     * @param value to parse
     */
    protected abstract void decodeValue(String value);

    /**
     * Get all the Property values as a List of Strings
     * @return List of Strings
     */
    protected abstract List getEncodedValuesAsStringList();

    private void initialize()
    {
        propertyDefinitionMap = new HashMap<String, PropertyDefinition>(10);
        propertyChangeSupport = new PropertyChangeSupport(this);
        validationSupport     = new ValidationSupport();
    }

    /**
     * Finds this class implementations BeanInfo and caches it.
     */
    private BeanInfo getMyBeanInfo() throws IntrospectionException
    {
        if(myBeanInfo == null)
        {
            myBeanInfo = Introspector.getBeanInfo(getClass());
        }
        return myBeanInfo;
    }

    void setType(BasePropertyType type)
    {
        this.type = type;
    }
}
