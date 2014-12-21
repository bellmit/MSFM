/**
 * <CODE>ChannelEvent</CODE> consists of three elements:
 * <UL>
 *      <LI> event source
 *      <LI> channel identifier
 *      <LI> and data to be passed in the event.
 * </UL>

 * @author Jeff Illian
 * @author Derek T. Chambers-Boucher
 * @author Gijo Joseph
 * @version 08/03/2009
 */
package com.cboe.util.channel;

import java.util.concurrent.atomic.AtomicInteger;

import com.cboe.util.ChannelKey;

public class ChannelEvent
{
    private volatile Object channel;
    private volatile Object data;
    private volatile Object source;

    // the ChannelAdapter
    protected volatile ChannelAdapter adapter;
    private AtomicInteger listenerCount = new AtomicInteger();

    public ChannelEvent() {
    };

    /**
     * ChannelEvent constructor comment.
     */
    public ChannelEvent(Object source, Object channel, Object data)
    {
        setData(source, channel, data);
    }

    public void setData(Object source, Object channel, Object data) {
        this.source = source;
        this.channel = channel;
        this.data = data;
    }

    public void setChannelAdapter(ChannelAdapter adapter) {
        this.adapter = adapter;
    }

    public void setListenerCount(int listenerCount)
    {
         this.listenerCount.set(listenerCount);
    }

    public void release()
    {
        int cnt = listenerCount.decrementAndGet();
        if ( cnt == 0 )
        {
            returnChannelEvent();
        }
    }

    public void releaseAll()
    {
    	this.listenerCount.set(0);
        returnChannelEvent();
    }

    public int getListenerCount()
    {
        return listenerCount.get();
    }

    private void returnChannelEvent()
    {
        // clean up the data for this event
    	// if adapter is present, the clean up will be performed  by the 
    	// adapter.returnChannelEvent.
    	if ( adapter == null )
    	{
    		setListenerCount(0);
    		setData(null, null, null);
    	}
    	else
        {
            adapter.returnChannelEvent(this);
        }
    }
    /**
     * getChannel returns the hashable "key" that represents the
     * event channel
     *
     * @author Jeff Illian
     */
    public Object getChannel()
    {
        return channel;
    }

    public String toString()
    {
        if (data == null || channel == null || ! (channel instanceof ChannelKey))
        {
            return super.toString();
        }
        ChannelKey channelKey = (ChannelKey) channel;
        String key = channelKey.toString();
        String value = data.toString();
        StringBuilder result = new StringBuilder(key.length()+value.length()+1);
        result.append(key).append(':').append(value);
        return result.toString();
    }

    /**
     * getEventData returns any data element that is being
     * transfered as part of the event.
     *
     * @author Jeff Illian
     */
    public Object getEventData()
    {
        return data;
    }

    public Object getSource() {
        return source;
    }
}
