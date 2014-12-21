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

public class ExampleOrderEntryStruct {
/**
 * ExampleOrderEntryStruct constructor comment.
 */
public ExampleOrderEntryStruct() {
    super();
}
/**
 * This method was created in VisualAge.
 * @author Nick DePasquale
 *
 * @return OrderEntryStruct
 */
public static OrderEntryStruct getExampleOrderEntryStructIBMBuyCall() {
OrderEntryStruct anOrderEntryStruct;

    anOrderEntryStruct = new OrderEntryStruct();
    anOrderEntryStruct.executingOrGiveUpFirm = new ExchangeFirmStruct();

    anOrderEntryStruct.executingOrGiveUpFirm.firmNumber = "54321";
    anOrderEntryStruct.executingOrGiveUpFirm.exchange = "CBOE";
    anOrderEntryStruct.originalQuantity = 100;
    anOrderEntryStruct.productKey = ExampleProductStruct.getExampleProductStructIBMCall().productKeys.productKey;
    anOrderEntryStruct.side = Sides.BUY;
    anOrderEntryStruct.price = ExamplePriceStruct.getExamplePriceStruct100();
    anOrderEntryStruct.timeInForce = TimesInForce.DAY;
    anOrderEntryStruct.contingency = ExampleOrderContingencyStruct.getExampleOrderContingencyAON();
    anOrderEntryStruct.optionalData = "";

    anOrderEntryStruct.positionEffect = PositionEffects.NOTAPPLICABLE;



    return anOrderEntryStruct;
}
}
