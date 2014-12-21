/*
 * Created on Aug 10, 2004
 *
 */
package com.cboe.presentation.fix.appia;

import com.cboe.interfaces.presentation.common.logging.IGUILogger;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.javtech.appia.Logout;
import com.javtech.appia.MessageObject;

/**
 * Handler for FIX logout message
 * @author Don Mendelson
 *
 */
public class LogoutDispatcher implements FixMessageDispatcher {


	/* (non-Javadoc)
	 * @see com.cboe.presentation.fix.appia.FixMessageDispatcher#dispatch(com.javtech.appia.MessageObject, com.cboe.presentation.fix.appia.FixSessionImpl)
	 */
	public void dispatch(MessageObject message, FixSessionImpl session) {
		Logout logout = (Logout) message;
		
		IGUILogger logger = GUILoggerHome.find();
		if (logger.isDebugOn()) {
			logger.debug("Logout message received; " +
					logout.Text != null ? logout.Text : "no reason given",
					GUILoggerBusinessProperty.USER_SESSION);
		}
		
		if (logout.Text != null) {
			session.sessionLoggedOut(logout.Text);
		} else {
			session.sessionLoggedOut("Logged out");
		}
	}

}
