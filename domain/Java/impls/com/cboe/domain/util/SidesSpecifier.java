/********************************************************************************
 * FILE:    SidesSpecifier.java
 *
 * PACKAGE: com.cboe.domain.util
 *
 * ----------------------------------------------------------------------
 * Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
 *
 *******************************************************************************/

package com.cboe.domain.util;

// CBOE imports
// Inlined to avoid name conflict: com.cboe.idl.cmiConstants.Sides;


/********************************************************************************
 * Represents Sides constants
 *
 * @see com.cboe.idl.cmiConstants.Sides
 */
public class SidesSpecifier
{

//*** Public Attributes

    // Sides (mapping to com.cboe.idl.cmiConstants.Sides)
    public static final char UNSPECIFIED = com.cboe.idl.cmiConstants.Sides.UNSPECIFIED;
    public static final char BUY  = com.cboe.idl.cmiConstants.Sides.BUY;
    public static final char SELL = com.cboe.idl.cmiConstants.Sides.SELL;
    public static final char BID  = com.cboe.idl.cmiConstants.Sides.BID;
    public static final char ASK  = com.cboe.idl.cmiConstants.Sides.ASK;
    public static final char DEFINED  = com.cboe.idl.cmiConstants.Sides.AS_DEFINED;
    public static final char OPPOSITE  = com.cboe.idl.cmiConstants.Sides.OPPOSITE;
    public static final char SELL_SHORT = com.cboe.idl.cmiConstants.Sides.SELL_SHORT;
    public static final char SELL_SHORT_EXEMPT = com.cboe.idl.cmiConstants.Sides.SELL_SHORT_EXEMPT;
    public static final char BUY_MINUS = com.cboe.idl.cmiConstants.Sides.BUY_MINUS;
    public static final char SELL_PLUS = com.cboe.idl.cmiConstants.Sides.SELL_PLUS;


//*** Public Methods

    public static boolean isBuyEquivalent(char side)
    {
        boolean result = false;
        if( side == BUY || side == BID || side == DEFINED || side == BUY_MINUS)
        {
            result = true;
        }
        return result;
    }

    public static boolean isSellEquivalent(char side)
    {
        boolean result = false;
        if( side == SELL || side == ASK || side == OPPOSITE || side == SELL_SHORT || side == SELL_SHORT_EXEMPT || side == SELL_PLUS)
        {
            result = true;
        }
        return result;
    }


//*** Private Methods

    /*****************************************************************************
     * Hide the default constructor from the public interface
     */
    private SidesSpecifier( )
    {
    }

}
