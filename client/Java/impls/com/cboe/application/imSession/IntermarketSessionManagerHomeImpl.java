package com.cboe.application.imSession;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.IntermarketSessionManagerHome;
import com.cboe.interfaces.application.IntermarketUserSessionManager;
import com.cboe.interfaces.application.SessionManager;

public class IntermarketSessionManagerHomeImpl extends ClientBOHome implements IntermarketSessionManagerHome {

    public IntermarketSessionManagerHomeImpl()
    {
        super();
    }
    public IntermarketUserSessionManager create(SessionManager theSession) {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating IntermarketSessionManagerImpl for " + theSession);
        }
        IntermarketSessionManagerImpl bo = new IntermarketSessionManagerImpl();
        bo.create(String.valueOf(bo.hashCode()));
        bo.setSessionManager(theSession);
        // Every BObject must be added to the container BEFORE anything else.
        addToContainer(bo);

        IntermarketSessionManagerInterceptor  boi = null;
        try {
            bo.initialize();
            boi = (IntermarketSessionManagerInterceptor) this.createInterceptor(bo);
            boi.setSessionManager(theSession);
            if(getInstrumentationEnablementProperty())
            {
                boi.startInstrumentation(getInstrumentationProperty());
            }
        } catch (Exception ex) {
            Log.exception(this, ex);
            return null;
        }
        return boi;
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
