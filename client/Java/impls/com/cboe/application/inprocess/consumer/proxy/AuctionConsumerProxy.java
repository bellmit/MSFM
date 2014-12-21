package com.cboe.application.inprocess.consumer.proxy;

import com.cboe.interfaces.application.inprocess.AuctionConsumer;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.util.channel.ListenerProxyQueueControl;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.idl.cmiOrder.AuctionStruct;

/**
 * @author Vivek Beniwal
 */

public class AuctionConsumerProxy extends InstrumentedConsumerProxy
{
    protected AuctionConsumer auctionConsumer;
    private BaseSessionManager sessionManager;
    private ListenerProxyQueueControl proxyWrapper;

    public AuctionConsumerProxy(AuctionConsumer auctionConsumer, BaseSessionManager sessionManager)
    {
        super(auctionConsumer, sessionManager);
        this.auctionConsumer = auctionConsumer;
        this.sessionManager = sessionManager;
    }

    public void channelUpdate(ChannelEvent event)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this,"calling channelUpdate for " + sessionManager);
        }
        ChannelKey channelKey = (ChannelKey)event.getChannel();
        if (Log.isDebugOn())
        {
            Log.debug(this," channelKey.channelType: " + channelKey.channelType);
        }
        try
        {
            AuctionStruct auctionStruct = (AuctionStruct)event.getEventData();
            String smgr = sessionManager.toString();
            StringBuilder calling = new StringBuilder(smgr.length()+110);
            switch (channelKey.channelType)
            {
                case ChannelType.AUCTION:
                case ChannelType.AUCTION_USER:
                    calling.append("calling acceptAuction for ").append(smgr)
                           .append(" auctionId:h=").append(auctionStruct.auctionId.highCboeId)
                           .append(":l=").append(auctionStruct.auctionId.lowCboeId)
                           .append(" auctionType:").append(auctionStruct.auctionType)
                           .append("channel: ").append(channelKey.channelType);
                    Log.information(this, calling.toString());
                    auctionConsumer.acceptAuction(auctionStruct);
                    break;

                case ChannelType.DAIM_USER:
                    calling.append("calling acceptDirectedAIMAuction for ").append(smgr)
                           .append(" auctionId:h=").append(auctionStruct.auctionId.highCboeId)
                           .append(":l=").append(auctionStruct.auctionId.lowCboeId)
                           .append(" auctionType:").append(auctionStruct.auctionType)
                           .append("channel: ").append(channelKey.channelType);
                    Log.information(this, calling.toString());
                    auctionConsumer.acceptDirectedAIMAuction(auctionStruct);
                    break;

                default:
                    if (Log.isDebugOn())
                    {
                        Log.debug(this, "AuctionConsumerProxy -> Unexpected Channel: " + channelKey.channelType);
                    }

            }
        }
        catch(Exception e)
        {
            Log.exception(this, "session:" + sessionManager, e);
        }
    }

    public String getMessageType()
    {
        return SupplierProxyMessageTypes.AUCTION;
    }

    public void queueInstrumentationInitiated()
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
