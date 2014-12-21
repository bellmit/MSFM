package com.cboe.application.supplier.proxy;

import com.cboe.application.supplier.QuoteNotificationSupplierFactory;
import com.cboe.idl.cmiCallbackV2.CMILockedQuoteStatusConsumer;
import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.idl.cmiQuote.LockNotificationStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelEvent;

/**
 * QuoteNotificationConsumerProxy serves as a SessionManager managed proxy to
 * the QuoteNotificationConsumer object on the presentation side in
 * com.cboe.presentation.consumer.  The QuoteNotificationSupplier on the CAS uses
 * this proxy object to communicate to the GUI callback object.  If a connection
 * to the presentation side consumer fails the <CODE>lostConnection</CODE> method
 * will be called letting the SessionManager this consumer reference is no longer
 * valid.
 *
 * @see com.cboe.idl.cmiCallbackV2.CMILockedQuoteStatusConsumer
 *
 * @author William Wei
 * @version 12/26/2001
 */

public class QuoteNotificationConsumerProxy extends InstrumentedConsumerProxy
{
    private final static int SCALE =    1000000000;

    /**
     * QuoteNotificationConsumerProxy constructor.
     *
     * @param quoteLockConsumer a reference to the proxied implementation object.
     * @param sessionManager a reference to the SessionManager managing subscriptions for this proxy.
     */
    public QuoteNotificationConsumerProxy(CMILockedQuoteStatusConsumer quoteLockConsumer, BaseSessionManager sessionManager)
    {
        super(sessionManager, QuoteNotificationSupplierFactory.find(), quoteLockConsumer);
        interceptor = new QuoteNotificationConsumerInterceptor(quoteLockConsumer);
    }

    /**
     * This method is called by ChannelThreadCommand.  It takes the passed
     * EventChannelEvent, parses out the relevant data for the proxied object,
     * and calls the proxied objects callback method passing in the appropriate
     * data.
     *
     * @param event the ChannelEvent containing the data to send the listener.
     */
    public final void channelUpdate(ChannelEvent event)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this,"calling channelUpdate for " + getSessionManager());
        }
        if (event != null)
        {
            try
            {
                LockNotificationStruct[] lockedQuotes = (LockNotificationStruct [])event.getEventData();
                String smgr = getSessionManager().toString();
                String locks = getLockedQuoteString(lockedQuotes);
                StringBuilder calling = new StringBuilder(smgr.length()+locks.length()+45);
                calling.append("calling acceptQuoteLockedReport for ").append(smgr).append(locks)
                       .append(" qSize=").append(getProxyWrapper().getQueueSize());
                Log.information(this, calling.toString());
                // Call the proxied method passing the extracted LockNotificationStruct[] from the EventChannelEvent.
                ((QuoteNotificationConsumerInterceptor)interceptor).acceptQuoteLockedReport(lockedQuotes,
                        getProxyWrapper().getQueueSize());
            }
            catch (Exception e)
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
        return "acceptQuoteLockedReport";
    }

    private String getLockedQuoteString(LockNotificationStruct[] lockedQuotes)
    {
        StringBuilder toStr = new StringBuilder(200);
//Printed in this format -> :W_MAIN:pKey=123456:price,qty=2.5,50:Buy[0]=CBOE,KCD:Sell[0]=CBOE,KPD
        for (int i=0; i<lockedQuotes.length; i++)
        {
            toStr.append(':');
            toStr.append(lockedQuotes[i].sessionName).append(':');
            toStr.append("pKey=").append(lockedQuotes[i].productKey).append(':');
            toStr.append("price,qty=");
            if(lockedQuotes[i].price.type == PriceTypes.VALUED)
            {
                long toLong = (long)lockedQuotes[i].price.whole * SCALE + (long) lockedQuotes[i].price.fraction;
                toStr.append((double)toLong/SCALE).append(',').append(lockedQuotes[i].quantity);
            }
            else
                toStr.append(lockedQuotes[i].price).append(',').append(lockedQuotes[i].quantity);

            for(int j=0; j<lockedQuotes[i].buySideUserAcronyms.length; j++)
            {
                toStr.append(":Buy[").append(j).append("]=");
                toStr.append(lockedQuotes[i].buySideUserAcronyms[j].exchange).append(',');
                toStr.append(lockedQuotes[i].buySideUserAcronyms[j].acronym);
            }
            for(int j=0; j<lockedQuotes[i].sellSideUserAcronyms.length; j++)
            {
                toStr.append(":Sell[").append(j).append("]=");
                toStr.append(lockedQuotes[i].sellSideUserAcronyms[j].exchange).append(',');
                toStr.append(lockedQuotes[i].sellSideUserAcronyms[j].acronym);
            }
        }
        return toStr.toString();
    }

    public String getMessageType()
    {
        return SupplierProxyMessageTypes.QUOTE_LOCK;
    }
}
