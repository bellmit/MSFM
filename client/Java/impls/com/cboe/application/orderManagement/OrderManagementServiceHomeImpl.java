package com.cboe.application.orderManagement;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.interfaces.application.OrderManagementServiceHome;
import com.cboe.interfaces.application.OrderManagementService;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class OrderManagementServiceHomeImpl  extends ClientBOHome implements OrderManagementServiceHome
{
    public OrderManagementServiceHomeImpl()
    {
        super();
    }

    /**
     * Creates an instance of the OrderManagementService.
     */
    public OrderManagementService create(SessionManager sessionManager)
    {
        if (Log.isDebugOn()) {
            Log.debug(this, "Creating OrderManagementServiceHomeImpl for " + sessionManager);
        }
        OrderManagementServiceImpl bo = new OrderManagementServiceImpl(sessionManager);

        //Every bo object must be added to the container.
        addToContainer(bo);

        //Every BOObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));

        //The addToContainer call MUST occur prior to creation of the interceptor.
        OrderManagementServiceInterceptor boi = null;
        try
        {
            boi = (OrderManagementServiceInterceptor) this.createInterceptor( bo );
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
