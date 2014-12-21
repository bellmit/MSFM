/*
 * Created on Jul 15, 2004
 *
 */
package com.cboe.presentation.fix.appia;

import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.javtech.appia.BusinessReject;
import com.javtech.appia.MessageObject;

/**
 * Handler for received Business Message Reject
 * @author Don Mendelson
 *
 */
public class BusinessRejectDispatcher implements FixMessageDispatcher {


	/* (non-Javadoc)
	 * @see com.cboe.presentation.fix.appia.FixMessageDispatcher#doDispatch(com.javtech.appia.MessageObject, com.cboe.presentation.fix.appia.FixSessionImpl)
	 */
	public void dispatch(MessageObject message, FixSessionImpl session) {
		BusinessReject reject = (BusinessReject) message;
		String requestID = reject.BusinessRejectRefID;
		if (requestID != null) {
			// Notify waiting requester that a rejection was received
			session.responseReceived(requestID, message);
		} else if (GUILoggerHome.find().isDebugOn()) {
			GUILoggerHome.find().debug("BusinessReject received without BusinessRejectRefID", GUILoggerBusinessProperty.COMMON);
		}
	}
}
