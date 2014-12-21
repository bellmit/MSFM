package com.cboe.presentation.fix.appia;

import com.javtech.appia.ExecutionReport;
import com.cboe.presentation.fix.util.FixExecutionReportToCmiMapper;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.exceptions.DataValidationException;
import com.cboe.idl.cmiOrder.OrderFilledReportStruct;
import com.cboe.idl.cmiCallback.CMIOrderStatusConsumer;

/**
 * Author: beniwalv
 * Date: Jul 20, 2004
 * Time: 3:42:14 PM
 */
public class OrderFillPublisher extends ExecutionReportPublisher{

    public void publishExecutionReport(ExecutionReport er, FixSessionImpl session, CMIOrderStatusConsumer ordStatusConsumer)
    {
    	OrderFilledReportStruct ordFilledReportStruct;
    	try {
    		ordFilledReportStruct = processOrderFill(er, session);
    		
    		// Call method on OrderStatusConsumerImpl/OrderStatusV2ConsumerImpl
    		// acceptOrderFilledReport(OrderFilledReportStruct filledReport)
    		ordStatusConsumer.acceptOrderFilledReport(ordFilledReportStruct);
    	} catch (DataValidationException dve) {
    		GUILoggerHome.find().exception(dve,
    		"OrderFillPublisher.processOrderFill");
    	}
    }

    /**
	 * This method maps an order ack. to a OrderFillStruct. There should be no
	 * difference in handling an execution report for a Order Fill Regular vs a
	 * Order Fill Strategy. This method calls
	 * FixExecutionReportToCmiMapper.mapToOrderFilledReportStruct(execReport) to
	 * map all the fields for a order fill.
	 * 
	 * @param execReport
	 * @param the FIX session that received the ExecutionReport
	 * @return 
	 * @throws DataValidationException if mapping fails
	 */
    private OrderFilledReportStruct processOrderFill(
			ExecutionReport execReport, FixSessionImpl session)
			throws DataValidationException {

		return FixExecutionReportToCmiMapper.mapToOrderFilledReportStruct(
				execReport, session.getDoNotSendValue());
	}
}
