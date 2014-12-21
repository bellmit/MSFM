//Source file: D:/SBT/SystemManagement/java/com/cboe/systemsManagementService/managedObjectFramework/managedObjectBaseClasses/MBeanEventSetDescriptor.java

/* Copyright (c) 1999 The Chicago Board Options Exchange.  All Rights Reserved */

package com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses;

/**
  Defines the operations supported by an MBean's event set descriptor.
*/  
public interface MBeanEventSetDescriptor extends MBeanFeatureDescriptor {

  /**
    Adds a listener to the event set.

	<p>NOTE: This method has been moved from the MOBaseClass because MOGEN 
	does not recognize event notifications at a granularity finer than
	an m-bean, as in this case, at the event set level.
  */
  public void addDynamicEventListener(DynamicEventListener listener);
	
  /**
    Removes a listener to this event set.

	<p>NOTE: This method has been moved from the MOBaseClass because MOGEN 
	does not recognize event notifications at a granularity finer than
	an m-bean, as in this case, at the event set level.
  */
  public void removeDynamicEventListener(DynamicEventListener listener);
  
  /*
    Notifies registered listeners that a dynamic event has occurred.

	<p>NOTE: This method has been moved from MOBaseClass because MOGEN 
	does not recognize event notifications at a granularity finer than
	an m-bean, as in this case, at the event set level.

    @param dynamicEvent the dynamic event subclass to be broadcast to listeners.
  */
  public void fireDynamicEvent(DynamicEvent dynamicEvent);

}