//
// -----------------------------------------------------------------------------------
// Source file: AuctionRangeTradingPropertyGroup.java
//
// PACKAGE: com.cboe.internalPresentation.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.tradingProperty;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import com.cboe.idl.tradingProperty.AuctionRangeClassStruct;
import com.cboe.idl.tradingProperty.AuctionRangeStruct;

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

import com.cboe.domain.tradingProperty.TradingPropertyFactoryHome;

/**
 * Provides a default OldTradingPropertyGroup implementation for a AuctionTimeRangeStruct
 */
public class AuctionRangeTradingPropertyGroup extends AbstractOldTradingPropertyGroup
{
    protected TradingPropertyType tradingPropertyType;

    /**
     * Constructor that initializes with the immutable trading session name and class key. WARNING!! THIS CONSTRUCTOR
     * MUST ALWAYS BE EXPOSED. THE FACTORY WILL USE THIS CONSTRUCTOR TO INSTANTIATE THE OBJECT WITH THE IMMUTABLE
     * SESSION NAME AND CLASS KEY.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public AuctionRangeTradingPropertyGroup(String sessionName, int classKey)
    {
        super(sessionName, classKey);
    }


    /**
     * Provides a getter to get an instance of this TradingPropertyGroup, downcasted for convenience.
     * @param sessionName to get TradingPropertyGroup for
     * @param classKey to get TradingPropertyGroup for
     * @param tradingPropertyType to get TradingPropertyGroup for
     * @return instance of TradingPropertyGroup downcasted to this class type.
     * @throws SystemException forwarded from TradingPropertyFactory
     * @throws AuthorizationException forwarded from TradingPropertyFactory
     * @throws CommunicationException forwarded from TradingPropertyFactory
     * @throws NotFoundException forwarded from TradingPropertyFactory
     * @throws InvocationTargetException forwarded from TradingPropertyFactory
     * @throws DataValidationException forwarded from TradingPropertyFactory
     */
    public static AuctionRangeTradingPropertyGroup getGroup(String sessionName, int classKey,
                                                            TradingPropertyType tradingPropertyType)
            throws SystemException, NotFoundException, DataValidationException, CommunicationException,
                   InvocationTargetException, AuthorizationException, TransactionFailedException
    {
        TradingPropertyGroup myGroup =
                TradingPropertyFactoryHome.find().getTradingPropertyGroup(sessionName, classKey,
                                                                          tradingPropertyType.getName());
        AuctionRangeTradingPropertyGroup castedGroup = (AuctionRangeTradingPropertyGroup) myGroup;
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
        AuctionRangeTradingProperty newTP =
                new AuctionRangeTradingProperty(getTradingPropertyType().getName(), sessionName, classKey);
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

    public AuctionRangeTradingProperty[] getAuctionRangeTradingProperties()
    {
        TradingProperty[] allTPs = getAllTradingProperties();
        AuctionRangeTradingProperty[] castedTPs = new AuctionRangeTradingProperty[allTPs.length];
        System.arraycopy(allTPs, 0, castedTPs, 0, allTPs.length);
        return castedTPs;
    }

    protected Object getMapKeyForTradingProperty(TradingProperty tradingProperty)
    {
        return new Short(((AuctionRangeTradingProperty) tradingProperty).getAuctionType());
    }

    protected Object getSaveParmsData(TradingProperty[] allTradingProperties, Class dataType)
    {
        AuctionRangeStruct[] structs = new AuctionRangeStruct[allTradingProperties.length];
        for(int i = 0; i < allTradingProperties.length; i++)
        {
            AuctionRangeTradingProperty auctionRangeTradingProperty =
                    (AuctionRangeTradingProperty) allTradingProperties[i];
            structs[i] = auctionRangeTradingProperty.getAuctionRangeStruct();
        }

        return structs;
    }

    protected void processDataFromLoad(Object returnValue)
            throws DataValidationException
    {
        AuctionRangeStruct[] structs = (AuctionRangeStruct[]) returnValue;

        for(int i = 0; i < structs.length; i++)
        {
            AuctionRangeStruct auctionRangeStruct = structs[i];
            AuctionRangeTradingProperty newTP =
                    (AuctionRangeTradingProperty) createNewTradingProperty(getSessionName(), getClassKey());
            newTP.setAuctionRangeStruct(auctionRangeStruct);
            addTradingProperty(newTP);
        }
    }

    protected OldTradingPropertyGroup[] processAllClassesDataFromLoad(String sessionName, Object returnValue)
            throws DataValidationException, InvocationTargetException
    {
        List newTPGList = new ArrayList(200);
        AuctionRangeClassStruct[] structs = (AuctionRangeClassStruct[]) returnValue;

        int classKey;

        for(int i = 0; i < structs.length; i++)
        {
            AuctionRangeClassStruct arrayElement = structs[i];
            classKey = getClassKeyFromStructObject(arrayElement);

            TradingPropertyFactory factory = TradingPropertyFactoryHome.find();

            AuctionRangeTradingPropertyGroup newTPGroup =
                    (AuctionRangeTradingPropertyGroup) factory.createNewTradingPropertyGroup(sessionName, classKey,
                                                                                             getTradingPropertyType().getName());
            newTPGroup.sequenceHolder.value = arrayElement.seqNum;
            newTPGroup.processDataFromLoad(arrayElement.range);

            newTPGList.add(newTPGroup);
        }

        return (OldTradingPropertyGroup[]) newTPGList.toArray(new OldTradingPropertyGroup[newTPGList.size()]);
    }

    protected int getClassKeyFromStructObject(Object arrayElement)
    {
        int classKey = ((AuctionRangeClassStruct) arrayElement).classKey;
        return classKey;
    }

    protected int getSequenceNumberFromStructObject(Object arrayElement)
    {
        int sequenceNumber = ((AuctionRangeClassStruct) arrayElement).seqNum;
        return sequenceNumber;
    }
}
