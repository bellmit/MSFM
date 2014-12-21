// $Workfile$ com.cboe.consumers.eventChannel.CurrentMarketConsumerHomeInProcessImpl.java
// $Revision$
/* $Log$
*   Initial Version                             Tom Lynch
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

import com.cboe.infrastructureServices.orbService.*;
import com.cboe.infrastructureServices.eventService.*;
import com.cboe.infrastructureServices.traderService.*;
import com.cboe.exceptions.*;
import com.cboe.domain.startup.ClientBOHome;

    /**
     * <b> Description </b>
     * <p>
     *      The Current Market Listener class.
     * </p>
     *
     * @author Tom Lynch
     * @author Jeff Illian
     * @author Keval Desai
     */
public class CurrentMarketConsumerHomeInProcessImpl extends ClientBOHome implements IECCurrentMarketConsumerHome {
    private CurrentMarketConsumerIECImpl currentMarketConsumer;

   /**
     * CurrentMarketConsumerHomeEventImpl constructor comment.
     */
    public CurrentMarketConsumerHomeInProcessImpl() {
        super();
    }

    public CurrentMarketConsumer create() {
        return find();
    }
    /**
     * Return the Current Market Listener (If first time, create it and bind it to the orb).
     * @author Tom Lynch
     * @author Jeff Illian
     * @return CurrentMarketConsumer
     */
    public CurrentMarketConsumer find() {
        return currentMarketConsumer;
    }

    public void clientStart()
        throws Exception
    {
        currentMarketConsumer.create(String.valueOf(currentMarketConsumer.hashCode()));
        addToContainer(currentMarketConsumer);
    }

    public void clientInitialize() {
        currentMarketConsumer = new CurrentMarketConsumerIECImpl();
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

    public void activateSubscription(boolean on)
    {
        // todo
    }

    // Unused methods declared in home interface for server usage.
    public void addConsumer(CurrentMarketConsumer consumer, ChannelKey key) {}
    public void removeConsumer(CurrentMarketConsumer consumer, ChannelKey key) {}
    public void removeConsumer(CurrentMarketConsumer consumer) {}

}// EOF
