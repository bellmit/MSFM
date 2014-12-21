package com.cboe.cfix.cas.marketData;

/**
 * CfixMDXMarketDataExpectedOpeningPriceDispatcherImpl.java
 *
 * @author Vivek Beniwal
 *
 */

/*
 *  This object registers for and receives all ExpectedOpeningPrice market data traffic for a specific session, and then forwards it to all listeners<br>
 *
 *  It contains the subscription maps for all listeners interested in receiving ExpectedOpeningPrice subscriptions for this session<br>
 *
 */

import com.cboe.interfaces.cfix.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiMarketData.ExpectedOpeningPriceStruct;
import com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumerPOA;
import com.cboe.idl.cmiConstants.QueueActions;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.client.util.collections.IntIntMultipleValuesMap;
import com.cboe.client.util.collections.IntArrayHolder;
import com.cboe.client.util.collections.ObjectArrayHolder;
import com.cboe.client.util.MutableInteger;
import com.cboe.client.util.BitHelper;
import com.cboe.client.util.ClassHelper;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.cfix.cas.shared.CfixServicesHelper;
import com.cboe.util.ExceptionBuilder;
import com.cboe.application.test.ReflectiveStructTester;

import java.io.StringWriter;
import java.lang.reflect.Array;

public final class CfixMDXMarketDataExpectedOpeningPriceDispatcherImpl extends CMIExpectedOpeningPriceConsumerPOA implements CfixMDXMarketDataDispatcherIF
{
    protected String                    name;
    protected int                       classKey;
    protected int                       debugFlags;
    protected CfixKeyToConsumersMap     productKeyConsumersMap;
    protected CfixKeyToConsumersMap     classKeyConsumersMap;
    protected IntIntMultipleValuesMap   classToProductsMap              = IntIntMultipleValuesMap.synchronizedMap();
    protected IntArrayHolder            acceptEOPIntArrayHolder         = new IntArrayHolder();
    protected ObjectArrayHolder         acceptEOPObjectArrayHolder      = new ObjectArrayHolder();

    protected static final String       W_MAIN_Str                      = "W_MAIN";
    protected static final String       Underlying_Str                  = "Underlying";
    protected CfixCasLogin              myCfixCasLogin;
    protected CfixCasExternalLogin      myCfixCasExternalLogin;
    protected boolean                   isUnderlyingSub                 = false;

    public static final String MARKET_DATA_TYPE_NAME = "ExpectedOpeningPrice";

    protected CfixMarketDataDispatcherInstrumentationImpl cfixMarketDataDispatcherInstrumentation = new CfixMarketDataDispatcherInstrumentationImpl();

    public CfixMDXMarketDataExpectedOpeningPriceDispatcherImpl(String name, int classKey)
    {
        if(Log.isDebugOn())
        {
            StringBuffer strBuf = new StringBuffer("Initialized CfixMDXMarketDataExpectedOpeningPriceDispatcherImpl for Dispatcher Name : ");
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
        return CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice;
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
            Log.information(new StringBuilder().append(Thread.currentThread().getName()).append(" ").append(ClassHelper.getClassNameFinalPortion(this)).
                    append(".subscribing ").append(classKey == productKey ? "ByClass" : "ByProduct").append(" classKey(").append(classKey).
                    append(") productKey(").append(productKey).append(") subscriptionType(").append(MARKET_DATA_TYPE_NAME).append(") num1(").
                    append(num1).append(") num2(").append(mutableInteger.integer).append(") consumerMap(").append(consumerMap).append(").size(").
                    append(consumerMap.size()).append(") classMap(").append(classMap).append(").size(").append(classMap.size()).append(") ").append(cfixFixMarketDataConsumerHolder).toString());
        }

        if (num1 == 1 && mutableInteger.integer == 1)
        {
            if (BitHelper.isBitMaskSet(debugFlags, CfixMDXMarketDataDispatcherIF.DEBUG_Subscribe))
            {
                Log.information(new StringBuilder().append(Thread.currentThread().getName()).append(" ").append(ClassHelper.getClassNameFinalPortion(this)).append(".subcribeEOP for sessionName(").append(sessionName).append(") classKey(").append(classKey).append(")").toString());
            }

            if(Log.isDebugOn())
            {
                Log.debug("In CfixMDXMarketDataExpectedOpeningPriceDispatcherImpl:internalSubscribe: MDX Enabled CFIX: Subscribing for EOP.");
            }
            if (this.isUnderlyingSub)
            {
                this.myCfixCasExternalLogin.subscribeExternalUserExpectedOpeningPrice(sessionName, classKey, this._this());
            } else
            {
                this.myCfixCasLogin.subscribeExpectedOpeningPrice(sessionName, classKey, this._this());
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
            Log.information(new StringBuilder().append(Thread.currentThread().getName()).append(" ").append(ClassHelper.getClassNameFinalPortion(this)).
                    append(".unsubscribing ").append(classKey == productKey ? "ByClass" : "ByProduct").append(" classKey(").append(classKey).
                    append(") productKey(").append(productKey).append(") subscriptionType(").append(MARKET_DATA_TYPE_NAME).append(") num1(").append(num1).
                    append(") num2(").append(mutableInteger.integer).append(") consumerMap(").append(consumerMap).append(").size(").append(consumerMap.size()).
                    append(") classMap(").append(classMap).append(").size(").append(classMap.size()).append(") ").append(cfixFixMarketDataConsumerHolder).toString());
        }

        if (num1 == 0 && mutableInteger.integer == 0)
        {
            if (BitHelper.isBitMaskSet(debugFlags, CfixMDXMarketDataDispatcherIF.DEBUG_Subscribe))
            {
                Log.information(new StringBuilder().append(Thread.currentThread().getName()).append(" ").append(ClassHelper.getClassNameFinalPortion(this)).
                        append(".unsubscribeEOP for sessionName(").append(sessionName).append(") classKey(").append(classKey).append(")").toString());
            }

            if(Log.isDebugOn())
            {
                Log.debug("In CfixMDXMarketDataExpectedOpeningPriceDispatcherImpl:internalUnsubscribe: MDX Enabled CFIX: Unsubscribing for EOP.");
            }
            if (this.isUnderlyingSub)
            {
                this.myCfixCasExternalLogin.unsubscribeExternalUserExpectedOpeningPrice(sessionName, classKey, this._this());    
            } else {
                this.myCfixCasLogin.unsubscribeExpectedOpeningPrice(sessionName, classKey, this._this());
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
                Log.debug("CfixMDXMarketDataExpectedOpeningPriceDispatcherImpl:subscribeByProduct:for productKey:: " + sessionProductStruct.productStruct.productKeys.productKey);
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
                Log.debug("CfixMDXMarketDataExpectedOpeningPriceDispatcherImpl:unsubscribeByProduct:for productKey:: " + sessionProductStruct.productStruct.productKeys.productKey);
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
                Log.debug("CfixMDXMarketDataExpectedOpeningPriceDispatcherImpl:subscribeByClass: for classKey:: " + sessionClassStruct.classStruct.classKey);
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
                Log.debug("CfixMDXMarketDataExpectedOpeningPriceDispatcherImpl:unsubscribeByClass: for classKey:: " + sessionClassStruct.classStruct.classKey);
        internalUnsubscribe(sessionClassStruct.classStruct.classKey,
                            sessionClassStruct.classStruct.classKey,
                            sessionClassStruct.sessionName,
                            cfixFixMarketDataConsumerHolder,
                            classKeyConsumersMap,
                            classToProductsMap);
    }

    public void acceptExpectedOpeningPrice(ExpectedOpeningPriceStruct eopStruct)
    {
        if (Log.isDebugOn())
            Log.debug("CfixMDXMarketDataExpectedOpeningPriceDispatcherImpl -> CFIX accepting EOP market data from MDX!!");
        acceptEOPV1(eopStruct);
    }

    public synchronized void acceptEOPV1(ExpectedOpeningPriceStruct eopStruct)
    {
        cfixMarketDataDispatcherInstrumentation.incMessagesConsumed();

        acceptEOPObjectArrayHolder.clear();

        classKeyConsumersMap.getConsumersForKey(classKey, acceptEOPObjectArrayHolder);

        //Log.information(Thread.currentThread().getName() + " " + ClassHelper.getClassNameFinalPortion(this) + " XXX 3 classKey: (" + classKey + ") holder.size(" + acceptTickerObjectArrayHolder.size() + ")");

        if (!acceptEOPObjectArrayHolder.isEmpty())
        {
            cfixMarketDataDispatcherInstrumentation.incMessagesProcessed();

            int count1 = acceptEOPObjectArrayHolder.size();

            for (int i = 0; i < count1; i++)
            {
                try
                {
                    CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder = (CfixMDXMarketDataConsumerHolder) acceptEOPObjectArrayHolder.getKey(i);

                    if (BitHelper.isBitMaskSet(debugFlags, CfixMDXMarketDataDispatcherIF.DEBUG_Accept))
                    {
                        Log.information(ClassHelper.getClassNameFinalPortion(this) + ".acceptEOP for Class" + "(" + cfixFixMarketDataConsumerHolder + ")");
                    }

                    acceptMarketDataEOP(cfixFixMarketDataConsumerHolder, eopStruct);

                    cfixMarketDataDispatcherInstrumentation.incMessagesDispatched();
                }
                catch (Exception ex)
                {
                    // nothing can be done -- continue with other consumers
                    Log.exception(ex);
                }
            }
        }

        int count2 = acceptEOPIntArrayHolder.size();

        if (count2 == 0 || (count2 == 1 && acceptEOPIntArrayHolder.getKey(0) == classKey))
        {
            return;
        }

        // Dispatch market data to users who have a product subscription -
        // Now, we get an EOP Struct of length 1 - vs. earlier, when you could get an array

        int productKey = eopStruct.productKeys.productKey;

        if (!acceptEOPIntArrayHolder.containsKey(productKey))
        {
            // Don't do anything - no product data to send out
        }
        else
        {
            acceptEOPObjectArrayHolder.clear();

            productKeyConsumersMap.getConsumersForKey(productKey, acceptEOPObjectArrayHolder);

            if (!acceptEOPObjectArrayHolder.isEmpty())
            {
                cfixMarketDataDispatcherInstrumentation.incMessagesProcessed();

                int count3 = acceptEOPObjectArrayHolder.size();

                for (int j = 0; j < count3; j++)
                {
                    try
                    {
                        CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder = (CfixMDXMarketDataConsumerHolder) acceptEOPObjectArrayHolder.getKey(j);

                        if (BitHelper.isBitMaskSet(debugFlags, CfixMDXMarketDataDispatcherIF.DEBUG_Accept))
                        {
                            Log.information(ClassHelper.getClassNameFinalPortion(this) + ".acceptEOP for Product" + getHandledMarketDataTypeName() + "(" + cfixFixMarketDataConsumerHolder + ")");
                        }

                        acceptMarketDataEOP(cfixFixMarketDataConsumerHolder, eopStruct);

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

    protected void acceptMarketDataEOP(CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, ExpectedOpeningPriceStruct struct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        cfixFixMarketDataConsumerHolder.acceptMarketDataExpectedOpeningPrice(struct);
    }
    
    protected int getClassKey(Object struct)
    {
        return ((ExpectedOpeningPriceStruct) struct).productKeys.classKey;
    }

    protected int getProductKey(Object struct)
    {
        return ((ExpectedOpeningPriceStruct) struct).productKeys.productKey;
    }
}
