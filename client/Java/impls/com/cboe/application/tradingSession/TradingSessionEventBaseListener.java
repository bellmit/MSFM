package com.cboe.application.tradingSession;

import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapterFactory;
import com.cboe.domain.util.InstrumentorNameHelper;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.idl.cmiSession.*;
import com.cboe.interfaces.domain.instrumentedChannel.InstrumentedEventChannelListener;

import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

import com.cboe.application.supplier.ProductStatusCollectorSupplier;
import com.cboe.application.supplier.ProductStatusCollectorSupplierFactory;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class TradingSessionEventBaseListener implements InstrumentedEventChannelListener
{
    private   ProductStatusCollectorSupplier productStatusSupplier;
    protected String                         sessionName;
    protected String                         instrumentorName;

    public TradingSessionEventBaseListener(String __sessionName)
    {
        super();

        sessionName = __sessionName;
        productStatusSupplier = ProductStatusCollectorSupplierFactory.find();
        String className = this.getClass().getName();
        instrumentorName = InstrumentorNameHelper.createInstrumentorName(
                new String[] { className.substring(1+className.lastIndexOf('.')), sessionName },
                this);
        subscribeForEvents();
    }

    public void shutdown()
    {
        unsubscribeForEvents();
        InstrumentedEventChannelAdapterFactory.find().removeChannelListener(this);
        sessionName = null;
        instrumentorName = null;
        productStatusSupplier = null;
    }

    private void unsubscribeForEvents()
    {
        ChannelKey channelKey;

        channelKey = new ChannelKey(ChannelType.SET_CLASS_STATE, sessionName);
        InstrumentedEventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);

        channelKey = new ChannelKey(ChannelType.SET_PRODUCT_STATE, sessionName);
        InstrumentedEventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);

        channelKey = new ChannelKey(ChannelType.UPDATE_PRODUCT_BY_CLASS, sessionName);
        InstrumentedEventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);

        channelKey = new ChannelKey(ChannelType.UPDATE_PRODUCT_CLASS, sessionName);
        InstrumentedEventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);

        channelKey = new ChannelKey(ChannelType.STRATEGY_UPDATE, sessionName);
        InstrumentedEventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
    }

    /**
     * Subscribing for all the Product related Events irrespective of any key
     */
    private void subscribeForEvents()
    {
        ChannelKey channelKey;

        channelKey = new ChannelKey(ChannelType.SET_CLASS_STATE, sessionName);
        InstrumentedEventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);

        channelKey = new ChannelKey(ChannelType.SET_PRODUCT_STATE, sessionName);
        InstrumentedEventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);

        channelKey = new ChannelKey(ChannelType.UPDATE_PRODUCT_BY_CLASS, sessionName);
        InstrumentedEventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);

        channelKey = new ChannelKey(ChannelType.UPDATE_PRODUCT_CLASS, sessionName);
        InstrumentedEventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);

        channelKey = new ChannelKey(ChannelType.STRATEGY_UPDATE, sessionName);
        InstrumentedEventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
    }


 /*   private SessionProductStruct generateProductFromStrategy(SessionStrategyStruct strategyStruct)
    {
        SessionProductStruct product = new SessionProductStruct();

        product.productState = ProductStates.CLOSED;
        product.productStateTransactionSequenceNumber = 0;
        product.productStruct = ClientProductStructBuilder.cloneProduct(strategyStruct.sessionProductStruct.productStruct);
        product.sessionName = strategyStruct.sessionProductStruct.sessionName;

        return product;
    }*/

    /**
     * IEC Events delivered to this method implmenting update of cache
     *
     * @param event Description of event.
     */
    public void channelUpdate(ChannelEvent event)
    {
        ChannelKey channelKey = (ChannelKey)event.getChannel();

        if (Log.isDebugOn())
        {
            Log.debug("TradingSessionEventBaseListener.channelUpdate received event " + channelKey + ":" + event.getEventData());
        }

        switch (channelKey.channelType)
        {
            case ChannelType.UPDATE_PRODUCT_CLASS :
                dispatchClass((SessionClassStruct) event.getEventData());
            break;

            case ChannelType.UPDATE_PRODUCT :
            case ChannelType.UPDATE_PRODUCT_BY_CLASS :
                dispatchProduct((SessionProductStruct) event.getEventData());
            break;

            case ChannelType.STRATEGY_UPDATE:
                SessionStrategyStruct strategyStruct = (SessionStrategyStruct) event.getEventData();
                dispatchProduct(strategyStruct.sessionProductStruct);
                dispatchStrategy(strategyStruct);
            break;

            case ChannelType.SET_PRODUCT_STATE :
                dispatchProductStates((ProductStateStruct[])event.getEventData());
            break;

            case ChannelType.SET_CLASS_STATE :
                dispatchClassState((ClassStateStruct)event.getEventData());
            break;

            default:
                if (Log.isDebugOn())
                {
                    Log.debug("TradingSessionEventBaseListener.channelUpdate does not handle this channel: " + channelKey.channelType);
                }
            break;
        }
    }

    public void queueInstrumentationInitiated()
    { }

    public String getName()
    {
        return instrumentorName;
    }

    protected void dispatchClass(SessionClassStruct classStruct)
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.UPDATE_PRODUCT_CLASS, new SessionKeyContainer(classStruct.sessionName, classStruct.classStruct.productType));
        ChannelEvent event = productStatusSupplier.getChannelEvent(this, channelKey, classStruct);
        productStatusSupplier.dispatch(event);

        channelKey = new ChannelKey(ChannelType.UPDATE_PRODUCT_CLASS, new SessionKeyContainer(classStruct.sessionName, classStruct.classStruct.classKey));
        event = productStatusSupplier.getChannelEvent(this, channelKey, classStruct);
        productStatusSupplier.dispatch(event);
    }

    protected void dispatchProduct(SessionProductStruct productStruct)
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.UPDATE_PRODUCT_BY_CLASS, new SessionKeyContainer(productStruct.sessionName, productStruct.productStruct.productKeys.classKey));
        ChannelEvent event = productStatusSupplier.getChannelEvent(this, channelKey, productStruct);
        productStatusSupplier.dispatch(event);

        channelKey = new ChannelKey(ChannelType.UPDATE_PRODUCT, new SessionKeyContainer(productStruct.sessionName, productStruct.productStruct.productKeys.productKey));
        event = productStatusSupplier.getChannelEvent(this, channelKey, productStruct);
        productStatusSupplier.dispatch(event);
    }

    protected void dispatchStrategy(SessionStrategyStruct strategyStruct)
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.STRATEGY_UPDATE, new SessionKeyContainer(strategyStruct.sessionProductStruct.sessionName, strategyStruct.sessionProductStruct.productStruct.productKeys.classKey));
        ChannelEvent event = productStatusSupplier.getChannelEvent(this, channelKey, strategyStruct);
        productStatusSupplier.dispatch(event);

       // Update the product that is the strategy
 //       dispatchProduct(strategyStruct.sessionProductStruct);
    }

    protected void dispatchClassState(ClassStateStruct classState)
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.SET_CLASS_STATE, new SessionKeyContainer(classState.sessionName, classState.classKey));
        ChannelEvent event = productStatusSupplier.getChannelEvent(this, channelKey, classState);
        productStatusSupplier.dispatch(event);

        channelKey = new ChannelKey(ChannelType.SET_CLASS_STATE, new SessionKeyContainer(classState.sessionName, classState.classKey));
        event = productStatusSupplier.getChannelEvent(this, channelKey, classState);
        productStatusSupplier.dispatch(event);
    }

    protected void dispatchProductStates(ProductStateStruct[] productStates)
    {
        if(productStates.length > 0)
        {
            ChannelKey channelKey = new ChannelKey(ChannelType.SET_PRODUCT_STATE, new SessionKeyContainer(productStates[0].sessionName, productStates[0].productKeys.classKey));
            ChannelEvent event = productStatusSupplier.getChannelEvent(this, channelKey, productStates);
            productStatusSupplier.dispatch(event);
        }
    }
}