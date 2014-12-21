package com.cboe.domain.routingProperty;
// -----------------------------------------------------------------------------------
// Source file: FirmPropertyTypeImpl
//
// PACKAGE: com.cboe.domain.routingProperty
// 
// Created: 
// -----------------------------------------------------------------------------------
// Copyright (c) 2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import com.cboe.interfaces.domain.property.PropertyCategoryTypes;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKeyType;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;

public class FirmPropertyTypeImpl extends BasePropertyTypeImpl 
{
    public static final BasePropertyType FIRM_TRADING_PARAM =
            new FirmPropertyTypeImpl("FirmTradingParameter", "Firm Trading Parameter",
                                     BasePropertyKeyTypeImpl.FIRM_TRADING_PARAM_KEY,
                                     FirmPropertyMaskHelper.FIRM_TRADING_PARAM_MASK);
    
    public static final BasePropertyType PRICE_ADJUSTMENTS_CANCEL_PREFERENCE =
        new FirmPropertyTypeImpl("PriceAdjustmentsCancelPreference", "Price Adjustments Cancel Preference",
                                    BasePropertyKeyTypeImpl.SESSION_FIRM_KEY,
                                    FirmPropertyMaskHelper.PRICE_ADJUSTMENTS_CANCEL_PREFERENCE_MASK);
    
    public static final BasePropertyType FIRM_ETN_PARAM =
        new FirmPropertyTypeImpl("FirmEtnParameter", "Firm Electronic Trade Notification Parameter",
                                 BasePropertyKeyTypeImpl.FIRM_MARKET_MAKER_KEY,
                                 FirmPropertyMaskHelper.FIRM_ETN_PARAM_MASK);

    public static final BasePropertyType DROP_COPY_PREFERENCE =
        new FirmPropertyTypeImpl("DropCopyPreference", "Drop Copy Preference",
                                    BasePropertyKeyTypeImpl.SESSION_FIRM_KEY,
                                    FirmPropertyMaskHelper.DROP_COPY_PREFERENCE_MASK);
    
    public static final BasePropertyType MMTN_PREFERENCE =
        new FirmPropertyTypeImpl("MMTNPreference", "Firm Market Maker Trade Notification Parameter",
                                    BasePropertyKeyTypeImpl.FIRM_MARKET_MAKER_KEY,
                                    FirmPropertyMaskHelper.MMTN_PREFERENCE_MASK);
    
    public static final BasePropertyType PAR_CLEARING_PREFERENCE =
        new FirmPropertyTypeImpl("ParClearingPreference", "Market Maker Trade Report Generation",
                                    BasePropertyKeyTypeImpl.FIRM_MARKET_MAKER_KEY,
                                    FirmPropertyMaskHelper.PAR_CLEARING_PREFERENCE_MASK);
    
    public static final BasePropertyType MMTN_MAPPING =
        new FirmPropertyTypeImpl("FirmMMTNMapping", "Firm Mapping for Floor Trades",
                                    BasePropertyKeyTypeImpl.SESSION_FIRM_CLASS_ACRONYM_KEY,
                                    FirmPropertyMaskHelper.MMTN_MAP_PREFERENCE_MASK);
    
    public static final BasePropertyType MMTN_MAPPING_POST_STATION =
        new FirmPropertyTypeImpl("FirmMMTNPostStationMapping", "Firm Mapping for Floor Trades Post Station",
                                    BasePropertyKeyTypeImpl.SESSION_FIRM_POST_STATION_ACRONYM_KEY,
                                    FirmPropertyMaskHelper.MMTN_POST_STATION_MAP_PREFERENCE_MASK);
    
    /**
     * sets the new param for Auction Firm Info. 
     * @author Cognizant Technology Solutions.
     */
    public static final BasePropertyType AUCTION_FIRM_INFO_PARAM =
        new FirmPropertyTypeImpl("AuctionFirmInfoParams", "Auction Firm Info Param",
                                    BasePropertyKeyTypeImpl.SESSION_FIRM_KEY,
                                    FirmPropertyMaskHelper.AUCTION_FIRM_INFO_MASK);
    
    public static final BasePropertyType DIRECTED_AIM_NOTIFICATION_FIRM_INFO_PARAM =
        new FirmPropertyTypeImpl("DirectedAIMNotificationFirmInfo", "DirectedAIM Notification Firm Info",
                                    BasePropertyKeyTypeImpl.SESSION_FIRM_KEY,
                                    FirmPropertyMaskHelper.DIRECTED_AIM_NOTIFICATION_FIRM_INFO_MASK);
    
    
    public static final BasePropertyType SHORT_SALE_MARKING =
        new FirmPropertyTypeImpl("ShortSaleMarking", "Short Sale Marking",
                                    BasePropertyKeyTypeImpl.SESSION_FIRM_KEY,
                                    FirmPropertyMaskHelper.SHORT_SALE_MARKING_MASK);
    
    public static final BasePropertyType ENABLE_NEW_BOB =
        new FirmPropertyTypeImpl("EnableNewBOB", "Enable New BOB",
                                    BasePropertyKeyTypeImpl.SESSION_DEFAULT_FIRM_CLASS_KEY,
                                    FirmPropertyMaskHelper.ENALBE_NEW_BOB_MASK);    
    
    
    /**
     * Add the new param added to the list.
     * Modified by Cognizant Technology Solutions 
     */
    public static final BasePropertyType[] allFirmProperties =
    {
        FIRM_TRADING_PARAM,
        PRICE_ADJUSTMENTS_CANCEL_PREFERENCE,
        FIRM_ETN_PARAM,
        DROP_COPY_PREFERENCE,
        MMTN_PREFERENCE,
        MMTN_MAPPING,
        MMTN_MAPPING_POST_STATION,
        PAR_CLEARING_PREFERENCE,
        AUCTION_FIRM_INFO_PARAM,
        DIRECTED_AIM_NOTIFICATION_FIRM_INFO_PARAM,
        SHORT_SALE_MARKING,
        ENABLE_NEW_BOB
    };

    static
    {
        for(BasePropertyType propertyType : allFirmProperties)
        {
            allPropertyTypes.put(propertyType.getName(), propertyType);
        }
    }

    /**
     * Constructor that uses PropertyCategoryTypes.TRADING_PROPERTIES as the set propertyCategory.
     * @param name programmatic name
     * @param fullName full english name for display trading session
     */
    protected FirmPropertyTypeImpl(String name, String fullName, BasePropertyKeyType keyType, int[][] masks)
    {
        super(PropertyCategoryTypes.FIRM_PROPERTIES, name, fullName, keyType, masks);
    }

}
