package com.cboe.presentation.exampleStructs;

import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiSession.*;

/**
 * This type was created by Michael Hatmaker.
 */
public class ExampleClassStruct {

/**
 * ExampleClassStruct constructor comment.
 */
public ExampleClassStruct() {
    super();
}

static public SessionClassStruct getExampleSessionClassStruct() {
    SessionClassStruct classStruct = new SessionClassStruct();

    classStruct.classStruct = getExampleClassStruct();
    classStruct.classState = com.cboe.idl.cmiConstants.ClassStates.NOT_IMPLEMENTED;
    classStruct.classStateTransactionSequenceNumber = 1;
    classStruct.eligibleSessions = new String[0];
    return classStruct;
}
/**
 * This method was created by Michael Hatmaker.
 * @return com.cboe.presentation.exampleStructs.ExampleClassStruct
 */
static public ClassStruct getExampleClassStruct() {
com.cboe.idl.cmiProduct.EPWStruct[] anEPWStruct = new com.cboe.idl.cmiProduct.EPWStruct[1];
com.cboe.idl.cmiProduct.ProductDescriptionStruct aProductDescriptionStruct = new com.cboe.idl.cmiProduct.ProductDescriptionStruct();

    anEPWStruct[0] = new EPWStruct();

    anEPWStruct[0].maximumAllowableSpread = (double) 100.00;
    anEPWStruct[0].maximumBidRange = (double) 10000.01;
    anEPWStruct[0].minimumBidRange = (double) 0.01;

    aProductDescriptionStruct.baseDescriptionName = "IBM";
    aProductDescriptionStruct.maxStrikePrice = ExamplePriceStruct.getExamplePriceStruct((double) 1000.0);
    aProductDescriptionStruct.minimumAbovePremiumFraction = ExamplePriceStruct.getExamplePriceStruct((double) 0.10);
    aProductDescriptionStruct.minimumStrikePriceFraction =ExamplePriceStruct.getExamplePriceStruct((double) 0.50);
    aProductDescriptionStruct.name = "IBM";
    aProductDescriptionStruct.premiumBreakPoint = ExamplePriceStruct.getExamplePriceStruct100();
    aProductDescriptionStruct.premiumPriceFormat = 6;
    aProductDescriptionStruct.priceDisplayType = 6;
    aProductDescriptionStruct.strikePriceFormat = 6;
    aProductDescriptionStruct.underlyingPriceFormat = 6;


    // need to add this...
    // com.cboe.idl.cmiProduct.ProductDescriptionStruct __productDescription

    //Get an example ProductStruct
    ProductStruct productStruct = ExampleProductStruct.getExampleProductStructIBMCall();
    int classKey = productStruct.productKeys.classKey;
    ClassStruct classStruct = new ClassStruct(classKey,ProductTypes.OPTION,ListingStates.ACTIVE,"IBM",productStruct,"CBOE",ExampleDateStruct.getExampleDateStructJan11999(),ExampleDateStruct.getExampleDateStructJan11999(),ExampleDateTimeStruct.getExampleDateTimeStructJan11999(),ExampleDateTimeStruct.getExampleDateTimeStructJan11999(),anEPWStruct, (double) 2.0, aProductDescriptionStruct,true,new ReportingClassStruct[0]);
    return classStruct;
}
}
