package com.cboe.domain.logout;


public class LogoutServiceFactory {

    private static LogoutService logoutService = null;

    public LogoutServiceFactory()
    {
        super();
    }

    public synchronized static LogoutService create()
    {
        if ( logoutService == null )
        {
            logoutService = new LogoutService();
        }

        return logoutService;
    }

    public static LogoutService find()
    {
        return create();
    }

}