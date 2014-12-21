package com.cboe.interfaces.application;

public interface ActivityServiceHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "ActivityServiceHome";
    

    /**
     * Creates an instance of the ActivityService.
     */
    public ActivityService create(SessionManager sessionManager);    
    public ActivityService find();
}
