package com.cboe.application.supplier.proxy.overlayProxy;

import com.cboe.idl.cmiMarketData.TickerStruct;

public class TickerOverlayHelper
{
    private ProductOverlayHelper tickerProductOverlayHelper;
    private int overlayCount;

    public TickerOverlayHelper()
    {
        tickerProductOverlayHelper = new ProductOverlayHelper();
        overlayCount = -1;
    }

    public synchronized int addTickers(TickerStruct[] tickers)
    {
        for (int i = 0; i < tickers.length; i++)
        {
            tickerProductOverlayHelper.addProductData(tickers[i].productKeys.productKey, tickers[i]);
        }
        return ++overlayCount;
    }

    public synchronized TickerStruct[] getTickers()
    {
        overlayCount = -1;
        return (TickerStruct[])tickerProductOverlayHelper.getProductData(com.cboe.client.util.CollectionHelper.EMPTY_TickerStruct_ARRAY);
    }
}
