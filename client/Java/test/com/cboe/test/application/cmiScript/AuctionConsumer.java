package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiCallbackV3.CMIAuctionConsumerPOA;
import com.cboe.idl.cmiOrder.AuctionStruct;

public class AuctionConsumer extends CMIAuctionConsumerPOA
{
    public void acceptAuction(AuctionStruct auctionRequest)
    {
        Log.message("AuctionConsumer.acceptAuction "
                + Struct.toString(auctionRequest));
    }
}
