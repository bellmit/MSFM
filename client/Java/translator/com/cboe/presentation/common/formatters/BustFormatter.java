//
// -----------------------------------------------------------------------------------
// Source file: BustFormatter.java
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

import com.cboe.idl.cmiOrder.BustReportStruct;
import com.cboe.idl.cmiOrder.OrderBustReinstateReportStruct;
import com.cboe.idl.cmiOrder.OrderBustReportStruct;
import com.cboe.idl.cmiQuote.QuoteBustReportStruct;
import com.cboe.idl.cmiConstants.ReportTypes;

import com.cboe.interfaces.presentation.common.formatters.BustFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.VolumeFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.ProductClassFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.ProductFormatStrategy;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.user.ExchangeFirm;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.api.APIHome;

import com.cboe.presentation.user.ExchangeFirmFactory;

/**
 * Responsible for formatting order/quote busts
 * @author Troy Wehrle
 */
class BustFormatter extends Formatter implements BustFormatStrategy
{
    private final String Category = this.getClass().getName();

/**
 */
public BustFormatter()
{
    super();

    addStyle(BRIEF_NAME, BRIEF_DESCRIPTION);
    addStyle(FULL_INFORMATION_NAME, FULL_INFORMATION_DESCRIPTION);

    setDefaultStyle(BRIEF_NAME);
}
/**
 * Implements format definition from BustFormatStrategy
 */
private String format(BustReportStruct[] bust)
{
    return format(bust, getDefaultStyle());
}
/**
 * Implements format definition from BustFormatStrategy
 */
private String format(BustReportStruct[] bust, String styleName)
{
    validateStyle(styleName);

    StringBuffer bustText = new StringBuffer(1000);

    VolumeFormatStrategy volumeFormatter = FormatFactory.getVolumeFormatStrategy();
    ProductClassFormatStrategy productClassFormatter = FormatFactory.getProductClassFormatStrategy();
    ProductFormatStrategy productFormatter = FormatFactory.getProductFormatStrategy();

    if (styleName.equalsIgnoreCase(FULL_INFORMATION_NAME))
    {
        Utility.portWarningPorted(Category + ".format(BustReportStruct[])");                                            // TODO: remove when Jasper port is completed
    }

    int bustReportLength = bust.length;
    for (int i=0; i<bustReportLength; i++)
    {
        if(styleName.equalsIgnoreCase(BRIEF_NAME))
        {
            Product product = null;
            ProductClass productClass = null;

            try
            {
                product = APIHome.findProductQueryAPI().getProductByKey(bust[i].productKey);
                if(product != null)
                {
                    productClass = APIHome.findProductQueryAPI().getProductClassByKey(product.getProductKeysStruct().classKey);
                }
                if(productClass != null)
                {
                    bustText.append(productClassFormatter.format(productClass, productClassFormatter.CLASS_TYPE_NAME)).append(' ');
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
//            catch (Exception e)
//            {
//                GUILoggerHome.find().exception(e);
//            }

            char sideChar = bust[i].side;
            if(product != null)
            {
                bustText.append(productFormatter.format(product)).append(' ');
                // SEDL # SYS008367: it's required that even though the server
                //      sends us Sides.DEFINED and OPPOSITE in the Strategy leg
                //      bust report for Option leg trades, we should display
                //      Sides.BUY and SELL
                if (bust[i].bustReportType == ReportTypes.STRATEGY_LEG_REPORT && product.getProductType() != ProductTypes.STRATEGY)
                {
                    if (sideChar == Sides.DEFINED)
                    {
                        sideChar = Sides.BUY;
                    }
                    else if (sideChar == Sides.OPPOSITE)
                    {
                        sideChar = Sides.SELL;
                    }
                }
            }

            ExchangeFirm anExchangeFrim = ExchangeFirmFactory.createExchangeFirm(bust[i].executingOrGiveUpFirm);

            bustText.append(Utility.sideToString(sideChar)).append(' ');
            bustText.append(volumeFormatter.format(bust[i].bustedQuantity)).append(' ');
            bustText.append("@ ");
            bustText.append(DisplayPriceFactory.create(bust[i].price)).append(' ');
            bustText.append("for firm ").append(anExchangeFrim).append(". ");
            bustText.append("Trade ID: ").append(bust[i].tradeId.highCboeId).append(':').append(bust[i].tradeId.lowCboeId);
        }
        else if(styleName.equalsIgnoreCase(FULL_INFORMATION_NAME))                                                      // TODO: remove when Jasper port is completed
        {
            int column2Margin = 38;
            int sizeFieldAdded = 0;

            ProductClass productClass = null;
            Product product = null;

            try
            {
                bustText.append("Class: ");

                product = APIHome.findProductQueryAPI().getProductByKey(bust[i].productKey);
                if(product != null)
                {
                    productClass = APIHome.findProductQueryAPI().getProductClassByKey(product.getProductKeysStruct().classKey);
                }
                if(productClass != null)
                {
                    sizeFieldAdded = addFieldWithSizeLimit(bustText, productClassFormatter.format(productClass, productClassFormatter.CLASS_TYPE_NAME), column2Margin - 1 - 7, false);
                    bustText = appendSpace(bustText, column2Margin - 7 - sizeFieldAdded);
                }
                else
                {
                    bustText.append("(Not Available)");
                    bustText = appendSpace(bustText, column2Margin - 7 - 15);
                }
            }
            catch(SystemException e)
            {
                GUILoggerHome.find().exception(e.details.message, e);
                bustText.append("(Not Available)");
                bustText = appendSpace(bustText, column2Margin - 7 - 15);
            }
            catch(CommunicationException e)
            {
                GUILoggerHome.find().exception(e.details.message, e);
                bustText.append("(Not Available)");
                bustText = appendSpace(bustText, column2Margin - 7 - 15);
            }
            catch(AuthorizationException e)
            {
                GUILoggerHome.find().exception(e.details.message, e);
                bustText.append("(Not Available)");
                bustText = appendSpace(bustText, column2Margin - 7 - 15);
            }
            catch(DataValidationException e)
            {
                GUILoggerHome.find().exception(e.details.message, e);
                bustText.append("(Not Available)");
                bustText = appendSpace(bustText, column2Margin - 7 - 15);
            }
            catch(NotFoundException e)
            {
                GUILoggerHome.find().exception(e.details.message, e);
                bustText.append("(Not Available)");
                bustText = appendSpace(bustText, column2Margin - 7 - 15);
            }
            catch (Exception e)
            {
                GUILoggerHome.find().exception(e);
                bustText.append("(Not Available)");
                bustText = appendSpace(bustText, column2Margin - 7 - 15);
            }

            bustText.append("Product: ");
            if(product != null)
            {
                bustText.append(productFormatter.format(product)).append('\n');
            }
            else
            {
                bustText.append("(Not Available)").append('\n');
            }

            bustText.append("Side: ");
            sizeFieldAdded = addFieldWithSizeLimit(bustText, Utility.sideToString(bust[i].side), column2Margin - 1 - 6, false);
            bustText = appendSpace(bustText, column2Margin - 6 - sizeFieldAdded);

            bustText.append("Underlying Symbol: ");
            if(productClass != null)
            {
                bustText.append(productFormatter.format(productClass.getUnderlyingProduct())).append('\n');
            }
            else
            {
                bustText.append("(Not Available)").append('\n');
            }

            bustText.append("Busted Vol: ");
            sizeFieldAdded = addFieldWithSizeLimit(bustText, volumeFormatter.format(bust[i].bustedQuantity), column2Margin - 1 - 12, false);
            bustText = appendSpace(bustText, column2Margin - 12 - sizeFieldAdded);

            bustText.append("Reinstate Req. Vol: ").append(volumeFormatter.format(bust[i].reinstateRequestedQuantity)).append('\n');

            bustText.append("Price:  ");
            sizeFieldAdded = addFieldWithSizeLimit(bustText, DisplayPriceFactory.create(bust[i].price).toString(), column2Margin - 1 - 8, false);
            bustText = appendSpace(bustText, column2Margin - 8 - sizeFieldAdded);

            bustText.append("Date/Time: ").append(Utility.toString(bust[i].timeSent, "yyyy/MM/dd HH:mm:ss.S")).append('\n');

            bustText.append("Executing/Give Up Firm: ");
            ExchangeFirm anExchangeFrim = ExchangeFirmFactory.createExchangeFirm(bust[i].executingOrGiveUpFirm);

            sizeFieldAdded = addFieldWithSizeLimit(bustText, anExchangeFrim.toString(), column2Margin - 1 - 24, false);
            bustText = appendSpace(bustText, column2Margin - 24 - sizeFieldAdded);

            bustText.append("Session: ").append(bust[i].sessionName).append('\n');

            bustText.append("User Id: ").append(bust[i].userId).append('\n');

            bustText.append("Trade ID: ").append(bust[i].tradeId.highCboeId).append(':').append(bust[i].tradeId.lowCboeId).append('\n');
        }
    }

    return bustText.toString();
}

/**
 * Implements format definition from BustFormatStrategy
 */
public String format(OrderBustReinstateReportStruct orderBustReinstate)
{
    return format(orderBustReinstate, getDefaultStyle());
}
/**
 * Implements format definition from BustFormatStrategy
 */
public String format(OrderBustReinstateReportStruct orderBustReinstate, String styleName)
{
    validateStyle(styleName);
    StringBuffer orderBustText = new StringBuffer(1000);

    VolumeFormatStrategy volumeFormatter = FormatFactory.getVolumeFormatStrategy();
    ProductClassFormatStrategy productClassFormatter = FormatFactory.getProductClassFormatStrategy();
    ProductFormatStrategy productFormatter = FormatFactory.getProductFormatStrategy();

    if (styleName.equalsIgnoreCase(FULL_INFORMATION_NAME))
    {
        Utility.portWarningPorted(Category + ".format(OrderBustReinstateReportStruct)");                                // TODO: remove when Jasper port is completed
    }

    if(styleName.equalsIgnoreCase(BRIEF_NAME))
    {
        ProductClass productClass = null;
        Product product = null;
        try
        {
            product = APIHome.findProductQueryAPI().getProductByKey(orderBustReinstate.bustReinstatedReport.productKey);
            if(product != null)
            {
                productClass = APIHome.findProductQueryAPI().getProductClassByKey(product.getProductKeysStruct().classKey);
            }
            if(productClass != null)
            {
                orderBustText.append(productClassFormatter.format(productClass, productClassFormatter.CLASS_TYPE_NAME)).append(' ');
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
            orderBustText.append(productFormatter.format(product)).append(' ');
        }

        orderBustText.append(Utility.sideToString(orderBustReinstate.bustReinstatedReport.side)).append(' ');
        orderBustText.append(volumeFormatter.format(orderBustReinstate.bustReinstatedReport.reinstatedQuantity)).append(' ');
        orderBustText.append("@ ");
        orderBustText.append(DisplayPriceFactory.create(orderBustReinstate.bustReinstatedReport.price));
    }
    else if(styleName.equalsIgnoreCase(FULL_INFORMATION_NAME))                                                          // TODO: remove when Jasper port is completed
    {
        int column2Margin = 38;
        int sizeFieldAdded = 0;

        ProductClass productClass = null;
        Product product = null;
        try
        {
            orderBustText.append("Class: ");

            product = APIHome.findProductQueryAPI().getProductByKey(orderBustReinstate.bustReinstatedReport.productKey);
            if(product != null)
            {
                productClass = APIHome.findProductQueryAPI().getProductClassByKey(product.getProductKeysStruct().classKey);
            }
            if(productClass != null)
            {
                sizeFieldAdded = addFieldWithSizeLimit(orderBustText, productClassFormatter.format(productClass, productClassFormatter.CLASS_TYPE_NAME), column2Margin - 1 - 7, false);
                orderBustText = appendSpace(orderBustText, column2Margin - 7 - sizeFieldAdded);
            }
            else
            {
                orderBustText.append("(Not Available)");
                orderBustText = appendSpace(orderBustText, column2Margin - 7 - 15);
            }
        }
        catch(SystemException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
            orderBustText.append("(Not Available)");
            orderBustText = appendSpace(orderBustText, column2Margin - 7 - 15);
        }
        catch(CommunicationException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
            orderBustText.append("(Not Available)");
            orderBustText = appendSpace(orderBustText, column2Margin - 7 - 15);
        }
        catch(AuthorizationException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
            orderBustText.append("(Not Available)");
            orderBustText = appendSpace(orderBustText, column2Margin - 7 - 15);
        }
        catch(DataValidationException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
            orderBustText.append("(Not Available)");
            orderBustText = appendSpace(orderBustText, column2Margin - 7 - 15);
        }
        catch(NotFoundException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
            orderBustText.append("(Not Available)");
            orderBustText = appendSpace(orderBustText, column2Margin - 7 - 15);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e);
            orderBustText.append("(Not Available)");
            orderBustText = appendSpace(orderBustText, column2Margin - 7 - 15);
        }

        orderBustText.append("Product: ");
        if(product != null)
        {
            orderBustText.append(productFormatter.format(product)).append('\n');
        }
        else
        {
            orderBustText.append("(Not Available)").append('\n');
        }

        orderBustText.append("Side: ");
        sizeFieldAdded = addFieldWithSizeLimit(orderBustText, Utility.sideToString(orderBustReinstate.bustReinstatedReport.side), column2Margin - 1 - 6, false);
        orderBustText = appendSpace(orderBustText, column2Margin - 6 - sizeFieldAdded);

        orderBustText.append("Underlying Symbol: ");
        if(productClass != null)
        {
            orderBustText.append(productFormatter.format(productClass.getUnderlyingProduct())).append('\n');
        }
        else
        {
            orderBustText.append("(Not Available)").append('\n');
        }

        orderBustText.append("Busted Vol: ");
        sizeFieldAdded = addFieldWithSizeLimit(orderBustText, volumeFormatter.format(orderBustReinstate.bustReinstatedReport.bustedQuantity), column2Margin - 1 - 12, false);
        orderBustText = appendSpace(orderBustText, column2Margin - 12 - sizeFieldAdded);

        orderBustText.append("Remaining Vol: ").append(volumeFormatter.format(orderBustReinstate.bustReinstatedReport.totalRemainingQuantity)).append('\n');

        orderBustText.append("Reinstated Vol: ");
        sizeFieldAdded = addFieldWithSizeLimit(orderBustText, volumeFormatter.format(orderBustReinstate.bustReinstatedReport.reinstatedQuantity), column2Margin - 1 - 16, false);
        orderBustText = appendSpace(orderBustText, column2Margin - 16 - sizeFieldAdded);

        orderBustText.append("Price:  ").append(DisplayPriceFactory.create(orderBustReinstate.bustReinstatedReport.price).toString()).append('\n');

        orderBustText.append("Session: ").append(orderBustReinstate.bustReinstatedReport.sessionName).append('\n');

        orderBustText.append("Date/Time: ").append(Utility.toString(orderBustReinstate.bustReinstatedReport.timeSent, "yyyy/MM/dd HH:mm:ss.S")).append('\n');
    }

    return orderBustText.toString();
}
/**
 * Implements format definition from BustFormatStrategy
 */
public String format(OrderBustReportStruct orderBust)
{
    return format(orderBust, getDefaultStyle());
}
/**
 * Implements format definition from BustFormatStrategy
 */
public String format(OrderBustReportStruct orderBust, String styleName)
{
    return format(orderBust.bustedReport, styleName);
}
/**
 * Implements format definition from BustFormatStrategy
 */
public String format(QuoteBustReportStruct quoteBust)
{
    return format(quoteBust, getDefaultStyle());
}
/**
 * Implements format definition from BustFormatStrategy
 */
public String format(QuoteBustReportStruct quoteBust, String styleName)
{
    return format(quoteBust.bustedReport, styleName);
}


}
