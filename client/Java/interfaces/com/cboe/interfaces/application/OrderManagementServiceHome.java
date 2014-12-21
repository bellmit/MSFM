package com.cboe.interfaces.application;

public interface OrderManagementServiceHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "OrderManagementServiceHome";

    /**
     * Creates an instance of the OrderManagementService.
     */
    public OrderManagementService create(SessionManager sessionManager);
}
