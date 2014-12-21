//
// -----------------------------------------------------------------------------------
// Source file: AbstractStorageAccessor.java
//
// PACKAGE: com.cboe.presentation.common.storage
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.storage;

import java.util.*;
import java.io.IOException;

import com.cboe.interfaces.presentation.common.storage.Storage;

import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;

public abstract class AbstractStorageAccessor
{
    protected static final String PROPERTY_SECTION_NAME = "StorageDataSources";

    protected Properties cache;
    protected Storage remoteStorage;
    protected Storage localStorage;
    protected String remoteStorageFileName;
    protected String localStorageFileName;

    protected AbstractStorageAccessor()
    {
        initializeCache();
    }

    protected abstract String getLocalFileNamePropertyKey();
    protected abstract String getRemoteFileNamePropertyKey();
    protected abstract String getDefaultFileName();
    protected abstract Storage getRemoteStorage();

    protected void initializeCache()
    {
        remoteStorage = getRemoteStorage();
        localStorage = StorageManagerFactory.getStorageManager().getLocalStorage();

        cache = readAndCopy(getRemoteStorageFileName(), remoteStorage,
                            getLocalStorageFileName(), localStorage, 2);

        if(cache == null || cache.size() == 0)
        {
            cache = readAndCopy(getLocalStorageFileName(), localStorage, null, null, 2);
        }

        if(cache == null)
        {
            cache = new Properties();
        }
    }

    protected String getRemoteStorageFileName()
    {
        if(remoteStorageFileName == null)
        {
            remoteStorageFileName = getDefaultFileName();
            if(AppPropertiesFileFactory.isAppPropertiesAvailable())
            {
                String value =
                        AppPropertiesFileFactory.find().getValue(PROPERTY_SECTION_NAME,
                                                                 getRemoteFileNamePropertyKey());
                if(value != null && value.length() > 0)
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
            localStorageFileName = getDefaultFileName();
            if(AppPropertiesFileFactory.isAppPropertiesAvailable())
            {
                String value =
                        AppPropertiesFileFactory.find().getValue(PROPERTY_SECTION_NAME,
                                                                 getLocalFileNamePropertyKey());
                if(value != null && value.length() > 0)
                {
                    localStorageFileName = value;
                }
            }
        }
        return localStorageFileName;
    }

    protected Properties readAndCopy(String mainLocation, Storage mainStorage,
                                     String backupLocation, Storage backupStorage,
                                     int tries)
    {
        Properties properties = null;
        int count = 0;

        //noinspection ConstantConditions
        while(count < tries && properties == null)
        {
            try
            {
                properties = mainStorage.retrieveProperties(mainLocation);

                //noinspection NestedTryStatement
                try
                {
                    // copy to local file
                    if(backupStorage != null && backupLocation != null &&
                       backupLocation.length() > 0 && properties != null && properties.size() > 0)
                    {
                        backupStorage.store(backupLocation, properties);
                    }
                }
                catch(IOException e)
                {
                    // cannot save to backup mainLocation, just log and continue
                    GUILoggerHome.find().exception(e, "BackupStorage Store Failed: location=" +
                                                      backupLocation + " storage=" +
                                                      backupStorage.getClass().getName());
                }
                break;
            }
            catch(IOException e)
            {
                // this could fail the first time if the previous read to the main storage failed.
                GUILoggerHome.find().exception(e, "MainStorage Retrieve Failed: location=" +
                                                  mainLocation + " storage=" +
                                                  mainStorage.getClass().getName());
            }
            count++;
        }
        return properties;
    }

    protected void save()
    {
        if(localStorage.isSaveAllowed())
        {
            try
            {
                localStorage.store(getLocalStorageFileName(), cache);
            }
            catch(IOException e)
            {
                GUILoggerHome.find().exception("Could not save local copy of:" +
                                               getLocalStorageFileName(), e);
            }
        }

        if(remoteStorage.isSaveAllowed())
        {
            try
            {
                remoteStorage.store(getRemoteStorageFileName(), cache);
            }
            catch(IOException e)
            {
                GUILoggerHome.find().exception("Could not save remote copy of:" +
                                               getRemoteStorageFileName(), e);
            }
        }
    }
}
