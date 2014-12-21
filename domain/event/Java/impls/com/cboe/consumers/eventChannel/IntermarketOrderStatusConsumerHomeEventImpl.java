
package com.cboe.consumers.eventChannel;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.IECIntermarketOrderStatusConsumerHome;
import com.cboe.interfaces.events.IntermarketOrderStatusConsumer;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

public class IntermarketOrderStatusConsumerHomeEventImpl extends ClientBOHome implements IECIntermarketOrderStatusConsumerHome {
    private IntermarketOrderStatusEventConsumerInterceptor imOrderStatusEventConsumerInterceptor;
    private IntermarketOrderStatusEventConsumerImpl imOrderStatusEvent;
    private EventService eventService;
    private EventChannelFilterHelper eventChannelFilterHelper;
    private final String CHANNEL_NAME = "IntermarketOrderStatus";

    public IntermarketOrderStatusConsumerHomeEventImpl() {
        super();
    }

    public IntermarketOrderStatusConsumer create() {
        return find();
    }

    public IntermarketOrderStatusConsumer find() {
        return imOrderStatusEventConsumerInterceptor;
    }

    public void clientStart()
        throws Exception
    {
        if (eventService == null){
            eventService = eventChannelFilterHelper.connectEventService();
        }

        String interfaceRepId = com.cboe.idl.events.IntermarketOrderStatusEventConsumerHelper.id();
        // connect to the event channel without filter and add the constraint filter later on
        // using addConstraint - Emily
        eventChannelFilterHelper.connectConsumer( CHANNEL_NAME, interfaceRepId, imOrderStatusEvent );
    }

    public void clientInitialize()
        throws Exception
    {
        eventChannelFilterHelper = new EventChannelFilterHelper();
        IntermarketOrderStatusConsumerIECImpl imOrderStatusConsumer = new IntermarketOrderStatusConsumerIECImpl();
        imOrderStatusConsumer.create(String.valueOf(imOrderStatusConsumer.hashCode()));
        //Every bo object must be added to the container.
        addToContainer(imOrderStatusConsumer);
        imOrderStatusEventConsumerInterceptor = new IntermarketOrderStatusEventConsumerInterceptor(imOrderStatusConsumer);
        if(getInstrumentationEnablementProperty())
        {
            imOrderStatusEventConsumerInterceptor.startInstrumentation(getInstrumentationProperty());
        }
        imOrderStatusEvent = new IntermarketOrderStatusEventConsumerImpl(imOrderStatusEventConsumerInterceptor);
    }

    /**
     * Adds a  Filter to the internal event channel. Constraints based on the
     * ChannelKey will be added as well. Do not make call to addConstraints when this method has
     * already being called.
     *
     * @param channelKey the event channel key
     *
     */
    public void addFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        addConstraint(channelKey);
    }

    /**
     * Adds constraint based on the channel key
     *
     * @param channelKey the event channel key
     *
     */
    private void addConstraint(ChannelKey channelKey)
        throws SystemException
    {
        if ( find() != null )
        {
            String constraintString = getConstraintString(channelKey);

            eventChannelFilterHelper.addEventFilter( imOrderStatusEvent, channelKey,
                    eventChannelFilterHelper.getChannelName(CHANNEL_NAME), constraintString);
        }
    }// end of addConstraint


    /**
     * Removes the event channel Filter from the CBOE event channel.
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     * @author Keval Desai
     * @version 12/1/00
     */
    public void removeFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        removeConstraint(channelKey);
    }
    /**
     * Removes constraint based on the channel key
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     */
    private void removeConstraint(ChannelKey channelKey)
        throws SystemException
    {
        if ( find() != null ) {
            String constraintString = getConstraintString(channelKey);
            eventChannelFilterHelper.removeEventFilter( channelKey, constraintString);
        }
    }// end of addConstraint

    /**
     * Returns the constraint string based on the channel key
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     */
    protected String getConstraintString(ChannelKey channelKey)
    {
        String parm = getParmName(channelKey);

        if(parm.equals(EventChannelFilterHelper.ALL_EVENTS_CONSTRAINT) ||
           parm.equals(EventChannelFilterHelper.NO_EVENTS_CONSTRAINT))
        {
            return parm;
        }

        StringBuilder buf = new StringBuilder(parm.length()+2);
        buf.append("$.").append(parm);
        return buf.toString();
    }

    /**
     * Returns the constraint parameter string based on the channel key
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     */
    protected String getParmName(ChannelKey channelKey)
    {
        SessionKeyContainer key = (SessionKeyContainer)channelKey.key;
        StringBuilder name = new StringBuilder(170);

        switch (channelKey.channelType)
        {
            case ChannelType.HELD_ORDER_CANCEL_REPORT :
                name.append("acceptHeldOrderCancelReport.heldOrder.order.classKey==")
                    .append(key.getKey())
                    .append(" and $.acceptHeldOrderCancelReport.cancelReport.sessionName=='")
                    .append(key.getSessionName()).append("'");
                return name.toString();
            case ChannelType.HELD_ORDER_FILLED_REPORT:
                name.append("acceptHeldOrderFilledReport.heldOrder.order.classKey==").append(key.getKey())
                    .append(" and $.acceptHeldOrderFilledReport.heldOrder.order.activeSession=='")
                    .append(key.getSessionName()).append("'");
                return name.toString();
            case ChannelType.HELD_ORDER_STATUS :
                name.append("acceptHeldOrderStatus.order.order.classKey==").append(key.getKey())
                    .append(" and $.acceptHeldOrderStatus.order.order.activeSession=='")
                    .append(key.getSessionName()).append("'");
                return name.toString();
            case ChannelType.HELD_ORDERS:
                name.append("acceptHeldOrders.classKey==").append(key.getKey())
                    .append(" and $.acceptHeldOrders.sessionName=='")
                    .append(key.getSessionName()).append("'");
                return name.toString();
            case ChannelType.CANCEL_HELD_ORDER:
                name.append("acceptCancelHeldOrder.productKeys.classKey==").append(key.getKey())
                    .append(" and $.acceptCancelHeldOrder.heldOrderCancelRequest.cancelRequest.sessionName=='")
                    .append(key.getSessionName()).append("'");
                return name.toString();
            case ChannelType.NEW_HELD_ORDER:
                name.append("acceptNewHeldOrder.heldOrder.order.classKey==").append(key.getKey())
                    .append(" and $.acceptNewHeldOrder.heldOrder.order.activeSession=='")
                    .append(key.getSessionName()).append("'");
                return name.toString();
            case ChannelType.FILL_REJECT_REPORT:
                name.append("acceptFillRejectReport.fillRejects[0].order.classKey==").append(key.getKey())
                    .append(" and $.acceptFillRejectReport.fillRejects[0].order.activeSession=='")
                    .append(key.getSessionName()).append("'");
                return name.toString();
            default :
                Log.alarm(this, "Invalid Channel Type for filtering for context: " + channelKey.channelType);
                return EventChannelFilterHelper.NO_EVENTS_CONSTRAINT;
        }
    } // end of getParmName

    // Unused methods declared in home interface for server usage.
    public void addConsumer(IntermarketOrderStatusConsumer consumer, ChannelKey key) {}
    public void removeConsumer(IntermarketOrderStatusConsumer consumer, ChannelKey key) {}
    public void removeConsumer(IntermarketOrderStatusConsumer consumer) {}
}// EOF