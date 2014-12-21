package com.cboe.interfaces.application.inprocess;

import com.cboe.idl.cmiOrder.AuctionStruct;

/** Interface for creating FIX messages announcing an Auction.
 */
public interface AuctionConsumer
{
    /** Translate auction announcement to FIX and send to user.
     * @param auctionStruct Details of the auction.
     */
    public void acceptAuction(AuctionStruct auctionStruct);
    public void acceptDirectedAIMAuction(AuctionStruct auctionStruct);
}
