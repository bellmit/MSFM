//
// -----------------------------------------------------------------------------------
// Source file: CMITickerV4ConsumerCache.java
//
// PACKAGE: com.cboe.interfaces.consumers.callback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.consumers.callback;

import com.cboe.idl.cmiCallbackV4.CMITickerConsumer;

/**
 * Provides an interface to get a CMITickerConsumer from the cache for a classKey.
 */
public interface CMITickerV4ConsumerCache extends CallbackConsumerCache
{
    public CMITickerConsumer getTickerConsumer(int key);
}