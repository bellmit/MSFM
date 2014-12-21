package com.cboe.application.supplier.proxy.overlayProxy;

import com.cboe.idl.cmiMarketData.ExpectedOpeningPriceStruct;

public class ExpectedOpeningPriceOverlayHelper
{
    private ProductOverlayHelper expectedOpeningPriceProductOverlayHelper;
    private int overlayCount;

    public ExpectedOpeningPriceOverlayHelper()
    {
        expectedOpeningPriceProductOverlayHelper = new ProductOverlayHelper();
        overlayCount = -1;
    }

    public synchronized int addExpectedOpeningPrices(ExpectedOpeningPriceStruct[] expectedOpeningPrices)
    {
        for (int i = 0; i < expectedOpeningPrices.length; i++)
        {
            expectedOpeningPriceProductOverlayHelper.addProductData(expectedOpeningPrices[i].productKeys.productKey, expectedOpeningPrices[i]);
        }
        return ++overlayCount;
    }

    public synchronized ExpectedOpeningPriceStruct[] getExpectedOpeningPrices()
    {
        overlayCount = -1;
        return (ExpectedOpeningPriceStruct[])expectedOpeningPriceProductOverlayHelper.getProductData(com.cboe.client.util.CollectionHelper.EMPTY_ExpectedOpeningPriceStruct_ARRAY);
    }
}
