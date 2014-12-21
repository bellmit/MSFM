package com.cboe.remoteApplication.startup;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.domain.startup.StartupHelper;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.application.shared.RemoteConnection;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.remoteApplication.RemoteCASHome;
import com.cboe.util.event.EventChannelAdapterFactory;

/**
 * @author Jing Chen
 */
public class RemoteCASHomeImpl extends ClientBOHome implements RemoteCASHome
{
    public void clientInitialize() throws Exception
    {
        StartupHelper.setStartupStatus(StartupHelper.STARTING);
        if (Log.isDebugOn())
        {
            Log.debug(this, "Logging in CAS to SecurityService...");
            Log.debug(this, "Authenticating CAS with Security Service...");
        }
        String sessionId = FoundationFramework.getInstance().getSecurityService().authenticateWithCertificate();
        if (sessionId == null)
        {
            Log.alarm(this, "Authentication Failed!");
        }
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating Client interceptor for CAS...");
        }
        boolean interceptorReturnCode = FoundationFramework.getInstance().getSecurityService().createCasClientInterceptor(sessionId);
        if (Log.isDebugOn())
        {
            Log.debug(this, "Created Client interceptor for CAS:" + interceptorReturnCode);
        }
        String[] args = com.cboe.client.util.CollectionHelper.EMPTY_String_ARRAY;
        RemoteConnection connection = RemoteConnectionFactory.create(args);
        EventChannelAdapterFactory.find().setDynamicChannels(true);
        if (Log.isDebugOn())
        {
            Log.debug(this, "SMA Type = " + this.getSmaType());
        }
    }

    public void clientShutdown()
    {
        StartupHelper.setStartupStatus(StartupHelper.SHUTTING_DOWN);
    }
}
