package com.cboe.interfaces.application;
/*
 * Home interface for UserAccessV5
 */
public interface UserAccessV5Home {
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "UserAccessV5Home";

    /**
     * Finds an instance of UserAccessV5.
     *
     * @return reference to UserAccessV5
     */
    public UserAccessV5 find();

    /**
     * Creates an instance of UserAccessV5.
     *
     * @return reference to UserAccessV5
     */
    public UserAccessV5 create();

    /**
     * Return a stringfied IOR for UserAccessV5 CORBA object.
     * @return
     */
    public String objectToString();
}
