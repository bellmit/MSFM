//
// -----------------------------------------------------------------------------------
// Source file: CMIRecapV5ConsumerCacheImpl.java
//
// PACKAGE: com.cboe.consumers.callback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import com.cboe.idl.cmiCallbackV4.CMIRecapConsumer;
import com.cboe.interfaces.consumers.callback.CMIRecapV4ConsumerCache;
import com.cboe.util.event.EventChannelAdapter;

public class CMIRecapV5ConsumerCacheImpl extends AbstractCallbackConsumerCache 
            implements CMIRecapV4ConsumerCache
{
    public CMIRecapV5ConsumerCacheImpl(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
    }

    public CMIRecapConsumer getRecapConsumer(int key)
    {
        return (CMIRecapConsumer)getCallbackConsumer(key);
    }

    protected CMIRecapConsumer createNewCallbackConsumer()
    {
        return RecapV5ConsumerFactory.create(getEventChannel());
    }
}

