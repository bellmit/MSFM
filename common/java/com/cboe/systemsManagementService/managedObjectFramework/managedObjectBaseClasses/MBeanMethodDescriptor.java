//Source file: D:/SBT/SystemManagement/java/com/cboe/systemsManagementService/managedObjectFramework/managedObjectBaseClasses/MBeanMethodDescriptor.java

/* Copyright (c) 1999 The Chicago Board Options Exchange.  All Rights Reserved */

package com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses;

import java.lang.reflect.InvocationTargetException;
import java.util.TooManyListenersException;

/**
  Defines the operations on an MBean's method descriptor.

  @author Luis I. Benavides
  @version 1.0
*/  
public interface MBeanMethodDescriptor extends MBeanFeatureDescriptor {

  /**
    Returns an array of the method's parameter descriptors.
  */  
  public MBeanParameterDescriptor[] getMBeanParameterDescriptors();

  /**
    Adds a method invocation listener. At the most only one listener can be register at any time.
  */
  public void addMethodInvocationListener(MethodInvocationListener methodInvocationListener) 
	throws TooManyListenersException;
	  
  /**
    Returns <code>true</code> if a method invocation listener is currently registered.
  */
  public boolean isMethodInvocationListenerRegistered();
    
  /**
	Removes the currently registered <code>methodInvocationListener</code>, even if the 
	one passed as a parameter is distinct.
  */
  public void removeMethodInvocationListener(MethodInvocationListener methodInvocationListener);

  /**
	Invokes the <code>methodInvoked</code> method on the registered (unique)
	<code>methodInvocationListener</code>, if any; ignores the request and 
	returns null if no <code>methodInvocationListener</code> has been registered.
	  
	@param parameters array with the parameters for the invocation.
	  
	@return the value returned by the <code>methodInvoked</code> method invoked on the 
	registered (unique) <code>methodInvocationListener</code>, if any; or null if no 
	listener has been registered.
	  
	@exception InvocationTargetException wraps an exception returned by 
	the <code>methodInvoked</code> method.
  */
  public void fireMethodInvocation(Object[] parameters)
	throws InvocationTargetException;

}