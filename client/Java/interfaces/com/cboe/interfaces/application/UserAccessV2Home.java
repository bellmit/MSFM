/*
 * Created by IntelliJ IDEA.
 * User: huange
 * Date: Feb 28, 2003
 * Time: 10:25:56 AM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.interfaces.application;


public interface UserAccessV2Home
{
     /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "UserAccessV2Home";
  /**
   * Returns a reference to the market query service.
   *
   * @return reference to market query service
   *
   * @author Jeff Illian
   */
  public UserAccessV2 find();
  /**
   * Creates an instance of the market query service.
   *
   * @return reference to market query service
   *
   * @author Jeff Illian
   */
  public com.cboe.interfaces.application.UserAccessV2 create();

  public String objectToString();
}
