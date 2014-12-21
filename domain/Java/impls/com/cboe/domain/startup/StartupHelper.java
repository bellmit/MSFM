package com.cboe.domain.startup;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.systemsManagementService.ApplicationPropertyHelper;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.systemsManagementService.PropertyQuery;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
import com.cboe.infrastructureServices.interfaces.adminService.Property;
import com.cboe.infrastructureServices.interfaces.adminService.StorageType;

public class StartupHelper
{
    public static final String PRE_START = "PRE-START";
    public static final String STARTING = "STARTING";
    public static final String ERROR = "ERROR";
    public static final String READY = "READY";
    public static final String SHUTTING_DOWN = "SHUTTING DOWN";
    public static final String SHUTDOWN = "SHUTDOWN";

    private static String startupStatus = PRE_START;

    synchronized public static String getStartupStatus()
    {
        return startupStatus;
    }

    synchronized public static void setStartupStatus(String newStatus)
    {
        Log.debug("StartupHelper -> Setting process state = " + newStatus);

        startupStatus = newStatus;
        // Add code to persist state with the configuration service.

        String currentStatus;
        PropertyQuery pq;
        FoundationFramework ff = null;
        ConfigurationService configService = null;
        try {
            ff = FoundationFramework.getInstance();
            configService = ff.getConfigService();

            pq = PropertyQuery.queryFor( "currentStatus" ).from( "Application" ).from( configService.getFullName( ff ) );
            Property prop = new Property(pq.queryString(), startupStatus, StorageType.TRANSIENT);
            configService.defineProperty(prop);
        } catch (Exception e)
        {
            Log.exception("Could not set currentStatus property", e);
        }
    }
}