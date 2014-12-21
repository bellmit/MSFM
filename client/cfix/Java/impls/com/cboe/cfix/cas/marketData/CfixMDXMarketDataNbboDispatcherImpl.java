package com.cboe.cfix.cas.marketData;

/**
 * CfixMDXMarketDataNbboDispatcherImpl.java
 *
 * @author Vivek Beniwal
 *
 */

/*
 *  This object registers for and receives all Nbbo market data traffic for a specific session, and then forwards it to all listeners<br>
 *
 *  It contains the subscription maps for all listeners interested in receiving Nbbo subscriptions for this session<br>
 *
 */

import com.cboe.interfaces.cfix.CfixMarketDataDispatcherIF;
import com.cboe.interfaces.cfix.CfixMDXMarketDataConsumerHolder;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiMarketData.NBBOStruct;

public final class CfixMDXMarketDataNbboDispatcherImpl extends CfixMDXMarketDataDispatcherImpl
{
    public static final String MARKET_DATA_TYPE_NAME = "Nbbo";

    public CfixMDXMarketDataNbboDispatcherImpl(String name, int classKey)
    {
        super(name, classKey);
    }

    public int getHandledMarketDataType()
    {
        return CfixMarketDataDispatcherIF.MarketDataType_Nbbo;
    }

    public String getHandledMarketDataTypeName()
    {
        return MARKET_DATA_TYPE_NAME;
    }

    protected void acceptMarketData(CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, Object struct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        cfixFixMarketDataConsumerHolder.acceptMarketDataNbbo((NBBOStruct) struct);
    }

    protected void acceptMarketData(CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, Object[] structs) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
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
