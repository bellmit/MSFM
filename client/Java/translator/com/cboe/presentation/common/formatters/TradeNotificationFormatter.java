//
// -----------------------------------------------------------------------------------
// Source file: TradeNotificationFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import com.cboe.idl.cmiTradeNotification.TradeNotificationContraPartyStruct;
import com.cboe.idl.cmiTradeNotification.TradeNotificationStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;

import com.cboe.interfaces.presentation.common.formatters.DateFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.ProductClassFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.ProductFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.TradeNotificationFormatStrategy;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.product.SessionReportingClass;

import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;

public class TradeNotificationFormatter extends Formatter implements TradeNotificationFormatStrategy
{
    public TradeNotificationFormatter()
    {
        super();
        addStyle(MESSAGE_SUMMARY, MESSAGE_SUMMARY_DESC);
        addStyle(PRODUCT_ONLY, PRODUCT_ONLY_DESC);
        addStyle(PRODUCT_DESCRIPTION, PRODUCT_DESCRIPTION_DESC);
        addStyle(CLASS_DESCRIPTION, CLASS_DESCRIPTION_DESC);
        addStyle(REPORTING_CLASS_DESCRIPTION, REPORTING_CLASS_DESCRIPTION_DESC);

        setDefaultStyle(MESSAGE_SUMMARY);
    }

    public String format(TradeNotificationStruct tradeNotificationStruct)
    {
        return format(tradeNotificationStruct, getDefaultStyle());
    }

    public String format(TradeNotificationStruct tradeNotificationStruct, String styleName)
    {
        if (MESSAGE_SUMMARY.equals(styleName))
        {
            return formatSummary(tradeNotificationStruct);
        }
        else if (PRODUCT_ONLY.equals(styleName))
        {
            return formatProductOnly(tradeNotificationStruct);
        }
        else if(PRODUCT_DESCRIPTION.equals(styleName))
        {
            return formatProductDescription(tradeNotificationStruct);
        }
        else if(CLASS_DESCRIPTION.equals(styleName))
        {
            return formatProductClassDescription(tradeNotificationStruct);
        }
        else if(REPORTING_CLASS_DESCRIPTION.equals(styleName))
        {
            return formatReportingClassDescription(tradeNotificationStruct);
        }
        else
        {
            return formatSummary(tradeNotificationStruct);
        }
    }

    private String formatSummary(TradeNotificationStruct tradeNotificationStruct)
    {
        StringBuilder buf = new StringBuilder(128);
        buf.append(getBuySellString(tradeNotificationStruct.side));
        buf.append(" ");
        buf.append(tradeNotificationStruct.tradedQuantity);
        buf.append(" ");
//        buf.append(getProductString(tradeNotificationStruct.sessionName,
//                                    tradeNotificationStruct.prodKeysStruct.productKey,
//                                    ProductFormatStrategy.FULL_PRODUCT_NAME));
        buf.append(getProductString(tradeNotificationStruct.sessionName,
                                    tradeNotificationStruct.prodKeysStruct.productKey,
                                    ProductFormatStrategy.FULL_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE));
        buf.append(" @ ");
        buf.append(DisplayPriceFactory.create(tradeNotificationStruct.executionPrice).toString());
        buf.append(", Executing Broker: ");
        buf.append(tradeNotificationStruct.executingBroker.acronym);
        buf.append(", ETN Broker: ");
        buf.append(tradeNotificationStruct.etnBroker.acronym);
        buf.append(" (");
        buf.append(tradeNotificationStruct.etnExchangeFirm.firmNumber);
        buf.append(")");
        buf.append(", Executed: ");
        buf.append(getTimeString(tradeNotificationStruct.executionTime));
        buf.append(", ORSID: ");
        buf.append(getOrsIdString(tradeNotificationStruct.tnContraParties));
        buf.append(", ETN ID: ");
        buf.append(tradeNotificationStruct.tradeNotificationId);

        return buf.toString();
    }


    private String formatProductOnly(TradeNotificationStruct tradeNotificationStruct)
    {
        StringBuilder buf = new StringBuilder(128);
        buf.append(getBuySellString(tradeNotificationStruct.side));
        buf.append(" ");
        buf.append(tradeNotificationStruct.tradedQuantity);
        buf.append(" ");
//        buf.append(getProductString(tradeNotificationStruct.sessionName,
//                                    tradeNotificationStruct.prodKeysStruct.productKey,
//                                    ProductFormatStrategy.FULL_PRODUCT_NAME));
        buf.append(getProductString(tradeNotificationStruct.sessionName,
                                    tradeNotificationStruct.prodKeysStruct.productKey,
                                    ProductFormatStrategy.FULL_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE));
        buf.append(" @ ");
        buf.append(DisplayPriceFactory.create(tradeNotificationStruct.executionPrice).toString());

        return buf.toString();
    }

    private String formatProductDescription(TradeNotificationStruct tradeNotificationStruct)
    {
        return getProductString(tradeNotificationStruct.sessionName,
                                tradeNotificationStruct.prodKeysStruct.productKey,
                                ProductFormatStrategy.FULL_PRODUCT_NAME_WITH_KEY);
    }

    private String formatProductClassDescription(TradeNotificationStruct tradeNotificationStruct)
    {
        return getClassString(tradeNotificationStruct.sessionName,
                              tradeNotificationStruct.prodKeysStruct.classKey,
                              ProductClassFormatStrategy.CLASS_TYPE_NAME_KEY);
    }

    private String formatReportingClassDescription(TradeNotificationStruct tradeNotificationStruct)
    {
        return getReportingClassString(tradeNotificationStruct.sessionName,
                              tradeNotificationStruct.prodKeysStruct.reportingClass,
                              ProductClassFormatStrategy.CLASS_TYPE_NAME_KEY);
    }

    private String getTimeString(DateTimeStruct struct)
    {
        return FormatFactory.getDateFormatStrategy().format(
                new DisplayDate(struct).getDate(),
                DateFormatStrategy.TIME_FORMAT_24_HOURS_SECONDS_STYLE);
    }

    private String getBuySellString(char buySell)
    {
        String result = "?";
        if ((buySell == 'b') || (buySell == 'B'))
        {
            result = "BUY";
        }
        else if((buySell == 's') || (buySell == 'S'))
        {
            result = "SELL";
        }
        else if((buySell == 'd') || (buySell == 'D'))
        {
            result = "DEFINED";
        }
        else if((buySell == 'o') || (buySell == 'O'))
        {
            result = "OPPOSITE";
        }
        else if((buySell == 'h') || (buySell == 'H'))
        {
            result = "SELL SHRT";
        }
        else if((buySell == 'x') || (buySell == 'X'))
        {
            result = "SELL S EXEMPT";
        }
        else if((buySell == 'm') || (buySell == 'M'))
        {
            result = "-BUY";
        }
        else if((buySell == 'p') || (buySell == 'P'))
        {
            result = "+SELL";
        }
        return result;
    }

    private String getProductString(String session, int productKey, String style)
    {
        String result = "";
        SessionProduct product = null;
        try
        {
            product = APIHome.findProductQueryAPI().getProductByKeyForSession(session, productKey);
        }
        catch(Exception e)
        {
            DefaultExceptionHandlerHome.find().process(e);
        }

        if (product != null)
        {
            result = FormatFactory.getProductFormatStrategy().format(product, style);
        }
        else
        {
            result += productKey;
        }
        return result;
    }

    private String getClassString(String session, int classKey, String style)
    {
        String result = "";
        SessionProductClass sessionProductClass = null;
        try
        {
            sessionProductClass = APIHome.findProductQueryAPI().getClassByKeyForSession(session,classKey);
        }
        catch(Exception e)
        {
            DefaultExceptionHandlerHome.find().process(e);
        }

        if(sessionProductClass != null)
        {
            result = FormatFactory.getProductClassFormatStrategy().format(sessionProductClass, style);
        }
        else
        {
            result += classKey;
        }
        return result;
    }

    private String getReportingClassString(String session, int classKey, String style)
    {
        String result = "";
        SessionReportingClass sessionReportingClass = null;
        try
        {
            sessionReportingClass =
                    APIHome.findProductQueryAPI().getReportingClassByKeyForSession(classKey, session);
        }
        catch(Exception e)
        {
            DefaultExceptionHandlerHome.find().process(e);
        }

        if(sessionReportingClass != null)
        {
            result = FormatFactory.getProductClassFormatStrategy()
                    .format(sessionReportingClass.getSessionProductClass(), style);
        }
        else
        {
            result += classKey;
        }
        return result;
    }

    private String getOrsIdString(TradeNotificationContraPartyStruct[] contraStructs)
    {
        StringBuilder buf = new StringBuilder(16);
        boolean isFirst = true;
        for (TradeNotificationContraPartyStruct struct : contraStructs)
        {
            if (isFirst)
            {
                isFirst = false;
            }
            else
            {
                buf.append(", ");
            }
            buf.append(struct.orsId);
        }
        return buf.toString();
    }

}
