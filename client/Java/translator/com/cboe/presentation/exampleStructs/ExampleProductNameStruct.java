package com.cboe.presentation.exampleStructs;

/**
 * This type was created in VisualAge.
 */
//import com.cboe.application.interfaces.sbtApplications.*;
//import com.cboe.domainObjects.interfaces.product.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.cmiConstants.*;

public class ExampleProductNameStruct {
/**
 * ExampleProductStruct constructor comment.
 */
private ExampleProductNameStruct() {
    super();
}
/**
 * This method was created in VisualAge.
 * @return ProductNameStruct
 */
public final static ProductNameStruct getExampleProductNameStructIBMCall() {

ProductNameStruct aProductNameStruct = null;

    aProductNameStruct = new ProductNameStruct();

    aProductNameStruct.productSymbol = "";
    aProductNameStruct.reportingClass = "IBM";
    aProductNameStruct.expirationDate = new DateStruct();
    aProductNameStruct.expirationDate.day = 1;
    aProductNameStruct.expirationDate.month = 1;
    aProductNameStruct.expirationDate.year = 1999;
    aProductNameStruct.exercisePrice = new PriceStruct(PriceTypes.VALUED,100,0);
    aProductNameStruct.optionType = OptionTypes.CALL;


    return aProductNameStruct;
}
/**
 * This method was created in VisualAge.
 * @return ProductNameStruct
 */
public final static ProductNameStruct getExampleProductNameStructIBMPut() {

ProductNameStruct aProductNameStruct = null;

    aProductNameStruct = ExampleProductNameStruct.getExampleProductNameStructIBMCall();

    aProductNameStruct.optionType = OptionTypes.PUT;
    
    return aProductNameStruct;
}
/**
 * This method was created in VisualAge.
 * @return ProductNameStruct
 */
public final static ProductNameStruct getExampleProductNameStructIBMStock() {

ProductNameStruct aProductNameStruct = null;

    aProductNameStruct = new ProductNameStruct();

    aProductNameStruct.productSymbol = "IBM";
    aProductNameStruct.reportingClass = "IBM";
    aProductNameStruct.expirationDate = new DateStruct();
    aProductNameStruct.expirationDate.day = 1;
    aProductNameStruct.expirationDate.month = 1;
    aProductNameStruct.expirationDate.year = 1999;
    aProductNameStruct.exercisePrice = new PriceStruct(PriceTypes.VALUED,100,0);
    aProductNameStruct.optionType = OptionTypes.CALL;


    return aProductNameStruct;
}
}
