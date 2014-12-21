package com.cboe.consumers.internalPresentation;

import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiSession.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.interfaces.internalCallback.*;

/**
 * This is the implementation of the CMITradingSessionStatusConsumer callback object which
 * receives  market ticker data from a callback supplier on the CAS and publishes it
 * on a designated event channel.
 *
 * @author Connie Feng
 */

public class TradingSessionStatusConsumerImpl extends com.cboe.consumers.callback.TradingSessionStatusConsumerImpl implements TradingSessionStatusConsumer
{
   public TradingSessionStatusConsumerImpl(EventChannelAdapter eventChannel)
   {
      super(eventChannel);
   }

}
