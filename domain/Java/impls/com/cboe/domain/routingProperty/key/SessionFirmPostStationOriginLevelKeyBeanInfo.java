package com.cboe.domain.routingProperty.key;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;


/**
 * Exposes a limited set of BeanInfo for a SessionFirmPostStationOriginLevelKeyBeanInfo
 */
public class SessionFirmPostStationOriginLevelKeyBeanInfo extends SimpleBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    /**
     * Returns the property descriptor for the booleanValue method only
     */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if (allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[6];
            try
            {
                allowedDescriptors[0] = new PropertyDescriptor(SessionFirmPostStationOriginLevelKey.TRADING_SESSION_PROPERTY_NAME,
                        SessionFirmPostStationOriginLevelKey.class);

                allowedDescriptors[1] = new PropertyDescriptor(SessionFirmPostStationOriginLevelKey.FIRM_PROPERTY_NAME,
                        SessionFirmPostStationOriginLevelKey.class);

                allowedDescriptors[2] = new PropertyDescriptor(SessionFirmPostStationOriginLevelKey.POST_PROPERTY_NAME,
                        SessionFirmPostStationOriginLevelKey.class);

                allowedDescriptors[3] = new PropertyDescriptor(SessionFirmPostStationOriginLevelKey.STATION_PROPERTY_NAME,
                        SessionFirmPostStationOriginLevelKey.class);

                allowedDescriptors[4] = new PropertyDescriptor(SessionFirmPostStationOriginLevelKey.ORIGIN_CODE_PROPERTY_NAME,
                        SessionFirmPostStationOriginLevelKey.class);

                allowedDescriptors[5] = new PropertyDescriptor(SessionFirmPostStationOriginLevelKey.LEVEL_PROPERTY_NAME,
                        SessionFirmPostStationOriginLevelKey.class);
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
