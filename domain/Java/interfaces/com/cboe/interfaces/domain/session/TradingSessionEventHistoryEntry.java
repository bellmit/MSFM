package com.cboe.interfaces.domain.session;

import java.util.Collection;

/**
 * An entry in the firing history for a trading session event.
 *
 * @author Steven Sinclair
 */
public interface TradingSessionEventHistoryEntry
{
    /**
     *  Return the session key for this event
     */
    public String getSessionName();

    /**
     * Return the type of event
     */
    public short getEventType();

    /**
     * Return the status of the event 
     */
    public short getEventState();

    /**
     * Return the time of the event
     */
    public long getDateTimeMillis();

    /**
     * Return extra context information
     */
    public String getContextString();

    /**
     * Returns a collection of TradingSessionEventHistoryDetail, possibly empty but never null.
     */
    public Collection getHistoryDetails();

    public String getEventGroup();

    public String getEventTypeAsString();

    /**
     *  Return the history detail entry for this history event for the given clientName.
     *  @return TradingSessionEventHistoryDetail - the detail for the client; null if no such detail exists.
     */
    public TradingSessionEventHistoryDetail getHistoryDetail(String clientName);

    public void setEventGroup(String value);
}
