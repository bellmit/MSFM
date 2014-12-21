/*
 * Created on Aug 4, 2004
 *
 */
package com.cboe.presentation.fix.appia;

import com.cboe.domain.util.fixUtil.FixUtilConstants;
import com.cboe.domain.util.fixUtil.FixUtilDateTimeHelper;
import com.cboe.idl.cmiAdmin.MessageStruct;
import com.javtech.appia.Email;

/**
 * Map between FIX Email message and CMi message structures
 * @author Don Mendelson
 *
 */
public class EmailMapper {

	/**
	 * Map a FIX Email message to a CMi MessageStruct
	 * @param email a FIX text message
	 * @param cmiMessage a CMi text message
	 * @throws Exception if a field cannot be parsed
	 */
	public static void mapEmailToCmi(Email email, MessageStruct cmiMessage) throws Exception {
		cmiMessage.messageKey = 0;
		
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < email.LinesOfText; i++) {
			buffer.append(email.Text[i]);
		}
		cmiMessage.messageText = buffer.toString();
	    
		cmiMessage.originalMessageKey = 0;
		cmiMessage.replyRequested = false;
		cmiMessage.sender = email.header.SenderLocationID;
		cmiMessage.subject = email.Subject;
		
		if (email.OrigTime != null) {
			cmiMessage.timeStamp = 
				FixUtilDateTimeHelper.makeDateTimeStruct(email.OrigTime);
		} else {
			cmiMessage.timeStamp = 
				FixUtilDateTimeHelper.makeDateTimeStruct(email.header.SendingTime);
		}
	}
	
	/**
	 * Map CMi message structure to FIX Email message
	 * @param cmiMessage a CMi message
	 * @param email a FIX text message
	 */
	public static void mapMessageToFix(MessageStruct cmiMessage, Email email ) {
		email.EmailType = FixUtilConstants.Email.NEW;
		email.EmailThreadID = Integer.toString(cmiMessage.messageKey);
		email.LinesOfText = 1;
		email.Text = new String[1];
		email.Text[0] = cmiMessage.messageText;
		email.header.SenderLocationID = cmiMessage.sender;
		email.Subject = cmiMessage.subject;
		email.OrigTime = 
			FixUtilDateTimeHelper.dateTimeStructToString(cmiMessage.timeStamp);
	}


}
