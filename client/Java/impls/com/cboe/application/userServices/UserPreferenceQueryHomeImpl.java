package com.cboe.application.userServices;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.application.UserPreferenceQuery;
import com.cboe.interfaces.application.UserPreferenceQueryHome;

/**
 * This type was created in VisualAge.
 */

public class UserPreferenceQueryHomeImpl extends ClientBOHome implements UserPreferenceQueryHome
{

    /**
     * UserPreferencesQueryFactory constructor comment.
     */
    public UserPreferenceQueryHomeImpl()
    {
        super();
    }

    /**
     * This method was created in VisualAge.
     */
    public UserPreferenceQuery create(SessionManager sessionManager)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating UserPreferenceQueryImpl for " + sessionManager);
        }
        UserPreferenceQueryImpl bo = new UserPreferenceQueryImpl();

        bo.setSessionManager(sessionManager);
        // Every BObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));

        // Every BObject must be added to the container.
        addToContainer(bo);

        //The addToContainer call MUST occur prior to creation of the interceptor.
        UserPreferenceQueryInterceptor boi = null;
        try {
            boi = (UserPreferenceQueryInterceptor) this.createInterceptor( bo );
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
