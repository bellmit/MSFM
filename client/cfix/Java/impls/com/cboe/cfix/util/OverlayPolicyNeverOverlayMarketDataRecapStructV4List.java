package com.cboe.cfix.util;

/**
 * OverlayPolicyNeverOverlayMarketDataRecapStructList.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 *
 * List of mdReqIDs/RecapStructs, with no overlay
 *
 */

import com.cboe.client.util.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiMarketData.RecapStructV4;
import com.cboe.interfaces.cfix.*;

public class OverlayPolicyNeverOverlayMarketDataRecapStructV4List extends OverlayPolicyNeverOverlayMarketDataStructList implements OverlayPolicyMarketDataRecapStructV4ListIF
{
    protected RecapStructV4[] structs = CollectionHelper.EMPTY_RecapStructV4_ARRAY;

    public OverlayPolicyNeverOverlayMarketDataRecapStructV4List(String mdReqID)
    {
        super(mdReqID);
    }

    private OverlayPolicyNeverOverlayMarketDataRecapStructV4List(String mdReqID, RecapStructV4[] structs, int offset, int length)
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

    private OverlayPolicyNeverOverlayMarketDataRecapStructV4List(String mdReqID, RecapStructV4 struct)
    {
        super(mdReqID);

        this.structs = CollectionHelper.arrayclone(struct);
    }

    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, RecapStructV4[] structs, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        cfixMarketDataConsumer.acceptMarketDataRecap(new OverlayPolicyNeverOverlayMarketDataRecapStructV4List(mdReqID, structs, offset, length));
    }

    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, RecapStructV4 struct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        cfixMarketDataConsumer.acceptMarketDataRecap(new OverlayPolicyNeverOverlayMarketDataRecapStructV4List(mdReqID, struct));
    }

    public void remove(OverlayPolicyMarketDataHolderIF cfixOverlayPolicyMarketDataHolder)
    {
        super.remove(cfixOverlayPolicyMarketDataHolder);

        cfixOverlayPolicyMarketDataHolder.setMarketDataType(CfixMarketDataDispatcherIF.MarketDataType_Recap);

        cfixOverlayPolicyMarketDataHolder.setSize(structs.length);

        if (cfixOverlayPolicyMarketDataHolder.getRecapStructsV4().length < structs.length)
        {
            cfixOverlayPolicyMarketDataHolder.setRecapStructsV4(new RecapStructV4[structs.length]);
        }

        System.arraycopy(this.structs, 0, cfixOverlayPolicyMarketDataHolder.getRecapStructsV4(), 0, structs.length);
    }

    public boolean equals(Object other)
    {
        return this == other &&
               other instanceof OverlayPolicyNeverOverlayMarketDataRecapStructV4List &&
               mdReqID.compareTo(((OverlayPolicyNeverOverlayMarketDataRecapStructV4List) other).mdReqID) == 0;
    }

    public int size()
    {
        return structs.length;
    }
}
