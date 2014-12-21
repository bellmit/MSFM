package com.cboe.internalPresentation.api;

import com.cboe.util.event.*;
import com.cboe.idl.cmiUser.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.internalApplication.*;

import com.cboe.interfaces.internalPresentation.SystemAdminAPI;

/**
 * This class creates the user specific instance of the SystemAdminAPI
 * interface the client needs to access the SACAS.
 *
 * @author Derek T. Chambers-Boucher
 * @version 09/09/1999
 */

public class SystemAdminAPIFactory
{
    static SystemAdminSessionManager sessionManager = null;
    static SystemAdminAPI adminAPI = null;

    /**
     * SystemAdminAPIFactory constructor comment.
     */
    public SystemAdminAPIFactory()
    {
        super();
    }
    /**
     * This method creates a SystemAdminAPI instance that provides a user
     * with all the appropriate SystemAdmin based interfaces.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return the SystemAdminAPI interface.
     *
     * @param sessionMgr the SystemAdminSessionManager that provides access to all pertinent Trader interfaces.
     *
     */
    public static SystemAdminAPI create(SystemAdminSessionManager sessionMgr, CMIUserSessionAdmin userListener, EventChannelListener clientListener)
        throws Exception
    {
            sessionManager = sessionMgr;
            SystemAdminAPIImpl adminAPIImpl = new SystemAdminAPIImpl(sessionMgr, userListener, clientListener);
            adminAPIImpl.initialize();
            adminAPI = adminAPIImpl;

            return adminAPI;
    }
    /**
     * This returns a reference to the users SystemAdminAPI instance.
     * This is then used to get references to all other pertinent interfaces.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return a reference to the users SystemAdminAPI instance.
     */
    public static SystemAdminAPI find()
    {
        if (adminAPI != null)
        {
            return adminAPI;
        }
        else
        {
            throw new IllegalStateException("The SystemAdminAPI must be created before calling find().");
        }
    }
}
