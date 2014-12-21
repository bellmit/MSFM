package com.cboe.domain.routingProperty.properties;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import com.cboe.domain.routingProperty.AbstractRoutingPropertyGroupBeanInfo;
import com.cboe.domain.routingProperty.key.SessionFirmCorrClassKey;
import com.cboe.domain.routingProperty.key.SessionFirmCorrPostStationKey;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class FirmCorrPARSimplePostStationGroupBeanInfo extends AbstractRoutingPropertyGroupBeanInfo
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
           allowedDescriptors = new PropertyDescriptor[3];
           try
           {
               allowedDescriptors[0] = new PropertyDescriptor(SessionFirmCorrPostStationKey.CORRESPONDENT_PROPERTY_NAME,
                       SessionFirmCorrPostStationKey.class);
               allowedDescriptors[1] =
                       new PropertyDescriptor(SessionFirmCorrPostStationKey.POST_PROPERTY_NAME, SessionFirmCorrPostStationKey.class);
               allowedDescriptors[2] =
                   new PropertyDescriptor(SessionFirmCorrPostStationKey.STATION_PROPERTY_NAME, SessionFirmCorrPostStationKey.class);
           }
           catch(IntrospectionException e)
           {
               Log.exception("Could not create PropertyDescriptor.", e);
               allowedDescriptors= null;
              return super.getPropertyDescriptors();
          }
       }
       return allowedDescriptors;
   }
   }
