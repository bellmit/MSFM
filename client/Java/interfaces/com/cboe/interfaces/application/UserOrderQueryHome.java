package com.cboe.interfaces.application;

/**
 * This is the common interface for the Order Query Home
 * @author Thomas Lynch
 */
public interface UserOrderQueryHome {
    /** Name that will be used for this home.    */
    public final static String HOME_NAME = "UserOrderQueryHome";

  /**
   * Returns a reference to the order query service.
   *
   * @return reference to order query service
   * @author Thomas Lynch
   */
//  public OrderQuery find();

  /**
   * Creates an instance of the order query service.
   *
   * @return reference to order query service
   * @author Thomas Lynch
   */
  public OrderQueryV6 create(SessionManager session);
}
