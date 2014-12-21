//Source file: D:/SBT/SystemManagement/java/com/cboe/systemsManagementService/managedObjectFramework/managedObjectBaseClasses/TreeChangeListener.java

/* Copyright (c) 1999 The Chicago Board Options Exchange.  All Rights Reserved */

package com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses;

public interface TreeChangeListener extends java.util.EventListener {
	
	/**
	   Invoked when a component m-bean has been added to the m-bean.
	 */
	public void componentAdded(TreeChangeEvent treeChangeEvent);
	
	/**
	   Invoked when a component m-bean has been removed from the m-bean.
	   
	 */
	public void componentRemoved(TreeChangeEvent treeChangeEvent);

}
