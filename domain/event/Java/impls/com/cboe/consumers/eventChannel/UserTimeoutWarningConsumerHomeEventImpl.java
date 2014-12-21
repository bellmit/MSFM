// $Workfile$ com.cboe.consumers.eventChannel.UserTimeoutWarningConsumerHomeEventImpl.java
// $Revision$
/* $Log$
*   Initial Version                             Connie Feng
*   Revision        Increment 6     12/1/00     desaik
*/
//----------------------------------------------------------------------
// Copyright (c) 1999 CBOE. All Rights Reserved.

package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.domain.startup.ClientBOHome;

import com.cboe.infrastructureServices.orbService.*;
import com.cboe.infrastructureServices.eventService.*;
import com.cboe.infrastructureServices.traderService.*;
import com.cboe.exceptions.*;

    /**
     * <b> Description </b>
     * <p>
     *      The User TimeOut Warning Listener class.
     * </p>
     *
     * @author Connie Feng
     * @author Keval Desai
     */
public class UserTimeoutWarningConsumerHomeEventImpl extends ClientBOHome implements IECUserTimeoutWarningConsumerHome {
    private UserTimeoutWarningConsumerIECImpl userTimeoutWarningConsumer;
    private UserTimeoutWarningEventConsumerImpl userTimeoutWarningEvent;
    private EventService eventService;
    private EventChannelFilterHelper eventChannelFilterHelper;
    private final String CHANNEL_NAME = "UserTimeoutWarning";
    /**
     * UserTimeoutWarningConsumerHomeEventImpl constructor comment.
     */
    public UserTimeoutWarningConsumerHomeEventImpl() {
        super();
    }

    public UserTimeoutWarningConsumer create() {
        return find();
    }
    /**
     * Return the UserTimeoutWarning Listener (If first time, create it and bind it to the orb).
     * @return UserTimeoutWarningConsumer
     */
    public UserTimeoutWarningConsumer find() {
        return userTimeoutWarningConsumer;
    }

    public void clientStart()
        throws Exception
    {
        userTimeoutWarningConsumer.create(String.valueOf(userTimeoutWarningConsumer.hashCode()));
        //Every bo object must be added to the container.
        addToContainer(userTimeoutWarningConsumer);

        //The addToContainer call MUST occur prior to creation of the interceptor.
        //      manageObject(orderStatusConsumer);

        if (eventService == null){
            eventService = eventChannelFilterHelper.connectEventService();
        }
        String interfaceRepId = com.cboe.idl.infrastructureServices.infrastructureEvents.UserTimeoutWarningConsumerHelper.id();
        // connect to the event channel without filter and add the constraint filter later on
        // using addConstraint - Connie Feng
        eventChannelFilterHelper.connectConsumer( CHANNEL_NAME, interfaceRepId, userTimeoutWarningEvent );
    }

    public void clientInitialize()
        throws Exception
    {
        eventChannelFilterHelper = new EventChannelFilterHelper();
        userTimeoutWarningConsumer = new UserTimeoutWarningConsumerIECImpl();
        userTimeoutWarningEvent = new UserTimeoutWarningEventConsumerImpl(userTimeoutWarningConsumer);
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
            eventChannelFilterHelper.addEventFilter( userTimeoutWarningEvent, channelKey,
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
        switch (channelKey.channelType)
        {
            case ChannelType.USER_SECURITY_TIMEOUT :
                return new StringBuilder(45)
                          .append("acceptUserTimeoutWarning.userName=='")
                          .append(channelKey.key).append("'").toString();
             default :
                Log.alarm(this, "Invalid Channel Type for filtering for context: " + channelKey.channelType);
                return EventChannelFilterHelper.NO_EVENTS_CONSTRAINT;
        }
    }

    // Unused methods declared in home interface for server usage.
    public void addConsumer(UserTimeoutWarningConsumer consumer, ChannelKey key) {}
    public void removeConsumer(UserTimeoutWarningConsumer consumer, ChannelKey key) {}
    public void removeConsumer(UserTimeoutWarningConsumer consumer) {}
}// EOF
