package com.cboe.application.parOrderManagementService;

import com.cboe.application.parOrderManagementService.ParOrderManagementServiceInterceptor;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.interfaces.application.ParOrderManagementServiceHome;
import com.cboe.interfaces.application.ParOrderManagementService;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;


public class ParOrderManagementServiceHomeImpl  extends ClientBOHome implements ParOrderManagementServiceHome
{
    private ParOrderManagementServiceImpl bo;
    private ParOrderManagementServiceInterceptor boi;
    public ParOrderManagementServiceHomeImpl()
    {
        super();
    }

    /**
     * Creates an instance of the ParOrderManagementService.
     */
    public ParOrderManagementService create(SessionManager sessionManager)
    {
        if (Log.isDebugOn()) {
            Log.debug(this, "Creating ParOrderManagementServiceHomeImpl for " + sessionManager);
        }
        bo = new ParOrderManagementServiceImpl(sessionManager);

        //Every bo object must be added to the container.
        addToContainer(bo);

        //Every BOObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));

        //The addToContainer call MUST occur prior to creation of the interceptor.
        boi = null;
        try
        {
            boi = (ParOrderManagementServiceInterceptor) this.createInterceptor( bo );
            boi.setSessionManager(sessionManager);
            if(getInstrumentationEnablementProperty())
            {
                boi.startInstrumentation(getInstrumentationProperty());
            }
        }
        catch(Exception ex)
        {
            Log.exception(this, ex);
        }

        return boi;
    }
    public ParOrderManagementService find()
    {
        return boi;
    }
}
