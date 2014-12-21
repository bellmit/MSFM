package com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses;

import com.sun.jaw.reference.common.*;

/**
 * An "AttributeChange" event gets delivered whenever an attribute of an m-bean's feature
 * changes.
 */

public class AttributeChangeEvent extends java.util.EventObject {

  /**
    * @param source  The bean that fired the event.
    * @param propertyName  The programmatic name of the property
    *		that was changed.
    * @param oldValue  The old value of the property.
    * @param newValue  The new value of the property.
    */
  public AttributeChangeEvent(ObjectName source, 
                              String attributeName,
				                      Object oldValue, 
				                      Object newValue) {
    super(source);
    this.attributeName = attributeName;
    this.newValue = newValue;
    this.oldValue = oldValue;
  }

  /**
    * @return  The programmatic name of the property that was changed.
    *		May be null if multiple properties have changed.
    */
  public String getAttributeName() {
	  return attributeName;
  }
    
  /**
    * @return  The new value for the property, expressed as an Object.
    *		May be null if multiple properties have changed.
    */
  public Object getNewValue() {
  	return newValue;
  }

  /**
    * @return  The old value for the property, expressed as an Object.
    */
  public Object getOldValue() {
  	return oldValue;
  }

    private String attributeName;
    private Object newValue;
    private Object oldValue;

}
