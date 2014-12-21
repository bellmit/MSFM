package com.cboe.interfaces.application;

/**
 * This is the common interface for the Intermarket UserAccess Home
 * @author Emily Huang
 */
public interface IntermarketUserAccessHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "IntermarketUserAccessHome";
  /**
   * Returns a reference to IntermarketUserAccess .
   *
   * @return reference to IntermarketUserAccess
   *
   * @author Emily Huang
   */
  public IntermarketUserAccess find();
  /**
   * Creates an instance of the IntermarketUserAccess.
   *
   * @return reference to IntermarketUserAccess
   *
   * @author Emily Huang
   */
  public IntermarketUserAccess create();

  public String objectToString();
}
