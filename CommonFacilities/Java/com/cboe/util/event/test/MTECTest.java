package com.cboe.util.event.test;

/**
 * This class
 *
 * @author Derek T. Chambers-Boucher
 * @version
 */

import java.util.*;
import com.cboe.util.event.*;
import com.cboe.util.channel.*;

public class MTECTest
{
	//private ChannelTest eventChannel = new ChannelTest();
    static EventChannelAdapter eventChannel;

	static String ch1;
	static String ch2;
	static String ch3;
	static String ch4;
	static String ch5;

    static String[] channelNames;
    static ChannelListener[] listeners;

    public static void main(String[] args) throws Exception
    {
        eventChannel = EventChannelAdapterFactory.find();
        eventChannel.setDynamicChannels(true);

        System.out.println("Creating channel names\n");
        createChannelNames(5);  // Create 5 separate channels

        System.out.println("Creating channel listeners\n");
        createListeners(5);     // Create 5 listeners

        System.out.println("Adding listeners to the event channel\n");
        addListeners(0, 0, 0);  // Add listener #0 to channel #0
        addListeners(1, 0, 2);  // Add listeners #0-2 to channel #1
        addListeners(2, 1, 3);  // Add listeners #1-3 to channel #2
        addListeners(3, 2, 4);  // Add listeners #2-4 to channel #3
        addListeners(4, 4, 4);  // Add listener #4 to channel #4

        System.out.println("Creating publishers");
        TestThread t1 = new TestThread(channelNames[0]);
        TestThread t2 = new TestThread(channelNames[1]);
        TestThread t3 = new TestThread(channelNames[2]);
        TestThread t4 = new TestThread(channelNames[3]);
        TestThread t5 = new TestThread(channelNames[4]);

        System.out.println("Starting tests");
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();

        printStatus("Is channel #0? " + eventChannel.isChannel(channelNames[0]));

        pause();
        printStatus("All five channels and all five listeners");

        // Remove the listener #1 - shared by three channels, but should not cause channel cleanup
        removeListener(1);
        printStatus("Is channel #0? " + eventChannel.isChannel(channelNames[0]));
        pause();
        printStatus("Should have all five channels and listeners 0, 2-4");

        // Remove listener #0 - should remove channel 0 as well
        removeListener(0);
        printStatus("Is channel #0? " + eventChannel.isChannel(channelNames[0]));
        pause();
        printStatus("Should have channels 1-4 and listeners 2-4");

        // Remove listener #2 - should remove channel 1 as well
        removeListener(2);
        pause();
        printStatus("Should have channels 2-4 and listeners 3-4");

        // Remove channel 4 - should not affect any listeners
        removeChannel(4);
        pause();
        printStatus("Should have channels 2-3 and listeners 3-4");

        // Remove channel 3 - should remove listener 4
        removeChannel(3);
        pause();
        printStatus("Should have channel 2 and listener 3");

        // We should have just channel 2 and listener 3 left at this point
        removeGroup();
        pause();
        printStatus("Everything should be gone now");

        System.out.println("Adding listeners to the event channel\n");
        addListeners(0, 0, 0);  // Add listener #0 to channel #0
        addListeners(1, 1, 2);  // Add listeners #0-2 to channel #1
        addListeners(2, 1, 3);  // Add listeners #1-3 to channel #2
        addListeners(3, 2, 4);  // Add listeners #2-4 to channel #3
        addListeners(4, 4, 4);  // Add listener #4 to channel #4

        // Remove channel 0 - should remove listener 0 too
        removeChannel(0);
        pause();
        printStatus("Should have channels 1-4 and listeners 1-4");

        // Remove everything else
        removeGroup();
        pause();
        printStatus("Everything should be gone now");

        eventChannel.stopChannelAdapter();
        pause();
        printStatus("We've stopped the adapter");
    }

    public static void createChannelNames(int numNames)
    {
        channelNames = new String[numNames];

        for (int i=0; i < numNames; i++)
        {
            channelNames[i] = "Channel Name #"+Integer.toString(i);
            System.out.println("\tCreating "+channelNames[i]);
        }
    }

    public static void createListeners(int numListeners)
    {
        listeners = new ChannelListener[numListeners];

        for (int i=0; i < numListeners; i++)
        {
            listeners[i] = new ChannelListener("Channel Listener #"+Integer.toString(i));
            System.out.println("\tCreating "+listeners[i].getName());
        }
    }

    public static void addListeners(int channel, int start, int end)
    {
        for (int i=start; i < end+1; i++)
        {
            System.out.println("\tAdding "+listeners[i].getName()+" to "+channelNames[channel]);
    	    ListenerProxyQueueControl proxy = eventChannel.addChannelListener(eventChannel, listeners[i], channelNames[channel]);
            listeners[i].setProxy(proxy);
        }
    }

    public static void removeListener(int listener) throws Exception
    {
        if (listeners[listener] == null)
        {
            throw new java.lang.Exception("Test error: Already removed listener #"+listener);
        }

        System.out.println("\tRemoving "+listeners[listener].getName());
        eventChannel.removeChannelListener(listeners[listener]);
        listeners[listener] = null;
    }

    public static void removeChannel(int channel) throws Exception
    {
        if (channelNames[channel] == null)
        {
            throw new java.lang.Exception("Test error: Already removed channel #"+channel);
        }

        System.out.println("\tRemoving "+channelNames[channel]);
        eventChannel.removeChannel(channelNames[channel]);
        channelNames[channel] = null;
    }

    public static void removeGroup()
    {
        System.out.println("\tRemoving the event channel");
        eventChannel.removeListenerGroup(eventChannel);
        channelNames = new String[channelNames.length];
        listeners = new ChannelListener[listeners.length];
    }

    public static void printStatus(String footer)
    {
        ChannelAdapter adapter;
        ChannelListenerProxy proxy;
        List adapters = ChannelAdapterRegistrar.getAdapters();
        for (int i = 0; i < adapters.size(); i++)
        {
            adapter = (ChannelAdapter)adapters.get(i);
            System.out.println("Adapter # " + i + " : " + adapter);
            Map channels = adapter.getRegisteredChannels();
            System.out.println("    adapter current queue size = " + adapter.getQueueSize());
            System.out.println("    adapter max queue size = " + adapter.getMaxQueueSize());
            Object[] channelArray = channels.keySet().toArray();
            for (int j = 0; j < channelArray.length; j++)
            {
                System.out.println("--> channel # " + j + " : " + channelArray[j]);
                List proxies = (List)channels.get(channelArray[j]);
                for (int k = 0; k < proxies.size(); k++)
                {
                    proxy = (ChannelListenerProxy)proxies.get(k);
                    System.out.println("    proxy #"+k);
                    System.out.println("    proxy listener # " + k + " : " + proxy);
                    System.out.println("    proxy delegate # " + k + " : " + proxy.getDelegateListener());
                    if (proxy.getDelegateListener() instanceof com.cboe.util.event.test.ChannelListener)
                    {
                        ChannelListener listener = (ChannelListener) proxy.getDelegateListener();
                        System.out.println("    delegate name " + listener.getName());
                    }
                    System.out.println("        proxy current queue size = " + proxy.getQueueSize());
                    System.out.println("        proxy max queue size = " + proxy.getMaxQueueSize());
                }
            }
        }
        System.out.println("  --  --  --  --  ");
        System.out.println("  "+footer);
        System.out.println("  --  --  --  --  ");
    }

    public static void pause()
    {
        java.lang.Object waiter = new java.lang.Object();

        try {
            synchronized(waiter)
            {
                waiter.wait(5000);
            }
        } catch (Exception e) {
        };
    }
}
