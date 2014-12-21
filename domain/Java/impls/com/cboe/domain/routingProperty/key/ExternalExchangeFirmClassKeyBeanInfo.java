package com.cboe.domain.routingProperty.key;
// -----------------------------------------------------------------------------------
// Source file: SessionFirmClassKeyBeanInfo
//
// PACKAGE: com.cboe.domain.routingProperty.key
// 
// Created: Aug 8, 2006 7:04:41 AM
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
public class ExternalExchangeFirmClassKeyBeanInfo extends SimpleBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    /**
     * Returns the property descriptor for the booleanValue method only
     */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if (allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[4];
            try
            {
                allowedDescriptors[0] =
                        new PropertyDescriptor(ExternalExchangeFirmClassKey.TRADING_SESSION_PROPERTY_NAME, ExternalExchangeFirmClassKey.class);

                allowedDescriptors[1] =
                        new PropertyDescriptor(ExternalExchangeFirmClassKey.EXTERNAL_EXCHANGE_PROPERTY_NAME, ExternalExchangeFirmClassKey.class);

                allowedDescriptors[2] =
                        new PropertyDescriptor(ExternalExchangeFirmClassKey.FIRM_PROPERTY_NAME, ExternalExchangeFirmClassKey.class);

                allowedDescriptors[3] =
                        new PropertyDescriptor(ExternalExchangeFirmClassKey.PRODUCT_CLASS_PROPERTY_NAME, ExternalExchangeFirmClassKey.class);
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