package com.cboe.interfaces.domain;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiOrder.BustReportStruct;
import com.cboe.idl.cmiOrder.FilledReportStruct;

/**
 * Base class for quote history service that extends the quote history IDL.
 * 
 * Service Identifier: It's a unique identifier that's exported by the history
 * server at the time of registering it's service with the ORB. The client
 * (trader server) looks up the offer using the service identifier as a
 * constraint. The service identifier is specified in the configuration files -
 * the setContext.main and is passed to both client and server via command line
 * args to JVM (-DprefixmdhServiceIdentifier). The service identifier may have a
 * naming scheme but it's basic use is for the trade server to lookup history
 * service to persist history data.
 * 
 * Server Route name: This is the route name of the history server hosting the
 * quote history service. The trader server stops using the history service on
 * receiving erroes on CORBA calls. The server route name is used for telling
 * the client (trade server) to reconnect to the history service after service
 * failure. There's an AR (admin request) command that notifies the trade server
 * of availability of history server.
 * 
 * Transactions: History data gets persisted either directly during processing
 * (QuoteHistoryHomeImpl) or via a set of queues (QuoteHistoryHomeQueueImpl). We
 * don't want to incur the overhead of Jgrinder in case of
 * QuoteHistoryHomeQueueImpl as the persistence is going to be done in the
 * history server. Hence the need for hooks for transaction processing.
 * 
 * Code navigation: 1. The naming scheme for the java file is
 * QuoteHistoryService<>.java 2. The client code has 'Proxy' in the class name
 * 3. The server code has 'Local' in the class name. The term 'Local' in the
 * class name implies persistence via Jgrinder => JDBC calls.
 * 
 * @author singh
 * 
 */

public interface QuoteHistoryService
{
	void checkServiceIdentifier(String serviceId) throws DataValidationException;

	String getRemoteServiceIdentifier() throws SystemException, CommunicationException, AuthorizationException,
			DataValidationException;

	String getServiceIdentifier();

	String getServerRouteName();

	void setServerRouteName(String routeName);

	/**
	 * Hook to process transaction start/end/rollback
	 * 
	 */
	void startTransaction();

	void rollbackTransaction();

	boolean commitTransaction();

	void createHistoryMaintainQuote(Quote newQuote, short maintainEventType, long eventTime);

	void createHistoryCancelQuote(Quote quote, short cancelType, short cancelReason, long eventTime);

	void createHistoryFillQuote(Quote updatedQuote, FilledReportStruct fillInfo, long currentTime);

	void createHistoryFillQuoteLeg(Quote updatedQuote, FilledReportStruct fillInfo, long currentTime);

	void createHistoryBustQuote(Quote bustedQuote, BustReportStruct bustInfo, long currentTime);

	void createHistoryBustQuoteLeg(Quote bustedQuote, BustReportStruct bustInfo, long currentTime);

	void createBlockedEntries(java.lang.String blockId, com.cboe.idl.internalBusinessServices.QuoteHistoryEntriesStruct[] entry)
			throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

	void createEntries(com.cboe.idl.internalBusinessServices.QuoteHistoryEntriesStruct[] entry) throws SystemException,
			CommunicationException, AuthorizationException, DataValidationException;

	void createEntry(QuoteHistory entry);
}
