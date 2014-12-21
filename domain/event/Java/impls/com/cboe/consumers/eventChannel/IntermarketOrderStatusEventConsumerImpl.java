
package com.cboe.consumers.eventChannel;

/**
 * @author Emily Huang
 */
import com.cboe.interfaces.events.*;
import com.cboe.util.event.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiIntermarketMessages.HeldOrderStruct;
import com.cboe.idl.cmiIntermarketMessages.FillRejectStruct;
import com.cboe.idl.cmiIntermarketMessages.HeldOrderCancelRequestStruct;
import com.cboe.util.*;
import com.cboe.infrastructureServices.foundationFramework.*;

public class IntermarketOrderStatusEventConsumerImpl extends com.cboe.idl.events.POA_IntermarketOrderStatusEventConsumer
        implements IntermarketOrderStatusConsumer {

    private IntermarketOrderStatusConsumer delegate;

    /**
     * constructor comment.
     */
    public IntermarketOrderStatusEventConsumerImpl(IntermarketOrderStatusConsumer imOrderStatusConsumer) {
        super();
        delegate = imOrderStatusConsumer;
    }
    public void acceptCancelHeldOrder(int[] groups, ProductKeysStruct productKeys, HeldOrderCancelRequestStruct cancelRequest) {
        delegate.acceptCancelHeldOrder(groups, productKeys, cancelRequest);
    }
//    public void acceptCancelHeldOrder(int[] groups, int classKey, HeldOrderCancelRequestStruct cancelRequest) {
//        delegate.acceptCancelHeldOrder(groups, classKey, cancelRequest);
//    }

    public void acceptFillRejectReport(int[] groups, FillRejectStruct[] fillReject)
    {
        delegate.acceptFillRejectReport(groups, fillReject);
    };

    public void acceptHeldOrders(int[] groups,
                  String sessionName,
                  int classKey,
                  HeldOrderStruct[] heldOrders)
    {
        delegate.acceptHeldOrders(groups, sessionName, classKey, heldOrders);
    };

    public void acceptNewHeldOrder(int[] groups, HeldOrderStruct heldOrder)
    {
        delegate.acceptNewHeldOrder(groups, heldOrder);
    };

    public void acceptHeldOrderStatus(int[] groups, HeldOrderStruct order)
    {
        delegate.acceptHeldOrderStatus(groups, order);
    };

    public void acceptHeldOrderCancelReport(int[] groups,
                           HeldOrderStruct heldOrder,
                           CboeIdStruct cancelRequestId,
                           CancelReportStruct cancelReport )
    {
        delegate.acceptHeldOrderCancelReport(groups, heldOrder, cancelRequestId, cancelReport);
    };

    public void acceptHeldOrderFilledReport(int[] groups,
                    HeldOrderStruct heldOrder,
                    FilledReportStruct[] filledOrder)
    {
        delegate.acceptHeldOrderFilledReport(groups, heldOrder, filledOrder);
    }


    public org.omg.CORBA.Object get_typed_consumer() {
        return null;
    }

    public void push(org.omg.CORBA.Any data)
    throws org.omg.CosEventComm.Disconnected {
    }

    public void disconnect_push_consumer() {
    }
}
