package com.cboe.remoteApplication.startup;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.domain.startup.StartupHelper;
import com.cboe.domain.util.MarketDataARCommandHelper;
import com.cboe.interfaces.remoteApplication.RemoteCASConfigurationService;
import com.cboe.interfaces.remoteApplication.RemoteCASConfigurationServiceHome;
import com.cboe.interfaces.application.VersionQuery;
import com.cboe.util.ExceptionBuilder;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.sessionManagementService.SessionManagementService;
import com.cboe.infrastructureServices.sessionManagementService.SessionManagementServiceUserConsumer;
import com.cboe.infrastructureServices.sessionManagementService.SessionManagementServiceComponentConsumer;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.cas.CASSummaryData;
import com.cboe.application.cas.VersionQueryImpl;
import com.cboe.instrumentationService.instrumentors.HeapInstrumentor;
import com.cboe.remoteApplication.shared.RemoteMarketDataSessionNameHelper;

/**
 * @author Jing Chen
 */
public class RemoteCASConfigurationServiceHomeImpl extends ClientBOHome implements RemoteCASConfigurationServiceHome
{
//    private List configurationGroups;
    public static final String MDCAS_CONFIGURATION_GROUPS  = "ConfigurationGroups";
    private String groups;
    private RemoteCASConfigurationService configurationService;

    //Two new variable to control EOP/EOS and LTLS publishing.
    private String publishEOP;
    private String publishLTLS;
    private static final String[] AR_ARGS = new String[]{"java.lang.String"};
    private static final String[] AR_ARGS_DESC = new String[]{"Start/Stop/Status Publishing"};

    public void clientInitialize() throws Exception
    {
        VersionQuery versionQuery = new VersionQueryImpl();
        CASSummaryData casSummaryData = new CASSummaryData(versionQuery);
        HeapInstrumentor hi = FoundationFramework.getInstance().getInstrumentationService().getHeapInstrumentorFactory().find(HeapInstrumentor.INSTRUMENTOR_TYPE_NAME);
        if(hi == null)
        {
            hi = FoundationFramework.getInstance().getInstrumentationService().getHeapInstrumentorFactory().create(HeapInstrumentor.INSTRUMENTOR_TYPE_NAME, casSummaryData);
            FoundationFramework.getInstance().getInstrumentationService().getHeapInstrumentorFactory().register(hi);
        }
        else
        {
            hi.setUserData(casSummaryData);
        }
        registerCommand(this, "publishExpectedOpeningPrice", "publishExpectedOpeningPrice", "Start/Stop/Status EOP/EOS publishing", AR_ARGS, AR_ARGS_DESC);
        registerCommand(this, "publishLargeTradeLastSale", "publishLargeTradeLastSale", "Start/Stop/Status Large Trade Last Sale publishing", AR_ARGS, AR_ARGS_DESC);
    }



    public RemoteCASConfigurationService create()
    {
        if (configurationService == null)
        {
            RemoteCASConfigurationServiceImpl remoteCASConfigurationService = new RemoteCASConfigurationServiceImpl(groups);
            addToContainer(remoteCASConfigurationService);
            configurationService = remoteCASConfigurationService;
        }
        return configurationService;
    }

    public RemoteCASConfigurationService find()
    {
        return create();
    }


    public void clientStart() throws Exception
    {
        //turn off default subscriptions on all levels.
        ServicesHelper.getSubscriptionCollectionService().setDefaultSubscriptonFlag(false);
        RemoteMarketDataSessionNameHelper.init();
        groups = System.getProperty(MDCAS_CONFIGURATION_GROUPS);
        if ((groups == null) || (groups.length() == 0))
        {
            throw ExceptionBuilder.systemException("missing ConfigurationGroups value", 0);
        }

        //Two runtime parameters to control EOP/EOS and LTLS publishing
        //start new code for ar command support for EOP and LTLS
        publishEOP = System.getProperty("EnableEOPEOS", "false");
        publishLTLS = System.getProperty("EnableLargeTradeLastSale", "false");

        if (publishEOP != null && publishEOP.equalsIgnoreCase("true")) {
            MarketDataARCommandHelper.PublishEOPData.setPublishEOP(true);
        } else {
            MarketDataARCommandHelper.PublishEOPData.setPublishEOP(false);
        }
        if (publishLTLS != null && publishLTLS.equalsIgnoreCase("true")) {
            MarketDataARCommandHelper.PublishLTLSData.setPublishLTLS(true);
        } else {
            MarketDataARCommandHelper.PublishLTLSData.setPublishLTLS(false);
        } // end new code for ar command support

        create();
        //Register Forced Logout Consumer with SMS
        FoundationFramework ff = FoundationFramework.getInstance();
        SessionManagementService sms = ff.getSessionManagementService();
        SessionManagementServiceUserConsumer forcedLogoutConsumer = ServicesHelper.getForcedLogoutConsumerHome().find();
        sms.registerConsumer(forcedLogoutConsumer);
        SessionManagementServiceComponentConsumer componentConsumer = ServicesHelper.getComponentConsumerHome().find();
        sms.registerConsumerForProcessReferences(componentConsumer);
        if (Log.isDebugOn()) {
            Log.debug(this, "Registration of SMS component consumer to SMS completed...");
        }
        StartupHelper.setStartupStatus(StartupHelper.READY);
    }

    public void clientShutdown() throws Exception
    {
        StartupHelper.setStartupStatus(StartupHelper.SHUTDOWN);
    }

    public String publishExpectedOpeningPrice(String args) {
        if (args.equalsIgnoreCase(MarketDataARCommandHelper.START)) {
            MarketDataARCommandHelper.PublishEOPData.setPublishEOP(true);
            return "EOP Publishing ENABLED";
        } else if (args.equalsIgnoreCase(MarketDataARCommandHelper.STOP)) {
            MarketDataARCommandHelper.PublishEOPData.setPublishEOP(false);
            return "EOP Publishing DISABLED";
        } else if(args.equalsIgnoreCase(MarketDataARCommandHelper.STATUS)){
            return MarketDataARCommandHelper.PublishEOPData.isPublishEOP()
                   ? "EOP Publishing ENABLED"
                   : "EOP Publishing DISABLED";
        } else {
            return "USAGE : ar publishExpectedOpeningPrice start/stop/status ";
        }
    }

    public String publishLargeTradeLastSale(String args) {

        if (MarketDataARCommandHelper.START.equalsIgnoreCase(args)) {
            MarketDataARCommandHelper.PublishLTLSData.setPublishLTLS(true);
            return "LTLS Publishing ENABLED";
        } else if (MarketDataARCommandHelper.STOP.equalsIgnoreCase(args)) {
            MarketDataARCommandHelper.PublishLTLSData.setPublishLTLS(false);
            return "LTLS Publishing DISABLED";
        } else if (args.equalsIgnoreCase(MarketDataARCommandHelper.STATUS)) {
            return MarketDataARCommandHelper.PublishLTLSData.isPublishLTLS()
                   ? "LTLS Publishing ENABLED"
                   : "LTLS Publishing DISABLED";
        } else {
            return "USAGE : ar publishLargeTradeLastSale start/stop/status ";
        }
    }
}
