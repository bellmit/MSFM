package com.cboe.consumers.eventChannel;

/**
 * Quote Locked listener object listens on the CBOE event channel as a QuoteLockedNotificationConsumer.
 * There will only be a single quoteLock listener per CAS.
 *
 * @author William Wei
 */

//import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapterFactory;
//import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapter;
import com.cboe.domain.util.fixUtil.FixUtilPriceHelper;
import com.cboe.idl.cmiQuote.*;
import com.cboe.interfaces.events.*;
import com.cboe.util.*;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;

import java.text.DecimalFormat;
import com.cboe.domain.iec.ClientIECFactory;
import com.cboe.domain.iec.ConcurrentEventChannelAdapter;

public class QuoteNotificationConsumerIECImpl extends BObject implements QuoteNotificationConsumer{
//    private InstrumentedEventChannelAdapter internalEventChannel;
    private ConcurrentEventChannelAdapter internalEventChannel = null;
    private static ThreadLocal<DecimalFormat> priceFormatRef = new ThreadLocal<DecimalFormat>()
    {
        protected DecimalFormat initialValue()
        {
            return new DecimalFormat("####0.00");
        }
    };

    /**
     * MarketBestListener constructor comment.
     */
    public QuoteNotificationConsumerIECImpl() {
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

//    public void acceptQuoteLockedNotificationForClass(RoutingParameterStruct routing, LockNotificationStruct[] quoteLocks) {
//        Log.debug(this, "event received -> quoteLock: ForClass: classkey[0] " + quoteLocks[0].classKey + " for " + quoteLocks[0].sessionName);
//
//        ChannelKey channelKey = null;
//        ChannelEvent event = null;
//
//        SessionKeyContainer sessionClass = new SessionKeyContainer(routing.sessionName, routing.classKey);
//        channelKey = new ChannelKey(ChannelKey.QUOTE_LOCKED_NOTIFICATION_BY_CLASS, sessionClass);
//        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, quoteLocks);
//    }

    /**
     * This method is called by the CORBA event channel when a QuoteLockedNotification event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     */
    public void acceptQuoteLockedNotification(int[] userKeys, LockNotificationStruct quoteLock) {
        // We use FixUtilPriceHelper instead of PriceHelper in this Log call,
        // because that is what's available to this class at compile time.
        StringBuilder received = new StringBuilder(160);
        received.append("event received -> acceptQuoteLockedNotification:")
                .append(quoteLock.sessionName)
                .append(":pKey=").append(quoteLock.productKey)
                .append(":users=").append(userKeys.length)
                .append(":classKey=").append(quoteLock.classKey)
                .append(":side=").append(quoteLock.side)
                .append(":price=").append(FixUtilPriceHelper.priceStructToString(quoteLock.price, priceFormatRef.get()))
                .append(":#buyers=").append(quoteLock.buySideUserAcronyms.length)
                .append(":#sellers=").append(quoteLock.sellSideUserAcronyms.length);
        Log.information(this, received.toString());

        ChannelKey channelKey = null;
        ChannelEvent event = null;

        for (int i=0; i< userKeys.length; i++ )
        {
            channelKey = new ChannelKey(ChannelKey.QUOTE_LOCKED_NOTIFICATION, Integer.valueOf(userKeys[i]));
            event = internalEventChannel.getChannelEvent(this, channelKey, quoteLock);
            internalEventChannel.dispatch(event);
        }
    }
}
