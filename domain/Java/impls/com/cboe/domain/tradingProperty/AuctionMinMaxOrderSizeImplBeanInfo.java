//
// -----------------------------------------------------------------------------------
// Source file: AuctionMinMaxOrderSizeImplBeanInfo.java
//
// PACKAGE: com.cboe.domain.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.tradingProperty;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * Exposes a limited set of BeanInfo for a AuctionMinMaxOrderSizeImpl
 */
public class AuctionMinMaxOrderSizeImplBeanInfo extends SimpleBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    /**
     * Returns the property descriptors for auctionType, minOrderSize, and maxOrderSize methods only
     */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if(allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[3];
            try
            {
                allowedDescriptors[0] =
                    new PropertyDescriptor("auctionType", AuctionMinMaxOrderSizeImpl.class);
                allowedDescriptors[1] =
                    new PropertyDescriptor("minOrderSize", AuctionMinMaxOrderSizeImpl.class);
                allowedDescriptors[2] =
                    new PropertyDescriptor("maxOrderSize", AuctionMinMaxOrderSizeImpl.class);
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
     * Returns the default property index for the auctionType.
     */
    public int getDefaultPropertyIndex()
    {
        return 0;
    }
}
