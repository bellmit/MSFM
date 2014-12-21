package com.cboe.domain.routingProperty.key;

import java.beans.SimpleBeanInfo;
import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
// -----------------------------------------------------------------------------------
// Source file: SessionFirmCorrParKeyBeanInfo
//
// PACKAGE: com.cboe.domain.routingProperty.key
// 
// Created: Aug 25, 2006 10:55:46 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------


/**
 * Exposes a limited set of BeanInfo for a SimpleBooleanTradingPropertyImpl
 */
public class SessionFirmCorrParKeyBeanInfo extends SimpleBeanInfo
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
                        new PropertyDescriptor(SessionFirmCorrParKey.TRADING_SESSION_PROPERTY_NAME, SessionFirmCorrParKey.class);

                allowedDescriptors[1] =
                        new PropertyDescriptor(SessionFirmCorrParKey.FIRM_PROPERTY_NAME, SessionFirmCorrParKey.class);
                
                allowedDescriptors[2] =
                        new PropertyDescriptor(SessionFirmCorrParKey.CORRESPONDENT_PROPERTY_NAME, SessionFirmCorrParKey.class);

                allowedDescriptors[3] =
                        new PropertyDescriptor(SessionFirmCorrParKey.PAR_WORKSTATION_PROPERTY_NAME, SessionFirmCorrParKey.class);

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
