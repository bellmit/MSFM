//
// -----------------------------------------------------------------------------------
// Source file: OrderRoutingConsumerProxyHomeImpl.java
//
// PACKAGE: com.cboe.application.supplier.proxy
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.application.supplier.proxy;

import com.cboe.idl.ohsConsumers.OrderRoutingConsumer;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;

import com.cboe.interfaces.application.OrderRoutingConsumerProxyHome;
import com.cboe.interfaces.application.SessionManager;

import com.cboe.util.channel.ChannelListener;

import com.cboe.application.supplier.proxy.GMDConsumerProxyHomeImpl;
import com.cboe.domain.supplier.proxy.GMDSupplierProxy;

public class OrderRoutingConsumerProxyHomeImpl
        extends GMDConsumerProxyHomeImpl
        implements OrderRoutingConsumerProxyHome
{
    public ChannelListener create(OrderRoutingConsumer consumer, SessionManager sessionManager,
                                  boolean gmd)
            throws DataValidationException, SystemException, CommunicationException,
            AuthorizationException
    {
        OrderRoutingConsumerProxy proxy =
                new OrderRoutingConsumerProxy(consumer, sessionManager, gmd, this);

        processProxy(proxy, sessionManager);
        if(getInstrumentationEnablementProperty())
        {
            proxy.startMethodInstrumentation(getInstrumentationProperty());
        }
        return proxy;
    }

    /**
     * Adds this proxy to the GMD maps (if the proxy's GMD flag is 'true').
     */
    public void addGMDProxy(ChannelListener proxy, boolean forUser, Integer classKey)
            throws DataValidationException
    {
        if (proxy instanceof GMDSupplierProxy)
        {
            GMDSupplierProxy gmdProxy = (GMDSupplierProxy) proxy;

            if (gmdProxy.getGMDStatus())
            {
                super.addGMDProxy(forUser, gmdProxy, classKey);
            }
        }
    }

    /**
     * Cleans up the given consumer from the home's maps of registered
     * consumers.
     */
    public void removeGMDProxy(ChannelListener proxy, boolean forUser, Integer classKey)
    {
        if (proxy instanceof GMDSupplierProxy)
        {
            removeGMDProxy(forUser, (GMDSupplierProxy) proxy, classKey);
        }
    }

    /**
     * Handles the common processing of a proxy (things that have to be done
     * regardless of what CMI version the proxy is for).
     */
    private void processProxy(GMDSupplierProxy proxy, SessionManager sessionManager)
    {
        // Every business object must be added to the container BEFORE anything
        // else.
        addToContainer(proxy);

        // Every BObject created MUST have a name if the object is to be a
        // managed object.
        proxy.create(String.valueOf(proxy.hashCode()));
        proxy.initConnectionProperty(getConnectionProperty(sessionManager));
    }
}
