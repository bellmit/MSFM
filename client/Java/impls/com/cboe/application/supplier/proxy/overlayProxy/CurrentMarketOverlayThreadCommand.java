package com.cboe.application.supplier.proxy.overlayProxy;

import com.cboe.domain.supplier.proxy.LostConnectionException;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.util.ChannelKey;
import com.cboe.util.ThreadCommand;
import com.cboe.util.channel.ChannelEvent;

/**
 * @author Jing Chen
 */
public class CurrentMarketOverlayThreadCommand extends ThreadCommand
{
    private CurrentMarketV2ConsumerOverlayProxy currentMarketOverlayProxy;
    private CurrentMarketOverlayHelper productOverlayHelper;

    public CurrentMarketOverlayThreadCommand(CurrentMarketV2ConsumerOverlayProxy currentMarketOverlayProxy, CurrentMarketOverlayHelper overlayHelper)
    {
        super();
        this.currentMarketOverlayProxy = currentMarketOverlayProxy;
        productOverlayHelper = overlayHelper;
    }

    public void execute()
    {
        CurrentMarketStruct[] currentMarkets = productOverlayHelper.getBestMarkets();
        if(currentMarkets.length==0)
        {
            return;
        }
        try
        {
            // Call the proxied method passing the extracted CurrentMarketStruct[] from the EventChannelEvent.
            currentMarketOverlayProxy.getCurrentMarketConsumerInterceptor().acceptCurrentMarket(currentMarkets,
                    currentMarketOverlayProxy.getProxyWrapper().getQueueSize(), currentMarketOverlayProxy.getAction());
        }
        catch (org.omg.CORBA.TIMEOUT toe)
        {
            Log.exception("session:" + currentMarketOverlayProxy.getSessionManager(), toe);
        }
        catch (Exception e)
        {
            Log.exception("session:" + currentMarketOverlayProxy.getSessionManager(), e);
            try {
                currentMarketOverlayProxy.lostConnection(new ChannelEvent(currentMarketOverlayProxy, new ChannelKey(0, currentMarketOverlayProxy), currentMarkets));
            } catch (LostConnectionException lce) {
                if (Log.isDebugOn())
                {
                    Log.debug("Lost Connection: removing listener " + currentMarketOverlayProxy);
                }
                currentMarketOverlayProxy.getChannelAdapter().removeChannelListener(currentMarketOverlayProxy);
            }
        }
    }

    protected void complete()
    {
    }
}
