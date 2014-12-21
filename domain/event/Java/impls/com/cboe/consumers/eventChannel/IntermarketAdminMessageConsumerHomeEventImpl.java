package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.*;
import com.cboe.util.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.infrastructureServices.eventService.*;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.domain.util.UserSessionClassContainer;
import com.cboe.exceptions.*;

public class IntermarketAdminMessageConsumerHomeEventImpl extends ClientBOHome implements IECIntermarketAdminMessageConsumerHome
{
    private IntermarketAdminMessageEventConsumerInterceptor imAdminMessageEventConsumerInterceptor;
    private IntermarketAdminMessageEventConsumerImpl            imAdminMessageEvent;
    private EventService                            eventService;
    private EventChannelFilterHelper                eventChannelFilterHelper;
    private final String                            CHANNEL_NAME = "IntermarketAdminMessage";

    public IntermarketAdminMessageConsumerHomeEventImpl()
    {
        super();
    }

    public IntermarketAdminMessageConsumer create()
    {
        return find();
    }

    public IntermarketAdminMessageConsumer find()
    {
        return imAdminMessageEventConsumerInterceptor;
    }

    public void clientStart()
        throws Exception
    {
        if (eventService == null)
        {
            eventService = eventChannelFilterHelper.connectEventService();
        }

        String interfaceRepId = com.cboe.idl.events.IntermarketAdminMessageEventConsumerHelper.id();
        eventChannelFilterHelper.connectConsumer( CHANNEL_NAME, interfaceRepId, imAdminMessageEvent );
    }

    public void clientInitialize()
        throws Exception
    {
        eventChannelFilterHelper = new EventChannelFilterHelper();
        IntermarketAdminMessageConsumerIECImpl imAdminMessageConsumer = new IntermarketAdminMessageConsumerIECImpl();
        imAdminMessageConsumer.create(String.valueOf(imAdminMessageConsumer.hashCode()));
        // Every BO object must be added to a container.
        addToContainer(imAdminMessageConsumer);

        imAdminMessageEventConsumerInterceptor = new IntermarketAdminMessageEventConsumerInterceptor(imAdminMessageConsumer);
        if (getInstrumentationEnablementProperty())
        {
            imAdminMessageEventConsumerInterceptor.startInstrumentation(getInstrumentationProperty());
        }
        imAdminMessageEvent = new IntermarketAdminMessageEventConsumerImpl(imAdminMessageEventConsumerInterceptor);
    }

    public void addFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        addConstraint(channelKey);
    }

    private void addConstraint(ChannelKey channelKey)
        throws SystemException
    {
        if ( find() != null )
        {
            String constraintString = getConstraintString(channelKey);
            eventChannelFilterHelper.addEventFilter( imAdminMessageEvent
                                                    , channelKey
                                                    , eventChannelFilterHelper.getChannelName(CHANNEL_NAME)
                                                    , constraintString
                                                    );
        }
    }// end of addConstraint


    public void removeFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        removeConstraint(channelKey);
    }

    private void removeConstraint(ChannelKey channelKey)
        throws SystemException
    {
        if ( find() != null )
        {
            String constraintString = getConstraintString(channelKey);
            eventChannelFilterHelper.removeEventFilter( channelKey, constraintString);
        }
    }// end of addConstraint

    protected String getConstraintString(ChannelKey channelKey)
    {
        String parm = getParmName(channelKey);

        if ( parm.equals(""))
        {
            return "";
        }

        else
        {
        	StringBuilder buf = new StringBuilder(parm.length()+2);
            buf.append("$.").append(parm);
            return buf.toString();
        }

    }// end of getConstraintString

    protected String getParmName(ChannelKey channelKey)
    {
        switch (channelKey.channelType)
        {
            case ChannelType.INTERMARKET_ADMIN_MESSAGE :
                UserSessionClassContainer key = (UserSessionClassContainer)channelKey.key;
                StringBuffer str = new StringBuffer(125);
                int classesKey = key.getClassKey();
                if (classesKey != 0)
                {
                    str.append("acceptIntermarketAdminMessage.productKey.classKey==");
                    str.append(classesKey);
                    str.append(" and $.");
                }
                str.append("acceptIntermarketAdminMessage.sessionName=='");
                str.append(key.getSessionName());
                str.append("'");
                return str.toString();
            case ChannelType.INTERMARKET_ADMIN_MESSAGE_BROADCAST :
                return "";
            default:
                if (Log.isDebugOn())
                {
                    Log.debug(this, "IntermarketAdminMessageConsumerHomeEventImpl::Unknown channel type: " + channelKey.channelType);
                }
                return "";
        }
    }

    // Unused methods declared in home interface for server usage.
    public void addConsumer(IntermarketAdminMessageConsumer consumer, ChannelKey key) {}
    public void removeConsumer(IntermarketAdminMessageConsumer consumer, ChannelKey key) {}
    public void removeConsumer(IntermarketAdminMessageConsumer consumer) {}
}// EOF