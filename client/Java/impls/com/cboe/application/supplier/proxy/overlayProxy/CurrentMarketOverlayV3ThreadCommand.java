package com.cboe.application.supplier.proxy.overlayProxy;

import com.cboe.domain.supplier.proxy.LostConnectionException;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.util.ChannelKey;
import com.cboe.util.ThreadCommand;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.interfaces.domain.CurrentMarketContainer;

/**
 * @author Jing Chen
 */
public class CurrentMarketOverlayV3ThreadCommand extends ThreadCommand
{
    private CurrentMarketV3ConsumerOverlayProxy currentMarketOverlayProxy;
    private CurrentMarketOverlayHelper productOverlayHelper;

    public CurrentMarketOverlayV3ThreadCommand(CurrentMarketV3ConsumerOverlayProxy currentMarketOverlayProxy, CurrentMarketOverlayHelper overlayHelper)
    {
        super();
        this.currentMarketOverlayProxy = currentMarketOverlayProxy;
        productOverlayHelper = overlayHelper;
    }

    public void execute()
    {
        CurrentMarketContainer currentMarketContainer = productOverlayHelper.getBestMarketsWithPublicMarket();
        CurrentMarketStruct[] bestMarkets = currentMarketContainer.getBestMarkets();
        CurrentMarketStruct[] bestPublicMarkets = currentMarketContainer.getBestPublicMarketsAtTop();
        if(bestMarkets.length==0)  // this should never happen, but just in case
        {
            return;
        }

        try
        {
            // Call the proxied method passing the extracted CurrentMarketStruct[] from the EventChannelEvent.
            currentMarketOverlayProxy.getCurrentMarketConsumerInterceptor().acceptCurrentMarket(bestMarkets,bestPublicMarkets,
                                        currentMarketOverlayProxy.getProxyWrapper().getQueueSize(),
                                        currentMarketOverlayProxy.getAction());
        }
        catch (org.omg.CORBA.TIMEOUT toe)
        {
            Log.exception("session:" + currentMarketOverlayProxy.getSessionManager(), toe);
        }
        catch (Exception e)
        {
            Log.exception("session:" + currentMarketOverlayProxy.getSessionManager(), e);
            try {
                currentMarketOverlayProxy.lostConnection(new ChannelEvent(currentMarketOverlayProxy, new ChannelKey(0, currentMarketOverlayProxy), bestMarkets));
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
