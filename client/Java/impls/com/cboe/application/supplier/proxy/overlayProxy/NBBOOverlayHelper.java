package com.cboe.application.supplier.proxy.overlayProxy;

import com.cboe.idl.cmiMarketData.NBBOStruct;

public class NBBOOverlayHelper
{
    private ProductOverlayHelper NBBOProductOverlayHelper;
    private int overlayCount;

    public NBBOOverlayHelper()
    {
        NBBOProductOverlayHelper = new ProductOverlayHelper();
        overlayCount = -1;
    }

    public synchronized int addNBBOs(NBBOStruct[] NBBOs)
    {
        for (int i = 0; i < NBBOs.length; i++)
        {
            NBBOProductOverlayHelper.addProductData(NBBOs[i].productKeys.productKey, NBBOs[i]);
        }
        return ++overlayCount;
    }

    public synchronized NBBOStruct[] getNBBOs()
    {
        overlayCount = -1;
        return (NBBOStruct[])NBBOProductOverlayHelper.getProductData(com.cboe.client.util.CollectionHelper.EMPTY_NBBOStruct_ARRAY);
    }
}
