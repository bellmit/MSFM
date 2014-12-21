/**
 * 
 */
package com.cboe.infrastructureServices.cacheService;


import java.util.Map;

import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;

/**
 * This is a factory that encapsulates creation of the service provider specific interface.
 * 
 * @author craig bomba
 *
 */
public interface JCacheFactory
{
    public <K, V> JCache<K, V> getCache(String name);
    public <K, V> JCache<K, V> getCache(String name, boolean fetchStateFromPartner);

    /**
     * FoundationFramework Framework methods. CacheService is one of the FF core services
     * which will have to provide these two methods.
     */
    public void goMaster();
    public boolean initialize(ConfigurationService con); 
    public void shutdown();
}
