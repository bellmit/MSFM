//
// -----------------------------------------------------------------------------------
// Source file: SessionFirmPostStationCorrBranchKeyBeanInfo.java
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

public class SessionFirmPostStationCorrBranchKeyBeanInfo extends SimpleBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if(allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[6];
            try
            {
                allowedDescriptors[0] = new PropertyDescriptor(SessionFirmPostStationCorrBranchKey.TRADING_SESSION_PROPERTY_NAME,
                                                               SessionFirmPostStationCorrBranchKey.class);

                allowedDescriptors[1] = new PropertyDescriptor(SessionFirmPostStationCorrBranchKey.FIRM_PROPERTY_NAME,
                                                               SessionFirmPostStationCorrBranchKey.class);

                allowedDescriptors[2] = new PropertyDescriptor(SessionFirmPostStationCorrBranchKey.POST_PROPERTY_NAME,
                                                               SessionFirmPostStationCorrBranchKey.class);

                allowedDescriptors[3] = new PropertyDescriptor(SessionFirmPostStationCorrBranchKey.STATION_PROPERTY_NAME,
                                                               SessionFirmPostStationCorrBranchKey.class);

                allowedDescriptors[4] = new PropertyDescriptor(SessionFirmPostStationCorrBranchKey.CORRESPONDENT_PROPERTY_NAME,
                                                               SessionFirmPostStationCorrBranchKey.class);

                allowedDescriptors[5] = new PropertyDescriptor(SessionFirmPostStationCorrBranchKey.BRANCH_PROPERTY_NAME,
                                                               SessionFirmPostStationCorrBranchKey.class);
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
