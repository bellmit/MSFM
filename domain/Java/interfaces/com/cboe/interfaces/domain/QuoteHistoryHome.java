package com.cboe.interfaces.domain;

import com.cboe.idl.cmiOrder.BustReportStruct;
import com.cboe.idl.cmiOrder.FilledReportStruct;
import com.cboe.idl.internalBusinessServices.QuoteHistoryEntriesStruct;

/**
 * This class creates and finds QuoteHistory.
 * 
 * @author Werner Kubitsch
 */
public interface QuoteHistoryHome
{
	public final static String HOME_NAME = "QuoteHistoryHome";

	/**
	 * Creates a quote maintenance history event. Used for new and updated
	 * quotes.
	 */
	void createHistoryMaintainQuote(Quote newQuote, short maintainEventType);

	/**
	 * Creates a quote cancel history event. Used for system and user cancelled
	 * quotes.
	 */
	void createHistoryCancelQuote(Quote cancelledQuote, short cancelType, short cancelReason);

	/**
	 * Creates a fill quote history event.
	 */
	void createHistoryFillQuote(Quote filledQuote, FilledReportStruct fillInfo);

	/**
	 * Creates a fill quote leg history event.
	 */
	void createHistoryFillQuoteLeg(Quote filledQuote, FilledReportStruct fillInfo);

	/**
	 * Creates a bust quote history event.
	 */
	void createHistoryBustQuote(Quote bustedQuote, BustReportStruct bustInfo);

	/**
	 * Creates a bust quote leg history event.
	 */
	void createHistoryBustQuoteLeg(Quote bustedQuote, BustReportStruct bustInfo);

	/**
	 * 
	 * @author Werner Kubitsch
	 * @return com.cboe.interfaces.domain.QuoteHistory[]
	 * @param memberKey
	 *            java.lang.String
	 * @param productKey
	 *            int
	 */
	public QuoteHistory[] findClassQuotesByTime(String userID, int classKey, long startTime, short direction);

	public QuoteHistory[] findProductQuotesByTime(String userID, int productKey, long startTime, short direction);

	/**
	 * @return The instance id of the server process hosting this home
	 */
	String getServerInstanceId();
	
	/**
	 * @deprecated
	 */
	void createEntries(String blockId, QuoteHistoryEntriesStruct[] structs);
	
	/**
	 * @deprecated
	 */
	void createEntry (QuoteHistory entry);
	
	/**
	 * @deprecated
	 */
	void enqueueEntry (QHQueueEntry entry);
}
