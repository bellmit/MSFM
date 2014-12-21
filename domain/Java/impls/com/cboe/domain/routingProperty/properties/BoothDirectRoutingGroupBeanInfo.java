//
// -----------------------------------------------------------------------------------
// Source file: BartDirectRoutingGroupBeanInfo.java
//
// PACKAGE: com.cboe.domain.routingProperty.properties
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.routingProperty.properties;

import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.SimpleBeanInfo;

import com.cboe.domain.routingProperty.key.SessionFirmBoothKey;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class BoothDirectRoutingGroupBeanInfo extends SimpleBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if(allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[1];
            try
            {
                allowedDescriptors[0] = new PropertyDescriptor(SessionFirmBoothKey.BOOTH_WORKSTATION_PROPERTY_NAME,
                                                               SessionFirmBoothKey.class);
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
