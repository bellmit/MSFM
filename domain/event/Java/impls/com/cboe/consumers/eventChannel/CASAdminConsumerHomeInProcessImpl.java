// $Workfile$ com.cboe.consumers.eventChannel.CASAdminConsumerHomeInProcessImpl.java
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

import com.cboe.infrastructureServices.orbService.*;
import com.cboe.infrastructureServices.eventService.*;
import com.cboe.infrastructureServices.traderService.*;
import com.cboe.exceptions.*;
import com.cboe.domain.startup.ClientBOHome;

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
public class CASAdminConsumerHomeInProcessImpl extends ClientBOHome implements IECCASAdminConsumerHome
{

    private CASAdminConsumerIECImpl casAdminConsumer;

   /**
     * CASAdminConsumerHomeEventImpl constructor comment.
     */
    public CASAdminConsumerHomeInProcessImpl()
    {
        super();
    }

    public CASAdminConsumer create()
    {
        return find();
    }

    /**
     * Return the AcceptCASAdminConsumer Listener (If first time, create it and bind it to the orb).
     * @return AcceptCASAdminConsumer
     */
    public CASAdminConsumer find()
    {
        return casAdminConsumer;
    }

    public void clientStart()
        throws Exception
    {
        casAdminConsumer.create(String.valueOf(casAdminConsumer.hashCode()));
        addToContainer(casAdminConsumer);
    }

    public void clientInitialize()
    {
        casAdminConsumer = new CASAdminConsumerIECImpl();
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
    public void addConsumer(CASAdminConsumer consumer, ChannelKey key) {}
    public void removeConsumer(CASAdminConsumer consumer, ChannelKey key) {}
    public void removeConsumer(CASAdminConsumer consumer) {}

}// EOF
