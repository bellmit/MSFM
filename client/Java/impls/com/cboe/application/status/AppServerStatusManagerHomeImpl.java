package com.cboe.application.status;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.interfaces.application.AppServerStatusManagerHome;
import com.cboe.interfaces.application.AppServerStatusManager;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.instrumentationService.EntityID;
import com.cboe.infrastructureServices.sessionManagementService.SessionManagementService;
import com.cboe.infrastructureServices.sessionManagementService.SessionManagementServiceComponentConsumer;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.TransactionTimingUtil;
import com.cboe.application.shared.TransactionTimingRegistration;

public class AppServerStatusManagerHomeImpl extends ClientBOHome implements AppServerStatusManagerHome
{
    private static final String LOGGING_DIR = "LoggingDir";
    private static final String CACHE_DIRECTORY = "cacheDirectory";
    private static final String CACHE_BASENAME = "cacheBasename";
    private static final String SOURCETYPE = "prefixCluster";
    private static final String SOURCETYPE_CAS = "CAS";
    private static final String SOURCETYPE_FIX = "FIXCAS";

    // Value to use if System.getProperty(LOGGING_DIR) fails (such as when running Simulator)
    private static final String NO_LOGGING_DIR = ".";

    protected AppServerStatusManagerImpl appServerStatusManager;
    protected String processName;
    private String cacheDirectory;
    private String cacheBasename;
    
    public AppServerStatusManagerHomeImpl()
    {
        setSmaType("GlobalAppServerStatusManagerHome.AppServerStatusManagerHome");
    }

    public void clientInitialize() throws Exception
    {
        cacheDirectory = getProperty(CACHE_DIRECTORY).replace("{}", System.getProperty(LOGGING_DIR, NO_LOGGING_DIR));
        cacheBasename = getProperty(CACHE_BASENAME);
    }

    public AppServerStatusManager create()
    {
        if(appServerStatusManager == null)
        {
            appServerStatusManager = new AppServerStatusManagerImpl(processName, cacheDirectory, cacheBasename);
            addToContainer( appServerStatusManager );
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
            Log.debug(this, "Creating CAS AppServerStatus Manager"); 
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
        String sType = System.getProperty(SOURCETYPE);
		if ( (SOURCETYPE_CAS.equals(sType)) || (SOURCETYPE_FIX.equals(sType)) )
		{
			initializeTransactionTimer();  			
		}
           
    }
    protected void initializeTransactionTimer()
    {
        TransactionTimingUtil.initialize();
        TransactionTimingRegistration.registerCasTransactionIdentifier();
        TransactionTimingRegistration.registerFETransactionIdentifier();
       
    }

}
