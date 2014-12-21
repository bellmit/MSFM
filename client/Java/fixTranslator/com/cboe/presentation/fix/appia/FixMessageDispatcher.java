/*
 * Created on Jul 15, 2004
 *
 */
package com.cboe.presentation.fix.appia;

import com.javtech.appia.MessageObject;

/**
 * Handler for a FIX message type
 * @author Don Mendelson
 *
 */
interface FixMessageDispatcher {

	/**
	 * Handle a FIX message
	 * @param message to handle
	 * @param session the FIX session that received the message
	 */
	void dispatch(MessageObject message, FixSessionImpl session);
}
