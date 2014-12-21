/*
 * Created by IntelliJ IDEA.
 * User: huange
 * Date: Oct 9, 2002
 * Time: 12:01:19 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.consumers.eventChannel;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.util.ChannelKey;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.interfaces.events.IECIntermarketOrderStatusConsumerHome;
import com.cboe.interfaces.events.IntermarketOrderStatusConsumer;

public class IntermarketOrderStatusConsumerHomeInProcessImpl extends ClientBOHome implements
        IECIntermarketOrderStatusConsumerHome {
    private IntermarketOrderStatusConsumerIECImpl imOrderConsumer;

    /**
     * RFQConsumerHomeEventImpl constructor comment.
     */
    public IntermarketOrderStatusConsumerHomeInProcessImpl() {
        super();
    }

    public IntermarketOrderStatusConsumer create() {
        return find();
    }

    public IntermarketOrderStatusConsumer find() {
        return imOrderConsumer;
    }

    public void clientStart()
        throws Exception
    {
        imOrderConsumer.create(String.valueOf(imOrderConsumer.hashCode()));
        //Every bo object must be added to the container.
        addToContainer(imOrderConsumer);
    }

    public void clientInitialize() {
        imOrderConsumer = new IntermarketOrderStatusConsumerIECImpl();
    }

    /**
     * Adds a  Filter to the internal event channel. Constraints based on the
     * ChannelKey will be added as well. Do not make call to addConstraints when this method has
     * already being called.
     *
     * @param channelKey the event channel key
     *
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
     */
    public void removeFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    }

    // Unused methods declared in home interface for server usage.
    public void addConsumer(IntermarketOrderStatusConsumer consumer, ChannelKey key) {}
    public void removeConsumer(IntermarketOrderStatusConsumer consumer, ChannelKey key) {}
    public void removeConsumer(IntermarketOrderStatusConsumer consumer) {}
}
