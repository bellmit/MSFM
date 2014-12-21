package com.cboe.cfix.cas.marketData;

import com.cboe.interfaces.cfix.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiCallbackV4.CMIRecapConsumerPOA;
import com.cboe.idl.cmiConstants.QueueActions;
import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.idl.cmiConstants.PriceConstants;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.client.util.collections.IntIntMultipleValuesMap;
import com.cboe.client.util.collections.IntArrayHolder;
import com.cboe.client.util.collections.ObjectArrayHolder;
import com.cboe.client.util.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.cfix.cas.shared.CfixServicesHelper;
import com.cboe.util.ExceptionBuilder;
import com.cboe.application.test.ReflectiveStructTester;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.domain.util.RecapContainerV4;

import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.*;

/**
 * CfixMDXMarketDataRecapDispatcherImpl.java
 *
 * @author Vivek Beniwal
 *
 */

/*
 *  This object registers for and receives all Recap market data traffic for a specific session, and then forwards it to all listeners<br>
 *
 *  It contains the subscription maps for all listeners interested in receiving Recap subscriptions for this session<br>
 *
 */

public final class CfixMDXMarketDataRecapDispatcherImpl extends CMIRecapConsumerPOA implements CfixMDXMarketDataDispatcherIF
{
    protected String                    name;
    protected int                       classKey;
    protected int                       debugFlags;
    protected CfixKeyToConsumersMap     productKeyConsumersMap;
    protected CfixKeyToConsumersMap     classKeyConsumersMap;
    protected IntIntMultipleValuesMap   classToProductsMap              = IntIntMultipleValuesMap.synchronizedMap();
    protected IntArrayHolder            acceptRecapIntArrayHolder      = new IntArrayHolder();
    protected ObjectArrayHolder         acceptRecapObjectArrayHolder   = new ObjectArrayHolder();

    protected CfixCasLogin              myCfixCasLogin;
    protected CfixCasExternalLogin      myCfixCasExternalLogin;
    protected boolean                   isUnderlyingSub                 = false;

    public static final String MARKET_DATA_TYPE_NAME = "Recap";

    public static final String W_MAIN_Str = "W_MAIN";
    public static final String ONE_MAINStr = "ONE_MAIN";
    public static final String Underlying_Str =  "Underlying";
    public static final String EMPTYStr =  "";


    protected HashMap<Integer, RecapStructV4> cachedRecapV4  = new HashMap<Integer, RecapStructV4> (800);
    protected HashMap<Integer, LastSaleStructV4> cachedLastSaleV4 = new HashMap<Integer, LastSaleStructV4> (800);

    //protected Hashtable<Integer, ArrayList> recapMsgMap = new Hashtable <Integer, ArrayList> (256);

    protected CfixMarketDataDispatcherInstrumentationImpl cfixMarketDataDispatcherInstrumentation = new CfixMarketDataDispatcherInstrumentationImpl();
    protected CfixMDXMarketDataCurrentMarketDispatcherImpl currentMarketDispatcher;

    protected static char[] ZERO_PRICE_CHAR = {'0'};

    private int exceptionCount = 0;

    

    public CfixMDXMarketDataRecapDispatcherImpl(String name, int classKey)
    {
        if(Log.isDebugOn())
        {
            StringBuffer strBuf = new StringBuffer("Initialized CfixMDXMarketDataRecapDispatcherImpl for Dispatcher Name : ");
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
        return CfixMarketDataDispatcherIF.MarketDataType_Recap;
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

        if(Log.isDebugOn())
        {
            Log.debug("In CfixMDXMarketDataRecapDispatcherImpl:internalSubscribe: MutableInteger is : " + mutableInteger.integer + " and num1 is : " + num1);
        }

        if (num1 == 1 && mutableInteger.integer == 1)
        {
            if (BitHelper.isBitMaskSet(debugFlags, CfixMDXMarketDataDispatcherIF.DEBUG_Subscribe))
            {
                Log.information(Thread.currentThread().getName() + " " + ClassHelper.getClassNameFinalPortion(this)
                        + ".subcribeRecap for sessionName(" + sessionName + ") classKey(" + classKey + ")");
            }

            if(Log.isDebugOn())
            {
                Log.debug("In CfixMDXMarketDataRecapDispatcherImpl:internalSubscribe: MDX Enabled CFIX: Subscribing for Recap.");
            }
            if (this.isUnderlyingSub)
            {
                this.myCfixCasExternalLogin.subscribeExternalUserRecap(classKey, this._this(), QueueActions.OVERLAY_LAST);
            } else {
                this.myCfixCasLogin.subscribeRecap(classKey, this._this(), QueueActions.OVERLAY_LAST);
            }


            return true;
        } else {

            if(Log.isDebugOn())
            {
                Log.debug("In CfixMDXMarketDataRecapDispatcherImpl:internalSubscribe: MDX Enabled CFIX: Already subscribed to MDX for this class. Returning Cached Recap to service the initial get.");
            }
            try {
                if (classKey == productKey)
                    this.dispatchInitialRecapForClass(cfixFixMarketDataConsumerHolder);
                else
                    this.dispatchInitialRecapForProduct(cfixFixMarketDataConsumerHolder, productKey);
            } catch (Exception ex)
            {
                // nothing can be done --
                Log.exception(ex);
            }
            return false;
        }
    }

    protected synchronized boolean internalUnsubscribe(int classKey, int productKey, String sessionName,
                                                       CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder,
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
                        + ".unsubscribeRecap for sessionName(" + sessionName + ") classKey(" + classKey + ")");
            }

            if(Log.isDebugOn())
            {
                Log.debug("In CfixMDXMarketDataRecapDispatcherImpl:internalUnsubscribe: MDX Enabled CFIX: Unsubscribing for Recap.");
            }
            if (this.isUnderlyingSub)
            {
                this.myCfixCasExternalLogin.unsubscribeExternalUserRecap(classKey, this._this());
            } else {
                this.myCfixCasLogin.unsubscribeRecap(classKey, this._this());
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
                Log.debug("CfixMDXMarketDataRecapDispatcherImpl:subscribeByProduct:for productKey:: " + sessionProductStruct.productStruct.productKeys.productKey);
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
                Log.debug("CfixMDXMarketDataRecapDispatcherImpl:unsubscribeByProduct:for productKey:: " + sessionProductStruct.productStruct.productKeys.productKey);
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
                Log.debug("CfixMDXMarketDataRecapDispatcherImpl:subscribeByClass:for classKey:: " + sessionClassStruct.classStruct.classKey);
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
                Log.debug("CfixMDXMarketDataRecapDispatcherImpl:unsubscribeByClass:for classKey:: " + sessionClassStruct.classStruct.classKey);
        internalUnsubscribe(sessionClassStruct.classStruct.classKey,
                            sessionClassStruct.classStruct.classKey,
                            sessionClassStruct.sessionName,
                            cfixFixMarketDataConsumerHolder,
                            classKeyConsumersMap,
                            classToProductsMap);
    }

    private synchronized void dispatchInitialRecapForClass(CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if(Log.isDebugOn()){Log.debug("CfixMDXMarketDataCurrentMarketDispatcherImpl -> dispatchInitialRecapForClass!! Size of cached Recap map : " + this.cachedRecapV4.size() + " Size of cached Last Sale map : " + this.cachedLastSaleV4.size());}
        if (0 == this.cachedRecapV4.size() || 0 == this.cachedLastSaleV4.size())
        {
            return;
        }
        //
        Iterator itLS = this.cachedLastSaleV4.values().iterator();
        while (itLS.hasNext())
        {
            LastSaleStructV4 lssV4 = (LastSaleStructV4) itLS.next();
            Integer key = lssV4.productKey;
            RecapStructV4 recapProd = this.cachedRecapV4.get(key);

            //if (null != recapProd && lssV4.lastSalePrice <= recapProd.highPrice && lssV4.lastSalePrice >= recapProd.lowPrice)
            if (null != recapProd)
            {
                RecapContainerV4 rcV4 = new RecapContainerV4();
                this.mapLastSaleStructV4ToRecapContainerV4(lssV4, rcV4);
                this.mapRecapStructsV4ToRecapContainerV4(recapProd, rcV4);
                this.fillInBidAsk(rcV4, cfixFixMarketDataConsumerHolder);
                acceptMarketDataRecap(cfixFixMarketDataConsumerHolder, rcV4);
                cfixMarketDataDispatcherInstrumentation.incMessagesDispatched();

            }else{
                Log.debug("CfixMDXMarketDataCurrentMarketDispatcherImpl -> dispatchInitialRecapForClass!! Recap for some resting data is incomplete. All resting data not dispatched." );
            }
        }
    }

    private synchronized void dispatchInitialRecapForProduct(CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {

        Integer key = productKey;
        if (this.cachedRecapV4.containsKey(key) && this.cachedLastSaleV4.containsKey(key))
        {
            if(Log.isDebugOn()){ Log.debug("CfixMDXMarketDataCurrentMarketDispatcherImpl -> dispatchInitialRecapForProduct!! Size of cached Recap map : " + this.cachedRecapV4.size() + " Size of cached Last Sale map : " + this.cachedLastSaleV4.size() + ". Dispatching productKey: " + productKey );}
            LastSaleStructV4 lssV4 = this.cachedLastSaleV4.get(key);
            RecapStructV4 recapProd = this.cachedRecapV4.get(key);
            RecapContainerV4 rcV4 = new RecapContainerV4();
            this.mapLastSaleStructV4ToRecapContainerV4(lssV4, rcV4);
            this.mapRecapStructsV4ToRecapContainerV4(recapProd, rcV4);
            this.fillInBidAsk(rcV4, cfixFixMarketDataConsumerHolder);
            acceptMarketDataRecap(cfixFixMarketDataConsumerHolder, rcV4);
            cfixMarketDataDispatcherInstrumentation.incMessagesDispatched();
        }
        else
        {
            Log.debug("CfixMDXMarketDataCurrentMarketDispatcherImpl -> dispatchInitialRecapForProduct!! Product Key not updated in the cache. No initial updates dispatched for product subscription.");
        }

    }

    /**
     * Premise - for acceptRecap - MDX calls us with acceptRecap and acceptLastSale for a productKey
     * We will check the map for any existing partially mapped containers for the productKey
     * If we find an existing partially mapped container, we will map the recapStruct to the RecapContainer
     * We will then remove it from the map, and dispatch the data
     * Else we will map the recapStruct to the RecapContainer, and add it to the map.
     * When mapping the recapStruct to the RecapContainer, we will also set the RecapContainer.isRecapStructMapped() = true
     *
     * Changed - recapStruct is purely added to Cache. We do not process this any further.
     * The processing is done with lastSale - if we find that lastSale is out of bounds of the high and low price,
     * it will modify the cached recap value and move on.
     *
     * @param recapStructsV4
     * @param messageSequence
     * @param queueDepth
     * @param queueAction
     */
    public synchronized void acceptRecap(RecapStructV4[] recapStructsV4, int messageSequence, int queueDepth, short queueAction)
    {

        if (Log.isDebugOn()) { Log.debug("CfixMDXMarketDataRecapDispatcherImpl -> CFIX accepting Recap market data from MDX!! Size of RecapStructV4 is: " + recapStructsV4.length + "  for object  " + this.toString());}
        for (int i = 0; i < recapStructsV4.length; i++ )
        {
            if (PriceConstants.NO_PRICE == recapStructsV4[i].lowPrice && PriceConstants.NO_PRICE == recapStructsV4[i].highPrice)
            {
                if (Log.isDebugOn()) { Log.debug("CfixMDXMarketDataRecapDispatcherImpl -> Recap message delivered with NO_PRICE data for Low and High Price for productKey : " + recapStructsV4[i].productKey );}
                //Log.information("VivekB: CfixMDXMarketDataRecapDispatcherImpl -> acceptRecap : Received NO_PRICE for productKey : " + recapStructsV4[i].productKey);
                continue;
            }
            Integer productKey = recapStructsV4[i].productKey;
            if (Log.isDebugOn()) { Log.debug("CfixMDXMarketDataRecapDispatcherImpl -> Caching Recap for productKey: " + productKey);}
            this.cachedRecapV4.put(productKey, recapStructsV4[i]);
            //this.processRecap(recapStructsV4[i]);
        }
    }

    /**
     * Premise - for acceptLastSale - MDX calls us with acceptRecap and acceptLastSale for a productKey
     * We will check the map for any existing partially mapped containers for the productKey
     * If we find an existing partially mapped container, we will map the lastSaleStruct to the RecapContainer
     * We will then remove it from the map, and dispatch the data
     * Else we will map the lastSaleStruct to the RecapContainer, and add it to the map.
     * When mapping the lastSaleStruct to the RecapContainer, we will also set the RecapContainer.isLastSaleStructMapped() = true
     * @param lastSaleStructV4s
     * @param messageSequence
     * @param queueDepth
     * @param queueAction
     */
    public synchronized void acceptLastSale(LastSaleStructV4[] lastSaleStructV4s, int messageSequence, int queueDepth, short queueAction)
    {
        if (Log.isDebugOn()) {Log.debug(" CfixMDXMarketDataRecapDispatcherImpl -> CFIX accepting Last Sale market data from MDX!! Size of LastSaleStructV4 " + lastSaleStructV4s.length + "  for object  " + this.toString());}

        for (int i = 0; i < lastSaleStructV4s.length; i++ )
        {
            Integer productKey = lastSaleStructV4s[i].productKey;
            this.cachedLastSaleV4.put(productKey, lastSaleStructV4s[i]);
            try {
                this.processLastSale(lastSaleStructV4s[i]);
            } catch (Exception e){
                this.exceptionCount++;
                if (this.exceptionCount < 1000){
                    StringBuffer strBuf = new StringBuffer("Exception in mapLastSaleStructV4ToRecapContainerV4 for class : ");
                    strBuf.append(lastSaleStructV4s[i].classKey).append(". The LastSaleStructV4 received from MDX was : ");
                    Log.debug(strBuf.toString());
                    com.cboe.domain.util.ReflectiveStructBuilder.printStruct(lastSaleStructV4s[i], "VB -- LastSaleStructV4");
                }
            }
        }
    }

    /**
     * This method is not used - we process this in the processLastSale method.
     *
    private synchronized void processRecap(RecapStructV4 structsV4)
    {
        RecapStructV4 rsV4 = structsV4;
        Integer key = rsV4.productKey;
        if (Log.isDebugOn()) { Log.debug("CfixMDXMarketDataRecapDispatcherImpl -> processRecap : size of recapMsgMap : " + recapMsgMap.size()); }

        if ( recapMsgMap.isEmpty() )
        {
            if (Log.isDebugOn()) { Log.debug("CfixMDXMarketDataRecapDispatcherImpl -> processRecap : recapMsgMap is empty. No LastSale awaiting to be published. Exiting method!!" ); }
            return;
        } else if ( recapMsgMap.containsKey(key))
        // there exists a reference for this key
        {
            ArrayList<RecapContainerV4> rcAL = (ArrayList<RecapContainerV4>) recapMsgMap.get(key);
            // check the size of the ArrayList
            if (rcAL.isEmpty())
            {
                if (Log.isDebugOn()){ Log.debug("CfixMDXMarketDataRecapDispatcherImpl -> processRecap : Key exists for product : " + key + "  but ArrayList is empty. No LastSale awaiting to be published. Exiting method!! ");}
            } else
            // Array has one or many partially mapped recap containers
            {
                if (Log.isDebugOn()){ Log.debug("CfixMDXMarketDataRecapDispatcherImpl -> processRecap : Size of RecapContainerV4 ArrayList is : " + rcAL.size() + " for productKey : " + key);}
                for (int i = 0; i < rcAL.size(); i ++)
                {
                    RecapContainerV4 rcV4i = rcAL.get(i);
                    // Last Sale cannot be lower than the low price, nor higher than the high price. It can be equal to either.
                    if (Log.isDebugOn()){ Log.debug("CfixMDXMarketDataRecapDispatcherImpl -> processRecap : for i = " + i + " . LastSale Price : " + rcV4i.getLastSaleRawPrice() + ". Recap Low Price : " + rsV4.lowPrice + ". Recap High Price : " + rsV4.highPrice);}
                    if (rcV4i.getLastSaleRawPrice() >= rsV4.lowPrice && rcV4i.getLastSaleRawPrice() <= rsV4.highPrice)
                    {
                        // Map the recap, and remove this from the list
                        this.mapRecapStructsV4ToRecapContainerV4(rsV4, rcV4i);
                        this.dispatchRecapContainerV4(rcV4i);
                        if (Log.isDebugOn()) {Log.debug("CfixMDXMarketDataRecapDispatcherImpl -> processRecap : dispatching RecapContainer for productKey : " + key);}
                        rcAL.remove(i);
                    }
                    // VivekB : Added for diagnosis of recap problem
                    else if (rcV4i.getLastSaleRawPrice() <= rsV4.lowPrice){
                        Log.information("VivekB: CfixMDXMarketDataRecapDispatcherImpl -> processRecap : LastSale Lower than Low for productKey : " + key);
                    } else if (rcV4i.getLastSaleRawPrice() >= rsV4.highPrice){
                        Log.information("VivekB: CfixMDXMarketDataRecapDispatcherImpl -> processRecap : LastSale Higher than High for productKey : " + key);
                    }
                }
            }
            return;
        } else
        {
            if (Log.isDebugOn()){ Log.debug("CfixMDXMarketDataRecapDispatcherImpl -> processRecap : recapMsgMap is not empty, but does not contain key, No LastSale awaiting to be published. Exiting method!! ");}
        }
        return;
    }
    */

    private synchronized void processLastSale(LastSaleStructV4 structsV4)
    {
        LastSaleStructV4 lssV4 = structsV4;
        Integer key = lssV4.productKey;
        //if (Log.isDebugOn()){Log.debug("CfixMDXMarketDataRecapDispatcherImpl -> processLastSale : Size of recapMsgMap : " + recapMsgMap.size() + ". Size of Cached Recap map : " + this.cachedRecapV4.size());}
        if (Log.isDebugOn()){Log.debug("CfixMDXMarketDataRecapDispatcherImpl -> processLastSale : Size of Cached Recap map : " + this.cachedRecapV4.size());}

        // VivekB: No existing recap for this product -
        if (this.cachedRecapV4.size() == 0 || this.cachedRecapV4.get(key) == null)
        {
            if (Log.isDebugOn()){Log.debug("CfixMDXMarketDataRecapDispatcherImpl -> processLastSale : cachedRecapV4 does not contain key : " + key );}
            /*
            ArrayList<RecapContainerV4> rcAL;
            if (recapMsgMap.containsKey(key))
            {
                rcAL = (ArrayList<RecapContainerV4>) recapMsgMap.get(key);
            }else {
                rcAL = new ArrayList<RecapContainerV4>(8);
            }
            */
            RecapContainerV4 rcV4 = new RecapContainerV4();
            // map Last Sale and Recap - and dispatch.
            // This sets the recapStruct data such that all relevant prices match the lastSal data
            this.mapLastSaleStructV4RecapStructV4ToRecapContainerV4(lssV4, rcV4);
            if (Log.isDebugOn()){Log.debug("CfixMDXMarketDataRecapDispatcherImpl -> processLastSale : after mapping both Last Sale and Recap. Dispatching RecapContainer for productKey : " + key);}
            this.dispatchRecapContainerV4(rcV4);
            //rcAL.add(rcV4);
            //recapMsgMap.put(key, rcAL);
        }
        // VivekB: There is existing Recap - use this recap data
        else {
            RecapStructV4 recapProd = this.cachedRecapV4.get(key);
            RecapContainerV4 rcV4 = new RecapContainerV4();
            this.mapRecapStructsV4ToRecapContainerV4(recapProd, rcV4);
            this.mapLastSaleStructV4ToRecapContainerV4(lssV4, rcV4);
            if (Log.isDebugOn()){ Log.debug("CfixMDXMarketDataRecapDispatcherImpl -> processLastSale : The mapped LastSale Price : " + String.valueOf(rcV4.getLastSalePrice()) + ". Recap Low Price : " + String.valueOf(rcV4.getLowPrice()) + ". Recap High Price : " + String.valueOf(rcV4.getHighPrice()));}
            // Adjust Recap data - if LastSale data is more current
            this.adjustOutgoingRecapPrice(rcV4);
            if (Log.isDebugOn()){Log.debug("CfixMDXMarketDataRecapDispatcherImpl -> processLastSale : dispatching RecapContainer for productKey : " + key);}
            this.dispatchRecapContainerV4(rcV4);

            /*
            //Log.debug("CfixMDXMarketDataRecapDispatcherImpl -> processLastSale : LastSale Price : " + lssV4.lastSalePrice + ". Recap Low Price : " + recapProd.lowPrice + ". Recap High Price : " + recapProd.highPrice);
            if (lssV4.lastSalePrice >= recapProd.lowPrice && lssV4.lastSalePrice <= recapProd.highPrice)
            {
                RecapContainerV4 rcV4 = new RecapContainerV4();
                this.mapRecapStructsV4ToRecapContainerV4(recapProd, rcV4);
                this.mapLastSaleStructV4ToRecapContainerV4(lssV4, rcV4);
                if (Log.isDebugOn()){Log.debug("CfixMDXMarketDataRecapDispatcherImpl -> processLastSale : dispatching RecapContainer for productKey : " + key);}
                this.dispatchRecapContainerV4(rcV4);
                return;
            } else {
                // VivekB : Added for diagnosis of recap problem
                Log.information("VivekB: CfixMDXMarketDataRecapDispatcherImpl -> processLastSale : LastSale Price out of bounds : " + lssV4.lastSalePrice + ". Recap Low Price : " + recapProd.lowPrice + ". Recap High Price : " + recapProd.highPrice);
                if (recapMsgMap.containsKey(key))
                {
                    if (Log.isDebugOn()){Log.debug("CfixMDXMarketDataRecapDispatcherImpl -> processLastSale : recapMsgMap contains key : " + key);}
                    ArrayList<RecapContainerV4> rcAL = (ArrayList<RecapContainerV4>) recapMsgMap.get(key);
                    RecapContainerV4 rcV4 = new RecapContainerV4();
                    this.mapLastSaleStructV4ToRecapContainerV4(lssV4, rcV4);
                    if (Log.isDebugOn()){Log.debug("CfixMDXMarketDataRecapDispatcherImpl -> processLastSale : after mapping, inserting into ArrayList ");}
                    rcAL.add(rcV4);
                    recapMsgMap.put(key, rcAL);

                }else
                {
                    if (Log.isDebugOn()) {Log.debug("CfixMDXMarketDataRecapDispatcherImpl -> processLastSale : recapMsgMap does not contain key : Inserting new Key into map for product : " + key);}
                    RecapContainerV4 rcV4 = new RecapContainerV4();
                    rcV4 = this.mapLastSaleStructV4ToRecapContainerV4(lssV4, rcV4);
                    if (Log.isDebugOn()) {Log.debug("CfixMDXMarketDataRecapDispatcherImpl -> processLastSale : after mapping, inserting into ArrayList ");}
                    ArrayList<RecapContainerV4> rcAL = new ArrayList<RecapContainerV4>(8);
                    rcAL.add(rcV4);
                    recapMsgMap.put(key, rcAL);
                    if (Log.isDebugOn()){Log.debug("CfixMDXMarketDataRecapDispatcherImpl -> processLastSale : inserted into MsgMap ");}
                }
                return;
            }

            */
        }
    }

    public void dispatchRecapContainersV4(RecapContainerV4[] recapContainerV4Array)
    {
        int classKey = recapContainerV4Array[0].getClassKey();

        int count;
        int i;
        int j;
        CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder;

        cfixMarketDataDispatcherInstrumentation.incMessagesConsumed();

        acceptRecapObjectArrayHolder.clear();

        // get the users who have a class subscription - the call will populate the acceptCMObjectArrayHolder with the consumers.
        classKeyConsumersMap.getConsumersForKey(classKey, acceptRecapObjectArrayHolder);

        //Log.information(Thread.currentThread().getName() + " " + ClassHelper.getClassNameFinalPortion(this) + " XXX 3 classKey: (" + classKey + ") holder.size(" + acceptTickerObjectArrayHolder.size() + ")");

        if (!acceptRecapObjectArrayHolder.isEmpty())
        {
            cfixMarketDataDispatcherInstrumentation.incMessagesProcessed();

            count = acceptRecapObjectArrayHolder.size();

            for (i = 0; i < count; i++)
            {
                try
                {
                    cfixFixMarketDataConsumerHolder = (CfixMDXMarketDataConsumerHolder) acceptRecapObjectArrayHolder.getKey(i);

                    if (BitHelper.isBitMaskSet(debugFlags, CfixMDXMarketDataDispatcherIF.DEBUG_Accept))
                    {
                        Log.information(ClassHelper.getClassNameFinalPortion(this) + ".acceptRecap for Class" + "(" + cfixFixMarketDataConsumerHolder + ")");
                    }

                    acceptMarketDataRecap(cfixFixMarketDataConsumerHolder, recapContainerV4Array);

                    cfixMarketDataDispatcherInstrumentation.incMessagesDispatched();
                }
                catch (Exception ex)
                {
                    // nothing can be done -- continue with other consumers
                    Log.exception(ex);
                }
            }
        }

        acceptRecapIntArrayHolder.clear();

        classToProductsMap.getValuesForKey(classKey, acceptRecapIntArrayHolder);

        count = acceptRecapIntArrayHolder.size();

        if (count == 0 || (count == 1 && acceptRecapIntArrayHolder.getKey(0) == classKey))
        {
            return;
        }

        int productKey;

        // dispatch market data to users who have a product subscription -
        for (i = 0; i < recapContainerV4Array.length; i++)
        {
            productKey = recapContainerV4Array[i].getProductKey();

            if (!acceptRecapIntArrayHolder.containsKey(productKey))
            {
                continue;
            }

            acceptRecapObjectArrayHolder.clear();

            productKeyConsumersMap.getConsumersForKey(productKey, acceptRecapObjectArrayHolder);
            if (!acceptRecapObjectArrayHolder.isEmpty())
            {
                cfixMarketDataDispatcherInstrumentation.incMessagesProcessed();

                count = acceptRecapObjectArrayHolder.size();

                for (j = 0; j < count; j++)
                {
                    try
                    {
                        cfixFixMarketDataConsumerHolder = (CfixMDXMarketDataConsumerHolder) acceptRecapObjectArrayHolder.getKey(j);

                        if (BitHelper.isBitMaskSet(debugFlags, CfixMDXMarketDataDispatcherIF.DEBUG_Accept))
                        {
                            Log.information(ClassHelper.getClassNameFinalPortion(this) + ".acceptRecap for Product" + getHandledMarketDataTypeName() + "(" + cfixFixMarketDataConsumerHolder + ")");
                        }

                        acceptMarketDataRecap(cfixFixMarketDataConsumerHolder, recapContainerV4Array[i]);

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

    public void dispatchRecapContainerV4(RecapContainerV4 recapContainerV4)
    {
        int classKey = recapContainerV4.getClassKey();

        int count;
        int i;
        int j;
        CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder;
        boolean cmFilled = false;

        cfixMarketDataDispatcherInstrumentation.incMessagesConsumed();

        acceptRecapObjectArrayHolder.clear();

        // get the users who have a class subscription - the call will populate the acceptCMObjectArrayHolder with the consumers.
        
        classKeyConsumersMap.getConsumersForKey(classKey, acceptRecapObjectArrayHolder);

        //Log.information(Thread.currentThread().getName() + " " + ClassHelper.getClassNameFinalPortion(this) + " XXX 3 classKey: (" + classKey + ") holder.size(" + acceptTickerObjectArrayHolder.size() + ")");

        if (!acceptRecapObjectArrayHolder.isEmpty())
        {
            cfixMarketDataDispatcherInstrumentation.incMessagesProcessed();

            count = acceptRecapObjectArrayHolder.size();

            for (i = 0; i < count; i++)
            {
                try
                {
                    cfixFixMarketDataConsumerHolder = (CfixMDXMarketDataConsumerHolder) acceptRecapObjectArrayHolder.getKey(i);
                    if(!cmFilled)
                    {
                        this.fillInBidAsk(recapContainerV4, cfixFixMarketDataConsumerHolder);
                    }


                    if (BitHelper.isBitMaskSet(debugFlags, CfixMDXMarketDataDispatcherIF.DEBUG_Accept))
                    {
                        Log.information(ClassHelper.getClassNameFinalPortion(this) + ".acceptRecap for Class" + "(" + cfixFixMarketDataConsumerHolder + ")");
                    }

                    acceptMarketDataRecap(cfixFixMarketDataConsumerHolder, recapContainerV4);

                    cfixMarketDataDispatcherInstrumentation.incMessagesDispatched();
                }
                catch (Exception ex)
                {
                    // nothing can be done -- continue with other consumers
                    Log.exception(ex);
                }
            }
        }

        acceptRecapIntArrayHolder.clear();

        classToProductsMap.getValuesForKey(classKey, acceptRecapIntArrayHolder);

        count = acceptRecapIntArrayHolder.size();

        if (count == 0 || (count == 1 && acceptRecapIntArrayHolder.getKey(0) == classKey))
        {
            return;
        }

        int productKey;

        // dispatch market data to users who have a product subscription -
        productKey = recapContainerV4.getProductKey();

        if (acceptRecapIntArrayHolder.containsKey(productKey))
        {
            acceptRecapObjectArrayHolder.clear();

            productKeyConsumersMap.getConsumersForKey(productKey, acceptRecapObjectArrayHolder);
            if (!acceptRecapObjectArrayHolder.isEmpty())
            {
                cfixMarketDataDispatcherInstrumentation.incMessagesProcessed();

                count = acceptRecapObjectArrayHolder.size();

                for (j = 0; j < count; j++)
                {
                    try
                    {
                        cfixFixMarketDataConsumerHolder = (CfixMDXMarketDataConsumerHolder) acceptRecapObjectArrayHolder.getKey(j);
                        if(!cmFilled)
                            this.fillInBidAsk(recapContainerV4, cfixFixMarketDataConsumerHolder);

                        if (BitHelper.isBitMaskSet(debugFlags, CfixMDXMarketDataDispatcherIF.DEBUG_Accept))
                        {
                            Log.information(ClassHelper.getClassNameFinalPortion(this) + ".acceptRecap for Product" + getHandledMarketDataTypeName() + "(" + cfixFixMarketDataConsumerHolder + ")");
                        }

                        acceptMarketDataRecap(cfixFixMarketDataConsumerHolder, recapContainerV4);

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

    private synchronized RecapContainerV4 mapRecapStructsV4ToRecapContainerV4(RecapStructV4 recapStructsV4, RecapContainerV4 recapContainerV4)
    {
        if (recapContainerV4.getSessionName().equalsIgnoreCase(EMPTYStr))
            recapContainerV4.setSessionName(mapProductTypeToSessionName(recapStructsV4.productType));

        recapContainerV4.setProductKey(recapStructsV4.productKey);
        recapContainerV4.setClassKey(recapStructsV4.classKey);

        if (PriceConstants.NO_PRICE == recapStructsV4.highPrice)
            recapContainerV4.setHighPrice(ZERO_PRICE_CHAR);
        else
            recapContainerV4.setHighPrice((StringHelper.appendPriceWithScale(new FastCharacterWriter(1), recapStructsV4.highPrice, recapStructsV4.priceScale)).toCharArray());

        if (PriceConstants.NO_PRICE == recapStructsV4.lowPrice)
            recapContainerV4.setLowPrice(ZERO_PRICE_CHAR);
        else
            recapContainerV4.setLowPrice((StringHelper.appendPriceWithScale(new FastCharacterWriter(1), recapStructsV4.lowPrice, recapStructsV4.priceScale)).toCharArray());

        if (PriceConstants.NO_PRICE == recapStructsV4.openPrice)
            recapContainerV4.setOpenPrice(ZERO_PRICE_CHAR);
        else
            recapContainerV4.setOpenPrice((StringHelper.appendPriceWithScale(new FastCharacterWriter(1), recapStructsV4.openPrice, recapStructsV4.priceScale)).toCharArray());

        if (PriceConstants.NO_PRICE == recapStructsV4.previousClosePrice)
            recapContainerV4.setPreviousClosePrice(ZERO_PRICE_CHAR);
        else
            recapContainerV4.setPreviousClosePrice((StringHelper.appendPriceWithScale(new FastCharacterWriter(1), recapStructsV4.previousClosePrice, recapStructsV4.priceScale)).toCharArray());


        // todo - update this with correct value
        if (PriceConstants.NO_PRICE == recapStructsV4.previousClosePrice)
            recapContainerV4.setClosePrice(ZERO_PRICE_CHAR);
        else
            recapContainerV4.setClosePrice((StringHelper.appendPriceWithScale(new FastCharacterWriter(1), recapStructsV4.previousClosePrice, recapStructsV4.priceScale)).toCharArray());

        recapContainerV4.setRecapSentTime(recapStructsV4.sentTime);
        recapContainerV4.setLowRawPrice(recapStructsV4.lowPrice);
        recapContainerV4.setHighRawPrice(recapStructsV4.highPrice);

        recapContainerV4.setRecapMapped(true);

        return recapContainerV4;
    }

    private synchronized RecapContainerV4 mapLastSaleStructV4ToRecapContainerV4(LastSaleStructV4 lastSaleStructV4, RecapContainerV4 recapContainerV4)
    {
        if (recapContainerV4.getSessionName().equalsIgnoreCase(EMPTYStr))
            recapContainerV4.setSessionName(mapProductTypeToSessionName(lastSaleStructV4.productType));

        if (PriceConstants.NO_PRICE == lastSaleStructV4.lastSalePrice)
            recapContainerV4.setLastSalePrice(ZERO_PRICE_CHAR);
        else
            recapContainerV4.setLastSalePrice((StringHelper.appendPriceWithScale(new FastCharacterWriter(1), lastSaleStructV4.lastSalePrice, lastSaleStructV4.priceScale)).toCharArray());

        recapContainerV4.setLastSaleVolume(lastSaleStructV4.lastSaleVolume);

        // Check for time -
        recapContainerV4.setTradeTime(DateHelper.makeHHMMSSsss(lastSaleStructV4.lastSaleTime, DateHelper.TIMEZONE_OFFSET_UTC));
        recapContainerV4.setTickDirection(lastSaleStructV4.tickDirection);

        recapContainerV4.setLastSaleSentTime(lastSaleStructV4.sentTime);
        recapContainerV4.setLastSaleRawPrice(lastSaleStructV4.lastSalePrice);

        recapContainerV4.setLastSaleMapped(true);

        return recapContainerV4;
    }

    private synchronized RecapContainerV4 mapLastSaleStructV4RecapStructV4ToRecapContainerV4(LastSaleStructV4 lastSaleStructV4, RecapContainerV4 recapContainerV4)
    {
        if (recapContainerV4.getSessionName().equalsIgnoreCase(EMPTYStr))
            recapContainerV4.setSessionName(mapProductTypeToSessionName(lastSaleStructV4.productType));

        recapContainerV4.setProductKey(lastSaleStructV4.productKey);
        recapContainerV4.setClassKey(lastSaleStructV4.classKey);

        if (PriceConstants.NO_PRICE == lastSaleStructV4.lastSalePrice)
        {
            recapContainerV4.setLastSalePrice(ZERO_PRICE_CHAR);
            recapContainerV4.setHighPrice(ZERO_PRICE_CHAR);
            recapContainerV4.setLowPrice(ZERO_PRICE_CHAR);
            recapContainerV4.setOpenPrice(ZERO_PRICE_CHAR);
            recapContainerV4.setPreviousClosePrice(ZERO_PRICE_CHAR);
            recapContainerV4.setClosePrice(ZERO_PRICE_CHAR);
        }
        else
        {
            recapContainerV4.setLastSalePrice((StringHelper.appendPriceWithScale(new FastCharacterWriter(1), lastSaleStructV4.lastSalePrice, lastSaleStructV4.priceScale)).toCharArray());
            recapContainerV4.setHighPrice((StringHelper.appendPriceWithScale(new FastCharacterWriter(1), lastSaleStructV4.lastSalePrice, lastSaleStructV4.priceScale)).toCharArray());
            recapContainerV4.setLowPrice((StringHelper.appendPriceWithScale(new FastCharacterWriter(1), lastSaleStructV4.lastSalePrice, lastSaleStructV4.priceScale)).toCharArray());
            recapContainerV4.setOpenPrice((StringHelper.appendPriceWithScale(new FastCharacterWriter(1), lastSaleStructV4.lastSalePrice, lastSaleStructV4.priceScale)).toCharArray());
            recapContainerV4.setPreviousClosePrice((StringHelper.appendPriceWithScale(new FastCharacterWriter(1), lastSaleStructV4.lastSalePrice, lastSaleStructV4.priceScale)).toCharArray());
            recapContainerV4.setClosePrice((StringHelper.appendPriceWithScale(new FastCharacterWriter(1), lastSaleStructV4.lastSalePrice, lastSaleStructV4.priceScale)).toCharArray());
        }


        recapContainerV4.setLastSaleVolume(lastSaleStructV4.lastSaleVolume);

        // Check for time -
        recapContainerV4.setTradeTime(DateHelper.makeHHMMSSsss(lastSaleStructV4.lastSaleTime, DateHelper.TIMEZONE_OFFSET_UTC));
        recapContainerV4.setTickDirection(lastSaleStructV4.tickDirection);

        recapContainerV4.setLastSaleSentTime(lastSaleStructV4.sentTime);
        recapContainerV4.setLastSaleRawPrice(lastSaleStructV4.lastSalePrice);

        recapContainerV4.setRecapSentTime(lastSaleStructV4.sentTime);
        recapContainerV4.setLowRawPrice(lastSaleStructV4.lastSalePrice);
        recapContainerV4.setHighRawPrice(lastSaleStructV4.lastSalePrice);

        recapContainerV4.setRecapMapped(true);
        recapContainerV4.setLastSaleMapped(true);

        return recapContainerV4;
    }

    private synchronized void adjustOutgoingRecapPrice (RecapContainerV4 recapContainerV4)
    {
        if (recapContainerV4.getLowPrice().length == 1 && recapContainerV4.getLowPrice()[0] == '0')
        {
            recapContainerV4.setLowPrice(recapContainerV4.getLastSalePrice());
        }
        if (recapContainerV4.getHighPrice().length == 1 && recapContainerV4.getHighPrice()[0] == '0')
        {
            recapContainerV4.setHighPrice(recapContainerV4.getLastSalePrice());
        }

        try {
            Double tradePrice =  new Double (String.valueOf(recapContainerV4.getLastSalePrice()).trim());
            Double lowPrice = new Double (String.valueOf(recapContainerV4.getLowPrice()).trim());
            Double highPrice = new Double (String.valueOf(recapContainerV4.getHighPrice()).trim());

            if (tradePrice.compareTo(lowPrice) < 0)
            {
                recapContainerV4.setLowPrice(recapContainerV4.getLastSalePrice());
            }
            if (tradePrice.compareTo(highPrice) > 0)
            {
                recapContainerV4.setHighPrice(recapContainerV4.getLastSalePrice());
            }
        } catch (NumberFormatException nfe) {
            StringBuilder strB = new StringBuilder("Exception in comparing prices. Trade Price : ").append(recapContainerV4.getLastSalePrice().toString());
            strB.append(" Low Price : ").append(recapContainerV4.getLowPrice().toString()).append(" High Price : ").append(recapContainerV4.getHighPrice().toString());
            Log.information(strB.toString());
        }
    }

    private RecapContainerV4 fillInBidAsk(RecapContainerV4 recapContainerV4, CfixMDXMarketDataConsumerHolder cfixMDXMarketDataConsumerHolder)
    {
        try
        {
            String sessionName = EMPTYStr;
            if (cfixMDXMarketDataConsumerHolder.getSessionClassStruct() != null)
            {
                sessionName = cfixMDXMarketDataConsumerHolder.getSessionClassStruct().sessionName;
            }else
            {
                sessionName = cfixMDXMarketDataConsumerHolder.getSessionProductStruct().sessionName;
            }
            if (!sessionName.equalsIgnoreCase(EMPTYStr));
                recapContainerV4.setSessionName(sessionName);

            CurrentMarketStructV4 cmStructV4 = this.currentMarketDispatcher.getCachedCurrentMarketForProduct(recapContainerV4.getProductKey());

            if (null == cmStructV4)
            {
                for (int i=0; i < 3; i++)
                {
                    if (Log.isDebugOn()) { Log.debug("CfixMDXMarketDataRecapDispatcherImpl -> fillInBidAsk : Sleeping for 50 millis for iteration " + i);}
                    Thread.currentThread().sleep(50);
                    cmStructV4 = this.currentMarketDispatcher.getCachedCurrentMarketForProduct(recapContainerV4.getProductKey());
                    if (null != cmStructV4)
                        break;

                }
            }

            if (null == cmStructV4)
            {
                Log.debug("Could not get bid and ask for Recap! Filling in zeroes.");
                recapContainerV4.setBidPrice(ZERO_PRICE_CHAR);
                recapContainerV4.setBidSize(0);
                recapContainerV4.setBidTime(DateHelper.makeHHMMSSsss(0, DateHelper.TIMEZONE_OFFSET_UTC));

                recapContainerV4.setAskPrice(ZERO_PRICE_CHAR);
                recapContainerV4.setAskSize(0);
                recapContainerV4.setAskTime(DateHelper.makeHHMMSSsss(0, DateHelper.TIMEZONE_OFFSET_UTC));
                
            } else
            {
                if (PriceConstants.NO_PRICE == cmStructV4.bidPrice)
                recapContainerV4.setBidPrice(ZERO_PRICE_CHAR);
                else
                    recapContainerV4.setBidPrice((StringHelper.appendPriceWithScale(new FastCharacterWriter(1), cmStructV4.bidPrice, cmStructV4.priceScale)).toCharArray());

                int bidSize = 0;
                for (int l = 0; l < cmStructV4.bidSizeSequence.length; l++)
                {
                    bidSize += cmStructV4.bidSizeSequence[l].quantity;
                }
                recapContainerV4.setBidSize(bidSize);
                recapContainerV4.setBidTime(DateHelper.makeHHMMSSsss(cmStructV4.sentTime, DateHelper.TIMEZONE_OFFSET_UTC));

                if (PriceConstants.NO_PRICE == cmStructV4.askPrice)
                    recapContainerV4.setAskPrice(ZERO_PRICE_CHAR);
                else
                    recapContainerV4.setAskPrice((StringHelper.appendPriceWithScale(new FastCharacterWriter(1), cmStructV4.askPrice, cmStructV4.priceScale)).toCharArray());
                int askSize = 0;
                for (int l = 0; l < cmStructV4.askSizeSequence.length; l++)
                {
                    bidSize += cmStructV4.askSizeSequence[l].quantity;
                }
                recapContainerV4.setAskSize(askSize);
                recapContainerV4.setAskTime(DateHelper.makeHHMMSSsss(cmStructV4.sentTime, DateHelper.TIMEZONE_OFFSET_UTC));
            }



        } catch (Exception e)
        {
            Log.exception("Could not get bid and ask for Recap!" + e.getMessage(),e );
        }

        return recapContainerV4;
    }

    private String mapProductTypeToSessionName(short productType)
    {
        String sessionName;
        switch (productType)
        {
            case ProductTypes.OPTION:
                sessionName = W_MAIN_Str;
                break;
            case ProductTypes.FUTURE:
                sessionName = ONE_MAINStr;
                break;
            case ProductTypes.STRATEGY:
                sessionName = W_MAIN_Str;
                break;
            case ProductTypes.EQUITY:
                sessionName = Underlying_Str;
                break;
            case ProductTypes.INDEX:
                sessionName = W_MAIN_Str;
                break;
            default:
                sessionName = W_MAIN_Str;
                break;
        }
        return sessionName;
    }

    protected void acceptMarketDataRecap(CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, RecapContainerV4 recapContainerV4) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        cfixFixMarketDataConsumerHolder.acceptMarketDataRecap(recapContainerV4);
    }

    protected void acceptMarketDataRecap(CfixMDXMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, RecapContainerV4[] recapContainersV4) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        cfixFixMarketDataConsumerHolder.acceptMarketDataRecap(recapContainersV4);
    }

    protected int getClassKey(Object struct)
    {
        if (struct instanceof RecapStructV4)
            return ((RecapStructV4) struct).classKey;
        else
            return ((LastSaleStructV4) struct).classKey;
    }

    protected int getProductKey(Object struct)
    {
        if (struct instanceof RecapStructV4)
            return ((RecapStructV4) struct).classKey;
        else
            return ((LastSaleStructV4) struct).classKey;
    }

    protected void setCurrentMarketCallback(CfixMDXMarketDataCurrentMarketDispatcherImpl currentMarketDataDispatcher)
    {
        this.currentMarketDispatcher = currentMarketDataDispatcher;  
    }


}
