/********************************************************************************
 * FILE:    ActivityFieldTypes.java
 *
 * PACKAGE: com.cboe.presentation.common.types.constants
 *
 * ----------------------------------------------------------------------
 * Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
 *
 *******************************************************************************/

package com.cboe.presentation.common.formatters;

import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.routingProperty.common.OrderLocation;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.common.formatters.LocationFormatStrategy;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.product.ProductHelper;

// CBOE imports
// Inlined to avoid name conflict: com.cboe.idl.cmiConstants.ActivityFieldTypes;


/**
 * ***************************************************************************** Represents Activity Events
 *
 * @see com.cboe.idl.cmiConstants.ActivityFieldTypes
 */
@SuppressWarnings({"ConstantNamingConvention"})
public class ActivityFieldTypes
{
//*** Public Attributes

    // Activity Fields (mapping to com.cboe.idl.cmiConstants.ActivityFieldTypes)
    public static final short ACCOUNT = com.cboe.idl.cmiConstants.ActivityFieldTypes.ACCOUNT;
    public static final short ACTIVITY_TIME = com.cboe.idl.cmiConstants.ActivityFieldTypes.ACTIVITY_TIME;
    public static final short ASK_PRICE = com.cboe.idl.cmiConstants.ActivityFieldTypes.ASK_PRICE;
    public static final short ASK_QTY = com.cboe.idl.cmiConstants.ActivityFieldTypes.ASK_QTY;
    public static final short BID_PRICE = com.cboe.idl.cmiConstants.ActivityFieldTypes.BID_PRICE;
    public static final short BID_QTY = com.cboe.idl.cmiConstants.ActivityFieldTypes.BID_QTY;
    public static final short BOOKED_QUANTITY = com.cboe.idl.cmiConstants.ActivityFieldTypes.BOOKED_QUANTITY;
    public static final short BUSTED_QUANTITY = com.cboe.idl.cmiConstants.ActivityFieldTypes.BUSTED_QUANTITY;
    public static final short CANCEL_REASON = com.cboe.idl.cmiConstants.ActivityFieldTypes.CANCEL_REASON;
    public static final short CANCELLED_QUANTITY = com.cboe.idl.cmiConstants.ActivityFieldTypes.CANCELLED_QUANTITY;
    public static final short CMTA = com.cboe.idl.cmiConstants.ActivityFieldTypes.CMTA;
    public static final short CONTINGENCY_TYPE = com.cboe.idl.cmiConstants.ActivityFieldTypes.CONTINGENCY_TYPE;
    public static final short EVENT_STATUS = com.cboe.idl.cmiConstants.ActivityFieldTypes.EVENT_STATUS;
    public static final short IS_AUTO_LINKED = com.cboe.idl.cmiConstants.ActivityFieldTypes.IS_AUTO_LINKED;
    public static final short LEAVES_QUANTITY = com.cboe.idl.cmiConstants.ActivityFieldTypes.LEAVES_QUANTITY;
    public static final short MARKETIBILITY_INDICATOR = com.cboe.idl.cmiConstants.ActivityFieldTypes.MARKETIBILITY_INDICATOR;
    public static final short MISMATCHED_QUANTITY = com.cboe.idl.cmiConstants.ActivityFieldTypes.MISMATCHED_QUANTITY;
    public static final short OPTIONAL_DATA = com.cboe.idl.cmiConstants.ActivityFieldTypes.OPTIONAL_DATA;
    public static final short ORDERID = com.cboe.idl.cmiConstants.ActivityFieldTypes.ORDERID;
    public static final short ORDER_PRICE = com.cboe.idl.cmiConstants.ActivityFieldTypes.ORDER_PRICE;
    public static final short ORDER_STATE = com.cboe.idl.cmiConstants.ActivityFieldTypes.ORDER_STATE;
    public static final short ORIGINAL_QUANTITY = com.cboe.idl.cmiConstants.ActivityFieldTypes.ORIGINAL_QUANTITY;
    public static final short PRICE = com.cboe.idl.cmiConstants.ActivityFieldTypes.PRICE;
    public static final short PRODUCT = com.cboe.idl.cmiConstants.ActivityFieldTypes.PRODUCT;
    public static final short PRODUCT_STATE = com.cboe.idl.cmiConstants.ActivityFieldTypes.PRODUCT_STATE;
    public static final short QUANTITY = com.cboe.idl.cmiConstants.ActivityFieldTypes.QUANTITY;
    public static final short QUOTEKEY = com.cboe.idl.cmiConstants.ActivityFieldTypes.QUOTEKEY;
    public static final short REINSTATED_QUANTITY = com.cboe.idl.cmiConstants.ActivityFieldTypes.REINSTATED_QUANTITY;
    public static final short REPLACE_ORDERID = com.cboe.idl.cmiConstants.ActivityFieldTypes.REPLACE_ORDERID;
    public static final short RFQ_TYPE = com.cboe.idl.cmiConstants.ActivityFieldTypes.RFQ_TYPE;
    public static final short SIDE = com.cboe.idl.cmiConstants.ActivityFieldTypes.SIDE;
    public static final short SUB_ACCOUNT = com.cboe.idl.cmiConstants.ActivityFieldTypes.SUBACCOUNT;
    public static final short TIME_IN_FORCE = com.cboe.idl.cmiConstants.ActivityFieldTypes.TIME_IN_FORCE;
    public static final short TIME_TO_LIVE = com.cboe.idl.cmiConstants.ActivityFieldTypes.TIME_TO_LIVE;
    public static final short TLC_QUANTITY = com.cboe.idl.cmiConstants.ActivityFieldTypes.TLC_QUANTITY;
    public static final short TRADED_QUANTITY = com.cboe.idl.cmiConstants.ActivityFieldTypes.TRADED_QUANTITY;
    public static final short TRADEID = com.cboe.idl.cmiConstants.ActivityFieldTypes.TRADEID;
    public static final short TRANSACTION_SEQUENCE_NUMBER = com.cboe.idl.cmiConstants.ActivityFieldTypes.TRANSACTION_SEQUENCE_NUMBER;
    public static final short USER_ASSIGNED_ID = com.cboe.idl.cmiConstants.ActivityFieldTypes.USER_ASSIGNED_ID;
    public static final short USER_KEY = com.cboe.idl.cmiConstants.ActivityFieldTypes.USER_KEY;
    public static final short EXEC_BROKER = com.cboe.idl.cmiConstants.ActivityFieldTypes.EXEC_BROKER;
    public static final short QUOTE_UPDATE_CONTROL_ID = com.cboe.idl.cmiConstants.ActivityFieldTypes.QUOTE_UPDATE_CONTROL_ID;

    public static final short ROUTE_REASON = com.cboe.idl.cmiConstants.ActivityFieldTypes.ROUTE_REASON;
    public static final short BBO_BID_PRICE = com.cboe.idl.cmiConstants.ActivityFieldTypes.BBO_BID_PRICE;
    public static final short BBO_BID_SIZE = com.cboe.idl.cmiConstants.ActivityFieldTypes.BBO_BID_SIZE;
    public static final short BBO_ASK_PRICE = com.cboe.idl.cmiConstants.ActivityFieldTypes.BBO_ASK_PRICE;
    public static final short BBO_ASK_SIZE = com.cboe.idl.cmiConstants.ActivityFieldTypes.BBO_ASK_SIZE;
    public static final short BOTR_ASK_PRICE = com.cboe.idl.cmiConstants.ActivityFieldTypes.BOTR_ASK_PRICE;
    public static final short BOTR_BID_PRICE = com.cboe.idl.cmiConstants.ActivityFieldTypes.BOTR_BID_PRICE;
    public static final short BOTR_ASK_EXCHANGES = com.cboe.idl.cmiConstants.ActivityFieldTypes.BOTR_ASK_EXCHANGES;
    public static final short BOTR_BID_EXCHANGES = com.cboe.idl.cmiConstants.ActivityFieldTypes.BOTR_BID_EXCHANGES;
    public static final short DSM_BID_PRICE = com.cboe.idl.cmiConstants.ActivityFieldTypes.DSM_BID_PRICE;
    public static final short DSM_BID_SIZE = com.cboe.idl.cmiConstants.ActivityFieldTypes.DSM_BID_SIZE;
    public static final short DSM_ASK_PRICE = com.cboe.idl.cmiConstants.ActivityFieldTypes.DSM_ASK_PRICE;
    public static final short DSM_ASK_SIZE = com.cboe.idl.cmiConstants.ActivityFieldTypes.DSM_ASK_SIZE;
    public static final short AUCTION_TYPE = com.cboe.idl.cmiConstants.ActivityFieldTypes.AUCTION_TYPE;
    public static final short QUANTITY_TRADED_IN_AUCTION = com.cboe.idl.cmiConstants.ActivityFieldTypes.QUANTITY_TRADED_IN_AUCTION;
    public static final short EARLY_AUCTION_END_FLAG = com.cboe.idl.cmiConstants.ActivityFieldTypes.EARLY_AUCTION_END_FLAG;
    public static final short COMPLEX_ORDER_TYPE = com.cboe.idl.cmiConstants.ActivityFieldTypes.COMPLEX_ORDER_TYPE;
    public static final short SOURCE_FIELD = com.cboe.idl.cmiConstants.ActivityFieldTypes.SOURCE_FIELD;
    public static final short ROUTE_DESCRIPTION = com.cboe.idl.cmiConstants.ActivityFieldTypes.ROUTE_DESCRIPTION;
    public static final short NBBO_ASK_PRICE = com.cboe.idl.cmiConstants.ActivityFieldTypes.NBBO_ASK_PRICE;
    public static final short NBBO_BID_PRICE = com.cboe.idl.cmiConstants.ActivityFieldTypes.NBBO_BID_PRICE;
    public static final short NBBO_ASK_EXCHANGES = com.cboe.idl.cmiConstants.ActivityFieldTypes.NBBO_ASK_EXCHANGES;
    public static final short NBBO_BID_EXCHANGES = com.cboe.idl.cmiConstants.ActivityFieldTypes.NBBO_BID_EXCHANGES;
    public static final short LOCATION = com.cboe.idl.cmiConstants.ActivityFieldTypes.LOCATION;
    public static final short LOCATION_TYPE = com.cboe.idl.cmiConstants.ActivityFieldTypes.LOCATION_TYPE;
    public static final short BOOK_BID_PRICE = com.cboe.idl.cmiConstants.ActivityFieldTypes.BOOK_BID_PRICE;
    public static final short BOOK_BID_SIZE = com.cboe.idl.cmiConstants.ActivityFieldTypes.BOOK_BID_SIZE;
    public static final short BOOK_ASK_PRICE = com.cboe.idl.cmiConstants.ActivityFieldTypes.BOOK_ASK_PRICE;
    public static final short BOOK_ASK_SIZE = com.cboe.idl.cmiConstants.ActivityFieldTypes.BOOK_ASK_SIZE;
    public static final short DELTA_NEUTRAL_INDICATOR = com.cboe.idl.cmiConstants.ActivityFieldTypes.DELTA_NEUTRAL_INDICATOR;
    public static final short SOURCE_FIELD_TYPE = com.cboe.idl.cmiConstants.ActivityFieldTypes.SOURCE_FIELD_TYPE;
    public static final short TT_INDICATOR = com.cboe.idl.cmiConstants.ActivityFieldTypes.TT_INDICATOR;
    public static final short EXCHANGE_INDICATORS = com.cboe.idl.cmiConstants.ActivityFieldTypes.EXCHANGE_INDICATORS;
    public static final short BTM_INDICATOR = com.cboe.idl.cmiConstants.ActivityFieldTypes.BTM_INDICATOR;
    public static final short LINKED_ORDERDBID = com.cboe.idl.cmiConstants.ActivityFieldTypes.LINKED_ORDERDBID;
    public static final short LINKED_ORDERIDSTR = com.cboe.idl.cmiConstants.ActivityFieldTypes.LINKED_ORDERIDSTR;
    public static final short ROUTE_DESTINATION = com.cboe.idl.cmiConstants.ActivityFieldTypes.ROUTE_DESTINATION;
    public static final short ROUTED_QUANTITY = com.cboe.idl.cmiConstants.ActivityFieldTypes.ROUTED_QUANTITY;
    public static final short RETURN_CODE = com.cboe.idl.cmiConstants.ActivityFieldTypes.RETURN_CODE;
    public static final short TSB_BID_PRICE = com.cboe.idl.cmiConstants.ActivityFieldTypes.TSB_BID_PRICE;
    public static final short TSB_BID_SIZE = com.cboe.idl.cmiConstants.ActivityFieldTypes.TSB_BID_SIZE;
    public static final short TSB_ASK_PRICE = com.cboe.idl.cmiConstants.ActivityFieldTypes.TSB_ASK_PRICE;
    public static final short TSB_ASK_SIZE = com.cboe.idl.cmiConstants.ActivityFieldTypes.TSB_ASK_SIZE;
    public static final short LEG_TRADE_RPT_IDS = com.cboe.idl.cmiConstants.ActivityFieldTypes.LEG_TRADE_RPT_IDS;
    public static final short RELATED_ORDER_IDS = com.cboe.idl.cmiConstants.ActivityFieldTypes.RELATED_ORDER_IDS;
    public static final short RELATED_ORDERDBID = com.cboe.idl.cmiConstants.ActivityFieldTypes.RELATED_ORDERDBID;
    public static final short RELATED_ORDER_FIRMNUMEBR = com.cboe.idl.cmiConstants.ActivityFieldTypes.RELATED_ORDER_FIRMNUMEBR;
    public static final short RELATED_ORDER_FIRMEXCHANGE = com.cboe.idl.cmiConstants.ActivityFieldTypes.RELATED_ORDER_FIRMEXCHANGE;
    public static final short RELATED_ORDER_BRANCH = com.cboe.idl.cmiConstants.ActivityFieldTypes.RELATED_ORDER_BRANCH;
    public static final short RELATED_ORDER_BRANCHSEQNUMBER = com.cboe.idl.cmiConstants.ActivityFieldTypes.RELATED_ORDER_BRANCHSEQNUMBER;
    public static final short RELATED_ORDER_CORRESPONDENTFIRM = com.cboe.idl.cmiConstants.ActivityFieldTypes.RELATED_ORDER_CORRESPONDENTFIRM;
    public static final short RELATED_ORDER_DATE = com.cboe.idl.cmiConstants.ActivityFieldTypes.RELATED_ORDER_DATE;
    public static final short RELATED_ORDER_ORSID = com.cboe.idl.cmiConstants.ActivityFieldTypes.RELATED_ORDER_ORSID;
    public static final short FILL_REASON = com.cboe.idl.cmiConstants.ActivityFieldTypes.FILL_REASON;
    public static final short SESSION_NAME = com.cboe.idl.cmiConstants.ActivityFieldTypes.SESSION_NAME;
    public static final short BULK_ORDER_REQ_ID = com.cboe.idl.cmiConstants.ActivityFieldTypes.BULK_ORDER_REQ_ID;
    public static final short SUBEVENT_TYPE = com.cboe.idl.cmiConstants.ActivityFieldTypes.SUBEVENT_TYPE;

    public static final short[] PRIORITY_DETAIL_FIELD_TYPES = {ORDERID,
                                                                SIDE,
                                                                ORIGINAL_QUANTITY,
                                                                PRODUCT,
                                                                PRICE,
                                                                ORDER_PRICE,
                                                                TRADED_QUANTITY,
                                                                BOOKED_QUANTITY,
                                                                ACTIVITY_TIME,
                                                                CONTINGENCY_TYPE,
                                                                LOCATION,
                                                                ROUTE_DESCRIPTION,
                                                                ROUTE_REASON,
                                                                BBO_ASK_SIZE,
                                                                BBO_ASK_PRICE,
                                                                BBO_BID_PRICE,
                                                                BBO_BID_SIZE,
                                                                NBBO_ASK_PRICE,
                                                                NBBO_BID_PRICE,
                                                                BOOK_ASK_SIZE,
                                                                BOOK_ASK_PRICE,
                                                                BOOK_BID_PRICE,
                                                                BOOK_BID_SIZE,
                                                                BOTR_ASK_PRICE,
                                                                BOTR_BID_PRICE,
                                                                BTM_INDICATOR,
                                                                DSM_ASK_SIZE,
                                                                DSM_ASK_PRICE,
                                                                DSM_BID_PRICE,
                                                                DSM_BID_SIZE};

    public static final short[] NON_DISPLAY_FIELD_TYPES = {USER_KEY,
                                                            SESSION_NAME,
                                                            SOURCE_FIELD,
                                                            SOURCE_FIELD_TYPE,
                                                            BULK_ORDER_REQ_ID};

                                                               // Format constants
    public static final String TRADERS_FORMAT = "TRADERS_FORMAT";
    /**
     * Format used to display the fields values for End User Booth personnel.
     */
    public static final String BOOTH_FORMAT = "BOOTH_FORMAT";

    public static final String INVALID_FORMAT = "ERROR: Invalid Format Specifier";
    public static final String INVALID_TYPE = "ERROR: Invalid Type Code";

//*** Private Attributes

    private static final String ACCOUNT_STRING = "Account";
    private static final String ASK_PRICE_STRING = "Ask Price";
    private static final String ASK_QTY_STRING = "Ask Quantity";
    private static final String BID_PRICE_STRING = "Bid Price";
    private static final String BID_QTY_STRING = "Bid Quantity";
    private static final String BOOKED_QUANTITY_STRING = "Booked Quantity";
    private static final String BUSTED_QUANTITY_STRING = "Busted Quantity";
    private static final String CANCEL_REASON_STRING = "Cancel Reason";
    private static final String CANCELLED_QUANTITY_STRING = "Cancelled Quantity";
    private static final String CMTA_STRING = "CMTA";
    private static final String CONTINGENCY_TYPE_STRING = "Contingency Type";
    private static final String EVENT_STATUS_STRING = "Event Status";
    private static final String IS_AUTO_LINKED_STRING = "Auto Linked";
    private static final String LEAVES_QUANTITY_STRING = "Leaves Quantity";
    private static final String MARKETIBILITY_INDICATOR_STRING = "Marketibility Indicator";
    private static final String MISMATCHED_QUANTITY_STRING = "Mismatched Quantity";
    private static final String OPTIONAL_DATA_STRING = "Optional Data";
    private static final String ORDER_STATE_STRING = "Order State";
    private static final String ORDERID_STRING = "Order ID";
    private static final String ORIGINAL_QUANTITY_STRING = "Original Quantity";
    private static final String PRICE_STRING = "Price";
    private static final String PRODUCT_STRING = "Product";
    private static final String PRODUCT_STATE_STRING = "Product State";
    private static final String QUANTITY_STRING = "Quantity";
    private static final String QUOTEKEY_STRING = "Quote Key";
    private static final String REINSTATED_QUANTITY_STRING = "Reinstated Quantity";
    private static final String REPLACE_ORDERID_STRING = "Replace Order ID";
    private static final String RFQ_TYPE_STRING = "RFQ Type";
    private static final String SIDE_STRING = "Side";
    private static final String SUBACCOUNT_STRING = "Sub Account";
    private static final String TIME_IN_FORCE_STRING = "Time in Force";
    private static final String TIME_TO_LIVE_STRING = "Time To Live";
    private static final String TLC_QUANTITY_STRING = "TLC Quantity";
    private static final String TRADED_QUANTITY_STRING = "Traded Quantity";
    private static final String TRADEID_STRING = "Trade ID";
    private static final String TRANSACTION_SEQUENCE_NUMBER_STRING = "Transaction Sequence Number";
    private static final String USER_ASSIGNED_ID_STRING = "User Assigned ID";
    private static final String EXEC_BROKER_STRING = "Exec Broker";
    private static final String QUOTE_UPDATE_CONTROL_ID_STRING = "Quote Update Control ID";

    //*** Public Methods

    /**
     * ************************************************************************** Returns a string representation of the
     * object in TRADERS_FORMAT format
     *
     * @param activityEvent - the activity event code to render (see defined constants)
     * @return a string representation of the activityEvent
     * @see com.cboe.idl.cmiConstants.ActivityFieldTypes
     */
    public static String toString(short activityEvent)
    {
        return toString(activityEvent, TRADERS_FORMAT);
    }


    /**
     * ************************************************************************** Returns a string representation of the
     * object in the given format
     *
     * @param activityEvent   - the activity event code to render (see defined constants)
     * @param formatSpecifier - a string that specifies how the object should format itself.
     * @return a string representation of the activityEvent
     * @see com.cboe.idl.cmiConstants.ActivityFieldTypes
     */
    @SuppressWarnings({"OverlyLongMethod", "OverlyComplexMethod", "MethodWithMultipleReturnPoints"})
    public static String toString(short activityEvent, String formatSpecifier)
    {
        if (formatSpecifier.equals(TRADERS_FORMAT))
        {
            switch (activityEvent)
            {
                case ACCOUNT:
                    return ACCOUNT_STRING;
                case ASK_PRICE:
                    return ASK_PRICE_STRING;
                case ASK_QTY:
                    return ASK_QTY_STRING;
                case BID_PRICE:
                    return BID_PRICE_STRING;
                case BID_QTY:
                    return BID_QTY_STRING;
                case BOOKED_QUANTITY:
                    return BOOKED_QUANTITY_STRING;
                case BUSTED_QUANTITY:
                    return BUSTED_QUANTITY_STRING;
                case CANCEL_REASON:
                    return CANCEL_REASON_STRING;
                case CANCELLED_QUANTITY:
                    return CANCELLED_QUANTITY_STRING;
                case CMTA:
                    return CMTA_STRING;
                case CONTINGENCY_TYPE:
                    return CONTINGENCY_TYPE_STRING;
                case EVENT_STATUS:
                    return EVENT_STATUS_STRING;
                case IS_AUTO_LINKED:
                    return IS_AUTO_LINKED_STRING;
                case LEAVES_QUANTITY:
                    return LEAVES_QUANTITY_STRING;
                case MARKETIBILITY_INDICATOR:
                    return MARKETIBILITY_INDICATOR_STRING;
                case MISMATCHED_QUANTITY:
                    return MISMATCHED_QUANTITY_STRING;
                case OPTIONAL_DATA:
                    return OPTIONAL_DATA_STRING;
                case ORDERID:
                    return ORDERID_STRING;
                case ORDER_STATE:
                    return ORDER_STATE_STRING;
                case ORIGINAL_QUANTITY:
                    return ORIGINAL_QUANTITY_STRING;
                case PRICE:
                    return PRICE_STRING;
                case PRODUCT:
                    return PRODUCT_STRING;
                case PRODUCT_STATE:
                    return PRODUCT_STATE_STRING;
                case QUANTITY:
                    return QUANTITY_STRING;
                case QUOTEKEY:
                    return QUOTEKEY_STRING;
                case REINSTATED_QUANTITY:
                    return REINSTATED_QUANTITY_STRING;
                case REPLACE_ORDERID:
                    return REPLACE_ORDERID_STRING;
                case RFQ_TYPE:
                    return RFQ_TYPE_STRING;
                case SIDE:
                    return SIDE_STRING;
                case SUB_ACCOUNT:
                    return SUBACCOUNT_STRING;
                case TIME_IN_FORCE:
                    return TIME_IN_FORCE_STRING;
                case TIME_TO_LIVE:
                    return TIME_TO_LIVE_STRING;
                case TLC_QUANTITY:
                    return TLC_QUANTITY_STRING;
                case TRADED_QUANTITY:
                    return TRADED_QUANTITY_STRING;
                case TRADEID:
                    return TRADEID_STRING;
                case TRANSACTION_SEQUENCE_NUMBER:
                    return TRANSACTION_SEQUENCE_NUMBER_STRING;
                case USER_ASSIGNED_ID:
                    return USER_ASSIGNED_ID_STRING;
                case EXEC_BROKER:
                    return EXEC_BROKER_STRING;
                case QUOTE_UPDATE_CONTROL_ID:
                    return QUOTE_UPDATE_CONTROL_ID_STRING;
                default:
//                    return INVALID_TYPE;
                    //noinspection StringBufferWithoutInitialCapacity
                    return new StringBuffer().append("[ ").append(activityEvent).append(" ]").toString();
            }
        }
        return INVALID_FORMAT;
    }

    /**
     * ************************************************************************** Returns a string representation of the
     * object in the given format
     *
     * @param activityEvent   - the activity event code to render (see defined constants)
     * @param formatSpecifier - a string that specifies how the object should format itself.
     * @param activityField   activity field
     * @return a string representation of the activityEvent
     * @see com.cboe.idl.cmiConstants.ActivityFieldTypes
     */
    @SuppressWarnings({"OverlyLongMethod", "OverlyComplexMethod"})
    public static String toString(short activityEvent, String activityField, String formatSpecifier)
    {
        StringBuilder result = new StringBuilder(50);
        String formattedField=null;
        boolean appendActivityField = true;
        try
        {
            if (TRADERS_FORMAT.equals(formatSpecifier))
            {
                switch (activityEvent)
                {
                    case ASK_PRICE:
                    case BID_PRICE:
                    case PRICE:
                    case BBO_BID_PRICE:
                    case BBO_ASK_PRICE:
                    case BOTR_ASK_PRICE:
                    case BOTR_BID_PRICE:
                    case DSM_BID_PRICE:
                    case DSM_ASK_PRICE:
                    case NBBO_ASK_PRICE:
                    case NBBO_BID_PRICE:
                    case BOOK_ASK_PRICE:
                    case BOOK_BID_PRICE:
                    case TSB_BID_PRICE:
                    case TSB_ASK_PRICE:
                        formattedField = getPrice(activityField);
                        break;
                    case CANCEL_REASON:
                        formattedField = FormatFactory.getCancelReasonFormatStrategy().format(Short.parseShort(activityField));
                        break;
                    case CONTINGENCY_TYPE:
                        formattedField = ContingencyTypes.toString(Short.parseShort(activityField));
                        break;
                    case ORDER_STATE:
                        formattedField = OrderStates.toString(Short.parseShort(activityField));
                        break;
                    case SIDE:
                        formattedField = Sides.toString(activityField.charAt(0), Sides.BUY_SELL_FORMAT);
                        appendActivityField = false;
                        break;
                    case TIME_IN_FORCE:
                        formattedField = TimesInForce.toString(activityField.charAt(0), TimesInForce.TRADERS_FORMAT);
                        break;
                    case LOCATION_TYPE:
                    case SOURCE_FIELD_TYPE:
                        formattedField = OrderLocation.findOrderLocationEnum(Short.parseShort(activityField)).description;
                        break;
                    case AUCTION_TYPE:
                        formattedField = AuctionTypes.toString(Short.parseShort(activityField));
                        break;
                    case ROUTE_REASON:
                        formattedField = RoutingReasonsFormatter.getString(Short.parseShort(activityField));
                        break;
                    case PRODUCT:
                        Product product = ProductHelper.getProduct(Integer.parseInt(activityField));
                        formattedField = FormatFactory.getProductFormatStrategy().format(product);
                        appendActivityField = false;
                        break;
                    case BTM_INDICATOR:
                    case DELTA_NEUTRAL_INDICATOR:
                    case TT_INDICATOR:
                        formattedField = FormatFactory.getBooleanFormatter().format(Short.parseShort(activityField));
                        break;
                    case EVENT_STATUS:
                        formattedField = FormatFactory.getErrorCodeFormatter().format(Short.parseShort(activityField));
                        break;
                    case COMPLEX_ORDER_TYPE:
                        formattedField = FormatFactory.getStrategyTypeFormatter().format(Short.parseShort(activityField));
                        break;
                    case RETURN_CODE:
                        formattedField = FormatFactory.getRoutingErrorFormatter().format(Short.parseShort(activityField));
                        break;
                    case ROUTED_QUANTITY:
                    case RELATED_ORDERDBID:
                    case RELATED_ORDER_FIRMNUMEBR:
                    case RELATED_ORDER_FIRMEXCHANGE:
                    case RELATED_ORDER_BRANCH:
                    case RELATED_ORDER_BRANCHSEQNUMBER:
                    case RELATED_ORDER_CORRESPONDENTFIRM:
                    case RELATED_ORDER_DATE:
                    case RELATED_ORDER_ORSID:
                        formattedField = activityField;
                        break;
                    case ORDERID:
                        int i = activityField.indexOf(':');
                        formattedField = activityField.substring(i + 1);
                        appendActivityField = false;
                        break;
                    case LOCATION:
                        formattedField = FormatFactory.getLocationFormatter().format(activityField);
                        break;
                    case SUBEVENT_TYPE:
                        formattedField = ActivitySubTypes.toString(Short.parseShort(activityField),
                                                                   TRADERS_FORMAT);
                        break;
                    default:
                        formattedField = "";
                        break;
                }
            }
            else if(BOOTH_FORMAT.equals(formatSpecifier))
            {
                switch(activityEvent)
                {
                    case AUCTION_TYPE:
                        formattedField = AuctionTypes.toString(Short.parseShort(activityField),
                                                               BOOTH_FORMAT);
                        appendActivityField = false;
                        break;
                    case LOCATION:
                        formattedField = FormatFactory.getLocationFormatter().format(activityField,
                                                                                     LocationFormatStrategy.LOCATION_ROUTE_DESTINATION);
                        appendActivityField = false;
                        break;
                    case SUBEVENT_TYPE:
                        formattedField = ActivitySubTypes
                                .toString(Short.parseShort(activityField), TRADERS_FORMAT);
                        appendActivityField = false;
                        break;
                    case RETURN_CODE:
                        formattedField = FormatFactory.getRoutingErrorFormatter()
                                .format(Short.parseShort(activityField));
                        appendActivityField = false;
                        break;
                    case RELATED_ORDER_CORRESPONDENTFIRM:
                    case RELATED_ORDER_BRANCH:
                    case RELATED_ORDER_FIRMNUMEBR:
                    case RELATED_ORDER_BRANCHSEQNUMBER:
                        formattedField = activityField;
                        appendActivityField = false;
                        break;
                    case CANCEL_REASON:
                        formattedField = FormatFactory.getCancelReasonFormatStrategy()
                                .format(Short.parseShort(activityField));
                        appendActivityField = false;
                        break;
                    default:
                        formattedField = activityField;
                        appendActivityField = false;
                }
            }
        }
        catch(NumberFormatException e)
        {
            GUILoggerHome.find()
                    .exception("Invalid Activity Field '" + activityField + "' received", e);
            formattedField = ' ' + activityField + ' ';
            appendActivityField = false;
        }
        if(formattedField == null || formattedField.length() < 1)
        {
            result.append(activityField);
        }
        else
        {
            result.append(formattedField);
            if(appendActivityField)
            {
                result.append(" (").append(activityField).append(')');
            }
        }
        return result.toString();
    }

    /**
     * This method checks whether or not priority could be enforced. 
     *
     * @param activityEvent activityEvent to check. 
     * @param activityField ActivityField value.
     * @return boolean
     */
    public static boolean isPriorityEnforceable(short activityEvent, String activityField){
        boolean result = true;
        if(activityEvent == ROUTE_REASON)
        {
            try
            {
                result = Short.parseShort(activityField) != 0;
            }
            catch(NumberFormatException e)
            {
                GUILoggerHome.find().exception("Invalid Activity Field '" + activityField + "' received", e);
                result = false;
            }
        }
        return result;
    }

//*** Private Methods

    private static String getPrice(String activityField)
    {
        String formattedPrice = "";
        Price price = DisplayPriceFactory.create(activityField);
        if(price.isNoPrice())
        {
            formattedPrice = "No Price";
        }
        return formattedPrice;
    }

    /**
     * ************************************************************************** Hide the default constructor from the
     * public interface
     */
    private ActivityFieldTypes()
    {
    }

}
