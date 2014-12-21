package com.cboe.interfaces.domain;

import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;

public interface OrderIdGeneratorHome
{
    public static final String HOME_NAME = "OrderIdGeneratorHome";
    public OrderIdStruct generateOrderId(OrderStruct anOrder);
    public OrderIdStruct generateOrderId(ExchangeFirmStruct executingOrGiveUpFirm);
}
