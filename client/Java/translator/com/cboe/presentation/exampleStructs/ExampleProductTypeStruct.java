package com.cboe.presentation.exampleStructs;

import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiConstants.*;

/**
 * This type was created by Michael Hatmaker.
 */
public class ExampleProductTypeStruct {
/**
 * ExampleProductTypeStruct constructor comment.
 */
public ExampleProductTypeStruct() {
    super();
}
/**
 * This method was created by Michael Hatmaker.
 * @return ProductTypeStruct
 */
public static ProductTypeStruct getExampleProductTypeStruct() {
    ProductTypeStruct productTypeStruct = new ProductTypeStruct();

    //productTypeStruct.activeForTrading = true;
    productTypeStruct.createdTime = ExampleDateTimeStruct.getExampleDateTimeStructJan11999();
    productTypeStruct.lastModifiedTime = ExampleDateTimeStruct.getExampleDateTimeStructJan11999();
    productTypeStruct.description = "Options on equities";
    productTypeStruct.name = "Option";
    productTypeStruct.type = ProductTypes.OPTION;

    return productTypeStruct;
}
}
