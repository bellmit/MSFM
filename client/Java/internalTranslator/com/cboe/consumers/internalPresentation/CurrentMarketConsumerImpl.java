package com.cboe.consumers.internalPresentation;

import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.interfaces.internalCallback.*;

/**
 * This is the implementation of the CMICurrentMarketConsumer callback object which
 * receives market best data from a callback supplier on the CAS and publishes it
 * on a designated event channel.
 *
 * @author Derek T. Chambers-Boucher
 * @version 03/16/1999
 */

public class CurrentMarketConsumerImpl extends com.cboe.consumers.callback.CurrentMarketConsumerImpl implements CurrentMarketConsumer
{
    public CurrentMarketConsumerImpl(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
    }
}
