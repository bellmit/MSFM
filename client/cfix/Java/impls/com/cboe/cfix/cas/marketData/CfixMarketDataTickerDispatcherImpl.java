package com.cboe.cfix.cas.marketData;

/**
 * CfixMarketDataTickerDispatcherImpl.java
 *
 * @author Dmitry Volpyansky
 *
 */

/*
 *  This object registers for and receives all Ticker market data traffic for a specific session, and then forwards it to all listeners<br>
 *
 *  It contains the subscription maps for all listeners interested in receiving Ticker subscriptions for this session<br>
 *
 */

import com.cboe.exceptions.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.interfaces.cfix.*;
import com.cboe.util.*;

public final class CfixMarketDataTickerDispatcherImpl extends CfixMarketDataDispatcherImpl
{
    public static final String MARKET_DATA_TYPE_NAME = "Ticker";

    public CfixMarketDataTickerDispatcherImpl(String name)
    {
        super(name);
    }

    public int getHandledChannelType()
    {
        return ChannelType.TICKER_BY_CLASS;
    }

    public int getHandledMarketDataType()
    {
        return CfixMarketDataDispatcherIF.MarketDataType_Ticker;
    }

    public String getHandledMarketDataTypeName()
    {
        return MARKET_DATA_TYPE_NAME;
    }

    protected void acceptMarketData(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, Object struct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        cfixFixMarketDataConsumerHolder.acceptMarketDataTicker((TickerStruct) struct);
    }

    protected void acceptMarketData(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, Object[] structs) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        cfixFixMarketDataConsumerHolder.acceptMarketDataTicker((TickerStruct[]) structs);
    }

    protected int getClassKey(Object struct)
    {
        return ((TickerStruct) struct).productKeys.classKey;
    }

    protected int getProductKey(Object struct)
    {
        return ((TickerStruct) struct).productKeys.productKey;
    }
}
