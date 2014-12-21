package com.cboe.domain.routingProperty.key;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;


/**
 * Exposes a limited set of BeanInfo for a SessionFirmClassOriginCorrBranchKey
 */
public class SessionFirmClassOriginCorrBranchKeyBeanInfo extends SimpleBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    /**
     * Returns the property descriptor for the booleanValue method only
     */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if (allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[5];
            try
            {
                allowedDescriptors[0] = new PropertyDescriptor(SessionFirmClassOriginCorrBranchKey.FIRM_PROPERTY_NAME, SessionFirmClassOriginCorrBranchKey.class);

                allowedDescriptors[1] = new PropertyDescriptor(SessionFirmClassOriginCorrBranchKey.PRODUCT_CLASS_PROPERTY_NAME, SessionFirmClassOriginCorrBranchKey.class);

                allowedDescriptors[2] = new PropertyDescriptor(SessionFirmClassOriginCorrBranchKey.ORIGIN_CODE_PROPERTY_NAME, SessionFirmClassOriginCorrBranchKey.class);

                allowedDescriptors[3] = new PropertyDescriptor(SessionFirmClassOriginCorrBranchKey.CORRESPONDENT_PROPERTY_NAME, SessionFirmClassOriginCorrBranchKey.class);

                allowedDescriptors[4] = new PropertyDescriptor(SessionFirmClassOriginCorrBranchKey.BRANCH_PROPERTY_NAME, SessionFirmClassOriginCorrBranchKey.class);
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
