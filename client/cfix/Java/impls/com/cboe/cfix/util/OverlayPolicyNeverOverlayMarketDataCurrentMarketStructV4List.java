package com.cboe.cfix.util;

/**
 * OverlayPolicyNeverOverlayMarketDataCurrentMarketStructV4List.java
 *
 * @author Dmitry Volpyansky / Vivek Beniwal
 *
 */

/**
 *
 * List of mdReqIDs/CurrentMarketStructs, with no overlay
 *
 */

import com.cboe.client.util.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiMarketData.CurrentMarketStructV4;
import com.cboe.interfaces.cfix.*;

public class OverlayPolicyNeverOverlayMarketDataCurrentMarketStructV4List extends OverlayPolicyNeverOverlayMarketDataStructList implements OverlayPolicyMarketDataCurrentMarketStructV4ListIF
{
    protected CurrentMarketStructV4[] structs = CollectionHelper.EMPTY_CurrentMarketStructV4_ARRAY;

    public OverlayPolicyNeverOverlayMarketDataCurrentMarketStructV4List(String mdReqID)
    {
        super(mdReqID);
    }

    private OverlayPolicyNeverOverlayMarketDataCurrentMarketStructV4List(String mdReqID, CurrentMarketStructV4[] structs, int offset, int length)
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

    private OverlayPolicyNeverOverlayMarketDataCurrentMarketStructV4List(String mdReqID, CurrentMarketStructV4 struct)
    {
        super(mdReqID);

        this.structs = CollectionHelper.arrayclone(struct);
    }

    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, CurrentMarketStructV4[] structs, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        cfixMarketDataConsumer.acceptMarketDataCurrentMarket(new OverlayPolicyNeverOverlayMarketDataCurrentMarketStructV4List(mdReqID, structs, offset, length));
    }

    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, CurrentMarketStructV4 struct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        cfixMarketDataConsumer.acceptMarketDataCurrentMarket(new OverlayPolicyNeverOverlayMarketDataCurrentMarketStructV4List(mdReqID, struct));
    }

    public void remove(OverlayPolicyMarketDataHolderIF cfixOverlayPolicyMarketDataHolder)
    {
        super.remove(cfixOverlayPolicyMarketDataHolder);

        cfixOverlayPolicyMarketDataHolder.setMarketDataType(CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket);

        cfixOverlayPolicyMarketDataHolder.setSize(structs.length);

        if (cfixOverlayPolicyMarketDataHolder.getCurrentMarketStructsV4().length < structs.length)
        {
            cfixOverlayPolicyMarketDataHolder.setCurrentMarketStructsV4(new CurrentMarketStructV4[structs.length]);
        }

        System.arraycopy(this.structs, 0, cfixOverlayPolicyMarketDataHolder.getCurrentMarketStructsV4(), 0, structs.length);
    }

    public boolean equals(Object other)
    {
        return this == other &&
               other instanceof OverlayPolicyNeverOverlayMarketDataCurrentMarketStructV4List &&
               mdReqID.compareTo(((OverlayPolicyNeverOverlayMarketDataCurrentMarketStructV4List) other).mdReqID) == 0;
    }

    public int size()
    {
        return structs.length;
    }
}
