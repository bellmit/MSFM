/* Copyright (c) 1999 The Chicago Board Options Exchange.  All Rights Reserved */

package com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses;

import java.util.HashMap;

/**
  This interface defines the common operations supported by a feature descriptor. 
  An MBean feature is a property, method, or event.
*/  
public interface MBeanFeatureDescriptor {

  /**
    Returns the <code>MBeanInfo</code> object aggregating this object.
  */  
  public MBeanInfo getMBeanInfo();
  
  /**
    Returns the name of the MBean feature. An MBean feature is either a property, 
    method, or event set.
  */
  public String getName();
	
  /**
    Returns the human readable description of the property and its use.
  */
  public String getDescription();

  /**
    Returns the string used to display the name of the feature to the user.
  */
  public String getDisplayName();

  /**
    * Returns a map of immutable attribute name/value pairs for retrieval from a remote proxy
    */
  public HashMap getAttributes();

  /**
	Adds a <code>AttributeChangeListener</code>.
  */
  public void addAttributeChangeListener(AttributeChangeListener attributeChangeListener);
	  
  /**
	Removes a <code>AttributeChangeListener</code>.
  */
  public void removeAttributeChangeListener(AttributeChangeListener attributeChangeListener);
  
}
