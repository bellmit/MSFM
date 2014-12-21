//
// -----------------------------------------------------------------------------------
// Source file: ProductStatusConsumerImpl.java
//
// PACKAGE: com.cboe.consumers.callback;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import com.cboe.interfaces.callback.ProductStatusConsumer;

import com.cboe.idl.cmiSession.ProductStateStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;

import com.cboe.domain.util.SessionKeyContainer;

import com.cboe.interfaces.presentation.product.SessionProduct;

import com.cboe.presentation.product.SessionProductFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;

/**
 * This is the implementation of the ProductStatusConsumer callback object which
 * receives product status data from a callback supplier on the CAS and publishes it
 * on a designated event channel.
 */
public class ProductStatusConsumerImpl implements ProductStatusConsumer
{
    public static final int LOG_COUNT = 1;
    private EventChannelAdapter eventChannel = null;
    protected int stateCount;
    protected int updateCount;

    /**
     * @param eventChannel the event channel to publish to.
     */
    public ProductStatusConsumerImpl(EventChannelAdapter eventChannel)
    {
        super();
        this.eventChannel = eventChannel;
        this.stateCount = 0;
        this.updateCount = 0;
    }

    /**
     * The callback method used by the CAS to publish product state data.
     * @param productState the product state data to publish to all subscribed listeners
     */
    public void acceptProductState(ProductStateStruct[] productState)
    {
        ChannelKey key;
        ChannelEvent event;

        for (int i = 0; i < productState.length; i++)
        {
            this.stateCount++;
            if(GUILoggerHome.find().isDebugOn() &&
               GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY) &&
               this.stateCount % LOG_COUNT == 0)
            {
                String item = productState[i].sessionName + "." + productState[i].productKeys.productKey;
                GUILoggerHome.find().debug(this.getClass().getName() + ":acceptProductState() Count for " + item + " ",
                                           GUILoggerBusinessProperty.PRODUCT_QUERY, String.valueOf(this.stateCount));
            }

            key = new ChannelKey(ChannelType.CB_PRODUCT_STATE,
                                 new SessionKeyContainer(productState[i].sessionName,
                                                         productState[i].productKeys.productKey));
            event = EventChannelAdapterFactory.find().getChannelEvent(this, key, productState[i]);
            eventChannel.dispatch(event);

            key = new ChannelKey(ChannelType.CB_PRODUCT_STATE_BY_CLASS,
                                 new SessionKeyContainer(productState[i].sessionName,
                                                         productState[i].productKeys.classKey));
            event = EventChannelAdapterFactory.find().getChannelEvent(this, key, productState[i]);
            eventChannel.dispatch(event);

            key = new ChannelKey(ChannelType.CB_PRODUCT_STATE_BY_CLASS, productState[i].sessionName);
            event = EventChannelAdapterFactory.find().getChannelEvent(this, key, productState[i]);
            eventChannel.dispatch(event);
        }
    }

    /**
    * Accept an updated product struct.
    * @param updatedProduct an updated product struct
    */
    public void updateProduct(SessionProductStruct updatedProduct)
    {
        ChannelKey key;
        ChannelEvent event;

        this.updateCount++;
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY) &&
           this.updateCount % LOG_COUNT == 0)
        {
            String item = updatedProduct.sessionName + "." + updatedProduct.productStruct.productKeys.productKey;
            GUILoggerHome.find().debug(this.getClass().getName() + ":updateProduct() Count for " + item + " ",
                                       GUILoggerBusinessProperty.PRODUCT_QUERY, String.valueOf(this.updateCount));
        }

        SessionProduct sessionProduct = SessionProductFactory.create(updatedProduct);

        key = new ChannelKey(ChannelType.CB_PRODUCT_UPDATE,
                             new SessionKeyContainer(updatedProduct.sessionName,
                                                     updatedProduct.productStruct.productKeys.productKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, sessionProduct);
        eventChannel.dispatch(event);

        key = new ChannelKey(ChannelType.CB_PRODUCT_UPDATE_BY_CLASS,
                             new SessionKeyContainer(updatedProduct.sessionName,
                                                     updatedProduct.productStruct.productKeys.classKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, sessionProduct);
        eventChannel.dispatch(event);

        key = new ChannelKey(ChannelType.CB_PRODUCT_UPDATE_BY_CLASS, updatedProduct.sessionName);
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, sessionProduct);
        eventChannel.dispatch(event);
    }
}

