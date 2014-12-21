package com.cboe.cfix.cas.marketData;

/**
 * CfixMDXMarketDataCurrentMarketDispatcherImpl.java
 *
 * @author Vivek Beniwal
 *
 */

/*
 *  This object registers for and receives all CurrentMarket market data traffic for a specific session, and then forwards it to all listeners<br>
 *
 *  It contains the subscription maps for all listeners interested in receiving CurrentMarket subscriptions for this session<br>
 *
 */

import com.cboe.application.test.ReflectiveStructTester;
import com.cboe.cfix.cas.shared.CfixServicesHelper;
import com.cboe.client.util.BitHelper;
import com.cboe.client.util.ClassHelper;
import com.cboe.client.util.MutableInteger;
import com.cboe.client.util.collections.IntArrayHolder;
import com.cboe.client.util.collections.IntIntMultipleValuesMap;
import com.cboe.client.util.collections.ObjectArrayHolder;
import com.cboe.domain.util.CurrentMarketContainerV4Impl;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiCallbackV4.CMICurrentMarketConsumerPOA;
import com.cboe.idl.cmiConstants.QueueActions;
import com.cboe.idl.cmiMarketData.CurrentMarketStructV4;
import com.cboe.idl.cmiMarketData.MarketVolumeStructV4;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.cfix.*;
import com.cboe.interfaces.domain.CurrentMarketContainerV4;
import com.cboe.util.ExceptionBuilder;

import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Iterator;

public final class CfixMDXMarketDataCurrentMarketDispatcherImpl extends CMICurrentMarketConsumerPOA implements CfixMDXMarketDataDispatcherIF
{

    protected String                    name;
    protected int                       classKey;
    protected int                       debugFlags;
    protected CfixKeyToConsumersMap     productKeyConsumersMap;
    protected CfixKeyToConsumersMap     classKeyConsumersMap;
    protected IntIntMultipleValuesMap   classToProductsMap              = IntIntMultipleValuesMap.synchronizedMap();
    protected IntArrayHolder            acceptCMIntArrayHolder          = new IntArrayHolder();
    protected ObjectArrayHolder         acceptCMObjectArrayHolder       = new ObjectArrayHolder();

    protected static final String       W_MAIN_Str                      = "W_MAIN";
    protected static final String       Underlying_Str                  = "Underlying";
    protected CfixCasLogin              myCfixCasLogin;
    protected CfixCasExternalLogin      myCfixCasExternalLogin;
    protected boolean                   isUnderlyingSub                 = false;

    public static final String          MARKET_DATA_TYPE_NAME           = "CurrentMarket";

    // todo - evaluate use of ConcurrentHashMap, and evaluate how best to determine initial size so that we do not rehash.
    // Maybe, what we can do is look up the ProductCache - and see how many products are associated for each class key.

    protected HashMap<Integer, CurrentMarketStructV4> cachedCMV4        = new HashMap<Integer, CurrentMarketStructV4> (800);
    protected boolean                   usersSubscribed                 = false;
    protected boolean                   recapSubscribed                 = false;

    protected CfixMarketDataDispatcherInstrumentationImpl cfixMarketDataDispatcherInstrumentation = new CfixMarketDataDispatcherInstrumentationImpl();

    public CfixMDXMarketDataCurrentMarketDispatcherImpl(String name, int classKey)
    {
        if(Log.isDebugOn())
        {
            StringBuffer strBuf = new StringBuffer("Initialized CfixMDXMarketDataCurrentMarketDispatcherImpl for Dispatcher Name : ");
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
        return CfixMDXMarketDataDispatcherIF.MarketDataType_CurrentMarket;
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
                                                     CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder,
                                                     CfixKeyToConsumersMap consumerMap, IntIntMultipleValuesMap classMap, boolean isUserSub)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (isUserSub)
            return internalSubscribeForUser(classKey, productKey, sessionName, cfixFixMarketDataConsumerHolder, consumerMap, classMap);
        else
            return internalSubscribeForRecap(classKey, productKey, sessionName, cfixFixMarketDataConsumerHolder, consumerMap, classMap);
    }

    protected boolean internalSubscribeForUser(int classKey, int productKey, String sessionName,
                                                     CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder,
                                                     CfixKeyToConsumersMap consumerMap, IntIntMultipleValuesMap classMap)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        // Note - productKey = classKey, for a subscribe by class
        MutableInteger mutableInteger = MutableInteger.threadLocalMutableInteger.getMutableInteger();
        int num1 = consumerMap.addConsumerToKey(productKey, cfixFixMarketDataConsumerHolder);
        classMap.putKeyValue(classKey, productKey, mutableInteger);

        if(Log.isDebugOn())
        {
            Log.debug("In CfixMDXMarketDataCurrentMarketDispatcherImpl:internalSubscribe: num1 is: " + num1);
            Log.debug("In CfixMDXMarketDataCurrentMarketDispatcherImpl:internalSubscribe: mutableInteger.integer is: " + mutableInteger.integer);
        }


        if (BitHelper.isBitMaskSet(debugFlags, CfixMDXMarketDataDispatcherIF.DEBUG_Subscribe))
        {
            Log.information(Thread.currentThread().getName() + " " + ClassHelper.getClassNameFinalPortion(this) + ".subscribing "
                    + (classKey == productKey ? "ByClass" : "ByProduct") + " classKey(" + classKey + ") productKey(" + productKey
                    + ") subscriptionType(" + MARKET_DATA_TYPE_NAME  + ") num1(" + num1 + ") num2(" + mutableInteger.integer
                    + ") consumerMap(" + consumerMap + ").size(" + consumerMap.size() + ") classMap(" + classMap
                    + ").size(" + classMap.size() + ") " + cfixFixMarketDataConsumerHolder);
        }

        // this is the first subscription request for this class - forward this subscription to the MDX
        // In case we already had a subscription because of recap - we do not want to subscribe to MDX again
        if (num1 == 1 && mutableInteger.integer == 1 && !recapSubscribed && !usersSubscribed)
        {
            if (BitHelper.isBitMaskSet(debugFlags, CfixMDXMarketDataDispatcherIF.DEBUG_Subscribe))
            {
                Log.information(Thread.currentThread().getName() + " " + ClassHelper.getClassNameFinalPortion(this)
                        + ".subcribeCurrentMarket for sessionName(" + sessionName + ") classKey(" + classKey + ")");
            }
            if (this.isUnderlyingSub){
                this.myCfixCasExternalLogin.subscribeExternalUserCurrentMarket(classKey, this._this(), QueueActions.OVERLAY_LAST);
            }else {
                this.myCfixCasLogin.subscribeCurrentMarket(classKey, this._this(), QueueActions.OVERLAY_LAST);
            }
            usersSubscribed = true;
            if(Log.isDebugOn())
            {
                Log.debug("In CfixMDXMarketDataCurrentMarketDispatcherImpl:internalSubscribeForUser: Not subscribed for Recap: Subscribed User for Current Market on the MDX interface.");
            }

            return true;
        } else  // return the cached data from the cachedCMV4, if this was a subscribe by class, then the productKey = classKey, else it is subscribe by product
        {
            usersSubscribed = true; 
            if(Log.isDebugOn())
            {
                Log.debug("In CfixMDXMarketDataCurrentMarketDispatcherImpl:internalSubscribeForUser: Already subscribed to MDX for this class. Servicing initial get from Cached CM.");
            }
            try {
                if (classKey == productKey)
                    this.dispatchInitialCurrentMarketForClass(cfixFixMarketDataConsumerHolder);
                else
                    this.dispatchInitialCurrentMarketForProduct(cfixFixMarketDataConsumerHolder, productKey);
            } catch (Exception ex)
            {
                // nothing can be done --
                Log.exception(ex);
            }
            return false;
        }
    }

    protected boolean internalSubscribeForRecap(int classKey, int productKey, String sessionName,
                                                     CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder,
                                                     CfixKeyToConsumersMap consumerMap, IntIntMultipleValuesMap classMap)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (!this.usersSubscribed && !this.recapSubscribed)
        {
            if (this.isUnderlyingSub){
                this.myCfixCasExternalLogin.subscribeExternalUserCurrentMarket(classKey, this._this(), QueueActions.OVERLAY_LAST);
            }else {
                this.myCfixCasLogin.subscribeCurrentMarket(classKey, this._this(), QueueActions.OVERLAY_LAST);
            }
            recapSubscribed = true;
            if(Log.isDebugOn())
            {
                Log.debug("In CfixMDXMarketDataCurrentMarketDispatcherImpl:internalSubscribeForRecap: MDX Enabled CFIX: Subscribed for Current Market on the MDX interface.");
            }
            return true;
        }
        return false;
    }


    protected synchronized boolean internalUnsubscribe(int classKey, int productKey, String sessionName,
                                                       CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder,
                                                       CfixKeyToConsumersMap consumerMap, IntIntMultipleValuesMap classMap, boolean isUserSub)
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
                        + ".unsubscribeCurrentMarket for sessionName(" + sessionName + ") classKey(" + classKey + ")");
            }

            this.usersSubscribed = false;

            //if (!this.recapSubscribed)
            //    this.myCfixCasLogin.unsubscribeCurrentMarket(classKey, this._this());

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
                    // this is at the time of session cleanup - so we are cleaning up user sessions
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
                    // this is at the time of session cleanup - so we are cleaning up user sessions
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
        if(Log.isDebugOn())
            Log.debug("CfixMDXMarketDataCurrentMarketDispatcherImpl:subscribeByProduct:for productKey:: " + sessionProductStruct.productStruct.productKeys.productKey + " for Class : " + sessionProductStruct.productStruct.productKeys.classKey);
        internalSubscribe(sessionProductStruct.productStruct.productKeys.classKey,
                          sessionProductStruct.productStruct.productKeys.productKey,
                          sessionProductStruct.sessionName,
                          cfixFixMarketDataConsumerHolder,
                          productKeyConsumersMap,
                          classToProductsMap,
                          isUserSub);
    }

    public void unsubscribeByProduct(CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, boolean isUserSub) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        SessionProductStruct sessionProductStruct = cfixFixMarketDataConsumerHolder.getSessionProductStruct();
        if(Log.isDebugOn())
            Log.debug("CfixMDXMarketDataCurrentMarketDispatcherImpl:unsubscribeByProduct:for productKey:: " + sessionProductStruct.productStruct.productKeys.productKey + " for Class : " + sessionProductStruct.productStruct.productKeys.classKey);
        internalUnsubscribe(sessionProductStruct.productStruct.productKeys.classKey,
                            sessionProductStruct.productStruct.productKeys.productKey,
                            sessionProductStruct.sessionName,
                            cfixFixMarketDataConsumerHolder,
                            productKeyConsumersMap,
                            classToProductsMap,
                            isUserSub);
    }

    public void subscribeByClass(CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, boolean isUserSub) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        SessionClassStruct sessionClassStruct = cfixFixMarketDataConsumerHolder.getSessionClassStruct();
        if(Log.isDebugOn())
            Log.debug("CfixMDXMarketDataCurrentMarketDispatcherImpl:subscribeByClass:for classKey:: " + sessionClassStruct.classStruct.classKey);
        internalSubscribe(sessionClassStruct.classStruct.classKey,
                          sessionClassStruct.classStruct.classKey,
                          sessionClassStruct.sessionName,
                          cfixFixMarketDataConsumerHolder,
                          classKeyConsumersMap,
                          classToProductsMap,
                          isUserSub);
    }

    public void unsubscribeByClass(CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, boolean isUserSub) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        SessionClassStruct sessionClassStruct = cfixFixMarketDataConsumerHolder.getSessionClassStruct();
        if(Log.isDebugOn())
            Log.debug("CfixMDXMarketDataCurrentMarketDispatcherImpl:unsubscribeByClass:for classKey:: " + sessionClassStruct.classStruct.classKey);
        internalUnsubscribe(sessionClassStruct.classStruct.classKey,
                            sessionClassStruct.classStruct.classKey,
                            sessionClassStruct.sessionName,
                            cfixFixMarketDataConsumerHolder,
                            classKeyConsumersMap,
                            classToProductsMap,
                            isUserSub);
    }

    public void acceptCurrentMarket(CurrentMarketStructV4[] bestMarket, CurrentMarketStructV4[] bestPublicMarket, int messageSequence, int queueDepth, short queueAction)
    {
        if(Log.isDebugOn())
        {
            Log.debug("CfixMDXMarketDataCurrentMarketDispatcherImpl -> CFIX accepting CurrentMarketV4 market data from MDX!!");
        }

        this.acceptCurrentMarketV4(bestMarket,  bestPublicMarket,  messageSequence,  queueDepth, queueAction);

    }

    private synchronized void acceptCurrentMarketV4(CurrentMarketStructV4[] bestMarket, CurrentMarketStructV4[] bestPublicMarket, int messageSequence, int queueDepth, short queueAction)
    {
        if (this.usersSubscribed)
            this.acceptCurrentMarketV4ForUser(bestMarket, bestPublicMarket, messageSequence, queueDepth, queueAction);
        else
            this.acceptCurrentMarketV4ForRecap(bestMarket, bestPublicMarket, messageSequence, queueDepth, queueAction);
    }

    private void acceptCurrentMarketV4ForUser(CurrentMarketStructV4[] bestMarket, CurrentMarketStructV4[] bestPublicMarket, int messageSequence, int queueDepth, short queueAction)
    {
        cfixMarketDataDispatcherInstrumentation.incMessagesConsumed();

        acceptCMObjectArrayHolder.clear();

        // get the users who have a class subscription - the call will populate the acceptCMObjectArrayHolder with the consumers.
        classKeyConsumersMap.getConsumersForKey(classKey, acceptCMObjectArrayHolder);

        //Log.information(Thread.currentThread().getName() + " " + ClassHelper.getClassNameFinalPortion(this) + " XXX 3 classKey: (" + classKey + ") holder.size(" + acceptCMObjectArrayHolder.size() + ")");

        if (!acceptCMObjectArrayHolder.isEmpty())
        {
            cfixMarketDataDispatcherInstrumentation.incMessagesProcessed();

            int count = acceptCMObjectArrayHolder.size();

            for (int i = 0; i < count; i++)
            {
                try
                {
                    CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder = (CfixMDXMarketDataConsumerHolder) acceptCMObjectArrayHolder.getKey(i);

                    CurrentMarketContainerV4 structs;
                    // We cannot assume that either of the structs are non-null
                    // todo VivekB: check which struct is not going to be null - and which can. Make this faster
                    if (isNullArray(bestPublicMarket) && isNullArray(bestMarket))
                    {
                        Log.alarm("CfixMDXMarketDataCurrentMarketDispatcherImpl:acceptCurrentMarketV4ForUser -> both bestMarket and bestPublicMarket structs are null!!");
                        break;
                    } else if(isNullArray(bestPublicMarket))
                    {
                        // update cachedCMV4 with latest market data updates
                        this.updateCurrentMarketCacheV4(bestMarket);
                        cfixFixMarketDataConsumerHolder.acceptMarketDataCurrentMarket(bestMarket);
                        cfixMarketDataDispatcherInstrumentation.incMessagesDispatched();
                    } else if(isNullArray(bestMarket))
                    {
                        // update cachedCMV4 with latest market data updates
                        this.updateCurrentMarketCacheV4(bestPublicMarket);
                        cfixFixMarketDataConsumerHolder.acceptMarketDataCurrentMarket(bestPublicMarket);
                        cfixMarketDataDispatcherInstrumentation.incMessagesDispatched();
                    } else
                    {
                        // repackageCurrentMarketContainer will update cachedCMV4 with latest market data updates
                        structs = new CurrentMarketContainerV4Impl(bestMarket, bestPublicMarket, messageSequence, queueDepth, queueAction);
                        repackageCurrentMarketContainer(cfixFixMarketDataConsumerHolder, structs);
                        cfixMarketDataDispatcherInstrumentation.incMessagesDispatched();
                    }

                    if (BitHelper.isBitMaskSet(debugFlags, CfixMDXMarketDataDispatcherIF.DEBUG_Accept))
                    {
                        Log.information(ClassHelper.getClassNameFinalPortion(this) + ".acceptCurrentMarket" + "(" + cfixFixMarketDataConsumerHolder + ")");
                    }

                }
                catch (Exception ex)
                {
                    // nothing can be done -- continue with other consumers
                    Log.exception(ex);
                }
            }
        }

        acceptCMIntArrayHolder.clear();

        classToProductsMap.getValuesForKey(classKey, acceptCMIntArrayHolder);

        int count = acceptCMIntArrayHolder.size();

        if (count == 0 || (count == 1 && acceptCMIntArrayHolder.getKey(0) == classKey))
        {
            return;
        }

        int productKey;

        // dispatch market data to users who have a product subscription - and update cache for each of the products
        for (int i = 0; i < bestMarket.length; i++)
        {
            productKey = bestMarket[i].productKey;

            if (acceptCMIntArrayHolder.containsKey(productKey))
            {
                acceptCMObjectArrayHolder.clear();

                productKeyConsumersMap.getConsumersForKey(productKey, acceptCMObjectArrayHolder);
                if (!acceptCMObjectArrayHolder.isEmpty())
                {
                    cfixMarketDataDispatcherInstrumentation.incMessagesProcessed();

                    count = acceptCMObjectArrayHolder.size();

                    for (int j = 0; j < count; j++)
                    {
                        try
                        {
                            CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder = (CfixMDXMarketDataConsumerHolder) acceptCMObjectArrayHolder.getKey(j);

                            CurrentMarketContainerV4 struct;

                            if (isNullArray(bestPublicMarket) && isNullArray(bestMarket))
                            {
                                Log.alarm("CfixMDXMarketDataCurrentMarketDispatcherImpl:acceptCurrentMarketV4ForUser -> both bestMarket and bestPublicMarket structs are null!!");
                                break;
                            } else if(isNullArray(bestPublicMarket))
                            {
                                cfixFixMarketDataConsumerHolder.acceptMarketDataCurrentMarket(bestMarket[i]);
                                cfixMarketDataDispatcherInstrumentation.incMessagesDispatched();
                            } else if(isNullArray(bestMarket))
                            {
                                cfixFixMarketDataConsumerHolder.acceptMarketDataCurrentMarket(bestPublicMarket[i]);
                                cfixMarketDataDispatcherInstrumentation.incMessagesDispatched();
                            } else if (bestPublicMarket.length < i)
                            {
                                cfixFixMarketDataConsumerHolder.acceptMarketDataCurrentMarket(bestMarket[i]);
                                cfixMarketDataDispatcherInstrumentation.incMessagesDispatched();
                            } else
                            {
                                CurrentMarketStructV4[] bmArray = new CurrentMarketStructV4[1];
                                bmArray[0] = bestMarket[i];
                                CurrentMarketStructV4[] bpmArray = new CurrentMarketStructV4[1];
                                bpmArray[0] = bestPublicMarket[i];
                                struct = new CurrentMarketContainerV4Impl(bmArray, bpmArray);
                                repackageCurrentMarketContainer(cfixFixMarketDataConsumerHolder, struct);
                                cfixMarketDataDispatcherInstrumentation.incMessagesDispatched();
                            }

                            if (BitHelper.isBitMaskSet(debugFlags, CfixMDXMarketDataDispatcherIF.DEBUG_Accept))
                            {
                                Log.information(ClassHelper.getClassNameFinalPortion(this) + ".acceptMarketData" + getHandledMarketDataTypeName() + "(" + cfixFixMarketDataConsumerHolder + ")");
                            }

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

    private void acceptCurrentMarketV4ForRecap(CurrentMarketStructV4[] bestMarket, CurrentMarketStructV4[] bestPublicMarket, int messageSequence, int queueDepth, short queueAction)
    {
        if (isNullArray(bestPublicMarket) && isNullArray(bestMarket))
        {
            Log.alarm("CfixMDXMarketDataCurrentMarketDispatcherImpl:acceptCurrentMarketV4ForRecap -> both bestMarket and bestPublicMarket structs are null!!");
        } else if(isNullArray(bestPublicMarket))
        {
            // update cachedCMV4 with latest market data updates
            this.updateCurrentMarketCacheV4(bestMarket);
        } else if(isNullArray(bestMarket))
        {
            // update cachedCMV4 with latest market data updates
            this.updateCurrentMarketCacheV4(bestPublicMarket);
        } else
        {
            // repackageCurrentMarketContainer will update cachedCMV4 with latest market data updates
            CurrentMarketContainerV4 structs = new CurrentMarketContainerV4Impl(bestMarket, bestPublicMarket, messageSequence, queueDepth, queueAction);
            repackageCurrentMarketContainer(structs);
        }
    }

    protected void repackageCurrentMarketContainer(CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, CurrentMarketContainerV4 containerCMV4)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        CurrentMarketStructV4[]     bestMarket                   = containerCMV4.getBestMarkets();
        CurrentMarketStructV4[]     bestPublicMarketsAtTop       = containerCMV4.getBestPublicMarkets();

        CurrentMarketStructV4[]     newBestMarketArray           = new CurrentMarketStructV4[bestMarket.length];
        int                         bestPublicMarketsAtTopLength = bestPublicMarketsAtTop.length;
        CurrentMarketStructV4       newBestMarket;
        CurrentMarketStructV4       oldBestMarket;
        CurrentMarketStructV4       oldBestPublicMarketsAtTop;
        int                         j;
        int                         k;

        for (int i = 0; i < newBestMarketArray.length; i++)
        {
            oldBestMarket = bestMarket[i];

            for (j = 0; j < bestPublicMarketsAtTopLength; j++)
            {
                if (oldBestMarket.productKey == bestPublicMarketsAtTop[j].productKey)
                {
                    break;
                }
            }

            if (j == bestPublicMarketsAtTop.length) // does not need to be modified
            {
                newBestMarketArray[i] = oldBestMarket;

                continue;
            }

            newBestMarket                 = new CurrentMarketStructV4();
            newBestMarketArray[i]         = newBestMarket;
            oldBestPublicMarketsAtTop     = bestPublicMarketsAtTop[j];

            newBestMarket.classKey          = oldBestMarket.classKey;
            newBestMarket.productKey        = oldBestMarket.productKey;
            newBestMarket.productType       = oldBestMarket.productType;
            newBestMarket.exchange          = oldBestMarket.exchange;
            newBestMarket.sentTime          = oldBestMarket.sentTime;
            newBestMarket.currentMarketType = oldBestMarket.currentMarketType;
            newBestMarket.bidPrice          = oldBestMarket.bidPrice;
            newBestMarket.askPrice          = oldBestMarket.askPrice;
            newBestMarket.bidTickDirection  = oldBestMarket.bidTickDirection;
            newBestMarket.marketIndicator   = oldBestMarket.marketIndicator;
            newBestMarket.productState      = oldBestMarket.productState;
            newBestMarket.priceScale        = oldBestMarket.priceScale;

            j = 0;
            newBestMarket.bidSizeSequence = new MarketVolumeStructV4[oldBestMarket.bidSizeSequence.length + oldBestPublicMarketsAtTop.bidSizeSequence.length];
            for (k = 0; k < oldBestMarket.bidSizeSequence.length; k++, j++)
            {
                newBestMarket.bidSizeSequence[j] = oldBestMarket.bidSizeSequence[k];
            }
            for (k = 0; k < oldBestPublicMarketsAtTop.bidSizeSequence.length; k++, j++)
            {
                newBestMarket.bidSizeSequence[j] = oldBestPublicMarketsAtTop.bidSizeSequence[k];
            }

            j = 0;
            newBestMarket.askSizeSequence = new MarketVolumeStructV4[oldBestMarket.askSizeSequence.length + oldBestPublicMarketsAtTop.askSizeSequence.length];
            for (k = 0; k < oldBestMarket.askSizeSequence.length; k++, j++)
            {
                newBestMarket.askSizeSequence[j] = oldBestMarket.askSizeSequence[k];
            }
            for (k = 0; k < oldBestPublicMarketsAtTop.askSizeSequence.length; k++, j++)
            {
                newBestMarket.askSizeSequence[j] = oldBestPublicMarketsAtTop.askSizeSequence[k];
            }
        }
        // update cachedCMV4 with latest market data updates
        this.updateCurrentMarketCacheV4(newBestMarketArray);
        cfixFixMarketDataConsumerHolder.acceptMarketDataCurrentMarket(newBestMarketArray);
    }

    protected void repackageCurrentMarketContainer(CurrentMarketContainerV4 containerCMV4)
    {
        CurrentMarketStructV4[]     bestMarket                   = containerCMV4.getBestMarkets();
        CurrentMarketStructV4[]     bestPublicMarketsAtTop       = containerCMV4.getBestPublicMarkets();

        CurrentMarketStructV4[]     newBestMarketArray           = new CurrentMarketStructV4[bestMarket.length];
        int                         bestPublicMarketsAtTopLength = bestPublicMarketsAtTop.length;
        CurrentMarketStructV4       newBestMarket;
        CurrentMarketStructV4       oldBestMarket;
        CurrentMarketStructV4       oldBestPublicMarketsAtTop;
        int                         j;
        int                         k;

        for (int i = 0; i < newBestMarketArray.length; i++)
        {
            oldBestMarket = bestMarket[i];

            for (j = 0; j < bestPublicMarketsAtTopLength; j++)
            {
                if (oldBestMarket.productKey == bestPublicMarketsAtTop[j].productKey)
                {
                    break;
                }
            }

            if (j == bestPublicMarketsAtTop.length) // does not need to be modified
            {
                newBestMarketArray[i] = oldBestMarket;

                continue;
            }

            newBestMarket                 = new CurrentMarketStructV4();
            newBestMarketArray[i]         = newBestMarket;
            oldBestPublicMarketsAtTop     = bestPublicMarketsAtTop[j];

            newBestMarket.classKey          = oldBestMarket.classKey;
            newBestMarket.productKey        = oldBestMarket.productKey;
            newBestMarket.productType       = oldBestMarket.productType;
            newBestMarket.exchange          = oldBestMarket.exchange;
            newBestMarket.sentTime          = oldBestMarket.sentTime;
            newBestMarket.currentMarketType = oldBestMarket.currentMarketType;
            newBestMarket.bidPrice          = oldBestMarket.bidPrice;
            newBestMarket.askPrice          = oldBestMarket.askPrice;
            newBestMarket.bidTickDirection  = oldBestMarket.bidTickDirection;
            newBestMarket.marketIndicator   = oldBestMarket.marketIndicator;
            newBestMarket.productState      = oldBestMarket.productState;
            newBestMarket.priceScale        = oldBestMarket.priceScale;

            j = 0;
            newBestMarket.bidSizeSequence = new MarketVolumeStructV4[oldBestMarket.bidSizeSequence.length + oldBestPublicMarketsAtTop.bidSizeSequence.length];
            for (k = 0; k < oldBestMarket.bidSizeSequence.length; k++, j++)
            {
                newBestMarket.bidSizeSequence[j] = oldBestMarket.bidSizeSequence[k];
            }
            for (k = 0; k < oldBestPublicMarketsAtTop.bidSizeSequence.length; k++, j++)
            {
                newBestMarket.bidSizeSequence[j] = oldBestPublicMarketsAtTop.bidSizeSequence[k];
            }

            j = 0;
            newBestMarket.askSizeSequence = new MarketVolumeStructV4[oldBestMarket.askSizeSequence.length + oldBestPublicMarketsAtTop.askSizeSequence.length];
            for (k = 0; k < oldBestMarket.askSizeSequence.length; k++, j++)
            {
                newBestMarket.askSizeSequence[j] = oldBestMarket.askSizeSequence[k];
            }
            for (k = 0; k < oldBestPublicMarketsAtTop.askSizeSequence.length; k++, j++)
            {
                newBestMarket.askSizeSequence[j] = oldBestPublicMarketsAtTop.askSizeSequence[k];
            }
        }
        // update cachedCMV4 with latest market data updates
        this.updateCurrentMarketCacheV4(newBestMarketArray);
    }

    private void updateCurrentMarketCacheV4(CurrentMarketStructV4[] cmArray)
    {
        for (int i = 0; i < cmArray.length; i++)
        {
            Integer productKey = cmArray[i].productKey;
            this.cachedCMV4.put(productKey, cmArray[i]);
        }
    }

    private synchronized void dispatchInitialCurrentMarketForClass(CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if(Log.isDebugOn())
        {
            Log.debug("CfixMDXMarketDataCurrentMarketDispatcherImpl -> dispatchInitialCurrentMarketForClass!! Size of cached map is: " + this.cachedCMV4.size());
        }

        if (0 == this.cachedCMV4.size())
        {
            return;
        }
        CurrentMarketStructV4[] cmArray = new CurrentMarketStructV4[this.cachedCMV4.size()];
        Iterator it = this.cachedCMV4.values().iterator();
        int count = 0;
        while (it.hasNext())
        {
            cmArray[count] = (CurrentMarketStructV4) it.next();
            count++;
        }
        cfixFixMarketDataConsumerHolder.acceptMarketDataCurrentMarket(cmArray);
    }

    private synchronized void dispatchInitialCurrentMarketForProduct(CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        Integer key = productKey;
        CurrentMarketStructV4 cm = this.cachedCMV4.get(key);

        if(Log.isDebugOn())
        {
            Log.debug("CfixMDXMarketDataCurrentMarketDispatcherImpl -> dispatchInitialCurrentMarketForProduct!! Size of cached map is: " + this.cachedCMV4.size() + ". Dispatching productKey: " + productKey);
        }

        if (null != cm)
            cfixFixMarketDataConsumerHolder.acceptMarketDataCurrentMarket(cm);
    }

    protected synchronized CurrentMarketStructV4[] getCachedCurrentMarket()
    {
        CurrentMarketStructV4[] cmArray = new CurrentMarketStructV4[this.cachedCMV4.size()];
        Iterator it = this.cachedCMV4.values().iterator();
        int count = 0;
        while (it.hasNext())
        {
            cmArray[count] = (CurrentMarketStructV4) it.next();
            count++;
        }
        return cmArray;
    }

    protected synchronized CurrentMarketStructV4 getCachedCurrentMarketForProduct(int productKey)
    {
        return this.cachedCMV4.get(productKey);
    }

    public void queueInstrumentationInitiated(){};

    private static boolean isNullArray(Object[] objArray)
    {
        if (objArray == null || objArray.length == 0)
            return true;
        else
            return false;
    }

}
