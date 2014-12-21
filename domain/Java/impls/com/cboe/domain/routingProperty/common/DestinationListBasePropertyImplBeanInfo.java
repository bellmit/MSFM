//
// -----------------------------------------------------------------------------------
// Source file: DestinationListBasePropertyImplBeanInfo.java
//
// PACKAGE: com.cboe.domain.routingProperty.common
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.routingProperty.common;

import java.beans.SimpleBeanInfo;
import java.beans.PropertyDescriptor;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class DestinationListBasePropertyImplBeanInfo extends SimpleBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    /**
     * Returns the property descriptor for the destinationListValue method only
     */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if(allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[1];
            try
            {
                allowedDescriptors[0] =
                        new IndexedPropertyDescriptor("destinationListValue", DestinationListBasePropertyImpl.class);
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
     * Returns the default property index for the destinationListValue.
     */
    public int getDefaultPropertyIndex()
    {
        return 0;
    }
}
