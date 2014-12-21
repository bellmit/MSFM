package com.cboe.interfaces.floorApplication;

import com.cboe.interfaces.application.SessionManager;

/**
 * User: mahoney
 * Date: Jul 17, 2007
 */
public interface FloorSessionManagerHome
{
    public final static String HOME_NAME = "FloorSessionManagerHome";
    public FloorSessionManager create(SessionManager sessionManager);
}
