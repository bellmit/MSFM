//
//-----------------------------------------------------------------------------------
//Source file: LocationImpl.java
//
//PACKAGE: com.cboe.domain.routingProperty.common
//
//-----------------------------------------------------------------------------------
//Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
//-----------------------------------------------------------------------------------

package com.cboe.domain.routingProperty.common;


import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.routingProperty.common.Location;
import com.cboe.interfaces.domain.routingProperty.common.OrderLocation;


/**
* Wrapper for the three attributes that define a Destination: workstation name, workstation type, and action.
*/
public class LocationImpl implements Location
{
 public String workstation;
 public OrderLocation workstationType;    

 private String displayStr = null;

 
 
 public LocationImpl(String workstation, String workstationTypeStr)
 {
     this.workstation = workstation;
     try
     {
         workstationType = OrderLocation.findOrderLocationEnum(Short.parseShort(workstationTypeStr));
     }
     catch(NumberFormatException e)
     {
         Log.information(getClass().getName()+": NumberFormatException trying to convert workstationType '"+workstationTypeStr+"' to short value; setting workstationType to OrderLocations.UNSPECIFIED. ");
         workstationType = OrderLocation.UNSPECIFIED;
     }
     
 }

 public LocationImpl(String workstation, OrderLocation workstationType)
 {
     this.workstation = workstation;
     this.workstationType = workstationType;        
 }
 

 public String getWorkstation()
 {
     return workstation;
 }

 public OrderLocation getWorkstationType()
 {
     return workstationType;
 }

 public void setWorkstation(String workstation)
 {
     this.workstation = workstation;
     displayStr = null;
 }

 public void setWorkstationType(OrderLocation workstationType)
 {
     this.workstationType = workstationType;
     displayStr = null;
 }

 public String toString()
 {
     if(displayStr == null)
     {
         StringBuffer sb = new StringBuffer(50);
         sb.append("[");
         sb.append("Name=").append(workstation).append(",Type=").append(workstationType);
         sb.append("]");
         displayStr = sb.toString();
     }
     return displayStr;
 }

 public int compareTo(Location otherDest)
 {
     return toString().compareTo(otherDest.toString());
 }
}
