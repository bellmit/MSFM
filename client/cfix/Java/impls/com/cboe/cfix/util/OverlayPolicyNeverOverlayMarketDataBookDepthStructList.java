package com.cboe.cfix.util;

/**
 * OverlayPolicyNeverOverlayMarketDataBookDepthStructList.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 *
 * List of mdReqIDs/BookDepthStructs, with no overlay
 *
 */

import com.cboe.client.util.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.interfaces.cfix.*;

public class OverlayPolicyNeverOverlayMarketDataBookDepthStructList extends OverlayPolicyNeverOverlayMarketDataStructList implements OverlayPolicyMarketDataBookDepthStructListIF
{
    protected BookDepthStruct[] structs = CollectionHelper.EMPTY_BookDepthStruct_ARRAY;

    public OverlayPolicyNeverOverlayMarketDataBookDepthStructList(String mdReqID)
    {
        super(mdReqID);
    }

    private OverlayPolicyNeverOverlayMarketDataBookDepthStructList(String mdReqID, BookDepthStruct[] structs, int offset, int length)
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

    private OverlayPolicyNeverOverlayMarketDataBookDepthStructList(String mdReqID, BookDepthStruct struct)
    {
        super(mdReqID);

        this.structs = CollectionHelper.arrayclone(struct);
    }

    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, BookDepthStruct[] structs, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        cfixMarketDataConsumer.acceptMarketDataBookDepth(new OverlayPolicyNeverOverlayMarketDataBookDepthStructList(mdReqID, structs, offset, length));
    }

    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, BookDepthStruct struct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        cfixMarketDataConsumer.acceptMarketDataBookDepth(new OverlayPolicyNeverOverlayMarketDataBookDepthStructList(mdReqID, struct));
    }

    public void remove(OverlayPolicyMarketDataHolderIF cfixOverlayPolicyMarketDataHolder)
    {
        super.remove(cfixOverlayPolicyMarketDataHolder);

        cfixOverlayPolicyMarketDataHolder.setMarketDataType(CfixMarketDataDispatcherIF.MarketDataType_BookDepth);

        cfixOverlayPolicyMarketDataHolder.setSize(structs.length);

        if (cfixOverlayPolicyMarketDataHolder.getBookDepthStructs().length < structs.length)
        {
            cfixOverlayPolicyMarketDataHolder.setBookDepthStructs(new BookDepthStruct[structs.length]);
        }

        System.arraycopy(this.structs, 0, cfixOverlayPolicyMarketDataHolder.getBookDepthStructs(), 0, structs.length);
    }

    public boolean equals(Object other)
    {
        return this == other &&
               other instanceof OverlayPolicyNeverOverlayMarketDataBookDepthStructList &&
               mdReqID.compareTo(((OverlayPolicyNeverOverlayMarketDataBookDepthStructList) other).mdReqID) == 0;
    }

    public int size()
    {
        return structs.length;
    }
}
