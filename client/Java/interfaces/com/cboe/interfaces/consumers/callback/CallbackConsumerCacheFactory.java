package com.cboe.interfaces.consumers.callback;

/**
 * Created by IntelliJ IDEA.
 * User: Davisson
 * Date: Feb 26, 2003
 * Time: 3:38:52 PM
 * To change this template use Options | File Templates.
 */
public interface CallbackConsumerCacheFactory
{
    public CMICurrentMarketConsumerCache getCurrentMarketConsumerCache();
    public CMIRecapConsumerCache getRecapConsumerCache();
    public CMINBBOConsumerCache getNBBOConsumerCache();
    public CMIOrderBookConsumerCache getBookDepthConsumerCache();
}
