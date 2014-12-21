package com.cboe.cfix.cas.marketData;

/**
 * CfixMarketDataBookDepthUpdateDispatcherImpl.java
 *
 * @author Dmitry Volpyansky
 *
 */

/*
 *  This object registers for and receives all BookDepthUpdate market data traffic for a specific session, and then forwards it to all listeners<br>
 *
 *  It contains the subscription maps for all listeners interested in receiving BookDepthUpdate subscriptions for this session<br>
 *
 */

import com.cboe.exceptions.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.interfaces.cfix.*;
import com.cboe.util.*;

public final class CfixMarketDataBookDepthUpdateDispatcherImpl extends CfixMarketDataDispatcherImpl
{
    public static final String MARKET_DATA_TYPE_NAME = "BookDepthUpdate";

    public CfixMarketDataBookDepthUpdateDispatcherImpl(String name)
    {
        super(name);
    }

    public int getHandledChannelType()
    {
        return ChannelType.BOOK_DEPTH_BY_CLASS;
    }

    public int getHandledMarketDataType()
    {
        return CfixMarketDataDispatcherIF.MarketDataType_BookDepthUpdate;
    }

    public String getHandledMarketDataTypeName()
    {
        return MARKET_DATA_TYPE_NAME;
    }

    protected void acceptMarketData(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, Object struct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        cfixFixMarketDataConsumerHolder.acceptMarketDataBookDepthUpdate((BookDepthStruct) struct);
    }

    protected void acceptMarketData(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, Object[] structs) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        cfixFixMarketDataConsumerHolder.acceptMarketDataBookDepthUpdate((BookDepthStruct[]) structs);
    }

    protected int getClassKey(Object struct)
    {
        return ((BookDepthStruct) struct).productKeys.classKey;
    }

    protected int getProductKey(Object struct)
    {
        return ((BookDepthStruct) struct).productKeys.productKey;
    }
}
