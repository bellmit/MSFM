// $Workfile$ com.cboe.consumers.eventChannel.QuoteNotificationConsumerHomeEventImpl.java
// $Revision$
/* $Log$
*   Initial Version                             Tom Lynch
*   Revision                                    Jeff Illian
*   Revision        Increment 6     12/1/00     desaik
*/
//----------------------------------------------------------------------
// Copyright (c) 1999 CBOE. All Rights Reserved.

package com.cboe.consumers.eventChannel;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.IECQuoteNotificationConsumerHome;
import com.cboe.interfaces.events.QuoteNotificationConsumer;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

    /**
     * <b> Description </b>
     * <p>
     *      The Quote Locked Listener class.
     * </p>
     *
     * @author Tom Lynch
     * @author Jeff Illian
     * @author Keval Desai
     */
public class QuoteNotificationConsumerHomeEventImpl extends ClientBOHome implements IECQuoteNotificationConsumerHome {
    private QuoteNotificationEventConsumerInterceptor quoteLockEventConsumerInterceptor;
    private QuoteNotificationEventConsumerImpl quoteLockEvent;
    private EventService eventService;
    private EventChannelFilterHelper eventChannelFilterHelper;
    private final String CHANNEL_NAME = "QuoteNotification";
    /**
     * QuoteNotificationConsumerHomeEventImpl constructor comment.
     */
    public QuoteNotificationConsumerHomeEventImpl() {
        super();
    }

    public QuoteNotificationConsumer create() {
        return find();
    }
    /**
     * Return the Quote Lock Listener (If first time, create it and bind it to the orb).
     * @author Tom Lynch
     * @author Jeff Illian
     * @return QuoteNotificationConsumer
     */
    public QuoteNotificationConsumer find() {
        return quoteLockEventConsumerInterceptor;
    }

    public void clientStart()
        throws Exception
    {

        if (eventService == null){
            eventService = eventChannelFilterHelper.connectEventService();
        }
        String interfaceRepId = com.cboe.idl.events.QuoteNotificationEventConsumerHelper.id();
        // connect to the event channel without filter and add the constraint filter later on
        // using addConstraint - Connie Feng
        eventChannelFilterHelper.connectConsumer( CHANNEL_NAME, interfaceRepId, quoteLockEvent );
    }

    public void clientInitialize() {
        eventChannelFilterHelper = new EventChannelFilterHelper();
        QuoteNotificationConsumerIECImpl  quoteLockConsumer = new QuoteNotificationConsumerIECImpl();
        quoteLockConsumer.create(String.valueOf(quoteLockConsumer.hashCode()));
        //Every bo object must be added to the container.
        addToContainer(quoteLockConsumer);

        quoteLockEventConsumerInterceptor = new QuoteNotificationEventConsumerInterceptor(quoteLockConsumer);
        if(getInstrumentationEnablementProperty())
        {
            quoteLockEventConsumerInterceptor.startInstrumentation(getInstrumentationProperty());
        }
        quoteLockEvent = new QuoteNotificationEventConsumerImpl(quoteLockEventConsumerInterceptor);
    }

    /**
     * Adds a  Filter to the internal event channel. Constraints based on the
     * ChannelKey will be added as well. Do not make call to addConstraints when this method has
     * already being called.
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     * @author Keval Desai
     * @version 12/1/00
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
     * @author Connie Feng
     */
    private void addConstraint(ChannelKey channelKey)
        throws SystemException
    {
//        SessionKeyContainer key = (SessionKeyContainer)channelKey.key;
//        Integer keyInt = Integer.valueOf(key.getKey());

//        channelKey = new ChannelKey(channelKey.channelType, keyInt);

        if ( find() != null )
        {
            String constraintString = getConstraintString(channelKey);
            if (Log.isDebugOn())
            {
                Log.debug(this, "addConstraint:: channel type: " + channelKey.channelType + " string = " + constraintString);
            }
            eventChannelFilterHelper.addEventFilter( quoteLockEvent, channelKey,
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

//        ChannelKey quoteLockChannelKey =
//                        new ChannelKey(ChannelKey.QUOTE_LOCKED_NOTIFICATION, channelKey.key);
//        removeConstraint(quoteLockChannelKey);
//        quoteLockChannelKey =
//                        new ChannelKey(ChannelKey.QUOTE_LOCKED_NOTIFICATION_BY_CLASS, channelKey.key);
//        removeConstraint(quoteLockChannelKey);
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
//        SessionKeyContainer key = (SessionKeyContainer)channelKey.key;
//        Integer keyInt = Integer.valueOf(key.getKey());

//        channelKey = new ChannelKey(channelKey.channelType, keyInt);

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
        return getParmName(channelKey);
    }// end of getConstraintString

    /**
     * Returns the constraint parameter string based on the channel key
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     */
    protected String getParmName(ChannelKey channelKey)
    {
		// todo: channelKey is what?
        switch (channelKey.channelType)
        {
            case ChannelType.QUOTE_LOCKED_NOTIFICATION :
                return new StringBuilder(60)
                          .append(channelKey.key)
                          .append(" in $.acceptQuoteLockedNotification.userKeys").toString();

            default :
                Log.alarm(this, "Invalid Channel Type for filtering for context: " + channelKey.channelType);
                return EventChannelFilterHelper.NO_EVENTS_CONSTRAINT;
        }
    }
    // Unused methods declared in home interface for server usage.
    public void addConsumer(QuoteNotificationConsumer consumer, ChannelKey key) {}
    public void removeConsumer(QuoteNotificationConsumer consumer, ChannelKey key) {}
    public void removeConsumer(QuoteNotificationConsumer consumer) {}
}// EOF
