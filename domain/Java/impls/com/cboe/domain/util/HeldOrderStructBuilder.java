/*
 * Created by IntelliJ IDEA.
 * User: huange
 * Date: Oct 17, 2002
 * Time: 2:14:57 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.domain.util;

import com.cboe.idl.cmiIntermarketMessages.HeldOrderStruct;
import com.cboe.idl.cmiIntermarketMessages.ExchangeMarketStruct;
import com.cboe.idl.cmiIntermarketMessages.HeldOrderDetailStruct;
import com.cboe.idl.cmiOrder.OrderStruct;

public class HeldOrderStructBuilder {

    public static HeldOrderStruct buildHeldOrderStruct()
    {
        HeldOrderStruct heldOrder = new HeldOrderStruct();
        heldOrder.currentMarketBest = new ExchangeMarketStruct[1];
        heldOrder.currentMarketBest[0] = MarketDataStructBuilder.buildExchangeMarketStruct();
        heldOrder.order = OrderStructBuilder.buildOrderStruct();

        return heldOrder;
    }

    public static HeldOrderStruct buildHeldOrderStruct(OrderStruct order)
    {
        HeldOrderStruct heldOrder = new HeldOrderStruct();
        heldOrder.currentMarketBest = new ExchangeMarketStruct[1];
        heldOrder.currentMarketBest[0] = MarketDataStructBuilder.buildExchangeMarketStruct();
        heldOrder.order = order;

        return heldOrder;
    }

    public static HeldOrderStruct buildHeldOrderStruct(OrderStruct order, ExchangeMarketStruct[] exchangeMarket)
    {
        HeldOrderStruct heldOrder = new HeldOrderStruct();
        heldOrder.order = order;
        heldOrder.currentMarketBest = exchangeMarket;
        return heldOrder;
    }

    public static HeldOrderStruct buildHeldOrderStruct(ExchangeMarketStruct[] exchangeMarket)
    {
        HeldOrderStruct heldOrder = new HeldOrderStruct();
        heldOrder.order = OrderStructBuilder.buildOrderStruct();
        heldOrder.currentMarketBest = exchangeMarket;
        return heldOrder;
    }

    public static HeldOrderStruct cloneHeldOrderStruct(HeldOrderStruct heldOrder)
    {
        HeldOrderStruct clonedheldOrder = null;
        if (heldOrder != null )
        {
            clonedheldOrder = new HeldOrderStruct();
            clonedheldOrder.order = OrderStructBuilder.cloneOrderStruct(heldOrder.order);
            clonedheldOrder.currentMarketBest = heldOrder.currentMarketBest;
        }
        return clonedheldOrder;
    }

public static HeldOrderDetailStruct[] cloneHeldOrderDetailStructs(HeldOrderDetailStruct[] heldorders)
{
    if ( heldorders == null )
    {
        return null;
    }
    else
    {
        HeldOrderDetailStruct[] cloned = new HeldOrderDetailStruct[heldorders.length];
        for ( int i = 0; i < heldorders.length; i++ )
        {
            cloned[i] = cloneHeldOrderDetailStruct(heldorders[i]);
        }

        return cloned;
    }
}

public static HeldOrderDetailStruct cloneHeldOrderDetailStruct(HeldOrderDetailStruct heldOrderDetail)
{
    HeldOrderDetailStruct clonedHeldOrderDetail = null;
    HeldOrderStruct clonedHeldOrder = null;

    if (heldOrderDetail != null )
    {
        clonedHeldOrderDetail = new HeldOrderDetailStruct();
        clonedHeldOrderDetail.heldOrder = cloneHeldOrderStruct(heldOrderDetail.heldOrder);
        clonedHeldOrderDetail.statusChange = heldOrderDetail.statusChange;
        clonedHeldOrderDetail.productInformation = ClientProductStructBuilder.cloneProductName(heldOrderDetail.productInformation);
    }
    return clonedHeldOrderDetail;
}
}
