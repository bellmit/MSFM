package com.cboe.application.shared.consumer;

import com.cboe.interfaces.application.*;
/**
 * @author Jeff Illian
 */

import com.cboe.util.event.*;
import com.cboe.util.channel.*;
import com.cboe.domain.util.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.util.ChannelType;
import com.cboe.util.ChannelKey;

public class OrderStatusProcessor implements EventChannelListener {
    private OrderStatusCollector parent = null;

  /**
   * @author Jeff Illian
   */
  public OrderStatusProcessor() {
    super();
  }

  /**
   * @author Jeff Illian
   */
  public void setParent(OrderStatusCollector parent) {
    this.parent = parent;
  }

  /**
   * @author Jeff Illian
   */
  public OrderStatusCollector getParent() {
    return parent;
  }

  /**
   * @author Jeff Illian
   */
  public void channelUpdate(ChannelEvent event)
  {
    ChannelKey channelKey = (ChannelKey)event.getChannel();
    Object eventData = event.getEventData();

    if (parent != null)
    {
        switch (channelKey.channelType)
        {
            case ChannelType.NEW_ORDER :
            case ChannelType.NEW_ORDER_BY_FIRM:
                GroupOrderStructContainer newOrderContainer = (GroupOrderStructContainer)eventData;
                parent.acceptNewOrder(newOrderContainer.getOrderStruct(), newOrderContainer.getStatusChange());
            break;

            case ChannelType.ORDER_ACCEPTED_BY_BOOK :
            case ChannelType.ORDER_ACCEPTED_BY_BOOK_BY_FIRM :
                parent.acceptOrderAcceptedByBook((OrderStruct)eventData);
            break;

            case ChannelType.ORDER_FILL_REPORT :
            case ChannelType.ORDER_FILL_REPORT_BY_FIRM :
            case ChannelType.ORDER_FILL_REPORT_BY_TRADING_FIRM :
                GroupOrderIdFillReportContainer fillContainer = (GroupOrderIdFillReportContainer)eventData;
                parent.acceptOrderFillReport(fillContainer.getOrderStruct(), fillContainer.getFilledReportStruct(), fillContainer.getStatusChange());
            break;

            case ChannelType.ORDER_BUST_REPORT :
            case ChannelType.ORDER_BUST_REPORT_BY_FIRM :
            case ChannelType.ORDER_BUST_REPORT_BY_TRADING_FIRM :
                OrderIdBustStructContainer bustContainer = (OrderIdBustStructContainer)eventData;
                parent.acceptOrderBustReport(bustContainer.getOrderStruct(), bustContainer.getBustReportStruct(), bustContainer.getStatusChange());
            break;

            case ChannelType.ORDER_BUST_REINSTATE_REPORT :
            case ChannelType.ORDER_BUST_REINSTATE_REPORT_BY_FIRM :
            case ChannelType.ORDER_BUST_REINSTATE_REPORT_BY_TRADING_FIRM :                
                OrderIdReinstateStructContainer reinstateContainer = (OrderIdReinstateStructContainer)eventData;
                parent.acceptOrderBustReinstateReport(reinstateContainer.getOrderStruct(), reinstateContainer.getBustReinstateReportStruct(), reinstateContainer.getStatusChange());
            break;

            case ChannelType.CANCEL_REPORT :
            case ChannelType.CANCEL_REPORT_BY_FIRM :
                GroupCancelReportContainer cancelContainer = (GroupCancelReportContainer)eventData;
                OrderIdCancelReportContainer cancelReportContainer = cancelContainer.getCancelReport();
                parent.acceptCancelReport(cancelReportContainer.getOrderStruct(), cancelReportContainer.getCancelReportStruct(), cancelContainer.getStatusChange());
            break;

            case ChannelType.ORDER_UPDATE :
            case ChannelType.ORDER_UPDATE_BY_FIRM :
                GroupOrderStructContainer orderStructContainer = (GroupOrderStructContainer)eventData;
                parent.acceptOrderUpdate(orderStructContainer.getOrderStruct());
            break;

            case ChannelType.ACCEPT_ORDERS :
            case ChannelType.ACCEPT_ORDERS_BY_FIRM :
                GroupOrderStructSequenceContainer orderStructSequence = (GroupOrderStructSequenceContainer)eventData;
                parent.acceptOrders(orderStructSequence.getOrderStructSequence());
            break;

            case ChannelType.ORDER_QUERY_EXCEPTION :
                OrderQueryExceptionStructContainer orderQueryException = (OrderQueryExceptionStructContainer)eventData;
                parent.acceptException(orderQueryException.getDescription());
            break;

            default:
                System.out.println("OrderStatusProcessor -> Wrong Channel : " + channelKey.channelType);
      }
    }
  }
}
