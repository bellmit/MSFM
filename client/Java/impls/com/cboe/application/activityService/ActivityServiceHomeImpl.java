package com.cboe.application.activityService;

import com.cboe.application.activityService.ActivityServiceInterceptor;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.interfaces.application.ActivityServiceHome;
import com.cboe.interfaces.application.ActivityService;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;


public class ActivityServiceHomeImpl  extends ClientBOHome implements ActivityServiceHome
{
    private ActivityServiceImpl bo;
    private ActivityServiceInterceptor boi;
    public ActivityServiceHomeImpl()
    {
        super();
    }

    /**
     * Creates an instance of the ParOrderManagementService.
     */
    public ActivityService create(SessionManager sessionManager)
    {
        if (Log.isDebugOn()) {
            Log.debug(this, "Creating ActivityServiceHomeImpl for " + sessionManager);
        }
        bo = new ActivityServiceImpl(sessionManager);

        //Every bo object must be added to the container.
        addToContainer(bo);

        //Every BOObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));

        //The addToContainer call MUST occur prior to creation of the interceptor.
        boi = null;
        try
        {
            boi = (ActivityServiceInterceptor) this.createInterceptor( bo );
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
    public ActivityService find()
    {
        return boi;
    }
}
