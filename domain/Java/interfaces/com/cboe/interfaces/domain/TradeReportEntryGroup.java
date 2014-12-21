package com.cboe.interfaces.domain;

import com.cboe.idl.trade.AtomicTradeStruct;
import com.cboe.idl.trade.TradeReportBillingStruct;

public interface TradeReportEntryGroup
{
    TradeReportEntry[] getTradeReportEntries();
    AtomicTradeStruct[] getAtomicTradeStructsForGroup();
    TradeReport getTradeReport();
    long getTradeReportObjectIdentifier();
    TradeReportBillingStruct getTradeReportBillingStruct();
}