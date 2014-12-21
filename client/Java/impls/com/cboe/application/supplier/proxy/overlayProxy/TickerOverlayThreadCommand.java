package com.cboe.application.supplier.proxy.overlayProxy;

import com.cboe.domain.supplier.proxy.LostConnectionException;
import com.cboe.idl.cmiMarketData.TickerStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.util.ChannelKey;
import com.cboe.util.ThreadCommand;
import com.cboe.util.channel.ChannelEvent;

/**
 * @author Jing Chen
 */
public class TickerOverlayThreadCommand extends ThreadCommand
{
    private TickerV2ConsumerOverlayProxy tickerOverlayProxy;
    private TickerOverlayHelper productOverlayHelper;

    public TickerOverlayThreadCommand(TickerV2ConsumerOverlayProxy tickerOverlayProxy, TickerOverlayHelper overlayHelper)
    {
        super();
        this.tickerOverlayProxy = tickerOverlayProxy;
        productOverlayHelper = overlayHelper;
    }

    public void execute()
    {
        TickerStruct[] tickers = productOverlayHelper.getTickers();
        if(tickers.length == 0)
        {
            return;
        }
        try
        {
            if (Log.isDebugOn())
            {
                Log.debug("TickerOverlayThread calling acceptTicker for " + tickerOverlayProxy.getSessionManager());
            }
            // Call the proxied method passing the extracted TickerStruct[] from the EventChannelEvent.
            tickerOverlayProxy.getTickerConsumerInterceptor().acceptTicker(tickers,
                    tickerOverlayProxy.getProxyWrapper().getQueueSize(), tickerOverlayProxy.getAction());
        }
        catch (org.omg.CORBA.TIMEOUT toe)
        {
            Log.exception("session:" + tickerOverlayProxy.getSessionManager(), toe);
        }
        catch (Exception e)
        {
            Log.exception("session:" + tickerOverlayProxy.getSessionManager(), e);
            try {
                tickerOverlayProxy.lostConnection(new ChannelEvent(tickerOverlayProxy, new ChannelKey(0, tickerOverlayProxy), tickers));
            } catch (LostConnectionException lce) {
                if (Log.isDebugOn())
                {
                    Log.debug("Lost Connection: removing listener " + tickerOverlayProxy);
                }
                tickerOverlayProxy.getChannelAdapter().removeChannelListener(tickerOverlayProxy);
            }
        }
    }

    protected void complete()
    {
    }
}
