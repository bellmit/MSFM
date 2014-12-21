//
// -----------------------------------------------------------------------------------
// Source file: SessionFirmSimpleClassKeyBeanInfo.java
//
// PACKAGE: com.cboe.domain.routingProperty.key
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.routingProperty.key;

import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.SimpleBeanInfo;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class SessionFirmComplexClassKeyBeanInfo extends SimpleBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    /**
     * Returns the property descriptor for the booleanValue method only
     */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if(allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[2];
            try
            {
                allowedDescriptors[0] = new PropertyDescriptor(SessionFirmComplexClassKey.FIRM_PROPERTY_NAME,
                                                               SessionFirmComplexClassKey.class);

                allowedDescriptors[1] = new PropertyDescriptor(SessionFirmComplexClassKey.COMPLEX_PRODUCT_CLASS_PROPERTY_NAME,
                                                               SessionFirmComplexClassKey.class);
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
     * Returns the default property index for the booleanValue.
     */
    public int getDefaultPropertyIndex()
    {
        return 0;
    }
}
