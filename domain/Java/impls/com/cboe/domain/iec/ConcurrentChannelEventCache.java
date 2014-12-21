package com.cboe.domain.iec;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.cboe.util.channel.ChannelEvent;


public class ConcurrentChannelEventCache implements ChannelEventCache 
{
    protected ConcurrentLinkedQueue<ChannelEvent> queue;

    public ConcurrentChannelEventCache()
    {
    	queue = new ConcurrentLinkedQueue<ChannelEvent>();
    	// initialize the queue some 100 ChannelEvents
    	for (int i=0; i < 100; i++)
    	{
    		queue.add(new ChannelEvent());
    	}
   
    }
    
	public ChannelEvent getChannelEvent()
	{
		ChannelEvent event = queue.poll();
		if (event == null)
		{
			event = new ChannelEvent();
		}
		return event;
	}

	public void returnChannelEvent(ChannelEvent event)
	{
		queue.add(event);
	}

	public void clear()
	{
		queue.clear();
	}

}
