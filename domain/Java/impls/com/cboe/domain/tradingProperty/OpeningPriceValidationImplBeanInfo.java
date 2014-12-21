//
// -----------------------------------------------------------------------------------
// Source file: OpeningPriceValidationImplBeanInfo.java
//
// PACKAGE: com.cboe.domain.tradingProperty;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.tradingProperty;

import java.beans.SimpleBeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * Exposes a limited set of BeanInfo for a OpeningPriceValidationImpl
 */
public class OpeningPriceValidationImplBeanInfo extends SimpleBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;
    
    /**
     * Returns the property descriptors for validationType only
     */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if(allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[1];
            try
            {
                allowedDescriptors[0] =
                    new PropertyDescriptor("validationType", OpeningPriceValidationImpl.class);
                
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
     * Returns the default property index for the validationType.
     */
    public int getDefaultPropertyIndex()
    {
        return 0;
    }
}
