//
// ------------------------------------------------------------------------
// FILE: LockedQuoteStatusV2ConsumerImpl.java
//
// PACKAGE: com.cboe.consumers.callback
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.consumers.callback;

import com.cboe.interfaces.callback.LockedQuoteStatusV2Consumer;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.idl.cmiQuote.LockNotificationStruct;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;

public class LockedQuoteStatusV2ConsumerImpl implements LockedQuoteStatusV2Consumer
{
    public static final int LOG_COUNT = 100;
    private EventChannelAdapter eventChannel;
    protected int count;

    public LockedQuoteStatusV2ConsumerImpl(EventChannelAdapter eventChannel)
    {
        super();
        this.eventChannel = eventChannel;
        this.count = 0 ;
    }

    public void acceptQuoteLockedReport(LockNotificationStruct[] lockNotification, int queueDepth)
    {
        ChannelKey key = null;
        ChannelEvent event = null;

        // this channel is not keyed.
        key = new ChannelKey(ChannelType.CB_QUOTE_LOCKED_NOTIFICATION, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, lockNotification);
        eventChannel.dispatch(event);

        for (int i = 0; i < lockNotification.length; i++)
        {
            LockNotificationStruct lockNotificationStruct = lockNotification[i];
            key = new ChannelKey(ChannelType.CB_QUOTE_LOCKED_NOTIFICATION_BY_CLASS, new Integer(lockNotificationStruct.classKey));
            event = EventChannelAdapterFactory.find().getChannelEvent(this, key, lockNotificationStruct);
            eventChannel.dispatch(event);

            this.count++;
            if(GUILoggerHome.find().isDebugOn())// TODO: add this back  && this.count % LOG_COUNT == 0 )
            {
                String item = lockNotification[i].sessionName + "."+ lockNotification[i].productKey;
                GUILoggerHome.find().debug(this.getClass().getName() + ".acceptQuoteLockedReport() Count for "+item+" ",
                                           GUILoggerBusinessProperty.QUOTE,String.valueOf(this.count));
            }
        }
    }
}
