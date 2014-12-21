// $Workfile$ com.cboe.consumers.eventChannel.UserTimeoutWarningConsumerHomeInProcessImpl.java
// $Revision$
/* $Log$
*   Initial Version                             Connie Feng
*   Revision                                    Jeff Illian
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
     * @author Jeff Illian
     * @author Keval Desai
     */
public class UserTimeoutWarningConsumerHomeInProcessImpl extends ClientBOHome implements IECUserTimeoutWarningConsumerHome {
    private UserTimeoutWarningConsumerIECImpl UserTimeoutWarningConsumer;

   /**
     * UserTimeoutWarningConsumerHomeEventImpl constructor comment.
     */
    public UserTimeoutWarningConsumerHomeInProcessImpl() {
        super();
    }

    public UserTimeoutWarningConsumer create() {
        return find();
    }
    /**
     * Return the UserTimeoutWarningConsumer Listener (If first time, create it and bind it to the orb).
     * @return UserTimeoutWarningConsumer
     */
    public UserTimeoutWarningConsumer find() {
        return UserTimeoutWarningConsumer;
    }

    public void clientStart()
        throws Exception
    {
        UserTimeoutWarningConsumer.create(String.valueOf(UserTimeoutWarningConsumer.hashCode()));
        addToContainer(UserTimeoutWarningConsumer);
    }

    public void clientInitialize() {
        UserTimeoutWarningConsumer = new UserTimeoutWarningConsumerIECImpl();
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
    }

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
    }

    // Unused methods declared in home interface for server usage.
    public void addConsumer(UserTimeoutWarningConsumer consumer, ChannelKey key) {}
    public void removeConsumer(UserTimeoutWarningConsumer consumer, ChannelKey key) {}
    public void removeConsumer(UserTimeoutWarningConsumer consumer) {}

}// EOF
