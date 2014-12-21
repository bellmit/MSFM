package com.cboe.domain.util;

import com.cboe.idl.cmiOrder.CancelRequestStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.order.CancelReplaceRoutingStruct;
import com.cboe.idl.order.CancelRoutingStruct;
import com.cboe.idl.order.ManualContraBrokerStruct;
import com.cboe.idl.order.ManualFillRoutingStruct;
import com.cboe.idl.order.ManualFillStruct;
import com.cboe.idl.order.ManualFillTimeoutRoutingStruct;
import com.cboe.idl.order.ManualLegMarket;
import com.cboe.idl.order.ManualMarketDataStruct;
import com.cboe.idl.order.ManualOrderTimeoutRoutingStruct;
import com.cboe.idl.order.OrderHandlingInstructionStruct;
import com.cboe.idl.order.OrderRoutingStruct;
import com.cboe.idl.util.RouteReasonStruct;

public class RoutingStructBuilder
{
    private RoutingStructBuilder()
    {
        super();
    }

    public static CancelRoutingStruct buildCancelRoutingStruct()
    {
        CancelRoutingStruct cancelRequest = new CancelRoutingStruct();
        cancelRequest.userId = "";        
        cancelRequest.productKeys = ProductStructBuilder.buildProductKeysStruct();
        cancelRequest.cancelRequest = buildCancelRequestStruct();
        cancelRequest.order = OrderStructBuilder.buildOrderStruct();
        cancelRequest.routeReason = buildRouteReasonStruct();
        return cancelRequest;
    }
    
    public static CancelReplaceRoutingStruct buildCancelReplaceRoutingStruct()
    {
        CancelReplaceRoutingStruct cancelReplaceRequest = new CancelReplaceRoutingStruct();
        cancelReplaceRequest.productKeys = ProductStructBuilder.buildProductKeysStruct();
        cancelReplaceRequest.cancelRequest = buildCancelRequestStruct();       
        cancelReplaceRequest.originalOrder = OrderStructBuilder.buildOrderStruct();
        cancelReplaceRequest.replacementOrder = OrderStructBuilder.buildOrderStruct();
        cancelReplaceRequest.routeReason = buildRouteReasonStruct();
        return cancelReplaceRequest;
    }
    
    public static RouteReasonStruct buildRouteReasonStruct()
    {
        RouteReasonStruct routeReasonInfo = new RouteReasonStruct();
        routeReasonInfo.routeReason = 0;
        routeReasonInfo.routeDescription = "Unknown route reason";
        routeReasonInfo.messageId = 0;
        routeReasonInfo.routeTime = StructBuilder.buildDateTimeStruct(); 
        routeReasonInfo.routeTime = DateWrapper.convertToDateTime(System.currentTimeMillis());
        return routeReasonInfo;        
    }
    
    public static CancelRequestStruct buildCancelRequestStruct()
    {
        CancelRequestStruct cancelRequest = new CancelRequestStruct();
        cancelRequest.orderId = OrderStructBuilder.buildOrderIdStruct();
        cancelRequest.sessionName = "";
        cancelRequest.userAssignedCancelId = "";
        return cancelRequest;
    }    
    
    public static ManualOrderTimeoutRoutingStruct buildManualOrderTimeoutRoutingStruct()
    {
        ManualOrderTimeoutRoutingStruct manualOrderTimeout = new ManualOrderTimeoutRoutingStruct();
        manualOrderTimeout.orderHandlingStruct = new OrderHandlingInstructionStruct();
        manualOrderTimeout.orderRoutingStruct = buildOrderRoutingStruct();
        manualOrderTimeout.timeoutTime = StructBuilder.buildDateTimeStruct();
        return manualOrderTimeout;
    }
    
    public static OrderRoutingStruct buildOrderRoutingStruct()
    {
        OrderRoutingStruct orderRoutingStruct = new OrderRoutingStruct();
        orderRoutingStruct.order = OrderStructBuilder.buildOrderStruct();
        orderRoutingStruct.routeReason = buildRouteReasonStruct();
        return orderRoutingStruct;
    }
      
    public static ManualFillTimeoutRoutingStruct buildManualFillTimeoutRoutingStruct()
    {
        ManualFillTimeoutRoutingStruct manualFillTimeout = new ManualFillTimeoutRoutingStruct();
        manualFillTimeout.cboeId = new CboeIdStruct();
        manualFillTimeout.manualFillRouteMsg = buildManualFillRoutingStruct();  
        return manualFillTimeout;
    }
    
    public static ManualFillRoutingStruct buildManualFillRoutingStruct()
    {
        ManualFillRoutingStruct manualFillRouting = new ManualFillRoutingStruct();
        
        manualFillRouting.manualFillInfo = new ManualFillStruct();
        manualFillRouting.manualFillInfo.orderId = OrderStructBuilder.buildOrderIdStruct();
        manualFillRouting.manualFillInfo.productKey = 0;
        manualFillRouting.manualFillInfo.sessionName = "";
        manualFillRouting.manualFillInfo.tradeDateTime = 0;
        manualFillRouting.manualFillInfo.tradeBookPrice = StructBuilder.buildPriceStruct();
        manualFillRouting.manualFillInfo.executionPrice = StructBuilder.buildPriceStruct();
        manualFillRouting.manualFillInfo.tradeBookSize = 0;
        manualFillRouting.manualFillInfo.tradedQuantity = 0;
        manualFillRouting.manualFillInfo.contraBrokers = buildManualContraBrokerSequence();
        manualFillRouting.manualFillInfo.marketDataWhenReceived = new ManualMarketDataStruct();
        manualFillRouting.manualFillInfo.marketDataWhenTraded = new ManualMarketDataStruct();
        manualFillRouting.manualFillInfo.marketDataWhenSelected = new ManualMarketDataStruct();
        manualFillRouting.manualFillInfo.highLowNbboBidAskPrice = StructBuilder.buildPriceStruct();
        manualFillRouting.manualFillInfo.highLowCboeBidAskPrice = StructBuilder.buildPriceStruct();
        manualFillRouting.manualFillInfo.nbboAskExchanges = new String[ 0 ];
        manualFillRouting.manualFillInfo.nbboBidExchanges = new String[ 0 ];
        manualFillRouting.manualFillInfo.nbboTime = 0;
        manualFillRouting.manualFillInfo.cboeTime = 0;
        manualFillRouting.manualFillInfo.awayExchange = "";
        manualFillRouting.manualFillInfo.fadeExchange = "";
        manualFillRouting.manualFillInfo.lastSaleIndicator = "";
        manualFillRouting.manualFillInfo.linkageFillIndicator = ' ';
        manualFillRouting.manualFillInfo.mmqAccount = "";
        manualFillRouting.manualFillInfo.marketabilityIndicator = ' ';
        manualFillRouting.manualFillInfo.legRatios =  new int[0];
        manualFillRouting.manualFillInfo.legTradedQuantities = new int[0];
        manualFillRouting.manualFillInfo.legExecutionPrices = buildPriceStructSequence();
        manualFillRouting.manualFillInfo.theoryExecutionPrice = StructBuilder.buildPriceStruct();
        manualFillRouting.manualFillInfo.cboeQuoteInfo = new ManualLegMarket[0];
        manualFillRouting.manualFillInfo.tradeBookInfo = new ManualLegMarket[0];
         manualFillRouting.manualFillInfo.tradeBookInfoWhenReceived = buildManualLegMarketSequence();
        manualFillRouting.manualFillInfo.tradeBookInfoWhenTraded = buildManualLegMarketSequence();
        manualFillRouting.manualFillInfo.tradeBookInfoWhenSelected = buildManualLegMarketSequence();
        manualFillRouting.manualFillInfo.tradeBookInfoHighLow = buildManualLegMarketSequence();
        manualFillRouting.manualFillInfo.parBroker = "";        
        manualFillRouting.routeReason = buildRouteReasonStruct();
        return manualFillRouting;
    }
    
    private static ManualContraBrokerStruct[] buildManualContraBrokerSequence()
    {
        ManualContraBrokerStruct contraBroker = new ManualContraBrokerStruct();
        contraBroker.exchange = "";
        contraBroker.broker = "";
        contraBroker.firm = "";
        contraBroker.tradedQuantity = 0;
        contraBroker.legTradedQuantities = new int[0];
        ManualContraBrokerStruct[] contraBrokers = {contraBroker};        
        return contraBrokers;
    }

    public static ManualLegMarket[] buildManualLegMarketSequence()
    {
        ManualLegMarket manualLegMarket = new ManualLegMarket();
        manualLegMarket.askPrice = StructBuilder.buildPriceStruct();
        manualLegMarket.bidPrice = StructBuilder.buildPriceStruct();;
        manualLegMarket.askQuantity = 0;
        manualLegMarket.bidQuantity = 0;
        ManualLegMarket[] manualLegMarkets = {manualLegMarket};
        return manualLegMarkets;
    }
    
    public static PriceStruct[] buildPriceStructSequence()
    {
        PriceStruct price = StructBuilder.buildPriceStruct();
        PriceStruct[] prices = {price};
        return prices;
    }
}