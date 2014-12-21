//
// -----------------------------------------------------------------------------------
// Source file: TestTradingPropertyBeanInfo.java
//
// PACKAGE: com.cboe.internalPresentation.common.formatters
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.common.formatters;

import java.beans.SimpleBeanInfo;
import java.beans.PropertyDescriptor;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.IntrospectionException;

public class TestTradingPropertyBeanInfo extends SimpleBeanInfo
{
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        try
        {
            BeanInfo beanInfo = Introspector.getBeanInfo(TestTradingProperty.class,
                                                         Introspector.IGNORE_IMMEDIATE_BEANINFO);
            PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
            PropertyDescriptor[] alteredDescriptors = new PropertyDescriptor[descriptors.length - 1];
            int j = 0;
            for(int i = 0; i < descriptors.length; i++)
            {
                PropertyDescriptor descriptor = descriptors[i];
                if(!descriptor.getName().equals("tradingPropertyType"))
                {
                    alteredDescriptors[j++] = descriptor;
                }
            }
            return alteredDescriptors;
        }
        catch(IntrospectionException e)
        {
            return super.getPropertyDescriptors();
        }
    }
}
