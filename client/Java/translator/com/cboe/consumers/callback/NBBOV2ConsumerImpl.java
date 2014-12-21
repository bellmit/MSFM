//
// ------------------------------------------------------------------------
// Source file: NBBOV2ConsumerImpl.java
//
// PACKAGE: com.cboe.consumers.callback
//
// ------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import com.cboe.idl.cmiMarketData.NBBOStruct;

import com.cboe.interfaces.callback.NBBOV2Consumer;
import com.cboe.interfaces.consumers.callback.MarketDataTimeDelay;
import com.cboe.interfaces.consumers.callback.MarketDataQueueDepthLogging;

import com.cboe.util.ChannelType;
import com.cboe.util.event.EventChannelAdapter;

import com.cboe.domain.util.SessionKeyContainer;

public class NBBOV2ConsumerImpl extends AbstractCallbackConsumer
        implements NBBOV2Consumer, MarketDataTimeDelay, MarketDataQueueDepthLogging
{
    public NBBOV2ConsumerImpl(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
    }

    protected String getDelayPropertyName()
    {
        return NBBO_CONSUMER_DELAY_PROPERTY_NAME;
    }

    protected String getLogQueueDepthPropertyName()
    {
        return NBBO_LOG_QUEUE_DEPTH_THRESHOLD_PROPERTY_NAME;
    }

    public void acceptNBBO(NBBOStruct[] nbbo, int queueDepth, short queueAction)
    {
        for( int i = 0; i < nbbo.length; i++ )
        {
            dispatchEvent(this, -1 * ChannelType.CB_NBBO_BY_PRODUCT,
                          new SessionKeyContainer(nbbo[i].sessionName,
                                                  nbbo[i].productKeys.productKey),
                          nbbo[i]);

            dispatchEvent(this, -1 * ChannelType.CB_NBBO_BY_CLASS,
                          new SessionKeyContainer(nbbo[i].sessionName,
                                                  nbbo[i].productKeys.classKey),
                          nbbo[i]);

            increaseMethodCallCounter();

            if( isMethodCallLoggingOn() )
            {
                logMethodCall(nbbo[i].sessionName + "." + nbbo[i].productKeys.productKey,
                              "acceptNBBO(NBBOStruct[], int, short)");
            }
        }

        logQueueDepth(queueDepth, queueAction, "acceptNBBO", nbbo.length);

        waitDelay();
    }
}