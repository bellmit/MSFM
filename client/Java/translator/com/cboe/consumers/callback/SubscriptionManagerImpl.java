package com.cboe.consumers.callback;

import com.cboe.interfaces.callback.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import java.util.*;
import org.omg.CORBA.*;

public class SubscriptionManagerImpl
{
    private final static int DEFAULT_SIZE = 53;
    Hashtable subscribership;
    EventChannelAdapter eventChannel;

    public SubscriptionManagerImpl()
    {
        super();

        subscribership = new Hashtable(DEFAULT_SIZE);
        eventChannel = EventChannelAdapterFactory.find();
    }

    /**
     * Subscribes the given listener from the IEC and increments the corresponding
     * consumers reference count and returns the new count.
     *
     * @return the new reference count after increment.
     * @param key the ChannelKey used for subscription.
     * @param listener the event channel listener subscribed on the IEC.
     * @param consumer the cmi callback consumer.
     */
    public int subscribe(java.lang.Object key, EventChannelListener listener, org.omg.CORBA.Object consumer)
    {
        SubscriptionImpl subscription = (SubscriptionImpl) subscribership.get(new SubscriptionImpl(key, consumer));
        if (subscription == null)
        {
            subscription = new SubscriptionImpl(key, consumer);
            subscribership.put(subscription, subscription);
        }

        if (listener != null)
        {
            eventChannel.addChannelListener(eventChannel, listener, key);
        }

        return subscription.increment();
    }

    /**
     * Unsubscribes the given listener from the IEC and decrements the corresponding
     * consumers reference count and returns the new count.  If the count goes to
     * zero the caller should unsubscribe that consumer from its underlying subscription.
     *
     * @return the new reference count after decrement.
     * @param key the ChannelKey used for subscription.
     * @param listener the event channel listener subscribed on the IEC.
     * @param consumer the cmi callback consumer.
     */
    public int unsubscribe(java.lang.Object key, EventChannelListener listener, org.omg.CORBA.Object consumer)
    {
        int count = -1;
        SubscriptionImpl container = new SubscriptionImpl(key, consumer);
        SubscriptionImpl subscription = (SubscriptionImpl) subscribership.get(container);

        if (subscription != null)
        {
            if ((count = subscription.decrement()) == 0)
            {
                subscribership.remove(container);
            }
        }

        if (listener != null)
        {
            eventChannel.removeChannelListener(this, listener, key);
        }

        return count;
    }


    /**
     * Inner container class for subscription information including reference
     * counting for consumer channel subscriptions.
     */
    class SubscriptionImpl
    {
        private int referenceCount;
        private java.lang.Object key;
        private org.omg.CORBA.Object consumer;

        public SubscriptionImpl(java.lang.Object key, org.omg.CORBA.Object consumer)
        {
            this.key = key;
            this.consumer = consumer;
        }

        public boolean equals(java.lang.Object obj)
        {
            if ((obj != null) && (obj instanceof SubscriptionImpl))
            {
                return ( (key.equals(((SubscriptionImpl) obj).key))
                      && (consumer.equals(((SubscriptionImpl) obj).consumer))
                      );
            }
            return false;
        }

        public int hashCode()
        {
            return ( (key.hashCode() + consumer.hashCode()) / 2 );
        }

        public int increment()
        {
            return ++referenceCount;
        }

        public int decrement()
        {
            return --referenceCount;
        }
    }
}
