//
// -----------------------------------------------------------------------------------
// Source file: PDPMRightsScalesImplBeanInfo.java
//
// PACKAGE: com.cboe.domain.tradingProperty;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.tradingProperty;

import java.beans.SimpleBeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * Exposes a limited set of BeanInfo for a PDPMRightsScalesImpl
 */
public class PDPMRightsScalesImplBeanInfo extends SimpleBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    /**
     * Returns the property descriptors for lowNbrParticipants, highNbrParticipants, and scalePercentage methods only
     */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if(allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[3];
            try
            {
                allowedDescriptors[0] =
                    new PropertyDescriptor("lowNbrParticipants", PDPMRightsScalesImpl.class);
                allowedDescriptors[1] =
                    new PropertyDescriptor("highNbrParticipants", PDPMRightsScalesImpl.class);
                allowedDescriptors[2] =
                    new PropertyDescriptor("scalePercentage", PDPMRightsScalesImpl.class);
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
     * Returns the default property index for the lowNbrParticipants.
     */
    public int getDefaultPropertyIndex()
    {
        return 0;
    }
}
