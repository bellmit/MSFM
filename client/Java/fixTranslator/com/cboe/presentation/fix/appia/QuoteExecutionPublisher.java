/*
 * Created on Sep 1, 2004
 *
 */
package com.cboe.presentation.fix.appia;

import org.omg.CORBA.UserException;

import com.cboe.domain.util.fixUtil.FixUtilConstants;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer;
import com.cboe.idl.cmiQuote.QuoteBustReportStruct;
import com.cboe.idl.cmiQuote.QuoteDetailStruct;
import com.cboe.idl.cmiQuote.QuoteFilledReportStruct;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.fix.quote.FixQuoteToCmiMapper;
import com.cboe.presentation.fix.quote.QuotePublisherFactory;

import com.javtech.appia.ExecutionReport;
import com.javtech.appia.MassQuote;
import com.javtech.appia.MessageObject;
import com.javtech.appia.QuoteAcknowledgement;

/**
 * Publish an execution of a quote
 * 
 * @author Don Mendelson
 *
 */
public class QuoteExecutionPublisher {

	/**
	 * Publish a quote execution
	 * @param session the FIX session that received the message
	 * @param execReport a FIX execution report
	 */
	public static void processQuoteExecution(FixSessionImpl session, 
			ExecutionReport execReport) {
		try {
			CMIQuoteStatusConsumer quoteStatusConsumer = 
				QuotePublisherFactory.instance().getQuoteStatusConsumer();
			
			String execType = execReport.ExecType;
			if (FixUtilConstants.ExecType.FILL.equals(execType)
				|| FixUtilConstants.ExecType.PARTIAL_FILL.equals(execType)) {
					publishQuoteFill(execReport, session, quoteStatusConsumer);
			} else if (FixUtilConstants.ExecType.CANCELED.equals(execType)) {
					publishQuoteBust(execReport, session, quoteStatusConsumer); 
			} else {
				GUILoggerHome.find().alarm(
						"ExecutionReport for quote received with unexpected ExecType "
						+ execReport.ExecType,
						GUILoggerBusinessProperty.QUOTE);
			}
		} catch (UserException e) {
			GUILoggerHome.find().exception("Failed to publish quote execution", e);
		} 

	}
	
	/**
	 * Publish quote status
	 * @param request a FIX quote request
	 * @param ack a FIX quote ack
	 * @param session the FIX session
	 * @throws NotFoundException
	 * @throws DataValidationException
	 * @throws AuthorizationException
	 * @throws CommunicationException
	 * @throws SystemException
	 */
	public static void publishQuoteStatus(MessageObject request,
			QuoteAcknowledgement ack, FixSessionImpl session)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException, NotFoundException {
		
		if (request instanceof MassQuote) {
			CMIQuoteStatusConsumer quoteStatusConsumer = 
				QuotePublisherFactory.instance().getQuoteStatusConsumer();
			
			QuoteDetailStruct [] quoteDetails = new QuoteDetailStruct[ack.NoQuoteEntries[0]];
			FixQuoteToCmiMapper.mapQuoteStatus((MassQuote)request, ack, quoteDetails);
			int queueDepth = FixQuoteToCmiMapper.getQueueDepth(ack);
			quoteStatusConsumer.acceptQuoteStatus(quoteDetails, queueDepth);
		}
	}
	
	private static void publishQuoteBust(ExecutionReport execReport,
			FixSessionImpl session, CMIQuoteStatusConsumer quoteStatusConsumer)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException, NotFoundException {
		QuoteBustReportStruct bustReport = new QuoteBustReportStruct();
		FixQuoteToCmiMapper.mapQuoteFillBust(execReport, bustReport);
		int queueDepth = FixQuoteToCmiMapper.getQueueDepth(execReport);
		quoteStatusConsumer.acceptQuoteBustReport(bustReport, queueDepth);
	}
	
	private static void publishQuoteFill(ExecutionReport execReport,
			FixSessionImpl session, CMIQuoteStatusConsumer quoteStatusConsumer)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException, NotFoundException {
		QuoteFilledReportStruct filledReport = new QuoteFilledReportStruct();
		FixQuoteToCmiMapper.mapQuoteFill(execReport, filledReport);
		int queueDepth = FixQuoteToCmiMapper.getQueueDepth(execReport);
		quoteStatusConsumer.acceptQuoteFilledReport(filledReport, queueDepth);
	}
}
