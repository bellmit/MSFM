// $Workfile$ com.cboe.consumers.eventChannel.TextMessageConsumerHomeEventImpl.java
// $Revision$
/* $Log$
*   Initial Version                             Keith A. Korecky
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
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.IECTextMessageConsumerHome;
import com.cboe.interfaces.events.TextMessageConsumer;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

    /**
     * <b> Description </b>
     * <p>
     *      The Text Message Listener class.
     * </p>
     *
     * @author Jeff Illian
     * @author Keval Desai
     */
public class TextMessageConsumerHomeEventImpl extends ClientBOHome implements IECTextMessageConsumerHome
{
    private TextMessageEventConsumerInterceptor          textMessageEventConsumerInterceptor;
    private TextMessageEventConsumerImpl            textMessageEvent;
    private EventService                            eventService;
    private EventChannelFilterHelper                eventChannelFilterHelper;
    private final String                            CHANNEL_NAME = "TextMessage";

    /**
     * TextMessageConsumerHomeEventImpl constructor comment.
     */
    public TextMessageConsumerHomeEventImpl()
    {
        super();
    }

    public TextMessageConsumer create()
    {
        return find();
    }

    /**
     * Return the TextMessage Listener (If first time, create it and bind it to the orb).
     * @return TextMessageConsumer
     */
    public TextMessageConsumer find()
    {
        return textMessageEventConsumerInterceptor;
    }

    public void clientStart()
        throws Exception
    {
        if (eventService == null)
        {
            eventService = eventChannelFilterHelper.connectEventService();
        }

        String interfaceRepId = com.cboe.idl.events.TextMessageEventConsumerHelper.id();
        // connect to the event channel without filter and add the constraint filter later on
        // using addConstraint - Connie Feng
        eventChannelFilterHelper.connectConsumer( CHANNEL_NAME, interfaceRepId, textMessageEvent );
    }

    public void clientInitialize()
        throws Exception
    {
        eventChannelFilterHelper = new EventChannelFilterHelper();
        TextMessageConsumerIECImpl textMessageConsumer = new TextMessageConsumerIECImpl();
        textMessageConsumer.create(String.valueOf(textMessageConsumer.hashCode()));
        //Every bo object must be added to the container.
        addToContainer(textMessageConsumer);

        textMessageEventConsumerInterceptor = new TextMessageEventConsumerInterceptor(textMessageConsumer);
        if(getInstrumentationEnablementProperty())
        {
            textMessageEventConsumerInterceptor.startInstrumentation(getInstrumentationProperty());
        }
        textMessageEvent = new TextMessageEventConsumerImpl(textMessageEventConsumerInterceptor);
    }

    /**
     * Adds a  Filter to the internal event channel. Constraints based on the
     * ChannelKey will be added as well. Do not make call to addConstraints when this method has
     * already being called.
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     * @author Keval Desai
     * @version 12/1/00
     */
    public void addFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        addConstraint(channelKey);
    }

    /**
     * Adds constraint based on the channel key
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     */
    private void addConstraint(ChannelKey channelKey)
        throws SystemException
    {
        if ( find() != null )
        {
            String constraintString = getConstraintString(channelKey);
            eventChannelFilterHelper.addEventFilter( textMessageEvent
                                                    , channelKey
                                                    , eventChannelFilterHelper.getChannelName(CHANNEL_NAME)
                                                    , constraintString
                                                    );
        }
    }// end of addConstraint

    /**
     * Removes the event channel Filter from the CBOE event channel.
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     * @author Keval Desai
     * @version 12/1/00
     */
    public void removeFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        removeConstraint(channelKey);
    }

    /**
     * Removes constraint based on the channel key
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     */
    private void removeConstraint(ChannelKey channelKey)
        throws SystemException
    {
        if ( find() != null )
        {
            String constraintString = getConstraintString(channelKey);
            eventChannelFilterHelper.removeEventFilter( channelKey, constraintString);
        }
    }// end of addConstraint

    /**
     * Returns the constraint string based on the channel key
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     */
    protected String getConstraintString(ChannelKey channelKey)
    {
        String parm = getParmName(channelKey);

        if(parm.equals(EventChannelFilterHelper.ALL_EVENTS_CONSTRAINT) ||
           parm.equals(EventChannelFilterHelper.NO_EVENTS_CONSTRAINT))
        {
            return parm;
        }
        
        StringBuilder buf = new StringBuilder(parm.length()+2);
        buf.append("$.").append(parm);
        return buf.toString();
    }

    /**
     * Returns the constraint parameter string based on the channel key
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     */
    protected String getParmName(ChannelKey channelKey)
    {
        StringBuilder parmName = new StringBuilder(60);

        switch (channelKey.channelType)
        {
            case ChannelType.TEXT_MESSAGE_BY_USER:
                parmName.append("acceptTextMessageForUser.userId=='").append(channelKey.key).append("'");
                break;

            case ChannelType.TEXT_MESSAGE_BY_CLASS:
                parmName.append("acceptTextMessageForProductClass.classKey==").append(channelKey.key);
                break;

            case ChannelType.TEXT_MESSAGE_BY_TYPE:
                parmName.append("acceptTextMessageForProductClass.productType==").append(channelKey.key);
                break;

            default:
                Log.alarm(this, "Invalid Channel Type for filtering for context: " + channelKey.channelType);
                parmName.append(EventChannelFilterHelper.NO_EVENTS_CONSTRAINT);
                break;
        }

        return parmName.toString();
    }

    // Unused methods declared in home interface for server usage.
    public void addConsumer(TextMessageConsumer consumer, ChannelKey key) {}
    public void removeConsumer(TextMessageConsumer consumer, ChannelKey key) {}
    public void removeConsumer(TextMessageConsumer consumer) {}
}// EOF
