package com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses;

/**
 * An "AttributeChange" event gets fired whenever a there is a change to an
 * m-beans's feature's attribute
 */

public interface AttributeChangeListener extends java.util.EventListener {

    /**
     * This method gets called when an m-bean's feature's attribute is changed.
     * @param attributeChangeEvent A AttributeChangeEvent object describing the event source 
     *   	and the feature's attrobute that has changed.
     */

    void attributeChange(AttributeChangeEvent AttributeChangeEvent);

}
