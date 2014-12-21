package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.domain.startup.ClientBOHome;

import com.cboe.exceptions.*;
/**
 * @author Jeff Illian
 */


public class ProductStatusConsumerHomeInProcessImpl extends ClientBOHome implements IECProductStatusConsumerHome {
    private ProductStatusConsumerIECImpl productStatusConsumer;

    /**
     * ProductStatusListenerFactory constructor comment.
     */
    public ProductStatusConsumerHomeInProcessImpl() {
        super();
    }

    public ProductStatusConsumer create() {
        return find();
    }

    /**
     * Return the OrderStatus Listener (If first time, create it and bind it to the orb).
     * @author Jeff Illian
     * @return ProductStatusListener
     */
    public ProductStatusConsumer find() {
        return productStatusConsumer;
    }

    public void clientStart()
        throws Exception
    {
        productStatusConsumer.create(String.valueOf(productStatusConsumer.hashCode()));
        //Every bo object must be added to the container.
        addToContainer(productStatusConsumer);
    }

    public void clientInitialize() {
        productStatusConsumer = new ProductStatusConsumerIECImpl();
    }

    /**
     * Adds a filter to the internal event channel. Constraints based on the
     * ChannelKey will be added as well. Do not make call to addConstraints when this method has
     * already being called.
     *
     * @param channelKey the event channel key
     *
     * @author Keith A. Korecky
     */
    public void addFilter (ChannelKey channelKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    }

    /**
     * Removes the event channel filter from the CBOE event channel.
     *
     * @param channelKey the event channel key
     *
     * @author Keith A. Korecky
     */
    public void removeFilter (ChannelKey channelKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    }

    // Unused methods declared in home interface for server usage.
    public void addConsumer(ProductStatusConsumer consumer, ChannelKey key) {}
    public void removeConsumer(ProductStatusConsumer consumer, ChannelKey key) {}
    public void removeConsumer(ProductStatusConsumer consumer) {}

}// EOF
