// $Workfile$ com.cboe.consumers.eventChannel.TextMessageConsumerHomeInProcessImpl.java
// $Revision$
/* $Log$
*   Initial Version                             Connie Feng
*   Revision                                    Jeff Illian
*   Revision                                    Keith A. Korecky
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
     *      The Text Message Listener class.
     * </p>
     *
     * @author Connie Feng
     * @author Jeff Illian
     * @author Keith A. Korecky
     * @author Keval Desai
     */
public class TextMessageConsumerHomeInProcessImpl extends ClientBOHome implements IECTextMessageConsumerHome
{

    private TextMessageConsumerIECImpl textMessageConsumer;

   /**
     * TextMessageConsumerHomeEventImpl constructor comment.
     */
    public TextMessageConsumerHomeInProcessImpl()
    {
        super();
    }

    public TextMessageConsumer create()
    {
        return find();
    }

    /**
     * Return the AcceptTextMessageConsumer Listener (If first time, create it and bind it to the orb).
     * @return AcceptTextMessageConsumer
     */
    public TextMessageConsumer find()
    {
        return textMessageConsumer;
    }

    public void clientStart()
    {
        textMessageConsumer.create(String.valueOf(textMessageConsumer.hashCode()));
        addToContainer(textMessageConsumer);
    }

    public void clientInitialize()
    {
        textMessageConsumer = new TextMessageConsumerIECImpl();
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
    public void addConsumer(TextMessageConsumer consumer, ChannelKey key) {}
    public void removeConsumer(TextMessageConsumer consumer, ChannelKey key) {}
    public void removeConsumer(TextMessageConsumer consumer) {}

}// EOF
