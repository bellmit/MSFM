package com.cboe.consumers.eventChannel;

/**
 * @author Jeff Illian
 */
import com.cboe.domain.iec.ClientIECFactory;
import com.cboe.domain.iec.ConcurrentEventChannelAdapter;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.interfaces.events.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.util.*;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;

public class RecapConsumerIECImpl extends BObject implements RecapConsumer {
    private ConcurrentEventChannelAdapter internalEventChannel;
    /**
     * constructor comment.
     */
    public RecapConsumerIECImpl() {
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

    public void acceptRecapForClass(RoutingParameterStruct routing, RecapStruct[] recaps) {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> Recap classKey:" + routing.classKey + " for " + routing.sessionName);
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;

        channelKey = new ChannelKey(ChannelKey.RECAP_BY_CLASS, new SessionKeyContainer(routing.sessionName, routing.classKey));
        event = internalEventChannel.getChannelEvent(this, channelKey, recaps);
        internalEventChannel.dispatch(event);
    }
    public void acceptRecap(int[] groups, RecapStruct recap) {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> Recap " + recap.productKeys.classKey + " for " + recap.sessionName);
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;

        channelKey = new ChannelKey(ChannelKey.RECAP_BY_CLASS, new SessionKeyContainer(recap.sessionName, recap.productKeys.classKey));
        RecapStruct[] recaps = {recap};
        event = internalEventChannel.getChannelEvent(this, channelKey, recaps);
        internalEventChannel.dispatch(event);

    }
}
