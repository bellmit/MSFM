package com.cboe.application.supplier.proxy;


import com.cboe.application.supplier.OrderStatusCollectorSupplierFactory;
import com.cboe.domain.util.*;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.domain.util.*;
import com.cboe.idl.cmiUtil.CallbackInformationStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.OrderStatusCollector;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
/**
 * OrderStatusCollectorProxy serves as a proxy to the OrderQueryConsumer
 * object on the presentation side in com.cboe.presentation.consumer.  The
 * QuoteStatusSupplier on the CAS uses this proxy object to communicate to
 * the GUI callback object.
 *
 *
 * @author Keith A. Korecky
 */

public class OrderStatusCollectorProxy extends InstrumentedCollectorProxy
{
    // the CORBA callback object.
    private OrderStatusCollector orderStatusCollector;

    /**
     * OrderStatusCollectorProxy constructor.
     *
     * @author Keith A. Korecky
     *
     * @param OrderStatusCollector a reference to the proxied implementation object.
     * @param stringIOR - stringified IOR for BaseConsumerProxy hash table usage
     */
    public OrderStatusCollectorProxy(OrderStatusCollector orderStatusCollector, BaseSessionManager sessionManager, Object hashKey)
    {
        super( sessionManager, OrderStatusCollectorSupplierFactory.find(), hashKey);
        this.orderStatusCollector = orderStatusCollector;
    }

    /**
     * This method is called by ChannelThreadCommand object.  It takes the passed
     * EventChannelEvent, parses out the relevant data for the proxied object,
     * and calls the proxied objects callback method passing in the appropriate
     * data.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param event the ChannelEvent containing the data to send the listener.
     */
    public void channelUpdate(ChannelEvent event)
    {
    	if (Log.isDebugOn())
    	{
    		Log.debug(this,"Got channel update " + event);
    	}
        ChannelKey channelKey = (ChannelKey)event.getChannel();
        if (orderStatusCollector != null)
        {
            switch (channelKey.channelType)
            {
                case ChannelType.ORDER_FILL_REPORT:
                case ChannelType.ORDER_FILL_REPORT_BY_FIRM:
                case ChannelType.ORDER_FILL_REPORT_BY_TRADING_FIRM:
                    GroupOrderIdFillReportContainer orderFilledContainer = (GroupOrderIdFillReportContainer)event.getEventData();
                    orderStatusCollector.acceptOrderFillReport(  orderFilledContainer.getOrderStruct()
                                                                ,orderFilledContainer.getFilledReportStruct()
                                                                ,orderFilledContainer.getStatusChange()
                                                                );
                break;

                case ChannelType.CANCEL_REPORT:
                case ChannelType.CANCEL_REPORT_BY_FIRM:
                    GroupCancelReportContainer cancelContainer = (GroupCancelReportContainer)event.getEventData();
                    OrderIdCancelReportContainer cancelReportContainer = cancelContainer.getCancelReport();
                    orderStatusCollector.acceptCancelReport( cancelReportContainer.getOrderStruct()
                                                            ,cancelReportContainer.getCancelReportStruct()
                                                            ,cancelContainer.getStatusChange()
                                                            );
                break;

                case ChannelType.ORDER_ACCEPTED_BY_BOOK:
                case ChannelType.ORDER_ACCEPTED_BY_BOOK_BY_FIRM:
                    OrderStruct order = (OrderStruct)event.getEventData();
                    orderStatusCollector.acceptOrderAcceptedByBook( order  );
                break;

                case ChannelType.ORDER_UPDATE:
                case ChannelType.ORDER_UPDATE_BY_FIRM:
                    GroupOrderStructContainer orderStructContainer = (GroupOrderStructContainer)event.getEventData();
                    orderStatusCollector.acceptOrderUpdate( orderStructContainer.getOrderStruct() );
                break;

                case ChannelType.NEW_ORDER:
                case ChannelType.NEW_ORDER_BY_FIRM:
                    orderStructContainer = (GroupOrderStructContainer)event.getEventData();
                    orderStatusCollector.acceptNewOrder( orderStructContainer.getOrderStruct(), orderStructContainer.getStatusChange() );
                break;

                case ChannelType.ACCEPT_ORDERS:
                case ChannelType.ACCEPT_ORDERS_BY_FIRM:
                    GroupOrderStructSequenceContainer orderStructSequence = (GroupOrderStructSequenceContainer)event.getEventData();
                    orderStatusCollector.acceptOrders( orderStructSequence.getOrderStructSequence() );
                break;

                case ChannelType.ORDER_QUERY_EXCEPTION:
                    OrderQueryExceptionStructContainer orderQueryException = (OrderQueryExceptionStructContainer)event.getEventData();
                    orderStatusCollector.acceptException( orderQueryException.getDescription() );
                break;

                case ChannelType.ORDER_BUST_REPORT:
                case ChannelType.ORDER_BUST_REPORT_BY_FIRM:
                case ChannelType.ORDER_BUST_REPORT_BY_TRADING_FIRM:                    
                    OrderIdBustStructContainer orderBustReportContainer = (OrderIdBustStructContainer)event.getEventData();
                    orderStatusCollector.acceptOrderBustReport(  orderBustReportContainer.getOrderStruct()
                                                                ,orderBustReportContainer.getBustReportStruct()
                                                                ,orderBustReportContainer.getStatusChange()
                                                                );
                break;

                case ChannelType.ORDER_BUST_REINSTATE_REPORT:
                case ChannelType.ORDER_BUST_REINSTATE_REPORT_BY_FIRM:
                case ChannelType.ORDER_BUST_REINSTATE_REPORT_BY_TRADING_FIRM:
                    OrderIdReinstateStructContainer reinstatedOrderContainer = (OrderIdReinstateStructContainer)event.getEventData();
                    orderStatusCollector.acceptOrderBustReinstateReport( reinstatedOrderContainer.getOrderStruct()
                                                                        ,reinstatedOrderContainer.getBustReinstateReportStruct()
                                                                        ,reinstatedOrderContainer.getStatusChange()
                                                                        );
                break;

                case ChannelType.ORDER_STATUS_UPDATE:
                    GroupOrderStructContainer orderStatusContainer = (GroupOrderStructContainer)event.getEventData();
                    orderStatusCollector.acceptOrderStatusUpdate(orderStatusContainer.getOrderStruct(), orderStatusContainer.getStatusChange());
                break;

                default :
                	if (Log.isDebugOn())
                	{
                		Log.debug(this, "Wrong Channel : " + channelKey.channelType);
                	}
                break;
            }
        }
    }

    public CallbackInformationStruct getCallbackInformationStruct(ChannelEvent event)
    {
        return null;
    }

    public String getMessageType()
    {
        return SupplierProxyMessageTypes.ORDER;
    }
}
