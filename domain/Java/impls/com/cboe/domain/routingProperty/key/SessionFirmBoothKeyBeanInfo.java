package com.cboe.domain.routingProperty.key;

import java.beans.SimpleBeanInfo;
import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
// -----------------------------------------------------------------------------------
// Source file: SessionFirmBoothKeyBeanInfo
//
// PACKAGE: com.cboe.domain.routingProperty.key
// 
// Created: Aug 24, 2006 3:06:41 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------


/**
 * Exposes a limited set of BeanInfo for a SimpleBooleanTradingPropertyImpl
 */
public class SessionFirmBoothKeyBeanInfo extends SimpleBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    /**
     * Returns the property descriptor for the booleanValue method only
     */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if (allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[3];
            try
            {
                allowedDescriptors[0] =
                        new PropertyDescriptor(SessionFirmBoothKey.TRADING_SESSION_PROPERTY_NAME, SessionFirmBoothKey.class);

                allowedDescriptors[1] =
                        new PropertyDescriptor(SessionFirmBoothKey.FIRM_PROPERTY_NAME, SessionFirmBoothKey.class);
                
                allowedDescriptors[2] =
                        new PropertyDescriptor(SessionFirmBoothKey.BOOTH_WORKSTATION_PROPERTY_NAME, SessionFirmBoothKey.class);
                
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
