/*
 * Created by IntelliJ IDEA.
 * User: chenj
 * Date: Nov 4, 2002
 * Time: 10:59:18 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.intermarketPresentation.api;

import com.cboe.util.event.EventChannelListener;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.idl.cmiIntermarketMessages.*;


public class TestIMTranslatorEventChannel implements EventChannelListener{

    public TestIMTranslatorEventChannel()
    {
        super();
    }

    public void channelUpdate(ChannelEvent event)
    {
        int channelType = ((ChannelKey) event.getChannel()).channelType;
        Object eventData = event.getEventData();
        switch (channelType)
        {
            case ChannelType.CB_HELD_ORDERS:
                System.out.println("received channel event.  channelType --> CB_HELD_ORDERS");
                HeldOrderDetailStruct[] orderDetails = (HeldOrderDetailStruct[])event.getEventData();
                for (int i=0; i<orderDetails.length; i++)
                {
                    com.cboe.application.test.ReflectiveStructTester.printStruct(orderDetails[i], "heldOrderDetails["+i+"]:");
                }
                break;
            case ChannelType.CB_HELD_ORDER_CANCELED_REPORT:
                System.out.println("received channel event.  channelType --> CB_HELD_ORDER_CANCELED_REPORT");
                HeldOrderCancelReportStruct cancelReport = (HeldOrderCancelReportStruct)event.getEventData();
                com.cboe.application.test.ReflectiveStructTester.printStruct(cancelReport, "HeldOrderCancelReportStruct:");
                break;
            case ChannelType.CB_HELD_ORDER_FILLED_REPORT:
                System.out.println("received channel event. channelType --> CB_HELD_ORDER_FILLED_REPORT");
                HeldOrderFilledReportStruct fillReport = (HeldOrderFilledReportStruct)event.getEventData();
                com.cboe.application.test.ReflectiveStructTester.printStruct(fillReport, "HeldOrderFilledReportStruct:");
                break;
            case ChannelType.CB_NEW_HELD_ORDER:
                System.out.println("received channel event. channelType --> CB_NEW_HELD_ORDER");
                HeldOrderDetailStruct heldOrder = (HeldOrderDetailStruct)event.getEventData();
                com.cboe.application.test.ReflectiveStructTester.printStruct(heldOrder, "HeldOrderDetailStruct:");
                break;
            case ChannelType.CB_CANCEL_HELD_ORDER_REQUEST:
                System.out.println("received channel event. channelType --> CB_CANCEL_HELD_ORDER_REQUEST");
                HeldOrderCancelRequestStruct requestStruct = (HeldOrderCancelRequestStruct)event.getEventData();
                com.cboe.application.test.ReflectiveStructTester.printStruct(requestStruct, "HeldOrderCancelRequestStruct:");
                break;
            case ChannelType.CB_FILL_REJECT_REPORT:
                System.out.println("received channel event. channelType --> CB_FILL_REJECT_REPORT");
                OrderFillRejectStruct rejectStruct = (OrderFillRejectStruct)event.getEventData();
                com.cboe.application.test.ReflectiveStructTester.printStruct(rejectStruct, "OrderFillRejectStruct:");
                break;
            case ChannelType.CB_NBBO_AGENT_FORCED_OUT:
                System.out.println("received channel event. channelType --> CB_NBBO_AGENT_FORCED_OUT");
                String reason = (String)event.getEventData();
                System.out.println("reason:"+reason);
                break;
            case ChannelType.CB_NBBO_AGENT_REMINDER:
                System.out.println("received channel event. channelType --> CB_NBBO_AGENT_REMINDER");
                OrderReminderStruct reminderStruct = (OrderReminderStruct)event.getEventData();
                com.cboe.application.test.ReflectiveStructTester.printStruct(reminderStruct, "OrderReminderStruct:");
                break;
            case ChannelType.CB_NBBO_AGENT_SATISFACTION_ALERT:
                System.out.println("received channel event. channelType --> CB_NBBO_AGENT_SATISFACTION_ALERT");
                SatisfactionAlertStruct satisfactionAlertStruct = (SatisfactionAlertStruct) event.getEventData();
                com.cboe.application.test.ReflectiveStructTester.printStruct(satisfactionAlertStruct, "SatisfactionAlertStruct:");
                break;
            case ChannelType.CB_NBBO_AGENT_INTERMARKET_ADMIN:
                System.out.println("received channel event. channelType --> CB_NBBO_AGENT_INTERMARKET_ADMIN");
                AdminStruct adminStruct = (AdminStruct) event.getEventData();
                com.cboe.application.test.ReflectiveStructTester.printStruct(adminStruct, "AdminStruct:");
                break;
            default:
                System.out.println("received wrong channel event with channelType:"+channelType);
        }
    }
}
