package com.cboe.presentation.common.formatters;

import com.cboe.interfaces.domain.routingProperty.common.ContingencyType;

public class ContingencyTypes
{

	//*** Public Attributes

	    // Contingency Types (mapping to com.cboe.idl.cmiConstants.ContingencyTypes)
		// The All contingency type is used by the gui to notify server that all contingency types will be applied.
		public static final short ALL = ContingencyType.ALL;
	    public static final short AON      = com.cboe.idl.cmiConstants.ContingencyTypes.AON;
	    public static final short CLOSE    = com.cboe.idl.cmiConstants.ContingencyTypes.CLOSE;
	    public static final short FOK      = com.cboe.idl.cmiConstants.ContingencyTypes.FOK;
	    public static final short IOC      = com.cboe.idl.cmiConstants.ContingencyTypes.IOC;
	    public static final short MIN      = com.cboe.idl.cmiConstants.ContingencyTypes.MIN;
	    public static final short MIT      = com.cboe.idl.cmiConstants.ContingencyTypes.MIT;
	    public static final short NONE     = com.cboe.idl.cmiConstants.ContingencyTypes.NONE;
	    public static final short NOTHELD  = com.cboe.idl.cmiConstants.ContingencyTypes.NOTHELD;
	    public static final short OPG      = com.cboe.idl.cmiConstants.ContingencyTypes.OPG;
	    public static final short STP      = com.cboe.idl.cmiConstants.ContingencyTypes.STP;
	    public static final short STP_LOSS = com.cboe.idl.cmiConstants.ContingencyTypes.STP_LOSS;
	    public static final short STP_LIMIT = com.cboe.idl.cmiConstants.ContingencyTypes.STP_LIMIT;
	    public static final short WD       = com.cboe.idl.cmiConstants.ContingencyTypes.WD;
	    public static final short AUCTION_RESPONSE = com.cboe.idl.cmiConstants.ContingencyTypes.AUCTION_RESPONSE;
	    public static final short SWEEP = com.cboe.idl.cmiConstants.ContingencyTypes.INTERMARKET_SWEEP;
	    public static final short RESERVE = com.cboe.idl.cmiConstants.ContingencyTypes.RESERVE;
	    public static final short CROSS   = com.cboe.idl.cmiConstants.ContingencyTypes.CROSS;
	    public static final short MIDPOINT_CROSS   = com.cboe.idl.cmiConstants.ContingencyTypes.MIDPOINT_CROSS;
	    public static final short AUTOLINK_CROSS = com.cboe.idl.cmiConstants.ContingencyTypes.AUTOLINK_CROSS;
	    public static final short AUTOLINK_CROSS_MATCH = com.cboe.idl.cmiConstants.ContingencyTypes.AUTOLINK_CROSS_MATCH;
	    public static final short TIED_CROSS = com.cboe.idl.cmiConstants.ContingencyTypes.TIED_CROSS;
	    public static final short CROSS_WITHIN = com.cboe.idl.cmiConstants.ContingencyTypes.CROSS_WITHIN;
	    public static final short TIED_CROSS_WITHIN = com.cboe.idl.cmiConstants.ContingencyTypes.TIED_CROSS_WITHIN;
	    public static final short STOCK_ODD_LOT_NBBO_ONLY =  com.cboe.idl.cmiConstants.ContingencyTypes.STOCK_ODD_LOT_NBBO_ONLY;
	    public static final short NBBO_FLASH_RESPONSE =  com.cboe.idl.cmiConstants.ContingencyTypes.NBBO_FLASH_RESPONSE;
	    public static final short DO_NOT_ROUTE =  com.cboe.idl.cmiConstants.ContingencyTypes.DO_NOT_ROUTE;
	    public static final short NBBO_FLASH_THEN_CANCEL =  com.cboe.idl.cmiConstants.ContingencyTypes.NBBO_FLASH_THEN_CANCEL;
	    public static final short SWEEP_BOOK = com.cboe.idl.cmiConstants.ContingencyTypes.INTERMARKET_SWEEP_BOOK;
	    public static final short BID_PEG_CROSS = com.cboe.idl.cmiConstants.ContingencyTypes.BID_PEG_CROSS;
	    public static final short OFFER_PEG_CROSS = com.cboe.idl.cmiConstants.ContingencyTypes.OFFER_PEG_CROSS;
	    public static final short TIED_CROSS_SWEEP = com.cboe.idl.cmiConstants.ContingencyTypes.TIED_CROSS_SWEEP;
	    public static final short CASH_CROSS = com.cboe.idl.cmiConstants.ContingencyTypes.CASH_CROSS;
	    public static final short NEXT_DAY_CROSS = com.cboe.idl.cmiConstants.ContingencyTypes.NEXT_DAY_CROSS;
	    public static final short TWO_DAY_CROSS = com.cboe.idl.cmiConstants.ContingencyTypes.TWO_DAY_CROSS;
        public static final short WTP = com.cboe.idl.cmiConstants.ContingencyTypes.WTP;

	    // Format constants
	    public static final String TRADERS_FORMAT = "TRADERS_FORMAT";
	    public static final String FULL_FORMAT    = "FULL_FORMAT";

	    public static final String INVALID_FORMAT = "ERROR: Invalid Format Specifier";
	    public static final String INVALID_CONTINGENCY_TYPE   = "ERROR: Invalid Contingency Type Code";

	//*** Private Attributes
	    private static final String ALL_STRING              = "ALL";
	    private static final String AON_STRING              = "AON";
	    private static final String CLOSE_STRING            = "CLOSE";
	    private static final String FOK_STRING              = "FOK";
	    private static final String IOC_STRING              = "IOC";
	    private static final String MIN_STRING              = "MIN";
	    private static final String MIT_STRING              = "MIT";
	    private static final String NONE_STRING             = "NONE";
	    private static final String NOTHELD_STRING          = "NOT HELD";
	    private static final String OPG_STRING              = "OPG";
	    private static final String STP_STRING              = "STP";
	    private static final String STP_LOSS_STRING         = "STP LOSS";
	    private static final String STP_LIMIT_STRING        = "STP LIMIT";
	    private static final String WD_STRING               = "WD";
	    private static final String AUCTION_RESPONSE_STRING = "AUCTION RESPONSE";
	    private static final String SWEEP_STRING            = "SWEEP";
	    private static final String RESERVE_STRING          = "RESERVE";
	    private static final String CROSS_STRING            = "CROSS";
	    private static final String MIDPOINT_CROSS_STRING   = "MIDPOINT CROSS";
	    private static final String AUTOLINK_CROSS_STRING   = "AUTOLINK CROSS";
	    private static final String AUTOLINK_CROSS_MATCH_STRING   = "AUTOLINK CROSS MATCH";
	    private static final String TIED_CROSS_STRING       = "TIED CROSS";
	    private static final String CROSS_WITHIN_STRING     = "CROSS WITHIN";
	    private static final String TIED_CROSS_WITHIN_STRING = "TIED CROSS WITHIN";
	    private static final String STOCK_ODD_LOT_NBBO_ONLY_STRING = "STOCK ODD LOT NBBO ONLY";
	    private static final String NBBO_FLASH_RESPONSE_STRING = "NBBO_FLASH_RESPONSE";
	    private static final String DO_NOT_ROUTE_STRING = "DO NOT ROUTE";
	    private static final String NBBO_FLASH_THEN_CANCEL_STRING = "NBBO_FLASH_THEN_CANCEL";
	    private static final String SWEEP_BOOK_STRING       = "SWEEP_BOOK";
	    private static final String BID_PEG_CROSS_STRING    = "BID PEG CROSS";
	    private static final String OFFER_PEG_CROSS_STRING  = "OFFER PEG CROSS";
	    private static final String TIED_CROSS_SWEEP_STRING = "TIED_CROSS_SWEEP ";
	    private static final String CASH_CROSS_STRING = "CASH CROSS";
	    private static final String NEXT_DAY_CROSS_STRING = "NEXT DAY CROSS";
	    private static final String TWO_DAY_CROSS_STRING = "TWO_DAY CROSS";
        private static final String WTP_STRING = "WTP";
	    private static final String EMPTY_STRING            = "";

	//*** Public Methods

	    /*****************************************************************************
	     * Returns a string representation of the object in TRADERS_FORMAT format
	     *
	     * @param contingencyType - the ContingencyType code to render (see defined constants)
	     * @return a string representation of the ContingencyType
	     * @see com.cboe.idl.cmiConstants.ContingencyTypes
	     */
	    public static String toString( short contingencyType )
	    {
	        return toString( contingencyType, TRADERS_FORMAT );
	    }


	    /*****************************************************************************
	     * Returns a string representation of the object in the given format
	     *
	     * @param contingencyType - the ContingencyType code to render (see defined constants)
	     * @param formatSpecifier - a string that specifies how the object should
	     *                          format itself.
	     * @return a string representation of the ContingencyType
	     * @see com.cboe.idl.cmiConstants.ContingencyTypes
	     */
	    @SuppressWarnings({"OverlyComplexMethod", "OverlyLongMethod", "MethodWithMultipleReturnPoints"})
	    public static String toString( short contingencyType, String formatSpecifier )
	    {
	        if (!(formatSpecifier.equals(TRADERS_FORMAT)) && !(formatSpecifier.equals(FULL_FORMAT)))
	        {
	            return INVALID_FORMAT;
	        }
	        else
	        {
	            switch( contingencyType )
	            {
	                case NONE:
	                    if( formatSpecifier.equals( FULL_FORMAT ))
	                    {
	                        return NONE_STRING;
	                    }
	                    return EMPTY_STRING;
	                case ALL:
	                    return ALL_STRING;
	                case AON:
	                    return AON_STRING;
	                case FOK:
	                    return FOK_STRING;
	                case IOC:
	                    return IOC_STRING;
	                case OPG:
	                    return OPG_STRING;
	                case MIN:
	                    return MIN_STRING;
	                case NOTHELD:
	                    return NOTHELD_STRING;
	                case WD:
	                    return WD_STRING;
	                case MIT:
	                    return MIT_STRING;
	                case STP:
	                    return STP_STRING;
	                case STP_LOSS:
	                    return STP_LOSS_STRING;
	                case STP_LIMIT:
	                    return STP_LIMIT_STRING;
	                case CLOSE:
	                    return CLOSE_STRING;
	                case AUCTION_RESPONSE:
	                    return AUCTION_RESPONSE_STRING;
	                case SWEEP:
	                    return SWEEP_STRING;
	                case RESERVE:
	                    return RESERVE_STRING;
	                case CROSS:
	                    return CROSS_STRING;
	                case CROSS_WITHIN:
	                    return CROSS_WITHIN_STRING;
	                case MIDPOINT_CROSS:
	                    return MIDPOINT_CROSS_STRING;
	                case AUTOLINK_CROSS:
	                    return AUTOLINK_CROSS_STRING;
	                case AUTOLINK_CROSS_MATCH:
	                    return AUTOLINK_CROSS_MATCH_STRING;
	                case TIED_CROSS:
	                    return TIED_CROSS_STRING;
	                case TIED_CROSS_WITHIN:
	                    return TIED_CROSS_WITHIN_STRING;
	                case STOCK_ODD_LOT_NBBO_ONLY:
	                    return STOCK_ODD_LOT_NBBO_ONLY_STRING;
	                case NBBO_FLASH_RESPONSE:
	                    return NBBO_FLASH_RESPONSE_STRING;
	                case DO_NOT_ROUTE:
	                    return DO_NOT_ROUTE_STRING;
	                case NBBO_FLASH_THEN_CANCEL:
	                    return NBBO_FLASH_THEN_CANCEL_STRING;
	                case SWEEP_BOOK:
	                    return SWEEP_BOOK_STRING;
	                case BID_PEG_CROSS:
	                    return BID_PEG_CROSS_STRING;
	                case OFFER_PEG_CROSS:
	                    return OFFER_PEG_CROSS_STRING;
	                case TIED_CROSS_SWEEP:
	                    return TIED_CROSS_SWEEP_STRING;
	                case CASH_CROSS:
	                    return CASH_CROSS_STRING;
	                case NEXT_DAY_CROSS:
	                    return NEXT_DAY_CROSS_STRING;
	                case TWO_DAY_CROSS:
	                    return TWO_DAY_CROSS_STRING;
                    case WTP:
                        return WTP_STRING;
	                default:
//	                    return new StringBuffer(20).append(INVALID_TYPE).append(" ").append(contingencyType).toString();
	                    return new StringBuffer(20).append(INVALID_CONTINGENCY_TYPE).append("[ ").append(contingencyType).append(" ]").toString();
	            }
	        }
	    }



	//*** Private Methods

	    /*****************************************************************************
	     * Hide the default constructor from the public interface
	     */
	    private ContingencyTypes( )
	    {
	    }


}
