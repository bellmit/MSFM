package com.cboe.util.event.test;

import com.cboe.util.event.*;
import com.cboe.util.channel.*;

import java.io.*;

/**
 * This class
 *
 * @author Derek T. Chambers-Boucher
 * @version
 */

class ChannelListener implements EventChannelListener
{
    static boolean b = false;
    private String name;
    private FileOutputStream out;
    private ListenerProxyQueueControl proxyStats;

    public ChannelListener(String title)
    {
//	super(title);
        name = title;
        try
        {
            out = new FileOutputStream(new File("./", title + ".out"));
        } catch (Exception e)
        {
        }
    }

    public void setProxy(ListenerProxyQueueControl proxyStats)
    {
        this.proxyStats = proxyStats;
    }

    public String getName()
    {
        return name;
    }

    /**
     * This method
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param event com.cboe.util.channel.ChannelEvent
     */
    public void channelUpdate(ChannelEvent event)
    {
        try
        {
            out.write((event.getChannel() + " " + event.getEventData()).getBytes());
            if (proxyStats != null) {
                out.write((" " + proxyStats.getQueueSize() + "," + proxyStats.getMaxQueueSize()).getBytes());
            }
            out.write("\n".getBytes());

            if (proxyStats.getQueueSize() > 100) {
                proxyStats.flushQueue();
            }

//            Thread.sleep(10);
        } catch (Exception e)
        {
        }
    }

    public boolean equals(Object obj)
    {
        return super.equals(obj);
    }

    /**
     * This method
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return int
     */
    public int hashCode()
    {
        return super.hashCode();
    }

    public void run()
    {
/*	try
	{
		//wait();
	}
	catch(InterruptedException e)
	{

	}
*/
    }

}
