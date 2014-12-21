package com.cboe.domain.routingProperty.properties;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import com.cboe.domain.routingProperty.AbstractRoutingPropertyGroupBeanInfo;
import com.cboe.domain.routingProperty.key.SessionFirmClassCorrBranchKey;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;


public class PDPMAssignmentGroupBeanInfo extends AbstractRoutingPropertyGroupBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    /**
     * Hides all PropertyDesciptor's for the represented class
     * @return an empty array
     */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if(allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[3];
            try
            {
                allowedDescriptors[0] =
                        new PropertyDescriptor(SessionFirmClassCorrBranchKey.PRODUCT_CLASS_PROPERTY_NAME,
                                               SessionFirmClassCorrBranchKey.class);
                allowedDescriptors[1] =
                        new PropertyDescriptor(SessionFirmClassCorrBranchKey.CORRESPONDENT_PROPERTY_NAME,
                                               SessionFirmClassCorrBranchKey.class);
                allowedDescriptors[2] = new PropertyDescriptor(SessionFirmClassCorrBranchKey.BRANCH_PROPERTY_NAME,
                                                               SessionFirmClassCorrBranchKey.class);
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
