/*
 * Created by IntelliJ IDEA.
 * User: huange
 * Date: Oct 9, 2002
 * Time: 3:09:19 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.application.shared.consumer;

import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.HeldOrderCollector;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.idl.cmiIntermarketMessages.*;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.domain.util.HeldOrderCancelReportContainer;
import com.cboe.domain.util.HeldOrderFilledReportContainer;
import com.cboe.domain.util.HeldOrderCancelRequestContainer;

/**
 * @author Emily Huang
 */
public class HeldOrderProcessor extends InstrumentedProcessor {
	private HeldOrderCollector parent = null;

  public HeldOrderProcessor(HeldOrderCollector parent) {
    super(parent);
    this.parent = parent;
  }

  public void setParent(HeldOrderCollector parent) {
    this.parent = parent;
  }

  public HeldOrderCollector getParent() {
    return parent;
  }

  public void channelUpdate(ChannelEvent event) {

      ChannelKey channelKey = (ChannelKey)event.getChannel();
      Object eventData = event.getEventData();
      SessionKeyContainer sessionKey = (SessionKeyContainer)channelKey.key;

      HeldOrderStruct heldOrder;
      HeldOrderStruct[] heldOrders;
      FillRejectStruct[] fillRejects;

      if (parent != null)
      {
          switch (channelKey.channelType)
          {
              case ChannelType.NEW_HELD_ORDER:
                  heldOrder = (HeldOrderStruct)eventData;
                  parent.acceptNewHeldOrder(sessionKey.getSessionName(), sessionKey.getKey(), heldOrder);
              break;

              case ChannelType.HELD_ORDER_CANCEL_REPORT :
                  HeldOrderCancelReportContainer cancelReportContainer = (HeldOrderCancelReportContainer)eventData;
                  parent.acceptHeldOrderCanceledReport(sessionKey.getSessionName(),
                                                       sessionKey.getKey(),
                                                       cancelReportContainer.getHeldOrder(),
                                                       cancelReportContainer.getCancelRequestId(),
                                                       cancelReportContainer.getCancelReport());
              break;

              case ChannelType.HELD_ORDER_FILLED_REPORT :
                  HeldOrderFilledReportContainer heldOrderFilledContainer = (HeldOrderFilledReportContainer)eventData;
                  parent.acceptHeldOrderFilledReport(sessionKey.getSessionName(),
                                                     sessionKey.getKey(),
                                                     heldOrderFilledContainer.getHeldOrderStruct(),
                                                     heldOrderFilledContainer.getFilledReportStruct());
              break;

              case ChannelType.HELD_ORDER_STATUS :
                  heldOrder = (HeldOrderStruct)eventData;
                  parent.acceptHeldOrderStatus(sessionKey.getSessionName(), sessionKey.getKey(), heldOrder);
              break;
              case ChannelType.HELD_ORDERS :
                  heldOrders = (HeldOrderStruct[])eventData;
                  parent.acceptHeldOrders(sessionKey.getSessionName(), sessionKey.getKey(), heldOrders);
              break;

              case ChannelType.CANCEL_HELD_ORDER:
                  HeldOrderCancelRequestContainer cancelRequestContainer = (HeldOrderCancelRequestContainer)eventData;
                  parent.acceptCancelHeldOrderRequest(sessionKey.getSessionName(),
                                                      sessionKey.getKey(),
                                                      cancelRequestContainer.getProductKeyes(),
                                                      cancelRequestContainer.getCancelRequest());
              break;
              case ChannelType.FILL_REJECT_REPORT:
                  fillRejects = (FillRejectStruct[])eventData;
                  parent.acceptFillRejectReport(sessionKey.getSessionName(), sessionKey.getKey(), fillRejects);

              break;
              default:
                  if (Log.isDebugOn())
                  {
                      Log.debug("HeldOrderProcessor -> Wrong Channel : " + channelKey.channelType);
                  }
        }
      }
  }

  public String getMessageType()
  {
    return SupplierProxyMessageTypes.HELD_ORDER;
  }
}
