package com.cboe.domain.routingProperty.properties;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import com.cboe.domain.routingProperty.AbstractRoutingPropertyGroupBeanInfo;
import com.cboe.domain.routingProperty.key.SessionFirmClassKey;
import com.cboe.domain.routingProperty.key.SessionFirmCorrClassKey;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class FirmCorrClassPARComplexGroupBeanInfo extends AbstractRoutingPropertyGroupBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if(allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[2];
            try
            {
                allowedDescriptors[0] = new PropertyDescriptor(SessionFirmCorrClassKey.CORRESPONDENT_PROPERTY_NAME,
                                                               SessionFirmCorrClassKey.class);
                allowedDescriptors[1] =
                    new PropertyDescriptor(SessionFirmCorrClassKey.PRODUCT_CLASS_PROPERTY_NAME, SessionFirmCorrClassKey.class);

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
