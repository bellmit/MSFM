//
// -----------------------------------------------------------------------------------
// Source file: DefaultMessageCollection.java
//
// PACKAGE: com.cboe.presentation.omt
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.omt;

import java.util.*;

import com.cboe.interfaces.presentation.omt.MessageCollection;
import com.cboe.interfaces.presentation.omt.MessageCollectionListener;
import com.cboe.interfaces.presentation.omt.MessageElement;
import com.cboe.interfaces.presentation.util.CBOEId;
 
@SuppressWarnings({"AbstractClassWithoutAbstractMethods"})
public abstract class DefaultMessageCollection implements MessageCollection
{
    protected final List<MessageCollectionListener> listeners =
            new ArrayList<MessageCollectionListener>(5);

    protected final List<MessageElement> elements = new ArrayList<MessageElement>(40);

    //protected final Object elementsLockObject = new Object();
    protected final Object eventProcessingLockObject;

    protected DefaultMessageCollection(Object eventProcessingLockObject)
    {
        this.eventProcessingLockObject = eventProcessingLockObject;
    }
    
    public void addListener(MessageCollectionListener listener)
    {
        synchronized (listeners)
        {
            if (listener != null && !listeners.contains(listener))
            {
                listeners.add(listener);
            }
        }
    }

    /**
     * Add Listener MessageCollectionListener to this collection.
     * 
     * @param listener to add.
     * @param republishMessages flag set to true if willing to get all the messages in the collection republished by the listener. If set to false it 
	 *	will simply add the listener.
     */
    public void addListener(MessageCollectionListener listener, boolean republishMessages){
    	if (listener == null || listeners.contains(listener) == true){
    		return;
    	}
    	synchronized (eventProcessingLockObject){
	    	synchronized (listeners){
	    		listeners.add(listener);
	    		
    			if (republishMessages == true){
    				MessageElement[] msgAry = elements.toArray(new MessageElement[elements.size()]);
    				listener.messageElementAdded(msgAry);
	    		}
	    	}
    	}
    }
    
    public void removeListener(MessageCollectionListener listener)
    {
        synchronized (listeners)
        {
            listeners.remove(listener);
        }
    }

    public MessageElement[] getAllMessageElements()
    {
        MessageElement[] messageElements;
        synchronized (eventProcessingLockObject)
        {
            messageElements = elements.toArray(new MessageElement[elements.size()]);
        }

        return messageElements;
    }

    public int getCount()
    {
        synchronized (eventProcessingLockObject)
        {
            return elements.size();
        }
    }

    public MessageElement getMessageElement(int index)
    {
        synchronized (eventProcessingLockObject)
        {
            return elements.get(index);
        }
    }
    

    protected void addElement(MessageElement element)
    {
        boolean wasAdded = false;
        boolean wasReplaced = false;
        synchronized (eventProcessingLockObject)
        {
            if (elements.contains(element))
            {
                int index = elements.indexOf(element);
                wasReplaced = elements.set(index, element) != null;
            }
            else
            {
                wasAdded = elements.add(element);
            }
        }
        if (wasReplaced)
        {
            fireElementUpdated(element);
        }
        else if (wasAdded)
        {
            fireElementAdded(element);
        }
    }

    protected void addElements(MessageElement[] elementsArray)
    {
        for (MessageElement element : elementsArray)
        {
            addElement(element);
        }
    }

    public void removeMessageElements(List<MessageElement> elements)
    {
        for(MessageElement e : elements)
        {
            removeMessageElement(e);
        }
    }

    public boolean removeMessageElement(MessageElement element)
    {
        boolean wasRemoved;
        synchronized (eventProcessingLockObject)
        {
            wasRemoved = elements.remove(element);
        }
        if (wasRemoved)
        {
            fireElementRemoved(element);
        }
        return wasRemoved;
    }

    protected void fireElementAdded(MessageElement element)
    {
        MessageCollectionListener[] localListeners;
        synchronized (listeners)
        {
            localListeners = listeners.toArray(new MessageCollectionListener[listeners.size()]);
        }
        for (MessageCollectionListener listener : localListeners)
        {
            listener.messageElementAdded(element);
        }
    }

    protected void fireElementRemoved(MessageElement element)
    {
        MessageCollectionListener[] localListeners;
        synchronized (listeners)
        {
            localListeners = listeners.toArray(new MessageCollectionListener[listeners.size()]);
        }
        for (MessageCollectionListener listener : localListeners)
        {
            listener.messageElementRemoved(element);
        }
    }

    protected void fireElementUpdated(MessageElement element)
    {
        MessageCollectionListener[] localListeners;
        synchronized (listeners)
        {
            localListeners = listeners.toArray(new MessageCollectionListener[listeners.size()]);
        }
        for (MessageCollectionListener listener : localListeners)
        {
            listener.messageElementUpdated(element);
        }
    }

    public List<MessageElement> findElements(MessageElement.MessageType[] types, CBOEId id)
    {
        List<MessageElement> list = new ArrayList<MessageElement>(5);
        synchronized(eventProcessingLockObject)
        {
            for(MessageElement element : getAllMessageElements())
            {
                if(element.getCboeId().equals(id))
                {
                    for(MessageElement.MessageType type : types)
                    {
                        if(element.getType().equals(type))
                        {
                            list.add(element);
                        }
                    }
                }
            }
        }
        return list;
    }

    /**
     * find an element based on CBOEId only, regardless of underlying message element type
     *
     */
    public List<MessageElement> findElements(CBOEId id)
    {
        List<MessageElement> list = new ArrayList<MessageElement>(5);
        synchronized(eventProcessingLockObject)
        {
            for(MessageElement element : getAllMessageElements())
            {
                if(element.getCboeId().equals(id))
                {
                    list.add(element);
                }
            }
        }
        return list;
    }
}
