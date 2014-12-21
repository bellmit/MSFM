/*
 * Created by IntelliJ IDEA.
 * User: huange
 * Date: Sep 19, 2002
 * Time: 9:31:46 AM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.interfaces.application;

/**
 * This is the common interface for the Intermarket Query Home
 * @author Emily Huang
 */
public interface IntermarketQueryHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "IntermarketQueryHome";
  /**
   * Creates an instance of the intermarket query service.
   *
   * @return reference to intermarket query service
   *
   * @author Emily Huang
   */
  public IntermarketQuery create(SessionManager sessionManager);

}
