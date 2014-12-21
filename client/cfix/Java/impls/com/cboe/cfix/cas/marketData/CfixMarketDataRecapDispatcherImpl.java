package com.cboe.cfix.cas.marketData;

/**
 * CfixMarketDataRecapDispatcherImpl.java
 *
 * @author Dmitry Volpyansky
 *
 */

/*
 *  This object registers for and receives all Recap market data traffic for a specific session, and then forwards it to all listeners<br>
 *
 *  It contains the subscription maps for all listeners interested in receiving Recap subscriptions for this session<br>
 *
 */

import com.cboe.exceptions.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.interfaces.cfix.*;
import com.cboe.util.*;

public final class CfixMarketDataRecapDispatcherImpl extends CfixMarketDataDispatcherImpl
{
    public static final String MARKET_DATA_TYPE_NAME = "Recap";

    public CfixMarketDataRecapDispatcherImpl(String name)
    {
        super(name);
    }

    public int getHandledChannelType()
    {
        return ChannelType.RECAP_BY_CLASS;
    }

    public int getHandledMarketDataType()
    {
        return CfixMarketDataDispatcherIF.MarketDataType_Recap;
    }

    public String getHandledMarketDataTypeName()
    {
        return MARKET_DATA_TYPE_NAME;
    }

    protected void acceptMarketData(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, Object struct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        cfixFixMarketDataConsumerHolder.acceptMarketDataRecap((RecapStruct) struct);
    }

    protected void acceptMarketData(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, Object[] structs) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        cfixFixMarketDataConsumerHolder.acceptMarketDataRecap((RecapStruct[]) structs);
    }

    protected int getClassKey(Object struct)
    {
        return ((RecapStruct) struct).productKeys.classKey;
    }

    protected int getProductKey(Object struct)
    {
        return ((RecapStruct) struct).productKeys.productKey;
    }
}
