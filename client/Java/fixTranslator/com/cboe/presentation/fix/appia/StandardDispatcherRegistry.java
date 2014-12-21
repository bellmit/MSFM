/*
 * Created on Jul 15, 2004
 *
 */
package com.cboe.presentation.fix.appia;

import com.javtech.appia.BusinessReject;
import com.javtech.appia.Email;
import com.javtech.appia.ExecutionReport;
import com.javtech.appia.LogonMsg;
import com.javtech.appia.Logout;
import com.javtech.appia.OrderCancelReject;
import com.javtech.appia.Quote;
import com.javtech.appia.QuoteAcknowledgement;

/**
 * Registers message dispatchers for a FIX session
 * @author Don Mendelson
 *
 */
public class StandardDispatcherRegistry extends FixMessageDispatcherRegisty {

	/**
	 * Creates a registry with basic handlers
	 */
	public StandardDispatcherRegistry() {
		super();
		// Register usual message handlers here ...
		register(BusinessReject.msg_type, new BusinessRejectDispatcher());
		register(Email.msg_type, new EmailDispatcher());
		register(ExecutionReport.msg_type, new ExecutionReportDispatcher());
		register(LogonMsg.msg_type, new LogonDispatcher());
		register(Logout.msg_type, new LogoutDispatcher());
		register(OrderCancelReject.msg_type, new OrderCancelRejectDispatcher());
		register(Quote.msg_type, new QuoteDispatcher());
		register(QuoteAcknowledgement.msg_type, new QuoteAcknowledgementDispatcher());
	}
}
