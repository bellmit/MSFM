//
// -----------------------------------------------------------------------------------
// Source file: SimpleIntegerSequenceOldTradingPropertyGroup.java
//
// PACKAGE: com.cboe.internalPresentation.tradingProperty.common;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.tradingProperty.common;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import com.cboe.idl.tradingProperty.LongClassStruct;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;

import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.tradingProperty.SimpleIntegerTradingProperty;
import com.cboe.interfaces.domain.tradingProperty.TradingProperty;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyGroup;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyFactory;
import com.cboe.interfaces.internalPresentation.tradingProperty.OldTradingPropertyGroup;

import com.cboe.internalPresentation.tradingProperty.AbstractOldTradingPropertyGroup;

import com.cboe.domain.tradingProperty.TradingPropertyFactoryHome;
import com.cboe.domain.tradingProperty.common.SimpleIntegerTradingPropertyImpl;

/**
 * Represents an OldTradingPropertyGroup for an integer sequence
 */
public class SimpleIntegerSequenceOldTradingPropertyGroup extends AbstractOldTradingPropertyGroup
{
    protected TradingPropertyType tradingPropertyType;

    /**
     * Constructor that initializes with the immutable trading session name and class key. WARNING!! THIS CONSTRUCTOR
     * MUST ALWAYS BE EXPOSED. THE FACTORY WILL USE THIS CONSTRUCTOR TO INSTANTIATE THE OBJECT WITH THE IMMUTABLE
     * SESSION NAME AND CLASS KEY.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public SimpleIntegerSequenceOldTradingPropertyGroup(String sessionName, int classKey)
    {
        super(sessionName, classKey);
    }

    /**
     * Constructor that initializes with the immutable trading session name, class key and the
     * PropertyServicePropertyGroup to initialize the sub-classes trading property data with.
     * @param sessionName that this TradingPropertyGroup is for
     * @param classKey that this TradingPropertyGroup is for
     * @param propertyGroup to initialize with
     */
    public SimpleIntegerSequenceOldTradingPropertyGroup(String sessionName, int classKey,
                                                        PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException
    {
        super(sessionName, classKey, propertyGroup);
    }

    /**
     * Provides a getter to get an instance of this TradingPropertyGroup, downcasted for convenience.
     * @param sessionName to get TradingPropertyGroup for
     * @param classKey to get TradingPropertyGroup for
     * @param tradingPropertyType to get TradingPropertyGroup for
     * @return instance of TradingPropertyGroup downcasted to this class type.
     * @throws com.cboe.exceptions.SystemException forwarded from TradingPropertyFactory
     * @throws com.cboe.exceptions.AuthorizationException forwarded from TradingPropertyFactory
     * @throws com.cboe.exceptions.CommunicationException forwarded from TradingPropertyFactory
     * @throws com.cboe.exceptions.NotFoundException forwarded from TradingPropertyFactory
     * @throws java.lang.reflect.InvocationTargetException forwarded from TradingPropertyFactory
     * @throws com.cboe.exceptions.DataValidationException forwarded from TradingPropertyFactory
     */
    public static SimpleIntegerSequenceOldTradingPropertyGroup getGroup(String sessionName, int classKey,
                                                                        TradingPropertyType tradingPropertyType)
            throws SystemException, NotFoundException, DataValidationException, CommunicationException,
                   InvocationTargetException, AuthorizationException, TransactionFailedException
    {
        TradingPropertyGroup myGroup =
                TradingPropertyFactoryHome.find().getTradingPropertyGroup(sessionName, classKey,
                                                                          tradingPropertyType.getName());
        SimpleIntegerSequenceOldTradingPropertyGroup castedGroup =
                (SimpleIntegerSequenceOldTradingPropertyGroup) myGroup;
        castedGroup.setTradingPropertyType(tradingPropertyType);
        return castedGroup;
    }

    /**
     * Create a new implementation specific TradingProperty.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @return new TradingProperty
     */
    public TradingProperty createNewTradingProperty(String sessionName, int classKey)
    {
        SimpleIntegerTradingPropertyImpl newTP = new SimpleIntegerTradingPropertyImpl("Property", sessionName, classKey);
        newTP.setTradingPropertyType(getTradingPropertyType());
        return newTP;
    }

    public TradingPropertyType getTradingPropertyType()
    {
        return tradingPropertyType;
    }

    public void setTradingPropertyType(TradingPropertyType tradingPropertyType)
    {
        this.tradingPropertyType = tradingPropertyType;
    }

    public SimpleIntegerTradingProperty[] getIntegerTradingProperties()
    {
        Collection values = getTradingPropertyMap().values();
        return (SimpleIntegerTradingProperty[]) values.toArray(new SimpleIntegerTradingPropertyImpl[values.size()]);
    }

    /**
     * This method is used to get an Object to be used as the key for adding, obtaining and removing the
     * TradingProperty's from the underlying Map collection returned from getTradingPropertyMap(). This implementation
     * will return the integer value of the TradingProperty
     * @param tradingProperty to get key Object for
     * @return the integer value of the TradingProperty
     */
    protected Object getMapKeyForTradingProperty(TradingProperty tradingProperty)
    {
        return new Integer(((SimpleIntegerTradingProperty)tradingProperty).getIntegerValue());
    }

    protected Object getSaveParmsData(TradingProperty[] allTradingProperties, Class dataType)
    {
        int[] integers = new int[allTradingProperties.length];
        for(int i = 0; i < allTradingProperties.length; i++)
        {
            SimpleIntegerTradingProperty simpleIntegerTradingProperty =
                    (SimpleIntegerTradingProperty) allTradingProperties[i];
            integers[i] = simpleIntegerTradingProperty.getIntegerValue();
        }

        return integers;
    }

    protected void processDataFromLoad(Object returnValue)
            throws DataValidationException
    {
        int[] integers = (int[]) returnValue;

        for(int i = 0; i < integers.length; i++)
        {
            SimpleIntegerTradingProperty newTP =
                    (SimpleIntegerTradingProperty) createNewTradingProperty(getSessionName(), getClassKey());
            newTP.setIntegerValue(integers[i]);
            addTradingProperty(newTP);
        }
    }

    protected OldTradingPropertyGroup[] processAllClassesDataFromLoad(String sessionName, Object returnValue)
            throws DataValidationException, InvocationTargetException
    {
        List newTPGList = new ArrayList(200);
        LongClassStruct[] structs = (LongClassStruct[]) returnValue;

        int classKey;

        for(int i = 0; i < structs.length; i++)
        {
            LongClassStruct arrayElement = structs[i];
            classKey = getClassKeyFromStructObject(arrayElement);

            TradingPropertyFactory factory = TradingPropertyFactoryHome.find();

            SimpleIntegerSequenceOldTradingPropertyGroup newTPGroup =
                    (SimpleIntegerSequenceOldTradingPropertyGroup) factory.createNewTradingPropertyGroup(sessionName,
                                                                                                         classKey,
                                                                                                         getTradingPropertyType().getName());
            newTPGroup.sequenceHolder.value = arrayElement.seqNum;
            newTPGroup.processDataFromLoad(arrayElement.values);

            newTPGList.add(newTPGroup);
        }

        return (OldTradingPropertyGroup[]) newTPGList.toArray(new OldTradingPropertyGroup[newTPGList.size()]);
    }

    protected int getClassKeyFromStructObject(Object arrayElement)
    {
        int classKey = ((LongClassStruct) arrayElement).classKey;
        return classKey;
    }

    protected int getSequenceNumberFromStructObject(Object arrayElement)
    {
        int sequenceNumber = ((LongClassStruct) arrayElement).seqNum;
        return sequenceNumber;
    }
}
