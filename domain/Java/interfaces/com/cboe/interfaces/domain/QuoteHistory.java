package com.cboe.interfaces.domain;

import com.cboe.idl.internalBusinessServices.QuoteHistoryEntriesStruct;
import com.cboe.idl.internalBusinessServices.QuoteHistoryEntryStruct;

public interface QuoteHistory extends ActivityHistory
{
	QuoteHistoryEntryStruct toCORBAStruct();

	QuoteHistory fromCORBAStruct(QuoteHistoryEntryStruct entry);

	QuoteHistoryEntriesStruct toQuoteHistoryEntriesStruct();

	QuoteHistory fromCORBAStruct(QuoteHistoryEntriesStruct entry);

	void prepare();
}
