//
// ------------------------------------------------------------------------
// FILE: AlarmPublishersHomeImpl.java
// 
// PACKAGE: com.cboe.presentation.alarms.events
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2005 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//
package com.cboe.presentation.groups.events;

import org.omg.CORBA.ORB;

import com.cboe.idl.icsGroupElementEvents.ICSGroupElementEventService;
import com.cboe.idl.icsGroupElementEvents.ICSGroupElementEventServiceHelper;

import com.cboe.interfaces.events.*;

import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.ORBInfra.ORB.Orb;
import com.cboe.eventServiceUtilities.EventChannelUtility;
import com.cboe.publishers.eventChannel.ICSGroupElementServiceConsumerPublisherImpl;

public class ICSGroupElementPublisherHomeImpl implements ICSGroupElementPublisherHome
{
    protected EventChannelUtility eventChannelUtility;

    protected ICSGroupElementEventDelegateServiceConsumer groupElementPublisher;
    protected ICSGroupElementEventService groupElementEventService;

    protected String channelName;

    public void initializePublisher(String channelName) throws Exception
    {
        this.channelName = channelName;

        // call the get methods to create the event channel publishers and connect them to the proxy
        getICSGroupElementEventChannelPublisher();
        getICSGroupElementPublisher();
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


    public String getChannelName()
    {
        return channelName;
    }

    public ICSGroupElementEventDelegateServiceConsumer getICSGroupElementPublisher()
    {
        if (groupElementPublisher == null)
        {
            groupElementPublisher = new ICSGroupElementServiceConsumerPublisherImpl();
            groupElementPublisher.setGroupElementEventServiceDelegate(getICSGroupElementEventChannelPublisher());
        }
        return groupElementPublisher;
    }


    protected String getICSGroupElementEventServiceInterfaceRepId()
    {
        return ICSGroupElementEventServiceHelper.id();
    }

    protected ICSGroupElementEventService getICSGroupElementEventChannelPublisher()
    {
        if(groupElementEventService == null)
        {
            try
            {
                org.omg.CORBA.Object obj =
                        getEventChannelUtility().getEventChannelSupplierStub(getChannelName(),
                                                                             getICSGroupElementEventServiceInterfaceRepId());

                groupElementEventService = ICSGroupElementEventServiceHelper.narrow(obj);
            }
            catch (Exception e)
            {
                IllegalStateException ise =
                        new IllegalStateException("ICSGroupElementPublisherHomeImpl: unable to create ec supplier stub for channel(" +
                                                  getChannelName() + "). ");
                ise.initCause(e);
                throw ise;
            }
        }
        return groupElementEventService;
    }

}