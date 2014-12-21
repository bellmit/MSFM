package com.cboe.presentation.exampleStructs;

/**
 * This type was created in VisualAge.
 * @author Nick DePasquale
 */
import com.cboe.idl.cmiMarketData.*;
 
public class ExampleRecapStruct {
/**
 * Example constructor comment.
 */
public ExampleRecapStruct() {
    super();
}
/**
 * This method was created in VisualAge.
 * @author Nick DePasquale
 * 
 * @return UnderlyingRecapStruct
 */
public static RecapStruct getExampleRecapStruct() {
RecapStruct aRecapStruct;

    aRecapStruct = new RecapStruct();

    aRecapStruct.productKeys = ExampleProductKeysStruct.getExampleProductKeysStructIBMStock();
    aRecapStruct.bidPrice = ExamplePriceStruct.getExamplePriceStruct(98.75);
    aRecapStruct.askPrice = ExamplePriceStruct.getExamplePriceStruct(99.25);

    aRecapStruct.bidSize = 100;
    aRecapStruct.askSize = 30;

    aRecapStruct.bidTime = ExampleTimeStruct.getExampleTimeStruct("12:00:00:50");
    aRecapStruct.askTime = ExampleTimeStruct.getExampleTimeStruct("12:00:02:50");

    aRecapStruct.lastSalePrice =    ExamplePriceStruct.getExamplePriceStruct(99.00);

    aRecapStruct.netChange =    ExamplePriceStruct.getExamplePriceStruct(1.0625);

    aRecapStruct.recapPrefix = "";

    aRecapStruct.tickDirection = '-';

    aRecapStruct.tradeTime = ExampleTimeStruct.getExampleTimeStruct("12:00:01:00");

    aRecapStruct.totalVolume = 1895200;
        
    
    return aRecapStruct;
}
}
