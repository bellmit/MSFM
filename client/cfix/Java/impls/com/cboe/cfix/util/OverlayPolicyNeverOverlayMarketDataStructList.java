package com.cboe.cfix.util;

import com.cboe.interfaces.cfix.*;

/**
 * OverlayPolicyNeverOverlayMarketDataStructList.java
 * @author Dmitry Volpyansky
 */
public abstract class OverlayPolicyNeverOverlayMarketDataStructList extends OverlayPolicyBaseMarketDataStructList
{
    public OverlayPolicyNeverOverlayMarketDataStructList(String mdReqID)
    {
        super(mdReqID);
    }

    public void clear()
    {

    }

    public int capacity()
    {
        return 1;
    }

    public void remove(OverlayPolicyMarketDataHolderIF cfixOverlayPolicyMarketDataHolder)
    {
        cfixOverlayPolicyMarketDataHolder.setMdReqID(mdReqID);
        cfixOverlayPolicyMarketDataHolder.setPolicyType(getPolicyType());
        cfixOverlayPolicyMarketDataHolder.setOverlaid(OverlayPolicyBaseMarketDataStructList.NeverOverlaidBitArray);
    }

    public int getPolicyType()
    {
        return OverlayPolicyMarketDataListIF.NEVER_OVERLAY_POLICY;
    }
}
