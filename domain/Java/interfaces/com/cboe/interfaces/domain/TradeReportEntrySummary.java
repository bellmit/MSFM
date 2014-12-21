package com.cboe.interfaces.domain;

// ------------------------------------------------------------------------
//  Source file: TradeReportEntrySummary.java
//
//  PACKAGE: com.cboe.interfaces.domain
//
//  @author dowat
// ------------------------------------------------------------------------
//  Copyright (c) 2009 The Chicago Board Options Exchange. All rights
//  reserved.
// ------------------------------------------------------------------------
public interface TradeReportEntrySummary
{
	public static final String TABLE_NAME = "sbt_tradereportentry";

    public boolean isReportActive();
    public void setReportActive(boolean reportActive);
    public TradeReportSummary getTradeReportForEntry();
    public String getBuyerUserKey();
    public void setBuyerUserKey(String buyUserKey);
    public String getSellerUserKey();
    public void setSellerUserKey(String sellUserKey);
}


