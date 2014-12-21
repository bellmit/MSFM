//
// -----------------------------------------------------------------------------------
// Source file: TradingPropertyFactoryCacheImpl.java
//
// PACKAGE: com.cboe.domain.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.tradingProperty;

import java.lang.reflect.InvocationTargetException;
import java.beans.PropertyChangeListener;
import java.beans.IntrospectionException;

import com.cboe.idl.cmiErrorCodes.NotFoundCodes;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;

import com.cboe.interfaces.domain.tradingProperty.TradingPropertyGroup;
import com.cboe.interfaces.domain.tradingProperty.TradingProperty;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;

import com.cboe.util.event.EventChannelListener;
import com.cboe.util.ExceptionBuilder;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * This factory provides the conversion of PropertyGroup's to TradingPropertyGroup implementations.
 * This implementation overrides a getter to provide cached functionality.
 */
public class TradingPropertyFactoryCacheImpl extends TradingPropertyFactoryImpl
{
    private final TradingPropertyGroup USE_DEFAULT_REFERENCE = new UseDefaultTradingPropertyGroup();

    private int initialCacheSize;

    /**
     * local cache handling for TradingProperty objects
     */
    private TradingPropertyGroupCache cacheInstance;

    /**
     * Constructs with the default initial cache size defined by the TradingPropertyGroupCache.
     */
    public TradingPropertyFactoryCacheImpl()
    {}

    /**
     * Constructs with a passed initial cache size.
     * @param initialCacheSize initial size of the cache that holds the objects.
     */
    public TradingPropertyFactoryCacheImpl(int initialCacheSize)
    {
        this();
        this.initialCacheSize = initialCacheSize;
    }

    /**
     * Subscribes the listener to events for the TradingPropertyGroup identified.
     * @param sessionName of TradingPropertyGroup to subscribe to
     * @param classKey of TradingPropertyGroup to subscribe to
     * @param tradingPropertyName of TradingPropertyGroup to subscribe to
     * @param listener to subscribe
     */
    public void subscribe(String sessionName, int classKey, String tradingPropertyName, EventChannelListener listener)
            throws SystemException, DataValidationException, CommunicationException, AuthorizationException
    {
        String keyForCacheSubscribe = buildTradingPropertyKey(sessionName, classKey, tradingPropertyName);
        getCache().subscribeSelf(keyForCacheSubscribe);
        super.subscribe(sessionName, classKey, tradingPropertyName, listener);
    }

    /**
     * Gets the TradingPropertyGroup for a specific sessionName, classKey and tradingPropertyName.
     * @param sessionName to get group for
     * @param classKey to get group for
     * @param tradingPropertyName of specific group to get
     * @param withDefault designates if this implementation should query for the default class key, if the specified
     * class key was not found and itself was not the default class key. True if it should, false to just return
     * exception.
     * @return the appropriate implementation instance of a TradingPropertyGroup based on the values passed
     * @throws InvocationTargetException If any exception occurs from instantiating the appropriate domain wrapper
     * object, it is returned as the cause in this exception.
     * @throws DataValidationException This exception is forwarded from the TradingPropertyGroup.setPropertyGroup
     * interface. It will probably indicate that the raw data used to create the TradingPropertyGroup was not of a valid
     * format.
     * @throws SystemException forwarded from PropertyServiceFacadeHome
     * @throws NotFoundException forwarded from PropertyServiceFacadeHome
     * @throws CommunicationException forwarded from PropertyServiceFacadeHome
     * @throws AuthorizationException forwarded from PropertyServiceFacadeHome
     */
    public TradingPropertyGroup getTradingPropertyGroup(String sessionName, int classKey,
                                                           String tradingPropertyName, boolean withDefault)
            throws InvocationTargetException, DataValidationException, SystemException, NotFoundException,
                   CommunicationException, AuthorizationException, TransactionFailedException
    {
        TradingPropertyGroup newTradingPropertyGroup;

        String cacheKey = buildTradingPropertyKey(sessionName, classKey, tradingPropertyName);

        newTradingPropertyGroup = getCache().getTradingPropertyGroup(cacheKey);

        if(newTradingPropertyGroup == null)
        {
            try
            {
                newTradingPropertyGroup = super.getTradingPropertyGroup(sessionName, classKey,
                                                                        tradingPropertyName, false);
                getCache().addTradingPropertyGroup(cacheKey, newTradingPropertyGroup);
            }
            catch(NotFoundException e)
            {
                if(withDefault && classKey != DEFAULT_CLASS_KEY)
                {
                    newTradingPropertyGroup = getTradingPropertyGroup(sessionName, DEFAULT_CLASS_KEY,
                                                                      tradingPropertyName, false);

                    String newGroupKey = buildTradingPropertyKey(newTradingPropertyGroup);
                    getCache().addTradingPropertyGroup(newGroupKey, newTradingPropertyGroup);
                    getCache().addTradingPropertyGroup(cacheKey, USE_DEFAULT_REFERENCE);
                }
                else
                {
                    throw e;
                }
            }
        }
        else if(newTradingPropertyGroup == USE_DEFAULT_REFERENCE)
        {
            if(withDefault)
            {
                newTradingPropertyGroup = getTradingPropertyGroup(sessionName, DEFAULT_CLASS_KEY,
                                                                  tradingPropertyName, false);
            }
            else
            {
                StringBuffer msg = new StringBuffer(100);
                msg.append(getClass().getName()).append(":Could not find TradingPropertyGroup:");
                msg.append("sessionName=").append(sessionName);
                msg.append("; classKey=").append(classKey);
                msg.append("; tradingPropertyName=").append(tradingPropertyName);
                msg.append("; withDefault=").append(withDefault);

                throw ExceptionBuilder.notFoundException(msg.toString(), NotFoundCodes.RESOURCE_DOESNT_EXIST);
            }
        }

        return newTradingPropertyGroup;
    }

    /**
     * Lazily creates the Cache object and returns it.
     */
    private TradingPropertyGroupCache getCache()
    {
        if(Log.isDebugOn())
        {
            Log.debug("TradingPropertyFactoryCacheImpl:getCache()");
        }
        if(cacheInstance == null)
        {
            if(Log.isDebugOn())
            {
                Log.debug("TradingPropertyFactoryCacheImpl:getCache() - new cache instance created.");
            }
            if(initialCacheSize > 0)
            {
                cacheInstance = new TradingPropertyGroupCache(initialCacheSize);
            }
            else
            {
                cacheInstance = new TradingPropertyGroupCache();
            }
        }
        return cacheInstance;
    }

    private class UseDefaultTradingPropertyGroup implements TradingPropertyGroup
    {
        public void addPropertyChangeListener(PropertyChangeListener listener)
        {}

        public void addTradingProperty(TradingProperty tradingProperty)
        {}

        public Object clone()
                throws CloneNotSupportedException
        {return null;}

        public TradingProperty createNewTradingProperty(String sessionName, int classKey)
        {return null;}

        public void delete()
                throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                       TransactionFailedException, NotFoundException
        {}

        public TradingProperty[] getAllTradingProperties()
        {return new TradingProperty[0];}

        public int getClassKey()
        {return 0;}

        public Class getCustomizerClass()
                throws IntrospectionException
        {return null;}

        public int getMaxTradingPropertiesAllowed()
        {return 0;}

        public PropertyServicePropertyGroup getPropertyGroup()
        {return null;}

        public String getSessionName()
        {return null;}

        public TradingPropertyType getTradingPropertyType()
        {return null;}

        public int getVersionNumber()
        {return 0;}

        public void removePropertyChangeListener(PropertyChangeListener listener)
        {}

        public TradingProperty removeTradingProperty(TradingProperty tradingProperty)
        {return null;}

        public void save()
                throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                       TransactionFailedException, NotFoundException
        {}

        public void setPropertyGroup(PropertyServicePropertyGroup propertyGroup)
                throws DataValidationException
        {}

        public void subscribe(EventChannelListener listener)
                throws SystemException, CommunicationException, AuthorizationException, DataValidationException
        {}

        public void unsubscribe(EventChannelListener listener)
                throws SystemException, CommunicationException, AuthorizationException, DataValidationException
        {}

        public TradingProperty updateTradingProperty(TradingProperty tradingProperty)
        {return null;}
    }
}
