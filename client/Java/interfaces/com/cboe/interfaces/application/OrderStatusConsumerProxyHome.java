package com.cboe.interfaces.application;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.util.channel.ChannelListener;

/**
 * This is the common interface for the OrderStatusConsumerProxyHome
 * @author Jimmy Wang
 */
public interface OrderStatusConsumerProxyHome extends BaseProxyHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "OrderStatusConsumerProxyHome";

    /**
     * Creates a V1 instance of the OrderStatusQueryProxy.
     */
    public ChannelListener create(
            com.cboe.idl.cmiCallback.CMIOrderStatusConsumer consumer,
            SessionManager sessionManager,
            boolean gmd)
        throws  DataValidationException,
                SystemException,
                CommunicationException,
                AuthorizationException;

    /**
     * Creates a V2 instance of the OrderStatusConsumerProxy.
     */
    public ChannelListener create(
            com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer consumer,
            SessionManager sessionManager,
            boolean gmd)
        throws  DataValidationException,
                SystemException,
                CommunicationException,
                AuthorizationException;

    /**
     * Adds this proxy to the GMD maps (if the proxy's GMD flag is 'true').
     */
    public void addGMDProxy(
            ChannelListener proxy,
            boolean forUser,
            Integer classKey)
        throws DataValidationException;

    /**
     * Cleans up the given consumer from the home's maps of registered
     * consumers.
     */
    public void removeGMDProxy(
            ChannelListener proxy,
            boolean forUser,
            Integer classKey);
}
