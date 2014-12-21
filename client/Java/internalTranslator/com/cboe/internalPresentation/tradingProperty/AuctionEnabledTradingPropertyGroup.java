//
// -----------------------------------------------------------------------------------
// Source file: AuctionEnabledTradingPropertyGroup.java
//
// PACKAGE: com.cboe.internalPresentation.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.tradingProperty;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import com.cboe.idl.tradingProperty.AuctionBooleanClassStruct;
import com.cboe.idl.tradingProperty.AuctionBooleanStruct;

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
 * Provides a default OldTradingPropertyGroup implementation for AuctionBooleanStruct's.
 */
public class AuctionEnabledTradingPropertyGroup extends AbstractOldTradingPropertyGroup
{
    public static final TradingPropertyType TRADING_PROPERTY_TYPE =
            TradingPropertyTypeImpl.AUCTION_ENABLED;

    /**
     * Constructor that initializes with the immutable trading session name and class key. WARNING!! THIS CONSTRUCTOR
     * MUST ALWAYS BE EXPOSED. THE FACTORY WILL USE THIS CONSTRUCTOR TO INSTANTIATE THE OBJECT WITH THE IMMUTABLE
     * SESSION NAME AND CLASS KEY.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public AuctionEnabledTradingPropertyGroup(String sessionName, int classKey)
    {
        super(sessionName, classKey);
    }


    /**
     * Provides a getter to get an instance of this TradingPropertyGroup, downcasted for convenience.
     * @param sessionName to get TradingPropertyGroup for
     * @param classKey to get TradingPropertyGroup for
     * @return instance of TradingPropertyGroup downcasted to this class type.
     * @throws SystemException forwarded from TradingPropertyFactory
     * @throws AuthorizationException forwarded from TradingPropertyFactory
     * @throws CommunicationException forwarded from TradingPropertyFactory
     * @throws NotFoundException forwarded from TradingPropertyFactory
     * @throws InvocationTargetException forwarded from TradingPropertyFactory
     * @throws DataValidationException forwarded from TradingPropertyFactory
     */
    public static AuctionEnabledTradingPropertyGroup getGroup(String sessionName, int classKey)
            throws SystemException, NotFoundException, DataValidationException, CommunicationException,
                   InvocationTargetException, AuthorizationException, TransactionFailedException
    {
        TradingPropertyGroup myGroup =
                TradingPropertyFactoryHome.find().getTradingPropertyGroup(sessionName, classKey,
                                                                          TRADING_PROPERTY_TYPE.getName());
        AuctionEnabledTradingPropertyGroup castedGroup = (AuctionEnabledTradingPropertyGroup) myGroup;
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
        AuctionEnabledTradingProperty newTP = new AuctionEnabledTradingProperty(sessionName, classKey);
        return newTP;
    }

    public TradingPropertyType getTradingPropertyType()
    {
        return TRADING_PROPERTY_TYPE;
    }

    public AuctionEnabledTradingProperty[] getAuctionEnabledTradingProperties()
    {
        TradingProperty[] allTPs = getAllTradingProperties();
        AuctionEnabledTradingProperty[] castedTPs = new AuctionEnabledTradingProperty[allTPs.length];
        System.arraycopy(allTPs, 0, castedTPs, 0, allTPs.length);
        return castedTPs;
    }

    protected Object getMapKeyForTradingProperty(TradingProperty tradingProperty)
    {
        return new Short(((AuctionEnabledTradingProperty) tradingProperty).getAuctionType());
    }

    protected Object getSaveParmsData(TradingProperty[] allTradingProperties, Class dataType)
    {
        AuctionBooleanStruct[] structs = new AuctionBooleanStruct[allTradingProperties.length];
        for(int i = 0; i < allTradingProperties.length; i++)
        {
            AuctionEnabledTradingProperty auctionEnabledTradingProperty =
                    (AuctionEnabledTradingProperty) allTradingProperties[i];
            structs[i] = auctionEnabledTradingProperty.getAuctionBooleanStruct();
        }

        return structs;
    }

    protected void processDataFromLoad(Object returnValue)
            throws DataValidationException
    {
        AuctionBooleanStruct[] structs = (AuctionBooleanStruct[]) returnValue;

        for(int i = 0; i < structs.length; i++)
        {
            AuctionBooleanStruct auctionBooleanStruct = structs[i];
            AuctionEnabledTradingProperty newTP =
                    (AuctionEnabledTradingProperty) createNewTradingProperty(getSessionName(), getClassKey());
            newTP.setAuctionBooleanStruct(auctionBooleanStruct);
            addTradingProperty(newTP);
        }
    }

    protected OldTradingPropertyGroup[] processAllClassesDataFromLoad(String sessionName, Object returnValue)
            throws DataValidationException, InvocationTargetException
    {
        List newTPGList = new ArrayList(200);
        AuctionBooleanClassStruct[] structs = (AuctionBooleanClassStruct[]) returnValue;

        int classKey;

        for(int i = 0; i < structs.length; i++)
        {
            AuctionBooleanClassStruct arrayElement = structs[i];
            classKey = getClassKeyFromStructObject(arrayElement);

            TradingPropertyFactory factory = TradingPropertyFactoryHome.find();

            AuctionEnabledTradingPropertyGroup newTPGroup =
                    (AuctionEnabledTradingPropertyGroup) factory.createNewTradingPropertyGroup(sessionName, classKey,
                                                                                            getTradingPropertyType().getName());
            newTPGroup.sequenceHolder.value = arrayElement.seqNum;
            newTPGroup.processDataFromLoad(arrayElement.booleanValue);

            newTPGList.add(newTPGroup);
        }

        return (OldTradingPropertyGroup[]) newTPGList.toArray(new OldTradingPropertyGroup[newTPGList.size()]);
    }

    protected int getClassKeyFromStructObject(Object arrayElement)
    {
        int classKey = ((AuctionBooleanClassStruct) arrayElement).classKey;
        return classKey;
    }

    protected int getSequenceNumberFromStructObject(Object arrayElement)
    {
        int sequenceNumber = ((AuctionBooleanClassStruct) arrayElement).seqNum;
        return sequenceNumber;
    }
}
