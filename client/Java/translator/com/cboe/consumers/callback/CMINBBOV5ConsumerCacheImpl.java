//
// -----------------------------------------------------------------------------------
// Source file: CMINBBOV5ConsumerCacheImpl.java
//
// PACKAGE: com.cboe.consumers.callback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import com.cboe.idl.cmiCallbackV4.CMINBBOConsumer;
import com.cboe.interfaces.consumers.callback.CMINBBOV4ConsumerCache;
import com.cboe.util.event.EventChannelAdapter;

/**
 * @author Maheo
 *
 */
public class CMINBBOV5ConsumerCacheImpl extends AbstractCallbackConsumerCache
        implements CMINBBOV4ConsumerCache
{
    public CMINBBOV5ConsumerCacheImpl(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
    }

    public CMINBBOConsumer getNBBOConsumer(int key)
    {
        return (CMINBBOConsumer) getCallbackConsumer(key);
    }

    protected CMINBBOConsumer createNewCallbackConsumer()
    {
        return NBBOV5ConsumerFactory.create(getEventChannel());
    }
}
