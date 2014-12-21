package com.cboe.interfaces.consumers.callback;

/**
 * Created by IntelliJ IDEA.
 * User: HallB
 * Date: Apr 4, 2003
 * Time: 8:16:32 AM
 * To change this template use Options | File Templates.
 */
public interface CallbackV2ConsumerCacheFactory
{
    public CMICurrentMarketV2ConsumerCache getCurrentMarketConsumerCache();
    public CMIRecapV2ConsumerCache getRecapConsumerCache();
    public CMINBBOV2ConsumerCache getNBBOConsumerCache();
    public CMIOrderBookV2ConsumerCache getBookDepthConsumerCache();
    public CMIExpectedOpeningPriceV2ConsumerCache getEOPConsumerCache();
}
