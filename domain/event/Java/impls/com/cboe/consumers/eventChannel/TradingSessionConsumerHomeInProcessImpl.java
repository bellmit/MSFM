// $Workfile$ com.cboe.consumers.eventChannel.TradingSessionConsumerHomeInProcessImpl.java
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
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.exceptions.*;
import com.cboe.domain.startup.ClientBOHome;

    /**
     * <b> Description </b>
     * <p>
     *      The Trading Session Listener class.
     * </p>
     *
     * @author Jeff Illian
     * @author Keval Desai
     */
public class TradingSessionConsumerHomeInProcessImpl extends ClientBOHome implements IECTradingSessionConsumerHome {
    private TradingSessionConsumerIECImpl tradingSessionConsumer;

    /**
     * TradingSessionConsumerHomeEventImpl constructor comment.
     */
    public TradingSessionConsumerHomeInProcessImpl() {
        super();
    }

    public TradingSessionConsumer create() {
        return find();
    }

    /**
     * Return the TradingSessionConsumer  (If first time, create it and bind it to the orb).
     * @author Connie Feng
     * @return TradingSessionConsumer
     */
    public TradingSessionConsumer find() {
        return tradingSessionConsumer;
    }

    public void clientStart()
        throws Exception
    {
        tradingSessionConsumer.create(String.valueOf(tradingSessionConsumer.hashCode()));
        //Every bo object must be added to the container.
        addToContainer(tradingSessionConsumer);
    }

    public void clientInitialize() {
        tradingSessionConsumer = new TradingSessionConsumerIECImpl();
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
    public void addConsumer(TradingSessionConsumer consumer, ChannelKey key) {}
    public void removeConsumer(TradingSessionConsumer consumer, ChannelKey key) {}
    public void removeConsumer(TradingSessionConsumer consumer) {}

}// EOF
