//
// -----------------------------------------------------------------------------------
// Source file: RegisteredServerEventCollection.java
//
// PACKAGE: com.cboe.internalPresentation.tradingSession;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.tradingSession;

import java.util.*;

import com.cboe.interfaces.internalPresentation.tradingSession.RegisteredServerEvent;

public class RegisteredServerEventCollection
{
    private SortedMap eventMap;
    private ArrayList eventList;
    private Comparator comparator;
    private boolean sortEnabled;
    private static int INITIAL_CAPACITY = 10;

    private RegisteredServerEventCollection()
    {
        super();
        comparator = null;
        setSortEnabled(false);
    }

    public RegisteredServerEventCollection(Comparator comparator)
    {
        this();
        setComparator(comparator);
        setSortEnabled(true);
        initialize();
    }

    public void setSortEnabled(boolean enabled)
    {
        sortEnabled = enabled;
    }

    public boolean isSortEnabled()
    {
        return sortEnabled;
    }

    public synchronized RegisteredServerEvent get(int index)
    {
        return ( RegisteredServerEvent )eventList.get(index);
    }

    public synchronized RegisteredServerEvent[] getModelSequence()
    {
        RegisteredServerEvent[] models = new RegisteredServerEvent[getRowCount()];

        models = ( RegisteredServerEvent[])eventList.toArray(models);

        return models;
    }

    public synchronized int getRowCount()
    {
        return eventList.size();
    }

    public synchronized void clear()
    {
        eventMap.clear();
        eventList.clear();
    }

    public synchronized boolean containsValue(RegisteredServerEvent element)
    {
        return eventMap.containsValue(element);
    }

    public synchronized boolean containsKey(RegisteredServerEvent element)
    {
//        return eventMap.containsKey(element.getKey());
        for (Iterator i = eventMap.keySet().iterator(); i.hasNext();)
        {
            RegisteredServerEvent event = (RegisteredServerEvent)i.next();
            if (event.getKey().equals(element.getKey()))
            {
                return true;
            }
        }

        return false;
    }

    public synchronized void put(RegisteredServerEvent element)
    {
        if(element != null)
        {
            int index;
            boolean isAdd = !containsKey(element);
            eventMap.put(element.getKey(), element);
            if (isSortEnabled())
            {
                index = eventMapIndexOf(element);
                if (index > -1)
                {
                    if (isAdd)
                    {
                        eventList.add(index, element);
                    }
                    else
                    {
                        eventList.set(index, element);
                    }
                }
            }
            else
            {
                // Sort is OFF
                if (isAdd)
                {
                    eventList.add(element);
                    index = eventListIndexOf(element);
                }
                else
                {
                    index = eventListIndexOf(element);
                    if (index > -1)
                    {
                        eventList.set(index, element);
                    }
                }

            }
        }
    }

    private void setComparator(Comparator comparator)
    {
        this.comparator = comparator;
    }

    private Comparator getComparator()
    {
        return comparator;
    }

    private void initialize()
    {
        eventList = new ArrayList(INITIAL_CAPACITY);
        if (getComparator() != null)
        {
            eventMap = new TreeMap(getComparator());
        }
        else
        {
            eventMap = new TreeMap();
        }
    }

    private int eventMapIndexOf(RegisteredServerEvent element)
    {
        RegisteredServerEvent collectionElement;
        int index = 0;
        for (Iterator i = eventMap.values().iterator(); i.hasNext(); index++)
        {
            collectionElement = ( RegisteredServerEvent )i.next();
            if (collectionElement.equals(element))
            {
                break;
            }
        }

        if (index >= eventMap.size())
        {
            index = -1;
        }
        return index;
    }

    private int eventListIndexOf(RegisteredServerEvent element)
    {
        RegisteredServerEvent collectionElement;
        int index;

        for (index = 0; index < eventList.size(); index++)
        {
            collectionElement = ( RegisteredServerEvent )eventList.get(index);
            if (collectionElement.equals(element))
            {
                break;
            }
        }

        if (index >= eventList.size())
        {
            index = -1;
        }

        return index;
    }
}
