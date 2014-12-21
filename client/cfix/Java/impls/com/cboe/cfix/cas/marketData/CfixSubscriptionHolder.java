package com.cboe.cfix.cas.marketData;

/**
 * CfixSubscriptionHolder.java
 *
 * @author Dmitry Volpyansky
 *
 */

import com.cboe.client.util.*;
import com.cboe.interfaces.cfix.*;

public class CfixSubscriptionHolder implements HasSizeIF
{
    public String[] subscriptions = new String[CfixMarketDataDispatcherIF.NumberSubscriptionMarketDataTypes];

    public CfixSubscriptionHolder()
    {

    }

    public CfixSubscriptionHolder(int marketDataType, String mdReqID)
    {
        subscribe(marketDataType, mdReqID);
    }

    public void subscribe(int marketDataType, String mdReqID)
    {
        switch (marketDataType)
        {
            case CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket:        subscriptions[CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket_index]        = mdReqID; break;
            case CfixMarketDataDispatcherIF.MarketDataType_Nbbo:                 subscriptions[CfixMarketDataDispatcherIF.MarketDataType_Nbbo_index]                 = mdReqID; break;
            case CfixMarketDataDispatcherIF.MarketDataType_Recap:                subscriptions[CfixMarketDataDispatcherIF.MarketDataType_Recap_index]                = mdReqID; break;
            case CfixMarketDataDispatcherIF.MarketDataType_BookDepth:            subscriptions[CfixMarketDataDispatcherIF.MarketDataType_BookDepth_index]            = mdReqID; break;
            case CfixMarketDataDispatcherIF.MarketDataType_BookDepthUpdate:      subscriptions[CfixMarketDataDispatcherIF.MarketDataType_BookDepthUpdate_index]      = mdReqID; break;
            case CfixMarketDataDispatcherIF.MarketDataType_Ticker:               subscriptions[CfixMarketDataDispatcherIF.MarketDataType_Ticker_index]               = mdReqID; break;
            case CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice: subscriptions[CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice_index] = mdReqID; break;
            default: return;
        }
    }

    public String unsubscribe(int marketDataType)
    {
        String mdReqID;

        switch (marketDataType)
        {
            case CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket:        mdReqID = subscriptions[CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket_index];        subscriptions[CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket_index]        = null; break;
            case CfixMarketDataDispatcherIF.MarketDataType_Nbbo:                 mdReqID = subscriptions[CfixMarketDataDispatcherIF.MarketDataType_Nbbo_index];                 subscriptions[CfixMarketDataDispatcherIF.MarketDataType_Nbbo_index]                 = null; break;
            case CfixMarketDataDispatcherIF.MarketDataType_Recap:                mdReqID = subscriptions[CfixMarketDataDispatcherIF.MarketDataType_Recap_index];                subscriptions[CfixMarketDataDispatcherIF.MarketDataType_Recap_index]                = null; break;
            case CfixMarketDataDispatcherIF.MarketDataType_BookDepth:            mdReqID = subscriptions[CfixMarketDataDispatcherIF.MarketDataType_BookDepth_index];            subscriptions[CfixMarketDataDispatcherIF.MarketDataType_BookDepth_index]            = null; break;
            case CfixMarketDataDispatcherIF.MarketDataType_BookDepthUpdate:      mdReqID = subscriptions[CfixMarketDataDispatcherIF.MarketDataType_BookDepthUpdate_index];      subscriptions[CfixMarketDataDispatcherIF.MarketDataType_BookDepthUpdate_index]      = null; break;
            case CfixMarketDataDispatcherIF.MarketDataType_Ticker:               mdReqID = subscriptions[CfixMarketDataDispatcherIF.MarketDataType_Ticker_index];               subscriptions[CfixMarketDataDispatcherIF.MarketDataType_Ticker_index]               = null; break;
            case CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice: mdReqID = subscriptions[CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice_index]; subscriptions[CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice_index] = null; break;
            default: return null;
        }

        return mdReqID;
    }

    public boolean isSubscribed(int marketDataType)
    {
        switch (marketDataType)
        {
            case CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket:        return subscriptions[CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket_index]        != null;
            case CfixMarketDataDispatcherIF.MarketDataType_Nbbo:                 return subscriptions[CfixMarketDataDispatcherIF.MarketDataType_Nbbo_index]                 != null;
            case CfixMarketDataDispatcherIF.MarketDataType_Recap:                return subscriptions[CfixMarketDataDispatcherIF.MarketDataType_Recap_index]                != null;
            case CfixMarketDataDispatcherIF.MarketDataType_BookDepth:            return subscriptions[CfixMarketDataDispatcherIF.MarketDataType_BookDepth_index]            != null;
            case CfixMarketDataDispatcherIF.MarketDataType_BookDepthUpdate:      return subscriptions[CfixMarketDataDispatcherIF.MarketDataType_BookDepthUpdate_index]      != null;
            case CfixMarketDataDispatcherIF.MarketDataType_Ticker:               return subscriptions[CfixMarketDataDispatcherIF.MarketDataType_Ticker_index]               != null;
            case CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice: return subscriptions[CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice_index] != null;
        }

        return false;
    }

    public String getMdReqID(int marketDataType)
    {
        switch (marketDataType)
        {
            case CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket:        return subscriptions[CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket_index];
            case CfixMarketDataDispatcherIF.MarketDataType_Nbbo:                 return subscriptions[CfixMarketDataDispatcherIF.MarketDataType_Nbbo_index];
            case CfixMarketDataDispatcherIF.MarketDataType_Recap:                return subscriptions[CfixMarketDataDispatcherIF.MarketDataType_Recap_index];
            case CfixMarketDataDispatcherIF.MarketDataType_BookDepth:            return subscriptions[CfixMarketDataDispatcherIF.MarketDataType_BookDepth_index];
            case CfixMarketDataDispatcherIF.MarketDataType_BookDepthUpdate:      return subscriptions[CfixMarketDataDispatcherIF.MarketDataType_BookDepthUpdate_index];
            case CfixMarketDataDispatcherIF.MarketDataType_Ticker:               return subscriptions[CfixMarketDataDispatcherIF.MarketDataType_Ticker_index];
            case CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice: return subscriptions[CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice_index];
        }

        return null;
    }

    public int size()
    {
        int size = 0;

        for (int i = 0; i < subscriptions.length; i++)
        {
            if (subscriptions[i] != null)
            {
                size++;
            }
        }

        return size;
    }

    public boolean isEmpty()
    {
        for (int i = 0; i < subscriptions.length; i++)
        {
            if (subscriptions[i] != null)
            {
                return false;
            }
        }

        return true;
    }

    public void clear()
    {
        for (int i = 0; i < subscriptions.length; i++)
        {
            subscriptions[i] = null;
        }
    }
}
