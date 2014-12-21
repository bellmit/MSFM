package com.cboe.interfaces.application;
/*
 * Home interface for UserAccessTMS
 */
public interface UserAccessTMSHome {
	/**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "UserAccessTMSHome";
    
    /**
     * Finds an instance of UserAccessTMS.
     *
     * @return reference to UserAccessTMS
     */
    public UserAccessTMS find();
    
    /**
     * Creates an instance of UserAccessTMS.
     *
     * @return reference to UserAccessTMS
     */
    public UserAccessTMS create();
    
    /**
     * Return a stringfied IOR for UserAccessTMS CORBA object.
     * @return
     */
    public String objectToString();

}
