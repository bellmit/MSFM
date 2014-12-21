package com.cboe.interfaces.domain;

import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.trade.ErrorFlagStruct;

public interface TradeReportAck
{
    void create(CboeIdStruct anAtomicTradeId, int aMatchedSequenceNumber, char entryType,
        boolean aPositiveAck, ErrorFlagStruct theErrorFlags);

    void setAtomicTradeId(CboeIdStruct anAtomicTradeId);
    CboeIdStruct getAtomicTradeId();

    void setMatchedSequenceNumber(int aMatchedSequenceNumber);
    int getMatchedSequenceNumber();

    public void setEntryType(char anEntryType);
    public char getEntryType();

    void setIsAPositiveAck(boolean aPositiveAck);
    boolean isAPositiveAck();
    
    void setErrorFlags(long theErrorFlags);
    long getErrorFlags();
}