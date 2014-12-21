//
// ------------------------------------------------------------------------
// FILE: RFQV2ConsumerImpl.java
//
// PACKAGE: com.cboe.consumers.callback
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.consumers.callback;

import com.cboe.interfaces.callback.RFQV2Consumer;
import com.cboe.util.event.*;
import com.cboe.util.*;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.idl.cmiQuote.RFQStruct;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;

public class RFQV2ConsumerImpl implements RFQV2Consumer
{
    public static final int LOG_COUNT = 100;
    private EventChannelAdapter eventChannel;
    protected int count;


    public RFQV2ConsumerImpl(EventChannelAdapter eventChannel)
    {
        super();
        this.eventChannel = eventChannel;
        this.count = 0 ;
    }

    public void acceptRFQ(RFQStruct[] rfq, int queueDepth, short queueAction)
    {
        for ( int i=0; i<rfq.length; i++)
        {
            ChannelKey key = new ChannelKey(ChannelType.CB_RFQ, rfq[i].sessionName);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, rfq[i]);
            eventChannel.dispatch(event);

            this.count++;
            if(GUILoggerHome.find().isDebugOn() && this.count % LOG_COUNT == 0 )
            {
                String item = rfq[i].sessionName + "."+ rfq[i].productKeys.productKey;
                GUILoggerHome.find().debug(this.getClass().getName() + ".acceptRFQ()-V2 Count for "+item+" ",
                                           GUILoggerBusinessProperty.COMMON,String.valueOf(this.count));
            }
        }
    }
}
