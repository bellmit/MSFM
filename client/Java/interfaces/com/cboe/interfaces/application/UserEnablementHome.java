/*
 * Created by IntelliJ IDEA.
 * User: HUANGE
 * Date: Mar 1, 2002
 * Time: 4:20:06 PM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.interfaces.application;

public interface UserEnablementHome {

    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "UserEnablementHome";


  /**
   * Returns a reference to the user enalblement service.
   *
   * @return reference to UserEnablement home
   *
   * @author Emily Huang
   */
// FIXME - KAK may want to support still  public UserEnablement find(String userId);
    public UserEnablement find(String userId, String exchange, String acronym);

    /**
     * Creates an instance of the user enablement service.
     *
     * @return reference to UserEnablemetn home
     *
     * @author Emily Huang
     */
    public UserEnablement create(String userId, String exchange, String acronym);

// FIXME - KAK may want to support still    public void remove(String userId);
    public void remove(String userId, String exchange, String acronym);


    /**
     * Returns the user enablement object for the specified user.
     *
     * @param userId  
     * 
     * @return UserEnablement for the userId supplied if exists; else returns null.
     *
     * @author Gijo Joseph
     */
    public UserEnablement getUserEnablement(String userId);
}

