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
import com.cboe.idl.cmiMarketData.*;
import com.cboe.interfaces.cfix.*;

public class OverlayPolicyNeverOverlayMarketDataRecapStructList extends OverlayPolicyNeverOverlayMarketDataStructList implements OverlayPolicyMarketDataRecapStructListIF
{
    protected RecapStruct[] structs = CollectionHelper.EMPTY_RecapStruct_ARRAY;

    public OverlayPolicyNeverOverlayMarketDataRecapStructList(String mdReqID)
    {
        super(mdReqID);
    }

    private OverlayPolicyNeverOverlayMarketDataRecapStructList(String mdReqID, RecapStruct[] structs, int offset, int length)
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

    private OverlayPolicyNeverOverlayMarketDataRecapStructList(String mdReqID, RecapStruct struct)
    {
        super(mdReqID);

        this.structs = CollectionHelper.arrayclone(struct);
    }

    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, RecapStruct[] structs, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        cfixMarketDataConsumer.acceptMarketDataRecap(new OverlayPolicyNeverOverlayMarketDataRecapStructList(mdReqID, structs, offset, length));
    }

    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, RecapStruct struct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        cfixMarketDataConsumer.acceptMarketDataRecap(new OverlayPolicyNeverOverlayMarketDataRecapStructList(mdReqID, struct));
    }

    public void remove(OverlayPolicyMarketDataHolderIF cfixOverlayPolicyMarketDataHolder)
    {
        super.remove(cfixOverlayPolicyMarketDataHolder);

        cfixOverlayPolicyMarketDataHolder.setMarketDataType(CfixMarketDataDispatcherIF.MarketDataType_Recap);

        cfixOverlayPolicyMarketDataHolder.setSize(structs.length);

        if (cfixOverlayPolicyMarketDataHolder.getRecapStructs().length < structs.length)
        {
            cfixOverlayPolicyMarketDataHolder.setRecapStructs(new RecapStruct[structs.length]);
        }

        System.arraycopy(this.structs, 0, cfixOverlayPolicyMarketDataHolder.getRecapStructs(), 0, structs.length);
    }

    public boolean equals(Object other)
    {
        return this == other &&
               other instanceof OverlayPolicyNeverOverlayMarketDataRecapStructList &&
               mdReqID.compareTo(((OverlayPolicyNeverOverlayMarketDataRecapStructList) other).mdReqID) == 0;
    }

    public int size()
    {
        return structs.length;
    }
}
