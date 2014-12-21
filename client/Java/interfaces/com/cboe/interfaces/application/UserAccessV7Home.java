package com.cboe.interfaces.application;

public interface UserAccessV7Home
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "UserAccessV7Home";

    /**
     * Finds an instance of UserAccessV7.
     *
     * @return reference to UserAccessV7
     */
    public com.cboe.interfaces.application.UserAccessV7 find();

    /**
     * Creates an instance of UserAccessV7.
     *
     * @return reference to UserAccessV7
     */
    public com.cboe.interfaces.application.UserAccessV7 create();

    /**
     * Return a stringfied IOR for UserAccessV7 CORBA object.
     * @return stringfied IOR for UserAccessV7 CORBA object
     */
    public String objectToString();
}
