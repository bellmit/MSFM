package com.cboe.cfix.cas.marketData;

/**
 * CfixMDXMarketDataBookDepthUpdateDispatcherImpl.java
 *
 * @author Vivek Beniwal
 *
 */

/*
 *  This object registers for and receives all BookDepthUpdate market data traffic for a specific session, and then forwards it to all listeners<br>
 *
 *  It contains the subscription maps for all listeners interested in receiving BookDepthUpdate subscriptions for this session<br>
 *
 */

import com.cboe.interfaces.cfix.CfixMarketDataDispatcherIF;
import com.cboe.interfaces.cfix.CfixMDXMarketDataConsumerHolder;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiMarketData.BookDepthStruct;

public class CfixMDXMarketDataBookDepthUpdateDispatcherImpl extends CfixMDXMarketDataDispatcherImpl
{
    public static final String MARKET_DATA_TYPE_NAME = "BookDepthUpdate";

    public CfixMDXMarketDataBookDepthUpdateDispatcherImpl(String name, int classKey)
    {
        super(name, classKey);
    }

    public int getHandledMarketDataType()
    {
        return CfixMarketDataDispatcherIF.MarketDataType_BookDepthUpdate;
    }

    public String getHandledMarketDataTypeName()
    {
        return MARKET_DATA_TYPE_NAME;
    }

    protected void acceptMarketData(CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, Object struct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        cfixFixMarketDataConsumerHolder.acceptMarketDataBookDepthUpdate((BookDepthStruct) struct);
    }

    protected void acceptMarketData(CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, Object[] structs) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
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
