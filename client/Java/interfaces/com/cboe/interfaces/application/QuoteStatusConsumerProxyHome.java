package com.cboe.interfaces.application;

import com.cboe.util.channel.ChannelListener;
import com.cboe.exceptions.*;
import com.cboe.interfaces.domain.instrumentedChannel.InstrumentedChannelListener;

/**
 * This is the common interface for the QuoteStatusConsumerProxyHome
 * @author Jimmy Wang
 */
public interface QuoteStatusConsumerProxyHome extends BaseProxyHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "QuoteStatusConsumerProxyHome";

    /**
     * Creates a V1 instance of the QuoteStatusQueryProxy.
     */
    public ChannelListener create(
            com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer consumer,
            SessionManager sessionManager,
            boolean gmd)
        throws  DataValidationException,
                SystemException,
                CommunicationException,
                AuthorizationException;

    /**
     * Creates a V2 instance of the QuoteStatusConsumerProxy.
     */
    public ChannelListener create(
            com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer consumer,
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
