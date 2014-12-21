package com.cboe.application.shared.consumer;

import com.cboe.idl.cmiAdmin.*;
import com.cboe.idl.textMessage.*;
import com.cboe.util.channel.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.*;


/**
 * @author Keith A. Korecky
 */
import com.cboe.util.*;
import com.cboe.util.event.*;

public class AcceptTextMessageProcessor implements EventChannelListener
{

    private AcceptTextMessageCollector parent = null;
    private EventChannelAdapter internalEventChannel = null;

    /**
    * @author Keith A. Korecky
    */
    public AcceptTextMessageProcessor()
    {
        super();
        internalEventChannel = EventChannelAdapterFactory.find();
    }

    /**
    * @author Keith A. Korecky
    */
    public void setParent(AcceptTextMessageCollector parent)
    {
        this.parent = parent;
    }

    /**
    * @author Keith A. Korecky
    */
    public AcceptTextMessageCollector getParent()
    {
        return parent;
    }

    /**
    * @author Keith A. Korecky
    */
    public void channelUpdate(ChannelEvent event)
    {
        MessageTransportStruct  messageTransport    = null;
        ChannelKey              channelKey          = (ChannelKey)event.getChannel();

        if (    (       (channelKey.channelType == ChannelType.TEXT_MESSAGE_BY_USER)
                    ||  (channelKey.channelType == ChannelType.TEXT_MESSAGE_BY_CLASS)
                    ||  (channelKey.channelType == ChannelType.TEXT_MESSAGE_BY_TYPE)
                )
            &&  ( parent != null )
            )
        {
            messageTransport = (MessageTransportStruct)event.getEventData();
            parent.acceptTextMessage( messageTransport.message );
        }
        else
        {
            if (Log.isDebugOn())
            {
                Log.debug("AcceptTexTMessageProcessor -> Wrong Channel : " + channelKey.channelType);
            }
        }
    }
}
