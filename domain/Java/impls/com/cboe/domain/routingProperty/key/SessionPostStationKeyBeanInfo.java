package com.cboe.domain.routingProperty.key;

//-----------------------------------------------------------------------------------
//Source file: SessionKeyBeanInfo
//
//PACKAGE: com.cboe.domain.routingProperty.key
//
//Created: Aug 8, 2006 7:00:26 AM
//-----------------------------------------------------------------------------------
//Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
//-----------------------------------------------------------------------------------

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * Exposes a limited set of BeanInfo for a SimpleBooleanTradingPropertyImpl
 */
public class SessionPostStationKeyBeanInfo extends SimpleBeanInfo
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
                allowedDescriptors[0] = new PropertyDescriptor(
                        SessionPostStationKey.TRADING_SESSION_PROPERTY_NAME,
                        SessionPostStationKey.class);

                allowedDescriptors[1] = new PropertyDescriptor(
                        SessionPostStationKey.POST_PROPERTY_NAME, SessionPostStationKey.class);

                allowedDescriptors[2] = new PropertyDescriptor(
                        SessionPostStationKey.STATION_PROPERTY_NAME, SessionPostStationKey.class);
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
}
