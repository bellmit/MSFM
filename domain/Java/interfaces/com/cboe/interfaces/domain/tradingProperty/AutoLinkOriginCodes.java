/**
 * 
 */
package com.cboe.interfaces.domain.tradingProperty;

/**
 * @author misbahud
 *
 */
public interface AutoLinkOriginCodes extends TradingProperty
{
    void setAutoLinkOriginCode(int originCode);
    int getAutoLinkOriginCode();
    void setAutoLinkOriginCodeEnabledFlag(int enabledFlag);
    int getAutoLinkOriginCodeEnabledFlag();
    char getDisplayAutoLinkOriginCode();
    void setDisplayAutoLinkOriginCode(char newAutoLinkOriginCode);
    boolean getDisplayAutoLinkOriginCodeEnabledFlag();
    void setDisplayAutoLinkOriginCodeEnabledFlag(boolean newAutoLinkOriginCodeEnabledFlag);
}