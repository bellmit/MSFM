/*
 * Created on Aug 23, 2004
 *
 */
package com.cboe.presentation.fix.quote;

import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.DomainManager;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.CORBA.Request;
import org.omg.CORBA.SetOverrideType;

import com.cboe.domain.util.fixUtil.FixUtilConstants;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmiCallbackV2.CMILockedQuoteStatusConsumer;
import com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer;
import com.cboe.idl.cmiCallbackV2.CMIRFQConsumer;
import com.cboe.idl.cmiQuote.ClassQuoteResultStruct;
import com.cboe.idl.cmiQuote.ClassQuoteResultStructV2;
import com.cboe.idl.cmiQuote.ClassQuoteResultStructV3;
import com.cboe.idl.cmiQuote.QuoteDetailStruct;
import com.cboe.idl.cmiQuote.QuoteEntryStruct;
import com.cboe.idl.cmiQuote.QuoteEntryStructV3;
import com.cboe.idl.cmiV3.AMI_QuoteHandler;
import com.cboe.idl.cmiV3.Quote;
import com.cboe.interfaces.presentation.common.logging.IGUILogger;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.fix.appia.FixSessionImpl;
import com.cboe.util.ExceptionBuilder;

import com.javtech.appia.MassQuote;
import com.javtech.appia.MessageObject;
import com.javtech.appia.QuoteAcknowledgement;
import com.javtech.appia.QuoteCancel;
import com.javtech.appia.QuoteStatusRequest;

/**
 * Implementation of Quote interface
 * @author Don Mendelson
 */
public class QuoteImpl implements Quote {
	
	/** 
	 * Default timeout for receiving an quote ack in millis
	 */
	public static final int DEFAULT_REQUEST_TIMEOUT = 60000;
	
	/** Quote ID prefix for a mass quote request */
	public static final String MASS_QUOTE_PREFIX = "MQ";
	
	/** Quote ID prefix for a quote cancel request */
	public static final String QUOTE_CANCEL_PREFIX = "QC";

	/** Quote ID prefix for a quote status request */
	public static final String QUOTE_STATUS_PREFIX = "QS";
	
	/** Quote ID prefix for a single quote request */
	public static final String SINGLE_QUOTE_PREFIX = "SQ";
	
	/**
	 * Examine a request ID to determine whether it starts with a prefix
	 * used for quotes. 
	 * @param requestID a request ID, such as ClOrdId returned in an ExecutionReport 
	 * @return
	 */
	public static boolean isAQuoteId(String requestID) {
		return requestID.startsWith(MASS_QUOTE_PREFIX) || 
			requestID.startsWith(SINGLE_QUOTE_PREFIX);
	}
	
	private FixSessionImpl fixSession;

	private int requestID = 0;

	/**
	 * Creates an instance of QuoteImpl
	 */
	public QuoteImpl() {
	}
	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.QuoteOperations#acceptQuote(com.cboe.idl.cmiQuote.QuoteEntryStruct)
	 */
	public void acceptQuote(QuoteEntryStruct quote) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException, NotAcceptedException,
			TransactionFailedException {
		
		// Create a FIX quote message
		com.javtech.appia.Quote fixQuote = new com.javtech.appia.Quote(fixSession.getDoNotSendValue());
		// Populate the FIX message from the CMi quote
		CmiQuoteToFixMapper.mapQuote(quote, fixQuote);
		// Assign a unique request ID
		fixQuote.QuoteID = assignRequestID(SINGLE_QUOTE_PREFIX);
		// Send the quote and wait for an acknowledgement
		enterQuote(fixQuote);
	}
	
	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.QuoteOperations#acceptQuotesForClass(int, com.cboe.idl.cmiQuote.QuoteEntryStruct[])
	 */
	public ClassQuoteResultStruct[] acceptQuotesForClass(int classKey,
			QuoteEntryStruct[] quotes) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException, NotAcceptedException,
			TransactionFailedException {
		// Convert V1 to V2 call
		ClassQuoteResultStructV2[] resultsV2 = 
			acceptQuotesForClassV2(classKey, quotes);
		ClassQuoteResultStruct[] results = new ClassQuoteResultStruct[resultsV2.length];
		for (int i = 0; i < results.length; i++) {
			results[i] = new ClassQuoteResultStruct(resultsV2[i].productKey,
					resultsV2[i].errorCode);
		}
		return results;
	}
	
	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.QuoteOperations#acceptQuotesForClassV2(int, com.cboe.idl.cmiQuote.QuoteEntryStruct[])
	 */
	public ClassQuoteResultStructV2[] acceptQuotesForClassV2(int classKey,
			QuoteEntryStruct[] quotes) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException, NotAcceptedException,
			TransactionFailedException {
		// Convert V2 to V3 call
		QuoteEntryStructV3 [] quotesV3 = new QuoteEntryStructV3[quotes.length];
		for (int i = 0; i < quotes.length; i++) {
			quotesV3[i] = new QuoteEntryStructV3(quotes[i], (short) 0);
		}
		ClassQuoteResultStructV3[] resultsV3 = acceptQuotesForClassV3(classKey,
				quotesV3);
		ClassQuoteResultStructV2 [] results = new ClassQuoteResultStructV2[resultsV3.length];
		for (int i = 0; i < results.length; i++) {
			results[i] = resultsV3[i].quoteResult;
		}		
		return results;
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV3.QuoteOperations#acceptQuotesForClassV3(int, com.cboe.idl.cmiQuote.QuoteEntryStructV3[])
	 */
	public ClassQuoteResultStructV3[] acceptQuotesForClassV3(int classKey,
			QuoteEntryStructV3[] quotes) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException, NotAcceptedException,
			TransactionFailedException {
		
		MassQuote fixQuote = new MassQuote(fixSession.getDoNotSendValue());
		CmiQuoteToFixMapper.mapQuote(classKey, quotes, fixQuote);
		// Assign a unique request ID
		fixQuote.QuoteID = assignRequestID(MASS_QUOTE_PREFIX);
		return enterQuote(fixQuote);
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.QuoteOperations#cancelAllQuotes(java.lang.String)
	 */
	public void cancelAllQuotes(String sessionName) throws SystemException,
			CommunicationException, AuthorizationException,
			NotAcceptedException, TransactionFailedException {
		// Create a FIX quote cancel message
		QuoteCancel fixQuote = new QuoteCancel(fixSession.getDoNotSendValue());
		CmiQuoteToFixMapper.mapQuoteCancel(sessionName, fixQuote);
		// Assign a unique request ID
		fixQuote.QuoteID = assignRequestID(QUOTE_CANCEL_PREFIX);
		// Send the quote
		// No normal synchronous return -- do NOT wait for an acknowledgement
		// TODO: wait a short time for possible reject, but continue without ack
		fixSession.sendMessage(fixQuote);	
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV3.QuoteOperations#cancelAllQuotesV3(java.lang.String)
	 */
	public void cancelAllQuotesV3(String sessionName) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException, NotAcceptedException,
			TransactionFailedException {
		cancelAllQuotes(sessionName);
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.QuoteOperations#cancelQuote(java.lang.String, int)
	 */
	public void cancelQuote(String sessionName, int productKey) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException, TransactionFailedException,
			NotAcceptedException, NotFoundException {
		// Create a FIX quote cancel message
		QuoteCancel fixQuote = new QuoteCancel(fixSession.getDoNotSendValue());
		// Populate the FIX message from the CMi quote
		CmiQuoteToFixMapper.mapQuoteCancel(sessionName, productKey, fixQuote);
		// Assign a unique request ID
		fixQuote.QuoteID = assignRequestID(QUOTE_CANCEL_PREFIX);
		// No normal synchronous return -- do NOT wait for an acknowledgement
		fixSession.sendMessage(fixQuote);			
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.QuoteOperations#cancelQuotesByClass(java.lang.String, int)
	 */
	public void cancelQuotesByClass(String sessionName, int classKey)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException,
			TransactionFailedException, NotAcceptedException, NotFoundException {
		// Create a FIX quote cancel message
		QuoteCancel fixQuote = new QuoteCancel(fixSession.getDoNotSendValue());
		// Populate the FIX message from the CMi quote
		CmiQuoteToFixMapper.mapQuoteCancelByClass(sessionName, classKey, fixQuote);
		// Assign a unique request ID
		fixQuote.QuoteID = assignRequestID(QUOTE_CANCEL_PREFIX);
		// No normal synchronous return -- do NOT wait for an acknowledgement
		fixSession.sendMessage(fixQuote);	
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.QuoteOperations#getQuote(java.lang.String, int)
	 */
	public QuoteDetailStruct getQuote(String sessionName, int productKey)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException, NotFoundException {
		throw ExceptionBuilder.dataValidationException("getQuote not supported", 0);
	}
	
	/**
	 * Set a reference to a FIX session
	 * @param fixSession a session for sending FIX messages
	 */
	public void setFixSession(FixSessionImpl fixSession) {
		this.fixSession = fixSession;
	}
	
	/**
	 * No-op since FIXCAS subscribes on behalf of user
	 * @see com.cboe.idl.cmiV2.QuoteOperations#subscribeQuoteLockedNotification(boolean, com.cboe.idl.cmiCallbackV2.CMILockedQuoteStatusConsumer, boolean)
	 */
	public void subscribeQuoteLockedNotification(boolean publishOnSubscribe,
			CMILockedQuoteStatusConsumer clientListener, boolean gmdCallback)
			throws SystemException, CommunicationException,
			DataValidationException, AuthorizationException {
	}

	/**
	 * No-op since FIXCAS subscribes on behalf of user
	 * @see com.cboe.idl.cmiV2.QuoteOperations#subscribeQuoteLockedNotificationForClass(int, boolean, com.cboe.idl.cmiCallbackV2.CMILockedQuoteStatusConsumer, boolean)
	 */
	public void subscribeQuoteLockedNotificationForClass(int classKey,
			boolean publishOnSubscribe, CMILockedQuoteStatusConsumer clientListener, boolean gmdCallback)
			throws SystemException, CommunicationException,
			DataValidationException, AuthorizationException {
	}

	/**
	 * No-op since FIXCAS subscribes on behalf of user
	 * @see com.cboe.idl.cmi.QuoteOperations#subscribeQuoteStatus(com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer, boolean)
	 */
	public void subscribeQuoteStatus(
			com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer clientListener, boolean gmdCallback)
			throws SystemException, CommunicationException,
			DataValidationException, AuthorizationException {
	}

	/**
	 * No-op since FIXCAS subscribes on behalf of user
	 * @see com.cboe.idl.cmiV2.QuoteOperations#subscribeQuoteStatusForClassV2(int, boolean, boolean, com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer, boolean)
	 */
	public void subscribeQuoteStatusForClassV2(int classKey, boolean publishOnSubscribe,
			boolean includeUserInitiatedStatus, CMIQuoteStatusConsumer clientListener, boolean gmdCallback)
			throws SystemException, CommunicationException,
			DataValidationException, AuthorizationException {
	}

	/**
	 * No-op since FIXCAS subscribes on behalf of user
	 * @see com.cboe.idl.cmi.QuoteOperations#subscribeQuoteStatusForFirm(com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer, boolean)
	 */
	public void subscribeQuoteStatusForFirm(
			com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer clientListener, boolean gmdCallback)
			throws SystemException, CommunicationException,
			DataValidationException, AuthorizationException {
	}

	/**
	 * No-op since FIXCAS subscribes on behalf of user
	 * @see com.cboe.idl.cmiV2.QuoteOperations#subscribeQuoteStatusForFirmForClassV2(int, com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer, boolean)
	 */
	public void subscribeQuoteStatusForFirmForClassV2(int classKey,
			CMIQuoteStatusConsumer clientListener, boolean gmdCallback) throws SystemException,
			CommunicationException, DataValidationException,
			AuthorizationException {
	}

	/**
	 * No-op since FIXCAS subscribes on behalf of user
	 * @see com.cboe.idl.cmiV2.QuoteOperations#subscribeQuoteStatusForFirmV2(com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer, boolean)
	 */
	public void subscribeQuoteStatusForFirmV2(CMIQuoteStatusConsumer clientListener,
			boolean gmdCallback) throws SystemException, CommunicationException,
			DataValidationException, AuthorizationException {
	}

	/**
	 * No-op since FIXCAS subscribes on behalf of user
	 * @see com.cboe.idl.cmi.QuoteOperations#subscribeQuoteStatusForFirmWithoutPublish(com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer, boolean)
	 */
	public void subscribeQuoteStatusForFirmWithoutPublish(
			com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer clientListener, boolean gmdCallback)
			throws SystemException, CommunicationException,
			DataValidationException, AuthorizationException {
	}

	/**
	 * No-op since FIXCAS subscribes on behalf of user
	 * @see com.cboe.idl.cmiV2.QuoteOperations#subscribeQuoteStatusV2(com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer, boolean, boolean, boolean)
	 */
	public void subscribeQuoteStatusV2(CMIQuoteStatusConsumer clientListener,
			boolean publishOnSubscribe, boolean includeUserInitiatedStatus, boolean gmdCallback) throws SystemException,
			CommunicationException, DataValidationException,
			AuthorizationException {
	}

	/**
	 * No-op since FIXCAS subscribes on behalf of user
	 * @see com.cboe.idl.cmi.QuoteOperations#subscribeQuoteStatusWithoutPublish(com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer, boolean)
	 */
	public void subscribeQuoteStatusWithoutPublish(
			com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer clientListener, boolean gmdCallback)
			throws SystemException, CommunicationException,
			DataValidationException, AuthorizationException {
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.QuoteOperations#subscribeRFQ(java.lang.String, int, com.cboe.idl.cmiCallback.CMIRFQConsumer)
	 */
	public void subscribeRFQ(String sessionName, int classKey,
			com.cboe.idl.cmiCallback.CMIRFQConsumer clientListener)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
		QuoteStatusRequest fixQuote = new QuoteStatusRequest(fixSession.getDoNotSendValue());
		try {
			// Populate the FIX message 
			CmiQuoteToFixMapper.mapQuoteStatusSubscription(sessionName, classKey, fixQuote);
			// Assign a unique request ID
			fixQuote.QuoteID = assignRequestID(QUOTE_STATUS_PREFIX);
			// No normal synchronous return -- do NOT wait for an acknowledgement
			fixSession.sendMessage(fixQuote);	
		} catch (NotFoundException e) {
			throw ExceptionBuilder.dataValidationException(e.getMessage(), 0);
		}
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.QuoteOperations#subscribeRFQV2(java.lang.String, int, com.cboe.idl.cmiCallbackV2.CMIRFQConsumer)
	 */
	public void subscribeRFQV2(String sessionName, int classKey, CMIRFQConsumer clientListener)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
		// Create a FIX quote cancel status request
		QuoteStatusRequest fixQuote = new QuoteStatusRequest(fixSession.getDoNotSendValue());
		try {
			// Populate the FIX message 
			CmiQuoteToFixMapper.mapQuoteStatusSubscription(sessionName, classKey, fixQuote);
			// Assign a unique request ID
			fixQuote.QuoteID = assignRequestID(QUOTE_STATUS_PREFIX);
			// No normal synchronous return -- do NOT wait for an acknowledgement
			fixSession.sendMessage(fixQuote);	
		} catch (NotFoundException e) {
			throw ExceptionBuilder.dataValidationException(e.getMessage(), 0);
		}
	}

	/**
	 * No-op since FIXCAS subscribes on behalf of user
	 * @see com.cboe.idl.cmiV2.QuoteOperations#unsubscribeQuoteLockedNotification(com.cboe.idl.cmiCallbackV2.CMILockedQuoteStatusConsumer)
	 */
	public void unsubscribeQuoteLockedNotification(
			CMILockedQuoteStatusConsumer clientListener) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException {
	}

	/**
	 * No-op since FIXCAS subscribes on behalf of user
	 * @see com.cboe.idl.cmiV2.QuoteOperations#unsubscribeQuoteLockedNotificationForClass(int, com.cboe.idl.cmiCallbackV2.CMILockedQuoteStatusConsumer)
	 */
	public void unsubscribeQuoteLockedNotificationForClass(int classKey,
			CMILockedQuoteStatusConsumer clientListener) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException {
	}

	/**
	 * No-op since FIXCAS subscribes on behalf of user
	 * @see com.cboe.idl.cmi.QuoteOperations#unsubscribeQuoteStatus(com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer)
	 */
	public void unsubscribeQuoteStatus(
			com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer clientListener)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
	}

	/**
	 * No-op since FIXCAS subscribes on behalf of user
	 * @see com.cboe.idl.cmiV2.QuoteOperations#unsubscribeQuoteStatusForClassV2(int, com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer)
	 */
	public void unsubscribeQuoteStatusForClassV2(int classKey,
			CMIQuoteStatusConsumer clientListener) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException {
	}

	/**
	 * No-op since FIXCAS subscribes on behalf of user
	 * @see com.cboe.idl.cmi.QuoteOperations#unsubscribeQuoteStatusForFirm(com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer)
	 */
	public void unsubscribeQuoteStatusForFirm(
			com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer clientListener)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
	}

	/**
	 * No-op since FIXCAS subscribes on behalf of user
	 * @see com.cboe.idl.cmiV2.QuoteOperations#unsubscribeQuoteStatusForFirmForClassV2(int, com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer)
	 */
	public void unsubscribeQuoteStatusForFirmForClassV2(int classKey,
			CMIQuoteStatusConsumer clientListener) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException {
	}

	/**
	 * No-op since FIXCAS subscribes on behalf of user
	 * @see com.cboe.idl.cmiV2.QuoteOperations#unsubscribeQuoteStatusForFirmV2(com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer)
	 */
	public void unsubscribeQuoteStatusForFirmV2(CMIQuoteStatusConsumer clientListener)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
	}

	/**
	 * No-op since FIXCAS subscribes on behalf of user
	 * @see com.cboe.idl.cmiV2.QuoteOperations#unsubscribeQuoteStatusV2(com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer)
	 */
	public void unsubscribeQuoteStatusV2(CMIQuoteStatusConsumer clientListener)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.QuoteOperations#unsubscribeRFQ(java.lang.String, int, com.cboe.idl.cmiCallback.CMIRFQConsumer)
	 */
	public void unsubscribeRFQ(String sessionName, int classKey,
			com.cboe.idl.cmiCallback.CMIRFQConsumer clientListener)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
		QuoteStatusRequest fixQuote = new QuoteStatusRequest(fixSession.getDoNotSendValue());
		try {
			// Populate the FIX message 
			CmiQuoteToFixMapper.mapQuoteStatusUnsubscribe(sessionName, classKey, fixQuote);
			// Assign a unique request ID
			fixQuote.QuoteID = assignRequestID(QUOTE_STATUS_PREFIX);
			// No normal synchronous return -- do NOT wait for an acknowledgement
			fixSession.sendMessage(fixQuote);	
		} catch (NotFoundException e) {
			throw ExceptionBuilder.dataValidationException(e.getMessage(), 0);
		}
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.QuoteOperations#unsubscribeRFQV2(java.lang.String, int, com.cboe.idl.cmiCallbackV2.CMIRFQConsumer)
	 */
	public void unsubscribeRFQV2(String sessionName, int classKey, CMIRFQConsumer clientListener)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
		QuoteStatusRequest fixQuote = new QuoteStatusRequest(fixSession.getDoNotSendValue());
		try {
			// Populate the FIX message 
			CmiQuoteToFixMapper.mapQuoteStatusUnsubscribe(sessionName, classKey, fixQuote);
			// Assign a unique request ID
			fixQuote.QuoteID = assignRequestID(QUOTE_STATUS_PREFIX);
			// No normal synchronous return -- do NOT wait for an acknowledgement
			fixSession.sendMessage(fixQuote);	
		} catch (NotFoundException e) {
			throw ExceptionBuilder.dataValidationException(e.getMessage(), 0);
		}
	}
	 
	/**
	 * Assign a unique request ID for this order entry session
	 * @return a unique ID
	 */
	protected synchronized String assignRequestID(String prefix) {
		StringBuffer sb = new StringBuffer(prefix);
		sb.append(++requestID);
		return sb.toString();
	}
	
	/**
	 * Send a FIX quote and wait for acknowledgement
	 * @param fixQuote a FIX quote message
	 * @throws CommunicationException
	 * @throws SystemException
	 * @throws DataValidationException
	 */
	private void enterQuote(com.javtech.appia.Quote fixQuote)
			throws CommunicationException, SystemException,
			DataValidationException {
		// Send the FIX request and wait for acknowledgement. 
		// sendRequest() throws an exception if it times out or can't send request. 
		MessageObject responses[] = fixSession.sendRequest(fixQuote.QuoteID,
				fixQuote, DEFAULT_REQUEST_TIMEOUT);
		
		if (responses.length == 0) {
			throw ExceptionBuilder.systemException(
					"No response received for quote " + fixQuote.QuoteID, 0);
		} else for (int i=0; i < responses.length; i++ ) {
			if (responses[i] instanceof QuoteAcknowledgement) {
				QuoteAcknowledgement quoteAck = (QuoteAcknowledgement) responses[i];
				if (quoteAck.QuoteAckStatus == FixUtilConstants.QuoteAckStatus.REJECTED) {
					DataValidationException exception = 
						FixQuoteToCmiMapper.mapQuoteRejectReason(quoteAck);
					throw exception;
				}
			} else {
				MessageObject msg = responses[i];
				GUILoggerHome.find().alarm("Unexpected response for quote " 
						+ fixQuote.QuoteID
						+ " of type " + msg.getMsgType(),
						GUILoggerBusinessProperty.QUOTE);
			}
		}
	}
	
	/**
	 * Send a FIX mass quote and wait for acknowledgement
	 * @param fixQuote a FIX mass quote message
	 * @throws CommunicationException
	 * @throws SystemException
	 * @throws AuthorizationException
	 * @throws DataValidationException
	 */
	private ClassQuoteResultStructV3[] enterQuote(MassQuote fixQuote)
			throws CommunicationException, SystemException,
			AuthorizationException, DataValidationException {
		
		ClassQuoteResultStructV3[] resultsV3 = 
			new ClassQuoteResultStructV3[fixQuote.NoQuoteEntries[0]];
		
		// Send the FIX request and wait for acknowledgement. 
		// sendRequest() throws an exception if it times out or can't send request. 
		MessageObject responses[] = fixSession.sendRequest(fixQuote.QuoteID,
				fixQuote, DEFAULT_REQUEST_TIMEOUT);
		
		if (responses.length == 0) {
			throw ExceptionBuilder.systemException(
					"No response received for mass quote " + fixQuote.QuoteID, 0);
		} else for (int i=0; i < responses.length; i++ ) {
			if (responses[i] instanceof QuoteAcknowledgement) {
				QuoteAcknowledgement quoteAck = (QuoteAcknowledgement) responses[i];
				if (quoteAck.QuoteAckStatus == FixUtilConstants.QuoteAckStatus.REJECTED) {
					switch (quoteAck.QuoteRejectReason) {
					case FixUtilConstants.QuoteEntryRejectReason.NOT_AUTHORIZED_TO_QUOTE_SECURITY :
						throw ExceptionBuilder.authorizationException(quoteAck.Text, 0);
					default :
						throw ExceptionBuilder.dataValidationException(quoteAck.Text, 0);
					}
				} else {
					try {
						// Populate the results
						FixQuoteToCmiMapper.mapQuoteResults(quoteAck, resultsV3);
						IGUILogger logger = GUILoggerHome.find();
						if (logger.isDebugOn()) {
							logger.debug("Mass quote accepted " 
									+ fixQuote.QuoteID,
									GUILoggerBusinessProperty.QUOTE);
						}						
					} catch (NotFoundException e) {
						throw ExceptionBuilder.dataValidationException(
							"Product not found for mass quote " + fixQuote.QuoteID, 0);
					}
				}
			} else {
				MessageObject msg = responses[i];
				GUILoggerHome.find().alarm("Unexpected response for mass quote " 
						+ fixQuote.QuoteID
						+ " of type " + msg.getMsgType(),
						GUILoggerBusinessProperty.QUOTE);
			}
		}
		return resultsV3;
	}
	
	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_create_request(org.omg.CORBA.Context, java.lang.String, org.omg.CORBA.NVList, org.omg.CORBA.NamedValue)
	 */
	public Request _create_request(Context ctx, String operation,
			NVList arg_list, NamedValue result) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_create_request(org.omg.CORBA.Context, java.lang.String, org.omg.CORBA.NVList, org.omg.CORBA.NamedValue, org.omg.CORBA.ExceptionList, org.omg.CORBA.ContextList)
	 */
	public Request _create_request(Context ctx, String operation,
			NVList arg_list, NamedValue result, ExceptionList exclist,
			ContextList ctxlist) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_duplicate()
	 */
	public Object _duplicate() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_get_domain_managers()
	 */
	public DomainManager[] _get_domain_managers() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_get_interface_def()
	 */
	public Object _get_interface_def() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_get_policy(int)
	 */
	public Policy _get_policy(int policy_type) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_hash(int)
	 */
	public int _hash(int maximum) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_is_a(java.lang.String)
	 */
	public boolean _is_a(String repositoryIdentifier) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_is_equivalent(org.omg.CORBA.Object)
	 */
	public boolean _is_equivalent(Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_non_existent()
	 */
	public boolean _non_existent() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_release()
	 */
	public void _release() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_request(java.lang.String)
	 */
	public Request _request(String operation) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_set_policy_override(org.omg.CORBA.Policy[], org.omg.CORBA.SetOverrideType)
	 */
	public Object _set_policy_override(Policy[] policies,
			SetOverrideType set_add) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.Quote#sendc_acceptQuote(com.cboe.idl.cmi.AMI_QuoteHandler, com.cboe.idl.cmiQuote.QuoteEntryStruct)
	 */
	public void sendc_acceptQuote(com.cboe.idl.cmi.AMI_QuoteHandler arg0,
			QuoteEntryStruct arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.Quote#sendc_acceptQuotesForClass(com.cboe.idl.cmi.AMI_QuoteHandler, int, com.cboe.idl.cmiQuote.QuoteEntryStruct[])
	 */
	public void sendc_acceptQuotesForClass(
			com.cboe.idl.cmi.AMI_QuoteHandler arg0, int arg1,
			QuoteEntryStruct[] arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.Quote#sendc_acceptQuotesForClassV2(com.cboe.idl.cmiV2.AMI_QuoteHandler, int, com.cboe.idl.cmiQuote.QuoteEntryStruct[])
	 */
	public void sendc_acceptQuotesForClassV2(
			com.cboe.idl.cmiV2.AMI_QuoteHandler arg0, int arg1,
			QuoteEntryStruct[] arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV3.Quote#sendc_acceptQuotesForClassV3(com.cboe.idl.cmiV3.AMI_QuoteHandler, int, com.cboe.idl.cmiQuote.QuoteEntryStructV3[])
	 */
	public void sendc_acceptQuotesForClassV3(AMI_QuoteHandler arg0, int arg1,
			QuoteEntryStructV3[] arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.Quote#sendc_cancelAllQuotes(com.cboe.idl.cmi.AMI_QuoteHandler, java.lang.String)
	 */
	public void sendc_cancelAllQuotes(com.cboe.idl.cmi.AMI_QuoteHandler arg0,
			String arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV3.Quote#sendc_cancelAllQuotesV3(com.cboe.idl.cmiV3.AMI_QuoteHandler, java.lang.String)
	 */
	public void sendc_cancelAllQuotesV3(AMI_QuoteHandler arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.Quote#sendc_cancelQuote(com.cboe.idl.cmi.AMI_QuoteHandler, java.lang.String, int)
	 */
	public void sendc_cancelQuote(com.cboe.idl.cmi.AMI_QuoteHandler arg0,
			String arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.Quote#sendc_cancelQuotesByClass(com.cboe.idl.cmi.AMI_QuoteHandler, java.lang.String, int)
	 */
	public void sendc_cancelQuotesByClass(
			com.cboe.idl.cmi.AMI_QuoteHandler arg0, String arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.Quote#sendc_getQuote(com.cboe.idl.cmi.AMI_QuoteHandler, java.lang.String, int)
	 */
	public void sendc_getQuote(com.cboe.idl.cmi.AMI_QuoteHandler arg0,
			String arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.Quote#sendc_subscribeQuoteLockedNotification(com.cboe.idl.cmiV2.AMI_QuoteHandler, boolean, com.cboe.idl.cmiCallbackV2.CMILockedQuoteStatusConsumer, boolean)
	 */
	public void sendc_subscribeQuoteLockedNotification(
			com.cboe.idl.cmiV2.AMI_QuoteHandler arg0, boolean arg1,
			CMILockedQuoteStatusConsumer arg2, boolean arg3) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.Quote#sendc_subscribeQuoteLockedNotificationForClass(com.cboe.idl.cmiV2.AMI_QuoteHandler, int, boolean, com.cboe.idl.cmiCallbackV2.CMILockedQuoteStatusConsumer, boolean)
	 */
	public void sendc_subscribeQuoteLockedNotificationForClass(
			com.cboe.idl.cmiV2.AMI_QuoteHandler arg0, int arg1, boolean arg2,
			CMILockedQuoteStatusConsumer arg3, boolean arg4) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.Quote#sendc_subscribeQuoteStatus(com.cboe.idl.cmi.AMI_QuoteHandler, com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer, boolean)
	 */
	public void sendc_subscribeQuoteStatus(
			com.cboe.idl.cmi.AMI_QuoteHandler arg0,
			com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer arg1, boolean arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.Quote#sendc_subscribeQuoteStatusForClassV2(com.cboe.idl.cmiV2.AMI_QuoteHandler, int, boolean, boolean, com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer, boolean)
	 */
	public void sendc_subscribeQuoteStatusForClassV2(
			com.cboe.idl.cmiV2.AMI_QuoteHandler arg0, int arg1, boolean arg2,
			boolean arg3, CMIQuoteStatusConsumer arg4, boolean arg5) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.Quote#sendc_subscribeQuoteStatusForFirm(com.cboe.idl.cmi.AMI_QuoteHandler, com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer, boolean)
	 */
	public void sendc_subscribeQuoteStatusForFirm(
			com.cboe.idl.cmi.AMI_QuoteHandler arg0,
			com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer arg1, boolean arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.Quote#sendc_subscribeQuoteStatusForFirmForClassV2(com.cboe.idl.cmiV2.AMI_QuoteHandler, int, com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer, boolean)
	 */
	public void sendc_subscribeQuoteStatusForFirmForClassV2(
			com.cboe.idl.cmiV2.AMI_QuoteHandler arg0, int arg1,
			CMIQuoteStatusConsumer arg2, boolean arg3) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.Quote#sendc_subscribeQuoteStatusForFirmV2(com.cboe.idl.cmiV2.AMI_QuoteHandler, com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer, boolean)
	 */
	public void sendc_subscribeQuoteStatusForFirmV2(
			com.cboe.idl.cmiV2.AMI_QuoteHandler arg0,
			CMIQuoteStatusConsumer arg1, boolean arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.Quote#sendc_subscribeQuoteStatusForFirmWithoutPublish(com.cboe.idl.cmi.AMI_QuoteHandler, com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer, boolean)
	 */
	public void sendc_subscribeQuoteStatusForFirmWithoutPublish(
			com.cboe.idl.cmi.AMI_QuoteHandler arg0,
			com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer arg1, boolean arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.Quote#sendc_subscribeQuoteStatusV2(com.cboe.idl.cmiV2.AMI_QuoteHandler, com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer, boolean, boolean, boolean)
	 */
	public void sendc_subscribeQuoteStatusV2(
			com.cboe.idl.cmiV2.AMI_QuoteHandler arg0,
			CMIQuoteStatusConsumer arg1, boolean arg2, boolean arg3,
			boolean arg4) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.Quote#sendc_subscribeQuoteStatusWithoutPublish(com.cboe.idl.cmi.AMI_QuoteHandler, com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer, boolean)
	 */
	public void sendc_subscribeQuoteStatusWithoutPublish(
			com.cboe.idl.cmi.AMI_QuoteHandler arg0,
			com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer arg1, boolean arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.Quote#sendc_subscribeRFQ(com.cboe.idl.cmi.AMI_QuoteHandler, java.lang.String, int, com.cboe.idl.cmiCallback.CMIRFQConsumer)
	 */
	public void sendc_subscribeRFQ(com.cboe.idl.cmi.AMI_QuoteHandler arg0,
			String arg1, int arg2, com.cboe.idl.cmiCallback.CMIRFQConsumer arg3) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.Quote#sendc_subscribeRFQV2(com.cboe.idl.cmiV2.AMI_QuoteHandler, java.lang.String, int, com.cboe.idl.cmiCallbackV2.CMIRFQConsumer)
	 */
	public void sendc_subscribeRFQV2(com.cboe.idl.cmiV2.AMI_QuoteHandler arg0,
			String arg1, int arg2, CMIRFQConsumer arg3) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.Quote#sendc_unsubscribeQuoteLockedNotification(com.cboe.idl.cmiV2.AMI_QuoteHandler, com.cboe.idl.cmiCallbackV2.CMILockedQuoteStatusConsumer)
	 */
	public void sendc_unsubscribeQuoteLockedNotification(
			com.cboe.idl.cmiV2.AMI_QuoteHandler arg0,
			CMILockedQuoteStatusConsumer arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.Quote#sendc_unsubscribeQuoteLockedNotificationForClass(com.cboe.idl.cmiV2.AMI_QuoteHandler, int, com.cboe.idl.cmiCallbackV2.CMILockedQuoteStatusConsumer)
	 */
	public void sendc_unsubscribeQuoteLockedNotificationForClass(
			com.cboe.idl.cmiV2.AMI_QuoteHandler arg0, int arg1,
			CMILockedQuoteStatusConsumer arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.Quote#sendc_unsubscribeQuoteStatus(com.cboe.idl.cmi.AMI_QuoteHandler, com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer)
	 */
	public void sendc_unsubscribeQuoteStatus(
			com.cboe.idl.cmi.AMI_QuoteHandler arg0,
			com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.Quote#sendc_unsubscribeQuoteStatusForClassV2(com.cboe.idl.cmiV2.AMI_QuoteHandler, int, com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer)
	 */
	public void sendc_unsubscribeQuoteStatusForClassV2(
			com.cboe.idl.cmiV2.AMI_QuoteHandler arg0, int arg1,
			CMIQuoteStatusConsumer arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.Quote#sendc_unsubscribeQuoteStatusForFirm(com.cboe.idl.cmi.AMI_QuoteHandler, com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer)
	 */
	public void sendc_unsubscribeQuoteStatusForFirm(
			com.cboe.idl.cmi.AMI_QuoteHandler arg0,
			com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.Quote#sendc_unsubscribeQuoteStatusForFirmForClassV2(com.cboe.idl.cmiV2.AMI_QuoteHandler, int, com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer)
	 */
	public void sendc_unsubscribeQuoteStatusForFirmForClassV2(
			com.cboe.idl.cmiV2.AMI_QuoteHandler arg0, int arg1,
			CMIQuoteStatusConsumer arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.Quote#sendc_unsubscribeQuoteStatusForFirmV2(com.cboe.idl.cmiV2.AMI_QuoteHandler, com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer)
	 */
	public void sendc_unsubscribeQuoteStatusForFirmV2(
			com.cboe.idl.cmiV2.AMI_QuoteHandler arg0,
			CMIQuoteStatusConsumer arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.Quote#sendc_unsubscribeQuoteStatusV2(com.cboe.idl.cmiV2.AMI_QuoteHandler, com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer)
	 */
	public void sendc_unsubscribeQuoteStatusV2(
			com.cboe.idl.cmiV2.AMI_QuoteHandler arg0,
			CMIQuoteStatusConsumer arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.Quote#sendc_unsubscribeRFQ(com.cboe.idl.cmi.AMI_QuoteHandler, java.lang.String, int, com.cboe.idl.cmiCallback.CMIRFQConsumer)
	 */
	public void sendc_unsubscribeRFQ(com.cboe.idl.cmi.AMI_QuoteHandler arg0,
			String arg1, int arg2, com.cboe.idl.cmiCallback.CMIRFQConsumer arg3) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.Quote#sendc_unsubscribeRFQV2(com.cboe.idl.cmiV2.AMI_QuoteHandler, java.lang.String, int, com.cboe.idl.cmiCallbackV2.CMIRFQConsumer)
	 */
	public void sendc_unsubscribeRFQV2(
			com.cboe.idl.cmiV2.AMI_QuoteHandler arg0, String arg1, int arg2,
			CMIRFQConsumer arg3) {
		// TODO Auto-generated method stub

	}

}
