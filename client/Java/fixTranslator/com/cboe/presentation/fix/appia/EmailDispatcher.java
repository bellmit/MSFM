/*
 * Created on Aug 4, 2004
 *
 */
package com.cboe.presentation.fix.appia;

import com.cboe.idl.cmiAdmin.MessageStruct;
import com.cboe.presentation.common.logging.GUILoggerHome;

import com.javtech.appia.Email;
import com.javtech.appia.MessageObject;

/**
 * Handler for received FIX Email message
 * @author Don Mendelson
 */
public class EmailDispatcher implements FixMessageDispatcher {


	/* (non-Javadoc)
	 * @see com.cboe.presentation.fix.appia.FixMessageDispatcher#dispatch(com.javtech.appia.MessageObject, com.cboe.presentation.fix.appia.FixSessionImpl)
	 */
	public void dispatch(MessageObject message, FixSessionImpl session) {
		Email email = (Email) message;
		MessageStruct cmiMessage = new MessageStruct();
		try {
			EmailMapper.mapEmailToCmi(email, cmiMessage);
			session.getUserSessionAdminConsumer().acceptTextMessage(cmiMessage);
		} catch (Exception e) {
			GUILoggerHome.find().exception("Failed to dispatch received Email message", e);
		}
	}
}
