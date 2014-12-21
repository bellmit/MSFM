package com.cboe.interfaces.application;

public interface ParOrderManagementServiceHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "ParOrderManagementServiceHome";

    /**
     * Creates an instance of the ParOrderManagementService.
     */
    public ParOrderManagementService create(SessionManager sessionManager);
    
    public ParOrderManagementService find();
}
