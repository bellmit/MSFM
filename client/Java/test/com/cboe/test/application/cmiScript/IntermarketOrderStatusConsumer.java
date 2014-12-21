package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiIntermarketCallback.CMIIntermarketOrderStatusConsumerPOA;
import com.cboe.idl.cmiIntermarketMessages.HeldOrderCancelReportStruct;
import com.cboe.idl.cmiIntermarketMessages.HeldOrderCancelRequestStruct;
import com.cboe.idl.cmiIntermarketMessages.HeldOrderDetailStruct;
import com.cboe.idl.cmiIntermarketMessages.HeldOrderFilledReportStruct;
import com.cboe.idl.cmiIntermarketMessages.OrderFillRejectStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;

public class IntermarketOrderStatusConsumer extends CMIIntermarketOrderStatusConsumerPOA
{
    public void acceptNewHeldOrder(HeldOrderDetailStruct heldOrder)
    {
        Log.message("IntermarketOrderStatusConsumer.acceptNewHeldOrder "
                + Struct.toString(heldOrder));
    }

    public void acceptCancelHeldOrderRequest(
            ProductKeysStruct productKeys,
            HeldOrderCancelRequestStruct cancelRequestStruct)
    {
        Log.message(
                "IntermarketOrderStatusConsumer.acceptCancelHeldOrderRequest "
                + " productKeys:{" + Struct.toString(productKeys)
                + "} cancelRequestStruct:{" + Struct.toString(cancelRequestStruct)
                + "}");
    }

    public void acceptHeldOrderStatus(HeldOrderDetailStruct heldOrders[])
    {
        Log.message("IntermarketOrderStatusConsumer.acceptHeldOrderStatus "
                + Struct.toString(heldOrders));
    }

    public void acceptHeldOrderCanceledReport(
            HeldOrderCancelReportStruct canceledReport)
    {
        Log.message("IntermarketOrderStatusConsumer.acceptHeldOrderCanceledReport "
                + Struct.toString(canceledReport));
    }

    public void acceptHeldOrderFilledReport(HeldOrderFilledReportStruct filledReport)
    {
        Log.message(
                "IntermarketOrderStatusConsumer.acceptHeldOrderFilledReport "
                + Struct.toString(filledReport));
    }

    public void acceptFillRejectReport(OrderFillRejectStruct orderFillReject)
    {
        Log.message("IntermarketOrderStatusConsumer.acceptFillRejectReport "
                + Struct.toString(orderFillReject));
    }
}
