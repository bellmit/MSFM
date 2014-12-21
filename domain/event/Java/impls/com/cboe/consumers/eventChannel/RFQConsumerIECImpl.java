package com.cboe.consumers.eventChannel;

/**
 * @author Jeff Illian
 */
import com.cboe.interfaces.events.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.util.*;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.domain.iec.ConcurrentEventChannelAdapter;
//import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapterFactory;
//import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapter;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.domain.iec.ClientIECFactory;
import com.cboe.domain.iec.ConcurrentEventChannelAdapter;


public class RFQConsumerIECImpl extends BObject implements RFQConsumer {
//    private InstrumentedEventChannelAdapter internalEventChannel = null;
    private ConcurrentEventChannelAdapter internalEventChannel = null;
    
    /**
     * constructor comment.
     */
    public RFQConsumerIECImpl() {
        super();
        try
        {
//        internalEventChannel = InstrumentedEventChannelAdapterFactory.find();
        	internalEventChannel = ClientIECFactory.findConcurrentEventChannelAdapter(ClientIECFactory.CAS_INSTRUMENTED_IEC);
        }
        catch (Exception e)
        {
        	Log.exception(this, "Exception getting CAS_INSTRUMENTED_IEC!", e);
        }
    }

    public void acceptRFQ(RFQStruct rfq) {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> RFQ : ClassKey = " + rfq.productKeys.classKey + " ProductKey = " + rfq.productKeys.productKey);
        }

        ChannelKey channelKey = new ChannelKey(ChannelKey.RFQ, new SessionKeyContainer(rfq.sessionName, rfq.productKeys.classKey));
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, rfq);
        internalEventChannel.dispatch(event);
    }
}
