package com.cboe.presentation.common.formatters;

/**
 * Title:        ProductTypes
 * Description:  Describes the product types
 * Company:      The Chicago Board Options Exchange
 * Copyright:    Copyright (c) 2001 The Chicago Board Options Exchange. All Rights Reserved.
 * @author Luis Torres
 * @version 1.0
 */

public class ProductTypes
{
    // Status Update Reasons (mapping to com.cboe.idl.cmiConstants.StatusUpdateReasons)

    public  static final short OPTION                          = com.cboe.idl.cmiConstants.ProductTypes.OPTION;
    public  static final short EQUITY                          = com.cboe.idl.cmiConstants.ProductTypes.EQUITY;
    public  static final short COMMODITY                       = com.cboe.idl.cmiConstants.ProductTypes.COMMODITY;
    public  static final short DEBT                            = com.cboe.idl.cmiConstants.ProductTypes.DEBT;
    public  static final short FUTURE                          = com.cboe.idl.cmiConstants.ProductTypes.FUTURE;
    public  static final short INDEX                           = com.cboe.idl.cmiConstants.ProductTypes.INDEX;
    public  static final short LINKED_NOTE                     = com.cboe.idl.cmiConstants.ProductTypes.LINKED_NOTE;
    public  static final short STRATEGY                        = com.cboe.idl.cmiConstants.ProductTypes.STRATEGY;
    public  static final short UNIT_INVESTMENT_TRUST           = com.cboe.idl.cmiConstants.ProductTypes.UNIT_INVESTMENT_TRUST;
    public  static final short VOLATILITY_INDEX                = com.cboe.idl.cmiConstants.ProductTypes.VOLATILITY_INDEX;
    public  static final short WARRANT                         = com.cboe.idl.cmiConstants.ProductTypes.WARRANT;
    public  static final short INTEREST_RATE_COMPOSITE         = com.cboe.idl.cmiConstants.ProductTypes.INTEREST_RATE_COMPOSITE;

    private static final String OPTION_STRING                  = "Option";
    private static final String EQUITY_STRING                  = "Equity";
    private static final String COMMODITY_STRING               = "Commodity";
    private static final String DEBT_STRING                    = "Debt";
    private static final String FUTURE_STRING                  = "Future";
    private static final String INDEX_STRING                   = "Index";
    private static final String LINKED_NOTE_STRING             = "Linked Note";
    private static final String SPREAD_STRING                  = "Sprd";
    private static final String STRATEGY_STRING                = "Strategy";
    private static final String UNIT_INVESTMENT_TRUST_STRING   = "Unit Investment";
    private static final String VOLATILITY_INDEX_STRING        = "Volatility Index";
    private static final String WARRANT_STRING                 = "Warrant";
    private static final String INTEREST_RATE_COMPOSITE_STRING = "Interest Rate Composite";
    private static final String UNKNOWN_STRING                 = "Unknown";

    // Format constants
    public static final String TRADERS_FORMAT = new String( "TRADERS_FORMAT" );
    public static final String DROP_COPY_FORMAT = new String("DROP_COPY_FORMAT");


    /*****************************************************************************
     * Returns a string representation of the object in TRADERS_FORMAT format
     *
     * @param type - the order state code to render (see defined constants)
     * @return a string representation of the orderState
     * @see com.cboe.idl.cmiConstants.OrderStates
     */
    public static String toString( short type )
    {
        return toString( type, TRADERS_FORMAT );
    }

    /*****************************************************************************
     * Returns a string representation of the object in the given format
     *
     * @param type - the product type code to render (see defined constants)
     * @param formatSpecifier - a string that specifies how the object should
     *                          format itself.
     * @return a string representation of the product type
     * @see com.cboe.idl.cmiConstants.ProductTypes
     */
    public static String toString( short type, String formatSpecifier )
    {
        if(formatSpecifier.equals(DROP_COPY_FORMAT))
        {
            switch(type)
            {
                case STRATEGY:
                    return SPREAD_STRING;
                default:
                    return toString(type, TRADERS_FORMAT);
            }
        }
        else if( formatSpecifier.equals( TRADERS_FORMAT ))
        {
            switch( type )
            {
            case OPTION:
                return OPTION_STRING;
            case EQUITY:
                return EQUITY_STRING;
            case COMMODITY:
                return COMMODITY_STRING;
            case DEBT:
                return DEBT_STRING;
            case FUTURE:
                return FUTURE_STRING;
            case INDEX:
                return INDEX_STRING;
            case LINKED_NOTE:
                return LINKED_NOTE_STRING;
            case STRATEGY:
                return STRATEGY_STRING;
            case UNIT_INVESTMENT_TRUST:
                return UNIT_INVESTMENT_TRUST_STRING;
            case VOLATILITY_INDEX:
                return VOLATILITY_INDEX_STRING;
            case WARRANT:
                return WARRANT_STRING;
            case INTEREST_RATE_COMPOSITE:
                return INTEREST_RATE_COMPOSITE_STRING;
            default:
                return new StringBuffer(20).append("[").append(type).append("]").toString();
            }
        }
        return UNKNOWN_STRING;
    }

    /**
     * Hide the default constructor from the public interface
     */
    private ProductTypes()
    {
    }
}
