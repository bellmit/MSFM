//
// -----------------------------------------------------------------------------------
// Source file: CurrentMarketV4Impl.java
//
// PACKAGE: com.cboe.presentation.marketData.express
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.marketData.express;

import java.util.*;

import com.cboe.idl.cmiMarketData.CurrentMarketStructV4;
import com.cboe.idl.cmiMarketData.MarketVolumeStructV4;

import com.cboe.interfaces.presentation.marketData.express.CurrentMarketV4;
import com.cboe.interfaces.domain.Price;

import com.cboe.presentation.common.formatters.DisplayPriceFactory;

public class CurrentMarketV4Impl implements CurrentMarketV4
{
    private CurrentMarketStructV4 struct;
    private String identifierString;

    private int classKey;
    private int productKey;
    private short productType;
    private String exchange;
    private Date sentTime;
    private byte currentMarketType;
    private char bidTickDirection;
    private byte marketIndicator;
    private short productState;
    private byte priceScale;

    private Price bidPrice = DisplayPriceFactory.getNoPrice();
    private Price askPrice = DisplayPriceFactory.getNoPrice();

    private MarketVolumeStructV4[] bidSizeSequence;
    private MarketVolumeStructV4[] askSizeSequence;

    private Price bidPricePrev = DisplayPriceFactory.getNoPrice();
    private Price askPricePrev = DisplayPriceFactory.getNoPrice();
    private MarketVolumeStructV4[] bidSizeSequencePrev = new MarketVolumeStructV4[0];
    private MarketVolumeStructV4[] askSizeSequencePrev = new MarketVolumeStructV4[0];
    private int prevAskQty = -1;
    private int prevBidQty = -1;

    private Date bidPriceLastUpdated;
    private Date askPriceLastUpdated;
    private Date bidSizeLastUpdated;
    private Date askSizeLastUpdated;

    public CurrentMarketV4Impl(CurrentMarketStructV4 struct)
    {
        setCurrentMarketStructV4(struct);
    }

    public synchronized void setCurrentMarketStructV4(CurrentMarketStructV4 struct)
    {
        Date curTime = new Date();

        this.struct = struct;

        classKey = struct.classKey;
        productKey = struct.productKey;
        productType = struct.productType;
        exchange = struct.exchange;
        currentMarketType = struct.currentMarketType;
        bidTickDirection = struct.bidTickDirection;
        marketIndicator = struct.marketIndicator;
        productState = struct.productState;
        priceScale = struct.priceScale;
        sentTime = ExpressMDTimeHelper.convertMillisSinceMidnight(struct.sentTime).getDate();

        Price newBidPrice = DisplayPriceFactory.create(struct.bidPrice, struct.priceScale);
        if(bidPrice == null)
        {
            bidPrice = newBidPrice;
            bidPricePrev = bidPrice;
            bidPriceLastUpdated = curTime;
        }
        else if(!bidPrice.equals(newBidPrice))
        {
            bidPricePrev = bidPrice;
            bidPrice = newBidPrice;
            bidPriceLastUpdated = curTime;
        }

        Price newAskPrice = DisplayPriceFactory.create(struct.askPrice, struct.priceScale);
        if(askPrice == null)
        {
            askPrice = newAskPrice;
            askPricePrev = askPrice;
            askPriceLastUpdated = curTime;
        }
        else if(!askPrice.equals(newAskPrice))
        {
            askPricePrev = askPrice;
            askPrice = newAskPrice;
            askPriceLastUpdated = curTime;
        }

        int newBidQty = getTotalMarketVolume(struct.bidSizeSequence);
        if (newBidQty != prevBidQty)
        {
            bidSizeSequencePrev = bidSizeSequence;
            bidSizeSequence = struct.bidSizeSequence;
            prevBidQty = newBidQty;
            bidSizeLastUpdated = curTime;
        }

        int newAskQty = getTotalMarketVolume(struct.askSizeSequence);
        if(newAskQty != prevAskQty)
        {
            askSizeSequencePrev = askSizeSequence;
            askSizeSequence = struct.askSizeSequence;
            prevAskQty = newAskQty;
            askSizeLastUpdated = curTime;
        }
    }

    private static int getTotalMarketVolume(MarketVolumeStructV4[] newStructs)
    {
        int newTotal = 0;
        for (int i = 0; i < newStructs.length; i++)
        {
            newTotal += newStructs[i].quantity;
        }
        return newTotal;
    }

    public CurrentMarketStructV4 getCurrentMarketStructV4()
    {
        return struct;
    }

    public int getClassKey()
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

    public String getExchange()
    {
        return exchange;
    }

    /**
     * Returns a string to uniquely identify this CurrentMarketV4, based on its Exchange and Product
     */
    public String getIdentifierString()
    {
        if(identifierString == null)
        {
            identifierString = getProductKey()+getExchange();
        }
        return identifierString;
    }

    public byte getCurrentMarketType()
    {
        return currentMarketType;
    }

    public Price getBidPrice()
    {
        return bidPrice;
    }

    public MarketVolumeStructV4[] getBidSizeSequence()
    {
        return bidSizeSequence;
    }

    public char getBidTickDirection()
    {
        return bidTickDirection;
    }

    public Price getAskPrice()
    {
        return askPrice;
    }

    public MarketVolumeStructV4[] getAskSizeSequence()
    {
        return askSizeSequence;
    }

    public Date getSentTime()
    {
        return sentTime;
    }

    public byte getMarketIndicator()
    {
        return marketIndicator;
    }

    public short getProductState()
    {
        return productState;
    }

    public byte getPriceScale()
    {
        return priceScale;
    }

    public Date getAskPriceLastUpdated()
    {
        return askPriceLastUpdated;
    }

    public Price getAskPricePrev()
    {
        return askPricePrev;
    }

    public Date getAskSizeSequenceLastUpdated()
    {
        return askSizeLastUpdated;
    }

    public MarketVolumeStructV4[] getAskSizeSequencePrev()
    {
        return askSizeSequencePrev;
    }

    public Date getBidPriceLastUpdated()
    {
        return bidPriceLastUpdated;
    }

    public Price getBidPricePrev()
    {
        return bidPricePrev;
    }

    public Date getBidSizeSequenceLastUpdated()
    {
        return bidSizeLastUpdated;
    }

    public MarketVolumeStructV4[] getBidSizeSequencePrev()
    {
        return bidSizeSequencePrev;
    }

    public int hashCode()
    {
        return getIdentifierString().hashCode();
    }

    /**
     * Two instances of CurrentMarketV4 are considered equal if they're for the same exchange and product.
     * @param obj
     * @return true if exchanges and productKeys are equal
     */
    public boolean equals(Object obj)
    {
        boolean retVal = false;
        if(obj != null)
        {
            CurrentMarketV4Impl other = (CurrentMarketV4Impl) obj;
            if(other.getIdentifierString().equals(getIdentifierString()))
            {
                retVal = true;
            }
        }
        return retVal;
    }
}
