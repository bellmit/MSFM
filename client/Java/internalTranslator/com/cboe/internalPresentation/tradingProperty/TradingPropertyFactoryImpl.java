//
// -----------------------------------------------------------------------------------
// Source file: TradingPropertyFactoryConversion.java
//
// PACKAGE: com.cboe.internalPresentation.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.tradingProperty;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import org.omg.CORBA.IntHolder;

import com.cboe.idl.cmiProduct.EPWStruct;
import com.cboe.idl.tradingProperty.AllocationStrategyStructV2;
import com.cboe.idl.tradingProperty.AuctionBooleanStruct;
import com.cboe.idl.tradingProperty.AuctionLongStruct;
import com.cboe.idl.tradingProperty.AuctionOrderSizeTicksStruct;
import com.cboe.idl.tradingProperty.AuctionRangeStruct;
import com.cboe.idl.tradingProperty.DpmRightsScaleStruct;
import com.cboe.idl.tradingProperty.InternalizationPercentageStruct;
import com.cboe.idl.tradingProperty.TimeRangeStruct;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;

import com.cboe.interfaces.domain.tradingProperty.TradingProperty;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyGroup;
import com.cboe.interfaces.internalPresentation.SystemAdminAPI;
import com.cboe.interfaces.internalPresentation.tradingProperty.OldTradingPropertyGroup;

import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.internalPresentation.common.logging.GUILoggerSABusinessProperty;
import com.cboe.internalPresentation.tradingProperty.common.SimpleBooleanOldTradingPropertyGroup;
import com.cboe.internalPresentation.tradingProperty.common.SimpleDoubleOldTradingPropertyGroup;
import com.cboe.internalPresentation.tradingProperty.common.SimpleIntegerOldTradingPropertyGroup;
import com.cboe.internalPresentation.tradingProperty.common.SimpleIntegerSequenceOldTradingPropertyGroup;

import com.cboe.domain.tradingProperty.TradingPropertyClassType;
import com.cboe.domain.tradingProperty.TradingPropertyFactoryCacheImpl;
import com.cboe.domain.tradingProperty.TradingPropertyTypeImpl;

public class TradingPropertyFactoryImpl extends TradingPropertyFactoryCacheImpl
{
    /**
     * Provides a mapping from the TradingProperty names to the Class that is
     * responsible for that TradingProperty's implementation.
     */
    private static final Map PROPERTY_NAME_CLASS_TYPE_MAP = new HashMap(101);

    //***** START OF EDITABLE SECTION *****************
    // This static initializer needs modified for each new type of domain implementation of the TradingProperty
    // interface. You must instantiate a new ReflectiveTradingPropertyClassType object. Then this new
    // ReflectiveTradingPropertyClassType must be added to the Map above. You must use the tradingPropertyName
    // that you passed to the constructor of ReflectiveTradingPropertyClassType as the key when inserting into the Map.
    // See the class ReflectiveTradingPropertyClassType for detailed comments and documentation on that class.
    static
    {
        try
        {
            String[] queryParmNames;
            String[] queryAllClassesParmNames;
            String[] saveParmNames;
            Method queryMethod;
            Method queryAllClassesMethod;
            Method saveMethod;
            ReflectiveTradingPropertyClassType newClassType;

            //ALLOCATION_STRATEGY
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getAllocationStrategies",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getAllocationStrategiesForAllClasses",
                                                                   new Class[]{String.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setAllocationStrategies",
                                                        new Class[]{String.class, int.class,
                                                                    AllocationStrategyStructV2[].class, int.class});
            newClassType =
                    new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.ALLOCATION_STRATEGY,
                                                           AllocationStrategyTradingPropertyGroup.class,
                                                           queryMethod, queryParmNames,
                                                           saveMethod, saveParmNames,
                                                           queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.ALLOCATION_STRATEGY.getName(), newClassType);

            //AUTO_EX_ELIGIBLE_STRATEGY_TYPES
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getAutoExEligibleStrategyTypes",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getAutoExEligibleStrategyTypesForAllClasses",
                                                                   new Class[]{String.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setAutoExEligibleStrategyTypes",
                                                        new Class[]{String.class, int.class,
                                                                    int[].class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.AUTO_EX_ELIGIBLE_STRATEGY_TYPES,
                                                   SimpleIntegerSequenceOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.AUTO_EX_ELIGIBLE_STRATEGY_TYPES.getName(), newClassType);

            //AUCTION_ENABLED
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getAuctionEnabled",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getAuctionEnabledForAllClasses",
                                                                   new Class[]{String.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setAuctionEnabled",
                                                        new Class[]{String.class, int.class,
                                                                    AuctionBooleanStruct[].class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.AUCTION_ENABLED,
                                                   AuctionEnabledTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.AUCTION_ENABLED.getName(), newClassType);

            //AUCTION_MIN_ORDER_SIZE_FOR_TICKS_ABOVE_NBBO
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getAuctionMinOrderSizeForTicksAboveNBBO",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getAuctionMinOrderSizeForTicksAboveNBBOForAllClasses",
                                                                   new Class[]{String.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setAuctionMinOrderSizeForTicksAboveNBBO",
                                                        new Class[]{String.class, int.class,
                                                                    AuctionOrderSizeTicksStruct[].class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.AUCTION_MIN_ORDER_SIZE_FOR_TICKS_ABOVE_NBBO,
                                                   AuctionOrderSizeTicksTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.AUCTION_MIN_ORDER_SIZE_FOR_TICKS_ABOVE_NBBO.getName(),
                                             newClassType);

            //AUCTION_MIN_PRICE_INCREMENT
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getAuctionMinPriceIncrement",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getAuctionMinPriceIncrementForAllClasses",
                                                                   new Class[]{String.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setAuctionMinPriceIncrement",
                                                        new Class[]{String.class, int.class,
                                                                    AuctionLongStruct[].class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.AUCTION_MIN_PRICE_INCREMENT,
                                                   AuctionLongTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.AUCTION_MIN_PRICE_INCREMENT.getName(), newClassType);

            //AUCTION_MIN_QUOTERS
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getAuctionMinQuoters",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getAuctionMinQuotersForAllClasses",
                                                                   new Class[]{String.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setAuctionMinQuoters",
                                                        new Class[]{String.class, int.class,
                                                                    AuctionLongStruct[].class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.AUCTION_MIN_QUOTERS,
                                                   AuctionLongTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.AUCTION_MIN_QUOTERS.getName(),
                                             newClassType);

            //AUCTION_ORDER_TICKS_AWAY_FROM_NBBO
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getAuctionOrderTicksAwayFromNBBO",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getAuctionOrderTicksAwayFromNBBOForAllClasses",
                                                                   new Class[]{String.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setAuctionOrderTicksAwayFromNBBO",
                                                        new Class[]{String.class, int.class,
                                                                    AuctionLongStruct[].class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.AUCTION_ORDER_TICKS_AWAY_FROM_NBBO,
                                                   AuctionLongTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.AUCTION_ORDER_TICKS_AWAY_FROM_NBBO.getName(),
                                             newClassType);

            //AUCTION_RECEIVER_TYPES
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getAuctionReceiverTypes",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getAuctionReceiverTypesForAllClasses",
                                                                   new Class[]{String.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setAuctionReceiverTypes",
                                                        new Class[]{String.class, int.class,
                                                                    AuctionLongStruct[].class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.AUCTION_RECEIVER_TYPES,
                                                   AuctionLongTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.AUCTION_RECEIVER_TYPES.getName(),
                                             newClassType);

            //AUCTION_TIME_TO_LIVE
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getAuctionTimeToLive",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getAuctionTimeToLiveForAllClasses",
                                                                   new Class[]{String.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setAuctionTimeToLive",
                                                        new Class[]{String.class, int.class,
                                                                    AuctionRangeStruct[].class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.AUCTION_TIME_TO_LIVE,
                                                   AuctionRangeTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.AUCTION_TIME_TO_LIVE.getName(), newClassType);

            //BOOK_DEPTH_SIZE
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getBookDepthSize",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                                    OldTradingPropertyGroup.TRADING_PROPERTY_TYPE_NUMBER};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getBookDepthSizeAllClasses",
                                                                   new Class[]{String.class, int.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setBookDepthSize",
                                                        new Class[]{String.class, int.class,
                                                                    int.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.BOOK_DEPTH_SIZE,
                                                   SimpleIntegerOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.BOOK_DEPTH_SIZE.getName(), newClassType);

            //CONTINGENCY_TIME_TO_LIVE
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getContingencyTimeToLive",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                                    OldTradingPropertyGroup.TRADING_PROPERTY_TYPE_NUMBER};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getContingencyTimeToLiveAllClasses",
                                                                   new Class[]{String.class, int.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setContingencyTimeToLive",
                                                        new Class[]{String.class, int.class,
                                                                    int.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.CONTINGENCY_TIME_TO_LIVE,
                                                   SimpleIntegerOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.CONTINGENCY_TIME_TO_LIVE.getName(), newClassType);

            //CONTINUOUS_QUOTE_PERIOD_FOR_CREDIT
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getContinuousQuotePeriodForCredit",
                                                         new Class[]{String.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                                    OldTradingPropertyGroup.TRADING_PROPERTY_TYPE_NUMBER};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getContinuousQuotePeriodForCreditAllClasses",
                                                                   new Class[]{String.class, int.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setContinuousQuotePeriodForCredit",
                                                        new Class[]{String.class, int.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.CONTINUOUS_QUOTE_PERIOD_FOR_CREDIT,
                                                   SimpleIntegerOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.CONTINUOUS_QUOTE_PERIOD_FOR_CREDIT.getName(), newClassType);

            //DPM_PARTICIPATION_PERCENTAGE
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getDPMParticipationPercentage",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                                    OldTradingPropertyGroup.TRADING_PROPERTY_TYPE_NUMBER};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getDPMParticipationPercentageAllClasses",
                                                                   new Class[]{String.class, int.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setDPMParticipationPercentage",
                                                        new Class[]{String.class, int.class,
                                                                    double.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.DPM_PARTICIPATION_PERCENTAGE,
                                                   SimpleDoubleOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.DPM_PARTICIPATION_PERCENTAGE.getName(), newClassType);

            //DPM_RIGHTS_SCALES
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getDpmRightsScales",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getDpmRightsScaleForAllClasses",
                                                                   new Class[]{String.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setDpmRightsScales",
                                                        new Class[]{String.class, int.class,
                                                                    DpmRightsScaleStruct[].class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.DPM_RIGHTS_SCALES,
                                                   DpmRightsScaleTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.DPM_RIGHTS_SCALES.getName(), newClassType);

            //DPM_RIGHTS_SPLIT_RATE
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getDpmRightsSplitRate",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getDpmRightsSplitRateForAllClasses",
                                                                   new Class[]{String.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setDpmRightsSplitRate",
                                                        new Class[]{String.class, int.class,
                                                                    double.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.DPM_RIGHTS_SPLIT_RATE,
                                                   SimpleDoubleOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.DPM_RIGHTS_SPLIT_RATE.getName(), newClassType);

            //ETF_FLAG
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getETFFlag",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getETFFlagForAllClasses",
                                                                   new Class[]{String.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setETFFlag",
                                                        new Class[]{String.class, int.class,
                                                                    boolean.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.ETF_FLAG,
                                                   SimpleBooleanOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.ETF_FLAG.getName(), newClassType);

            //EPW_STRUCT
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getExchangePrescribedWidth",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getExchangePrescribedWidthForAllClasses",
                                                                   new Class[]{String.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setExchangePrescribedWidth",
                                                        new Class[]{String.class, int.class,
                                                                    EPWStruct[].class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.EPW_STRUCT,
                                                   EPWTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.EPW_STRUCT.getName(), newClassType);

            //FAST_MARKET_SPREAD_MULTIPLIER
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getFastMarketSpreadMultiplier",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                                    OldTradingPropertyGroup.TRADING_PROPERTY_TYPE_NUMBER};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getFastMarketSpreadMultiplierAllClasses",
                                                                   new Class[]{String.class, int.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setFastMarketSpreadMultiplier",
                                                        new Class[]{String.class, int.class,
                                                                    double.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.FAST_MARKET_SPREAD_MULTIPLIER,
                                                   SimpleDoubleOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.FAST_MARKET_SPREAD_MULTIPLIER.getName(), newClassType);

            //FIRM_CUSTOMER_QUOTE_SIZE
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getFCQS",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getFCQSForAllClasses",
                                                                   new Class[]{String.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setFCQS",
                                                        new Class[]{String.class, int.class,
                                                                    int.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.FIRM_CUSTOMER_QUOTE_SIZE,
                                                   SimpleIntegerOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.FIRM_CUSTOMER_QUOTE_SIZE.getName(), newClassType);

            //FIRM_PRINCIPAL_QUOTE_SIZE
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getFPQS",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getFPQSForAllClasses",
                                                                   new Class[]{String.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setFPQS",
                                                        new Class[]{String.class, int.class,
                                                                    int.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.FIRM_PRINCIPAL_QUOTE_SIZE,
                                                   SimpleIntegerOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.FIRM_PRINCIPAL_QUOTE_SIZE.getName(), newClassType);

            //INTERNALIZATION_GUARANTEED_PERCENTAGE
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getInternalizationGuaranteedPercentage",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getInternalizationGuaranteedPercentageForAllClasses",
                                                                   new Class[]{String.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setInternalizationGuaranteedPercentage",
                                                        new Class[]{String.class, int.class,
                                                                    InternalizationPercentageStruct[].class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.INTERNALIZATION_GUARANTEED_PERCENTAGE,
                                                   InternalizationPercentageTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.INTERNALIZATION_GUARANTEED_PERCENTAGE.getName(), newClassType);

            //IPP_MIN_SIZE
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getIPPMinSize",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                                    OldTradingPropertyGroup.TRADING_PROPERTY_TYPE_NUMBER};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getIPPMinSizeAllClasses",
                                                                   new Class[]{String.class, int.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setIPPMinSize",
                                                        new Class[]{String.class, int.class,
                                                                    int.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.IPP_MIN_SIZE,
                                                   SimpleIntegerOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.IPP_MIN_SIZE.getName(), newClassType);

            //IPP_TOLERANCE_AMOUNT
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getIPPToleranceAmount",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getIPPToleranceAmountForAllClasses",
                                                                   new Class[]{String.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setIPPToleranceAmount",
                                                        new Class[]{String.class, int.class,
                                                                    double.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.IPP_TOLERANCE_AMOUNT,
                                                   SimpleDoubleOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.IPP_TOLERANCE_AMOUNT.getName(), newClassType);

            //IPP_TRADE_THROUGH_FLAG
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getIPPTradeThroughFlag",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                                    OldTradingPropertyGroup.TRADING_PROPERTY_TYPE_NUMBER};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getIPPTradeThroughFlagAllClasses",
                                                                   new Class[]{String.class, int.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setIPPTradeThroughFlag",
                                                        new Class[]{String.class, int.class,
                                                                    boolean.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.IPP_TRADE_THROUGH_FLAG,
                                                   SimpleBooleanOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.IPP_TRADE_THROUGH_FLAG.getName(), newClassType);

            //LINKAGE_ENABLED_FLAG
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getLinkageEnabledFlag",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getLinkageEnabledFlagForAllClasses",
                                                                   new Class[]{String.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setLinkageEnabledFlag",
                                                        new Class[]{String.class, int.class,
                                                                    boolean.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.LINKAGE_ENABLED_FLAG,
                                                   SimpleBooleanOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.LINKAGE_ENABLED_FLAG.getName(), newClassType);

            //LOT_SIZE
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getLotSize",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getLotSizeForAllClasses",
                                                                   new Class[]{String.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setLotSize",
                                                        new Class[]{String.class, int.class,
                                                                    int.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.LOT_SIZE,
                                                   SimpleIntegerOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.LOT_SIZE.getName(), newClassType);

            //MIN_QUOTE_CREDIT_DEFAULT_SIZE
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getMinQuoteCreditDefaultSize",
                                                         new Class[]{String.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                                    OldTradingPropertyGroup.TRADING_PROPERTY_TYPE_NUMBER};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getMinQuoteCreditDefaultSizeAllClasses",
                                                                   new Class[]{String.class, int.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setMinQuoteCreditDefaultSize",
                                                        new Class[]{String.class, int.class, int.class});
            newClassType =
                    new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.MIN_QUOTE_CREDIT_DEFAULT_SIZE,
                                                           SimpleIntegerOldTradingPropertyGroup.class,
                                                           queryMethod, queryParmNames,
                                                           saveMethod, saveParmNames,
                                                           queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.MIN_QUOTE_CREDIT_DEFAULT_SIZE.getName(), newClassType);

            //MIN_SIZE_FOR_BLOCK_TRADE
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getMinSizeForBlockTrade",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                                    OldTradingPropertyGroup.TRADING_PROPERTY_TYPE_NUMBER};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getMinSizeForBlockTradeAllClasses",
                                                                   new Class[]{String.class, int.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setMinSizeForBlockTrade",
                                                        new Class[]{String.class, int.class, int.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.MIN_SIZE_FOR_BLOCK_TRADE,
                                                   SimpleIntegerOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.MIN_SIZE_FOR_BLOCK_TRADE.getName(), newClassType);

            //NEEDS_DPM_QUOTE_TO_OPEN
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getNeedsDpmQuoteToOpen",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getNeedsDpmQuoteToOpenForAllClasses",
                                                                   new Class[]{String.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setNeedsDpmQuoteToOpen",
                                                        new Class[]{String.class, int.class,
                                                                    boolean.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.NEEDS_DPM_QUOTE_TO_OPEN,
                                                   SimpleBooleanOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.NEEDS_DPM_QUOTE_TO_OPEN.getName(), newClassType);

            //OPENING_TIME_PERIOD_RANGE
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getOpeningPeriodTimeRange",
                                                         new Class[]{String.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                                    OldTradingPropertyGroup.TRADING_PROPERTY_TYPE_NUMBER};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getOpeningPeriodTimeRangeAllClasses",
                                                                   new Class[]{String.class, int.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setOpeningPeriodTimeRange",
                                                        new Class[]{String.class, TimeRangeStruct.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.OPENING_TIME_PERIOD_RANGE,
                                                   TimeRangeTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.OPENING_TIME_PERIOD_RANGE.getName(), newClassType);

            //OPENING_PRICE_DELAY
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getOpeningPriceDelay",
                                                         new Class[]{String.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                                    OldTradingPropertyGroup.TRADING_PROPERTY_TYPE_NUMBER};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getOpeningPriceDelayAllClasses",
                                                                   new Class[]{String.class, int.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setOpeningPriceDelay",
                                                        new Class[]{String.class, int.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.OPENING_PRICE_DELAY,
                                                   SimpleIntegerOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.OPENING_PRICE_DELAY.getName(), newClassType);

            //OPENING_PRICE_RATE
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getOpeningPriceRate",
                                                         new Class[]{String.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                                    OldTradingPropertyGroup.TRADING_PROPERTY_TYPE_NUMBER};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getOpeningPriceRateAllClasses",
                                                                   new Class[]{String.class, int.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setOpeningPriceRate",
                                                        new Class[]{String.class, int.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.OPENING_PRICE_RATE,
                                                   SimpleIntegerOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.OPENING_PRICE_RATE.getName(), newClassType);

            //PA_ORDER_TIME_TO_LIVE
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getPAOrderTimeToLive",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getPAOrderTimeToLiveForAllClasses",
                                                                   new Class[]{String.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setPAOrderTimeToLive",
                                                        new Class[]{String.class, int.class, int.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.PA_ORDER_TIME_TO_LIVE,
                                                   SimpleIntegerOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.PA_ORDER_TIME_TO_LIVE.getName(), newClassType);

            //P_ORDER_TIME_TO_LIVE
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getPOrderTimeToLive",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getPOrderTimeToLiveForAllClasses",
                                                                   new Class[]{String.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setPOrderTimeToLive",
                                                        new Class[]{String.class, int.class, int.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.P_ORDER_TIME_TO_LIVE,
                                                   SimpleIntegerOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.P_ORDER_TIME_TO_LIVE.getName(), newClassType);

            //PRECLOSING_TIME_PERIOD
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getPreClosingTimePeriod",
                                                         new Class[]{String.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                                    OldTradingPropertyGroup.TRADING_PROPERTY_TYPE_NUMBER};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getPreClosingTimePeriodAllClasses",
                                                                   new Class[]{String.class, int.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setPreClosingTimePeriod",
                                                        new Class[]{String.class, TimeRangeStruct.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.PRECLOSING_TIME_PERIOD,
                                                   TimeRangeTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.PRECLOSING_TIME_PERIOD.getName(), newClassType);

            //PRESCRIBED_WIDTH_RATIO
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getPrescribedWidthRatio",
                                                         new Class[]{String.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                                    OldTradingPropertyGroup.TRADING_PROPERTY_TYPE_NUMBER};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getPrescribedWidthRatioAllClasses",
                                                                   new Class[]{String.class, int.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setPrescribedWidthRatio",
                                                        new Class[]{String.class, double.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.PRESCRIBED_WIDTH_RATIO,
                                                   SimpleDoubleOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.PRESCRIBED_WIDTH_RATIO.getName(), newClassType);

            //PRODUCT_OPEN_PROCEDURE_TYPE
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getProductOpenProcedureType",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getProductOpenProcedureTypeForAllClasses",
                                                                   new Class[]{String.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setProductOpenProcedureType",
                                                        new Class[]{String.class, int.class, int.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.PRODUCT_OPEN_PROCEDURE_TYPE,
                                                   SimpleIntegerOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.PRODUCT_OPEN_PROCEDURE_TYPE.getName(), newClassType);

            //QUOTE_LOCK_MIN_TRADE_QTY
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getQuoteLockMinimumTradeQuantity",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getQuoteLockMinimumTradeQuantityAllClasses",
                                                                   new Class[]{String.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setQuoteLockMinimumTradeQuantity",
                                                        new Class[]{String.class, int.class, int.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.QUOTE_LOCK_MIN_TRADE_QTY,
                                                   SimpleIntegerOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.QUOTE_LOCK_MIN_TRADE_QTY.getName(), newClassType);

            //QUOTE_LOCK_NOTIFICATION_TIMER
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getQuoteLockNotificationTimer",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                                    OldTradingPropertyGroup.TRADING_PROPERTY_TYPE_NUMBER};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getQuoteLockNotificationTimerAllClasses",
                                                                   new Class[]{String.class, int.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setQuoteLockNotificationTimer",
                                                        new Class[]{String.class, int.class, int.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.QUOTE_LOCK_NOTIFICATION_TIMER,
                                                   SimpleIntegerOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.QUOTE_LOCK_NOTIFICATION_TIMER.getName(), newClassType);

            //QUOTE_LOCK_TIMER
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getQuoteLockTimer",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                                    OldTradingPropertyGroup.TRADING_PROPERTY_TYPE_NUMBER};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getQuoteLockTimerAllClasses",
                                                                   new Class[]{String.class, int.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setQuoteLockTimer",
                                                        new Class[]{String.class, int.class, int.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.QUOTE_LOCK_TIMER,
                                                   SimpleIntegerOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.QUOTE_LOCK_TIMER.getName(), newClassType);

            //QUOTE_TRIGGER_TIMER
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getQuoteTriggerTimer",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                                    OldTradingPropertyGroup.TRADING_PROPERTY_TYPE_NUMBER};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getQuoteTriggerTimerAllClasses",
                                                                   new Class[]{String.class, int.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setQuoteTriggerTimer",
                                                        new Class[]{String.class, int.class, int.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.QUOTE_TRIGGER_TIMER,
                                                   SimpleIntegerOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.QUOTE_TRIGGER_TIMER.getName(), newClassType);

            //RFQ_RESPONSE_RATIO
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getRFQResponseRatio",
                                                         new Class[]{String.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                                    OldTradingPropertyGroup.TRADING_PROPERTY_TYPE_NUMBER};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getRFQResponseRatioAllClasses",
                                                                   new Class[]{String.class, int.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setRFQResponseRatio",
                                                        new Class[]{String.class, double.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.RFQ_RESPONSE_RATIO,
                                                   SimpleDoubleOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.RFQ_RESPONSE_RATIO.getName(), newClassType);

            //RFQ_TIMEOUT
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getRFQTimeout",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                                    OldTradingPropertyGroup.TRADING_PROPERTY_TYPE_NUMBER};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getRFQTimeoutAllClasses",
                                                                   new Class[]{String.class, int.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setRFQTimeout",
                                                        new Class[]{String.class, int.class, int.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.RFQ_TIMEOUT,
                                                   SimpleIntegerOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.RFQ_TIMEOUT.getName(), newClassType);

            //SATISFACTION_ALERT_FLAG
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getSatisfactionAlertFlag",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getSatisfactionAlertFlagForAllClasses",
                                                                   new Class[]{String.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setSatisfactionAlertFlag",
                                                        new Class[]{String.class, int.class,
                                                                    boolean.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.SATISFACTION_ALERT_FLAG,
                                                   SimpleBooleanOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.SATISFACTION_ALERT_FLAG.getName(), newClassType);

            //S_ORDER_TIME_TO_CREATE
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getSOrderTimeToCreate",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getSOrderTimeToCreateForAllClasses",
                                                                   new Class[]{String.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setSOrderTimeToCreate",
                                                        new Class[]{String.class, int.class, int.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.S_ORDER_TIME_TO_CREATE,
                                                   SimpleIntegerOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.S_ORDER_TIME_TO_CREATE.getName(), newClassType);

            //S_ORDER_TIME_TO_CREATE_BEFORE_CLOSE
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getSOrderTimeToCreateBeforeClose",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getSOrderTimeToCreateBeforeCloseForAllClasses",
                                                                   new Class[]{String.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setSOrderTimeToCreateBeforeClose",
                                                        new Class[]{String.class, int.class, int.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.S_ORDER_TIME_TO_CREATE_BEFORE_CLOSE,
                                                   SimpleIntegerOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.S_ORDER_TIME_TO_CREATE_BEFORE_CLOSE.getName(), newClassType);

            //S_ORDER_TIME_TO_LIVE
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getSOrderTimeToLive",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getSOrderTimeToLiveForAllClasses",
                                                                   new Class[]{String.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setSOrderTimeToLive",
                                                        new Class[]{String.class, int.class, int.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.S_ORDER_TIME_TO_LIVE,
                                                   SimpleIntegerOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.S_ORDER_TIME_TO_LIVE.getName(),
                                             newClassType);

            //S_ORDER_TIME_TO_REJECT_FILL
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getSOrderTimeToRejectFill",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getSOrderTimeToRejectFillForAllClasses",
                                                                   new Class[]{String.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setSOrderTimeToRejectFill",
                                                        new Class[]{String.class, int.class, int.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.S_ORDER_TIME_TO_REJECT_FILL,
                                                   SimpleIntegerOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.S_ORDER_TIME_TO_REJECT_FILL.getName(), newClassType);

            //TRADE_TYPE
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getTradeType",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getTradeTypeForAllClasses",
                                                                   new Class[]{String.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setTradeType",
                                                        new Class[]{String.class, int.class, int.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.TRADE_TYPE,
                                                   SimpleIntegerOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.TRADE_TYPE.getName(), newClassType);

            //UMA_EQUAL_DISTRIBUTION_WEIGHT_FOR_DPM
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getUMAEqualDistributionWeightForDPM",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getUMAEqualDistributionWeightForDPMForAllClasses",
                                                                   new Class[]{String.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setUMAEqualDistributionWeightForDPM",
                                                        new Class[]{String.class, int.class, int.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.UMA_EQUAL_DISTRIBUTION_WEIGHT_FOR_DPM,
                                                   SimpleIntegerOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.UMA_EQUAL_DISTRIBUTION_WEIGHT_FOR_DPM.getName(), newClassType);

            //UMA_SPLIT_RATE
            queryParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                          OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                          OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            queryMethod = SystemAdminAPI.class.getMethod("getUMASplitRate",
                                                         new Class[]{String.class, int.class, IntHolder.class});
            queryAllClassesParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME};
            queryAllClassesMethod = SystemAdminAPI.class.getMethod("getUMASplitRateForAllClasses",
                                                                   new Class[]{String.class});
            saveParmNames = new String[]{OldTradingPropertyGroup.SESSION_PARM_NAME,
                                         OldTradingPropertyGroup.CLASS_KEY_PARM_NAME,
                                         OldTradingPropertyGroup.VALUE_PARM_NAME,
                                         OldTradingPropertyGroup.SEQUENCE_NUMBER_PARM_NAME};
            saveMethod = SystemAdminAPI.class.getMethod("setUMASplitRate",
                                                        new Class[]{String.class, int.class, double.class, int.class});
            newClassType =
            new ReflectiveTradingPropertyClassType(TradingPropertyTypeImpl.UMA_SPLIT_RATE,
                                                   SimpleDoubleOldTradingPropertyGroup.class,
                                                   queryMethod, queryParmNames,
                                                   saveMethod, saveParmNames,
                                                   queryAllClassesMethod, queryAllClassesParmNames);
            PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.UMA_SPLIT_RATE.getName(),
                                             newClassType);

            //insert new ones below

        }
        catch(NoSuchMethodException e)
        {
            DefaultExceptionHandlerHome.find().process(e, "Could not initialize TradingPropertyFactoryConversion.");
        }
    }
    //***** END OF EDITABLE SECTION *******************

    /**
     * Provides for the creation of a new trading property group that did not exist before.
     * @param sessionName to build group for
     * @param classKey to build group for
     * @param tradingPropertyName to build group for
     * @return an appropriate implementation of a new instance of a TradingPropertyGroup based on the values passed. The
     *         intention would be that this TradingPropertyGroup was not obtained from the host source.
     * @throws DataValidationException should be thrown if tradingPropertyName is not a known type.
     * @throws InvocationTargetException If any exception occurs from instantiating the appropriate domain wrapper
     * object, it is returned as the cause in this exception.
     */
    public TradingPropertyGroup createNewTradingPropertyGroup(String sessionName, int classKey,
                                                              String tradingPropertyName)
            throws DataValidationException, InvocationTargetException
    {
        TradingPropertyGroup newTradingPropertyGroup;

        ReflectiveTradingPropertyClassType classTypeWrapper = findReflectiveTradingPropertyClassType(tradingPropertyName);

        if(classTypeWrapper != null)
        {
            //we have a known property name and implementation type to try to instantiate
            newTradingPropertyGroup = createNewTradingPropertyGroup(classTypeWrapper, sessionName, classKey);
        }
        else
        {
            newTradingPropertyGroup = super.createNewTradingPropertyGroup(sessionName, classKey, tradingPropertyName);
        }
        return newTradingPropertyGroup;
    }

    /**
     * Gets the TradingPropertyGroup for a specific category, sessionName, classKey and tradingPropertyName.
     * @param sessionName to get group for
     * @param classKey to get group for
     * @param tradingPropertyName of specific group to get
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
    public TradingPropertyGroup getTradingPropertyGroup(String sessionName, int classKey, String tradingPropertyName)
            throws InvocationTargetException, DataValidationException, SystemException, NotFoundException,
                   CommunicationException, AuthorizationException, TransactionFailedException
    {
        TradingPropertyGroup newTradingPropertyGroup;

        ReflectiveTradingPropertyClassType classTypeWrapper = findReflectiveTradingPropertyClassType(tradingPropertyName);

        if(classTypeWrapper != null)
        {
            newTradingPropertyGroup = createNewTradingPropertyGroup(classTypeWrapper, sessionName, classKey);

            try
            {
                ((OldTradingPropertyGroup)newTradingPropertyGroup).loadTradingProperties();
            }
            catch(IllegalAccessException e)
            {
                throw new InvocationTargetException(e);
            }
        }
        else
        {
            newTradingPropertyGroup = super.getTradingPropertyGroup(sessionName, classKey, tradingPropertyName);
        }

        return newTradingPropertyGroup;
    }

    /**
     * Gets all the TradingPropertyGroup's for a specific sessionName and tradingPropertyName, for all classes.
     * @param sessionName to get groups for
     * @param tradingPropertyName of specific groups to get
     * @return an array of the appropriate implementation instance's of a TradingPropertyGroup based on the values
     *         passed. They will be all of the same type.
     * @throws InvocationTargetException If any exception occurs from instantiating the appropriate domain wrapper
     * object, it is returned as the cause in this exception.
     * @throws DataValidationException This exception is forwarded from the TradingPropertyGroup.setPropertyGroup
     * interface. It will probably indicate that the raw data used to create the TradingPropertyGroup was not of a valid
     * format.
     * @throws SystemException forwarded from PropertyServiceFacadeHome
     * @throws CommunicationException forwarded from PropertyServiceFacadeHome
     * @throws AuthorizationException forwarded from PropertyServiceFacadeHome
     */
    public TradingPropertyGroup[] getTradingPropertyGroupsForAllClasses(String sessionName, String tradingPropertyName)
            throws InvocationTargetException, DataValidationException, SystemException, CommunicationException,
                   AuthorizationException, TransactionFailedException
    {
        ReflectiveTradingPropertyClassType classTypeWrapper = findReflectiveTradingPropertyClassType(tradingPropertyName);

        if(classTypeWrapper != null)
        {
            Class classToCreate = classTypeWrapper.getClassType();
            try
            {
                Class[] parmTypes = {String.class, int.class};
                Constructor constructor = classToCreate.getConstructor(parmTypes);

                Object[] parms = {sessionName, new Integer(0)};
                OldTradingPropertyGroup templateTradingPropertyGroup =
                        (OldTradingPropertyGroup) constructor.newInstance(parms);

                OldTradingPropertyGroup templateCastedAsOldTPG =
                        initializeOldTradingPropertyGroup(templateTradingPropertyGroup, classTypeWrapper);

                OldTradingPropertyGroup[] newTradingPropertyGroups =
                        templateCastedAsOldTPG.getAllGroupsForAllClasses(sessionName);
                for(int i = 0; i < newTradingPropertyGroups.length; i++)
                {
                    OldTradingPropertyGroup newTradingPropertyGroup = newTradingPropertyGroups[i];
                    initializeOldTradingPropertyGroup(newTradingPropertyGroup, classTypeWrapper);
                }
                return newTradingPropertyGroups;
            }
            catch(InstantiationException e)
            {
                throw new InvocationTargetException(e, "Could not instantiate TradingPropertyGroup to act as a Template.");
            }
            catch(IllegalAccessException e)
            {
                throw new InvocationTargetException(e, "Could not instantiate TradingPropertyGroup to act as a Template.");
            }
            catch(NoSuchMethodException e)
            {
                throw new InvocationTargetException(e, "Could not instantiate TradingPropertyGroup to act as a Template.");
            }
        }
        else
        {
            return super.getTradingPropertyGroupsForAllClasses(sessionName, tradingPropertyName);
        }
    }

    /**
     * Gets all the TradingPropertyGroup's for a specific sessionName and classKey.
     * @param sessionName to get groups for
     * @param classKey to get groups for
     * @return an array of the appropriate implementation instances of TradingPropertyGroup's based on the values
     *         passed.
     * @throws InvocationTargetException If any exception occurs from instantiating the appropriate domain wrapper
     * object, it is returned as the cause in this exception.
     * @throws DataValidationException This exception is forwarded from the TradingPropertyGroup.setPropertyGroup
     * interface. It will probably indicate that the raw data used to create the TradingPropertyGroup's was not of a
     * valid format.
     * @throws SystemException forwarded from PropertyServiceFacadeHome
     * @throws CommunicationException forwarded from PropertyServiceFacadeHome
     * @throws AuthorizationException forwarded from PropertyServiceFacadeHome
     */
    public TradingPropertyGroup[] getAllTradingPropertyGroups(String sessionName, int classKey)
            throws InvocationTargetException, DataValidationException, SystemException, CommunicationException,
                   AuthorizationException, TransactionFailedException
    {
        TradingPropertyClassType[] myFactoryHandledTypes = getAllHandledClassTypes();

        TradingPropertyGroup[] domainGroups = super.getAllTradingPropertyGroups(sessionName, classKey);

        List groupsList = new ArrayList(domainGroups.length + myFactoryHandledTypes.length);
        for(int i = 0; i < domainGroups.length; i++)
        {
            groupsList.add(domainGroups[i]);
        }

        TradingPropertyGroup newGroup;
        for(int i = 0; i < myFactoryHandledTypes.length; i++)
        {
            TradingPropertyClassType tpClassType = myFactoryHandledTypes[i];

            String propertyName = tpClassType.getTradingPropertyType().getName();

            try
            {
                newGroup = getTradingPropertyGroup(sessionName, classKey, propertyName);
                groupsList.add(newGroup);
            }
            catch(NotFoundException e)
            {
                //normal exception, just won't add to collection
                if(GUILoggerHome.find().isDebugOn() &&
                   GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
                {
                    StringBuffer msg = new StringBuffer(100);
                    msg.append("NotFoundException received for getTradingPropertyGroup(");
                    msg.append(sessionName).append(", ").append(classKey).append(", ").append(propertyName).append(')');
                    GUILoggerHome.find().debug(msg.toString(), GUILoggerSABusinessProperty.TRADING_PROPERTY);
                }
            }
        }
        return (TradingPropertyGroup[]) groupsList.toArray(new TradingPropertyGroup[groupsList.size()]);
    }

    /**
     * Gets all the TradingProperty's from the TradingPropertyGroup obtained for a specific category, sessionName,
     * classKey and tradingPropertyName.
     * @param sessionName to get group for
     * @param classKey to get group for
     * @param tradingPropertyName of specific group to get
     * @return an array of all the TradingProperty's from the appropriate implementation instance of a
     *         TradingPropertyGroup based on the values passed. WARNING! WITHOUT THE TradingPropertyGroup THESE WILL NOT
     *         BE ABLE TO BE SAVED, IF MODIFIED.
     * @throws InvocationTargetException If any exception occurs from instantiating the appropriate domain wrapper
     * object, it is returned as the cause in this exception.
     * @throws DataValidationException This exception is forwarded from the TradingPropertyGroup.setPropertyGroup
     * interface. It will probably indicate that the Property's from the PropertyGroup or the PropertyGroup itself was
     * not of a valid format.
     * @throws SystemException forwarded from PropertyServiceFacadeHome
     * @throws NotFoundException forwarded from PropertyServiceFacadeHome
     * @throws CommunicationException forwarded from PropertyServiceFacadeHome
     * @throws AuthorizationException forwarded from PropertyServiceFacadeHome
     */
    public TradingProperty[] getAllTradingProperties(String sessionName, int classKey,
                                                            String tradingPropertyName)
            throws InvocationTargetException, DataValidationException, SystemException, NotFoundException,
                   CommunicationException, AuthorizationException, TransactionFailedException
    {
        TradingPropertyGroup group = getTradingPropertyGroup(sessionName, classKey, tradingPropertyName);
        TradingProperty[] properties = group.getAllTradingProperties();

        return properties;
    }

    /**
     * Gets all the TradingProperty's from all the TradingPropertyGroup's obtained for a specific sessionName and
     * classKey.
     * @param sessionName to get groups for
     * @param classKey to get groups for
     * @return an array of all the TradingProperty's from all the appropriate implementation instances of
     *         TradingPropertyGroup's based on the values passed. WARNING! WITHOUT THE TradingPropertyGroup THESE WILL
     *         NOT BE ABLE TO BE SAVED, IF MODIFIED.
     * @throws InvocationTargetException If any exception occurs from instantiating the appropriate domain wrapper
     * object, it is returned as the cause in this exception.
     * @throws DataValidationException This exception is forwarded from the TradingPropertyGroup.setPropertyGroup
     * interface. It will probably indicate that the Property's from the PropertyGroup or the PropertyGroup itself was
     * not of a valid format.
     * @throws SystemException forwarded from PropertyServiceFacadeHome
     * @throws CommunicationException forwarded from PropertyServiceFacadeHome
     * @throws AuthorizationException forwarded from PropertyServiceFacadeHome
     */
    public TradingProperty[] getAllTradingProperties(String sessionName, int classKey)
            throws InvocationTargetException, DataValidationException, SystemException, CommunicationException,
                   AuthorizationException, TransactionFailedException
    {
        TradingPropertyGroup[] allGroups = getAllTradingPropertyGroups(sessionName, classKey);

        List tpList = new ArrayList(allGroups.length * 10);

        for(int i = 0; i < allGroups.length; i++)
        {
            TradingPropertyGroup tradingPropertyGroup = allGroups[i];
            TradingProperty[] thisGroupsTP = tradingPropertyGroup.getAllTradingProperties();
            for(int j = 0; j < thisGroupsTP.length; j++)
            {
                TradingProperty tradingProperty = thisGroupsTP[j];
                tpList.add(tradingProperty);
            }
        }
        return (TradingProperty[]) tpList.toArray(new TradingProperty[tpList.size()]);
    }

    private ReflectiveTradingPropertyClassType[] getAllHandledClassTypes()
    {
        Collection values = PROPERTY_NAME_CLASS_TYPE_MAP.values();
        return (ReflectiveTradingPropertyClassType[]) values.toArray(new ReflectiveTradingPropertyClassType[values.size()]);
    }

    private ReflectiveTradingPropertyClassType findReflectiveTradingPropertyClassType(String tradingPropertyName)
    {
        ReflectiveTradingPropertyClassType classTypeWrapper =
                (ReflectiveTradingPropertyClassType) PROPERTY_NAME_CLASS_TYPE_MAP.get(tradingPropertyName);
        return classTypeWrapper;
    }

    private OldTradingPropertyGroup createNewTradingPropertyGroup(ReflectiveTradingPropertyClassType classTypeWrapper,
                                                                  String sessionName, int classKey)
            throws InvocationTargetException
    {
        OldTradingPropertyGroup newTradingPropertyGroup;
        try
        {
            Constructor constructor = classTypeWrapper.getConstructor();
            if(constructor != null)
            {
                //we have a known constructor that accepts (String, int), try to instantiate using
                //this constructor for session name and class key
                Object[] parms = {sessionName, new Integer(classKey)};
                newTradingPropertyGroup = (OldTradingPropertyGroup) constructor.newInstance(parms);
            }
            else
            {
                //we never found a constructor that takes (String, int), just try to instantiate using
                //default constructor. WARNING!!! DOMAIN OBJECT WILL NOT KNOW WHICH SESSION NAME
                //AND CLASS KEY IT IS FOR, SINCE THESE ARE IMMUTABLE
                Class classToCreate = classTypeWrapper.getClassType();
                newTradingPropertyGroup = (OldTradingPropertyGroup) classToCreate.newInstance();

                StringBuffer msg = new StringBuffer(100);
                msg.append("Created a new TradingPropertyGroup domain object by using the default ");
                msg.append("constructor. This TradingPropertyGroup will NOT know its Trading Session ");
                msg.append("Name or Class Key. It did not expose a constructor that accepted ");
                msg.append("(String, int). The class name was:").append(classToCreate.getName());

                GUILoggerHome.find().alarm("TradingPropertyFactoryConversion", msg.toString());
            }

            newTradingPropertyGroup = initializeOldTradingPropertyGroup(newTradingPropertyGroup, classTypeWrapper);
        }
        catch(InstantiationException e)
        {
            throw new InvocationTargetException(e);
        }
        catch(IllegalAccessException e)
        {
            throw new InvocationTargetException(e);
        }
        return newTradingPropertyGroup;
    }

    private OldTradingPropertyGroup initializeOldTradingPropertyGroup(OldTradingPropertyGroup newTradingPropertyGroup,
                                                                      ReflectiveTradingPropertyClassType classTypeWrapper)
    {
        if(newTradingPropertyGroup instanceof SimpleBooleanOldTradingPropertyGroup)
        {
            SimpleBooleanOldTradingPropertyGroup castedTPG =
                    (SimpleBooleanOldTradingPropertyGroup) newTradingPropertyGroup;
            castedTPG.setTradingPropertyType(classTypeWrapper.getTradingPropertyType());
        }
        else if(newTradingPropertyGroup instanceof SimpleDoubleOldTradingPropertyGroup)
        {
            SimpleDoubleOldTradingPropertyGroup castedTPG =
                    (SimpleDoubleOldTradingPropertyGroup) newTradingPropertyGroup;
            castedTPG.setTradingPropertyType(classTypeWrapper.getTradingPropertyType());
        }
        else if(newTradingPropertyGroup instanceof SimpleIntegerOldTradingPropertyGroup)
        {
            SimpleIntegerOldTradingPropertyGroup castedTPG =
                    (SimpleIntegerOldTradingPropertyGroup) newTradingPropertyGroup;
            castedTPG.setTradingPropertyType(classTypeWrapper.getTradingPropertyType());
        }
        else if(newTradingPropertyGroup instanceof SimpleIntegerSequenceOldTradingPropertyGroup)
        {
            SimpleIntegerSequenceOldTradingPropertyGroup castedTPG =
                    (SimpleIntegerSequenceOldTradingPropertyGroup) newTradingPropertyGroup;
            castedTPG.setTradingPropertyType(classTypeWrapper.getTradingPropertyType());
        }
        else if(newTradingPropertyGroup instanceof TimeRangeTradingPropertyGroup)
        {
            TimeRangeTradingPropertyGroup castedTPG =
                    (TimeRangeTradingPropertyGroup) newTradingPropertyGroup;
            castedTPG.setTradingPropertyType(classTypeWrapper.getTradingPropertyType());
        }
        else if(newTradingPropertyGroup instanceof AuctionLongTradingPropertyGroup)
        {
            AuctionLongTradingPropertyGroup castedTPG =
                    (AuctionLongTradingPropertyGroup) newTradingPropertyGroup;
            castedTPG.setTradingPropertyType(classTypeWrapper.getTradingPropertyType());
        }
        else if(newTradingPropertyGroup instanceof AuctionRangeTradingPropertyGroup)
        {
            AuctionRangeTradingPropertyGroup castedTPG =
                    (AuctionRangeTradingPropertyGroup) newTradingPropertyGroup;
            castedTPG.setTradingPropertyType(classTypeWrapper.getTradingPropertyType());
        }

        newTradingPropertyGroup.setQueryMethod(classTypeWrapper.getQueryMethod());
        newTradingPropertyGroup.setQueryParameterOrder(classTypeWrapper.getQueryMethodParmNames());
        newTradingPropertyGroup.setSaveMethod(classTypeWrapper.getSaveMethod());
        newTradingPropertyGroup.setSaveParameterOrder(classTypeWrapper.getSaveMethodParmNames());
        newTradingPropertyGroup.setQueryAllClassesMethod(classTypeWrapper.getQueryAllClassesMethod());
        newTradingPropertyGroup.setQueryAllClassesParameterOrder(classTypeWrapper.getQueryAllClassesMethodParmNames());

        return newTradingPropertyGroup;
    }
}
