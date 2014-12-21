//
// -----------------------------------------------------------------------------------
// Source file: DSMCalculator.java
//
// PACKAGE: com.cboe.presentation.api;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2010 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import com.cboe.util.event.EventChannelListener;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.ChannelType;
import com.cboe.util.ChannelKey;
import com.cboe.util.channel.ChannelEvent;

import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.presentation.product.*;
import com.cboe.interfaces.presentation.api.MarketQueryV3API;
import com.cboe.interfaces.presentation.api.ProductQueryAPI;
import com.cboe.interfaces.presentation.marketData.UserMarketDataStruct;
import com.cboe.interfaces.presentation.marketData.DsmParameterStruct;
import com.cboe.interfaces.presentation.marketData.DsmBidAskStruct;
import com.cboe.interfaces.presentation.common.logging.IGUILogger;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.domain.util.PriceFactory;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiConstants.ProductTypes;

import java.util.*;
import java.util.concurrent.CountDownLatch;

import org.omg.CORBA.UserException;


/**
 * This class calculates and publishes DSM data for strategies. For every strategy class, it subscribes for market data
 * updates for the class keys of the constituent legs. When a leg price is updated, it marks all strategies containing
 * that leg as dirty, so that their DSMs can be calculated. The calculation is done when a timer expires, after which
 * the strategies are removed from the dirty list. It also subscribes for updates to the strategy class, so that it
 * can rebuild its internal maps when new strategies are created on the fly.
 *
 * It keeps a count of listeners per strategy class. It starts the DSM calculation timer when the first listener is
 * added, and it stops the timer when the last listener is removed. For every strategy class key, it subscribes for
 * updates to the strategy as well as market data updates for legs, when the first listener is added for that strategy
 * class. It unsubscribes when the last listener is removed for that strategy class.
 *
 * This class is not a singleton, but currently only one copy is created by the factory
 * {@link com.cboe.presentation.api.DSMCalculatorFactory#find()}
 *
 * @author  Shawn Khosravani
 */
public class DSMCalculator implements EventChannelListener
{
    private static final String                    myClassName               = "DSMCalculator";
    private static final String                    PROPERTIES_SECTION_NAME   = "Timers";
    private static final String                    DSM_UPDATE_TIMER_KEY_NAME = "DsmUpdateTimerMillis";

    private static final int                       MIN_UPDATE_DELAY          = 500;
    private static final int                       MAX_UPDATE_DELAY          = 5000;
    private static final int                       DEFAULT_UPDATE_DELAY      = MIN_UPDATE_DELAY;

    private static final GUILoggerBusinessProperty loggingProperty;
    private static final MarketQueryV3API          marketQuertyV3API;
    private static final ProductQueryAPI           productQueryAPI;

    private        final IGUILogger                logger;
    private              Timer                     timer;

    // inner class /////////////////////////////////
    private class BidAskPrices
    {
        // unused
        // public BidAskPrices()
        // {
        //     this(PriceFactory.createPriceStruct(PriceTypes.NO_PRICE, 0, 0),
        //          PriceFactory.createPriceStruct(PriceTypes.NO_PRICE, 0, 0));
        // }
        public BidAskPrices(PriceStruct bid, PriceStruct ask)
        {
            bidPrice = bid;
            askPrice = ask;
        }
        public PriceStruct bidPrice;
        public PriceStruct askPrice;
    }

    /**
     * this is a sorted map indexed by (Future, Option, or underlying) product key, and each value is a Set of Strategy
     * product keys where one of its legs is the given index product key. this is used to figure out what strategy
     * product(s) need to have their DSM recalculated when a [leg] product's current market changes
     */
    private final Map<Integer, Set<Strategy>> strategiesContainingLeg;

    /**
     * This is a map indexed by strategy class key, with the value being another map which is indexed by product keys of
     * the legs that comprise strategy products of this strategy class. The values of this nested map are the bid / ask
     * prices of the indexed legs which are needed to calculate DSM for the indexed strategy class' strategy products.
     *
     * When timer expires, we first loop over strategy class keys in this map, because there is only one DSM calculator
     * object that calculates DSM for all subscribed strategy classes. then use the strategy class key to find the map
     * of product keys and their corresponding bid / ask prices. this map is really the price cache for all relevant
     * produts, whether or not they appear as legs in any strategies.
     * use the class keys in here to find affected strategy products and calculate their DSM using
     * the DSM parameter structures of its constituent leg products
     *
     * This map is added to when a strategy class is first subscribed to, but it is accessed every time the timer
     * expires to calculate DSM. So it is sorted (TreeMap) to speed up read access, at the expense of write access.
     *
     * When a new strategy product is created after the subscription, the strategy key class is used to retrieve the
     * map of product keys to Bid / Ask prices, so that the constinuent legs of the new strategy can be added as new
     * keys of the map, if they are not already there (because the same legs may also exist in other exisitng strategies)
     *
     * All access to this map is in synchronized methods
     */
    private Map<Integer, Map<Integer, BidAskPrices>>  mapStrategyClassToUpdatedBidAskForLegs;

    /**
     * This map is indexed by a product class key, which may be underlying, option, future, or index, and the value is
     * class key of the Strategy class where indexed product can be used as legs. When market data updates for a product,
     * this map is used to find the relevant strategy class key, which is then used to look in
     * mapStrategyClassToUpdatedBidAskForLegs for the map of constinuent leg products so that the updated market data
     * can be stored in the Bid / Ask structure of the leg product. When the DSM timer expires, the Bid / Ask structures
     * are used to calculate the DSM for the dirty strategies (i.e. those where at least one leg's market data has been
     * updated since the last DSM calculation)
     *
     * This map is built once when a strategy class is first subscribed to, but it is accessed with every price update
     * (in addToMapStrategyClassToUpdatedBidAskForLegs method) to help find the strategy class key, which is then used
     * to find the Bid / Ask price structures to record the price for the updated leg. Therefore it is sorted (TreeMap)
     * to speed up read access, at the expense of write access.
     *
     * All access to this map is inside synchronized methods since it is accessed by (un)subcription threads as well as
     * the IEC thread (channelUpdate)
     */
    private Map<Integer, Integer>  productClassToStrategyClassKey;

    /**
     * This map is somewhat reverse of productClassToStrategyClassKey allowing us to find Underlying and non-Underlying
     * class keys for a Strategy class key. It is used to find which leg classes to unsubscribe from, when the strategy
     * class is unsubscribed.
     *
     * This map can be removed and we can still find the same info by scanning the productClassToStrategyClassKey map
     * for all keys whose mapped value is the given strategy class key, but that would be slower.
     *
     * It is accessed for read just as often as for write, so it is not sorted.
     *
     * All access is in synchronized methods because it can be accessed by multiple (un)subscription threads
     */
    private Map<Integer, DsmSubscriptionKeys> strategyClassKeyToProductClassKeys;

    /**
     * This map is indexed by the Strategy class key with the value being the number of active DSM update subscribers.
     * The count increments with subscriptions, and decrements with unsubscription. The first subscription causes an
     * actual subscription to the market data for legs and product updates for the strategy itself, i.e. new strategies
     * being created (see {@link #subscribeForUpdatesToStrategyAndItsLegs} and {@link #subscribeCurrentMarket}).
     * The last unsubscription actually unsubscribes from the market data and removes the entry from this map.
     *
     * Keeping a count of subscribers per strategy class helps with debugging, but it not necessary. We can change this
     * Map to a Set and start the timer the first time a class key is added, and stop it when last one is removed.
     *
     * This map is updated as often as it is accessed for read, so it is not sorted.
     *
     * Access is in synchronized methods because it is accessed by subscribeForDSM and unsubscribeForDSM methods which
     * can be invoked by multiple threads
     */
    private final Map<Integer, Integer> subscribedStrategyClassKeys;


    /**
     * this is a set of product keys for products which we have received market data updates for. it does not contain
     * stratety product keys, but the product keys of the legs of strategies. The products in this set can be processed
     * in any order, so it is not sorted.
     * All access to this is inside synchronized methods because it is added to by the IEC thread as well as the
     * subsctiption thread (see addToMapStrategyClassToUpdatedBidAskForLegs and updateDSMMaps methods) and removed from
     * by the timer thread (see updateDSM method)
     */
    private final Set<Integer> updatedLegProductKeySet;

    /**
     * there is only one DSMCalculator which subscribes to user market data on behalf of all DSM consumers, such as
     * multiple Market Display tables and OMT tables. to prevent an unsubscription when one market display goes away
     * from interfering with subscriptions by other market displays, TraderAPIImpl keeps track of subscribers. but
     * since there is only one DSMCalculator that subscribeds to the UserMarketDataCacheProxy, we are creating a
     * separate listener proxy for each DSM subscription. we then store the proxy in this map keyed by the DSM consumer
     * that caused the market data subscription (ex. the strategy market display table). During unsubscription, we will
     * use the DSM consumer to find the listener proxy to unsubscribe.
     *
     * all access to this map is inside synchronized methods.
     *
     * @see TraderAPIImpl#removeUserMarketDataListener
     */
    private final Map<EventChannelListener, DSMCalculatorEventChannelListenerProxy> mapDSMListenerToMDListenerProxy;


    static
    {
        loggingProperty   = GUILoggerBusinessProperty.STRATEGY_DSM;
        marketQuertyV3API = APIHome.findMarketQueryAPI();
        productQueryAPI   = APIHome.findProductQueryAPI();
    }

    protected DSMCalculator()
    {
        logger                                 = GUILoggerHome.find();
        timer                                  = null;
        strategiesContainingLeg                = new TreeMap<Integer, Set<Strategy>>();
        mapStrategyClassToUpdatedBidAskForLegs = new TreeMap<Integer, Map<Integer, BidAskPrices>>();
        productClassToStrategyClassKey         = new TreeMap<Integer, Integer>();
        strategyClassKeyToProductClassKeys     = new HashMap<Integer, DsmSubscriptionKeys>();
        subscribedStrategyClassKeys            = new HashMap<Integer, Integer>();
        updatedLegProductKeySet                = new HashSet<Integer>();
        mapDSMListenerToMDListenerProxy        = new HashMap<EventChannelListener, DSMCalculatorEventChannelListenerProxy>();

        // don't start timer here. start when first class subscribes, stop when last one unsubscribes

//        System.out.println(">>>>>>>>>>>>>>>> Shawn " + myClassName + ": instance=" + this);
    }

    /*
     * listeners that wish to receive DSM update messages call this method to register as a listener. what they will
     * receive as event data is a map indexed by the strategy product key, and the value being the calculated DSM bid
     * and Ask, as well as boolean flip and credit / debit flags for the indexed strategy product.
     *
     * starts the DSM timer if this is the first listener across all strategy classes, otherwise it just increments
     * the count of listeners per strategy class
     *
     * NOTE: caller has already verified that spc is a Strategy
     * NOTE: this is called in the execute (non-Swing) method of a thread. see setCurOptionPanel method of
     *       com.cboe.presentation.marketDisplay.CBOETabbedPane which calls applySubscriptions methods of
     *       com.cboe.presentation.marketDisplay.table.MDTableModel which calls overriden subscribe method of
     *       com.cboe.presentation.marketDisplay.table.MDSpreadTableModel which calls this method
     */
    public synchronized void subscribeForDSM(SessionProductClass spc, EventChannelListener clientListener, CountDownLatch completedLatch)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        String methodName       = myClassName + ".subscribeForDSM:";
        String sessionName      = spc.getTradingSessionName();
        int    strategyClassKey = spc.getClassKey();

        if (isDebug())
        {
            Object[] argObj = new Object[6];
            argObj[0] = sessionName;
            argObj[1] = strategyClassKey;
            argObj[2] = spc.getClassSymbol();
            argObj[3] = spc.getKey();
            argObj[4] = spc.getProductType();
            argObj[5] = clientListener;

            logger.debug(methodName, loggingProperty, argObj);
        }

        ChannelKey key = new ChannelKey(ChannelType.DSM_MARKET_DATA,  strategyClassKey);
        EventChannelAdapterFactory.find().addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);

        // we are not subscribed, subscribe for the strategy class, as well as the class keys of its 2 possible leg types
        subscribeForUpdatesToStrategyAndItsLegs(spc, clientListener, completedLatch);
    } // end subscribeForDSM()

     /*
      * listeners that wish to stop receiving DSM update messages call this method to unregister as a listener. what they will
      * receive as event data is a map indexed by the strategy product key, and the value being the calculated DSM bid
      * and Ask, as well as boolean flip and credit / debit flags for the index strategy product.
      *
      * stops the DSM timer if there are no more listeners for ANY strategy class, otherwise it decrements the count of
      * listeners for this strategy class
      *
      * NOTE: caller has already verified that spc is a Strategy
      * NOTE: this is called in the execute (non-Swing) method of a thread. see unsubscribe method of
      *       com.cboe.presentation.marketDisplay.table.MDTableModel which calls overriden unsubscribe method of
      *       com.cboe.presentation.marketDisplay.table.MDSpreadTableModel which calls this method
      * NOTE: the thread in MDTableModel counts down a CountDownLatch when all unsubscribe calls are done. the latch
      *       is not passed down here (unlike the subscribe method)
      */
    public synchronized void unsubscribeForDSM(SessionProductClass spc, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        String methodName       = myClassName + ".unsubscribeForDSM:";
        String sessionName      = spc.getTradingSessionName();
        int    strategyClassKey = spc.getClassKey();

//       new Exception("unsubscribeForDSM called from ...").printStackTrace(System.out);

        if (isDebug())
        {
            Object[] argObj = new Object[6];
            argObj[0] = sessionName;
            argObj[1] = strategyClassKey;
            argObj[2] = spc.getClassSymbol();
            argObj[3] = spc.getKey();
            argObj[4] = spc.getProductType();
            argObj[5] = clientListener;

            logger.debug(methodName, loggingProperty, argObj);
        }

        ChannelKey key = new ChannelKey(ChannelType.DSM_MARKET_DATA, strategyClassKey);
        EventChannelAdapterFactory.find().removeChannelListener(EventChannelAdapterFactory.find(), clientListener, key);

        // unsubscribe from the strategy class, as well as the class keys of its 2 possible leg types
        // note that we are not passing a latch
        unsubscribeForUpdatesToStrategyAndItsLegs(spc, clientListener);
    } // end unsubscribeForDSM()

    private synchronized boolean haveAnySubscribedStrategies()
    {
        return subscribedStrategyClassKeys.size() > 0;
    }

    /*
     * this is used to pretend that all constinuent leg products of the given strategy class have had a market data
     * update, so that the DSM is calculated for all affected strategies. This is used when a new listener is added for
     * a strategy class that already has other listeners. if this is not done, the new listener will only get DSM
     * numbers for strategies whose constituent legs are updated from then on.
     */
    private synchronized void markAllProductsDirty(Integer strategyClassKey)
    {
        Map<Integer, BidAskPrices> mapOfProductToBidAskPrices = mapStrategyClassToUpdatedBidAskForLegs.get(strategyClassKey);

        if (mapOfProductToBidAskPrices != null)
        {
            for (Integer productKey : mapOfProductToBidAskPrices.keySet())
            {
                markProductDirty(productKey);
            }
        }
        else
        {
            logger.alarm("DSMCalculator.markAllProductsDirty: mapOfProductToBidAskPrices is null for strategyClassKey=" + strategyClassKey);
        }
    }

    private synchronized void markProductDirty(Integer productKey)
    {
        // we keep the bid / ask prices for this product even if it is not in any strategies yet, but we don't have to
        // mark it as dirty if it doesn't cause any DSMs to get recalculated.

        if (isLegInAnyStrategy(productKey))
        {
            updatedLegProductKeySet.add(productKey);
        }

        logDebug(myClassName + "markProductDirty: ", " updatedLegProductKeySet.size=" + updatedLegProductKeySet.size());
    }

    /*
     * Given the strategy class, this method calls buildStrategyMaps to find the class keys of the relevant Underlying
     * and non-Underlying (Option, Future, Index) that may be used as legs of strategies of the given strategy class. It
     * then subscribes for market data updates to the two classes. It also subscribes for produtct updates to the
     * strategy class, such as when a new strategy product is created.
     * this is called from execute method of a GUI worker thread
     */
    private synchronized void subscribeForUpdatesToStrategyAndItsLegs(SessionProductClass spc,
                                                                      EventChannelListener dsmListener,
                                                                      CountDownLatch completedLatch)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        String methodName = myClassName + ".subscribeForUpdatesToStrategyAndItsLegs:";

        logInformation(methodName, " SessionProductClass=" + spc);

        String              sessionName             = spc.getTradingSessionName();
        final int           strategyClassKey        = spc.getClassKey();
        SessionKeyContainer strategySessionClassKey = new SessionKeyContainer(sessionName, strategyClassKey);

        final DSMCalculatorEventChannelListenerProxy listenerProxy = new DSMCalculatorEventChannelListenerProxy(this);
        mapDSMListenerToMDListenerProxy.put(dsmListener, listenerProxy);

        DsmSubscriptionKeys dsmSubscriptionKeys = buildStrategyMaps(strategySessionClassKey);
        if (dsmSubscriptionKeys != null)
        {
            // update the maps before subscribing so the market data events will be properly handled rather than being ignored due to not finding their keys in the maps
            addKeysToMaps(spc, dsmSubscriptionKeys);

            // since this method should never be called on the AWT event thread, avoiding synchronization issues by doing all the subscriptions on this thread
            subscribeCurrentMarket(dsmSubscriptionKeys.getSessionName(), dsmSubscriptionKeys.getNonUnderlyingClassKey(), listenerProxy);
            subscribeCurrentMarket(dsmSubscriptionKeys.getUnderlyingSessionName(), dsmSubscriptionKeys.getUnderlyingClassKey(), listenerProxy);

            try
            {
                // subscribe for new strategy products being created while we are subscribed for markete data
                // todo: look into calling subscribeProductsForSession() instead of session-less call, but first make sure it handles Strategies correctly
                productQueryAPI.subscribeProductsByClass(strategyClassKey, listenerProxy);
            }
            catch (UserException e)
            {
                DefaultExceptionHandlerHome.find().process(e);
            }
        }
        else
        {
            logger.alarm(methodName + " dsmSubscriptionKeys is null for strategyClassKey=" + strategyClassKey);
        }

        completedLatch.countDown();
    } // end subscribeForUpdatesToStrategyAndItsLegs()

    // this is called from execute method of a GUI worker thread
    private synchronized void unsubscribeForUpdatesToStrategyAndItsLegs(SessionProductClass spc, EventChannelListener dsmListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        String methodName = myClassName + ".unsubscribeForUpdatesToStrategyAndItsLegs:";

        logInformation(methodName, " SessionProductClass=" + spc);

        int strategyClassKey = spc.getClassKey();

        DsmSubscriptionKeys dsmSubscriptionKeys = strategyClassKeyToProductClassKeys.get(strategyClassKey);

        if (dsmSubscriptionKeys != null)
        {
            DSMCalculatorEventChannelListenerProxy listenerProxy = mapDSMListenerToMDListenerProxy.remove(dsmListener);
            if (listenerProxy == null)
            {
                logger.alarm(methodName + " can't find listenerProxy for dsmListener=" + dsmListener);
            }
            else
            {
//                System.out.println(methodName + " found listenerProxy=" + listenerProxy
//                                 + " for delegate=" + listenerProxy.getEventChannelListenerDelegate());

                // Unsubscribe market data updates. note that we are not passing a latch
                marketQuertyV3API.unsubscribeUserMarketData(dsmSubscriptionKeys.getSessionName(),
                                                            dsmSubscriptionKeys.getNonUnderlyingClassKey(),
                                                            listenerProxy);
                marketQuertyV3API.unsubscribeUserMarketData(dsmSubscriptionKeys.getUnderlyingSessionName(),
                                                            dsmSubscriptionKeys.getUnderlyingClassKey(),
                                                            listenerProxy);

                // unsubscribe from new strategy products being created while we are subscribed for markete data
                productQueryAPI.unsubscribeProductsByClass(strategyClassKey, listenerProxy);

                removeKeysFromMaps(spc, dsmSubscriptionKeys);
            } // else listenerProxy != null
        } // end if (dsmSubscriptionKeys != null)
        else
        {
            logger.alarm(methodName + " dsmSubscriptionKeys is null for strategyClassKey=" + strategyClassKey);
        }
    } // end unsubscribeForUpdatesToStrategyAndItsLegs()

    private void addKeysToMaps(SessionProductClass spc, DsmSubscriptionKeys dsmSubscriptionKeys)
    {
        String methodName = myClassName + ".addKeysToMaps:";

        int strategyClassKey = spc.getClassKey();

        productClassToStrategyClassKey.put(dsmSubscriptionKeys.getNonUnderlyingClassKey(), strategyClassKey);
        productClassToStrategyClassKey.put(dsmSubscriptionKeys.getUnderlyingClassKey()   , strategyClassKey);
        strategyClassKeyToProductClassKeys.put(strategyClassKey, dsmSubscriptionKeys);

        Integer count = subscribedStrategyClassKeys.get(strategyClassKey);
        if (count == null  ||  count == 0)  // count is removed when zero, but to be on the safe side
        {
            subscribedStrategyClassKeys.put(strategyClassKey, 1);

            if (subscribedStrategyClassKeys.size() == 1)
            {
                logInformation(methodName, "started timer after first subscription, strategy=" + spc.getClassSymbol());

                startTimer();
            }
            else
            {
                logInformation(methodName, "timer keeps running, strategy=" + spc.getClassSymbol() + ", but not first subscription");
            }
        }
        else
        {
            // we are already subscribed to this strategy class and it's legs
            subscribedStrategyClassKeys.put(strategyClassKey, count + 1);

            logInformation(methodName, "same timer, not the first subscription for this class=" + spc.getClassSymbol()
                          + ", count after=" + subscribedStrategyClassKeys.get(strategyClassKey));

            // not likely scenario in production, but if we don't publish existing DSM numbers for this class,
            // this subscriber will never get any DSM data if none of the legs tick anymore. We'll mark all
            // products of this class as dirty so that all DSM's are recalculated and republished.
            //
            // the following alternative will re-subscribe and rebuild the maps, which is more expensive.
            //
            //    subscribeForUpdatesToStrategyAndItsLegs(spc);
            //
            // the following alternative will only update strategies whose leg(s) have been updated since last
            // DSM publish
            //
            //   updateDSM();

            markAllProductsDirty(spc.getClassKey());
        }

//        System.out.println("#### Shawn " + methodName + " size are subscribedStrategyClassKeys="
//                         + subscribedStrategyClassKeys.size() + " strategyClassKeyToProductClassKeys="
//                         + strategyClassKeyToProductClassKeys.size() + " productClassToStrategyClassKey="
//                         + productClassToStrategyClassKey.size());
    }

    private void removeKeysFromMaps(SessionProductClass spc, DsmSubscriptionKeys dsmSubscriptionKeys)
    {
        String methodName = myClassName + ".removeKeysFromMaps:";

        int strategyClassKey = spc.getClassKey();

        Integer count = subscribedStrategyClassKeys.get(strategyClassKey);
        if (count != null)
        {
            if ((count - 1) == 0)
            {
                Object removedObj;
                removedObj = subscribedStrategyClassKeys.remove(strategyClassKey);
//                System.out.println("##### " + methodName + " subscribedStrategyClassKeys.remove(" + strategyClassKey + ")=" + removedObj);

                // TODO: if it provides a great advantage, we can remove all knowledge of this strategy class from the
                //       maps below. but keeping them may prevent null pointer exceptions due to minor bugs.
                //

                removedObj = productClassToStrategyClassKey.remove(dsmSubscriptionKeys.getNonUnderlyingClassKey());
//                System.out.println("##### " + methodName + " productClassToStrategyClassKey.remove(" + dsmSubscriptionKeys.getNonUnderlyingClassKey() + ")=" + removedObj);
                removedObj = productClassToStrategyClassKey.remove(dsmSubscriptionKeys.getUnderlyingClassKey());
//                System.out.println("##### " + methodName + " productClassToStrategyClassKey.remove(" + dsmSubscriptionKeys.getUnderlyingClassKey() + ")=" + removedObj);

                removedObj = strategyClassKeyToProductClassKeys.remove(strategyClassKey);
//                System.out.println("##### " + methodName + " strategyClassKeyToProductClassKeys.remove(" + strategyClassKey + ")=" + removedObj);

                if (!haveAnySubscribedStrategies())
                {
                    // this is the last unsubscriber, stop the timer
                    stopTimer();

                    logInformation(methodName, "stopped timer after last unsubscription strategy=" + spc.getClassSymbol());
                }
            }
            else
            {
                // we now have one less subscriber
                subscribedStrategyClassKeys.put(strategyClassKey, count - 1);

                logInformation(methodName, "not the last unsubscription, timer keeps running, new count=" + (count - 1));
            }

            logInformation(methodName, "strategy=" + spc.getClassSymbol() + ", new subscription count= "
                        + subscribedStrategyClassKeys.get(strategyClassKey));
        }
        else
        {
            // TODO there is currently a bug whereby unsubscribe is called twice when we remove a tab from
            //      market display. this is the case for both strategies and option tabs. probably being called
            //      once when we remove the tab and once when we switch to a new tab
            logger.alarm(methodName + "we are not subscribed to strategy=" + spc.getClassSymbol() + " and its legs");
        }

//        System.out.println("##### " + methodName + " sizes are subscribedStrategyClassKeys="
//                         + subscribedStrategyClassKeys.size() + " strategyClassKeyToProductClassKeys="
//                         + strategyClassKeyToProductClassKeys.size() + " productClassToStrategyClassKey="
//                         + productClassToStrategyClassKey.size());
    }

    /*
     * given a product class key, and the session name, it first gets a snapshot of the market data for all products of
     * the product class, and then subscribes for market data updates to these products, which will be processed in the
     * channelUpdate() method.
     */
    private void subscribeCurrentMarket(String tradingSessionName, int classKey, EventChannelListener listenerProxy)
    {
        String className = myClassName + ".subscribeCurrentMarket";
        try
        {
            UserMarketDataStruct[] marketData = marketQuertyV3API.getUserMarketData(tradingSessionName, classKey, listenerProxy);

            logDebug(className + ".execute: ", "tradingSessionName=" + tradingSessionName
                    + ", marketData.length=" + marketData.length);

            for (int i = 0; i < marketData.length; ++i)
            {
                UserMarketDataStruct userMarketDataStruct = marketData[i];
                logDebug(className + ".execute: ", "marketData[" + i + "].classKey="
                        + userMarketDataStruct.productKeys.classKey + ", prodKey=" + userMarketDataStruct.productKeys.productKey);

                storeProductInfoForDSM(userMarketDataStruct);
            }
        }
        catch(UserException e)
        {
            DefaultExceptionHandlerHome.find().process(e, "Unable to get Market Data for session=" +
                    tradingSessionName + " classKey=" + classKey);
        }
    }

    /**
     * this is called when the timer expires to calculate DSM for all strategy products where at least one leg of the
     * strategy has been updated since the last timer event. this runs on the timer thread.
     *
     * It loops over all subscribed strategy classes, and gets the map of products (i.e. legs) that have updated market
     * data. The said market data is in the DsmParameterStruct value of the map keyed by the product key. As market
     * data updates arrive in the channelUpdate method for each product, the DsmParameterStruct will be initialized
     * with the Bid and Ask prices for the product {@link #storeProductInfoForDSM}. Then here in this method, if the
     * updated product is a leg in any strategies of interest, the structure  will be updated with the leg ratio, side,
     * and Product reference, and is then passed on to StrategyUtility.calculateDSM() method to calculate the DSM.
     *
     * The calculated DSMs for all strategies for all subscribed strategy classes are collected into a single map keyed
     * by the strategy product key, and is then published to all listeners.
     */
    private synchronized void updateDSM()
    {
        String methodName = myClassName + ".updateDSM:";
        boolean isDebugOn = isDebug();

        for (Integer strategyClassKey : mapStrategyClassToUpdatedBidAskForLegs.keySet())
        {
            // given a strategy class, get a map of all non-strategy products, and the corresponding Bid and Ask prices
            // for all products that may appear as legs in strategies of this strategy class
            Map<Integer, BidAskPrices> mapOfProductToBidAskPrices = mapStrategyClassToUpdatedBidAskForLegs.get(strategyClassKey);

            // check if debug logging is enabled before concat'ing strings
            if (isDebugOn && mapOfProductToBidAskPrices.size() > 0)
            {
                logDebug(methodName, "for strategyClassKey=" + strategyClassKey
                        + ", mapOfProductToBidAskPrices.size=" + mapOfProductToBidAskPrices.size());
            }

            Set<Strategy> strategiesToUpdate = new HashSet<Strategy>();

            for (Integer productKey : mapOfProductToBidAskPrices.keySet())
            {
                // for every updated product
                if (updatedLegProductKeySet.contains(productKey))
                {
                    // get the list of strategies containing this product as a leg
                    Set<Strategy> strategies = getStrategies(productKey);
                    if (strategies != null)
                    {
                        // add the strategies containing this leg to the list of strategies whose DSM is to be calculated
                        strategiesToUpdate.addAll(strategies);
                    }
                    // remove the updated product. this is equivalent to clearing the dirty flag on this product
                    updatedLegProductKeySet.remove(productKey);
                }
                // check if debug logging is enabled before concat'ing strings
                if (isDebugOn && strategiesToUpdate.size() > 0)
                {
                    logDebug(methodName, "after productKey=" + productKey
                            + ", strategiesToUpdate.size=" + strategiesToUpdate.size());
                }
            }

            // this map is indexed by strategy product key, with the value being a structure containing calculated DSM data.
            Map<Integer, DsmBidAskStruct> updatedDSMByProduct = new HashMap<Integer, DsmBidAskStruct>();

            // for all strategies whose DSM is to be calculated (because one or more of its legs have been udpated)
            for (Strategy strategy : strategiesToUpdate)
            {
                StrategyLeg       [] strategyLegs       = strategy.getStrategyLegs();
                int                  legCount           = strategyLegs.length;
                DsmParameterStruct[] dsmParameterStruct = new DsmParameterStruct[legCount];

                // check if debug logging is enabled before concat'ing strings
                if (isDebugOn)
                {
                    logDebug(methodName, "for strategy  key=" + strategy.getProductKey() + ", leg cnt=" + legCount);
                }

                boolean allLegsArePriced = true;
                for (int i = 0; allLegsArePriced  &&  i < legCount; ++i)
                {
                    // check if debug logging is enabled before concat'ing strings
                    if (isDebugOn)
                    {
                        logDebug(methodName, "for leg # " + i + ", prodKey=" + strategyLegs[i].getProductKey());
                    }

                    BidAskPrices bidAskPrices = mapOfProductToBidAskPrices.get(strategyLegs[i].getProductKey());
                    if (bidAskPrices != null)
                    {
                        dsmParameterStruct[i] = StrategyUtility.createDsmParameterStruct(strategyLegs[i],
                                                                                         bidAskPrices.bidPrice, bidAskPrices.askPrice);
                    }
                    else
                    {
                        // check if debug logging is enabled before concat'ing strings
                        if (isDebugOn)
                        {
                            logDebug(methodName, "dsmParameterStruct[" + i + "] is NULL skipping this strategy");
                        }
                        allLegsArePriced = false;
                    }
                } // for legCount

                if (allLegsArePriced)
                {
                    // calculate the DSM for the given strategy product and add it to the map to be published
                    DsmBidAskStruct dsmBidAskStruct = StrategyUtility.calculateDSM(strategy, dsmParameterStruct);

                    updatedDSMByProduct.put(strategy.getProductKey(), dsmBidAskStruct);

                    // check if debug logging is enabled before concat'ing strings
                    if (isDebugOn)
                    {
                        logDebug(methodName, "calculated DSM for strProdKey=" + strategy.getProductKey() + ", bid / ask="
                                + PriceFactory.create(dsmBidAskStruct.getBid()).toDouble() + " / "
                                + PriceFactory.create(dsmBidAskStruct.getAsk()).toDouble());
                    }
                }
            } // for strategiesToUpdate

            if (updatedDSMByProduct.size() > 0)
            {
                publishDSM(strategyClassKey, updatedDSMByProduct);
            }
        } // for strategyClassKey

        // when all is done, updatedLegProductKeySet should be empty, otherwise we are holding on to dirtied products
        // whose affected strategy DSM we are no longer interested in calculating. log an alarm and clear it.
        if (updatedLegProductKeySet.size() > 0)
        {
            logDebug(methodName, updatedLegProductKeySet.size() + " dirtied products left unprocessed." +
                                 ". will clear it. first productKey=[" + updatedLegProductKeySet.iterator().next() + "]");
            updatedLegProductKeySet.clear();
        }

        // don't clear mapStrategyClassToUpdatedBidAskForLegs or its contained Map<Integer, BidAskPrices>, or else we
        // will los prior price updates. only clear dirtied product set.
    } // end method updateDSM()

    public void publishDSM(Integer strategyClassKey,  Map<Integer, DsmBidAskStruct> updatedDSMByProduct)
    {
        // it is up to the receiver of DSM_MARKET_DATA to clear out and free updatedDSMByProduct
        // receiver must be able to handle emptry map being published

        ChannelKey   key   = new ChannelKey(ChannelType.DSM_MARKET_DATA, strategyClassKey);
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, updatedDSMByProduct);
        EventChannelAdapterFactory.find().dispatch(event);
    }

    /*
     * This method is called by storeProductInfoForDSM method, which is called by channelUpdate method when a user
     * market data update is received for a product, as well as after the initial query for all products just prior
     * to subscription (see subscribeCurrentMarket method)
     *
     * Given the product key and the class key of the product, it finds the corresponding strategy class key, which it
     * then uses to find the map of leg products and their corresponding Bid / Ask values received in the channelUpdate.
     * if the map does not exist yet, it is created. the product's updated bid / ask is stored in the mapped indexed
     * by the product key. the updated leg product key is then marked as dirty, which is scanned when DSM timer fires
     * to re-calculate DSM for all strategies affected by dirtied products
     */
    private synchronized void addToMapStrategyClassToUpdatedBidAskForLegs(Integer productClassKey, int productKey, BidAskPrices bidAskPrices)
    {
        if (productClassKey != null)
        {
            Integer strategyClassKey = productClassToStrategyClassKey.get(productClassKey);
            if (strategyClassKey != null)
            {
                Map<Integer, BidAskPrices> mapOfProductToBidAskPrices = mapStrategyClassToUpdatedBidAskForLegs.get(strategyClassKey);
                if (mapOfProductToBidAskPrices == null)
                {
                    mapOfProductToBidAskPrices = new TreeMap<Integer, BidAskPrices>();
                    mapStrategyClassToUpdatedBidAskForLegs.put(strategyClassKey, mapOfProductToBidAskPrices);
                }
                mapOfProductToBidAskPrices.put(productKey, bidAskPrices);    // replace if it exists

                markProductDirty(productKey);

                logDebug(myClassName + "mapStrategyClassToUpdatedBidAskForLegs: ", "productClassKey=" + productClassKey
                       + ", strategyClassKey=" + strategyClassKey + ", mapOfProductToBidAskPrices.size="
                       + mapOfProductToBidAskPrices.size());
            }
            else
            {
                logger.alarm(myClassName + ".addToMapStrategyClassToUpdatedBidAskForLegs: strategyClassKey is null, productClassKey=" + productClassKey + ", productKey=" + productKey);
            }
        }
        else
        {
            logger.alarm(myClassName + ".addToMapStrategyClassToUpdatedBidAskForLegs: productClassKey is null, productKey=" + productKey);
        }
    }

    /*
     * implements EventChannelListener (actually ChannelListener)
     * it handles user market data updates for subscribed underlying and non-underlying products (not the strategy),
     * and if the said product is in any strategies, the Bid and Ask will be added to a DsmParameterStruct and placed
     * in a map that will be processed when the DSM timer expires.
     * it also handles product updates, which are fired when a new strategy product is created, so that the maps of
     * products to afftected strategies can be updated in updateDSMMaps method. The said maps are actually recreated
     * the same way they were when the strategy class was first subscribed.
     */
    public void channelUpdate(ChannelEvent event)
    {
        String methodName  = myClassName + ".channelUpdate: ";
        int    channelType = ((ChannelKey)event.getChannel()).channelType;
        Object eventData   = event.getEventData();

        switch(channelType)
        {
            // ignore if the product is not in any of the strategies, else mark it as dirty
            case ChannelType.CB_USER_MARKET_DATA:
            case ChannelType.CB_USER_MARKET_DATA_BY_PRODUCT:
                UserMarketDataStruct summary = (UserMarketDataStruct)eventData;

                if (summary.productKeys.productType != ProductTypes.STRATEGY)
                {
                    if(isDebug())
                    {
                        Object key = ((ChannelKey)event.getChannel()).key;
                        if (key instanceof SessionKeyWrapper)
                        {
                            SessionKeyWrapper classKeyContainer = (SessionKeyWrapper) key;

                            logDebug(methodName, "session / key=" + classKeyContainer.getSessionName() + " / "
                                    + classKeyContainer.getKey() + ", classKey=" + summary.productKeys.classKey
                                    + ", productKey=" + summary.productKeys.productKey);
                        }
                    }

                    if (haveAnySubscribedStrategies())
                    {
                        storeProductInfoForDSM(summary);
                    }
                    else
                    {
                        logDebug(methodName, "no subscribed strategies, ignore market data");
                    }
                }
                // else System.out.println(methodName + "ProductTypes.STRATEGY don't know what to do yet ??");
                break;
            case ChannelType.CB_PRODUCT_UPDATE_BY_CLASS:  // case ChannelType.CB_PRODUCT_UPDATE:
                // a new strategy is created, update the maps of legs and strategies

                SessionStrategy[] sessionStrategy = new SessionStrategy[1];
                sessionStrategy[0] =(SessionStrategy)eventData;
                updateDSMMaps(sessionStrategy[0]);

                logInformation(methodName, "CB_PRODUCT_UPDATE_BY_CLASS. update DSM maps. sessionStrategy=" + sessionStrategy[0]);
                break;
            default:
                logDebug(methodName, "default ignored. channelType=" + channelType);
                // No action
                break;
        }
    } // end method channelUpdate()

    /*
     * this method is called when market data is recieved for a non-strategy product, resulting in the product's Bid and
     * Ask prices being stored in a map indexed by the product key. if the product happens to be a leg in any strategies,
     * then the product is also marked dirty so that when the DSM timer fires all affected strategies will have their
     * DSM re-calculated.
     */
    private void storeProductInfoForDSM(UserMarketDataStruct userMarketDataStruct)
    {
        String className = myClassName + ".storeProductInfoForDSM:";

        int productKey = userMarketDataStruct.productKeys.productKey;
        Price bidPrice = PriceFactory.create(userMarketDataStruct.currentMarket.bidPrice);
        Price askPrice = PriceFactory.create(userMarketDataStruct.currentMarket.askPrice);
        if (bidPrice.isValuedPrice()  &&  askPrice.isValuedPrice())
        {
            // 0.0 are valid prices
            double bid = bidPrice.toDouble();
            double ask = askPrice.toDouble();
            logDebug(className , "bid / ask=" + bid + " / " + ask);

            BidAskPrices bidAskPrices = new BidAskPrices(userMarketDataStruct.currentMarket.bidPrice,
                                                         userMarketDataStruct.currentMarket.askPrice);
            // product, side, and ratio are set later

            addToMapStrategyClassToUpdatedBidAskForLegs(userMarketDataStruct.productKeys.classKey, productKey, bidAskPrices);
        }

        if (isDebug() && !isLegInAnyStrategy(productKey))
        {
            // if we ignore market data for products that are currently not in any strategies, then if a strategy is
            // created on the fly while we are subscribed to the class, we can not calculate the DSM, or worse calculate
            // the wrong value, until all legs of the strategy tick. so we keep market data for all products, but we
            // don't have to re-calc any DSM's due to this unused product update

            logDebug(className, "product " + productKey
                   + " not in any strategy, but keeping bid/ask for newly created strategies");
        }
    } // end storeProductInfoForDSM()

    // a new Strategy product has been added. update the DSM related maps
    private synchronized void updateDSMMaps(SessionStrategy sessionStrategy)
    {
        if (sessionStrategy != null)
        {
            Integer strategyClassKey = sessionStrategy.getProductKeysStruct().classKey;
            Map<Integer, BidAskPrices> mapOfProductToBidAskPrices = mapStrategyClassToUpdatedBidAskForLegs.get(strategyClassKey);
            if (mapOfProductToBidAskPrices != null)
            {
                SessionStrategyLeg[] sessionStrategyLegs = sessionStrategy.getSessionStrategyLegs();
                for (SessionStrategyLeg sessionStrategyLeg : sessionStrategyLegs)
                {
                    markProductDirty(sessionStrategyLeg.getProductKey());
                } // end for
            }
            else
            {
                logDebug(myClassName + ".updateDSMMaps:", " mapOfProductToBidAskPrices is null for strategy classKey=" + strategyClassKey);
            }

            // add the strategy to all the Sets looked up using product key of each of its legs
             addToStrategySetPerLeg(sessionStrategy);
        } // end if (strategyClassKey != null)
        else
        {
            logger.alarm(myClassName + ".updateDSMMaps: sessionStrategy is null");
        }
    } // end updateDSMMaps()

    private void stopTimer()
    {
        if (timer != null)
        {
            timer.cancel();
        }
        timer = null;
    }

    private Timer startTimer()
    {
        if (timer == null)
        {
            timer = new Timer("DSM_Timer");
            int updateDelay = getDSMUpdateDelay();
            timer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    updateDSM();
                }
            }, updateDelay, updateDelay);
        }
        return timer;
    }

    /**
     * Reads the property file for a time in milliseconds to wait between DSM calculations. If the property does not
     * exist, it uses a default value hard coded in this class.
     *
     * @return the time in milliseconds to sleep between DSM recalucations.
     */
    private int getDSMUpdateDelay()
    {
        String methodName  = myClassName + ".getDSMUpdateDelay:";
        int    updateDelay = DEFAULT_UPDATE_DELAY;
        if (AppPropertiesFileFactory.isAppPropertiesAvailable())
        {
            String value = AppPropertiesFileFactory.find().getValue(PROPERTIES_SECTION_NAME, DSM_UPDATE_TIMER_KEY_NAME);

            logDebug(methodName, DSM_UPDATE_TIMER_KEY_NAME + '=' + value);

            if (value != null)
            {
                try
                {
                    updateDelay = Integer.parseInt(value);
                    updateDelay = Math.max(updateDelay, MIN_UPDATE_DELAY);
                    updateDelay = Math.min(updateDelay, MAX_UPDATE_DELAY);
                }
                catch(NumberFormatException e)
                {
                    logger.exception(methodName, "Error parsing " + DSM_UPDATE_TIMER_KEY_NAME + ", value =" + value, e);
                }
            }
        }
        else
        {
            updateDelay = DEFAULT_UPDATE_DELAY;
        }

        logDebug(methodName, "updateDelay = " + updateDelay);

        return updateDelay;
    } // end getDSMUpdateDelay()

    /*
     * given the class key for a strategy class, it finds the class key of the Underlying, as well as the Option or
     * Future whose proudcts comprise legs of the stretagey proudcts belonging to the passed in strategy class
     */
    public DsmSubscriptionKeys buildStrategyMaps(SessionKeyWrapper strategySessionKeyWrapper)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        String              methodName          = myClassName + ".buildStrategyMaps: ";
        int                 strategyClassKey    = strategySessionKeyWrapper.getKey();
        DsmSubscriptionKeys dsmSubscriptionKeys = null;

        try
        {
            String strategySessionName = strategySessionKeyWrapper.getSessionName();
            ProductClass strategyProductClass = APIHome.findProductQueryAPI().getProductClassByKey(strategyClassKey); // throws NotFoundException

            logDebug(methodName, "strategyClassKey=[" + strategyClassKey + "] sessionName=[" + strategySessionName
                   + "]" + " classSymbol=[" + strategyProductClass.getClassSymbol() + "]");

            if (strategyProductClass.getProductType() == ProductTypes.STRATEGY)
            {
                Product[] strategyProducts = APIHome.findProductQueryAPI().getAllProductsForClass(strategyClassKey, true);
                if (strategyProducts.length > 0)
                {
                    // get the 2 possible class keys and session names for the underlying and the option/future legs
                    dsmSubscriptionKeys = getLegClassKeys(strategySessionName, strategyProductClass);

                    logDebug(methodName, "strategyProducts.length=" + strategyProducts.length + ", dsmSubscriptionKeys: "
                           + dsmSubscriptionKeys.getUnderlyingSessionName() + ", " + dsmSubscriptionKeys.getStrategyClassKey()
                           + ", " + dsmSubscriptionKeys.getUnderlyingClassKey() + ", " + dsmSubscriptionKeys.getSessionName()
                           + ", " + dsmSubscriptionKeys.getNonUnderlyingClassKey());

                    for (Product strategyProduct : strategyProducts)
                    {
                        addToStrategySetPerLeg((Strategy) strategyProduct);
                    }

                    if (isDebug())
                    {
                        Map<Integer, Set<Strategy>> mapCopy;
                        synchronized(strategiesContainingLeg)
                        {
                            mapCopy = new HashMap<Integer, Set<Strategy>>(strategiesContainingLeg);
                        }
                        dump(mapCopy);
                    }
                } // end if products > 0
                else
                {
                    logger.alarm(methodName + "didn't find any products for strategyClassKey=" + strategyClassKey);
                }
            } // end if strategy
            else
            {
                logger.alarm(methodName + "expected strategy but got getProductType=" + strategyProductClass.getProductType());
            }
        }
        catch(NotFoundException nfe)
        {
            logger.exception("can't find ProductClass for strategyClassKey=" + strategyClassKey, nfe);
        }

        return dsmSubscriptionKeys;
    } // end method buildStrategyMaps()


    /*
     * given a Strategy product, it adds the strategy to the Set of strategies mapped to each of its legs. in other
     * words, given the product key of each of its legs, the strategy can be found in a Set retrieved using the leg
     * product key.
     */
    public synchronized void addToStrategySetPerLeg(Strategy strategy)
    {
        boolean       isDebug      = isDebug();
        StringBuilder debugStr     = null;
        StrategyLeg[] strategyLegs = strategy.getStrategyLegs();

        if (isDebug)
        {
            debugStr = new StringBuilder();
            debugStr.append("for strategy=[").append(strategy).append("] legs are:\n");
        }

        for (StrategyLeg strategyLeg : strategyLegs)
        {
            int prodKey = strategyLeg.getProductKey();
            Set<Strategy> strategySet = strategiesContainingLeg.get(prodKey);
            if (strategySet == null)
            {
                strategySet = new HashSet<Strategy>(5);
                strategiesContainingLeg.put(prodKey, strategySet);
            }
            if (! strategySet.contains(strategy))   // TODO just go ahead and add without checking
            {
                strategySet.add(strategy);
            }
            if (isDebug)
            {
                debugStr.append("        leg=[").append(strategyLeg).append("]\n");
            }
        }

        if(isDebug)
        {
            logDebug(myClassName + ".addToStrategySetPerLeg: ", debugStr.toString());
        }
    } // end addToStrategySetPerLeg()

    /*
     * given a product key, it returns a Set of Strategy products that contain the given product as a leg
     * this is called to get the list of strategies whose DSM needs to be calculated because the current market
     * of he passed in leg has changed
     */
    public synchronized Set<Strategy> getStrategies(Integer productKey)
    {
        return strategiesContainingLeg.get(productKey);
    }

    /*
     * given a Strategy class key and its session name, returns a structure containing 2 class keys and their
     * corresponding session names, one for the Underlying, and one for the Option or Future, depending on the
     * sessionName passed in
    */
    private DsmSubscriptionKeys getLegClassKeys(String sessionName, ProductClass strategyProductClass)
    {
        DsmSubscriptionKeys dsmSubscriptionKeys = new DsmSubscriptionKeys(sessionName, strategyProductClass.getClassKey());

        try
        {
            short productType = 0;
            // look up the non-Strategy type for this session (e.g., Option, Future, etc.)
            ProductType[] sessionProductTypes = APIHome.findProductQueryAPI().getProductTypesForSession(sessionName);
            for(ProductType t : sessionProductTypes)
            {
                if(t.getType() != ProductTypes.STRATEGY)
                {
                    productType = t.getType();
                    break;
                }
            }

            if(productType != 0)
            {
                String productSymbol = strategyProductClass.getClassSymbol();
                SessionProductClass legSpc = APIHome.findProductQueryAPI().getClassBySymbolForSession(sessionName, productType, productSymbol);

                dsmSubscriptionKeys.setNonUnderlyingClassKey(legSpc.getClassKey());

                Product underlyingProduct = strategyProductClass.getUnderlyingProduct();
                if (underlyingProduct != null)
                {
                    dsmSubscriptionKeys.setUnderlyingClassKey(underlyingProduct.getProductKeysStruct().classKey);
                    dsmSubscriptionKeys.setUnderlyingSessionName(legSpc.getUnderlyingSessionName());
                }
            }
        } // end try
        catch(UserException ex)
        {
            logger.exception("Error trying to find leg classKeys for strategy", ex);
        }
        return dsmSubscriptionKeys;
    } // end getLegClassKeys()

    /*
     * returns True if the passed in product key is a leg in any strategy
     */
    public boolean isLegInAnyStrategy(Integer productKey)
    {
        return getStrategies(productKey) != null;   // no need to add "&& getStrategies(productKey).size() > 0"
    }

    public void dump(Map<Integer, Set<Strategy>> legToStrategiesMap)
    {
        StringBuilder debugStr = new StringBuilder();
        debugStr.append("\n\nlegToStrategiesMap.size=" + legToStrategiesMap.size());
        for (Integer legProductKey : legToStrategiesMap.keySet())
        {
            Set<Strategy> strategies = legToStrategiesMap.get(legProductKey);
            debugStr.append("\n    for legProductKey=[").append(legProductKey).append("] strategies are:")
                    .append("\n         strategies.size=").append(strategies.size()).append(": ");
            for (Strategy strategy : strategies)
            {
                debugStr.append(strategy).append(", ");
            }
            debugStr.append("\n");
        }
        debugStr.append("\n");
        logDebug(myClassName + ".dump:", debugStr.toString());
    } // end dump()

    private void logDebug(String methodName, String msg)
    {
        if (isDebug())
        {
            logger.debug(methodName, loggingProperty, msg);
        }
    }

    private boolean isDebug()
    {
        return logger.isDebugOn() && logger.isPropertyOn(loggingProperty);
    }

    private void logInformation(String methodName, String msg)
    {
        if (logger.isInformationOn() && logger.isPropertyOn(loggingProperty))
        {
            logger.information(methodName, loggingProperty, msg);
        }
    }


    // inner class ////////////////////////////////////////////////////////////////
    /**
     * since there is only one DSMCalculator object, which is the ultimate consumer of user market data, this proxy
     * class allows us to create a unique listener object to subscribe for user market data of behalf of each DSM consumer,
     * such as strategy market displays and OMT screens.
     *
     * @see TraderAPIImpl#removeUserMarketDataListener
     */
    private class DSMCalculatorEventChannelListenerProxy implements EventChannelListener
    {
        private EventChannelListener eventChannelListenerDelegate;

        public DSMCalculatorEventChannelListenerProxy(EventChannelListener delegate)
        {
//            System.out.println("??? Shawn DSMCalculatorEventChannelListenerProxy " + this + " is proxy for " + delegate);
            eventChannelListenerDelegate = delegate;
        }

        public void channelUpdate(ChannelEvent event)
        {
            eventChannelListenerDelegate.channelUpdate(event);
        }

        public EventChannelListener getEventChannelListenerDelegate()
        {
            return eventChannelListenerDelegate;
        }
    }
}

