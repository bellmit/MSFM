package com.cboe.application.supplier.proxy.overlayProxy;

import com.cboe.domain.supplier.proxy.LostConnectionException;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiMarketData.RecapStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.util.ChannelKey;
import com.cboe.util.ThreadCommand;
import com.cboe.util.channel.ChannelEvent;

/**
 * @author Jing Chen
 */
public class RecapOverlayThreadCommand extends ThreadCommand
{
    private RecapV2ConsumerOverlayProxy recapOverlayProxy;
    private RecapOverlayHelper productOverlayHelper;

    public RecapOverlayThreadCommand(RecapV2ConsumerOverlayProxy recapOverlayProxy, RecapOverlayHelper overlayHelper)
    {
        super();
        this.recapOverlayProxy = recapOverlayProxy;
        productOverlayHelper = overlayHelper;
    }

    public void execute()
    {
        RecapStruct[] recaps = productOverlayHelper.getRecaps();
        if(recaps.length == 0)
        {
            return;
        }
        try
        {
            if (Log.isDebugOn())
            {
                Log.debug("RecapOverlayThread calling acceptRecap for " + recapOverlayProxy.getSessionManager());
            }
            // Call the proxied method passing the extracted RecapStruct[] from the EventChannelEvent.
            recapOverlayProxy.getRecapConsumerInterceptor().acceptRecap(recaps,
                    recapOverlayProxy.getProxyWrapper().getQueueSize(), recapOverlayProxy.getAction());
        }
        catch (org.omg.CORBA.TIMEOUT toe)
        {
            Log.exception("session:" + recapOverlayProxy.getSessionManager(), toe);
        }
        catch (Exception e)
        {
            Log.exception("session:" + recapOverlayProxy.getSessionManager(), e);
            try {
                recapOverlayProxy.lostConnection(new ChannelEvent(recapOverlayProxy, new ChannelKey(0, recapOverlayProxy), recaps));
            } catch (LostConnectionException lce) {
                if (Log.isDebugOn())
                {
                    Log.debug("Lost Connection: removing listener " + recapOverlayProxy);
                }
                recapOverlayProxy.getChannelAdapter().removeChannelListener(recapOverlayProxy);
            }
        }
    }

    protected void complete()
    {
    }
}
