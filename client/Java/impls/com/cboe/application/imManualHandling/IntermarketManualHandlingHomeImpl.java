
package com.cboe.application.imManualHandling;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.application.IntermarketManualHandlingHome;
import com.cboe.interfaces.application.IntermarketManualHandling;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;


public class IntermarketManualHandlingHomeImpl extends ClientBOHome implements IntermarketManualHandlingHome{

    public IntermarketManualHandling create(SessionManager sessionManager)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating IntermarketManualHandlingImpl for " + sessionManager);
        }

        IntermarketManualHandlingImpl bo = new IntermarketManualHandlingImpl();
        bo.create(String.valueOf(bo.hashCode()));
        bo.setSessionManager(sessionManager);
        //Every bo object must be added to the container.
        addToContainer(bo);

        //The addToContainer call MUST occur prior to creation of the interceptor.
        IntermarketManualHandlingInterceptor boi = null;
        try {
            bo.initialize();
            boi = (IntermarketManualHandlingInterceptor) this.createInterceptor(bo);
            boi.setSessionManager(sessionManager);
        } catch (Exception ex) {
            Log.exception(this, ex);
            return null;
        }
        IntermarketManualHandling imHandling = boi;
        return imHandling;

    }

    public void clientInitialize()
        throws Exception
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "SMA Type = " + this.getSmaType());
        }
    }
}
