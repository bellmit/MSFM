package com.cboe.presentation.exampleStructs;

import com.cboe.idl.cmiQuote.*;

/**
 * This type was created in VisualAge.
 * @author Nick DePasquale
 */
public class ExampleRFQStruct {
/**
 * ExampleRFQStruct constructor comment.
 */
public ExampleRFQStruct() {
    super();
}
/**
 * This method was created in VisualAge.
 * @author Nick DePasquale
 * 
 * @return com.cboe.idl.cmiQuote.RFQStruct
 */
static public RFQStruct getExampleRFQStructIBM() {
RFQStruct aRFQStruct;

    aRFQStruct = new RFQStruct();

    aRFQStruct.entryTime = ExampleTimeStruct.getExampleCurrentTimeStruct();
    aRFQStruct.productKeys = ExampleProductKeysStruct.getExampleProductKeysStructIBMCall();
    aRFQStruct.quantity = 100;
    aRFQStruct.timeToLive = 30;
    
    return aRFQStruct;
}
}
