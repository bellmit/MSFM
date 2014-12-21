//
// -----------------------------------------------------------------------------------
// Source file: MarketMakerAPIFactory.java
//
// PACKAGE: com.cboe.presentation.api;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.idl.cmiV9.UserSessionManagerV9;
import com.cboe.interfaces.presentation.api.MarketMakerAPI;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.util.event.EventChannelListener;

/**
 * This class creates the user specific instance of the MarketMakerAPI
 * interface the client needs to access the CAS.
 *
 * @author Derek T. Chambers-Boucher
 * @version 03/16/1999
 */
public class MarketMakerAPIFactory
{
    private static UserSessionManagerV9 sessionManager = null;
    private static MarketMakerAPI marketMakerAPI = null;

    /**
     * MarketMakerAPIFactory constructor comment.
     */
    public MarketMakerAPIFactory()
    {
        super();
    }
    /**
     * This creates a users MarketMakerAPI instance and returns a reference to it.
     * This is then used to get references to all other pertinent interfaces.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return a reference to the users MarketMakerAPI instance.
     *
     * @param sessionMgrV4 the SessionManager object used to get other interfaces.
     */
    public static MarketMakerAPI create(UserSessionManagerV9 sessionMgr, CMIUserSessionAdmin userListener, EventChannelListener clientListener, boolean gmd)
        throws Exception
    {
        sessionManager = sessionMgr;
        try
        {
            MarketMakerAPIImpl marketMakerAPIImpl = new MarketMakerAPIImpl(sessionManager, userListener, clientListener, gmd);
            marketMakerAPIImpl.initialize();
            marketMakerAPI = marketMakerAPIImpl;

            return marketMakerAPI;
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception("com.cboe.presentation.api.create()","MarketMakerAPI creation exception",e);
            throw e;
        }
    }

    /**
     * Provides a way to force this to cache a specific instance of MarketMakerAPI
     * @param mmAPI
     */ 
    public static MarketMakerAPI create(MarketMakerAPI mmAPI)//(UserSessionManagerV3 sessionManager, 
    {
        marketMakerAPI = mmAPI;
        return marketMakerAPI;
    }
    /**
     * This returns a reference to the users MarketMakerAPI instance.
     * This is then used to get references to all other pertinent interfaces.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return a reference to the users MarketMakerAPI instance.
     */
    public static MarketMakerAPI find()
    {
        if (marketMakerAPI != null)
        {
            return marketMakerAPI;
        }
        else
        {
            throw new IllegalStateException("The MarketMakerAPI must be created before calling find().");
        }
    }
    /**
     * Shutdown procedure. The MarketMakerAPI reference will be nulled out
     *
     * @return none
     */
    public static void shutdown()
    {
        marketMakerAPI = null;
    }
}
