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
package com.cboe.presentation.alarms.events;

import org.omg.CORBA.ORB;

import com.cboe.idl.alarmEvents.AlarmActivationEventService;
import com.cboe.idl.alarmEvents.AlarmActivationEventServiceHelper;
import com.cboe.idl.alarmEvents.AlarmDefinitionEventService;
import com.cboe.idl.alarmEvents.AlarmDefinitionEventServiceHelper;
import com.cboe.idl.alarmEvents.AlarmNotificationWatchdogEventService;
import com.cboe.idl.alarmEvents.AlarmNotificationWatchdogEventServiceHelper;

import com.cboe.interfaces.events.AlarmActivationEventDelegateServiceConsumer;
import com.cboe.interfaces.events.AlarmActivationServiceConsumer;
import com.cboe.interfaces.events.AlarmDefinitionEventDelegateServiceConsumer;
import com.cboe.interfaces.events.AlarmDefinitionServiceConsumer;
import com.cboe.interfaces.events.AlarmNotificationWatchdogEventDelegateServiceConsumer;
import com.cboe.interfaces.events.AlarmNotificationWatchdogServiceConsumer;
import com.cboe.interfaces.instrumentation.alarms.AlarmPublishersHome;

import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;

import com.cboe.ORBInfra.ORB.Orb;
import com.cboe.eventServiceUtilities.EventChannelUtility;

public class AlarmPublishersHomeImpl
        implements AlarmPublishersHome
{
    public static final String ADAPTERS_SECTION = "Adapters";
    public static final String ALARM_ACTIVATION_CONSUMER_KEY_NAME = "AlarmActivationEventDelegateServiceConsumer.Class";
    public static final String ALARM_DEFINITION_CONSUMER_KEY_NAME = "AlarmDefinitionEventDelegateServiceConsumer.Class";
    public static final String ALARM_WATCHDOG_CONSUMER_KEY_NAME = "AlarmWatchdogEventDelegateServiceConsumer.Class";

    protected EventChannelUtility eventChannelUtility;

    protected AlarmDefinitionEventDelegateServiceConsumer alarmDefinitionPublisher;
    protected AlarmDefinitionEventService alarmDefinitionEventService;
    protected AlarmActivationEventDelegateServiceConsumer alarmActivationPublisher;
    protected AlarmActivationEventService alarmActivationEventService;
    protected AlarmNotificationWatchdogEventDelegateServiceConsumer alarmWatchdogPublisher;
    protected AlarmNotificationWatchdogEventService alarmWatchdogEventService;

    protected String channelName;

    public void initializePublishers(String channelName) throws Exception
    {
        this.channelName = channelName;

        connectAlarmDefinitionPublisher();
        connectAlarmActivationPublisher();
        connectAlarmWatchdogPublisher();
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

    protected void connectAlarmDefinitionPublisher()
    {
        // call the get methods to create the event channel publishers and connect them to the proxy
        getAlarmDefinitionEventChannelPublisher();
        getAlarmDefinitionPublisher();
    }

    protected void connectAlarmActivationPublisher()
    {
        // call the get methods to create the event channel publishers and connect them to the proxy
        getAlarmActivationEventChannelPublisher();
        getAlarmActivationPublisher();
    }

    protected void connectAlarmWatchdogPublisher()
    {
        getAlarmWatchdogEventChannelPublisher();
        getAlarmWatchdogPublisher();
    }

    public String getChannelName()
    {
        return channelName;
    }

    public AlarmDefinitionServiceConsumer getAlarmDefinitionPublisher()
    {
        if (alarmDefinitionPublisher == null)
        {
            if(AppPropertiesFileFactory.isAppPropertiesAvailable())
            {
                String className = AppPropertiesFileFactory.find().getValue(ADAPTERS_SECTION,
                                                                            ALARM_DEFINITION_CONSUMER_KEY_NAME);
                if(className != null && className.length() > 0)
                {
                    try
                    {
                        Class interfaceClass = AlarmDefinitionEventDelegateServiceConsumer.class;

                        Class theClass = Class.forName(className);
                        Object newOBJ = theClass.newInstance();

                        if(interfaceClass.isInstance(newOBJ))
                        {
                            alarmDefinitionPublisher = (AlarmDefinitionEventDelegateServiceConsumer) newOBJ;
                            alarmDefinitionPublisher.setAlarmDefinitionEventServiceDelegate(getAlarmDefinitionEventChannelPublisher());
                        }
                        else
                        {
                            throw new IllegalArgumentException(ADAPTERS_SECTION + '=' + ALARM_DEFINITION_CONSUMER_KEY_NAME +
                                                               " is set to an invalid class. It does not support interface: " +
                                                               "com.cboe.interfaces.events.AlarmDefinitionEventDelegateServiceConsumer. className=" +
                                                               theClass.getName());
                        }
                    }
                    catch(Exception e)
                    {
                        GUILoggerHome.find().exception(getClass().getName(),
                                                       ADAPTERS_SECTION + '=' + ALARM_DEFINITION_CONSUMER_KEY_NAME +
                                                       " is set to an invalid class. Could not load class for: " + className, e);
                    }
                }
                else
                {
                    throw new IllegalArgumentException(ADAPTERS_SECTION + '=' + ALARM_DEFINITION_CONSUMER_KEY_NAME +
                                                       " is set to an invalid class. It was not specified.");
                }
            }
            else
            {
                throw new IllegalArgumentException(ADAPTERS_SECTION + '=' + ALARM_DEFINITION_CONSUMER_KEY_NAME +
                                                   " is set to an invalid class. Application Properties were not available.");
            }
        }
        return alarmDefinitionPublisher;
    }

    public AlarmActivationServiceConsumer getAlarmActivationPublisher()
    {
        if(alarmActivationPublisher == null)
        {
            if(AppPropertiesFileFactory.isAppPropertiesAvailable())
            {
                String className = AppPropertiesFileFactory.find().getValue(ADAPTERS_SECTION,
                                                                            ALARM_ACTIVATION_CONSUMER_KEY_NAME);
                if(className != null && className.length() > 0)
                {
                    try
                    {
                        Class interfaceClass = AlarmActivationEventDelegateServiceConsumer.class;

                        Class theClass = Class.forName(className);
                        Object newOBJ = theClass.newInstance();

                        if(interfaceClass.isInstance(newOBJ))
                        {
                            alarmActivationPublisher = (AlarmActivationEventDelegateServiceConsumer) newOBJ;
                            alarmActivationPublisher.setAlarmActivationEventServiceDelegate(getAlarmActivationEventChannelPublisher());
                        }
                        else
                        {
                            throw new IllegalArgumentException(ADAPTERS_SECTION + '=' +
                                                               ALARM_ACTIVATION_CONSUMER_KEY_NAME +
                                                               " is set to an invalid class. It does not support interface: " +
                                                               "com.cboe.interfaces.events.AlarmActivationEventDelegateServiceConsumer. className=" +
                                                               theClass.getName());
                        }
                    }
                    catch(Exception e)
                    {
                        GUILoggerHome.find().exception(getClass().getName(),
                                                       ADAPTERS_SECTION + '=' + ALARM_ACTIVATION_CONSUMER_KEY_NAME +
                                                       " is set to an invalid class. Could not load class for: " +
                                                       className,
                                                       e);
                    }
                }
                else
                {
                    throw new IllegalArgumentException(ADAPTERS_SECTION + '=' + ALARM_ACTIVATION_CONSUMER_KEY_NAME +
                                                       " is set to an invalid class. It was not specified.");
                }
            }
            else
            {
                throw new IllegalArgumentException(ADAPTERS_SECTION + '=' + ALARM_ACTIVATION_CONSUMER_KEY_NAME +
                                                   " is set to an invalid class. Application Properties were not available.");
            }
        }
        return alarmActivationPublisher;
    }

    public AlarmNotificationWatchdogServiceConsumer getAlarmWatchdogPublisher()
    {
        if(alarmWatchdogPublisher == null)
        {
            if(AppPropertiesFileFactory.isAppPropertiesAvailable())
            {
                String className = AppPropertiesFileFactory.find().getValue(ADAPTERS_SECTION,
                                                                            ALARM_WATCHDOG_CONSUMER_KEY_NAME);
                if(className != null && className.length() > 0)
                {
                    try
                    {
                        Class interfaceClass = AlarmNotificationWatchdogEventDelegateServiceConsumer.class;

                        Class theClass = Class.forName(className);
                        Object newOBJ = theClass.newInstance();

                        if(interfaceClass.isInstance(newOBJ))
                        {
                            alarmWatchdogPublisher = (AlarmNotificationWatchdogEventDelegateServiceConsumer) newOBJ;
                            alarmWatchdogPublisher.setAlarmNotificationWatchdogEventServiceDelegate(getAlarmWatchdogEventChannelPublisher());
                        }
                        else
                        {
                            throw new IllegalArgumentException(ADAPTERS_SECTION + '=' +
                                                               ALARM_WATCHDOG_CONSUMER_KEY_NAME +
                                                               " is set to an invalid class. It does not support interface: " +
                                                               "com.cboe.interfaces.events.AlarmNotificationWatchdogEventDelegateServiceConsumer. className=" +
                                                               theClass.getName());
                        }
                    }
                    catch(Exception e)
                    {
                        GUILoggerHome.find().exception(getClass().getName(),
                                                       ADAPTERS_SECTION + '=' + ALARM_WATCHDOG_CONSUMER_KEY_NAME +
                                                       " is set to an invalid class. Could not load class for: " +
                                                       className,
                                                       e);
                    }
                }
                else
                {
                    throw new IllegalArgumentException(ADAPTERS_SECTION + '=' + ALARM_WATCHDOG_CONSUMER_KEY_NAME +
                                                       " is set to an invalid class. It was not specified.");
                }
            }
            else
            {
                throw new IllegalArgumentException(ADAPTERS_SECTION + '=' + ALARM_WATCHDOG_CONSUMER_KEY_NAME +
                                                   " is set to an invalid class. Application Properties were not available.");
            }
        }
        return alarmWatchdogPublisher;
    }

    protected String getAlarmDefinitionEventServiceInterfaceRepId()
    {
        return AlarmDefinitionEventServiceHelper.id();
    }

    protected AlarmDefinitionEventService getAlarmDefinitionEventChannelPublisher()
    {
        if(alarmDefinitionEventService == null)
        {
            try
            {
                org.omg.CORBA.Object obj =
                        getEventChannelUtility().getEventChannelSupplierStub(getChannelName(),
                                                                             getAlarmDefinitionEventServiceInterfaceRepId());

                alarmDefinitionEventService = AlarmDefinitionEventServiceHelper.narrow(obj);
            }
            catch (Exception e)
            {
                IllegalStateException ise =
                        new IllegalStateException("AlarmPublishersHomeImpl: unable to create ec supplier stub for channel(" +
                                                  getChannelName() + "). ");
                ise.initCause(e);
                throw ise;
            }
        }
        return alarmDefinitionEventService;
    }

    protected String getAlarmActivationEventServiceInterfaceRepId()
    {
        return AlarmActivationEventServiceHelper.id();
    }

    protected AlarmActivationEventService getAlarmActivationEventChannelPublisher()
    {
        if (alarmActivationEventService == null)
        {
            try
            {
                org.omg.CORBA.Object obj =
                        getEventChannelUtility().getEventChannelSupplierStub(getChannelName(),
                                                                             getAlarmActivationEventServiceInterfaceRepId());
                alarmActivationEventService = AlarmActivationEventServiceHelper.narrow(obj);
            }
            catch (Exception e)
            {
                IllegalStateException ise =
                        new IllegalStateException("AlarmPublishersHomeImpl: unable to create ec supplier stub for channel(" +
                                                  getChannelName() + "). ");
                ise.initCause(e);
                throw ise;
            }
        }
        return alarmActivationEventService;
    }

    protected String getAlarmWatchdogEventServiceInterfaceRepId()
    {
        return AlarmNotificationWatchdogEventServiceHelper.id();
    }

    protected AlarmNotificationWatchdogEventService getAlarmWatchdogEventChannelPublisher()
    {
        if(alarmWatchdogEventService == null)
        {
            try
            {
                org.omg.CORBA.Object obj =
                        getEventChannelUtility().getEventChannelSupplierStub(getChannelName(),
                                                                             getAlarmWatchdogEventServiceInterfaceRepId());
                alarmWatchdogEventService = AlarmNotificationWatchdogEventServiceHelper.narrow(obj);
            }
            catch(Exception e)
            {
                IllegalStateException ise =
                        new IllegalStateException("AlarmPublishersHomeImpl: unable to create ec supplier stub for channel(" +
                                                  getChannelName() + "). ");
                ise.initCause(e);
                throw ise;
            }
        }
        return alarmWatchdogEventService;
    }
}
