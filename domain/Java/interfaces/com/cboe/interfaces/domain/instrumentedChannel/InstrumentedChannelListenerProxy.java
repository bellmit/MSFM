/**
 * @author Jing Chen
 */
package com.cboe.interfaces.domain.instrumentedChannel;

import com.cboe.util.channel.ChannelListenerProxy;

public interface InstrumentedChannelListenerProxy extends ChannelListenerProxy, Instrumentation, InstrumentationUserData
{
}
