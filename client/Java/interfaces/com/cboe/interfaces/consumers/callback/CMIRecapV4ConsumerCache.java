//
// -----------------------------------------------------------------------------------
// Source file: CMIRecapV4ConsumerCache.java
//
// PACKAGE: com.cboe.interfaces.consumers.callback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.consumers.callback;

import com.cboe.idl.cmiCallbackV4.CMIRecapConsumer;

/**
 * Provides an interface to get a CMIRecapConsumer from the cache for a classKey.
 */
public interface CMIRecapV4ConsumerCache extends CallbackConsumerCache
{
    public CMIRecapConsumer getRecapConsumer(int key);
}