package com.cboe.presentation.exampleStructs;

/**
 * This type was created in VisualAge.
 */
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiSession.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiUtil.*;

public class ExampleProductStruct {
/**
 * ExampleProductStruct constructor comment.
 */
private ExampleProductStruct() {
    super();
}

private static SessionProductStruct getExampleSessionProductStruct(ProductStruct product) {
    SessionProductStruct result = new SessionProductStruct();
    result.productState = ProductStates.OPEN;
    result.productStateTransactionSequenceNumber = 1;
    result.sessionName = "W_AM1";
    result.productStruct = product;
    return result;
}

public static SessionProductStruct getExampleSessionProductStructIBMCall() {
    return getExampleSessionProductStruct(getExampleProductStructIBMCall());
}

public static SessionProductStruct getExampleSessionProductStructIBMPut() {
    return getExampleSessionProductStruct(getExampleProductStructIBMPut());
}

/**
 * This method was created in VisualAge.
 * @return ProductNameStruct
 */
public static ProductStruct getExampleProductStructIBMCall() {

ProductStruct aProductStruct = null;

    aProductStruct = new ProductStruct();

    aProductStruct.productKeys = ExampleProductKeysStruct.getExampleProductKeysStructIBMCall();
//  aProductStruct.productType = ProductTypes.OPTION;
    aProductStruct.productName = ExampleProductNameStruct.getExampleProductNameStructIBMCall();
    //aProductStruct.productState = ProductStates.OPEN;
    aProductStruct.description = "IBM JAN 1999 100 CALL";
    aProductStruct.companyName = "IBM";
    aProductStruct.unitMeasure =  "D";
    aProductStruct.standardQuantity = 100;

    DateStruct date = new DateStruct((byte)15,(byte)1,(short)1999);
    aProductStruct.maturityDate = date;
    aProductStruct.activationDate = date;
    aProductStruct.inactivationDate = date;

    DateTimeStruct dateTime = new DateTimeStruct(date, new TimeStruct((byte)1,(byte)1,(byte)1,(byte)1));

    aProductStruct.createdTime = dateTime;
    aProductStruct.lastModifiedTime = dateTime;

    aProductStruct.opraMonthCode = 'A';
    aProductStruct.opraPriceCode = 'A';

    return aProductStruct;
}
/**
 * This method was created in VisualAge.
 * @return ProductNameStruct
 */
public static ProductStruct getExampleProductStructIBMPut() {

ProductStruct aProductStruct = null;

    aProductStruct = new ProductStruct();

    aProductStruct.productKeys = ExampleProductKeysStruct.getExampleProductKeysStructIBMPut();
    //aProductStruct.productType = ProductTypes.OPTION;
    aProductStruct.productName = ExampleProductNameStruct.getExampleProductNameStructIBMPut();
    //aProductStruct.productState = ProductStates.OPEN;
    aProductStruct.description = "IBM JAN 1999 100 PUT";
    aProductStruct.companyName = "IBM";
    aProductStruct.unitMeasure =  "D";
    aProductStruct.standardQuantity = 100;

    DateStruct date = new DateStruct((byte)15,(byte)1,(short)1999);
    aProductStruct.maturityDate = date;
    aProductStruct.activationDate = date;
    aProductStruct.inactivationDate = date;

    DateTimeStruct dateTime = new DateTimeStruct(date, new TimeStruct((byte)1,(byte)1,(byte)1,(byte)1));

    aProductStruct.createdTime = dateTime;
    aProductStruct.lastModifiedTime = dateTime;

    aProductStruct.opraMonthCode = 'A';
    aProductStruct.opraPriceCode = 'A';

    return aProductStruct;
}
/**
 * This method was created in VisualAge.
 * @return ProductNameStruct
 */
public static ProductStruct getExampleProductStructIBMStock() {

ProductStruct aProductStruct = null;

    aProductStruct = new ProductStruct();

    aProductStruct.productKeys = ExampleProductKeysStruct.getExampleProductKeysStructIBMStock();
    //aProductStruct.productType = ProductTypes.EQUITY;
    aProductStruct.productName = ExampleProductNameStruct.getExampleProductNameStructIBMStock();
    //aProductStruct.productState = ProductStates.OPEN;
    aProductStruct.description = "IBM";
    aProductStruct.companyName = "IBM";
    aProductStruct.unitMeasure =  "D";
    aProductStruct.standardQuantity = 100;

    DateStruct date = new DateStruct((byte)15,(byte)1,(short)1999);
    aProductStruct.maturityDate = date;
    aProductStruct.activationDate = date;
    aProductStruct.inactivationDate = date;

    DateTimeStruct dateTime = new DateTimeStruct(date, new TimeStruct((byte)1,(byte)1,(byte)1,(byte)1));

    aProductStruct.createdTime = dateTime;
    aProductStruct.lastModifiedTime = dateTime;

    aProductStruct.opraMonthCode = 'A';
    aProductStruct.opraPriceCode = 'A';

    return aProductStruct;
}
}
