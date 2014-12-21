package com.cboe.cfix.util;

/**
 * CfixOverlaidMarketDataHolder.java
 *
 * @author Dmitry Volpyansky
 *
 */

import com.cboe.client.util.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.interfaces.cfix.*;
import com.cboe.interfaces.domain.RecapContainerV4IF;
import com.cboe.domain.util.RecapContainerV4;

public class OverlayPolicyMarketDataHolder implements OverlayPolicyMarketDataHolderIF
{
    protected String                       mdReqID;
    protected int                          size;
    protected int                          marketDataType;
    protected int                          policyType;
    protected BitArrayIF                   overlaidNever;
    protected BitArrayIF                   overlaidAlways;
    protected BookDepthStruct[]            bookDepthStructs            = CollectionHelper.EMPTY_BookDepthStruct_ARRAY;
    protected CurrentMarketStruct[]        currentMarketStructs        = CollectionHelper.EMPTY_CurrentMarketStruct_ARRAY;
    protected CurrentMarketStructV4[]      currentMarketStructsV4      = CollectionHelper.EMPTY_CurrentMarketStructV4_ARRAY;
    protected ExpectedOpeningPriceStruct[] expectedOpeningPriceStructs = CollectionHelper.EMPTY_ExpectedOpeningPriceStruct_ARRAY;
    protected NBBOStruct[]                 nbboStructs                 = CollectionHelper.EMPTY_NBBOStruct_ARRAY;
    protected RecapStruct[]                recapStructs                = CollectionHelper.EMPTY_RecapStruct_ARRAY;
    protected TickerStruct[]               tickerStructs               = CollectionHelper.EMPTY_TickerStruct_ARRAY;
    protected RecapStructV4[]              recapStructsV4              = CollectionHelper.EMPTY_RecapStructV4_ARRAY;
    protected RecapContainerV4IF[]         recapContainersV4           = CollectionHelper.EMPTY_RecapContainerV4_ARRAY;
    protected TickerStructV4[]             tickerStructsV4             = CollectionHelper.EMPTY_TickerStructV4_ARRAY;

    public String getMdReqID()
    {
        return mdReqID;
    }

    public void setMdReqID(String mdReqID)
    {
        this.mdReqID = mdReqID;
    }

    public int getMarketDataType()
    {
        return marketDataType;
    }

    public void setMarketDataType(int marketDataType)
    {
        this.marketDataType = marketDataType;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public int size()
    {
        return size;
    }

    public BookDepthStruct[] getBookDepthStructs()
    {
        return bookDepthStructs;
    }

    public void setBookDepthStructs(BookDepthStruct[] bookDepthStructs)
    {
        this.bookDepthStructs = bookDepthStructs;
    }

    public BookDepthStruct getBookDepthStruct(int index)
    {
        return bookDepthStructs[index];
    }

    public CurrentMarketStruct[] getCurrentMarketStructs()
    {
        return currentMarketStructs;
    }

    public void setCurrentMarketStructs(CurrentMarketStruct[] currentMarketStructs)
    {
        this.currentMarketStructs = currentMarketStructs;
    }

    public CurrentMarketStruct getCurrentMarketStruct(int index)
    {
        return currentMarketStructs[index];
    }

    public CurrentMarketStructV4[] getCurrentMarketStructsV4()
    {
        return currentMarketStructsV4;
    }

    public void setCurrentMarketStructsV4(CurrentMarketStructV4[] currentMarketStructsV4)
    {
        this.currentMarketStructsV4 = currentMarketStructsV4;
    }

    public CurrentMarketStructV4 getCurrentMarketStructV4(int index)
    {
        return currentMarketStructsV4[index];
    }

    public ExpectedOpeningPriceStruct[] getExpectedOpeningPriceStructs()
    {
        return expectedOpeningPriceStructs;
    }

    public void setExpectedOpeningPriceStructs(ExpectedOpeningPriceStruct[] expectedOpeningPriceStructs)
    {
        this.expectedOpeningPriceStructs = expectedOpeningPriceStructs;
    }

    public ExpectedOpeningPriceStruct getExpectedOpeningPriceStruct(int index)
    {
        return expectedOpeningPriceStructs[index];
    }

    public NBBOStruct[] getNbboStructs()
    {
        return nbboStructs;
    }

    public void setNbboStructs(NBBOStruct[] nbboStructs)
    {
        this.nbboStructs = nbboStructs;
    }

    public NBBOStruct getNbboStruct(int index)
    {
        return nbboStructs[index];
    }

    public RecapStruct[] getRecapStructs()
    {
        return recapStructs;
    }

    public void setRecapStructs(RecapStruct[] recapStructs)
    {
        this.recapStructs = recapStructs;
    }

    public RecapStruct getRecapStruct(int index)
    {
        return recapStructs[index];
    }

    public RecapStructV4[] getRecapStructsV4()
    {
        return recapStructsV4;
    }

    public void setRecapStructsV4(RecapStructV4[] recapStructsV4)
    {
        this.recapStructsV4 = recapStructsV4;
    }

    public RecapStructV4 getRecapStructV4(int index)
    {
        return recapStructsV4[index];
    }

    public RecapContainerV4IF[] getRecapContainersV4()
    {
        return recapContainersV4;
    }

    public void setRecapContainersV4(RecapContainerV4IF[] recapContainersV4)
    {
        this.recapContainersV4 = recapContainersV4;
    }

    public RecapContainerV4IF getRecapContainerV4(int index)
    {
        return recapContainersV4[index];
    }


    public TickerStruct[] getTickerStructs()
    {
        return tickerStructs;
    }

    public void setTickerStructs(TickerStruct[] tickerStructs)
    {
        this.tickerStructs = tickerStructs;
    }

    public TickerStruct getTickerStruct(int index)
    {
        return tickerStructs[index];
    }

    public TickerStructV4[] getTickerStructsV4()
    {
        return tickerStructsV4;
    }

    public void setTickerStructsV4(TickerStructV4[] tickerStructsV4)
    {
        this.tickerStructsV4 = tickerStructsV4;
    }

    public TickerStructV4 getTickerStructV4(int index)
    {
        return tickerStructsV4[index];
    }

    public void clear()
    {
        mdReqID = null;

        if (overlaidAlways != null)
            overlaidAlways.clear();

        if (overlaidNever != null)
            overlaidNever.clear();

        switch (marketDataType)
        {
            case CfixMarketDataDispatcherIF.MarketDataType_BookDepth:
                while (size-- > 0)
                {
                    bookDepthStructs[size] = null;
                }
                size = 0;
                break;
            case CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket:
                while (size-- > 0)
                {
                    currentMarketStructs[size] = null;
                    currentMarketStructsV4[size] = null;
                }
                size = 0;
                break;
            case CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice:
                while (size-- > 0)
                {
                    expectedOpeningPriceStructs[size] = null;
                }
                size = 0;
                break;
            case CfixMarketDataDispatcherIF.MarketDataType_Nbbo:
                while (size-- > 0)
                {
                    nbboStructs[size] = null;
                }
                size = 0;
                break;
            case CfixMarketDataDispatcherIF.MarketDataType_Recap:
                while (size-- > 0)
                {
                    recapStructs[size] = null;
                    recapStructsV4[size] = null;
                }
                size = 0;
                break;
            case CfixMarketDataDispatcherIF.MarketDataType_Ticker:
                while (size-- > 0)
                {
                    tickerStructs[size] = null;
                    tickerStructsV4[size] = null;
                }
                size = 0;
                break;
        }
    }

    public void setOverlaid(BitArrayIF overlaid)
    {
        switch (policyType)
        {
            case OverlayPolicyMarketDataListIF.ALWAYS_OVERLAY_POLICY:
                if (overlaidAlways == null)
                {
                    overlaidAlways = (BitArrayIF) overlaid.clone();
                }
                else
                {
                    overlaidAlways.copyFrom(overlaid);
                }
                break;
            default:
                if (overlaidNever == null)
                {
                    overlaidNever = (BitArrayIF) overlaid.clone();
                }
                else
                {
                    overlaidNever.copyFrom(overlaid);
                }
                break;
        }
    }

    public BitArrayIF getOverlaid()
    {
        switch (policyType)
        {
            case OverlayPolicyMarketDataListIF.ALWAYS_OVERLAY_POLICY:
                return overlaidAlways;
            default:
                return overlaidNever;
        }
    }

    public void setPolicyType(int policyType)
    {
        this.policyType = policyType;
    }

    public int getPolicyType()
    {
        return policyType;
    }
}
