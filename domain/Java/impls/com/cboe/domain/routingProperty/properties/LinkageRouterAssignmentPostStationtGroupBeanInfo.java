package com.cboe.domain.routingProperty.properties;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import com.cboe.domain.routingProperty.AbstractRoutingPropertyGroupBeanInfo;
import com.cboe.domain.routingProperty.key.ExternalExchangeFirmPostStationKey;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class LinkageRouterAssignmentPostStationtGroupBeanInfo extends
        AbstractRoutingPropertyGroupBeanInfo
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
            allowedDescriptors = new PropertyDescriptor[5];
            try
            {
                allowedDescriptors[0] =
                        new PropertyDescriptor(ExternalExchangeFirmPostStationKey.TRADING_SESSION_PROPERTY_NAME, ExternalExchangeFirmPostStationKey.class);

                allowedDescriptors[1] =
                        new PropertyDescriptor(ExternalExchangeFirmPostStationKey.EXTERNAL_EXCHANGE_PROPERTY_NAME, ExternalExchangeFirmPostStationKey.class);

                allowedDescriptors[2] =
                        new PropertyDescriptor(ExternalExchangeFirmPostStationKey.EXCHANGE_FIRM_PROPERTY_NAME, ExternalExchangeFirmPostStationKey.class, "getExchangeFirm", null);

                allowedDescriptors[3] =
                        new PropertyDescriptor(ExternalExchangeFirmPostStationKey.POST_PROPERTY_NAME, ExternalExchangeFirmPostStationKey.class);

                allowedDescriptors[4] =
                        new PropertyDescriptor(ExternalExchangeFirmPostStationKey.STATION_PROPERTY_NAME, ExternalExchangeFirmPostStationKey.class);
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
