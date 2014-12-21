//Source file: D:/SBT/SystemManagement/java/com/cboe/systemsManagementService/managedObjectFramework/managedObjectBaseClasses/MBeanFeatureSetChangeListener.java

/* Copyright (c) 1999 The Chicago Board Options Exchange.  All Rights Reserved */

package com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses;

public interface MBeanFeatureSetChangeListener extends java.util.EventListener {
	
	/**
	   Invoked when an MBean feature (property, method, or event set) has been added to 
	   the MBeanInfo object.
	 */
	public void featureAdded(MBeanFeatureSetChangeEvent mBeanFeatureSetChangeEvent);
	
	/**
	   Invoked when an MBean feature (property, method, or event set) has been removed from 
	   the MBeanInfo object.
	   
	 */
	public void featureRemoved(MBeanFeatureSetChangeEvent mBeanFeatureSetChangeEvent);
}
