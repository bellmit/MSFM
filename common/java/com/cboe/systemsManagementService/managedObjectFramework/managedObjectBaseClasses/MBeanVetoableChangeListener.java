package com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses;

/**
 * A VetoableChange event gets fired whenever an m-bean changes a "constrained"
 * property.  You can register a MBeanVetoableChangeListener with a source bean
 * so as to be notified of any constrained property updates.
 */
public interface MBeanVetoableChangeListener extends java.util.EventListener {

  /**
    * This method gets called when a constrained property is changed.
    *
    * @param evt a <code>MBeanPropertyChangeEvent</code> object describing the
    *   	      event source and the property that has changed.
    * @exception MBeanPropertyVetoException if the recipient wishes the property
    *              change to be rolled back.
    */
  public void vetoableChange(MBeanPropertyChangeEvent evt) throws MBeanPropertyVetoException;

}
