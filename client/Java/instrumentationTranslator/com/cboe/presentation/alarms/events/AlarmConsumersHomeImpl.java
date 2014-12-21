//
// ------------------------------------------------------------------------
// FILE: AlarmConsumersHomeImpl.java
// 
// PACKAGE: com.cboe.presentation.alarms.events
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2005 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//
package com.cboe.presentation.alarms.events;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.Servant;

import com.cboe.idl.alarmEvents.AlarmActivationEventConsumerHelper;
import com.cboe.idl.alarmEvents.AlarmDefinitionEventConsumerHelper;
import com.cboe.idl.alarmEvents.AlarmNotificationEventConsumerHelper;
import com.cboe.idl.alarmEvents.AlarmNotificationWatchdogEventConsumerHelper;

import com.cboe.interfaces.events.AlarmActivationConsumer;
import com.cboe.interfaces.events.AlarmDefinitionConsumer;
import com.cboe.interfaces.events.AlarmNotificationConsumer;
import com.cboe.interfaces.events.AlarmNotificationWatchdogConsumer;
import com.cboe.interfaces.instrumentation.alarms.AlarmConsumersHome;

import com.cboe.presentation.alarms.events.consumers.AlarmActivationConsumerImpl;
import com.cboe.presentation.alarms.events.consumers.AlarmDefinitionConsumerImpl;
import com.cboe.presentation.alarms.events.consumers.AlarmNotificationConsumerImpl;
import com.cboe.presentation.alarms.events.consumers.AlarmWatchdogConsumerImpl;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.ORBInfra.ORB.Orb;
import com.cboe.consumers.eventChannel.AlarmActivationConsumerProxyImpl;
import com.cboe.consumers.eventChannel.AlarmDefinitionConsumerProxyImpl;
import com.cboe.consumers.eventChannel.AlarmNotificationConsumerProxyImpl;
import com.cboe.consumers.eventChannel.AlarmWatchdogConsumerProxyImpl;
import com.cboe.eventServiceUtilities.EventChannelUtility;

public class AlarmConsumersHomeImpl
        implements AlarmConsumersHome
{
    protected EventChannelUtility eventChannelUtility;
    protected AlarmNotificationConsumer alarmNotificationConsumerDelegate;
    protected AlarmNotificationConsumer alarmNotificationEventChannelConsumer;
    protected AlarmDefinitionConsumer alarmDefinitionConsumerDelegate;
    protected AlarmDefinitionConsumer alarmDefinitionEventChannelConsumer;
    protected AlarmActivationConsumer alarmActivationConsumerDelegate;
    protected AlarmActivationConsumer alarmActivationEventChannelConsumer;
    protected AlarmNotificationWatchdogConsumer alarmWatchdogConsumerDelegate;
    protected AlarmNotificationWatchdogConsumer alarmWatchdogEventChannelConsumer;

    protected String alarmNotificationEventChannelName;
    protected String alarmDefinitionActivationEventChannelName;

    /**
     * Initialize the Definition/Activation Home.  This method will create the consumer and attach them to the Event
     * Channel.
     */
    public void initializeDefinitionConsumer(String alarmDefinitionActivationEventChannelName)
            throws Exception
    {
        this.alarmDefinitionActivationEventChannelName = alarmDefinitionActivationEventChannelName;
        connectAlarmDefinitionConsumer();
        connectAlarmActivationConsumer();
        connectAlarmWatchdogConsumer();
    }

    /**
     * Initialize the Notification Home.  This method will create the consumer and attach them to the Event Channel.
     */
    public void initializeNotificationConsumer(String alarmNotificationEventChannelName, boolean connectConsumer)
            throws Exception
    {
        this.alarmNotificationEventChannelName = alarmNotificationEventChannelName;
        if(connectConsumer)
        {
            connectAlarmNotificationConsumer();
        }
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

    public AlarmNotificationConsumer getAlarmNotificationConsumer()
    {
        return getAlarmNotificationConsumerDelegate();
    }

    protected AlarmNotificationConsumer getAlarmNotificationConsumerDelegate()
    {
        if(alarmNotificationConsumerDelegate == null)
        {
            alarmNotificationConsumerDelegate = new AlarmNotificationConsumerImpl();
        }
        return alarmNotificationConsumerDelegate;
    }

    protected AlarmNotificationConsumer getAlarmNotificationEventChannelConsumer()
    {
        if(alarmNotificationEventChannelConsumer == null)
        {
            alarmNotificationEventChannelConsumer = new AlarmNotificationConsumerProxyImpl(getAlarmNotificationConsumerDelegate());
        }
        return alarmNotificationEventChannelConsumer;
    }

    public AlarmDefinitionConsumer getAlarmDefinitionConsumer()
    {
        return getAlarmDefinitionConsumerDelegate();
    }

    protected AlarmDefinitionConsumer getAlarmDefinitionConsumerDelegate()
    {
        if (alarmDefinitionConsumerDelegate == null)
        {
            alarmDefinitionConsumerDelegate = new AlarmDefinitionConsumerImpl();
        }
        return alarmDefinitionConsumerDelegate;
    }

    protected AlarmDefinitionConsumer getAlarmDefinitionEventChannelConsumer()
    {
        if (alarmDefinitionEventChannelConsumer == null)
        {
            alarmDefinitionEventChannelConsumer = new AlarmDefinitionConsumerProxyImpl(getAlarmDefinitionConsumerDelegate());
        }
        return alarmDefinitionEventChannelConsumer;
    }

    public AlarmActivationConsumer getAlarmActivationConsumer()
    {
        return getAlarmActivationConsumerDelegate();
    }

    protected AlarmActivationConsumer getAlarmActivationConsumerDelegate()
    {
        if (alarmActivationConsumerDelegate == null)
        {
            alarmActivationConsumerDelegate = new AlarmActivationConsumerImpl();
        }
        return alarmActivationConsumerDelegate;
    }

    protected AlarmActivationConsumer getAlarmActivationEventChannelConsumer()
    {
        if (alarmActivationEventChannelConsumer == null)
        {
            alarmActivationEventChannelConsumer = new AlarmActivationConsumerProxyImpl(getAlarmActivationConsumerDelegate());
        }
        return alarmActivationEventChannelConsumer;
    }

    public AlarmNotificationWatchdogConsumer getAlarmNotificationWatchdogConsumer()
    {
        return getAlarmWatchdogConsumerDelegate();
    }

    private AlarmNotificationWatchdogConsumer getAlarmWatchdogConsumerDelegate()
    {
        if(alarmWatchdogConsumerDelegate == null)
        {
            alarmWatchdogConsumerDelegate = new AlarmWatchdogConsumerImpl();
        }
        return alarmWatchdogConsumerDelegate;
    }

    private AlarmNotificationWatchdogConsumer getAlarmWatchdogEventChannelConsumer()
    {
        if(alarmWatchdogEventChannelConsumer == null)
        {
            alarmWatchdogEventChannelConsumer =
                new AlarmWatchdogConsumerProxyImpl(getAlarmWatchdogConsumerDelegate());
        }
        return alarmWatchdogEventChannelConsumer;
    }

    public String getAlarmNotificationEventChannelName()
    {
        return alarmNotificationEventChannelName;
    }

    public String getAlarmDefinitionActivationWatchdogEventChannelName()
    {
        return alarmDefinitionActivationEventChannelName;
    }

    protected String getAlarmNotificationInterfaceRepId()
    {
        return AlarmNotificationEventConsumerHelper.id();
    }

    protected String getAlarmDefinitionInterfaceRepId()
    {
        return AlarmDefinitionEventConsumerHelper.id();
    }

    protected String getAlarmActivationInterfaceRepId()
    {
        return AlarmActivationEventConsumerHelper.id();
    }

    protected String getAlarmWatchdogInterfaceRepId()
    {
        return AlarmNotificationWatchdogEventConsumerHelper.id();
    }

    protected void connectAlarmNotificationConsumer()
    {
        try
        {
            getEventChannelUtility().connectConsumer(getAlarmNotificationEventChannelName(),
                                                     getAlarmNotificationInterfaceRepId(),
                                                     (Servant) getAlarmNotificationEventChannelConsumer(),
                                                     null);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, e.getMessage());
            DefaultExceptionHandlerHome.find().process(e);
        }
    }

    protected void connectAlarmDefinitionConsumer()
    {
        try
        {
            getEventChannelUtility().connectConsumer(getAlarmDefinitionActivationWatchdogEventChannelName(),
                                                     getAlarmDefinitionInterfaceRepId(),
                                                     (Servant) getAlarmDefinitionEventChannelConsumer(),
                                                     null);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, e.getMessage());
            DefaultExceptionHandlerHome.find().process(e);
        }
    }

    protected void connectAlarmActivationConsumer()
    {
        try
        {
            getEventChannelUtility().connectConsumer(getAlarmDefinitionActivationWatchdogEventChannelName(),
                                                     getAlarmActivationInterfaceRepId(),
                                                     (Servant) getAlarmActivationEventChannelConsumer(),
                                                     null);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, e.getMessage());
            DefaultExceptionHandlerHome.find().process(e);
        }
    }

    protected void connectAlarmWatchdogConsumer()
    {
        try
        {
            getEventChannelUtility().connectConsumer(getAlarmDefinitionActivationWatchdogEventChannelName(),
                                                     getAlarmWatchdogInterfaceRepId(),
                                                     (Servant) getAlarmWatchdogEventChannelConsumer(),
                                                     null);
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(e, e.getMessage());
            DefaultExceptionHandlerHome.find().process(e);
        }
    }
}
