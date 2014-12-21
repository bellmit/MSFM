//
//-----------------------------------------------------------------------------------
//Source file: VolumeDeviationDestinationGroupBeanInfo.java
//
//PACKAGE: com.cboe.domain.routingProperty.properties
//
//-----------------------------------------------------------------------------------
//Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
//-----------------------------------------------------------------------------------
package com.cboe.domain.routingProperty.properties;

import java.beans.SimpleBeanInfo;
import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;

import com.cboe.domain.routingProperty.key.SessionFirmClassKey;
import com.cboe.domain.routingProperty.key.SessionFirmClassOriginLevelKey;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class VolumeDeviationDestinationGroupBeanInfo extends SimpleBeanInfo
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
             allowedDescriptors[0] =
                     new PropertyDescriptor(SessionFirmClassOriginLevelKey.PRODUCT_CLASS_PROPERTY_NAME, SessionFirmClassOriginLevelKey.class);
             allowedDescriptors[1] =
                 new PropertyDescriptor(SessionFirmClassOriginLevelKey.ORIGIN_CODE_PROPERTY_NAME, SessionFirmClassOriginLevelKey.class);
             allowedDescriptors[2] =
                     new PropertyDescriptor(SessionFirmClassOriginLevelKey.LEVEL_PROPERTY_NAME, SessionFirmClassOriginLevelKey.class);
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
