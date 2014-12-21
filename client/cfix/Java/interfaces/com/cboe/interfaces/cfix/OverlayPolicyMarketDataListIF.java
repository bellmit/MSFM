package com.cboe.interfaces.cfix;

/**
 * OverlayPolicyMarketDataListIF.java
 *
 * @author Dmitry Volpyansky
 *
 */

import com.cboe.client.util.*;

public interface OverlayPolicyMarketDataListIF extends HasSizeIF
{
    public static final int NEVER_OVERLAY_POLICY         = 2;  public static final String strNEVER_OVERLAY_POLICY         = "NeverOverlay";
    public static final int ALWAYS_OVERLAY_POLICY        = 4;  public static final String strALWAYS_OVERLAY_POLICY        = "AlwaysOverlay";

    public String getMdReqID();
    public void   remove(OverlayPolicyMarketDataHolderIF cfixOverlayPolicyMarketDataHolder);
    public int    size();
    public int    capacity();
    public int    getPolicyType();
}
