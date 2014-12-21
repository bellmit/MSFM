package com.cboe.presentation.exampleStructs;

import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiProduct.*;

/**
 * This type was created by Michael Hatmaker.
 */
public class ExampleQuoteDetailStruct {
/**
 * ExampleQuoteDetailStruct constructor comment.
 */
public ExampleQuoteDetailStruct() {
    super();
}
/**
 * This method was created by Michael Hatmaker.
 * @return QuoteDetailStruct
 */
public static QuoteDetailStruct getExampleQuoteDetailStruct() {

    ProductNameStruct productName = ExampleProductNameStruct.getExampleProductNameStructIBMCall();
    QuoteStruct quote = ExampleQuoteStruct.getExampleQuoteStruct();
    QuoteDetailStruct aQuoteDetailStruct = new QuoteDetailStruct();
    aQuoteDetailStruct.productName = ExampleProductNameStruct.getExampleProductNameStructIBMCall();
    aQuoteDetailStruct.quote = ExampleQuoteStruct.getExampleQuoteStruct();

    return aQuoteDetailStruct;
}
}
