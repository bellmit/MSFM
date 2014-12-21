package com.cboe.consumers.eventChannel;

import com.cboe.domain.util.*;
//import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapterFactory;
//import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapter;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.AuctionConsumer;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.domain.iec.ClientIECFactory;
import com.cboe.domain.iec.ConcurrentEventChannelAdapter;

/** Dispatch notification of auction to AUCTION and AUCTION_USER Internal
 * Event Channels. AUCTION IEC is keyed by session and class key,
 * AUCTION_USER is keyed by user, session and class key.
 */
public class AuctionConsumerIECImpl extends BObject implements AuctionConsumer
{
//    private InstrumentedEventChannelAdapter internalEventChannel;
    private ConcurrentEventChannelAdapter internalEventChannel = null;

    public AuctionConsumerIECImpl()
    {
        super();
        try
        {
//        internalEventChannel = InstrumentedEventChannelAdapterFactory.find();
        	internalEventChannel = ClientIECFactory.findConcurrentEventChannelAdapter(ClientIECFactory.CAS_INSTRUMENTED_IEC);
        }
        catch (Exception e)
        {
        	Log.exception(this, "Exception getting CAS_INSTRUMENTED_IEC!", e);
        }
    }

    // Functions for interface AuctionConsumer

    /** Dispatch notification to AUCTION and AUCTION_USER Internal Event
     * Channels.
     * @param routingParameters Information for proper dispatching.
     * @param activeUserKeys Users interested in this auction.
     * @param auctionStruct Auction details.
     */
    public void acceptAuction(
            RoutingParameterStruct routingParameters,
            int[] activeUserKeys,
            AuctionStruct auctionStruct)
    {
    	StringBuilder received = new StringBuilder(130);
        received.append("event received -> acceptAuction id:h=")
                .append(auctionStruct.auctionId.highCboeId)
                .append(",l=").append(auctionStruct.auctionId.lowCboeId)
                .append(" productKey:").append(auctionStruct.productKey)
                .append(" classKey:").append(auctionStruct.classKey)
                .append(" auctionType:").append(auctionStruct.auctionType)
                .append(" users:").append(activeUserKeys.length);
        Log.information(this, received.toString());
        
        // Run-time configuration decides whether users (clients) listen to a
        // single channel with a key of session name, class key, and auction type,
        // or whether they listen to a user-specific channel. So we dispatch to both.

        // Dispatch to single channel.
        TypeSessionClassContainer typeSessionClass = new TypeSessionClassContainer(auctionStruct.auctionType,  auctionStruct.sessionName,  auctionStruct.classKey);
        ChannelKey channelKey = new ChannelKey(ChannelType.AUCTION, typeSessionClass);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, auctionStruct);
        internalEventChannel.dispatch(event);

        // Dispatch to user-specific channels, one for each client specified
        // by the server.
        for (int i = 0; i < activeUserKeys.length; ++i)
        {
            TypeUserSessionClassContainer typeUserSessionClass = new TypeUserSessionClassContainer(auctionStruct.auctionType, activeUserKeys[i], auctionStruct.sessionName, auctionStruct.classKey); 
            channelKey = new ChannelKey(ChannelType.AUCTION_USER, typeUserSessionClass);
            event = internalEventChannel.getChannelEvent(this, channelKey, auctionStruct);
            internalEventChannel.dispatch(event);
        }
    }
    
    public void acceptDirectedAIMAuction(
            RoutingParameterStruct routingParameters,
            int[] activeUserKeys,
            AuctionStruct auctionStruct)
    {
        StringBuilder received = new StringBuilder(145);
        received.append("event received -> acceptDirectedAIMAuction id:h=")
                .append(auctionStruct.auctionId.highCboeId)
                .append(",l=").append(auctionStruct.auctionId.lowCboeId)
                .append(" productKey:").append(auctionStruct.productKey)
                .append(" classKey:").append(auctionStruct.classKey)
                .append(" auctionType:").append(auctionStruct.auctionType)
                .append(" users:").append(activeUserKeys.length);
        Log.information(this, received.toString());

    	// Dispatch to user-specific channels, one for each client specified
        // by the server.
        for (int i = 0; i < activeUserKeys.length; ++i)
        {
            TypeUserSessionClassContainer typeUserSessionClass = new TypeUserSessionClassContainer(auctionStruct.auctionType, activeUserKeys[i], auctionStruct.sessionName, auctionStruct.classKey); 
            ChannelKey channelKey = new ChannelKey(ChannelType.DAIM_USER, typeUserSessionClass);
            ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, auctionStruct);
            internalEventChannel.dispatch(event);
        }
    }
}
