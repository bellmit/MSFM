// $Workfile$ com.cboe.consumers.eventChannel.TickerConsumerHomeEventImpl.java
// $Revision$
/* $Log$
*   Initial Version                             Jeff Illian
*   Revision        Increment 6     12/1/00     desaik
*/
//----------------------------------------------------------------------
// Copyright (c) 1999 CBOE. All Rights Reserved.

package com.cboe.consumers.eventChannel;

import java.util.ArrayList;

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
     *      The Ticker Listener class.
     * </p>
     *
     * @author Jeff Illian
     * @author Keval Desai
     */
public class TickerConsumerHomeInProcessImpl extends ClientBOHome implements IECTickerConsumerHome {
    private TickerConsumerIECImpl tickerConsumer;

    /**
     * TickerListenerFactory constructor comment.
     */
    public TickerConsumerHomeInProcessImpl() {
        super();
    }

    public TickerConsumer create() {
        return find();
    }

    /**
     * Return the UnderlyngTicker Listener (If first time, create it and bind it to the orb).
     * @author Jeff Illian
     * @return TickerListener
     */
    public TickerConsumer find() {
        return tickerConsumer;
    }

    public void clientStart() {
        tickerConsumer.create(String.valueOf(tickerConsumer.hashCode()));
        //Every bo object must be added to the container.
        addToContainer(tickerConsumer);
    }

    public void clientInitialize() {
        tickerConsumer = new TickerConsumerIECImpl();
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
    public void addConsumer(TickerConsumer consumer, ChannelKey key) {}
    public void removeConsumer(TickerConsumer consumer, ChannelKey key) {}
    public void removeConsumer(TickerConsumer consumer) {}
    public ArrayList getInvalidSalePrefixesForLargeTrade() {return new ArrayList() ;}
    public ArrayList getValidSessionsForLargeTrade() {return new ArrayList();}

}// EOF
