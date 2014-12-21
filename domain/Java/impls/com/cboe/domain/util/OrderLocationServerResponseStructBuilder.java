package com.cboe.domain.util;

import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.order.OrderLocationServerResponseStruct;
import com.cboe.idl.order.OrderLocationSummaryServerResponseStruct;
import com.cboe.idl.order.OrderLocationSummaryStruct;
import com.cboe.idl.order.OrderRoutingStruct;
import com.cboe.idl.util.LocationStruct;
import com.cboe.idl.util.RouteReasonStruct;
import com.cboe.idl.util.ServerTransactionIdStruct;

public class OrderLocationServerResponseStructBuilder
{
    public static OrderLocationServerResponseStruct buildOrdLocationSrvrRespondStruct()
    {
        OrderLocationServerResponseStruct olsrStruct = new OrderLocationServerResponseStruct();
        olsrStruct.transactionId = new ServerTransactionIdStruct();
        olsrStruct.orders = new OrderRoutingStruct[0];
        olsrStruct.totalOrdersCount = 0;
        olsrStruct.pageNum = 0;
        olsrStruct.totalPageCount = 0;

        return olsrStruct;
    }

    public static OrderRoutingStruct buildOrderRoutingStruct()
    {
        OrderRoutingStruct ordRoutingStruct = new OrderRoutingStruct();
        ordRoutingStruct.order = OrderStructBuilder.buildOrderStruct();
        ordRoutingStruct.routeReason = new RouteReasonStruct();
        ordRoutingStruct.routeReason.routeReason = 0;
        ordRoutingStruct.routeReason.messageId = 0;
        ordRoutingStruct.routeReason.routeDescription = "";
        ordRoutingStruct.routeReason.routeReason = 0;
        ordRoutingStruct.routeReason.routeTime = StructBuilder.buildDateTimeStruct();
        
        return ordRoutingStruct;
    }
    
    public static OrderLocationSummaryServerResponseStruct buildOrdLocSummarySrvrResponseStruct()
    {
        OrderLocationSummaryServerResponseStruct olssrStruct = new OrderLocationSummaryServerResponseStruct();
        olssrStruct.transactionId = OrderLocationServerResponseStructBuilder.buildOrdTransactionIdStruct();
        olssrStruct.summary = new OrderLocationSummaryStruct[1];
        olssrStruct.summary[0] = OrderLocationServerResponseStructBuilder.buidOrdLocSummaryStruct();
        
        return olssrStruct;
    }

    public static ServerTransactionIdStruct buildOrdTransactionIdStruct()
    {
        ServerTransactionIdStruct stiStruct = new ServerTransactionIdStruct();
        stiStruct.serverId = "";
        stiStruct.transactionId = "";

        return stiStruct;
    }

    public static OrderLocationSummaryStruct buidOrdLocSummaryStruct()
    {
        OrderLocationSummaryStruct olsStruct = new OrderLocationSummaryStruct();
        olsStruct.locationStruct = OrderLocationServerResponseStructBuilder.buildLocationStruct();
        olsStruct.isLoggedIn = false;
        olsStruct.ordersCount = 0;

        return olsStruct;
    }

    public static LocationStruct buildLocationStruct()
    {
        LocationStruct lStruct = new LocationStruct();
        lStruct.locationType = 0;
        lStruct.location = "";

        return lStruct;
    }
}
