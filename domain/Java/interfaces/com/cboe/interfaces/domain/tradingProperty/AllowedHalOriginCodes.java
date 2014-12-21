//
// -----------------------------------------------------------------------------------
// Source file: NewHalOriginCode.java
//
// PACKAGE: com.cboe.interfaces.domain.tradingProperty;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.tradingProperty;

public interface AllowedHalOriginCodes extends TradingProperty
{
    void setAllowedHalOriginCode(int originCode);
    int getAllowedHalOriginCode();
    void setAllowedHalOriginCodeEnabledFlag(int enabledFlag);
    int getAllowedHalOriginCodeEnabledFlag();
    char getDisplayAllowedHalOriginCode();
    void setDisplayAllowedHalOriginCode(char allowedHalOriginCode);
    boolean getDisplayAllowedHalOriginCodeEnabledFlag();
    void setDisplayAllowedHalOriginCodeEnabledFlag(boolean allowedHalOriginCodeEnabledFlag);
}