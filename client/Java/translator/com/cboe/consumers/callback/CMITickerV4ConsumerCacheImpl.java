//
// -----------------------------------------------------------------------------------
// Source file: CMITickerV4ConsumerCacheImpl.java
//
// PACKAGE: com.cboe.consumers.callback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import com.cboe.idl.cmiCallbackV4.CMITickerConsumer;

import com.cboe.interfaces.consumers.callback.CMITickerV4ConsumerCache;

import com.cboe.util.event.EventChannelAdapter;

/**
 * This maintains a cache of CMITickerConsumers by classKey.
 */
public class CMITickerV4ConsumerCacheImpl extends AbstractCallbackConsumerCache implements CMITickerV4ConsumerCache
{
    public CMITickerV4ConsumerCacheImpl(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
    }

    public CMITickerConsumer getTickerConsumer(int key)
    {
        return (CMITickerConsumer)getCallbackConsumer(key);
    }

    protected CMITickerConsumer createNewCallbackConsumer()
    {
        return TickerV4ConsumerFactory.create(getEventChannel());
    }
}
