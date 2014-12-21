//
//-----------------------------------------------------------------------------------
//Source file: Destination.java
//
//
//-----------------------------------------------------------------------------------
//Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
//-----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.routingProperty.common;

import com.cboe.interfaces.domain.routingProperty.common.OrderLocation;

/**
* Wrapper for the two attributes that define a Location: workstation name, workstation type.
*/
public interface Location extends Comparable<Location>
{
 /**
  * Returns the destination workstation name
  * @return
  */ 
 public String getWorkstation();
 public OrderLocation getWorkstationType();

 public void setWorkstation(String workstation);
 public void setWorkstationType(OrderLocation workstationType);
 
}