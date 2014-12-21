package com.cboe.util.channel;

import java.util.*;

public class ChannelAdapterReferenceCounter
{
    private Map groups;         // Map of Lists of listeners (group:list of listeners)
    private Map channels;       // Map of Lists of listeners (channel:list of listeners)
    private Map proxyChannels;  // Map of Lists of proxies   (channel:list of proxies)
    private Map listeners;      // Map of Maps (listener:[channel:reference count])
    private Map listenerGroups; // Listener:group linkage
    private Map proxies;        // Listener:proxy

    private static final ChannelListener[] EMPTY_ChannelListener_ARRAY = new ChannelListener[0];

    public ChannelAdapterReferenceCounter()
    {
        groups         = new HashMap();
        channels       = new HashMap();
        proxyChannels  = new HashMap();
        listeners      = new HashMap();
        listenerGroups = new HashMap();
        proxies        = new HashMap();
    }

    private Map findSubMap(Map map, Object key)
    {
        Map subMap = (Map) map.get(key);
        return subMap;
    }

    private Map makeSubMap(Map map, Object key)
    {
        Map subMap = findSubMap(map, key);
        if (subMap == null)
        {
            subMap = new HashMap();
            map.put(key, subMap);
        }
        return subMap;
    }

    private LinkedList findList(Map map, Object key)
    {
        LinkedList list = (LinkedList) map.get(key);
        return list;
    }

    private LinkedList makeList(Map map, Object key)
    {
        LinkedList list = findList(map, key);
        if (list == null)
        {
            list = new LinkedList();
            map.put(key, list);
        }
        return list;
    }

    /**
     * Adds an object to a list that is accessed via a map:key pair. The list is "set-like" in
     * that it only allows a single copy of the element within the list.
     */
    private synchronized void addToList(Map map, Object key, Object element)
    {
        if (element != null)
        {
            LinkedList newList = makeList(map, key);

            if (!newList.contains(element))
            {
                LinkedList clonedNewList = (LinkedList)newList.clone();
                clonedNewList.add(element);
                map.put(key, clonedNewList);
            }
        }
    }

    private void removeFromList(Map map, Object key, Object element)
    {
       if (element != null)
       {
           LinkedList newList = makeList(map, key);
           LinkedList clonedNewList = (LinkedList)newList.clone();
           clonedNewList.remove(element);
           if (clonedNewList.size() == 0)
           {
               map.remove(key);
           }
           else
           {
               map.put(key, clonedNewList);
           }
       }
    }


    private void addListenerToGroup(Object group, ChannelListener listener)
    {
        Object existingGroup = listenerGroups.get(listener);

        // Is this listener already part of a different group?
        if ((existingGroup != null) && (!existingGroup.equals(group)))
        {
            throw new NullPointerException("Listener ["+listener.toString()+"] cannot be part of 2 different groups");
        }
        else
        {
            // Nope, create a new listener-to-group linkage
            listenerGroups.put(listener, group);
        }

        // Add the listener to the group
        addToList(groups, group, listener);
    }

    private void removeListenerFromGroup(Object group, ChannelListener listener)
    {
        // Remove listener-to-group linkage (listener can only be a member of one group)
        listenerGroups.remove(listener);

      // Now we remove the listener from the group

        // Get the set of listeners for this group
        removeFromList(groups, group, listener);
    }

    private void addListenerToChannel(Object channel, ChannelListener listener)
    {
        // Add the listener to the channel
        addToList(channels, channel, listener);
    }

    private void addProxyToChannel(Object channel, ChannelListenerProxy proxy)
    {
        // Add the proxy to the channel
        addToList(proxyChannels, channel, proxy);
    }

    private void removeListenerFromChannel(Object channel, ChannelListener listener)
    {
        // Remove the reference count, if it still exists
        removeListenerReference(listener, channel);

        // Get rid of the listener from the channel
        removeFromList(channels, channel, listener);
    }

    private void removeProxyFromChannel(Object channel, ChannelListenerProxy proxy)
    {
        // Get rid of the listener from the channel
        removeFromList(proxyChannels, channel, proxy);
    }

    private int incrementListenerReference(ChannelListener listener, Object channel)
    {
        Map referenceMap = makeSubMap(listeners, listener);
        Integer refCount = (Integer) referenceMap.get(channel);
        if (refCount == null)
        {
            refCount = Integer.valueOf(1);
        }
        else
        {
            refCount = Integer.valueOf(refCount.intValue()+1);
        }
        referenceMap.put(channel, refCount);
        return refCount.intValue();
    }

    private int decrementListenerReference(ChannelListener listener, Object channel)
    {
        int intCount = 0;
        Map referenceMap = findSubMap(listeners, listener);

        if (referenceMap != null)
        {
            Integer refCount = (Integer) referenceMap.get(channel);
            if (refCount != null)
            {
                intCount = refCount.intValue()-1;

                refCount = Integer.valueOf(intCount);
                referenceMap.put(channel, refCount);
            }
        }
        return intCount;
    }

    private void removeListenerReference(ChannelListener listener, Object channel)
    {
        Map referenceMap = findSubMap(listeners, listener);
        if (referenceMap != null)
        {
            // Remove the channel from the listener->channel map
            referenceMap.remove(channel);

            // If this listener no longer listens to ANY channel, remove it
            if (referenceMap.size() == 0)
            {
                // Remove the listener<-->group linkages
                removeListenerFromGroup(listenerGroups.get(listener), listener);

                // Get rid of the listener
                listeners.remove(listener);

                // Get rid of and clean up its proxy
                removeProxy(listener);
            }
        }
    }

    public synchronized ChannelListenerProxy addChannelListener(Object group, ChannelListener listener, Object channel)
    {
        // Add the listener to this group
        addListenerToGroup(group, listener);

        // Increment the reference count for this listener on this channel
        if (incrementListenerReference(listener, channel) == 1)
        {
            // This is the first reference, so add the listener to this channel
            addListenerToChannel(channel, listener);

            // Add its proxy to the channel as well
            addProxyToChannel(channel, getProxy(listener));
        }

        return getProxy(listener);
    }

    public synchronized ChannelListenerProxy removeChannelListener(Object group, ChannelListener listener, Object channel)
    {
        // Decrement the reference count for this listener - see if we've fallen to zero
        ChannelListenerProxy proxy = getProxy(listener);
        if (decrementListenerReference(listener, channel) == 0)
        {
            // Let the proxy know it's no longer part of this channel
            removeProxyFromChannel(channel, proxy);

            removeListenerFromChannel(channel, listener);
            
            // This may have removed the listener from its last channel, so check for that and
            // clean up the listener and proxy if it did happen
            removeListenerReference(listener, channel);
        }
        
        return proxy;
    }

    public synchronized ChannelListenerProxy removeChannelListener(ChannelListener listener)
    {
        // Remove the listener from its group
        removeListenerFromGroup(listenerGroups.get(listener), listener);

        // Remove the listener and its proxy from all channels
        Object[] array = channels.keySet().toArray();
        ChannelListenerProxy proxy = getProxy(listener);
        for (int i = 0; i < array.length; i++)
        {
            removeProxyFromChannel(array[i], getProxy(listener));
            removeListenerFromChannel(array[i], listener);
        }
        
        return proxy;
    }

    public synchronized void removeChannel(Object channel)
    {
        LinkedList listenerList = findList(channels, channel);

        if (listenerList != null)
        {
            // Remove all listeners on this channel
            ChannelListener[] listeners = (ChannelListener[]) listenerList.toArray(EMPTY_ChannelListener_ARRAY);
            for (int i = 0; i < listeners.length; i++)
            {
                removeProxyFromChannel(channel, getProxy(listeners[i]));
                removeListenerFromChannel(channel, listeners[i]);
                removeListenerReference(listeners[i], channel);
            }
        }
    }

    public synchronized void removeChannelGroup(Object group)
    {
        LinkedList listenerList = findList(groups, group);

        if (listenerList != null)
        {
            Object[] listeners = listenerList.toArray();
            for (int i = 0; i < listeners.length; i++)
            {
                removeChannelListener((ChannelListener) listeners[i]);
            }
        }
    }

    public synchronized void cleanUp()
    {
        Object[] registeredGroups = groups.keySet().toArray();
        for (int i = 0; i < registeredGroups.length; i++)
        {
            removeChannelGroup(registeredGroups[i]);
        }
        groups.clear();
        channels.clear();
        proxyChannels.clear();
        listeners.clear();
        listenerGroups.clear();
    }

    public synchronized ChannelListenerProxy assignProxyToListener(ChannelListener listener, ChannelListenerProxy proxy)
    {
        // We only allow one proxy per listener
        if (!proxies.containsKey(listener))
        {
            proxies.put(listener, proxy);
        }

        return proxy;
    }

    private synchronized void removeProxy(ChannelListener listener)
    {
        ChannelListenerProxy proxy = (ChannelListenerProxy) proxies.remove(listener);
        if (proxy != null)
        {
            proxy.cleanUp();
        }
    }

    public synchronized ChannelListenerProxy getProxy(ChannelListener listener)
    {
        return (ChannelListenerProxy) proxies.get(listener);
    }

    public synchronized boolean previouslyProxied(ChannelListener listener)
    {
        return proxies.containsKey(listener);
    }

    public synchronized boolean isChannel(Object channel)
    {
        return channels.containsKey(channel);
    }

    public synchronized List getListenersForChannel(Object channel)
    {
        return findList(channels, channel);
    }

    public synchronized List getProxiesForChannel(Object channel)
    {
        return findList(proxyChannels, channel);
    }

    public synchronized List getListenersForGroup(Object group)
    {
        return findList(groups, group);
    }

    public synchronized void addChannel(Object channel)
    {
        makeList(channels, channel);
    }

    public synchronized Map getListenerChannels()
    {
        return (Map)((HashMap)channels).clone();
    }

    public synchronized Map getProxyChannels()
    {
        return (Map)((HashMap)proxyChannels).clone();
    }
}
