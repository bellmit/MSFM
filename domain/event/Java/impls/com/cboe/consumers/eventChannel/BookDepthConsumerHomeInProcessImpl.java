// $Workfile$ com.cboe.consumers.eventChannel.BookDepthConsumerHomeInProcessImpl.java
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
     *      The Book Depth Listener class.
     * </p>
     *
     * @author Tom Lynch
     * @author Jeff Illian
     * @author Keval Desai
     */
public class BookDepthConsumerHomeInProcessImpl extends ClientBOHome implements IECBookDepthConsumerHome {
    private BookDepthConsumerIECImpl bookMarketConsumer;

   /**
     * BookDepthConsumerHomeEventImpl constructor comment.
     */
    public BookDepthConsumerHomeInProcessImpl() {
        super();
    }

    public BookDepthConsumer create() {
        return find();
    }
    /**
     * Return the Current Market Listener (If first time, create it and bind it to the orb).
     * @author Tom Lynch
     * @author Jeff Illian
     * @return BookDepthConsumer
     */
    public BookDepthConsumer find() {
        return bookMarketConsumer;
    }

    public void clientStart()
        throws Exception
    {
        bookMarketConsumer.create(String.valueOf(bookMarketConsumer.hashCode()));
        addToContainer(bookMarketConsumer);
    }

    public void clientInitialize() {
        bookMarketConsumer = new BookDepthConsumerIECImpl();
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
    public void addConsumer(BookDepthConsumer consumer, ChannelKey key) {}
    public void removeConsumer(BookDepthConsumer consumer, ChannelKey key) {}
    public void removeConsumer(BookDepthConsumer consumer) {}

}// EOF
