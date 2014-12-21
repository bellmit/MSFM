//
// -----------------------------------------------------------------------------------
// Source file: InternalizationPercentageTradingPropertyBeanInfo.java
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

public class InternalizationPercentageTradingPropertyBeanInfo extends SimpleBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if(allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[4];
            try
            {
                allowedDescriptors[0] = new PropertyDescriptor("auctionType",
                                                               InternalizationPercentageTradingProperty.class);
                allowedDescriptors[1] = new PropertyDescriptor("lowerRange",
                                                               InternalizationPercentageTradingProperty.class);
                allowedDescriptors[2] = new PropertyDescriptor("upperRange",
                                                               InternalizationPercentageTradingProperty.class);
                allowedDescriptors[3] = new PropertyDescriptor("percentage",
                                                               InternalizationPercentageTradingProperty.class);
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
