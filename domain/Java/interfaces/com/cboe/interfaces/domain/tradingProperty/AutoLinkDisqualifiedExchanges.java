/**
 * 
 */
package com.cboe.interfaces.domain.tradingProperty;

/**
 * @author misbahud
 *
 */
public interface AutoLinkDisqualifiedExchanges extends TradingProperty
{
    public int getAutoLinkDisqualifiedExchangesId();
    public void setAutoLinkDisqualifiedExchangesId(int newAutoLinkDisqualifiedExchanges);
    public String getAutoLinkDisqualifiedExchanges();
    public void setAutoLinkDisqualifiedExchanges(String newAutoLinkDisqualifiedExchanges);
    public int getAutoLinkDisqualifiedExchangesFlag();
    public void setAutoLinkDisqualifiedExchangesFlag(int newAutoLinkDisqualifiedExchangesFlag);
    public boolean isAutoLinkDisqualifiedExchangesFlag();
    public void setAutoLinkDisqualifiedExchangesFlag(boolean newAutoLinkDisqualifiedExchangesFlag);
}
