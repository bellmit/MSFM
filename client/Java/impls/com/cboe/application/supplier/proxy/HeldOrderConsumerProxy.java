/*
 * Created by IntelliJ IDEA.
 * User: huange
 * Date: Oct 4, 2002
 * Time: 4:19:07 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.application.supplier.proxy;

import com.cboe.application.supplier.HeldOrderSupplierFactory;
import com.cboe.domain.util.HeldOrderCancelRequestContainer;
import com.cboe.idl.cmiIntermarketCallback.CMIIntermarketOrderStatusConsumer;
import com.cboe.idl.cmiIntermarketMessages.HeldOrderCancelReportStruct;
import com.cboe.idl.cmiIntermarketMessages.HeldOrderDetailStruct;
import com.cboe.idl.cmiIntermarketMessages.HeldOrderFilledReportStruct;
import com.cboe.idl.cmiIntermarketMessages.OrderFillRejectStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;

public class HeldOrderConsumerProxy extends InstrumentedConsumerProxy
{
    /**
     * HeldOrderConsumerProxy constructor.
     */
    public HeldOrderConsumerProxy(CMIIntermarketOrderStatusConsumer imOrderStatusConsumer, BaseSessionManager sessionManager)
    {
        super(sessionManager, HeldOrderSupplierFactory.find(), imOrderStatusConsumer);
        interceptor = new IntermarketOrderStatusConsumerInterceptor(imOrderStatusConsumer);
    }


    /**
     * This method is called by ChannelThreadCommand object.  It takes the passed
     * EventChannelEvent, parses out the relevant data for the proxied object,
     * and calls the proxied objects callback method passing in the appropriate
     * data.
     *
     * @param event the ChannelEvent containing the data to send the listener.
     */
    public void channelUpdate(ChannelEvent event)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this,"calling channelUpdate for " + getSessionManager());
        }
        if (event != null)
        {
            try
            {
                ChannelKey key = (ChannelKey) event.getChannel();
                switch(key.channelType)
                {
                    case ChannelType.CB_HELD_ORDER_CANCELED_REPORT:
                        ((IntermarketOrderStatusConsumerInterceptor)interceptor).acceptHeldOrderCanceledReport((HeldOrderCancelReportStruct)event.getEventData());
                        break;

                    case ChannelType.CB_HELD_ORDER_FILLED_REPORT:
                        ((IntermarketOrderStatusConsumerInterceptor)interceptor).acceptHeldOrderFilledReport((HeldOrderFilledReportStruct)event.getEventData());
                        break;
                    case ChannelType.CB_NEW_HELD_ORDER:
                        ((IntermarketOrderStatusConsumerInterceptor)interceptor).acceptNewHeldOrder((HeldOrderDetailStruct)event.getEventData());
                        break;

                    case ChannelType.CB_HELD_ORDERS:
                        ((IntermarketOrderStatusConsumerInterceptor)interceptor).acceptHeldOrderStatus((HeldOrderDetailStruct[])event.getEventData());
                        break;
                    case ChannelType.CB_CANCEL_HELD_ORDER_REQUEST:
                        HeldOrderCancelRequestContainer cancelRequestContainer = (HeldOrderCancelRequestContainer)event.getEventData();
                        ((IntermarketOrderStatusConsumerInterceptor)interceptor).acceptCancelHeldOrderRequest(cancelRequestContainer.getProductKeyes(),
                                cancelRequestContainer.getCancelRequest());
                        break;
                    case ChannelType.CB_FILL_REJECT_REPORT:
                        ((IntermarketOrderStatusConsumerInterceptor)interceptor).acceptFillRejectReport((OrderFillRejectStruct)event.getEventData());
                        break;
                    default:
                        break;
                }
            }
            catch(Exception e)
            {
                Log.exception(this, "session:" + getSessionManager(), e);
                lostConnection(event);
            }
        }
        else
        {
            Log.information(this, "Null event");
        }
    }

    public String getMethodName(ChannelEvent event)
    {
        String method = "";

        ChannelKey key = (ChannelKey) event.getChannel();

        switch(key.channelType)
        {
            case ChannelType.CB_HELD_ORDER_CANCELED_REPORT:
                method = "acceptHeldOrderCanceledReport";
                break;

            case ChannelType.CB_HELD_ORDER_FILLED_REPORT:
                method = "acceptHeldOrderFilledReport";
                break;
            case ChannelType.CB_NEW_HELD_ORDER:
                method = "acceptNewHeldOrder";
                break;

            case ChannelType.CB_HELD_ORDERS:
                method = "acceptHeldOrderStatus";
                break;
            case ChannelType.CB_CANCEL_HELD_ORDER_REQUEST:
                method = "acceptCancelHeldOrderRequest";
                break;
            case ChannelType.CB_FILL_REJECT_REPORT:
                method = "acceptFillRejectReport";
                break;

            default:
                break;
        }
        return method;
    }

    public String getMessageType()
    {
        return SupplierProxyMessageTypes.HELD_ORDER;
    }
}
