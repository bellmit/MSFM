package com.cboe.consumers.callback;

import com.cboe.util.event.EventChannelAdapter;
import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.consumers.callback.CallbackConsumerCache;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;

import java.util.*;

import org.omg.CORBA.*;

/**
 * Caches callback consumer objects by a Session and/or key, using the SessionKeyWrapper or int key as the unique key.
 */
public abstract class AbstractCallbackConsumerCache implements CallbackConsumerCache
{
    // the maps are private so the only way to add consumers is through getCallbackConsumer()
    // the values are the consumer objects created by createNewCallbackConsumer
    private Hashtable<SessionKeyWrapper, org.omg.CORBA.Object> consumersBySessionKeyMap;
    private Hashtable<Integer, org.omg.CORBA.Object> consumersByKeyMap;
    private EventChannelAdapter eventChannel;

    public AbstractCallbackConsumerCache(EventChannelAdapter eventChannel)
    {
        if(eventChannel == null)
        {
            throw new IllegalArgumentException("EventChannelAdapter cannot be null");
        }
        this.eventChannel = eventChannel;
        // creating consumersBySessionKeyMap in constructor to avoid possible synchronization issues of creating it lazily
        consumersBySessionKeyMap = new Hashtable<SessionKeyWrapper, org.omg.CORBA.Object>();
        consumersByKeyMap = new Hashtable<Integer, org.omg.CORBA.Object>();
    }

    /**
     * Removes all consumers from the cache.
     */
    public synchronized void cleanupCallbackConsumers()
    {
        consumersByKeyMap.clear();
        consumersBySessionKeyMap.clear();
    }

    /**
     * Returns an array of all cached callback consumers.
     *
     * @return all CMi callback consumers in the cache
     */
    public org.omg.CORBA.Object[] getAllCallbackConsumers()
    {
        Collection<org.omg.CORBA.Object> collection = consumersByKeyMap.values();
        collection.addAll(consumersBySessionKeyMap.values());
        return collection.toArray(new org.omg.CORBA.Object[collection.size()]);
    }

    /**
     * Returns the callback consumer associated with the supplied SessionKeyWrapper.  If there is no such consumer
     * in the cache, it is created and added.
     *
     * This is synchronized to handle a situation where two threads could try adding the same consumer.
     *
     * The extending class should provide a public interface that casts the returned Object to the same class as the
     * callback consumer that is created by createNewCallbackConsumer().
     *
     * @return the cached consumer for the SessionKeyWrapper
     */
    public synchronized org.omg.CORBA.Object getCallbackConsumer(SessionKeyWrapper key)
    {
        org.omg.CORBA.Object consumer = consumersBySessionKeyMap.get(key);

        if(consumer == null)
        {
            if(GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.COMMON))
            {
                GUILoggerHome.find().debug(this.getClass().getName()+".getCallbackConsumer()",GUILoggerBusinessProperty.COMMON,
                                           "Adding new callback consumer to cache for sessionName:'"+key.getSessionName()+"' key="+key.getKey());
            }
            consumer = createNewCallbackConsumer();
            consumersBySessionKeyMap.put(key, consumer);
        }

        if(GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.COMMON))
        {
            GUILoggerHome.find().debug(this.getClass().getName()+".getCallbackConsumer()",GUILoggerBusinessProperty.COMMON,
                                       "Returning cached callback consumer object for key="+key.toString());
        }
        return consumer;
    }

    /**
     * Returns the callback consumer associated with the supplied int key.  If there is no such consumer in
     * the cache, it is created and added.
     *
     * This is synchronized to handle a situation where two threads could try adding the same consumer.
     *
     * The extending class should provide a public interface that casts the returned Object to the same class as the
     * callback consumer that is created by createNewCallbackConsumer().
     *
     * @return the cached consumer for the int key
     */
    public synchronized org.omg.CORBA.Object getCallbackConsumer(int key)
    {
        org.omg.CORBA.Object consumer = consumersByKeyMap.get(key);

        if(consumer == null)
        {
            if(GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.COMMON))
            {
                GUILoggerHome.find()
                        .debug(this.getClass().getName() + ".getCallbackConsumer()", GUILoggerBusinessProperty.COMMON,
                               "Adding new callback consumer to cache for key=" + key);
            }
            consumer = createNewCallbackConsumer();
            consumersByKeyMap.put(key, consumer);
        }

        if(GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.COMMON))
        {
            GUILoggerHome.find()
                    .debug(this.getClass().getName() + ".getCallbackConsumer()", GUILoggerBusinessProperty.COMMON,
                           "Returning cached callback consumer object for key=" + key);
        }
        return consumer;
    }

    /**
     * Returns the EventChannelAdapter that the consumers will dispatch their events on.
     *
     * @return EventChannelAdapter that consumers will dispatch their events onto
     */
    public EventChannelAdapter getEventChannel()
    {
        return eventChannel;
    }

    /**
     * Create a callback consumer object to be added to the cache.
     *
     * @return new CMI callback consumer
     */
    abstract protected org.omg.CORBA.Object createNewCallbackConsumer();
}
