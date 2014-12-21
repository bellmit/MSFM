//
// -----------------------------------------------------------------------------------
// Source file: SimpleIntegerTradingPropertyImplBeanInfo.java
//
// PACKAGE: com.cboe.domain.tradingProperty.common
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.tradingProperty.common;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * Exposes a limited set of BeanInfo for a SimpleIntegerTradingPropertyImpl
 */
public class SimpleIntegerTradingPropertyImplBeanInfo extends SimpleBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    /**
     * Returns the property descriptor for the integerValue method only
     */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if(allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[1];
            try
            {
                allowedDescriptors[0] =
                    new PropertyDescriptor("integerValue", SimpleIntegerTradingPropertyImpl.class);
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
     * Returns the default property index for the integerValue.
     */
    public int getDefaultPropertyIndex()
    {
        return 0;
    }
}
