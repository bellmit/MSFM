package com.cboe.presentation.exampleStructs;

/**
 * This type was created in VisualAge.
 * @author Nick DePasquale
 */
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.domain.util.*;

public class ExampleOrderIdStruct {
/**
 * ExampleOrderIdStruct constructor comment.
 */
public ExampleOrderIdStruct() {
    super();
}
/**
 * This method was created in VisualAge.
 * @author Nick DePasquale
 *
 * @return com.cboe.interfaces.cmiOrder.OrderIdStruct
 */
public static OrderIdStruct getExampleOrderIdStructIBMCall() {

OrderIdStruct anOrderIdStruct;

    anOrderIdStruct = new OrderIdStruct();

    anOrderIdStruct.executingOrGiveUpFirm = new ExchangeFirmStruct();

    anOrderIdStruct.executingOrGiveUpFirm.exchange = "CBOE";
    anOrderIdStruct.executingOrGiveUpFirm.firmNumber = "FK1";
    anOrderIdStruct.branch = "BId1";
    anOrderIdStruct.branchSequenceNumber = 123;
    anOrderIdStruct.correspondentFirm = "CFK1";
//  anOrderIdStruct.orderDate = ExampleDateStruct.getExampleDateStructJan11999();
    anOrderIdStruct.orderDate = "19990101";

    return anOrderIdStruct;
}
}
