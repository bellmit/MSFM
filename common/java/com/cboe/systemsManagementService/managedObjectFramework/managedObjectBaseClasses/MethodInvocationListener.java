//Source file: D:/SBT/SystemManagement/java/com/cboe/systemsManagementService/managedObjectFramework/managedObjectBaseClasses/MethodInvocationListener.java

/* Copyright (c) 1999 The Chicago Board Options Exchange.  All Rights Reserved */

package com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses;

public interface MethodInvocationListener extends java.util.EventListener {
	
	/**
	   Invoked on the MethodInvocationListener when the dynamic method is invoked on the m-bean
	   @roseuid 37601EDF00C1
	 */
	public void methodInvoked(MethodInvocationEvent methodInvocationEvent) throws Throwable;
	
}
