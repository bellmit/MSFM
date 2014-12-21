package com.cboe.presentation.exampleStructs;

/**
 * This type was created in VisualAge.
 * @author Nick DePasquale
 */
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.cmiConstants.*;
 
public class ExampleProductKeysStruct {
/**
 * ExampleProductKeysStructIBMCall constructor comment.
 */
public ExampleProductKeysStruct() {
    super();
}
/**
 * This method was created in VisualAge.
 * @author Nick DePasquale
 * 
 * @return ProductKeysStruct
 */
static public ProductKeysStruct getExampleProductKeysStructIBMCall() {
ProductKeysStruct aProductKeysStruct;

    aProductKeysStruct = new ProductKeysStruct();

    aProductKeysStruct.classKey = 1;
    aProductKeysStruct.productKey = 1;
    aProductKeysStruct.productType = ProductTypes.OPTION;
    aProductKeysStruct.reportingClass = 1;
    
    return aProductKeysStruct;
}
/**
 * This method was created in VisualAge.
 * @author Nick DePasquale
 * 
 * @return ProductKeysStruct
 */
static public ProductKeysStruct getExampleProductKeysStructIBMPut() {
ProductKeysStruct aProductKeysStruct;

    aProductKeysStruct = new ProductKeysStruct();

    aProductKeysStruct.classKey = 1;
    aProductKeysStruct.productKey = 2;
    aProductKeysStruct.productType = ProductTypes.OPTION;
    aProductKeysStruct.reportingClass = 1;
    
    return aProductKeysStruct;
}
/**
 * This method was created in VisualAge.
 * @author Nick DePasquale
 * 
 * @return ProductKeysStruct
 */
static public ProductKeysStruct getExampleProductKeysStructIBMStock() {
ProductKeysStruct aProductKeysStruct;

    aProductKeysStruct = new ProductKeysStruct();

    aProductKeysStruct.classKey = 101;
    aProductKeysStruct.productKey = 100;
    aProductKeysStruct.productType = ProductTypes.EQUITY;
    aProductKeysStruct.reportingClass = 101;
    
    return aProductKeysStruct;
}
}
