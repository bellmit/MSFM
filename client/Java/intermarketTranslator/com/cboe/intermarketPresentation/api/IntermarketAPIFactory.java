/*
 * Created by IntelliJ IDEA.
 * User: torresl
 * Date: Oct 3, 2002
 * Time: 9:41:01 AM
 */
package com.cboe.intermarketPresentation.api;

import com.cboe.idl.cmi.UserSessionManager;
import com.cboe.idl.cmiIntermarket.IntermarketUserSessionManager;
import com.cboe.interfaces.intermarketPresentation.api.IntermarketAPI;
import com.cboe.presentation.common.logging.GUILoggerHome;

public class IntermarketAPIFactory
{
    private static IntermarketAPI intermarketAPI;

    public IntermarketAPIFactory()
    {
        super();
    }

    public static void create(UserSessionManager userSessionManager)
        throws Exception
    {
        try
        {
            IntermarketUserSessionManager intermarketUserSessionManager = IntermarketUserAccessFactory.getIntermarketUserSessionManager(userSessionManager);
            IntermarketAPIImpl impl = new IntermarketAPIImpl();
            impl.initialize(intermarketUserSessionManager);
            intermarketAPI = impl;
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception("com.cboe.presentation.api.create()","MarketMakerAPI creation exception",e);
            throw e;
        }
    }

    public static IntermarketAPI find()
    {
        if( intermarketAPI == null)
        {
            throw new IllegalStateException("The IntermarketAPI must be created before calling find().");
        }
        else
        {
            return intermarketAPI;
        }
    }
}
