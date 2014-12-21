package com.cboe.interfaces.application.inprocess;

import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiProduct.ProductStruct;

/**
 * @author Jing Chen
 */
public interface OrderStatusConsumer
{
    public void acceptOrderStatus(OrderStruct order, ProductStruct product, short statusChange, int queueDepth);
    public void acceptOrderCanceledReport(OrderStruct order, ProductStruct product, short statusChange, CancelReportStruct[] cancelReports, int queueDepth);
    public void acceptOrderFilledReport(OrderStruct order, ProductStruct product, short statusChange, FilledReportStruct[] filledReports, int queueDepth);
    public void acceptOrderBustReport(OrderStruct order, ProductStruct product, short statusChange, BustReportStruct[] bustReports, int queueDepth);
    public void acceptOrderBustReinstateReport(OrderStruct order, ProductStruct product, short statusChange, BustReinstateReportStruct bustReinstatedReport, int queueDepth);
    public void acceptNewOrder(OrderStruct order, ProductStruct product, short statusChange, int queueDepth);
    public void acceptOrderStatusUpdate(OrderStruct order, ProductStruct product, short statusChange, int queueDepth);

    // handle exception 
    public void acceptConsumerException(OrderStruct order, String text, short statusChange, int queueDepth);
}
