package com.cboe.cfix.cas.marketData;

import com.cboe.application.test.ReflectiveStructTester;
import com.cboe.cfix.cas.shared.CfixServicesHelper;
import com.cboe.client.util.BitHelper;
import com.cboe.client.util.ClassHelper;
import com.cboe.client.util.MutableInteger;
import com.cboe.client.util.collections.IntArrayHolder;
import com.cboe.client.util.collections.IntIntMultipleValuesMap;
import com.cboe.client.util.collections.ObjectArrayHolder;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiCallbackV4.CMITickerConsumerPOA;
import com.cboe.idl.cmiConstants.QueueActions;
import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.idl.cmiConstants.PriceConstants;
import com.cboe.idl.cmiMarketData.TickerStruct;
import com.cboe.idl.cmiMarketData.TickerStructV4;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.cfix.*;
import com.cboe.util.ExceptionBuilder;

import java.io.StringWriter;
import java.lang.reflect.Array;

/**
 * CfixMDXMarketDataTickerDispatcherImpl.java
 *
 * @author Vivek Beniwal
 *
 */

/*
 *  This object registers for and receives all Ticker market data traffic for a specific session, and then forwards it to all listeners<br>
 *
 *  It contains the subscription maps for all listeners interested in receiving Ticker subscriptions for this session<br>
 *
 */
public final class CfixMDXMarketDataTickerDispatcherImpl extends CMITickerConsumerPOA implements CfixMDXMarketDataDispatcherIF
{
    protected String                    name;
    protected int                       classKey;
    protected int                       debugFlags;
    protected CfixKeyToConsumersMap     productKeyConsumersMap;
    protected CfixKeyToConsumersMap     classKeyConsumersMap;
    protected IntIntMultipleValuesMap   classToProductsMap              = IntIntMultipleValuesMap.synchronizedMap();
    protected IntArrayHolder            acceptTickerIntArrayHolder      = new IntArrayHolder();
    protected ObjectArrayHolder         acceptTickerObjectArrayHolder   = new ObjectArrayHolder();

    protected CfixCasLogin              myCfixCasLogin;
    protected CfixCasExternalLogin      myCfixCasExternalLogin;
    protected boolean                   isUnderlyingSub                 = false;

    public static final String W_MAIN_Str = "W_MAIN";
    public static final String Underlying_Str =  "Underlying";

    public static final String MARKET_DATA_TYPE_NAME = "Ticker";

    protected CfixMarketDataDispatcherInstrumentationImpl cfixMarketDataDispatcherInstrumentation
                                                                    = new CfixMarketDataDispatcherInstrumentationImpl();

    public CfixMDXMarketDataTickerDispatcherImpl(String name, int classKey)
    {
        if(Log.isDebugOn())
        {
            StringBuffer strBuf = new StringBuffer("Initialized CfixMDXMarketDataTickerDispatcherImpl for Dispatcher Name : ");
            strBuf.append(name).append(" and ClassKey : ").append(classKey).append(". CFIX is MDX Enabled.");
            Log.debug(strBuf.toString());
        }

        this.name = name;
        this.classKey = classKey;

        productKeyConsumersMap  = new CfixKeyToConsumersMap(getHandledMarketDataTypeName() + "ByProduct");
        classKeyConsumersMap    = new CfixKeyToConsumersMap(getHandledMarketDataTypeName() + "ByClass");

        if (name.substring(0,10).equalsIgnoreCase(Underlying_Str))
        {
		if(Log.isDebugOn())
		    Log.debug(" Dispatcher name is determined to be Underlying ");
            this.isUnderlyingSub = true;
            myCfixCasExternalLogin = CfixServicesHelper.getCfixCasExternalLogin();
            myCfixCasExternalLogin.associateExternalUserWithOrb(this);
        } else {
            this.isUnderlyingSub = false;
            // activate this object - associate this with the Orb so we can subscribe.
            myCfixCasLogin = CfixServicesHelper.getCfixCasLogin();
            myCfixCasLogin.associateWithOrb(this);
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

    public int getHandledMarketDataType()
    {
        return CfixMDXMarketDataDispatcherIF.MarketDataType_Ticker;
    }

    public String getHandledMarketDataTypeName()
    {
        return MARKET_DATA_TYPE_NAME;
    }

    public void accept(CfixMDXMarketDataDispatcherVisitor cfixMDXMarketDataDispatcherVisitor)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (cfixMDXMarketDataDispatcherVisitor != null)
        {
            try
            {
                cfixMDXMarketDataDispatcherVisitor.visit(this);
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

    protected synchronized boolean internalSubscribe(int classKey, int productKey, String sessionName,
                                                     CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder,
                                                     CfixKeyToConsumersMap consumerMap, IntIntMultipleValuesMap classMap)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        MutableInteger mutableInteger = MutableInteger.threadLocalMutableInteger.getMutableInteger();

        int num1 = consumerMap.addConsumerToKey(productKey, cfixFixMarketDataConsumerHolder);
        classMap.putKeyValue(classKey, productKey, mutableInteger);

        if (BitHelper.isBitMaskSet(debugFlags, CfixMDXMarketDataDispatcherIF.DEBUG_Subscribe))
        {
            Log.information(Thread.currentThread().getName() + " " + ClassHelper.getClassNameFinalPortion(this) + ".subscribing "
                    + (classKey == productKey ? "ByClass" : "ByProduct") + " classKey(" + classKey + ") productKey(" + productKey
                    + ") subscriptionType(" + MARKET_DATA_TYPE_NAME  + ") num1(" + num1 + ") num2(" + mutableInteger.integer
                    + ") consumerMap(" + consumerMap + ").size(" + consumerMap.size() + ") classMap(" + classMap
                    + ").size(" + classMap.size() + ") " + cfixFixMarketDataConsumerHolder);
        }

        if (num1 == 1 && mutableInteger.integer == 1)
        {
            if (BitHelper.isBitMaskSet(debugFlags, CfixMDXMarketDataDispatcherIF.DEBUG_Subscribe))
            {
                Log.information(Thread.currentThread().getName() + " " + ClassHelper.getClassNameFinalPortion(this)
                        + ".subcribeTicker for sessionName(" + sessionName + ") classKey(" + classKey + ")");
            }

            if(Log.isDebugOn())
            {
                Log.debug("In CfixMDXMarketDataTickerDispatcherImpl:internalSubscribe: MDX Enabled CFIX: Subscribing for Ticker.");
            }
            if (this.isUnderlyingSub)
            {
                this.myCfixCasExternalLogin.subscribeExternalUserTicker(classKey, this._this(), QueueActions.OVERLAY_LAST);
            } else
            {
                this.myCfixCasLogin.subscribeTicker(classKey, this._this(), QueueActions.OVERLAY_LAST);
            }

            return true;
        }

        return false;
    }

    protected synchronized boolean internalUnsubscribe(int classKey, int productKey, String sessionName,
                                                       CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder,
                                                       CfixKeyToConsumersMap consumerMap, IntIntMultipleValuesMap classMap)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        MutableInteger mutableInteger = MutableInteger.threadLocalMutableInteger.getMutableInteger();

        int num1 = consumerMap.removeConsumerFromKey(productKey, cfixFixMarketDataConsumerHolder);
        classMap.removeKeyValue(classKey, productKey, mutableInteger);

        if (BitHelper.isBitMaskSet(debugFlags, CfixMDXMarketDataDispatcherIF.DEBUG_Subscribe))
        {
            Log.information(Thread.currentThread().getName() + " " + ClassHelper.getClassNameFinalPortion(this)
                    + ".unsubscribing " + (classKey == productKey ? "ByClass" : "ByProduct") + " classKey(" + classKey
                    + ") productKey(" + productKey + ") subscriptionType(" + MARKET_DATA_TYPE_NAME + ") num1(" + num1 + ") num2("
                    + mutableInteger.integer + ") consumerMap(" + consumerMap + ").size(" + consumerMap.size()
                    + ") classMap(" + classMap + ").size(" + classMap.size() + ") " + cfixFixMarketDataConsumerHolder);
        }

        if (num1 == 0 && mutableInteger.integer == 0)
        {
            if (BitHelper.isBitMaskSet(debugFlags, CfixMDXMarketDataDispatcherIF.DEBUG_Subscribe))
            {
                Log.information(Thread.currentThread().getName() + " " + ClassHelper.getClassNameFinalPortion(this)
                        + ".unsubscribeTicker for sessionName(" + sessionName + ") classKey(" + classKey + ")");
            }

            if(Log.isDebugOn())
            {
                Log.debug("In CfixMDXMarketDataTickerDispatcherImpl:internalUnsubscribe: MDX Enabled CFIX: Unsubscribing for Ticker.");
            }
            if (this.isUnderlyingSub)
            {
                this.myCfixCasExternalLogin.unsubscribeExternalUserTicker(classKey, this._this());
            } else
            {
                this.myCfixCasLogin.unsubscribeTicker(classKey, this._this());
            }

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

            //if (BitHelper.isBitMaskSet(debugFlags, CfixMDXMarketDataDispatcherIF.DEBUG_Subscribe)) {Log.information(Thread.currentThread().getName() + " " + ClassHelper.getClassNameFinalPortion(this) + " UnsubscribeConsumer(" + cfixFixMarketDataConsumer + ") product consumers(" + arrayHolder.size() + ") XXX map(" + productKeyConsumersMap + ").size(" + productKeyConsumersMap.size() + ")");}

            count = arrayHolder.size();

            for (int i = 0; i < count; i++)
            {
                try
                {
                    unsubscribeByProduct((CfixMDXMarketDataConsumerHolder) arrayHolder.getKey(i), true);
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

            //if (BitHelper.isBitMaskSet(debugFlags, CfixMDXMarketDataDispatcherIF.DEBUG_Subscribe)) {Log.information(Thread.currentThread().getName() + " " + ClassHelper.getClassNameFinalPortion(this) + " UnsubscribeConsumer(" + cfixFixMarketDataConsumer + ") class consumers(" + arrayHolder.size() + ") XXX map(" + classKeyConsumersMap + ").size(" + classKeyConsumersMap.size() + ")");}

            count = arrayHolder.size();

            for (int i = 0; i < count; i++)
            {
                try
                {
                    unsubscribeByClass((CfixMDXMarketDataConsumerHolder) arrayHolder.getKey(i), true);
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
        if (BitHelper.isBitMaskSet(debugFlags, CfixMDXMarketDataDispatcherIF.DEBUG_ChannelUpdateDecode))
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

        if (BitHelper.isBitMaskSet(debugFlags, CfixMDXMarketDataDispatcherIF.DEBUG_ChannelUpdate))
        {
            int length = 1;

            if (eventData.getClass().isArray())
            {
                length = Array.getLength(eventData);
            }

            Log.information(Thread.currentThread().getName() + " " + ClassHelper.getClassNameFinalPortion(this) + ".channelUpdate object.class='" + ClassHelper.getClassName(eventData) + "' channelKey='" + channelType + "' length=" + length);

        }
    }

    public void subscribeByProduct(CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, boolean isUserSub) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        SessionProductStruct sessionProductStruct = cfixFixMarketDataConsumerHolder.getSessionProductStruct();
        if (Log.isDebugOn())
                Log.debug("CfixMDXMarketDataTickerDispatcherImpl:subscribeByProduct:for productKey : " + sessionProductStruct.productStruct.productKeys.productKey);
        internalSubscribe(sessionProductStruct.productStruct.productKeys.classKey,
                          sessionProductStruct.productStruct.productKeys.productKey,
                          sessionProductStruct.sessionName,
                          cfixFixMarketDataConsumerHolder,
                          productKeyConsumersMap,
                          classToProductsMap);
    }

    public void unsubscribeByProduct(CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, boolean isUserSub) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        SessionProductStruct sessionProductStruct = cfixFixMarketDataConsumerHolder.getSessionProductStruct();
        if (Log.isDebugOn())
                Log.debug("CfixMDXMarketDataTickerDispatcherImpl:unsubscribeByProduct:for productKey : " + sessionProductStruct.productStruct.productKeys.productKey);
        internalUnsubscribe(sessionProductStruct.productStruct.productKeys.classKey,
                            sessionProductStruct.productStruct.productKeys.productKey,
                            sessionProductStruct.sessionName,
                            cfixFixMarketDataConsumerHolder,
                            productKeyConsumersMap,
                            classToProductsMap);
    }

    public void subscribeByClass(CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, boolean isUserSub) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        SessionClassStruct sessionClassStruct = cfixFixMarketDataConsumerHolder.getSessionClassStruct();
        if (Log.isDebugOn())
                Log.debug("CfixMDXMarketDataTickerDispatcherImpl:subscribeByClass:for classKey : " + sessionClassStruct.classStruct.classKey);
        internalSubscribe(sessionClassStruct.classStruct.classKey,
                          sessionClassStruct.classStruct.classKey,
                          sessionClassStruct.sessionName,
                          cfixFixMarketDataConsumerHolder,
                          classKeyConsumersMap,
                          classToProductsMap);
    }

    public void unsubscribeByClass(CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, boolean isUserSub) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        SessionClassStruct sessionClassStruct = cfixFixMarketDataConsumerHolder.getSessionClassStruct();
        if (Log.isDebugOn())
                Log.debug("CfixMDXMarketDataTickerDispatcherImpl:unsubscribeByClass:for classKey : " + sessionClassStruct.classStruct.classKey);
        internalUnsubscribe(sessionClassStruct.classStruct.classKey,
                            sessionClassStruct.classStruct.classKey,
                            sessionClassStruct.sessionName,
                            cfixFixMarketDataConsumerHolder,
                            classKeyConsumersMap,
                            classToProductsMap);
    }

    public void acceptTicker(TickerStructV4[] tickerStructsV4, int messageSequence, int queueDepth, short queueAction)
    {
        if (Log.isDebugOn())
            Log.debug("CfixMDXMarketDataTickerDispatcherImpl -> CFIX accepting Ticker market data from MDX!!");
        //acceptTickerV1(tickerStructsV4, messageSequence, queueDepth,  queueAction);
        acceptTickerV4(tickerStructsV4, messageSequence, queueDepth,  queueAction);
    }

    public synchronized void acceptTickerV1(TickerStructV4[] tickerStructsV4, int messageSequence, int queueDepth, short queueAction)
    {
        int classKey = tickerStructsV4[0].classKey;

        int count;
        int i;
        int j;
        CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder;

        cfixMarketDataDispatcherInstrumentation.incMessagesConsumed();

        acceptTickerObjectArrayHolder.clear();

        classKeyConsumersMap.getConsumersForKey(classKey, acceptTickerObjectArrayHolder);

        //Log.information(Thread.currentThread().getName() + " " + ClassHelper.getClassNameFinalPortion(this) + " XXX 3 classKey: (" + classKey + ") holder.size(" + acceptTickerObjectArrayHolder.size() + ")");

        if (!acceptTickerObjectArrayHolder.isEmpty())
        {
            cfixMarketDataDispatcherInstrumentation.incMessagesProcessed();

            count = acceptTickerObjectArrayHolder.size();

            for (i = 0; i < count; i++)
            {
                try
                {
                    cfixFixMarketDataConsumerHolder = (CfixMDXMarketDataConsumerHolder) acceptTickerObjectArrayHolder.getKey(i);

                    TickerStruct[] tickerStructs = mapTickerStructsV4ToV1(tickerStructsV4, cfixFixMarketDataConsumerHolder);

                    if (BitHelper.isBitMaskSet(debugFlags, CfixMDXMarketDataDispatcherIF.DEBUG_Accept))
                    {
                        Log.information(ClassHelper.getClassNameFinalPortion(this) + ".acceptTicker for Class" + "(" + cfixFixMarketDataConsumerHolder + ")");
                    }

                    acceptMarketDataTicker(cfixFixMarketDataConsumerHolder, tickerStructs);

                    cfixMarketDataDispatcherInstrumentation.incMessagesDispatched();
                }
                catch (Exception ex)
                {
                    // nothing can be done -- continue with other consumers
                    Log.exception(ex);
                }
            }
        }

        count = acceptTickerIntArrayHolder.size();

        if (count == 0 || (count == 1 && acceptTickerIntArrayHolder.getKey(0) == classKey))
        {
            return;
        }

        int productKey;

        // dispatch market data to users who have a product subscription -
        for (i = 0; i < tickerStructsV4.length; i++)
        {
            productKey = tickerStructsV4[i].productKey;

            if (!acceptTickerIntArrayHolder.containsKey(productKey))
            {
                continue;
            }

            acceptTickerObjectArrayHolder.clear();

            productKeyConsumersMap.getConsumersForKey(productKey, acceptTickerObjectArrayHolder);
            if (!acceptTickerObjectArrayHolder.isEmpty())
            {
                cfixMarketDataDispatcherInstrumentation.incMessagesProcessed();

                count = acceptTickerObjectArrayHolder.size();

                for (j = 0; j < count; j++)
                {
                    try
                    {
                        cfixFixMarketDataConsumerHolder = (CfixMDXMarketDataConsumerHolder) acceptTickerObjectArrayHolder.getKey(j);

                        TickerStruct tickerStruct = mapTickerStructV4ToV1(tickerStructsV4[i], cfixFixMarketDataConsumerHolder);

                        if (BitHelper.isBitMaskSet(debugFlags, CfixMDXMarketDataDispatcherIF.DEBUG_Accept))
                        {
                            Log.information(ClassHelper.getClassNameFinalPortion(this) + ".acceptTicker for Product" + getHandledMarketDataTypeName() + "(" + cfixFixMarketDataConsumerHolder + ")");
                        }

                        acceptMarketDataTicker(cfixFixMarketDataConsumerHolder, tickerStruct);

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

    public synchronized void acceptTickerV4(TickerStructV4[] tickerStructsV4, int messageSequence, int queueDepth, short queueAction)
    {
        //if (Log.isDebugOn())
        //        Log.debug("CfixMDXMarketDataTickerDispatcherImpl -> acceptTickerV4 ");

        int classKey = tickerStructsV4[0].classKey;

        cfixMarketDataDispatcherInstrumentation.incMessagesConsumed();

        acceptTickerObjectArrayHolder.clear();

        classKeyConsumersMap.getConsumersForKey(classKey, acceptTickerObjectArrayHolder);

        //Log.information(Thread.currentThread().getName() + " " + ClassHelper.getClassNameFinalPortion(this) + " XXX 3 classKey: (" + classKey + ") holder.size(" + acceptTickerObjectArrayHolder.size() + ")");

        if (!acceptTickerObjectArrayHolder.isEmpty())
        {
            //if (Log.isDebugOn())
            //    Log.debug("CfixMDXMarketDataTickerDispatcherImpl -> Class Subscriptions exist!");

            cfixMarketDataDispatcherInstrumentation.incMessagesProcessed();

            int count1 = acceptTickerObjectArrayHolder.size();

            for (int i = 0; i < count1; i++)
            {
                try
                {
                    CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder = (CfixMDXMarketDataConsumerHolder) acceptTickerObjectArrayHolder.getKey(i);

                    if (BitHelper.isBitMaskSet(debugFlags, CfixMDXMarketDataDispatcherIF.DEBUG_Accept))
                    {
                        Log.information(ClassHelper.getClassNameFinalPortion(this) + ".acceptTicker for Class" + "(" + cfixFixMarketDataConsumerHolder + ")");
                    }

                    acceptMarketDataTickerV4(cfixFixMarketDataConsumerHolder, tickerStructsV4);

                    cfixMarketDataDispatcherInstrumentation.incMessagesDispatched();
                }
                catch (Exception ex)
                {
                    // nothing can be done -- continue with other consumers
                    Log.exception(ex);
                }
            }
        }

        acceptTickerIntArrayHolder.clear();

        classToProductsMap.getValuesForKey(classKey, acceptTickerIntArrayHolder);

        int count2 = acceptTickerIntArrayHolder.size();

        //if (Log.isDebugOn())
        //        Log.debug("CfixMDXMarketDataTickerDispatcherImpl -> acceptTickerIntArrayHolder size is : " + count2);

        if (count2 == 0 || (count2 == 1 && acceptTickerIntArrayHolder.getKey(0) == classKey))
        {
            //if (Log.isDebugOn())
            //    Log.debug("CfixMDXMarketDataTickerDispatcherImpl -> acceptTickerIntArrayHolder size is : " + count2);
            return;
        }

        // dispatch market data to users who have a product subscription -
        //if (Log.isDebugOn())
        //        Log.debug("CfixMDXMarketDataTickerDispatcherImpl -> Checking Product Subscriptions... !");


        for (int i = 0; i < tickerStructsV4.length; i++)
        {
            int productKey = tickerStructsV4[i].productKey;

            if (acceptTickerIntArrayHolder.containsKey(productKey))
            {
                //if (Log.isDebugOn())
                //    Log.debug("CfixMDXMarketDataTickerDispatcherImpl -> Product Subscriptions exists for product key : " + productKey);
                acceptTickerObjectArrayHolder.clear();

                productKeyConsumersMap.getConsumersForKey(productKey, acceptTickerObjectArrayHolder);
                if (!acceptTickerObjectArrayHolder.isEmpty())
                {
                    cfixMarketDataDispatcherInstrumentation.incMessagesProcessed();

                    int count3 = acceptTickerObjectArrayHolder.size();

                    //if (Log.isDebugOn())
                    //    Log.debug("CfixMDXMarketDataTickerDispatcherImpl -> Consumers exist for product sub! Consumer Count is : " + count3);

                    for (int j = 0; j < count3; j++)
                    {
                        try
                        {
                            CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder = (CfixMDXMarketDataConsumerHolder) acceptTickerObjectArrayHolder.getKey(j);

                            if (BitHelper.isBitMaskSet(debugFlags, CfixMDXMarketDataDispatcherIF.DEBUG_Accept))
                            {
                                Log.information(ClassHelper.getClassNameFinalPortion(this) + ".acceptTicker for Product" + getHandledMarketDataTypeName() + "(" + cfixFixMarketDataConsumerHolder + ")");
                            }

                            acceptMarketDataTickerV4(cfixFixMarketDataConsumerHolder, tickerStructsV4[i]);

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
    }

    private TickerStruct[] mapTickerStructsV4ToV1(TickerStructV4[] tickerStructsV4, CfixMDXMarketDataConsumerHolder cfixMDXMarketDataConsumerHolder)
    {
        String sessionName;
        if (cfixMDXMarketDataConsumerHolder.getSessionClassStruct() != null)
        {
            sessionName = cfixMDXMarketDataConsumerHolder.getSessionClassStruct().sessionName;
        }else
        {
            sessionName = cfixMDXMarketDataConsumerHolder.getSessionProductStruct().sessionName;
        }

        TickerStruct[] retTickerStruct = new TickerStruct[tickerStructsV4.length];

        for (int k = 0; k < tickerStructsV4.length; k++)
        {
            retTickerStruct[k] = new TickerStruct();
            retTickerStruct[k].exchangeSymbol = tickerStructsV4[k].exchange;
            retTickerStruct[k].sessionName = sessionName;

            retTickerStruct[k].productKeys = new ProductKeysStruct();
            retTickerStruct[k].productKeys.classKey = tickerStructsV4[k].classKey;
            retTickerStruct[k].productKeys.productKey = tickerStructsV4[k].productKey;
            retTickerStruct[k].productKeys.productType = tickerStructsV4[k].productType;
            //retTickerStruct[k].productKeys.reportingClass - we don't have this info - but it is not needed

            retTickerStruct[k].lastSalePrice = mapPriceV4ToV1(tickerStructsV4[k].tradePrice, tickerStructsV4[k].priceScale);
            retTickerStruct[k].lastSaleVolume = tickerStructsV4[k].tradeVolume;
            retTickerStruct[k].salePrefix = tickerStructsV4[k].salePrefix;
            retTickerStruct[k].salePostfix = tickerStructsV4[k].salePostfix;

        }
        return retTickerStruct;
    }

    private TickerStruct mapTickerStructV4ToV1(TickerStructV4 tickerStructsV4, CfixMDXMarketDataConsumerHolder cfixMDXMarketDataConsumerHolder)
    {
        String sessionName;
        if (cfixMDXMarketDataConsumerHolder.getSessionClassStruct() != null)
        {
            sessionName = cfixMDXMarketDataConsumerHolder.getSessionClassStruct().sessionName;
        }else
        {
            sessionName = cfixMDXMarketDataConsumerHolder.getSessionProductStruct().sessionName;
        }

        TickerStruct retTickerStruct            = new TickerStruct();
        retTickerStruct.exchangeSymbol          = tickerStructsV4.exchange;
        retTickerStruct.sessionName             = sessionName;

        retTickerStruct.productKeys             = new ProductKeysStruct();
        retTickerStruct.productKeys.classKey    = tickerStructsV4.classKey;
        retTickerStruct.productKeys.productKey  = tickerStructsV4.productKey;
        retTickerStruct.productKeys.productType = tickerStructsV4.productType;
        //retTickerStruct.productKeys.reportingClass - we don't have this info - but it is not needed

        retTickerStruct.lastSalePrice           = mapPriceV4ToV1(tickerStructsV4.tradePrice, tickerStructsV4.priceScale);
        retTickerStruct.lastSaleVolume          = tickerStructsV4.tradeVolume;
        retTickerStruct.salePrefix              = tickerStructsV4.salePrefix;
        retTickerStruct.salePostfix             = tickerStructsV4.salePostfix;


        return retTickerStruct;
    }

    private PriceStruct mapPriceV4ToV1(int priceV4, byte priceScale)
    {
        if (Log.isDebugOn())
        {
            Log.debug("CfixMDXMarketDataTickerDispatcherImpl -> mapPriceV4ToV1: The askPrice from the V4 struct is: " + priceV4);
            Log.debug("CfixMDXMarketDataTickerDispatcherImpl -> mapPriceV4ToV1: The priceScale from the V4 struct is: " + priceScale);
        }
        PriceStruct rtnPriceStruct = new PriceStruct(PriceTypes.VALUED, 0, 0);
        // When no Current Market - we see a negative price of -2147483648 - i.e. PriceConstants.NO_PRICE
        if(priceV4 == PriceConstants.NO_PRICE || priceV4 == 0)
                return rtnPriceStruct;
        /*
        Price - priceV4 - is an int - it is the price in pennies.
        - i.e. $2.10 shows up as 210.

        PriceStruct should look like the following:

        for an order with price -5.30, it looks like
        price.whole = -5
        price.fraction = -300000000

        for an order with price -0.05, it looks like
        price.whole = 0
        price.fraction = -50000000

        for an order with price -5.00, it looks like
        price.whole = -5
        price.fraction = 0

        */
        rtnPriceStruct.whole = priceV4/100;
        int tmpFraction = priceV4%100;
        // priceV4 == 0 checked above, priceV4 is either > 0 or it is < 0
        if (priceV4 > 0)
        {
            // 530 % 100 = 30 - we want 300,000,000 - multiply by - 10,000,000
            // PriceScale in V4 structs - 1,000,000,000 - i.e. two more zeroes - which is what we get if we multiply by 100 -
            rtnPriceStruct.fraction = tmpFraction * 10000000;
        } else // priceV4 is less than 0
        {
            rtnPriceStruct.fraction = tmpFraction * (-10000000);
        }
        return rtnPriceStruct;
    }


    protected void acceptMarketDataTicker(CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, TickerStruct struct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        cfixFixMarketDataConsumerHolder.acceptMarketDataTicker(struct);
    }

    protected void acceptMarketDataTicker(CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, TickerStruct[] structs) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        cfixFixMarketDataConsumerHolder.acceptMarketDataTicker(structs);
    }

    protected void acceptMarketDataTickerV4(CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, TickerStructV4 struct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        cfixFixMarketDataConsumerHolder.acceptMarketDataTicker(struct);
    }

    protected void acceptMarketDataTickerV4(CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, TickerStructV4[] structs) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        cfixFixMarketDataConsumerHolder.acceptMarketDataTicker(structs);
    }

    protected int getClassKey(TickerStruct struct)
    {
        return struct.productKeys.classKey;
    }

    protected int getProductKey(TickerStruct struct)
    {
        return struct.productKeys.productKey;
    }

    protected int getClassKey(TickerStructV4 struct)
    {
        return struct.classKey;
    }

    protected int getProductKey(TickerStructV4 struct)
    {
        return struct.productKey;
    }

    public void queueInstrumentationInitiated(){};


}
