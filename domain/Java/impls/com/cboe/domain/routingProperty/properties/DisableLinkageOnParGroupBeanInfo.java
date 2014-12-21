package com.cboe.domain.routingProperty.properties;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import com.cboe.domain.routingProperty.AbstractRoutingPropertyGroupBeanInfo;
import com.cboe.domain.routingProperty.key.SessionClassKey;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class DisableLinkageOnParGroupBeanInfo extends AbstractRoutingPropertyGroupBeanInfo
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
            allowedDescriptors = new PropertyDescriptor[1];
            try
            {
                allowedDescriptors[0] =
                        new PropertyDescriptor(SessionClassKey.PRODUCT_CLASS_PROPERTY_NAME, SessionClassKey.class);
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