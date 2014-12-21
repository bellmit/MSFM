package com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses;

import java.util.*;

import com.sun.jaw.reference.common.*;

public abstract class DynamicEvent extends EventObject {
  
  
  
  public DynamicEvent() {
	  super(new String("dummyserializable"));
  }
	
  /**
	Sets the source to the m-bean emitting the event. This method is invoked by 
	a <code>DynamicEventSupport</code> instance. It has package 
	visibility to prevent clients outside the package from invoking it.
  */
  void setSource(ObjectName sourceMBean) {
	this.source = sourceMBean;
  } 
	
  /**
    This method has been added to have MOGEN generate a useful event MO.
  */  
  public DynamicEvent getDynamicEventInstance() {
	return this;
  }
}