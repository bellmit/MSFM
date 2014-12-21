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

import com.cboe.domain.util.fixUtil.FixUtilClOrdIDWrapper;
import com.cboe.domain.util.fixUtil.FixUtilConstants;
import com.cboe.domain.util.fixUtil.FixUtilExchangeFirmMapper;
import com.cboe.domain.util.fixUtil.FixUtilMapper;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer;
import com.cboe.idl.cmiOrder.OrderDetailStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.PendingOrderStruct;
import com.cboe.idl.cmiTraderActivity.ActivityHistoryStruct;
import com.cboe.idl.cmiV2.AMI_OrderQueryHandler;
import com.cboe.idl.cmiV2.OrderQuery;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.fix.appia.FixSessionImpl;
import com.cboe.presentation.fix.util.FixExecutionReportToCmiMapper;
import com.cboe.util.ExceptionBuilder;

import com.javtech.appia.ExecutionReport;
import com.javtech.appia.MessageObject;
import com.javtech.appia.OrderStatusRequest;

/**
 * Implementation of OrderQuery interface
 * @author Don Mendelson
 *
 */
public class OrderQueryImpl implements OrderQuery {

	/** 
	 * Default timeout for receiving an order status in millis
	 */
	public static final int FIX_REQUEST_TIMEOUT = 60000;

	private FixSessionImpl fixSession;
	

	/**
	 * Creates an instance of OrderQueryImpl
	 */
	public OrderQueryImpl() {

	}

	/** 
	 * Set a reference to a FIX session
	 * @param fixSession a session for sending FIX messages
	 */
	public void setFixSession(FixSessionImpl fixSession) {
		this.fixSession = fixSession;
	}
	 
	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderQueryOperations#getOrderById(com.cboe.idl.cmiOrder.OrderIdStruct)
	 */
	public OrderDetailStruct getOrderById(OrderIdStruct orderId)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException, NotFoundException {
		// Create a FIX order status request message
		OrderStatusRequest request = new OrderStatusRequest(fixSession.getDoNotSendValue());
		mapOrderStatusRequest(orderId, request);
		return requestOrderStatus(request);
	}

	/**
	 * Populate a FIX order status request from a CMi OrderIdStruct
	 * @param orderId a CMi order ID
	 * @param request a FIX order status request
	 * @throws DataValidationException
	 */
	private void mapOrderStatusRequest(OrderIdStruct orderId, OrderStatusRequest request) 
		throws DataValidationException {
		// Populate the FIX fields actually used to identify the order
		FixUtilClOrdIDWrapper clOrdId = 
			new FixUtilClOrdIDWrapper(orderId.branch, orderId.branchSequenceNumber, orderId.orderDate);
		request.ClOrdID = clOrdId.toString();
		request.OrderID = FixUtilMapper.getFixOrderID(orderId);
		request.ClientID = orderId.correspondentFirm;
		FixUtilExchangeFirmMapper firmMapper = 
			new FixUtilExchangeFirmMapper(orderId.executingOrGiveUpFirm); 
		request.ExecBroker = firmMapper.getFIXExchangeFirmString();

		// Required FIX fields - arbitrary values
		request.Symbol = "XXX";
		request.Side = FixUtilConstants.Side.BUY;
	}	
	
	/**
	 * Send a FIX order status request and wait for result
	 * @param request
	 * @return
	 * @throws CommunicationException
	 * @throws SystemException
	 * @throws DataValidationException
	 */
	private OrderDetailStruct requestOrderStatus(OrderStatusRequest request) 
		throws CommunicationException, SystemException, DataValidationException {
		// Send the FIX request and wait for acknowledgement. 
		// sendRequest() throws an exception if it times out or can't send request. 

		// Issue: order status request does not have its own ID. It possible to mix up
		// a status request and another request, such as a cancel, for the same order.
		MessageObject responses[] = fixSession.sendRequest(request.ClOrdID,
				request, FIX_REQUEST_TIMEOUT);
		
		OrderDetailStruct orderDetail = null;
		if (responses.length == 0) {
			throw ExceptionBuilder.systemException("No response received for order status request", 0);
		} else for (int i=responses.length-1; i >= 0; i-- ) {
			if (responses[i] instanceof ExecutionReport) {
				ExecutionReport executionReport = (ExecutionReport) responses[i];
				orderDetail = 
					FixExecutionReportToCmiMapper.mapToOrderDetailStruct(executionReport, 
							fixSession.getDoNotSendValue());
			} else {
				MessageObject msg = responses[i];
				GUILoggerHome.find().alarm("Unexpected response for order status request " 
						+ request.ClOrdID
						+ " of type " + msg.getMsgType(),
						GUILoggerBusinessProperty.ORDER_ENTRY);
			}
		}
		
		return orderDetail;	
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderQueryOperations#getOrdersForClass(int)
	 */
	public OrderDetailStruct[] getOrdersForClass(int classKey)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
		throw ExceptionBuilder.systemException("getOrdersForClass not supported", 0);
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderQueryOperations#getOrdersForProduct(int)
	 */
	public OrderDetailStruct[] getOrdersForProduct(int productKey)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
		throw ExceptionBuilder.systemException("getOrdersForProduct not supported", 0);
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderQueryOperations#getOrdersForSession(java.lang.String)
	 */
	public OrderDetailStruct[] getOrdersForSession(String sessionName)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
		throw ExceptionBuilder.systemException("getOrdersForSession not supported", 0);
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderQueryOperations#getOrdersForType(short)
	 */
	public OrderDetailStruct[] getOrdersForType(short productType)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
		throw ExceptionBuilder.systemException("getOrdersForType not supported", 0);
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderQueryOperations#getPendingAdjustmentOrdersByClass(java.lang.String, int)
	 */
	public PendingOrderStruct[] getPendingAdjustmentOrdersByClass(String sessionName,
			int classKey) throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
		throw ExceptionBuilder.systemException("getPendingAdjustmentOrdersByClass not supported", 0);
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderQueryOperations#getPendingAdjustmentOrdersByProduct(java.lang.String, int)
	 */
	public PendingOrderStruct[] getPendingAdjustmentOrdersByProduct(
			String sessionName, int productKey) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException {
		throw ExceptionBuilder.systemException("getPendingAdjustmentOrdersByProduct not supported", 0);
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderQueryOperations#queryOrderHistory(com.cboe.idl.cmiOrder.OrderIdStruct)
	 */
	public ActivityHistoryStruct queryOrderHistory(OrderIdStruct orderId )
			throws SystemException, CommunicationException,
			AuthorizationException, NotFoundException, DataValidationException {
		throw ExceptionBuilder.systemException("queryOrderHistory not supported", 0);
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderQueryOperations#subscribeOrders(com.cboe.idl.cmiCallback.CMIOrderStatusConsumer, boolean)
	 */
	public void subscribeOrders(
			com.cboe.idl.cmiCallback.CMIOrderStatusConsumer clientListener, boolean gmdCallback)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderQueryOperations#subscribeOrdersByFirm(com.cboe.idl.cmiCallback.CMIOrderStatusConsumer, boolean)
	 */
	public void subscribeOrdersByFirm(
			com.cboe.idl.cmiCallback.CMIOrderStatusConsumer clientListener, boolean gmdCallback)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderQueryOperations#subscribeOrdersByFirmWithoutPublish(com.cboe.idl.cmiCallback.CMIOrderStatusConsumer, boolean)
	 */
	public void subscribeOrdersByFirmWithoutPublish(
			com.cboe.idl.cmiCallback.CMIOrderStatusConsumer clientListener, boolean gmdCallback)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.OrderQueryOperations#subscribeOrderStatusForClassV2(int, com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer, boolean, boolean)
	 */
	public void subscribeOrderStatusForClassV2(int classKey,
			CMIOrderStatusConsumer clientListener, boolean publishOnSubscribe, boolean gmdCallback)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.OrderQueryOperations#subscribeOrderStatusForFirmForClassV2(int, com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer, boolean, boolean)
	 */
	public void subscribeOrderStatusForFirmForClassV2(int classKey,
			CMIOrderStatusConsumer clientListener, boolean publishOnSubscribe, boolean gmdCallback)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.OrderQueryOperations#subscribeOrderStatusForFirmV2(com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer, boolean, boolean)
	 */
	public void subscribeOrderStatusForFirmV2(CMIOrderStatusConsumer clientListener,
			boolean publishOnSubscribe, boolean gmdCallback) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.OrderQueryOperations#subscribeOrderStatusV2(com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer, boolean, boolean)
	 */
	public void subscribeOrderStatusV2(CMIOrderStatusConsumer clientListener,
			boolean publishOnSubscribe, boolean gmdCallback) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderQueryOperations#subscribeOrdersWithoutPublish(com.cboe.idl.cmiCallback.CMIOrderStatusConsumer, boolean)
	 */
	public void subscribeOrdersWithoutPublish(
			com.cboe.idl.cmiCallback.CMIOrderStatusConsumer clientListener, boolean gmdCallback)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderQueryOperations#unsubscribeAllOrderStatusForType(short, com.cboe.idl.cmiCallback.CMIOrderStatusConsumer)
	 */
	public void unsubscribeAllOrderStatusForType(short productType,
			com.cboe.idl.cmiCallback.CMIOrderStatusConsumer clientListener)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderQueryOperations#unsubscribeOrderStatusByClass(int, com.cboe.idl.cmiCallback.CMIOrderStatusConsumer)
	 */
	public void unsubscribeOrderStatusByClass(int classKey,
			com.cboe.idl.cmiCallback.CMIOrderStatusConsumer clientListener)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.OrderQueryOperations#unsubscribeOrderStatusForClassV2(int, com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer)
	 */
	public void unsubscribeOrderStatusForClassV2(int classKey,
			CMIOrderStatusConsumer clientListener) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderQueryOperations#unsubscribeOrderStatusForFirm(com.cboe.idl.cmiCallback.CMIOrderStatusConsumer)
	 */
	public void unsubscribeOrderStatusForFirm(
			com.cboe.idl.cmiCallback.CMIOrderStatusConsumer clientListener)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.OrderQueryOperations#unsubscribeOrderStatusForFirmForClassV2(int, com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer)
	 */
	public void unsubscribeOrderStatusForFirmForClassV2(int classKey,
			CMIOrderStatusConsumer clientListener) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.OrderQueryOperations#unsubscribeOrderStatusForFirmV2(com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer)
	 */
	public void unsubscribeOrderStatusForFirmV2(CMIOrderStatusConsumer clientListener)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderQueryOperations#unsubscribeOrderStatusForProduct(int, com.cboe.idl.cmiCallback.CMIOrderStatusConsumer)
	 */
	public void unsubscribeOrderStatusForProduct(int productKey,
			com.cboe.idl.cmiCallback.CMIOrderStatusConsumer clientListener)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderQueryOperations#unsubscribeOrderStatusForSession(java.lang.String, com.cboe.idl.cmiCallback.CMIOrderStatusConsumer)
	 */
	public void unsubscribeOrderStatusForSession(String sessionName,
			com.cboe.idl.cmiCallback.CMIOrderStatusConsumer clientListener)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.OrderQueryOperations#unsubscribeOrderStatusV2(com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer)
	 */
	public void unsubscribeOrderStatusV2(CMIOrderStatusConsumer clientListener)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
		// TODO Auto-generated method stub

	}

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
	 * @see com.cboe.idl.cmi.OrderQuery#sendc_getOrderById(com.cboe.idl.cmi.AMI_OrderQueryHandler, com.cboe.idl.cmiOrder.OrderIdStruct)
	 */
	public void sendc_getOrderById(com.cboe.idl.cmi.AMI_OrderQueryHandler arg0,
			OrderIdStruct arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderQuery#sendc_getOrdersForClass(com.cboe.idl.cmi.AMI_OrderQueryHandler, int)
	 */
	public void sendc_getOrdersForClass(
			com.cboe.idl.cmi.AMI_OrderQueryHandler arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderQuery#sendc_getOrdersForProduct(com.cboe.idl.cmi.AMI_OrderQueryHandler, int)
	 */
	public void sendc_getOrdersForProduct(
			com.cboe.idl.cmi.AMI_OrderQueryHandler arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderQuery#sendc_getOrdersForSession(com.cboe.idl.cmi.AMI_OrderQueryHandler, java.lang.String)
	 */
	public void sendc_getOrdersForSession(
			com.cboe.idl.cmi.AMI_OrderQueryHandler arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderQuery#sendc_getOrdersForType(com.cboe.idl.cmi.AMI_OrderQueryHandler, short)
	 */
	public void sendc_getOrdersForType(
			com.cboe.idl.cmi.AMI_OrderQueryHandler arg0, short arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderQuery#sendc_getPendingAdjustmentOrdersByClass(com.cboe.idl.cmi.AMI_OrderQueryHandler, java.lang.String, int)
	 */
	public void sendc_getPendingAdjustmentOrdersByClass(
			com.cboe.idl.cmi.AMI_OrderQueryHandler arg0, String arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderQuery#sendc_getPendingAdjustmentOrdersByProduct(com.cboe.idl.cmi.AMI_OrderQueryHandler, java.lang.String, int)
	 */
	public void sendc_getPendingAdjustmentOrdersByProduct(
			com.cboe.idl.cmi.AMI_OrderQueryHandler arg0, String arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderQuery#sendc_queryOrderHistory(com.cboe.idl.cmi.AMI_OrderQueryHandler, com.cboe.idl.cmiOrder.OrderIdStruct)
	 */
	public void sendc_queryOrderHistory(
			com.cboe.idl.cmi.AMI_OrderQueryHandler arg0, OrderIdStruct arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderQuery#sendc_subscribeOrders(com.cboe.idl.cmi.AMI_OrderQueryHandler, com.cboe.idl.cmiCallback.CMIOrderStatusConsumer, boolean)
	 */
	public void sendc_subscribeOrders(
			com.cboe.idl.cmi.AMI_OrderQueryHandler arg0,
			com.cboe.idl.cmiCallback.CMIOrderStatusConsumer arg1, boolean arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderQuery#sendc_subscribeOrdersByFirm(com.cboe.idl.cmi.AMI_OrderQueryHandler, com.cboe.idl.cmiCallback.CMIOrderStatusConsumer, boolean)
	 */
	public void sendc_subscribeOrdersByFirm(
			com.cboe.idl.cmi.AMI_OrderQueryHandler arg0,
			com.cboe.idl.cmiCallback.CMIOrderStatusConsumer arg1, boolean arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderQuery#sendc_subscribeOrdersByFirmWithoutPublish(com.cboe.idl.cmi.AMI_OrderQueryHandler, com.cboe.idl.cmiCallback.CMIOrderStatusConsumer, boolean)
	 */
	public void sendc_subscribeOrdersByFirmWithoutPublish(
			com.cboe.idl.cmi.AMI_OrderQueryHandler arg0,
			com.cboe.idl.cmiCallback.CMIOrderStatusConsumer arg1, boolean arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.OrderQuery#sendc_subscribeOrderStatusForClassV2(com.cboe.idl.cmiV2.AMI_OrderQueryHandler, int, com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer, boolean, boolean)
	 */
	public void sendc_subscribeOrderStatusForClassV2(
			AMI_OrderQueryHandler arg0, int arg1, CMIOrderStatusConsumer arg2,
			boolean arg3, boolean arg4) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.OrderQuery#sendc_subscribeOrderStatusForFirmForClassV2(com.cboe.idl.cmiV2.AMI_OrderQueryHandler, int, com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer, boolean, boolean)
	 */
	public void sendc_subscribeOrderStatusForFirmForClassV2(
			AMI_OrderQueryHandler arg0, int arg1, CMIOrderStatusConsumer arg2,
			boolean arg3, boolean arg4) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.OrderQuery#sendc_subscribeOrderStatusForFirmV2(com.cboe.idl.cmiV2.AMI_OrderQueryHandler, com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer, boolean, boolean)
	 */
	public void sendc_subscribeOrderStatusForFirmV2(AMI_OrderQueryHandler arg0,
			CMIOrderStatusConsumer arg1, boolean arg2, boolean arg3) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.OrderQuery#sendc_subscribeOrderStatusV2(com.cboe.idl.cmiV2.AMI_OrderQueryHandler, com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer, boolean, boolean)
	 */
	public void sendc_subscribeOrderStatusV2(AMI_OrderQueryHandler arg0,
			CMIOrderStatusConsumer arg1, boolean arg2, boolean arg3) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderQuery#sendc_subscribeOrdersWithoutPublish(com.cboe.idl.cmi.AMI_OrderQueryHandler, com.cboe.idl.cmiCallback.CMIOrderStatusConsumer, boolean)
	 */
	public void sendc_subscribeOrdersWithoutPublish(
			com.cboe.idl.cmi.AMI_OrderQueryHandler arg0,
			com.cboe.idl.cmiCallback.CMIOrderStatusConsumer arg1, boolean arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderQuery#sendc_unsubscribeAllOrderStatusForType(com.cboe.idl.cmi.AMI_OrderQueryHandler, short, com.cboe.idl.cmiCallback.CMIOrderStatusConsumer)
	 */
	public void sendc_unsubscribeAllOrderStatusForType(
			com.cboe.idl.cmi.AMI_OrderQueryHandler arg0, short arg1,
			com.cboe.idl.cmiCallback.CMIOrderStatusConsumer arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderQuery#sendc_unsubscribeOrderStatusByClass(com.cboe.idl.cmi.AMI_OrderQueryHandler, int, com.cboe.idl.cmiCallback.CMIOrderStatusConsumer)
	 */
	public void sendc_unsubscribeOrderStatusByClass(
			com.cboe.idl.cmi.AMI_OrderQueryHandler arg0, int arg1,
			com.cboe.idl.cmiCallback.CMIOrderStatusConsumer arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.OrderQuery#sendc_unsubscribeOrderStatusForClassV2(com.cboe.idl.cmiV2.AMI_OrderQueryHandler, int, com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer)
	 */
	public void sendc_unsubscribeOrderStatusForClassV2(
			AMI_OrderQueryHandler arg0, int arg1, CMIOrderStatusConsumer arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderQuery#sendc_unsubscribeOrderStatusForFirm(com.cboe.idl.cmi.AMI_OrderQueryHandler, com.cboe.idl.cmiCallback.CMIOrderStatusConsumer)
	 */
	public void sendc_unsubscribeOrderStatusForFirm(
			com.cboe.idl.cmi.AMI_OrderQueryHandler arg0,
			com.cboe.idl.cmiCallback.CMIOrderStatusConsumer arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.OrderQuery#sendc_unsubscribeOrderStatusForFirmForClassV2(com.cboe.idl.cmiV2.AMI_OrderQueryHandler, int, com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer)
	 */
	public void sendc_unsubscribeOrderStatusForFirmForClassV2(
			AMI_OrderQueryHandler arg0, int arg1, CMIOrderStatusConsumer arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.OrderQuery#sendc_unsubscribeOrderStatusForFirmV2(com.cboe.idl.cmiV2.AMI_OrderQueryHandler, com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer)
	 */
	public void sendc_unsubscribeOrderStatusForFirmV2(
			AMI_OrderQueryHandler arg0, CMIOrderStatusConsumer arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderQuery#sendc_unsubscribeOrderStatusForProduct(com.cboe.idl.cmi.AMI_OrderQueryHandler, int, com.cboe.idl.cmiCallback.CMIOrderStatusConsumer)
	 */
	public void sendc_unsubscribeOrderStatusForProduct(
			com.cboe.idl.cmi.AMI_OrderQueryHandler arg0, int arg1,
			com.cboe.idl.cmiCallback.CMIOrderStatusConsumer arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.OrderQuery#sendc_unsubscribeOrderStatusForSession(com.cboe.idl.cmi.AMI_OrderQueryHandler, java.lang.String, com.cboe.idl.cmiCallback.CMIOrderStatusConsumer)
	 */
	public void sendc_unsubscribeOrderStatusForSession(
			com.cboe.idl.cmi.AMI_OrderQueryHandler arg0, String arg1,
			com.cboe.idl.cmiCallback.CMIOrderStatusConsumer arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.OrderQuery#sendc_unsubscribeOrderStatusV2(com.cboe.idl.cmiV2.AMI_OrderQueryHandler, com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer)
	 */
	public void sendc_unsubscribeOrderStatusV2(AMI_OrderQueryHandler arg0,
			CMIOrderStatusConsumer arg1) {
		// TODO Auto-generated method stub

	}
}
