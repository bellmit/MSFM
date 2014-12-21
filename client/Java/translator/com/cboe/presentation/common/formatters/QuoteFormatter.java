//
// -----------------------------------------------------------------------------------
// Source file: QuoteFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;

import com.cboe.idl.cmiQuote.QuoteFilledReportStruct;
import com.cboe.idl.cmiQuote.QuoteStruct;
import com.cboe.idl.cmiOrder.FilledReportStruct;
import com.cboe.idl.cmiOrder.ContraPartyStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;

import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.ProductClass;

import com.cboe.interfaces.presentation.common.formatters.*;
import com.cboe.interfaces.presentation.quote.Quote;
import com.cboe.interfaces.presentation.quote.QuoteDetail;
import com.cboe.interfaces.presentation.quote.QuoteEntry;

import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.presentation.product.ProductHelper;

/**
 * Responsible for formatting Quotes
 * @author Troy Wehrle
 */
class QuoteFormatter extends Formatter implements QuoteFormatStrategy
{
    private final String Category = this.getClass().getName();

/**
 */
public QuoteFormatter()
{
    super();

    addStyle(FULL_INFORMATION, FULL_INFORMATION_DESCRIPTION);
    addStyle(CLASSLESS_INFORMATION, CLASSLESS_INFORMATION_DESCRIPTION);
    addStyle(BRIEF, BRIEF_DESCRIPTION);

    setDefaultStyle(FULL_INFORMATION);
}

/**
 * Implements format definition from QuoteFormatStrategy
 */
public String format(QuoteFilledReportStruct quoteFill, int reportIndex)
{
    return format(quoteFill, getDefaultStyle(), reportIndex);
}
/**
 * Implements format definition from QuoteFormatStrategy
 */
public String format(QuoteFilledReportStruct quoteFill, String styleName, int reportIndex)
{
    validateStyle(styleName);
    StringBuffer quoteFillText = new StringBuffer(1000);

    VolumeFormatStrategy volumeFormatter = FormatFactory.getVolumeFormatStrategy();
    ProductClassFormatStrategy productClassFormatter = FormatFactory.getProductClassFormatStrategy();
    ProductFormatStrategy productFormatter = FormatFactory.getProductFormatStrategy();
    FilledReportStruct fillReport = quoteFill.filledReport[reportIndex];

    if (styleName.equalsIgnoreCase(FULL_INFORMATION)
    ||  styleName.equalsIgnoreCase(CLASSLESS_INFORMATION))
    {
        Utility.portWarningPorted(Category + ".format(QuoteFilledReportStruct)");                                       // TODO: remove when Jasper port is completed
    }

    if(styleName.equalsIgnoreCase(FULL_INFORMATION))                                                                    // TODO: remove when Jasper port is completed
    {
//        FilledReportStruct fillReport = quoteFill.filledReport;

        ProductClass productClass = null;
        Product product = null;
        try
        {
            quoteFillText.append("Class: ");

            product = APIHome.findProductQueryAPI().getProductByKey(fillReport.productKey);
            if(product != null)
            {
                productClass = APIHome.findProductQueryAPI().getProductClassByKey(product.getProductKeysStruct().classKey);
            }
            if(productClass != null)
            {
                quoteFillText.append(productClassFormatter.format(productClass, productClassFormatter.CLASS_TYPE_NAME)).append('\n');
//                quoteFillText.append("Underlying Symbol: ").append(ProductHelper.toString(productClass.getUnderlyingProduct().getProductNameStruct())).append('\n');
                quoteFillText.append("Underlying Symbol: ").append(productFormatter.format(productClass.getUnderlyingProduct())).append('\n');
            }
            else
            {
                quoteFillText.append("(Not Available)").append('\n');
                quoteFillText.append("Underlying Symbol: ").append("(Not Available)").append('\n');
            }
        }
        catch(SystemException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
            quoteFillText.append("(Not Available)").append('\n');
            quoteFillText.append("Underlying Symbol: ").append("(Not Available)").append('\n');
        }
        catch(CommunicationException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
            quoteFillText.append("(Not Available)").append('\n');
            quoteFillText.append("Underlying Symbol: ").append("(Not Available)").append('\n');
        }
        catch(AuthorizationException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
            quoteFillText.append("(Not Available)").append('\n');
            quoteFillText.append("Underlying Symbol: ").append("(Not Available)").append('\n');
        }
        catch(DataValidationException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
            quoteFillText.append("(Not Available)").append('\n');
            quoteFillText.append("Underlying Symbol: ").append("(Not Available)").append('\n');
        }
        catch(NotFoundException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
            quoteFillText.append("(Not Available)").append('\n');
            quoteFillText.append("Underlying Symbol: ").append("(Not Available)").append('\n');
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(e);
            quoteFillText.append("(Not Available)").append('\n');
            quoteFillText.append("Underlying Symbol: ").append("(Not Available)").append('\n');
        }

        quoteFillText.append("Product: ");
        if(product != null)
        {
            quoteFillText.append(productFormatter.format(product)).append('\n');
        }
        else
        {
            quoteFillText.append("(Not Available)").append('\n');
        }

        quoteFillText.append("Executed Vol: ").append(volumeFormatter.format(fillReport.tradedQuantity)).append('\n');
        quoteFillText.append("Vol Rem: ").append(volumeFormatter.format(fillReport.leavesQuantity)).append('\n');
        quoteFillText.append("Price:  ").append(DisplayPriceFactory.create(fillReport.price).toString()).append('\n');
        quoteFillText.append("Side: ").append(Utility.sideToString(fillReport.side)).append('\n');
        quoteFillText.append("Date/Time: ").append(Utility.toString(fillReport.timeSent, "yyyy/MM/dd HH:mm:ss.S")).append('\n');
        quoteFillText.append("Executing/Give Up Firm: ").append(getFormattedExchangeFirm(fillReport.executingOrGiveUpFirm)).append('\n');
        quoteFillText.append("Position: ").append(PositionEffects.toString(fillReport.positionEffect)).append('\n');
        quoteFillText.append("Session: ").append(fillReport.sessionName).append('\n');
        quoteFillText.append("Account: ").append(fillReport.account).append('\n');
        quoteFillText.append("Subaccount: ").append(fillReport.subaccount).append('\n');
        quoteFillText.append("ORS Id: ").append(fillReport.orsId).append('\n');
        quoteFillText.append("CMTA: ").append(getFormattedExchangeFirm(fillReport.cmta)).append('\n');
        quoteFillText.append("Originator: ").append(getFormattedExchangeAcronym(fillReport.originator)).append('\n');

        ContraPartyStruct[] contras = fillReport.contraParties;
        for(int j = 0; j < contras.length; j++)
        {
            quoteFillText.append("Contra Party").append(j + 1).append(" - ");
            quoteFillText.append("Firm: ").append(getFormattedExchangeFirm(contras[j].firm)).append("; ");
            quoteFillText.append("User Id: ").append(getFormattedExchangeAcronym(contras[j].user)).append("; ");
            quoteFillText.append("Vol: ").append(volumeFormatter.format(contras[j].quantity));

            if(j < contras.length)
            {
                quoteFillText.append('\n');
            }
        }

        quoteFillText.append("Optional Data: ").append(fillReport.optionalData);
    }
    else if(styleName.equalsIgnoreCase(CLASSLESS_INFORMATION))                                                          // TODO: remove when Jasper port is completed
    {
        int column2Margin = 38;
        int sizeFieldAdded = 0;

//        FilledReportStruct fillReport = quoteFill.filledReport;

        ProductClass productClass = null;
        Product product = null;
        try
        {
            quoteFillText.append("Class: ");

            product = APIHome.findProductQueryAPI().getProductByKey(fillReport.productKey);
            if(product != null)
            {
                productClass = APIHome.findProductQueryAPI().getProductClassByKey(product.getProductKeysStruct().classKey);
            }
            if(productClass != null)
            {
                sizeFieldAdded = addFieldWithSizeLimit(quoteFillText, productClassFormatter.format(productClass, productClassFormatter.CLASS_TYPE_NAME), column2Margin - 1 - 7, false);
                quoteFillText = appendSpace(quoteFillText, column2Margin - 7 - sizeFieldAdded);
            }
            else
            {
                quoteFillText.append("(Not Available)");
                quoteFillText = appendSpace(quoteFillText, column2Margin - 7 - 15);
            }
        }
        catch(SystemException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
            quoteFillText.append("(Not Available)");
            quoteFillText = appendSpace(quoteFillText, column2Margin - 7 - 15);
        }
        catch(CommunicationException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
            quoteFillText.append("(Not Available)");
            quoteFillText = appendSpace(quoteFillText, column2Margin - 7 - 15);
        }
        catch(AuthorizationException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
            quoteFillText.append("(Not Available)");
            quoteFillText = appendSpace(quoteFillText, column2Margin - 7 - 15);
        }
        catch(DataValidationException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
            quoteFillText.append("(Not Available)");
            quoteFillText = appendSpace(quoteFillText, column2Margin - 7 - 15);
        }
        catch(NotFoundException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
            quoteFillText.append("(Not Available)");
            quoteFillText = appendSpace(quoteFillText, column2Margin - 7 - 15);
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(e);
            quoteFillText.append("(Not Available)");
            quoteFillText = appendSpace(quoteFillText, column2Margin - 7 - 15);
        }

        quoteFillText.append("Product: ");
        if(product != null)
        {
            quoteFillText.append(productFormatter.format(product)).append('\n');
        }
        else
        {
            quoteFillText.append("(Not Available)").append('\n');
        }

        quoteFillText.append("Side: ");
        sizeFieldAdded = addFieldWithSizeLimit(quoteFillText, Utility.sideToString(fillReport.side), column2Margin - 1 - 6, false);
        quoteFillText = appendSpace(quoteFillText, column2Margin - 6 - sizeFieldAdded);

        quoteFillText.append("Underlying Symbol: ");
        if(productClass != null)
        {
            quoteFillText.append(productFormatter.format(productClass.getUnderlyingProduct())).append('\n');
        }
        else
        {
            quoteFillText.append("(Not Available)").append('\n');
        }

        quoteFillText.append("Executed Vol: ");
        sizeFieldAdded = addFieldWithSizeLimit(quoteFillText, volumeFormatter.format(fillReport.tradedQuantity), column2Margin - 1 - 14, false);
        quoteFillText = appendSpace(quoteFillText, column2Margin - 14 - sizeFieldAdded);

        quoteFillText.append("Vol Rem: ").append(volumeFormatter.format(fillReport.leavesQuantity)).append('\n');

        quoteFillText.append("Price:  ");
        sizeFieldAdded = addFieldWithSizeLimit(quoteFillText, DisplayPriceFactory.create(fillReport.price).toString(), column2Margin - 1 - 8, false);
        quoteFillText = appendSpace(quoteFillText, column2Margin - 8 - sizeFieldAdded);

        quoteFillText.append("Date/Time: ").append(Utility.toString(fillReport.timeSent, "yyyy/MM/dd HH:mm:ss.S")).append('\n');

        quoteFillText.append("Executing/Give Up Firm: ");

        sizeFieldAdded = addFieldWithSizeLimit(quoteFillText, getFormattedExchangeFirm(fillReport.executingOrGiveUpFirm), column2Margin - 1 - 24, false);
        quoteFillText = appendSpace(quoteFillText, column2Margin - 24 - sizeFieldAdded);

        quoteFillText.append("Position: ").append(PositionEffects.toString(fillReport.positionEffect)).append('\n');

        quoteFillText.append("Session: ").append(fillReport.sessionName).append('\n');

        quoteFillText.append("Account: ");
        sizeFieldAdded = addFieldWithSizeLimit(quoteFillText, fillReport.account, column2Margin - 1 - 9, false);
        quoteFillText = appendSpace(quoteFillText, column2Margin - 9 - sizeFieldAdded);

        quoteFillText.append("Subaccount: ").append(fillReport.subaccount).append('\n');

        quoteFillText.append("ORS Id: ");
        sizeFieldAdded = addFieldWithSizeLimit(quoteFillText, fillReport.orsId, column2Margin - 1 - 8, false);
        quoteFillText = appendSpace(quoteFillText, column2Margin - 8 - sizeFieldAdded);

        quoteFillText.append("CMTA: ").append(getFormattedExchangeFirm(fillReport.cmta)).append('\n');
        quoteFillText.append("Originator: ").append(getFormattedExchangeAcronym(fillReport.originator)).append('\n');

        ContraPartyStruct[] contras = fillReport.contraParties;
        for(int j = 0; j < contras.length; j++)
        {
            quoteFillText.append("Contra Party").append(j + 1).append(" - ");
            quoteFillText.append("Firm: ").append(getFormattedExchangeFirm(contras[j].firm)).append("; ");
            quoteFillText.append("User Id: ").append(getFormattedExchangeAcronym(contras[j].user)).append("; ");
            quoteFillText.append("Vol: ").append(volumeFormatter.format(contras[j].quantity));
            quoteFillText.append('\n');
        }

        quoteFillText.append("Optional Data: ");
        sizeFieldAdded = addFieldWithSizeLimit(quoteFillText, fillReport.optionalData, column2Margin + column2Margin - 15, true);
        quoteFillText.append('\n');

        while(sizeFieldAdded < fillReport.optionalData.length())
        {
            quoteFillText = appendSpace(quoteFillText, 15);
            sizeFieldAdded = sizeFieldAdded + addFieldWithSizeLimit(quoteFillText, fillReport.optionalData.substring(sizeFieldAdded), column2Margin + column2Margin - 15, true);

            if(sizeFieldAdded < fillReport.optionalData.length())
            {
                quoteFillText.append('\n');
            }
        }
    }
    else if(styleName.equalsIgnoreCase(BRIEF))
    {
//        FilledReportStruct fillReport = quoteFill.filledReport;

        ProductClass productClass = null;
        Product product = null;

        try
        {
            product = APIHome.findProductQueryAPI().getProductByKey(fillReport.productKey);
            if(product != null)
            {
                productClass = APIHome.findProductQueryAPI().getProductClassByKey(product.getProductKeysStruct().classKey);
            }
            if(productClass != null)
            {
                quoteFillText.append(productClassFormatter.format(productClass, ProductClassFormatter.CLASS_TYPE_NAME)).append(' ');
            }
        }
        catch(SystemException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
        }
        catch(CommunicationException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
        }
        catch(AuthorizationException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
        }
        catch(DataValidationException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
        }
        catch(NotFoundException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e);
        }

        if(product != null)
        {
            quoteFillText.append(productFormatter.format(product)).append(' ');
        }

        quoteFillText.append(Utility.sideToString(fillReport.side)).append(' ');
        quoteFillText.append(volumeFormatter.format(fillReport.tradedQuantity)).append(' ');
        quoteFillText.append("@ ");
        quoteFillText.append(DisplayPriceFactory.create(fillReport.price));
        quoteFillText.append(", leaves ").append(volumeFormatter.format(fillReport.leavesQuantity));
    }

    return quoteFillText.toString();
}
/**
 * Implements format definition from QuoteFormatStrategy
 */
public String format(QuoteStruct quote)
{
    return format(quote, getDefaultStyle());
}
/**
 * Implements format definition from QuoteFormatStrategy
 */
public String format(QuoteStruct quote, String styleName)
{
    validateStyle(styleName);
    StringBuffer quoteText = new StringBuffer(1000);

    VolumeFormatStrategy volumeFormatter = FormatFactory.getVolumeFormatStrategy();
    ProductClassFormatStrategy productClassFormatter = FormatFactory.getProductClassFormatStrategy();
    ProductFormatStrategy productFormatter = FormatFactory.getProductFormatStrategy();

    if (styleName.equalsIgnoreCase(FULL_INFORMATION)
    ||  styleName.equalsIgnoreCase(CLASSLESS_INFORMATION))
    {
        Utility.portWarningPorted(Category + ".format(QuoteStruct)");                                                   // TODO: remove when Jasper port is completed
    }

    if(styleName.equalsIgnoreCase(FULL_INFORMATION))                                                                    // TODO: remove when Jasper port is completed
    {
        ProductClass productClass = null;
        Product product = null;

        quoteText.append("Class: ");

        try
        {
            product = APIHome.findProductQueryAPI().getProductByKey(quote.productKey);
            if(product != null)
            {
                productClass = APIHome.findProductQueryAPI().getProductClassByKey(product.getProductKeysStruct().classKey);
            }
            if(productClass != null)
            {
                quoteText.append(productClassFormatter.format(productClass, productClassFormatter.CLASS_TYPE_NAME)).append('\n');
                quoteText.append("Underlying Symbol: ").append(productFormatter.format(productClass.getUnderlyingProduct())).append('\n');
            }
            else
            {
                quoteText.append("(Not Available)").append('\n');
                quoteText.append("Underlying Symbol: ").append("(Not Available)").append('\n');
            }
        }
        catch(SystemException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
            quoteText.append("(Not Available)").append('\n');
            quoteText.append("Underlying Symbol: ").append("(Not Available)").append('\n');
        }
        catch(CommunicationException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
            quoteText.append("(Not Available)").append('\n');
            quoteText.append("Underlying Symbol: ").append("(Not Available)").append('\n');
        }
        catch(AuthorizationException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
            quoteText.append("(Not Available)").append('\n');
            quoteText.append("Underlying Symbol: ").append("(Not Available)").append('\n');
        }
        catch(DataValidationException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
            quoteText.append("(Not Available)").append('\n');
            quoteText.append("Underlying Symbol: ").append("(Not Available)").append('\n');
        }
        catch(NotFoundException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
            quoteText.append("(Not Available)").append('\n');
            quoteText.append("Underlying Symbol: ").append("(Not Available)").append('\n');
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e);
            quoteText.append("(Not Available)").append('\n');
            quoteText.append("Underlying Symbol: ").append("(Not Available)").append('\n');
        }

        quoteText.append("Product: ").append(productFormatter.format(ProductHelper.getProduct(quote.productKey))).append('\n');
        quoteText.append("Bid Qty: ").append(volumeFormatter.format(quote.bidQuantity)).append('\n');
        quoteText.append("Bid Price: ").append(DisplayPriceFactory.create(quote.bidPrice)).append('\n');
        quoteText.append("Ask Qty: ").append(volumeFormatter.format(quote.askQuantity)).append('\n');
        quoteText.append("Ask Price: ").append(DisplayPriceFactory.create(quote.askPrice)).append('\n');
        quoteText.append("Session: ").append(quote.sessionName).append('\n');
        // 4/3/2002 - quote doesn't have a clearing firm
//        quoteText.append("Clearing Firm: ").append("????").append('\n');
//      quoteText.append("Clearing Firm: ").append(quote. clearingFirm).append('\n');
        quoteText.append("User ID: ").append(quote.userId).append('\n');
        quoteText.append("User Assigned ID: ").append(quote.userAssignedId);
    }
    else if(styleName.equalsIgnoreCase(CLASSLESS_INFORMATION))                                                          // TODO: remove when Jasper port is completed
    {
        int column2Margin = 38;
        int sizeFieldAdded = 0;

        // 4/3/2002 - quote doesn't have a clearing firm
//        quoteText.append("Clearing Firm: ");
//        sizeFieldAdded = addFieldWithSizeLimit(quoteText, "????", column2Margin - 1 - 15, false);
////        sizeFieldAdded = addFieldWithSizeLimit(quoteText, quote.clearingFirm, column2Margin - 1 - 15, false);
//        quoteText = appendSpace(quoteText, column2Margin - 15 - sizeFieldAdded);

        quoteText.append("Session: ").append(quote.sessionName).append('\n');

        quoteText.append("Bid Vol: ");
        sizeFieldAdded = addFieldWithSizeLimit(quoteText, volumeFormatter.format(quote.bidQuantity), column2Margin - 1 - 9, false);
        quoteText = appendSpace(quoteText, column2Margin - 9 - sizeFieldAdded);

        quoteText.append("Ask Vol: ").append(volumeFormatter.format(quote.askQuantity)).append('\n');

        quoteText.append("Bid Price:  ");
        sizeFieldAdded = addFieldWithSizeLimit(quoteText, DisplayPriceFactory.create(quote.bidPrice).toString(), column2Margin - 1 - 12, false);
        quoteText = appendSpace(quoteText, column2Margin - 12 - sizeFieldAdded);

        quoteText.append("Ask Price:  ").append(DisplayPriceFactory.create(quote.askPrice)).append('\n');

        quoteText.append("User Id: ");
        sizeFieldAdded = addFieldWithSizeLimit(quoteText, quote.userId, column2Margin - 1 - 9, false);
        quoteText = appendSpace(quoteText, column2Margin - 9 - sizeFieldAdded);

        quoteText.append("User Assigned ID: ").append(quote.userAssignedId);
    }
    else if(styleName.equalsIgnoreCase(BRIEF))
    {
        ProductClass productClass = null;
        Product product = null;

        try
        {
            product = APIHome.findProductQueryAPI().getProductByKey(quote.productKey);
            if(product != null)
            {
                productClass = APIHome.findProductQueryAPI().getProductClassByKey(product.getProductKeysStruct().classKey);
            }
            if(productClass != null)
            {
                quoteText.append(productClassFormatter.format(productClass, ProductClassFormatter.CLASS_TYPE_NAME)).append(' ');
            }
        }
        catch(SystemException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
        }
        catch(CommunicationException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
        }
        catch(AuthorizationException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
        }
        catch(DataValidationException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
        }
        catch(NotFoundException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e);
        }

        if(product != null)
        {
            quoteText.append(productFormatter.format(product)).append(' ');
        }

        quoteText.append("Bid ").append(volumeFormatter.format(quote.bidQuantity)).append(' ');
        quoteText.append("@ ").append(DisplayPriceFactory.create(quote.bidPrice)).append(' ');
        quoteText.append(", Ask ").append(volumeFormatter.format(quote.askQuantity)).append(' ');
        quoteText.append("@ ").append(DisplayPriceFactory.create(quote.askPrice));
    }

    return quoteText.toString();
}
    private String getFormattedExchangeFirm(ExchangeFirmStruct anExchangeFirmStruct)
    {
        String retVal;
        if(anExchangeFirmStruct != null)
        {
            retVal = FormatFactory.getExchangeFirmFormatStrategy().format(anExchangeFirmStruct, ExchangeFirmFormatter.BRIEF);
        }
        else
        {
            retVal = " ";
        }

        return retVal;
    }

    private String getFormattedExchangeAcronym(ExchangeAcronymStruct anExchangeAcronymStruct)
    {
        String retVal;
        if(anExchangeAcronymStruct != null)
        {
            retVal = FormatFactory.getExchangeAcronymFormatStrategy().format(anExchangeAcronymStruct, ExchangeAcronymFormatter.BRIEF);
        }
        else
        {
            retVal = " ";
        }

        return retVal;
    }

    public String format(QuoteEntry quoteEntry, String styleName)
    {
        validateStyle(styleName);
        StringBuffer quoteEntryText = new StringBuffer(1000);

        VolumeFormatStrategy volumeFormatter = FormatFactory.getVolumeFormatStrategy();

        if (styleName.equalsIgnoreCase(FULL_INFORMATION)
        ||  styleName.equalsIgnoreCase(CLASSLESS_INFORMATION))
        {
            Utility.portWarningPorted(Category + ".format(QuoteEntry)");                                                // TODO: remove when Jasper port is completed
        }

        if(styleName.equalsIgnoreCase(FULL_INFORMATION))                                                                // TODO: remove when Jasper port is completed
        {
            quoteEntryText.append(formatProductClass(quoteEntry.getProductKey(), styleName));

            quoteEntryText.append("Bid Qty: ").append(volumeFormatter.format(quoteEntry.getBidQuantity())).append('\n');
            quoteEntryText.append("Bid Price: ").append(quoteEntry.getBidPrice()).append('\n');
            quoteEntryText.append("Ask Qty: ").append(volumeFormatter.format(quoteEntry.getAskQuantity())).append('\n');
            quoteEntryText.append("Ask Price: ").append(quoteEntry.getAskPrice()).append('\n');
            quoteEntryText.append("Session: ").append(quoteEntry.getSessionName()).append('\n');

            quoteEntryText.append("User Assigned ID: ").append(quoteEntry.getUserAssignedId());
        }
        else if(styleName.equalsIgnoreCase(CLASSLESS_INFORMATION))                                                      // TODO: remove when Jasper port is completed
        {
            int column2Margin = 38;
            int sizeFieldAdded = 0;

            quoteEntryText.append("Session: ").append(quoteEntry.getSessionName()).append('\n');

            quoteEntryText.append("Bid Vol: ");
            sizeFieldAdded = addFieldWithSizeLimit(quoteEntryText, volumeFormatter.format(quoteEntry.getBidQuantity()), column2Margin - 1 - 9, false);
            quoteEntryText = appendSpace(quoteEntryText, column2Margin - 9 - sizeFieldAdded);

            quoteEntryText.append("Ask Vol: ").append(volumeFormatter.format(quoteEntry.getAskQuantity())).append('\n');

            quoteEntryText.append("Bid Price:  ");
            sizeFieldAdded = addFieldWithSizeLimit(quoteEntryText, quoteEntry.getBidPrice().toString(), column2Margin - 1 - 12, false);
            quoteEntryText = appendSpace(quoteEntryText, column2Margin - 12 - sizeFieldAdded);

            quoteEntryText.append("Ask Price:  ").append(quoteEntry.getAskPrice()).append('\n');

            quoteEntryText.append("User Assigned ID: ").append(quoteEntry.getUserAssignedId());
        }
        else if(styleName.equalsIgnoreCase(BRIEF))
        {
            quoteEntryText.append(formatProductClass(quoteEntry.getProductKey(), styleName));

            quoteEntryText.append("Bid ").append(volumeFormatter.format(quoteEntry.getBidQuantity())).append(' ');
            quoteEntryText.append("@ ").append(quoteEntry.getBidPrice()).append(' ');
            quoteEntryText.append(", Ask ").append(volumeFormatter.format(quoteEntry.getAskQuantity())).append(' ');
            quoteEntryText.append("@ ").append(quoteEntry.getAskPrice());
        }

        return quoteEntryText.toString();
    }

    private StringBuffer formatProductClass(int productKey, String styleName)
    {
        StringBuffer text = new StringBuffer();
        ProductClass productClass = null;
        ProductClassFormatStrategy productClassFormatter = FormatFactory.getProductClassFormatStrategy();
        ProductFormatStrategy productFormatter = FormatFactory.getProductFormatStrategy();

        Product product = getProduct(productKey);
        if(product != null)
        {
            productClass = getProductClass(product.getProductKeysStruct().classKey);
        }

        if (styleName.equalsIgnoreCase(FULL_INFORMATION)
        ||  styleName.equalsIgnoreCase(CLASSLESS_INFORMATION))
        {
            Utility.portWarningPorted(Category + ".formatProductClass(productKey)");                                    // TODO: remove when Jasper port is completed
        }

        if(styleName.equalsIgnoreCase(FULL_INFORMATION))                                                                // TODO: remove when Jasper port is completed
        {
            text.append("Class: ");

            if(productClass != null)
            {
                text.append(productClassFormatter.format(productClass, productClassFormatter.CLASS_TYPE_NAME)).append('\n');
                text.append("Underlying Symbol: ").append(productFormatter.format(productClass.getUnderlyingProduct())).append('\n');
            }
            else
            {
                text.append("(Not Available)").append('\n');
                text.append("Underlying Symbol: ").append("(Not Available)").append('\n');
            }

            text.append("Product: ").append(productFormatter.format(ProductHelper.getProduct(productKey))).append('\n');
        }
        else if(styleName.equalsIgnoreCase(BRIEF))
        {
            if(productClass != null)
            {
                text.append(productClassFormatter.format(productClass, ProductClassFormatter.CLASS_TYPE_NAME)).append(' ');
            }
            if(product != null)
            {
                text.append(productFormatter.format(product)).append(' ');
            }
        }
        else if(styleName.equalsIgnoreCase(CLASSLESS_INFORMATION))
        {
        }

        return text;
    }

    public String format(Quote quote, String styleName)
    {
        return format(quote.getQuoteStruct(), styleName);
    }

    public String format(QuoteDetail quoteDetail, String styleName)
    {
        return format(quoteDetail.getQuote(), styleName);
    }

    private Product getProduct(int productKey)
    {
        Product product = null;
        try
        {
            product = APIHome.findProductQueryAPI().getProductByKey(productKey);
        }
        catch (SystemException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
        }
        catch (CommunicationException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
        }
        catch (AuthorizationException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
        }
        catch (DataValidationException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
        }
        catch (NotFoundException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e);
        }

        return product;
    }

    private ProductClass getProductClass(int classKey)
    {
        ProductClass productClass = null;
        try
        {
            productClass = APIHome.findProductQueryAPI().getProductClassByKey(classKey);
        }
        catch (SystemException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
        }
        catch (CommunicationException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
        }
        catch (AuthorizationException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
        }
        catch (DataValidationException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
        }
        catch (NotFoundException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e);
        }

        return productClass;
    }
}
