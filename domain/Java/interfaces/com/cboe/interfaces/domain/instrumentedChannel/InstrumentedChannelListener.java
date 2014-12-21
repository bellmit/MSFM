/**
 * @author Jing Chen
 */
package com.cboe.interfaces.domain.instrumentedChannel;

import com.cboe.util.channel.ChannelListener;

public interface InstrumentedChannelListener extends ChannelListener, Instrumentation
{
    public void queueInstrumentationInitiated();
}
