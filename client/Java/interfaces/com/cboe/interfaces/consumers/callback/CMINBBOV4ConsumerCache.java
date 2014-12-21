//
// -----------------------------------------------------------------------------------
// Source file: CMINBBOV4ConsumerCache.java
//
// PACKAGE: com.cboe.interfaces.consumers.callback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.consumers.callback;

import com.cboe.idl.cmiCallbackV4.CMINBBOConsumer;

public interface CMINBBOV4ConsumerCache extends CallbackConsumerCache
{
    CMINBBOConsumer getNBBOConsumer(int key);
}