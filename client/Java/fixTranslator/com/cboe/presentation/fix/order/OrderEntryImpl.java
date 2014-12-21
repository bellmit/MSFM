/*
 * Created on Jul 27, 2004
 *
 */
package com.cboe.presentation.fix.order;

import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.DomainManager;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.CORBA.Request;
import org.omg.CORBA.SetOverrideType;

import com.cboe.domain.util.fixUtil.FixUtilConstants;
import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmi.AMI_OrderEntryHandler;
import com.cboe.idl.cmiV3.OrderEntry;
import com.cboe.idl.cmiConstants.OrderCancelTypes;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiQuote.RFQEntryStruct;
import com.cboe.idl.cmiUtil.OperationResultStruct;
import com.cboe.interfaces.presentation.common.logging.IGUILogger;
import com.cboe.interfaces.application.OrderEntryV3;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.fix.appia.FixSessionImpl;
import com.cboe.presentation.fix.util.FixExecutionReportToCmiMapper;
import com.cboe.util.ExceptionBuilder;

import com.javtech.appia.*;

/**
 * Implementation of OrderEntry interface
 * @author Don Mendelson
 *
 */
public class OrderEntryImpl implements OrderEntry {

	/** 
	 * Default timeout for receiving an order ack in millis
	 */
	public static final int FIX_REQUEST_TIMEOUT = 60000;
	
	
	private FixSessionImpl fixSession;
	private int requestID = 0;
	
	/**
	 * Creates an instance of OrderEntryImpl
	 */
	public OrderEntryImpl() {
	}

	/**
	 * Set a reference to a FIX session
	 * @param fixSession a session for sending FIX messages
	 */
	public void setFixSession(FixSessionImpl fixSession) {
		this.fixSession = fixSession;
	}

    /* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderEntryOperations#acceptInternalizationOrder(com.cboe.idl.cmiOrder.OrderEntryStruct, com.cboe.idl.cmiOrder.OrderEntryStruct, short)
	 */
    public InternalizationOrderResultStruct acceptInternalizationOrder(OrderEntryStruct primaryOrderEntry,
                                                                OrderEntryStruct matchedOrderEntry, short matchOrderType)
                    throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                            NotAcceptedException, TransactionFailedException
    {
        OrderList ordList = new OrderList();


        CmiOrderToFixMapper.mapOrderListToFix(primaryOrderEntry, matchedOrderEntry, ordList, matchOrderType);

        // Send the FIX request and wait for acknowledgement.
        // sendRequest() throws an exception if it times out or can't send request.
		return enterOrderList(ordList);

    }
	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderEntryOperations#acceptCrossingOrder(com.cboe.idl.cmiOrder.OrderEntryStruct, com.cboe.idl.cmiOrder.OrderEntryStruct)
	 */
	public void acceptCrossingOrder(OrderEntryStruct buyCrossingOrder, 
			OrderEntryStruct sellCrossingOrder)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException,
			NotAcceptedException, TransactionFailedException,
			AlreadyExistsException {
		throw ExceptionBuilder.notAcceptedException("acceptCrossingOrder not supported", 0);
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderEntryOperations#acceptOrder(com.cboe.idl.cmiOrder.OrderEntryStruct)
	 */
	public OrderIdStruct acceptOrder(OrderEntryStruct anOrder)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException,
			NotAcceptedException, TransactionFailedException,
			AlreadyExistsException {
		// Create a FIX order message
		Order order = new Order(fixSession.getDoNotSendValue());
		// Translate OrderEntryStruct to FIX Order
		CmiOrderToFixMapper.mapOrderToFix(anOrder, order, fixSession.getDoNotSendValue());	
		// Send the FIX request and wait for acknowledgement. 
		// sendRequest() throws an exception if it times out or can't send request. 
		return enterOrder(order);
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderEntryOperations#acceptOrderByProductName(com.cboe.idl.cmiProduct.ProductNameStruct, com.cboe.idl.cmiOrder.OrderEntryStruct)
	 */
	public OrderIdStruct acceptOrderByProductName(ProductNameStruct product,
			OrderEntryStruct anOrder) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException, NotAcceptedException,
			TransactionFailedException, AlreadyExistsException {
		// Create a FIX order message
		Order order = new Order(fixSession.getDoNotSendValue());
		// Translate OrderEntryStruct to FIX Order
		CmiOrderToFixMapper.mapOrderToFix(anOrder, product, order, fixSession.getDoNotSendValue());
		// Send the FIX request and wait for acknowledgement. 
		// sendRequest() throws an exception if it times out or can't send request. 		
		return enterOrder(order);
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderEntryOperations#acceptOrderCancelReplaceRequest(com.cboe.idl.cmiOrder.CancelRequestStruct, com.cboe.idl.cmiOrder.OrderEntryStruct)
	 */
	public OrderIdStruct acceptOrderCancelReplaceRequest(
			CancelRequestStruct cancelRequest, OrderEntryStruct newOrder)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException,
			NotAcceptedException, TransactionFailedException {
		//	Create a FIX cancel/replace message
		OrderCancelReplace order = new OrderCancelReplace(fixSession.getDoNotSendValue());
		try {
			// Translate OrderEntryStruct to FIX Cancel/Replace
			CmiOrderToFixMapper.mapCancelReplaceToFix(cancelRequest, newOrder,
					order, fixSession.getDoNotSendValue());
		} catch (NotFoundException e) {
			throw ExceptionBuilder.notAcceptedException("Orginal order not found", 
					e.details.error);
		}
		// Send the FIX request and wait for acknowledgement.
		// sendRequest() throws an exception if it times out or can't send
		// request.
		return enterOrder(order);
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderEntryOperations#acceptOrderCancelRequest(com.cboe.idl.cmiOrder.CancelRequestStruct)
	 */
	public void acceptOrderCancelRequest(CancelRequestStruct cancelRequest)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException,
			NotAcceptedException, TransactionFailedException {
		
		try {
			if ( !CmiOrderToFixMapper.isPartialCancel(cancelRequest) ) {		
				//	Create a FIX cancel/replace message
				OrderCancelRequest cancel = new OrderCancelRequest(fixSession.getDoNotSendValue());
				// Translate CancelRequestStruct to FIX Cancel Request
				CmiOrderToFixMapper.mapCancelRequestToFix(cancelRequest, cancel);
				// Send the FIX request and wait for acknowledgement. 
				// sendRequest() throws an exception if it times out or can't send request. 		
				cancelOrder(cancel);	
			} else {
				// Handle partial cancel as a FIX cancel/replace
			
				//	Create a FIX cancel/replace message
				OrderCancelReplace order = new OrderCancelReplace(fixSession.getDoNotSendValue());

				// Translate OrderEntryStruct to FIX Cancel/Replace
				CmiOrderToFixMapper.mapPartialCancelToFix(cancelRequest,
						order, fixSession.getDoNotSendValue());

				// Send the FIX request and wait for acknowledgement.
				// sendRequest() throws an exception if it times out or can't send
				// request.
				enterOrder(order);			
			}

		} catch (NotFoundException e) {
			throw ExceptionBuilder.notAcceptedException("Orginal order not found", 
					e.details.error);
		}
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderEntryOperations#acceptOrderUpdateRequest(int, com.cboe.idl.cmiOrder.OrderEntryStruct)
	 */
	public void acceptOrderUpdateRequest(int currentRemainingQuantity, 
			OrderEntryStruct updatedOrder)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException,
			NotAcceptedException, TransactionFailedException {
		throw ExceptionBuilder.notAcceptedException("acceptOrderUpdateRequest not supported", 0);
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderEntryOperations#acceptRequestForQuote(com.cboe.idl.cmiQuote.RFQEntryStruct)
	 */
	public void acceptRequestForQuote(RFQEntryStruct rfq)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException,
			NotAcceptedException, TransactionFailedException {
		QuoteRequest quoteRequest = new QuoteRequest(fixSession.getDoNotSendValue());
		quoteRequest.QuoteReqID = "RFQ" + assignRequestID();
		CmiOrderToFixMapper.mapRfqToFix(rfq, quoteRequest);
		// Send the FIX message; do not wait for a response.
		fixSession.sendMessage(quoteRequest);
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderEntryOperations#acceptStrategyOrder(com.cboe.idl.cmiOrder.OrderEntryStruct, com.cboe.idl.cmiOrder.LegOrderEntryStruct[])
	 */
	public OrderIdStruct acceptStrategyOrder(OrderEntryStruct anOrder,
			LegOrderEntryStruct[] legEntryDetails) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException, NotAcceptedException,
			TransactionFailedException, AlreadyExistsException {
		// Create a FIX order message
		Order order = new Order(fixSession.getDoNotSendValue());
		// Translate OrderEntryStruct to FIX Order
		CmiOrderToFixMapper.mapOrderToFix(anOrder, legEntryDetails, order, 
				fixSession.getDoNotSendValue());

		// Send the FIX request and wait for acknowledgement. 
		// sendRequest() throws an exception if it times out or can't send request. 
		return enterOrder(order);
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderEntryOperations#acceptStrategyOrderCancelReplaceRequest(com.cboe.idl.cmiOrder.CancelRequestStruct, com.cboe.idl.cmiOrder.OrderEntryStruct, com.cboe.idl.cmiOrder.LegOrderEntryStruct[])
	 */
	public OrderIdStruct acceptStrategyOrderCancelReplaceRequest(
			CancelRequestStruct cancelRequest, OrderEntryStruct newOrder,
			LegOrderEntryStruct[] legEntryDetails) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException, NotAcceptedException,
			TransactionFailedException {
		//	Create a FIX cancel/replace message
		OrderCancelReplace order = new OrderCancelReplace(fixSession.getDoNotSendValue());
		try {
			// Translate OrderEntryStruct to FIX Cancel/Replace
			CmiOrderToFixMapper.mapCancelReplaceToFix(cancelRequest, newOrder,
					legEntryDetails, order, fixSession.getDoNotSendValue());
		} catch (NotFoundException e) {
			throw ExceptionBuilder.notAcceptedException("Orginal order not found", 
					e.details.error);
		}
		// Send the FIX request and wait for acknowledgement.
		// sendRequest() throws an exception if it times out or can't send
		// request.
		return enterOrder(order);
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderEntryOperations#acceptStrategyOrderUpdateRequest(int, com.cboe.idl.cmiOrder.OrderEntryStruct, com.cboe.idl.cmiOrder.LegOrderEntryStruct[])
	 */
	public void acceptStrategyOrderUpdateRequest(int currentRemainingQuantity,
			OrderEntryStruct updatedOrder, 
			LegOrderEntryStruct[] legEntryDetails)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException,
			NotAcceptedException, TransactionFailedException {
		throw ExceptionBuilder.notAcceptedException("acceptStrategyOrderUpdateRequest not supported", 0);
	}
	
	/**
	 * Send a FIX order and wait for acknowlegement
	 * @param order FIX message to send
	 * @return order ID of accepted order, if successful
	 * @throws CommunicationException
	 * @throws SystemException
	 * @throws AlreadyExistsException
	 * @throws NotAcceptedException
	 * @throws DataValidationException
	 */
	private OrderIdStruct enterOrder(Order order)
			throws CommunicationException, SystemException,
			AlreadyExistsException, NotAcceptedException, DataValidationException {
		// Send the FIX request and wait for acknowledgement. 
		// sendRequest() throws an exception if it times out or can't send request. 
		MessageObject responses[] = fixSession.sendRequest(order.ClOrdID,
				order, FIX_REQUEST_TIMEOUT);
		
		OrderIdStruct orderId = null;
		if (responses.length == 0) {
			throw ExceptionBuilder.systemException(
					"No response received for order " + order.ClOrdID, 0);
		} else for (int i=0; i < responses.length; i++ ) {
			if (responses[i] instanceof ExecutionReport) {
				ExecutionReport executionReport = (ExecutionReport) responses[i];
				if (FixUtilConstants.ExecType.REJECTED.equals(executionReport.ExecType)) {
					// If reject received, throw an exception
					switch (executionReport.OrdRejReason) {
					case FixUtilConstants.OrdRejReason.DUPLICATE_ORDER :
						throw ExceptionBuilder.alreadyExistsException(executionReport.Text, 0);
					default :				
						throw ExceptionBuilder.notAcceptedException(executionReport.Text, 0);
					}
				} else {
					// If succesful, extract OrderIdStruct from execution report and return it
					orderId = FixExecutionReportToCmiMapper.mapToOrderIdStruct(executionReport);
					IGUILogger logger = GUILoggerHome.find();
					if (logger.isDebugOn()) {
						logger.debug("Order accepted " 
								+ order.ClOrdID,
								GUILoggerBusinessProperty.ORDER_ENTRY);
					}						
				}
			} else {
				MessageObject msg = responses[i];
				GUILoggerHome.find().alarm("Unexpected response for order " 
						+ order.ClOrdID
						+ " of type " + msg.getMsgType(),
						GUILoggerBusinessProperty.ORDER_ENTRY);
			}
		}
		
		return orderId;
	}

	/**
	 * Send a FIX cancel/replace and wait for acknowlegement
	 * @param order FIX message to send
	 * @return order ID of accepted order, if successful
	 * @throws CommunicationException
	 * @throws SystemException
	 * @throws NotAcceptedException
	 * @throws DataValidationException
	 */
	private OrderIdStruct enterOrder(OrderCancelReplace order)
			throws CommunicationException, SystemException,
			NotAcceptedException, DataValidationException {
		// Send the FIX request and wait for acknowledgement. 
		// sendRequest() throws an exception if it times out or can't send request. 
		MessageObject responses[] = fixSession.sendRequest(order.ClOrdID,
				order, FIX_REQUEST_TIMEOUT);
		
		OrderIdStruct orderId = null;
		if (responses.length == 0) {
			throw ExceptionBuilder.systemException(
					"No response received for cancel/replace " + order.ClOrdID,
					0);
		} else for (int i=0; i < responses.length; i++ ) {
			if (responses[i] instanceof OrderCancelReject) {
				OrderCancelReject cancelReject = (OrderCancelReject) responses[i];
				throw ExceptionBuilder.notAcceptedException(cancelReject.Text, 0);
			} else if (responses[i] instanceof ExecutionReport) {
				ExecutionReport executionReport = (ExecutionReport) responses[i];
				// If succesful, extract OrderIdStruct from execution report and return it
				orderId = FixExecutionReportToCmiMapper.mapToOrderIdStruct(executionReport);
				IGUILogger logger = GUILoggerHome.find();
				if (logger.isDebugOn()) {
					logger.debug("Cancel/replace accepted " 
							+ order.ClOrdID,
							GUILoggerBusinessProperty.ORDER_ENTRY);
				}				
			} else {
				MessageObject msg = responses[i];
				GUILoggerHome.find().alarm("Unexpected response for cancel/replace " 
						+ order.ClOrdID
						+ " of type " + msg.getMsgType(),
						GUILoggerBusinessProperty.ORDER_ENTRY);
			}			
		}
		
		return orderId;
	}

	/**
	 * Send a FIX cancel request and wait for acknowlegement
	 * @param cancel FIX message to send
	 * @throws CommunicationException
	 * @throws SystemException
	 * @throws NotAcceptedException
	 * @throws DataValidationException
	 */
	private void cancelOrder(OrderCancelRequest cancel)
			throws CommunicationException, SystemException,
			NotAcceptedException, DataValidationException {
		// Send the FIX request and wait for acknowledgement. 
		// sendRequest() throws an exception if it times out or can't send request. 
		MessageObject responses[] = fixSession.sendRequest(cancel.ClOrdID,
				cancel, FIX_REQUEST_TIMEOUT);
		
		if (responses.length == 0) {
			throw ExceptionBuilder.systemException(
					"No response received for cancel request "
					+ cancel.ClOrdID, 0);
		} else for (int i=0; i < responses.length; i++ ) {
			if (responses[i] instanceof OrderCancelReject) {
				OrderCancelReject cancelReject = (OrderCancelReject) responses[i];
				StringBuffer reason = new StringBuffer("Order Cancel Request rejected");
				if (cancelReject.Text != null) {
					reason.append(cancelReject.Text);
				} else switch (cancelReject.CxlRejReason){
					case FixUtilConstants.CxlRejReason.TOO_LATE_TO_CANCEL :
						reason.append("; Too Late To Cancel");
						break;
					case FixUtilConstants.CxlRejReason.UNKNOWN_ORDER :
						reason.append("; Unknown order");
						break;
				}
				throw ExceptionBuilder.dataValidationException(reason.toString(), 0);
			} else if (responses[i] instanceof ExecutionReport) {
				ExecutionReport executionReport = (ExecutionReport) responses[i];
				// If succesful, return without throwing an exception
				OrderIdStruct orderId = FixExecutionReportToCmiMapper.mapToOrderIdStruct(executionReport);
				IGUILogger logger = GUILoggerHome.find();
				if (logger.isDebugOn()) {
					logger.debug("Cancel confirmed " 
							+ cancel.ClOrdID,
							GUILoggerBusinessProperty.ORDER_ENTRY);
				}
			} else {
				MessageObject msg = responses[i];
				GUILoggerHome.find().alarm("Unexpected response for cancel request " 
						+ cancel.ClOrdID
						+ " of type " + msg.getMsgType(),
						GUILoggerBusinessProperty.ORDER_ENTRY);
			}	
		}
	}

    /**
	 * Send a FIX order and wait for acknowlegement
	 * @param orderList FIX OrderList (35-=E) message to send
	 * @return order ID of accepted order, if successful
	 * @throws CommunicationException
	 * @throws SystemException
	 * @throws NotAcceptedException
	 * @throws DataValidationException
	 */
	private InternalizationOrderResultStruct enterOrderList(OrderList orderList)
			throws CommunicationException, SystemException,
		    NotAcceptedException, DataValidationException {
		// Send the FIX request and wait for acknowledgement.
		// sendRequest() throws an exception if it times out or can't send request.
        // The orderList.ListID is = "L" + ClOrdID - of the primary order.
        // The SenderSubID (Tag 50) is also the same as the ListID; for coordinating the two Execution Reports.

        // todo - check what happens if any of these values is true
        boolean primaryOrderRejected = false;
        boolean matchOrderRejected = false;

        MessageObject responses[] = fixSession.sendOrderListRequest(orderList.ListID, orderList, FIX_REQUEST_TIMEOUT);
        OrderIdStruct pOrderIdStruct = new OrderIdStruct();
        OperationResultStruct pOperationResultStruct = new OperationResultStruct();
        OrderIdStruct mOrderIdStruct = new OrderIdStruct();
        OperationResultStruct mOperationResultStruct = new OperationResultStruct();
        OrderResultStruct pOrderResultStruct = new OrderResultStruct(pOrderIdStruct, pOperationResultStruct);
        OrderResultStruct mOrderResultStruct = new OrderResultStruct(mOrderIdStruct, mOperationResultStruct);
        InternalizationOrderResultStruct iOrderResultStruct = new InternalizationOrderResultStruct(pOrderResultStruct, mOrderResultStruct);

		if (responses.length == 0) {
			throw ExceptionBuilder.systemException("No response received for orderList " + orderList.ListID, 0);
		} else {
            for (int i=0; i < responses.length; i++ ) {
                if (responses[i] instanceof ExecutionReport) {
                    ExecutionReport eR = (ExecutionReport) responses[i];
                    // check if this is the primary order
                    if (eR.ClOrdID.toString().equals(orderList.ClOrdID[0]))
                    {
                        if (FixUtilConstants.ExecType.REJECTED.equals(eR.ExecType)) {
                            primaryOrderRejected = true;
                            pOrderResultStruct.result.errorMessage = eR.Text;
                            // Todo : find out what this needs to be.
                            pOrderResultStruct.result.errorCode = -1;
                        } else {
                            // If succesful, extract OrderIdStruct from execution report and return it
                            pOrderResultStruct.result.errorCode = 0;
                            pOrderResultStruct.orderId =
                                    FixExecutionReportToCmiMapper.mapToOrderIdStruct(eR);
                            IGUILogger logger = GUILoggerHome.find();
                            if (logger.isDebugOn())
                            {
                                logger.debug(new StringBuffer().append("Primary Order accepted ").append(orderList.ClOrdID[i]).toString(),
                                                GUILoggerBusinessProperty.ORDER_ENTRY);
                            }
                        }
                    }
                    // Check if this is the match order
                    else if (eR.ClOrdID.toString().equals(orderList.ClOrdID[1])) {
                        if (FixUtilConstants.ExecType.REJECTED.equals(eR.ExecType)) {
                            matchOrderRejected = true;
                            mOrderResultStruct.result.errorMessage = eR.Text;
                            // Todo : find out what this needs to be.
                            mOrderResultStruct.result.errorCode = -1;
                        } else {
                            // If succesful, extract OrderIdStruct from execution report and return it
                            mOrderResultStruct.result.errorCode = 0;
                            mOrderResultStruct.orderId =
                                    FixExecutionReportToCmiMapper.mapToOrderIdStruct(eR);
                            IGUILogger logger = GUILoggerHome.find();
                            if (logger.isDebugOn())
                            {
                                logger.debug(new StringBuffer().append("Match Order accepted ").append(orderList.ClOrdID[i]).toString(),
                                                GUILoggerBusinessProperty.ORDER_ENTRY);
                            }
                        }
                    }
                } else {
                    MessageObject msg = responses[i];
                    GUILoggerHome.find().alarm("Unexpected response for order "
                            + orderList.ClOrdID[i]
                            + " of type " + msg.getMsgType(),
                            GUILoggerBusinessProperty.ORDER_ENTRY);
                }
            }
		}
		return iOrderResultStruct;
	}
	/**
	 * Assign a unique request ID for this order entry session
	 * @return a unique ID
	 */
	private synchronized int assignRequestID() {
		return ++requestID;
	}
	
	// **** Code below not used, but satisfies compiler ****
	
	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_create_request(org.omg.CORBA.Context, java.lang.String, org.omg.CORBA.NVList, org.omg.CORBA.NamedValue)
	 */
	public Request _create_request(Context arg0, String arg1, NVList arg2,
			NamedValue arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_create_request(org.omg.CORBA.Context, java.lang.String, org.omg.CORBA.NVList, org.omg.CORBA.NamedValue, org.omg.CORBA.ExceptionList, org.omg.CORBA.ContextList)
	 */
	public Request _create_request(Context arg0, String arg1, NVList arg2,
			NamedValue arg3, ExceptionList arg4, ContextList arg5) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_duplicate()
	 */
	public Object _duplicate() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_get_domain_managers()
	 */
	public DomainManager[] _get_domain_managers() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_get_interface_def()
	 */
	public Object _get_interface_def() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_get_policy(int)
	 */
	public Policy _get_policy(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_hash(int)
	 */
	public int _hash(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_is_a(java.lang.String)
	 */
	public boolean _is_a(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_is_equivalent(org.omg.CORBA.Object)
	 */
	public boolean _is_equivalent(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_non_existent()
	 */
	public boolean _non_existent() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_release()
	 */
	public void _release() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_request(java.lang.String)
	 */
	public Request _request(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_set_policy_override(org.omg.CORBA.Policy[], org.omg.CORBA.SetOverrideType)
	 */
	public Object _set_policy_override(Policy[] arg0, SetOverrideType arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderEntry#sendc_acceptCrossingOrder(com.cboe.idl.cmi.AMI_OrderEntryHandler, com.cboe.idl.cmiOrder.OrderEntryStruct, com.cboe.idl.cmiOrder.OrderEntryStruct)
	 */
	public void sendc_acceptCrossingOrder(AMI_OrderEntryHandler arg0,
			OrderEntryStruct arg1, OrderEntryStruct arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderEntry#sendc_acceptOrder(com.cboe.idl.cmi.AMI_OrderEntryHandler, com.cboe.idl.cmiOrder.OrderEntryStruct)
	 */
	public void sendc_acceptOrder(AMI_OrderEntryHandler arg0,
			OrderEntryStruct arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderEntry#sendc_acceptOrderByProductName(com.cboe.idl.cmi.AMI_OrderEntryHandler, com.cboe.idl.cmiProduct.ProductNameStruct, com.cboe.idl.cmiOrder.OrderEntryStruct)
	 */
	public void sendc_acceptOrderByProductName(AMI_OrderEntryHandler arg0,
			ProductNameStruct arg1, OrderEntryStruct arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderEntry#sendc_acceptOrderCancelReplaceRequest(com.cboe.idl.cmi.AMI_OrderEntryHandler, com.cboe.idl.cmiOrder.CancelRequestStruct, com.cboe.idl.cmiOrder.OrderEntryStruct)
	 */
	public void sendc_acceptOrderCancelReplaceRequest(
			AMI_OrderEntryHandler arg0, CancelRequestStruct arg1,
			OrderEntryStruct arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderEntry#sendc_acceptOrderCancelRequest(com.cboe.idl.cmi.AMI_OrderEntryHandler, com.cboe.idl.cmiOrder.CancelRequestStruct)
	 */
	public void sendc_acceptOrderCancelRequest(AMI_OrderEntryHandler arg0,
			CancelRequestStruct arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderEntry#sendc_acceptOrderUpdateRequest(com.cboe.idl.cmi.AMI_OrderEntryHandler, int, com.cboe.idl.cmiOrder.OrderEntryStruct)
	 */
	public void sendc_acceptOrderUpdateRequest(AMI_OrderEntryHandler arg0,
			int arg1, OrderEntryStruct arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderEntry#sendc_acceptRequestForQuote(com.cboe.idl.cmi.AMI_OrderEntryHandler, com.cboe.idl.cmiQuote.RFQEntryStruct)
	 */
	public void sendc_acceptRequestForQuote(AMI_OrderEntryHandler arg0,
			RFQEntryStruct arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderEntry#sendc_acceptStrategyOrder(com.cboe.idl.cmi.AMI_OrderEntryHandler, com.cboe.idl.cmiOrder.OrderEntryStruct, com.cboe.idl.cmiOrder.LegOrderEntryStruct[])
	 */
	public void sendc_acceptStrategyOrder(AMI_OrderEntryHandler arg0,
			OrderEntryStruct arg1, LegOrderEntryStruct[] arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderEntry#sendc_acceptStrategyOrderCancelReplaceRequest(com.cboe.idl.cmi.AMI_OrderEntryHandler, com.cboe.idl.cmiOrder.CancelRequestStruct, com.cboe.idl.cmiOrder.OrderEntryStruct, com.cboe.idl.cmiOrder.LegOrderEntryStruct[])
	 */
	public void sendc_acceptStrategyOrderCancelReplaceRequest(
			AMI_OrderEntryHandler arg0, CancelRequestStruct arg1,
			OrderEntryStruct arg2, LegOrderEntryStruct[] arg3) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderEntry#sendc_acceptStrategyOrderUpdateRequest(com.cboe.idl.cmi.AMI_OrderEntryHandler, int, com.cboe.idl.cmiOrder.OrderEntryStruct, com.cboe.idl.cmiOrder.LegOrderEntryStruct[])
	 */
	public void sendc_acceptStrategyOrderUpdateRequest(
			AMI_OrderEntryHandler arg0, int arg1, OrderEntryStruct arg2,
			LegOrderEntryStruct[] arg3) {
		// TODO Auto-generated method stub

	}

    /* (non-Javadoc)
	 * @see com.cboe.idl.cmiV3.OrderEntry#sendc_acceptInternalizationOrder(com.cboe.idl.cmiV3.AMI_OrderEntryHandler ami_orderEntryHandler, OrderEntryStruct orderEntryStruct, OrderEntryStruct orderEntryStruct1, short i)
	 */
    public void sendc_acceptInternalizationOrder(com.cboe.idl.cmiV3.AMI_OrderEntryHandler ami_orderEntryHandler,
                                                 OrderEntryStruct orderEntryStruct,
                                                 OrderEntryStruct orderEntryStruct1, short i) {
        // TODO Auto-generated method stub
    }
}
