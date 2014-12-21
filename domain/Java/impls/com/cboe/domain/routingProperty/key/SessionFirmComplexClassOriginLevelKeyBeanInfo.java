package com.cboe.domain.routingProperty.key;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;


public class SessionFirmComplexClassOriginLevelKeyBeanInfo extends SimpleBeanInfo
{

    private PropertyDescriptor[] allowedDescriptors;

    /**
     * Returns the property descriptor for the booleanValue method only
     */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if(allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[4];
            try
            {
                allowedDescriptors[0] = new PropertyDescriptor(SessionFirmComplexClassOriginLevelKey.FIRM_PROPERTY_NAME,
                                                               SessionFirmComplexClassOriginLevelKey.class);

                allowedDescriptors[1] = new PropertyDescriptor(SessionFirmComplexClassOriginLevelKey.COMPLEX_PRODUCT_CLASS_PROPERTY_NAME,
                                                               SessionFirmComplexClassOriginLevelKey.class);

                allowedDescriptors[2] = new PropertyDescriptor(SessionFirmComplexClassOriginLevelKey.ORIGIN_CODE_PROPERTY_NAME,
                                                               SessionFirmComplexClassOriginLevelKey.class);

                allowedDescriptors[3] = new PropertyDescriptor(SessionFirmComplexClassOriginLevelKey.LEVEL_PROPERTY_NAME,
                                                               SessionFirmComplexClassOriginLevelKey.class);
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
