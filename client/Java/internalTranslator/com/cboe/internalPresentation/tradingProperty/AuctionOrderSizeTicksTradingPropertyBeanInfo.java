//
// -----------------------------------------------------------------------------------
// Source file: AuctionOrderSizeTicksTradingPropertyBeanInfo.java
//
// PACKAGE: com.cboe.internalPresentation.tradingProperty;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.tradingProperty;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import com.cboe.presentation.common.logging.GUILoggerHome;

public class AuctionOrderSizeTicksTradingPropertyBeanInfo extends SimpleBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if(allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[3];
            try
            {
                allowedDescriptors[0] = new PropertyDescriptor("auctionType",
                                                               AuctionOrderSizeTicksTradingProperty.class);
                allowedDescriptors[1] = new PropertyDescriptor("orderSize",
                                                               AuctionOrderSizeTicksTradingProperty.class);
                allowedDescriptors[2] = new PropertyDescriptor("ticksAboveNBBO",
                                                               AuctionOrderSizeTicksTradingProperty.class);
            }
            catch(IntrospectionException e)
            {
                GUILoggerHome.find().exception(getClass().getName(), "Could not create PropertyDescriptor.", e);
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
