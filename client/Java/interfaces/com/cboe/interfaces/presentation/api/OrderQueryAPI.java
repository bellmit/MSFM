package com.cboe.interfaces.presentation.api;

import com.cboe.exceptions.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiTraderActivity.*;
import com.cboe.util.event.*;
import com.cboe.interfaces.presentation.order.OrderId;
import com.cboe.interfaces.presentation.order.Order;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.domain.Price;

import java.util.List;

public interface OrderQueryAPI
{
    /**
     * Initializes callback listener to CAS
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void initializeOrderCallbackListener()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Gets a trader's orders for the given product and
     * subscribes the client listener to receive continued order data for the given
     * product.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return a sequence of order detail structs for the given product key.
     * @param productKey the product key to retrieve orders for.
     * @param clientListener the client listener to subscribe for continued order data.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public OrderDetailStruct[] getOrdersForProduct(int productKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public OrderDetailStruct[] getOrdersForSession(String sessionName, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * getOrderById gets the requested order.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return an order detail struct containing the requested order.
     * @param orderId the order id of the order to retrieve.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     */
    public OrderDetailStruct getOrderById(OrderIdStruct orderId)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    /**
     * Gets order detail information from an order id.
     *
     * @return OderDetailStruct
     *
     * @param orderId the order id to query for.
     * @param cacheOnly if true only tries to find the order in the local order cache
     *
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     */
    public OrderDetailStruct getOrderById(OrderIdStruct orderId, boolean cacheOnly)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    
    /**
     * publishAllOrdersForFirm publishes orders for all firm members to the given listener.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return void
     * @param clientListener the event channel listener receiving all published orders.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
//    public void subscribeOrdersByFirm(EventChannelListener clientListener)
//           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * publishAllOrders publishes all the users orders to the given listener.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return void
     * @param clientListener the event channel listener receiving all published orders.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void subscribeOrders(EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Gets all the traders's orders for the given product
     * type and subscribes the client listener for continued order data.
     *
     * @return a sequence of order detail structs containing the trader's orders.
     * @param productType the product type to retrieve order data for.
     * @param clientListener the client listener to subscribe for continued order data.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public OrderDetailStruct[] getAllOrdersForType(short productType, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Gets all the trader's orders for the given class and
     * subscribes the the client listener to receive continued order data.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return a sequence of order detail structs for orders based on the given class.
     * @param classKey the class key to get orders for.
     * @param clientListener the client listener to subscribe for continued order data.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public OrderDetailStruct[] getOrdersByClass(int classKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Subscribes the client listener for cancel reports.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param clientListener the client listener to subscribe for cancel reports
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void subscribeOrderCanceledReport(EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Unsubscribes the client listener for cancel reports.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param clientListener the client listener to unsubscribe for cancel reports
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void unsubscribeOrderCanceledReport(EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Subscribes the client listener to receive order
     * filled report information.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param clientListener the client listener to receive continued order filled
     *        reports.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void subscribeOrderFilledReport(EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Unsubscribes the client listener from receiving order filled reports.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param clientListener the client listener to unsubscribe.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void unsubscribeOrderFilledReport(EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Subscribes the client listener for cancel reports for the current firm.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param clientListener the client listener to subscribe for cancel reports
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
//    public void subscribeOrderCanceledReportForFirm(EventChannelListener clientListener)
//           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Unsubscribes the client listener for cancel reports for the current firm.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param clientListener the client listener to unsubscribe for cancel reports
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
//    public void unsubscribeOrderCanceledReportForFirm(EventChannelListener clientListener)
//           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Subscribes the client listener to receive all order filled reports for
     * the current firm.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param clientListener the client listener to unsubscribe.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
//    public void subscribeOrderFilledReportForFirm(EventChannelListener clientListener)
//           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Unsubscribes the client listener from receiving all order filled reports
     * for the current firm.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param clientListener the client listener to unsubscribe.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
//    public void unsubscribeOrderFilledReportForFirm(EventChannelListener clientListener)
//           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Gets the order history for the given order id.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return the orders history.
     * @param orderId the order id to get historical information for.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public ActivityHistoryStruct queryOrderHistory(OrderIdStruct orderId)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    /**
     * Gets all pending adjustment orders for the given product key.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return a sequence of pending order structs for product key.
     * @param productKey the product key to get pending adjustment data for.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public PendingOrderStruct[] getPendingAdjustmentOrdersByProduct(String sessionName, int productKey)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Gets all pending adjustment orders for
     * the given sequence of class keys.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return a sequence of pending order structs for any order based on the given
     *         sequence of class keys.
     * @param classKeys the sequence of class keys to get pending order data for.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public PendingOrderStruct[] getPendingAdjustmentOrdersByClass(String sessionName, int classKey)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Unsubscribes the client listener from receiving all order status for the
     * given product.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return void
     * @param productKey the product to unsubscribe order status from.
     * @param clientListener the client listener to unsubscribe.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void unsubscribeOrderStatusForProduct(int productKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Unsubscribes the client listener from receiving all order status for the
     * current firm.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return void
     * @param clientListener the client listener to unsubscribe.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
//    public void unsubscribeOrderStatusForFirm(EventChannelListener clientListener)
//           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Unsubscribes the client listener from receiving all order status for the
     * the given product type.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return void
     * @param productType the product type to unsubscribe order status from.
     * @param clientListener the client listener to unsubscribe.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void unsubscribeAllOrderStatusForType(short productType, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Unsubscribes the client listener from receiving all order status for the
     * the given class.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return void
     * @param class the class to unsubscribe order status from.
     * @param clientListener the client listener to unsubscribe.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void unsubscribeOrderStatusByClass(int classKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void unsubscribeOrderStatusBySession(String sessionName, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

               /**
     * @description retrieves all cached orders and register the event channel listener
     * @param clientListener the listener to subscribe
     * @returns OrderDetailStruct
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public OrderDetailStruct[] getAllOrders(EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;


    /**
     * Unsubscribes the clientlistener from receiving all order events.
     * @param clientListener the unsubscribing listener.
     */
    public void unsubscribeAllOrders(EventChannelListener clientListener);

    /**
     * Subscribes the client listener to receive OrderFilledReport info
     *
     * @param clientListener the client listener to subscribe for receiving data.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public OrderFilledReportStruct[] getOrderFilledReports(EventChannelListener listener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Subscribes the client listener to receive order
     * bust and reinstate report information.
     *
     * @author Connie Feng
     *
     * @param clientListener the client listener to receive continued order bust
     *        reports.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void subscribeOrderBustReport(EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    /**
     * Subscribes the client listener to receive order
     * bust and reinstate report information for firm.
     *
     * @author Connie Feng
     *
     * @param clientListener the client listener to receive continued order bust
     *        reports.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
//    public void subscribeOrderBustReportForFirm(EventChannelListener clientListener)
//           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    /**
     * Unsubscribes the client listener to receive order
     * bust and reinstate report information.
     *
     * @author Connie Feng
     *
     * @param clientListener the client listener to unsubscribe continued order bust
     *        reports.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void unsubscribeOrderBustReport(EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Unsubscribes the client listener to receive order
     * bust and reinstate report information for firm.
     *
     * @author Connie Feng
     *
     * @param clientListener the client listener to unsubscribe continued order bust
     *        reports.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
//    public void unsubscribeOrderBustReportForFirm(EventChannelListener clientListener)
//           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Subscribes the client listener to receive OrderBustReport info
     *
     * @param clientListener the client listener to subscribe for receiving data.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public OrderBustReportStruct[] getOrderBustReports(EventChannelListener listener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;


    /**
     * UbSubscribes Orders by the Firm Type
     *
     * @param clientListener the client listener to subscribe for receiving data.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
//    public void unsubscribeOrdersByFirm (EventChannelListener clientListener)
//           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public OrderFilledReportStruct[] getOrderFilledReportsByOrderId(OrderId orderId);
    public int getOrderFilledReportsCountByOrderId(OrderId orderId);

    /**
     * @param product
     * @param quantity
     * @param price
     * @param side
     * @param millis millisecond time range
     * @return return List of matching orders that have been entered within the specified time range
     */
    List<OrderDetailStruct> getOrders(SessionProduct product, int quantity, Price price, char side, int millis);
}
