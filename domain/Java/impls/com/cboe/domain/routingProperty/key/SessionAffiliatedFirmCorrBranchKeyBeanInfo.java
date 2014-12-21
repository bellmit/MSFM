//
// -----------------------------------------------------------------------------------
// Source file: SessionAffiliatedFirmCorrBranchKeyBeanInfo.java
//
// PACKAGE: com.cboe.domain.routingProperty.key
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.routingProperty.key;

import java.beans.SimpleBeanInfo;
import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class SessionAffiliatedFirmCorrBranchKeyBeanInfo extends SimpleBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if(allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[4];
            try
            {
                allowedDescriptors[0] = new PropertyDescriptor(SessionAffiliatedFirmCorrBranchKey.TRADING_SESSION_PROPERTY_NAME,
                                                               SessionAffiliatedFirmCorrBranchKey.class);

                allowedDescriptors[1] = new PropertyDescriptor(SessionAffiliatedFirmCorrBranchKey.AFFILIATED_FIRM_PROPERTY_NAME,
                                                               SessionAffiliatedFirmCorrBranchKey.class);
                
                allowedDescriptors[2] = new PropertyDescriptor(SessionAffiliatedFirmCorrBranchKey.CORRESPONDENT_PROPERTY_NAME,
                                                               SessionAffiliatedFirmCorrBranchKey.class);

                allowedDescriptors[3] = new PropertyDescriptor(SessionAffiliatedFirmCorrBranchKey.BRANCH_PROPERTY_NAME,
                                                               SessionAffiliatedFirmCorrBranchKey.class);
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
     * Returns the default property index for the booleanValue.
     */
    public int getDefaultPropertyIndex()
    {
        return 0;
    }
}
