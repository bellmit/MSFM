package com.cboe.application.tradeMaintenance;

import com.cboe.interfaces.application.FloorTradeMaintenanceServiceHome;
import com.cboe.interfaces.application.FloorTradeMaintenanceService;
import com.cboe.interfaces.application.SessionManagerV6;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.application.FloorTradeMaintenanceServiceInterceptor;
import com.cboe.domain.startup.ClientBOHome;

/**
 * Created by IntelliJ IDEA.
 * User: josephg
 * Date: Feb. 27, 2009
 */
public class FloorTradeMaintenanceServiceHomeImpl extends ClientBOHome implements FloorTradeMaintenanceServiceHome
{
    public FloorTradeMaintenanceServiceHomeImpl()
    {

    }

    public FloorTradeMaintenanceService create(SessionManagerV6 sessionManager)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating FloorTradeMaintenanceServiceImpl for " + sessionManager);
        }

        FloorTradeMaintenanceServiceImpl bo = new FloorTradeMaintenanceServiceImpl(sessionManager);


        //Every BObject must be added to the container.
        addToContainer(bo);

        bo.create(String.valueOf(bo.hashCode()));

        //The addToContainer call MUST occur prior to creation of the interceptor.
        FloorTradeMaintenanceServiceInterceptor boi = null;
        try
        {
            boi = (FloorTradeMaintenanceServiceInterceptor) this.createInterceptor(bo);
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