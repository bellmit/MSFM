package com.cboe.domain.iec;

import com.cboe.util.channel.ChannelEvent;

public class NoPoolingChannelEventCache implements ChannelEventCache 
{
	
	public ChannelEvent getChannelEvent()
	{
		return new ChannelEvent();
	}

	public void returnChannelEvent(ChannelEvent event)
	{
		// Nothing to do
	}

	public void clear()
	{
		// Nothing to do		
	}

}
