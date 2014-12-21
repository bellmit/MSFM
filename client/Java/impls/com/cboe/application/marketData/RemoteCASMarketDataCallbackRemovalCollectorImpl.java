package com.cboe.application.marketData;

import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.RemoteCASCallbackRemovalCollector;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.interfaces.domain.session.CallbackDeregistrationInfo;
import com.cboe.domain.supplier.proxy.CallbackSupplierProxy;


public class RemoteCASMarketDataCallbackRemovalCollectorImpl implements RemoteCASCallbackRemovalCollector
{
    BaseSessionManager sessionManager;
    RemoteCASMarketDataCallbackRemovalCollectorImpl(BaseSessionManager sessionManager)
    {
        this.sessionManager = sessionManager;
    }

    public void acceptRemoteCASCallbackRemoval(CallbackDeregistrationInfo eventData)
    {
        try
        {
            sessionManager.unregisterNotification(eventData);
        }
        catch(Exception e)
        {
            Log.exception("An exception occurred while notifying callback removal to session:"+sessionManager,e);
        }
        try
        {
            cleanupCallback(eventData.getCallbackInformationStruct().ior);
        }
        catch(Exception e)
        {
            Log.exception("An exception occurred while cleaning up a callback:"+eventData.getCallbackInformationStruct().subscriptionInterface,e);
        }
    }


    private void cleanupCallback(String ior) throws Exception
    {
        org.omg.CORBA.Object object = (org.omg.CORBA.Object)RemoteConnectionFactory.find().string_to_object(ior);
        String typeID = RemoteConnectionFactory.find().getTypeId(object);
        CallbackSupplierProxy listener;
        // starting the ugly way of finding the CORBA object type so that we could narrow it to the proper java class.
        if(typeID.equals(com.cboe.idl.cmiCallback.CMICurrentMarketConsumerHelper.id()))
        {
            com.cboe.idl.cmiCallback.CMICurrentMarketConsumer consumer = com.cboe.idl.cmiCallback.CMICurrentMarketConsumerHelper.narrow(object);
            listener = (CallbackSupplierProxy)ServicesHelper.getCurrentMarketConsumerProxy(consumer, sessionManager);
        }else
        if(typeID.equals(com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumerHelper.id()))
        {
            com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer consumer = com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumerHelper.narrow(object);
            listener = (CallbackSupplierProxy)ServicesHelper.getCurrentMarketV2ConsumerProxy(consumer, sessionManager, (short)0);
        }else
        if(typeID.equals(com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumerHelper.id()))
        {
            com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer consumer = com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumerHelper.narrow(object);
            listener = (CallbackSupplierProxy)ServicesHelper.getCurrentMarketV3ConsumerProxy(consumer, sessionManager, (short)0);
        }else
        if(typeID.equals(com.cboe.idl.cmiCallback.CMIRecapConsumerHelper.id()))
        {
            com.cboe.idl.cmiCallback.CMIRecapConsumer consumer = com.cboe.idl.cmiCallback.CMIRecapConsumerHelper.narrow(object);
            listener = (CallbackSupplierProxy)ServicesHelper.getRecapConsumerProxy(consumer, sessionManager);
        }else
        if(typeID.equals(com.cboe.idl.cmiCallbackV2.CMIRecapConsumerHelper.id()))
        {
            com.cboe.idl.cmiCallbackV2.CMIRecapConsumer consumer = com.cboe.idl.cmiCallbackV2.CMIRecapConsumerHelper.narrow(object);
            listener = (CallbackSupplierProxy)ServicesHelper.getRecapV2ConsumerProxy(consumer, sessionManager, (short)0);
        }else
        if(typeID.equals(com.cboe.idl.cmiCallback.CMINBBOConsumerHelper.id()))
        {
            com.cboe.idl.cmiCallback.CMINBBOConsumer consumer = com.cboe.idl.cmiCallback.CMINBBOConsumerHelper.narrow(object);
            listener = (CallbackSupplierProxy)ServicesHelper.getNBBOConsumerProxy(consumer, sessionManager);
        }else
        if(typeID.equals(com.cboe.idl.cmiCallbackV2.CMINBBOConsumerHelper.id()))
        {
            com.cboe.idl.cmiCallbackV2.CMINBBOConsumer consumer = com.cboe.idl.cmiCallbackV2.CMINBBOConsumerHelper.narrow(object);
            listener = (CallbackSupplierProxy)ServicesHelper.getNBBOV2ConsumerProxy(consumer, sessionManager, (short)0);
        }else
        if(typeID.equals(com.cboe.idl.cmiCallback.CMITickerConsumerHelper.id()))
        {
            com.cboe.idl.cmiCallback.CMITickerConsumer consumer = com.cboe.idl.cmiCallback.CMITickerConsumerHelper.narrow(object);
            listener = (CallbackSupplierProxy)ServicesHelper.getTickerConsumerProxy(consumer, sessionManager);
        }else
        if(typeID.equals(com.cboe.idl.cmiCallbackV2.CMITickerConsumerHelper.id()))
        {
            com.cboe.idl.cmiCallbackV2.CMITickerConsumer consumer = com.cboe.idl.cmiCallbackV2.CMITickerConsumerHelper.narrow(object);
            listener = (CallbackSupplierProxy)ServicesHelper.getTickerV2ConsumerProxy(consumer, sessionManager, (short)0);
        }else
        if(typeID.equals(com.cboe.idl.cmiCallback.CMIOrderBookConsumerHelper.id()))
        {
            com.cboe.idl.cmiCallback.CMIOrderBookConsumer consumer = com.cboe.idl.cmiCallback.CMIOrderBookConsumerHelper.narrow(object);
            listener = (CallbackSupplierProxy)ServicesHelper.getBookDepthConsumerProxy(consumer, sessionManager);
        }else
        if(typeID.equals(com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumerHelper.id()))
        {
            com.cboe.idl.cmiCallback.CMIOrderBookConsumer consumer = com.cboe.idl.cmiCallback.CMIOrderBookConsumerHelper.narrow(object);
            listener = (CallbackSupplierProxy)ServicesHelper.getBookDepthConsumerProxy(consumer, sessionManager);
        }else
        if(typeID.equals(com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumerHelper.id()))
        {
            com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer consumer = com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumerHelper.narrow(object);
            listener = (CallbackSupplierProxy)ServicesHelper.getExpectedOpeningPriceConsumerProxy(consumer, sessionManager);
        }else
        if(typeID.equals(com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumerHelper.id()))
        {
            com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer consumer = com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumerHelper.narrow(object);
            listener = (CallbackSupplierProxy)ServicesHelper.getExpectedOpeningPriceV2ConsumerProxy(consumer, sessionManager, (short)0);
        }else 
        if(typeID.equals(com.cboe.idl.cmiCallbackV4.CMICurrentMarketConsumerHelper.id()))
        {
        	// no need since MDX is always remote
        	return;         
        }else 
        if(typeID.equals(com.cboe.idl.cmiCallbackV4.CMIRecapConsumerHelper.id()))
        {
        	//  no need since MDX is always remote
            return;  
        }else 
        if(typeID.equals(com.cboe.idl.cmiCallbackV4.CMITickerConsumerHelper.id()))
        {
        	//  no need since MDX is always remote
        	return;  
        } else
        if(typeID.equals(com.cboe.idl.internalCallback.CurrentMarketManualQuoteConsumerHelper.id()))
        {
//          no need since MDX is always remote
            return;  
        }
        else
        {
            throw new Exception("unknown type id:"+typeID);
        }
        listener.getChannelAdapter().removeChannelListener(listener);
    }
}
