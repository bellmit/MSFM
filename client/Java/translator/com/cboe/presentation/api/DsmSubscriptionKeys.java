//
// -----------------------------------------------------------------------------------
// Source file: DsmSubscriptionKeys.java
//
// PACKAGE: com.cboe.presentation.api;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2010 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;


/**
 * This class contains the session name and class key of the legs of a strategy. At most one of the legs can be an
 * underlying. Up to 3 other legs, must all be one fo these: options, futures, or indices. While underlying can be from
 * a different session, the other leg(s) and the strategy are from the same session
 *
 * @author  Shawn Khosravani
 */
public class DsmSubscriptionKeys
{
    private static final int INVALID_KEY = -1;

    private int    strategyClassKey;            // for debugging. dont really need to store
    private int    underlyingClassKey;
    private int    nonUnderlyingLegClassKey;    // the leg(s) other than underlying. may be option, future, or index
    private String underlyingSessionName;
    private String sessionName;

    public DsmSubscriptionKeys(String strSessionName, int strClassKey)
    {
        strategyClassKey         = strClassKey;
        underlyingClassKey       = INVALID_KEY;
        nonUnderlyingLegClassKey = INVALID_KEY;
        underlyingSessionName    = null;
        sessionName              = strSessionName;
    }

    // no setter. set only in constructor.
    public int getStrategyClassKey()
    {
        return strategyClassKey;
    }

    public int getNonUnderlyingClassKey()
    {
        return nonUnderlyingLegClassKey;
    }

    public void setNonUnderlyingClassKey(int key)
    {
        nonUnderlyingLegClassKey = key;
    }

    public int getUnderlyingClassKey()
    {
        return underlyingClassKey;
    }

    public void setUnderlyingClassKey(int key)
    {
        underlyingClassKey = key;
    }

    // no setter. set only in constructor.
    public String getSessionName()
    {
        return sessionName;
    }

    public String getUnderlyingSessionName()
    {
        return underlyingSessionName;
    }

    public void setUnderlyingSessionName(String name)
    {
        underlyingSessionName = name;
    }
}
