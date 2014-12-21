package com.cboe.application.cas;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.domain.startup.StartupHelper;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.util.ExceptionBuilder;

public class UserAccessBaseHomeImpl extends ClientBOHome
{

    public final static String SESSION_MODE = "session_mode";
    public final static String HEARTBEAT_TIMEOUT = "heartbeat_timeout";
    public final static String CMI_VERSION = "CMI_version";

    protected char sessionMode;
    protected int heartbeatTimeout;
    protected String cmiVersion;

    public UserAccessBaseHomeImpl()
    {
        super();
        setSmaType("GlobalUserAccessHome.UserAccessHomeImpl");
    }

    public String objectToString(org.omg.CORBA.Object obj)
    {
        try {
            if (obj != null)
            {
                FoundationFramework ff = FoundationFramework.getInstance();
                return ff.getOrbService().getOrb().object_to_string(obj);
            } else
            {
                return null;
            }
        }
        catch (Exception e)
        {
            Log.exception(this, "Could not stringify the object", e);
            return null;
        }
    }

    public void clientInitialize()
            throws Exception
    {
        sessionMode = getProperty(SESSION_MODE).charAt(0);
        if (Log.isDebugOn())
        {
            Log.debug(this, "LoginSessionMode = " + sessionMode);
        }
        heartbeatTimeout = Integer.parseInt(getProperty(HEARTBEAT_TIMEOUT));
        if (Log.isDebugOn())
        {
            Log.debug(this, "Heartbeat TIMEOUT = " + heartbeatTimeout);
        }
        cmiVersion = getProperty(CMI_VERSION);
    }

    public void clientShutdown()
    {
        StartupHelper.setStartupStatus(StartupHelper.SHUTTING_DOWN);
    }

}
