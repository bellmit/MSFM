package com.cboe.cfix.util;

/**
 * OverlayPolicyNeverOverlayMarketDataExpectedOpeningPriceStructList.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 *
 * List of mdReqIDs/ExpectedOpeningPriceStructs, with no overlay
 *
 */

import com.cboe.client.util.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.interfaces.cfix.*;

public class OverlayPolicyNeverOverlayMarketDataExpectedOpeningPriceStructList extends OverlayPolicyNeverOverlayMarketDataStructList implements OverlayPolicyMarketDataExpectedOpeningPriceStructListIF
{
    protected ExpectedOpeningPriceStruct[] structs = CollectionHelper.EMPTY_ExpectedOpeningPriceStruct_ARRAY;

    public OverlayPolicyNeverOverlayMarketDataExpectedOpeningPriceStructList(String mdReqID)
    {
        super(mdReqID);
    }

    private OverlayPolicyNeverOverlayMarketDataExpectedOpeningPriceStructList(String mdReqID, ExpectedOpeningPriceStruct[] structs, int offset, int length)
    {
        super(mdReqID);

        if (structs.length != length)
        {
            this.structs = CollectionHelper.arrayclone(structs, offset, length, length);
        }
        else
        {
            this.structs = structs;
        }
    }

    private OverlayPolicyNeverOverlayMarketDataExpectedOpeningPriceStructList(String mdReqID, ExpectedOpeningPriceStruct struct)
    {
        super(mdReqID);

        this.structs = CollectionHelper.arrayclone(struct);
    }

    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, ExpectedOpeningPriceStruct[] structs, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        cfixMarketDataConsumer.acceptMarketDataExpectedOpeningPrice(new OverlayPolicyNeverOverlayMarketDataExpectedOpeningPriceStructList(mdReqID, structs, offset, length));
    }

    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, ExpectedOpeningPriceStruct struct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        cfixMarketDataConsumer.acceptMarketDataExpectedOpeningPrice(new OverlayPolicyNeverOverlayMarketDataExpectedOpeningPriceStructList(mdReqID, struct));
    }

    public void remove(OverlayPolicyMarketDataHolderIF cfixOverlayPolicyMarketDataHolder)
    {
        super.remove(cfixOverlayPolicyMarketDataHolder);

        cfixOverlayPolicyMarketDataHolder.setMarketDataType(CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice);

        cfixOverlayPolicyMarketDataHolder.setSize(structs.length);

        if (cfixOverlayPolicyMarketDataHolder.getExpectedOpeningPriceStructs().length < structs.length)
        {
            cfixOverlayPolicyMarketDataHolder.setExpectedOpeningPriceStructs(new ExpectedOpeningPriceStruct[structs.length]);
        }

        System.arraycopy(this.structs, 0, cfixOverlayPolicyMarketDataHolder.getExpectedOpeningPriceStructs(), 0, structs.length);
    }

    public boolean equals(Object other)
    {
        return this == other &&
               other instanceof OverlayPolicyNeverOverlayMarketDataExpectedOpeningPriceStructList &&
               mdReqID.compareTo(((OverlayPolicyNeverOverlayMarketDataExpectedOpeningPriceStructList) other).mdReqID) == 0;
    }

    public int size()
    {
        return structs.length;
    }
}
