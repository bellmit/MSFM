package com.cboe.application.tradingSession.common;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.UserTradingSessionService;
import com.cboe.interfaces.application.UserTradingSessionServiceHome;
import com.cboe.interfaces.domain.session.BaseSessionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jing Chen
 *
 */
public class UserTradingSessionServiceHomeImpl extends ClientBOHome implements UserTradingSessionServiceHome
{
    protected Map userTradingSessionServices;
    public UserTradingSessionServiceHomeImpl()
    {
        userTradingSessionServices = new HashMap(11);
    }
    public synchronized UserTradingSessionService create(BaseSessionManager sessionManager)
    {
        UserTradingSessionServiceImpl userTradingSessionService = (UserTradingSessionServiceImpl)userTradingSessionServices.get(sessionManager);
        if(userTradingSessionService == null)
        {
            userTradingSessionService = new UserTradingSessionServiceImpl(sessionManager);
            addToContainer(userTradingSessionService);
            userTradingSessionServices.put(sessionManager, userTradingSessionService);
        }
        return userTradingSessionService;
    }

    public UserTradingSessionService find(BaseSessionManager sessionManager)
    {
        return create(sessionManager);
    }

    public void clientInitialize() throws Exception
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "SMA Type = " + this.getSmaType());
        }
    }

    public synchronized void remove(BaseSessionManager sessionManager)
    {
        userTradingSessionServices.remove(sessionManager);
    }
}
