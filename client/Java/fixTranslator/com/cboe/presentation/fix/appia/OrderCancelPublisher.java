package com.cboe.presentation.fix.appia;

import com.javtech.appia.ExecutionReport;
import com.javtech.appia.MessageObject;
import com.cboe.idl.cmiOrder.OrderCancelReportStruct;
import com.cboe.idl.cmiCallback.CMIOrderStatusConsumer;
import com.cboe.interfaces.presentation.common.logging.IGUILogger;
import com.cboe.presentation.fix.util.FixExecutionReportToCmiMapper;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.exceptions.DataValidationException;

/**
 * Author: beniwalv
 * Date: Jul 20, 2004
 * Time: 3:41:45 PM
 */
public class OrderCancelPublisher extends ExecutionReportPublisher{

	public void publishExecutionReport(ExecutionReport execReport,
			FixSessionImpl session, CMIOrderStatusConsumer ordStatusConsumer) {
		
		OrderCancelReportStruct ordCancelReportStruct;
		try {
			ordCancelReportStruct = processOrderCancel(execReport, session);
			// Call method on OrderStatusConsumerImpl/OrderStatusV2ConsumerImpl
			// acceptOrderCanceledReport( OrderCancelReportStruct
			// ordCancelReportStruct )
			ordStatusConsumer.acceptOrderCanceledReport(ordCancelReportStruct);
			
			IGUILogger logger = GUILoggerHome.find();
			if (logger.isDebugOn()) {
				logger.debug("Published order cancel report " + execReport.ClOrdID,
						GUILoggerBusinessProperty.ORDER_ENTRY);
			}
		} catch (DataValidationException dve) {
			GUILoggerHome.find().exception(dve,
					"OrderCancelPublisher.processOrderCancel");
		}
	}

    /**
	 * This method maps an order ack. to a OrderCancelStruct. There should be no
	 * difference in handling an execution report for a Order Cancel Regular vs
	 * a Order Cancel Strategy. This method calls
	 * FixExecutionReportToCmiMapper.mapToOrderCancelReportStruct(execReport) to
	 * map all the fields for a order cancel.
	 * 
	 * @param execReport
	 * @throws DataValidationException
	 */
    private OrderCancelReportStruct processOrderCancel(ExecutionReport execReport, 
    		FixSessionImpl session)
			throws DataValidationException {
		return FixExecutionReportToCmiMapper.mapToOrderCancelReportStruct(
				execReport, session.getDoNotSendValue());
	}
}
