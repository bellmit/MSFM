package com.cboe.domain.routingProperty.key;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;


/**
 * Exposes a limited set of BeanInfo for a SessionFirmClassOriginLevelKey
 */
public class SessionFirmClassOriginLevelKeyBeanInfo extends SimpleBeanInfo
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
                allowedDescriptors[0] = new PropertyDescriptor(SessionFirmClassOriginLevelKey.FIRM_PROPERTY_NAME,
                        SessionFirmClassOriginLevelKey.class);

                allowedDescriptors[1] = new PropertyDescriptor(SessionFirmClassOriginLevelKey.PRODUCT_CLASS_PROPERTY_NAME,
                        SessionFirmClassOriginLevelKey.class);

                allowedDescriptors[2] = new PropertyDescriptor(SessionFirmClassOriginLevelKey.ORIGIN_CODE_PROPERTY_NAME,
                        SessionFirmClassOriginLevelKey.class);

                allowedDescriptors[3] = new PropertyDescriptor(SessionFirmClassOriginLevelKey.LEVEL_PROPERTY_NAME,
                        SessionFirmClassOriginLevelKey.class);
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
