package com.cboe.application.inprocess.marketData;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.inprocess.InProcessSessionManager;
import com.cboe.interfaces.application.inprocess.RemoteMarketQuery;
import com.cboe.interfaces.application.inprocess.RemoteMarketQueryHome;

/**
 * @author Jing Chen
 */
public class RemoteMarketQueryHomeImpl extends ClientBOHome implements RemoteMarketQueryHome
{ 
    // Config parameters (names)
    private static final String MARKETDATA_CALLBACK_TIMEOUT = "marketDataCallbackTimeout";
    private static final String RESUBSCRIPTION_INTERVAL     = "resubscribeInterval";
    private static final String RESUBSCRIPTION_MAX_ATTEMPTS = "maxResubscribeAttempts";
    // Config parameters (values)
    private int     marketDataCallbackTimeout;
    private long    resubscribeInterval;
    private int     maxResubscribeAttempts;

    public RemoteMarketQuery create(InProcessSessionManager sessionManager)
    {
        String userId=null;

        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating RemoteMarketQueryImpl for " + sessionManager);
        }
        try {
            userId = sessionManager.getUserId();
        } catch (Exception e) {
            Log.exception(this,e);
        }
        RemoteMarketQueryImpl bo = new RemoteMarketQueryImpl(sessionManager, userId, marketDataCallbackTimeout, resubscribeInterval, maxResubscribeAttempts);
        // Every BObject must be added to the container BEFORE anything else.
        addToContainer(bo);

        // Every BObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));


        RemoteMarketQueryInterceptor boi = null;
        try {
            boi = (RemoteMarketQueryInterceptor) this.createInterceptor(bo);
            boi.setSessionManager(sessionManager);
        }
        catch (Exception ex) {
            Log.exception(this, ex);
        }
        return boi;
    }
    public void clientInitialize() throws Exception
    {
         if (Log.isDebugOn())
         {
            Log.debug(this, "SMA Type = " + this.getSmaType());
         }
         marketDataCallbackTimeout  = Integer.parseInt(getProperty(MARKETDATA_CALLBACK_TIMEOUT));
         resubscribeInterval        = Integer.parseInt(getProperty(RESUBSCRIPTION_INTERVAL));
         maxResubscribeAttempts     = Integer.parseInt(getProperty(RESUBSCRIPTION_MAX_ATTEMPTS));
    }     
}
