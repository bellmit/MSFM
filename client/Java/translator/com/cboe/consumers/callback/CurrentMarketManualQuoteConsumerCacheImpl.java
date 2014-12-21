//
// -----------------------------------------------------------------------------------
// Source file: CurrentMarketManualQuoteConsumerCacheImpl.java
//
// PACKAGE: com.cboe.consumers.callback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import com.cboe.interfaces.consumers.callback.CurrentMarketManualQuoteConsumerCache;
import com.cboe.idl.internalCallback.CurrentMarketManualQuoteConsumer;
import com.cboe.util.event.EventChannelAdapter;


/**
 * 
 * 
 * @author Eric Maheo
 *
 */
public class CurrentMarketManualQuoteConsumerCacheImpl extends AbstractCallbackConsumerCache 
	implements CurrentMarketManualQuoteConsumerCache
{

	public CurrentMarketManualQuoteConsumerCacheImpl(EventChannelAdapter eventChannel)
    {
	    super(eventChannel);
    }

	@Override
    public CurrentMarketManualQuoteConsumer getCurrentMarketManualQuoteConsumer(int key)
	{ 
		return (CurrentMarketManualQuoteConsumer)getCallbackConsumer(key);
    }

	
	protected CurrentMarketManualQuoteConsumer createNewCallbackConsumer()
    {
		return CurrentMarketManualQuoteConsumerFactory.create(getEventChannel());
    }

}
