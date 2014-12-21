// 
//
// -----------------------------------------------------------------------------------
// Source file: LogicalNameCache
//
// PACKAGE: com.cboe.presentation.logicalName;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.orbNameAlias;

import java.util.*;

import org.omg.CORBA.UserException;

import com.cboe.idl.clusterInfo.LogicalOrbNameStruct;

import com.cboe.interfaces.presentation.api.TimedOutException;
import com.cboe.interfaces.presentation.common.storage.Storage;
import com.cboe.interfaces.presentation.processes.LogicalName;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.storage.OrbNameAliasSynchEventChannelDelegate;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.orbNameAlias.events.OrbNameAliasEventPublisherHomeImpl;
import com.cboe.presentation.orbNameAlias.events.OrbNameAliasEventConsumerHomeImpl;

public class LogicalNameCache implements EventChannelListener
{
    private static final String CATEGORY = LogicalNameCache.class.getName();
    public static final String DEFAULT_LOGICAL_NAME = "Default";
    public static final String ORB_NAME_ALIAS_CHANNEL_NAME_PROPERTY = "OrbNameAlias.ChannelName";
    private OrbNameAliasEventPublisherHomeImpl publisherHome;
    private OrbNameAliasSynchEventChannelDelegate delegate;
    private OrbNameAliasEventConsumerHomeImpl eventConsumerHome;
    private static final String SYSTEM_HEALTH_PROPERTY_SECTION = "SystemHealthMonitor";
    private static final String ADMIN_SERVICE_TIMEOUT_PROPERTY_KEY = "AdminServiceTimeout";
    private static final int ADMIN_SERVICE_DEFAULT_TIMEOUT = 5000;

    protected EventChannelAdapter eventChannel;

    // Singleton
    private static LogicalNameCache instance;

    // The collection
    protected HashMap<String, LogicalName> names;
    protected boolean cacheInitialized;

    private LogicalNameCache()
    {
        instance = this;
        eventChannel = EventChannelAdapterFactory.find();
        names = new HashMap<String, LogicalName>();
        initialize();
        subscribeEvents();
    }

    /* (non-Javadoc)
    */
    public void initialize()
    {
        publisherHome = new OrbNameAliasEventPublisherHomeImpl();
        eventConsumerHome = new OrbNameAliasEventConsumerHomeImpl();
        String orbNameAliasChannelName = System.getProperty(ORB_NAME_ALIAS_CHANNEL_NAME_PROPERTY);
        try
        {
            eventConsumerHome.initializeOrbNameAliasConsumer(orbNameAliasChannelName);
            publisherHome.initializeOrbNameAliasPublisher(orbNameAliasChannelName);
            int defaultAdminServiceTimeout = getAdminServiceDefaultTimeout();
            delegate = new OrbNameAliasSynchEventChannelDelegate(publisherHome.getOrbNameAliasPublisher(), defaultAdminServiceTimeout);
        }
        catch(Exception e)
        {
            DefaultExceptionHandlerHome.find().process(e);
        }
    }

    private void subscribeEvents()
    {
        unsubscribeEvents();
        eventChannel.addChannelListener(eventChannel, this,
            new ChannelKey(ChannelType.IC_ACCEPT_NEW_LOGICAL_ORB_NAME, new Integer(0)));
        eventChannel.addChannelListener(eventChannel, this,
            new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_LOGICAL_ORB_NAME, new Integer(0)));
        eventChannel.addChannelListener(eventChannel, this,
            new ChannelKey(ChannelType.IC_ACCEPT_DELETE_LOGICAL_ORB_NAME, new Integer(0)));
    }

    private void unsubscribeEvents()
    {
        eventChannel = EventChannelAdapterFactory.find();
        eventChannel.removeChannelListener(eventChannel, this, new ChannelKey(ChannelType.IC_ACCEPT_NEW_LOGICAL_ORB_NAME, new Integer(0)));
        eventChannel.removeChannelListener(eventChannel, this, new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_LOGICAL_ORB_NAME, new Integer(0)));
        eventChannel.removeChannelListener(eventChannel, this, new ChannelKey(ChannelType.IC_ACCEPT_DELETE_LOGICAL_ORB_NAME, new Integer(0)));
    }

    public static LogicalNameCache getInstance()
    {
        if (instance == null)
        {
            instance = new LogicalNameCache();
        }
        return instance;
    }

    public void initializeCache(LogicalName[] namesArray, String icsManagerName)
    {
        if(cacheInitialized)
        {
            return;
        }
        GUILoggerHome.find().audit("LogicalNameCache.initializCache with " + namesArray.length +
                   "names and ICS Manager orb name: " + icsManagerName);
        try
        {
            for (int i = 0; i < namesArray.length; i++)
            {
                names.put(namesArray[i].getLogicalName(), namesArray[i]);
            }
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(e);
        }
        checkForDefault(icsManagerName);
        cacheInitialized = true;
    }

    private void checkForDefault(String icsManagerName)
    {
        LogicalName defaultName = names.get(DEFAULT_LOGICAL_NAME);
        if (defaultName == null)
        {
            defaultName = new LogicalNameImpl(DEFAULT_LOGICAL_NAME, icsManagerName);
            setLogicalName(defaultName);
        }
    }

    public LogicalName getLogicalName(String logicalName)
    {
        LogicalName name;
        synchronized(instance)
        {
            name = names.get(logicalName);
        }

        return name;
    }

    public LogicalName[] getAllLogicalNames()
    {
        LogicalName[] allNames = new LogicalName[names.size()];
        synchronized(instance)
        {
            allNames = names.values().toArray(allNames);
        }

        return allNames;
    }

    public  void setLogicalName(LogicalName name)
    {

        GUILoggerHome.find().audit(CATEGORY + ": setLogicalName " + name.getLogicalName() +
                                   " for orb name " + name.getOrbName());

        LogicalName oldName = null;
        synchronized(names)
        {
            oldName = names.get(name.getLogicalName());
        }
        try
        {
            if (oldName == null)
            {
                addEntry(name);
            }
            else
            {
                updateEntry(name);
            }
        }
        catch(TimedOutException t)
        {
            GUILoggerHome.find().exception(t);
        }
        catch(UserException u)
        {
            GUILoggerHome.find().exception(u);
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(e);
        }
    }

    public void removeLogicalName(LogicalName name) throws TimedOutException, UserException
    {
        GUILoggerHome.find().audit(CATEGORY + ": removeLogicalName " + name.getLogicalName() +
                                   " for orb name " + name.getOrbName());

        LogicalName oldName = null;
        synchronized(names)
        {
            oldName = names.get(name.getLogicalName());
        }

        if(oldName != null)
        {
            removeEntry(name);
        }
    }

    private void updateLogicalName(LogicalName name)
    {
        Object oldName = null;
        synchronized (names)
        {
            oldName = names.put(name.getLogicalName(), name);
        }
        if (oldName == null)
        {
            fireNameAdded(name);
        }
        else if (!name.getOrbName().equals(((LogicalName)oldName).getOrbName()))
        {
            fireNameChanged(name);
        }
    }

    private void deleteLogicalName(LogicalName name)
    {
        Object oldName = null;
        synchronized(names)
        {
            oldName = names.remove(name.getLogicalName());
        }
        if(oldName != null)
        {
            fireNameDeleted(name);
        }
    }

    protected void fireNameAdded(LogicalName name)
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTATION_LOGICAL_ORBNAME_UPDATE, new Integer(0));
        ChannelEvent channelEvent = eventChannel.getChannelEvent(this, channelKey, name);
        eventChannel.dispatch(channelEvent);
    }

    protected void fireNameChanged(LogicalName name)
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTATION_LOGICAL_ORBNAME_UPDATE, new Integer(0));
        ChannelEvent channelEvent = eventChannel.getChannelEvent(this, channelKey, name);
        eventChannel.dispatch(channelEvent);
    }

    protected void fireNameDeleted(LogicalName name)
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTATION_LOGICAL_ORBNAME_DELETE, new Integer(0));
        ChannelEvent channelEvent = eventChannel.getChannelEvent(this, channelKey, name);
        eventChannel.dispatch(channelEvent);
    }

    public void channelUpdate(ChannelEvent evt)
    {
        int aChannelType = ((ChannelKey) evt.getChannel()).channelType;

        switch(aChannelType)
        {
            case ChannelType.IC_ACCEPT_DELETE_LOGICAL_ORB_NAME:
                deleteLogicalName((LogicalName) evt.getEventData());
                break;
            case ChannelType.IC_ACCEPT_NEW_LOGICAL_ORB_NAME:
            case ChannelType.IC_ACCEPT_CHANGED_LOGICAL_ORB_NAME:
                updateLogicalName((LogicalName) evt.getEventData());
                break;
            default:
                GUILoggerHome.find()
                        .alarm("LogicalNameCache Received unknown channelType: " +
                               aChannelType);
        }
    }

    public void addEntry(Object entry) throws TimedOutException, UserException
    {
        LogicalOrbNameStruct struct = createLogicalNameStruct(entry);
        delegate.createLogicalOrbName(struct);
    }

    public void updateEntry(Object entry) throws TimedOutException, UserException
    {
        LogicalOrbNameStruct struct = createLogicalNameStruct(entry);
        delegate.updateLogicalOrbName(struct);
    }

    public void removeEntry(Object entry) throws TimedOutException, UserException
    {
        LogicalOrbNameStruct struct = createLogicalNameStruct(entry);
        delegate.deleteLogicalOrbName(struct);
    }

    private LogicalOrbNameStruct createLogicalNameStruct(Object entry)
    {
        LogicalOrbNameStruct struct = new LogicalOrbNameStruct();
        try
        {
            LogicalName name = (LogicalName) entry;
            struct.orbName = name.getOrbName();
            struct.logicalName = name.getLogicalName();
        }
        catch(Exception e)
        {
            GUILoggerHome.find()
                    .exception(CATEGORY, "Could not create LogicalNameStruct from LogicalName.", e);
        }
        return struct;
    }

    private int getAdminServiceDefaultTimeout()
    {
        int defaultTimeout = ADMIN_SERVICE_DEFAULT_TIMEOUT;
        if(AppPropertiesFileFactory.isAppPropertiesAvailable())
        {
            String value = AppPropertiesFileFactory.find()
                    .getValue(SYSTEM_HEALTH_PROPERTY_SECTION, ADMIN_SERVICE_TIMEOUT_PROPERTY_KEY);
            if(value != null && value.length() > 0)
            {
                try
                {
                    defaultTimeout = Integer.parseInt(value);
                }
                catch(NumberFormatException e)
                {
                    GUILoggerHome.find().exception(CATEGORY,
                                                   "Could not parse Admin Service timeout property. " +
                                                   ADMIN_SERVICE_TIMEOUT_PROPERTY_KEY + "=" + value +
                                                                                                    ". Will use default value.", e);
                }
            }
            else
            {
                GUILoggerHome.find().alarm(CATEGORY,
                                           "Unable to retrieve Admin Service timeout property. Property " +
                                           ADMIN_SERVICE_TIMEOUT_PROPERTY_KEY + "is not specified.");
            }
        }
        else
        {
            GUILoggerHome.find().alarm(CATEGORY,
                                       "Unable to retrieve Admin Service timeout property. Application Properties Not Available.");
        }
        return defaultTimeout;
    }
}
