package com.cboe.interfaces.application;

/**
 *
 * @author Jeff Illian
 *
 */
import com.cboe.idl.cmiOrder.*;

public interface OrderStatusCollector {
  public void acceptOrderFillReport(OrderStruct order, FilledReportStruct[] filledOrder, short statusChange) ;

  public void acceptCancelReport(OrderStruct order, CancelReportStruct[] cancelReport, short statusChange) ;

  public void acceptOrderAcceptedByBook(OrderStruct order) ;

  public void acceptOrderUpdate(OrderStruct updatedOrder) ;

  public void acceptOrderStatusUpdate(OrderStruct updatedOrder, short statusChange);

  public void acceptNewOrder(OrderStruct newOrder, short statusChange) ;

  public void acceptOrders(OrderStruct[] orders) ;

  public void acceptException(String description) ;

  public void acceptOrderBustReport(OrderStruct order, BustReportStruct[] bustedOrder, short statusChange) ;

  public void acceptOrderBustReinstateReport(OrderStruct order, BustReinstateReportStruct bustReinstatedOrder, short statusChange) ;

}
