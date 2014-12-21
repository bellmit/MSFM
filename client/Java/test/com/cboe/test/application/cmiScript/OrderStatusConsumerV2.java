package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumerPOA;
import com.cboe.idl.cmiOrder.OrderBustReportStruct;
import com.cboe.idl.cmiOrder.OrderBustReinstateReportStruct;
import com.cboe.idl.cmiOrder.OrderCancelReportStruct;
import com.cboe.idl.cmiOrder.OrderDetailStruct;
import com.cboe.idl.cmiOrder.OrderFilledReportStruct;

public class OrderStatusConsumerV2 extends CMIOrderStatusConsumerPOA
{
    public void acceptOrderStatus(OrderDetailStruct odseq[], int queueDepth)
    {
        Log.message("OrderStatusConsumerV2.acceptOrderStatus "
                + Struct.toString(odseq) + " queueDepth:" + queueDepth);
    }

    public void acceptOrderCanceledReport(
            OrderCancelReportStruct ocr, int queueDepth)
    {
        Log.message("OrderStatusConsumerV2.acceptOrderCancelReport "
                + Struct.toString(ocr) + " queueDepth:" + queueDepth);
    }

    public void acceptOrderFilledReport(
            OrderFilledReportStruct ofr, int queueDepth)
    {
        Log.message("OrderStatusConsumerV2.acceptOrderFilledReport "
                + Struct.toString(ofr) + " queueDepth:" + queueDepth);
    }

    public void acceptOrderBustReport(OrderBustReportStruct obr, int queueDepth)
    {
        Log.message("OrderStatusConsumerV2.acceptOrderBusReport "
                + Struct.toString(obr) + " queueDepth:" + queueDepth);
    }

    public void acceptOrderBustReinstateReport(
            OrderBustReinstateReportStruct obrr, int queueDepth)
    {
        Log.message("OrderStatusConsumerV2.acceptOrderBustReinstateReport "
                + Struct.toString(obrr) + " queueDepth:" + queueDepth);
    }

    public void acceptNewOrder(OrderDetailStruct od, int queueDepth)
    {
        Log.message("OrderStatusConsumerV2.acceptNewOrder "
                + Struct.toString(od) + " queueDepth:" + queueDepth);
    }
}
