/*
 * Created on Aug 9, 2004
 *
 */
package com.cboe.presentation.fix.appia;

import com.cboe.domain.util.fixUtil.FixUtilUserDefinedTagConstants;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiUser.UserStruct;
import com.cboe.interfaces.presentation.common.logging.IGUILogger;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;

import com.javtech.appia.LogonMsg;
import com.javtech.appia.MessageObject;

/**
 * Handler for FIX logon ack
 * 
 * Requires Appia configuration:
 * middleware_session_events = ON
 * 
 * @author Don Mendelson
 *
 */
public class LogonDispatcher implements FixMessageDispatcher {
	// Tags in user-defined fields
	private static final String USERID_TAG = "UserId";
	private static final String ACCOUNT_TAG = "Account";
	private static final String EXECUTINGGIVEUP_TAG = "ExecutingGiveup";
	
	/* (non-Javadoc)
	 * @see com.cboe.presentation.fix.appia.FixMessageDispatcher#dispatch(com.javtech.appia.MessageObject, com.cboe.presentation.fix.appia.FixSessionImpl)
	 */

	public void dispatch(MessageObject message, FixSessionImpl session) {
		LogonMsg msg = (LogonMsg) message;		
		
		// User profile is sent in tag 6600
		if (msg.UserDefined != null && msg.UserDefined.length() > 0) {
			// Extract a string between "6600=" and "|" delimiter.
			// Can't use udf table because it can't handle tag=value format within this field.
			int pos = msg.UserDefined.indexOf(FixUtilUserDefinedTagConstants.USER_PROFILE);
			if (pos >= 0) {
				// Map user defined fields to UserStruct
				UserStruct userStruct = session.getValidUser();
				mapUserStruct(msg.UserDefined.substring(pos+FixUtilUserDefinedTagConstants.USER_PROFILE.length()+1, 
						msg.UserDefined.length()-1), userStruct);
			}
		}
	}
	
	// Parse string created by CasAccessManager class in FIXCAS
	private UserStruct mapUserStruct(String str, UserStruct user) {
		
		IGUILogger logger = GUILoggerHome.find();
		if (logger.isDebugOn()) {
			logger.debug("User profile: " + str,
					GUILoggerBusinessProperty.USER_SESSION);
		}
		
		// Fields of serialized structure are comma delimited
		String [] fields = str.split(",");
		if (fields.length < 1) {
			logger.information("Failed to parse UserStruct in Logon msg", 
					GUILoggerBusinessProperty.USER_SESSION);
		}
		
		for (int i= 0; i < fields.length; i++) {
			// Each field is sent as a tag=value pair
			String [] pairs = fields[i].split("=");
			if (USERID_TAG.equals(pairs[0])) {
				user.userId = pairs[1];
				if (logger.isDebugOn()) {
					logger.debug("User ID: " + pairs[1],
							GUILoggerBusinessProperty.USER_SESSION);
				}
			} else if (ACCOUNT_TAG.equals(pairs[0])) {
				user.defaultProfile.account = pairs[1];
				if (logger.isDebugOn()) {
					logger.debug("Default account: " + pairs[1],
							GUILoggerBusinessProperty.USER_SESSION);
				}
			} else if (EXECUTINGGIVEUP_TAG.equals(pairs[0])) {
				// ExchangeFirm struct is colon delimited
				String [] firmStr = pairs[1].split(":");
				if (firmStr.length == 2) {
					user.defaultProfile.executingGiveupFirm = 
						new ExchangeFirmStruct(firmStr[0], firmStr[1]);
					if (logger.isDebugOn()) {
						logger.debug("ExecutingGiveup exchange: " + firmStr[0]
								+ " firmNumber: " + firmStr[1],
								GUILoggerBusinessProperty.USER_SESSION);
					}
				} else {
					logger.alarm(
					"Expected ExecutingGiveup in Logon msg as exchange:firmNumber but found [" +
					pairs[1] + "]",
					GUILoggerBusinessProperty.USER_SESSION);
				}
			}
		}
				
		return user;
	}

}
