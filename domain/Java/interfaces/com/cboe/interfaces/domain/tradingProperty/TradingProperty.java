//
// -----------------------------------------------------------------------------------
// Source file: TradingProperty.java
//
// PACKAGE: com.cboe.interfaces.domain.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.tradingProperty;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;

import com.cboe.interfaces.domain.property.Property;
import com.cboe.interfaces.domain.property.PropertyDefinition;

/**
 * Provides the contract that all Trading Properties must adhere to.
 */
public interface TradingProperty extends Cloneable, Comparable
{
    String PROPERTY_DEFINITION_CHANGE_EVENT = "PropertyDefinition";
    String INTEGER1_CHANGE_EVENT = "Integer1";
    String INTEGER2_CHANGE_EVENT = "Integer2";
    String INTEGER3_CHANGE_EVENT = "Integer3";
    String DOUBLE1_CHANGE_EVENT = "Double1";
    String DOUBLE2_CHANGE_EVENT = "Double2";
    String DOUBLE3_CHANGE_EVENT = "Double3";
    String SEQUENCE_NUMBER_CHANGE_EVENT = "SequenceNumber";

    Object clone() throws CloneNotSupportedException;

    /**
     * Registers a listener to be informed whenever changes to this TradingProperty occur. The PropertyChangeListener
     * will be informed of internal changes, not reflective of an event channel event.
     * @param listener to register
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Removes a previous registration.
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * Provides immutable getter for Trading Session Name
     * @return Trading Session Name that this TradingProperty is for
     */
    String getSessionName();

    /**
     * Provides immutable getter for class key
     * @return class key that this TradingProperty is for
     */
    int getClassKey();

    /**
     * Provides the setter for the conversion of the Property, representing a PropertyStruct, to this TradingProperty.
     * The Property, representing a PropertyStruct, is the transport mechanism used to move TradingProperty's
     * across CORBA, using the PropertyService IDL. This set and the corresponding get, act as the translation
     * mechanism for transport across CORBA.
     * @param property to translate the TradingProperty values from
     * @throws DataValidationException is thrown when the passed Property does not reflect the Trading Properties
     * appropriately for the implementing TradingProperty.
     */
    void setProperty(Property property) throws DataValidationException;

    /**
     * Provides the getter for the conversion of this TradingProperty to a Property, representing a PropertyStruct.
     * The Property, representing a PropertyStruct, is the transport mechanism used to move TradingProperty's across
     * CORBA, using the PropertyService IDL. This get and the corresponding set, act as the translation mechanism for
     * transport across CORBA.
     * @return Property that represents the value from the implementing TradingProperty.
     */
    Property getProperty();

    /**
     * Gets the name of the Property that defined this TradingProperty. Should be unique among all of the same
     * implementations of this interface.
     */
    String getPropertyName();

    /**
     * Gets the Type for this TradingProperty
     */
    TradingPropertyType getTradingPropertyType();

    /**
     * Gets the PropertyDefinition for the passed fieldName that defines what is allowed for that field.
     * @param fieldName to get PropertyDefinition for. This fieldName will be as defined by the JavaBeans specification.
     * @return defines the allowed values, display values, etc. for the passed fieldName.
     * @throws NotFoundException
     */
    PropertyDefinition getPropertyDefinition(String fieldName)
            throws NotFoundException;

    /**
     * Sets the PropertyDefinition for the passed fieldName that defines what is allowed for that field.
     * @param fieldName to set PropertyDefinition for. This fieldName will be as defined by the JavaBeans specification.
     * @param newDefinition to set.
     */
    void setPropertyDefinition(String fieldName, PropertyDefinition newDefinition);

    /**
     * Gets all the PropertyDefinition's regardless of fieldName
     */
    PropertyDefinition[] getAllPropertyDefinitions();

    /**
     * Can be used to determine if this TradingProperty had its PropertyDefinition's modified
     * @return True if modified, false otherwise.
     */
    boolean isPropertyDefinitionsModified();

    /**
     * Gets the Class that implements the GUI representation of this TradingProperty.
     * @return May return null if this TradingProperty does not support its own customizer.
     * @exception IntrospectionException that could be returned from the Introspector
     */
    Class getCustomizerClass()
            throws IntrospectionException;

    /**
     * Gets the PropertyDescriptor's for this TradingProperty.
     * @return The PropertyDescriptor's that are returned from the BeanInfo
     * @exception IntrospectionException that could be returned from the Introspector
     */ 
    PropertyDescriptor[] getPropertyDescriptors()
            throws IntrospectionException;

    /**
     * Gets the default property descriptor that represents the key field for this TradingProperty.
     * @return default field property descriptor. May return null if one is not defined.
     * @exception IntrospectionException that could be returned from the Introspector
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
     * Sets all field values to their default value. This default may be defined by a property definition or
     * some other method.
     */
    void initializeDefaultValues()
            throws IntrospectionException;
}