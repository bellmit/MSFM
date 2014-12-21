package com.cboe.application.supplier.proxy;

import com.cboe.interfaces.application.*;

import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiUtil.CallbackInformationStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

import com.cboe.application.supplier.*;
/**
 * TickerCollectorProxy serves as a proxy to the OrderQueryConsumer
 * object on the presentation side in com.cboe.presentation.consumer.  The
 * QuoteStatusSupplier on the CAS uses this proxy object to communicate to
 * the GUI callback object.
 *
 *
 * @author Keith A. Korecky
 */

public class TickerCollectorProxy extends InstrumentedCollectorProxy
{
    // the CORBA callback object.
    private TickerCollector tickerCollector;

    /**
     * TickerCollectorProxy constructor.
     *
     * @param tickerCollector a reference to the proxied implementation object.
     * @param sessionManager the SessionManager managing subscriptions for this proxy.
     * @param hashKey object to supply hash code for BaseSupplierProxy hash table usage.
     */
    public TickerCollectorProxy(TickerCollector tickerCollector, BaseSessionManager sessionManager, Object hashKey )
    {
        super( sessionManager, TickerCollectorSupplierFactory.find(), hashKey );
        setHashKey(tickerCollector);
        this.tickerCollector = tickerCollector;
    }

    /**
     * This method is called by ChannelThreadCommand object.  It takes the passed
     * EventChannelEvent, parses out the relevant data for the proxied object,
     * and calls the proxied objects callback method passing in the appropriate
     * data.
     *
     * @param event the ChannelEvent containing the data to send the listener.
     */
    public void channelUpdate(ChannelEvent event)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this,"Got channel update " + event);
        }
        ChannelKey channelKey = (ChannelKey)event.getChannel();
        if (tickerCollector != null)
        {
            switch (channelKey.channelType)
            {
                case ChannelType.TICKER_BY_CLASS:
                    tickerCollector.acceptTickersForClass( (TickerStruct[])event.getEventData() );
                    break;

                default :
                    if (Log.isDebugOn())
                    {
                        Log.debug( this, "Wrong Channel : " + channelKey.channelType);
                    }
                break;
            }
        }
    }

    public CallbackInformationStruct getCallbackInformationStruct(ChannelEvent event)
    {
        return null;
    }

    public void startMethodInstrumentation(boolean privateOnly){}
    public void stopMethodInstrumentation(){}

    public String getMessageType()
    {
        return SupplierProxyMessageTypes.TICKER;
    }
}
