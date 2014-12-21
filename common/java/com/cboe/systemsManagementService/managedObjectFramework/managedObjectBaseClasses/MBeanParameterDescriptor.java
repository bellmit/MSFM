//Source file: D:/SBT/SystemManagement/java/com/cboe/systemsManagementService/managedObjectFramework/managedObjectBaseClasses/MBeanParameterDescriptor.java

/* Copyright (c) 1999 The Chicago Board Options Exchange.  All Rights Reserved */

package com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses;

/**
  Defines the operations on a parameter descriptor belonging to an MBean's method.

  @author Luis I. Benavides
  @version 1.0
*/  
public interface MBeanParameterDescriptor extends MBeanFeatureDescriptor {

  public static final String STRING = "string";
  
  public static final String INTEGER = "integer";
  
  public static final String FLOAT = "float";
  
  public static final String ENUM = "enum";
  
  /**
    Returns the type of the parameter. Possible values are: "string", "integer", and "float".
  */  
  public String getParameterType();

  /**
    Returns the possible values of a parameter of type "enum".
  */
  public String[] getEnumerationValues();

}