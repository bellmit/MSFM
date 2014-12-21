//
// -----------------------------------------------------------------------------------
// Source file: ActionThreaderPoolImpl.java
//
// PACKAGE: com.cboe.presentation.common.threading
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.threading;

import java.util.*;

import com.cboe.interfaces.presentation.common.threading.ActionThreaderPool;
import com.cboe.interfaces.presentation.common.threading.ActionThreader;

import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;

public class ActionThreaderPoolImpl implements ActionThreaderPool
{
    public static final String PROPERTY_SECTION_SIZE = "Defaults";
    public static final String PROPERTY_NAME_SIZE = "IECActionServicers";
    public static final int DEFAULT_POOL_SIZE = 10;
    public static final int SMALLEST_POOL_SIZE = 1;//must not be less than 1

    protected ActionThreader singleActionThreader;
    protected ArrayList actionThreaderList;
    protected Map actionThreaderCounterMap;
    protected Map sharedActionThreaders;

    private int currentActionThreader;

    private int poolSize;
    private boolean useSingleThreader;

    public ActionThreaderPoolImpl()
    {
        super();
        poolSize = 1;
        currentActionThreader = 0;
        useSingleThreader = true;

        initializePoolSize();
        createPool();
    }

    /**
     * Notifies the pool that the ActionThreader has completed its execution and is available for reuse.
     */
    public synchronized void actionThreaderCompleted(ActionThreader threader)
    {
        ActionThreaderCountReference counter = (ActionThreaderCountReference)actionThreaderCounterMap.get(threader);
        if(counter != null)
        {
            counter.decreaseCounter();
        }
    }

    /**
     * Gets the next available ActionThreader from the pool to be used
     */
    public ActionThreader getNextAvailableActionThreader()
    {
        ActionThreader threader;

        if(useSingleThreader)
        {
            threader = singleActionThreader;
        }
        else
        {
            synchronized(this)
            {
                threader = getNextActionThreader();
                ActionThreaderCountReference counter = (ActionThreaderCountReference) actionThreaderCounterMap.get(threader);
                // try to get the next Available thread from the pool; if they all have count > 0, just return the next one
                for(int i=0; i<poolSize && counter.getCounter() > 0; i++)
                {
                    threader = getNextActionThreader();
                    // get the counter for the ActionThreader
                    counter = (ActionThreaderCountReference) actionThreaderCounterMap.get(threader);
                }
                // increment the counter for the threader being returned
                counter.increaseCounter();
            }
        }
        return threader;
    }

    /**
     * Gets the next available ActionThreader from the pool to be used.
     * @param sharedReference used to either find the same instance of a previous
     * @exception IllegalArgumentException <code>sharedReference</code> cannot be null
     */
    public ActionThreader getNextAvailableActionThreader(Object sharedReference)
    {
        ActionThreader threader;

        if(sharedReference == null)
        {
            throw new IllegalArgumentException("sharedReference may not be null.");
        }

        if(useSingleThreader)
        {
            threader = singleActionThreader;
        }
        else
        {
            ActionThreaderCountReference counter;

            synchronized(sharedReference)
            {
                counter = (ActionThreaderCountReference)sharedActionThreaders.get(sharedReference);

                if(counter == null)
                {
                    threader = getNextAvailableActionThreader();
                    counter = new ActionThreaderCountReference(threader);
                    sharedActionThreaders.put(sharedReference, counter);
                }
                else
                {
                    threader = counter.getActionThreader();
                }

                counter.increaseCounter();
            }
        }
        return threader;
    }

    public void decrementSharedReferenceCount(Object sharedReference)
    {
        ActionThreaderCountReference counter;

        synchronized(sharedReference)
        {
            counter = (ActionThreaderCountReference)sharedActionThreaders.get(sharedReference);

            if(counter != null)
            {
                int currentCount = counter.decreaseCounter();
                if(currentCount <= 0)
                {
                    sharedActionThreaders.remove(sharedReference);
                }
            }
        }
    }

    public void incrementSharedReferenceCount(Object sharedReference)
    {
        ActionThreaderCountReference counter;

        synchronized(sharedReference)
        {
            counter = (ActionThreaderCountReference)sharedActionThreaders.get(sharedReference);

            if(counter != null)
            {
                counter.increaseCounter();
            }
        }
    }

    private void initializePoolSize()
    {
        if(AppPropertiesFileFactory.isAppPropertiesAvailable())
        {
            String sizeString = AppPropertiesFileFactory.find().getValue(PROPERTY_SECTION_SIZE, PROPERTY_NAME_SIZE);
            if(sizeString != null && sizeString.length() > 0)
            {
                try
                {
                    poolSize = Integer.parseInt(sizeString);
                    if(poolSize < SMALLEST_POOL_SIZE)
                    {
                        poolSize = DEFAULT_POOL_SIZE;
                    }
                }
                catch(NumberFormatException e)
                {
                    StringBuffer msg = new StringBuffer(50);
                    msg.append("Invalid property value for ").append(PROPERTY_SECTION_SIZE);
                    msg.append('.').append(PROPERTY_NAME_SIZE).append('=').append(sizeString).append(". ");
                    msg.append("Setting to default=").append(DEFAULT_POOL_SIZE).append('.');

                    GUILoggerHome.find().exception(msg.toString(), e);
                    poolSize = DEFAULT_POOL_SIZE;
                }
            }
            else
            {
                poolSize = DEFAULT_POOL_SIZE;
            }
        }
        else
        {
            poolSize = DEFAULT_POOL_SIZE;
        }

        if(poolSize == 1)
        {
            useSingleThreader = true;
        }
        else
        {
            useSingleThreader = false;
        }
    }

    private void createPool()
    {
        if(useSingleThreader)
        {
            singleActionThreader = new ActionThreaderImpl(this);
        }
        else
        {
            actionThreaderList = new ArrayList(poolSize);
            actionThreaderCounterMap = new HashMap(poolSize);
            for(int i = 0; i < poolSize; i++)
            {
                ActionThreader threader = new ActionThreaderImpl(this);
                actionThreaderList.add(threader);
                actionThreaderCounterMap.put(threader, new ActionThreaderCountReference(threader));
            }

            sharedActionThreaders = new HashMap(101);
        }
    }

    // convenience method to get the next ActionThreader from the list
    private ActionThreader getNextActionThreader()
    {
        ActionThreader threader = (ActionThreader) actionThreaderList.get(currentActionThreader++);
        if(currentActionThreader >= poolSize)
        {
            currentActionThreader = 0;
        }
        return threader;
    }
}