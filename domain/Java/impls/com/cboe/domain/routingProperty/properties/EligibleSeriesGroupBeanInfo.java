//
// -----------------------------------------------------------------------------------
// Source file: EligibleSeriesGroupBeanInfo.java
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

import com.cboe.domain.routingProperty.key.SessionClassProductKey;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

@Deprecated
public class EligibleSeriesGroupBeanInfo extends SimpleBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    /**
     * Hides all PropertyDesciptor's for the represented class
     * @return an empty array
     */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if(allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[1];
            try
            {
                allowedDescriptors[0] =
                        new PropertyDescriptor(SessionClassProductKey.PRODUCT_PROPERTY_NAME,
                                               SessionClassProductKey.class);
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
