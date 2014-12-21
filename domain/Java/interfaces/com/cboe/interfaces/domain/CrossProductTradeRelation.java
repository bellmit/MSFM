package com.cboe.interfaces.domain;

import com.cboe.idl.trade.TradeReportSummaryStruct;
 
/**
 * The relationship between strategy trade report of a cross-product
 * strategy and strategy legs that trade external to the local
 * trade server. Example: Buy-Write strategy products have one 
 * equity leg that trades in W_STOCK while the strategy itself is
 * defined and trades in W_MAIN. The strategy report and the option leg
 * reports are recorded in W_MAIN schema while the equity leg report
 * is recorded in W_STOCK schema. This interface and its implementation
 * define association between these two trade reports.
 * 
 * @author thakkar
 *
 */
public interface CrossProductTradeRelation
{
    public void setLocalTradeReportId(long p_localTradeReportId);
    public void setRemoteTradeReportId(long p_remoteTradeReportId);
    public void setRemoteProductKey(long p_remoteProductKey);
    public void setRemoteSessionName(String p_remoteSessionName);
    public void setRemoteTradeTime(long p_remoteTradeTime);
    public void setRemoteTradeQuantity(int p_remoteTradeQuantity);

    public long getLocalTradeReportId();
    public long getRemoteTradeReportId();
    public long getRemoteProductKey();
    public String getRemoteSessionName();
    public long getRemoteTradeTime();
    public int getRemoteTradeQuantity();  
    public TradeReportSummaryStruct getRemoteTradeSummary();  
    public TradeReportSummary getRemoteSummary();
}
