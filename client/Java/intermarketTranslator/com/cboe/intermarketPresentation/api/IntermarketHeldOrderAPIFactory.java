package com.cboe.intermarketPresentation.api;

import com.cboe.idl.cmiIntermarket.NBBOAgentSessionManager;
import com.cboe.interfaces.intermarketPresentation.api.IntermarketHeldOrderAPI;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.presentation.common.logging.GUILoggerHome;

import java.util.HashMap;

final public class IntermarketHeldOrderAPIFactory
{
    private static final HashMap imhoMap;
    private static IntermarketHeldOrderAPI intermarketHeldOrderAPI;
    private static boolean initialized = false;
    static {
        imhoMap = new HashMap(10);
    }

    private IntermarketHeldOrderAPIFactory()
    {
        super();
    }

    public static void createIntermarketHeldOrderAPIInstance(SessionProductClass spc)
    {
        synchronized(imhoMap)
        {
            if(imhoMap.containsKey(spc) == false)
            {
                imhoMap.put(spc, new IntermarketHeldOrderAPIImpl(spc));
            }
        }
    }

    public static IntermarketHeldOrderAPI initializeIntermarketHeldOrderAPI(SessionProductClass spc, NBBOAgentSessionManager nbboAgentSessionManager)
        throws Exception
    {
        try
        {
            IntermarketHeldOrderAPI imhoapi = null;
            synchronized(imhoMap)
            {
                imhoapi = (IntermarketHeldOrderAPI) imhoMap.get(spc);
                if(imhoapi == null)
                {
                    createIntermarketHeldOrderAPIInstance(spc);
                    imhoapi = (IntermarketHeldOrderAPI) imhoMap.get(spc);
                }
            }
            ((IntermarketHeldOrderAPIImpl)imhoapi).initialize(nbboAgentSessionManager);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception("com.cboe.intermarketPresentation.api.IntermarketHeldOrderAPI.create()","IntermarketHeldOrderAPI creation exception",e);
            throw e;
        }
        return intermarketHeldOrderAPI;
    }

    public static IntermarketHeldOrderAPI find(SessionProductClass spc)
    {
        synchronized(imhoMap)
        {
            return (IntermarketHeldOrderAPI) imhoMap.get(spc);
        }
    }

    /**
     * Need to create the instance first to allow the caches to register before the first event is received
     */
//    public static void createIntermarketHeldOrderAPIInstance()
//    {
//        intermarketHeldOrderAPI = new IntermarketHeldOrderAPIImpl();
//    }
//    private static IntermarketHeldOrderAPI initialize(NBBOAgentSessionManager nbboAgentSessionManager)
//        throws Exception
//    {
//        try
//        {
//            if(intermarketHeldOrderAPI == null)
//            {
//                createIntermarketHeldOrderAPIInstance();
//            }
//            ((IntermarketHeldOrderAPIImpl)intermarketHeldOrderAPI).initialize(nbboAgentSessionManager);
//            initialized = true;
//        }
//        catch (Exception e)
//        {
//            GUILoggerHome.find().exception("com.cboe.intermarketPresentation.api.IntermarketHeldOrderAPI.create()","IntermarketHeldOrderAPI creation exception",e);
//            throw e;
//        }
//        return intermarketHeldOrderAPI;
//    }
//
//    public static IntermarketHeldOrderAPI find(NBBOAgentSessionManager nbboAgentSessionManager)
//        throws Exception
//    {
//        if (initialized == false)
//        {
//            initialize(nbboAgentSessionManager);
//        }
//        return intermarketHeldOrderAPI;
//    }
//
//    public static IntermarketHeldOrderAPI find()
//    {
//        if( initialized == false)
//        {
//            throw new IllegalStateException("The IntermarketHeldOrderAPI must be created before calling find().");
//        }
//        else
//        {
//            return intermarketHeldOrderAPI;
//        }
//    }


}
