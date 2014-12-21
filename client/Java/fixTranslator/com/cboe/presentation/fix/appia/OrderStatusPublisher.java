package com.cboe.presentation.fix.appia;

import com.javtech.appia.ExecutionReport;
import com.cboe.idl.cmiOrder.OrderDetailStruct;
import com.cboe.idl.cmiCallback.CMIOrderStatusConsumer;
import com.cboe.interfaces.presentation.common.logging.IGUILogger;
import com.cboe.presentation.fix.util.FixExecutionReportToCmiMapper;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.exceptions.DataValidationException;

/**
 * Author: beniwalv
 * Date: Jul 20, 2004
 * Time: 4:13:02 PM
 */
public class OrderStatusPublisher extends ExecutionReportPublisher{

    public void publishExecutionReport(ExecutionReport execReport,
			FixSessionImpl session, CMIOrderStatusConsumer ordStatusConsumer) {

		OrderDetailStruct[] ordDetailStruct = new OrderDetailStruct[1];
		try {
			ordDetailStruct[0] = processOrderStatus(execReport, session);

			// Call method on OrderStatusConsumerImpl/OrderStatusV2ConsumerImpl
			// acceptOrderStatus( OrderDetailStruct order )
			ordStatusConsumer.acceptOrderStatus(ordDetailStruct);
			
			IGUILogger logger = GUILoggerHome.find();
			if (logger.isDebugOn()) {
				logger.debug("Published order status " + execReport.ClOrdID,
						GUILoggerBusinessProperty.ORDER_ENTRY);
			}
		} catch (DataValidationException dve) {
			GUILoggerHome.find().exception(dve,
					"OrderStatusPublisher.processOrderStatus");
		}
	}

    /**
	 * This method maps an order ack. to a OrderDetailStruct
	 * 
	 * This method calls
	 * FixExecutionReportToCmiMapper.mapToOrderDetailStruct(execReport) to map
	 * all the fields for a new order.
	 * 
	 * @param execReport
	 * @throws DataValidationException
	 */
    private OrderDetailStruct processOrderStatus(ExecutionReport execReport,
			FixSessionImpl session) throws DataValidationException {

		return FixExecutionReportToCmiMapper.mapToOrderDetailStruct(execReport,
				session.getDoNotSendValue());

	}
}
