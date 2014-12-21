package com.cboe.domain.routingProperty.properties;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import com.cboe.domain.routingProperty.key.SessionFirmPostStationAcronymKey;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class FirmPostStationAcronymPreferenceGroupBeanInfo extends SimpleBeanInfo
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
            allowedDescriptors = new PropertyDescriptor[3];
            try
            {   
                allowedDescriptors[0] =
                    new PropertyDescriptor(SessionFirmPostStationAcronymKey.POST_PROPERTY_NAME, SessionFirmPostStationAcronymKey.class);
                
                allowedDescriptors[1] =
                    new PropertyDescriptor(SessionFirmPostStationAcronymKey.STATION_PROPERTY_NAME, SessionFirmPostStationAcronymKey.class);

                allowedDescriptors[2] =
                    new PropertyDescriptor(SessionFirmPostStationAcronymKey.ACRONYM_PROPERTY_NAME, SessionFirmPostStationAcronymKey.class);
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