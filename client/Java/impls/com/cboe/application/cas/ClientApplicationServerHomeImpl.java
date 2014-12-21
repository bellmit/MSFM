package com.cboe.application.cas;

/**
 * This class is the CAS server.  It must be run before the GUI
 * is started.
 *
 * When the CAS is started it will create a SBTSystemCore object.
 * SBTSystemCore is a container of all of the objects that exist
 * as singletons within the CAS.  These objects include the
 * system market query service and the product query service.
 *
 * The CAS also creates a SBTAccess object and exposes it through
 * CORBA by publishing its IOR to a file on the C:\ drive called
 * IOR.txt.
 *
 * @author Jeff Illian
 */
import com.cboe.application.shared.RemoteConnection;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.domain.startup.StartupHelper;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.sessionManagementService.SessionManagementService;
import com.cboe.infrastructureServices.sessionManagementService.SessionManagementServiceUserConsumer;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.factories.HeapInstrumentorFactory;
import com.cboe.instrumentationService.instrumentors.HeapInstrumentor;
import com.cboe.instrumentationService.impls.HeapInstrumentorDefaultFactory;
import com.cboe.interfaces.application.ClientApplicationServerHome;
import com.cboe.interfaces.application.VersionQuery;
import com.cboe.util.event.EventChannelAdapterFactory;

public class ClientApplicationServerHomeImpl extends ClientBOHome implements ClientApplicationServerHome {
    /**
     * ClientApplicationServer constructor comment.
     */
    public ClientApplicationServerHomeImpl() {
        super();
    }
    /**
     *
     * @author Jeff Illian
     *
     */
    public void clientStart()
    {
        // The SBTConnection object is the object that manages
        // all of the CAS-Client CORBA interaction.

        //Register Forced Logout Consumer with SMS
        FoundationFramework ff = FoundationFramework.getInstance();
        SessionManagementService sms = ff.getSessionManagementService();
        SessionManagementServiceUserConsumer forcedLogoutConsumer = ServicesHelper.getForcedLogoutConsumerHome().find();
        sms.registerConsumer(forcedLogoutConsumer);
        //Register UserLoadManagerConsumer
        SessionManagementServiceUserConsumer userLoadConsumer = ServicesHelper.getUserLoadManagerHome().find();
        sms.registerConsumer(userLoadConsumer);
        // Connect the SBTAccess object to the Orb and
        // publish its IOR to a text file.
        if (Log.isDebugOn())
        {
            Log.debug(this, "CAS Initialization complete.  Awaiting connections...");
        }
        StartupHelper.setStartupStatus(StartupHelper.READY);
    }

    public void clientInitialize()
        throws Exception
    {
        String[] args = com.cboe.client.util.CollectionHelper.EMPTY_String_ARRAY;
        RemoteConnection connection = RemoteConnectionFactory.create(args);
        EventChannelAdapterFactory.find().setDynamicChannels(true);
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
        if (Log.isDebugOn())
        {
            Log.debug(this, "SMA Type = " + this.getSmaType());
        }
    }

    public void clientShutdown()
    {
        StartupHelper.setStartupStatus(StartupHelper.SHUTDOWN);
    }
}
