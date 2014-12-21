//
// -----------------------------------------------------------------------------------
// Source file: FirmCorrBranchOMTDestinationGroupBeanInfo.java
//
// PACKAGE: com.cboe.domain.routingProperty.properties
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.routingProperty.properties;

import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;

import com.cboe.domain.routingProperty.AbstractRoutingPropertyGroupBeanInfo;
import com.cboe.domain.routingProperty.key.SessionFirmCorrBranchKey;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;


public class FirmCorrBranchOMTDestinationGroupBeanInfo extends AbstractRoutingPropertyGroupBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if(allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[2];
            try
            {
                allowedDescriptors[0] = new PropertyDescriptor(SessionFirmCorrBranchKey.BRANCH_PROPERTY_NAME,
                                                               SessionFirmCorrBranchKey.class);
                allowedDescriptors[1] = new PropertyDescriptor(SessionFirmCorrBranchKey.CORRESPONDENT_PROPERTY_NAME,
                                                               SessionFirmCorrBranchKey.class);
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
}
