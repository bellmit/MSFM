package com.cboe.domain.routingProperty.key;

import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.SimpleBeanInfo;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

// -----------------------------------------------------------------------------------
// Source file: PropertyKeyElementDefinitionBeanInfo
//
// PACKAGE: com.cboe.domain.routingProperty.key
// 
// Created: Jan 29, 2008
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2008 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------


public class PropertyKeyElementDefinitionBeanInfo extends SimpleBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    @Override
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if (allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[7];
            try
            {
                allowedDescriptors[0] = new PropertyDescriptor(PropertyKeyElementDefinition.TRADING_SESSION_PROPERTY_NAME,
                                                               PropertyKeyElementDefinition.class);

                allowedDescriptors[1] = new PropertyDescriptor(PropertyKeyElementDefinition.FIRM_PROPERTY_NAME,
                                                               PropertyKeyElementDefinition.class);

                allowedDescriptors[2] = new PropertyDescriptor(PropertyKeyElementDefinition.POST_PROPERTY_NAME,
                                                               PropertyKeyElementDefinition.class);

                allowedDescriptors[3] = new PropertyDescriptor(PropertyKeyElementDefinition.STATION_PROPERTY_NAME,
                                                               PropertyKeyElementDefinition.class);

                allowedDescriptors[4] = new PropertyDescriptor(PropertyKeyElementDefinition.BRANCH_PROPERTY_NAME,
                                                               PropertyKeyElementDefinition.class);

                allowedDescriptors[5] = new PropertyDescriptor(PropertyKeyElementDefinition.PRODUCT_CLASS_PROPERTY_NAME,
                                                               PropertyKeyElementDefinition.class);

                allowedDescriptors[6] = new PropertyDescriptor(PropertyKeyElementDefinition.LEVEL_PROPERTY_NAME,
                                                               PropertyKeyElementDefinition.class);
            }
            catch (IntrospectionException e)
            {
                Log.exception("Could not create PropertyDescriptor.", e);
                allowedDescriptors = null;
                return super.getPropertyDescriptors();
            }
        }
        return allowedDescriptors;
    }

    @Override
    public int getDefaultPropertyIndex()
    {
        return 0;
    }
}
