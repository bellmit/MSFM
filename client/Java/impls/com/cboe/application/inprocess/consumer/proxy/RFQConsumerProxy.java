package com.cboe.application.inprocess.consumer.proxy;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiQuote.RFQStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.interfaces.application.inprocess.RFQConsumer;
import com.cboe.interfaces.application.ProductQueryServiceAdapter;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.channel.ListenerProxyQueueControl;
import com.cboe.domain.iec.ClientIECFactory;
import com.cboe.domain.iec.ConcurrentEventChannelAdapter;

/**
 * @author Jing Chen
 */

public class RFQConsumerProxy extends InstrumentedConsumerProxy
{
    protected RFQConsumer rfqConsumer;
    private BaseSessionManager sessionManager;
    private ListenerProxyQueueControl proxyWrapper;

    private ProductQueryServiceAdapter pqAdapter; 
    private ConcurrentEventChannelAdapter internalEventChannel; 
    
    public RFQConsumerProxy(RFQConsumer rfqConsumer, BaseSessionManager sessionManager)
    {
        super(rfqConsumer, sessionManager);
        this.rfqConsumer = rfqConsumer;
        this.sessionManager = sessionManager;
        setHashKey(rfqConsumer);
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

    public void channelUpdate(ChannelEvent event)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this,"calling channelUpdate for " + sessionManager);
        }
        try
        {
            RFQStruct rfq = (RFQStruct)event.getEventData();
            //PQRefactor: used to be ProductQueryManagerImpl.getProduct (synchronized)                        
            ProductStruct productStruct = getProductQueryServiceAdapter().getProductByKey(rfq.productKeys.productKey);
            rfqConsumer.acceptRFQ(rfq, productStruct, getProxyWrapper().getQueueSize());
        }
        catch(Exception e)
        {
            Log.exception(this, "session:" + sessionManager, e);
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

    public String getMessageType()
    {
        return SupplierProxyMessageTypes.RFQ;
    }

    public void queueInstrumentationInitiated()
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
