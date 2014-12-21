package com.cboe.application.nbboAgent;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.NBBOAgent;
import com.cboe.interfaces.application.NBBOAgentHome;
import com.cboe.interfaces.application.SessionManager;

public class NBBOAgentHomeImpl extends ClientBOHome implements NBBOAgentHome{
    public NBBOAgentHomeImpl()
    {
        super();
    }

    public NBBOAgent create(SessionManager sessionManager)
    {
        NBBOAgentInterceptor boi = null;
        NBBOAgentImpl bo = new NBBOAgentImpl();
        bo.setSessionManager(sessionManager);
        // Every BObject must be added to the container BEFORE anything else.
        addToContainer(bo);
        // Every BObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));
        try
        {
            boi = (NBBOAgentInterceptor) this.createInterceptor(bo);
            boi.setSessionManager(sessionManager);
            if(getInstrumentationEnablementProperty())
            {
                boi.startInstrumentation(getInstrumentationProperty());
            }
        }
        catch (Exception ex)
        {
            Log.exception(this, "session : " + sessionManager, ex);
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
