/*
 * Created on Aug 24, 2004
 *
 */
package com.cboe.presentation.fix.appia;

import org.omg.CORBA.UserException;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiCallbackV2.CMILockedQuoteStatusConsumer;
import com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer;
import com.cboe.idl.cmiQuote.LockNotificationStruct;
import com.cboe.idl.cmiQuote.QuoteDetailStruct;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.fix.quote.FixQuoteToCmiMapper;
import com.cboe.presentation.fix.quote.QuotePublisherFactory;

import com.javtech.appia.MessageObject;
import com.javtech.appia.Quote;

/**
 * Handler for received FIX Quote message
 * @author Don Mendelson
 *
 */
public class QuoteDispatcher implements FixMessageDispatcher {

	/* (non-Javadoc)
	 * @see com.cboe.presentation.fix.appia.FixMessageDispatcher#dispatch(com.javtech.appia.MessageObject, com.cboe.presentation.fix.appia.FixSessionImpl)
	 */
	public void dispatch(MessageObject message, FixSessionImpl session) {
		Quote fixQuote = (Quote) message;
		
		String quoteStatus = FixQuoteToCmiMapper.getQuoteStatus(fixQuote);
		int queueDepth = FixQuoteToCmiMapper.getQueueDepth(fixQuote);
		
		if ("Locked".equals(quoteStatus)) {
			// It's a quote locked notification. Only 1 per FIX messsage.
			LockNotificationStruct [] lockedQuotes = new LockNotificationStruct[1];
			
			try {
				FixQuoteToCmiMapper.mapQuoteLock(fixQuote, lockedQuotes[0]);
			} catch (UserException e) {
				GUILoggerHome.find().exception(
						"Failed to dispatch received Quote message (lock notification", e);
			} 
			
			CMILockedQuoteStatusConsumer quoteLockedStatusConsumer = 
				QuotePublisherFactory.instance().getQuoteLockedStatusConsumer();
			quoteLockedStatusConsumer.acceptQuoteLockedReport(lockedQuotes, queueDepth);
			
		} else {
			// A FIX quote message only contains 1 quote
			QuoteDetailStruct[] quotes = new QuoteDetailStruct[1];
							
			try {
				FixQuoteToCmiMapper.mapQuote(fixQuote, quotes[0]);
				
				CMIQuoteStatusConsumer quoteStatusConsumer = 
					QuotePublisherFactory.instance().getQuoteStatusConsumer();
				quoteStatusConsumer.acceptQuoteStatus(quotes, queueDepth);
			} catch (UserException e) {
				GUILoggerHome.find().exception(
						"Failed to dispatch received Quote message", e);
			} 
		}
	}


}
