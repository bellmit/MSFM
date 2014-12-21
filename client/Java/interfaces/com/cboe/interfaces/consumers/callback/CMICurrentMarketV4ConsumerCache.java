//
// -----------------------------------------------------------------------------------
// Source file: CMICurrentMarketV4ConsumerCache.java
//
// PACKAGE: com.cboe.interfaces.consumers.callback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.consumers.callback;

import com.cboe.idl.cmiCallbackV4.CMICurrentMarketConsumer;

/**
 * Provides an interface to get a CMICurrentMarketConsumer from the cache for a classKey.
 */
public interface CMICurrentMarketV4ConsumerCache extends CallbackConsumerCache
{
    public CMICurrentMarketConsumer getCurrentMarketConsumer(int key);
}