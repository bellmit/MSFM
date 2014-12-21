package com.cboe.application.supplier.proxy.overlayProxy;

import com.cboe.domain.supplier.proxy.LostConnectionException;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiMarketData.ExpectedOpeningPriceStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.util.ChannelKey;
import com.cboe.util.ThreadCommand;
import com.cboe.util.channel.ChannelEvent;

/**
 * @author Jing Chen
 */
public class ExpectedOpeningPriceOverlayThreadCommand extends ThreadCommand
{
    private ExpectedOpeningPriceV2ConsumerOverlayProxy expectedOpeningPriceOverlayProxy;
    private ExpectedOpeningPriceOverlayHelper productOverlayHelper;

    public ExpectedOpeningPriceOverlayThreadCommand(ExpectedOpeningPriceV2ConsumerOverlayProxy expectedOpeningPriceOverlayProxy, ExpectedOpeningPriceOverlayHelper overlayHelper)
    {
        super();
        this.expectedOpeningPriceOverlayProxy = expectedOpeningPriceOverlayProxy;
        productOverlayHelper = overlayHelper;
    }

    public void execute()
    {
        ExpectedOpeningPriceStruct[] expectedOpeningPrices = productOverlayHelper.getExpectedOpeningPrices();
        if (expectedOpeningPrices.length == 0)
        {
            return;
        }
        try
        {
            if (Log.isDebugOn())
            {
                Log.debug("ExpectedOpeningPriceOverlayThread calling acceptExpectedOpeningPrice for " + expectedOpeningPriceOverlayProxy.getSessionManager());
            }
            // Call the proxied method passing the extracted ExpectedOpeningPriceStruct[] from the EventChannelEvent.
            expectedOpeningPriceOverlayProxy.getExpectedOpeningPriceConsumerInterceptor().acceptExpectedOpeningPrice(expectedOpeningPrices,
                    expectedOpeningPriceOverlayProxy.getProxyWrapper().getQueueSize(), expectedOpeningPriceOverlayProxy.getAction());
        }
        catch (org.omg.CORBA.TIMEOUT toe)
        {
            Log.exception("session:" + expectedOpeningPriceOverlayProxy.getSessionManager(), toe);
        }
        catch (Exception e)
        {
            Log.exception("session:" + expectedOpeningPriceOverlayProxy.getSessionManager(), e);
            try {
                expectedOpeningPriceOverlayProxy.lostConnection(new ChannelEvent(expectedOpeningPriceOverlayProxy, new ChannelKey(0, expectedOpeningPriceOverlayProxy), expectedOpeningPrices));
            } catch (LostConnectionException lce) {
                if (Log.isDebugOn())
                {
                    Log.debug("Lost Connection: removing listener " + expectedOpeningPriceOverlayProxy);
                }
                expectedOpeningPriceOverlayProxy.getChannelAdapter().removeChannelListener(expectedOpeningPriceOverlayProxy);
            }
        }
    }

    protected void complete()
    {
    }
}
