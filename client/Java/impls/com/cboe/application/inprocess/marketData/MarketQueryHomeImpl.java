package com.cboe.application.inprocess.marketData;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.inprocess.InProcessSessionManager;
import com.cboe.interfaces.application.inprocess.MarketQuery;
import com.cboe.interfaces.application.inprocess.MarketQueryHome;

/**
 * @author Jing Chen
 */
public class MarketQueryHomeImpl extends ClientBOHome implements MarketQueryHome
{

    public MarketQuery create(InProcessSessionManager sessionManager)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating MarketQueryImpl for " + sessionManager);
        }
        MarketQueryImpl bo = new MarketQueryImpl();
        // Every BObject must be added to the container BEFORE anything else.
        addToContainer(bo);
        bo.setInProcessSessionManager(sessionManager);

        // Every BObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));

        MarketQueryInterceptor boi = null;
        try {
            boi = (MarketQueryInterceptor) this.createInterceptor(bo);
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
    }     
}
