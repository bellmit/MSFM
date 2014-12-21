package com.cboe.domain.util;

import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiProduct.*;

/**
 * A helper that makes it easy to create valid CORBA structs.  The structs created
 * by the methods of this class have default values for all attributes.  There are
 * also some test methods that can be used to check if a struct is a default struct.
 *
 * @author Connie Liang
 * @author Mike Hasbrouck - 04/04 - Added QuoteStructV3 methods
 */
public class QuoteStructBuilder
{
    /**
     * All methods are static, so no instance is needed.
     *
     * @author Connie Liang
     */
    private QuoteStructBuilder()
    {
      super();
    }
    /**
     * Creates a default instance of a QuoteEntryStruct.
     *
     * @return default instance of struct
     *
     * @author Connie Liang
     */
    public static QuoteEntryStruct buildQuoteEntryStruct()
    {
        QuoteEntryStruct aStruct = new QuoteEntryStruct();

        aStruct.productKey = 0;
        aStruct.sessionName  = "";
        aStruct.bidPrice      = StructBuilder.buildPriceStruct();
        aStruct.bidQuantity   = 0;
        aStruct.askPrice      = StructBuilder.buildPriceStruct();
        aStruct.askQuantity   = 0;
        aStruct.userAssignedId = "";

        return aStruct;
    }

    public static QuoteStruct buildQuoteStruct(QuoteEntryStruct quoteEntry)
    {
        QuoteStruct quote = new QuoteStruct();
        quote.askPrice = quoteEntry.askPrice;
        quote.askQuantity = quoteEntry.askQuantity;
        quote.bidPrice = quoteEntry.bidPrice;
        quote.bidQuantity = quoteEntry.bidQuantity;
        quote.productKey = quoteEntry.productKey;
        quote.quoteKey = 0;
        quote.transactionSequenceNumber = 0;
        quote.userId = "";
        quote.userAssignedId = "";

        return quote;
    }

    public static QuoteStruct buildQuoteStruct()
    {
        QuoteStruct quote = new QuoteStruct();
        quote.askPrice = StructBuilder.buildPriceStruct();
        quote.askQuantity = 0;
        quote.bidPrice = StructBuilder.buildPriceStruct();
        quote.bidQuantity = 0;
        quote.productKey = 0;
        quote.quoteKey = 0;
        quote.transactionSequenceNumber = 0;
        quote.userId = "";
        quote.userAssignedId = "";

        return quote;
    }

    public static QuoteStructV3 buildQuoteStructV3()
       {
           QuoteStructV3 quoteV3 = new QuoteStructV3();
           quoteV3.quote.askPrice = StructBuilder.buildPriceStruct();
           quoteV3.quote.askQuantity = 0;
           quoteV3.quote.bidPrice = StructBuilder.buildPriceStruct();
           quoteV3.quote.bidQuantity = 0;
           quoteV3.quote.productKey = 0;
           quoteV3.quote.quoteKey = 0;
           quoteV3.quote.transactionSequenceNumber = 0;
           quoteV3.quote.userId = "";
           quoteV3.quote.userAssignedId = "";
           quoteV3.quoteUpdateControlId = 0;

           return quoteV3;
       }


    public static QuoteCancelReportStruct buildQuoteCancelReportStruct()
    {
        QuoteCancelReportStruct quoteCancelReport = new QuoteCancelReportStruct();
        quoteCancelReport.productKeys = ClientProductStructBuilder.buildProductKeysStruct();
        quoteCancelReport.productName = ClientProductStructBuilder.buildProductNameStruct();
        quoteCancelReport.cancelReason = 0;
        quoteCancelReport.quoteKey = 0;
        quoteCancelReport.statusChange = 0;

        return quoteCancelReport;
    }

    public static QuoteCancelReportStruct cloneQuoteCancelReportStruct(QuoteCancelReportStruct quoteCancelReport)
    {
        QuoteCancelReportStruct clone = null;
        if ( quoteCancelReport != null )
        {
            clone = new QuoteCancelReportStruct();
            clone.productKeys = ClientProductStructBuilder.cloneProductKeys(quoteCancelReport.productKeys);
            clone.productName = ClientProductStructBuilder.cloneProductName(quoteCancelReport.productName);
            clone.cancelReason = quoteCancelReport.cancelReason;
            clone.quoteKey = quoteCancelReport.quoteKey;
            clone.statusChange = quoteCancelReport.statusChange;
        }
        return clone;
    }

    public static QuoteEntryStruct cloneQuoteEntryStruct(QuoteEntryStruct quoteEntry)
    {
        QuoteEntryStruct clone = null;
        if ( quoteEntry != null )
        {
            clone = new QuoteEntryStruct();
            clone.askPrice = StructBuilder.clonePrice(quoteEntry.askPrice);
            clone.askQuantity = quoteEntry.askQuantity;
            clone.bidPrice = StructBuilder.clonePrice(quoteEntry.bidPrice);
            clone.bidQuantity = quoteEntry.bidQuantity;
            clone.productKey = quoteEntry.productKey;
            clone.sessionName = quoteEntry.sessionName;
            clone.userAssignedId = quoteEntry.userAssignedId;
        }
        return clone;
    }

    public static QuoteDetailStruct[] cloneQuoteDetailStructs(QuoteDetailStruct[] quotes)
    {
        if ( quotes == null )
        {
            return null;
        }
        else
        {
            QuoteDetailStruct[] cloned = new QuoteDetailStruct[quotes.length];
            for ( int i = 0; i < quotes.length; i++ )
            {
                cloned[i] = cloneQuoteDetailStruct(quotes[i]);
            }

            return cloned;
        }
    }

    public static QuoteDetailStruct cloneQuoteDetailStruct(QuoteDetailStruct quote)
    {
        QuoteDetailStruct cloned = null;

        if (quote != null )
        {
            cloned = new QuoteDetailStruct();
            cloned.productKeys = ClientProductStructBuilder.cloneProductKeys(quote.productKeys);
            cloned.productName = ClientProductStructBuilder.cloneProductName(quote.productName);
            cloned.statusChange = quote.statusChange;
            cloned.quote = cloneQuoteStruct(quote.quote);
        }
        return cloned;
    }

     public static QuoteDetailStruct buildQuoteDetailStruct(QuoteStruct theQuoteStruct)
      {
           QuoteDetailStruct ret = new QuoteDetailStruct();

            ret.quote = theQuoteStruct;
            ret.productName = ClientProductStructBuilder.buildProductNameStruct();
            ret.productKeys = ClientProductStructBuilder.buildProductKeysStruct();
            ret.statusChange = StatusUpdateReasons.NEW;
            return ret;
      }

    public static QuoteDetailStruct buildQuoteDetailStruct()
     {
          QuoteDetailStruct ret = new QuoteDetailStruct();

           ret.quote = buildQuoteStruct();
           ret.productName = ClientProductStructBuilder.buildProductNameStruct();
           ret.productKeys = ClientProductStructBuilder.buildProductKeysStruct();
           ret.statusChange = StatusUpdateReasons.NEW;
           return ret;
     }

    public static QuoteStruct cloneQuoteStruct(QuoteStruct quote)
    {
        QuoteStruct cloned = null;

        if (quote != null )
        {
            cloned                           = new QuoteStruct();
            cloned.bidPrice                  = StructBuilder.clonePrice(quote.bidPrice);
            cloned.bidQuantity               = quote.bidQuantity;
            cloned.askPrice                  = StructBuilder.clonePrice(quote.askPrice);
            cloned.askQuantity               = quote.askQuantity;
            cloned.userId                    = quote.userId;
            cloned.productKey                = quote.productKey;
            cloned.quoteKey                  = quote.quoteKey;
            cloned.transactionSequenceNumber = quote.transactionSequenceNumber;
            cloned.userAssignedId = quote.userAssignedId;
            cloned.sessionName = quote.sessionName;
        }
        return cloned;
    }

    public static QuoteStructV3 cloneQuoteStructV3(QuoteStructV3 quoteV3)
    {
        QuoteStructV3 clonedV3 = null;

        if (quoteV3 != null )
        {
            clonedV3                                  = new QuoteStructV3();

            clonedV3.quote                           = new QuoteStruct();
            clonedV3.quote.bidPrice                  = StructBuilder.clonePrice(quoteV3.quote.bidPrice);
            clonedV3.quote.bidQuantity               = quoteV3.quote.bidQuantity;
            clonedV3.quote.askPrice                  = StructBuilder.clonePrice(quoteV3.quote.askPrice);
            clonedV3.quote.askQuantity               = quoteV3.quote.askQuantity;
            clonedV3.quote.userId                    = quoteV3.quote.userId;
            clonedV3.quote.productKey                = quoteV3.quote.productKey;
            clonedV3.quote.quoteKey                  = quoteV3.quote.quoteKey;
            clonedV3.quote.transactionSequenceNumber = quoteV3.quote.transactionSequenceNumber;
            clonedV3.quote.userAssignedId            = quoteV3.quote.userAssignedId;
            clonedV3.quote.sessionName               = quoteV3.quote.sessionName;
            clonedV3.quoteUpdateControlId             = quoteV3.quoteUpdateControlId;
        }
        return clonedV3;
    }
    
    public static QuoteStructV4 cloneQuoteStructV4(QuoteStructV4 quoteV4)
    {
        QuoteStructV4 clonedV4 = null;

        if (quoteV4 != null )
        {
            clonedV4                    = new QuoteStructV4();
            clonedV4.quoteV3            = QuoteStructBuilder.cloneQuoteStructV3(quoteV4.quoteV3);
            
            clonedV4.sellShortIndicator = quoteV4.sellShortIndicator;
        }
        return clonedV4;
    }

    public static RFQStruct cloneRFQStruct(RFQStruct rfq)
    {
        RFQStruct newStruct = null;
        if (rfq != null)
        {
            newStruct = new RFQStruct();
            newStruct.entryTime = StructBuilder.cloneTime(rfq.entryTime);
            newStruct.productKeys = new ProductKeysStruct();
            newStruct.productKeys.classKey = rfq.productKeys.classKey;
            newStruct.productKeys.productKey = rfq.productKeys.productKey;
            newStruct.productKeys.productType = rfq.productKeys.productType;
            newStruct.productKeys.reportingClass = rfq.productKeys.reportingClass;
            newStruct.quantity = rfq.quantity;
            newStruct.rfqType = rfq.rfqType;
            newStruct.sessionName = rfq.sessionName;
            newStruct.timeToLive = rfq.timeToLive;
        }
        return newStruct;
    }

}
