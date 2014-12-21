package com.cboe.application.supplier.proxy;

import com.cboe.application.supplier.TradingSessionStatusSupplierFactory;
import com.cboe.idl.cmiCallback.CMITradingSessionStatusConsumer;
import com.cboe.idl.cmiSession.TradingSessionStateStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelEvent;
/**
 * TradingSessionAdminProxy serves as a proxy to the UserSessionAdmin
 * object on the presentation side in com.cboe.presentation.consumer.  The
 * UserSessionAdminSupplier on the CAS uses this proxy object to communicate to
 * the GUI callback object.
 *
 * @see com.cboe.idl.cmiCallback.CMIUserSessionAdmin
 *
 * @author Derek T. Chambers-Boucher
 * @version 06/25/1999
 */

public class TradingSessionStatusConsumerProxy extends InstrumentedConsumerProxy
{
    /**
     * TradingSessionStatusConsumerProxy constructor.i
     *
     * @param tradingSessionStatusConsumer a reference to the proxied implementation object.
     * @param sessionManager a reference to the SessionManager managing subscriptions for this proxy.
     */
    protected TradingSessionStatusConsumerProxy(CMITradingSessionStatusConsumer tradingSessionStatusConsumer,
                                                BaseSessionManager sessionManager )
    {
        super(sessionManager, TradingSessionStatusSupplierFactory.find(), tradingSessionStatusConsumer);
        interceptor = new TradingSessionStatusConsumerInterceptor(tradingSessionStatusConsumer);
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
            Log.debug(this,"calling channelUpdate for " + getSessionManager());
        }
        if (event != null)
        {
            try
            {
                // Call the proxied method passing the extracted TradingSessionStateStruct from the EventChannelEvent.
                ((TradingSessionStatusConsumerInterceptor)interceptor).acceptTradingSessionState( (TradingSessionStateStruct)event.getEventData());
            }
            catch(Exception e)
            {
                Log.exception(this, "session:" + getSessionManager(), e);
                lostConnection(event);
            }
        }
        else
        {
            Log.information(this, "Null event");
        }
    }

    public String getMethodName(ChannelEvent event)
    {
        return "acceptTradingSessionState";
    }

    public String getMessageType()
    {
        return SupplierProxyMessageTypes.TRADING_SESSION_STATUS;
    }
}
