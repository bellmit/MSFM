package com.cboe.domain.instrumentedChannel;

/**
 * @author Jing Chen
 */

import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.channel.ChannelEventRingQueue;
import com.cboe.domain.instrumentorExtension.QueueInstrumentorExtension;
import com.cboe.domain.instrumentorExtension.QueueInstrumentorExtensionFactory;
import com.cboe.instrumentationService.instrumentors.QueueInstrumentor;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

import java.util.Enumeration;

public class InstrumentedChannelEventRingQueue extends ChannelEventRingQueue
{
    private QueueInstrumentorExtension queueInstrumentorExtension;
    private QueueInstrumentor queueInstrumentor;

    /**
     * Constructor
     */
    public InstrumentedChannelEventRingQueue(String name, Object userData)
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

    /**
     * Adds a new command to the queue
     */
    public void insertEvent( ChannelEvent event )
    {
        super.insertEvent(event);
        queueInstrumentor.incEnqueued((long)1);
        queueInstrumentor.setCurrentSize((long)currentCount);
        queueInstrumentor.setHighWaterMark(maxCount);
    }
    /**
     * This method will get the next available command.
     *  If there are no commands available, wait for one.
     *  If we are shutting down, return null
     * @return ThreadCommand the command to be executed.
     */
    public ChannelEvent getNextEvent()
    {
        ChannelEvent retVal = super.getNextEvent();
        queueInstrumentor.incDequeued((long)1);
        queueInstrumentor.setCurrentSize((long)currentCount);
        return retVal;
    }

    public Enumeration clearQueue()
    {
        queueInstrumentor.incFlushed(currentCount);
        Enumeration clearedCommands = super.clearQueue();
        queueInstrumentor.setCurrentSize((long)0);
        return clearedCommands;
    }
}
