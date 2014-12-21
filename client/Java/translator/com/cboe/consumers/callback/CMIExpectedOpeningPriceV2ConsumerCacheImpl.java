//
// -----------------------------------------------------------------------------------
// Source file: CMIExpectedOpeningPriceV2ConsumerCacheImpl.java
//
// PACKAGE: com.cboe.consumers.callback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import java.util.*;

import org.omg.CORBA.UserException;

import com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer;

import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.consumers.callback.CMIExpectedOpeningPriceV2ConsumerCache;

import com.cboe.util.event.EventChannelAdapter;

import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;

import com.cboe.domain.util.SessionKeyContainer;

public class CMIExpectedOpeningPriceV2ConsumerCacheImpl extends AbstractCallbackConsumerCache implements
        CMIExpectedOpeningPriceV2ConsumerCache
{
    public static final int DEFAULT_MAX_EOP_CONSUMERS = 10;
    private List<CMIExpectedOpeningPriceConsumer> consumers;
    private int consumerIndex = 0;
    private int maxConsumers;

    public CMIExpectedOpeningPriceV2ConsumerCacheImpl(EventChannelAdapter eventChannel)
    {
        this(eventChannel, DEFAULT_MAX_EOP_CONSUMERS);
    }

    public CMIExpectedOpeningPriceV2ConsumerCacheImpl(EventChannelAdapter eventChannel, int maxConsumers)
    {
        super(eventChannel);
        if(GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.COMMON))
        {
            GUILoggerHome.find().debug(getClass().getName(), GUILoggerBusinessProperty.COMMON, "Maximum number of EOP V2 consumers cached will be "+maxConsumers);
        }
        this.maxConsumers = maxConsumers;
    }

    public CMIExpectedOpeningPriceConsumer getExpectedOpeningPriceConsumer(SessionKeyWrapper key)
    {
        return (CMIExpectedOpeningPriceConsumer) getCallbackConsumer(key);
    }

    public CMIExpectedOpeningPriceConsumer getExpectedOpeningPriceConsumer(SessionProductClass productClass)
    {
        return getExpectedOpeningPriceConsumer(productClass.getSessionKeyWrapper());
    }

    public CMIExpectedOpeningPriceConsumer getExpectedOpeningPriceConsumer(String sessionName, int classKey)
    {
        return getExpectedOpeningPriceConsumer(new SessionKeyContainer(sessionName, classKey));
    }

    public CMIExpectedOpeningPriceConsumer getExpectedOpeningPriceConsumerForProduct(String sessionName, int productKey)
    {
        int classKey = 0;
        try
        {
            Product product = APIHome.findProductQueryAPI().getProductByKey(productKey);
            if(product != null)
            {
                classKey = product.getProductKeysStruct().classKey;
            }
        }
        catch(UserException e)
        {
            // if the Product wasn't found, get a "default" EOP consumer from the cache
            GUILoggerHome.find().exception(
                    getClass().getName() + ".getExpectedOpeningPriceConsumerForProduct("+sessionName+", "+productKey+')',
                    "Product could not be found for productKey "+productKey+". Returning a default CMIExpectedOpeningPriceConsumer.", e);
        }
        return getExpectedOpeningPriceConsumer(sessionName, classKey);
    }


    public synchronized void cleanupCallbackConsumers()
    {
        super.cleanupCallbackConsumers();
        getConsumerList().clear();
    }

    /**
     * Create a callback consumer object to be added to the cache.
     *
     * This implementation will create and cache only 'maxConsumers' new consumer objects.
     * After 'maxConsumers' have been created, each time this is called it will iterate
     * through the cached consumers and return the next one.
     *
     * @return CMI callback consumer
     */
    protected synchronized org.omg.CORBA.Object createNewCallbackConsumer()
    {
        CMIExpectedOpeningPriceConsumer retVal;
        // if the max number of consumers haven't been created yet, create a new consumer and cache it in the list
        if(consumerIndex < maxConsumers)
        {
            retVal = ExpectedOpeningPriceV2ConsumerFactory.create(getEventChannel());
            getConsumerList().add(retVal);
        }
        else
        {
            // if the max number of consumers have already been created, return the next cached consumer from the list
            retVal = getConsumerList().get(consumerIndex % maxConsumers);
        }
        consumerIndex++;
        return retVal;
    }

    private synchronized List<CMIExpectedOpeningPriceConsumer> getConsumerList()
    {
        if(consumers == null)
        {
            consumers = new ArrayList<CMIExpectedOpeningPriceConsumer>(maxConsumers);
        }
        return consumers;
    }
}
