package com.cboe.presentation.orbNameAlias.events;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.Servant;

import com.cboe.ORBInfra.ORB.Orb;
import com.cboe.consumers.eventChannel.OrbNameAliasConsumerProxyImpl;
import com.cboe.eventServiceUtilities.EventChannelUtility;
import com.cboe.idl.clusterInfoEvents.OrbNameAliasEventConsumerHelper;
import com.cboe.interfaces.events.OrbNameAliasConsumer;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.logging.GUILoggerHome;

public class OrbNameAliasEventConsumerHomeImpl  
{
    protected EventChannelUtility eventChannelUtility;
    protected OrbNameAliasConsumer orbNameAliasConsumerDelegate;
    protected OrbNameAliasConsumer orbNameAliasEventChannelConsumer;

    protected String orbNameAliasEventChannelName;

    /**
     * Initialize the OrbNameAliasConsumer Home.  This method will create the consumer and attach them to the
     * Event Channel.
     */
    public void initializeOrbNameAliasConsumer(String orbNameAliasEventChannelName)
            throws Exception
    {
        this.orbNameAliasEventChannelName = orbNameAliasEventChannelName;
        connectOrbNameAliasConsumer();
    }

    public String getOrbNamealiasEventChannelName()
    {
        return this.orbNameAliasEventChannelName;
    }
    
    protected EventChannelUtility getEventChannelUtility()
    {
        if(eventChannelUtility == null)
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

    public OrbNameAliasConsumer getOrbNameAliasConsumer()
    {
        return getOrbNameAliasConsumerDelegate();
    }

    protected OrbNameAliasConsumer getOrbNameAliasConsumerDelegate()
    {
        if (orbNameAliasConsumerDelegate == null)
        {
        	orbNameAliasConsumerDelegate = new OrbNameAliasEventConsumerIECImpl();
        }
        return orbNameAliasConsumerDelegate;
    }

    protected OrbNameAliasConsumer getOrbNameAliasEventChannelConsumer()
    {
        if (orbNameAliasEventChannelConsumer == null)
        {
        	orbNameAliasEventChannelConsumer = new OrbNameAliasConsumerProxyImpl(getOrbNameAliasConsumerDelegate());
        }
        return orbNameAliasEventChannelConsumer;
    }

    protected String getOrbNameAliasInterfaceRepId()
    {
        return OrbNameAliasEventConsumerHelper.id();
    }

    protected void connectOrbNameAliasConsumer()
    {
        try
        {
            getEventChannelUtility().connectConsumer( getOrbNamealiasEventChannelName(),
            										  getOrbNameAliasInterfaceRepId(),
                                                      (Servant) getOrbNameAliasEventChannelConsumer(),
                                                      null);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, e.getMessage());
            DefaultExceptionHandlerHome.find().process(e);
        }
    }
}