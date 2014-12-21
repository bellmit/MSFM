package com.cboe.interfaces.domain.userLoadManager;

/**
 * This is the common interface for the UserLoadManagementServiceHome
 * @author David De La Vega
 * @author alin
 */
public interface UserLoadManagerHome 
{
	/**
     * Name that will be used for this home.
     */
	public final static String HOME_NAME = "UserLoadManagerHome";
	
	/**
	   * Returns a reference to the UserLoadManager Home.
	   *
	   * @return reference to UserLoadManager home
	   *
	   * @author David De La Vega
	   */
	public UserLoadManager find();
	
	/**
    * Creates an instance of the UserManager
    * 
    * @return reference to UserLoadManager home
	*
	* @author David De La Vega
    */
	public UserLoadManager create();
}
