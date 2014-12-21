package com.cboe.interfaces.expressApplication;

import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.floorApplication.NBBOService;
import com.cboe.interfaces.floorApplication.MarketQueryV5;;

public interface MarketQueryV4Home
{
    public final static String HOME_NAME = "MarketQueryV4Home";

    public MarketQueryV4 createMarketQueryV4(SessionManager sessionManager);
    
    /**
     * Creates an instance of  nbbo service.
     */
    public NBBOService createNBBOService(SessionManager sessionManager);
    
    /**
     * Creates an instance of  nbbo service.
     */
    public MarketQueryV5 createMarketQueryV5(SessionManager sessionManager);
    
    
}
