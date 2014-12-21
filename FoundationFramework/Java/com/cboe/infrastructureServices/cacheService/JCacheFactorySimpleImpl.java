/**
 * 
 */

package com.cboe.infrastructureServices.cacheService;

//import static com.cboe.infrastructureServices.foundationFramework.utilities.Log.information;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import static com.cboe.infrastructureServices.foundationFramework.utilities.Log.alarm;
import static com.cboe.infrastructureServices.foundationFramework.utilities.Log.information;
import static com.cboe.infrastructureServices.foundationFramework.utilities.Log.exception;

/**
 * @author craig bomba
 *
 */
public class JCacheFactorySimpleImpl extends JCacheFactoryBaseImpl implements JCacheFactory
{    

    public <K, V> JCache<K, V> getCache(String name)
    {
        return getCache(name, false);  // call getCache with the cacheName and default false for isMaster indicator
    }
    
    
    /* (non-Javadoc)
     * @see com.cboe.infinispan.FFCacheFactory#getCache(java.lang.String)
     */
    public <K, V> JCache<K, V> getCache(String name, boolean isMaster)
    {
        JCache<K, V> cache;
        
        synchronized (cacheInstances) {
            cache = cacheInstances.get(name);
            if (cache == null){
                cache = new JCacheServiceSimpleImpl<K, V>(new ConcurrentHashMap());                    
                cacheInstances.put(name, cache);
                String msg = "CacheService >> getCache. A new cache instance is created";
                msg = msg + " for context: " + name;
                //information(msg);
                information(msg);
            }
        }
        return cache;
    }

}
