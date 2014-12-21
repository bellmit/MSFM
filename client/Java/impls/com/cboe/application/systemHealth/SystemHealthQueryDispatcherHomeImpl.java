package com.cboe.application.systemHealth;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.SystemHealthQueryDispatcher;
import com.cboe.interfaces.application.SystemHealthQueryDispatcherHome;
import com.cboe.interfaces.domain.SystemMonitorCommandMethodNames;

public class SystemHealthQueryDispatcherHomeImpl extends ClientBOHome implements SystemHealthQueryDispatcherHome
{
    public static final String ALLOW_CONTEXT_DETAIL_QUERY_PROPERTY = "allowContextDetailQuery";
    public static final String ALLOW_CONFIGURATION_DATA_QUERY_PROPERTY = "allowConfigurationDataQuery";
    public static final String ALLOW_PRODUCT_QUERY_PROPERTY = "allowProductQuery";

    private static final String TRUE = "true";

    private SystemHealthQueryDispatcherImpl instance;

    public SystemHealthQueryDispatcherHomeImpl()
    {
        setSmaType("GlobalSystemHealthQueryDispatcherHome.SystemHealthQueryDispatcherHomeImpl");
    }

    public void clientInitialize()
        throws Exception
    {
        create();

        if(instance == null)
        {
            throw new Exception("SystemHealthQueryDispatcherImpl instantiation failed.");
        }

        boolean allowContextDetailQuery = getProperty(ALLOW_CONTEXT_DETAIL_QUERY_PROPERTY).trim().equals(TRUE);
        boolean allowConfigurationDataQuery = getProperty(ALLOW_CONFIGURATION_DATA_QUERY_PROPERTY).trim().equals(TRUE);
        boolean allowProductQuery = getProperty(ALLOW_PRODUCT_QUERY_PROPERTY).trim().equals(TRUE);


        String[] argumentTypes = { "java.lang.String" };
        String[] argumentDescriptions = { "XML" };

        if(allowContextDetailQuery)
        {
            Log.information("Registering Context Detail Query with admin service for system health monitor.");
            registerCommand(instance, SystemMonitorCommandMethodNames.CONTEXT_DETAIL_QUERY, "getContextDetail", "Instrumentor Context Detail Query", argumentTypes, argumentDescriptions);
        }

        if(allowConfigurationDataQuery)
        {
            Log.information("Registering Configuration Data Query with admin service for system health monitor.");
            registerCommand(instance, SystemMonitorCommandMethodNames.CONFIGURATION_DATA_QUERY, "getConfigurationData", "CAS Configuration Data Query", argumentTypes, argumentDescriptions);
        }

        if(allowProductQuery)
        {
            Log.information("Registering Product/Trading Session Query with admin service for system health monitor.");
            registerCommand(instance, SystemMonitorCommandMethodNames.PRODUCT_DATA_QUERY, "getProductData", "Product and Trading Session Query", argumentTypes, argumentDescriptions);
        }

        SystemHealthQueryProcessorFactory.initialize(allowContextDetailQuery, allowConfigurationDataQuery, allowProductQuery);

    }

    public SystemHealthQueryDispatcher create()
    {
        return find();
    }

    public SystemHealthQueryDispatcher find()
    {
        if(instance == null)
        {
            instance = new SystemHealthQueryDispatcherImpl();
            addToContainer(instance);
            instance.create("SystemHealthQueryDispatcher");
        }
        return instance;
    }
}
