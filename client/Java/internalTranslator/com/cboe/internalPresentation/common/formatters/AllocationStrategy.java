//
// -----------------------------------------------------------------------------------
// Source file: AllocationStrategy
//
// PACKAGE: com.cboe.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

package com.cboe.internalPresentation.common.formatters;

import com.cboe.idl.constants.AllocationStrategyCodes;

/**
 * Formatter for AllocationStrategyStruct strategy codes.
 */
public class AllocationStrategy
{
    //CONSTANTS----------------------------------------------------------------

    public static final short CUSTOMER = AllocationStrategyCodes.CUSTOMER;
    public static final short PRO_RATA = AllocationStrategyCodes.PRO_RATA;
    public static final short DPM_FIXED_PCT = AllocationStrategyCodes.DPM_FIXED_PCT;
    public static final short DPM_SCALED_PCT = AllocationStrategyCodes.DPM_SCALED_PCT;
    public static final short DPM_VAR_PCT = AllocationStrategyCodes.DPM_VAR_PCT;
    public static final short MARKET_TURNER = AllocationStrategyCodes.MARKET_TURNER;
    public static final short PRICE_TIME = AllocationStrategyCodes.PRICE_TIME;
    public static final short UMA_Q_NONQ = AllocationStrategyCodes.UMA_Q_NONQ;
    public static final short UMA_VAR_PCT = AllocationStrategyCodes.UMA_VAR_PCT;
    public static final short BEST_OF_DPM_UMA = AllocationStrategyCodes.BEST_OF_DPM_UMA;
    public static final short UMA_WITH_DPM = AllocationStrategyCodes.UMA_WITH_DPM;
    public static final short DPM_COMPLEX = AllocationStrategyCodes.DPM_COMPLEX;
    public static final short BEST_DPMCOMPLEX_UMA = AllocationStrategyCodes.BEST_DPMCOMPLEX_UMA;
    public static final short LOCK_MINIMUM_TRADE = AllocationStrategyCodes.LOCK_MINIMUM_TRADE;
    public static final short CAPPEDUMA = AllocationStrategyCodes.CAPPEDUMA;
    public static final short CAPPEDUMA_WITH_DPM = AllocationStrategyCodes.CAPPEDUMA_WITH_DPM;
    public static final short DPM_COMPLEX_REVISED = AllocationStrategyCodes.DPM_COMPLEX_REVISED;
    public static final short BEST_DPMCOMPLEXREVISED_CAPPEDUMAWITHDPM = AllocationStrategyCodes.BEST_DPMCOMPLEXREVISED_CAPPEDUMAWITHDPM;
    public static final short PREF_DPM_UMA = AllocationStrategyCodes.PREF_DPM_UMA;
    public static final short MANUAL_QUOTE = AllocationStrategyCodes.MANUAL_QUOTE;
    public static final short CUSTOMER_PRE_AUCTION = AllocationStrategyCodes.CUSTOMER_PRE_AUCTION;
    public static final short MANUAL_QUOTE_PRE_AUCTION = AllocationStrategyCodes.MANUAL_QUOTE_PRE_AUCTION;
    
    public static final String CUSTOMER_STRING = "Customer";
    public static final String PRO_RATA_STRING = "Pro Rata";
    public static final String DPM_FIXED_PCT_STRING = "DPM Fixed Percentage";
    public static final String DPM_SCALED_PCT_STRING = "DPM Scaled Percentage";
    public static final String DPM_VAR_PCT_STRING = "DPM Variable Percentage";
    public static final String MARKET_TURNER_STRING = "Market Turner";
    public static final String PRICE_TIME_STRING = "Price/Time";
    public static final String UMA_Q_NONQ_STRING = "UMA Quote/NonQuote";
    public static final String UMA_VAR_PCT_STRING = "UMA Variable Percentage";
    public static final String BEST_OF_DPM_UMA_STRING = "Best oF DPM UMA";
    public static final String UMA_WITH_DPM_STRING = "UMA with DPM";
    public static final String DPM_COMPLEX_STRING = "DPM Complex";
    public static final String BEST_DPMCOMPLEX_UMA_STRING = "Best DPM Complex UMA";
    public static final String LOCK_MINIMUM_TRADE_STRING = "Quote Lock Minimum Trade Quantity";
    public static final String CAPPEDUMA_STRING = "Capped UMA";
    public static final String CAPPEDUMA_WITH_DPM_STRING = "Capped UMA with DPM";
    public static final String DPM_COMPLEX_REVISED_STRING = "DPM Complex Revised";
    public static final String BEST_DPMCOMPLEXREVISED_CAPPEDUMAWITHDPM_STRING = "Best DPM Complex Revised Capped UMA";
    public static final String PREF_DPM_UMA_STRING = "Preferred DPM UMA";
    public static final String MANUAL_QUOTE_STRING = "Manual Quote";
    public static final String CUSTOMER_PRE_AUCTION_STRING = "Customer Pre Auction";
    public static final String MANUAL_QUOTE_PRE_AUCTION_STRING = "Manual Quote Pre Auction";
    

    //CLASS VARIABLES----------------------------------------------------------

    public static short[] strats;

    //PUBLIC METHODS-----------------------------------------------------------

    /**
     * Returns a string representation of the AllocationStrategy code.
     * @param code to render (see defined constants)
     * @return a string representation of the code
     * @see com.cboe.idl.constants.AllocationStrategyCodes
     */
    public static String toString(short code)
    {
        String str = null;

        switch(code)
        {
            case CUSTOMER:
                str = CUSTOMER_STRING;
                break;

            case PRO_RATA:
                str = PRO_RATA_STRING;
                break;

            case DPM_FIXED_PCT:
                str = DPM_FIXED_PCT_STRING;
                break;

            case DPM_SCALED_PCT:
                str = DPM_SCALED_PCT_STRING;
                break;

            case DPM_VAR_PCT:
                str = DPM_VAR_PCT_STRING;
                break;

            case MARKET_TURNER:
                str = MARKET_TURNER_STRING;
                break;

            case PRICE_TIME:
                str = PRICE_TIME_STRING;
                break;

            case UMA_Q_NONQ:
                str = UMA_Q_NONQ_STRING;
                break;

            case UMA_VAR_PCT:
                str = UMA_VAR_PCT_STRING;
                break;

            case BEST_OF_DPM_UMA:
                str = BEST_OF_DPM_UMA_STRING;
                break;

            case UMA_WITH_DPM:
                str = UMA_WITH_DPM_STRING;
                break;

            case DPM_COMPLEX:
                str = DPM_COMPLEX_STRING;
                break;

            case BEST_DPMCOMPLEX_UMA:
                str = BEST_DPMCOMPLEX_UMA_STRING;
                break;

            case LOCK_MINIMUM_TRADE:
                str = LOCK_MINIMUM_TRADE_STRING;
                break;

            case CAPPEDUMA:
                str = CAPPEDUMA_STRING;
                break;

            case CAPPEDUMA_WITH_DPM:
                str = CAPPEDUMA_WITH_DPM_STRING;
                break;

            case DPM_COMPLEX_REVISED:
                str = DPM_COMPLEX_REVISED_STRING;
                break;

            case BEST_DPMCOMPLEXREVISED_CAPPEDUMAWITHDPM:
                str = BEST_DPMCOMPLEXREVISED_CAPPEDUMAWITHDPM_STRING;
                break;

            case PREF_DPM_UMA:
                str = PREF_DPM_UMA_STRING;
                break;
                
            case MANUAL_QUOTE:
            	str = MANUAL_QUOTE_STRING;
            	break;
            case CUSTOMER_PRE_AUCTION:
            	str = CUSTOMER_PRE_AUCTION_STRING;
            	break;
            case MANUAL_QUOTE_PRE_AUCTION:
            	str = MANUAL_QUOTE_PRE_AUCTION_STRING;
            	break;
            default:
                str = "<undefined>";
                break;
        }
        return str;
    }

    /**
     * Returns an array of all AllocationStrategies
     * @return
     */
    public static short[] getAll()
    {
        if(strats == null)
        {
            strats = new short[19];

            strats[0]  = CUSTOMER;
            strats[1]  = PRO_RATA;
            strats[2]  = DPM_FIXED_PCT;
            strats[3]  = DPM_SCALED_PCT;
            strats[4]  = DPM_VAR_PCT;
            strats[5]  = MARKET_TURNER;
            strats[6]  = PRICE_TIME;
            strats[7]  = UMA_Q_NONQ;
            strats[8]  = UMA_VAR_PCT;
            strats[9]  = BEST_OF_DPM_UMA;
            strats[10] = UMA_WITH_DPM;
            strats[11] = DPM_COMPLEX;
            strats[12] = BEST_DPMCOMPLEX_UMA;
            strats[13] = LOCK_MINIMUM_TRADE;
            strats[14] = CAPPEDUMA;
            strats[15] = CAPPEDUMA_WITH_DPM;
            strats[16] = DPM_COMPLEX_REVISED;
            strats[17] = BEST_DPMCOMPLEXREVISED_CAPPEDUMAWITHDPM;
            strats[18] = PREF_DPM_UMA;
            strats[19] = MANUAL_QUOTE;
            strats[20] = CUSTOMER_PRE_AUCTION;
            strats[21] = MANUAL_QUOTE_PRE_AUCTION;
            
        }

        return strats;
    }
}
