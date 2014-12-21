package com.cboe.application.supplier.proxy.overlayProxy;

import com.cboe.idl.cmiMarketData.BookDepthStruct;

public class BookDepthOverlayHelper
{
    private ProductOverlayHelper bookDepthProductOverlayHelper;
    private int overlayCount;

    public BookDepthOverlayHelper()
    {
        bookDepthProductOverlayHelper = new ProductOverlayHelper();
        overlayCount = -1;
    }

    public synchronized int addBookDepths(BookDepthStruct[] bookDepths)
    {
        for (int i = 0; i < bookDepths.length; i++)
        {
            bookDepthProductOverlayHelper.addProductData(bookDepths[i].productKeys.productKey, bookDepths[i]);
        }
        return ++overlayCount;
    }

    public synchronized BookDepthStruct[] getBookDepths()
    {
        overlayCount = -1;
        return (BookDepthStruct[])bookDepthProductOverlayHelper.getProductData(com.cboe.client.util.CollectionHelper.EMPTY_BookDepthStruct_ARRAY);
    }
}
