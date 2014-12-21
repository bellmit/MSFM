package com.cboe.cfix.util;

/**
 * OverlayPolicyNeverOverlayMarketDataCurrentMarketStructList.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 *
 * List of mdReqIDs/CurrentMarketStructs, with no overlay
 *
 */

import com.cboe.client.util.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.interfaces.cfix.*;

public class OverlayPolicyNeverOverlayMarketDataCurrentMarketStructList extends OverlayPolicyNeverOverlayMarketDataStructList implements OverlayPolicyMarketDataCurrentMarketStructListIF
{
    protected CurrentMarketStruct[] structs = CollectionHelper.EMPTY_CurrentMarketStruct_ARRAY;

    public OverlayPolicyNeverOverlayMarketDataCurrentMarketStructList(String mdReqID)
    {
        super(mdReqID);
    }

    private OverlayPolicyNeverOverlayMarketDataCurrentMarketStructList(String mdReqID, CurrentMarketStruct[] structs, int offset, int length)
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

    private OverlayPolicyNeverOverlayMarketDataCurrentMarketStructList(String mdReqID, CurrentMarketStruct struct)
    {
        super(mdReqID);

        this.structs = CollectionHelper.arrayclone(struct);
    }

    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, CurrentMarketStruct[] structs, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        cfixMarketDataConsumer.acceptMarketDataCurrentMarket(new OverlayPolicyNeverOverlayMarketDataCurrentMarketStructList(mdReqID, structs, offset, length));
    }

    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, CurrentMarketStruct struct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        cfixMarketDataConsumer.acceptMarketDataCurrentMarket(new OverlayPolicyNeverOverlayMarketDataCurrentMarketStructList(mdReqID, struct));
    }

    public void remove(OverlayPolicyMarketDataHolderIF cfixOverlayPolicyMarketDataHolder)
    {
        super.remove(cfixOverlayPolicyMarketDataHolder);

        cfixOverlayPolicyMarketDataHolder.setMarketDataType(CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket);

        cfixOverlayPolicyMarketDataHolder.setSize(structs.length);

        if (cfixOverlayPolicyMarketDataHolder.getCurrentMarketStructs().length < structs.length)
        {
            cfixOverlayPolicyMarketDataHolder.setCurrentMarketStructs(new CurrentMarketStruct[structs.length]);
        }

        System.arraycopy(this.structs, 0, cfixOverlayPolicyMarketDataHolder.getCurrentMarketStructs(), 0, structs.length);
    }

    public boolean equals(Object other)
    {
        return this == other &&
               other instanceof OverlayPolicyNeverOverlayMarketDataCurrentMarketStructList &&
               mdReqID.compareTo(((OverlayPolicyNeverOverlayMarketDataCurrentMarketStructList) other).mdReqID) == 0;
    }

    public int size()
    {
        return structs.length;
    }
}
