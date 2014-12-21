package com.cboe.interfaces.domain.routingProperty;
// -----------------------------------------------------------------------------------
// Source file: MutableBasePropertyKey
//
// PACKAGE: com.cboe.domain.routingProperty.key
// 
// Created: Aug 9, 2006 8:12:31 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public interface MutableBasePropertyKey extends BasePropertyKey
{
    /**
     * Allows the Routing Property to determine the order of its PropertyDescriptors.
     * @return comparator to use for sorting the returned PropertyDescriptors from getPropertyDescriptors().
     */
    Comparator getPropertyDescriptorSortComparator();

    /**
     * Gets the PropertyDescriptor's for this BaseProperty.
     * @return The PropertyDescriptor's that are returned from the BeanInfo
     * @throws java.beans.IntrospectionException that could be returned from the Introspector
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

    /**
     * Sets the actual value for the propertyDescriptor to this Object
     * @param propertyDescriptor for field in this Object
     * @param value Object value to set with
     */
    void setFieldValue(PropertyDescriptor propertyDescriptor, Object value)
           throws IllegalAccessException, InvocationTargetException;

    Object getFieldDefaultValue(PropertyDescriptor propertyDescriptor);

    void setFieldDefaultValue(PropertyDescriptor propertyDescriptor, Object value);

    boolean hasFieldDefaultValue(PropertyDescriptor propertyDescriptor);

    boolean isFieldDefaultValue(PropertyDescriptor propertyDescriptor);
}
