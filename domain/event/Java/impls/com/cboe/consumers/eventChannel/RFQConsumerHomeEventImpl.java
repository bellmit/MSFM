// $Workfile$ com.cboe.consumers.eventChannel.RFQConsumerHomeEventImpl.java
// $Revision$
/* $Log$
*   Initial Version                             Jeff Illian
*   Revision        Increment 6     12/1/00     desaik
*/
//----------------------------------------------------------------------
// Copyright (c) 1999 CBOE. All Rights Reserved.

package com.cboe.consumers.eventChannel;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.IECRFQConsumerHome;
import com.cboe.interfaces.events.RFQConsumer;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

    /**
     * <b> Description </b>
     * <p>
     *      The RFQ Listener class.
     * </p>
     *
     * @author Jeff Illian
     * @author Keval Desai
     */
public class RFQConsumerHomeEventImpl extends ClientBOHome implements IECRFQConsumerHome {
    private RFQEventConsumerInterceptor rfqEventConsumerInterceptor;
    private RFQEventConsumerImpl rfqEvent;
    private EventService eventService;
    private EventChannelFilterHelper eventChannelFilterHelper;
    private final String CHANNEL_NAME = "RFQ";

    /**
     * RFQConsumerHomeEventImpl constructor comment.
     */
    public RFQConsumerHomeEventImpl() {
        super();
    }

    public RFQConsumer create() {
        return find();
    }
    /**
     * Return the RFQ Listener (If first time, create it and bind it to the orb).
     * @author Jeff Illian
     * @return RFQListener
     */
    public RFQConsumer find() {
        return rfqEventConsumerInterceptor;
    }

    public void clientStart()
        throws Exception
    {
        if (eventService == null){
            eventService = eventChannelFilterHelper.connectEventService();
        }
        String interfaceRepId = com.cboe.idl.events.RFQEventConsumerHelper.id();
        // connect to the event channel without filter and add the constraint filter later on
        // using addConstraint - Connie Feng
        eventChannelFilterHelper.connectConsumer( CHANNEL_NAME, interfaceRepId, rfqEvent );
    }

    public void clientInitialize()
        throws Exception
    {
        eventChannelFilterHelper = new EventChannelFilterHelper();
        RFQConsumerIECImpl rfqConsumer = new RFQConsumerIECImpl();
        rfqConsumer.create(String.valueOf(rfqConsumer.hashCode()));
        //Every bo object must be added to the container.
        addToContainer(rfqConsumer);

        rfqEventConsumerInterceptor = new RFQEventConsumerInterceptor(rfqConsumer);
        if(getInstrumentationEnablementProperty())
        {
            rfqEventConsumerInterceptor.startInstrumentation(getInstrumentationProperty());
        }
        rfqEvent = new RFQEventConsumerImpl(rfqEventConsumerInterceptor);
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
        addConstraint(channelKey);
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
        if ( find() != null )
        {
            String constraintString = getConstraintString(channelKey);

            eventChannelFilterHelper.addEventFilter( rfqEvent, channelKey,
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

        switch (channelKey.channelType)
        {
            case ChannelType.RFQ :
                return new StringBuilder(100)
                          .append("acceptRFQ.rfq.productKeys.classKey==").append(key.getKey())
                          .append(" and $.acceptRFQ.rfq.sessionName=='").append(key.getSessionName()).append("'")
                          .toString();
            default :
                Log.alarm(this, "Invalid Channel Type for filtering for context: " + channelKey.channelType);
                return EventChannelFilterHelper.NO_EVENTS_CONSTRAINT;
        }
    } // end of getParmName

    // Unused methods declared in home interface for server usage.
    public void addConsumer(RFQConsumer consumer, ChannelKey key) {}
    public void removeConsumer(RFQConsumer consumer, ChannelKey key) {}
    public void removeConsumer(RFQConsumer consumer) {}
}// EOF
