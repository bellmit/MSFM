package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.*;
import com.cboe.util.*;
import com.cboe.domain.util.UserSessionClassContainer;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.eventService.*;
import com.cboe.exceptions.*;

/**
 * @author Emily Huang
 */
public class AlertConsumerHomeEventImpl extends ClientBOHome implements IECAlertConsumerHome {

    // Instrumented Alert Listener on CAS IEC
    private AlertEventConsumerInterceptor alertEventConsumerInterceptor;

    // Alert listener on CBOE alert event channel
    private AlertEventConsumerImpl alertEvent;

    // Instance of CBOE event service
    private EventService eventService;

    // Instance of Event Channel Helper Classlass, which hides event channel set up.
    private EventChannelFilterHelper eventChannelFilterHelper;

    // Alert event channel name
    private final String CHANNEL_NAME = "Alert";

    /**
     * AlertConsumerHomeEventImpl constructor comment.
     */
    public AlertConsumerHomeEventImpl() {
        super();
    }

    public AlertConsumer create() {
        return find();
    }

    public AlertConsumer find() {
        return alertEventConsumerInterceptor;
    }

    public void clientStart()
        throws Exception
    {
        if (eventService == null){
            eventService = eventChannelFilterHelper.connectEventService();
        }
        String interfaceRepId = com.cboe.idl.internalConsumers.AlertConsumerHelper.id();

        // connect to the event channel without filter and add the constraint filter later on
        // using addConstraint
        eventChannelFilterHelper.connectConsumer( CHANNEL_NAME, interfaceRepId, alertEvent );
    }

    public void clientInitialize() {
        eventChannelFilterHelper = new EventChannelFilterHelper();
        AlertConsumerIECImpl alertConsumer = new AlertConsumerIECImpl();
        alertConsumer.create(String.valueOf(alertConsumer.hashCode()));
        //Every bo object must be added to the container.
        addToContainer(alertConsumer);

        alertEventConsumerInterceptor = new AlertEventConsumerInterceptor(alertConsumer);
        if (getInstrumentationEnablementProperty())
        {
            alertEventConsumerInterceptor.startInstrumentation(getInstrumentationProperty());
        }
        alertEvent = new AlertEventConsumerImpl(alertEventConsumerInterceptor);
    }

    /**
     * Adds a Filter to CBOE event channel. Constraints based on the
     * ChannelKey will be added. Do not make call to addConstraints when this method has
     * already being called.
     *
     * @param channelKey the event channel key
     */
    public void addFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        addConstraint( channelKey );
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
            eventChannelFilterHelper.addEventFilter( alertEvent, channelKey,
                    eventChannelFilterHelper.getChannelName(CHANNEL_NAME), constraintString);
        }
    }

    /**
     * Removes the event channel Filter from the CBOE event channel.
     * @param channelKey the event channel key
     */
    public void removeFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        removeConstraint(channelKey);
    }

    /**
     * Removes constraint based on the channel key
     * @param channelKey the event channel key
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
     */
    protected String getConstraintString(ChannelKey channelKey)
    {
        String parm = getParmName(channelKey);

        if ( parm.equals(""))
        {
            return "";
        }

        else
        {
        	StringBuilder buf = new StringBuilder(parm.length()+2);
            buf.append("$.").append(parm);
            return buf.toString();
        }

    }// end of getConstraintString

    /**
     * Returns the constraint parameter string based on the channel key
     *
     * @param channelKey the event channel key
     */
    protected String getParmName(ChannelKey channelKey)
    {
        switch (channelKey.channelType)
        {
            case ChannelType.ALERT_SATISFACTION:
                UserSessionClassContainer key = (UserSessionClassContainer)channelKey.key;
                return new StringBuilder(150)
                          .append("acceptSatisfactionAlert.alert.lastSale.productKeys.classKey==").append(key.getClassKey())
                          .append(" and $.acceptSatisfactionAlert.alert.alertHdr.sessionName=='").append(key.getSessionName())
                          .append("'").toString();
            case ChannelType.ALERT_ALL:
            case ChannelType.ALERT_UPDATE_ALL:
                 return "";
            default :
                if (Log.isDebugOn())
                {
                    Log.debug(this, "Unknown channel type: " + channelKey.channelType);
                }
                return "";
        }
    }
    // Unused methods declared in home interface for server usage.
    public void addConsumer(AlertConsumer consumer, ChannelKey key) {}
    public void removeConsumer(AlertConsumer consumer, ChannelKey key) {}
    public void removeConsumer(AlertConsumer consumer) {}
}// EOF
