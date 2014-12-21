package com.cboe.application.supplier.proxy.overlayProxy;

import com.cboe.client.util.CollectionHelper;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.interfaces.domain.CurrentMarketContainer;
import com.cboe.domain.util.CurrentMarketContainerImpl;

public class CurrentMarketOverlayHelper
{
    private ProductOverlayHelper currentMarketProductOverlayHelper;
    private ProductOverlayHelper currentMarketV3ProductOverlayHelper;
    private int overlayCount;

    public CurrentMarketOverlayHelper()
    {
        currentMarketProductOverlayHelper = new ProductOverlayHelper();
        currentMarketV3ProductOverlayHelper = new ProductOverlayHelper();
        overlayCount = -1;
    }

    public synchronized int addCurrentMarkets(CurrentMarketStruct[] currentMarkets)
    {
        for (int i = 0; i < currentMarkets.length; i++)
        {
            currentMarketProductOverlayHelper.addProductData(currentMarkets[i].productKeys.productKey, currentMarkets[i]);
        }
        return ++overlayCount;
    }


    public synchronized int addCurrentMarkets(CurrentMarketContainer currentMarketV2Container)
    {
        CurrentMarketStruct[] bestMarkets = currentMarketV2Container.getBestMarkets();
        CurrentMarketStruct[] bestPublicMarkets = currentMarketV2Container.getBestPublicMarketsAtTop();
        for (int i = 0; i < bestMarkets.length; i++)
        {
            currentMarketProductOverlayHelper.addProductData(bestMarkets[i].productKeys.productKey, bestMarkets[i]);
            currentMarketV3ProductOverlayHelper.removeProductData(bestMarkets[i].productKeys.productKey);
        }

        for (int i = 0; i < bestPublicMarkets.length; i++)
        {
            currentMarketV3ProductOverlayHelper.addProductData(bestPublicMarkets[i].productKeys.productKey,
                                                               bestPublicMarkets[i]);
        }

        
        return ++overlayCount;
    }

    public synchronized CurrentMarketStruct[] getBestMarkets()
    {
        overlayCount = -1;
        return (CurrentMarketStruct[])currentMarketProductOverlayHelper.getProductData(CollectionHelper.EMPTY_CurrentMarketStruct_ARRAY);
    }

    public synchronized CurrentMarketContainer getBestMarketsWithPublicMarket()
    {
        overlayCount = -1;
        CurrentMarketStruct[] bestMarkets = (CurrentMarketStruct[])currentMarketProductOverlayHelper.getProductData(CollectionHelper.EMPTY_CurrentMarketStruct_ARRAY);
        CurrentMarketStruct[] publicBestMarkets = (CurrentMarketStruct[])currentMarketV3ProductOverlayHelper.getProductData(CollectionHelper.EMPTY_CurrentMarketStruct_ARRAY);
        return new CurrentMarketContainerImpl(bestMarkets,publicBestMarkets);
    }
}
