/**
 * 
 */
package com.cboe.util.event;

import com.cboe.util.channel.ChannelEvent;

/**
 * This interface defines a method that takes an array of events and
 * return a new blocked event.
 * Added as part of CSI Project. 
 *  
 * @author Gijo Joseph
 *
 */
public interface ChannelEventBlocker {
	int getMaxBlockSize();
	ChannelEvent blockEvents(ChannelEvent[] events);
}
