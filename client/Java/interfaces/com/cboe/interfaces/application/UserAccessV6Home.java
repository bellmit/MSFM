package com.cboe.interfaces.application;
/*
 * Home interface for UserAccessV6
 */
public interface UserAccessV6Home {
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "UserAccessV6Home";

    /**
     * Finds an instance of UserAccessV6.
     *
     * @return reference to UserAccessV6
     */
    public UserAccessV6 find();

    /**
     * Creates an instance of UserAccessV6.
     *
     * @return reference to UserAccessV6
     */
    public UserAccessV6 create();

    /**
     * Return a stringfied IOR for UserAccessV6 CORBA object.
     * @return stringfied IOR for UserAccessV6 CORBA object
     */
    public String objectToString();
}
