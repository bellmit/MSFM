//
// -----------------------------------------------------------------------------------
// Source file: CMIRecapV4ConsumerCacheImpl.java
//
// PACKAGE: com.cboe.consumers.callback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import com.cboe.idl.cmiCallbackV4.CMIRecapConsumer;

import com.cboe.interfaces.consumers.callback.CMIRecapV4ConsumerCache;

import com.cboe.util.event.EventChannelAdapter;

/**
 * This maintains a cache of CMIRecapConsumers by classKey.
 */
public class CMIRecapV4ConsumerCacheImpl extends AbstractCallbackConsumerCache implements CMIRecapV4ConsumerCache
{
    public CMIRecapV4ConsumerCacheImpl(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
    }

    public CMIRecapConsumer getRecapConsumer(int key)
    {
        return (CMIRecapConsumer)getCallbackConsumer(key);
    }

    protected CMIRecapConsumer createNewCallbackConsumer()
    {
        return RecapV4ConsumerFactory.create(getEventChannel());
    }
}
