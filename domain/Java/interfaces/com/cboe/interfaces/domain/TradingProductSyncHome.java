package com.cboe.interfaces.domain;

/**
 * This home is a container trading product data synchronization between master
 * and slave trade servers.
 * 
 * @author Hemant Thakkar
 *
 */
public interface TradingProductSyncHome
{
    public static final String HOME_NAME = "TradingProductSyncHome";
    public TradingProductSync getTradingProductSync();
}
