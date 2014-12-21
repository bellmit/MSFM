//
// -----------------------------------------------------------------------------------
// Source file: TickerV4Impl.java
//
// PACKAGE: com.cboe.presentation.marketData.express
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.marketData.express;

import java.util.*;

import com.cboe.idl.cmiMarketData.TickerStructV4;

import com.cboe.interfaces.presentation.marketData.express.TickerV4;
import com.cboe.interfaces.domain.Price;

import com.cboe.presentation.common.formatters.DisplayPriceFactory;

import com.cboe.domain.util.MarketDataStructBuilder;

public class TickerV4Impl extends AbstractV4MarketData implements TickerV4
{
    private TickerStructV4 struct;
    private Price tradePrice;
    private Date tradeTime;
    private Date sentTime;

    private int classKey;
    private int productKey;
    private short productType;
    private String exchange;
    private byte priceScale;
    private int tradeVolume;
    private String salePrefix;
    private String salePostfix;

    private Price tradePricePrev;
    private int tradeVolumePrev;

    private Date tradePriceLastUpdated;
    private Date tradeVolumeLastUpdated;

    public TickerV4Impl()
    {
        this(new TickerStructV4(0, 0, (short)0, "", 0, (byte)0, 0, 0, 0, "", ""));
    }

    public TickerV4Impl(TickerStructV4 struct)
    {
        this(struct, -1);
    }

    public TickerV4Impl(TickerStructV4 struct, int messageSequenceNumber)
    {
        super(messageSequenceNumber);
        setTickerStructV4(struct);
    }

    public String getExchange()
    {
        return exchange;
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

    public String getSalePostfix()
    {
        return salePostfix;
    }

    public String getSalePrefix()
    {
        return salePrefix;
    }

    public TickerStructV4 getTickerStructV4()
    {
        return struct;
    }

    public synchronized void setTickerStructV4(TickerStructV4 struct)
    {
        this.struct = MarketDataStructBuilder.cloneTickerStructV4(struct);
        Date curTime = new Date();

        tradeTime = ExpressMDTimeHelper.convertMillisSinceMidnight(struct.tradeTime).getDate();
        sentTime = ExpressMDTimeHelper.convertMillisSinceMidnight(struct.sentTime).getDate();

        classKey = struct.classKey;
        productKey = struct.productKey;
        productType = struct.productType;
        exchange = struct.exchange;
        priceScale = struct.priceScale;
        salePrefix = struct.salePrefix;
        salePostfix = struct.salePostfix;

        Price newTradePrice = DisplayPriceFactory.create(struct.tradePrice, struct.priceScale);
        if (tradePrice == null)
        {
            tradePrice = newTradePrice;
            tradePricePrev = tradePrice;
        }
        else if (!tradePrice.equals(newTradePrice))
        {
            tradePricePrev = tradePrice;
            tradePrice = newTradePrice;
            tradePriceLastUpdated = curTime;
        }

        if (struct.tradeVolume != tradeVolume)
        {
            tradeVolumePrev = tradeVolume;
            tradeVolume = struct.tradeVolume;
            tradeVolumeLastUpdated = curTime;
        }
    }

    public Price getTradePrice()
    {
        return tradePrice;
    }

    public Date getTradeTime()
    {
        return tradeTime;
    }

    public int getTradeVolume()
    {
        return tradeVolume;
    }

    public Date getSentTime()
    {
        return sentTime;
    }


    public Date getTradePriceLastUpdated()
    {
        return tradePriceLastUpdated;
    }

    public Price getTradePricePrev()
    {
        return tradePricePrev;
    }

    public Date getTradeVolumeLastUpdated()
    {
        return tradeVolumeLastUpdated;
    }

    public int getTradeVolumePrev()
    {
        return tradeVolumePrev;
    }

    /**
     * Two instances of TickerV4 are considered equal if they're for the same exchange and product.
     * @param obj
     * @return true if exchanges and productKeys are equal
     */
    public boolean equals(Object obj)
    {
        boolean retVal = false;
        if(obj != null && obj instanceof TickerV4)
        {
            retVal = super.equals(obj);
        }
        return retVal;
    }
}
