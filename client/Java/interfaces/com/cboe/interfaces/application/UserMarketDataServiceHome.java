package com.cboe.interfaces.application;

import java.util.Map;

import com.cboe.interfaces.domain.session.BaseSessionManager;

/**
 * This is the common interface for the User Market Query Home
 * @author Jing Chen
 */
public interface UserMarketDataServiceHome
{
    /** Name that will be used for this home.    */
    public final static String HOME_NAME = "UserMarketDataServiceHome";
    public UserMarketDataService find(BaseSessionManager session);
    public UserMarketDataService create(BaseSessionManager session);
    public void remove(BaseSessionManager session);
    public Map getSessionConstraints();
}
