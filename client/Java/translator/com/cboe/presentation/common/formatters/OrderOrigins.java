//
// -----------------------------------------------------------------------------------
// Source file: OrderOrigins.java
//
// PACKAGE: com.cboe.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

public class OrderOrigins
{
    // Order Origins (mapping to com.cboe.idl.cmiConstants.OrderOrigins)
    public  static final char BROKER_DEALER = com.cboe.idl.cmiConstants.OrderOrigins.BROKER_DEALER;
    public  static final char CUSTOMER = com.cboe.idl.cmiConstants.OrderOrigins.CUSTOMER;
    public  static final char CUSTOMER_BROKER_DEALER = com.cboe.idl.cmiConstants.OrderOrigins.CUSTOMER_BROKER_DEALER;
    public  static final char FIRM = com.cboe.idl.cmiConstants.OrderOrigins.FIRM;
    public  static final char MARKET_MAKER = com.cboe.idl.cmiConstants.OrderOrigins.MARKET_MAKER;
    public  static final char MARKET_MAKER_AWAY = com.cboe.idl.cmiConstants.OrderOrigins.MARKET_MAKER_AWAY;
    public  static final char CTI1ORIGIN1 = com.cboe.idl.cmiConstants.OrderOrigins.CTI1Origin1;
    public  static final char CTI1ORIGIN2 = com.cboe.idl.cmiConstants.OrderOrigins.CTI1Origin2;
    public  static final char CTI1ORIGIN5 = com.cboe.idl.cmiConstants.OrderOrigins.CTI1Origin5;
    public  static final char CTI3ORIGIN1 = com.cboe.idl.cmiConstants.OrderOrigins.CTI3Origin1;
    public  static final char CTI3ORIGIN2 = com.cboe.idl.cmiConstants.OrderOrigins.CTI3Origin2;
    public  static final char CTI3ORIGIN5 = com.cboe.idl.cmiConstants.OrderOrigins.CTI3Origin5;
    public  static final char CTI4ORIGIN2 = com.cboe.idl.cmiConstants.OrderOrigins.CTI4Origin2;
    public  static final char CTI4ORIGIN5 = com.cboe.idl.cmiConstants.OrderOrigins.CTI4Origin5;
    // Special "MARKET MAKER" origin types for Linkage

    public static final char PRINCIPAL = com.cboe.idl.cmiConstants.OrderOrigins.PRINCIPAL;
    public static final char PRINCIPAL_ACTING_AS_AGENT = com.cboe.idl.cmiConstants.OrderOrigins.PRINCIPAL_ACTING_AS_AGENT;
    public static final char SATISFACTION = com.cboe.idl.cmiConstants.OrderOrigins.SATISFACTION;
    public static final char BROKER_DEALER_FBW_NON_CUSTOMER = com.cboe.idl.cmiConstants.OrderOrigins.BROKER_DEALER_FBW_NON_CUSTOMER;

    // NOTE: The UNDERLYING_SECURITY_SPECIALIST and FACILITATION origin codes are NOT defined in IDL
//    public  static final char UNDERLYING_SECURITY_SPECIALIST = 'Y';
//    public  static final char FACILITATION = 'I';
    // now that they are in the IDL...
    public static final char UNDERLY_SPECIALIST = com.cboe.idl.cmiConstants.OrderOrigins.UNDERLY_SPECIALIST;
    public static final char MARKET_MAKER_IN_CROWD = com.cboe.idl.cmiConstants.OrderOrigins.MARKET_MAKER_IN_CROWD;
    public static final char UNDERLYING_SECURITY_SPECIALIST = UNDERLY_SPECIALIST;
    public static final char FACILITATION = MARKET_MAKER_IN_CROWD;

    public static final short[] ALL_TYPES = { BROKER_DEALER, CUSTOMER, CUSTOMER_BROKER_DEALER, FIRM, MARKET_MAKER, MARKET_MAKER_AWAY,
                                              CTI1ORIGIN1, CTI1ORIGIN2, CTI1ORIGIN5, CTI3ORIGIN1, CTI3ORIGIN2, CTI3ORIGIN5,
                                              CTI4ORIGIN2, CTI4ORIGIN5, PRINCIPAL, PRINCIPAL_ACTING_AS_AGENT, SATISFACTION,
                                              UNDERLY_SPECIALIST, MARKET_MAKER_IN_CROWD};

    private static final String BROKER_DEALER_STRING = "Broker/Dealer";
    private static final String CUSTOMER_STRING = "Customer";
    private static final String CUSTOMER_BROKER_DEALER_STRING = "Customer Broker/Dealer";
    private static final String FIRM_STRING = "Firm";
    private static final String MARKET_MAKER_STRING = "Market Maker";
    private static final String MARKET_MAKER_AWAY_STRING = "Market Maker Away";
    private static final String CTI1ORIGIN1_STRING = "Member, Customer Segregated Account";
    private static final String CTI1ORIGIN2_STRING = "Member, House Account";
    private static final String CTI1ORIGIN5_STRING = "Member, SIPC Protected Account";
    private static final String CTI3ORIGIN1_STRING = "User Proxy for trader, Customer Segregated Account";
    private static final String CTI3ORIGIN2_STRING = "User Proxy for trader, House Account";
    private static final String CTI3ORIGIN5_STRING = "User Proxy for trader, SIPC Protected Account";
    private static final String CTI4ORIGIN2_STRING = "Non Member, House Account";
    private static final String CTI4ORIGIN5_STRING = "Non Member, SIPC Protected Account";
    private static final String PRINCIPAL_STRING = "Principal";
    private static final String PRINCIPAL_ACTING_AS_AGENT_STRING = "Principal Acting as Agent";
    private static final String SATISFACTION_STRING = "Satisfaction";
    private static final String BROKER_DEALER_FBW_NON_CUSTOMER_STRING = "Professional Customer";
    private static final String EMPTY_STRING = "";

    private static final String UNDERLY_SPECIALIST_STRING = "Underlying Security Specialist";
    private static final String MARKET_MARKER_IN_CROWD_STRING = "ICM";

    // Format constants
    public static final String TRADERS_FORMAT = new String("TRADERS_FORMAT");
    public static final String INVALID_FORMAT = "INVALID_FORMAT";
    public static final String INVALID_TYPE   = "INVALID_TYPE";

    public static final String CODE_FORMAT = new String("CODE_FORMAT");
    public static final String DROP_COPY_FORMAT = new String("DROP_COPY_FORMAT");

    /**
     * Hide the default constructor from the public interface
     */
    private OrderOrigins()
    {}

    /**
     * Returns a string representation of the object in TRADERS_FORMAT format
     * @param type - the order state code to render (see defined constants)
     * @return a string representation of the OrderOrigins
     * @see com.cboe.idl.cmiConstants.OrderOrigins
     */
    public static String toString(char type)
    {
        return toString(type, TRADERS_FORMAT);
    }

    /**
     * Returns a string representation of the object in the given format
     * @param type code to render (see defined constants)
     * @param formatSpecifier a string that specifies how the object should format itself.
     * @return a string representation of the product type
     * @see com.cboe.idl.cmiConstants.OrderOrigins
     */
    public static String toString(char type, String formatSpecifier)
    {
        if(formatSpecifier.equals(TRADERS_FORMAT))
        {
            return toTraderString(type);
        }
        else if(formatSpecifier.equals(CODE_FORMAT))
        {
            return toCodeString(type);
        }
        else if(formatSpecifier.equals(DROP_COPY_FORMAT))
        {
            return toDropCopyString(type);
        }
        return INVALID_FORMAT;
    }

    private static String toCodeString(char type)
    {
        // start with the type in uppercase
        StringBuffer sb = new StringBuffer(String.valueOf(type).toUpperCase());
        String traderRepresentation = toTraderString(type);
        if(!traderRepresentation.equals(INVALID_TYPE) &&
                !traderRepresentation.equals(Character.toString(type)))
        {
            // add the trader representation
            sb.append(" - ").append(traderRepresentation);
        }
        return sb.toString();
    }

    private static String toTraderString(char type)
    {
        switch(type)
            {
                case BROKER_DEALER:
                return BROKER_DEALER_STRING;
            case CUSTOMER:
                return CUSTOMER_STRING;
            case CUSTOMER_BROKER_DEALER:
                return CUSTOMER_BROKER_DEALER_STRING;
            case FIRM:
                return FIRM_STRING;
            case MARKET_MAKER:
                return MARKET_MAKER_STRING;
            case MARKET_MAKER_AWAY:
                return MARKET_MAKER_AWAY_STRING;
            case CTI1ORIGIN1:
                return CTI1ORIGIN1_STRING;
            case CTI1ORIGIN2:
                return CTI1ORIGIN2_STRING;
            case CTI1ORIGIN5:
                return CTI1ORIGIN5_STRING;
            case CTI3ORIGIN1:
                return CTI3ORIGIN1_STRING;
            case CTI3ORIGIN2:
                return CTI3ORIGIN2_STRING;
            case CTI3ORIGIN5:
                return CTI3ORIGIN5_STRING;
            case CTI4ORIGIN2:
                return CTI4ORIGIN2_STRING;
            case CTI4ORIGIN5:
                return CTI4ORIGIN5_STRING;
            case UNDERLY_SPECIALIST:
                return UNDERLY_SPECIALIST_STRING;
            case MARKET_MAKER_IN_CROWD:
                return MARKET_MARKER_IN_CROWD_STRING;
            case PRINCIPAL:
                return PRINCIPAL_STRING;
            case PRINCIPAL_ACTING_AS_AGENT:
                return PRINCIPAL_ACTING_AS_AGENT_STRING;
            case SATISFACTION:
                return SATISFACTION_STRING;
            case BROKER_DEALER_FBW_NON_CUSTOMER:
                return BROKER_DEALER_FBW_NON_CUSTOMER_STRING;
                
            default:
                return Character.toString(type);
        }
    }

    private static String toDropCopyString(char type)
    {
        switch(type)
        {
            case BROKER_DEALER:
                return "BD";
            case CUSTOMER:
                return "Cust";
            case FIRM:
                return "Firm";
            case MARKET_MAKER:
                return "MM";
            case MARKET_MAKER_AWAY:
                return "AwayMM";
            case MARKET_MAKER_IN_CROWD:
                return "InCrowdMM";
            default:
                return Character.toString(type);
        }
    }
}
