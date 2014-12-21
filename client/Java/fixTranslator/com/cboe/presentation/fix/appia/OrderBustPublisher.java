package com.cboe.presentation.fix.appia;

import com.javtech.appia.ExecutionReport;
import com.cboe.presentation.fix.util.FixExecutionReportToCmiMapper;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.exceptions.DataValidationException;
import com.cboe.idl.cmiOrder.OrderBustReportStruct;
import com.cboe.idl.cmiCallback.CMIOrderStatusConsumer;

/**
 * Author: beniwalv
 * Date: Jul 20, 2004
 * Time: 3:35:20 PM
 */
public class OrderBustPublisher extends ExecutionReportPublisher{

    public void publishExecutionReport(ExecutionReport er,
			FixSessionImpl session, CMIOrderStatusConsumer ordStatusConsumer) {

    	OrderBustReportStruct ordBustStruct;
		try {
			ordBustStruct = processOrderBustReport(er, session);
			// Call method on OrderStatusConsumerImpl/OrderStatusV2ConsumerImpl
			// acceptOrderBustReport(OrderBustReportStruct bustReport)
			ordStatusConsumer.acceptOrderBustReport(ordBustStruct);
		} catch (DataValidationException dve) {
			GUILoggerHome.find().exception(dve,
					"OrderBustPublisher.processOrderBustReport");
		}
    }

    /**
	 * This method maps an order ack. to a OrderBustReportStruct. This method
	 * calls FixExecutionReportToCmiMapper.mapToOrderStruct(execReport) to map
	 * all the fields for a order bust report.
	 * 
	 * @param execReport the ExecutionReport
     * @return a CMi structure to hold order bust status
     * @throws DataValidationException if mapping fails
	 */
    private OrderBustReportStruct processOrderBustReport(
			ExecutionReport execReport, FixSessionImpl session)
			throws DataValidationException {
		return FixExecutionReportToCmiMapper.mapToOrderBustReportStruct(
				execReport, session.getDoNotSendValue());
	}

}
