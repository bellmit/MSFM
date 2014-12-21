package com.cboe.interfaces.remoteApplication;

/**
 * @author Jing Chen
 */
public interface RemoteCASMarketDataServiceHome
{
    public final static String HOME_NAME = "RemoteCASMarketDataServiceHome";
    public RemoteCASMarketDataService create(RemoteCASSessionManager userSession);
}
