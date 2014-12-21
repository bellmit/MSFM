package com.cboe.domain.iec;

import com.cboe.util.channel.ChannelEvent;

public interface ChannelEventCache
{
	ChannelEvent getChannelEvent();

	void returnChannelEvent(ChannelEvent event);

	void clear();

}
