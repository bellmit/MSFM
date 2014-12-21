//
// -----------------------------------------------------------------------------------
// Source file: NBBOV5ConsumerImpl.java
//
// PACKAGE: com.cboe.consumers.callback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import com.cboe.idl.cmiMarketData.NBBOStructV4;
import com.cboe.interfaces.callback.NBBOV4Consumer;
import com.cboe.interfaces.consumers.callback.MarketDataQueueDepthLogging;
import com.cboe.interfaces.consumers.callback.MarketDataTimeDelay;
import com.cboe.util.ChannelType;
import com.cboe.util.event.EventChannelAdapter;

/**
 * Consumer NBBO for the MarketQueryV5 for the product.
 * 
 * @author Eric Maheo
 *
 */
public class NBBOV5ConsumerImpl extends AbstractV4CallbackConsumer implements NBBOV4Consumer, 
      MarketDataTimeDelay, MarketDataQueueDepthLogging
      {

    public NBBOV5ConsumerImpl(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDelayPropertyName()
    {
        return NBBO_CONSUMER_DELAY_PROPERTY_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getLogQueueDepthPropertyName()
    {
        return NBBO_LOG_QUEUE_DEPTH_THRESHOLD_PROPERTY_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void acceptNBBO(NBBOStructV4[] nbboStructs, int messageSequence, int queueDepth, short queueAction)
    {
        for(NBBOStructV4 nbbo : nbboStructs)
        {
            dispatchEvent(this, ChannelType.CB_NBBO_BY_PRODUCT_V4 , nbbo.productKey, nbbo);
            increaseMethodCallCounter();

            if( isMethodCallLoggingOn() )
            {
                logMethodCall(nbbo.productKey, "acceptNBBO(NBBOStructV4[] nbboStructs, int messageSequence, int queueDepth, short queueAction)");
            }
        }

        logQueueDepth(queueDepth, queueAction, "acceptNBBO", nbboStructs.length);
        waitDelay();
    }

}
