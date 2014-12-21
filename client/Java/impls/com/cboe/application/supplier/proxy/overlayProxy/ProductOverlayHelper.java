package com.cboe.application.supplier.proxy.overlayProxy;

import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.util.Collection;

public class ProductOverlayHelper
{
    protected Map lastProductData;

    public ProductOverlayHelper()
    {
        lastProductData = new HashMap();
    }

    public synchronized void addProductData(int productKey, Object productData)
    {
        lastProductData.put(Integer.valueOf(productKey), productData);
    }

    public synchronized void removeProductData(int productKey)
    {
        lastProductData.remove(Integer.valueOf(productKey));
    }

    public synchronized Object[] getProductData(Object[] type)
    {
        Map currentBlock = lastProductData;
        lastProductData = new HashMap();
        return currentBlock.values().toArray(type);
    }

    public boolean isEmpty()
    {
        return lastProductData.isEmpty();
    }

//    public Collection getProductData()
//    {
//        return Collections.unmodifiableCollection(lastProductData.values());
//    }
}