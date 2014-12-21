package com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses;
 
/**
 * A PropertyVetoException is thrown when a proposed change to an m-bean
 * property represents an unacceptable value.
 */

public class MBeanPropertyVetoException extends Exception {

  /**
    * @param mess Descriptive message
    * @param evt A PropertyChangeEvent describing the vetoed change.
    */
  public MBeanPropertyVetoException (String mess, MBeanPropertyChangeEvent evt) {
    super(mess);
    this.evt = evt;	
  }

  public MBeanPropertyChangeEvent getMBeanPropertyChangeEvent() {
    return evt;
  }

  private MBeanPropertyChangeEvent evt;

}
