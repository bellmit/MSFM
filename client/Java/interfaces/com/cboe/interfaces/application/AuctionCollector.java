package com.cboe.interfaces.application;

/** Interface for putting Auction announcements onto client callback (CB_)
 * Internal Event Channels.
 */
import com.cboe.idl.cmiOrder.*;
import com.cboe.interfaces.domain.session.SessionBasedCollector;

public interface AuctionCollector extends SessionBasedCollector
{
    /** Add an Auction announcement to the appropriate client callback IEC.
     * @param auctionStruct Auction details to deliver to the client.
     */
    public void acceptAuction(AuctionStruct auctionStruct);
    public void acceptDirectedAIMAuction(AuctionStruct auctionStruct);
}
