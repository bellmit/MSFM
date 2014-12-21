package com.cboe.presentation.fix.appia;

import com.javtech.appia.*;
import com.cboe.exceptions.DataValidationException;
import com.cboe.idl.cmiOrder.OrderDetailStruct;
import com.cboe.idl.cmiCallback.CMIOrderStatusConsumer;
import com.cboe.interfaces.presentation.common.logging.IGUILogger;
import com.cboe.presentation.fix.util.FixExecutionReportToCmiMapper;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Author: beniwalv
 * Date: Jul 20, 2004
 * Time: 3:42:31 PM
 */
public class OrderNewPublisher extends ExecutionReportPublisher{

    public void publishExecutionReport(ExecutionReport execReport,
			FixSessionImpl session, CMIOrderStatusConsumer ordStatusConsumer) {
    	try {
    		// Return from synchronous call
    		MessageObject request = responseReceived(execReport, session);
    		
    		if (request == null) {
    			IGUILogger logger = GUILoggerHome.find();
    			if (logger.isDebugOn()) {
    				logger.debug("Execution report ExecType=New received but no request waiting",
    						GUILoggerBusinessProperty.ORDER_ENTRY);
    			}
    		} else if (request instanceof Order) {
    			Order order = (Order)request;
    			
    			OrderDetailStruct ordDetailStruct = processNewOrder(execReport, order, session);    			

				// Call method on OrderStatusConsumerImpl/OrderStatusV2ConsumerImpl
				// acceptNewOrder( OrderDetailStruct order )
				ordStatusConsumer.acceptNewOrder(ordDetailStruct);
				
    			IGUILogger logger = GUILoggerHome.find();
    			if (logger.isDebugOn()) {
    				logger.debug("Published new order " + execReport.ClOrdID,
    						GUILoggerBusinessProperty.ORDER_ENTRY);
    			}
    		} else if (request instanceof OrderCancelReplace) {
    			OrderCancelReplace order = (OrderCancelReplace)request;
    			
    			OrderDetailStruct ordDetailStruct = processNewOrder(execReport, order, session);
				// Call method on OrderStatusConsumerImpl/OrderStatusV2ConsumerImpl
				// acceptNewOrder( OrderDetailStruct order )
				ordStatusConsumer.acceptNewOrder(ordDetailStruct);	
	
    			IGUILogger logger = GUILoggerHome.find();
    			if (logger.isDebugOn()) {
    				logger.debug("Published new cancel/replace " + execReport.ClOrdID,
    						GUILoggerBusinessProperty.ORDER_ENTRY);
    			}
    		} else if (request instanceof OrderList) {
                OrderList oList = (OrderList) request;
                // todo : Check which order from the order list matches the Exec Report and send that out.
                if (oList.ClOrdID[0].equals(execReport.ClOrdID)) {
                    OrderDetailStruct ordDetailStruct = processNewOrder(execReport, oList, 0, session);
                    ordStatusConsumer.acceptNewOrder(ordDetailStruct);
                } else if (oList.ClOrdID[1].equals(execReport.ClOrdID)) {
                    OrderDetailStruct ordDetailStruct = processNewOrder(execReport, oList, 1, session);
				    ordStatusConsumer.acceptNewOrder(ordDetailStruct);
                }
            }
            else {
    			IGUILogger logger = GUILoggerHome.find();
    			if (logger.isDebugOn()) {
    				logger.debug("Execution report ExecType=New received for request of type " +
    						request.getMsgTypeStr(), 
							GUILoggerBusinessProperty.ORDER_ENTRY);
    			}
    		}
    	} catch (DataValidationException e) {
    		GUILoggerHome.find().exception("Failed to publish Order New", e);
    	}
    }

    /**
     * This method maps an order ack. to a OrderDetailStruct.
     * There should be no difference in handling an execution report for a New Order Regular vs a
     * New Order Strategy.
     * This method calls FixExecutionReportToCmiMapper.mapToOrderDetailStruct(execReport) to
     * map all the fields for a new order.
     * @param execReport the ExecutionReport
     * @param order the FIX order
     * @param session the FIX session that received the ExecutionReport
     * @return a CMi structure to hold order status
     * @throws DataValidationException if mapping fails
     */
    private OrderDetailStruct processNewOrder(ExecutionReport execReport,
			Order order, FixSessionImpl session) throws DataValidationException {
		return FixExecutionReportToCmiMapper.mapToOrderDetailStruct(execReport,
				order, session.getDoNotSendValue());
	}

    /**
     * Map acknowledgement of a cancel/replace to a OrderDetailStruct
     * @param execReport the ExecutionReport
     * @param order a FIX cancel/replace message
     * @param session the FIX session that received the ExecutionReport
     * @return a CMi structure to hold order status
     * @throws DataValidationException if mapping fails
     */
	private OrderDetailStruct processNewOrder(ExecutionReport execReport,
			OrderCancelReplace order, FixSessionImpl session) throws DataValidationException {
		return FixExecutionReportToCmiMapper.mapToOrderDetailStruct(execReport,
				order, session.getDoNotSendValue());
	}

    /**
     * This method maps an order ack. to a OrderDetailStruct.
     * There should be no difference in handling an execution report for a New Order Regular vs a
     * New Order Strategy.
     * This method calls FixExecutionReportToCmiMapper.mapToOrderDetailStruct(execReport) to
     * map all the fields for a new order.
     * @param execReport : the ExecutionReport
     * @param orderList : the FIX OrderList Message
     * @param index : the index of the order in the list to be mapped to the Execution Report
     * @param session : the FIX session that received the ExecutionReport
     * @return a CMi structure to hold order status
     * @throws DataValidationException if mapping fails
     */
    private OrderDetailStruct processNewOrder(ExecutionReport execReport,
			OrderList orderList, int index, FixSessionImpl session) throws DataValidationException {
		return FixExecutionReportToCmiMapper.mapToOrderDetailStruct(execReport,
				orderList, index, session.getDoNotSendValue());
	}
}
