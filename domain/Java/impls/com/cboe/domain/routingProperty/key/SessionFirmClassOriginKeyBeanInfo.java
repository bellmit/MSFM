package com.cboe.domain.routingProperty.key;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;


/**
 * Exposes a limited set of BeanInfo for a SimpleBooleanTradingPropertyImpl
 */
public class SessionFirmClassOriginKeyBeanInfo extends SimpleBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    /**
     * Returns the property descriptor for the booleanValue method only
     */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if (allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[3];
            try
            {
                allowedDescriptors[0] =
                        new PropertyDescriptor(SessionFirmClassOriginKey.FIRM_PROPERTY_NAME, SessionFirmClassOriginKey.class);

                allowedDescriptors[1] =
                        new PropertyDescriptor(SessionFirmClassOriginKey.PRODUCT_CLASS_PROPERTY_NAME, SessionFirmClassOriginKey.class);

                allowedDescriptors[2] =
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

    /**
     * Returns the default property index for the booleanValue.
     */
    public int getDefaultPropertyIndex()
    {
        return 0;
    }
}
