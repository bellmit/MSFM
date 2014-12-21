//
// -----------------------------------------------------------------------------------
// Source file: DirectedAIMFirmRegistrationDateParameterBeanInfo.java
//
// PACKAGE: com.cboe.domain.routingProperty.properties
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.routingProperty.properties;

import java.beans.SimpleBeanInfo;
import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;

import com.cboe.domain.routingProperty.common.LongBasePropertyImpl;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class DirectedAIMFirmRegistrationDateParameterBeanInfo extends SimpleBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    /**
     * Returns the property descriptor for the longValue method only
     */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if(allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[1];
            try
            {
                allowedDescriptors[0] =
                        new PropertyDescriptor("dateTimeContainer", DirectedAIMFirmRegistrationDateParameter.class);
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

    /**
     * Returns the default property index for the doubleValue.
     */
    public int getDefaultPropertyIndex()
    {
        return 0;
    }
}

