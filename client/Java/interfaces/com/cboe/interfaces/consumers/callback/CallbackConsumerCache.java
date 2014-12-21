package com.cboe.interfaces.consumers.callback;

import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.util.event.EventChannelAdapter;

/**
 * Provides a basic interface for a cache of CMI callback consumers.  The
 * consumers can be cached by an int key (e.g., classKey), or by a
 * SessionKeyWrapper.
 */
public interface CallbackConsumerCache
{
    public void cleanupCallbackConsumers();
    public org.omg.CORBA.Object[] getAllCallbackConsumers();
    public org.omg.CORBA.Object getCallbackConsumer(SessionKeyWrapper key);
    public org.omg.CORBA.Object getCallbackConsumer(int key);
    public EventChannelAdapter getEventChannel();
}
