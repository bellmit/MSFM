package com.cboe.consumers.internalPresentation;

import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.interfaces.internalCallback.*;

/**
 * This is the implementation of the CMIExpectedOpeningPrice callback object which
 * receives opening prive data from a callback supplier on the CAS and publishes it
 * on a designated event channel.
 *
 * @author Derek T. Chambers-Boucher
 * @version 06/07/1999
 */

public class ExpectedOpeningPriceConsumerImpl extends com.cboe.consumers.callback.ExpectedOpeningPriceConsumerImpl implements ExpectedOpeningPriceConsumer
{
    /**
     * ExpectedOpeningPriceConsumerImpl constructor.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param channelType the channel type to publish on.
     * @param eventChannel the event channel to publish to.
     */
    public ExpectedOpeningPriceConsumerImpl(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
    }
}
