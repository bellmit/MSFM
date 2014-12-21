package com.cboe.domain.routingProperty.properties;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import com.cboe.domain.routingProperty.AbstractRoutingPropertyGroupBeanInfo;
import com.cboe.domain.routingProperty.key.SessionPostStationKey;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class LinkageOrderDestinationPostStationGroupBeanInfo extends AbstractRoutingPropertyGroupBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    /**
     * Hides all PropertyDesciptor's for the represented class
     * @return an empty array
     */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if(allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[2];
            try
            {
                allowedDescriptors[0] =
                    new PropertyDescriptor(SessionPostStationKey.POST_PROPERTY_NAME, SessionPostStationKey.class);
                
                allowedDescriptors[1] =
                        new PropertyDescriptor(SessionPostStationKey.STATION_PROPERTY_NAME, SessionPostStationKey.class);
            }
            catch(IntrospectionException e)
            {
                Log.exception("Could not create PropertyDescriptor.", e);
                allowedDescriptors = null;
                return super.getPropertyDescriptors();
            }
        }
        return allowedDescriptors;
    }
}
