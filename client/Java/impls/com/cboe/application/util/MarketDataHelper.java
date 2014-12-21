/*
 * Created on Apr 27, 2004
 * MarketDataHelper.java
 * This static class contains Helper methods for MarketData application functions.
 * 
 */
package com.cboe.application.util;

import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiMarketData.CurrentMarketStructV4;

/**
 * @author Carol Vazirani
 *
 */

public class MarketDataHelper {
    /***********************
     * findPublicIndexAtBest - Look for publicMarket interest for the given product Key
     * @param productKey
     * @param bestPublicMarket
     * @return index position of the product key if found, else return -1
     */
    public static int findPublicIndexAtBest(int productKey, CurrentMarketStruct[] bestPublicMarket)
    {
        int indexAtBest = -1;
        if (bestPublicMarket.length == 0)
        {
            return indexAtBest;
        }
        for (int i=0; i < bestPublicMarket.length; i++)
        {
            if (productKey == (bestPublicMarket[i].productKeys.productKey))
            {
                return (i);
            }
        }
        //if code comes here, there is no publicBestOffer for products in this 
        return indexAtBest;
    }

    public static int findPublicIndexAtBest(String exchange, int productKey, CurrentMarketStructV4[] bestPublicMarket)
    {
        int indexAtBest = -1;
        if(bestPublicMarket.length > 0)
        {
            for(int i = 0; i < bestPublicMarket.length; i++)
            {
                if(productKey == bestPublicMarket[i].productKey && exchange.equals(bestPublicMarket[i].exchange))
                {
                    indexAtBest = i;
                    break;
                }
            }
        }
        return indexAtBest;
    }
}
