package com.cboe.cfix.cas.status;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.status.AppServerStatusManagerImpl;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.sessionManagementService.SessionManagementService;
import com.cboe.infrastructureServices.sessionManagementService.SessionManagementServiceComponentConsumer;
import com.cboe.interfaces.application.AppServerStatusManager;
import com.cboe.interfaces.application.AppServerStatusManagerHome;

public class CfixAppServerStatusManagerHomeImpl extends ClientBOHome implements AppServerStatusManagerHome
{
    public final static String HOME_NAME = "AppServerStatusManagerHome";
    public static final String LOGGING_DIR = "LoggingDir";
    public static final String CACHE_DIRECTORY = "cacheDirectory";
    public static final String CACHE_BASENAME = "cacheBasename";
    protected AppServerStatusManagerImpl appServerStatusManager;
    protected String processName;
    private String cacheDirectory;
    private String cacheBasename;

    public CfixAppServerStatusManagerHomeImpl()
    {
        setSmaType("GlobalAppServerStatusManagerHome.AppServerStatusManagerHome");
    }

    public void clientInitialize() throws Exception
    {
        cacheDirectory = getProperty(CACHE_DIRECTORY).replace("{}", System.getProperty(LOGGING_DIR));
        cacheBasename = getProperty(CACHE_BASENAME);
    }

    public AppServerStatusManager create()
    {
        if(appServerStatusManager == null)
        {
            appServerStatusManager = new CfixAppServerStatusManagerImpl(processName, cacheDirectory, cacheBasename);
            addToContainer(appServerStatusManager);
            appServerStatusManager.create(String.valueOf(appServerStatusManager.hashCode()));
        }
        return appServerStatusManager;
    }

    public AppServerStatusManager find()
    {
        return create();
    }

    public void clientStart()
        throws Exception
    {
        if (Log.isDebugOn()) {
            Log.debug(this, "Creating Cfix AppServerStatus Manager");
        }
        FoundationFramework ff = FoundationFramework.getInstance();
        processName = ff.getConfigService().getProperty("Process.name()");
        create();
        SessionManagementService sms = ff.getSessionManagementService();
        SessionManagementServiceComponentConsumer componentConsumer = ServicesHelper.getComponentConsumerHome().find();
        sms.registerConsumerForProcessReferences(componentConsumer);
        String[] processList = {processName};
        componentConsumer = ServicesHelper.getComponentConsumerHome().findCASStatusListener();
        sms.registerConsumer(componentConsumer, processList);
        if (Log.isDebugOn()) {
            Log.debug(this, "Registration of SMS component consumer to SMS completed...");
        }
    }
}
