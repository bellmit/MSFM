package com.cboe.domain.routingProperty.key;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class SessionFirmCorrPostStationKeyBeanInfo extends SimpleBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    /**
     * Returns the property descriptor for the booleanValue method only
     */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if (allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[5];
            try
            {
                allowedDescriptors[0] = new PropertyDescriptor(SessionFirmCorrPostStationKey.TRADING_SESSION_PROPERTY_NAME,
                        SessionFirmCorrPostStationKey.class);
                
                allowedDescriptors[1] =
                    new PropertyDescriptor(SessionFirmCorrPostStationKey.FIRM_PROPERTY_NAME, SessionFirmCorrPostStationKey.class);
                
                allowedDescriptors[2] =
                    new PropertyDescriptor(SessionFirmCorrPostStationKey.CORRESPONDENT_PROPERTY_NAME, SessionFirmCorrPostStationKey.class);

                allowedDescriptors[3] =
                    new PropertyDescriptor(SessionFirmCorrPostStationKey.POST_PROPERTY_NAME, SessionFirmCorrPostStationKey.class);

                allowedDescriptors[4] =
                    new PropertyDescriptor(SessionFirmCorrPostStationKey.STATION_PROPERTY_NAME, SessionFirmCorrPostStationKey.class);
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
