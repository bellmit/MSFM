//
// -----------------------------------------------------------------------------------
// Source file: LastSaleV4Impl.java
//
// PACKAGE: com.cboe.presentation.marketData.express
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.marketData.express;

import java.util.*;

import com.cboe.idl.cmiMarketData.LastSaleStructV4;

import com.cboe.interfaces.presentation.marketData.express.LastSaleV4;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.domain.Price;

import com.cboe.presentation.common.formatters.DisplayPriceFactory;

import com.cboe.domain.util.MarketDataStructBuilder;

public class LastSaleV4Impl extends AbstractV4MarketData implements LastSaleV4
{
    private LastSaleStructV4 struct;
    private Price lastSalePrice;
    private Date lastSaleTime;
    private Date sentTime;
    private Price netPriceChange;

    private int classKey;
    private int productKey;
    private short productType;
    private String exchange;
    private byte priceScale;
    private int lastSaleVolume;
    private int totalVolume;
    private char tickDirection;

    private Price lastSalePricePrev = DisplayPriceFactory.getNoPrice();
    private Price netPriceChangePrev = DisplayPriceFactory.getNoPrice();
    private int lastSaleVolumePrev;

    private Date lastSalePriceLastUpdated;
    private Date netPriceChangeLastUpdated;
    private Date lastSaleVolumeLastUpdated;

    public LastSaleV4Impl()
    {
        this(new LastSaleStructV4(0, 0, (short) 0, "", 0, (byte) 0, 0, 0, 0, 0, ' ', 0));
    }

    public LastSaleV4Impl(Product product, String exchange)
    {
        this(new LastSaleStructV4(product.getProductKeysStruct().classKey, product.getProductKey(), product.getProductType(), exchange, 0, (byte)0, 0, 0, 0, 0, ' ', 0));
        this.product = product;
    }

    public LastSaleV4Impl(LastSaleStructV4 struct)
    {
        this(struct, -1);
    }

    public LastSaleV4Impl(LastSaleStructV4 struct, int messageSequenceNumber)
    {
        super(messageSequenceNumber);
        setLastSaleStructV4(struct);
    }

    public char getTickDirection()
    {
        return tickDirection;
    }

    public String getExchange()
    {
        return exchange;
    }

    public Price getLastSalePrice()
    {
        return lastSalePrice;
    }

    public LastSaleStructV4 getLastSaleStructV4()
    {
        // return a clone so the fields can't be externally edited in this.struct
        return struct;
    }

    public synchronized void setLastSaleStructV4(LastSaleStructV4 struct)
    {
        this.struct = MarketDataStructBuilder.cloneLastSaleStructV4(struct);
        Date curTime = new Date();

        lastSaleTime = ExpressMDTimeHelper.convertMillisSinceMidnight(struct.lastSaleTime).getDate();
        sentTime = ExpressMDTimeHelper.convertMillisSinceMidnight(struct.sentTime).getDate();

        classKey = struct.classKey;
        productKey = struct.productKey;
        productType = struct.productType;
        exchange = struct.exchange;
        priceScale = struct.priceScale;
        totalVolume = struct.totalVolume;
        tickDirection = struct.tickDirection;


        Price newLastSalePrice = DisplayPriceFactory.create(struct.lastSalePrice, struct.priceScale);
        if(lastSalePrice == null)
        {
            lastSalePrice = newLastSalePrice;
            lastSalePricePrev = lastSalePrice;
            lastSalePriceLastUpdated = curTime;
        }
        else if (!lastSalePrice.equals(newLastSalePrice))
        {
            lastSalePricePrev = lastSalePrice;
            lastSalePrice = newLastSalePrice;
            lastSalePriceLastUpdated = curTime;
        }

        Price newNetPriceChange = DisplayPriceFactory.create(struct.netPriceChange, struct.priceScale);
        if(netPriceChange == null)
        {
            netPriceChange = newNetPriceChange;
            netPriceChangePrev = netPriceChange;
            netPriceChangeLastUpdated = curTime;
        }
        else
        {
            netPriceChangePrev = netPriceChange;
            netPriceChange = newNetPriceChange;
            netPriceChangeLastUpdated = curTime;
        }

        if(struct.lastSaleVolume != lastSaleVolume)
        {
            lastSaleVolumePrev = lastSaleVolume;
            lastSaleVolume = struct.lastSaleVolume;
            lastSaleVolumeLastUpdated = curTime;
        }
    }

    public Date getLastSaleTime()
    {
        return lastSaleTime;
    }

    public int getLastSaleVolume()
    {
        return lastSaleVolume;
    }

    public Price getNetPriceChange()
    {
        return netPriceChange;
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

    public int getTotalVolume()
    {
        return totalVolume;
    }

    public Date getSentTime()
    {
        return sentTime;
    }

    public Date getLastSalePriceLastUpdated()
    {
        return lastSalePriceLastUpdated;
    }

    public Price getLastSalePricePrev()
    {
        return lastSalePricePrev;
    }

    public Date getLastSaleVolumeLastUpdated()
    {
        return lastSaleVolumeLastUpdated;
    }

    public int getLastSaleVolumePrev()
    {
        return lastSaleVolumePrev;
    }

    public Date getNetPriceChangeLastUpdated()
    {
        return netPriceChangeLastUpdated;
    }

    public Price getNetPriceChangePrev()
    {
        return netPriceChangePrev;
    }

    /**
     * Two instances of LastSaleV4 are considered equal if they're for the same exchange and product.
     * @param obj
     * @return true if exchanges and productKeys are equal
     */
    public boolean equals(Object obj)
    {
        boolean retVal = false;
        if(obj != null && obj instanceof LastSaleV4)
        {
            retVal = super.equals(obj);
        }
        return retVal;
    }
}
