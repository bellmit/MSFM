package com.cboe.presentation.fix.appia;

import com.cboe.domain.util.fixUtil.FixUtilConstants;

import com.javtech.appia.ExecutionReport;

/**
 * Author: beniwalv
 * Date: Jul 20, 2004
 * Time: 3:00:04 PM
 */
public class ExecutionReportPublisherFactory {

	// Singleton
	private static ExecutionReportPublisherFactory theInstance = null;
	
	/**
	 * Returns a publisher based on the type of execution report. 
	 * Side effect: responds to synchronous calls for solicited execution reports.
	 * @param execReport a FIX execution report message
	 * @param session a FIX session
	 * @return a publisher
	 */
    public static ExecutionReportPublisher createPublisher(ExecutionReport execReport, FixSessionImpl session)
    {
    	ExecutionReportPublisherFactory theFactory = instance();
    	return theFactory.doCreatePublisher(execReport, session);
    }
	
	/**
	 * Access a singleton ExecutionReportPublisherFactory
	 * @return the instance of the factory
	 */
	public static synchronized ExecutionReportPublisherFactory instance() {
		if (theInstance == null) {
			theInstance = new ExecutionReportPublisherFactory();
		}
		return theInstance;
	}
	
	// Publishers for various types of execution reports
	private OrderBustPublisher orderBustPublisher = new OrderBustPublisher();
	private OrderBustReinstatePublisher orderBustReinstatePublisher = new OrderBustReinstatePublisher();
	private OrderCancelPublisher orderCancelPublisher = new OrderCancelPublisher();
	private OrderFillPublisher orderFillPublisher = new OrderFillPublisher();	
	private OrderNewPublisher orderNewPublisher = new OrderNewPublisher();
	private OrderPendingCancelPublisher orderPendingCancelPublisher = new OrderPendingCancelPublisher();
	private OrderPendingReplacePublisher orderPendingReplacePublisher = new OrderPendingReplacePublisher();
	private OrderRejectPublisher orderRejectPublisher = new OrderRejectPublisher();
	private OrderStatusPublisher orderStatusPublisher = new OrderStatusPublisher();
	
	// No public constructor
	protected ExecutionReportPublisherFactory() {
	}
    
    protected ExecutionReportPublisher doCreatePublisher(
			ExecutionReport execReport, FixSessionImpl session) {
		ExecutionReportPublisher erp;
		String execTransType = execReport.ExecTransType;
		if (execTransType.trim().equalsIgnoreCase(
				FixUtilConstants.ExecTransType.STATUS)) {
			erp = new OrderStatusPublisher();
		} else {
			String execType = execReport.ExecType;
			if (FixUtilConstants.ExecType.NEW.equals(execType)) {
				erp = orderNewPublisher;
			} else if (FixUtilConstants.ExecType.FILL.equals(execType)
					|| FixUtilConstants.ExecType.PARTIAL_FILL.equals(execType)) {
				erp = orderFillPublisher;
			} else if (FixUtilConstants.ExecType.CANCELED.equals(execType)) {
				if (execReport.Text.trim().equalsIgnoreCase("Trade Bust Report")) {
					erp = orderBustPublisher;
				} else {
					erp = orderCancelPublisher;
				}
			} else if (FixUtilConstants.ExecType.PENDING_CANCEL
					.equals(execType)) {
				erp = orderPendingCancelPublisher;
			} else if (FixUtilConstants.ExecType.RESTATED.equals(execType)) {
				erp = orderBustReinstatePublisher;
			} else if (FixUtilConstants.ExecType.PENDING_REPLACE
					.equals(execType)) {
				erp = orderPendingReplacePublisher;
			} else if (FixUtilConstants.ExecType.REJECTED.equals(execType)) {
				erp = orderRejectPublisher;
			} else {
				// What to return in a default situation - maybe an
				// OrderStatusPublisher()
				erp = orderStatusPublisher;
			}
		}
		return erp;
	}


}    
