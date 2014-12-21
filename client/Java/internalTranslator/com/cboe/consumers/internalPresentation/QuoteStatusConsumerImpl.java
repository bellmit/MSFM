package com.cboe.consumers.internalPresentation;

import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.interfaces.internalCallback.*;

/**
 * This is the implementation of the CMIQuoteStatusConsumer callback object which
 * receives quote status data from a callback supplier on the CAS and publishes it
 * on a designated event channel.
 *
 * @author Derek T. Chambers-Boucher
 * @version 03/16/1999
 */

public class QuoteStatusConsumerImpl extends com.cboe.consumers.callback.QuoteStatusConsumerImpl implements QuoteStatusConsumer
{
    /**
     * QuoteStatusConsumerImpl constructor.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param channelType the channel type to publish on.
     * @param eventChannel the event channel to publish to.
     */
    public QuoteStatusConsumerImpl(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
    }
}
