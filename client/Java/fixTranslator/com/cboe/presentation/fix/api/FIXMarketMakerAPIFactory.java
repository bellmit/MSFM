//
// -----------------------------------------------------------------------------------
// Source file: FIXMarketMakerAPIFactory.java
//
// PACKAGE: com.cboe.presentation.fix.api;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.fix.api;

import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.idl.cmiV3.UserSessionManagerV3;
import com.cboe.idl.cmiV9.UserSessionManagerV9;
import com.cboe.interfaces.presentation.api.FIXMarketMakerAPI;
import com.cboe.presentation.api.MarketMakerAPIFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.util.event.EventChannelListener;

/**
 * This class creates the user specific instance of the FIXMarketMakerAPI
 * interface the client needs to access the CAS and FIX.
 */
public class FIXMarketMakerAPIFactory
{
    private static FIXMarketMakerAPI fixMarketMakerAPI = null;

    public FIXMarketMakerAPIFactory()
    {
        super();
    }
    /**
     * This creates a user's FIXMarketMakerAPI instance and returns a reference to it.
     * This is then used to get references to all other pertinent interfaces.
     */
    public static FIXMarketMakerAPI create(UserSessionManagerV9 userSessionManager, UserSessionManagerV3 fixUserSessionManagerV3, CMIUserSessionAdmin userListener, EventChannelListener clientListener, boolean gmd)
        throws Exception
    {
        try
        {
            FIXMarketMakerAPIImpl marketMakerAPIImpl = new FIXMarketMakerAPIImpl(userSessionManager, fixUserSessionManagerV3, userListener, clientListener, gmd);
            marketMakerAPIImpl.initialize();
            fixMarketMakerAPI = marketMakerAPIImpl;
            // hack to force the MarketMakerAPIFactory to cache this same instance
            MarketMakerAPIFactory.create(fixMarketMakerAPI);
            return fixMarketMakerAPI;
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception("com.cboe.presentation.fix.api.create()","FIXMarketMakerAPI creation exception",e);
            throw e;
        }
    }
    /**
     * This returns a reference to the users FIXMarketMakerAPI instance.
     * This is then used to get references to all other pertinent interfaces.
     */
    public static FIXMarketMakerAPI find()
    {
        if (fixMarketMakerAPI != null)
        {
            return fixMarketMakerAPI;
        }
        else
        {
            throw new IllegalStateException("The FIXMarketMakerAPI must be created before calling find().");
        }
    }
    /**
     * Shutdown procedure. The FIXMarketMakerAPI reference will be nulled out
     */
    public static void shutdown()
    {
        fixMarketMakerAPI = null;
    }
}
