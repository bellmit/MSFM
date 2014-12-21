/********************************************************************************
 * FILE:    ProductStates.java
 *
 * PACKAGE: com.cboe.presentation.common.types.constants
 *
 * ----------------------------------------------------------------------
 * Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
 *
 *******************************************************************************/

package com.cboe.presentation.common.formatters;

// CBOE imports
// Inlined to avoid name conflict: com.cboe.idl.cmiConstants.ProductStates;


/********************************************************************************
 * Represents Order States
 *
 * @see com.cboe.idl.cmiConstants.ProductStates
 */
public class ProductStates
{

//*** Public Attributes

    // Order States (mapping to com.cboe.idl.cmiConstants.ProductStates)
    public static final short UNKNOWN          = com.cboe.idl.cmiConstants.ProductStates.UNKNOWN;
    public static final short CLOSED           = com.cboe.idl.cmiConstants.ProductStates.CLOSED;
    public static final short ENDING_HOLD      = com.cboe.idl.cmiConstants.ProductStates.ENDING_HOLD;
    public static final short FAST_MARKET      = com.cboe.idl.cmiConstants.ProductStates.FAST_MARKET;
    public static final short HALTED           = com.cboe.idl.cmiConstants.ProductStates.HALTED;
    public static final short NO_SESSION       = com.cboe.idl.cmiConstants.ProductStates.NO_SESSION;
    public static final short ON_HOLD          = com.cboe.idl.cmiConstants.ProductStates.ON_HOLD;
    public static final short OPEN             = com.cboe.idl.cmiConstants.ProductStates.OPEN;
    public static final short OPENING_ROTATION = com.cboe.idl.cmiConstants.ProductStates.OPENING_ROTATION;
    public static final short PRE_OPEN         = com.cboe.idl.cmiConstants.ProductStates.PRE_OPEN;
    public static final short SUSPENDED        = com.cboe.idl.cmiConstants.ProductStates.SUSPENDED;


    // Format constants
    public static final String TRADERS_FORMAT  = "TRADERS_FORMAT";
    public static final String BRIEF_FORMAT    = "BRIEF_FORMAT";

    public static final String INVALID_FORMAT             = "ERROR: Invalid Format Specifier";
    public static final String INVALID_TYPE               = "ERROR: Invalid Type Code";
    public static final String INVALID_PRODUCT_STATE_TYPE = "ERROR: Invalid Product State Type Code";

//*** Private Attributes

    private static final String UNKNOWN_STRING           = "Unknown";
    private static final String CLOSED_STRING            = "Closed";
    private static final String ENDING_HOLD_STRING       = "Ending Hold";
    private static final String HALTED_STRING            = "Halted";
    private static final String NO_SESSION_STRING        = "No Session";
    private static final String ON_HOLD_STRING           = "On Hold";
    private static final String OPEN_STRING              = "Open";
    private static final String OPENING_ROTATION_STRING  = "Opening Rotation";
    private static final String FAST_MARKET_STRING       = "Fast Market";
    private static final String FAST_MARKET_STRING_BRIEF = "Fast";
    private static final String PRE_OPEN_STRING          = "Pre-Open";
    private static final String PRE_OPEN_STRING_BRIEF    = "PreOpen";
    private static final String SUSPENDED_STRING         = "Suspended";



//*** Public Methods

    /*****************************************************************************
     * Returns a string representation of the object in TRADERS_FORMAT format
     *
     * @param productState - the product state code to render (see defined constants)
     * @return a string representation of the productState
     * @see com.cboe.idl.cmiConstants.ProductStates
     */
    public static String toString( short productState )
    {
        return toString( productState, TRADERS_FORMAT );
    }


    /*****************************************************************************
     * Returns a string representation of the object in the given format
     *
     * @param productState - the product state code to render (see defined constants)
     * @param formatSpecifier - a string that specifies how the object should
     *                          format itself.
     * @return a string representation of the productState
     * @see com.cboe.idl.cmiConstants.ProductStates
     */
    public static String toString( short productState, String formatSpecifier )
    {
        if (formatSpecifier.equals(TRADERS_FORMAT) || formatSpecifier.equals(BRIEF_FORMAT))
        {

            switch( productState )
            {
                case UNKNOWN:
                    return UNKNOWN_STRING;
                case CLOSED:
                    return CLOSED_STRING;
                case ENDING_HOLD:
                    return ENDING_HOLD_STRING;
                case FAST_MARKET:
                    if( formatSpecifier.equals( TRADERS_FORMAT ))
                    {
                        return FAST_MARKET_STRING;
                    }
                    else
                    {
                        return FAST_MARKET_STRING_BRIEF;
                    }
                case HALTED:
                    return HALTED_STRING;
                case NO_SESSION:
                    return NO_SESSION_STRING;
                case ON_HOLD:
                    return ON_HOLD_STRING;
                case OPEN:
                    return OPEN_STRING;
                case OPENING_ROTATION:
                    return OPENING_ROTATION_STRING;
                case SUSPENDED:
                    return SUSPENDED_STRING;
                case PRE_OPEN:
                    if( formatSpecifier.equals( TRADERS_FORMAT ))
                    {
                        return PRE_OPEN_STRING;
                    }
                    else
                    {
                        return PRE_OPEN_STRING_BRIEF;
                    }
                default:
                    if (formatSpecifier.equals(BRIEF_FORMAT))
                    {
                        return Short.toString(productState);
                    }
                    return new StringBuffer(20).append(INVALID_PRODUCT_STATE_TYPE).append("[ ").append(productState).append(" ]").toString();
            }
        } // end of if (formatSpecifier.equals(TRADERS_FORMAT) || formatSpecifier.equals(BRIEF_FORMAT))
        else
        {
            return INVALID_FORMAT;
        }
    }


//*** Private Methods

    /*****************************************************************************
     * Hide the default constructor from the public interface
     */
    private ProductStates( )
    {
    }

}
