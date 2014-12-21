package com.cboe.interfaces.application;

/**
 * This is the common interface for the Order entry Home
 * @author Thomas Lynch
 */
public interface UserOrderEntryHome {
    /** Name that will be used for this home.    */
    public final static String HOME_NAME = "UserOrderEntryHome";

  /**
   * Creates an instance of the order query service.
   *
   * @return reference to order query service
   */
  public OrderEntryV9 create(SessionManager session); 
}
