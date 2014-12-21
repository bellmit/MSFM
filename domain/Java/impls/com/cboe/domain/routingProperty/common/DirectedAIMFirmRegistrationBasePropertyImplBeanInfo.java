//
//-----------------------------------------------------------------------------------
//Source file: DirectedAIMFirmRegistrationBasePropertyImplBeanInfo.java
//
//PACKAGE: com.cboe.domain.routingProperty.common
//
//-----------------------------------------------------------------------------------
//Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
//-----------------------------------------------------------------------------------
package com.cboe.domain.routingProperty.common;

import java.beans.SimpleBeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class DirectedAIMFirmRegistrationBasePropertyImplBeanInfo extends SimpleBeanInfo
{
 private PropertyDescriptor[] allowedDescriptors;

 public PropertyDescriptor[] getPropertyDescriptors()
 {
     if(allowedDescriptors == null)
     {
         allowedDescriptors = new PropertyDescriptor[2];
         try
         {
             allowedDescriptors[0] = new PropertyDescriptor("isRegistered", DirectedAIMFirmRegistrationBasePropertyImpl.class);
             allowedDescriptors[1] = new PropertyDescriptor("lastUpdatedTime", DirectedAIMFirmRegistrationBasePropertyImpl.class);
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

 public int getDefaultPropertyIndex()
 {
     return 0;
 }
}
