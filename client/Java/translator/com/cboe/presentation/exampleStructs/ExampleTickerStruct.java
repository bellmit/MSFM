package com.cboe.presentation.exampleStructs;

/**
 * This type was created in VisualAge.
 * @author Nick DePasquale
 */
import com.cboe.idl.cmiMarketData.*;
 
public class ExampleTickerStruct {
/**
 * Example constructor comment.
 */
public ExampleTickerStruct() {
    super();
}
/**
 * This method was created in VisualAge.
 * @author Nick DePasquale
 * 
 * @return UnderlyingRecapStruct
 */
public static TickerStruct getExampleTickerStruct() {
TickerStruct aTickerStruct;

    aTickerStruct = new TickerStruct();

    aTickerStruct.productKeys = ExampleProductKeysStruct.getExampleProductKeysStructIBMStock();
    aTickerStruct.exchangeSymbol = "N";
    aTickerStruct.lastSalePrice =   ExamplePriceStruct.getExamplePriceStruct(99.00);
    aTickerStruct.lastSaleVolume = 10;
    aTickerStruct.salePostfix = "";
    aTickerStruct.salePrefix = "";
    
    return aTickerStruct;
}
}
