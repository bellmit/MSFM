package com.cboe.util;

import java.util.Enumeration;
import java.util.Hashtable;

import com.cboe.util.channel.ChannelListener;

public class ChannelListenerReferenceHelper
{
    protected Hashtable         consumerCollection;
	private static int			DEFAULT_SIZE = 50;
    private static final ChannelKey[] EMPTY_ChannelKey_ARRAY = new ChannelKey[0];

    public ChannelListenerReferenceHelper()
	{
		consumerCollection = new Hashtable(101);
	}

	public ChannelListenerReferenceHelper(int initialSize)
	{
		consumerCollection = new Hashtable(initialSize);
	}

	/**
	* Adds the call back consumer to the collection
	* @param listener the channel listener
	* @param channelKey the channel to subscribe for
	* @author Connie Feng
	*/
	public int addChannelListener(ChannelListener listener, ChannelKey channelKey)
	{

		Hashtable keys = (Hashtable)consumerCollection.get(listener);
        ChannelListenerChannelKeyContainer listenerKey = null;
		if ( keys == null )
		{
			keys = new Hashtable(DEFAULT_SIZE);

			consumerCollection.put(listener, keys);
            listenerKey = new ChannelListenerChannelKeyContainer(listener, channelKey);
			keys.put(channelKey, listenerKey);
    	}
        else
        {
            listenerKey = (ChannelListenerChannelKeyContainer)keys.get(channelKey);

            if ( listenerKey == null )
            {
                listenerKey = new ChannelListenerChannelKeyContainer(listener, channelKey);
			    keys.put(channelKey, listenerKey);
            }
            else
            {
                listenerKey.increase();
            }
        }
        return listenerKey.getReferenceCount();
	}

	/**
	* Finds out if the consumer in conjuction with channelKey
	* already exist in the collection
	* @param listener the consumer
	* @param channelKey the channel to subscribe for
	* @return boolean true if found, else false
	* @author Connie Feng
	*/
	public boolean ifContains(ChannelListener listener, ChannelKey channelKey)
	{
        if (getReferenceCount(listener, channelKey) == 0 )
        {
            return false;
        }
        else
        {
            return true;
        }
	}

	/**
	* Finds out if the consumer already exist in the collection
	* @param listener the consumer
	* @param channelKey the channel to subscribe for
	* @return boolean true if found, else false
	* @author Connie Feng
	*/
	public boolean ifContains(ChannelListener listener)
	{
 		Hashtable keys = (Hashtable)consumerCollection.get(listener);

		if ( keys == null )
		{
			return false;
		}
		else
		{
			return true;
		}
	}

 	/**
	* Finds out the count in conjuction with channelKey
	* already exist in the collection
	* @param listener the consumer
	* @param channelKey the channel to subscribe for
	* @return boolean true if found, else false
	* @author Connie Feng
	*/
	public int getReferenceCount(ChannelListener listener, ChannelKey channelKey)
	{
        ChannelListenerChannelKeyContainer channelContainer = getReferenceCountObject(listener, channelKey);

        if ( channelContainer == null )
        {
            return 0;
        }
        else
        {
            return channelContainer.getReferenceCount();
        }
	}
 	/**
	* Finds out the count in conjuction with channelKey
	* already exist in the collection
	* @param listener the consumer
	* @param channelKey the channel to subscribe for
	* @return boolean true if found, else false
	* @author Connie Feng
	*/
	private ChannelListenerChannelKeyContainer getReferenceCountObject(ChannelListener listener, ChannelKey channelKey)
	{
		Hashtable keys = (Hashtable)consumerCollection.get(listener);

		if ( keys == null )
		{
			return null;
		}
		else
		{
			ChannelListenerChannelKeyContainer channel = (ChannelListenerChannelKeyContainer)keys.get(channelKey);
            return channel;
		}
	}

    /**
    * Removes the consumer and the channel key from the collection
    * @param listener theconsumer
    * @param channelKey the channel to subscribe for
    * @param ifReferenceCount if no reference count the channelKey will be removed without counting the references
    * @author Connie Feng
    */
    public int removeChannelListener (ChannelListener listener, ChannelKey channelKey, boolean ifReferenceCount)
    {
        ChannelListenerChannelKeyContainer listenerKey = getReferenceCountObject(listener, channelKey);
        int count = 0;

        if ( listenerKey != null)
        {
            if (!ifReferenceCount)
            {
                listenerKey.releaseAll();
            }
            else
            {
                count = listenerKey.decrease();
            }
            if (count == 0)
            {
                Hashtable keys = (Hashtable)consumerCollection.get(listener);
                keys.remove(channelKey);
				
				if (keys.size() == 0)
				{
					consumerCollection.remove(listener);
				}
            }
        }
        return count;
    }


    /**
    * Removes the consumer and the channel key from the collection
    * @param listener theconsumer
    * @param channelKey the channel to subscribe for
    * @author Connie Feng
    */
    public int removeChannelListener (ChannelListener listener, ChannelKey channelKey)
    {
        return removeChannelListener(listener, channelKey, true);
    }

    /**
    * Removes the consumer and the channel key from the collection without reference counting
    * @param listener theconsumer
    * @param channelKey the channel to subscribe for
    * @author Connie Feng
    */
    public void removeChannelListener (ChannelListener listener)
    {
        consumerCollection.remove(listener);
    }

    /**
    * Gets all the consumers from the collection
    * @author Connie Feng
    */
	public ChannelListener[] getAllChannelListeners()
	{
		Enumeration enumeration = consumerCollection.keys();

		ChannelListener[] allListeners = new ChannelListener[consumerCollection.size()];
		int i = 0;
		while(enumeration.hasMoreElements())
		{
			allListeners[i++] = (ChannelListener)enumeration.nextElement();
		}

        return allListeners;
	}

    /**
    * Gets all the ChannelKeys from the listener
    * @author Connie Feng
    */
	public ChannelKey[] getAllChannelsForListener(ChannelListener listener)
	{
		Hashtable channelKeys = (Hashtable)consumerCollection.get(listener);

		if ( channelKeys == null )
		{
			return EMPTY_ChannelKey_ARRAY;
		}
		else
		{
			Enumeration enumeration = channelKeys.keys();
			ChannelKey[] allChannelKeys = new ChannelKey[channelKeys.size()];
			int i = 0;
			while(enumeration.hasMoreElements())
			{
				allChannelKeys[i++] = (ChannelKey)enumeration.nextElement();
			}

            return allChannelKeys;
		}
	}
}
