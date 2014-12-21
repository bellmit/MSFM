package com.cboe.presentation.orbNameAlias.events;

import org.omg.CORBA.ORB;

import com.cboe.ORBInfra.ORB.Orb;
import com.cboe.eventServiceUtilities.EventChannelUtility;
import com.cboe.idl.clusterInfoEvents.OrbNameAliasEventService;
import com.cboe.idl.clusterInfoEvents.OrbNameAliasEventServiceHelper;
import com.cboe.interfaces.events.OrbNameAliasServiceConsumer;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.publishers.eventChannel.OrbNameAliasServiceConsumerPublisherImpl;

public class OrbNameAliasEventPublisherHomeImpl{

    public static final String ADAPTERS_SECTION = "Adapters";
    public static final String ALARM_DEFINITION_CONSUMER_KEY_NAME = "AlarmDefinitionEventDelegateServiceConsumer.Class";

    protected OrbNameAliasServiceConsumer orbNameAliasPublisher;
    protected OrbNameAliasEventService orbNameAliasEventService;

    protected EventChannelUtility eventChannelUtility;
    protected String channelName;

    public void initializeOrbNameAliasPublisher(String channelName) throws Exception
    {
        this.channelName = channelName;
        connectOrbNameAliasPublisher();
    }

    protected EventChannelUtility getEventChannelUtility()
    {
        if (eventChannelUtility == null)
        {
            ORB orb = Orb.init();

            eventChannelUtility = new EventChannelUtility(orb);
            try
            {
                eventChannelUtility.startEventService();
            }
            catch (Exception e)
            {
                // todo: handle exception better
                GUILoggerHome.find().exception(e, e.getMessage());
                DefaultExceptionHandlerHome.find().process(e);
            }
        }
        return eventChannelUtility;
    }

    protected void connectOrbNameAliasPublisher()
    {
        // call the get methods to create the event channel publishers and connect them to the proxy
        getOrbNameAliasEventChannelPublisher();
        getOrbNameAliasPublisher();
    }

    public String getOrbNameAliasEventChannelName()
    {
        return channelName;
    }

    public OrbNameAliasServiceConsumer getOrbNameAliasPublisher()
    {
        if (orbNameAliasPublisher == null)
        {
        	orbNameAliasPublisher = new OrbNameAliasServiceConsumerPublisherImpl(getOrbNameAliasEventChannelPublisher());
        }
        return orbNameAliasPublisher;
    }

    protected String getOrbNameAliasEventServiceInterfaceRepId()
    {
        return OrbNameAliasEventServiceHelper.id();
    }

    protected OrbNameAliasEventService getOrbNameAliasEventChannelPublisher()
    {
        if( orbNameAliasEventService == null)
        {
            try
            {
                org.omg.CORBA.Object obj =
                        getEventChannelUtility().getEventChannelSupplierStub(   getOrbNameAliasEventChannelName(),
                        														getOrbNameAliasEventServiceInterfaceRepId());

                orbNameAliasEventService = OrbNameAliasEventServiceHelper.narrow(obj);
            }
            catch (Exception e)
            {
                IllegalStateException ise =
                        new IllegalStateException("OrbNameAliasPublisherHomeImpl: unable to create ec supplier stub for channel(" +
                        							getOrbNameAliasEventChannelName() + "). ");
                ise.initCause(e);
                throw ise;
            }
        }
        return orbNameAliasEventService;
    }
}
