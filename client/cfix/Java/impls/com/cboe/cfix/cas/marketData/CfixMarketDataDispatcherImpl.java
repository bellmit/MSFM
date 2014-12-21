package com.cboe.cfix.cas.marketData;

/**
 * CfixMarketDataDispatcherImpl.java
 *
 * @author Dmitry Volpyansky
 *
 */

/*
 *  This object registers for and receives all market data traffic for a specific session, and then forwards it to all listeners of that event<br>
 *
 *  It contains the subscription maps for all listeners interested in receiving subscriptions for this session and event<br>
 *
 */

import java.io.*;
import java.lang.reflect.*;

import com.cboe.application.test.*;
import com.cboe.client.util.*;
import com.cboe.client.util.collections.*;
import com.cboe.domain.iec.ClientIECFactory;
import com.cboe.domain.iec.ConcurrentEventChannelAdapter;
import com.cboe.domain.util.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiSession.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.interfaces.cfix.*;
import com.cboe.util.*;
import com.cboe.util.channel.*;

public abstract class CfixMarketDataDispatcherImpl implements CfixMarketDataDispatcherIF
{
    protected String                   name;
    protected int                      debugFlags;
    protected CfixKeyToConsumersMap    productKeyConsumersMap;
    protected CfixKeyToConsumersMap    classKeyConsumersMap;
    protected IntIntMultipleValuesMap  classToProductsMap              = IntIntMultipleValuesMap.synchronizedMap();
    protected IntArrayHolder           channelUpdateIntArrayHolder     = new IntArrayHolder();
    protected ObjectArrayHolder        channelUpdateObjectArrayHolder  = new ObjectArrayHolder();

    protected CfixMarketDataDispatcherInstrumentationImpl cfixMarketDataDispatcherInstrumentation = new CfixMarketDataDispatcherInstrumentationImpl();
    private ConcurrentEventChannelAdapter internalEventChannel;

    public CfixMarketDataDispatcherImpl(String name)
    {
        this.name = name;

        productKeyConsumersMap  = new CfixKeyToConsumersMap(getHandledMarketDataTypeName() + "ByProduct");
        classKeyConsumersMap    = new CfixKeyToConsumersMap(getHandledMarketDataTypeName() + "ByClass");

        try
        {
            internalEventChannel = ClientIECFactory.findConcurrentEventChannelAdapter(ClientIECFactory.MARKETDATA_INSTRUMENTED_IEC);
        }
        catch (Exception e)
        {
        	Log.exception("Exception getting MARKETDATA_INSTRUMENTED_IEC!", e);
        }
    }

    public int setDebugFlags(int debugFlags)
    {
        int oldDebugFlags = this.debugFlags;

        this.debugFlags = debugFlags;

        return oldDebugFlags;
    }

    public int getDebugFlags()
    {
        return debugFlags;
    }

    public String getName()
    {
        return name;
    }

    public void accept(CfixMarketDataDispatcherVisitor cfixMarketDataDispatcherVisitor) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (cfixMarketDataDispatcherVisitor != null)
        {
            try
            {
                cfixMarketDataDispatcherVisitor.visit(this);
            }
            catch (Exception ex)
            {
                throw ExceptionBuilder.dataValidationException(ex.toString(), 0);
            }
        }
    }

    public CfixMarketDataDispatcherInstrumentation getCfixMarketDataDispatcherInstrumentation()
    {
        try
        {
            return (CfixMarketDataDispatcherInstrumentation) cfixMarketDataDispatcherInstrumentation.clone();
        }
        catch (Exception ex)
        {

        }

        return null;
    }

    protected synchronized boolean internalSubscribe(int subscriptionType, int classKey, int productKey, String sessionName, CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, CfixKeyToConsumersMap consumerMap, IntIntMultipleValuesMap classMap) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        MutableInteger mutableInteger = MutableInteger.threadLocalMutableInteger.getMutableInteger();

        int num1 = consumerMap.addConsumerToKey(productKey, cfixFixMarketDataConsumerHolder);
        classMap.putKeyValue(classKey, productKey, mutableInteger);

        if (BitHelper.isBitMaskSet(debugFlags, CfixMarketDataDispatcherIF.DEBUG_Subscribe))
        {
            Log.information(Thread.currentThread().getName() + " " + ClassHelper.getClassNameFinalPortion(this) + ".subscribing " + (classKey == productKey ? "ByClass" : "ByProduct") + " classKey(" + classKey + ") productKey(" + productKey + ") subscriptionType(" + subscriptionType + ") num1(" + num1 + ") num2(" + mutableInteger.integer + ") consumerMap(" + consumerMap + ").size(" + consumerMap.size() + ") classMap(" + classMap + ").size(" + classMap.size() + ") " + cfixFixMarketDataConsumerHolder);
        }

        if (num1 == 1 && mutableInteger.integer == 1)
        {
            if (BitHelper.isBitMaskSet(debugFlags, CfixMarketDataDispatcherIF.DEBUG_Subscribe))
            {
                Log.information(Thread.currentThread().getName() + " " + ClassHelper.getClassNameFinalPortion(this) + ".addChannelListener subscriptionType(" + subscriptionType + ") sessionName(" + sessionName + ") classKey(" + classKey + ")");
            }

            internalEventChannel.addChannelListener(this, this, new ChannelKey(subscriptionType, new SessionKeyContainer(sessionName, classKey)));

            return true;
        }

        return false;
    }

    protected synchronized boolean internalUnsubscribe(int subscriptionType, int classKey, int productKey, String sessionName, CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, CfixKeyToConsumersMap consumerMap, IntIntMultipleValuesMap classMap) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        MutableInteger mutableInteger = MutableInteger.threadLocalMutableInteger.getMutableInteger();

        int num1 = consumerMap.removeConsumerFromKey(productKey, cfixFixMarketDataConsumerHolder);
        classMap.removeKeyValue(classKey, productKey, mutableInteger);

        if (BitHelper.isBitMaskSet(debugFlags, CfixMarketDataDispatcherIF.DEBUG_Subscribe))
        {
            Log.information(Thread.currentThread().getName() + " " + ClassHelper.getClassNameFinalPortion(this) + ".unsubscribing " + (classKey == productKey ? "ByClass" : "ByProduct") + " classKey(" + classKey + ") productKey(" + productKey + ") subscriptionType(" + subscriptionType + ") num1(" + num1 + ") num2(" + mutableInteger.integer + ") consumerMap(" + consumerMap + ").size(" + consumerMap.size() + ") classMap(" + classMap + ").size(" + classMap.size() + ") " + cfixFixMarketDataConsumerHolder);
        }

        if (num1 == 0 && mutableInteger.integer == 0)
        {
            if (BitHelper.isBitMaskSet(debugFlags, CfixMarketDataDispatcherIF.DEBUG_Subscribe))
            {
                Log.information(Thread.currentThread().getName() + " " + ClassHelper.getClassNameFinalPortion(this) + ".removeChannelListener subscriptionType(" + subscriptionType + ") sessionName(" + sessionName + ") classKey(" + classKey + ")");
            }

            internalEventChannel.removeChannelListener(this, this, new ChannelKey(subscriptionType, new SessionKeyContainer(sessionName, classKey)));

            return true;
        }

        return false;
    }

    public void debugGetSubscriptionMaps(Object[] twoCfixKeyToConsumersMaps)
    {
        twoCfixKeyToConsumersMaps[0] = classKeyConsumersMap;
        twoCfixKeyToConsumersMaps[1] = productKeyConsumersMap;
    }

    public void unsubscribeConsumer(CfixMarketDataConsumer cfixFixMarketDataConsumer) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        ObjectArrayHolder arrayHolder = new ObjectArrayHolder();
        int count;

        if (!productKeyConsumersMap.isEmpty())
        {
            productKeyConsumersMap.findConsumerHolders(cfixFixMarketDataConsumer, arrayHolder);

            //if (BitHelper.isBitMaskSet(debugFlags, CfixMarketDataDispatcherIF.DEBUG_Subscribe)) {Log.information(Thread.currentThread().getName() + " " + ClassHelper.getClassNameFinalPortion(this) + " UnsubscribeConsumer(" + cfixFixMarketDataConsumer + ") product consumers(" + arrayHolder.size() + ") XXX map(" + productKeyConsumersMap + ").size(" + productKeyConsumersMap.size() + ")");}

            count = arrayHolder.size();

            for (int i = 0; i < count; i++)
            {
                try
                {
                    unsubscribeByProduct((CfixMarketDataConsumerHolder) arrayHolder.getKey(i));
                }
                catch (Exception ex)
                {
                    Log.exception(ex);
                }
            }
        }

        if (!classKeyConsumersMap.isEmpty())
        {
            classKeyConsumersMap.findConsumerHolders(cfixFixMarketDataConsumer, arrayHolder);

            //if (BitHelper.isBitMaskSet(debugFlags, CfixMarketDataDispatcherIF.DEBUG_Subscribe)) {Log.information(Thread.currentThread().getName() + " " + ClassHelper.getClassNameFinalPortion(this) + " UnsubscribeConsumer(" + cfixFixMarketDataConsumer + ") class consumers(" + arrayHolder.size() + ") XXX map(" + classKeyConsumersMap + ").size(" + classKeyConsumersMap.size() + ")");}

            count = arrayHolder.size();

            for (int i = 0; i < count; i++)
            {
                try
                {
                    unsubscribeByClass((CfixMarketDataConsumerHolder) arrayHolder.getKey(i));
                }
                catch (Exception ex)
                {
                    Log.exception(ex);
                }
            }
        }
    }

    public void debugDump(Object eventData, int channelType)
    {
        if (BitHelper.isBitMaskSet(debugFlags, CfixMarketDataDispatcherIF.DEBUG_ChannelUpdateDecode))
        {
            try
            {
                StringWriter writer = new StringWriter();
                ReflectiveStructTester.printStruct(eventData, Thread.currentThread().getName() + " " + ClassHelper.getClassNameFinalPortion(this) + ".channelUpdate object.class(" + ClassHelper.getClassName(eventData) + ") channelKey(" + channelType + ") object: ", writer);
                Log.information(writer.toString());
            }
            catch (Exception ex)
            {

            }

            return;
        }

        if (BitHelper.isBitMaskSet(debugFlags, CfixMarketDataDispatcherIF.DEBUG_ChannelUpdate))
        {
            int length = 1;
            
            if (eventData.getClass().isArray())
            {
                length = Array.getLength(eventData);
            }
            
            Log.information(Thread.currentThread().getName() + " " + ClassHelper.getClassNameFinalPortion(this) + ".channelUpdate object.class='" + ClassHelper.getClassName(eventData) + "' channelKey='" + channelType + "' length=" + length);
            
        }
    }

    public void subscribeByProduct(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        SessionProductStruct sessionProductStruct = cfixFixMarketDataConsumerHolder.getSessionProductStruct();

        internalSubscribe(getHandledChannelType(),
                          sessionProductStruct.productStruct.productKeys.classKey,
                          sessionProductStruct.productStruct.productKeys.productKey,
                          sessionProductStruct.sessionName,
                          cfixFixMarketDataConsumerHolder,
                          productKeyConsumersMap,
                          classToProductsMap);
    }

    public void unsubscribeByProduct(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        SessionProductStruct sessionProductStruct = cfixFixMarketDataConsumerHolder.getSessionProductStruct();

        internalUnsubscribe(getHandledChannelType(),
                            sessionProductStruct.productStruct.productKeys.classKey,
                            sessionProductStruct.productStruct.productKeys.productKey,
                            sessionProductStruct.sessionName,
                            cfixFixMarketDataConsumerHolder,
                            productKeyConsumersMap,
                            classToProductsMap);
    }

    public void subscribeByClass(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        SessionClassStruct sessionClassStruct = cfixFixMarketDataConsumerHolder.getSessionClassStruct();

        internalSubscribe(getHandledChannelType(),
                          sessionClassStruct.classStruct.classKey,
                          sessionClassStruct.classStruct.classKey,
                          sessionClassStruct.sessionName,
                          cfixFixMarketDataConsumerHolder,
                          classKeyConsumersMap,
                          classToProductsMap);
    }

    public void unsubscribeByClass(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        SessionClassStruct sessionClassStruct = cfixFixMarketDataConsumerHolder.getSessionClassStruct();

        internalUnsubscribe(getHandledChannelType(),
                            sessionClassStruct.classStruct.classKey,
                            sessionClassStruct.classStruct.classKey,
                            sessionClassStruct.sessionName,
                            cfixFixMarketDataConsumerHolder,
                            classKeyConsumersMap,
                            classToProductsMap);
    }

    public void channelUpdate(ChannelEvent channelEvent)
    {
        ChannelKey channelKey = (ChannelKey) channelEvent.getChannel();

        // Event data could be an array of CurrentMarketContainers - each one in turn would have an array of bestMarkets and bestPublicMarkets 
        Object eventData = channelEvent.getEventData();

        //Log.information(Thread.currentThread().getName() + " " + ClassHelper.getClassNameFinalPortion(this) + " XXX 1 debugFlags: (" + debugFlags + ") [" + DebugFlagBuilder.stringizeDispatcherDebugFlags(debugFlags) + "]");
        //Log.information(Thread.currentThread().getName() + " " + ClassHelper.getClassNameFinalPortion(this) + " XXX 2 channelType: (" + channelKey.channelType + ") handledChannelType: [" + getHandledChannelType() + "]");

        // keep this above the check so we can see what it is that we are getting (or not getting)
        debugDump(eventData, channelKey.channelType);

        if (channelKey.channelType != getHandledChannelType())
        {
            return;
        }

        Object[] structs;

        if (eventData.getClass().isArray())
        {
            structs = (Object[]) eventData;
            //if(Log.isDebugOn())
            //{
            //    Log.debug("In CfixMarketDataDispatcherImpl: eventData is an Array of length: " + structs.length);
            //}
        }
        else
        {
            structs = new Object[] {eventData};
            //if(Log.isDebugOn())
            //{
            //    Log.debug("In CfixMarketDataDispatcherImpl: placing eventData in an Object Array of length: " + structs.length);
            //}

        }            

        int classKey = getClassKey(structs[0]);
        int count;
        int i;
        int j;
        CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder;

        cfixMarketDataDispatcherInstrumentation.incMessagesConsumed();

        channelUpdateObjectArrayHolder.clear();

        classKeyConsumersMap.getConsumersForKey(classKey, channelUpdateObjectArrayHolder);

        //Log.information(Thread.currentThread().getName() + " " + ClassHelper.getClassNameFinalPortion(this) + " XXX 3 classKey: (" + classKey + ") holder.size(" + channelUpdateObjectArrayHolder.size() + ")");

        if (!channelUpdateObjectArrayHolder.isEmpty())
        {
            cfixMarketDataDispatcherInstrumentation.incMessagesProcessed();

            count = channelUpdateObjectArrayHolder.size();

            for (i = 0; i < count; i++)
            {
                try
                {
                    cfixFixMarketDataConsumerHolder = (CfixMarketDataConsumerHolder) channelUpdateObjectArrayHolder.getKey(i);

                    if (BitHelper.isBitMaskSet(debugFlags, CfixMarketDataDispatcherIF.DEBUG_Accept))
                    {
                        Log.information(ClassHelper.getClassNameFinalPortion(this) + ".acceptMarketData" + getHandledMarketDataTypeName() + "(" + cfixFixMarketDataConsumerHolder + ")");
                    }

                    acceptMarketData(cfixFixMarketDataConsumerHolder, structs);

                    cfixMarketDataDispatcherInstrumentation.incMessagesDispatched();
                }
                catch (Exception ex)
                {
                    // nothing can be done -- continue with other consumers
                    Log.exception(ex);
                }
            }
        }

        channelUpdateIntArrayHolder.clear();

        classToProductsMap.getValuesForKey(classKey, channelUpdateIntArrayHolder);

        count = channelUpdateIntArrayHolder.size();

        if (count == 0 || (count == 1 && channelUpdateIntArrayHolder.getKey(0) == classKey))
        {
            return;
        }

        int productKey;

        for (i = 0; i < structs.length; i++)
        {
            productKey = getProductKey(structs[i]);

            if (!channelUpdateIntArrayHolder.containsKey(productKey))
            {
                continue;
            }

            channelUpdateObjectArrayHolder.clear();

            productKeyConsumersMap.getConsumersForKey(productKey, channelUpdateObjectArrayHolder);
            if (!channelUpdateObjectArrayHolder.isEmpty())
            {
                cfixMarketDataDispatcherInstrumentation.incMessagesProcessed();

                count = channelUpdateObjectArrayHolder.size();

                for (j = 0; j < count; j++)
                {
                    try
                    {
                        cfixFixMarketDataConsumerHolder = (CfixMarketDataConsumerHolder) channelUpdateObjectArrayHolder.getKey(j);

                        if (BitHelper.isBitMaskSet(debugFlags, CfixMarketDataDispatcherIF.DEBUG_Accept))
                        {
                            Log.information(ClassHelper.getClassNameFinalPortion(this) + ".acceptMarketData" + getHandledMarketDataTypeName() + "(" + cfixFixMarketDataConsumerHolder + ")");
                        }

                        acceptMarketData(cfixFixMarketDataConsumerHolder, structs[i]);

                        cfixMarketDataDispatcherInstrumentation.incMessagesDispatched();
                    }
                    catch (Exception ex)
                    {
                        // nothing can be done -- continue with other channelUpdateObjectArrayHolder
                        Log.exception(ex);
                    }
                }
            }
        }
    }

    protected abstract void acceptMarketData(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, Object struct)    throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    protected abstract void acceptMarketData(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, Object[] structs) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    protected abstract int  getClassKey(Object struct);
    protected abstract int  getProductKey(Object struct);
    public void queueInstrumentationInitiated(){};
}
