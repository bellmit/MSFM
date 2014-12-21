//
// -----------------------------------------------------------------------------------
// Source file: CMICurrentMarketV4ConsumerCacheImpl.java
//
// PACKAGE: com.cboe.consumers.callback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import com.cboe.idl.cmiCallbackV4.CMICurrentMarketConsumer;

import com.cboe.interfaces.consumers.callback.CMICurrentMarketV4ConsumerCache;

import com.cboe.util.event.EventChannelAdapter;

/**
 * This maintains a cache of CMICurrentMarketConsumers by classKey.
 */
public class CMICurrentMarketV4ConsumerCacheImpl extends AbstractCallbackConsumerCache implements CMICurrentMarketV4ConsumerCache
{
    public CMICurrentMarketV4ConsumerCacheImpl(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
    }

    public CMICurrentMarketConsumer getCurrentMarketConsumer(int key)
    {
        return (CMICurrentMarketConsumer)getCallbackConsumer(key);
    }

    protected CMICurrentMarketConsumer createNewCallbackConsumer()
    {
        return CurrentMarketV4ConsumerFactory.create(getEventChannel());
    }
}
