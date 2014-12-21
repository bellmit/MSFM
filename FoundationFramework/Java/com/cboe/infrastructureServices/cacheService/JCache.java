/**
 * 
 */
package com.cboe.infrastructureServices.cacheService;

import java.util.Map;

/**
 * This interface defines the mechanism for efficient temporary storage of objects 
 * while maintaining good overall performance.  
 * 
 * @author craig bomba
 *
 */
public interface JCache<K, V> extends Map<K, V>
{
    public void addListener(JCacheListener<K, V> listener);
    public void removeListener(JCacheListener<K, V> listener);
    
    public void goMaster();  // placeholder, may need this for some housekeeping 
    public void goSlave();   // placeholder, may need this to decorate the cache for example
    
    // may want the following "Map-add-on methods that are more common in a Cache":
    // evict method(s)?
    // load method(s)?
    // may want overloaded methods including "lifespan" (on put for example)
}
