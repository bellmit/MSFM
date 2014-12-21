/*
 * Created on Jun 10, 2005
 * -----------------------------------------------------------------------------------
 * Copyright (c) 2005 The Chicago Board Options Exchange. All Rights Reserved.
 *-----------------------------------------------------------------------------------
 */
package com.cboe.infra.presentation.api;

import com.cboe.interfaces.presentation.processes.OrbProcessCache;
import com.cboe.interfaces.presentation.processes.OrbProcessCacheFactory;
import com.cboe.interfaces.presentation.common.storage.StorageManager;

import com.cboe.presentation.environment.EnvironmentManagerFactory;
import com.cboe.presentation.environment.EnvironmentProperties;
import com.cboe.presentation.common.storage.RemoteStorageECImpl;
import com.cboe.presentation.common.storage.StorageManagerFactory;
import com.cboe.presentation.orbNameAlias.OrbNameAliasCache;

import com.cboe.infra.presentation.network.Network;
import com.cboe.infra.presentation.network.SBTLiveNode;

/**
 * @author I Nyoman Mahartayasa
 */
public class OrbProcessCacheFactoryIMMImpl implements OrbProcessCacheFactory
{
    private static OrbProcessCache instance;
    /**
     * 
     */
    public OrbProcessCacheFactoryIMMImpl()
    {
        super();
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.OrbProcessCacheFactory#getProcessCache()
     */
    public OrbProcessCache getProcessCache()
    {
        if (instance == null)
        {
            EnvironmentProperties env = EnvironmentManagerFactory.find().getCurrentEnvironment();

            if (env != null)
            {
                String prefix = env.getSBTPrefix();
                String orbNameAliasChannel = System.getProperty(RemoteStorageECImpl.ORB_NAME_ALIAS_CHANNEL_NAME_PROPERTY);
                if (orbNameAliasChannel == null)
                {
                    orbNameAliasChannel = prefix+"ClusterInfo";
                }
                orbNameAliasChannel = orbNameAliasChannel.replace("$PREFIX",prefix);
                System.setProperty(RemoteStorageECImpl.ORB_NAME_ALIAS_CHANNEL_NAME_PROPERTY, orbNameAliasChannel);
                StorageManager storageManager = StorageManagerFactory.getStorageManager();
                OrbNameAliasCache orbNameAliasCache = new OrbNameAliasCache(storageManager.getOrbNameAliasStorage(),storageManager.getLocalStorage());
                orbNameAliasCache.setSbtPrefix(prefix);

                orbNameAliasCache.initializeCache();
                instance = (OrbProcessCache) Network.getNetwork(SBTLiveNode.class);
            }
            else
            {
                throw new IllegalStateException("IMM is not currently connected to an environment...");
            }
        }

        return instance;
    }
}
