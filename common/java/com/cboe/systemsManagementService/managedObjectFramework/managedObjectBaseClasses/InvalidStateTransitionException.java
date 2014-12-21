package com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses;


/**
 * Exception thrown when attempting to transition a m-bean to an 
 * invalid state.
 * 
 * @author Luis Benavides
 * @version 1.0
 */

public class InvalidStateTransitionException extends java.lang.Exception {

  /**
   * Deafult constructor
   */
	
  public InvalidStateTransitionException() {
    super();
  }  

  /**
   * Constructor accepting a detailed message.
   * 
   * @param detailMessage Detailed message.
   */
  
  public InvalidStateTransitionException(String detailMessage) {
    super(detailMessage);
  }
  
}

