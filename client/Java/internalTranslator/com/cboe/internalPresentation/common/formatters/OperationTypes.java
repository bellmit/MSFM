//
// -----------------------------------------------------------------------------------
// Source file: OperationTypes.java
//
// PACKAGE: com.cboe.internalPresentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.common.formatters;

public class OperationTypes
{
    public static final short ADMINISTRATOR = com.cboe.idl.constants.OperationTypes.ADMINISTRATOR;
    public static final short DEFAULT_OPERATION = com.cboe.idl.constants.OperationTypes.DEFAULT_OPERATION;
    public static final short MARKETQUERY_BOOKDEPTH = com.cboe.idl.constants.OperationTypes.MARKETQUERY_BOOKDEPTH;
    public static final short MARKETQUERY_CURRENTMARKET = com.cboe.idl.constants.OperationTypes.MARKETQUERY_CURRENTMARKET;
    public static final short MARKETQUERY_EXPECTEDOPENINGPRICE = com.cboe.idl.constants.OperationTypes.MARKETQUERY_EXPECTEDOPENINGPRICE;
    public static final short MARKETQUERY_MARKETDATAHISTORY = com.cboe.idl.constants.OperationTypes.MARKETQUERY_MARKETDATAHISTORY;
    public static final short MARKETQUERY_NBBO = com.cboe.idl.constants.OperationTypes.MARKETQUERY_NBBO;
    public static final short MARKETQUERY_RECAP = com.cboe.idl.constants.OperationTypes.MARKETQUERY_RECAP;
    public static final short MARKETQUERY_TICKER = com.cboe.idl.constants.OperationTypes.MARKETQUERY_TICKER;
    public static final short ORDERENTRY_LIGHTORDER = com.cboe.idl.constants.OperationTypes.ORDERENTRY_LIGHTORDER;
    public static final short ORDERENTRY_ORDER = com.cboe.idl.constants.OperationTypes.ORDERENTRY_ORDER;
    public static final short ORDERENTRY_RFQ = com.cboe.idl.constants.OperationTypes.ORDERENTRY_RFQ;
    public static final short ORDERQUERY = com.cboe.idl.constants.OperationTypes.ORDERQUERY;
    public static final short PRODUCTDEFINITION = com.cboe.idl.constants.OperationTypes.PRODUCTDEFINITION;
    public static final short PRODUCTQUERY = com.cboe.idl.constants.OperationTypes.PRODUCTQUERY;
    public static final short QUOTE_QUOTEENTRY = com.cboe.idl.constants.OperationTypes.QUOTE_QUOTEENTRY;
    public static final short QUOTE_QUOTESTATUS = com.cboe.idl.constants.OperationTypes.QUOTE_QUOTESTATUS;
    public static final short QUOTE_RFQ = com.cboe.idl.constants.OperationTypes.QUOTE_RFQ;
    public static final short TRADINGSESSION = com.cboe.idl.constants.OperationTypes.TRADINGSESSION;
    public static final short USERHISTORY = com.cboe.idl.constants.OperationTypes.USERHISTORY;
    public static final short USERPREFERENCEQUERY = com.cboe.idl.constants.OperationTypes.USERPREFERENCEQUERY;
    public static final short USERTRADINGPARAMETERS = com.cboe.idl.constants.OperationTypes.USERTRADINGPARAMETERS;
    public static final short INTERMARKETQUERY = com.cboe.idl.constants.OperationTypes.INTERMARKETQUERY;
    public static final short INTERMARKET_MANUALHANDLING = com.cboe.idl.constants.OperationTypes.INTERMARKET_MANUALHANDLING;
    public static final short DYNAMIC_BOOKDEPTH = com.cboe.idl.constants.OperationTypes.DYNAMIC_BOOKDEPTH;
    public static final short MARKETQUERY_DETAILMDHISTORY = com.cboe.idl.constants.OperationTypes.MARKETQUERY_DETAILMDHISTORY;
    public static final short MARKETQUERY_PRIORITYMDHISTORY = com.cboe.idl.constants.OperationTypes.MARKETQUERY_PRIORITYMDHISTORY;
    public static final short AUCTION = com.cboe.idl.constants.OperationTypes.AUCTION;
    public static final short EFPBLOCKTRADE = com.cboe.idl.constants.OperationTypes.EFPBLOCKTRADE;

    public static final short[] ALL_TYPES = {DEFAULT_OPERATION, ADMINISTRATOR, MARKETQUERY_BOOKDEPTH, DYNAMIC_BOOKDEPTH,
                                             MARKETQUERY_CURRENTMARKET, MARKETQUERY_EXPECTEDOPENINGPRICE,
                                             MARKETQUERY_MARKETDATAHISTORY, MARKETQUERY_DETAILMDHISTORY,
                                             MARKETQUERY_PRIORITYMDHISTORY, MARKETQUERY_NBBO, MARKETQUERY_RECAP,
                                             MARKETQUERY_TICKER, ORDERENTRY_ORDER, ORDERENTRY_RFQ, ORDERENTRY_LIGHTORDER, ORDERQUERY,
                                             PRODUCTDEFINITION, PRODUCTQUERY, QUOTE_QUOTEENTRY, QUOTE_QUOTESTATUS,
                                             QUOTE_RFQ, TRADINGSESSION, INTERMARKETQUERY, INTERMARKET_MANUALHANDLING,
                                             USERHISTORY, USERPREFERENCEQUERY, USERTRADINGPARAMETERS, AUCTION, EFPBLOCKTRADE};

    public static final String FULL_FORMAT = "FULL_FORMAT";
    public static final String SHORT_FORMAT = "SHORT_FORMAT";
    public static final String INVALID_FORMAT = "INVALID_FORMAT";
    public static final String INVALID_TYPE   = "INVALID_TYPE:";

    public static final String ADMINISTRATOR_FULL_STRING = "Administrator";
    public static final String DEFAULT_OPERATION_FULL_STRING = "Default";
    public static final String MARKETQUERY_BOOKDEPTH_FULL_STRING = "Book Depth";
    public static final String MARKETQUERY_CURRENTMARKET_FULL_STRING = "Current Market";
    public static final String MARKETQUERY_EXPECTEDOPENINGPRICE_FULL_STRING = "Expected Opening Price";
    public static final String MARKETQUERY_MARKETDATAHISTORY_FULL_STRING = "Market Data History";
    public static final String MARKETQUERY_NBBO_FULL_STRING = "NBBO";
    public static final String MARKETQUERY_RECAP_FULL_STRING = "Recap";
    public static final String MARKETQUERY_TICKER_FULL_STRING = "Ticker";
    public static final String ORDERENTRY_LIGHTORDER_FULL_STRING = "Light Order Entry";
    public static final String ORDERENTRY_ORDER_FULL_STRING = "Order Entry";
    public static final String ORDERENTRY_RFQ_FULL_STRING = "Order Entry RFQ";
    public static final String ORDERQUERY_FULL_STRING = "Order Query";
    public static final String PRODUCTDEFINITION_FULL_STRING = "Product Definition";
    public static final String PRODUCTQUERY_FULL_STRING = "Product Query";
    public static final String QUOTE_QUOTEENTRY_FULL_STRING = "Quote Entry";
    public static final String QUOTE_QUOTESTATUS_FULL_STRING = "Quote Status";
    public static final String QUOTE_RFQ_FULL_STRING = "Quote RFQ";
    public static final String TRADINGSESSION_FULL_STRING = "Trading Session";
    public static final String USERHISTORY_FULL_STRING = "User History";
    public static final String USERPREFERENCEQUERY_FULL_STRING = "User Preference";
    public static final String USERTRADINGPARAMETERS_FULL_STRING = "User Trading Parameters";
    public static final String INTERMARKETQUERY_FULL_STRING = "Intermarket Query";
    public static final String INTERMARKET_MANUALHANDLING_FULL_STRING = "Intermarket Manual Handling";
    public static final String DYNAMIC_BOOKDEPTH_FULL_STRING = "Book Depth Updates";
    public static final String MARKETQUERY_DETAILMDHISTORY_FULL_STRING = "Detail Market Data History";
    public static final String MARKETQUERY_PRIORITYMDHISTORY_FULL_STRING = "Priority Market Data History";
    public static final String AUCTION_FULL_STRING = "Auction";
    public static final String EFPBLOCKTRADE_FULL_STRING = "EFP/Block Trade";
    
    public static final String ADMINISTRATOR_SHORT_STRING = "Admin";
    public static final String DEFAULT_OPERATION_SHORT_STRING = "Deflt";
    public static final String MARKETQUERY_BOOKDEPTH_SHORT_STRING = "BkDpth";
    public static final String MARKETQUERY_CURRENTMARKET_SHORT_STRING = "CurrMkt";
    public static final String MARKETQUERY_EXPECTEDOPENINGPRICE_SHORT_STRING = "ExpOpen $";
    public static final String MARKETQUERY_MARKETDATAHISTORY_SHORT_STRING = "MktHis";
    public static final String MARKETQUERY_NBBO_SHORT_STRING = "NBBO";
    public static final String MARKETQUERY_RECAP_SHORT_STRING = "Recap";
    public static final String MARKETQUERY_TICKER_SHORT_STRING = "Ticker";
    public static final String ORDERENTRY_LIGHTORDER_SHORT_STRING = "LtOrdEtry";
    public static final String ORDERENTRY_ORDER_SHORT_STRING = "OrdEtry";
    public static final String ORDERENTRY_RFQ_SHORT_STRING = "OrdRFQ";
    public static final String ORDERQUERY_SHORT_STRING = "OrdQry";
    public static final String PRODUCTDEFINITION_SHORT_STRING = "ProdDef";
    public static final String PRODUCTQUERY_SHORT_STRING = "ProdQry";
    public static final String QUOTE_QUOTEENTRY_SHORT_STRING = "QteEntry";
    public static final String QUOTE_QUOTESTATUS_SHORT_STRING = "QteStat";
    public static final String QUOTE_RFQ_SHORT_STRING = "QteRFQ";
    public static final String TRADINGSESSION_SHORT_STRING = "TradSess";
    public static final String USERHISTORY_SHORT_STRING = "UsrHist";
    public static final String USERPREFERENCEQUERY_SHORT_STRING = "Pref";
    public static final String USERTRADINGPARAMETERS_SHORT_STRING = "TradParm";
    public static final String INTERMARKETQUERY_SHORT_STRING = "IntmktQry";
    public static final String INTERMARKET_MANUALHANDLING_SHORT_STRING = "IntmktManHdl";
    public static final String DYNAMIC_BOOKDEPTH_SHORT_STRING = "BkDpthUpdt";
    public static final String MARKETQUERY_DETAILMDHISTORY_SHORT_STRING = "DtlMktHis";
    public static final String MARKETQUERY_PRIORITYMDHISTORY_SHORT_STRING = "PrtyMktHis";
    public static final String AUCTION_SHORT_STRING = "Auction";
    public static final String EFPBLOCKTRADE_SHORT_STRING = "EFPBLK";

    private OperationTypes ()
    {}

    public static boolean validateOperationType(short operationType)
    {
        switch( operationType )
        {
            case ADMINISTRATOR:
            case DEFAULT_OPERATION:
            case MARKETQUERY_BOOKDEPTH:
            case MARKETQUERY_CURRENTMARKET:
            case MARKETQUERY_EXPECTEDOPENINGPRICE:
            case MARKETQUERY_MARKETDATAHISTORY:
            case MARKETQUERY_NBBO:
            case MARKETQUERY_RECAP:
            case MARKETQUERY_TICKER:
            case ORDERENTRY_LIGHTORDER:
            case ORDERENTRY_ORDER:
            case ORDERENTRY_RFQ:
            case ORDERQUERY:
            case PRODUCTDEFINITION:
            case PRODUCTQUERY:
            case QUOTE_QUOTEENTRY:
            case QUOTE_QUOTESTATUS:
            case QUOTE_RFQ:
            case TRADINGSESSION:
            case USERHISTORY:
            case USERPREFERENCEQUERY:
            case USERTRADINGPARAMETERS:
            case INTERMARKETQUERY:
            case INTERMARKET_MANUALHANDLING:
            case DYNAMIC_BOOKDEPTH:
            case MARKETQUERY_DETAILMDHISTORY:
            case MARKETQUERY_PRIORITYMDHISTORY:
            case AUCTION:
            case EFPBLOCKTRADE:	
                return true;
            default:
                return false;
        }
    }

    public static String toString(short operationType)
    {
        return toString(operationType, FULL_FORMAT);
    }

    public static String toString(short operationType, String formatSpecifier)
    {
        if(formatSpecifier.equals(FULL_FORMAT))
        {
            switch ( operationType )
            {
                case ADMINISTRATOR:
                    return ADMINISTRATOR_FULL_STRING;
                case DEFAULT_OPERATION:
                    return DEFAULT_OPERATION_FULL_STRING;
                case MARKETQUERY_BOOKDEPTH:
                    return MARKETQUERY_BOOKDEPTH_FULL_STRING;
                case MARKETQUERY_CURRENTMARKET:
                    return MARKETQUERY_CURRENTMARKET_FULL_STRING;
                case MARKETQUERY_EXPECTEDOPENINGPRICE:
                    return MARKETQUERY_EXPECTEDOPENINGPRICE_FULL_STRING;
                case MARKETQUERY_MARKETDATAHISTORY:
                    return MARKETQUERY_MARKETDATAHISTORY_FULL_STRING;
                case MARKETQUERY_NBBO:
                    return MARKETQUERY_NBBO_FULL_STRING;
                case MARKETQUERY_RECAP:
                    return MARKETQUERY_RECAP_FULL_STRING;
                case MARKETQUERY_TICKER:
                    return MARKETQUERY_TICKER_FULL_STRING;
                case ORDERENTRY_LIGHTORDER:
                	return ORDERENTRY_LIGHTORDER_FULL_STRING;
                case ORDERENTRY_ORDER:
                    return ORDERENTRY_ORDER_FULL_STRING;
                case ORDERENTRY_RFQ:
                    return ORDERENTRY_RFQ_FULL_STRING;
                case ORDERQUERY:
                    return ORDERQUERY_FULL_STRING;
                case PRODUCTDEFINITION:
                    return PRODUCTDEFINITION_FULL_STRING;
                case PRODUCTQUERY:
                    return PRODUCTQUERY_FULL_STRING;
                case QUOTE_QUOTEENTRY:
                    return QUOTE_QUOTEENTRY_FULL_STRING;
                case QUOTE_QUOTESTATUS:
                    return QUOTE_QUOTESTATUS_FULL_STRING;
                case QUOTE_RFQ:
                    return QUOTE_RFQ_FULL_STRING;
                case TRADINGSESSION:
                    return TRADINGSESSION_FULL_STRING;
                case USERHISTORY:
                    return USERHISTORY_FULL_STRING;
                case USERPREFERENCEQUERY:
                    return USERPREFERENCEQUERY_FULL_STRING;
                case USERTRADINGPARAMETERS:
                    return USERTRADINGPARAMETERS_FULL_STRING;
                case INTERMARKETQUERY:
                    return INTERMARKETQUERY_FULL_STRING;
                case INTERMARKET_MANUALHANDLING:
                    return INTERMARKET_MANUALHANDLING_FULL_STRING;
                case DYNAMIC_BOOKDEPTH:
                    return DYNAMIC_BOOKDEPTH_FULL_STRING;
                case MARKETQUERY_DETAILMDHISTORY:
                    return MARKETQUERY_DETAILMDHISTORY_FULL_STRING;
                case MARKETQUERY_PRIORITYMDHISTORY:
                    return MARKETQUERY_PRIORITYMDHISTORY_FULL_STRING;
                case AUCTION:
                    return AUCTION_FULL_STRING;
                case EFPBLOCKTRADE:
                	return EFPBLOCKTRADE_FULL_STRING;
                default:
                    return new StringBuffer(30).append(INVALID_TYPE).append(' ').append(operationType).toString();
            }
        }
        else if(formatSpecifier.equals(SHORT_FORMAT))
        {
            switch( operationType )
            {
                case ADMINISTRATOR:
                    return ADMINISTRATOR_SHORT_STRING;
                case DEFAULT_OPERATION:
                    return DEFAULT_OPERATION_SHORT_STRING;
                case MARKETQUERY_BOOKDEPTH:
                    return MARKETQUERY_BOOKDEPTH_SHORT_STRING;
                case MARKETQUERY_CURRENTMARKET:
                    return MARKETQUERY_CURRENTMARKET_SHORT_STRING;
                case MARKETQUERY_EXPECTEDOPENINGPRICE:
                    return MARKETQUERY_EXPECTEDOPENINGPRICE_SHORT_STRING;
                case MARKETQUERY_MARKETDATAHISTORY:
                    return MARKETQUERY_MARKETDATAHISTORY_SHORT_STRING;
                case MARKETQUERY_NBBO:
                    return MARKETQUERY_NBBO_SHORT_STRING;
                case MARKETQUERY_RECAP:
                    return MARKETQUERY_RECAP_SHORT_STRING;
                case MARKETQUERY_TICKER:
                    return MARKETQUERY_TICKER_SHORT_STRING;
                case ORDERENTRY_LIGHTORDER:
                	return ORDERENTRY_LIGHTORDER_SHORT_STRING;
                case ORDERENTRY_ORDER:
                    return ORDERENTRY_ORDER_SHORT_STRING;
                case ORDERENTRY_RFQ:
                    return ORDERENTRY_RFQ_SHORT_STRING;
                case ORDERQUERY:
                    return ORDERQUERY_SHORT_STRING;
                case PRODUCTDEFINITION:
                    return PRODUCTDEFINITION_SHORT_STRING;
                case PRODUCTQUERY:
                    return PRODUCTQUERY_SHORT_STRING;
                case QUOTE_QUOTEENTRY:
                    return QUOTE_QUOTEENTRY_SHORT_STRING;
                case QUOTE_QUOTESTATUS:
                    return QUOTE_QUOTESTATUS_SHORT_STRING;
                case QUOTE_RFQ:
                    return QUOTE_RFQ_SHORT_STRING;
                case TRADINGSESSION:
                    return TRADINGSESSION_SHORT_STRING;
                case USERHISTORY:
                    return USERHISTORY_SHORT_STRING;
                case USERPREFERENCEQUERY:
                    return USERPREFERENCEQUERY_SHORT_STRING;
                case USERTRADINGPARAMETERS:
                    return USERTRADINGPARAMETERS_SHORT_STRING;
                case INTERMARKETQUERY:
                    return INTERMARKETQUERY_SHORT_STRING;
                case INTERMARKET_MANUALHANDLING:
                    return INTERMARKET_MANUALHANDLING_SHORT_STRING;
                case DYNAMIC_BOOKDEPTH:
                    return DYNAMIC_BOOKDEPTH_SHORT_STRING;
                case MARKETQUERY_DETAILMDHISTORY:
                    return MARKETQUERY_DETAILMDHISTORY_SHORT_STRING;
                case MARKETQUERY_PRIORITYMDHISTORY:
                    return MARKETQUERY_PRIORITYMDHISTORY_SHORT_STRING;
                case AUCTION:
                    return AUCTION_SHORT_STRING;
                case EFPBLOCKTRADE:
                	return EFPBLOCKTRADE_SHORT_STRING;    
                default:
                    return new StringBuffer(30).append(INVALID_TYPE).append(' ').append(operationType).toString();
            }
        }
        return new StringBuffer(30).append(INVALID_FORMAT).append(' ').append(formatSpecifier).toString();
    }
}