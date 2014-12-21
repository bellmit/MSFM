package com.cboe.interfaces.application;

import com.cboe.interfaces.domain.session.BaseSessionManager;

/**
 * This is the common interface for the User Trading Session Home
 * @author Jing Chen
 */
public interface UserTradingSessionServiceHome
{
    /** Name that will be used for this home.    */
    public final static String HOME_NAME = "UserTradingSessionServiceHome";
    public UserTradingSessionService find(BaseSessionManager session);
    public UserTradingSessionService create(BaseSessionManager session);
    public void remove(BaseSessionManager session);
}
