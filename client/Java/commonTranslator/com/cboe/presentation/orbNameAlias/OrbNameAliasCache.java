// 
//
// -----------------------------------------------------------------------------------
// Source file: OrbNameAliasCache
//
// PACKAGE: com.cboe.presentation.OrbNameAlias;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.orbNameAlias;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

import org.omg.CORBA.UserException;

import com.cboe.exceptions.AuthenticationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.NotSupportedException;
import com.cboe.interfaces.presentation.api.TimedOutException;
import com.cboe.interfaces.presentation.common.storage.Storage;
import com.cboe.interfaces.presentation.processes.OrbNameAlias;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.common.storage.AbstractStorage;
import com.cboe.presentation.orbNameAlias.events.OrbNameAliasEventConsumerIECImpl;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;

public class OrbNameAliasCache  implements EventChannelListener
{ 
    // Property Change Stuff
    public static String PROPERTY_ALIAS_ADDED = "PROPERTY_ALIAS_ADDED";
    public static String PROPERTY_ALIAS_CHANGED = "PROPERTY_ALIAS_CHANGED";

    protected PropertyChangeSupport delegate;
    protected EventChannelAdapter eventChannel;

    // Singleton
    private static OrbNameAliasCache instance;

    // The collection
    protected HashMap<String,OrbNameAlias> aliases;

    // Stuff for persistence
    protected static final String PROPERTY_NAME_PREFIX         = "orbNameAlias.";
    protected static final String PROPERTY_NAME_DISPLAY_NAME   = "displayName";
    protected static final String PROPERTY_NAME_CLUSTER        = "cluster";
    protected static final String PROPERTY_NAME_SUB_CLUSTER    = "subCluster";
    protected static final String PROPERTY_NAME_ORB_LIST       = "orbNameAlias.orbNameList";

    protected static final String PROPERTY_SECTION_NAME                   = "StorageDataSources";
    protected static final String LOCAL_ORB_NAME_ALIAS_FILE_PROPERTY_KEY  = "Local.OrbNameAliasFile";
    protected static final String REMOTE_ORB_NAME_ALIAS_FILE_PROPERTY_KEY = "Remote.OrbNameAliasFile";
    protected static final String DEFAULT_FILE_NAME                       = "globalStorage/OrbNameAlias.properties";

    private static final String SBT_PREFIX="$SBT_PREFIX";

    protected Storage remoteStorage;
    protected Storage localStorage;
    protected boolean cacheInitialized;
    protected String  remoteStorageFileName;
    protected String  localStorageFileName;
    private String sbtPrefix;

    public OrbNameAliasCache(Storage remoteStorage, Storage localStorage)
    {
        instance = this;
        eventChannel = EventChannelAdapterFactory.find();
        initialize(remoteStorage, localStorage);
        subscribeEvents();
    }

    private OrbNameAliasCache()
    {
        eventChannel = EventChannelAdapterFactory.find();
        aliases = new HashMap<String,OrbNameAlias>();
        subscribeEvents();
    }

    protected void initialize(Storage remoteStorage, Storage localStorage)
    {
        sbtPrefix="prod";
        aliases = new HashMap<String,OrbNameAlias>();
        this.remoteStorage = remoteStorage;
        this.localStorage = localStorage;
    }

    private void subscribeEvents()
    {
        unsubscribeEvents();
        eventChannel.addChannelListener(eventChannel, this, AbstractStorage.channelKeyNew);
        eventChannel.addChannelListener(eventChannel, this, AbstractStorage.channelKeyUpdate);
        
    }

    private void unsubscribeEvents()
    {
        eventChannel = EventChannelAdapterFactory.find();
        eventChannel.removeChannelListener(eventChannel, this, AbstractStorage.channelKeyNew);
        eventChannel.removeChannelListener(eventChannel, this, AbstractStorage.channelKeyUpdate);
        
    }


    public void setSbtPrefix(String prefix)
    {
        this.sbtPrefix = prefix;
    }
    public static OrbNameAliasCache getInstance()
    {
        if (instance == null)
        {
            instance = new OrbNameAliasCache();
        }
        return instance;
    }

    public void initializeCache()
    {

        boolean usedLocal = false;
        if(cacheInitialized)
        {
            return;
        }
        Properties properties = new Properties();
        try
        {
            properties = remoteStorage.retrieveProperties(getRemoteStorageFileName());
        }
        catch (IOException e)
        {
            GUILoggerHome.find().exception(e);
        }
        if(properties.size()==0)
        {
            try
            {
                properties = localStorage.retrieveProperties(getLocalStorageFileName());
                usedLocal = true;
            }
            catch (IOException e)
            {
                GUILoggerHome.find().exception(e);
            }
        }
        if(properties.size()>0)
        {
            importOrbNameAlias(properties);
            try
            {
                if(!usedLocal) // update local copy
                {
                    localStorage.store(getLocalStorageFileName(), properties);
                }
            }
            catch (IOException e)
            {
                GUILoggerHome.find().exception(e);
            }
        }
        cacheInitialized = true;
    }

    public void saveCache()
    {
        // Check to see if this gui is allowed to save
        if (isSaveAllowed())
        {
            Properties properties = exportOrbNameAlias();
            // Save local
            try
            {
                localStorage.store(getLocalStorageFileName(), properties);
            }
            catch (IOException e)
            {
                GUILoggerHome.find().exception(e);
            }
            // Save remote
            try
            {
                remoteStorage.store(getRemoteStorageFileName(), properties);
            }
            catch (IOException e)
            {
                GUILoggerHome.find().exception(e);
            }
        }
    }

    public OrbNameAlias getOrbNameAlias(String orbName)
    {
        OrbNameAlias alias;
        synchronized(instance)
        {
            alias = aliases.get(orbName);
        }

        return alias;
    }

    public  void setOrbNameAlias(OrbNameAlias alias) throws TimedOutException, AuthorizationException, AuthenticationException, NotSupportedException, UserException
    {
        OrbNameAlias oldAlias = null;
        synchronized(aliases)
        {
            oldAlias = aliases.get(alias.getOrbName());
        }

        if (oldAlias == null)
        {
            remoteStorage.addEntry(alias);
        }
        else
        {
            remoteStorage.updateEntry(alias);
        }
    }

    private void updateOrbNameAlias(OrbNameAlias alias)
    {
        Object oldAlias=null;
        synchronized (aliases)
        {
            oldAlias = aliases.put(alias.getOrbName(),alias);
        }
        if (oldAlias == null)
        {
            fireAliasAdded(alias);
        }
        else
        {
            fireAliasChanged(alias);
        }
    }
    /**
     *  Import a set of aliases from properties.  The properties are all in the format
     *  of "orbNameAlias.<orbName>.<attribute>=value".
     */
    public void importOrbNameAlias(Properties properties)
    {
        synchronized(instance)
        {
            // Find all the orbNames
            String orbNameList = properties.getProperty(PROPERTY_NAME_ORB_LIST);

            if (orbNameList != null)
            {
                // Loop through all the orbNames
                StringTokenizer orbNameTokens = new StringTokenizer(orbNameList,",");
                while (orbNameTokens.hasMoreTokens())
                {
                    String orbName = orbNameTokens.nextToken();
                    if ((orbName != null) && (!orbName.equals("")))
                    {
                        String displayName = getPropertyValue(properties,orbName,PROPERTY_NAME_DISPLAY_NAME);
                        String cluster = getPropertyValue(properties,orbName,PROPERTY_NAME_CLUSTER);
                        String subCluster = getPropertyValue(properties,orbName,PROPERTY_NAME_SUB_CLUSTER);

                        if (displayName != null && cluster != null)
                        {
                            // Build the object
                            OrbNameAlias alias = OrbNameAliasFactory.createOrbNameAlias(orbName,displayName,cluster,subCluster);
                            aliases.put(orbName,alias);
                            // Fire an add event
                            fireAliasAdded(alias);
                        }
                        else
                        {
                            StringBuffer alarmMessage = new StringBuffer();
                            alarmMessage.append("Could not import orbNameAlias for ");
                            alarmMessage.append(orbName).append(" + ");
                            alarmMessage.append(displayName).append(" + ");
                            alarmMessage.append(cluster).append(" + ");
                            alarmMessage.append(subCluster).append(" + ");
                            GUILoggerHome.find().alarm(alarmMessage.toString());
                        }
                    }
                }
            }
        }
    }

    /**
     *  Import a set of aliases from properties.  The properties are all in the format
     *  of "orbNameAlias.<orbName>.<attribute>=value".
     */
    public Properties exportOrbNameAlias()
    {
        Properties properties;
        synchronized(instance)
        {
            properties = new Properties();
            StringBuffer orbNameList = new StringBuffer();

            // Loop through each orbName
            Iterator aliasIterator = aliases.values().iterator();

            while (aliasIterator.hasNext())
            {
                // Create a property for each attribute
                OrbNameAlias alias = (OrbNameAlias) aliasIterator.next();
                addProperty(properties,alias.getOrbName(),PROPERTY_NAME_DISPLAY_NAME,alias.getDisplayName());
                addProperty(properties,alias.getOrbName(),PROPERTY_NAME_CLUSTER,alias.getCluster());
                addProperty(properties,alias.getOrbName(),PROPERTY_NAME_SUB_CLUSTER,alias.getSubCluster());
                orbNameList.append(alias.getOrbName()).append(",");
            }
            // Add the list of orbNames
            properties.setProperty(PROPERTY_NAME_ORB_LIST,orbNameList.toString());

        }

        return properties;
    }

    private void addProperty(Properties properties, String orbName, String attributeName, String value)
    {
        if (value != null)
        {
            StringBuffer name = new StringBuffer();
        
            name.append(PROPERTY_NAME_PREFIX);
            name.append(orbName).append(".");
            name.append(attributeName);

            properties.setProperty(name.toString(),value);
        }
    }

    private String getPropertyValue(Properties properties, String orbName, String attributeName)
    {
        StringBuffer name = new StringBuffer();

        name.append(PROPERTY_NAME_PREFIX);
        name.append(orbName).append(".");
        name.append(attributeName);

        return properties.getProperty(name.toString());
    }
     

    public void addAliasListener(PropertyChangeListener listener)
    {
        getPropertyChangeSupportDelegate().addPropertyChangeListener(PROPERTY_ALIAS_ADDED, listener);
        getPropertyChangeSupportDelegate().addPropertyChangeListener(PROPERTY_ALIAS_CHANGED, listener);
    }

    public void removeAliasListener(PropertyChangeListener listener)
    {
        getPropertyChangeSupportDelegate().removePropertyChangeListener(PROPERTY_ALIAS_ADDED, listener);
        getPropertyChangeSupportDelegate().removePropertyChangeListener(PROPERTY_ALIAS_CHANGED, listener);
    }


    protected PropertyChangeSupport getPropertyChangeSupportDelegate()
    {
        if(delegate == null)
        {
            delegate = new PropertyChangeSupport(this);
        }
        return delegate;
    }

    protected void fireAliasAdded(OrbNameAlias alias)
    {
        getPropertyChangeSupportDelegate().firePropertyChange(PROPERTY_ALIAS_ADDED, null, alias);

        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTATION_ORBNAME_ALIAS_UPDATE, new Integer(0));
        ChannelEvent channelEvent = eventChannel.getChannelEvent(this, channelKey, alias);
        eventChannel.dispatch(channelEvent);
    }

    protected void fireAliasChanged(OrbNameAlias alias)
    {
        getPropertyChangeSupportDelegate().firePropertyChange(PROPERTY_ALIAS_CHANGED, null, alias);

        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTATION_ORBNAME_ALIAS_UPDATE, new Integer(0));
        ChannelEvent channelEvent = eventChannel.getChannelEvent(this, channelKey, alias);
        eventChannel.dispatch(channelEvent);
    }


    public boolean isSaveAllowed()
    {
    	return remoteStorage.isSaveAllowed();
    }

    protected String getRemoteStorageFileName()
    {
        if(remoteStorageFileName == null)
        {
            remoteStorageFileName = DEFAULT_FILE_NAME;
            if( AppPropertiesFileFactory.isAppPropertiesAvailable() )
            {
                String value =
                        AppPropertiesFileFactory.find().getValue(PROPERTY_SECTION_NAME,
                                                                 REMOTE_ORB_NAME_ALIAS_FILE_PROPERTY_KEY);
                if( value != null && value.length() > 0 )
                {
                    remoteStorageFileName = value.replaceAll("\\"+SBT_PREFIX,sbtPrefix);
                }
            }
        }
        return remoteStorageFileName;
    }
 
    protected String getLocalStorageFileName()
    {
        if(localStorageFileName == null)
        {
            localStorageFileName = DEFAULT_FILE_NAME;
            if( AppPropertiesFileFactory.isAppPropertiesAvailable() )
            {
                String value =
                        AppPropertiesFileFactory.find().getValue(PROPERTY_SECTION_NAME,
                                                                 LOCAL_ORB_NAME_ALIAS_FILE_PROPERTY_KEY);
                if( value != null && value.length() > 0 )
                {
                    localStorageFileName = value.replaceAll("\\"+SBT_PREFIX,sbtPrefix);
                    
                }
            }
        }
        return localStorageFileName;
    }

    public void channelUpdate(ChannelEvent evt)
    {
        ChannelKey channelKey = (ChannelKey)evt.getChannel();
        if (channelKey.channelType == ChannelType.IC_ACCEPT_NEW_ORB_NAME_ALIAS||
                channelKey.channelType == ChannelType.IC_ACCEPT_CHANGED_ORB_NAME_ALIAS    
            )
        {
            updateOrbNameAlias((OrbNameAlias)evt.getEventData());
        }
    }
}