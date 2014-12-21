package com.cboe.application.shared.consumer;

import com.cboe.interfaces.application.*;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.util.channel.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.util.ChannelType;
import com.cboe.util.ChannelKey;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/** Get events from the AUCTION* Internal Event Channels and dispatch them
 * to per-user CB_AUCTION channels.
 */
public class AuctionProcessor extends InstrumentedProcessor
 {
    // Object that dispatches events to interested users
    private AuctionCollector parent = null;

    public AuctionProcessor(AuctionCollector parent)
    {
        super(parent);
        this.parent = parent;
    }

    /** Save reference to object that dispatches events to user.
     * @param parent Collector object that dispatches events to IECs that
     *    represent interested listeners. The IECs represent user callbacks,
     *    and have names starting with CB_.
     */
    public void setParent(AuctionCollector parent)
    {
        this.parent = parent;
    }

    /** Get object that dispatches to user callback IEC.
     * @see #setParent
     */
    public AuctionCollector getParent()
    {
        return parent;
    }

    /** Handle an event. Called by parent class to dispatch an event
     * to registered listeners.
     * @param event Event to dispatch.
     */
    public void channelUpdate(ChannelEvent event)
    {
        ChannelKey channelKey = (ChannelKey)event.getChannel();
        Object eventData = event.getEventData();
        if (parent != null)
        {
            switch (channelKey.channelType)
            {
                case ChannelType.AUCTION:
                case ChannelType.AUCTION_USER:
                    // Pass the auction information to the Collector object       	
                    AuctionStruct auctionStruct = (AuctionStruct)eventData;
                    parent.acceptAuction(auctionStruct);
                    break;
                    
                case ChannelType.DAIM_USER:
                    // Pass the auction information to the Collector object                	
                    auctionStruct = (AuctionStruct)eventData;
                    parent.acceptDirectedAIMAuction(auctionStruct);
                    break;

                default:
                    if (Log.isDebugOn())
                    {
                        Log.debug("AuctionProcessor -> Wrong Channel : " + channelKey.channelType);
                    }
            }
        }
    }

    public String getMessageType()
    {
        return SupplierProxyMessageTypes.AUCTION;
    }
}
