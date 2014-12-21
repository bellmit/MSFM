package com.cboe.domain.routingProperty.common;
// -----------------------------------------------------------------------------------
// Source file: StringBasePropertyImplBeanInfo
//
// PACKAGE: com.cboe.domain.routingProperty.common
// 
// Created: Aug 1, 2006 4:34:28 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;


/**
 * Exposes a limited set of BeanInfo for a SimpleDoubleTradingPropertyImpl
 */
public class StringBasePropertyImplBeanInfo extends SimpleBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    /**
     * Returns the property descriptor for the doubleValue method only
     */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if (allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[1];
            try
            {
                allowedDescriptors[0] =
                        new PropertyDescriptor("stringValue", StringBasePropertyImpl.class);
            }
            catch (IntrospectionException e)
            {
                Log.exception("Could not create PropertyDescriptor.", e);
                allowedDescriptors = null;
                return super.getPropertyDescriptors();
            }
        }
        return allowedDescriptors;
    }

    /**
     * Returns the default property index for the doubleValue.
     */
    public int getDefaultPropertyIndex()
    {
        return 0;
    }
}
