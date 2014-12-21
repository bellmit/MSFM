//
// -----------------------------------------------------------------------------------
// Source file: TradingPropertyGroupCache.java
//
// PACKAGE: com.cboe.domain.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.tradingProperty;

import java.util.*;

import org.omg.CORBA.UserException;

import com.cboe.idl.constants.PropertyCategoryTypes;
import com.cboe.idl.property.PropertyGroupStruct;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;

import com.cboe.interfaces.domain.tradingProperty.TradingPropertyGroup;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelListener;

import com.cboe.domain.property.PropertyServiceFacadeHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * implement a cache for the domain object at the TradingPropertyGroup interface level
 */
public class TradingPropertyGroupCache implements EventChannelListener
{
    public static final int DEFAULT_CACHE_SIZE = 101;
    private int initialCacheSize;

    protected Map tradingPropertyGroupCache;

    private final Map selfRegisteredKeys;

    /**
     * Constructs with the initial cache size defined by the DEFAULT_CACHE_SIZE attribute.
     */
    public TradingPropertyGroupCache()
    {
        this(DEFAULT_CACHE_SIZE);
    }

    /**
     * Constructs with a passed initial cache size.
     * @param initialCacheSize initial size of the cache that holds the objects.
     */
    public TradingPropertyGroupCache(int initialCacheSize)
    {
        this.initialCacheSize = initialCacheSize;
        selfRegisteredKeys = new HashMap(initialCacheSize);
    }

    /**
     * Got an event from the event channel, handle it.
     */
    public void channelUpdate(ChannelEvent event)
    {
        final ChannelKey channelKey = (ChannelKey) event.getChannel();

        // Check for remove
        if(channelKey.channelType == ChannelType.REMOVE_PROPERTY)
        {
            String category = channelKey.key.toString();
            String tradingPropertyKey = (String) event.getEventData();
            acceptPropertyRemove(category, tradingPropertyKey);
        }
        // Check for update
        else if(channelKey.channelType == ChannelType.UPDATE_PROPERTY)
        {
            PropertyGroupStruct struct = (PropertyGroupStruct) event.getEventData();
            String category = struct.category;
            String tradingPropertyKey = struct.propertyKey;
            acceptPropertyUpdate(category, tradingPropertyKey);
        }
    }

    public synchronized TradingPropertyGroup getTradingPropertyGroup(String tradingPropertyGroupKey)
    {
        TradingPropertyGroup tradingPropertyGroup;

        if(Log.isDebugOn())
        {
            Log.debug("TradingPropertyGroupCache:getTradingPropertyGroup for"
                      + " tradingPropertyGroupKey=" + tradingPropertyGroupKey);
        }

        tradingPropertyGroup = (TradingPropertyGroup) getCacheMap().get(tradingPropertyGroupKey);

        return tradingPropertyGroup;
    }
    
    public synchronized TradingPropertyGroup addTradingPropertyGroup(String tradingPropertyGroupKey,
                                                                     TradingPropertyGroup tradingPropertyGroup)
    {
        if(Log.isDebugOn())
        {
            Log.debug("TradingPropertyGroupCache:addTradingPropertyGroup for"
                      + " tradingPropertyGroupKey=" + tradingPropertyGroupKey);
        }

        TradingPropertyGroup previousValue = (TradingPropertyGroup) getCacheMap().put(tradingPropertyGroupKey,
                                                                                      tradingPropertyGroup);

        if(previousValue == null)
        {
            try
            {
                subscribeSelf(tradingPropertyGroupKey);
            }
            catch(UserException e)
            {
                Log.exception("Exception trying to subscribe to property channels:"
                              + " tradingPropertyGroupKey=" + tradingPropertyGroupKey, e);
            }
        }

        return previousValue;
    }

    public synchronized TradingPropertyGroup removeTradingPropertyGroup(String tradingPropertyGroupKey)
    {
        if(Log.isDebugOn())
        {
            Log.debug("TradingPropertyGroupCache:removeTradingPropertyGroup for"
                      + " tradingPropertyGroupKey=" + tradingPropertyGroupKey);
        }

        TradingPropertyGroup previousValue = (TradingPropertyGroup) getCacheMap().remove(tradingPropertyGroupKey);

        if(previousValue != null)
        {
            try
            {
                unsubscribeSelf(tradingPropertyGroupKey);
            }
            catch(UserException e)
            {
                Log.exception("Exception trying to unsubscribe to property channels:"
                              + " tradingPropertyGroupKey=" + tradingPropertyGroupKey, e);
            }
        }

        return previousValue;
    }

    protected void subscribeSelf(String tradingPropertyGroupKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(Log.isDebugOn())
        {
            Log.debug("TradingPropertyGroupCache:subscribe for tradingPropertyGroupKey=" + tradingPropertyGroupKey);
        }

        synchronized(selfRegisteredKeys)
        {
            boolean alreadyRegistered = selfRegisteredKeys.containsKey(tradingPropertyGroupKey);
            if(!alreadyRegistered)
            {
                PropertyServiceFacadeHome.find().subscribe(PropertyCategoryTypes.TRADING_PROPERTIES,
                                                           tradingPropertyGroupKey, this);
                selfRegisteredKeys.put(tradingPropertyGroupKey, Boolean.TRUE);
            }
        }
    }

    protected void unsubscribeSelf(String tradingPropertyGroupKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(Log.isDebugOn())
        {
            Log.debug("TradingPropertyGroupCache:unsubscribe for tradingPropertyGroupKey=" + tradingPropertyGroupKey);
        }

        synchronized(selfRegisteredKeys)
        {
            PropertyServiceFacadeHome.find().unsubscribe(PropertyCategoryTypes.TRADING_PROPERTIES,
                                                         tradingPropertyGroupKey, this);
            selfRegisteredKeys.remove(tradingPropertyGroupKey);
        }
    }

    /**
     * Accept events for a property being updated. For simplicity, the group cache is cleared allowing future get calls
     * to repopulate.
     * @param category The category the group was in.
     * @param tradingPropertyKey The key for the group that was updated.
     */
    protected void acceptPropertyUpdate(String category, String tradingPropertyKey)
    {
        if(Log.isDebugOn())
        {
            Log.debug("TradingPropertyGroupCache:acceptPropertyUpdate for category = " +
                      category +
                      " and key = " +
                      tradingPropertyKey);
        }
        removeTradingPropertyGroup(tradingPropertyKey);
    }

    /**
     * Accept events for a property being removed.  This means that a set of category definitions has been totally
     * removed.  This is bizare, but go ahead and remove them from the cache.
     * @param category The category the group was in.
     * @param tradingPropertyKey The key for the group that was removed.
     */
    protected void acceptPropertyRemove(String category, String tradingPropertyKey)
    {
        if(Log.isDebugOn())
        {
            Log.debug("TradingPropertyGroupCache:acceptPropertyRemove for category = " +
                      category +
                      " and key = " +
                      tradingPropertyKey);
        }
        removeTradingPropertyGroup(tradingPropertyKey);
    }

    protected Map getCacheMap()
    {
        if(tradingPropertyGroupCache == null)
        {
            tradingPropertyGroupCache = new HashMap(initialCacheSize);
        }
        return tradingPropertyGroupCache;
    }

    protected String getTradingPropertyGroupKey(String sessionName, int classKey,
                                                String propertyName)
    {
        return TradingPropertyFactoryHome.find().buildTradingPropertyKey(sessionName, classKey, propertyName);
    }


    protected String getTradingPropertyGroupKey(TradingPropertyGroup tradingPropertyGroup)
    {
        return getTradingPropertyGroupKey(tradingPropertyGroup.getSessionName(),
                                          tradingPropertyGroup.getClassKey(),
                                          tradingPropertyGroup.getTradingPropertyType().getName());
    }
}