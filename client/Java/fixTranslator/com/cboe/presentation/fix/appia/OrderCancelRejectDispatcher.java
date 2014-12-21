/*
 * Created on Aug 17, 2004
 *
 */
package com.cboe.presentation.fix.appia;

import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.javtech.appia.MessageObject;
import com.javtech.appia.OrderCancelReject;

/**
 * Handler for received Order Cancel Reject (message type 9)
 * @author Don Mendelson
 */
public class OrderCancelRejectDispatcher implements FixMessageDispatcher {

	/* (non-Javadoc)
	 * @see com.cboe.presentation.fix.appia.FixMessageDispatcher#dispatch(com.javtech.appia.MessageObject, com.cboe.presentation.fix.appia.FixSessionImpl)
	 */
	public void dispatch(MessageObject message, FixSessionImpl session) {
		OrderCancelReject reject = (OrderCancelReject) message;
		String requestID = reject.ClOrdID;
		if (requestID != null) {
			// Notify waiting requester that a rejection was received
			session.responseReceived(requestID, message);
		} else if (GUILoggerHome.find().isDebugOn()) {
			GUILoggerHome.find().alarm("OrderCancelReject received without ClOrdID", 
					GUILoggerBusinessProperty.ORDER_ENTRY);
		}
	}

}
