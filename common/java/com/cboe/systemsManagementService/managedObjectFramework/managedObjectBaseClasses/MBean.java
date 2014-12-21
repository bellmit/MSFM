/* Copyright (c) 1999 The Chicago Board Options Exchange.  All Rights Reserved */

package com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.TooManyListenersException;

import com.sun.jaw.reference.common.InstanceAlreadyExistException;
import com.sun.jaw.reference.common.InstanceNotFoundException;
import com.sun.jaw.reference.common.ObjectName;

/**
  The MBean interface defines the operations supported by a generic MBean representing
  a managed resource. 
   
  @author Luis I. Benavides
  @version 1.0
*/
public interface MBean {

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
  
  /**
    * Returns the <i>object name</i> of this MBean. An <i>object name</i> is a globally 
    * unique identifier for the MBean within the System Management Service. It has the form:
    *
    * <p><i>DocumentName</i>:<i>MBeanType1</i>_<i>MBeanName1</i>. ... .<i>MBeanTypeN</i>_<i>MBeanNameN</i>
    *
    */
  public ObjectName getObjectName();

  /**
    Returns the value of a scalar or indexed property. In the case of an indexed property, the
    returned value will be an array of the appropriate type (e.g., String[], Integer[], Float[]).
    	  
    @param fullyQualifiedPropertyName Name of the property optionally prefixed by a path, relative to this 
      MBean, leading to an MBean down the hierarchy possessing the property. If the name is a single 
      token, it is interpreted as the name of a property in this MBean, otherwise, it is assumed
      that the prefix is a path leading to the MBean down the hierarchy possessing the property.
      The form of this parameter is:
      <p>
      <i>mBeanType1</i>(<i>mBeanName1_OR_Index1</i>).<i>mBeanType2</i>(<i>mBeanName2_OR_Index2</i>). ...
        <i>mBeanTypeN</i>(<i>mBeanNameN_OR_IndexN</i>).<i>propertyName</i>
      <p>where:
      <p>
        <i>mBeanTypeI</i> is the MBeanType of the I-th component MBean in the path. 
        <p><i>mBeanNameI_OR_IndexI</i> is either (a) the name of the I-th component MBean in the 
          path; or (b) the index of the I-th component in the list of components of type mBeanTypeI.
        <p><i>propertyName</i> is the simple name of the property.
    	  
    @exception InstanceNotFoundException The property does not exist or its value has not been set.
    
	*/
  public Object getPropertyValue(String fullyQualifiedPropertyName) 
    throws  InstanceNotFoundException;
  
  /**
	Returns the <code>index</code>-th element of the indexed property <code>fullyQualifiedPropertyName</code>.
    	  
    @param fullyQualifiedPropertyName Name of the property optionally prefixed by a path, relative to this 
      MBean, leading to an MBean down the hierarchy possessing the property. If the name is a single 
      token, it is interpreted as the name of a property in this MBean, otherwise, it is assumed
      that the prefix is a path leading to the MBean down the hierarchy possessing the property.
      The form of this parameter is:
      <p>
      <i>mBeanType1</i>(<i>mBeanName1_OR_Index1</i>).<i>mBeanType2</i>(<i>mBeanName2_OR_Index2</i>). ...
        <i>mBeanTypeN</i>(<i>mBeanNameN_OR_IndexN</i>).<i>propertyName</i>
      <p>where:
      <p>
        <i>mBeanTypeI</i> is the MBeanType of the I-th component MBean in the path. 
        <p><i>mBeanNameI_OR_IndexI</i> is either (a) the name of the I-th component MBean in the 
          path; or (b) the index of the I-th component in the list of components of type mBeanTypeI.
        <p><i>propertyName</i> is the simple name of the property.
    @param index index of the element
    	  
    @exception InstanceNotFoundException The property does not exist or is not indexed.
  */
  public Object getPropertyValue(String fullyQualifiedPropertyName, int index)
	  throws  InstanceNotFoundException;
	          
  /**
  	Returns the values of all the properties of the MBean.
    
    @return a hashtable whose keys are the names of the MBean's properties and elements 
    the values of their corresponding properties
  */
  public Hashtable getPropertyValues();
	          
  /**
	Sets the value of the <code>index</code>-th element of indexed property 
	<code>fullyQualifiedPropertyName</code>.

    @param fullyQualifiedPropertyName Name of the property optionally prefixed by a path, relative to this 
      MBean, leading to an MBean down the hierarchy possessing the property. If the name is a single 
      token, it is interpreted as the name of a property in this MBean, otherwise, it is assumed
      that the prefix is a path leading to the MBean down the hierarchy possessing the property.
      The form of this parameter is:
      <p>
      <i>mBeanType1</i>(<i>mBeanName1_OR_Index1</i>).<i>mBeanType2</i>(<i>mBeanName2_OR_Index2</i>). ...
        <i>mBeanTypeN</i>(<i>mBeanNameN_OR_IndexN</i>).<i>propertyName</i>
      <p>where:
      <p>
        <i>mBeanTypeI</i> is the MBeanType of the I-th component MBean in the path. 
        <p><i>mBeanNameI_OR_IndexI</i> is either (a) the name of the I-th component MBean in the 
          path; or (b) the index of the I-th component in the list of components of type mBeanTypeI.
        <p><i>propertyName</i> is the simple name of the property.
    @param index index of the element to set.
    @param newValue The new value.
    	
    @exception InstanceNotFoundException The property was not found.
    @exception IllegalAccessException The property is not writeable or this 
      mutator cannot set a scalar property value.
    @exception MBeanPropertyVetoException The property change has been vetoed.
    @exception IllegalArgumentException Cannot set property value to a non-assignable type.

	*/
  public void setPropertyValue(String fullyQualifiedPropertyName, int index, Object newValue)
			  throws  InstanceNotFoundException,
	              IllegalAccessException, 
	              MBeanPropertyVetoException,
	              IllegalArgumentException;
  /**
    Sets all the elements of an indexed property.
    	  
    @param fullyQualifiedPropertyName Name of the property optionally prefixed by a path, relative to this 
      MBean, leading to an MBean down the hierarchy possessing the property. If the name is a single 
      token, it is interpreted as the name of a property in this MBean, otherwise, it is assumed
      that the prefix is a path leading to the MBean down the hierarchy possessing the property.
      The form of this parameter is:
      <p>
      <i>mBeanType1</i>(<i>mBeanName1_OR_Index1</i>).<i>mBeanType2</i>(<i>mBeanName2_OR_Index2</i>). ...
        <i>mBeanTypeN</i>(<i>mBeanNameN_OR_IndexN</i>).<i>propertyName</i>
      <p>where:
      <p>
        <i>mBeanTypeI</i> is the MBeanType of the I-th component MBean in the path. 
        <p><i>mBeanNameI_OR_IndexI</i> is either (a) the name of the I-th component MBean in the 
          path; or (b) the index of the I-th component in the list of components of type mBeanTypeI.
        <p><i>propertyName</i> is the simple name of the property.
    @param newValue Array of new property values
    	  
    @exception InstanceNotFoundException The property was not found.
    @exception IllegalAccessException The property is not writeable or this 
      mutator cannot set a scalar property value.
    @exception MBeanPropertyVetoException The property change has been vetoed.
    @exception IllegalArgumentException Cannot set property value to a non-assignable type.
  */
  public void setPropertyValue(String fullyQualifiedPropertyName, Object[] newValue)
	throws  InstanceNotFoundException,
	        IllegalAccessException, 
	        MBeanPropertyVetoException,
	        IllegalArgumentException;
	  
  /**
  	Sets the value of a scalar property. If <code>fullyQualifiedPropertyName</code points 
  	to more than one component, the property is set in all these components.
  	
    @param fullyQualifiedPropertyName Name of the property optionally prefixed by a path, 
      relative to this MBean, leading to an MBean down the hierarchy possessing the property. 
      If the name is a single token, it is interpreted as the name of a property in this MBean, 
      otherwise, it is assumed that the prefix is a path leading to the MBean down the 
      hierarchy possessing the property.
      
      <p>The form of this parameter is:
      <p>
      <i>mBeanType1</i>(<i>mBeanName1_OR_Index1</i>).<i>mBeanType2</i>(<i>mBeanName2_OR_Index2</i>). ...
        <i>mBeanTypeN</i>(<i>mBeanNameN_OR_IndexN</i>).<i>propertyName</i>
      <p>where:
      <p>
        <i>mBeanTypeI</i> is the MBeanType of the I-th component MBean in the path. 
        <p><i>mBeanNameI_OR_IndexI</i> is either (a) the name of the I-th component MBean in the 
          path; or (b) the index of the I-th component in the list of components of type mBeanTypeI.
        <p><i>propertyName</i> is the simple name of the property.
  	@param newValue New value of the property. Must be a <code>String</code>, if the property 
  	  is type STRING or ENUM; an <code>Integer</code>, if the property is type INTEGER; or, a 
  	  <code>Float</code>, if the property is type FLOAT.
	  
    @exception InstanceNotFoundException The property was not found.
    @exception IllegalAccessException The property is not writeable or this 
      mutator cannot set an individual element of an indexed property.
    @exception MBeanPropertyVetoException The property change has been vetoed.
    @exception IllegalArgumentException Cannot set property value to a non-assignable type or 
      new value for enum property is not in the list of valid values.
  */
  public void setPropertyValue(String fullyQualifiedPropertyName, Object newValue) 
	  throws  InstanceNotFoundException,
	          IllegalAccessException, 
	          MBeanPropertyVetoException,
	          IllegalArgumentException;

  /**
	  Adds a property change listener for all the properties of this MBean.
  */
  public void addMBeanPropertyChangeListener(
    MBeanPropertyChangeListener mBeanPropertyChangeListener);

  /**
	  Removes a property change listener for all the properties of this MBean.
  */
  public void removeMBeanPropertyChangeListener(
    MBeanPropertyChangeListener mBeanPropertyChangeListener);

  /**
	  Adds a property change listener for all the properties of this MBean with the 
	  right to veto the change. At most one listener can be registered for 
	  vetoing property changes.
  */
  public void addMBeanVetoableChangeListener(
    MBeanVetoableChangeListener mBeanVetoableChangeListener);

  /**
	  Removes the (unique) property change listener for all the properties of this MBean 
	  with the right to veto the change.
	*/
  public void removeMBeanVetoableChangeListener(
    MBeanVetoableChangeListener mBeanVetoableChangeListener);

  /**
    Invokes the method <code>methodName</code> with <code>parameters</code>
  
    @param fullyQualifiedMethodName Name of the method optionally prefixed by a path, relative to this 
      MBean, leading to an MBean down the hierarchy possessing the method. If the name is a single 
      token, it is interpreted as the name of a method in this MBean, otherwise, it is assumed
      that the prefix is a path leading to the MBean down the hierarchy possessing the method.
      The form of this parameter is:
      <p>
      <i>mBeanType1</i>(<i>mBeanName1_OR_Index1</i>).<i>mBeanType2</i>(<i>mBeanName2_OR_Index2</i>). ...
        <i>mBeanTypeN</i>(<i>mBeanNameN_OR_IndexN</i>).<i>methodName</i>
      <p>where:
      <p>
        <i>mBeanTypeI</i> is the MBeanType of the I-th component MBean in the path. 
        <p><i>mBeanNameI_OR_IndexI</i> is either (a) the name of the I-th component MBean in the 
          path; or (b) the index of the I-th component in the list of components of type mBeanTypeI.
        <p><i>methodName</i> is the simple name of the method.
    @param parameters Parameters passed on the invocation.
  
    @exception InvalidStateTransitionException This action cannot be
      performed in the current state.
    @exception InstanceNotFoundException The method either does not exist.
    @exception InvocationTargetException exception wrapping an exception returned by the 
      registered method invocation listener executing the method
  */
  public void invokeMethod(String fullyQualifiedMethodName, Object[] parameters) 
	throws  InvalidStateTransitionException,
            InstanceNotFoundException,
            InvocationTargetException;

  /**
    Adds a method invocation listener. At the most only one listener can be register 
    at any time.
    
    @param methodName The name of the method.
    @param methodInvocationListener The listener.
    
    @exception TooManyListenersException When a listener is already registered.
    @exception InstanceNotFoundException The method does not exist.
  */
  public void addMethodInvocationListener(String methodName, MethodInvocationListener methodInvocationListener) 
	throws  TooManyListenersException,
	        InstanceNotFoundException;
	  
  /**
    Returns <code>true</code> if a method invocation listener is currently registered.

    @param methodName The name of the method.

    @exception InstanceNotFoundException The method does not exist.
  */
  public boolean isMethodInvocationListenerRegistered(String methodName)
	throws  InstanceNotFoundException;
    
  /**
	Removes the unique currently registered <code>methodInvocationListener</code>, even if the 
	one passed as a parameter is distinct.

    @param methodName The name of the method.
    
    @exception InstanceNotFoundException The method does not exist.
  */
  public void removeMethodInvocationListener(String methodName, MethodInvocationListener methodInvocationListener)
	throws InstanceNotFoundException;

  /**
	  Adds a component to this MBean.
  	
    @param fullyQualifiedMBeanName Name of the MBean to add, optionally prefixed by a path, 
      relative to this MBean, leading to the location down the hierarchy where the 
      MBean is to be added. 

      <p>The form of this parameter is:
      <p>
      <i>mBeanType1</i>(<i>mBeanName1_OR_Index1</i>).<i>mBeanType2</i>(<i>mBeanName2_OR_Index2</i>). ...
        <i>mBeanTypeN</i>(<i>mBeanNameN_OR_IndexN</i>)
      <p>where:
      <p>
        <i>mBeanTypeI</i> is the MBeanType of the I-th component MBean in the path. 
        <p><i>mBeanNameI_OR_IndexI</i> is either:
        
        <p>(a) the name of the I-th component MBean in the path. This name must be unique
          among the components of the same type.
        <p>(b) the index of the I-th component in the list 
          of components of type <code>mBeanTypeI</code>, or -1 for the last component in the list.

	  @return The MBean just added.

	  @exception InstanceAlreadyExistException An MBean with that name is already 
	    a component of this MBean.
	  @exception IllegalArgumentException The MBean cannot have components of 
	    type <code>mBeanTypeN</code> or <code>fullyQualifiedMBeanName cannot be parsed.
    @exception ArrayIndexOutOfBoundsException IndexI is less than -1 or larger than n-1,
      where n is the number of current ordered components of type 
      <code>mBeanTypeI</code>.
  */
  public MBean addComponent(String fullyQualifiedMBeanName)
	  throws  InstanceAlreadyExistException,
	          IllegalArgumentException,
	          ArrayIndexOutOfBoundsException;

  /**
	Removes immediate or descendant components of this MBean.
	
    @param fullyQualifiedMBeanName Name of the MBean(s) to remove, optionally prefixed by a path, 
      relative to this MBean(s), leading to the location down the hierarchy where the 
      MBean(s) is to be removed.

      <p>The form of this parameter is:
      <p>
      <i>mBeanType1</i>(<i>mBeanName1_OR_Index1</i>).<i>mBeanType2</i>(<i>mBeanName2_OR_Index2</i>). ...
        <i>mBeanTypeN</i>(<i>mBeanNameN_OR_IndexN</i>)
      <p>where:
      <p>
        <i>mBeanTypeI</i> is the MBeanType of the I-th component MBean in the path. 
        <p><i>mBeanNameI_OR_IndexI</i> is either:
        
        <p>(a) the name of the I-th component MBean in the path. This name must be unique
          among the components of the same type.
        <p>(b) the index of the I-th component in the list 
          of components of type <code>mBeanTypeI</code>, or -1 for the last component in the list.
        <p>(c) * to indicate to remove all the components of type <code>mBeanTypeN</code>.
	
	@exception InstanceNotFoundException The MBean is not a component of this MBean
  */
  public void removeComponents(String fullyQualifiedMBeanName)
	  throws  InstanceNotFoundException;
   
  /**
    Swaps the MBean components in the <code>index1</code> and <code>index2</code> positions.
    
    @param index1 Position of the first MBean component in the list of components of type 
      <code>mBeanType</code>.
    @param index2 Position of the second MBean component in the list of components of type 
      <code>mBeanType</code>.
    @param mBeanType The type of components.  

    @exception InstanceNotFoundException This MBean has no components of type <code>mBeanType</code>.
    @exception ArrayIndexOutOfBoundsException one or both indeces are negative or 
      larger than n-1, where n is the number of current ordered components of type 
      <code>mBeanType</code>.
*/  
  public void swapComponents(int index1, int index2, String mBeanType)
	  throws  InstanceNotFoundException,
	          ArrayIndexOutOfBoundsException;
  
  /**
    Returns immediate or descendant components of this MBean.
    
    @param fullyQualifiedMBeanName Name or index of the component to return, optionally 
      prefixed by a path, relative to this MBean, leading down the hierarchy to the 
      requested components.

      <p>The form of this parameter is:
      <p>
      <i>mBeanType1</i>(<i>mBeanName1_OR_Index1_OR_*</i>).<i>mBeanType2</i>(<i>mBeanName2_OR_Index2_OR_*</i>). ...
        <i>mBeanTypeN</i>(<i>mBeanNameN_OR_IndexN_OR_*</i>)
      <p>where:
      <p>
      <i>mBeanTypeI</i> is the MBeanType of the I-th component MBean in the path. 

      <p><i>mBeanNameI_OR_IndexI_OR_*</i> is either:
        
      <p>(a) the name of the I-th component MBean in the path. This name must be unique
        among the components of the same type.
      <p>(b) the index of the I-th component in the list 
        of components of type <code>mBeanTypeI</code>, or -1 for the last component in the 
        list.
      <p>(c) *, an empty string, or omission of the parentheses altogether indicate to return 
      all the components of type <code>mBeanTypeI</code>.
	
    @return the immediate or descendant components of this MBean satisfying the conditions imposed by
      the <code>fullyQualifiedMBeanName</code>.

    @exception InstanceNotFoundException No MBeans satisfy the conditions imposed by
      the fullyQualifiedMBeanName.
  */
  public MBean[] getComponents(String fullyQualifiedMBeanName)
    throws InstanceNotFoundException;
  
  /**
    * Returns the immediate components of this MBean of all types.
    *
    * @return The immediate component of this MBean of all types.
    */
  public MBean[] getComponents();
  
  /**
    Returns the number of components of type <code>MBeanType</code> of this MBean.
    
    @param mBeanType The type of the component MBeans.
    
    @return The number of components of type <code>MBeanType</code> of this MBean.

    @exception InstanceNotFoundException If the MBean does not have components of 
    type <code>mBeanType</code>.
  */
  public int getComponentOfTypeCount(String mBeanType)
      throws InstanceNotFoundException;
      
  /**
    Returns the composite (e.g., parent) of this MBean.
    
  */  
  public MBean getComposite();
  
  /**
    * Relates the MBean with object name <code>relatedMBeanObjectName/code> 
    * to this MBean under the relation <code>relationName</code>.
    *
    * @exception InstanceNotFoundException If no MBean with object name 
    * <code>relatedMBeanObjectName/code> exists in the repository.
    *
    * @exception IllegalArgumentException If this MBean cannot have relations named 
    * <code>relationName</code>.
    *
    * @pre relationName != null
    * @pre relatedMBeanObjectName != null
    */ 
  public void addRelatedMBean(String relationName, ObjectName relatedMBeanObjectName)
      throws  InstanceNotFoundException, 
              IllegalArgumentException;
    
  /**
    * Removes relation <code>relationName</code> to the MBean whose object name 
    * is <code>relatedMBeanObjectName/code>.
    *
    * @exception InstanceNotFoundException If the MBean does not have a related MBean with that
    * <code>relatedMBeanObjectName</code>.
    *
    * @exception IllegalArgumentException If this MBean does not have relations named 
    * <code>relationName</code>.
    *
    * @pre relationName != null
    * @pre relatedMBeanObjectName != null
    */  
  public void removeRelatedMBean(String relationName, ObjectName relatedMBeanObjectName) 
      throws  InstanceNotFoundException, 
              IllegalArgumentException;

  /**
    * Returns an array with the MBeans related to this MBean under the relation 
    * <code>relationName</code>. If this MBean may have <code>relationName</code> 
    * relations, but currently has no related MBeans, this method returns a zero-length array.
    *
    * @exception IllegalArgumentException If this MBean cannot have relations named 
    * <code>relationName</code>.
    *
    * @pre relationName != null
    */  
  public MBean[] getRelatedMBeans(String relationName)
      throws  IllegalArgumentException;
    
  /**
    * Returns an array with the MBeans related to this MBean under all relations, including 
    * composition defined in the Resource Spec XML document.
    * If this MBean may have <code>relationName</code> 
    * relations, but currently has no related MBeans, this method returns a zero-length array.
    *
    */  
  public MBean[] getRelatedMBeans();

  /**
    * Returns an array with the ObjectNames related to this MBean under the relation
    * <code>relationName</code>. If this MBean may have <code>relationName</code>
    * relations, but currently has no related MBeans, this method returns a zero-length array.
    *
    * @exception IllegalArgumentException If this MBean cannot have relations named
    * <code>relationName</code>.
    *
    * @pre relationName != null
    */
  public ObjectName[] getRelatedObjectNames(String relationName)
      throws  IllegalArgumentException;

  /**
    * Returns an array with the ObjectNames related to this MBean under all relations, including
    * composition defined in the Resource Spec XML document.
    * If this MBean may have <code>relationName</code>
    * relations, but currently has no related MBeans, this method returns a zero-length array.
    *
    */
  public ObjectName[] getRelatedObjectNames();

  /**
    * Returns an array with the object names of the MBeans related to this MBean under the relation 
    * <code>relationName</code>. If this MBean may have <code>relationName</code> 
    * relations, but currently has no related MBeans, this method returns a zero-length array.
    *
    * @exception IllegalArgumentException If this MBean cannot have relations named 
    * <code>relationName</code>.
    *
    * @pre relationName != null
    */  
  public ObjectName[] getObjectNamesOfRelatedMBeans(String relationName)
      throws  IllegalArgumentException;
  
  /*
	Adds a tree change listener to this MBean. A tree change listener is notified when 
	a component is added to or removed from the MBean.
  */  
  public void addTreeChangeListener(TreeChangeListener treeChangeListener);

  /**
    Removes a tree change listener from the MBean.
  */
  public void removeTreeChangeListener(TreeChangeListener treeChangeListener);
  
  /**
    * Returns a map of immutable attribute name/value pairs for retrieval from a remote proxy
    */
  public HashMap getAttributes();

  /**
    * Returns a map of object names to maps containing name/value pairs of the immutable attributes of this
    * MBean and its descriptors. The purpose of this method is to allow remote proxies retrieve all the MBean's 
    * immutable attributes in just one remote call, thereby saving remote calls.
    */
  public HashMap getAllAttributes();

  /**
    MBean action that enables the invocation of any public method in a subclass.
    
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
    
}
