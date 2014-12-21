package com.cboe.consumers.internalPresentation;

import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.interfaces.internalCallback.*;

/**
 * This is the implementation of the CMITickerConsumer callback object which
 * receives  market ticker data from a callback supplier on the CAS and publishes it
 * on a designated event channel.
 *
 * @author Keith A. Korecky
 * @version 06/29/1999
 */

public class TickerConsumerImpl extends com.cboe.consumers.callback.TickerConsumerImpl implements TickerConsumer
{
    /**
     * TickerConsumerImpl constructor.
     *
     * @author Keith A. Korecky
     *
     * @param channelType the channel type to publish on.
     * @param eventChannel the event channel to publish to.
     */
    public TickerConsumerImpl(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
    }
}
