/**
 * 
 */
package com.cboe.infrastructureServices.cacheService;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.config.Configuration;
import static com.cboe.infrastructureServices.foundationFramework.utilities.Log.alarm;
import static com.cboe.infrastructureServices.foundationFramework.utilities.Log.information;
import static com.cboe.infrastructureServices.foundationFramework.utilities.Log.exception;

/**
 * @author craig bomba
 *
 */
public class JCacheFactoryInfinispanImpl extends JCacheFactoryBaseImpl implements JCacheFactory
{
    static String configurationFileName = System.getProperty("CacheService.configurationFileName", "cache_config.xml");
    
    private DefaultCacheManager manager = null;
    
    public <K, V> JCache<K, V> getCache(String name)
    {
        return getCache(name, false);  // call getCache with the cacheName and default false for isMaster indicator
    }

    /* (non-Javadoc)
     * @see com.cboe.infinispan.FFCacheFactory#getCache(java.lang.String)
     */
    public <K, V> JCache<K, V> getCache(String name, boolean fetchStateFromPartner)
    {
        JCache<K, V> cache;
        
        synchronized (cacheInstances) {
            cache = cacheInstances.get(name);
            if (cache == null){
                // assume the state is set to false by default..meaning we override only when true
                if (fetchStateFromPartner)
                {
                    Configuration overrideConfig = new Configuration();
                    overrideConfig.setFetchInMemoryState(true);
                    getCacheManager().defineConfiguration(name, overrideConfig);
                }
                cache = new JCacheServiceInfinispanImpl(getCacheManager().getCache(name));                    
                cacheInstances.put(name, cache);
                String msg = "CacheService >> getCache. A new cache instance is created";
                msg = msg + " for context: " + name;
                //information(msg);
                information(msg);
                information("CacheService for context: " + name + " has inMemoryState set to: " + getCacheManager().getCache(name).getConfiguration().isFetchInMemoryState());
                information("CacheService for context: " + name + " operating in cluster: " +  getCacheManager().getClusterName());
            }
        }
        return cache;
    }
    
    private DefaultCacheManager getCacheManager()
    {
        if (manager == null)
        {
            try 
            {
                manager = new DefaultCacheManager(configurationFileName);
            }
            catch (IOException ioe) {
                alarm("Caught an IOException in JCacheFactoryInfinispanImpl setup of getCacheManager.  Check config!");
                ioe.printStackTrace();
            }
        }
        return manager;
    }

}
