//
// ------------------------------------------------------------------------
// FILE: CASInformationCache.java
//
// PACKAGE: com.cboe.presentation.api
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//

package com.cboe.presentation.api;

import java.io.IOException;
import java.util.*;

import com.cboe.interfaces.casMonitor.CASInformation;
import com.cboe.interfaces.presentation.common.storage.Storage;
import com.cboe.interfaces.presentation.processes.CBOEProcess;

import com.cboe.presentation.casMonitor.CASInformationFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;

import com.cboe.client.xml.XmlBindingFacade;
import com.cboe.client.xml.bind.GICASConfigurationType;
import com.cboe.client.xml.bind.GISystemConfigurationType;

/**
 * @author torresl@cboe.com
 */
public class CASInformationCache
{
    protected static final String PROPERTY_SECTION_NAME = "StorageDataSources";
    protected static final String LOCAL_CAS_INFO_FILE_PROPERTY_KEY = "Local.CasInfoFile";
    protected static final String REMOTE_CAS_INFO_FILE_PROPERTY_KEY = "Remote.CasInfoFile";
    protected static final String DEFAULT_FILE_NAME = "globalStorage/SystemConfigurationInfo.xml";
    protected Map<String, CASInformation> casInformationCache;
    protected boolean cacheInitialized;
    protected Storage remoteStorage;
    protected Storage localStorage;
    protected String remoteStorageFileName;
    protected String localStorageFileName;
    protected List<String> firmNamesList;
    protected String[] firmNames;

    public CASInformationCache()
    {
        initialize();
        cacheInitialized = false;
    }
    public CASInformationCache(Storage remoteStorage, Storage localStorage)
    {
        this();
        setRemoteStorage(remoteStorage);
        setLocalStorage(localStorage);
    }
    private void initialize()
    {
        casInformationCache = new HashMap<String, CASInformation>(200);
        firmNamesList = new ArrayList<String>(200);
    }

    public Storage getRemoteStorage()
    {
        return remoteStorage;
    }

    public void setRemoteStorage(Storage remoteStorage)
    {
        this.remoteStorage = remoteStorage;
        if(remoteStorage == null)
        {
            throw new IllegalArgumentException("Remote Storage cannot be null");
        }
    }

    public Storage getLocalStorage()
    {
        return localStorage;
    }

    public void setLocalStorage(Storage localStorage)
    {
        this.localStorage = localStorage;
        if(localStorage == null)
        {
            throw new IllegalArgumentException("Local Storage cannot be null");
        }
    }

    private String readAndCopy(String mainLocation, String backupLocation, Storage mainStorage, Storage backupStorage, int tries)
    {
        String retVal = "";
        int count = 0;
        while(count < tries && retVal.length() == 0)
        {
            try
            {
                retVal = mainStorage.retrieveString(mainLocation);
                try
                {
                    // copy to local file
                    if(backupStorage != null && retVal.length() > 0)
                    {
                        backupStorage.store(backupLocation, retVal);
                    }
                }
                catch(IOException e)
                {
                    // cannot save to backup mainLocation, just log and continue
                    GUILoggerHome.find().exception(e, "BackupStorage Store Failed: location="+backupLocation+" storage="+backupStorage.getClass().getName());
                }
                break;
            }
            catch(IOException e)
            {
                // this could fail the first time if the previous read to the main storage failed.
                GUILoggerHome.find().exception(e, "MainStorage Retrieve Failed: location="+mainLocation+" storage="+mainStorage.getClass().getName());
            }
            count++;
        }
        return retVal;
    }
    public synchronized void reinitializeCache()
    {
        casInformationCache.clear();
        firmNamesList.clear();
        firmNames = null;
        cacheInitialized = false;
        initializeCache();
    }
    public synchronized void initializeCache()
    {
        if(cacheInitialized)
        {
            return;
        }
        String config = "";
        config = readAndCopy(getRemoteStorageFileName(),
                             getLocalStorageFileName(),
                             getRemoteStorage(),
                             getLocalStorage(),
                             2);
        if(config.length()==0 )
        {
            config = readAndCopy(getLocalStorageFileName(),
                                 null,
                                 getLocalStorage(),
                                 null,
                                 2);
        }
        if(config.length()>0)
        {
            Object object = XmlBindingFacade.getInstance().unmarshallXmlString(config);
            if(object instanceof GISystemConfigurationType)
            {
                GISystemConfigurationType sysconf = (GISystemConfigurationType) object;
                GICASConfigurationType[] casConfigurations = sysconf.getCasConfigurationsSequence().getCasConfigurations();
                for (int i = 0; i < casConfigurations.length; i++)
                {
                    GICASConfigurationType casConfiguration = casConfigurations[i];
                    CASInformation casInformation = CASInformationFactory.createCASInformation(casConfiguration);
                    casInformationCache.put(casInformation.getCasNumber().toUpperCase(), casInformation);
                    if(casInformation.getFirm().trim().length()>0)
                    {
                        firmNamesList.add(casInformation.getFirm());
                    }
                }
            }
        }
        cacheInitialized = true;
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
                                                                 REMOTE_CAS_INFO_FILE_PROPERTY_KEY);
                if( value != null && value.length() > 0 )
                {
                    remoteStorageFileName = value;
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
                                                                 LOCAL_CAS_INFO_FILE_PROPERTY_KEY);
                if( value != null && value.length() > 0 )
                {
                    localStorageFileName = value;
                }
            }
        }
        return localStorageFileName;
    }


    public synchronized CASInformation getCASInformation(CBOEProcess process)
    {
        CASInformation casInformation = null;
        if(process != null)
        {
            if(process.getHostName()!= null)
            {
                casInformation = casInformationCache.get(process.getHostName().toUpperCase());
            }
        }
        return casInformation;
    }

    public synchronized String[] getFirmNames()
    {
        if(firmNames == null)
        {
            firmNames = firmNamesList.toArray(new String[0]);
        }
        return firmNames;
    }
}
