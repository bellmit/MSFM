package com.cboe.interfaces.domain.tradingProperty;

public interface AllowedSalOriginCodes extends TradingProperty
{
    void setAllowedSalOriginCode(int originCode);
    int getAllowedSalOriginCode();
    void setAllowedSalOriginCodeEnabledFlag(int enabledFlag);
    int getAllowedSalOriginCodeEnabledFlag();
    char getDisplayAllowedSalOriginCode();
    void setDisplayAllowedSalOriginCode(char allowedSalOriginCode);
    boolean getDisplayAllowedSalOriginCodeEnabledFlag();
    void setDisplayAllowedSalOriginCodeEnabledFlag(boolean allowedSalOriginCodeEnabledFlag);
}
