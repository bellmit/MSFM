// $Workfile$ com.cboe.consumers.eventChannel.ForcedLogoutCallbackConsumerHomeImpl.java
// $Revision$
/* $Log$
*   Initial Version                             Mike Pyatetsky
*   Revision        Increment 6     12/1/00     desaik
*/
//----------------------------------------------------------------------
// Copyright (c) 1999 CBOE. All Rights Reserved.

package com.cboe.consumers.eventChannel;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.interfaces.events.IECUserSessionConsumerHome;
import com.cboe.interfaces.events.UserSessionConsumer;
import com.cboe.util.ChannelKey;

    /**
     * <b> Description </b>
     * <p>
     *      The Forced Logout Listener class.
     * </p>
     *
     * @author Mike Pyatetsky
     * @author Keval Desai
     */
public class  ForcedLogoutCallbackConsumerHomeImpl extends ClientBOHome implements IECUserSessionConsumerHome
{
    private UserSessionEventConsumerInterceptor forcedLogoutEventConsumerInterceptor;

   /**
     * forcedLogoutConsumerHomeEventImpl constructor comment.
     */
    public  ForcedLogoutCallbackConsumerHomeImpl()
    {
        super();
    }

    public UserSessionConsumer create() {
        return find();
    }
    /**
     * Return the ForcedLogoutConsumer Listener .
     * @return ForcedLogoutConsumer
     */
    public UserSessionConsumer find() {
        return forcedLogoutEventConsumerInterceptor;
    }

    public void clientStart() {

    }

    public void clientInitialize() {
        ForcedLogoutConsumerIECImpl forcedLogoutConsumer = new ForcedLogoutConsumerIECImpl();
        forcedLogoutConsumer.create(String.valueOf(forcedLogoutConsumer.hashCode()));
        addToContainer(forcedLogoutConsumer);
        forcedLogoutEventConsumerInterceptor = new UserSessionEventConsumerInterceptor(forcedLogoutConsumer);
        if(getInstrumentationEnablementProperty())
        {
            forcedLogoutEventConsumerInterceptor.startInstrumentation(getInstrumentationProperty());
        }
    }

    /**
     * Adds a  Filter to the internal event channel. Constraints based on the
     * ChannelKey will be added as well. Do not make call to addConstraints when this method has
     * already being called.
     *
     * @param channelKey the event channel key
     *
     * @author Mike Pyatetsky
     * @author Keval Desai
     * @version 12/1/00
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
     * @author Mike Pyatetsky
     * @author Keval Desai
     * @version 12/1/00
     */
    public void removeFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    }

    // Unused methods declared in home interface for server usage.
    public void addConsumer(UserSessionConsumer consumer, ChannelKey key) {}
    public void removeConsumer(UserSessionConsumer consumer, ChannelKey key) {}
    public void removeConsumer(UserSessionConsumer consumer) {}

}// EOF
