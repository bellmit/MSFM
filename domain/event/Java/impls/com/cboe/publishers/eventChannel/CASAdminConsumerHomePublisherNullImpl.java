// $Workfile$ com.cboe.consumers.eventChannel.CASAdminConsumerHomePublisherImpl.java
// $Revision$
/* $Log$
*   Initial Version                             Keith A. Korecky
*   Revision        Increment 6     12/1/00     desaik
*/
//----------------------------------------------------------------------
// Copyright (c) 1999 CBOE. All Rights Reserved.

package com.cboe.publishers.eventChannel;

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
import com.cboe.idl.events.CASAdminEventConsumerHelper;
import com.cboe.idl.events.CASAdminEventConsumer;

import com.cboe.consumers.eventChannel.CASAdminEventConsumerImpl;
import com.cboe.consumers.eventChannel.EventChannelFilterHelper;

    /**
     * <b> Description </b>
     * <p>
     *      The Text Message Listener class.
     * </p>
     *
     * @author Jeff Illian
     */
public class CASAdminConsumerHomePublisherNullImpl extends BOHome implements IECCASAdminConsumerHome
{
    private CASAdminEventConsumer casAdminEvent;
    private CASAdminConsumerPublisherImpl casAdminConsumer;
    private EventService eventService;
    private EventChannelFilterHelper eventChannelFilterHelper;
    private final String CHANNEL_NAME = "CASAdmin";

    /**
     * CASAdminConsumerHomePublisherImpl constructor comment.
     */
    public CASAdminConsumerHomePublisherNullImpl()
    {
        super();
    }

    public CASAdminConsumer create()
    {
        return find();
    }

    /**
     * Return the CASAdmin Listener (If first time, create it and bind it to the orb).
     * @return CASAdminConsumer
     */
    public CASAdminConsumer find()
    {
        return casAdminConsumer;
    }

    public void start()
    {
        try
        {
            casAdminConsumer = new CASAdminConsumerPublisherImpl(null);
            casAdminConsumer.create(String.valueOf(casAdminConsumer.hashCode()));
            //Every bo object must be added to the container.
            addToContainer(casAdminConsumer);
        }
        catch (Exception e)
        {
            Log.exception(e);
        }
    }

    public void initialize()
    {
        FoundationFramework ff = FoundationFramework.getInstance();
    	eventService = ff.getEventService();
        eventChannelFilterHelper = new EventChannelFilterHelper();
    }

    /**
     * Adds a  Filter to the internal event channel. Constraints based on the
     * ChannelKey will be added as well. Do not make call to addConstraints when this method has
     * already being called.
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     * @author Jeff Illian
     * @version 12/1/00
     */
    public void addFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        // NO IMPL NEEDED FOR THE PUBLISHER
    }

    /**
     * Removes the event channel Filter from the CBOE event channel.
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     * @author Jeff Illian
     * @version 12/1/00
     */
    public void removeFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        // NO IMPL NEEDED FOR THE PUBLISHER
    }

    public void addConsumer(CASAdminConsumer consumer, ChannelKey key) {}
    public void removeConsumer(CASAdminConsumer consumer, ChannelKey key) {}
    public void removeConsumer(CASAdminConsumer consumer) {}
}// EOF
