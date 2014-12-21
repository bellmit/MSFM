package com.cboe.domain.routingProperty.properties;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import com.cboe.domain.routingProperty.key.SessionFirmClassAcronymKey;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class FirmClassAcronymPreferenceGroupBeanInfo extends SimpleBeanInfo
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
                    new PropertyDescriptor(SessionFirmClassAcronymKey.PRODUCT_CLASS_PROPERTY_NAME, SessionFirmClassAcronymKey.class);

                allowedDescriptors[1] =
                    new PropertyDescriptor(SessionFirmClassAcronymKey.ACRONYM_PROPERTY_NAME, SessionFirmClassAcronymKey.class);
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
