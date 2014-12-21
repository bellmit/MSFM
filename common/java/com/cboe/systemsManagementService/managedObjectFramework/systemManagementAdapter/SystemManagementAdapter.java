/* Copyright (c) 1999 The Chicago Board Options Exchange.  All Rights Reserved */

package com.cboe.systemsManagementService.managedObjectFramework.systemManagementAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses.*;
import com.sun.jaw.reference.common.*;

/**
  Per-process singleton that is the entry point for obtaining trees of MBeans, 
  which expose managed resource components for external management. There are two basic 
  kinds of implementations of this abstract class. The first one, connects MBean proxies to 
  their corresponding MBeans in a remote Agent or resource process; the second implementation 
  constructs MBeans from their persistent images stored in a set of XML files.
   
  <p>Selection of the particular implementation is accomplished by passing Java system 
  properties when launching the process where the managed resource components and this 
  singleton run. Each implementation also requires specific java <i>system properties</i> 
  for initializing and locating the MBeans that are the roots of the 
  trees returned by the <code>getMBean()</code> method. The fully qualified names of 
  the component MBeans in a tree will always be relative to such root MBeans.
     
  @author Luis I. Benavides
  @version 2.0
*/
public abstract class SystemManagementAdapter
{
  private static SystemManagementAdapter systemManagementAdapterInstance = null;
  private static Object traceLockObject = new Object();

  /**
  * Initialization method that must be implemented by the implementation class.
  */  
  protected abstract void initialize() throws IllegalArgumentException;

  /**
  * Returns the top-level MBeans available to clients through the local SMA instance.
  * @return References to the top-level MBeans available to clients through the local SMA instance.
  */
  public abstract MBean[] getRootMBeans();

  /**
  * Returns an MBean given its object name.
  * @param objectName object name of the MBean to return.
  * @return A reference to the MBean whose object name is <code>objectName</code>.
  */
  public abstract MBean getMBean(ObjectName objectName) throws InstanceNotFoundException;

  /**
  * Returns an MBean instance given a dot-delimited list of MBean types which
  * are matched against system properties to construct a path leading to the desired MBean.
  *
  * <p>The application does not supply the name of the MBean name directly, but instead
  * specifies types, to provide a level of indirection that prevents the
  * application from having to hard-code any instance specific information.
  *
  * @param mBeanTypePath A dot-delimited list of MBean types, for example,
  * <code>GlobalLoggingService.LoggingServiceClient</code>.
  * @exception InstanceNotFoundException No MBean could be located using the <code>mBeanTypePath</code>.
  */
  public abstract MBean getMBean(String mBeanTypePath) throws InstanceNotFoundException;

  protected SystemManagementAdapter()
  {
    Debug.parseDebugProperties();
    String typeToInstanceFile = System.getProperty("SMA.TypeToInstanceFile");
    if (typeToInstanceFile != null) {
      boolean subsOK = getSubstitutionsFromFile(typeToInstanceFile);
      if (!subsOK) {
        trace("Attempted Type-to-Instance substitutions from file " + typeToInstanceFile + " has failed.", "constructor");
        traceln("Continuing anyway...", "constructor");
      }
    }
  }

  /**
   * Adds type-to-instance properties from a file.
   * File-based substitutions will <em>override</em> any substitutions
   * on the command line; no check for an existing property is done.<br>
   * This was done to circumvent a Windows NT command line length problem
   * @param fileName the filename to read the substitutions from
   */
  private boolean getSubstitutionsFromFile(String fileName)
  {
    File aFile = new File(fileName);
    if (!aFile.canRead()) {
      trace("I cannot read file " + fileName + ".", "getSubstitutionsFromFile");
      return false;
    }

    Properties myProps = new Properties();
    try {
      myProps.load(new FileInputStream(fileName));
    }
    catch(Exception e) {
      trace("Exception reading file " + fileName + ".", "getSubstitutionsFromFile");
      e.printStackTrace();
      return false;
    }

    String aKey = null;
    String aValue = null;
    Enumeration propKeys = myProps.keys();

    // transfer the properties into the System properties list
    // add the "SMA." prefix
    trace("\nstarting to add substitutions from file " + fileName + ".\n", "getSubstitutionsFromFile");
    while ( propKeys.hasMoreElements() )
    {
      aKey = (String)propKeys.nextElement();
      aValue = myProps.getProperty(aKey);
      System.setProperty( "SMA."+aKey, aValue);
      trace("adding substitution: SMA." + aKey + " = " + aValue, "getSubstitutionsFromFile");
    }
    trace("\ndone with file-based substitutions.", "getSubstitutionsFromFile");
    return true;
  }

  /**
	Factory method that returns the singleton instance of the 
	<code>SystemManagementAdapter</code> implementation class selected through 
	the <code>SMA.ImplClass</code> system property. The implementation class belongs to this 
	class' package and must implement a no-arg constructor with package visibility.
	
	<p>Initialization is delegated to the package-visible method
	<code>initialize</code> of the implementation class.
	   
	@returns singleton instance of the <code>SystemManagementAdapter</code>.

	@exception ClassNotFoundException The SystemManagementAdapter implementation class
	  could not be found.
	@exception InstantiationException Could not instantiate the SystemManagementAdapter 
	  implementation class.
    @exception IllegalArgumentException the system property passed in the command-line to 
	  initialize the SystemManagementAdapter implementation selected is illegal; 
	  the detail message in the exception provides more information about which 
	  system property was illegal.
	@exception IllegalAccessException If the implementation class is abstract an interface, 
	  or if the instantiation fails for some other reason.  
  */
  public static final synchronized SystemManagementAdapter getInstance()
  throws ClassNotFoundException, InstantiationException, IllegalArgumentException, IllegalAccessException
  {
	  if ( systemManagementAdapterInstance == null ) {
	    if ( System.getProperty("SMAImplClass") != null ) {
	      throw new RuntimeException("The system property 'SMAImplClass' has been deprecated. Please use 'SMA.ImplClass' instead");
	    }

	    String SMAimplClass = System.getProperty("SMA.ImplClass");
	    if ( SMAimplClass == null ) {
	      throw new IllegalArgumentException("The 'SMA.ImplClass' system property was not defined");
	    }  
  	  
	    SMAimplClass = "com.cboe.systemsManagementService.managedObjectFramework.systemManagementAdapter." + SMAimplClass;
	    systemManagementAdapterInstance = (SystemManagementAdapter)Class.forName(SMAimplClass).newInstance();
	    systemManagementAdapterInstance.initialize();
	}  
	return systemManagementAdapterInstance;
  }

  /**
  * Resets the SystemManagementAdapter so that a subsequent call to <code>getInstance()</code>
  * will return a new SMA implementation object initialized from the prevailing system 
  * properties.
  */
  public static final synchronized void reset()
  {
    systemManagementAdapterInstance = null;
  }  
  
  /**
  * Method available to subclasses to instantiate the SMAServer singleton in a 
  * managed resource process, when the SMA impl is initialized.
  */
  public abstract void instantiateSMAServer();

  /**
  * Returns a Proxied object (any of MBean, MBeanInfo, and MBean descriptor 
  * implementations), given its object name.
  * @param objectName object name of the Proxied object to return
  * @return Proxied object
  */
  public final Proxied getProxied(ObjectName objectName)
  throws InstanceNotFoundException
  {
    Proxied proxied = null;
    
    // Analyzes the object name to determine the type of object requested, i.e., MBean,
    // MBeanInfo, or an MBean descriptor
    Hashtable propertyList = objectName.getPropertyList();
    ObjectName mBeanObjectName = null;
    if (  propertyList.get("MBeanInfo") != null && propertyList.get("MBeanInfo").equals("MBeanInfo") ) {
      
      // remove the 'MBeanInfo' key from the object name to get the owner MBean
      propertyList.remove("MBeanInfo");
      mBeanObjectName = new ObjectName( objectName.getDomain(), objectName.getClassName(), propertyList);
      proxied = (Proxied)getMBean(mBeanObjectName).getMBeanInfo();
    }
    else if ( propertyList.get("Property") != null ) {
      
      // remove the 'Property' key from the object name to get the owner MBean
      String propertyName = (String)propertyList.remove("Property");
      mBeanObjectName = new ObjectName( objectName.getDomain(), objectName.getClassName(), propertyList);
      proxied = (Proxied)getMBean(mBeanObjectName).getMBeanInfo().getMBeanPropertyDescriptor(propertyName);
    }
    //    this check for Parameter *must* be before the Method check
    //    because the Parameter property includes the method name
    else if ( propertyList.get("Parameter") != null ) {
        String methodName = (String)propertyList.remove("Method");
        String parameterName = (String)propertyList.remove("Parameter");
        mBeanObjectName = new ObjectName( objectName.getDomain(), objectName.getClassName(), propertyList);
        MBeanMethodDescriptor methodDesc = getMBean(mBeanObjectName).getMBeanInfo().getMBeanMethodDescriptor(methodName);
        MBeanParameterDescriptor[] parmList = methodDesc.getMBeanParameterDescriptors();
        for (int i=0; i<parmList.length; i++) {
            if (parmList[i].getName().equals(parameterName)) {
                proxied = (Proxied)parmList[i];
                break;
            }
        }
    }
    else if ( propertyList.get("Method") != null ) {
      
      // remove the 'Method' key from the object name to get the owner MBean
      String methodName = (String)propertyList.remove("Method");
      mBeanObjectName = new ObjectName( objectName.getDomain(), objectName.getClassName(), propertyList);
      proxied = (Proxied)getMBean(mBeanObjectName).getMBeanInfo().getMBeanMethodDescriptor(methodName);
    }
    else if ( propertyList.get("Relation") != null ) {

      // remove the 'Relation' key from the object name to get the owner MBean
      String relationName = (String)propertyList.remove("Relation");
      mBeanObjectName = new ObjectName( objectName.getDomain(), objectName.getClassName(), propertyList);
      proxied = (Proxied)getMBean(mBeanObjectName).getMBeanInfo().getMBeanRelationDescriptor(relationName);
    }
    else { // assume the requested object is an MBean
      proxied = (Proxied)getMBean(objectName);
    }
	synchronized (traceLockObject) {
	 traceln("objectName = " + objectName, "getProxied");
	 trace("mBeanObjectName = " + mBeanObjectName, "getProxied");
	}

    return proxied; 
  }

  /**
  * Utility function to transfer command-line properties beginning with '-D' to 
  * system properties. This is a workaround for Visual Cafe's inability to accept 
  * system properties while debugging.
  * @param argv Command-line argument array passed to the <code>main</code> method.
  */  
  public static void parseSystemProperties(String[] argv)
  {
    String key;
    String value;
    int equalPos;
    for ( int i = 0; i < argv.length; i++ ) {
      if ( argv[i].startsWith("-D") ) {
        equalPos = argv[i].indexOf("=");
        key = equalPos != -1 ? argv[i].substring(2,equalPos) : argv[i].substring(2);
        value = equalPos != -1 && equalPos < argv[i].length() - 1 ? argv[i].substring(equalPos + 1) : "";
        System.getProperties().put( key, value );
      }
    }  
  }

  /**
  * Traces a message.
  * @param <VAR>message</VAR> message to trace.
  * @param <VAR>methodName</VAR> name of the method that generated the message.
  */
  protected void trace(String message, String methodName)
  {
    String name = this.getClass().getName();
    int idx = name.lastIndexOf('.');
    Debug.print(Debug.TRACE_DEBUG, name.substring(idx+1) + "::" + methodName + "[" + Thread.currentThread().getName() + "]: " + message);
  }

  /**
  * Traces a message and appends a carriage return.
  * @param <VAR>message</VAR> message to trace.
  * @param <VAR>methodName</VAR> name of the method that generated the message.
  */
  protected void traceln(String message, String methodName)
  {
    String name = this.getClass().getName();
    int idx = name.lastIndexOf('.');
    Debug.println(Debug.TRACE_DEBUG, name.substring(idx+1) + "::" + methodName + "[" + Thread.currentThread().getName() + "]: " + message);
  }

  /**
  * Sets the reinitialization callback object
  */
  public void setReinitCallback(java.lang.Object aCallback)
  { }

  /**
  * Clears the reinitialization callback object
  */
  public void clearReinitCallback()
  { }
}
