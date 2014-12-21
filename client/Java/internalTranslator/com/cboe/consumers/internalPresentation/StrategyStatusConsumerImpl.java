package com.cboe.consumers.internalPresentation;

import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.interfaces.internalCallback.*;

/**
 * This is the implementation of the CMIProductStatusConsumer callback object which
 * receives product status data from a callback supplier on the CAS and publishes it
 * on a designated event channel.
 *
 * @author Connie Feng
 */

public class StrategyStatusConsumerImpl extends com.cboe.consumers.callback.StrategyStatusConsumerImpl implements StrategyStatusConsumer
{

    /**
     * StrategyStatusConsumerImpl constructor.
     *
     * @author Connie Feng
     *
     * @param channelType the channel type to publish on.
     * @param eventChannel the event channel to publish to.
     */
    public StrategyStatusConsumerImpl(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
    }


}
