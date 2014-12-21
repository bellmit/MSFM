package com.cboe.cfix.cas.marketData;

/**
 * CfixMarketDataExpectedOpeningPriceDispatcherImpl.java
 *
 * @author Dmitry Volpyansky
 *
 */

/*
 *  This object registers for and receives all ExpectedOpeningPrice market data traffic for a specific session, and then forwards it to all listeners<br>
 *
 *  It contains the subscription maps for all listeners interested in receiving ExpectedOpeningPrice subscriptions for this session<br>
 *
 */

import com.cboe.exceptions.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.interfaces.cfix.*;
import com.cboe.util.*;

public final class CfixMarketDataExpectedOpeningPriceDispatcherImpl extends CfixMarketDataDispatcherImpl
{
    public static final String MARKET_DATA_TYPE_NAME = "ExpectedOpeningPrice";

    public CfixMarketDataExpectedOpeningPriceDispatcherImpl(String name)
    {
        super(name);
    }

    public int getHandledChannelType()
    {
        return ChannelType.OPENING_PRICE_BY_CLASS;
    }

    public int getHandledMarketDataType()
    {
        return CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice;
    }

    public String getHandledMarketDataTypeName()
    {
        return MARKET_DATA_TYPE_NAME;
    }

    protected void acceptMarketData(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, Object struct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        cfixFixMarketDataConsumerHolder.acceptMarketDataExpectedOpeningPrice((ExpectedOpeningPriceStruct) struct);
    }

    protected void acceptMarketData(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, Object[] structs) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        cfixFixMarketDataConsumerHolder.acceptMarketDataExpectedOpeningPrice((ExpectedOpeningPriceStruct[]) structs);
    }

    protected int getClassKey(Object struct)
    {
        return ((ExpectedOpeningPriceStruct) struct).productKeys.classKey;
    }

    protected int getProductKey(Object struct)
    {
        return ((ExpectedOpeningPriceStruct) struct).productKeys.productKey;
    }
}
