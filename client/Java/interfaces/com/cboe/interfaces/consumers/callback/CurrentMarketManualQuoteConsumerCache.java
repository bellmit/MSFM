/**
 * 
 */
package com.cboe.interfaces.consumers.callback;

import com.cboe.idl.internalCallback.CurrentMarketManualQuoteConsumer;


/**
 * @author Eric Maheo
 *
 */
public interface CurrentMarketManualQuoteConsumerCache extends CallbackConsumerCache
{
	CurrentMarketManualQuoteConsumer getCurrentMarketManualQuoteConsumer(int key);
}
