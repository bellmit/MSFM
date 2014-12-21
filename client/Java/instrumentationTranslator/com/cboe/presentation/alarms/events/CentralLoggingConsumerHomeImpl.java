//
// -----------------------------------------------------------------------------------
// Source file: CentralLoggingConsumerHomeImpl.java
//
// PACKAGE: com.cboe.presentation.alarms.events.consumers
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.alarms.events;

import java.util.*;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.Servant;

import com.cboe.idl.infrastructureServices.loggingService.corba.LoggingServerHelper;

import com.cboe.interfaces.events.CentralLoggingConsumer;
import com.cboe.interfaces.instrumentationCollector.CentralLoggingConsumerHome;

import com.cboe.presentation.alarms.events.consumers.CentralLoggingConsumerImpl;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;

import com.cboe.ORBInfra.ORB.Orb;
import com.cboe.consumers.eventChannel.CentralLoggingConsumerProxyImpl;
import com.cboe.eventServiceUtilities.EventChannelUtility;
import com.cboe.eventServiceUtilities.FilterUtility;

public class CentralLoggingConsumerHomeImpl implements CentralLoggingConsumerHome
{
    protected EventChannelUtility eventChannelUtility;
    protected CentralLoggingConsumer centralLoggingConsumerDelegate;
    protected CentralLoggingConsumer centralLoggingEventChannelConsumer;
    protected String centralLoggingEventChannelName;

    private ArrayList<FilterUtility> filters;

    private static final String FILTER_SUBSTRING_CAP = "XTP";
    private static final String FILTER_SUBSTRING_LOW = "xtp";

    private static final String[] FILTER_METHODS = {"log", "clearAlarm",};

    public CentralLoggingConsumerHomeImpl()
    {
        filters = new ArrayList<FilterUtility>( FILTER_METHODS.length );
        setUpFilters();
    }

    /**
     * Initialize the CentralLoggingConsumer Home.  This method will create the consumer and attach them to the Event
     * Channel.
     */
    public void initializeCentralLoggingServiceConsumer( String centralLoggingServiceEventChannelName )
            throws Exception
    {
        centralLoggingEventChannelName = centralLoggingServiceEventChannelName;
        connectCentralLoggingConsumer();
    }

    protected EventChannelUtility getEventChannelUtility()
    {
        if( eventChannelUtility == null )
        {
            ORB orb = Orb.init();

            eventChannelUtility = new EventChannelUtility( orb );
            try
            {
                eventChannelUtility.startEventService();
            }
            catch( Exception e )
            {
                DefaultExceptionHandlerHome.find().process(e);
            }
        }
        return eventChannelUtility;
    }

    public CentralLoggingConsumer getCentralLoggingConsumer()
    {
        return getCentralLoggingDelegate();
    }

    protected CentralLoggingConsumer getCentralLoggingDelegate()
    {
        if( centralLoggingConsumerDelegate == null )
        {
            centralLoggingConsumerDelegate = new CentralLoggingConsumerImpl();
        }
        return centralLoggingConsumerDelegate;
    }

    protected CentralLoggingConsumer getCentralLoggingEventChannelConsumer()
    {
        if( centralLoggingEventChannelConsumer == null )
        {
            centralLoggingEventChannelConsumer = new CentralLoggingConsumerProxyImpl( getCentralLoggingDelegate() );
        }
        return centralLoggingEventChannelConsumer;
    }


    public String getCentralLoggingEventChannelName()
    {
        return centralLoggingEventChannelName;
    }

    protected String getCentralLoggingInterfaceRepId()
    {
        return LoggingServerHelper.id();
    }

    protected void connectCentralLoggingConsumer()
    {
        try
        {
            getEventChannelUtility().connectConsumer(getCentralLoggingEventChannelName(),
                                                     getCentralLoggingInterfaceRepId(),
                                                     (Servant) getCentralLoggingEventChannelConsumer(),
                                                     getFilters());
            getEventChannelUtility().applyFilters(getCentralLoggingEventChannelName(),
                                                  getCentralLoggingInterfaceRepId(),
                                                  (Servant) getCentralLoggingEventChannelConsumer(),
                                                  getFilters());
        }
        catch(Exception e)
        {
            DefaultExceptionHandlerHome.find().process(e);
        }
    }

    private ArrayList<FilterUtility> getFilters()
    {
        return filters;
    }

    /**
     * Create a filter to apply filter directly on to event channel
     * i.e. filter on Message.tag which contains "XTP" and Message.messageId > 0
     */
    private void setUpFilters()
    {
        for( int i = 0; i < FILTER_METHODS.length; i++ )
        {
            String methodName = FILTER_METHODS[i];

            StringBuffer constraint = new StringBuffer(200);
            constraint.append('\'').append(FILTER_SUBSTRING_CAP).append("'~ ");
            constraint.append("$.").append(methodName).append(".message.tag").append(" or ");
            constraint.append('\'').append(FILTER_SUBSTRING_LOW).append("'~ ");
            constraint.append("$.").append(methodName).append(".message.tag").append(" and ");
            constraint.append("$.").append(methodName).append(".message.messageId > 0");

            filters.add(new FilterUtility(methodName, constraint.toString(), getCentralLoggingInterfaceRepId(), true));
        }
    }
}
