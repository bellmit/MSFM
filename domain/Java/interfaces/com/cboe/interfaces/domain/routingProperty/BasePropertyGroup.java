package com.cboe.interfaces.domain.routingProperty;
// -----------------------------------------------------------------------------------
// Source file: BasePropertyGroup
//
// PACKAGE: com.cboe.interfaces.domain.routingProperty
// 
// Created: Jun 20, 2006 2:33:36 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.beans.IntrospectionException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.cboe.idl.cmiUser.ExchangeFirmStruct;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;

import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;

import com.cboe.util.event.EventChannelListener;

public interface BasePropertyGroup extends Cloneable
{
    void addPropertyChangeListener(PropertyChangeListener listener);

    void removePropertyChangeListener(PropertyChangeListener listener);

    String getFirmNumber();

    String getExchangeAcronym();

    ExchangeFirmStruct getExchangeFirmStruct();

    String getPropertyName();

    String getSessionName();

    int getVersionNumber();

    BasePropertyKey getPropertyKey();

    BaseProperty getProperty(String name) throws DataValidationException;

    BaseProperty[] getAllProperties();

    Object clone() throws CloneNotSupportedException;

    void setPropertyGroup(PropertyServicePropertyGroup propertyGroup) throws DataValidationException,
                                                                             InvocationTargetException;

    PropertyServicePropertyGroup getPropertyGroup();

    void save()
            throws SystemException, CommunicationException, AuthorizationException,
                   DataValidationException, TransactionFailedException, NotFoundException, InvocationTargetException;

    void delete()
            throws SystemException, CommunicationException, AuthorizationException,
                   DataValidationException, TransactionFailedException, NotFoundException;

    /**
     * Subscribe an EventChannelListener to update/remove events on the is property group
     */
    void subscribe(EventChannelListener listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Unsubscribe an EventChannelListener to update/remove events on the is property group
     */
    void unsubscribe(EventChannelListener listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Gets the BasePropertyType for this group that identifies the type of this group.
     */
    BasePropertyType getType();

    /**
     * Gets the Class that implements the GUI representation of this group.
     * @return May return null if this group does not support its own customizer.
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
     * Gets the actual value for the propertyDescriptor from this Object
     * @param propertyDescriptor for field in this Object
     * @return value returned from read method in propertyDescriptor from this Object
     */
    Object getFieldValue(PropertyDescriptor propertyDescriptor)
            throws IllegalAccessException, InvocationTargetException;

    void copyProperties(PropertyServicePropertyGroup propertyGroup) throws DataValidationException;

    void setValidators(List<Validator> validators);

    void addValidators(List<Validator> validators);

    void addValidator(Validator validator);

//    void setValidator(Validator validator);
//    void addValidators(List<Validator> validators);
//    void removeValidator(Validator validator);
//    void removeValidators(List<Validator> validators);
//    void clearValidators();

    boolean isValid(StringBuffer validationReport);
}
