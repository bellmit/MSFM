//
// -----------------------------------------------------------------------------------
// Source file: PropertyServiceFacadeCacheProxy.java
//
// PACKAGE: com.cboe.domain.property
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.property;

import java.util.*;

import com.cboe.idl.property.PropertyGroupStruct;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;

import com.cboe.interfaces.domain.property.PropertyServiceFacade;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelListener;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentHashMap;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * Provides caching as a proxy to another implementation of the PropertyServiceFacade.
 */
public class PropertyServiceFacadeCacheProxy implements PropertyServiceFacade, EventChannelListener
{
    public static final int DEFAULT_CACHE_SIZE = 101;

    protected PropertyServiceFacade forwardingFacade;

    private final Map cacheMap;
    private final Map keyLockObjectMap;

    private final Map selfRegisteredKeys;

    /**
     * Constructs this caching proxy with the facade to forward calls to for API access. This constructor will
     * define the initial cache size using the DEFAULT_CACHE_SIZE attribute.
     * @param forwardingFacade for actual api access
     */
    public PropertyServiceFacadeCacheProxy(PropertyServiceFacade forwardingFacade)
    {
        this(forwardingFacade, DEFAULT_CACHE_SIZE, DEFAULT_CACHE_SIZE);
    }

    /**
     * Constructs this caching proxy with the facade to forward calls to for API access
     * @param forwardingFacade for actual api access
     * @param initialCacheSize initial size of the cache that holds the objects.
     * @param initialKeyCacheSize initial size of the cache that holds the keys of the to the cached objects.
     */
    public PropertyServiceFacadeCacheProxy(PropertyServiceFacade forwardingFacade,
                                           int initialCacheSize, int initialKeyCacheSize)
    {
        if(forwardingFacade == null)
        {
            throw new IllegalArgumentException("forwardingFacade may not be null.");
        }
        this.forwardingFacade = forwardingFacade;

        if(initialCacheSize <= 0 || initialKeyCacheSize <= 0)
        {
            throw new IllegalArgumentException("initialCacheSize & initialKeyCacheSize must be greater than zero");
        }

        cacheMap = new ConcurrentHashMap(initialCacheSize);
        keyLockObjectMap = new HashMap(initialKeyCacheSize);
        selfRegisteredKeys = new HashMap(initialKeyCacheSize);
    }

    /**
     * Retrieves all properties associated with a certain category and property key. The propertyKey will be considered
     * a partial key.
     * @param category of properties
     * @param partialKey to use for partial search
     * @param partialKeySearchType defines how to use the partialKey for a particular search type
     */
    public PropertyServicePropertyGroup[] getPropertyGroupsForPartialKey(String category, String partialKey,
                                                                         short partialKeySearchType)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        return getForwardingFacade().getPropertyGroupsForPartialKey(category, partialKey, partialKeySearchType);
    }

    /**
     * Get the property service property group.
     * @param category of the group to get.
     * @param key for the category to get.
     */
    public PropertyServicePropertyGroup getPropertyGroup(String category, String key)
            throws SystemException, AuthorizationException, CommunicationException, NotFoundException
    {
        PropertyServicePropertyGroup returnGroup;
        String cacheKey = findKeyLockObject(category, key, true);

        synchronized(cacheKey)
        {
            returnGroup = (PropertyServicePropertyGroup) cacheMap.get(cacheKey);

            if(returnGroup == null)
            {
                try
                {
                    registerSelf(category, key);
                    returnGroup = getForwardingFacade().getPropertyGroup(category, key);
                    cacheMap.put(cacheKey, returnGroup);
                }
                catch(DataValidationException e)
                {
                    Log.exception("PropertyServiceFacadeCacheProxy: Could not subscribe for events. Will not add " +
                                  "PropertyServicePropertyGroup to cache.", e);
                    returnGroup = getForwardingFacade().getPropertyGroup(category, key);
                }
            }
        }
        return returnGroup;
    }

    /**
     * Remove a full property group.
     * @param category of the group to remove.
     * @param key in the category of the group to remove.
     */
    public void removePropertyGroup(String category, String key)
            throws SystemException, AuthorizationException, CommunicationException
    {
        //this call to the forwarding facade must be first before the removal from the cache.
        //this call may throw an exception, in which case we do not want to remove from the cache.
        getForwardingFacade().removePropertyGroup(category, key);
        removeFromCache(category, key);
    }

    /**
     * Save a full property group.
     * @param propertyGroup to save
     * @return A refreshed copy of the property group
     */
    public PropertyServicePropertyGroup savePropertyGroup(PropertyServicePropertyGroup propertyGroup)
            throws SystemException, AuthorizationException, CommunicationException
    {
        PropertyServicePropertyGroup savedGroup = getForwardingFacade().savePropertyGroup(propertyGroup);
        return savedGroup;
    }

    /**
     * Subscribe to property change events. The event delivery implementation for this method MUST support delivering
     * the events in the order that subscribers were registered. In some cases, this means that a FIFO collection must
     * be used. The first subscriber must be the first one to receive the event. The events should NOT be delivered
     * asynchronously. An event delivery to a subscriber should complete before the next subscriber receives the event.
     * @param category to subscribe to events for.
     * @param listener object for sending events to.
     */
    public void subscribe(String category, EventChannelListener listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        registerSelf(category);
        getForwardingFacade().subscribe(category, listener);
    }

    /**
     * Unsubscribe to property change events. The event delivery implementation for this method MUST support delivering
     * the events in the order that subscribers were registered. In some cases, this means that a FIFO collection must
     * be used. The first subscriber must be the first one to receive the event. The events should NOT be delivered
     * asynchronously. An event delivery to a subscriber should complete before the next subscriber receives the event.
     * @param category to unsubscribe to events for.
     * @param listener object to unsubscribe, that will stop receiving events.
     */
    public void unsubscribe(String category, EventChannelListener listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        getForwardingFacade().unsubscribe(category, listener);
    }

    /**
     * Subscribe to property change events. The event delivery implementation for this method MUST support delivering
     * the events in the order that subscribers were registered. In some cases, this means that a FIFO collection must
     * be used. The first subscriber must be the first one to receive the event. The events should NOT be delivered
     * asynchronously. An event delivery to a subscriber should complete before the next subscriber receives the event.
     * @param category to subscribe to events for.
     * @param propertyKey to subscribe to events for.
     * @param listener object for sending events to.
     */
    public void subscribe(String category, String propertyKey, EventChannelListener listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        registerSelf(category, propertyKey);
        getForwardingFacade().subscribe(category, propertyKey, listener);
    }

    /**
     * Unsubscribe to property change events. The event delivery implementation for this method MUST support delivering
     * the events in the order that subscribers were registered. In some cases, this means that a FIFO collection must
     * be used. The first subscriber must be the first one to receive the event. The events should NOT be delivered
     * asynchronously. An event delivery to a subscriber should complete before the next subscriber receives the event.
     * @param category to unsubscribe to events for.
     * @param propertyKey to unsubscribe to events for.
     * @param listener object to unsubscribe, that will stop receiving events.
     */
    public void unsubscribe(String category, String propertyKey, EventChannelListener listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        getForwardingFacade().unsubscribe(category, propertyKey, listener);
    }

    /**
     * Handles events by either updating or removing the cached PropertyServicePropertyGroup.
     * @param event
     */
    public void channelUpdate(ChannelEvent event)
    {
        ChannelKey channelKey = (ChannelKey) event.getChannel();

        if(channelKey.channelType == ChannelType.REMOVE_PROPERTY)
        {
            String category = channelKey.key.toString();
            String key = (String) event.getEventData();
            removeFromCache(category, key);
        }
        else if(channelKey.channelType == ChannelType.UPDATE_PROPERTY)
        {
            PropertyGroupStruct struct = (PropertyGroupStruct) event.getEventData();
            PropertyServicePropertyGroup group = PropertyFactory.createPropertyGroup(struct);
            updateCache(group);
        }
    }

    /**
     * Gets the facade impl to forward calls to that this class is acting as a proxy to.
     */
    protected PropertyServiceFacade getForwardingFacade()
    {
        return forwardingFacade;
    }

    /**
     * Attempts to update the PropertyServicePropertyGroup in the cache if it is already cached.
     * @param propertyGroup to update
     */
    protected void updateCache(PropertyServicePropertyGroup propertyGroup)
    {
        String cacheKey = findKeyLockObject(propertyGroup.getCategory(), propertyGroup.getKey(), false);

        if(cacheKey != null)
        {
            synchronized(cacheKey)
            {
                cacheMap.put(cacheKey, propertyGroup);
            }
        }
    }

    /**
     * Attempts to remove from the caching any PropertyServicePropertyGroup references by the passed Strings
     * @param category to remove
     * @param key to remove
     */
    protected void removeFromCache(String category, String key)
    {
        String cacheKey = category + key;
        synchronized(keyLockObjectMap)
        {
            cacheKey = (String) keyLockObjectMap.remove(cacheKey);

            if(cacheKey != null)
            {
                synchronized(cacheKey)
                {
                    cacheMap.remove(cacheKey);
                }
            }
        }
    }

    /**
     * Tries to find the existing cache String lock object for matching category and key
     * @param category to find lock object for
     * @param key to find lock object for
     * @param addIfNotFound If true and lock object was not found, one will be created and returned. If false and one
     * is not found, null will be returned.
     * @return found lock object, new lock object, or null depending on the value of addIfNotFound parameter
     */
    protected String findKeyLockObject(String category, String key, boolean addIfNotFound)
    {
        String cacheKey = category + key;
        synchronized(keyLockObjectMap)
        {
            String foundCacheKey = (String) keyLockObjectMap.get(cacheKey);
            if(foundCacheKey != null)
            {
                cacheKey = foundCacheKey;
            }
            else
            {
                if(addIfNotFound)
                {
                    keyLockObjectMap.put(cacheKey, cacheKey);
                }
                else
                {
                    cacheKey = null;
                }
            }
        }

        return cacheKey;
    }

    protected void registerSelf(String category, String propertyKey)
            throws SystemException, DataValidationException, CommunicationException, AuthorizationException
    {
        String key = category + propertyKey;
        synchronized(selfRegisteredKeys)
        {
            boolean alreadyRegistered = selfRegisteredKeys.containsKey(key);
            if(!alreadyRegistered)
            {
                getForwardingFacade().subscribe(category, propertyKey, this);
                selfRegisteredKeys.put(key, Boolean.TRUE);
            }
        }
    }

    protected void registerSelf(String category)
            throws SystemException, DataValidationException, CommunicationException, AuthorizationException
    {
        synchronized(selfRegisteredKeys)
        {
            boolean alreadyRegistered = selfRegisteredKeys.containsKey(category);
            if(!alreadyRegistered)
            {
                getForwardingFacade().subscribe(category, this);
                selfRegisteredKeys.put(category, Boolean.TRUE);
            }
        }
    }
}
