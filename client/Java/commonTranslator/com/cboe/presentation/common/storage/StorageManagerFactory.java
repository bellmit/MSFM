//
// ------------------------------------------------------------------------
// FILE: StorageManagerFactory.java
//
// PACKAGE: com.cboe.presentation.common.storage
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//
package com.cboe.presentation.common.storage;

import com.cboe.interfaces.presentation.common.storage.StorageManager;

public class StorageManagerFactory
{
    private static StorageManager storageManagerInstance;

    private StorageManagerFactory()
    {
    }

    public static StorageManager createStorageManager()
    {
        return new StorageManagerImpl();
    }

    public static synchronized StorageManager getStorageManager()
    {
        if(storageManagerInstance == null)
        {
            storageManagerInstance = createStorageManager();
        }
        return storageManagerInstance;
    }
}
