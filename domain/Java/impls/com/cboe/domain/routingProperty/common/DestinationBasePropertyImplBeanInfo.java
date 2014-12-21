//
// -----------------------------------------------------------------------------------
// Source file: DestinationBasePropertyImplBeanInfo.java
//
// PACKAGE: com.cboe.domain.routingProperty.common
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.routingProperty.common;

import java.beans.SimpleBeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class DestinationBasePropertyImplBeanInfo extends SimpleBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if(allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[1];
            try
            {
                allowedDescriptors[0] = new PropertyDescriptor("destination", DestinationBasePropertyImpl.class);
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

    public int getDefaultPropertyIndex()
    {
        return 0;
    }
}
