//
// -----------------------------------------------------------------------------------
// Source file: CMINBBOV4ConsumerCacheImpl.java
//
// PACKAGE: com.cboe.consumers.callback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import com.cboe.idl.cmiCallbackV4.CMINBBOConsumer;

import com.cboe.interfaces.consumers.callback.CMINBBOV4ConsumerCache;

import com.cboe.util.event.EventChannelAdapter;

public class CMINBBOV4ConsumerCacheImpl extends AbstractCallbackConsumerCache
        implements CMINBBOV4ConsumerCache
{
    public CMINBBOV4ConsumerCacheImpl(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
    }

    public CMINBBOConsumer getNBBOConsumer(int key)
    {
        return (CMINBBOConsumer) getCallbackConsumer(key);
    }

    protected CMINBBOConsumer createNewCallbackConsumer()
    {
        return NBBOV4ConsumerFactory.create(getEventChannel());
    }
}
