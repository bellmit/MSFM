//
// -----------------------------------------------------------------------------------
// Source file: SessionFirmSimpleClassOriginLevelKeyBeanInfo.java
//
// PACKAGE: com.cboe.domain.routingProperty.key
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.routingProperty.key;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class SessionFirmSimpleClassOriginLevelKeyBeanInfo extends SimpleBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;
    /**
     * Returns the property descriptor for the booleanValue method only
     */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if(allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[4];
            try
            {
                allowedDescriptors[0] = new PropertyDescriptor(SessionFirmSimpleClassOriginLevelKey.FIRM_PROPERTY_NAME,
                                                               SessionFirmSimpleClassOriginLevelKey.class);

                allowedDescriptors[1] = new PropertyDescriptor(SessionFirmSimpleClassOriginLevelKey.SIMPLE_PRODUCT_CLASS_PROPERTY_NAME,
                                                               SessionFirmSimpleClassOriginLevelKey.class);

                allowedDescriptors[2] = new PropertyDescriptor(SessionFirmSimpleClassOriginLevelKey.ORIGIN_CODE_PROPERTY_NAME,
                                                               SessionFirmSimpleClassOriginLevelKey.class);

                allowedDescriptors[3] = new PropertyDescriptor(SessionFirmSimpleClassOriginLevelKey.LEVEL_PROPERTY_NAME,
                                                               SessionFirmSimpleClassOriginLevelKey.class);
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
