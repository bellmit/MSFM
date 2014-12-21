package com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses;

// java packages
import java.lang.reflect.InvocationTargetException;

import com.sun.jaw.reference.common.ObjectName;

/**
  This interface stipulates that a proxied MBean (including MBean descriptors), 
  must implement the <code>InvokeMethod</code> MBean 'action' to accept and 
  delegate invocation requests received from one of its remote proxies.

  @author Luis I. Benavides
  @version 1.0
 */
public interface Proxied {

  /**
    MBean 'action' that enables the invocation of any public method in a subclass.
    
    @param methodName simple name of a public method
    @param parameters parameters passed in the call to the method
    
    @exception ClassNotFoundException</li> a parameter's class could not be found.
    @exception IllegalAccessException</li> The class of one of the paramters could not be loaded because 
        definition its definition is not public and in another package.
    @exception IllegalArgumentException The number of actual and formal parameters differ, 
        or if an unwrapping conversion failed. 
    @exception InvocationTargetException</li> Encapsulates an exception thrown by the ultimate target, 
        the hardcoded method invoked on the m-bean.
    @exception NoSuchMethodException</li> the method does not exist.
    @exception SecurityException</li>

  */  
  public Object performInvokeMethod(String methodName, 
                                    Object[] parameters, 
                                    String[] paramTypes) 
    throws  ClassNotFoundException,
            IllegalAccessException,
            IllegalArgumentException,
            InvocationTargetException,
            NoSuchMethodException,
            SecurityException;

  public ObjectName getObjectName();

}

