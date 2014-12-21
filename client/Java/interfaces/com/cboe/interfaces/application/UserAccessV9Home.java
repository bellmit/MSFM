package com.cboe.interfaces.application;

/**
 * @author Piyush Patel June 23, 2010
 *
 */
public interface UserAccessV9Home
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "UserAccessV9Home";

    /**
     * Finds an instance of UserAccessV9.
     *
     * @return reference to UserAccessV9
     */
    public UserAccessV9 find();

    /**
     * Creates an instance of UserAccessV9.
     *
     * @return reference to UserAccessV9
     */
    public UserAccessV9 create();

    /**
     * Return a stringfied IOR for UserAccessV9 CORBA object.
     * @return stringfied IOR for UserAccessV9 CORBA object
     */
    public String objectToString();
}