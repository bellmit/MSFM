package com.cboe.cfix.cas.marketData;

/**
 * CfixMarketDataNbboDispatcherImpl.java
 *
 * @author Dmitry Volpyansky
 *
 */

/*
 *  This object registers for and receives all Nbbo market data traffic for a specific session, and then forwards it to all listeners<br>
 *
 *  It contains the subscription maps for all listeners interested in receiving Nbbo subscriptions for this session<br>
 *
 */

import com.cboe.exceptions.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.interfaces.cfix.*;
import com.cboe.util.*;

public final class CfixMarketDataNbboDispatcherImpl extends CfixMarketDataDispatcherImpl
{
    public static final String MARKET_DATA_TYPE_NAME = "Nbbo";

    public CfixMarketDataNbboDispatcherImpl(String name)
    {
        super(name);
    }

    public int getHandledChannelType()
    {
        return ChannelType.NBBO_BY_CLASS;
    }

    public int getHandledMarketDataType()
    {
        return CfixMarketDataDispatcherIF.MarketDataType_Nbbo;
    }

    public String getHandledMarketDataTypeName()
    {
        return MARKET_DATA_TYPE_NAME;
    }

    protected void acceptMarketData(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, Object struct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        cfixFixMarketDataConsumerHolder.acceptMarketDataNbbo((NBBOStruct) struct);
    }

    protected void acceptMarketData(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, Object[] structs) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        cfixFixMarketDataConsumerHolder.acceptMarketDataNbbo((NBBOStruct[]) structs);
    }

    protected int getClassKey(Object struct)
    {
        return ((NBBOStruct) struct).productKeys.classKey;
    }

    protected int getProductKey(Object struct)
    {
        return ((NBBOStruct) struct).productKeys.productKey;
    }
}
