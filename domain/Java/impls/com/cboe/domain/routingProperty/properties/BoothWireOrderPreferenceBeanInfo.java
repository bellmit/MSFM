package com.cboe.domain.routingProperty.properties;

import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.SimpleBeanInfo;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

// -----------------------------------------------------------------------------------
// Source file: BoothWireOrderPreferenceBeanInfo
//
// PACKAGE: com.cboe.domain.routingProperty.properties
// 
// Created: Feb 8, 2008
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2008 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------


public class BoothWireOrderPreferenceBeanInfo extends SimpleBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;


    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if (allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[1];
            try
            {
                allowedDescriptors[0] = new PropertyDescriptor("orderPreference", BoothWireOrderPreference.class);
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

    public int getDefaultPropertyIndex()
    {
        return 0;
    }
}
