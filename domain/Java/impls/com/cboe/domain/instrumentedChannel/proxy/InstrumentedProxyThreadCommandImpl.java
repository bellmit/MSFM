/**
 * @author Jing Chen
 */
package com.cboe.domain.instrumentedChannel.proxy;

import com.cboe.domain.instrumentedChannel.InstrumentedChannelEventRingQueue;
import com.cboe.interfaces.domain.instrumentedChannel.InstrumentedChannelListenerProxy;
import com.cboe.domain.instrumentedChannel.InstrumentedThreadPool;
import com.cboe.domain.instrumentorExtension.QueueInstrumentorExtension;
import com.cboe.domain.instrumentorExtension.QueueInstrumentorExtensionFactory;
import com.cboe.util.channel.proxy.ProxyThreadCommandImpl;
import com.cboe.util.ThreadPool;

public class InstrumentedProxyThreadCommandImpl extends ProxyThreadCommandImpl
{
    protected QueueInstrumentorExtension queueInstrumentorExtension;

    public InstrumentedProxyThreadCommandImpl(InstrumentedChannelListenerProxy proxy)
    {
        super();
        theProxy = proxy;
        state = IDLE;
        eventQueue = new InstrumentedChannelEventRingQueue(proxy.getName(), proxy.getUserData());
        queueInstrumentorExtension = ((InstrumentedChannelEventRingQueue)eventQueue).getQueueInstrumentorExtension();
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
