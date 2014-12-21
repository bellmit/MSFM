//
// -----------------------------------------------------------------------------------
// Source file: TradingPropertyFactoryImpl.java
//
// PACKAGE: com.cboe.domain.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.tradingProperty;

import com.cboe.domain.property.BasicPropertyParser;
import com.cboe.domain.property.PropertyServiceFacadeHome;
import com.cboe.domain.tradingProperty.common.SimpleBooleanTradingPropertyGroup;
import com.cboe.domain.tradingProperty.common.SimpleDoubleTradingPropertyGroup;
import com.cboe.domain.tradingProperty.common.SimpleIntegerTradingPropertyGroup;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmiConstants.ProductClass;
import com.cboe.idl.cmiErrorCodes.NotFoundCodes;
import com.cboe.idl.constants.PropertyCategoryTypes;
import com.cboe.idl.constants.PropertyQueryTypes;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.tradingProperty.TradingProperty;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyFactory;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyGroup;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.event.EventChannelListener;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * This factory provides the conversion of PropertyGroup's to TradingPropertyGroup implementations.
 */
public class TradingPropertyFactoryImpl implements TradingPropertyFactory
{
    /**
     * Provides a mapping from the TradingProperty names to the Class that is responsible for that TradingProperty's
     * implementation.
     */
    private static final Map<String, TradingPropertyClassType> PROPERTY_NAME_CLASS_TYPE_MAP = 
        new HashMap<String, TradingPropertyClassType>(101);

    static
    {
        TradingPropertyClassType newClassType;

        //***** START OF EDITABLE SECTION *****************
        // This static initializer needs modified for each new type of domain implementation of the TradingProperty
        // interface. You must instantiate a new TradingPropertyClassType object. Then this new TradingPropertyClassType
        // must be added to the Map above. You must use the tradingPropertyName that you passed to the constructor
        // of TradingPropertyClassType as the key when inserting into the Map.
        // See the class TradingPropertyClassType for detailed comments and documentation on that class.

        newClassType = new TradingPropertyClassType(AuctionMinMaxOrderSizeGroup.TRADING_PROPERTY_TYPE,
                                                    AuctionMinMaxOrderSizeGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(AuctionMinMaxOrderSizeGroup.TRADING_PROPERTY_TYPE.getName(), newClassType);

        newClassType = new TradingPropertyClassType(PDPMRightsScalesGroup.TRADING_PROPERTY_TYPE,
                                                    PDPMRightsScalesGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(PDPMRightsScalesGroup.TRADING_PROPERTY_TYPE.getName(), newClassType);

        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.ALLOW_MARKET_ORDER,
                                                    SimpleBooleanTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.ALLOW_MARKET_ORDER.getName(), newClassType);

        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.PRICE_PROTECT_PERCENTAGE,
                                                    SimpleDoubleTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.PRICE_PROTECT_PERCENTAGE.getName(), newClassType);

        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.SIZE_INCREASE_PERCENTAGE_FOR_NEW_RFP,
                                                    SimpleDoubleTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.SIZE_INCREASE_PERCENTAGE_FOR_NEW_RFP.getName(), newClassType );

        newClassType = new TradingPropertyClassType(AllowedHALTypesGroup.TRADING_PROPERTY_TYPE,
                                                    AllowedHALTypesGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(AllowedHALTypesGroup.TRADING_PROPERTY_TYPE.getName(), newClassType);

        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.HAL_TRIGGER_TIMER,
                                                    SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.HAL_TRIGGER_TIMER.getName(), newClassType );

        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.ITS_COMMITMENT_AUTO_CANCEL_THRESHOLD,
                SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.ITS_COMMITMENT_AUTO_CANCEL_THRESHOLD.getName(), newClassType );

        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.BOTR_ENABLED,
                                                    SimpleBooleanTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.BOTR_ENABLED.getName(), newClassType);

        newClassType = new TradingPropertyClassType(EOPCalcStartTimeGroup.TRADING_PROPERTY_TYPE,
                                                    EOPCalcStartTimeGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(EOPCalcStartTimeGroup.TRADING_PROPERTY_TYPE.getName(), newClassType);

        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.EOP_INTERVAL,
                                                    SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.EOP_INTERVAL.getName(), newClassType);

        newClassType = new TradingPropertyClassType(LeapsOpeningEPWGroup.TRADING_PROPERTY_TYPE,
                                                    LeapsOpeningEPWGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(LeapsOpeningEPWGroup.TRADING_PROPERTY_TYPE.getName(), newClassType);

        newClassType = new TradingPropertyClassType(BOTRRangeScaleGroup.TRADING_PROPERTY_TYPE,
                                                    BOTRRangeScaleGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(BOTRRangeScaleGroup.TRADING_PROPERTY_TYPE.getName(), newClassType);

        newClassType = new TradingPropertyClassType(OpeningPriceValidationGroup.TRADING_PROPERTY_TYPE,
                                                    OpeningPriceValidationGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(OpeningPriceValidationGroup.TRADING_PROPERTY_TYPE.getName(), newClassType);

        newClassType = new TradingPropertyClassType(OpeningExchangePrescribedWidthGroup.TRADING_PROPERTY_TYPE,
                                                    OpeningExchangePrescribedWidthGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(OpeningExchangePrescribedWidthGroup.TRADING_PROPERTY_TYPE.getName(), newClassType);

        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.MARKET_TURNER_PERCENTAGE,
                                                    SimpleDoubleTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.MARKET_TURNER_PERCENTAGE.getName(),newClassType);
        
        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.REPORTING_PRICE_ADJUSTMENT,
        		                                    SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.REPORTING_PRICE_ADJUSTMENT.getName(),newClassType);

        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.CROSS_MIN_ORDER_SIZE,
                                                    SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.CROSS_MIN_ORDER_SIZE.getName(), newClassType);

        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.CROSS_MIN_DOLLAR_AMOUNT,
                                                     SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.CROSS_MIN_DOLLAR_AMOUNT.getName(), newClassType);

        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.MARKET_LOT_SIZE,
                                                     SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.MARKET_LOT_SIZE.getName(), newClassType);

        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.NBBO_CROSS_MIN_ORDER_SIZE,
                SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.NBBO_CROSS_MIN_ORDER_SIZE.getName(), newClassType);

        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.NBBO_CROSS_MIN_DOLLAR_AMOUNT,
                 SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.NBBO_CROSS_MIN_DOLLAR_AMOUNT.getName(), newClassType);

        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.AUTO_LINK_ENABLED,
                SimpleBooleanTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.AUTO_LINK_ENABLED.getName(), newClassType);

        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.MANUALQUOTE_CANCEL_TIMER_FOR_PAR,
        		                                    SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.MANUALQUOTE_CANCEL_TIMER_FOR_PAR.getName(),newClassType);

        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.MANUALQUOTE_CANCEL_TIMER_FOR_NONPAR,
        		                                    SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.MANUALQUOTE_CANCEL_TIMER_FOR_NONPAR.getName(),newClassType);

        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.AUTOEX_SIZE_AGAINST_NONCUSTOMER,
        		                                    SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.AUTOEX_SIZE_AGAINST_NONCUSTOMER.getName(),newClassType);

        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.EXTREMELY_WIDE_QUOTE_WIDTH,
        		                                    ExtremelyWideQuoteWidthGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.EXTREMELY_WIDE_QUOTE_WIDTH.getName(),newClassType);

        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.INDEX_HYBRID_INDICATOR,
        		                                    SimpleBooleanTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.INDEX_HYBRID_INDICATOR.getName(),newClassType);

        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.MOC_INTERVAL_SECONDS,
                                                    SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.MOC_INTERVAL_SECONDS.getName(), newClassType );
        
        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.UIM_ACTIVE,
                SimpleBooleanTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.UIM_ACTIVE.getName(), newClassType );
        
        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.UIM_TIMEOUT,
                SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.UIM_TIMEOUT.getName(), newClassType );

        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.HOLD_BACK_TIMER,
        		                                    SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.HOLD_BACK_TIMER.getName(),newClassType);         

        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.BBO_ALLOCATION_PERCENTAGE,
                                                    SimpleDoubleTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.BBO_ALLOCATION_PERCENTAGE.getName(), newClassType );


        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.ALLOCATE_ALL_TO_RIGHTS_PARTICIPANT,
                SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.ALLOCATE_ALL_TO_RIGHTS_PARTICIPANT.getName(),newClassType);
        
        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.PUBLISH_LARGE_TRADE_TICKER_SIZE,
                SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.PUBLISH_LARGE_TRADE_TICKER_SIZE.getName(),newClassType);
        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.AON_SOLICITATION_MIN_QUANTITY,
                SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.AON_SOLICITATION_MIN_QUANTITY.getName(),newClassType);
        
        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.HALO_TRIGGER_TIMER,
                SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.HALO_TRIGGER_TIMER.getName(), newClassType );
        
        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.MAX_PROD_PER_BD_CLASS_REFRESH_CMD,
                SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.MAX_PROD_PER_BD_CLASS_REFRESH_CMD.getName(), newClassType );
        
        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.OPENING_MAX_QUOTE_INVERSION,
                OpeningMaxQuoteInversionGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.OPENING_MAX_QUOTE_INVERSION.getName(), newClassType );
        
        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.OPENING_MAX_RETRIES_WAITING_FOR_QUOTE,
                SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.OPENING_MAX_RETRIES_WAITING_FOR_QUOTE.getName(), newClassType );        
        
        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.TICKS_AWAY_FOR_RECOA,
                SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.TICKS_AWAY_FOR_RECOA.getName(), newClassType );
        
        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.RECOA_INTERVAL,
                SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.RECOA_INTERVAL.getName(), newClassType );        
        
        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.NUMBER_OF_ATTEMPTS_FOR_RECOA,
                SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.NUMBER_OF_ATTEMPTS_FOR_RECOA.getName(), newClassType );
        
        
        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.SLEEP_TIMER_FOR_RECOA,
                SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.SLEEP_TIMER_FOR_RECOA.getName(), newClassType );       
                
        newClassType = new TradingPropertyClassType(StrategyEPWGroup.TRADING_PROPERTY_TYPE,
                StrategyEPWGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(StrategyEPWGroup.TRADING_PROPERTY_TYPE.getName(), newClassType);

        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.DIRECTED_AIM_TIMER,
                SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.DIRECTED_AIM_TIMER.getName(), newClassType );
        
        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.ALLOWED_HAL_ORIGIN_CODES,
                AllowedHalOriginCodesPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.ALLOWED_HAL_ORIGIN_CODES.getName(),newClassType);

        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.AUTO_LINK_PREFERRED_TIE_EXCHANGES,
                AutoLinkPreferredTieExchangesGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.AUTO_LINK_PREFERRED_TIE_EXCHANGES.getName(),newClassType);
        
        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.AUTO_LINK_ORIGIN_CODES,
                AutoLinkOriginCodesGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.AUTO_LINK_ORIGIN_CODES.getName(),newClassType);
        
        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.AUTO_LINK_DISQUALIFIED_EXCHANGES,
                AutoLinkDisqualifiedExchangesGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.AUTO_LINK_DISQUALIFIED_EXCHANGES.getName(),newClassType);

        newClassType = new TradingPropertyClassType(MKTOrderDrillThroughPenniesGroup.TRADING_PROPERTY_TYPE,
                MKTOrderDrillThroughPenniesGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(MKTOrderDrillThroughPenniesGroup.TRADING_PROPERTY_TYPE.getName(), newClassType);

        
        
        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.TICKS_AWAY_FOR_CPS_RECOA,
                SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.TICKS_AWAY_FOR_CPS_RECOA.getName(), newClassType );
        
        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.CPS_RECOA_INTERVAL,
                SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.CPS_RECOA_INTERVAL.getName(), newClassType );        
        
        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.NUMBER_OF_ATTEMPTS_FOR_CPS_RECOA,
                SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.NUMBER_OF_ATTEMPTS_FOR_CPS_RECOA.getName(), newClassType );
        
        
        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.SLEEP_TIMER_FOR_CPS_RECOA,
                SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.SLEEP_TIMER_FOR_CPS_RECOA.getName(), newClassType );
              
        
        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.ENABLE_CPS_BOOKING,
                SimpleBooleanTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.ENABLE_CPS_BOOKING.getName(), newClassType);
        
        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.QCC_MIN_VALUE, SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.QCC_MIN_VALUE.getName(), newClassType);
        
        newClassType = new TradingPropertyClassType(RegularMarketHoursGroup.TRADING_PROPERTY_TYPE,
        		RegularMarketHoursGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(RegularMarketHoursGroup.TRADING_PROPERTY_TYPE.getName(), newClassType);

    	newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.ENABLE_REGULAR_MARKET_TIME_VALIDATION,
                SimpleBooleanTradingPropertyGroup.class);
    	PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.ENABLE_REGULAR_MARKET_TIME_VALIDATION.getName(), newClassType);

    	newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.ASYNCHRONOUS_TRADING_ENABLED,
                SimpleBooleanTradingPropertyGroup.class);
    	PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.ASYNCHRONOUS_TRADING_ENABLED.getName(), newClassType);
        
	newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.ROLLOUT_FLAG_BY_BC,
                RolloutFlagByBCGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.ROLLOUT_FLAG_BY_BC.getName(),newClassType);

        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.CPS_SPLITTING_MINIMUM_STOCK_NBBO_QUANTITY,
                SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.CPS_SPLITTING_MINIMUM_STOCK_NBBO_QUANTITY.getName(), newClassType );

        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.CPS_SPLITTING_MINIMUM_OPTION_NBBO_BID,
                SimpleDoubleTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.CPS_SPLITTING_MINIMUM_OPTION_NBBO_BID.getName(), newClassType );

        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.CPS_SPLITTING_MAX_STOCK_ORDER_SIZE,
                SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.CPS_SPLITTING_MAX_STOCK_ORDER_SIZE.getName(), newClassType );

        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.CPS_SPLITTING_MAX_OPTION_ORDER_SIZE,
                SimpleIntegerTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.CPS_SPLITTING_MAX_OPTION_ORDER_SIZE.getName(), newClassType );

        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.ENABLE_CPS_MKT_SPLITTING,
                SimpleBooleanTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.ENABLE_CPS_MKT_SPLITTING.getName(), newClassType);
        
        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.VARIANCE_STRIP_INDICATOR,
                SimpleBooleanTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.VARIANCE_STRIP_INDICATOR.getName(), newClassType);

        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.ALLOWED_SAL_ORIGIN_CODES,
                AllowedSalOriginCodesPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.ALLOWED_SAL_ORIGIN_CODES.getName(), newClassType);
        
        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl. ALLOW_COMPLEX_TRADES_WITH_QUOTES,
                SimpleBooleanTradingPropertyGroup.class);    
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.ALLOW_COMPLEX_TRADES_WITH_QUOTES.getName(), newClassType);
    
        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.ALLOWED_WTP_ORIGIN_CODES,
        		AllowedWtpOriginCodesPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.ALLOWED_WTP_ORIGIN_CODES.getName(), newClassType);
        
        newClassType = new TradingPropertyClassType(TradingPropertyTypeImpl.ENABLE_EARLY_CLOSE , SimpleBooleanTradingPropertyGroup.class);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(TradingPropertyTypeImpl.ENABLE_EARLY_CLOSE.getName(), newClassType);
        
        //***** END OF EDITABLE SECTION *******************
        newClassType = null;
    }

    protected static final int DEFAULT_CLASS_KEY = ProductClass.DEFAULT_CLASS_KEY;

    /**
     * Gets the TradingPropertyGroup for a specific sessionName, classKey and tradingPropertyName.
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
        TradingPropertyGroup newTradingPropertyGroup = getTradingPropertyGroup(sessionName, classKey,
                                                                               tradingPropertyName, true);
        return newTradingPropertyGroup;
    }

    /**
     * Provides for the creation of a new trading property group that did not exist before.
     * @param sessionName to build group for
     * @param classKey to build group for
     * @param tradingPropertyName to build group for
     * @return an appropriate implementation of a new instance of a TradingPropertyGroup based on the values passed.
     * The intention would be that this TradingPropertyGroup was not obtained from the host source.
     * @throws DataValidationException should be thrown if tradingPropertyName is not a known type.
     * @throws InvocationTargetException If any exception occurs from instantiating the appropriate domain wrapper
     *                                   object, it is returned as the cause in this exception.
     */
    public TradingPropertyGroup createNewTradingPropertyGroup(String sessionName, int classKey,
                                                              String tradingPropertyName)
            throws DataValidationException, InvocationTargetException
    {
        TradingPropertyGroup newTradingPropertyGroup;

        TradingPropertyClassType classTypeWrapper = findTradingPropertyClassType(tradingPropertyName);

        //we have a known property name and implementation type to try to instantiate
        Integer classKeyInteger = new Integer(classKey);
        newTradingPropertyGroup = createNewTradingPropertyGroup(classTypeWrapper, sessionName,
                                                                classKeyInteger);
        return newTradingPropertyGroup;
    }

    /**
     * Gets all the TradingPropertyGroup's for a specific sessionName and tradingPropertyName, for all classes.
     * @param sessionName to get groups for
     * @param tradingPropertyName of specific groups to get
     * @return an array of the appropriate implementation instance's of a TradingPropertyGroup based on the values
     * passed. They will be all of the same type.
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
        ArrayList<TradingPropertyGroup> newTPGList = new ArrayList<TradingPropertyGroup>();

        TradingPropertyClassType classTypeWrapper = findTradingPropertyClassType(tradingPropertyName);

        //we have a known property name and implementation type to try to instantiate
        TradingPropertyType tradingPropertyType = classTypeWrapper.getTradingPropertyType();

        String propertyKey = buildTradingPropertyKey(sessionName, tradingPropertyName);

        //try to get group from Trading Properties through PropertyService
        PropertyServicePropertyGroup[] groups =
                PropertyServiceFacadeHome.find().getPropertyGroupsForPartialKey(tradingPropertyType.getPropertyCategory(),
                                                                                propertyKey,
                                                                                PropertyQueryTypes.BEGINS_WITH);
        if(groups != null && groups.length > 0)
        {
            newTPGList.ensureCapacity(groups.length);

            for(int i = 0; i < groups.length; i++)
            {
                PropertyServicePropertyGroup group = groups[i];
                TradingPropertyGroup newTradingPropertyGroup;

                Integer classKeyInteger =
                        new Integer(AbstractTradingPropertyGroup.getClassKeyFromGroupName(group));

                newTradingPropertyGroup = createNewTradingPropertyGroup(classTypeWrapper, sessionName,
                                                                        classKeyInteger);
                newTradingPropertyGroup.setPropertyGroup(group);

                newTPGList.add(newTradingPropertyGroup);
            }
        }
        else
        {
            StringBuffer msg = new StringBuffer(100);
            msg.append("PropertyServicePropertyGroup's received from PropertyServiceFacadeHome was null or empty. ");
            msg.append("Category:").append(tradingPropertyType.getPropertyCategory()).append(" SessionName:");
            msg.append(sessionName);
            msg.append(" tradingPropertyName:").append(tradingPropertyName);

            Log.alarm(msg.toString());
        }

        TradingPropertyGroup[] newTradingPropertyGroups = new TradingPropertyGroup[newTPGList.size()];
        newTradingPropertyGroups = newTPGList.toArray(newTradingPropertyGroups);

        return newTradingPropertyGroups;
    }

    /**
     * Gets all the TradingPropertyGroup's for a specific sessionName and classKey.
     * @param sessionName to get groups for
     * @param classKey to get groups for
     * @return an array of the appropriate implementation instances of TradingPropertyGroup's based on the values
     * passed.
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
        TradingPropertyClassType[] allTypes = getAllHandledClassTypes();
        List<TradingPropertyGroup> groupsList = new ArrayList<TradingPropertyGroup>(allTypes.length);
        TradingPropertyGroup newGroup;
        for(int i = 0; i < allTypes.length; i++)
        {
            TradingPropertyClassType tpClassType = allTypes[i];

            String propertyName = tpClassType.getTradingPropertyType().getName();

            try
            {
                newGroup = getTradingPropertyGroup(sessionName, classKey, propertyName);
                groupsList.add(newGroup);
            }
            catch(NotFoundException e)
            {
                //normal exception, just won't add to collection
                if(Log.isDebugOn())
                {
                    StringBuffer msg = new StringBuffer(100);
                    msg.append("NotFoundException received for getTradingPropertyGroup(");
                    msg.append(sessionName).append(", ").append(classKey).append(", ").append(propertyName).append(')');
                    Log.debug(msg.toString());
                }
            }
        }
        return groupsList.toArray(new TradingPropertyGroup[groupsList.size()]);
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
    public TradingProperty[] getAllTradingProperties(String sessionName, int classKey, String tradingPropertyName)
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

        List<TradingProperty> tpList = new ArrayList<TradingProperty>(allGroups.length * 10);

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
        return tpList.toArray(new TradingProperty[tpList.size()]);
    }

    /**
     * Converts the integer of a property type to a String as defined by the TradingPropertyTypes
     * @param propertyType to convert
     * @return String representation
     */
    public String getPropertyName( int propertyType )
    {
        String propertyName = TradingPropertyType.PROPERTY_TYPE_NOT_DEFINED;

        for ( int i = 0; i < TradingPropertyTypeImpl.globalTradingPropertyTypes.length; i++ )
        {
            if ( TradingPropertyTypeImpl.globalTradingPropertyTypes[ i ].getType() == propertyType )
            {
                propertyName = TradingPropertyTypeImpl.globalTradingPropertyTypes[ i ].getName();
                break;
            }
        }

        return propertyName;
    }

    /**
     * Converts the String name of a property type to an integer as defined by the TradingPropertyTypes
     * @param propertyName to convert
     * @return int TradingPropertyType,  0 if not found.
     */
    public int getPropertyType( String propertyName )
    {
        for ( int i = 0; i < TradingPropertyTypeImpl.globalTradingPropertyTypes.length; i++ )
        {
            if ( TradingPropertyTypeImpl.globalTradingPropertyTypes[ i ].getName().compareTo( propertyName ) == 0 )
            {
                return TradingPropertyTypeImpl.globalTradingPropertyTypes[ i ].getType();
            }
        }

        return 0;
    }

    /**
     * Converts the String name of a property type to an integer as defined by the TradingPropertyTypes
     * @param allClassesPropertyName (in xxxAllClasses format) to convert
     * @return int representation
     */
    public int getPropertyTypeAllClassesName( String allClassesPropertyName )
    {
        for(int i = 0; i < TradingPropertyTypeImpl.globalTradingPropertyTypes.length; i++)
        {
            if(TradingPropertyTypeImpl.globalTradingPropertyTypes[i].getAllClassesName().compareTo(allClassesPropertyName) == 0)
            {
                return TradingPropertyTypeImpl.globalTradingPropertyTypes[i].getType();
            }
        }

        return 0;
    }

    /**
     * Will build the appropriate String key to use for the property service propertyKey, combined from the passed
     * arguments. This method is meant for class key based queries.
     * @param sessionName
     * @param classKey
     * @param tradingPropertyName
     */
    public String buildTradingPropertyKey(String sessionName, int classKey, String tradingPropertyName)
    {
        Integer classKeyInteger = new Integer(classKey);

        Object[] propertyKeyElements = {tradingPropertyName, sessionName, classKeyInteger};
        String propertyKey = BasicPropertyParser.buildCompoundString(propertyKeyElements);
        return propertyKey;
    }

    /**
     * Will build the appropriate String key to use for the property service propertyKey, combined from attributes
     * within the passed group. This method is meant for class key based queries.
     * @param group to obtain sessionName, classKey and tradingPropertyType from.
     */
    public String buildTradingPropertyKey(TradingPropertyGroup group)
    {
        if(group != null)
        {
            return buildTradingPropertyKey(group.getSessionName(), group.getClassKey(), group.getTradingPropertyType().getName());
        }
        else
        {
            return null;
        }
    }

    /**
     * Will build the appropriate String key to use for the property service propertyKey, combined from the passed
     * arguments. This method is meant for queries that are not class key based or for queries for all classes.
     * @param sessionName
     * @param tradingPropertyName
     */
    public String buildTradingPropertyKey(String sessionName, String tradingPropertyName)
    {
        Object[] propertyKeyElements = {tradingPropertyName, sessionName};
        String propertyKey = BasicPropertyParser.buildCompoundString(propertyKeyElements);

        return propertyKey;
    }

    /**
     * Will find the tradingPropertyName parsed from the passed fully qualified tradingPropertyKey.
     * @param tradingPropertyKey to parse
     * @return tradingPropertyName that the passed tradingPropertyKey represents
     */
    public String getTradingPropertyKeyPropertyName( String tradingPropertyKey )
    {
        return getPropertyItem( tradingPropertyKey, 0 );
    }

    /**
     * Will find the sessionName parsed from the passed fully qualified tradingPropertyKey.
     * @param tradingPropertyKey to parse
     * @return sessionName that the passed tradingPropertyKey represents
     */
    public String getTradingPropertyKeySessionName( String tradingPropertyKey )
    {
        return getPropertyItem( tradingPropertyKey, 1 );
    }

    /**
     * Will find the classKey parsed from the passed fully qualified tradingPropertyKey.
     * @param tradingPropertyKey to parse
     * @return classKey that the passed tradingPropertyKey represents
     */
    public int getTradingPropertyKeyClassKey( String tradingPropertyKey )
    {
        String returnClassKey = getPropertyItem( tradingPropertyKey, 2 );
        if ( returnClassKey.length() == 0 )
        {
            return 0;
        }
        else
        {
            return Integer.parseInt( returnClassKey );
        }
    }

    /**
     * Will determine the number of elements in the passed fully qualified tradingPropertyKey.
     * @param tradingPropertyKey to parse for the number of elements
     * @return number of parsed elements
     */
    public int getTradingPropertyKeyParsedLen( String tradingPropertyKey )
    {
        String[] parsedKeys = BasicPropertyParser.parseArray( tradingPropertyKey );
        return parsedKeys.length;
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
        String compoundKey = buildTradingPropertyKey(sessionName, classKey, tradingPropertyName);
        PropertyServiceFacadeHome.find().subscribe(PropertyCategoryTypes.TRADING_PROPERTIES, compoundKey, listener);
    }

    /**
     * Unsubscribes a listener to events for the TradingPropertyGroup identified.
     * @param sessionName of TradingPropertyGroup to unsubscribe to
     * @param classKey of TradingPropertyGroup to unsubscribe to
     * @param tradingPropertyName of TradingPropertyGroup to unsubscribe to
     * @param listener to unsubscribe
     */
    public void unsubscribe(String sessionName, int classKey, String tradingPropertyName,
                            EventChannelListener listener)
            throws SystemException, DataValidationException, CommunicationException, AuthorizationException
    {
        String compoundKey = buildTradingPropertyKey(sessionName, classKey, tradingPropertyName);
        PropertyServiceFacadeHome.find().unsubscribe(PropertyCategoryTypes.TRADING_PROPERTIES, compoundKey, listener);
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

        TradingPropertyClassType classTypeWrapper = findTradingPropertyClassType(tradingPropertyName);

        //we have a known property name and implementation type to try to instantiate
        Integer classKeyInteger = new Integer(classKey);
        String propertyKey = buildTradingPropertyKey(sessionName, classKey, tradingPropertyName);

        TradingPropertyType tradingPropertyType = classTypeWrapper.getTradingPropertyType();

        //try to get group from Trading Properties through PropertyService
        PropertyServicePropertyGroup group;
        try
        {
            //look for specific class key requested
            group = PropertyServiceFacadeHome.find().getPropertyGroup(tradingPropertyType.getPropertyCategory(),
                                                                      propertyKey);
        }
        catch(NotFoundException e)
        {
            if(withDefault && classKey != DEFAULT_CLASS_KEY)
            {
                classKeyInteger = new Integer(DEFAULT_CLASS_KEY);
                propertyKey = buildTradingPropertyKey(sessionName, DEFAULT_CLASS_KEY, tradingPropertyName);

                group = PropertyServiceFacadeHome.find().getPropertyGroup(tradingPropertyType.getPropertyCategory(),
                                                                          propertyKey);
            }
            else
            {
                throw e;
            }
        }

        if(group != null)
        {
            //got a group and did not throw out any exceptions
            newTradingPropertyGroup = createNewTradingPropertyGroup(classTypeWrapper, sessionName,
                                                                    classKeyInteger);
            newTradingPropertyGroup.setPropertyGroup(group);
        }
        else
        {
            StringBuffer msg = new StringBuffer(100);
            msg.append(getClass().getName());
            msg.append(":PropertyServicePropertyGroup received from PropertyServiceFacadeHome was null:");
            msg.append("category=").append(tradingPropertyType.getPropertyCategory());
            msg.append("; sessionName=").append(sessionName);
            msg.append("; classKey=").append(classKey);
            msg.append("; tradingPropertyName=").append(tradingPropertyName);
            msg.append("; withDefault=").append(withDefault);

            Log.alarm(msg.toString());

            throw ExceptionBuilder.notFoundException(msg.toString(), NotFoundCodes.RESOURCE_DOESNT_EXIST);
        }

        return newTradingPropertyGroup;
    }

    /**
     * Attempts to find a previously created TradingPropertyClassType keyed by the passed tradingPropertyName.
     * @param tradingPropertyName to find TradingPropertyClassType for
     * @return found TradingPropertyClassType for passed tradingPropertyName
     * @throws DataValidationException will be thrown if TradingPropertyClassType could not be found for passed
     * tradingPropertyName
     */
    protected TradingPropertyClassType findTradingPropertyClassType(String tradingPropertyName)
            throws DataValidationException
    {
        TradingPropertyClassType classTypeWrapper =
                PROPERTY_NAME_CLASS_TYPE_MAP.get(tradingPropertyName);
        if(classTypeWrapper != null)
        {
            return classTypeWrapper;
        }
        else
        {
            Log.alarm("Unknown tradingPropertyName:" + tradingPropertyName + ". Could not find class type to handle.");
            throw ExceptionBuilder.dataValidationException("Unknown tradingPropertyName:" + tradingPropertyName +
                                                           ". Could not find class type to handle.", 0);
        }
    }

    /**
     * Returns an array of all known TradingPropertyClassType's.
     */
    private TradingPropertyClassType[] getAllHandledClassTypes()
    {
        Collection<TradingPropertyClassType> values = PROPERTY_NAME_CLASS_TYPE_MAP.values();
        return values.toArray(new TradingPropertyClassType[values.size()]);
    }

    /**
     * Returns a specific element parsed from the passed tradingPropertyKey
     * @param tradingPropertyKey to parse and find the specific element for
     * @param offset to specific element parsed from tradingPropertyKey, zero-based.
     * @return specific element, or empty String if offset could not be found.
     */
    private String getPropertyItem( String tradingPropertyKey, int offset )
    {
        String[] parsedKeys = BasicPropertyParser.parseArray( tradingPropertyKey );
        if( parsedKeys.length >= offset )
        {
            return parsedKeys[ offset ];
        }
        else
        {
            return "";
        }
    }

    /**
     * Creates a new TradingPropertyGroup representing the passed attributes. If the new TradingPropertyGroup is one
     * of the known three Simple** types, then it will be further initialized so that it knows which
     * TradingPropertyType that it represents.
     * @param classTypeWrapper to find the representative implementation of TradingPropertyGroup for, for
     * instantiation.
     * @param sessionName for new TradingPropertyGroup to represent
     * @param classKeyInteger for new TradingPropertyGroup to represent
     * @return newly instantiated implementation of a TradingPropertyGroup, who's real class implements the type
     * as represented in the passed classTypeWrapper
     * @throws InvocationTargetException will be thrown if the implementation could not be instantiated.
     */
    private TradingPropertyGroup createNewTradingPropertyGroup(TradingPropertyClassType classTypeWrapper,
                                                               String sessionName, Integer classKeyInteger)
            throws InvocationTargetException
    {
        TradingPropertyGroup newTradingPropertyGroup;
        try
        {
            Constructor constructor = classTypeWrapper.getConstructor();
            if(constructor != null)
            {
                //we have a known constructor that accepts (String, int), try to instantiate using
                //this constructor for session name and class key
                Object[] parms = {sessionName, classKeyInteger};
                newTradingPropertyGroup = (TradingPropertyGroup) constructor.newInstance(parms);
            }
            else
            {
                //we never found a constructor that takes (String, int), just try to instantiate using
                //default constructor. WARNING!!! DOMAIN OBJECT WILL NOT KNOW WHICH SESSION NAME
                //AND CLASS KEY IT IS FOR, SINCE THESE ARE IMMUTABLE
                Class classToCreate = classTypeWrapper.getClassType();
                newTradingPropertyGroup = (TradingPropertyGroup) classToCreate.newInstance();

                StringBuffer msg = new StringBuffer(100);
                msg.append("Created a new TradingPropertyGroup domain object by using the default ");
                msg.append("constructor. This TradingPropertyGroup will NOT know its Trading Session ");
                msg.append("Name or Class Key. It did not expose a constructor that accepted ");
                msg.append("(String, int). The class name was:" + classToCreate.getName());

                Log.alarm(msg.toString());
            }

            if(newTradingPropertyGroup instanceof SimpleBooleanTradingPropertyGroup)
            {
                SimpleBooleanTradingPropertyGroup castedTPG =
                        (SimpleBooleanTradingPropertyGroup) newTradingPropertyGroup;
                castedTPG.setTradingPropertyType(classTypeWrapper.getTradingPropertyType());
            }
            else if(newTradingPropertyGroup instanceof SimpleDoubleTradingPropertyGroup)
            {
                SimpleDoubleTradingPropertyGroup castedTPG =
                        (SimpleDoubleTradingPropertyGroup) newTradingPropertyGroup;
                castedTPG.setTradingPropertyType(classTypeWrapper.getTradingPropertyType());
            }
            else if(newTradingPropertyGroup instanceof SimpleIntegerTradingPropertyGroup)
            {
                SimpleIntegerTradingPropertyGroup castedTPG =
                        (SimpleIntegerTradingPropertyGroup) newTradingPropertyGroup;
                castedTPG.setTradingPropertyType(classTypeWrapper.getTradingPropertyType());
            }
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
}
