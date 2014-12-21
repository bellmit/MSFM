//
// -----------------------------------------------------------------------------------
// Source file: SessionWorkstationNameKeyBeanInfo.java
//
// PACKAGE: com.cboe.domain.routingProperty.key
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.routingProperty.key;

import java.beans.SimpleBeanInfo;
import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class SessionWorkstationNameKeyBeanInfo extends SimpleBeanInfo
{
    PropertyDescriptor[] allowedDescriptors;

    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if(allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[2];
            try
            {
                allowedDescriptors[0] = new PropertyDescriptor(SessionWorkstationNameKey.TRADING_SESSION_PROPERTY_NAME, SessionWorkstationNameKey.class);
                allowedDescriptors[1] = new PropertyDescriptor(SessionWorkstationNameKey.WORKSTATION_NAME_PROPERTY_NAME, SessionWorkstationNameKey.class);
            }
            catch(IntrospectionException e)
            {
                Log.exception("Could not create PropertyDescriptor.", e);
                allowedDescriptors = null;
                return super.getPropertyDescriptors();
            }
        }
        return allowedDescriptors;
    }
}
