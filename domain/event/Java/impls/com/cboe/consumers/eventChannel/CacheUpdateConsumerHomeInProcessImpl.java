package com.cboe.consumers.eventChannel;

/**
 * @author Jimmy Wang
 */

import com.cboe.interfaces.events.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.exceptions.*;

public class CacheUpdateConsumerHomeInProcessImpl extends ClientBOHome implements IECCacheUpdateConsumerHome {
    private CacheUpdateConsumerIECImpl cacheUpdateConsumer;

    /**
     * CacheUpdateConsumerHomeEventImpl constructor comment.
     */
    public CacheUpdateConsumerHomeInProcessImpl() {
        super();
    }

    public CacheUpdateConsumer create() {
        return find();
    }

    /**
     * Return the CacheUpdateConsumer  (If first time, create it and bind it to the orb).
     * @author Connie Feng
     * @return CacheUpdateConsumer
     */
    public CacheUpdateConsumer find() {
        return cacheUpdateConsumer;
    }

    public void clientStart() {
        cacheUpdateConsumer.create(String.valueOf(cacheUpdateConsumer.hashCode()));
        //Every bo object must be added to the container.
        addToContainer(cacheUpdateConsumer);
    }

    public void clientInitialize() {
        cacheUpdateConsumer = new CacheUpdateConsumerIECImpl();
    }

    /**
     * Adds the event channel listener to the internal event channel. Constraints based on the
     * ChannelKey will be added as well. Do not make call to addConstraints when this method has
     * already being called.
     *
     * @param channelListener event channel listener
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     */
    public void addFilter (ChannelKey channelKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

    }// end of addEventChannelListener

    /**
     * Removes the event channel listener to the internal event channel and the CBOE event channel.
     *
     * @param channelListener event channel listener
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     */
    public void removeFilter (ChannelKey channelKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

    }// end of removeEventChannelListener

    // Unused methods declared in home interface for server usage.
    public void addConsumer(CacheUpdateConsumer consumer, ChannelKey key) {}
    public void removeConsumer(CacheUpdateConsumer consumer, ChannelKey key) {}
    public void removeConsumer(CacheUpdateConsumer consumer) {}

}// EOF
