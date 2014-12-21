package com.cboe.application.userServices;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.application.UserHistory;
import com.cboe.interfaces.application.UserHistoryHome;

/**
 * This is the Home implemenatation for the User History service.
 *
 * @author Dean Grippo
 * @version 08/02/2000
 */
public class UserHistoryHomeImpl extends ClientBOHome
                                implements UserHistoryHome
{
    /**
     * UserHistoryHomeImpl constructor.
     */
    public UserHistoryHomeImpl()
    {
        super();
    }

    /**
     * Creates a new user instance of a UserHistory Service.
     */
    public UserHistory create(SessionManager sessionMgr)
    {
        if (Log.isDebugOn()) {
            Log.debug(this, "Creating UserHistoryImpl for " + sessionMgr);
        }
        UserHistoryImpl bo = new UserHistoryImpl();
        addToContainer( bo );
        bo.create( String.valueOf( bo.hashCode() ) );
        bo.setSessionManager( sessionMgr );
        //add the bo to the container.

        UserHistoryInterceptor boi = null;
        try
        {
            boi = (UserHistoryInterceptor)this.createInterceptor( bo );
            boi.setSessionManager(sessionMgr);
            if(getInstrumentationEnablementProperty())
            {
                boi.startInstrumentation(getInstrumentationProperty());
            }
        }
        catch ( Exception ex)
        {
            Log.exception(this, ex);
        }

        return boi;
    }
}
