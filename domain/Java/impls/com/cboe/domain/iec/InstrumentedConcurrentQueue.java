package com.cboe.domain.iec;

import java.util.Enumeration;

import com.cboe.domain.instrumentorExtension.QueueInstrumentorExtension;
import com.cboe.domain.instrumentorExtension.QueueInstrumentorExtensionFactory;
import com.cboe.domain.util.InstrumentorUserData;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.instrumentors.QueueInstrumentor;
import com.cboe.util.channel.ChannelEvent;


/**
 * Extends ConcurrentQueue to add instrumentation.
 * 
 * @author josephg
 *
 */
public class InstrumentedConcurrentQueue extends ConcurrentQueue {
    private QueueInstrumentorExtension queueInstrumentorExtension;
    private QueueInstrumentor queueInstrumentor;

    public InstrumentedConcurrentQueue(String name)
    {
    	this(name, new InstrumentorUserData());
    }
    
    public InstrumentedConcurrentQueue(String name, Object userData)
    {
        super();
        try
        {
            queueInstrumentorExtension = QueueInstrumentorExtensionFactory.createQueueInstrumentor(name, userData, false);
        }
        catch(InstrumentorAlreadyCreatedException e)
        {
            // do not want to propagate this exception up.
            //It indicates the instrumentation data will be bad from this point on for this queue.
            Log.exception(e);
            queueInstrumentorExtension = QueueInstrumentorExtensionFactory.find(name);
        }
        queueInstrumentor = queueInstrumentorExtension.getQueueInstrumentor();
    }

    public QueueInstrumentorExtension getQueueInstrumentorExtension()
    {
        return queueInstrumentorExtension;
    }

    public int insertEvent( ChannelEvent event )
    {
        int cnt=super.insertEvent(event);
        queueInstrumentor.incEnqueued(1);
        queueInstrumentor.setCurrentSize(currentCount.get());
        queueInstrumentor.setHighWaterMark(maxCount.get());
        return cnt;
    }

    public ChannelEvent getNextEvent()
    {
        ChannelEvent retVal = super.getNextEvent();
        queueInstrumentor.incDequeued(1);
        queueInstrumentor.setCurrentSize(currentCount.get());
        return retVal;
    }

    public Enumeration clearQueue()
    {
        queueInstrumentor.incFlushed(currentCount.get());
        Enumeration clearedCommands = super.clearQueue();
        queueInstrumentor.setCurrentSize(0L);
        return clearedCommands;
    }

}
