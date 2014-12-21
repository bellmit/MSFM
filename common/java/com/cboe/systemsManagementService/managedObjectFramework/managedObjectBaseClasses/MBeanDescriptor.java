/* Copyright (c) 1999 The Chicago Board Options Exchange.  All Rights Reserved */

package com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses;

/**
  This interface defines the operations on an MBean descriptor.
*/  
public interface MBeanDescriptor extends MBeanFeatureDescriptor {

  /**
    Returns a reference to the <code>MBeanInfo</code> object associated with this MBean.
	*/
  public MBeanInfo getMBeanInfo();
  
  /**
    Returns the name of this MBean, which is equal to the value of the <code>name</code>
    attribute of the corresponding <code>ManagedResource</code> element in the XML document.
    For example, "LoggingClient1".
    
    @return name of this MBean.
  */
  public String getName();
  
  /**
    Returns the type of this MBean, which is equal to the tag of the corresponding 
    <code>ManagedResource</code> element in the Resource Instance XML document. For example, 
    "LoggingServiceClient"
    
    @return type of this MBean.
  */
  public String getMBeanType();
  
  /**
    Returns the name of the managed resource this MBean belongs to, which is the tag of the 
    root element in the Resource Instance XML document. For example, "GlobalLoggingService".
  */
  public String getResourceName();
  
  /**
    Returns the version of the Resource Schema (the DTD that validates the Resource Instance 
    XML documents of a given resource) in which this MBean's type is specified. 
    This version is the value of the <code>version</code> attribute of the root element 
    in the Resource Instance XML document.
  */
  public String getResourceSchemaVersion();
  
  /**
    Returns the name of the Resource Instance that contains this MBean, which is 
    the value of the <Code>name</code> attribute of the root element in the Resource Instance 
    XML document. For example, "SBTLoggingService".
  */
  public String getResourceInstanceName();
  
}