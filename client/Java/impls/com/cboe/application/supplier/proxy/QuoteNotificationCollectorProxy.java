package com.cboe.application.supplier.proxy;

import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiUtil.CallbackInformationStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.QuoteNotificationCollector;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

import com.cboe.application.supplier.*;

/**
 * QuoteNotificationCollectorProxy serves as a proxy to the QuoteNotificationConsumer
 * object on the presentation side in com.cboe.presentation.consumer.  The
 * QuoteNotificationSupplier on the CAS uses this proxy object to communicate to
 * the GUI callback object.
 *
 * @author William Wei
 */

public class QuoteNotificationCollectorProxy extends InstrumentedCollectorProxy
{
    // the CORBA callback object.
    private QuoteNotificationCollector quoteLockCollector;

    /**
     * QuoteNotificationCollectorProxy constructor.
     *
     * @param quoteLockCollector a reference to the proxied implementation object.
     * @param sessionManager a reference to the SessionManager managing subscriptions for this proxy.
     * @param hashKey object to supply hash code for BaseSupplierProxy hash table usage.
     */
    public QuoteNotificationCollectorProxy(QuoteNotificationCollector quoteLockCollector, BaseSessionManager sessionManager, Object hashKey)
    {
        super( sessionManager, QuoteNotificationCollectorSupplierFactory.find(), hashKey );
        setHashKey(quoteLockCollector);
        this.quoteLockCollector = quoteLockCollector;
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
            Log.debug(this,"QuoteNotificationCollectorProxy: channelUupdate " + event);
        }
        ChannelKey channelKey = (ChannelKey)event.getChannel();
        if (quoteLockCollector != null)
        {
            switch (channelKey.channelType)
            {
                case ChannelType.QUOTE_LOCKED_NOTIFICATION:
                    LockNotificationStruct[] quoteLockStructs = (LockNotificationStruct[])event.getEventData();
                    quoteLockCollector.acceptQuoteNotification( quoteLockStructs );
                    break;

                default :
                    if (Log.isDebugOn())
                    {
                        Log.debug(this, "Illegal internal publish channel : " + channelKey.channelType);
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
        return SupplierProxyMessageTypes.QUOTE_LOCK;
    }
}
