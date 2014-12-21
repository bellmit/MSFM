package com.cboe.domain.util;

import com.cboe.idl.trade.AtomicTradeStruct;
import com.cboe.idl.trade.TradeReportStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.constants.TradeReportEntryTypes;
import com.cboe.idl.cmiIntermarketMessages.SatisfactionAlertStruct;
import com.cboe.idl.cmiIntermarketMessages.AlertHdrStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiConstants.AlertTypes;
import com.cboe.idl.cmiConstants.PriceTypes;

/**
 * A helper that makes it easy to create valid Alert structs.
 *
 * @author Emily Huang
 */
public class AlertStructBuilder
{
    /**
     * All methods are static, so no instance is needed.
     */
    private AlertStructBuilder()
    {
        super();
    }

    /**
     * Creates a default instance of a SatisfactionAlertStruct.
     *
     * @return default instance of struct
     */
    public static SatisfactionAlertStruct buildSatisfactionAlertStruct()
    {
        SatisfactionAlertStruct struct = new SatisfactionAlertStruct();
        struct.alertHdr = buildAlertHdrStruct();
        struct.alertHdr.alertType = AlertTypes.SATISFACTION_ALERT;
        struct.extensions = "";
        struct.lastSale = MarketDataStructBuilder.buildTickerStruct(ClientProductStructBuilder.buildProductKeysStruct());
        struct.side = 'B';
        struct.tradedThroughOrders = new OrderIdStruct[1];
        struct.tradedThroughOrders[0] = OrderStructBuilder.buildOrderIdStruct();
        struct.tradedThroughPrice = new PriceStruct(PriceTypes.VALUED, 2, 500000000);
        struct.tradedThroughquantity = 10;
        return struct;
    }

    public static AlertHdrStruct buildAlertHdrStruct()
    {
        AlertHdrStruct struct = new AlertHdrStruct();
        struct.alertCreationTime = StructBuilder.buildDateTimeStruct();
        struct.alertId = StructBuilder.buildCboeIdStruct();
        return struct;
    }

}
