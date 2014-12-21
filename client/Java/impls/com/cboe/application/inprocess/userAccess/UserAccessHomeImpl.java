package com.cboe.application.inprocess.userAccess;

/**
 * @author Jing Chen
 */

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.domain.startup.StartupHelper;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.interfaces.application.inprocess.UserAccess;
import com.cboe.interfaces.application.inprocess.UserAccessHome;

public class UserAccessHomeImpl extends ClientBOHome implements UserAccessHome
{
    /**
     * MarketDataFactory constructor comment.
     */
    public final static String SESSION_MODE = "session_mode";

    private UserAccess userAccess;
    private char sessionMode;
    private String sessionId;

    public UserAccess create()
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating UserAccessImpl");
        }
        if (userAccess == null)
        {
            UserAccessImpl bo = new UserAccessImpl(sessionMode);
            bo.create(String.valueOf(bo.hashCode()));
            // Every BObject must be added to the container.
            addToContainer(bo);
            // Every BObject create MUST have a name...if the object is to be a managed object.
            userAccess = bo;
        }
        return userAccess;
    }

    public UserAccess find()
    {
        return create();
    }

    public void clientStart()
            throws Exception
    {
        find();
    }

    public void clientInitialize()
            throws Exception
    {
        StartupHelper.setStartupStatus(StartupHelper.STARTING);
        if (Log.isDebugOn())
        {
            Log.debug(this, "Logging in CAS to SecurityService...");
            Log.debug(this, "Authenticating CAS with Security Service...");
        }
        sessionId = FoundationFramework.getInstance().getSecurityService().authenticateWithCertificate();
        if (sessionId == null)
        {
            Log.alarm("Authentication Failed!");
        }
        /*if (Log.isDebugOn())
        {
        //Log.debug(this, "Creating Client interceptor for CAS...");
        //boolean interceptorReturnCode = FoundationFramework.getInstance().getSecurityService().createCasClientInterceptor(sessionId);
        //Log.debug(this, "Created Client interceptor for CAS:" + interceptorReturnCode);
        Log.debug(this, "Skipped Client interceptor for CAS:");
        }*/
        //boolean interceptorReturnCode = FoundationFramework.getInstance().getSecurityService().createCasClientInterceptor(sessionId);
        if (Log.isDebugOn())
        {
        	//Log.debug(this, "Created Client interceptor for CAS:" + interceptorReturnCode);
        	Log.debug(this, "Skipped Client interceptor for CAS:");            
            Log.debug(this, "Creating User Access Object to be bound with the Orb...");
            Log.debug(this, "SMA Type = " + this.getSmaType());
        }
        sessionMode = getProperty(SESSION_MODE).charAt(0);
        if (Log.isDebugOn())
        {
            Log.debug(this, "LoginSessionMode = " + sessionMode);
        }
        create();
    }

    public void clientShutdown()
    {
        StartupHelper.setStartupStatus(StartupHelper.SHUTTING_DOWN);
    }
}
