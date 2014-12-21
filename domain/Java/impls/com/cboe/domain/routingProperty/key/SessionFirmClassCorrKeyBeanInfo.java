package com.cboe.domain.routingProperty.key;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;


/**
 * Exposes a limited set of BeanInfo for a SessionFirmClassKeyCorr
 */
public class SessionFirmClassCorrKeyBeanInfo extends SimpleBeanInfo
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
                allowedDescriptors[0] = new PropertyDescriptor(SessionFirmClassCorrKey.FIRM_PROPERTY_NAME, SessionFirmClassCorrKey.class);

                allowedDescriptors[1] = new PropertyDescriptor(SessionFirmClassCorrKey.PRODUCT_CLASS_PROPERTY_NAME, SessionFirmClassCorrKey.class);

                allowedDescriptors[2] = new PropertyDescriptor(SessionFirmClassCorrKey.CORRESPONDENT_PROPERTY_NAME, SessionFirmClassCorrKey.class);

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
