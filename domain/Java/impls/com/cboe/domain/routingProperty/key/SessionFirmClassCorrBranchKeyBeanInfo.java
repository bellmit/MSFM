package com.cboe.domain.routingProperty.key;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;


/**
 * Exposes a limited set of BeanInfo for a SessionFirmClassOriginCorrBranchKey
 */
public class SessionFirmClassCorrBranchKeyBeanInfo extends SimpleBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    /**
     * Returns the property descriptor for the booleanValue method only
     */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if (allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[4];
            try
            {
                allowedDescriptors[0] = new PropertyDescriptor(SessionFirmClassCorrBranchKey.FIRM_PROPERTY_NAME,
                                                               SessionFirmClassCorrBranchKey.class);

                allowedDescriptors[1] = new PropertyDescriptor(SessionFirmClassCorrBranchKey.PRODUCT_CLASS_PROPERTY_NAME,
                                                               SessionFirmClassCorrBranchKey.class);

                allowedDescriptors[2] = new PropertyDescriptor(SessionFirmClassCorrBranchKey.CORRESPONDENT_PROPERTY_NAME,
                                                               SessionFirmClassCorrBranchKey.class);

                allowedDescriptors[3] = new PropertyDescriptor(SessionFirmClassCorrBranchKey.BRANCH_PROPERTY_NAME,
                                                               SessionFirmClassCorrBranchKey.class);
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

    /**
     * Returns the default property index for the booleanValue.
     */
    public int getDefaultPropertyIndex()
    {
        return 0;
    }
}
