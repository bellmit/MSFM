package com.cboe.interfaces.domain.routingProperty;
// -----------------------------------------------------------------------------------
// Source file: BaseProperty
//
// PACKAGE: com.cboe.interfaces.domain.routingProperty
// 
// Created: Jun 20, 2006 2:33:23 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.beans.IntrospectionException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.property.Property;

public interface BaseProperty extends Cloneable
{
    void addPropertyChangeListener(PropertyChangeListener listener);

    void removePropertyChangeListener(PropertyChangeListener listener);

    BasePropertyKey getPropertyKey();

    String getPropertyName();

    void setProperty(Property property) throws DataValidationException;

    Property getProperty();

    /**
     * Gets the Type for this BaseProperty
     */
    BasePropertyType getPropertyType();

    Object clone() throws CloneNotSupportedException;

    /**
     * Gets the Class that implements the GUI representation of this BaseProperty.
     * @return May return null if this BaseProperty does not support its own customizer.
     * @throws java.beans.IntrospectionException that could be returned from the Introspector
     */
    Class getCustomizerClass()
            throws IntrospectionException;

    /**
     * Gets the PropertyDescriptor's for this BaseProperty.
     * @return The PropertyDescriptor's that are returned from the BeanInfo
     * @throws IntrospectionException that could be returned from the Introspector
     */
    PropertyDescriptor[] getPropertyDescriptors()
            throws IntrospectionException;

    /**
     * Gets the default property descriptor that represents the key field for this BaseProperty.
     * @return default field property descriptor. May return null if one is not defined.
     * @throws IntrospectionException that could be returned from the Introspector
     */
    PropertyDescriptor getDefaultPropertyDescriptor()
            throws IntrospectionException;

    /**
     * Gets the actual value for the propertyDescriptor from this Object
     * @param propertyDescriptor for field in this Object
     * @return value returned from read method in propertyDescriptor from this Object
     */
    Object getFieldValue(PropertyDescriptor propertyDescriptor)
            throws IllegalAccessException, InvocationTargetException;

    /**
     * Sets the actual value for the propertyDescriptor to this Object
     * @param propertyDescriptor for field in this Object
     * @param value Object value to set with
     */
    void setFieldValue(PropertyDescriptor propertyDescriptor, Object value)
            throws IllegalAccessException, InvocationTargetException;

    /**
     * Sets all field values to their default value. This default may be defined by a property definition or some other
     * method.
     */
    void initializeDefaultValues()
            throws IntrospectionException;

    void setValidators(List<Validator> validators);

    List<Validator> getValidators();

//    void setValidator(Validator validator);
//    void addValidators(List<Validator> validators);
//    void addValidator(Validator validator);
//    void removeValidator(Validator validator);
//    void removeValidators(List<Validator> validators);
//    void clearValidators();

    void setOptional(boolean optional);

    boolean isOptional();
}
