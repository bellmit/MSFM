package com.cboe.domain.routingProperty.key;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;


/**
 * Exposes a limited set of BeanInfo for a SessionFirmClassKeyCorr
 */
public class SessionFirmPostStationAcronymKeyBeanInfo extends SimpleBeanInfo
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
                allowedDescriptors[0] = 
                    new PropertyDescriptor(SessionFirmPostStationAcronymKey.TRADING_SESSION_PROPERTY_NAME, SessionFirmPostStationAcronymKey.class);
                
                allowedDescriptors[1] = 
                    new PropertyDescriptor(SessionFirmPostStationAcronymKey.FIRM_PROPERTY_NAME, SessionFirmPostStationAcronymKey.class);

                allowedDescriptors[2] =
                    new PropertyDescriptor(SessionFirmPostStationAcronymKey.POST_PROPERTY_NAME, SessionFirmPostStationAcronymKey.class);

                allowedDescriptors[3] =
                    new PropertyDescriptor(SessionFirmPostStationAcronymKey.STATION_PROPERTY_NAME, SessionFirmPostStationAcronymKey.class);

                allowedDescriptors[4] = 
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

    /**
     * Returns the default property index for the booleanValue.
     */
    public int getDefaultPropertyIndex()
    {
        return 0;
    }
}