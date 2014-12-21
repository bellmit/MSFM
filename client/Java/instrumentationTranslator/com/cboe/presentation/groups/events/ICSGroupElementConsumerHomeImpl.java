//
// ------------------------------------------------------------------------
// FILE: ICSGroupElementConsumerHomeImpl.java
// 
// PACKAGE: com.cboe.presentation.alarms.events
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2005 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//
package com.cboe.presentation.groups.events;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.Servant;

import com.cboe.idl.icsGroupElementEvents.ICSGroupElementEventConsumerHelper;
import com.cboe.interfaces.events.ICSGroupElementConsumer;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.ORBInfra.ORB.Orb;
import com.cboe.eventServiceUtilities.EventChannelUtility;
import com.cboe.consumers.eventChannel.ICSGroupElementConsumerProxyImpl;

public class ICSGroupElementConsumerHomeImpl
        implements ICSGroupElementConsumerHome
{
    protected EventChannelUtility eventChannelUtility;
    protected ICSGroupElementConsumer groupElementConsumerDelegate;
    protected ICSGroupElementConsumer groupElementEventChannelConsumer;

    protected String groupElementEventChannelName;

    /**
     * Initialize the group element home. This method will create the consumer and attach them to the Event
     * Channel.
     */
    public void initializeConsumer(String groupElementEventChannelName)
            throws Exception
    {
        this.groupElementEventChannelName = groupElementEventChannelName;
        connectGroupElementConsumer();
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

    public ICSGroupElementConsumer getICSGroupElementConsumer()
    {
        return getICSGroupElementConsumerDelegate();
    }


    protected ICSGroupElementConsumer getICSGroupElementConsumerDelegate()
    {
        if (groupElementConsumerDelegate == null)
        {
            groupElementConsumerDelegate = new ICSGroupElementConsumerImpl();
        }
        return groupElementConsumerDelegate;
    }

    protected ICSGroupElementConsumer getICSGroupElementEventChannelConsumer()
    {
        if (groupElementEventChannelConsumer == null)
        {

            groupElementEventChannelConsumer = new ICSGroupElementConsumerProxyImpl(getICSGroupElementConsumerDelegate());
        }
        return groupElementEventChannelConsumer;
    }


    public String getICSGroupElementEventChannelName()
    {
        return groupElementEventChannelName;
    }

    protected String getICSGroupElementInterfaceRepId()
    {
        return ICSGroupElementEventConsumerHelper.id();
    }

    protected void connectGroupElementConsumer()
    {
        try
        {
            getEventChannelUtility().connectConsumer(getICSGroupElementEventChannelName(),
                                                     getICSGroupElementInterfaceRepId(),
                                                     (Servant) getICSGroupElementEventChannelConsumer(),
                                                     null);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, e.getMessage());
            DefaultExceptionHandlerHome.find().process(e);
        }
    }
}
