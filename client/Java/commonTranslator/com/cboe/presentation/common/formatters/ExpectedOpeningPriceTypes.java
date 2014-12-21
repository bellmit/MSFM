/********************************************************************************
 * FILE:    ContingencyTypes.java
 *
 * PACKAGE: com.cboe.presentation.common.types.constants
 *
 * ----------------------------------------------------------------------
 * Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
 *
 *******************************************************************************/

package com.cboe.presentation.common.formatters;

// CBOE imports
// Inlined to avoid name conflict: com.cboe.idl.cmiConstants.ExpectedOpeningPriceTypes;


/********************************************************************************
 * Represents Contingency Types
 *
 * @see com.cboe.idl.cmiConstants.ExpectedOpeningPriceTypes
 */
public class ExpectedOpeningPriceTypes
{

//*** Public Attributes

    // Contingency Types (mapping to com.cboe.idl.cmiConstants.ExpectedOpeningPriceTypes)
    public static final short OPENING_PRICE            = com.cboe.idl.cmiConstants.ExpectedOpeningPriceTypes.OPENING_PRICE;

    // Next two constants are depricated, i.e. MORE_BUYERS & MORE_SELLERS:
    public static final short MORE_BUYERS              = com.cboe.idl.cmiConstants.ExpectedOpeningPriceTypes.MORE_BUYERS;
    public static final short MORE_SELLERS             = com.cboe.idl.cmiConstants.ExpectedOpeningPriceTypes.MORE_SELLERS;

    public static final short NO_OPENING_TRADE         = com.cboe.idl.cmiConstants.ExpectedOpeningPriceTypes.NO_OPENING_TRADE;
    public static final short MULTIPLE_OPENING_PRICES  = com.cboe.idl.cmiConstants.ExpectedOpeningPriceTypes.MULTIPLE_OPENING_PRICES;
    public static final short NEED_QUOTE_TO_OPEN       = com.cboe.idl.cmiConstants.ExpectedOpeningPriceTypes.NEED_QUOTE_TO_OPEN;
    public static final short PRICE_NOT_IN_QUOTE_RANGE = com.cboe.idl.cmiConstants.ExpectedOpeningPriceTypes.PRICE_NOT_IN_QUOTE_RANGE;
    public static final short NEED_DPM_QUOTE_TO_OPEN   = com.cboe.idl.cmiConstants.ExpectedOpeningPriceTypes.NEED_DPM_QUOTE_TO_OPEN;

    // Hybrid Opening Enhancements (HOpE) added:
    public static final short DPM_QUOTE_INVALID        = com.cboe.idl.cmiConstants.ExpectedOpeningPriceTypes.DPM_QUOTE_INVALID;
    public static final short PRICE_NOT_IN_BOTR_RANGE  = com.cboe.idl.cmiConstants.ExpectedOpeningPriceTypes.PRICE_NOT_IN_BOTR_RANGE;
    public static final short NEED_MORE_BUYERS         = com.cboe.idl.cmiConstants.ExpectedOpeningPriceTypes.NEED_MORE_BUYERS;
    public static final short NEED_MORE_SELLERS        = com.cboe.idl.cmiConstants.ExpectedOpeningPriceTypes.NEED_MORE_SELLERS;

    // Format constants
    public static final String TRADERS_FORMAT = "TRADERS_FORMAT";


//*** Private Attributes

    public static final String OPENING_PRICE_STRING            ="OPENING PRICE";
    public static final String MORE_BUYERS_STRING              ="MORE BUYERS";
    public static final String MORE_SELLERS_STRING             ="MORE SELLERS";
    public static final String NO_OPENING_TRADE_STRING         ="NO OPENING TRADE";
    public static final String MULTIPLE_OPENING_PRICES_STRING  ="MULTIPLE OPENING PRICES";
    public static final String NEED_QUOTE_TO_OPEN_STRING       ="NEED QUOTE TO OPEN";
    public static final String PRICE_NOT_IN_QUOTE_RANGE_STRING ="PRICE NOT IN QUOTE RANGE";
    public static final String NEED_DPM_QUOTE_TO_OPEN_STRING   ="NEED DPM QUOTE TO OPEN";
    public static final String DPM_QUOTE_INVALID_STRING        ="DPM QUOTE INVALID";
    public static final String PRICE_NOT_IN_BOTR_RANGE_STRING  ="PRICE NOT IN BOTR RANGE";
    public static final String NEED_MORE_BUYERS_STRING         ="NEED BUYERS";
    public static final String NEED_MORE_SELLERS_STRING        ="NEED SELLERS";

    private static final String INVALID_FORMAT                 ="ERROR: Invalid Format Specifier";
    private static final String INVALID_EOP_TYPE               ="ERROR: Invalid EOP Type Code";
    private static final String NONE_STRING                    ="none";


//*** Public Methods

    /*****************************************************************************
     * Returns a string representation of the object in TRADERS_FORMAT format
     *
     * @param eopType - the ExpecterOpeningPriceType code to render (see defined constants)
     * @return a string representation of the ContingencyType
     * @see com.cboe.idl.cmiConstants.ContingencyTypes
     */
    public static String toString( short eopType )
    {
        return toString( eopType, TRADERS_FORMAT );
    }


    /*****************************************************************************
     * Returns a string representation of the object in the given format
     *
     * @param eopType - the ExpectedOpeningPriceType code to render (see defined constants)
     * @param formatSpecifier - a string that specifies how the object should
     *                          format itself.
     * @return a string representation of the ExpectedOpeningPriceType
     * @see com.cboe.idl.cmiConstants.ExpectedOpeningPriceTypes
     */
    public static String toString( short eopType, String formatSpecifier )
    {
        if( formatSpecifier.equals( TRADERS_FORMAT ))
        {
            switch( eopType )
            {
                case OPENING_PRICE:
                    return OPENING_PRICE_STRING;
                //case MORE_BUYERS:                                   // deprecated, == NEED_MORE_SELLERS
                //    return MORE_BUYERS_STRING;
                //case MORE_SELLERS:                                  // deprecated, == NEED_MORE_BUYERS
                //    return MORE_SELLERS_STRING;
                case NO_OPENING_TRADE:
                    return NO_OPENING_TRADE_STRING;
                case MULTIPLE_OPENING_PRICES:
                    return MULTIPLE_OPENING_PRICES_STRING;
                case NEED_QUOTE_TO_OPEN:
                    return NEED_QUOTE_TO_OPEN_STRING;
                case PRICE_NOT_IN_QUOTE_RANGE:
                    return PRICE_NOT_IN_QUOTE_RANGE_STRING;
                case NEED_DPM_QUOTE_TO_OPEN:
                    return NEED_DPM_QUOTE_TO_OPEN_STRING;
                case DPM_QUOTE_INVALID:
                    return DPM_QUOTE_INVALID_STRING;
                case PRICE_NOT_IN_BOTR_RANGE:
                    return PRICE_NOT_IN_BOTR_RANGE_STRING;
                case NEED_MORE_BUYERS:
                    return NEED_MORE_BUYERS_STRING;
                case NEED_MORE_SELLERS:
                    return NEED_MORE_SELLERS_STRING;
                default:
                    return new StringBuffer(20).append(INVALID_EOP_TYPE).append("[ ").append(eopType).append(" ]").toString();
            }
        }
        return INVALID_FORMAT;
    }

    public static String getDefault()
    {
        return NONE_STRING;
    }

//*** Private Methods

    /*****************************************************************************
     * Hide the default constructor from the public interface
     */
    private ExpectedOpeningPriceTypes( )
    {
    }

}
