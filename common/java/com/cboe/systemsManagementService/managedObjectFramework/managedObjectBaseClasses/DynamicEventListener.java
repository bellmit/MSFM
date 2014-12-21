package com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses;

/**
 * An "AttributeChange" event gets fired whenever a there is a change to an
 * m-beans's feature's attribute
 */

public interface DynamicEventListener extends java.util.EventListener {
//public abstract class DynamicEventListener implements java.util.EventListener {
    
  abstract public void dynamicEventOccurred(DynamicEvent dynamicEvent);

}
