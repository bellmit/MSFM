/* Copyright (c) 2000 The Chicago Board Options Exchange.  All Rights Reserved */

package com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses;

import java.util.TooManyListenersException;

/**
  Defines the operations supported by an MBean property descriptor.
*/
public interface MBeanPropertyDescriptor extends MBeanFeatureDescriptor {
	
  /**
    Constant for the possible values for the property's "mode" attribute.
  */  
  public static final String READ_ONLY = "readOnly";
  
  /**
    Constant for the possibel values for the property's "mode" attribute.
  */  
  public static final String READ_WRITE = "readWrite";
  
  /**
    Constant for the possibel values for the property's "mode" attribute.
  */  
  public static final String WRITE_ONLY = "writeOnly";

  public static final String STRING = "string";
  
  public static final String INTEGER = "integer";
  
  public static final String FLOAT = "float";
  
  public static final String ENUM = "enum";
  
  /**
	Returns the type of the property. Possible values are: "string", "integer", "float", 
	and "enum".
  */
  public String getPropertyType();

  /**
	  Returns <code>true</code> if the property value is persisted with the MBean; 
	  <code>false</code> if the property value is discarded before the MBean is persisted.
  */
  public boolean isPersistent();
  	
  /**
    Returns the default value for a non-persistent property. If the property is indexed, 
    it returns an array.
  */
  public Object getDefaultValue();
  
  /**
    Sets the default value of a non-persistent property. This method will be implemented only
    if the need to set default values at runtime arises. Currently, default values must be specified 
    in the Resource Instance XML document.
  */
  public void setDefaultValue(Object defaultValue);
  
  /**
    Returns the possible values of a property of type "enum".
  */
  public String[] getEnumerationValues();
  
  /**
    Returns the access constraints to the property value. Possible values are the following 
    static constants: READ_ONLY, READ_WRITE, WRITE_ONLY.
  */
  public String getMode();
  
  /**
    Indicates whether a value for the property is optional or required.
  */
  public boolean isOptional();
  
  /**
    Indicates whether the property has multiple, indexed values.
  */
  public boolean isIndexed();  
  
  /**
    * If not null, specifies that this property is aggregated. This method returns
    * the name of the property that is aggregated. Such property must be defined in the 
    * MBeans related to this MBean via the relations returned by the
    * <code>getAggregateRelations()</code> method.
    *
    * @return Name of the aggregate property or null if this property is not aggregated.
    */
  public String getAggregateProperty();
  
  /**
    * Returns an array with the names of the relations grouping the MBeans whose 
    * property will be aggregated. If the attribute <code>aggregateRelations</code> 
    * in the property's element declaration is omitted and this property is 
    * aggregated, it returns the names of all the relations of this MBean.
    *
    * @return Relations that group the MBeans to aggregate or null if this is not an aggregated
    * property.
    */
  public String[] getAggregateRelations();
  
  /**
    * If <code>getAggregateProperty()</code> returned a non-null, this method returns the
    * fully-qualified name of a class implementing the aggregation algorithm used
    * to calculate the value of the aggregated property. Such class must implement the 
    * com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses.Aggregator
    * interface. It must provide the static method <code>aggregate</code>, which is 
    * invoked by the MBean Framework every time the aggregated property needs to be 
    * re-calculated.
    *
    * @return fully-qualified class name of the aggregation class.
    */
  public String getAggregator();
  
  /**
    * Returns the interval in milliseconds between re-recalculations of the aggregated property 
    * value of MBeans related by a dynamic relation to this MBean. It defaults to 60,000 
    * milliseconds (60 seconds).
    *
    * @return aggregate re-calculation interval in milliseconds.
    */
  public int getRefreshInterval();
  
  /**
  	Adds a property change listener for this property. 	  
  */
  public void addMBeanPropertyChangeListener(MBeanPropertyChangeListener mBeanPropertyChangeListener);
  	  
  /**
	  Removes a property change listener.
  */
  public void removeMBeanPropertyChangeListener(MBeanPropertyChangeListener mBeanPropertyChangeListener);

  /**
	  Adds a property change listener for this property with the 
	  right to veto the change. At most one listener can be registered for 
	  vetoing property changes. Attempts to register additional listeners are ignored.
  */
  public void addMBeanVetoableChangeListener(MBeanVetoableChangeListener mBeanMBeanVetoableChangeListener) 
      throws  TooManyListenersException;
	  
  /**
	  Removes the (unique) vetoable change listener. The value passed in <code>mBeanVetoableChangeListener</code>
	  is ignored.
  */
  public void removeMBeanVetoableChangeListener(MBeanVetoableChangeListener mBeanVetoableChangeListener);
	  
  /**
    Returns <code>true</code> if there is a vetoable change listener currently registered.
  */
  public boolean isMBeanVetoableChangeListenerRegistered();
}

