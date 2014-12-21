package com.cboe.application.supplier.proxy.overlayProxy;

import com.cboe.idl.cmiMarketData.RecapStruct;

public class RecapOverlayHelper
{
    private ProductOverlayHelper recapProductOverlayHelper;
    private int overlayCount;

    public RecapOverlayHelper()
    {
        recapProductOverlayHelper = new ProductOverlayHelper();
        overlayCount = -1;
    }

    public synchronized int addRecaps(RecapStruct[] recaps)
    {
        for (int i = 0; i < recaps.length; i++)
        {
            recapProductOverlayHelper.addProductData(recaps[i].productKeys.productKey, recaps[i]);
        }
        return ++overlayCount;
    }

    public synchronized RecapStruct[] getRecaps()
    {
        overlayCount = -1;
        return (RecapStruct[])recapProductOverlayHelper.getProductData(com.cboe.client.util.CollectionHelper.EMPTY_RecapStruct_ARRAY);
    }
}
