/**
 * 
 */
package com.cboe.interfaces.domain.tradingProperty;

public interface RolloutFlagByBC extends TradingProperty
{
    public int getBCId();
    public void setBCId(int rolloutBCId);
    public String getBC();
    public void setBC(String rolloutBC);
    public int getRolloutFlag();
    public void setRolloutFlag(int rolloutFlag);
    public boolean isRollout();
    public void setRollout(boolean rolloutFlag);
    public boolean isAllBC();
    public String getGroupName();
}
