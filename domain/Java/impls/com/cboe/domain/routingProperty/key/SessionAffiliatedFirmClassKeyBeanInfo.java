package com.cboe.domain.routingProperty.key;
// -----------------------------------------------------------------------------------
// Source file: SessionAffiliatedFirmClassKeyBeanInfo
//
// PACKAGE: com.cboe.domain.routingProperty.key
// 
// Created: Feb 23, 2009
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * Exposes a limited set of BeanInfo for a SimpleBooleanTradingPropertyImpl
 */
public class SessionAffiliatedFirmClassKeyBeanInfo extends SimpleBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    /**
     * Returns the property descriptor for the booleanValue method only
     */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if (allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[2];
            try
            {
                allowedDescriptors[0] =
                        new PropertyDescriptor(SessionAffiliatedFirmClassKey.AFFILIATED_FIRM_PROPERTY_NAME, SessionAffiliatedFirmClassKey.class);

                allowedDescriptors[1] =
                        new PropertyDescriptor(SessionAffiliatedFirmClassKey.PRODUCT_CLASS_PROPERTY_NAME, SessionAffiliatedFirmClassKey.class);
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
     * Returns the default property index for the booleanValue.
     */
    public int getDefaultPropertyIndex()
    {
        return 0;
    }
}
