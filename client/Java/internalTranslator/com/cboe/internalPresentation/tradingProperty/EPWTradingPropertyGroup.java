//
// -----------------------------------------------------------------------------------
// Source file: EPWTradingPropertyGroup.java
//
// PACKAGE: com.cboe.internalPresentation.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.tradingProperty;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import com.cboe.idl.cmiProduct.EPWStruct;
import com.cboe.idl.tradingProperty.SpreadClassStruct;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;

import com.cboe.interfaces.domain.tradingProperty.TradingProperty;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyGroup;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyFactory;
import com.cboe.interfaces.internalPresentation.tradingProperty.OldTradingPropertyGroup;

import com.cboe.domain.tradingProperty.TradingPropertyTypeImpl;
import com.cboe.domain.tradingProperty.TradingPropertyFactoryHome;

/**
 * Provides a default OldTradingPropertyGroup implementation for EPWStruct's.
 */
public class EPWTradingPropertyGroup extends AbstractOldTradingPropertyGroup
{
    public static final TradingPropertyType TRADING_PROPERTY_TYPE = TradingPropertyTypeImpl.EPW_STRUCT;

    /**
     * Constructor that initializes with the immutable trading session name and class key. WARNING!! THIS CONSTRUCTOR
     * MUST ALWAYS BE EXPOSED. THE FACTORY WILL USE THIS CONSTRUCTOR TO INSTANTIATE THE OBJECT WITH THE IMMUTABLE
     * SESSION NAME AND CLASS KEY.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public EPWTradingPropertyGroup(String sessionName, int classKey)
    {
        super(sessionName, classKey);
    }


    /**
     * Provides a getter to get an instance of this TradingPropertyGroup, downcasted for convenience.
     * @param sessionName to get TradingPropertyGroup for
     * @param classKey to get TradingPropertyGroup for
     * @return instance of TradingPropertyGroup downcasted to this class type.
     * @throws com.cboe.exceptions.SystemException forwarded from TradingPropertyFactory
     * @throws com.cboe.exceptions.AuthorizationException forwarded from TradingPropertyFactory
     * @throws com.cboe.exceptions.CommunicationException forwarded from TradingPropertyFactory
     * @throws com.cboe.exceptions.NotFoundException forwarded from TradingPropertyFactory
     * @throws java.lang.reflect.InvocationTargetException forwarded from TradingPropertyFactory
     * @throws com.cboe.exceptions.DataValidationException forwarded from TradingPropertyFactory
     */
    public static EPWTradingPropertyGroup getGroup(String sessionName, int classKey)
            throws SystemException, NotFoundException, DataValidationException, CommunicationException,
                   InvocationTargetException, AuthorizationException, TransactionFailedException
    {
        TradingPropertyGroup myGroup =
                TradingPropertyFactoryHome.find().getTradingPropertyGroup(sessionName, classKey,
                                                                          TRADING_PROPERTY_TYPE.getName());
        EPWTradingPropertyGroup castedGroup = (EPWTradingPropertyGroup) myGroup;
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
        EPWTradingProperty newTP = new EPWTradingProperty(sessionName, classKey);
        return newTP;
    }

    public TradingPropertyType getTradingPropertyType()
    {
        return TRADING_PROPERTY_TYPE;
    }

    public EPWTradingProperty[] getEPWTradingProperties()
    {
        TradingProperty[] allTPs = getAllTradingProperties();
        EPWTradingProperty[] castedTPs = new EPWTradingProperty[allTPs.length];
        System.arraycopy(allTPs, 0, castedTPs, 0, allTPs.length);
        return castedTPs;
    }

    protected Object getMapKeyForTradingProperty(TradingProperty tradingProperty)
    {
        return new Double(((EPWTradingProperty) tradingProperty).getMinimumBidRange());
    }

    protected Object getSaveParmsData(TradingProperty[] allTradingProperties, Class dataType)
    {
        EPWStruct[] structs = new EPWStruct[allTradingProperties.length];
        for(int i = 0; i < allTradingProperties.length; i++)
        {
            EPWTradingProperty epwStructTradingProperty = (EPWTradingProperty) allTradingProperties[i];
            structs[i] = epwStructTradingProperty.getEPWStruct();
        }

        return structs;
    }

    protected void processDataFromLoad(Object returnValue)
            throws DataValidationException
    {
        EPWStruct[] structs = (EPWStruct[]) returnValue;

        for(int i = 0; i < structs.length; i++)
        {
            EPWStruct epwStruct = structs[i];
            EPWTradingProperty newTP = (EPWTradingProperty) createNewTradingProperty(getSessionName(),
                                                                                     getClassKey());
            newTP.setEPWStruct(epwStruct);
            addTradingProperty(newTP);
        }
    }

    protected OldTradingPropertyGroup[] processAllClassesDataFromLoad(String sessionName, Object returnValue)
            throws DataValidationException, InvocationTargetException
    {
        ArrayList newTPGList = new ArrayList(200);
        SpreadClassStruct[] structs = (SpreadClassStruct[]) returnValue;

        int classKey;

        for(int i = 0; i < structs.length; i++)
        {
            SpreadClassStruct arrayElement = structs[i];
            classKey = getClassKeyFromStructObject(arrayElement);

            TradingPropertyFactory factory = TradingPropertyFactoryHome.find();

            EPWTradingPropertyGroup newTPGroup =
                    (EPWTradingPropertyGroup) factory.createNewTradingPropertyGroup(sessionName, classKey,
                                                                                    getTradingPropertyType().getName());
            newTPGroup.sequenceHolder.value = arrayElement.seqNum;
            newTPGroup.processDataFromLoad(arrayElement.spreads);

            newTPGList.add(newTPGroup);
        }

        return (OldTradingPropertyGroup[]) newTPGList.toArray(new OldTradingPropertyGroup[newTPGList.size()]);
    }

    protected int getClassKeyFromStructObject(Object arrayElement)
    {
        int classKey = ((SpreadClassStruct) arrayElement).classKey;
        return classKey;
    }

    protected int getSequenceNumberFromStructObject(Object arrayElement)
    {
        int sequenceNumber = ((SpreadClassStruct) arrayElement).seqNum;
        return sequenceNumber;
    }
}
