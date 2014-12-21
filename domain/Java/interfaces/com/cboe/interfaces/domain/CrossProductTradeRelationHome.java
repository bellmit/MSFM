package com.cboe.interfaces.domain;

import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmiOrder.FilledReportStruct;
import com.cboe.idl.trade.TradeReportStructV2;

/**
 * Home for CrossProductTradeRelation objects
 * 
 * @author thakkar
 *
 */
public interface CrossProductTradeRelationHome
{
    public final static String HOME_NAME = "CrossProductTradeRelationHome"; 
    public CrossProductTradeRelation createRelation(long localTradeReportId, 
            long remoteTradeReportId, long remoteProductKey, long remoteTradeTime, 
            int remoteTradeQuantity, String remoteSessionName);
    public void addRemoteLegTradeRelation(TradeReport strategyTrade, FilledReportStruct remoteLegFill);
    public void addRemoteLegTradeRelation(TradeReport strategyTrade, TradeReportStructV2 remoteLegTrade);
    public CrossProductTradeRelation[] findRelations(long localTradeReportId) throws TransactionFailedException;
}
