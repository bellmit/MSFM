//
// -----------------------------------------------------------------------------------
// Source file: ManualReportingFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import com.cboe.domain.util.DateWrapper;
import com.cboe.idl.marketData.ManualPriceReportEntryStruct;
import com.cboe.idl.quote.ManualQuoteDetailStruct;
import com.cboe.idl.quote.ManualQuoteStruct;
import com.cboe.interfaces.presentation.common.formatters.DateFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.ManualReportingFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.ProductFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.VolumeFormatStrategy;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import org.omg.CORBA.UserException;

/**
 * Responsible for formatting Quotes
 * @author Troy Wehrle
 */
class ManualReportingFormatter extends Formatter implements ManualReportingFormatStrategy {
    private final String Category = this.getClass().getName();

/**
 */
public ManualReportingFormatter()
{
    super();

    addStyle(BRIEF, BRIEF_DESCRIPTION);
    setDefaultStyle(BRIEF);
}

   /**
    * Implements format definition from QuoteFormatStrategy
    */
    public String format(ManualQuoteStruct manualQuote)
    {
        return format(manualQuote, getDefaultStyle());
    }

    public String format(ManualQuoteStruct manualQuote, String styleName)
    {
        validateStyle(styleName);

        VolumeFormatStrategy volumeFormatter = FormatFactory.getVolumeFormatStrategy();
        ProductFormatStrategy productFormatter = FormatFactory.getProductFormatStrategy();
        DateFormatStrategy dateFormatter = FormatFactory.getDateFormatStrategy();
        StringBuffer manualQuoteText = new StringBuffer(200);

        if(styleName.equalsIgnoreCase(BRIEF))
        {
            try
            {
                if(manualQuote.productKeys.productKey > 0)
                {
                    Product product = APIHome.findProductQueryAPI().getProductByKey(manualQuote.productKeys.productKey);
                    manualQuoteText.append(productFormatter.format(product)).append("; ");
                }
                else
                {
                    manualQuoteText.append("Product not selected. ");
                }

                if(manualQuote.side == com.cboe.idl.cmiConstants.Sides.BID)
                {
                    manualQuoteText.append("Bid ");
                }
                else
                {
                    manualQuoteText.append("Ask ");
                }

                manualQuoteText.append(volumeFormatter.format(manualQuote.size)).append('@');
                manualQuoteText.append(DisplayPriceFactory.create(manualQuote.price)).append(' ');
                DateWrapper dw = new DateWrapper(System.currentTimeMillis());
                manualQuoteText.append("Time:").append(dateFormatter.format(dw.getDate(),
                        DateFormatStrategy.TIME_FORMAT_12_HOURS_SECONDS_STYLE)).append(' ');
            }
            catch(UserException e)
            {
                DefaultExceptionHandlerHome.find().process(e);
            }
        }

        return manualQuoteText.toString();
    }


   /**
    * Implements format definition from QuoteFormatStrategy
    */
    public String format(ManualQuoteDetailStruct manualQuoteDetail)
    {
        return format(manualQuoteDetail, getDefaultStyle());
    }

    public String format(ManualQuoteDetailStruct manualQuoteDetail, String styleName)
    {
        validateStyle(styleName);
        StringBuffer manualQuoteDetailText = new StringBuffer(200);

        if(styleName.equalsIgnoreCase(BRIEF))
        {
            manualQuoteDetailText.append("LocId ").append(manualQuoteDetail.locationId).append(' ');
            manualQuoteDetailText.append("ParId ").append(manualQuoteDetail.parId).append(' ');
            manualQuoteDetailText.append("IP# ").append(manualQuoteDetail.ipAddress).append(' ');
        }
        return manualQuoteDetailText.toString();
    }

    /**
     * Implements format definition from QuoteFormatStrategy
     */
     public String format(ManualPriceReportEntryStruct manualPriceStruct)
     {
         return format(manualPriceStruct, getDefaultStyle());
     }

    public String format(ManualPriceReportEntryStruct manualPriceStruct, String styleName)
    {
        validateStyle(styleName);
        StringBuffer manualPriceStructText = new StringBuffer(200);

        VolumeFormatStrategy volumeFormatter = FormatFactory.getVolumeFormatStrategy();
        ProductFormatStrategy productFormatter = FormatFactory.getProductFormatStrategy();
        DateFormatStrategy dateFormatter = FormatFactory.getDateFormatStrategy();

        if(styleName.equalsIgnoreCase(BRIEF))
        {
            try
            {
                if(manualPriceStruct.productKeys.productKey > 0)
                {
                    Product product = APIHome.findProductQueryAPI().getProductByKey(manualPriceStruct.productKeys.productKey);
                    manualPriceStructText.append(productFormatter.format(product)).append("; ");
                }
                else
                {
                    manualPriceStructText.append("Product not selected. ");
                }

                manualPriceStructText.append(volumeFormatter.format(manualPriceStruct.volume)).append('@');
                manualPriceStructText.append(DisplayPriceFactory.create(manualPriceStruct.price)).append(' ');
                manualPriceStructText.append("Prefix:").append(manualPriceStruct.salePrefix).append(' ');
                DateWrapper dw = new DateWrapper(manualPriceStruct.tradeTime);
                manualPriceStructText.append("Time:").append(dateFormatter.format(dw.getDate(),
                        DateFormatStrategy.TIME_FORMAT_12_HOURS_SECONDS_STYLE)).append(' ');
            }
            catch(UserException e)
            {
                DefaultExceptionHandlerHome.find().process(e);
            }
        }
        return manualPriceStructText.toString();
    }
}
