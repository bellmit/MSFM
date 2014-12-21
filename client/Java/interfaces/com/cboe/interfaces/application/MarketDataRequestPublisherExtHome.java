package com.cboe.interfaces.application;

/**
 * @author Jing Chen
 */
public interface MarketDataRequestPublisherExtHome
{
    public final static String HOME_NAME = "MarketDataRequestPublisherExtHome";

    public MarketDataRequestPublisherExt create() throws Exception;

    public MarketDataRequestPublisherExt find() throws Exception;

}
