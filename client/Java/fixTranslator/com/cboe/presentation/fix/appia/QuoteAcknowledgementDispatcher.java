/*
 * Created on Aug 24, 2004
 *
 */
package com.cboe.presentation.fix.appia;

import org.omg.CORBA.UserException;

import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;

import com.javtech.appia.MessageObject;
import com.javtech.appia.QuoteAcknowledgement;

/**
 * Handler for received Quote Acknowledgement
 * @author Don Mendelson */
public class QuoteAcknowledgementDispatcher implements FixMessageDispatcher {

	/* (non-Javadoc)
	 * @see com.cboe.presentation.fix.appia.FixMessageDispatcher#dispatch(com.javtech.appia.MessageObject, com.cboe.presentation.fix.appia.FixSessionImpl)
	 */
	public void dispatch(MessageObject message, FixSessionImpl session) {
		QuoteAcknowledgement ack = (QuoteAcknowledgement) message;
		// Some have QuoteReqID and others have QuoteID populated
		String requestID = ack.QuoteReqID;
		if (requestID == null) {
			requestID = ack.QuoteID;
		}
		
		if (requestID != null) {
			// Notify waiting requester that an ack was received
			MessageObject request = session.responseReceived(requestID, message);
			try {
				QuoteExecutionPublisher.publishQuoteStatus(request, ack, session);
			} catch (UserException e) {
				GUILoggerHome.find().exception("Failed to publish quote status", e);
			}
		} else if (GUILoggerHome.find().isDebugOn()) {
			GUILoggerHome.find().alarm("QuoteAcknowledgement received without QuoteReqID", 
					GUILoggerBusinessProperty.QUOTE);
		}

	}

}
