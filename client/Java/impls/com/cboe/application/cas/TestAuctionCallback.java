package com.cboe.application.cas;

import com.cboe.idl.cmiOrder.AuctionStruct;
import com.cboe.idl.cmiOrder.OrderEntryStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.CancelRequestStruct;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiV3.UserSessionManagerV3;
import com.cboe.domain.util.*;
import com.cboe.application.shared.UnitTestHelper;
import com.cboe.interfaces.callback.AuctionConsumer;

public class TestAuctionCallback implements Runnable, AuctionConsumer
{
    private boolean receivedEvent = false;
    private AuctionStruct auction = null;
    private final Object lock = new Object();
    private UserSessionManagerV3 sessionV3;
    
    
    TestAuctionCallback(UserSessionManagerV3 sessionV3)
    {
        this.sessionV3 = sessionV3;
        Thread t = new Thread(this);
        t.start();
    }
    
    public void acceptAuction(AuctionStruct auctionStruct)
    {
        if(!receivedEvent)
        {
            ReflectiveStructBuilder.printStruct(auctionStruct, "auction");
            auction = auctionStruct;
            receivedEvent = true;
            synchronized(lock)
            {
                lock.notifyAll();
            }
        }
    }

    public void run()
    {
        try
        {
            synchronized(lock)
            {
                while(!receivedEvent)
                {
                    lock.wait();
                }
            }
            
            //participateInAuction();
            receivedEvent = false;
            auction = null;
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        
    }
    
    private void participateInAuction()
    {
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        System.out.println("Participating in auction...");
        System.out.println("\n");
        
        try
        {
            OrderEntryStruct response = OrderStructBuilder.buildOrderEntryStruct();
            response.productKey = auction.productKey;
            response.sessionNames = new String[1];
            response.sessionNames[0] = auction.sessionName;
            response.contingency.type = ContingencyTypes.AUCTION_RESPONSE;
            ExtensionsHelper helper = new ExtensionsHelper();
            helper.setValue(ExtensionFields.AUCTION_ID, CboeId.toString(auction.auctionId));
            response.extensions = helper.toString();
            ExchangeFirmStruct firm = StructBuilder.buildExchangeFirmStruct("CBOE", "690");
            OrderIdStruct inputOrderId = UnitTestHelper.createOrderIdStruct(firm,"BRA",nextSeq(),"FRM","20031211");
            response.executingOrGiveUpFirm = inputOrderId.executingOrGiveUpFirm;
            response.correspondentFirm = inputOrderId.correspondentFirm;
            response.branch = inputOrderId.branch;
            response.branchSequenceNumber = inputOrderId.branchSequenceNumber;
            response.orderDate = inputOrderId.orderDate;
            response.productKey = auction.productKey;
            response.originalQuantity = auction.auctionQuantity;
            response.sessionNames[0] = auction.sessionName;
            response.side = auction.side == Sides.BUY ? Sides.SELL : Sides.BUY;
            PriceStruct p = new PriceStruct();
//            p.type = PriceTypes.MARKET;
//            p.whole = 0;
//            p.fraction = 0;
            p.type = auction.startingPrice.type;
            p.fraction = auction.startingPrice.fraction;
            if(auction.side == Sides.BUY)
            {
                p.whole = auction.startingPrice.whole - 1;
            }
            else
            {
                p.whole = auction.startingPrice.whole + 1;
            }
            response.price = p;
            
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            System.out.println("Testing acceptOrder for response...");

            System.out.println("Order built");
//            ReflectiveStructBuilder.printStruct(response, "--->>DEBUG_order");
            System.out.println("\nSending response....");
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            com.cboe.idl.cmiV3.OrderEntry orderEntry = sessionV3.getOrderEntryV3();
            OrderIdStruct orderId = orderEntry.acceptOrder(response);

            CancelRequestStruct cancelRequest = new CancelRequestStruct();
            cancelRequest.orderId = orderId;
            cancelRequest.cancelType = OrderCancelTypes.CANCEL_ALL_QUANTITY;
            cancelRequest.sessionName = "W_MAIN";
            cancelRequest.userAssignedCancelId = "xxx";
            orderEntry.acceptOrderCancelRequest(cancelRequest);
            
//            OrderEntryStruct newResponse = OrderStructBuilder.buildOrderEntryStruct();
//            newResponse.productKey = auction.productKey;
//            newResponse.sessionNames = new String[1];
//            newResponse.sessionNames[0] = auction.sessionName;
//            newResponse.contingency.type = ContingencyTypes.AUCTION_RESPONSE;
//            helper = new ExtensionsHelper();
//            helper.setValue(ExtensionFields.AUCTION_ID, CboeId.toString(auction.auctionId));
//            newResponse.extensions = helper.toString();
//            firm = StructBuilder.buildExchangeFirmStruct("CBOE", "690");
//            inputOrderId = UnitTestHelper.createOrderIdStruct(firm,"BRA",nextSeq(),"FRM","20031211");
//            newResponse.executingOrGiveUpFirm = inputOrderId.executingOrGiveUpFirm;
//            newResponse.correspondentFirm = inputOrderId.correspondentFirm;
//            newResponse.branch = inputOrderId.branch;
//            newResponse.branchSequenceNumber = inputOrderId.branchSequenceNumber;
//            newResponse.orderDate = inputOrderId.orderDate;
//            newResponse.productKey = auction.productKey;
//            newResponse.originalQuantity = auction.auctionQuantity;
//            newResponse.sessionNames[0] = auction.sessionName;
//            newResponse.side = auction.side == Sides.BUY ? Sides.SELL : Sides.BUY;
//            p = new PriceStruct();
////            p.type = PriceTypes.MARKET;
////            p.whole = 0;
////            p.fraction = 0;
//            p.type = auction.startingPrice.type;
//            p.fraction = auction.startingPrice.fraction;
//            if(auction.side == Sides.BUY)
//            {
//                p.whole = auction.startingPrice.whole - 1;
//            }
//            else
//            {
//                p.whole = auction.startingPrice.whole + 1;
//            }
//            newResponse.price = p;            
//            orderId = orderEntry.acceptOrderCancelReplaceRequest(cancelRequest, newResponse);
//
//
//            OrderEntryStruct finalResponse = OrderStructBuilder.buildOrderEntryStruct();
//            finalResponse.productKey = auction.productKey;
//            finalResponse.sessionNames = new String[1];
//            finalResponse.sessionNames[0] = auction.sessionName;
//            finalResponse.contingency.type = ContingencyTypes.AUCTION_RESPONSE;
//            helper = new ExtensionsHelper();
//            helper.setValue(ExtensionFields.AUCTION_ID, CboeId.toString(auction.auctionId));
//            finalResponse.extensions = helper.toString();
//            firm = StructBuilder.buildExchangeFirmStruct("CBOE", "690");
//            inputOrderId = UnitTestHelper.createOrderIdStruct(firm,"TTT",nextSeq(),"FRM","20031211");
//            finalResponse.executingOrGiveUpFirm = inputOrderId.executingOrGiveUpFirm;
//            finalResponse.correspondentFirm = inputOrderId.correspondentFirm;
//            finalResponse.branch = inputOrderId.branch;
//            finalResponse.branchSequenceNumber = inputOrderId.branchSequenceNumber;
//            finalResponse.orderDate = inputOrderId.orderDate;
//            finalResponse.productKey = auction.productKey;
//            finalResponse.originalQuantity = auction.auctionQuantity;
//            finalResponse.sessionNames[0] = auction.sessionName;
//            finalResponse.side = auction.side; //== Sides.BUY ? Sides.SELL : Sides.BUY;
//            p = new PriceStruct();
//            p.type = PriceTypes.MARKET;
//            p.whole = 0;
//            p.fraction = 0;
//            p.type = auction.startingPrice.type;
//            p.fraction = auction.startingPrice.fraction;
//            if(auction.side == Sides.BUY)
//            {
//                p.whole = auction.startingPrice.whole - 1;
//            }
//            else
//            {
//                p.whole = auction.startingPrice.whole + 1;
//            }
//            finalResponse.price = p;            
//
//            cancelRequest = new CancelRequestStruct();
//            cancelRequest.orderId = orderId;
//            cancelRequest.cancelType = OrderCancelTypes.CANCEL_ALL_QUANTITY;
//            cancelRequest.sessionName = "W_MAIN";
//            cancelRequest.userAssignedCancelId = "xxx";
//            orderEntry.acceptOrderCancelReplaceRequest(cancelRequest, finalResponse);
//        } catch (DataValidationException e) {
//            System.out.println("Exception adding order : " + e.details.message + " " + e.details.error);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    private static int seq = (int) System.currentTimeMillis();
    private static int nextSeq()
    {
        if(seq < 0)
        {
            seq *= -1;
        }
        seq++;
        return seq;
    }
}
