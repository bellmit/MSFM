package com.cboe.consumers.internalPresentation;

import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.interfaces.internalCallback.*;

/**
 * This is the implementation of the CMIClassStatusConsumer callback object which
 * receives class status data from a callback supplier on the CAS and publishes it
 * on a designated event channel.
 *
 * @author Connie Feng
 */

public class ClassStatusConsumerImpl extends com.cboe.consumers.callback.ClassStatusConsumerImpl implements ClassStatusConsumer
{

    /**
     * ClassStatusConsumerImpl constructor.
     *
     * @author Connie Feng
     *
     * @param channelType the channel type to publish on.
     * @param eventChannel the event channel to publish to.
     */
    public ClassStatusConsumerImpl(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
    }

}
