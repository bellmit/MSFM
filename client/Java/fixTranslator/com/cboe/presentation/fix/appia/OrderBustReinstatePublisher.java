package com.cboe.presentation.fix.appia;

import com.javtech.appia.ExecutionReport;
import com.cboe.presentation.fix.util.FixExecutionReportToCmiMapper;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.exceptions.DataValidationException;
import com.cboe.idl.cmiOrder.OrderBustReinstateReportStruct;
import com.cboe.idl.cmiCallback.CMIOrderStatusConsumer;

/**
 * Author: beniwalv
 * Date: Jul 20, 2004
 * Time: 3:34:45 PM
 */
public class OrderBustReinstatePublisher extends ExecutionReportPublisher{
	
	public void publishExecutionReport(ExecutionReport er,
			FixSessionImpl session, CMIOrderStatusConsumer ordStatusConsumer) {
		
		OrderBustReinstateReportStruct ordBustReStruct;
		try {
			ordBustReStruct = processOrderBustReinstateReport(er, session);
			// Call method on OrderStatusConsumerImpl/OrderStatusV2ConsumerImpl
			// acceptOrderBustReinstateReport(OrderBustReinstateReportStruct
			// reinstateReport)
			ordStatusConsumer.acceptOrderBustReinstateReport(ordBustReStruct);
		} catch (DataValidationException dve) {
			GUILoggerHome.find().exception(dve,
			"OrderBustReinstatePublisher.processOrderBustReinstateReport");
		}
	}
	
	/**
	 * This method maps an order ack. to a OrderBustReportStruct. This
	 * method calls
	 * FixExecutionReportToCmiMapper.mapToOrderStruct(execReport) to map all
	 * the fields for a order bust report.
	 * 
	 * @param execReport the ExecutionReport
	 * @return a CMi structure to hold order bust status
	 * @throws DataValidationException if mapping fails
	 */
	private OrderBustReinstateReportStruct processOrderBustReinstateReport(
			ExecutionReport execReport, FixSessionImpl session)
	throws DataValidationException {
		return FixExecutionReportToCmiMapper.mapToOrderBustReinstateReportStruct(
				execReport, session.getDoNotSendValue());
	}
	
}
