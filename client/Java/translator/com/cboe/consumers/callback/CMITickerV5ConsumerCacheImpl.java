//
// -----------------------------------------------------------------------------------
// Source file: CMITickerV5ConsumerCacheImpl.java
//
// PACKAGE: com.cboe.consumers.callback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import com.cboe.idl.cmiCallbackV4.CMITickerConsumer;
import com.cboe.interfaces.consumers.callback.CMITickerV4ConsumerCache;
import com.cboe.util.event.EventChannelAdapter;

public class CMITickerV5ConsumerCacheImpl extends AbstractCallbackConsumerCache implements CMITickerV4ConsumerCache
{
    public CMITickerV5ConsumerCacheImpl(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
    }

    public CMITickerConsumer getTickerConsumer(int key)
    {
        return (CMITickerConsumer)getCallbackConsumer(key);
    }

    protected CMITickerConsumer createNewCallbackConsumer()
    {
        return TickerV5ConsumerFactory.create(getEventChannel());
    }
    
}
