/**
 * 
 */
package com.cboe.infrastructureServices.cacheService;


/**
 * @author craig bomba
 *
 */
public interface JCacheListener<K, V>
{
    public void onClear();                  // entire cache cleared
    public void onEvict(K key, V value);    // entry evicted
    public void onPut(K key, V value);      // entry inserted or updated
    public void onRemove(K key, V value);   // entry removed
}
