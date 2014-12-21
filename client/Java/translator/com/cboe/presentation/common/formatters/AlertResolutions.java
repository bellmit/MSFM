/**
 * Copyright 2000-2002 (C) Chicago Board Options Exchange
 * Package: com.cboe.presentation.common.formatters
 * User: torresl
 * Date: Jan 9, 2003 11:29:00 AM
 */
package com.cboe.presentation.common.formatters;

import java.util.Hashtable;

public class AlertResolutions
{

    public static final String NOT_RESOLVED = com.cboe.idl.cmiConstants.AlertResolutions.NOT_RESOLVED;
    public static final String ADJUSTED = com.cboe.idl.cmiConstants.AlertResolutions.ADJUSTED;
    public static final String PARTIAL_PRICE_ADJUSTMENT = com.cboe.idl.cmiConstants.AlertResolutions.PARTIAL_PRICE_ADJUSTMENT;
    public static final String PARTIAL_QUANTITY_ADJ = com.cboe.idl.cmiConstants.AlertResolutions.PARTIAL_QUANTITY_ADJ;
    public static final String CONTRA_UNAVAILABLE = com.cboe.idl.cmiConstants.AlertResolutions.CONTRA_UNAVAILABLE;
    public static final String DELAYED_REPORT = com.cboe.idl.cmiConstants.AlertResolutions.DELAYED_REPORT;
    public static final String ERRONEOUS_REPORT = com.cboe.idl.cmiConstants.AlertResolutions.ERRONEOUS_REPORT;
    public static final String FIRM_DISCRETION = com.cboe.idl.cmiConstants.AlertResolutions.FIRM_DISCRETION;
    public static final String EXECUTED_UNDER_FIRM_INSTRUCTIONS = com.cboe.idl.cmiConstants.AlertResolutions.EXECUTED_UNDER_FIRM_INSTRUCTIONS;
    public static final String FAST_MARKET_AWAY = com.cboe.idl.cmiConstants.AlertResolutions.FAST_MARKET_AWAY;
    public static final String FLASH_QUOTE = com.cboe.idl.cmiConstants.AlertResolutions.FLASH_QUOTE;
    public static final String AWAY_MARKET_UNAVAIL_TO_TRADE = com.cboe.idl.cmiConstants.AlertResolutions.AWAY_MARKET_UNAVAIL_TO_TRADE;
    public static final String NBBO_LOCKED_W_CBOE = com.cboe.idl.cmiConstants.AlertResolutions.NBBO_LOCKED_W_CBOE;
    public static final String NBBO_FADE = com.cboe.idl.cmiConstants.AlertResolutions.NBBO_FADE;
    public static final String AWAY_MARKET_REFUSE_TO_TRADE_OR_FADE = com.cboe.idl.cmiConstants.AlertResolutions.AWAY_MARKET_REFUSE_TO_TRADE_OR_FADE;
    public static final String OTHER = com.cboe.idl.cmiConstants.AlertResolutions.OTHER;
    public static final String POST_TRADE_QUOTE = com.cboe.idl.cmiConstants.AlertResolutions.POST_TRADE_QUOTE;
    public static final String SHUT_OFF_ERROR = com.cboe.idl.cmiConstants.AlertResolutions.SHUT_OFF_ERROR;
    public static final String SINGLE_LISTED_OPTION = com.cboe.idl.cmiConstants.AlertResolutions.SINGLE_LISTED_OPTION;
    public static final String CBOE_SYSTEM_PROBLEMS = com.cboe.idl.cmiConstants.AlertResolutions.CBOE_SYSTEM_PROBLEMS;
    public static final String TRADE_BUSTED = com.cboe.idl.cmiConstants.AlertResolutions.TRADE_BUSTED;
    public static final String NOT_ADJUSTED = com.cboe.idl.cmiConstants.AlertResolutions.NOT_ADJUSTED;
    public static final String BOOK_TAKEN_OUT_AFTER_NOTIFICATION = com.cboe.idl.cmiConstants.AlertResolutions.BOOK_TAKEN_OUT_AFTER_NOTIFICATION;
    public static final String TRADE_ENTERED_ON_REFRESHED_QUOTE = com.cboe.idl.cmiConstants.AlertResolutions.TRADE_ENTERED_ON_REFRESHED_QUOTE;

    public static final String NOT_RESOLVED_STRING = "NOT RESOLVED";
    public static final String ADJUSTED_STRING = "ADJUSTED";
    public static final String PARTIAL_PRICE_ADJUSTMENT_STRING = "PARTIAL PRICE ADJUSTMENT";
    public static final String PARTIAL_QUANTITY_ADJ_STRING = "PARTIAL QUANTITY ADJ";
    public static final String CONTRA_UNAVAILABLE_STRING = "CONTRA UNAVAILABLE";
    public static final String DELAYED_REPORT_STRING = "DELAYED REPORT";
    public static final String ERRONEOUS_REPORT_STRING = "ERRONEOUS REPORT";
    public static final String FIRM_DISCRETION_STRING = "FIRM DISCRETION";
    public static final String EXECUTED_UNDER_FIRM_INSTRUCTIONS_STRING = "EXECUTED UNDER FIRM INSTRUCTIONS";
    public static final String FAST_MARKET_AWAY_STRING = "FAST MARKET AWAY";
    public static final String FLASH_QUOTE_STRING = "FLASH QUOTE";
    public static final String AWAY_MARKET_UNAVAIL_TO_TRADE_STRING = "AWAY MARKET UNAVAIL TO TRADE";
    public static final String NBBO_LOCKED_W_CBOE_STRING = "NBBO LOCKED W/CBOE";
    public static final String NBBO_FADE_STRING = "NBBO FADE";
    public static final String AWAY_MARKET_REFUSE_TO_TRADE_OR_FADE_STRING = "AWAY MARKET REFUSE TO TRADE OR FADE";
    public static final String OTHER_STRING = "OTHER";
    public static final String POST_TRADE_QUOTE_STRING = "POST-TRADE QUOTE";
    public static final String SHUT_OFF_ERROR_STRING = "SHUT-OFF ERROR";
    public static final String SINGLE_LISTED_OPTION_STRING = "SINGLE LISTED OPTION";
    public static final String CBOE_SYSTEM_PROBLEMS_STRING = "CBOE SYSTEM PROBLEMS";
    public static final String TRADE_BUSTED_STRING = "TRADE BUSTED";
    public static final String NOT_ADJUSTED_STRING = "NOT ADJUSTED";
    public static final String BOOK_TAKEN_OUT_AFTER_NOTIFICATION_STRING = "BOOK TAKEN OUT AFTER NOTIFICATION";
    public static final String TRADE_ENTERED_ON_REFRESHED_QUOTE_STRING = "TRADE ENTERED ON REFRESHED QUOTE";
    public static final String NOT_RESOLVED_DESCRIPTION = "Not Resolved";
    public static final String ADJUSTED_DESCRIPTION = "Adjusted to NBBO";
    public static final String PARTIAL_PRICE_ADJUSTMENT_DESCRIPTION = "Partial Price Adjustment";
    public static final String PARTIAL_QUANTITY_ADJ_DESCRIPTION = "Partial Quantity Adjusted";
    public static final String CONTRA_UNAVAILABLE_DESCRIPTION = "OPP-broker unavailable, unable to seek adjustment";
    public static final String DELAYED_REPORT_DESCRIPTION = "Executed at NBBO, fill report delayed";
    public static final String ERRONEOUS_REPORT_DESCRIPTION = "Execution report sent in error.  No trade occurred";
    public static final String FIRM_DISCRETION_DESCRIPTION = "Firm made decision to execute at trade price";
    public static final String EXECUTED_UNDER_FIRM_INSTRUCTIONS_DESCRIPTION = "Executed under Firm instructions";
    public static final String FAST_MARKET_AWAY_DESCRIPTION = "Away NBBO Market was fast, should not have been included in calculation";
    public static final String FLASH_QUOTE_DESCRIPTION = "Rapid multiple quote changes prevent reasonable attempt to obtain apparent NBBO";
    public static final String AWAY_MARKET_UNAVAIL_TO_TRADE_DESCRIPTION = "Away market unavailable to trade";
    public static final String NBBO_LOCKED_W_CBOE_DESCRIPTION = "NBBO locked with a CBOE customer limit book order";
    public static final String NBBO_FADE_DESCRIPTION = "Order sent away NBBO Exchange, order faded";
    public static final String AWAY_MARKET_REFUSE_TO_TRADE_OR_FADE_DESCRIPTION = "Away market refuses to trade/fade";
    public static final String OTHER_DESCRIPTION = "Other";
    public static final String POST_TRADE_QUOTE_DESCRIPTION = "Trade executed at NBBO, quote changed between time of execution and price/fill";
    public static final String SHUT_OFF_ERROR_DESCRIPTION = "Human error results in excluded markets being included in NBBO calculation";
    public static final String SINGLE_LISTED_OPTION_DESCRIPTION = "Single listed options are options that trade exclusively at CBOE";
    public static final String CBOE_SYSTEM_PROBLEMS_DESCRIPTION = "CBOE System Problems";
    public static final String TRADE_BUSTED_DESCRIPTION = "Trade busted by agreement of parties, and/or Floor Official ruling";
    public static final String NOT_ADJUSTED_DESCRIPTION = "Contra broker unwilling to adjust no rule requiring that adjustment be made";
    public static final String BOOK_TAKEN_OUT_AFTER_NOTIFICATION_DESCRIPTION = "Book taked out after notification" ;
    public static final String TRADE_ENTERED_ON_REFRESHED_QUOTE_DESCRIPTION = "Trade entered on a refreshed quote";


    public static final String TRADERS_FORMAT = "TRADERS_FORMAT";
    public static final String INVALID_FORMAT = "INVALID_FORMAT";
    public static final String INVALID_TYPE = "INVALID_TYPE";

    public static final String CODE_FORMAT = "CODE_FORMAT";
    public static final String DESCRIPTION_FORMAT = "DESCRIPTION_FORMAT";
    static Hashtable map = new Hashtable();

    static
    {
        map.put(NOT_RESOLVED + TRADERS_FORMAT, NOT_RESOLVED_STRING);
        map.put(ADJUSTED + TRADERS_FORMAT, ADJUSTED_STRING);
        map.put(PARTIAL_PRICE_ADJUSTMENT + TRADERS_FORMAT, PARTIAL_PRICE_ADJUSTMENT_STRING);
        map.put(PARTIAL_QUANTITY_ADJ + TRADERS_FORMAT, PARTIAL_QUANTITY_ADJ_STRING);
        map.put(CONTRA_UNAVAILABLE + TRADERS_FORMAT, CONTRA_UNAVAILABLE_STRING);
        map.put(DELAYED_REPORT + TRADERS_FORMAT, DELAYED_REPORT_STRING);
        map.put(ERRONEOUS_REPORT + TRADERS_FORMAT, ERRONEOUS_REPORT_STRING);
        map.put(FIRM_DISCRETION + TRADERS_FORMAT, FIRM_DISCRETION_STRING);
        map.put(EXECUTED_UNDER_FIRM_INSTRUCTIONS + TRADERS_FORMAT, EXECUTED_UNDER_FIRM_INSTRUCTIONS_STRING);
        map.put(FAST_MARKET_AWAY + TRADERS_FORMAT, FAST_MARKET_AWAY_STRING);
        map.put(FLASH_QUOTE + TRADERS_FORMAT, FLASH_QUOTE_STRING);
        map.put(AWAY_MARKET_UNAVAIL_TO_TRADE + TRADERS_FORMAT, AWAY_MARKET_UNAVAIL_TO_TRADE_STRING);
        map.put(NBBO_LOCKED_W_CBOE + TRADERS_FORMAT, NBBO_LOCKED_W_CBOE_STRING);
        map.put(NBBO_FADE + TRADERS_FORMAT, NBBO_FADE_STRING);
        map.put(AWAY_MARKET_REFUSE_TO_TRADE_OR_FADE + TRADERS_FORMAT, AWAY_MARKET_REFUSE_TO_TRADE_OR_FADE_STRING);
        map.put(OTHER + TRADERS_FORMAT, OTHER_STRING);
        map.put(POST_TRADE_QUOTE + TRADERS_FORMAT, POST_TRADE_QUOTE_STRING);
        map.put(SHUT_OFF_ERROR + TRADERS_FORMAT, SHUT_OFF_ERROR_STRING);
        map.put(SINGLE_LISTED_OPTION + TRADERS_FORMAT, SINGLE_LISTED_OPTION_STRING);
        map.put(CBOE_SYSTEM_PROBLEMS + TRADERS_FORMAT, CBOE_SYSTEM_PROBLEMS_STRING);
        map.put(TRADE_BUSTED + TRADERS_FORMAT, TRADE_BUSTED_STRING);
        map.put(NOT_ADJUSTED + TRADERS_FORMAT, NOT_ADJUSTED_STRING);
        map.put(BOOK_TAKEN_OUT_AFTER_NOTIFICATION + TRADERS_FORMAT, BOOK_TAKEN_OUT_AFTER_NOTIFICATION_STRING);
        map.put(TRADE_ENTERED_ON_REFRESHED_QUOTE + TRADERS_FORMAT, TRADE_ENTERED_ON_REFRESHED_QUOTE_STRING);

        map.put(NOT_RESOLVED + DESCRIPTION_FORMAT, NOT_RESOLVED_DESCRIPTION);
        map.put(ADJUSTED + DESCRIPTION_FORMAT, ADJUSTED_DESCRIPTION);
        map.put(PARTIAL_PRICE_ADJUSTMENT + DESCRIPTION_FORMAT + DESCRIPTION_FORMAT, PARTIAL_PRICE_ADJUSTMENT_DESCRIPTION);
        map.put(PARTIAL_QUANTITY_ADJ + DESCRIPTION_FORMAT + DESCRIPTION_FORMAT, PARTIAL_QUANTITY_ADJ_DESCRIPTION);
        map.put(CONTRA_UNAVAILABLE + DESCRIPTION_FORMAT + DESCRIPTION_FORMAT, CONTRA_UNAVAILABLE_DESCRIPTION);
        map.put(DELAYED_REPORT + DESCRIPTION_FORMAT, DELAYED_REPORT_DESCRIPTION);
        map.put(ERRONEOUS_REPORT + DESCRIPTION_FORMAT, ERRONEOUS_REPORT_DESCRIPTION);
        map.put(FIRM_DISCRETION + DESCRIPTION_FORMAT, FIRM_DISCRETION_DESCRIPTION);
        map.put(EXECUTED_UNDER_FIRM_INSTRUCTIONS + DESCRIPTION_FORMAT, EXECUTED_UNDER_FIRM_INSTRUCTIONS_DESCRIPTION);
        map.put(FAST_MARKET_AWAY + DESCRIPTION_FORMAT, FAST_MARKET_AWAY_DESCRIPTION);
        map.put(FLASH_QUOTE + DESCRIPTION_FORMAT, FLASH_QUOTE_DESCRIPTION);
        map.put(AWAY_MARKET_UNAVAIL_TO_TRADE + DESCRIPTION_FORMAT, AWAY_MARKET_UNAVAIL_TO_TRADE_DESCRIPTION);
        map.put(NBBO_LOCKED_W_CBOE + DESCRIPTION_FORMAT, NBBO_LOCKED_W_CBOE_DESCRIPTION);
        map.put(NBBO_FADE + DESCRIPTION_FORMAT, NBBO_FADE_DESCRIPTION);
        map.put(AWAY_MARKET_REFUSE_TO_TRADE_OR_FADE + DESCRIPTION_FORMAT, AWAY_MARKET_REFUSE_TO_TRADE_OR_FADE_DESCRIPTION);
        map.put(OTHER + DESCRIPTION_FORMAT, OTHER_DESCRIPTION);
        map.put(POST_TRADE_QUOTE + DESCRIPTION_FORMAT, POST_TRADE_QUOTE_DESCRIPTION);
        map.put(SHUT_OFF_ERROR + DESCRIPTION_FORMAT, SHUT_OFF_ERROR_DESCRIPTION);
        map.put(SINGLE_LISTED_OPTION + DESCRIPTION_FORMAT, SINGLE_LISTED_OPTION_DESCRIPTION);
        map.put(CBOE_SYSTEM_PROBLEMS + DESCRIPTION_FORMAT, CBOE_SYSTEM_PROBLEMS_DESCRIPTION);
        map.put(TRADE_BUSTED + DESCRIPTION_FORMAT, TRADE_BUSTED_DESCRIPTION);
        map.put(NOT_ADJUSTED + DESCRIPTION_FORMAT, NOT_ADJUSTED_DESCRIPTION);
        map.put(BOOK_TAKEN_OUT_AFTER_NOTIFICATION + DESCRIPTION_FORMAT, BOOK_TAKEN_OUT_AFTER_NOTIFICATION_DESCRIPTION);
        map.put(TRADE_ENTERED_ON_REFRESHED_QUOTE + DESCRIPTION_FORMAT, TRADE_ENTERED_ON_REFRESHED_QUOTE_DESCRIPTION);

    }

    private AlertResolutions()
    {
    }

    public static String toString(String type)
    {
        return toString(type, TRADERS_FORMAT);
    }

    public static String toString(String type, String formatSpecifier)
    {
        String value = INVALID_FORMAT;
        if (formatSpecifier.equals(TRADERS_FORMAT) || formatSpecifier.equals(DESCRIPTION_FORMAT))
        {
            value = (String) map.get(type + formatSpecifier);
            if (value == null)
            {
                value = new StringBuffer(30).append(INVALID_TYPE).append(" ").append(type).toString();
            }
        }
        else if (formatSpecifier.equals(CODE_FORMAT))
        {
            StringBuffer sb = new StringBuffer(type);
            sb.append(" - ").append(toString(type, TRADERS_FORMAT));
            value = sb.toString();
        }
        else
        {
            value = new StringBuffer(30).append(INVALID_FORMAT).append(" ").append(formatSpecifier).toString();
        }
        return value;
    }
}
