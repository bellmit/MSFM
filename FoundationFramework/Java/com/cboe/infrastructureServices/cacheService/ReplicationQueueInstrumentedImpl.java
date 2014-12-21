package com.cboe.infrastructureServices.cacheService;

import org.infinispan.commands.CommandsFactory;
import org.infinispan.commands.ReplicableCommand;
import org.infinispan.config.Configuration;
import org.infinispan.factories.KnownComponentNames;
import org.infinispan.factories.annotations.ComponentName;
import org.infinispan.factories.annotations.Inject;
import org.infinispan.remoting.ReplicationQueueImpl;
import org.infinispan.remoting.rpc.RpcManager;
import org.infinispan.Cache;

import com.cboe.instrumentationService.InstrumentorHome;
import com.cboe.instrumentationService.instrumentors.*;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.factories.QueueInstrumentorFactory;

import static com.cboe.infrastructureServices.foundationFramework.utilities.Log.alarm;
import static com.cboe.infrastructureServices.foundationFramework.utilities.Log.medium;
import static com.cboe.infrastructureServices.foundationFramework.utilities.Log.information;
import static com.cboe.infrastructureServices.foundationFramework.utilities.Log.exception;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.*;
import java.net.UnknownHostException;

public class ReplicationQueueInstrumentedImpl extends org.infinispan.remoting.ReplicationQueueImpl
{

    private QueueInstrumentor qinstr = null;
    private String baseInstrUserData = null;
    private ReentrantReadWriteLock qinstrLock = new ReentrantReadWriteLock();
    private int queueSize = 0;
    private Cache cache;
    
    @Inject
    private void injectCache(Cache c)
    {
        cache = c;
    }
    
    @Override
    public void add(ReplicableCommand job) {

        // lazy init the instrumentor stuff since no contructor
        if (baseInstrUserData == null)
        {
            try {
                qinstrLock.writeLock().lock();
                try
                {
                    baseInstrUserData = System.getProperties().getProperty("ORB.HostName",java.net.InetAddress.getLocalHost().getHostName());
                }
                catch(UnknownHostException uhe)
                {
                    uhe.printStackTrace();
                    medium( this.getClass().getName() + " could not get host info!  " +
                                        "Setting to 'HostNotSet' and continuing");
                    baseInstrUserData = "HostNotSet";
                }
                String qinstrName = "CacheService/" + baseInstrUserData + "/" + cache.getName() + "/ReplicationQueue";
                try
                {
                    qinstr = InstrumentorHome.findQueueInstrumentorFactory().create( qinstrName , null );
                    InstrumentorHome.findQueueInstrumentorFactory().register(qinstr);
                } 
                catch( InstrumentorAlreadyCreatedException e ) {
                    qinstr = InstrumentorHome.findQueueInstrumentorFactory().find( qinstrName );
                }
                information("==> QueueInstrumentor should now be created with name: " + qinstrName + " and value: " + qinstr);
                queueSize = 0;
            }
            finally {
                qinstrLock.writeLock().unlock();
            }
        }
        super.add(job);
        try {
            qinstrLock.writeLock().lock();
            qinstr.incEnqueued( 1 );
            queueSize++;
            qinstr.setCurrentSize(queueSize);
        }
        finally {
            qinstrLock.writeLock().unlock();
        }
        
    }
    
    @Override
    public synchronized int flush() {

        int toReplicateSize = super.flush();

        // lazy init the instrumentor stuff since no contructor
        if (baseInstrUserData == null)
        {
            try {
                qinstrLock.writeLock().lock();
                try
                {
                    baseInstrUserData = System.getProperties().getProperty("ORB.HostName",java.net.InetAddress.getLocalHost().getHostName());
                }
                catch(UnknownHostException uhe)                 {
                    uhe.printStackTrace();
                    medium( this.getClass().getName() + " could not get host info!  " +
                                        "Setting to 'HostNotSet' and continuing");
                    baseInstrUserData = "HostNotSet";
                }
                String qinstrName = "CacheService/" + baseInstrUserData + "/" + cache.getName() + "/ReplicationQueue";
                try
                {
                    qinstr = InstrumentorHome.findQueueInstrumentorFactory().create( qinstrName , null );
                    InstrumentorHome.findQueueInstrumentorFactory().register(qinstr);
                } 
                catch( InstrumentorAlreadyCreatedException e ) {
                    qinstr = InstrumentorHome.findQueueInstrumentorFactory().find( qinstrName );
                }
                information("==> QueueInstrumentor should now be created with name: " + qinstrName + " and value: " + qinstr);
                queueSize = 0;
            }
            finally {
                qinstrLock.writeLock().unlock();
            }
        }
        qinstrLock.writeLock().lock();
        try {
            qinstr.incDequeued( toReplicateSize );
            queueSize-=toReplicateSize;
            qinstr.setCurrentSize(queueSize);
        }
        finally {
            qinstrLock.writeLock().unlock();
        }
        
        return toReplicateSize;
    }
    
}
