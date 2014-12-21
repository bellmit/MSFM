package com.cboe.application.supplier.proxy;

import com.cboe.application.supplier.AuctionSupplierFactory;
import com.cboe.idl.cmiCallbackV3.CMIAuctionConsumer;
import com.cboe.idl.cmiOrder.AuctionStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;

/** Accept events from the CB_AUCTION channel and send them to the client's
 * AuctionConsumer object. If a connection to the client's consumer fails, this
 * class calls the lostConnection method to inform the SessionManager that this
 * consumer reference is no longer valid.
 *
 * @see com.cboe.idl.cmiCallbackV3.CMIAuctionConsumer
 */
public class AuctionConsumerProxy extends InstrumentedConsumerProxy
{
    /**
     * UserAuctionConsumerProxy constructor.
     *
     * @param auctionConsumer a reference to the proxied implementation object.
     * @param sessionManager a reference to the SessionManager managing subscriptions for this proxy.
     */
    public AuctionConsumerProxy(CMIAuctionConsumer auctionConsumer, BaseSessionManager sessionManager)
    {
        super(sessionManager, AuctionSupplierFactory.find(), auctionConsumer);
        interceptor = new AuctionConsumerInterceptor(auctionConsumer);
    }

    /** Receive an event from the server-side Internal Event Channel, and pass
     * it to the user's AuctionConsumer object. Most errors will result in
     * calling the lostConnection() method.
     * @param event Auction notification to send to the client.
     */
    public void channelUpdate(ChannelEvent event)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling channelUpdate for " + getSessionManager());
        }
        if (event != null)
        {
            boolean disconnect = false;
            try
            {
            	ChannelKey key = (ChannelKey)event.getChannel();
                switch (key.channelType)
                {
                    case ChannelType.CB_AUCTION:
                    	// Queue policy: disconnect if queue gets too large
                        if (getProxyWrapper().getQueueSize() > this.getNoActionProxyQueueDepthLimit())
                        {
                            String us = this.toString();
                            StringBuilder discon = new StringBuilder(us.length()+40);
                            discon.append("Disconnection consumer for : ").append(us)
                                  .append(" Q=").append(getProxyWrapper().getQueueSize());
                            Log.information(this, discon.toString());
                            disconnect = true;
                        }
                        else
                        {
                            AuctionStruct auctionStruct = (AuctionStruct) event.getEventData();
                            String smgr = getSessionManager().toString();
                            StringBuilder calling = new StringBuilder(smgr.length()+80);
                            calling.append("calling acceptAuction for ").append(smgr)
                                   .append(" auctionId:h=").append(auctionStruct.auctionId.highCboeId)
                                   .append(":l=").append(auctionStruct.auctionId.lowCboeId)
                                   .append(" auctionType:").append(auctionStruct.auctionType);
                            Log.information(this, calling.toString());
                            ((AuctionConsumerInterceptor)interceptor).acceptAuction(auctionStruct);
                        }
                        break;
                    case ChannelType.CB_DAIM:
                    	// Queue policy: disconnect if queue gets too large
                        if (getProxyWrapper().getQueueSize() > this.getNoActionProxyQueueDepthLimit())
                        {
                            String us = this.toString();
                            StringBuilder discon = new StringBuilder(us.length()+40);
                            discon.append("Disconnection consumer for : ").append(us)
                                  .append(" Q=").append(getProxyWrapper().getQueueSize());
                            Log.information(this, discon.toString());
                            disconnect = true;
                        }
                        else
                        {
                            AuctionStruct auctionStruct = (AuctionStruct) event.getEventData();
                            String smgr = getSessionManager().toString();
                            StringBuilder calling = new StringBuilder(smgr.length()+95);
                            calling.append("calling acceptDirectedAIMAuction for ").append(smgr)
                                   .append(" auctionId:h=").append(auctionStruct.auctionId.highCboeId)
                                   .append(":l=").append(auctionStruct.auctionId.lowCboeId)
                                   .append(" auctionType:").append(auctionStruct.auctionType);
                            Log.information(this, calling.toString());
                            ((AuctionConsumerInterceptor)interceptor).acceptAuction(auctionStruct);
                        }
                        break;
                    default:
                        Log.alarm(this,"Wrong channelType: " + key.channelType );
                        break;
                }       
                
            }
            catch (org.omg.CORBA.TIMEOUT toe)
            {
                Log.exception(this, "session:" + getSessionManager(), toe);
            }
            catch (Exception e)
            {
                Log.exception(this, "session:" + getSessionManager(), e);
                disconnect = true;
            }
            if (disconnect)
            {
                // End connection to client, throw exception
                lostConnection(event);
            }
        }
        else
        {
            Log.information(this, "Null event");
        }
    }

    public String getMethodName(ChannelEvent event)
    {
        
        String method;

        ChannelKey key = (ChannelKey) event.getChannel();

        switch(key.channelType)
        {
            case ChannelType.CB_AUCTION:
                method = "acceptAuction";
                break;
            case ChannelType.CB_DAIM:
                method = "acceptDirectedAIMAuction";
                break;
            default:
                method = "";
                break;
        }
        return method;
    }

    /** Get string identifying the type of instrumentation data.
     * @return Value to identify data in instrumentation output file.
     */
    public String getMessageType()
    {
        return SupplierProxyMessageTypes.AUCTION;
    }

}
