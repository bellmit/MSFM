package com.cboe.remoteApplication.marketData;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.interfaces.remoteApplication.RemoteCASMarketDataService;
import com.cboe.interfaces.remoteApplication.RemoteCASMarketDataServiceHome;
import com.cboe.interfaces.remoteApplication.RemoteCASSessionManager;

/**
 * @author Jing Chen
 */
public class RemoteCASMarketDataServiceHomeImpl extends ClientBOHome implements RemoteCASMarketDataServiceHome
{
    public static final String MARKET_DATA_CALLBACK_TIME_OUT    = "marketDataCallbackTimeout";
    protected int marketDataCallbackTimeout;

    public RemoteCASMarketDataServiceHomeImpl()
    {
        super();
    }

    public RemoteCASMarketDataService create(RemoteCASSessionManager session)
    {
        RemoteCASMarketDataServiceImpl remoteMarketDataService = new RemoteCASMarketDataServiceImpl(session, marketDataCallbackTimeout );
        addToContainer(remoteMarketDataService);
        remoteMarketDataService.create(String.valueOf(remoteMarketDataService.hashCode()));
        return remoteMarketDataService;
    }

    public void clientInitialize()
        throws Exception
    {
        marketDataCallbackTimeout = Integer.parseInt(getProperty(MARKET_DATA_CALLBACK_TIME_OUT));
    }
}
