package com.cboe.application.cas;

/**
 * This class is the CAS Callback clients simulator
 *
 * @author Jeff Illian
 */
/**
 * This class is the CAS client event consumer simulator which
 * implements all the cmiCallback consumer interfaces.
 * @author Jeff Illian
 * @author Connie Feng
 */

import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiAdmin.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.cmiSession.*;


import com.cboe.idl.cmiSession.TradingSessionStateStruct;


import com.cboe.interfaces.callback.*;

import com.cboe.application.test.*;
import com.cboe.domain.util.ReflectiveStructBuilder;

public class TestCallback implements ClassStatusConsumer,
                                    CurrentMarketConsumer,
                                    NBBOConsumer,
                                    ExpectedOpeningPriceConsumer,
//                                    OrderFilledReportConsumer,
//                                    OrderCanceledReportConsumer,
                                    OrderStatusConsumer,
                                    OrderStatusV2Consumer,
                                    ProductStatusConsumer,
//                                    QuoteFilledReportConsumer,
                                    QuoteStatusConsumer,
                                    QuoteStatusV2Consumer,
                                    RecapConsumer,
                                    RFQConsumer,
                                    TickerConsumer,
                                    TradingSessionStatusConsumer,
                                    UserSessionAdminConsumer,
                                    StrategyStatusConsumer,
                                    com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumerOperations,
                                    com.cboe.idl.cmiCallbackV2.CMINBBOConsumerOperations,
                                    com.cboe.idl.cmiCallbackV2.CMIRecapConsumerOperations,
                                    com.cboe.idl.cmiCallbackV2.CMITickerConsumerOperations,
                                    com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumerOperations,
                                    com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumerOperations,
                                    com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumerOperations,
                                    com.cboe.idl.cmiCallbackV2.CMIRFQConsumerOperations,
                                    com.cboe.idl.cmiCallbackV2.CMILockedQuoteStatusConsumerOperations,
                                    com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumerOperations

//                                    QuoteBustReportConsumer,
//                                    OrderBustReportConsumer
{

    // _CMICurrentMarketConsumerOperations  TESTED
    public void acceptCurrentMarket(CurrentMarketStruct[] marketData) {
        System.out.println();
        System.out.println("TestNullValues CurrentMarketStruct: " + ReflectiveStructTester.testNullStruct(marketData));
        System.out.println("< --- Got market data on callback --- > " + marketData[0].productKeys.productKey);
        System.out.println();
//        ReflectiveStructTester.printStruct(marketData, "CurrentMarketDataStruct");
    }

    // _CMICurrentMarketConsumerOperations  TESTED
    public void acceptNBBO(NBBOStruct[] nbbo) {
        System.out.println();
        System.out.println("TestNullValues NBBOStruct: " + ReflectiveStructTester.testNullStruct(nbbo));
        System.out.println("< --- Got NBBO data on callback --- > " + nbbo[0].productKeys.productKey);
        System.out.println();
//        ReflectiveStructTester.printStruct(nbbo, "NBBOStruct");
    }

    //_CMIOrderStatusConsumerOperations TESTED
    public void acceptOrderUpdate(OrderDetailStruct[] orders) {
        System.out.println();
        System.out.println("TestNullValues OrderDetailStruct: " + ReflectiveStructTester.testNullStruct(orders));
        System.out.println("< --- Got updated order!!! ---- > " + orders[0].orderStruct.userId + " "
            + orders[0].orderStruct.orderId.executingOrGiveUpFirm + " "
            + orders[0].orderStruct.orderId.branch + " "
            + orders[0].orderStruct.orderId.branchSequenceNumber );
        System.out.println();
//        ReflectiveStructTester.printStruct(orders, "OrderDetailStruct");
    }

    //_CMIQuoteStatusConsumerOperations TESTED
    public void acceptQuoteUpdate(QuoteDetailStruct[] quotes)
    {
        System.out.println();
        System.out.println("TestNullValues QuoteDetailStruct: " + ReflectiveStructTester.testNullStruct(quotes));
        System.out.println("< --- Got updated quote!!! ---- > " + quotes[0].quote.userId + " "
            + quotes[0].quote.productKey );
        System.out.println();
 //       ReflectiveStructTester.printStruct(quotes, "QuoteDetailStruct");
    }

    //_CMIRFQConsumerOperations TESTED
    public void acceptRFQ(RFQStruct rfq)
    {
        System.out.println();
        System.out.println("TestNullValues RFQStruct: " + ReflectiveStructTester.testNullStruct(rfq));
        System.out.println("< --- Got acceptRFQ!!! ---- > "  );
        System.out.println();
 //       ReflectiveStructTester.printStruct(rfq, "RFQStruct");
    }

    //_CMIOrderFilledReportConsumerOperations TESTED
    public void acceptOrderFilledReport(OrderFilledReportStruct filledReport )
    {
        System.out.println();
        System.out.println("TestNullValues OrderFilledReportStruct: " + ReflectiveStructTester.testNullStruct(filledReport));
        System.out.println("< --- Got acceptOrderFilledReport !!! ---- > " );
        System.out.println();
 //       ReflectiveStructTester.printStruct(filledReport, "OrderFilledReportStruct");
    }

    //_CMIQuoteFilledReportConsumerOperations TESTED
    public void acceptQuoteFilledReport(QuoteFilledReportStruct filledReport )
    {
        System.out.println();
        System.out.println("TestNullValues QuoteFilledReportStruct: " + ReflectiveStructTester.testNullStruct(filledReport));
        System.out.println("< --- Got acceptQuoteFilledReport !!! ---- > " + filledReport.quoteKey);
        System.out.println();
//        ReflectiveStructTester.printStruct(filledReport, "QuoteFilledReportStruct");
    }

    //_CMIProductStatusConsumerOperations TESTED
    public void setProductState(ProductStateStruct[] productState)
    {
        System.out.println();
        System.out.println("TestNullValues ProductStateStruct: " + ReflectiveStructTester.testNullStruct(productState));
        System.out.println("< --- Got setProductState !!! ---- > " + productState[0].productState );
        System.out.println();
//        ReflectiveStructTester.printStruct(productState, "ProductStateStruct");
    }


    //_CMIRecapConsumerOperations TESTED
    public void acceptRecap(RecapStruct[] underlyingRecap)
    {
        System.out.println();
        System.out.println("TestNullValues RecapStruct: " + ReflectiveStructTester.testNullStruct(underlyingRecap));
        System.out.println("< --- Got acceptUnderlyingRecap !!! ---- > " + underlyingRecap.length );
        System.out.println();
//        ReflectiveStructTester.printStruct(underlyingRecap, "RecapStruct");
    }

    //_CMIUnderlyingTickerConsumerOperations TESTED
    public void acceptTicker(TickerStruct[] underlyingTicker)
    {
        System.out.println();
        System.out.println("TestNullValues TickerStruct: " + ReflectiveStructTester.testNullStruct(underlyingTicker));
        System.out.println("< --- Got acceptUnderlyingTicker !!! ---- > " + underlyingTicker.length );
        System.out.println();
//        ReflectiveStructTester.printStruct(underlyingTicker, "TickerStruct");
    }

    public void acceptExpectedOpeningPrice(ExpectedOpeningPriceStruct expectedOpeningPrice) {
        System.out.println();
        System.out.println("TestNullValues ExpectedOpeningPriceStruct: " + ReflectiveStructTester.testNullStruct(expectedOpeningPrice));
        System.out.println("< --- Received Expected Opening Price !!! ---- > " + expectedOpeningPrice );
        System.out.println();
//        ReflectiveStructTester.printStruct(expectedOpeningPrice, "ExpectedOpeningPriceStruct");
    }

//    public void acceptOrderStatus(short statusChange, OrderDetailStruct[] orders) {
    public void acceptOrderStatus(OrderDetailStruct[] orders) {
        System.out.println();
        System.out.println("TestNullValues OrderDetailStruct: " + ReflectiveStructTester.testNullStruct(orders));
        System.out.println("< --- Received Order Status: ---- > " + orders[0] );
        System.out.println();
//        ReflectiveStructTester.printStruct(orders, "OrderDetailStruct");
    }

    public void updateProduct(SessionProductStruct updatedProduct) {
        System.out.println();
        System.out.println("TestNullValues ProductStruct: " + ReflectiveStructTester.testNullStruct(updatedProduct));
        System.out.println("< --- Received Update Product notice ---- > " + updatedProduct );
        System.out.println();
//        ReflectiveStructTester.printStruct(updatedProduct, "ProductStruct");
    }

    public void updateProductClass(SessionClassStruct updatedClass) {
        System.out.println();
        System.out.println("TestNullValues ClassStruct: " + ReflectiveStructTester.testNullStruct(updatedClass));
        System.out.println("< --- Received Update Product Class notice ---- > " + updatedClass );
        System.out.println();
//        ReflectiveStructTester.printStruct(updatedClass, "ClassStruct");
    }

    public void acceptProductState(ProductStateStruct[] productState) {
        System.out.println();
        System.out.println("TestNullValues ProductStateStruct: " + ReflectiveStructTester.testNullStruct(productState));
        System.out.println("< --- Received Product State notice by callback ---- > " + productState );
        System.out.println();
//        ReflectiveStructTester.printStruct(productState, "ProductStateStruct");
    }

    public void acceptClassState(ClassStateStruct classState) {
        System.out.println();
        System.out.println("TestNullValues ClassStateStruct: " + ReflectiveStructTester.testNullStruct(classState));
        System.out.println("< --- Received Class State notice ---- > " + classState );
        System.out.println();
//        ReflectiveStructTester.printStruct(classState, "ClassStateStruct");
    }

//    public void acceptQuoteStatus(short statusChange, QuoteDetailStruct[] quotes) {
    public void acceptQuoteStatus(QuoteDetailStruct[] quotes) {
        System.out.println();
        System.out.println("TestNullValues QuoteDetailStruct: " + ReflectiveStructTester.testNullStruct(quotes));
        System.out.println("< --- Received Quote Status: ---- > " + quotes[0] );
        System.out.println();
//        ReflectiveStructTester.printStruct(quotes, "QuoteDetailStruct");
    }

  public HeartBeatStruct acceptHeartBeat(HeartBeatStruct heartbeat) {
/*
        System.out.println();
        System.out.println("TestNullValues HeartBeatStruct: " + ReflectiveStructTester.testNullStruct(heartbeat));
        System.out.println("< --- Received Heartbeat notice ---- > " + heartbeat.toString() );
        System.out.println();
*/
        return heartbeat;
  }

  public void acceptLogout(String reason) {
        System.out.println();
        System.out.println("< --- Received Logout notice ---- > " + reason );
        System.out.println();
        System.exit(1);
  }

    public void acceptTextMessage( MessageStruct message )
    {
        System.out.println();
        System.out.println("TestNullValues MessageStruct: " + ReflectiveStructTester.testNullStruct(message));
        System.out.println("< --- Received Text Message ---- > " + message.subject );
        System.out.println("< ---                       ---- > " + message.messageText );
        System.out.println();
    }

  public void acceptAuthenticationNotice() {
        System.out.println();
        System.out.println("< --- Received acceptAuthenticationNotice notice ---- > ");
        System.out.println();
  }

  public void acceptClassState(ClassStateStruct[] classState) {
        System.out.println();
        System.out.println("TestNullValues ClassStateStruct: " + ReflectiveStructTester.testNullStruct(classState));
        System.out.println("< --- Received Class State notice ---- > " + classState );
        System.out.println();
//        ReflectiveStructTester.printStruct(classState, "ClassStateStruct");
  }

    public void updateProductStrategy(SessionStrategyStruct[] updatedStrategy) {
        System.out.println();
        System.out.println("TestNullValues StrategyStruct: " + ReflectiveStructTester.testNullStruct(updatedStrategy));
        System.out.println("< --- Received ProductStrategy notice ---- > " + updatedStrategy[0].sessionProductStruct.productStruct.productKeys.productKey );
        System.out.println();
//        ReflectiveStructTester.printStruct(updatedStrategy, "StrategyStruct");
    }

    public void acceptTradingSessionState(TradingSessionStateStruct parm1) {
        //Implement this com.cboe.idl.cmiCallback._CMITradingSessionStatusConsumerOperations method;
        System.out.println();
        System.out.println("TestNullValues TradingSessionStateStruct: " + ReflectiveStructTester.testNullStruct(parm1));
        System.out.println("< --- Received Trading Session State notice ---- > " + parm1 );
        System.out.println();
//        ReflectiveStructTester.printStruct(parm1, "TradingSessionStateStruct");
    }

//    public void acceptOrderCanceledReport(CancelReportStruct CancelReportStruct)
    public void acceptOrderCanceledReport(OrderCancelReportStruct cancelReportStruct)
    {
        System.out.println();
        System.out.println("TestNullValues CancelReportStruct: " + ReflectiveStructTester.testNullStruct(cancelReportStruct));
        System.out.println("< --- Received Order Canceled Report notice ---- > " + cancelReportStruct.cancelledOrder.orderStruct.orderId.executingOrGiveUpFirm);
        System.out.println();
//        ReflectiveStructTester.printStruct(CancelReportStruct, "CancelReportStruct");
    }

    public void acceptCallbackRemoval(CallbackInformationStruct callback, String reason, int errorCode)
    {
        System.out.println();
        System.out.print("TestCallback::acceptCallbackDeregistration::" );
        ReflectiveStructTester.printStruct(callback, "");
        System.out.println("TestCallback::acceptCallbackDeregistration::reason::" + reason);
        System.out.println("TestCallback::acceptCallbackDeregistration::errorCode::" + errorCode);
    }
    public void acceptQuoteBustReport(QuoteBustReportStruct quoteBusted) {
        System.out.println();
        System.out.println("TestNullValues QuoteBustReportStruct: " + ReflectiveStructTester.testNullStruct(quoteBusted));
        System.out.println("< --- Received QuoteBustReportStruct notice ---- > " + quoteBusted.quoteKey );
        System.out.println();
    }

    public void acceptOrderBustReinstateReport(OrderBustReinstateReportStruct reinstatedOrder) {
        System.out.println();
        System.out.println("TestNullValues OrderBustReinstateReportStruct: " + ReflectiveStructTester.testNullStruct(reinstatedOrder));
        System.out.println("< --- Received OrderBustReinstateReportStruct notice ---- > " + reinstatedOrder.reinstatedOrder.orderStruct.orderId.executingOrGiveUpFirm );
        System.out.println();
    }

    public void acceptOrderBustReport(OrderBustReportStruct bustedOrder) {
        System.out.println();
        System.out.println("TestNullValues OrderBustReportStruct: " + ReflectiveStructTester.testNullStruct(bustedOrder));
        System.out.println("< --- Received OrderBustReportStruct notice ---- > " + bustedOrder.bustedOrder.orderStruct.orderId.executingOrGiveUpFirm);
        System.out.println();
    }

    public void acceptNewOrder(OrderDetailStruct order)
    {
        System.out.println();
        System.out.println("TestNullValues OrderDetailStruct: " + ReflectiveStructTester.testNullStruct(order));
        System.out.println("< --- Got new order!!! ---- > " + order.orderStruct.userId + " "
            + order.orderStruct.orderId.executingOrGiveUpFirm + " "
            + order.orderStruct.orderId.branch + " "
            + order.orderStruct.orderId.branchSequenceNumber );
        System.out.println();
    }

    public void acceptQuoteCancelReport(QuoteCancelReportStruct struct)
    {
        System.out.println();
        System.out.println("TestNullValues QuoteCancelReportStruct: " + ReflectiveStructTester.testNullStruct(struct));
        System.out.println("< --- Received QuoteCancelReportStruct notice ---- > " + struct.quoteKey);
        System.out.println();
    }

    public void acceptCurrentMarket(com.cboe.idl.cmiMarketData.CurrentMarketStruct[] currentMarket, int queueDepth, short queueAction)
    {
        System.out.println("acceptCurrentMarket v2: l=" + currentMarket.length + " Q=" + queueDepth + " action="+queueAction
                + " classkey="+currentMarket[0].productKeys.classKey);

        try {
            Thread.sleep(500);
        }
        catch(Exception e){e.printStackTrace();}
//        for(int i=0; i<currentMarket.length; i++)
//        {
//           com.cboe.application.test.ReflectiveStructTester.printStruct(currentMarket[i],"currentMarket["+i+"]:");
//        }
    }

    public void acceptCurrentMarket(com.cboe.idl.cmiMarketData.CurrentMarketStruct[] bestCurrentMarket, com.cboe.idl.cmiMarketData.CurrentMarketStruct[] bestPublicMarket, int queueDepth, short queueAction)
    {
        System.out.println("acceptCurrentMarket v3: BestMarketlen=" + bestCurrentMarket.length +
                           "acceptCurrentMarket v3: BestPublicMarketlen=" + bestPublicMarket.length +
                           " Q=" + queueDepth + " action="+queueAction);
                //+ " classkey="+bestCurrentMarket[0].productKeys.classKey);

        for (int i = 0; i < bestPublicMarket.length; i++)
        {
            int bidSizeLength = bestPublicMarket[i].bidSizeSequence.length;
            int askSizeLength = bestPublicMarket[i].askSizeSequence.length;
            
            if (askSizeLength > 0)
            {
                for (int j = 0; j < askSizeLength; j++)
                {
                    System.out.println("ProductKey:" + bestPublicMarket[i].productKeys.productKey + 
                                       "AskVolume Type:" + bestPublicMarket[i].askSizeSequence[j].volumeType +
                                       "AskVolume Qty:" + bestPublicMarket[i].askSizeSequence[j].quantity);
                }
                
            }
            if (bidSizeLength > 0)
            {
                for (int j = 0; j < bidSizeLength; j++)
                {
                    System.out.println("ProductKey:" + bestPublicMarket[i].productKeys.productKey + 
                                       "BidVolume Type:" + bestPublicMarket[i].bidSizeSequence[j].volumeType +
                                       "BidVolume Qty:" + bestPublicMarket[i].bidSizeSequence[j].quantity);
                }
                
            }
            
            
            
        }

    }

    public void acceptNBBO(com.cboe.idl.cmiMarketData.NBBOStruct[] nbbo, int queueDepth, short queueAction)
    {
        System.out.println("acceptNBBOt v2:");
        for(int i=0; i<nbbo.length; i++)
        {
           com.cboe.application.test.ReflectiveStructTester.printStruct(nbbo[i],"nbbo["+i+"]:");
        }
    }

    public void acceptRecap(com.cboe.idl.cmiMarketData.RecapStruct[] recaps, int queueDepth, short queueAction)
    {
        System.out.println("acceptRecap v2: l=" + recaps.length + " Q=" + queueDepth + " action="+queueAction
                + " classkey="+recaps[0].productKeys.classKey);
        try {
            Thread.sleep(500);
        }
        catch(Exception e){e.printStackTrace();}
//        for(int i=0; i<recaps.length; i++)
//        {
//           com.cboe.application.test.ReflectiveStructTester.printStruct(recaps[i],"recaps["+i+"]:");
//        }
    }

    public void acceptTicker(com.cboe.idl.cmiMarketData.TickerStruct[] ticker, int queueDepth, short queueAction)
    {
        System.out.println("acceptTicker v2: l=" + ticker.length + " Q=" + queueDepth + " action="+queueAction
                + " classkey="+ticker[0].productKeys.classKey);
        for(int i=0; i<ticker.length; i++)
        {
           com.cboe.application.test.ReflectiveStructTester.printStruct(ticker[i],"ticker["+i+"]:");
        }
    }

    public void acceptBookDepth(com.cboe.idl.cmiMarketData.BookDepthStruct[] productBooks, int queueDepth, short queueAction)
    {
        System.out.println("acceptBookDepth v2:");
        for(int i=0; i<productBooks.length; i++)
        {
           com.cboe.application.test.ReflectiveStructTester.printStruct(productBooks[i],"productBooks["+i+"]:");
        }
    }

    public void acceptExpectedOpeningPrice(com.cboe.idl.cmiMarketData.ExpectedOpeningPriceStruct[] expectedOpeningPrices, int queueDepth, short queueAction)
    {
        System.out.println("acceptExpectedOpeningPrice v2:");
        for(int i=0; i<expectedOpeningPrices.length; i++)
        {
           com.cboe.application.test.ReflectiveStructTester.printStruct(expectedOpeningPrices[i],"expectedOpeningPrices["+i+"]:");
        }
    }

 /*   public void acceptCurrentMarket(com.cboe.idl.cmiMarketData.CurrentMarketStruct[] currentMarket, int queueDepth, short queueAction)
    {
        System.out.println("acceptCurrentMarket v2:");
        for(int i=0; i<currentMarket.length; i++)
        {
           com.cboe.application.test.ReflectiveStructTester.printStruct(currentMarket[i],"currentMarket["+i+"]:");
        }
    }  */
    public void acceptQuoteBustReport(QuoteBustReportStruct struct, int i)
    {
        System.out.println();
        System.out.println("V2 TestNullValues QuoteBustReportStruct: " + ReflectiveStructTester.testNullStruct(struct));
        System.out.println("< --- Got V2 Received acceptQuoteBustReport ---- > " + struct.quoteKey );
        System.out.println();
    }

    public void acceptQuoteDeleteReport(QuoteDeleteReportStruct[] structs, int i)
    {
        System.out.println();
        System.out.println("V2 TestNullValues QuoteDeleteReportStruct: " + ReflectiveStructTester.testNullStruct(structs[0]));
        System.out.println("< --- Got V2 acceptQuoteDeleteReport!!! ---- length: > " + structs.length );
        for(int j=0; i<structs.length; i++)
        {
           com.cboe.application.test.ReflectiveStructTester.printStruct(structs[i],"acceptQuoteDeleteReport["+i+"]:");
        }
        System.out.println();
    }

    public void acceptQuoteFilledReport(QuoteFilledReportStruct struct, int i)
    {
        System.out.println();
        System.out.println("V2 TestNullValues QuoteFilledReportStruct: " + ReflectiveStructTester.testNullStruct(struct));
        System.out.println("< --- V2 Got acceptQuoteFilledReport !!! ---- > " + struct.quoteKey);
        System.out.println();
    }

    public void acceptQuoteStatus(QuoteDetailStruct[] structs, int i)
    {
        System.out.println();
        System.out.println("V2 TestNullValues QuoteDetailStruct: " + ReflectiveStructTester.testNullStruct(structs[0]));
        System.out.println("< --- Got V2 acceptQuoteStatus!!! ---- length: > " + structs.length );
        for(int j=0; i<structs.length; i++)
        {
           com.cboe.application.test.ReflectiveStructTester.printStruct(structs[i],"QuoteDetailStruct["+i+"]:");
        }
        System.out.println();
    }

    public void acceptRFQ(RFQStruct[] structs, int i, short i1)
    {
        System.out.println();
        System.out.println("< --- Got V2 acceptRFQ!!! ---- > "  );
        System.out.println("V2 TestNullValues RFQStruct: " + ReflectiveStructTester.testNullStruct(structs[0]));
        System.out.println();
    }


    //--------------------------------------------------------------------------
    // com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumerOperations methods
    //--------------------------------------------------------------------------
    public void acceptOrderStatus(com.cboe.idl.cmiOrder.OrderDetailStruct[] orders, int queueDepth)
    {
        System.out.println();
        System.out.println("V2 acceptOrderStatus() called:");
        System.out.println("TestNullValues OrderDetailStruct: " + ReflectiveStructTester.testNullStruct(orders));
        System.out.println("Queue Depth: " + queueDepth);
        System.out.println("< --- Order Status Details Received: ---- > ");
        for (int i = 0; i < orders.length; ++i) {
            System.out.println("\t" + orders[i]);
        }
        System.out.println();
    }

    public void acceptOrderCanceledReport(com.cboe.idl.cmiOrder.OrderCancelReportStruct canceledReport, int queueDepth)
    {
        System.out.println();
        System.out.println("Time=" + System.currentTimeMillis());
        System.out.println("V2 acceptOrderCanceledReport() called:");
        System.out.println("Queue Depth: " + queueDepth);
        System.out.println("TestNullValues CancelReportStruct: " + ReflectiveStructTester.testNullStruct(canceledReport));
        System.out.println("< --- Received Order Canceled Report notice ---- > " + canceledReport.cancelledOrder.orderStruct.orderId.executingOrGiveUpFirm);
        ReflectiveStructBuilder.printStruct(canceledReport, "orderCancelReport");
        System.out.println();
    }

    public void acceptOrderFilledReport(com.cboe.idl.cmiOrder.OrderFilledReportStruct filledReport, int queueDepth)
    {
        System.out.println();
        System.out.println("Time=" + System.currentTimeMillis());
        System.out.println("V2 acceptOrderFilledReport() called:");
        System.out.println("Queue Depth: " + queueDepth);
        System.out.println("TestNullValues OrderFilledReportStruct: " + ReflectiveStructTester.testNullStruct(filledReport));
        System.out.println("< --- Got acceptOrderFilledReport !!! ---- > " );
        ReflectiveStructBuilder.printStruct(filledReport, "orderFilledReport");
        System.out.println();
    }

    public void acceptOrderBustReport(com.cboe.idl.cmiOrder.OrderBustReportStruct bustReport, int queueDepth)
    {
        System.out.println();
        System.out.println("Time=" + System.currentTimeMillis());
        System.out.println("V2 acceptOrderBustReport() called:");
        System.out.println("Queue Depth: " + queueDepth);
        System.out.println("TestNullValues OrderBustReportStruct: " + ReflectiveStructTester.testNullStruct(bustReport));
        System.out.println("< --- Received OrderBustReportStruct notice ---- > " + bustReport.bustedOrder.orderStruct.orderId.executingOrGiveUpFirm);
        ReflectiveStructBuilder.printStruct(bustReport, "bustReport");
        System.out.println();
    }

    public void acceptOrderBustReinstateReport(com.cboe.idl.cmiOrder.OrderBustReinstateReportStruct bustReinstatedReport, int queueDepth)
    {
        System.out.println();
        System.out.println("Time=" + System.currentTimeMillis());
        System.out.println("V2 acceptOrderBustReinstateReport() called:");
        System.out.println("Queue Depth: " + queueDepth);
        System.out.println("TestNullValues OrderBustReinstateReportStruct: " + ReflectiveStructTester.testNullStruct(bustReinstatedReport));
        System.out.println("< --- Received OrderBustReinstateReportStruct notice ---- > " + bustReinstatedReport.reinstatedOrder.orderStruct.orderId.executingOrGiveUpFirm );
        ReflectiveStructBuilder.printStruct(bustReinstatedReport, "bustReinstatedReport");
        System.out.println();
    }

    public void acceptNewOrder(com.cboe.idl.cmiOrder.OrderDetailStruct order, int queueDepth)
    {
        System.out.println("Time=" + System.currentTimeMillis());
        System.out.println("this:" + this);
        System.out.println("V2 acceptNewOrder() called:");
        System.out.println("Queue Depth: " + queueDepth);
        System.out.println("TestNullValues OrderDetailStruct: " + ReflectiveStructTester.testNullStruct(order));
        System.out.println("< --- Got new order!!! ---- > " + order.orderStruct.userId + " "
            + order.orderStruct.orderId.executingOrGiveUpFirm + " "
            + order.orderStruct.orderId.branch + " "
            + order.orderStruct.orderId.branchSequenceNumber );
        ReflectiveStructBuilder.printStruct(order, "newOrderDetail");
        System.out.println();
    }

    public void acceptQuoteLockedReport(LockNotificationStruct[] lockNotificationStructs, int i)
    {
        System.out.println("this:" + this);
        System.out.println("V2 acceptQuoteLockedReport called:");
        System.out.println();

    }

}
