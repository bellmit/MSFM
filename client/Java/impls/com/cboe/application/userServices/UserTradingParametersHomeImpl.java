package com.cboe.application.userServices;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.application.UserTradingParametersV5;
import com.cboe.interfaces.application.UserTradingParametersHome;

/**
 * This is used to create UserTradingParameters BObject and an interceptor
 *
 * @author Mike Pyatetsky
 */

public class UserTradingParametersHomeImpl extends ClientBOHome implements UserTradingParametersHome
{

    /**
     * UserTradingParametersHomeImpl constructor
     *
     *  @author Mike Pyatetsky
     */
    public UserTradingParametersHomeImpl()
    {
        super();
    }

    /**
     * This method creates UserTradingParametersImpl object and the interceptor to it
     *
     *  @author Mike Pyatetsky
     */
    public UserTradingParametersV5 create(SessionManager sessionManager)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating UserTradingParametersImpl for " + sessionManager);
        }
        UserTradingParametersImpl bo = new UserTradingParametersImpl();

        bo.setSessionManager(sessionManager);
        // Every BObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));

        // Every BObject must be added to the container.
        addToContainer(bo);

        //The addToContainer call MUST occur prior to creation of the interceptor.
        UserTradingParametersInterceptor boi = null;
        try {
            boi = (UserTradingParametersInterceptor) this.createInterceptor( bo );
            boi.setSessionManager(sessionManager);
            if(getInstrumentationEnablementProperty())
            {
                boi.startInstrumentation(getInstrumentationProperty());
            }
        } catch (Exception ex) {
            Log.exception(this, ex);
        }
        return boi;
    }
}
