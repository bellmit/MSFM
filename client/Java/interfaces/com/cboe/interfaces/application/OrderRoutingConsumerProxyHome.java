//
// -----------------------------------------------------------------------------------
// Source file: OrderRoutingConsumerProxyHome.java
//
// PACKAGE: com.cboe.interfaces.application
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.application;

import com.cboe.idl.ohsConsumers.OrderRoutingConsumer;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;

import com.cboe.util.channel.ChannelListener;

public interface OrderRoutingConsumerProxyHome extends BaseProxyHome
{
    /**
     * Name that will be used for this home.
     */
    String HOME_NAME = "OrderRoutingConsumerProxyHome";

    ChannelListener create(OrderRoutingConsumer consumer, SessionManager sessionManager,
                           boolean gmd)
            throws DataValidationException, SystemException, CommunicationException,
            AuthorizationException;

    /**
     * Adds this proxy to the GMD maps (if the proxy's GMD flag is 'true').
     */
    void addGMDProxy(ChannelListener proxy, boolean forUser, Integer classKey)
            throws DataValidationException;

    /**
     * Cleans up the given consumer from the home's maps of registered
     * consumers.
     */
    void removeGMDProxy(ChannelListener proxy, boolean forUser, Integer classKey);
}
