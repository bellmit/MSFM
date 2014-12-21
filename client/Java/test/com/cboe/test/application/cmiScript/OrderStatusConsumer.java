package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiCallback.CMIOrderStatusConsumerPOA;
import com.cboe.idl.cmiOrder.OrderBustReportStruct;
import com.cboe.idl.cmiOrder.OrderBustReinstateReportStruct;
import com.cboe.idl.cmiOrder.OrderCancelReportStruct;
import com.cboe.idl.cmiOrder.OrderDetailStruct;
import com.cboe.idl.cmiOrder.OrderFilledReportStruct;

public class OrderStatusConsumer extends CMIOrderStatusConsumerPOA
{
    public void acceptOrderStatus(OrderDetailStruct qdseq[])
    {
        Log.message("OrderStatusConsumer.acceptOrderStatus "
                + Struct.toString(qdseq));
    }

    public void acceptOrderCanceledReport(OrderCancelReportStruct ocr)
    {
        Log.message("OrderStatusConsumer.acceptOrderCancelReport "
                + Struct.toString(ocr));
    }

    public void acceptOrderFilledReport(OrderFilledReportStruct ofr)
    {
        Log.message("OrderStatusConsumer.acceptOrderFilledReport "
                + Struct.toString(ofr));
    }

    public void acceptOrderBustReport(OrderBustReportStruct obr)
    {
        Log.message("OrderStatusConsumer.acceptOrderBusReport "
                + Struct.toString(obr));
    }

    public void acceptOrderBustReinstateReport(
            OrderBustReinstateReportStruct obrr)
    {
        Log.message("OrderStatusConsumer.acceptOrderBustReinstateReport "
                + Struct.toString(obrr));
    }

    public void acceptNewOrder(OrderDetailStruct od)
    {
        Log.message("OrderStatusConsumer.acceptNewOrder "
                + Struct.toString(od));
    }
}
