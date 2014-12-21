package com.cboe.consumers.eventChannel;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;

import com.cboe.interfaces.ohsEvents.IECOrderRoutingConsumerHome;
import com.cboe.interfaces.ohsEvents.OrderRoutingConsumer;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class OrderRoutingConsumerHomeEventImpl extends ClientBOHome implements IECOrderRoutingConsumerHome
{
    protected OrderRoutingEventConsumerInterceptor orderRoutingEventConsumerInterceptor;
    protected OrderRoutingEventConsumerImpl orderRoutingEventConsumer;
    protected OrderRoutingConsumerIECImpl orderRoutingConsumer;
    protected EventService eventService;
    protected EventChannelFilterHelper eventChannelFilterHelper;
    protected final String CHANNEL_NAME = "OrderRouting";

    public OrderRoutingConsumerHomeEventImpl()
    {
        super();
        if (Log.isDebugOn())
        {
            Log.debug( "constructor::OrderRoutingConsumerHomeEventImpl" );
        }
    }

    public OrderRoutingConsumer find()
    {
        return orderRoutingEventConsumerInterceptor;
    }

    public OrderRoutingConsumer create()
    {
        return find();
    }

    public void clientStart() throws Exception
    {
        if (eventService == null)
        {
            eventService = eventChannelFilterHelper.connectEventService();
        }
        String interfaceRepId = com.cboe.idl.ohsEvents.OrderRoutingEventConsumerHelper.id();
        // connect to the event channel without filter and add the constraint filter later on
        // using addConstraint
        eventChannelFilterHelper.connectConsumer(CHANNEL_NAME, interfaceRepId, orderRoutingEventConsumer);
    }

    public void clientInitialize()
    {
        eventChannelFilterHelper = new EventChannelFilterHelper();
        orderRoutingConsumer = new OrderRoutingConsumerIECImpl();
        orderRoutingConsumer.create(String.valueOf(orderRoutingConsumer.hashCode()));

        //Every bo object must be added to the container.
        addToContainer(orderRoutingConsumer);
        orderRoutingEventConsumerInterceptor = new OrderRoutingEventConsumerInterceptor(orderRoutingConsumer);
        if(getInstrumentationEnablementProperty())
        {
        	
            orderRoutingEventConsumerInterceptor.startInstrumentation(getInstrumentationProperty());
        }
        orderRoutingEventConsumer = new OrderRoutingEventConsumerImpl(orderRoutingEventConsumerInterceptor);
    }

    public void addFilter (ChannelKey channelKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Adding filter for: " + channelKey);
        }

        addConstraint(channelKey);
    }

    /**
     * Adds constraint based on the channel key
     *
     * @param channelKey the event channel key
     */
    private void addConstraint(ChannelKey channelKey) throws SystemException
    {
        if ( find() != null )
        {
            String constraintString = getConstraintString(channelKey);
            if (Log.isDebugOn())
            {
                Log.debug(this, "add constraint string is: " + constraintString);
            }

            eventChannelFilterHelper.addEventFilter(orderRoutingEventConsumer, channelKey,
                    eventChannelFilterHelper.getChannelName(CHANNEL_NAME), constraintString);
        }
    }

    /**
     * Removes the event channel listener to the internal event channel and the CBOE event channel.
     *
     * @param channelKey the event channel key
     */
    public void removeFilter (ChannelKey channelKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Removing filter for: " + channelKey);
        }

        removeConstraint(channelKey);
    }

    /**
     * Removes constraint based on the channel key
     *
     * @param channelKey the event channel key
     */
    private void removeConstraint(ChannelKey channelKey) throws SystemException
    {
        if ( find() != null )
        {
            String constraintString = getConstraintString(channelKey);
            if (Log.isDebugOn())
            {
                Log.debug(this, "remove constraint string is: " + constraintString);
            }

            eventChannelFilterHelper.removeEventFilter(channelKey, constraintString);
        }
    }

    /**
     * Returns the constraint string based on the channel key
     *
     * @param channelKey the event channel key
     */
    protected String getConstraintString(ChannelKey channelKey)
    {
        String key = channelKey.key.toString();
        StringBuilder constraint = new StringBuilder(key.length()+80);
        switch (channelKey.channelType)
        {
            
            case ChannelType.OMT_ORDER_ACCEPTED:
                constraint.append("'").append(key)
                          .append("' in $.acceptOrders.orderRoutingStruct.destinations");
                return constraint.toString();
            case ChannelType.OMT_ORDER_CANCELED:
                constraint.append("'").append(key)
                          .append("' in $.acceptCancels.orderRoutingStruct.destinations");
                return constraint.toString();
            case ChannelType.OMT_ORDER_CANCEL_REPLACED:
                constraint.append("'").append(key)
                          .append("' in $.acceptCancelReplaces.orderRoutingStruct.destinations");
                return constraint.toString();
            case ChannelType.OMT_FILL_REPORT_REJECT:
                constraint.append("'").append(key)
                          .append("' in $.acceptFillReportReject.orderRoutingStruct.destinations");
                return constraint.toString();
            case ChannelType.OMT_ORDER_REMOVED:
                constraint.append("'").append(key)
                          .append("' in $.acceptRemoveOrder.orderRoutingStruct.destinations");
                return constraint.toString();
            case ChannelType.OMT_LINKAGE_CANCEL_REPORT:
                constraint.append("'").append(key)
                          .append("' in $.acceptLinkageCancelReport.orderRoutingStruct.destinations");
                return constraint.toString();
            case ChannelType.OMT_LINKAGE_FILL_REPORT:
                constraint.append("'").append(key)
                          .append("' in $.acceptLinkageFillReport.orderRoutingStruct.destinations");
                return constraint.toString();
            case ChannelType.OMT_ORDERS_FOR_LOCATION:
                constraint.append("$.acceptOrderLocationServerResponse.response.transactionId.userId=='")
                          .append(key).append("'");
                return constraint.toString();
            case ChannelType.OMT_LOCATION_SUMMARY:
                constraint.append("$.acceptOrderLocationSummaryServerResponse.response.transactionId.userId=='")
                          .append(key).append("'");
                return constraint.toString();
            case ChannelType.OMT_TRADE_NOTIFICATION:
                constraint.append("'").append(key)
                          .append("' in $.acceptTradeNotifications.orderRoutingStruct.destinations");
                return constraint.toString();
            case ChannelType.OMT_REMOVE_MESSAGE:
                constraint.append("'").append(key)
                          .append("' in $.acceptRemoveMessage.routingStruct.destinations");
                return constraint.toString();
           case ChannelType.OMT_FILL_REPORT_DROP_COPY:
                constraint.append("'").append(key)
                          .append("' in $.acceptFillReportDropCopy.orderRoutingStruct.destinations");
               return constraint.toString();
            case ChannelType.OMT_CANCEL_REPORT_DROP_COPY:
                constraint.append("'").append(key)
                          .append("' in $.acceptCancelReportDropCopy.orderRoutingStruct.destinations");
                return constraint.toString();
            case ChannelType.OMT_MANUAL_ORDER_TIMEOUT:
                constraint.append("'").append(key)
                          .append("' in $.acceptManualOrderTimeout.routingParameters.destinations");
                return constraint.toString();
            case ChannelType.OMT_MANUAL_FILL_TIMEOUT:
                constraint.append("'").append(key)
                          .append("' in $.acceptManualFillTimeout.routingParameters.destinations");
                return constraint.toString();
            case ChannelType.PAR_ORDER_ACCEPTED:
                constraint.append("'").append(key)
                          .append("' in $.acceptManualOrders.orderRoutingStruct.destinations");
                return constraint.toString();
            case ChannelType.PAR_ORDER_CANCELED:
                constraint.append("'").append(key)
                          .append("' in $.acceptManualCancels.orderRoutingStruct.destinations");
                return constraint.toString();
            case ChannelType.PAR_ORDER_CANCEL_REPLACED:
                constraint.append("'").append(key)
                          .append("' in $.acceptManualCancelReplaces.orderRoutingStruct.destinations");
                return constraint.toString();
            default :
                Log.alarm(this, "Invalid Channel Type for filtering for context: " + channelKey.channelType);
                return EventChannelFilterHelper.NO_EVENTS_CONSTRAINT;
        }
    }

    public void addConsumer(OrderRoutingConsumer consumer, ChannelKey key) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    }

    public void removeConsumer(OrderRoutingConsumer consumer, ChannelKey key) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    }

    public void removeConsumer(OrderRoutingConsumer consumer) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    }
}
