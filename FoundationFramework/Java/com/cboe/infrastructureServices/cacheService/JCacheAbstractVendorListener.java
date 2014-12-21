/**
 * 
 */
package com.cboe.infrastructureServices.cacheService;

/**
 * @author craig bomba
 *
 */
public abstract class JCacheAbstractVendorListener<K, V> implements JCacheVendorListener<K, V>
{
    protected JCacheListener<K, V> actualListener = null;

    public JCacheListener<K, V> getFrameworkListener()
    {
        return actualListener;
    }
    
    public void registerFrameworkListener(JCacheListener<K, V> listener) 
    {
        actualListener = listener;
    }

}
