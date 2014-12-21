//
// -----------------------------------------------------------------------------------
// Source file: OrderRoutingConsumerHome.java
//
// PACKAGE: com.cboe.interfaces.internalCallback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.ohsEvents;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;

import com.cboe.util.ChannelKey;

public interface OrderRoutingConsumerHome
{
    /**
     * Name that will be used for this home.
     */
    String HOME_NAME = "OrderRoutingConsumerHome";

    OrderRoutingConsumer find();

    OrderRoutingConsumer create();

    /**
     * Registers consumer as a listener to this channel for events matching key.
     * @param consumer implementation to receive events
     * @param key filtering key
     */
    void addConsumer(OrderRoutingConsumer consumer, ChannelKey key)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException;

    /**
     * Unregisters consumer as a listener to this channel for events matching key.
     * @param consumer implementation to receive events
     * @param key filtering key
     */
    void removeConsumer(OrderRoutingConsumer consumer, ChannelKey key)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException;

    /**
     * Unregisters consumer as a listener to this channel for all events.
     * @param consumer implementation to receive events
     */
    void removeConsumer(OrderRoutingConsumer consumer)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException;
}