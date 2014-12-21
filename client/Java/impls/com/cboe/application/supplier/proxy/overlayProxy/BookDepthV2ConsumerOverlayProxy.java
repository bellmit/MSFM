package com.cboe.application.supplier.proxy.overlayProxy;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.supplier.proxy.BookDepthV2ConsumerProxy;
import com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer;
import com.cboe.idl.cmiConstants.QueueActions;
import com.cboe.idl.cmiMarketData.BookDepthStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.InvalidThreadPoolStateException;
import com.cboe.util.ThreadPool;
import com.cboe.util.channel.ChannelEvent;

public class BookDepthV2ConsumerOverlayProxy extends BookDepthV2ConsumerProxy
{
    protected BookDepthOverlayHelper overlayHelper;
    protected boolean isFinished = false;
    protected short action = QueueActions.OVERLAY_LAST;
    protected BookDepthOverlayThreadCommand command;
    protected ThreadPool threadPool;

    public BookDepthV2ConsumerOverlayProxy(CMIOrderBookConsumer bookDepthConsumer, BaseSessionManager sessionManager)
    {
        super(bookDepthConsumer, sessionManager, QueueActions.OVERLAY_LAST);
        overlayHelper = new BookDepthOverlayHelper();
    }

    public short getAction()
    {
        return action;
    }

    public void initialize()
    {
        command = new BookDepthOverlayThreadCommand(this, overlayHelper);
        threadPool = ServicesHelper.getUserSessionMarketDataOverlayThreadPool(sessionManager);
    }

    /**
     * This method is called by ChannelThreadCommand.  It takes the passed
     * EventChannelEvent, parses out the relevant data for the proxied object,
     * and calls the proxied objects callback method passing in the appropriate
     * data.
     *
     * @param event the ChannelEvent containing the data to send the listener.
     */
    public void channelUpdate(ChannelEvent event)
    {
        if (event != null)
        {
            BookDepthStruct[] bookDepths = (BookDepthStruct[])event.getEventData();
            int overlay = overlayHelper.addBookDepths(bookDepths);
            if(overlay == 0)
            {
                try
                {
                    threadPool.schedule(command);
                }
                catch(InvalidThreadPoolStateException e)
                {
                    Log.exception("Exception in ThreadPool.schedule: " + e.getMessage(),e);
                }
            }
        }
    }

    public void cleanUp()
    {
        super.cleanUp();
        overlayHelper = null;
        command = null;
    }

}