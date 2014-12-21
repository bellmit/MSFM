package com.cboe.interfaces.domain.session;

import com.cboe.idl.session.TradingSessionEventHistoryStruct;
import com.cboe.idl.session.TradingSessionEventHistoryStructV2;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.exceptions.NotFoundException;
import java.util.Collection;
import java.util.Map;

/**
 * A home for TradingSessionEventHistory instances.
 *
 * @author Steven Sinclair
 */
public interface TradingSessionEventHistoryHome {

	/**
	 * Name used to get home from HomeFactory.  Must also be name used for
	 * home in configuration.
	 */
	public static final String HOME_NAME = "TradingSessionEventHistoryHome";

	/**
	 * Create a new trading session event history entry.
	 *
     * @param sessionKey - the session related to this event.
     * @param dateTimeMillis - the time of the event status change in millis.
     * @param eventType - an enumerated value indicating the type of event.
     * @param eventStatus - an enumerated value indicating the status of the event indicated by this entry.
     * @param contextString - possibly null or empty, any extra context information which may be considered useful
     *      in describing this event.
     * @param historyDetail - Map of "client name -> state".  May be empty.
	 * @return created or existing trading session event history entry.
	 */
	TradingSessionEventHistoryEntry create(String sessionName, long dateTimeMillis, short eventType, short eventStatus, String contextString, Map historyDetail);

    /**
     * Update the state of the history detail for the given client.  Affects the lmod_time 
     *
     * @return TradingSessionEventHistoryEntry - a <i>transient</i> clone of the entry passed containing a transient clone
     *  of the affected detail.
     * @exception NotFoundException - thrown if the history detail could not be found for the given client name.
     */
    TradingSessionEventHistoryEntry updateStateForHistoryDetail(TradingSessionEventHistoryEntry historyEntry, String clientName, short newState) throws NotFoundException;

	/**
	 * Finds event history for a session.
	 *
	 * @param sessionName name of session
	 * @return collection of TradingSessionEventHistoryEntry instances.
	 * @exception NotFoundException if product is not found
	 */
	Collection findBySession(String sessionName) throws TransactionFailedException;

    /**
     *  Given an event instance, produce a struct.
     *  @param entry - the entry to create a struct for
     *  @return TradingSessionEventHistoryStruct - the struct created.
     */
    TradingSessionEventHistoryStruct convertToStruct(TradingSessionEventHistoryEntry entry);

    /**
     *  Given an event instance, produce a struct.
     *  @param entry - the entry to create a struct for
     *  @return TradingSessionEventHistoryStruct - the struct created.
     */
    TradingSessionEventHistoryStructV2 convertToV2Struct(TradingSessionEventHistoryEntry entry);
}
