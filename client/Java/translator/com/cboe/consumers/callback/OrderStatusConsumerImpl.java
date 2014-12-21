package com.cboe.consumers.callback;

import com.cboe.application.shared.*;
import com.cboe.interfaces.callback.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.util.*;
import com.cboe.util.channel.*;
import com.cboe.util.event.*;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;

/**
 * This is the implementation of the CMIOrderStatusConsumer callback object which
 * receives order status data from a callback supplier on the CAS and publishes it
 * on a designated event channel.
 *
 * @author Derek T. Chambers-Boucher
 * @version 03/16/1999
 */

public class OrderStatusConsumerImpl implements OrderStatusConsumer
{
    private EventChannelAdapter eventChannel = null;
    private final String Category = this.getClass().getName();

    /**
     * OrderStatusConsumerImpl constructor.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param channelType the channel type to publish on.
     * @param eventChannel the event channel to publish to.
     */
    public OrderStatusConsumerImpl(EventChannelAdapter eventChannel)
    {
        super();

        this.eventChannel = eventChannel;
    }

    protected void publishOrderStatus(OrderDetailStruct order)
    {
        ChannelKey key = null;
        ChannelEvent event = null;

        OrderDetailStruct[] orders =  {order};

        // this channel is not keyed.
        key = new ChannelKey(ChannelType.CB_ALL_ORDERS, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, orders);
        eventChannel.dispatch(event);

        // keyed by productKey.
        key = new ChannelKey(ChannelType.CB_ORDERS_FOR_PRODUCT, new Integer(order.orderStruct.productKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, orders);
        eventChannel.dispatch(event);

        // keyed by product type.
        key = new ChannelKey(ChannelType.CB_ALL_ORDERS_FOR_TYPE, new Integer(order.orderStruct.productType));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, orders);
        eventChannel.dispatch(event);

        // keyed by product class.
        key = new ChannelKey(ChannelType.CB_ORDERS_BY_CLASS, new Integer(order.orderStruct.classKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, orders);
        eventChannel.dispatch(event);

        // keyed by product class.
        key = new ChannelKey(ChannelType.CB_ORDERS_FOR_SESSION, order.orderStruct.activeSession);
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, orders);
        eventChannel.dispatch(event);

        // keyed by firm key.
        key = new ChannelKey(ChannelType.CB_ORDERS_BY_FIRM, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, orders);
        eventChannel.dispatch(event);
    }

    /**
     * The callback method used by the CAS to publish order status data.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param statusChange the status change that occurred.
     * @param orders the order status data to publish to all subscribed listeners
     */
    public void acceptOrderStatus(OrderDetailStruct[] orders)
    {
        for (int i = 0; i < orders.length; i++)
        {
            publishOrderStatus(orders[i]);
        }
    }

    /**
     * The callback method used by the CAS to publish order status data.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param statusChange the status change that occurred.
     * @param orders the order status data to publish to all subscribed listeners
     */
    public void acceptOrderCanceledReport(OrderCancelReportStruct canceledReport)
    {
        ChannelKey key = null;
        ChannelEvent event = null;

        // This channel is not keyed.
        key = new ChannelKey(ChannelType.CB_CANCELED_REPORT, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, canceledReport);
        eventChannel.dispatch(event);

        // This channel is keyed by memberKey.
        key = new ChannelKey(ChannelType.CB_CANCELED_REPORT_BY_FIRM, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, canceledReport);
        eventChannel.dispatch(event);

        // This channel is keyed by memberKey.
        key = new ChannelKey(ChannelType.CB_CANCELED_REPORT_FOR_PRODUCT, new Integer(canceledReport.cancelledOrder.orderStruct.productKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, canceledReport);
        eventChannel.dispatch(event);

        // This channel is keyed by memberKey.
        key = new ChannelKey(ChannelType.CB_CANCELED_REPORT_FOR_TYPE, new Integer(canceledReport.cancelledOrder.orderStruct.productType));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, canceledReport);
        eventChannel.dispatch(event);

        // This channel is keyed by memberKey.
        key = new ChannelKey(ChannelType.CB_CANCELED_REPORT_BY_CLASS, new Integer(canceledReport.cancelledOrder.orderStruct.classKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, canceledReport);
        eventChannel.dispatch(event);

        // This channel is keyed by memberKey.
        key = new ChannelKey(ChannelType.CB_CANCELED_REPORT_FOR_SESSION, canceledReport.cancelledOrder.orderStruct.activeSession);
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, canceledReport);
        eventChannel.dispatch(event);

        OrderDetailStruct order = new OrderDetailStruct();
        order.orderStruct = canceledReport.cancelledOrder.orderStruct;
        order.productInformation = canceledReport.cancelledOrder.productInformation;
        order.statusChange = StatusUpdateReasons.CANCEL;
        publishOrderStatus(order);
    }

    /**
     * The callback method used by the CAS to publish order status data.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param statusChange the status change that occurred.
     * @param orders the order status data to publish to all subscribed listeners
     */
    public void acceptOrderFilledReport(OrderFilledReportStruct filledReport)
    {
        ChannelKey key = null;
        ChannelEvent event = null;

        if(GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(this.getClass()+" acceptOrderFilledReport", GUILoggerBusinessProperty.ORDER_QUERY, filledReport);
        }

        // This channel is not keyed.
        key = new ChannelKey(ChannelType.CB_FILLED_REPORT, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, filledReport);
        eventChannel.dispatch(event);

        // This channel is keyed by memberKey.
        key = new ChannelKey(ChannelType.CB_FILLED_REPORT_BY_FIRM, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, filledReport);
        eventChannel.dispatch(event);


        // This channel is keyed by product key.
        key = new ChannelKey(ChannelType.CB_FILLED_REPORT_FOR_PRODUCT, new Integer(filledReport.filledOrder.orderStruct.productKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, filledReport);
        eventChannel.dispatch(event);

        // This channel is keyed by product type.
        key = new ChannelKey(ChannelType.CB_FILLED_REPORT_FOR_TYPE, new Integer(filledReport.filledOrder.orderStruct.productType));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, filledReport);
        eventChannel.dispatch(event);

        // This channel is keyed by memberKey.
        key = new ChannelKey(ChannelType.CB_FILLED_REPORT_BY_CLASS, new Integer(filledReport.filledOrder.orderStruct.classKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, filledReport);
        eventChannel.dispatch(event);

        // This channel is keyed by memberKey.
        key = new ChannelKey(ChannelType.CB_FILLED_REPORT_FOR_SESSION, filledReport.filledOrder.orderStruct.activeSession);
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, filledReport);
        eventChannel.dispatch(event);


        OrderDetailStruct order = new OrderDetailStruct();
        order.orderStruct = filledReport.filledOrder.orderStruct;
        order.productInformation = filledReport.filledOrder.productInformation;
        order.statusChange = StatusUpdateReasons.FILL;
        publishOrderStatus(order);
    }

    /**
     * The callback method used by the CAS to publish order status data.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param statusChange the status change that occurred.
     * @param orders the order status data to publish to all subscribed listeners
     */
    public void acceptOrderBustReport(OrderBustReportStruct bustReport)
    {
        ChannelKey key = null;
        ChannelEvent event = null;

        // This channel is not keyed.
        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, bustReport);
        eventChannel.dispatch(event);

        // This channel is keyed by memberKey.
        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_BY_FIRM, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, bustReport);
        eventChannel.dispatch(event);


        // This channel is keyed by product key.
        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_FOR_PRODUCT, new Integer(bustReport.bustedOrder.orderStruct.productKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, bustReport);
        eventChannel.dispatch(event);

        // This channel is keyed by product type.
        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_FOR_TYPE, new Integer(bustReport.bustedOrder.orderStruct.productType));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, bustReport);
        eventChannel.dispatch(event);

        // This channel is keyed by memberKey.
        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_BY_CLASS, new Integer(bustReport.bustedOrder.orderStruct.classKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, bustReport);
        eventChannel.dispatch(event);

        // This channel is keyed by memberKey.
        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_SESSION, bustReport.bustedOrder.orderStruct.activeSession);
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, bustReport);
        eventChannel.dispatch(event);


        OrderDetailStruct order = new OrderDetailStruct();
        order.orderStruct = bustReport.bustedOrder.orderStruct;
        order.productInformation = bustReport.bustedOrder.productInformation;
        order.statusChange = StatusUpdateReasons.BUST;
        publishOrderStatus(order);
    }

    /**
     * The callback method used by the CAS to publish order status data.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param statusChange the status change that occurred.
     * @param orders the order status data to publish to all subscribed listeners
     */
    public void acceptOrderBustReinstateReport(OrderBustReinstateReportStruct reinstateReport)
    {
        ChannelKey key = null;
        ChannelEvent event = null;

        // This channel is not keyed.
        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, reinstateReport);
        eventChannel.dispatch(event);

        // This channel is keyed by memberKey.
        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_BY_FIRM, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, reinstateReport);
        eventChannel.dispatch(event);


        // This channel is keyed by product key.
        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_PRODUCT, new Integer(reinstateReport.reinstatedOrder.orderStruct.productKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, reinstateReport);
        eventChannel.dispatch(event);

        // This channel is keyed by product type.
        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_TYPE, new Integer(reinstateReport.reinstatedOrder.orderStruct.productType));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, reinstateReport);
        eventChannel.dispatch(event);

        // This channel is keyed by memberKey.
        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_BY_CLASS, new Integer(reinstateReport.reinstatedOrder.orderStruct.classKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, reinstateReport);
        eventChannel.dispatch(event);

        // This channel is keyed by memberKey.
        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_SESSION, reinstateReport.bustReinstatedReport.sessionName);
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, reinstateReport);
        eventChannel.dispatch(event);

        OrderDetailStruct order = new OrderDetailStruct();
        order.orderStruct = reinstateReport.reinstatedOrder.orderStruct;
        order.productInformation = reinstateReport.reinstatedOrder.productInformation;
        order.statusChange = StatusUpdateReasons.REINSTATE;
        publishOrderStatus(order);
    }

    /**
     * The callback method used by the CAS to publish order status data.
     *
     * @author Keith A. Korecky
     *
     * @param OrderDetailStruct order
     */
    public void acceptNewOrder( OrderDetailStruct order )
    {
        ChannelKey key = null;
        ChannelEvent event = null;

        key = new ChannelKey(ChannelType.CB_NEW_ORDER_REPORT, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, order);
        eventChannel.dispatch(event);

        key = new ChannelKey(ChannelType.CB_NEW_ORDER_REPORT_BY_FIRM, order.orderStruct.orderId.executingOrGiveUpFirm);
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, order);
        eventChannel.dispatch(event);

        key = new ChannelKey(ChannelType.CB_NEW_ORDER_REPORT_FOR_PRODUCT, new Integer(order.orderStruct.productKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, order);
        eventChannel.dispatch(event);

        key = new ChannelKey(ChannelType.CB_NEW_ORDER_REPORT_BY_CLASS, new Integer(order.orderStruct.classKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, order);
        eventChannel.dispatch(event);

        key = new ChannelKey(ChannelType.CB_NEW_ORDER_REPORT_FOR_TYPE, new Integer(order.orderStruct.productType));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, order);
        eventChannel.dispatch(event);

        key = new ChannelKey(ChannelType.CB_NEW_ORDER_REPORT_FOR_SESSION, order.orderStruct.activeSession);
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, order);
        eventChannel.dispatch(event);

// status should already be contained in the OrderDetailStruct
//
//            order.statusChange = StatusUpdateReasons.NEW;
        publishOrderStatus(order);
    }
}
