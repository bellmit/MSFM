package com.cboe.presentation.exampleStructs;

import com.cboe.idl.cmiQuote.*;

/**
 * This type was created by Michael Hatmaker.
 */
public class ExampleQuoteStruct {
/**
 * ExampleQuoteStruct constructor comment.
 */
public ExampleQuoteStruct() {
    super();
}
/**
 * This method was created by Michael Hatmaker.
 * @return QuoteStruct
 */
public static QuoteStruct getExampleQuoteStruct() {
    QuoteStruct aQuoteStruct = new QuoteStruct();
    /*
    aQuoteStruct.bidSide = ExampleQuoteSideStruct.getExampleQuoteSideStructBid();
    aQuoteStruct.askSide = ExampleQuoteSideStruct.getExampleQuoteSideStructAsk();
*/
    aQuoteStruct.askPrice = ExamplePriceStruct.getExamplePriceStruct(Math.random() * 10);
    aQuoteStruct.askQuantity = (int) (Math.random() * 15);
    aQuoteStruct.bidPrice = ExamplePriceStruct.getExamplePriceStruct(Math.random() * 10);
    aQuoteStruct.bidQuantity = (int) (Math.random() * 15);


    return aQuoteStruct;
}
}
