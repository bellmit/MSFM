package com.cboe.util.channel;

import java.util.*;

public class ChannelAdapterRegistrar
{
    private static List adapters;

    public synchronized static List getAdapters()
    {
        if (adapters == null)
        {
            adapters = new LinkedList();
        }
        return adapters;
    }

    public synchronized static void registerChannelAdapter(ChannelAdapter adapter)
    {
        getAdapters().add(adapter);
    }

    public synchronized static void unregisterChannelAdapter(ChannelAdapter adapter)
    {
        getAdapters().remove(adapter);
    }
}
