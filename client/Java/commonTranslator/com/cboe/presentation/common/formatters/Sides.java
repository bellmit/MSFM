/********************************************************************************
 * FILE:    Sides.java
 *
 * PACKAGE: com.cboe.presentation.common.types.constants
 *
 * ----------------------------------------------------------------------
 * Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
 *
 *******************************************************************************/

package com.cboe.presentation.common.formatters;

import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.domain.util.SidesSpecifier;

// CBOE imports
// Inlined to avoid name conflict: com.cboe.idl.cmiConstants.Sides;


/********************************************************************************
 * Represents Sides constants
 *
 * @see com.cboe.idl.cmiConstants.Sides
 */
public class Sides
{
    static private final String Category = "com.cboe.presentation.common.formatters.Sides";

//*** Public Attributes

    // Sides (mapping to com.cboe.idl.cmiConstants.Sides)
    public static final char UNSPECIFIED  = com.cboe.idl.cmiConstants.Sides.UNSPECIFIED;
    public static final char BUY  = com.cboe.idl.cmiConstants.Sides.BUY;
    public static final char SELL = com.cboe.idl.cmiConstants.Sides.SELL;
    public static final char BID  = com.cboe.idl.cmiConstants.Sides.BID;
    public static final char ASK  = com.cboe.idl.cmiConstants.Sides.ASK;
    public static final char DEFINED  = com.cboe.idl.cmiConstants.Sides.AS_DEFINED;
    public static final char OPPOSITE  = com.cboe.idl.cmiConstants.Sides.OPPOSITE;
    public static final char SELL_SHORT  = com.cboe.idl.cmiConstants.Sides.SELL_SHORT;
    public static final char SELL_SHORT_EXEMPT  = com.cboe.idl.cmiConstants.Sides.SELL_SHORT_EXEMPT;
    public static final char BUY_MINUS  = com.cboe.idl.cmiConstants.Sides.BUY_MINUS;
    public static final char SELL_PLUS  = com.cboe.idl.cmiConstants.Sides.SELL_PLUS;

    // Format constants
    public static final String LITERAL_FORMAT     = "LITERAL_FORMAT";
    public static final String BUY_SELL_FORMAT    = "BUY_SELL_FORMAT";
    public static final String BOUGHT_SOLD_FORMAT = "BOUGHT_SOLD_FORMAT" ;
    public static final String BOT_SOLD_FORMAT = "BOT_SOLD_FORMAT";
    public static final String NO_BID_FORMAT      = "NO_BID_FORMAT";

//*** Private Attributes

    private static final String BOUGHT_STRING  = "Bought";
    private static final String BOT_STRING = "Bot";
    private static final String BUY_STRING     = "Buy";
    private static final String INVALID_FORMAT = "ERROR: Invalid Format Specifier";
    private static final String INVALID_TYPE   = "ERROR: Invalid Side Type Code";
    private static final String SELL_STRING    = "Sell";
    private static final String SOLD_STRING    = "Sold";
    private static final String SELL_SHORT_STRING    = "Sell Shrt";
    private static final String SELL_SHORT_EXEMPT_STRING    = "Sell S Exempt";

    private static final String ASK_STRING     = "Ask";
    private static final String EMPTY_STRING   = "";
    private static final String DEFINED_STRING = "Defined";
    private static final String OPPOSITE_STRING = "Opposite";
    private static final String UNSPECIFIED_STRING = "Unspecified";
    private static final String BUY_MINUS_STRING = "-Buy";
    private static final String SELL_PLUS_STRING = "+Sell";

//*** Public Methods

    /*****************************************************************************
     * Returns a string representation of the object in PRESENT_TENSE_FORMAT format
     *
     * @param side - the sides code to render (see defined constants)
     * @return a string representation of the side
     * @see com.cboe.idl.cmiConstants.Sides
     */
    public static String toString( char side )
    {
        return toString( side, LITERAL_FORMAT );
    }

    public static boolean isBuyEquivalent(char side)
    {
        return SidesSpecifier.isBuyEquivalent(side);
    }

    public static boolean isSellEquivalent(char side)
    {
        return SidesSpecifier.isSellEquivalent(side);
    }


    /*****************************************************************************
     * Returns a string representation of the object in the given format
     *
     * @param side - the sides code to render (see defined constants)
     *               remember the case must match the constants
     *
     * @param formatSpecifier - a string that specifies how the object should
     *                          format itself.
     * @return a string representation of the side
     * @see com.cboe.idl.cmiConstants.Sides
     */
    public static String toString( char side, String formatSpecifier )
    {
    String retValue = "";

        if( formatSpecifier.equals( LITERAL_FORMAT ))
        {
            retValue = formatLiteralFormat(side);
        }
        else if( formatSpecifier.equals( BUY_SELL_FORMAT ))
        {
            retValue = formatBuySellFormat(side);
        }
        else if( formatSpecifier.equals( BOUGHT_SOLD_FORMAT ))
        {
            retValue = formatBoughtSoldFormat(side);
        }
        else if(formatSpecifier.equals(BOT_SOLD_FORMAT))
        {
            retValue = formatBotSoldFormat(side);
        }
        else if (formatSpecifier.equals(NO_BID_FORMAT))
        {
            retValue = formatNoBidFormat(side);
        }
        else
        {
            GUILoggerHome.find().alarm(Category+" toString ", "format: "+formatSpecifier+" not found");
            retValue = INVALID_FORMAT;
        }

//        GUILoggerHome.find().debug(Category+" toString format: "+formatSpecifier, GUILoggerBusinessProperty.COMMON, "return String = "+retValue);
        return retValue;
    }

    protected static String formatLiteralFormat( char side)
    {
        String retValue = "";

        switch( side )
        {
        // Both BUY and BID are defined as 'B'. The following is a kludge to
        // make up for this
            case 'B':
                retValue = String.valueOf( 'B' );
                break;
            case SELL:
                retValue = String.valueOf( SELL );
                break;
            case ASK:
                retValue = String.valueOf( ASK );
                break;
            case DEFINED:
                 retValue = String.valueOf( DEFINED );
                 break;
            case OPPOSITE:
                 retValue = String.valueOf( OPPOSITE );
                 break;
            case SELL_SHORT:
                 retValue = String.valueOf(SELL_SHORT);
                 break;
            case SELL_SHORT_EXEMPT:
                retValue = String.valueOf(SELL_SHORT_EXEMPT);
                break;
            case UNSPECIFIED:
                 retValue = String.valueOf( UNSPECIFIED );
                 break;
            case BUY_MINUS:
                 retValue = String.valueOf(BUY_MINUS);
                 break;
            case SELL_PLUS:
                retValue = String.valueOf(SELL_PLUS);
                break;

            default:
                GUILoggerHome.find().alarm(Category+" formatLiteralFormat - LITERAL_FORMAT", "Side: "+side+" not found");
                retValue = getFormattedInvalidSide(side);
                break;
        }

        return retValue;
    }

    protected static String formatBuySellFormat( char side)
    {
        String retValue = "";

        switch( side )
        {
            case BUY:
                retValue = BUY_STRING;
                break;
            case SELL:
                retValue = SELL_STRING;
                break;
            case DEFINED:
                 retValue = DEFINED_STRING;
                 break;
            case OPPOSITE:
                 retValue = OPPOSITE_STRING;
                 break;
            case SELL_SHORT:
                 retValue = SELL_SHORT_STRING;
                 break;
            case SELL_SHORT_EXEMPT:
                 retValue = SELL_SHORT_EXEMPT_STRING;
                 break;
            case UNSPECIFIED:
                 retValue = UNSPECIFIED_STRING;
                 break;
            case BUY_MINUS:
                 retValue = BUY_MINUS_STRING;
                 break;
            case SELL_PLUS:
                retValue = SELL_PLUS_STRING;
                break;
            default:
                GUILoggerHome.find().alarm(Category+" formatBuySellFormat - BUY_SELL_FORMAT", "Side: "+side+" not found");
                retValue = getFormattedInvalidSide(side);
                break;
        }
        return retValue;
    }

    protected static String formatBoughtSoldFormat( char side)
    {
        String retValue = "";

        switch( side )
        {
            case BUY:
            case BUY_MINUS:
                retValue = BOUGHT_STRING;
                break;
            case SELL:
            case SELL_SHORT:
            case SELL_SHORT_EXEMPT:
            case SELL_PLUS:
                retValue = SOLD_STRING;
                break;
            case DEFINED:
                 retValue = DEFINED_STRING;
                 break;
            case OPPOSITE:
                 retValue = OPPOSITE_STRING;
                 break;
            case UNSPECIFIED:
                 retValue = UNSPECIFIED_STRING;
                 break;
            default:
                GUILoggerHome.find().alarm(Category+" formatBoughtSoldFormat - BOUGHT_SOLD_FORMAT", "Side: "+side+" not found");
                retValue = getFormattedInvalidSide(side);
                break;
        }
        return retValue;
    }

    protected static String formatBotSoldFormat(char side)
    {
        String retValue = "";

        switch(side)
        {
            case BUY:
            case BUY_MINUS:
                retValue = BOT_STRING;
                break;
            case SELL:
            case SELL_SHORT:
            case SELL_SHORT_EXEMPT:
            case SELL_PLUS:
                retValue = SOLD_STRING;
                break;
            case DEFINED:
                retValue = DEFINED_STRING;
                break;
            case OPPOSITE:
                retValue = OPPOSITE_STRING;
                break;
            case UNSPECIFIED:
                retValue = UNSPECIFIED_STRING;
                break;
            default:
                GUILoggerHome.find().alarm(Category + " formatBotSoldFormat - BOT_SOLD_FORMAT",
                                           "Side: " + side + " not found");
                retValue = getFormattedInvalidSide(side);
                break;
        }
        return retValue;
    }

    protected static String formatNoBidFormat( char side)
    {
        String retValue = "";
        switch (side)
        {
            case BUY:
                retValue = BUY_STRING;
                break;
            case ASK:
                retValue = ASK_STRING;
                break;
            case SELL:
                retValue = SELL_STRING;
                break;
            case SELL_SHORT:
                retValue = SELL_SHORT_STRING;
                break;
            case SELL_SHORT_EXEMPT:
                retValue = SELL_SHORT_EXEMPT_STRING;
                break;
            case DEFINED:
                 retValue = DEFINED_STRING;
                 break;
            case OPPOSITE:
                 retValue = OPPOSITE_STRING;
                 break;
            case UNSPECIFIED:
                 retValue = UNSPECIFIED_STRING;
                 break;
            case BUY_MINUS:
                 retValue = BUY_MINUS_STRING;
                 break;
            case SELL_PLUS:
                retValue = SELL_PLUS_STRING;
                break;
            default:
                GUILoggerHome.find().alarm(Category+" formatNoBidFormat - NO_BID_FORMAT", "Side: "+side+" not found");
                retValue = retValue = getFormattedInvalidSide(side);
                break;
        }

        return retValue;
    }

//*** Private Methods

    /*****************************************************************************
     * Hide the default constructor from the public interface
     */
    private Sides( )
    {
    }

    private static String getFormattedInvalidSide(char side)
    {
        return new StringBuffer(20).append("Side: ").append(side).append(" not found ").toString();
    }

}
