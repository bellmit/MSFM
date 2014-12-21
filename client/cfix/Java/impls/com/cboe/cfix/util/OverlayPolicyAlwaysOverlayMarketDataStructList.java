package com.cboe.cfix.util;

/**
 * OverlayPolicyAlwaysOverlayMarketDataStructList.java
 *
 * @author Dmitry Volpyansky
 *
 */

import com.cboe.client.util.*;
import com.cboe.interfaces.cfix.*;

public abstract class OverlayPolicyAlwaysOverlayMarketDataStructList extends OverlayPolicyBaseMarketDataStructList
{
    protected BitArrayIF     overlaid = new CountingBitArray();
    protected int            size;

    public OverlayPolicyAlwaysOverlayMarketDataStructList(String mdReqID)
    {
        super(mdReqID);
    }

    public void remove(OverlayPolicyMarketDataHolderIF cfixOverlayPolicyMarketDataHolder, int marketDataType)
    {
        cfixOverlayPolicyMarketDataHolder.setMdReqID(mdReqID);
        cfixOverlayPolicyMarketDataHolder.setPolicyType(getPolicyType());
        cfixOverlayPolicyMarketDataHolder.setMarketDataType(marketDataType);
    }

    public int getPolicyType()
    {
        return OverlayPolicyMarketDataListIF.ALWAYS_OVERLAY_POLICY;
    }

    public int size()
    {
        return size;
    }

    public abstract void clear();
    public abstract int capacity();
}
