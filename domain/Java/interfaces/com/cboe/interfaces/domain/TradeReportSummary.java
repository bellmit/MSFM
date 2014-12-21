package com.cboe.interfaces.domain;

// ------------------------------------------------------------------------
//  Source file: TradeReportSummary.Java
//
// @author dowat
// ------------------------------------------------------------------------
//  Copyright (c) 2009 The Chicago Board Option
// ------------------------------------------------------------------------

import com.cboe.idl.trade.TradeReportSummaryStruct;

/**
 * This class defines a TradeReportSummary interface.
 *
 * @version 1.0
 * @author dowat
 */

public interface TradeReportSummary {
    
    public TradeReportEntrySummary[] getEntries();
    public long getTradeId();
    public int getProductKey();
    public int getClassKey();
    public String getSession();
    public long getTime();
    public TradeReportSummaryStruct toStruct();
    public TradeReportSummary getParentReport();
    public TradeReportSummary[] getChildren();
    public String getName();
    public boolean isActive();
    public boolean isReportForLocalTradeServer();
    public boolean hasParent();
}
