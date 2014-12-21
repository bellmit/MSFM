package com.cboe.application.inprocess.consumer.proxy;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiQuote.LockNotificationStruct;
import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.interfaces.application.inprocess.LockedQuoteStatusConsumer;
import com.cboe.interfaces.application.ProductQueryServiceAdapter;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.channel.ListenerProxyQueueControl;
//import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.domain.iec.ClientIECFactory;
import com.cboe.domain.iec.ConcurrentEventChannelAdapter;
//import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapterFactory;

/**
 * @author Jing Chen
 */

public class QuoteNotificationConsumerProxy extends InstrumentedConsumerProxy
{
    private LockedQuoteStatusConsumer quoteLockConsumer;
    private BaseSessionManager sessionManager;
    private ListenerProxyQueueControl proxyWrapper;
    private final static int SCALE =    1000000000;

    private ProductQueryServiceAdapter pqAdapter;
    private ConcurrentEventChannelAdapter internalEventChannel; 

    public QuoteNotificationConsumerProxy(LockedQuoteStatusConsumer quoteLockConsumer, BaseSessionManager sessionManager)
    {
        super(quoteLockConsumer, sessionManager);
        this.quoteLockConsumer = quoteLockConsumer;
        this.sessionManager = sessionManager;
        setHashKey(quoteLockConsumer);
        try
        {
        	internalEventChannel = ClientIECFactory.findConcurrentEventChannelAdapter(ClientIECFactory.CAS_INSTRUMENTED_IEC);
        }
        catch (Exception e)
        {
        	Log.exception(this, "Exception getting CAS_INSTRUMENTED_IEC!", e);
        }
    }

    public ListenerProxyQueueControl getProxyWrapper()
    {
        if (proxyWrapper == null)
        {
            proxyWrapper = internalEventChannel.getProxyForDelegate(this);
        }
        return proxyWrapper;
    }

    public final void channelUpdate(ChannelEvent event)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this,"calling channelUpdate for " + sessionManager);
        }
        if (event != null)
        {
            ProductStruct productStruct = null;
            try
            {
                LockNotificationStruct lockNotification = (LockNotificationStruct)event.getEventData();
                //PQRefactor: used to be ProductQueryManagerImpl.getProduct (synchronized)
                productStruct = getProductQueryServiceAdapter().getProductByKey(lockNotification.productKey);
                String smgr = sessionManager.toString();
                String locked = getLockedQuoteString(lockNotification);
                StringBuilder calling = new StringBuilder(smgr.length()+locked.length()+55);
                calling.append("calling acceptQuoteLockedReport for ").append(smgr).append(locked)
                       .append(" qSize=").append(getProxyWrapper().getQueueSize());
                Log.information(this, calling.toString());
                quoteLockConsumer.acceptQuoteLockedReport(lockNotification, productStruct,
                getProxyWrapper().getQueueSize());
            }
            catch (Exception e)
            {
                Log.exception(this, "session:" + sessionManager, e);
            }
        }
    }

    protected ProductQueryServiceAdapter getProductQueryServiceAdapter()
    {
        if(pqAdapter == null)
        {
            pqAdapter = ServicesHelper.getProductQueryServiceAdapter();
        }
        return pqAdapter;
    }

    private String getLockedQuoteString(LockNotificationStruct lockedQuotes)
    {
        StringBuilder toStr = new StringBuilder(200);
//Printed in this format -> :W_MAIN:pKey=123456:price,qty=2.5,50:Buy[0]=CBOE,KCD:Sell[0]=CBOE,KPD

        toStr.append(':');
        toStr.append(lockedQuotes.sessionName).append(':');
        toStr.append("pKey=").append(lockedQuotes.productKey).append(':');
        toStr.append("price,qty=");
        if(lockedQuotes.price.type == PriceTypes.VALUED)
        {
            long toLong = (long)lockedQuotes.price.whole * SCALE + (long) lockedQuotes.price.fraction;
            toStr.append((double)toLong/SCALE).append(',').append(lockedQuotes.quantity);
        }
        else
            toStr.append(lockedQuotes.price).append(',').append(lockedQuotes.quantity);

        for(int j=0; j<lockedQuotes.buySideUserAcronyms.length; j++)
        {
            toStr.append(":Buy[").append(j).append("]=");
            toStr.append(lockedQuotes.buySideUserAcronyms[j].exchange).append(',');
            toStr.append(lockedQuotes.buySideUserAcronyms[j].acronym);
        }
        for(int j=0; j<lockedQuotes.sellSideUserAcronyms.length; j++)
        {
            toStr.append(":Sell[").append(j).append("]=");
            toStr.append(lockedQuotes.sellSideUserAcronyms[j].exchange).append(',');
            toStr.append(lockedQuotes.sellSideUserAcronyms[j].acronym);
        }
        return toStr.toString();
    }

    public String getMessageType()
    {
        return SupplierProxyMessageTypes.QUOTE_LOCK;
    }

    public void queueInstrumentationInitiated()
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
