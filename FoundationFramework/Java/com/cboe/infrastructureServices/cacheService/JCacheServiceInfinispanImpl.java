/**
 * 
 */
package com.cboe.infrastructureServices.cacheService;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.cboe.common.log.Logger;
import com.cboe.instrumentationService.InstrumentorHome;
import com.cboe.instrumentationService.instrumentors.*;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.factories.QueueInstrumentorFactory;

import org.infinispan.Cache;

/**
 * @author craig bomba
 * @param <K, V> generics for Key and Value
 *
 */
public class JCacheServiceInfinispanImpl<K, V> extends JCacheServiceBaseImpl<K, V> implements JCache<K, V>
{
    private Cache<K, V> cacheAdaptee = null;
    
    private CountInstrumentor cachePutCount = null;
    private CountInstrumentor cacheRemoveCount = null;
    private String baseInstrUserData = null;

    // TODO - make the listenerList collection below thread safe!  Collections.synchronizedCollection for exmaple!
    //        Or, may need to synchronize on some object
    
    public JCacheServiceInfinispanImpl(Cache<K, V> cache)
    {
        super();
        this.cacheAdaptee = cache;
        init();
    }

    private void init()
    {
        setupInstrumentor();
    }

    private void setupInstrumentor()
    {
        // Register with default monitor so that instrumentors get published
        //InstrumentorHome.setupDefaultInstrumentorMonitorRegistrars();
        try 
        {
            baseInstrUserData = System.getProperties().getProperty("ORB.HostName",java.net.InetAddress.getLocalHost().getHostName());
        }
        catch(UnknownHostException uhe)
        {
            uhe.printStackTrace();
            Logger.sysNotify( this.getClass().getName() + " could not get host info!  " +
                    "Setting to 'HostNotSet' and continuing");
            baseInstrUserData = "HostNotSet";
        }
        cachePutCount = InstrumentorHome.findCountInstrumentorFactory().getInstance("CacheService/" + cacheAdaptee.getName() + "/" + baseInstrUserData + "/PutCount", null );
        cacheRemoveCount = InstrumentorHome.findCountInstrumentorFactory().getInstance("CacheService/" + cacheAdaptee.getName() + "/" + baseInstrUserData + "/RemoveCount", null );

    }
            
    public synchronized void addListener(JCacheListener<K, V> listener)
    {
        JCacheInfinispanListener<K, V> vendorListener = new JCacheInfinispanListener<K, V>();
        vendorListener.registerFrameworkListener(listener);
        cacheAdaptee.addListener(vendorListener);
        listenerList.add(vendorListener);  // add the listner to a list so we can remove later
    }
    
    public void clear()
    {
        cacheAdaptee.clear();
    }

    public boolean containsKey(Object key)
    {
        return cacheAdaptee.containsKey(key);
    }

    public boolean containsValue(Object value)
    {
        return cacheAdaptee.containsValue(value);
    }

    public Set<java.util.Map.Entry<K, V>> entrySet()
    {
        return cacheAdaptee.entrySet();
    }

    public V get(Object key)
    {
        return cacheAdaptee.get(key);
    }

    public boolean isEmpty()
    {
        return cacheAdaptee.isEmpty();
    }

    public Set<K> keySet()
    {
        return cacheAdaptee.keySet();
    }

    public V put(K key, V value)
    {
        cachePutCount.incCount( 1 );
        return cacheAdaptee.put(key, value);
    }

    public void putAll(Map<? extends K, ? extends V> m)
    {
        cacheAdaptee.putAll(m);
    }

    public V remove(Object key)
    {
        cacheRemoveCount.incCount( 1 );
        return cacheAdaptee.remove(key);
    }

    public synchronized void removeListener(JCacheListener<K, V> listener)
    {
        JCacheVendorListener<K, V> vendorListener;
        Iterator iter = listenerList.iterator();
        while (iter.hasNext())
        {
            vendorListener = (JCacheVendorListener<K, V>) iter.next();
            if (vendorListener.getFrameworkListener() == listener)
            {
                cacheAdaptee.removeListener(vendorListener);
                iter.remove();
            }
        }
    }
    public int size()
    {
        return cacheAdaptee.size();
    }

    public Collection<V> values()
    {
        return cacheAdaptee.values();
    }

}
