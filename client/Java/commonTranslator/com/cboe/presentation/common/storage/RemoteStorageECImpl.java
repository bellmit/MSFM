/*
 * Created on Dec 27, 2005
 * -----------------------------------------------------------------------------------
 * Copyright (c) 2005 The Chicago Board Options Exchange. All Rights Reserved.
 *-----------------------------------------------------------------------------------
 */
package com.cboe.presentation.common.storage;

import java.util.Properties;

import org.omg.CORBA.UserException;

import com.cboe.exceptions.AuthenticationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.NotSupportedException;
import com.cboe.idl.clusterInfo.OrbNameAliasStruct;
import com.cboe.interfaces.presentation.api.TimedOutException;	
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.orbNameAlias.OrbNameAliasImpl;
import com.cboe.presentation.orbNameAlias.events.OrbNameAliasEventConsumerHomeImpl;
import com.cboe.presentation.orbNameAlias.events.OrbNameAliasEventPublisherHomeImpl;

public class RemoteStorageECImpl extends DefaultRemoteStorage
{
    public static final String ORB_NAME_ALIAS_CHANNEL_NAME_PROPERTY = "OrbNameAlias.ChannelName";

    private static final String SYSTEM_HEALTH_PROPERTY_SECTION = "SystemHealthMonitor";
    private static final String ADMIN_SERVICE_TIMEOUT_PROPERTY_KEY = "AdminServiceTimeout";
    private static final String CATEGORY = RemoteStorageECImpl.class.getName();
    private static final int ADMIN_SERVICE_DEFAULT_TIMEOUT = 5000;
    
    private OrbNameAliasEventPublisherHomeImpl   publisherHome;
    private OrbNameAliasSynchEventChannelDelegate       delegate;
    private OrbNameAliasEventConsumerHomeImpl    eventConsumerHome;

    public RemoteStorageECImpl()
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
            //delegate = new OrbNameAliasSynchEventChannelDelegate(publisherHome.getOrbNameAliasPublisher());
            int defaultAdminServiceTimeout = getAdminServiceDefaultTimeout();
            delegate = new OrbNameAliasSynchEventChannelDelegate(publisherHome.getOrbNameAliasPublisher(), defaultAdminServiceTimeout);
        }
        catch (Exception e)
        {
            DefaultExceptionHandlerHome.find().process(e);
        }
    }

    /* (non-Javadoc)
     * @see com.cboe.presentation.common.storage.DefaultRemoteStorage#retrieveProperties(java.lang.String)
     */
    @Override
    public Properties retrieveProperties(String name)
    {
        StringBuffer orbNames=new StringBuffer();
        Properties properties = new Properties();
        try
        {
            OrbNameAliasStruct[] aliases = delegate.publishAllOrbNameAlias();
            for (int i=0;i<aliases.length;i++)
            {
                String key = "orbNameAlias."+aliases[i].orbName+".displayName";
                properties.put(key,aliases[i].displayName);
                key = "orbNameAlias."+aliases[i].orbName+".cluster";
                properties.put(key,aliases[i].clusterName);
                key = "orbNameAlias."+aliases[i].orbName+".subCluster";
                properties.put(key,aliases[i].subClusterName);
                orbNames.append(aliases[i].orbName);
                if (i < aliases.length -1)
                {
                    orbNames.append(",");
                }
            }
            properties.put("orbNameAlias.orbNameList",orbNames.toString());
        }
        catch (Exception e)
        {
            DefaultExceptionHandlerHome.find().process(e);
        }
        return properties;
    }

    
    /* (non-Javadoc)
     * @see com.cboe.presentation.common.storage.AbstractStorage#addEntry(java.lang.Object)
     */
    @Override
    public void addEntry(Object entry) throws TimedOutException, AuthorizationException, AuthenticationException, NotSupportedException, UserException
    {
        OrbNameAliasImpl alias = (OrbNameAliasImpl)entry;
        OrbNameAliasStruct struct = new OrbNameAliasStruct();
        struct.orbName = alias.getOrbName();
        struct.clusterName = alias.getCluster();
        struct.displayName = alias.getDisplayName();
        struct.subClusterName = alias.getSubCluster();
        delegate.createOrbNameAlias(struct);
    }

    /* (non-Javadoc)
     * @see com.cboe.presentation.common.storage.AbstractStorage#removeEntry(java.lang.Object)
     */
    @Override
    public void removeEntry(Object entry) throws TimedOutException, UserException
    {
        OrbNameAliasImpl alias = (OrbNameAliasImpl)entry;
        OrbNameAliasStruct struct = new OrbNameAliasStruct();
        struct.orbName = alias.getOrbName();
        struct.clusterName = alias.getCluster();
        struct.displayName = alias.getDisplayName();
        struct.subClusterName = alias.getSubCluster();
        delegate.deleteOrbNameAlias(struct);
    }

    /* (non-Javadoc)
     * @see com.cboe.presentation.common.storage.AbstractStorage#updateEntry(java.lang.Object)
     */
    @Override
    public void updateEntry(Object entry) throws TimedOutException, UserException
    {
        OrbNameAliasImpl alias = (OrbNameAliasImpl)entry;
        OrbNameAliasStruct struct = new OrbNameAliasStruct();
        struct.orbName = alias.getOrbName();
        struct.clusterName = alias.getCluster();
        struct.displayName = alias.getDisplayName();
        struct.subClusterName = alias.getSubCluster();
        delegate.updateOrbNameAlias(struct);
    }

    public void store(String name, Properties content)
    {
    }
    
    private int getAdminServiceDefaultTimeout()
    {
        int defaultTimeout = ADMIN_SERVICE_DEFAULT_TIMEOUT;
        if (AppPropertiesFileFactory.isAppPropertiesAvailable())
        {
            String value = AppPropertiesFileFactory.find().getValue(SYSTEM_HEALTH_PROPERTY_SECTION,
                                                                    ADMIN_SERVICE_TIMEOUT_PROPERTY_KEY);
            if (value != null && value.length() > 0)
            {
                try
                {
                    defaultTimeout = Integer.parseInt(value);
                }
                catch(NumberFormatException e)
                {
                    GUILoggerHome.find().exception(CATEGORY, "Could not parse Admin Service timeout property. " +
                                                             ADMIN_SERVICE_TIMEOUT_PROPERTY_KEY + "=" + value +
                                                             ". Will use default value.", e);
                }
            }
            else
            {
                GUILoggerHome.find().alarm(CATEGORY, "Unable to retrieve Admin Service timeout property. Property " + ADMIN_SERVICE_TIMEOUT_PROPERTY_KEY + "is not specified.");
            }
        }
        else
        {
            GUILoggerHome.find().alarm(CATEGORY, "Unable to retrieve Admin Service timeout property. Application Properties Not Available.");
        }
        return defaultTimeout;
    }
}
