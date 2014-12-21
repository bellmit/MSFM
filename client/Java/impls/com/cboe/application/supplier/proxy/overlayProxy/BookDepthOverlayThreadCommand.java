package com.cboe.application.supplier.proxy.overlayProxy;

import com.cboe.domain.supplier.proxy.LostConnectionException;
import com.cboe.idl.cmiMarketData.BookDepthStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.util.ChannelKey;
import com.cboe.util.ThreadCommand;
import com.cboe.util.channel.ChannelEvent;

/**
 * @author Jing Chen
 */
public class BookDepthOverlayThreadCommand extends ThreadCommand
{
    private BookDepthV2ConsumerOverlayProxy bookDepthOverlayProxy;
    private BookDepthOverlayHelper productOverlayHelper;

    public BookDepthOverlayThreadCommand(BookDepthV2ConsumerOverlayProxy bookDepthOverlayProxy, BookDepthOverlayHelper overlayHelper)
    {
        super();
        this.bookDepthOverlayProxy = bookDepthOverlayProxy;
        productOverlayHelper = overlayHelper;
    }

    public void execute()
    {
        BookDepthStruct[] bookDepths = productOverlayHelper.getBookDepths();
        if (bookDepths.length == 0)
        {
            return;
        }
        try
        {
            if (Log.isDebugOn())
            {
                Log.debug("BookDepthOverlayThread calling acceptBookDepth for " + bookDepthOverlayProxy.getSessionManager());
            }
            // Call the proxied method passing the extracted CurrentMarketStruct[] from the EventChannelEvent.
            bookDepthOverlayProxy.getOrderBookConsumerInterceptor().acceptBookDepth(bookDepths,
                    bookDepthOverlayProxy.getProxyWrapper().getQueueSize(), bookDepthOverlayProxy.getAction());
        }
        catch (org.omg.CORBA.TIMEOUT toe)
        {
            Log.exception("session:" + bookDepthOverlayProxy.getSessionManager(), toe);
        }
        catch (Exception e)
        {
            Log.exception("session:" + bookDepthOverlayProxy.getSessionManager(), e);
            try {
                bookDepthOverlayProxy.lostConnection(new ChannelEvent(bookDepthOverlayProxy, new ChannelKey(0, bookDepthOverlayProxy), bookDepths));
            } catch (LostConnectionException lce) {
                if (Log.isDebugOn())
                {
                    Log.debug("Lost Connection: removing listener " + bookDepthOverlayProxy);
                }
                bookDepthOverlayProxy.getChannelAdapter().removeChannelListener(bookDepthOverlayProxy);
            }
        }
    }

    protected void complete()
    {
    }
}
