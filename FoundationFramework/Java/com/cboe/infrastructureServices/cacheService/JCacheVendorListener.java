/**
 * 
 */
package com.cboe.infrastructureServices.cacheService;


/**
 * @author craig bomba
 *
 */
public interface JCacheVendorListener<K, V>
{
    public JCacheListener<K, V> getFrameworkListener(); 
    public void registerFrameworkListener(JCacheListener<K, V> listener); 
}
