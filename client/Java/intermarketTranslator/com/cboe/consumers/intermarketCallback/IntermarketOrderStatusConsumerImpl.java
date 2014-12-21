package com.cboe.consumers.intermarketCallback;

import com.cboe.application.shared.*;
import com.cboe.interfaces.intermarketCallback.*;
import com.cboe.util.*;
import com.cboe.util.channel.*;
import com.cboe.util.event.*;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.domain.util.HeldOrderCancelRequestContainer;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.idl.cmiIntermarketMessages.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiConstants.StatusUpdateReasons;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
/**
 * This is the implementation of the CMIIntermarketOrderStatusConsumer callback object which
 * receives order status data from a callback supplier on the CAS and publishes it
 * on a designated event channel.
 *
 */

public class IntermarketOrderStatusConsumerImpl implements IntermarketOrderStatusConsumer
{
    private EventChannelAdapter eventChannel = null;
    private final String Category = this.getClass().getName();

    /**
     * IntermarketOrderStatusConsumerImpl constructor.
     *
     * @param eventChannel the event channel to publish to.
     */
    public IntermarketOrderStatusConsumerImpl(EventChannelAdapter eventChannel)
    {
        super();

        this.eventChannel = eventChannel;
    }

    public void acceptNewHeldOrder(HeldOrderDetailStruct heldOrder)
    {
        ChannelKey key = null;
        ChannelEvent event = null;

        SessionKeyContainer sessionClass = new SessionKeyContainer(heldOrder.heldOrder.order.activeSession,
                                                                   heldOrder.heldOrder.order.classKey);
        key = new ChannelKey(ChannelType.CB_NEW_HELD_ORDER, sessionClass);
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, heldOrder);
        eventChannel.dispatch(event);

        publishOrderStatus(heldOrder);
    }

    public void acceptCancelHeldOrderRequest(ProductKeysStruct productKeys, HeldOrderCancelRequestStruct cancelRequestStruct )
    {
        ChannelKey key = null;
        ChannelEvent event = null;
        int classKey = productKeys.classKey;
        SessionKeyContainer sessionClass = new SessionKeyContainer(cancelRequestStruct.cancelRequest.sessionName, classKey);
        HeldOrderCancelRequestContainer heldOrderCancelRequestContainer = new HeldOrderCancelRequestContainer(productKeys, cancelRequestStruct);
        key = new ChannelKey(ChannelType.CB_CANCEL_HELD_ORDER_REQUEST, sessionClass);
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, heldOrderCancelRequestContainer);
        eventChannel.dispatch(event);
    }

    private void publishOrderStatus(HeldOrderDetailStruct heldOrder)
    {
        ChannelKey key = null;
        ChannelEvent event = null;

        HeldOrderDetailStruct[] orders =  {heldOrder};

        // this channel is not keyed.
        key = new ChannelKey(ChannelType.CB_HELD_ORDERS, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, orders);
        eventChannel.dispatch(event);

        key = new ChannelKey(ChannelType.CB_HELD_ORDERS,
                             new SessionKeyContainer(heldOrder.heldOrder.order.activeSession, heldOrder.heldOrder.order.classKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, orders);
        eventChannel.dispatch(event);
    }

    public void acceptHeldOrderStatus(HeldOrderDetailStruct[] orders)
    {
        for (int i = 0; i < orders.length; i++)
        {
            publishOrderStatus(orders[i]);
        }
    }

    public void acceptHeldOrderCanceledReport(HeldOrderCancelReportStruct canceledReport )
    {
        ChannelKey key = null;
        ChannelEvent event = null;

        SessionKeyContainer sessionClass = new SessionKeyContainer(canceledReport.heldOrderDetail.heldOrder.order.activeSession,
                                                                   canceledReport.heldOrderDetail.heldOrder.order.classKey);
        key = new ChannelKey(ChannelType.CB_HELD_ORDER_CANCELED_REPORT, sessionClass);
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, canceledReport);
        eventChannel.dispatch(event);

        HeldOrderDetailStruct heldOrder = new HeldOrderDetailStruct();
        heldOrder.productInformation = canceledReport.heldOrderDetail.productInformation;
        heldOrder.statusChange = StatusUpdateReasons.CANCEL;
        heldOrder.heldOrder = canceledReport.heldOrderDetail.heldOrder;
        publishOrderStatus(heldOrder);
    }

    public void acceptHeldOrderFilledReport(HeldOrderFilledReportStruct filledReport )
    {
        ChannelKey key = null;
        ChannelEvent event = null;

        SessionKeyContainer sessionClass = new SessionKeyContainer(filledReport.heldOrderDetail.heldOrder.order.activeSession,
                                                                   filledReport.heldOrderDetail.heldOrder.order.classKey);
        key = new ChannelKey(ChannelType.CB_HELD_ORDER_FILLED_REPORT, sessionClass);
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, filledReport);
        eventChannel.dispatch(event);

        HeldOrderDetailStruct heldOrder = new HeldOrderDetailStruct();
        heldOrder.productInformation = filledReport.heldOrderDetail.productInformation;
        heldOrder.statusChange = StatusUpdateReasons.FILL;
        heldOrder.heldOrder = filledReport.heldOrderDetail.heldOrder;
        publishOrderStatus(heldOrder);
    }

    public void acceptFillRejectReport(OrderFillRejectStruct orderFillReject)
    {
        ChannelKey key = null;
        ChannelEvent event = null;

        SessionKeyContainer sessionClass = new SessionKeyContainer(orderFillReject.rejectedFillOrder.orderStruct.activeSession,
                                                                   orderFillReject.rejectedFillOrder.orderStruct.classKey);
        key = new ChannelKey(ChannelType.CB_FILL_REJECT_REPORT, sessionClass);
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, orderFillReject);
        eventChannel.dispatch(event);
    }

}
