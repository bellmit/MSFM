package com.cboe.domain.routingProperty.properties;
//-----------------------------------------------------------------------------------
//Source file: FirmVolumeLimitGroupBeanInfo
//
//PACKAGE: com.cboe.domain.routingProperty.properties
//
//Created: Aug 7, 2006 2:47:40 PM
//-----------------------------------------------------------------------------------
//Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
//-----------------------------------------------------------------------------------

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import com.cboe.domain.routingProperty.AbstractRoutingPropertyGroupBeanInfo;
import com.cboe.domain.routingProperty.key.SessionFirmPostStationKey;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class FirmVolumePostStationGroupBeanInfo extends AbstractRoutingPropertyGroupBeanInfo
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
                     new PropertyDescriptor(SessionFirmPostStationKey.POST_PROPERTY_NAME, SessionFirmPostStationKey.class);
             allowedDescriptors[1] =
                 new PropertyDescriptor(SessionFirmPostStationKey.STATION_PROPERTY_NAME, SessionFirmPostStationKey.class);
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
