//
// -----------------------------------------------------------------------------------
// Source file: DpmRightsScaleTradingPropertyBeanInfo.java
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

public class DpmRightsScaleTradingPropertyBeanInfo extends SimpleBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if(allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[3];
            try
            {
                allowedDescriptors[0] = new PropertyDescriptor("lowNbrParticipants",
                                                               DpmRightsScaleTradingProperty.class);
                allowedDescriptors[1] = new PropertyDescriptor("highNbrParticipants",
                                                               DpmRightsScaleTradingProperty.class);
                allowedDescriptors[2] = new PropertyDescriptor("scalePercentage",
                                                               DpmRightsScaleTradingProperty.class);
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
