package com.cboe.application.omtSession;

import com.cboe.interfaces.application.OMTSessionManagerHome;
import com.cboe.interfaces.application.OMTSessionManager;
import com.cboe.interfaces.application.SessionManager;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class OMTSessionManagerHomeImpl extends ClientBOHome implements OMTSessionManagerHome
{
    public OMTSessionManagerHomeImpl()
    {
        super();
    }

    public OMTSessionManager create(SessionManager sessionManager)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating OMTSessionManagerImpl for " + sessionManager);
        }
        OMTSessionManagerImpl bo = new OMTSessionManagerImpl();
        bo.create(String.valueOf(bo.hashCode()));
        bo.setSessionManager(sessionManager);
        // Every BObject must be added to the container BEFORE anything else.
        addToContainer(bo);

        OMTSessionManagerInterceptor boi;
        try
        {
            bo.initialize();
            boi = (OMTSessionManagerInterceptor) this.createInterceptor(bo);
            boi.setSessionManager(sessionManager);
            if(getInstrumentationEnablementProperty())
            {
                boi.startInstrumentation(getInstrumentationProperty());
            }
        }
        catch(Exception ex)
        {
            Log.exception(this, ex);
            return null;
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