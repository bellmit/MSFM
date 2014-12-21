//
// -----------------------------------------------------------------------------------
// Source file: AllowedWtpOriginCodes.java
//
// PACKAGE: com.cboe.interfaces.domain.tradingProperty;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.tradingProperty;

public interface AllowedWtpOriginCodes extends TradingProperty
{
    void setAllowedWtpOriginCode(int originCode);
    int getAllowedWtpOriginCode();
    void setAllowedWtpOriginCodeEnabledFlag(int enabledFlag);
    int getAllowedWtpOriginCodeEnabledFlag();
    char getDisplayAllowedWtpOriginCode();
    void setDisplayAllowedWtpOriginCode(char allowedWtpOriginCode);
    boolean getDisplayAllowedWtpOriginCodeEnabledFlag();
    void setDisplayAllowedWtpOriginCodeEnabledFlag(boolean allowedWtpOriginCodeEnabledFlag);
}