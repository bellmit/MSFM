package com.cboe.interfaces.application;

/**
 * @author Jing Chen
 */
public interface MarketDataRequestPublisherHome
{
    public final static String HOME_NAME = "MarketDataRequestPublisherHome";

    public MarketDataRequestPublisher create() throws Exception;

    public MarketDataRequestPublisher find() throws Exception;

}
