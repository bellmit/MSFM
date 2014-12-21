package com.cboe.consumers.internalPresentation;

import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.interfaces.internalCallback.*;

/**
 * This is the implementation of the CMIRFQConsumer callback object which
 * receives request for quote data from a callback supplier on the CAS and publishes it
 * on a designated event channel.
 *
 * @author Derek T. Chambers-Boucher
 * @version 03/16/1999
 */

public class RFQConsumerImpl extends com.cboe.consumers.callback.RFQConsumerImpl implements RFQConsumer
{
    /**
     * RFQConsumerImpl constructor.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param channelType the channel type to publish on.
     * @param eventChannel the event channel to publish to.
     */
    public RFQConsumerImpl(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
    }
}
