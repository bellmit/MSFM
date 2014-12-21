package com.cboe.interfaces.domain;

import com.cboe.exceptions.*;
import com.cboe.idl.cmiUtil.*;
/**
 * This class creates and finds TradeHistory.
 *
 * @author Connie Feng
 */
public interface TradeHistoryHome
{
	public final static String HOME_NAME = "TradeHistoryHome";

    /**
     * Creates an "ADD_NEW_EVENT" history entry.
     *
     * @author Eric Fredericks
     * @date 12/04/2000
     *
     * @param tradeId the trade ID
     * @param entry String
     *
     * @return TradeHistory
     */
    public TradeHistory createAddHistoryEntry(String sessionName, long tradeId, String entry)
        throws TransactionFailedException;

    /**
     * Creates a "BUST_EVENT" or "MODIFY_EVENT" history entry.
     *
     * @author Eric Fredericks
     * @date 01/09/2001
     *
     * @author Mei Wu - Change it into createUpdateHistoryEntry from  createBustHistoryEntry, to allow sharing code
     * @date 10/23/2003
     *
     * @param historyEventType - the history Event type
     * @param tradeId the trade ID
     * @param oldMatchedSequenceNumber the old matched sequence number for the atomic trade
     *      affected. (<code>int</code>).
     * @param productKey <code>int</code>
     * @param price <code>Price</code>
     * @param timeTraded <code>long</code>
     * @param entry <code>String</code>
     * @param quantity <code>int</code>
     * @param buyerOriginType <code>char</code>
     * @param buyerCmta <code>String</code>
     * @param buyerPositionEffect <code>char</code>
     * @param buyerSubaccount <code>String</code>
     * @param buyerMemberKey <code>String</code>
     * @param buyerFirmKey <code>String</code>
     * @param buyerOptionalData <code>String</code>
     * @param sellerOriginType <code>char</code>
     * @param sellerCmta <code>String</code>
     * @param sellerPositionEffect <code>char</code>
     * @param sellerSubaccount <code>String</code>
     * @param sellerMemberKey <code>String</code>
     * @param sellerFirmKey <code>String</code>
     * @param sellerOptionalData <code>String</code>
     *
     * @return TradeHistory
     */

    public TradeHistory createUpdateHistoryEntry(
        char historyEventType,
        String sessionName,
        long tradeId,
        int oldMatchedSequenceNumber,
        int productKey,
        Price price,
        long timeTraded,
        long entryTime,
        String entry,
        int quantity,
        char buyerOriginType,
        String buyerCmta,
        char buyerPositionEffect,
        String buyerSubaccount,
        String buyerMemberKey,
        String buyerFirmKey,
        String buyerOptionalData,
        char sellerOriginType,
        String sellerCmta,
        char sellerPositionEffect,
        String sellerSubaccount,
        String sellerMemberKey,
        String sellerFirmKey,
        String sellerOptionalData)
        throws TransactionFailedException;

/**
 * finds the trade histories
 * @author Connie Feng
 * @return TradeHistory[]
 * @param tradeId the trade ID
 */
public TradeHistory[] find(long tradeId)
	throws TransactionFailedException, NotFoundException;
}
