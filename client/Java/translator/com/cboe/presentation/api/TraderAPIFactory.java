//
// -----------------------------------------------------------------------------------
// Source file: TraderAPIFactory.java
//
// PACKAGE: com.cboe.presentation.api;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.idl.cmiV9.UserSessionManagerV9;
import com.cboe.interfaces.presentation.api.TraderAPI;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.util.event.EventChannelListener;

/**
 * This class creates the user specific instance of the TraderAPI
 * interface the client needs to access the CAS.
 *
 * @author Derek T. Chambers-Boucher
 * @version 09/09/1999
 */

public class TraderAPIFactory
{
    static UserSessionManagerV9 sessionManager = null;
    static TraderAPI traderAPI = null;

    /**
     * TraderAPIFactory constructor comment.
     */
    public TraderAPIFactory()
    {
        super();
    }
    /**
     * This method creates a TraderAPI instance that provides a user
     * with all the appropriate Trader based interfaces.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return the TraderAPI interface.
     *
     * @param sessionMgr the UserSessionManagerV3 that provides access to all pertinent Trader interfaces.
     *
     */
    public static TraderAPI create(UserSessionManagerV9 sessionMgr, CMIUserSessionAdmin userListener, EventChannelListener clientListener, boolean gmd)
        throws Exception
    {
        sessionManager = sessionMgr;
        try
        {
            TraderAPIImpl traderAPIImpl = new TraderAPIImpl(sessionManager, userListener, clientListener, gmd);
            traderAPIImpl.initialize();
            traderAPI = traderAPIImpl;
            return traderAPI;
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception("com.cboe.presentation.api.cretae()","TraderAPI creation exception",e);
            throw e;
        }
    }
    /**
     * This returns a reference to the users TraderAPI instance.
     * This is then used to get references to all other pertinent interfaces.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return a reference to the users TraderAPI instance.
     */
    public static TraderAPI find()
    {
        if (traderAPI != null)
        {
            return traderAPI;
        }
        else
        {
            throw new IllegalStateException("The TraderAPI must be created before calling find().");
        }
    }

    /**
     * Shutdown procedure. The TraderAPI reference will be nulled out
     *
     * @return none
     */
    public static void shutdown()
    {
        traderAPI = null;
    }
}
