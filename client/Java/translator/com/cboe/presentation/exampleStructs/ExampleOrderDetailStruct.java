package com.cboe.presentation.exampleStructs;

/**
 * This type was created in VisualAge.
 * @author Nick DePasquale
 */
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.cmiOrder.*;
  
public class ExampleOrderDetailStruct {
/**
 * ExampleOrderEntryStruct constructor comment.
 */
public ExampleOrderDetailStruct() {
    super();
}
/**
 * This method was created in VisualAge.
 * @author Nick DePasquale
 * 
 * @return OrderEntryStruct
 */
public static OrderDetailStruct getExampleOrderDetailStructIBMBuyCall() {
OrderDetailStruct anOrderDetailStruct;

    anOrderDetailStruct = new OrderDetailStruct();
    anOrderDetailStruct.orderStruct = ExampleOrderStruct.getExampleOrderStructIBMCall();
    anOrderDetailStruct.productInformation = ExampleProductNameStruct.getExampleProductNameStructIBMCall();
        
    return anOrderDetailStruct;
}
}
