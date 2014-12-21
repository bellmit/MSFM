package com.cboe.interfaces.domain.session;

import java.util.Date;

/**
 * Per-TradingSessionClient detail of trading session events.
 * 
 * @see TradingSessionRegistration
 * @see TradingSessionRegistrationMapHome
 * @see TradingSessionEventHistory
 * @author Steven Sinclair
 */
public interface TradingSessionEventHistoryDetail
{
    /**
     *  The "parent" of this detail item.
     */
    TradingSessionEventHistoryEntry getHistoryEntry();

    /** 
     *  The name of the client for this detail entry (typically identified a trade server or other TradingSessionClient)
     */
    String getClientName();

    /**
     *  The state of the event for this particular trade server
     */
    short getEventState();

    /**
     *  The time the event state was most recently changed (if no changes yet, then the detail creation time).
     */
    Date getEventTime();
    long getEventTimeMillis();

    /**
     *  The transaction sequence number to associate with this item.
     */
    int getTransactionSequenceNumber();

    void setHistoryEntry(TradingSessionEventHistoryEntry value);
    void setClientName(String value);
    void setEventState(short value);
    void setEventTime(Date value);
    void setTransactionSequenceNumber(int value);
}
