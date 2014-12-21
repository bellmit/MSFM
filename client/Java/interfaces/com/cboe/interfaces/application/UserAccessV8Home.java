package com.cboe.interfaces.application;

/**
 * @author Arun Ramachandran Nov 12, 2009
 *
 */
public interface UserAccessV8Home
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "UserAccessV8Home";

    /**
     * Finds an instance of UserAccessV8.
     *
     * @return reference to UserAccessV8
     */
    public com.cboe.interfaces.application.UserAccessV8 find();

    /**
     * Creates an instance of UserAccessV8.
     *
     * @return reference to UserAccessV8
     */
    public com.cboe.interfaces.application.UserAccessV8 create();

    /**
     * Return a stringfied IOR for UserAccessV7 CORBA object.
     * @return stringfied IOR for UserAccessV7 CORBA object
     */
    public String objectToString();
}
