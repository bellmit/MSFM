package com.cboe.internalPresentation.common.formatters;

import com.cboe.idl.constants.AllocationTradeTypes;

//
// -----------------------------------------------------------------------------------
// Source file: AllocationTradeTypeCodes
//
// PACKAGE: com.cboe.internalPresentation.common.formatters
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

public class AllocationTradeTypeCodes
{
    //CONSTANTS----------------------------------------------------------------

    public static final short REGULAR = AllocationTradeTypes.REGULAR;
    public static final short OPENING = AllocationTradeTypes.OPENING;
    public static final short QUOTE_TRIGGER = AllocationTradeTypes.QUOTE_TRIGGER;
    public static final short QUOTE_LOCK_MIN_TRADE = AllocationTradeTypes.QUOTE_LOCK_MIN_TRADE;
    public static final short AUCTION_STRATEGY_TRADE = AllocationTradeTypes.STRATEGY_AUCTION_TRADE;
    public static final short AUCTION_DEFAULT_TRADE = AllocationTradeTypes.INTERNALIZATION_NO_AUCTION_TRADE;
    public static final short INTERNALIZATION_TRADE = AllocationTradeTypes.INTERNALIZATION_AUCTION_TRADE;

    public static final String REGULAR_STRING = "Regular";
    public static final String OPENING_STRING = "Opening";
    public static final String QUOTE_TRIGGER_STRING = "Quote Trigger";
    public static final String QUOTE_LOCK_MIN_TRADE_STRING = "Quote Lock Minimum Trade";
    public static final String AUCTION_STRATEGY_TRADE_STRING = "Auction Strategy Trade";
    public static final String AUCTION_DEFAULT_TRADE_STRING = "Auction Default Trade";
    public static final String INTERNALIZATION_TRADE_STRING = "Internalization Trade";

    //CLASS VARIABLES----------------------------------------------------------

    private static final short[] strats = {REGULAR, OPENING, QUOTE_TRIGGER, QUOTE_LOCK_MIN_TRADE, AUCTION_STRATEGY_TRADE,
                                           AUCTION_DEFAULT_TRADE, INTERNALIZATION_TRADE} ;

    //PUBLIC METHODS-----------------------------------------------------------

    /**
     * Returns a string representation of the AllocationTradeType codes.
     * @param code to render (see defined constants)
     * @return a string representation of the code
     * @see com.cboe.idl.constants.AllocationTradeTypes
     */
    public static String toString(short code)
    {
        String str = null;

        switch(code)
        {
            case REGULAR:
                str = REGULAR_STRING;
                break;

            case OPENING:
                str = OPENING_STRING;
                break;

            case QUOTE_TRIGGER:
                str = QUOTE_TRIGGER_STRING;
                break;

            case QUOTE_LOCK_MIN_TRADE:
                str = QUOTE_LOCK_MIN_TRADE_STRING;
                break;

            case AUCTION_STRATEGY_TRADE:
                str = AUCTION_STRATEGY_TRADE_STRING;
                break;

            case AUCTION_DEFAULT_TRADE:
                str = AUCTION_DEFAULT_TRADE_STRING;
                break;

            case INTERNALIZATION_TRADE:
                str = INTERNALIZATION_TRADE_STRING;
                break;

            default:
                str = "<undefined>";
                break;
        }

        return str;
    }

    /**
     * Returns an array of all AllocationTradeTypes
     * @return
     */
    public static short[] getAll()
    {
        return strats;
    }

} // -- end of class AllocationTradeTypeCodes
