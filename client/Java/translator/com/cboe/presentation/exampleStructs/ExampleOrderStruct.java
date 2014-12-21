package com.cboe.presentation.exampleStructs;

/**
 * This type was created in VisualAge.
 * @author Nick DePasquale
 */
import com.cboe.idl.cmiConstants.CoverageTypes;
import com.cboe.idl.cmiConstants.OrderStates;
import com.cboe.idl.cmiConstants.PositionEffects;
import com.cboe.idl.cmiConstants.Sides;
import com.cboe.idl.cmiConstants.Sources;
import com.cboe.idl.cmiConstants.TimesInForce;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;

public class ExampleOrderStruct {
/**
 * ExampleOrderStruct constructor comment.
 */
public ExampleOrderStruct() {
    super();
}
/**
 * This method was created in VisualAge.
 * @author Nick DePasquale
 *
 * @return OrderStruct
 */
public static OrderStruct getExampleOrderStructIBMCall() {

OrderStruct anOrderStruct;

    anOrderStruct = new OrderStruct();

//  anOrderStruct.originalOrder = ExampleOrderEntryStruct.getExampleOrderEntryStructIBMBuyCall();
//  anOrderStruct.firm = "54321";
    anOrderStruct.originalQuantity = 100;
    anOrderStruct.productKey = ExampleProductStruct.getExampleProductStructIBMCall().productKeys.productKey;
    anOrderStruct.side = Sides.BUY;
    anOrderStruct.price = ExamplePriceStruct.getExamplePriceStruct100();
    anOrderStruct.averagePrice = ExamplePriceStruct.getExamplePriceStruct100();
    anOrderStruct.sessionAveragePrice = ExamplePriceStruct.getExamplePriceStruct100();
    anOrderStruct.timeInForce = TimesInForce.DAY;
    anOrderStruct.contingency = ExampleOrderContingencyStruct.getExampleOrderContingencyAON();
    anOrderStruct.optionalData = "";
//  anOrderStruct. = "Other";
    anOrderStruct.positionEffect = PositionEffects.NOTAPPLICABLE;

    anOrderStruct.receivedTime = ExampleDateTimeStruct.getExampleDateTimeStructJan11999();
    anOrderStruct.state = OrderStates.ACTIVE;
    anOrderStruct.tradedQuantity = 0;
    anOrderStruct.cancelledQuantity = 0;
    //anOrderStruct.cancelRequestedQuantity = 0;
    //anOrderStruct.bookedQuantity = 0;
    anOrderStruct.orsId = "ABC123";
//  anOrderStruct.positionEffect = PositionEffects.OPEN;
    anOrderStruct.originator = new ExchangeAcronymStruct("CBOE", "ACR");
    anOrderStruct.userAcronym = new ExchangeAcronymStruct("CBOE", "ACR");
    anOrderStruct.source = Sources.SBT;
    anOrderStruct.coverage = CoverageTypes.COVERED;
    anOrderStruct.account = "12345";
    anOrderStruct.subaccount = "abcde";
    anOrderStruct.orderId = ExampleOrderIdStruct.getExampleOrderIdStructIBMCall();
    anOrderStruct.crossedOrder = ExampleOrderIdStruct.getExampleOrderIdStructIBMCall();
    anOrderStruct.cmta = ExampleExchangeFirm.getExampleDefaultExchangeFirmStruct();
    anOrderStruct.extensions = "";

    return anOrderStruct;
}
}
