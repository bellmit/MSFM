package com.cboe.application.supplier.proxy.overlayProxy;

import com.cboe.domain.supplier.proxy.LostConnectionException;
import com.cboe.idl.cmiMarketData.NBBOStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.util.ChannelKey;
import com.cboe.util.ThreadCommand;
import com.cboe.util.channel.ChannelEvent;

/**
 * @author Jing Chen
 */
public class NBBOOverlayThreadCommand extends ThreadCommand
{
    private NBBOV2ConsumerOverlayProxy nbboOverlayProxy;
    private NBBOOverlayHelper productOverlayHelper;

    public NBBOOverlayThreadCommand(NBBOV2ConsumerOverlayProxy nbboOverlayProxy, NBBOOverlayHelper overlayHelper)
    {
        super();
        this.nbboOverlayProxy = nbboOverlayProxy;
        productOverlayHelper = overlayHelper;
    }

    public void execute()
    {
        NBBOStruct[] nbbos = productOverlayHelper.getNBBOs();
        if(nbbos.length == 0)
        {
            return;
        }
        try
        {
            if (Log.isDebugOn())
            {
                Log.debug("NBBOOverlayThread calling acceptNBBO for " + nbboOverlayProxy.getSessionManager());
            }
            // Call the proxied method passing the extracted NBBOStruct[] from the EventChannelEvent.
            nbboOverlayProxy.getNBBOConsumerInterceptor().acceptNBBO(nbbos,
                    nbboOverlayProxy.getProxyWrapper().getQueueSize(), nbboOverlayProxy.getAction());
        }
        catch (org.omg.CORBA.TIMEOUT toe)
        {
            Log.exception("session:" + nbboOverlayProxy.getSessionManager(), toe);
        }
        catch (Exception e)
        {
            Log.exception("session:" + nbboOverlayProxy.getSessionManager(), e);
            try {
                nbboOverlayProxy.lostConnection(new ChannelEvent(nbboOverlayProxy, new ChannelKey(0, nbboOverlayProxy), nbbos));
            } catch (LostConnectionException lce) {
                if (Log.isDebugOn())
                {
                    Log.debug("Lost Connection: removing listener " + nbboOverlayProxy);
                }
                nbboOverlayProxy.getChannelAdapter().removeChannelListener(nbboOverlayProxy);
            }
        }
    }

    protected void complete()
    {
    }
}
