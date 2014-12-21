package com.cboe.domain.routingProperty.properties;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import com.cboe.domain.routingProperty.AbstractRoutingPropertyGroupBeanInfo;
import com.cboe.domain.routingProperty.key.SessionFirmClassKey;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class EnableNewBOBGroupBeanInfo extends AbstractRoutingPropertyGroupBeanInfo {
    
    private PropertyDescriptor[] allowedDescriptors;
    
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if (allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[1];
            try
            {
            	  allowedDescriptors[0] =
                      new PropertyDescriptor(SessionFirmClassKey.PRODUCT_CLASS_PROPERTY_NAME, SessionFirmClassKey.class);

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
