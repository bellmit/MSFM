package com.cboe.domain.routingProperty.properties;

import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;

import com.cboe.domain.routingProperty.AbstractRoutingPropertyGroupBeanInfo;
import com.cboe.domain.routingProperty.key.SessionFirmCorrBranchKey;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
// -----------------------------------------------------------------------------------
// Source file: FirmTradingParameterGroupBeanInfo
//
// PACKAGE: com.cboe.domain.routingProperty.properties
// 
// Created: Aug 23, 2006 10:36:03 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------


public class FirmTradingParameterGroupBeanInfo extends AbstractRoutingPropertyGroupBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    /**
     * Hides all PropertyDesciptor's for the represented class
     * @return an empty array
     */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if (allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[2];
            try
            {
                allowedDescriptors[0] = new PropertyDescriptor(SessionFirmCorrBranchKey.BRANCH_PROPERTY_NAME,
                                                               SessionFirmCorrBranchKey.class);
                allowedDescriptors[1] = new PropertyDescriptor(SessionFirmCorrBranchKey.CORRESPONDENT_PROPERTY_NAME,
                                                               SessionFirmCorrBranchKey.class);
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
