package com.cboe.domain.routingProperty.key;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class SessionReasonabilityEditBypassPostStationKeyBeanInfo extends SimpleBeanInfo
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
                allowedDescriptors[0] = new PropertyDescriptor(
                        SessionPostStationKey.TRADING_SESSION_PROPERTY_NAME,
                        SessionPostStationKey.class);

                allowedDescriptors[1] = new PropertyDescriptor(
                        SessionPostStationKey.POST_PROPERTY_NAME, SessionReasonabilityEditBypassPostStationKey.class);

                allowedDescriptors[2] = new PropertyDescriptor(
                        SessionPostStationKey.STATION_PROPERTY_NAME, SessionReasonabilityEditBypassPostStationKey.class);
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
