package com.cboe.application.inprocess.tradingSession;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.inprocess.*;
import com.cboe.application.inprocess.quoteQuery.QuoteQueryImpl;
import com.cboe.application.inprocess.quoteQuery.QuoteQueryInterceptor;

/**
 * @author Jing Chen
 */
public class InProcessTradingSessionHomeImpl extends ClientBOHome implements InProcessTradingSessionHome
{
    public InProcessTradingSession create(InProcessSessionManager theSession, UserSessionAdminConsumer consumer)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating InProcessTradingSessionImpl for " + theSession);
        }
        InProcessTradingSessionImpl bo = new InProcessTradingSessionImpl(consumer);
        // Every BObject must be added to the container BEFORE anything else.
        addToContainer(bo);
        bo.setInProcessSessionManager(theSession);

        // Every BObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));

        InProcessTradingSessionInterceptor boi = null;
        try
        {
            boi = (InProcessTradingSessionInterceptor) this.createInterceptor(bo);
            boi.setSessionManager(theSession);
        }
        catch (Exception ex)
        {
            Log.exception(this, ex);
        }
        return boi;
    }
}
