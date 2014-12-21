//Source file: D:/SBT/SystemManagement/java/com/cboe/systemsManagementService/managedObjectFramework/managedObjectBaseClasses/MBeanFeatureSetChangeEvent.java

/* Copyright (c) 1999 The Chicago Board Options Exchange.  All Rights Reserved */

package com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses;

import java.util.EventObject;

import com.sun.jaw.reference.common.*;

public class MBeanFeatureSetChangeEvent extends EventObject {
	
  /**
	  @roseuid 3763FC550004
	*/
  public MBeanFeatureSetChangeEvent(MBeanFeatureDescriptor mBeanFeatureDescriptor) {
	
	super(mBeanFeatureDescriptor.getMBeanInfo().getMBean());
	this.featureName = mBeanFeatureDescriptor.getName();
  }
  
  public ObjectName getMBeanFeatureDescriptorObjectName() {
    
    return (ObjectName)source;
  }  
  
  public String getFeatureName() {
    return featureName;
  }  

  private String featureName;

}
