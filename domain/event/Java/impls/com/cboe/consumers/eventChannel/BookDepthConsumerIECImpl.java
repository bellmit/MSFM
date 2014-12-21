package com.cboe.consumers.eventChannel;

/**
 * Book Depth listener object listens on the CBOE event channel as a BookDepthConsumer.
 * There will only be a single book depth listener per CAS.
 *
 * @author William Wei
 */
import com.cboe.domain.iec.ClientIECFactory;
import com.cboe.domain.iec.ConcurrentEventChannelAdapter;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.interfaces.events.*;
import com.cboe.util.*;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;

public class BookDepthConsumerIECImpl extends BObject implements BookDepthConsumer{
    private ConcurrentEventChannelAdapter internalEventChannel;

    /**
     * MarketBestListener constructor comment.
     */
    public BookDepthConsumerIECImpl() {
        super();
        try
        {
            internalEventChannel = ClientIECFactory.findConcurrentEventChannelAdapter(ClientIECFactory.MARKETDATA_INSTRUMENTED_IEC);
        }
        catch (Exception e)
        {
        	Log.exception("Exception getting MARKETDATA_INSTRUMENTED_IEC!", e);
        }
    }

    public void acceptBookDepthForClass(RoutingParameterStruct routing, BookDepthStruct[] bookDepths)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> bookDepth : " + routing.classKey + " for " + routing.sessionName);
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;

        SessionKeyContainer sessionKey = new SessionKeyContainer(routing.sessionName, routing.classKey);
        channelKey = new ChannelKey(ChannelKey.BOOK_DEPTH_BY_CLASS, sessionKey);
        event = internalEventChannel.getChannelEvent(this, channelKey, bookDepths);
        internalEventChannel.dispatch(event);
    }

    /**
     * This method is called by the CORBA event channel when a BookDepth event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     */
    public void acceptBookDepth(int[] groups, BookDepthStruct bookDepth)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> bookDepth : " + bookDepth.productKeys.productKey + " for " + bookDepth.sessionName);
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;

        SessionKeyContainer sessionKey = new SessionKeyContainer(bookDepth.sessionName, bookDepth.productKeys.classKey);
        channelKey = new ChannelKey(ChannelKey.BOOK_DEPTH_BY_CLASS, sessionKey);
        BookDepthStruct[] bookDepths = {bookDepth};
        event = internalEventChannel.getChannelEvent(this, channelKey, bookDepths);
        internalEventChannel.dispatch(event);
    }
}
