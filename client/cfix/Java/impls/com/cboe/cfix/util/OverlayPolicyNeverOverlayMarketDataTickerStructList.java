package com.cboe.cfix.util;

/**
 * OverlayPolicyNeverOverlayMarketDataTickerStructList.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 *
 * List of mdReqIDs/TickerStructs, with no overlay
 *
 */

import com.cboe.client.util.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.interfaces.cfix.*;

public class OverlayPolicyNeverOverlayMarketDataTickerStructList extends OverlayPolicyNeverOverlayMarketDataStructList implements OverlayPolicyMarketDataTickerStructListIF
{
    protected TickerStruct[] structs = CollectionHelper.EMPTY_TickerStruct_ARRAY;

    public OverlayPolicyNeverOverlayMarketDataTickerStructList(String mdReqID)
    {
        super(mdReqID);
    }

    private OverlayPolicyNeverOverlayMarketDataTickerStructList(String mdReqID, TickerStruct[] structs, int offset, int length)
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

    private OverlayPolicyNeverOverlayMarketDataTickerStructList(String mdReqID, TickerStruct struct)
    {
        super(mdReqID);

        this.structs = CollectionHelper.arrayclone(struct);
    }

    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, TickerStruct[] structs, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        cfixMarketDataConsumer.acceptMarketDataTicker(new OverlayPolicyNeverOverlayMarketDataTickerStructList(mdReqID, structs, offset, length));
    }

    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, TickerStruct struct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        cfixMarketDataConsumer.acceptMarketDataTicker(new OverlayPolicyNeverOverlayMarketDataTickerStructList(mdReqID, struct));
    }

    public void remove(OverlayPolicyMarketDataHolderIF cfixOverlayPolicyMarketDataHolder)
    {
        super.remove(cfixOverlayPolicyMarketDataHolder);

        cfixOverlayPolicyMarketDataHolder.setMarketDataType(CfixMarketDataDispatcherIF.MarketDataType_Ticker);

        cfixOverlayPolicyMarketDataHolder.setSize(structs.length);

        if (cfixOverlayPolicyMarketDataHolder.getTickerStructs().length < structs.length)
        {
            cfixOverlayPolicyMarketDataHolder.setTickerStructs(new TickerStruct[structs.length]);
        }

        System.arraycopy(this.structs, 0, cfixOverlayPolicyMarketDataHolder.getTickerStructs(), 0, structs.length);
    }

    public boolean equals(Object other)
    {
        return this == other &&
               other instanceof OverlayPolicyNeverOverlayMarketDataTickerStructList &&
               mdReqID.compareTo(((OverlayPolicyNeverOverlayMarketDataTickerStructList) other).mdReqID) == 0;
    }

    public int size()
    {
        return structs.length;
    }
}
