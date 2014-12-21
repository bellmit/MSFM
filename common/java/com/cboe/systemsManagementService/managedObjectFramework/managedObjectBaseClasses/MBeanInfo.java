//Source file: D:/SBT/SystemManagement/java/com/cboe/systemsManagementService/managedObjectFramework/managedObjectBaseClasses/MBeanBeanInfo.java

/* Copyright (c) 1999 The Chicago Board Options Exchange.  All Rights Reserved */

package com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses;

import java.util.HashMap;

import com.sun.jaw.reference.common.*;

/**
  This interface defines the operations supported by the MBeanInfo object associated with 
  a generic MBean. 
   
  @author Luis I. Benavides
  @version 1.0
*/
public interface MBeanInfo {

  /**
    Returns the <code>MBean</code> object which this <code>MBeanInfo</code> object describes.
  */
  public MBean getMBean();
  
  /**
    Returns a reference to the <code>MBeanDescriptor</code>.
  */
  public MBeanDescriptor getMBeanDescriptor();
  
  /**
    Returns an array of the <code>MBeanPropertyDescriptor</code>s. 
  */
  public MBeanPropertyDescriptor[] getMBeanPropertyDescriptors();

  /**
	Returns the descriptor of the property named <code>propertyName</code>.

    @exception InstanceNotFoundException The property does not exist.
  */
  public MBeanPropertyDescriptor getMBeanPropertyDescriptor(String propertyName)
    throws InstanceNotFoundException;

  /**
	Returns an array of the <code>MBeanMethodDescriptor</code>s.
  */
  public MBeanMethodDescriptor[] getMBeanMethodDescriptors();
	
  /**
    Returns the descriptor of the method named <code>methodName</code>.

    @exception InstanceNotFoundException The property does not exist.
  */
  public MBeanMethodDescriptor getMBeanMethodDescriptor(String methodName)
    throws InstanceNotFoundException;
	
  /**
	Returns an array of the <code>MBeanEventSetDescriptor</code>s.
  */
  public MBeanEventSetDescriptor[] getMBeanEventSetDescriptors();

  /**
    Returns the descriptor of the event set named <code>eventSetName</code>.

    @exception InstanceNotFoundException The property does not exist.
  */
  public MBeanEventSetDescriptor getMBeanEventSetDescriptor(String eventSetName) 
    throws InstanceNotFoundException;
	
  /**
	Returns an array of the <code>MBeanRelationDescriptor</code>s.
  */
  public MBeanRelationDescriptor[] getMBeanRelationDescriptors();

  /**
    Returns the descriptor of the relation named <code>relationName</code>.

    @exception InstanceNotFoundException The relation does not exist.
  */
  public MBeanRelationDescriptor getMBeanRelationDescriptor(String relationName)
    throws InstanceNotFoundException;
	
  /**
    * Returns a map of immutable attribute name/value pairs for retrieval from a remote proxy
    */
  public HashMap getAttributes();

  /**
	Adds an <code>MBeanFeatureSetChangeListener</code>.
  */
  public void addMBeanFeatureSetChangeListener(MBeanFeatureSetChangeListener mBeanFeatureSetChangeListener);
	
  /**
	Removes <code>MBeanFeatureSetChangeListener</code>.
  */
  public void removeMBeanFeatureSetChangeListener(MBeanFeatureSetChangeListener mBeanFeatureSetChangeListener);
	  
}

