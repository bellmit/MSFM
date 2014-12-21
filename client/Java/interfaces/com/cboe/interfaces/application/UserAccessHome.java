package com.cboe.interfaces.application;

/**
 * This is the common interface for the Market Query Home
 * @author Jeff Illian
 */
public interface UserAccessHome {
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "UserAccessHome";
  /**
   * Returns a reference to the market query service.
   *
   * @return reference to market query service
   *
   * @author Jeff Illian
   */
  public UserAccess find();
  /**
   * Creates an instance of the market query service.
   *
   * @return reference to market query service
   *
   * @author Jeff Illian
   */
  public UserAccess create();

  public String objectToString();
}
