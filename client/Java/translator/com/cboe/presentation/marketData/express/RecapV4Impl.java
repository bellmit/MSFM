//
// -----------------------------------------------------------------------------------
// Source file: RecapV4Impl.java
//
// PACKAGE: com.cboe.presentation.marketData.express
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.marketData.express;

import java.util.*;

import com.cboe.idl.cmiMarketData.RecapStructV4;

import com.cboe.interfaces.presentation.marketData.express.RecapV4;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.domain.Price;

import com.cboe.presentation.common.formatters.DisplayPriceFactory;

import com.cboe.domain.util.MarketDataStructBuilder;

public class RecapV4Impl extends AbstractV4MarketData implements RecapV4
{
    private RecapStructV4 struct;

    public int classKey;
    public int productKey;
    public short productType;
    public String exchange;
    public Date sentTime;
    public byte priceScale;
    public Price lowPrice;
    public Price highPrice;
    public Price openPrice;
    public Price previousClosePrice;
    public String statusCodes;

    public Price lowPricePrev;
    public Price highPricePrev;

    public Date lowPriceLastUpdated;
    public Date highPriceLastUpdated;

    public RecapV4Impl()
    {
        this(new RecapStructV4(0, 0, (short)0, "", 0, (byte)0, 0, 0, 0, 0, ""));
    }

    public RecapV4Impl(Product product, String exchange)
    {
        this(new RecapStructV4(product.getProductKeysStruct().classKey, product.getProductKey(),
                                  product.getProductType(), exchange, 0, (byte) 0, 0, 0, 0, 0, ""));
        this.product = product;
    }

    public RecapV4Impl(RecapStructV4 struct)
    {
        this(struct, -1);
    }

    public RecapV4Impl(RecapStructV4 struct, int messageSequenceNumber)
    {
        super(messageSequenceNumber);
        setRecapStructV4(struct);
    }

    public RecapStructV4 getRecapStructV4()
    {
        return struct;
    }

    public synchronized void setRecapStructV4(RecapStructV4 struct)
    {
        this.struct = MarketDataStructBuilder.cloneRecapStructV4(struct);
        Date curTime = new Date();

        sentTime = ExpressMDTimeHelper.convertMillisSinceMidnight(struct.sentTime).getDate();

        classKey = struct.classKey;
        productKey = struct.productKey;
        productType = struct.productType;
        exchange = struct.exchange;
        priceScale = struct.priceScale;
        statusCodes = struct.statusCodes;

        Price newHighPrice = DisplayPriceFactory.create(struct.highPrice, struct.priceScale);
        if (highPrice == null)
        {
            highPrice = newHighPrice;
            highPricePrev = highPrice;
            highPriceLastUpdated = curTime;
        }
        else if (!highPrice.equals(newHighPrice))
        {
            highPricePrev = highPrice;
            highPrice = newHighPrice;
            highPriceLastUpdated = curTime;
        }

        Price newLowPrice = DisplayPriceFactory.create(struct.lowPrice, struct.priceScale);
        if (lowPrice == null)
        {
            lowPrice = newLowPrice;
            lowPricePrev = lowPrice;
            lowPriceLastUpdated = curTime;
        }
        else if (!lowPrice.equals(newLowPrice))
        {
            lowPricePrev = lowPrice;
            lowPrice = newLowPrice;
            lowPriceLastUpdated = curTime;
        }
    }

    public String getExchange()
    {
        return exchange;
    }

    public Price getHighPrice()
    {
        return highPrice;
    }

    public Price getLowPrice()
    {
        return lowPrice;
    }

    public Price getOpenPrice()
    {
        return openPrice;
    }

    public Price getPreviousClosePrice()
    {
        return previousClosePrice;
    }

    public byte getPriceScale()
    {
        return priceScale;
    }

    public int getProductClassKey()
    {
        return classKey;
    }

    public int getProductKey()
    {
        return productKey;
    }

    public short getProductType()
    {
        return productType;
    }

    public Date getSentTime()
    {
        return sentTime;
    }

    public String getStatusCodes()
    {
        return statusCodes;
    }

    public Date getHighPriceLastUpdated()
    {
        return highPriceLastUpdated;
    }

    public Price getHighPricePrev()
    {
        return highPricePrev;
    }

    public Date getLowPriceLastUpdated()
    {
        return lowPriceLastUpdated;
    }

    public Price getLowPricePrev()
    {
        return lowPricePrev;
    }

    /**
     * Two instances of RecapV4 are considered equal if they're for the same exchange and product.
     * @param obj
     * @return true if exchanges and productKeys are equal
     */
    public boolean equals(Object obj)
    {
        boolean retVal = false;
        if(obj != null && obj instanceof RecapV4)
        {
            retVal = super.equals(obj);
        }
        return retVal;
    }
}
