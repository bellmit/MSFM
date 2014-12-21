/**
 * 
 */
package com.cboe.infrastructureServices.cacheService;

import java.net.UnknownHostException;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved;
import org.infinispan.notifications.cachemanagerlistener.annotation.CacheStarted;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;
import org.infinispan.notifications.cachemanagerlistener.event.CacheStartedEvent;

import com.cboe.common.log.Logger;
import com.cboe.instrumentationService.InstrumentorHome;
import com.cboe.instrumentationService.instrumentors.*;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.factories.QueueInstrumentorFactory;

/**
 * @author craig bomba
 *
 */

@Listener 
public class JCacheInfinispanListener<K, V> extends JCacheAbstractVendorListener<K, V> implements JCacheVendorListener<K, V>
{

    private CountInstrumentor cachePutCount = null;
    private CountInstrumentor cacheRemoveCount = null;
    private String baseInstrUserData = null;
    private String cacheName = "NotSet";
 
    private void setupInstrumentor(String cacheName)
    {
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
        this.cacheName = cacheName;
        cachePutCount = InstrumentorHome.findCountInstrumentorFactory().getInstance("CacheService/" + cacheName + "/" + baseInstrUserData + "/ListenerPutCount", null );
        cacheRemoveCount = InstrumentorHome.findCountInstrumentorFactory().getInstance("CacheService/" + cacheName + "/" + baseInstrUserData + "/ListenerRemoveCount", null );

    }

    @CacheStarted
    public void startedEvent(CacheStartedEvent evt)
    {
        cacheName = evt.getCacheName();
        setupInstrumentor(cacheName);
    }
    
    
    @CacheEntryRemoved
    public void entryDeleted(CacheEntryRemovedEvent evt)
    {
        if (evt.isPre())
        {
            if (baseInstrUserData == null)
            {
                // lazy initialization of the instrumentation 
                setupInstrumentor(evt.getCache().getName());
            }
            cacheRemoveCount.incCount( 1 );
            getFrameworkListener().onRemove((K) evt.getKey(), (V) evt.getValue());
        }
    }

    public void entryInserted(K key)
    {
        // TODO Auto-generated method stub
        
    }

    @CacheEntryModified
    public void entryUpdated(CacheEntryModifiedEvent evt)
    {
        if (!evt.isPre())
        {
            if (baseInstrUserData == null)
            {
                // lazy initialization of the instrumentation 
                setupInstrumentor(evt.getCache().getName());
            }
            cachePutCount.incCount( 1 );
            getFrameworkListener().onPut((K) evt.getKey(), (V) evt.getValue());
        }
    }


}
