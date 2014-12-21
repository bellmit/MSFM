/*
 * Created by IntelliJ IDEA.
 * User: huange
 * Date: Oct 11, 2002
 * Time: 12:16:59 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.consumers.eventChannel;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.interfaces.events.IECNBBOAgentAdminConsumerHome;
import com.cboe.interfaces.events.NBBOAgentAdminConsumer;
import com.cboe.util.ChannelKey;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;

public class NBBOAgentAdminConsumerHomeInProcessImpl extends ClientBOHome implements IECNBBOAgentAdminConsumerHome {
    private NBBOAgentAdminConsumerIECImpl nbboAgentAdminConsumer;

    /**
     * NBBOAgentAdminConsumerHomeEventImpl constructor comment.
     */
    public NBBOAgentAdminConsumerHomeInProcessImpl() {
        super();
    }

    public NBBOAgentAdminConsumer create() {
        return find();
    }
    /**
     * Return the NBBOAgentAdmin Listener
     * @author Emily Huang
     * @return NBBOAgentAdminListener
     */
    public NBBOAgentAdminConsumer find() {
        return nbboAgentAdminConsumer;
    }

    public void clientStart()
        throws Exception
    {
        nbboAgentAdminConsumer.create(String.valueOf(nbboAgentAdminConsumer.hashCode()));
        //Every bo object must be added to the container.
        addToContainer(nbboAgentAdminConsumer);
    }

    public void clientInitialize() {
        nbboAgentAdminConsumer = new NBBOAgentAdminConsumerIECImpl();
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
    public void addConsumer(NBBOAgentAdminConsumer consumer, ChannelKey key) {}
    public void removeConsumer(NBBOAgentAdminConsumer consumer, ChannelKey key) {}
    public void removeConsumer(NBBOAgentAdminConsumer consumer) {}

}// EOF