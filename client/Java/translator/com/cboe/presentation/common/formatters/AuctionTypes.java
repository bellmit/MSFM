// -----------------------------------------------------------------------------------
// Source file: AuctionTypes
//
// PACKAGE: com.cboe.presentation.common.formatters
//
// Created: Sep 13, 2004 10:09:33 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

public abstract class AuctionTypes
{
    public static final short INTERNALIZATION = com.cboe.idl.cmiConstants.AuctionTypes.AUCTION_INTERNALIZATION;
    public static final short REGULAR_SINGLE  = com.cboe.idl.cmiConstants.AuctionTypes.AUCTION_REGULAR_SINGLE;
    public static final short STRATEGY        = com.cboe.idl.cmiConstants.AuctionTypes.AUCTION_STRATEGY;
    public static final short HAL             = com.cboe.idl.cmiConstants.AuctionTypes.AUCTION_HAL;
    public static final short SAL             = com.cboe.idl.cmiConstants.AuctionTypes.AUCTION_SAL;
    public static final short STOCK_NBBO_FLASH= com.cboe.idl.cmiConstants.AuctionTypes.STOCK_NBBO_FLASH;
    public static final short UNSPECIFIED     = com.cboe.idl.cmiConstants.AuctionTypes.AUCTION_UNSPECIFIED;
    public static final short STOCK_ODD_LOT   = com.cboe.idl.cmiConstants.AuctionTypes.STOCK_ODD_LOT;
    public static final short DAIM            = com.cboe.idl.cmiConstants.AuctionTypes.AUCTION_DAIM;
    public static final short HALO   = com.cboe.domain.util.InternalAuctionTypes.AUCTION_HALO;
    public static final short NEW_HAL   = com.cboe.domain.util.InternalAuctionTypes.AUCTION_NEW_HAL;

    public static final String TRADERS_FORMAT = "TRADERS_FORMAT";
    public static final String BOOTH_FORMAT = "BOOTH_FORMAT";
    public static final String INVALID_FORMAT = "INVALID_FORMAT";

    private static final String INTERNALIZATION_STRING = "Internalization";
    private static final String REGULAR_SINGLE_STRING  = "Regular Single";
    private static final String STRATEGY_STRING        = "Strategy";
    private static final String STRATEGY_FLOOR_STRING  = "COA";
    private static final String HAL_STRING             = "HAL";
    private static final String SAL_STRING             = "SAL";
    private static final String STOCK_NBBO_FLASH_STRING= "Stock NBBO Flash";
    private static final String UNSPECIFIED_STRING     = "Unspecified";
    private static final String UNKNOWN_STRING         = "Unknown";
    private static final String STOCK_ODD_LOT_STRING   = "Stock Odd Lot";
    private static final String DAIM_STRING            = "Directed AIM";

    private static final String HALO_STRING   = "HALO";
    private static final String NEW_HAL_STRING   = "New HAL";

    public static final short[] ALL_TYPES = {INTERNALIZATION, REGULAR_SINGLE, STRATEGY, UNSPECIFIED, HAL, SAL, STOCK_NBBO_FLASH, STOCK_ODD_LOT,
                                                HALO, NEW_HAL} ;

    public static String toString(short auctionType)
    {
        return toString(auctionType, TRADERS_FORMAT);
    }

    public static String toString(short auctionType, String formatSpecifier)
    {
        if (formatSpecifier.equals(TRADERS_FORMAT))
        {
            switch (auctionType)
            {
                case INTERNALIZATION:
                    return INTERNALIZATION_STRING;
                case REGULAR_SINGLE:
                    return REGULAR_SINGLE_STRING;
                case STRATEGY:
                    return STRATEGY_STRING;
                case UNSPECIFIED:
                    return UNSPECIFIED_STRING;
                case HAL:
                    return HAL_STRING;
                case SAL:
                    return SAL_STRING;
                case STOCK_NBBO_FLASH:
                    return STOCK_NBBO_FLASH_STRING;
                case STOCK_ODD_LOT:
                    return STOCK_ODD_LOT_STRING;
                case DAIM:
                    return DAIM_STRING;
                case HALO:
                    return HALO_STRING;
                case NEW_HAL:
                    return NEW_HAL_STRING;
                default :
                    return new StringBuffer(12).append(UNKNOWN_STRING).append("[ ").append(auctionType).append(" ]").toString();
            }
        }
        else if (formatSpecifier.equals(BOOTH_FORMAT)){
            switch(auctionType){
                case STRATEGY:
                    return STRATEGY_FLOOR_STRING;
                default:
                    return toString(auctionType, TRADERS_FORMAT);
            }
        }

        return INVALID_FORMAT;
    }

    public static boolean validateAuctionType(short auctionType)
    {
        switch(auctionType)
        {
            case INTERNALIZATION:
            case REGULAR_SINGLE:
            case STRATEGY:
            case UNSPECIFIED:
            case HAL:
            case SAL:
            case STOCK_NBBO_FLASH:
            case STOCK_ODD_LOT:
            case DAIM:
            case HALO:
            case NEW_HAL:
                return true;
            default:
                return false;
        }
    }

}
