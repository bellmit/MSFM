package com.cboe.domain.routingProperty.properties;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import com.cboe.domain.routingProperty.AbstractRoutingPropertyGroupBeanInfo;
import com.cboe.domain.routingProperty.key.SessionFirmClassOriginKey;
import com.cboe.domain.routingProperty.key.SessionOriginKey;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;


public class EligibleCOAGroupBeanInfo extends AbstractRoutingPropertyGroupBeanInfo
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
                allowedDescriptors[0] =
                        new PropertyDescriptor(SessionFirmClassOriginKey.PRODUCT_CLASS_PROPERTY_NAME, SessionFirmClassOriginKey.class);

                allowedDescriptors[1] =
                        new PropertyDescriptor(SessionFirmClassOriginKey.ORIGIN_CODE_PROPERTY_NAME, SessionFirmClassOriginKey.class);
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
}
