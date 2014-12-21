package com.cboe.floorApplication.floorSession;

import com.cboe.interfaces.floorApplication.FloorSessionManager;
import com.cboe.interfaces.floorApplication.FloorSessionManagerHome;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * User: mahoney
 * Date: Jul 17, 2007
 */
public class FloorSessionManagerHomeImpl extends ClientBOHome implements FloorSessionManagerHome
{
    public FloorSessionManagerHomeImpl()
    {
        super();
    }

    public FloorSessionManager create(SessionManager sessionManager)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating FloorSessionManagerHomeImpl for " + sessionManager);
        }
        FloorSessionManagerImpl bo = new FloorSessionManagerImpl();
        bo.create(String.valueOf(bo.hashCode()));
        bo.setSessionManager(sessionManager);
        // Every BObject must be added to the container BEFORE anything else.
        addToContainer(bo);

        FloorSessionManagerInterceptor boi;
        try
        {
            bo.initialize();
            boi = (FloorSessionManagerInterceptor) this.createInterceptor(bo);
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
