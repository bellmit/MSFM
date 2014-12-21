package com.cboe.domain.util;

import com.cboe.idl.cmiConstants.AuctionTypes;
import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.idl.order.AuctionHistoryStruct;
import com.cboe.idl.order.MarketDetailStruct;
import com.cboe.interfaces.domain.AuctionInternal;

public class MarketDetailStructBuilder
{
    private static final String EMPTY_STRING = "";
    public static MarketDetailStruct[] buildMarketDetailStruct(int totalNumberofProducts){

        MarketDetailStruct[] mktDetStruct = null;
        if(totalNumberofProducts == 0) {
            mktDetStruct = new MarketDetailStruct[1];
            mktDetStruct[0] = createAndSetMarketDetailStruct();
            mktDetStruct[0].activityTime=System.currentTimeMillis();
        } 
        else 
        {
            mktDetStruct = new MarketDetailStruct[totalNumberofProducts];
            long activityTime = System.currentTimeMillis();
            for(int i=0; i <totalNumberofProducts; i++) 
            {
                mktDetStruct[i] = createAndSetMarketDetailStruct();
                mktDetStruct[i].activityTime = activityTime;
            }
        }
        return mktDetStruct;
    }
    
    public static AuctionHistoryStruct buildAuctionHistoryStruct(AuctionInternal theAuction, short eventType)
    {

        AuctionHistoryStruct auctionHistStruct = new AuctionHistoryStruct();
        auctionHistStruct.productKey = theAuction.getProductKey();
        auctionHistStruct.auctionTime = com.cboe.domain.util.DateWrapper.convertToTime(theAuction.getExpireTime());
        auctionHistStruct.auctionType = theAuction.getAuctionType();
        auctionHistStruct.auctionEventType = eventType;
        auctionHistStruct.auctionEndedEarly = theAuction.getAuctionTerminatedReason() != AuctionTypes.AUCTION_UNSPECIFIED;
        auctionHistStruct.quantityTradedInAuction = theAuction.getAuctionQuantity();
        auctionHistStruct.pairedOrderId = theAuction.getAuctionedOrder().getOrderId();
        
        return auctionHistStruct;
    }
   
    private static MarketDetailStruct createAndSetMarketDetailStruct() {
        MarketDetailStruct mktDetailStruct = new MarketDetailStruct();
        
        mktDetailStruct.botrAskExchanges   = EMPTY_STRING;
        mktDetailStruct.botrBidExchanges   = EMPTY_STRING;
        mktDetailStruct.nbboAskExchanges   = EMPTY_STRING;
        mktDetailStruct.nbboBidExchanges   = EMPTY_STRING;
        mktDetailStruct.exchangeIndicators = EMPTY_STRING;
        
        mktDetailStruct.bboBidPriceType  = PriceTypes.NO_PRICE;
        mktDetailStruct.bboAskPriceType  = PriceTypes.NO_PRICE;
        mktDetailStruct.bookBidPriceType = PriceTypes.NO_PRICE;
        mktDetailStruct.bookAskPriceType = PriceTypes.NO_PRICE;
        mktDetailStruct.dsmBidPriceType  = PriceTypes.NO_PRICE;
        mktDetailStruct.dsmAskPriceType  = PriceTypes.NO_PRICE;
        
        
        return mktDetailStruct;
        
    }
}
