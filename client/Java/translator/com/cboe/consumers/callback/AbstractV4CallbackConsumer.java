//
// -----------------------------------------------------------------------------------
// Source file: AbstractV4CallbackConsumer.java
//
// PACKAGE: com.cboe.consumers.callback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import com.cboe.util.event.EventChannelAdapter;

public abstract class AbstractV4CallbackConsumer extends AbstractCallbackConsumer
{
    protected long callbackCount;

    protected AbstractV4CallbackConsumer(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
        callbackCount = 0;
    }

    protected void waitDelay()
    {
        if(callbackCount++ > 0)
        {
            V4ConsumerThreadSleeper.find().waitDelay();
        }
    }
}
