package com.cboe.domain.util;

import com.cboe.idl.order.OrderQueryResultStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.util.ServerResponseStruct;

public class OrderQueryResultsStructBuilder
{
    public static OrderQueryResultStruct buildOrderQueryResultsStruct()
    {
        OrderQueryResultStruct  ordQueryResultStruct = new OrderQueryResultStruct();
        ordQueryResultStruct.orderStructSequence = new OrderStruct[1]; 
        ordQueryResultStruct.orderStructSequence[0] = OrderStructBuilder.buildOrderStruct();
        ordQueryResultStruct.serverResponseStructSequence = new ServerResponseStruct[1];  
        ordQueryResultStruct.serverResponseStructSequence[0] = new ServerResponseStruct();
        
        return ordQueryResultStruct;
    }
   
}
