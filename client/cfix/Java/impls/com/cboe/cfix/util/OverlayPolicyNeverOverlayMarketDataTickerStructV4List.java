package com.cboe.cfix.util;

/**
 * OverlayPolicyNeverOverlayMarketDataTickerStructV4List.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 *
 * List of mdReqIDs/TickerStructV4s, with no overlay
 *
 */

import com.cboe.client.util.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiMarketData.TickerStructV4;
import com.cboe.interfaces.cfix.*;

public class OverlayPolicyNeverOverlayMarketDataTickerStructV4List extends OverlayPolicyNeverOverlayMarketDataStructList implements OverlayPolicyMarketDataTickerStructV4ListIF
{
    protected TickerStructV4[] structs = CollectionHelper.EMPTY_TickerStructV4_ARRAY;

    public OverlayPolicyNeverOverlayMarketDataTickerStructV4List(String mdReqID)
    {
        super(mdReqID);
    }

    private OverlayPolicyNeverOverlayMarketDataTickerStructV4List(String mdReqID, TickerStructV4[] structs, int offset, int length)
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

    private OverlayPolicyNeverOverlayMarketDataTickerStructV4List(String mdReqID, TickerStructV4 struct)
    {
        super(mdReqID);

        this.structs = CollectionHelper.arrayclone(struct);
    }

    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, TickerStructV4[] structs, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        cfixMarketDataConsumer.acceptMarketDataTicker(new OverlayPolicyNeverOverlayMarketDataTickerStructV4List(mdReqID, structs, offset, length));
    }

    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, TickerStructV4 struct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        cfixMarketDataConsumer.acceptMarketDataTicker(new OverlayPolicyNeverOverlayMarketDataTickerStructV4List(mdReqID, struct));
    }

    public void remove(OverlayPolicyMarketDataHolderIF cfixOverlayPolicyMarketDataHolder)
    {
        super.remove(cfixOverlayPolicyMarketDataHolder);

        cfixOverlayPolicyMarketDataHolder.setMarketDataType(CfixMarketDataDispatcherIF.MarketDataType_Ticker);

        cfixOverlayPolicyMarketDataHolder.setSize(structs.length);

        if (cfixOverlayPolicyMarketDataHolder.getTickerStructsV4().length < structs.length)
        {
            cfixOverlayPolicyMarketDataHolder.setTickerStructsV4(new TickerStructV4[structs.length]);
        }

        System.arraycopy(this.structs, 0, cfixOverlayPolicyMarketDataHolder.getTickerStructsV4(), 0, structs.length);
    }

    public boolean equals(Object other)
    {
        return this == other &&
               other instanceof OverlayPolicyNeverOverlayMarketDataTickerStructV4List &&
               mdReqID.compareTo(((OverlayPolicyNeverOverlayMarketDataTickerStructV4List) other).mdReqID) == 0;
    }

    public int size()
    {
        return structs.length;
    }
}
