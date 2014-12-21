/**
 * 
 */
package com.cboe.interfaces.consumers.callback;

/**
 * Interface factory that dictates how the factory should be implemented.
 * 
 * @author Eric Maheo
 *
 */
public interface CallbackV5ConsumerCacheFactory
{
    /**
     * Creates a recap consumer cache.
     * 
     * @return the consumer.
     */
    public CMIRecapV4ConsumerCache getRecapConsumerCache();
    /**
     * Creates a ticker consumer cache.
     * 
     * @return the consumer.
     */
    public CMITickerV4ConsumerCache getTickerConsumerCache();
    /**
     * Creates a NBBO consumer cache.
     * 
     * @return the consumer.
     */
    public CMINBBOV4ConsumerCache getNBBOConsumerCache();
    /**
     * Creates a currentMarketManual consumer cache.
     * 
     * @return the consumer.
     */
    public CurrentMarketManualQuoteConsumerCache getCurrentMarketManualQuoteConsumerCache();
}
