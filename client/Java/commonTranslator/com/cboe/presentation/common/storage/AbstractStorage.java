/*
 * Created on Dec 27, 2005
 * -----------------------------------------------------------------------------------
 * Copyright (c) 2005 The Chicago Board Options Exchange. All Rights Reserved.
 *-----------------------------------------------------------------------------------
 */
package com.cboe.presentation.common.storage;

import org.omg.CORBA.UserException;

import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.AuthenticationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.NotSupportedException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.interfaces.presentation.api.TimedOutException;
import com.cboe.interfaces.presentation.common.storage.Storage;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;

public abstract class AbstractStorage implements Storage
{
   public static final ChannelKey channelKeyNew = new ChannelKey(ChannelType.IC_ACCEPT_NEW_ORB_NAME_ALIAS, new Integer(0));
   public static final ChannelKey channelKeyUpdate = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_ORB_NAME_ALIAS, new Integer(0));
   public static final ChannelKey channelKeyDelete = new ChannelKey(ChannelType.IC_ACCEPT_DELETE_ORB_NAME_ALIAS, new Integer(0));
   protected EventChannelAdapter eventChannel;

   public AbstractStorage()
    {
        super();
        eventChannel = EventChannelAdapterFactory.find();
    }

    public void addEntry(Object entry) throws CommunicationException, AlreadyExistsException, DataValidationException, NotAcceptedException, TimedOutException, AuthorizationException, AuthenticationException, NotSupportedException, UserException
    {
         ChannelEvent channelEvent = eventChannel.getChannelEvent(this, channelKeyNew, entry);
         eventChannel.dispatch(channelEvent);
    }

    public void updateEntry(Object entry) throws SystemException, CommunicationException, AlreadyExistsException, DataValidationException, NotAcceptedException, TimedOutException, UserException
    {
         ChannelEvent channelEvent = eventChannel.getChannelEvent(this, channelKeyUpdate, entry);
         eventChannel.dispatch(channelEvent);
    }

    public void removeEntry(Object entry) throws SystemException, CommunicationException, DataValidationException, NotAcceptedException, NotFoundException, TimedOutException, UserException
    {
        // No implementation
    }

}
