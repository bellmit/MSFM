package com.cboe.consumers.eventChannel;

import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.util.ChannelKey;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.interfaces.events.IntermarketOrderStatusConsumer;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiIntermarketMessages.*;
import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapterFactory;
import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapter;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.domain.util.HeldOrderCancelReportContainer;
import com.cboe.domain.util.HeldOrderFilledReportContainer;
import com.cboe.domain.util.HeldOrderCancelRequestContainer;


public class IntermarketOrderStatusConsumerIECImpl extends BObject implements IntermarketOrderStatusConsumer {
    private InstrumentedEventChannelAdapter internalEventChannel = null;
    /**
     * constructor comment.
     */
    public IntermarketOrderStatusConsumerIECImpl() {
        super();
        internalEventChannel = InstrumentedEventChannelAdapterFactory.find();
    }

    public void acceptCancelHeldOrder(int[] groups, ProductKeysStruct productKeys, HeldOrderCancelRequestStruct cancelRequest)
    {
         int classKey =  productKeys.classKey;
         if (Log.isDebugOn())
         {
             Log.debug(this, "event received -> CancelHeldOrder : classkey :  " + classKey + " session: " + cancelRequest.cancelRequest.sessionName);
         }

         ChannelKey channelKey = new ChannelKey(ChannelKey.CANCEL_HELD_ORDER,
                                     new SessionKeyContainer(cancelRequest.cancelRequest.sessionName, classKey));
         HeldOrderCancelRequestContainer cancelRequestContainer = new HeldOrderCancelRequestContainer(productKeys, cancelRequest);
         ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, cancelRequestContainer);
         internalEventChannel.dispatch(event);
     }

    public void acceptFillRejectReport(int[] groups, FillRejectStruct[] fillReject)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> FillRejectReport : classkey :  " + fillReject[0].order.classKey );
        }
        ChannelKey channelKey = new ChannelKey(ChannelKey.FILL_REJECT_REPORT,
                                    new SessionKeyContainer(fillReject[0].order.activeSession, fillReject[0].order.classKey));
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, fillReject);
        internalEventChannel.dispatch(event);
    };

    public void acceptHeldOrders(int[] groups,
                  String sessionName,
                  int classKey,
                  HeldOrderStruct[] heldOrders)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> HeldOrders : classkey :  "
                    + classKey );
        }

        ChannelKey channelKey = new ChannelKey(ChannelKey.HELD_ORDERS, new SessionKeyContainer(sessionName, classKey));
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, heldOrders);
        internalEventChannel.dispatch(event);
     };

    public void acceptNewHeldOrder(int[] groups, HeldOrderStruct heldOrder)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> NewHeldOrder : classkey :  "
                    + heldOrder.order.classKey );
        }

        ChannelKey channelKey = new ChannelKey(ChannelKey.NEW_HELD_ORDER, new SessionKeyContainer(heldOrder.order.activeSession, heldOrder.order.classKey ));
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, heldOrder);
        internalEventChannel.dispatch(event);
    };

    public void acceptHeldOrderStatus(int[] groups,
                       HeldOrderStruct heldOrder)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> HeldOrderStatus : productKey :  "  + heldOrder.order.productKey );
        }

        ChannelKey channelKey = new ChannelKey(ChannelKey.HELD_ORDER_STATUS, new SessionKeyContainer(heldOrder.order.activeSession, heldOrder.order.classKey));
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, heldOrder);
        internalEventChannel.dispatch(event);
    };

    public void acceptHeldOrderCancelReport(int[] groups,
                           HeldOrderStruct heldOrder,
                           CboeIdStruct cancelRequestId,
                           CancelReportStruct cancelReport )
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> HeldOrderCancelReport : classkey :  "
                    + heldOrder.order.classKey );
        }

        HeldOrderCancelReportContainer cancelReportContainer = new HeldOrderCancelReportContainer(heldOrder, cancelRequestId, cancelReport);
        ChannelKey channelKey = new ChannelKey(ChannelKey.HELD_ORDER_CANCEL_REPORT, new SessionKeyContainer(heldOrder.order.activeSession, heldOrder.order.classKey));
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, cancelReportContainer);
        internalEventChannel.dispatch(event);
    };

    public void acceptHeldOrderFilledReport(int[] groups,
                    HeldOrderStruct heldOrder,
                    FilledReportStruct[] filledOrder)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> HeldOrderFillReport : classkey :  "
                    + heldOrder.order.classKey + getOrderIdString(heldOrder.order.orderId));
        }

        HeldOrderFilledReportContainer heldOrderFilledContainer = new HeldOrderFilledReportContainer(heldOrder, filledOrder);
        ChannelKey channelKey = new ChannelKey(ChannelKey.HELD_ORDER_FILLED_REPORT, new SessionKeyContainer(heldOrder.order.activeSession, heldOrder.order.classKey));
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, heldOrderFilledContainer);
        internalEventChannel.dispatch(event);
    }


    private String getOrderIdString(OrderIdStruct orderId)
    {
        StringBuilder oid = new StringBuilder(55);
        oid.append(":oid=").append(orderId.executingOrGiveUpFirm).append(":")
           .append(orderId.branch).append(":").append(orderId.branchSequenceNumber)
           .append(": h=").append(orderId.highCboeId).append(": l=").append(orderId.lowCboeId);
        return oid.toString();
    }

}
