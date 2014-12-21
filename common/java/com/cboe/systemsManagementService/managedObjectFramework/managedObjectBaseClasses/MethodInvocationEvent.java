//Source file: D:/SBT/SystemManagement/java/com/cboe/systemsManagementService/managedObjectFramework/managedObjectBaseClasses/MethodInvocationEvent.java

/* Copyright (c) 1999 The Chicago Board Options Exchange.  All Rights Reserved */

package com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses;

import java.util.EventObject;

public class MethodInvocationEvent extends EventObject {
	
	/**
	   @roseuid 3763D5B401BB
	 */
	public MethodInvocationEvent( MBean sourceMBean, 
	                              String invokedMethodName, 
	                              Object[] parameters) {
	
	  super(sourceMBean);
	  this.invokedMethodName = invokedMethodName;
	  this.parameters = parameters;
  }
	
	/**
	   @roseuid 3763D83A034A
	 */
	public String getInvokedMethodName() {
	
	  return invokedMethodName;
  }
	
	/**
	   @roseuid 3763D84A025D
	 */
	public Object[] getParameters() {
	  
    return parameters;
	  
  }

  private String invokedMethodName;
  private Object[] parameters;

}
