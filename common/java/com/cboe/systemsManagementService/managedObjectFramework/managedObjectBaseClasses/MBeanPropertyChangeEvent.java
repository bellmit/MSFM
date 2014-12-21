package com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses;

/**
 * A "PropertyChange" event gets delivered whenever a bean changes a "bound"
 * or "constrained" property.  A PropertyChangeEvent object is sent as an
 * argument to the PropertyChangeListener and VetoableChangeListener methods.
 * <P>
 * Normally PropertyChangeEvents are accompanied by the name and the old
 * and new value of the changed property.  If the new value is a builtin
 * type (such as int or boolean) it must be wrapped as the 
 * corresponding java.lang.* Object type (such as Integer or Boolean).
 * <P>
 * Null values may be provided for the old and the new values if their
 * true values are not known.
 * <P>
 * An event source may send a null object as the name to indicate that an
 * arbitrary set of if its properties have changed.  In this case the
 * old and new values should also be null.
 */

public class MBeanPropertyChangeEvent extends java.util.EventObject {

    /**
     * @param source  The bean that fired the event.
     * @param propertyName  The programmatic name of the property
     *		that was changed.
     * @param oldValue  The old value of the property.
     * @param newValue  The new value of the property.
     */
    public MBeanPropertyChangeEvent(Object source, String propertyName,
				     Object oldValue, Object newValue) {
	super(source);
	this.propertyName = propertyName;
	this.newValue = newValue;
	this.oldValue = oldValue;
    }

    /**
     * @return  The programmatic name of the property that was changed.
     *		May be null if multiple properties have changed.
     */
    public String getPropertyName() {
	return propertyName;
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
     *		May be null if multiple properties have changed.
     */
    public Object getOldValue() {
	return oldValue;
    }

    private String propertyName;
    private Object newValue;
    private Object oldValue;

	//{{DECLARE_CONTROLS
	//}}
}
