package com.cboe.application.tradeMaintenance;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.ExternalTradeMaintenanceServiceHome;
import com.cboe.interfaces.application.SessionManagerTMS;
import com.cboe.interfaces.application.ExternalTradeMaintenanceService;
import com.cboe.application.tradeMaintenance.ExternalTradeMaintenanceServiceInterceptor;


public class ExternalTradeMaintenanceServiceHomeImpl extends ClientBOHome implements ExternalTradeMaintenanceServiceHome {

	public ExternalTradeMaintenanceService create(SessionManagerTMS sessionManager) {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating ExternalTradeMaintenanceServiceImpl for " + sessionManager);
        }

        ExternalTradeMaintenanceServiceImpl bo = new ExternalTradeMaintenanceServiceImpl(sessionManager);
        

        //Every BObject must be added to the container.
        addToContainer(bo);

        bo.create(String.valueOf(bo.hashCode()));

        //The addToContainer call MUST occur prior to creation of the interceptor.
        ExternalTradeMaintenanceServiceInterceptor boi = null;
        try
        {
            boi = (ExternalTradeMaintenanceServiceInterceptor) this.createInterceptor(bo);
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
