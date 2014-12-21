// $Workfile$ com.cboe.consumers.eventChannel.RFQConsumerHomeInProcessImpl.java
// $Revision$
/* $Log$
*   Initial Version                             Jeff Illian
*   Revision        Increment 6     12/1/00     desaik
*/
//----------------------------------------------------------------------
// Copyright (c) 1999 CBOE. All Rights Reserved.

package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.domain.startup.ClientBOHome;

import com.cboe.exceptions.*;

    /**
     * <b> Description </b>
     * <p>
     *      The RFQ Listener class.
     * </p>
     *
     * @author Jeff Illian
     * @author Keval Desai
     */
public class RFQConsumerHomeInProcessImpl extends ClientBOHome implements IECRFQConsumerHome {
    private RFQConsumerIECImpl rfqConsumer;

    /**
     * RFQConsumerHomeEventImpl constructor comment.
     */
    public RFQConsumerHomeInProcessImpl() {
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
        return rfqConsumer;
    }

    public void clientStart()
        throws Exception
    {
        rfqConsumer.create(String.valueOf(rfqConsumer.hashCode()));
        //Every bo object must be added to the container.
        addToContainer(rfqConsumer);
    }

    public void clientInitialize() {
        rfqConsumer = new RFQConsumerIECImpl();
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
    public void addConsumer(RFQConsumer consumer, ChannelKey key) {}
    public void removeConsumer(RFQConsumer consumer, ChannelKey key) {}
    public void removeConsumer(RFQConsumer consumer) {}

}// EOF
