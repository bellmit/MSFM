package com.cboe.presentation.fix.appia;

import com.javtech.appia.ExecutionReport;
import com.javtech.appia.MessageObject;
import com.cboe.idl.cmiCallback.CMIOrderStatusConsumer;

/**
 * Author: beniwalv
 * Date: Jul 20, 2004
 * Time: 3:45:09 PM
 */
public class OrderRejectPublisher extends ExecutionReportPublisher{

    public void publishExecutionReport(ExecutionReport execReport,
			FixSessionImpl session, CMIOrderStatusConsumer ordStatusConsumer) {
    	
		// Return from synchronous call
		MessageObject request = responseReceived(execReport, session);
	}
}
