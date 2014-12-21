package com.cboe.floorApplication.manualReporting;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.interfaces.floorApplication.ManualReportingServiceHome;
import com.cboe.interfaces.floorApplication.ManualReportingService;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * Author: mahoney
 * Date: Jul 18, 2007
 */
public class ManualReportingServiceHomeImpl extends ClientBOHome implements ManualReportingServiceHome
{
    public ManualReportingServiceHomeImpl()
    {
        super();
    }

    /**
     * Creates an instance of the ManualReportingService.
     */
    public ManualReportingService create(SessionManager sessionManager)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating ManualReportingServiceImpl for " + sessionManager);
        }
        ManualReportingServiceImpl bo = new ManualReportingServiceImpl(sessionManager);

        // Every BObject must be added to the container.
        addToContainer(bo);

        // Every BObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));

        //The addToContainer call MUST occur prior to creation of the interceptor.
        ManualReportingServiceInterceptor boi = null;
        try
        {
            boi = (ManualReportingServiceInterceptor) this.createInterceptor( bo );
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
}
