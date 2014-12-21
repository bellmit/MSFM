package com.cboe.domain.iec;

import com.cboe.domain.instrumentedChannel.InstrumentedThreadPool;
import com.cboe.domain.instrumentorExtension.QueueInstrumentorExtension;
import com.cboe.domain.instrumentorExtension.QueueInstrumentorExtensionFactory;
import com.cboe.util.ThreadPool;


/**
 * 
 * @author josephg
 *
 */
public class InstrumentedConcurrentProxyThreadCommandImpl extends ConcurrentProxyThreadCommandImpl {

	
    protected QueueInstrumentorExtension queueInstrumentorExtension;

    public InstrumentedConcurrentProxyThreadCommandImpl(InstrumentedConcurrentEventChannelListenerProxy proxy)
    {
        super(proxy);
        eventQueue = new InstrumentedConcurrentQueue(proxy.getName(), proxy.getUserData());
        queueInstrumentorExtension = ((InstrumentedConcurrentQueue)eventQueue).getQueueInstrumentorExtension();
    }

    public void setThreadPool(ThreadPool threadPool)
    {
        this.threadPool = threadPool;
        queueInstrumentorExtension.addThreadPoolInstrumentorRelation(((InstrumentedThreadPool)threadPool).getThreadPoolInstrumentorExtension());
    }

    public synchronized void release()
    {
        super.release();
        QueueInstrumentorExtensionFactory.removeQueueInstrumentor(queueInstrumentorExtension.getName());
        queueInstrumentorExtension = null;
    }
	 
}
