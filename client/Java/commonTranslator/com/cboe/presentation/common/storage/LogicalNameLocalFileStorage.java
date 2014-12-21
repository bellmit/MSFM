//
// -----------------------------------------------------------------------------------
// Source file: ARCommLocalFileStorage.java
//
// PACKAGE: com.cboe.presentation.common.storage
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.storage;

import org.omg.CORBA.UserException;

import com.cboe.idl.clusterInfo.LogicalOrbNameStruct;

import com.cboe.interfaces.presentation.api.TimedOutException;
import com.cboe.interfaces.presentation.processes.LogicalName;

import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.orbNameAlias.events.OrbNameAliasEventConsumerHomeImpl;
import com.cboe.presentation.orbNameAlias.events.OrbNameAliasEventPublisherHomeImpl;

public class LogicalNameLocalFileStorage extends LocalFileStorage
{
    public static final String ORB_NAME_ALIAS_CHANNEL_NAME_PROPERTY = "OrbNameAlias.ChannelName";
    private OrbNameAliasEventPublisherHomeImpl publisherHome;
    private OrbNameAliasSynchEventChannelDelegate delegate;
    private OrbNameAliasEventConsumerHomeImpl eventConsumerHome;
    private static final String SYSTEM_HEALTH_PROPERTY_SECTION = "SystemHealthMonitor";
    private static final String ADMIN_SERVICE_TIMEOUT_PROPERTY_KEY = "AdminServiceTimeout";
    private static final String CATEGORY = LogicalNameLocalFileStorage.class.getName();
    private static final int ADMIN_SERVICE_DEFAULT_TIMEOUT = 5000;

    public LogicalNameLocalFileStorage()
    {
        super();
        initializeStorage();
    }

    /* (non-Javadoc)
    * @see com.cboe.presentation.common.storage.DefaultRemoteStorage#initializeStorage()
    */
    @Override
    public void initializeStorage()
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
        catch (Exception e)
        {
            GUILoggerHome.find().exception(CATEGORY,
               "Could not create LogicalNameStruct from LogicalName.", e);
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