//
// -----------------------------------------------------------------------------------
// Source file: MarketMakerAPIImpl.java
//
// PACKAGE: com.cboe.presentation.api;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import com.cboe.idl.cmi.Quote;
import com.cboe.idl.cmi.UserTradingParameters;
import com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer;
import com.cboe.idl.cmiCallback.CMIRFQConsumer;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.idl.cmiQuote.ClassQuoteResultStruct;
import com.cboe.idl.cmiQuote.ClassQuoteResultStructV2;
import com.cboe.idl.cmiQuote.ClassQuoteResultStructV3;
import com.cboe.idl.cmiQuote.QuoteBustReportStruct;
import com.cboe.idl.cmiQuote.QuoteDetailStruct;
import com.cboe.idl.cmiQuote.QuoteEntryStruct;
import com.cboe.idl.cmiQuote.QuoteEntryStructV3;
import com.cboe.idl.cmiQuote.QuoteEntryStructV4;
import com.cboe.idl.cmiQuote.QuoteFilledReportStruct;
import com.cboe.idl.cmiQuote.QuoteRiskManagementProfileStruct;
import com.cboe.idl.cmiQuote.UserQuoteRiskManagementProfileStruct;
import com.cboe.idl.cmiV7.UserSessionManagerV7;
import com.cboe.idl.cmiV9.UserSessionManagerV9;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;

import com.cboe.interfaces.presentation.api.MarketMakerAPI;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.rfq.RFQ;
import com.cboe.interfaces.presentation.user.Role;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;

import com.cboe.presentation.api.express.TraderV4APIImpl;
import com.cboe.presentation.common.instruction.ScheduledInstructionThrottle;
import com.cboe.presentation.common.instruction.ScheduledInstructionThrottleFactory;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.consumers.callback.QuoteStatusConsumerFactory;
import com.cboe.consumers.callback.RFQConsumerFactory;
import com.cboe.consumers.callback.SubscriptionManagerFactory;
import com.cboe.domain.util.SessionKeyContainer;

/**
 * This class is the cache implementation of the MarketMaker interface to the CAS.
 *
 * @author Derek T. Chambers-Boucher
 * @author Michael Pyatetsky
 * @version 2/15/2000
 */

public class MarketMakerAPIImpl extends TraderV4APIImpl implements MarketMakerAPI
{
    protected Quote quoteQuery;
    protected UserTradingParameters userTradingParametersQuery;
    protected QuoteCache quoteCache;

    protected CMIQuoteStatusConsumer quoteStatusConsumer;
    protected CMIRFQConsumer rfqConsumer;
    protected ScheduledInstructionThrottle throttle;

    protected com.cboe.idl.cmiV2.Quote quoteQueryV2;
    protected com.cboe.idl.cmiV3.Quote quoteQueryV3;
    protected com.cboe.idl.cmiV7.Quote quoteQueryV7;
    
    protected com.cboe.idl.cmiCallbackV2.CMILockedQuoteStatusConsumer lockedQuoteStatusConsumerV2;
    protected com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer quoteStatusConsumerV2;
    protected com.cboe.idl.cmiCallbackV2.CMIRFQConsumer rfqConsumerV2;

    public static final boolean DEFAULT_INCLUDE_USER_EVENT_ACTION = true;

    /**
     * Default constructor
     */
    public MarketMakerAPIImpl()
    {
        super();
    }

    /**
     * MarketMakerCacheAPIImpl constructor gets its delegate object and
     * initializes the quote cache.
     *
     * @param expressSessionMgr the SessionManager object for the current user.
     */
    public MarketMakerAPIImpl( UserSessionManagerV9 expressSessionMgr, CMIUserSessionAdmin userListener, EventChannelListener clientListener, boolean gmd)
    {
        super(expressSessionMgr, userListener, clientListener, gmd);
    }

    public void initialize() throws Exception
    {
        try
        {
            if(isAllowedSubscribeRFQ())
            {
                createThrottle();
            }
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(TRANSLATOR_NAME + ": MarketMakerAPIImpl.initialize", e);
            throw e;
        }
        super.initialize();
    }

    protected void initializeSessionManagerInterfaces() throws SystemException, CommunicationException, AuthorizationException
    {
        super.initializeSessionManagerInterfaces();

        quoteQuery = sessionManager.getQuote();
        userTradingParametersQuery = sessionManager.getUserTradingParameters();
    }

    protected void initializeSessionManagerV2Interfaces() throws SystemException, CommunicationException, AuthorizationException
    {
        super.initializeSessionManagerV2Interfaces();

        quoteQueryV2 = sessionManagerV2.getQuoteV2();
    }

    protected void initializeSessionManagerV3Interfaces() throws SystemException, CommunicationException, AuthorizationException
    {
        super.initializeSessionManagerV3Interfaces();

        quoteQueryV3 = sessionManagerV3.getQuoteV3();
    }

    protected void initializeSessionManagerV7Interfaces() throws SystemException, CommunicationException, AuthorizationException
    {
        super.initializeSessionManagerV7Interfaces();

        quoteQueryV7 = sessionManagerV7.getQuoteV7();
    }
    
    protected void initializeCaches() throws Exception
    {
        super.initializeCaches();
    }

    protected void initializeCachesV2() throws Exception
    {
        super.initializeCachesV2();

        if(isAllowedSubscribeRFQ())
        {
            initializeQuoteCacheV2();
        }
    }

    protected void createThrottle()
    {
        RFQInstructionProcessor processor = new RFQInstructionProcessor();
        throttle = ScheduledInstructionThrottleFactory.create(processor);
        throttle.start();
    }

    protected void initializeSessionCaches(String sessionName)
    {
        super.initializeSessionCaches(sessionName);
        if(isAllowedSubscribeRFQ())
        {
            RFQCacheFactory.create(sessionName);
        }
    }
    protected QuoteCache getQuoteCache()
    {
        if(quoteCache == null)
        {
            quoteCache = new QuoteCache();
        }
        return quoteCache;
    }
// switched to V2 quote subscriptions; see initializeQuoteCacheV2()
/*
    protected void initializeQuoteCache() throws Exception {
        // populate the quote cache
        try
        {
            subscribeQuoteStatus(getQuoteCache());
            subscribeQuoteStatus(orderBookManager);
            subscribeQuoteFilledReport(filledReportCache);
            subscribeQuoteBustReport(bustReportCache);
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(TRANSLATOR_NAME+": initializeQuoteCache()", e);
            throw e;
        }
    }
*/

    protected void initializeQuoteCacheV2() throws Exception
    {
        try
        {
            subscribeQuoteFilledReportV2(getQuoteCache());
            subscribeQuoteDeletedReportV2(getQuoteCache());
            subscribeQuoteStatusV2(getQuoteCache());

            subscribeQuoteStatusV2(orderBookManager);
            subscribeQuoteDeletedReportV2(orderBookManager);
            subscribeQuoteFilledReportV2(orderBookManager);

            subscribeQuoteFilledReportV2(filledReportCache);

            subscribeQuoteBustReportV2(bustReportCache);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(TRANSLATOR_NAME + ": initializeQuoteCacheV2()", e);
            throw e;
        }
    }

    protected void initializeCallbackConsumers()
    {
        super.initializeCallbackConsumers();

        quoteStatusConsumer = QuoteStatusConsumerFactory.create(eventChannel);
        rfqConsumer = RFQConsumerFactory.create(eventChannel);
    }

    protected void initializeCallbackV2Consumers()
    {
        super.initializeCallbackV2Consumers();

        quoteStatusConsumerV2 = com.cboe.consumers.callback.QuoteStatusV2ConsumerFactory.create(eventChannel);
        lockedQuoteStatusConsumerV2 = com.cboe.consumers.callback.LockedQuoteStatusV2ConsumerFactory.create(eventChannel);
        rfqConsumerV2 = com.cboe.consumers.callback.RFQV2ConsumerFactory.create(eventChannel);
    }

    protected void initializeCallbackV3Consumers()
    {
        super.initializeCallbackV3Consumers();
    }

    /**
     * Initializes callback listener to CAS
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void initializeQuoteCallbackListener()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        GUILoggerHome.find().debug(TRANSLATOR_NAME + ": initializeQuoteCallbackListener", GUILoggerBusinessProperty.QUOTE);

        ChannelKey key = new ChannelKey(ChannelType.CB_ALL_QUOTES, new Integer(0));

        if(SubscriptionManagerFactory.find().subscribe(key, null, quoteStatusConsumer) == 1)
        {
            if(isFirmUser())
            {
                quoteQuery.subscribeQuoteStatusForFirm(quoteStatusConsumer, gmdCallback);
            }
            else
            {
                quoteQuery.subscribeQuoteStatus(quoteStatusConsumer, gmdCallback);
            }
        }
    }

    public void initializeQuoteV2CallbackListener()
            throws CommunicationException, AuthorizationException, DataValidationException, SystemException
    {
        GUILoggerHome.find().debug(TRANSLATOR_NAME + ": initializeQuoteV2CallbackListener", GUILoggerBusinessProperty.QUOTE);

        ChannelKey key = new ChannelKey(ChannelType.CB_ALL_QUOTES_V2, new Integer(0));

        // subscribe to the CAS, but not to the event channel.
        if (SubscriptionManagerFactory.find().subscribe(key, null, quoteStatusConsumerV2) == 1)
        {
            if (isFirmUser())
            {
                quoteQueryV2.subscribeQuoteStatusForFirmV2(quoteStatusConsumerV2, gmdCallback);
            }
            else
            {
                quoteQueryV2.subscribeQuoteStatusV2(quoteStatusConsumerV2, DEFAULT_PUBLISH_ON_SUBSCRIBE_ACTION,
                                                    DEFAULT_INCLUDE_USER_EVENT_ACTION, gmdCallback);
            }
        }
    }

    protected void cleanupSessionManagerInterfaces()
    {
        quoteQuery = null;
        userTradingParametersQuery = null;

        super.cleanupSessionManagerInterfaces();
    }

    protected void cleanupSessionManagerV2Interfaces()
    {
        quoteQueryV2 = null;

        super.cleanupSessionManagerV2Interfaces();
    }

    protected void cleanupCaches()
    {
        if(isAllowedSubscribeRFQ())
        {
            cleanupQuoteCache();
        }

        super.cleanupCaches();
    }

    protected void cleanupCachesV2()
    {
        if(isAllowedSubscribeRFQ())
        {
            cleanupQuoteCacheV2();
            quoteCache = null;
        }

        super.cleanupCachesV2();
    }

    /**
     * cleanupCallbackConsumers()
     * @description This method cleans up all user related callbacks
     * @param       none
     * @returns     void
     */
    protected void cleanupCallbackConsumers()
    {
        quoteStatusConsumer = null;
        rfqConsumer         = null;

        super.cleanupCallbackConsumers();
    }

    protected void cleanupCallbackV2Consumers()
    {
        quoteStatusConsumerV2 = null;
        lockedQuoteStatusConsumerV2 = null;
        rfqConsumerV2 = null;

        super.cleanupCallbackV2Consumers();
    }

    /**
     * cleanupQuoteCache()
     * @description This method cleans up user's quote cache
     * @param       none
     * @returns     void
     */
    private void cleanupQuoteCache()
    {
        try
        {
            unsubscribeQuoteFilledReport(filledReportCache);
            unsubscribeQuoteBustReport(bustReportCache);
            quoteCache = null;
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(TRANSLATOR_NAME+": cleanupQuoteCache", e);
        }
    }

    private void cleanupQuoteCacheV2()
    {
        try
        {
            unsubscribeQuoteStatusV2(getQuoteCache());
            unsubscribeQuoteStatusV2(orderBookManager);
            unsubscribeQuoteFilledReportV2(getQuoteCache());
            unsubscribeQuoteFilledReportV2(filledReportCache);
            unsubscribeQuoteFilledReportV2(orderBookManager);
            unsubscribeQuoteBustReportV2(bustReportCache);
            unsubscribeQuoteDeletedReportV2(orderBookManager);
            unsubscribeQuoteDeletedReportV2(getQuoteCache());
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(TRANSLATOR_NAME + ": cleanupQuoteCacheV2", e);
        }
    }

    public SessionProductClass[] getAllQuotedClasses() {

        try {
            if (GUILoggerHome.find().isDebugOn())
            {
                GUILoggerHome.find().debug(TRANSLATOR_NAME+": getAllQuotedClasses", GUILoggerBusinessProperty.QUOTE, "");
            }

            SessionKeyContainer[] keys = getQuoteCache().getAllQuotedClasses();
            SessionProductClass[] classes = new SessionProductClass[keys.length];
            for (int i=0; i < keys.length; i++) {
                    classes[i] = this.getClassByKeyForSession(keys[i].getSessionName(), keys[i].getKey());
            }
            return classes;
        } catch (Exception e) {
            GUILoggerHome.find().exception(TRANSLATOR_NAME+": getAllQuotedClasses", "Unknown quoted class ", e);
            return null;
        }
    }

    public SessionProductClass[] getAllQuotedClassesForSession(String sessionName) {


        try {
            if (GUILoggerHome.find().isDebugOn())
            {
                GUILoggerHome.find().debug(TRANSLATOR_NAME+": getAllQuotedClassesForSession", GUILoggerBusinessProperty.QUOTE, sessionName);
            }

            SessionKeyContainer[] keys = getQuoteCache().getAllQuotedClassesForSession(sessionName);
            SessionProductClass[] classes = new SessionProductClass[keys.length];
            for (int i=0; i < keys.length; i++) {
                classes[i] = this.getClassByKeyForSession(keys[i].getSessionName(), keys[i].getKey());
            }
            return classes;
        } catch (Exception e) {
            GUILoggerHome.find().exception(TRANSLATOR_NAME+": getAllQuotedClassesForSession", "Unknown quoted class ",e);
            return null;
        }
    }

    /**
     * Delegates to the delegate object to accept quotes.
     *
     * @param quotes array of QuoteEntryStructs indicating the quotes to accept.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotAcceptedException
     * @exception TransactionFailedException
     */
    public void acceptQuote(QuoteEntryStruct quote)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": acceptQuote", GUILoggerBusinessProperty.QUOTE, quote);
        }
        quoteQuery.acceptQuote(quote);
    }

    /**
     * accepts the quote entry for class.
     *
     * @param classKey class key
     * @param quotes quote entries to be accepted
     * @return ClassQuoteResultStruct sequence
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotAcceptedException
     * @exception TransactionFailedException
     */
    public ClassQuoteResultStruct[] acceptQuotesForClass(int classKey, QuoteEntryStruct[] quotes)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = new Integer(classKey);
            argObj[1] = quotes;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": acceptQuotesForClass", GUILoggerBusinessProperty.QUOTE, argObj);
        }
        return quoteQuery.acceptQuotesForClass(classKey, quotes);
    }

    /**
     * Delegates to the quoteQuery object to get quotes for a product.
     *
     * @param productKey the product key to get quotes for.
     * @param clientListener the subscribing listener.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     */
    public QuoteDetailStruct getQuote(String sessionName, int productKey)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getQuote", GUILoggerBusinessProperty.QUOTE, argObj);
        }

        QuoteDetailStruct quote = getQuoteCache().getQuote(sessionName, productKey);
        if(quote == null)
        {
            quote = quoteQuery.getQuote(sessionName, productKey);
            getQuoteCache().addQuote(quote);
        }

        return quote;
    }

    /**
     * Delegates to the quoteQuery object to get quotes for a product.
     *
     * @param productKey the product key to get quotes for.
     * @param clientListener the subscribing listener.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     */
    public QuoteDetailStruct[] getQuotesForProduct(int productKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = new Integer(productKey);
            argObj[1] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getQuotesForProduct", GUILoggerBusinessProperty.QUOTE, argObj);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_QUOTE_BY_CLASS, new Integer(productKey));
        eventChannel.addChannelListener(eventChannel, clientListener, key);

        return getQuoteCache().getQuotesForProduct(productKey);
    }

    /**
     * Delegates to the QuoteCache object to get all quotes for the user, and subscribes the
     * listener for QuoteStatusV2 (ChannelType.CB_ALL_QUOTES).
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param clientListener the subscribing listener.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public QuoteDetailStruct[] getAllQuotes(EventChannelListener clientListener)
           throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getAllQuotes", GUILoggerBusinessProperty.QUOTE, clientListener);
        }
        subscribeQuoteStatusV2(clientListener);
        return getQuoteCache().getAllQuotes();
    }


    /**
     * Subscribes to event channel for CB_ALL_QUOTES
     * @param clientListener to subscribe
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @deprecated Should use V2 Quote methods instead of V1
     */
    public void subscribeQuoteStatus(EventChannelListener clientListener)
       throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        GUILoggerHome.find().debug(TRANSLATOR_NAME + ": subscribeQuoteStatus", GUILoggerBusinessProperty.QUOTE, clientListener);

        ChannelKey key = new ChannelKey(ChannelType.CB_ALL_QUOTES, new Integer(0));
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
    }

    /**
     * Cancels a quote for a product.
     *
     * @param productKeys an array of product keys to cancel quotes for.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception TransactionFailedException
     * @exception NotAcceptedException
     * @exception NotFoundException
     */
    public void cancelQuote(String sessionName, int productKey)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException, NotAcceptedException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": cancelQuote", GUILoggerBusinessProperty.QUOTE, argObj);
        }

        quoteQuery.cancelQuote(sessionName, productKey);
    }

    /**
     * cancelAllQuotes cancels all of a marketmakers quotes.
     *
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception NotAcceptedException
     * @exception TransactionFailedException
     */
    public void cancelAllQuotes(String sessionName)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException, NotAcceptedException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": cancelAllQuotes", GUILoggerBusinessProperty.QUOTE, sessionName);
        }
        quoteQuery.cancelAllQuotes(sessionName);
    }

    /**
     * Cancels all quotes for each given class.
     *
     * @param classKeys the product classes to cancel quotes for.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception TransactionFailedException
     * @exception NotAcceptedException
     * @exception NotFoundException
     */
    public void cancelQuotesByClass(String sessionName, int classKey)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException, NotAcceptedException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": cancelQuotesByClass", GUILoggerBusinessProperty.QUOTE, argObj);
        }
        quoteQuery.cancelQuotesByClass(sessionName, classKey);
    }

    /**
     * Returns the quote from the cache with the class key passed.
     * @param classKey Class key of quote to get.
     * @param clientListener the subscribing listener.
     * @return A <code>QuoteDetailStruct[]</code>. Will be null if Quote Caching is turned off.
     */
    public QuoteDetailStruct[] getQuotesByClass(int classKey, EventChannelListener clientListener)
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = new Integer(classKey);
            argObj[1] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getQuotesByClass", GUILoggerBusinessProperty.QUOTE, argObj);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_QUOTE_BY_CLASS, new Integer(classKey));
        eventChannel.addChannelListener(eventChannel, clientListener, key);
        return getQuoteCache().getQuotesForClass(classKey);
    }

    /**
     * Unsubscribes the listener for quote status information for the given classes.
     *
     * @param classKeys the array of product classes to unsubscribe for.
     * @param clientListener the unsubscribing listener.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @deprecated Should use V2 Quote methods instead of V1
     */
    public void unsubscribeQuoteStatusByClass(int classKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = new Integer(classKey);
            argObj[1] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeQuoteStatusByClass", GUILoggerBusinessProperty.QUOTE, argObj );
        }
        ChannelKey key = new ChannelKey(ChannelType.CB_QUOTE_BY_CLASS, new Integer(classKey));
        eventChannel.removeChannelListener(eventChannel, clientListener, key);
    }

    /**
     * 
     * @param productKey
     * @param clientListener
     * @throws SystemException
     * @throws CommunicationException
     * @throws AuthorizationException
     * @throws DataValidationException
     * @deprecated Should use V2 Quote methods instead of V1
     */ 
    public void unsubscribeQuoteStatusByProduct(int productKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = new Integer (productKey);
            argObj[1] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeQuoteStatusByProduct", GUILoggerBusinessProperty.QUOTE, argObj);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_QUOTE_BY_CLASS, new Integer(productKey));
        eventChannel.removeChannelListener(eventChannel, clientListener, key);
    }

    /**
     * Subscribes listening for the given product classes by the given listener.
     *
     * @param classKeys an array of all interested class keys.
     * @param clientListener the subscribing listener.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */

/*
    public void subscribeRFQ(String sessionName, int classKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeRFQ", argObj);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_RFQ, new SessionKeyContainer(sessionName, classKey));
        if (SubscriptionManagerFactory.find().subscribe(key, clientListener, rfqConsumer) == 1) {
            quoteQuery.subscribeRFQ(sessionName, classKey, rfqConsumer);
        }
    }
*/

    /**
     * Unsubscribes listening for the given product classes by the given listener.
     *
     * @return an array of RFQStructs for the given product classes.
     * @param classKeys an array of all interested class keys.
     * @param clientListener the unsubscribing listener.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */

/*
    public void unsubscribeRFQ(String sessionName, int classKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeRFQ", argObj);
        }
        ChannelKey key = new ChannelKey(ChannelType.CB_RFQ, new SessionKeyContainer(sessionName, classKey));

        if (SubscriptionManagerFactory.find().unsubscribe(key, clientListener, rfqConsumer) == 0) {
            quoteQuery.unsubscribeRFQ(sessionName, classKey, rfqConsumer);
        }
    }
*/


    /**
     * Subscribes the listener for quote filled report information for all of
     * the users filled quotes.
     *
     * @param clientListener the subscribing listener.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @deprecated Should use V2 Quote methods instead of V1
     */
    public void subscribeQuoteFilledReport(EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (isFirmUser())
        {
            subscribeQuoteFilledReportForFirm(clientListener);
        }
        else
        {
            subscribeQuoteFilledReportForUser(clientListener);
        }
    }

    /**
     * Unsubscribes the listener for quote filled report information for all of
     * the users filled quotes.
     *
     * @param clientListener the unsubscribing listener.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @deprecated Should use V2 Quote methods instead of V1
     */
    public void unsubscribeQuoteFilledReport(EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (isFirmUser())
        {
            unsubscribeQuoteFilledReportForFirm(clientListener);
        }
        else
        {
            unsubscribeQuoteFilledReportForUser(clientListener);
        }
    }

    /**
     * Unsubscribes to event channel for CB_ALL_QUOTES
     * @param clientListener to unsubscribe
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void unsubscribeAllQuoteStatus(EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        GUILoggerHome.find().debug(TRANSLATOR_NAME + ": unsubscribeAllQuoteStatus", GUILoggerBusinessProperty.QUOTE, clientListener);

        ChannelKey key = new ChannelKey(ChannelType.CB_ALL_QUOTES, new Integer(0));
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
    }
/*
     * Gets the quote history for the given class.
     *
     * @author Connie Feng
     *
     * @return quotes activity history.
     * @param classKey the class key for which to get quote activities
     * @param startTime the start time to query for
     * @param directions the directions to query for
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException

       public ActivityHistoryStruct[] getQuoteActivity(int classKey, DateTimeStruct startTime, short direction)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        return quoteQuery.getQuoteActivity(classKey, startTime, direction);
    }
 */

    public QuoteFilledReportStruct[] getQuoteFilledReports(EventChannelListener listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getQuoteFilledReports", GUILoggerBusinessProperty.QUOTE, listener);
        }

        try
        {
            // TODO: find out why this interface throws Exception????
            subscribeQuoteFilledReportV2(listener);
        }
        catch(SystemException e)
        {
            throw(e);
        }
        catch(CommunicationException e)
        {
            throw(e);
        }
        catch(AuthorizationException e)
        {
            throw(e);
        }
        catch(DataValidationException e)
        {
            throw(e);
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(e);
        }
        return filledReportCache.getFilledReportsForQuotes();
    }

    /******************************************************************************************
     * The Following block of methods is Quote Risk Management (QRM) profile query methods
     */
    /**
     * Queries all User Quote Risk Management (QRM) Profile for this user including defaults and triggers
     *
     * @author Mike Pyatetsky
     *
     * @return UserQuoteRiskManagementProfileStruct
     *
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception NotFoundException
     */
     public UserQuoteRiskManagementProfileStruct getAllQuoteRiskProfiles()
        throws SystemException, CommunicationException, AuthorizationException, NotFoundException
     {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getAllQuoteRiskProfiles", GUILoggerBusinessProperty.QRM, "");
        }
        return userTradingParametersQuery.getAllQuoteRiskProfiles();
     }

     /**
     * Queries all Quote Risk Management (QRM) Profile for given classKey
     *
     * @author Mike Pyatetsky
     *
     * @return QuoteRiskManagementProfileStruct
     *
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception NotFoundException
     */
     public QuoteRiskManagementProfileStruct getQuoteRiskManagementProfileByClass(int classKey)
        throws SystemException, CommunicationException, AuthorizationException, NotFoundException
     {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getQuoteRiskManagementProfileByClass", GUILoggerBusinessProperty.QRM, new Integer(classKey));
        }
        return userTradingParametersQuery.getQuoteRiskManagementProfileByClass(classKey);
     }


    /**
     * Sets QRM global on/off switch status for this user
     *
     * @author Mike Pyatetsky
     *
     * @param status Boolean to enable/disable QRM globally
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception TransactionFailedException
     */
     public void setQuoteRiskManagementEnabledStatus(boolean status)
        throws SystemException, CommunicationException, AuthorizationException, TransactionFailedException
     {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": setQuoteRiskManagementEnabledStatus", GUILoggerBusinessProperty.QRM, new Boolean(status));
        }
        userTradingParametersQuery.setQuoteRiskManagementEnabledStatus(status);
     }

    /**
     * Gets QRM global on/off switch status for this user
     *
     * @author Mike Pyatetsky
     *
     * @return  Boolean to enable/disable QRM globally
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     */
     public boolean getQuoteRiskManagementEnabledStatus()
        throws SystemException, CommunicationException, AuthorizationException
     {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getQuoteRiskManagementEnabledStatus", GUILoggerBusinessProperty.QRM, "");
        }
        return userTradingParametersQuery.getQuoteRiskManagementEnabledStatus();
     }

    /**
     * Gets defauld QRM profile for this user
     *
     * @author Mike Pyatetsky
     *
     * @return QuoteRiskManagementProfileStruct
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception NotFoundException
     */
     public QuoteRiskManagementProfileStruct getDefaultQuoteRiskProfile()
        throws SystemException, CommunicationException, AuthorizationException, NotFoundException
     {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getDefaultQuoteRiskProfile", GUILoggerBusinessProperty.QRM, "");
        }
        return userTradingParametersQuery.getDefaultQuoteRiskProfile();
     }

    /**
     * Sets QRM profile for this user per class key passed in QuoteRiskManagementProfileStruct
     *
     * @author Mike Pyatetsky
     *
     * @param quoteRiskProfile Object of type QuoteRiskManagementProfileStruct
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception TransactionFailedException
     * @exception DataValidationException
     */
     public void setQuoteRiskProfile(QuoteRiskManagementProfileStruct quoteRiskProfile)
        throws SystemException, CommunicationException, AuthorizationException, TransactionFailedException, DataValidationException
     {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": setQuoteRiskProfile", GUILoggerBusinessProperty.QRM, quoteRiskProfile);
        }
        userTradingParametersQuery.setQuoteRiskProfile(quoteRiskProfile);
     }

    /**
     * Removes QRM profile for this user for given class key
     *
     * @author Mike Pyatetsky
     *
     * @param classKey int class key
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception TransactionFailedException
     * @exception DataValidationException
     */
     public void removeQuoteRiskProfile(int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException
     {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": removeQuoteRiskProfile", GUILoggerBusinessProperty.QRM, new Integer(classKey));
        }
        userTradingParametersQuery.removeQuoteRiskProfile(classKey);
     }

    /**
     * Removes all quote risk profiles for this user
     *
     * @author Mike Pyatetsky
     *
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception TransactionFailedException
     */
     public void removeAllQuoteRiskProfiles()
        throws SystemException, CommunicationException, AuthorizationException, TransactionFailedException
     {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": removeAllQuoteRiskProfiles", GUILoggerBusinessProperty.QRM, "");
        }
        userTradingParametersQuery.removeAllQuoteRiskProfiles();
     }

     /**********************  END of QRM profile handling methodes ***********************************************/
    public QuoteBustReportStruct[] getQuoteBustReports(EventChannelListener listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getQuoteBustReports", GUILoggerBusinessProperty.QUOTE, listener);
        }
        try
        {
            subscribeQuoteBustReportV2(listener);
        }
        catch(SystemException e)
        {
            throw(e);
        }
        catch(CommunicationException e)
        {
            throw(e);
        }
        catch(AuthorizationException e)
        {
            throw(e);
        }
        catch(DataValidationException e)
        {
            throw(e);
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(e);
        }
        return bustReportCache.getBustReportsForQuotes();
    }


    /**
     * Subscribes the client listener to receive quote
     * bust report information.
     *
     * @author Connie Feng
     *
     * @param clientListener the client listener to receive continued quote bust
     *        reports.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @deprecated Should use V2 Quote methods instead of V1
     */
    public void subscribeQuoteBustReport(EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (isFirmUser())
        {
            subscribeQuoteBustReportForFirm(clientListener);
        }
        else
        {
            subscribeQuoteBustReportForUser(clientListener);
        }
    }

    /**
     * Unsubscribes the client listener to receive quote
     * bust report information.
     *
     * @author Connie Feng
     *
     * @param clientListener the client listener to unsubscribe continued quote bust
     *        reports.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @deprecated Should use V2 Quote methods instead of V1
     */
    public void unsubscribeQuoteBustReport(EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (isFirmUser())
        {
            unsubscribeQuoteBustReportForFirm(clientListener);
        }
        else
        {
            unsubscribeQuoteBustReportForUser(clientListener);
        }
    }

    public RFQ[] getCachedRFQsForSession(String sessionName, EventChannelListener listener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        registerGenericRFQListener(sessionName, listener);
        return RFQCacheFactory.find(sessionName).getAllRFQs();
    }

    public RFQ[] getRFQsForClass(String sessionName, int classKey, EventChannelListener listener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        subscribeRFQForClass(sessionName, classKey, listener);
        return RFQCacheFactory.find(sessionName).getRFQsForClass(classKey);
    }

    public RFQ getRFQsForProduct(String sessionName, int productKey, EventChannelListener listener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        try
        {
            Product product = getProductByKey(productKey);
            subscribeRFQForClass(sessionName, product.getProductKeysStruct().classKey, listener);
        } catch (NotFoundException e)
        {
            DataValidationException de = new DataValidationException(e.details);
            throw de;
        }
        return RFQCacheFactory.find(sessionName).getRFQForProduct(productKey);
    }

    public void registerGenericRFQListener(String sessionName, EventChannelListener listener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = listener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": registerGenericRFQListener", GUILoggerBusinessProperty.RFQ, argObj);
        }
        ChannelKey key = new ChannelKey(ChannelType.CB_RFQ, sessionName);
        EventChannelAdapterFactory.find().addChannelListener(eventChannel, listener, key);
    }

    public void unregisterGenericRFQListener(String sessionName, EventChannelListener listener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = listener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unregisterGenericRFQListener", GUILoggerBusinessProperty.RFQ, argObj);
        }
        ChannelKey key = new ChannelKey(ChannelType.CB_RFQ, sessionName);
        EventChannelAdapterFactory.find().removeChannelListener(eventChannel, listener, key);
    }

    public void subscribeRFQForClass(String sessionName, int classKey, EventChannelListener listener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = listener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeRFQForClass", GUILoggerBusinessProperty.RFQ, argObj);
        }

        // register the listener

        ChannelKey key = new ChannelKey(ChannelType.CB_RFQ, new SessionKeyContainer(sessionName, classKey));
        if (SubscriptionManagerFactory.find().subscribe(key, listener, rfqConsumer) == 1) {
            quoteQuery.subscribeRFQ(sessionName, classKey, rfqConsumer);
        }
    }

    public void unsubscribeRFQForClass(String sessionName, int classKey, EventChannelListener listener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = listener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeRFQForClass", GUILoggerBusinessProperty.RFQ, argObj);
        }
        ChannelKey key = new ChannelKey(ChannelType.CB_RFQ, new SessionKeyContainer(sessionName, classKey));

        //unsubscribe the listener

        if (SubscriptionManagerFactory.find().unsubscribe(key, listener, rfqConsumer) == 0) {
            quoteQuery.unsubscribeRFQ(sessionName, classKey, rfqConsumer);
        }
    }

///////////////////////////////////////////////////////////////////////////////
// PROTECTED METHODS

    ///////////////////////////////////
    // Quote Filled

    protected void subscribeQuoteFilledReportForUser(EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeQuoteFilledReportForUser", GUILoggerBusinessProperty.QUOTE, clientListener);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT, new Integer(0));
        eventChannel.addChannelListener(eventChannel, clientListener, key);
    }
    /**
     * 
     * @param clientListener
     * @throws SystemException
     * @throws CommunicationException
     * @throws AuthorizationException
     * @throws DataValidationException
     * @deprecated Should use unsubscribeQuoteStatusForFirmV2
     */ 
    protected void unsubscribeQuoteFilledReportForUser(EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeQuoteFilledReportForUser", GUILoggerBusinessProperty.QUOTE, clientListener);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT, new Integer(0));
        eventChannel.removeChannelListener(eventChannel, clientListener, key);
    }

    /**
     * Subscribes to event channel for CB_QUOTE_FILLED_REPORT_BY_FIRM, CB_QUOTE_BUST_REPORT_BY_FIRM
     * @param clientListener to subscribe
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    protected void subscribeQuoteFilledReportForFirm(EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        GUILoggerHome.find().debug(TRANSLATOR_NAME + ": subscribeQuoteFilledReportForFirm", GUILoggerBusinessProperty.QUOTE, clientListener);

        ChannelKey key;

        key = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_BY_FIRM, new Integer(0));
        eventChannel.addChannelListener(eventChannel, clientListener, key);
        key = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_BY_FIRM, new Integer(0));
        eventChannel.addChannelListener(eventChannel, clientListener, key);
    }

    /**
     * Unsubscribes to event channel for CB_QUOTE_FILLED_REPORT_BY_FIRM, CB_QUOTE_BUST_REPORT_BY_FIRM
     * @param clientListener to unsubscribe
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @deprecated Should use unsubscribeQuoteStatusForFirmV2
     */
    protected void unsubscribeQuoteFilledReportForFirm(EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        GUILoggerHome.find().debug(TRANSLATOR_NAME + ": unsubscribeQuoteFilledReportForFirm", GUILoggerBusinessProperty.QUOTE, clientListener);

        ChannelKey key;

        key = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_BY_FIRM, new Integer(0));
        eventChannel.removeChannelListener(eventChannel, clientListener, key);
        key = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_BY_FIRM, new Integer(0));
        eventChannel.removeChannelListener(eventChannel, clientListener, key);
    }

    ///////////////////////////////////
    // Quote Bust


    protected void subscribeQuoteBustReportForUser(EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {

        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeQuoteBustReportForUser", GUILoggerBusinessProperty.QUOTE, clientListener);
        }
        ChannelKey key = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT, new Integer(0));
        eventChannel.addChannelListener(eventChannel, clientListener, key);
    }
    protected void unsubscribeQuoteBustReportForUser(EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeQuoteBustReportForUser", GUILoggerBusinessProperty.QUOTE, clientListener);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT, new Integer(0));
        eventChannel.removeChannelListener(eventChannel, clientListener, key);
    }

    /**
     * Subscribes to event channel for CB_QUOTE_FILLED_REPORT_BY_FIRM, CB_QUOTE_BUST_REPORT_BY_FIRM
     * @param clientListener to subscribe
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    protected void subscribeQuoteBustReportForFirm(EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        GUILoggerHome.find().debug(TRANSLATOR_NAME + ": subscribeQuoteBustReportForFirm", GUILoggerBusinessProperty.QUOTE, clientListener);

        ChannelKey key;

        key = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_BY_FIRM, new Integer(0));
        eventChannel.addChannelListener(eventChannel, clientListener, key);
        key = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_BY_FIRM, new Integer(0));
        eventChannel.addChannelListener(eventChannel, clientListener, key);
    }

    /**
     * Unsubscribes to event channel for CB_QUOTE_FILLED_REPORT_BY_FIRM, CB_QUOTE_BUST_REPORT_BY_FIRM
     * @param clientListener to unsubscribe
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    protected void unsubscribeQuoteBustReportForFirm(EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        GUILoggerHome.find().debug(TRANSLATOR_NAME + ": unsubscribeQuoteBustReportForFirm", GUILoggerBusinessProperty.QUOTE, clientListener);

        ChannelKey key;

        key = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_BY_FIRM, new Integer(0));
        eventChannel.removeChannelListener(eventChannel, clientListener, key);
        key = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_BY_FIRM, new Integer(0));
        eventChannel.removeChannelListener(eventChannel, clientListener, key);
    }

    protected boolean isAllowedSubscribeRFQ()
    {
        Role role = getUserRole();
        boolean retVal = (role == Role.MARKET_MAKER ||
                          role == Role.DPM ||
                          role == Role.HELPDESK_OMT);
        return retVal;
    }

    // UserAccessV2

    // QuoteV2API
    public ClassQuoteResultStructV2[] acceptQuotesForClassV2(int classKey, QuoteEntryStruct[] quoteEntryStructs)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[1];
            argObj[0] = new Integer(classKey);
            argObj[1] = quoteEntryStructs;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": acceptQuotesForClass", GUILoggerBusinessProperty.QUOTE, argObj);
        }
        return quoteQueryV2.acceptQuotesForClassV2(classKey, quoteEntryStructs);
    }

    
    public void subscribeQuoteLockedNotification(EventChannelListener clientListener)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[4];
            argObj[0] = new Integer(0);
            argObj[1] = new Boolean(DEFAULT_PUBLISH_ON_SUBSCRIBE_ACTION);
            argObj[2] = clientListener;
            argObj[3] = new Boolean(gmdCallback);
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeQuoteLockedNotification", GUILoggerBusinessProperty.QUOTE, argObj);
        }
        ChannelKey key = new ChannelKey(ChannelType.CB_QUOTE_LOCKED_NOTIFICATION, new Integer(0));
        if (SubscriptionManagerFactory.find().subscribe(key, clientListener, lockedQuoteStatusConsumerV2) == 1)
        {
            quoteQueryV2.subscribeQuoteLockedNotification(DEFAULT_PUBLISH_ON_SUBSCRIBE_ACTION, lockedQuoteStatusConsumerV2, gmdCallback);
        }
    }

    public void unsubscribeQuoteLockedNotification(EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        GUILoggerHome.find().debug(TRANSLATOR_NAME + ": unsubscribeQuoteLockedNotification", GUILoggerBusinessProperty.QUOTE, clientListener);

        ChannelKey key = new ChannelKey(ChannelType.CB_QUOTE_LOCKED_NOTIFICATION, new Integer(0));
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
        if (SubscriptionManagerFactory.find().unsubscribe(key, clientListener, lockedQuoteStatusConsumerV2) == 0)
        {
            quoteQueryV2.unsubscribeQuoteLockedNotification(lockedQuoteStatusConsumerV2);
        }
    }

    public void subscribeQuoteLockedNotificationForClass(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[4];
            argObj[0] = new Integer(classKey);
            argObj[1] = new Boolean(DEFAULT_PUBLISH_ON_SUBSCRIBE_ACTION);
            argObj[2] = clientListener;
            argObj[3] = new Boolean(gmdCallback);
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeQuoteLockedNotificationForClass", GUILoggerBusinessProperty.QUOTE, argObj);
        }
        ChannelKey key = new ChannelKey(ChannelType.CB_QUOTE_LOCKED_NOTIFICATION_BY_CLASS, new Integer(classKey));
        if (SubscriptionManagerFactory.find().subscribe(key, clientListener, lockedQuoteStatusConsumerV2) == 1)
        {
            quoteQueryV2.subscribeQuoteLockedNotificationForClass(classKey, DEFAULT_PUBLISH_ON_SUBSCRIBE_ACTION, lockedQuoteStatusConsumerV2, gmdCallback);
        }
    }

    public void unsubscribeQuoteLockedNotificationForClass(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = new Integer(classKey);
            argObj[1] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": unsubscribeQuoteLockedNotificationForClass", GUILoggerBusinessProperty.QUOTE, argObj);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_QUOTE_LOCKED_NOTIFICATION_BY_CLASS, new Integer(classKey));
        if (SubscriptionManagerFactory.find().unsubscribe(key, clientListener, lockedQuoteStatusConsumerV2) == 0)
        {
            quoteQueryV2.unsubscribeQuoteLockedNotificationForClass(classKey, lockedQuoteStatusConsumerV2);
        }
    }

    public void subscribeQuoteStatusV2(EventChannelListener clientListener)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": subscribeQuoteStatusV2", GUILoggerBusinessProperty.QUOTE, clientListener);
        }

        // V2 consumer publishes CB_ALL_QUOTES, not CB_ALL_QUOTES_V2
        subscribeQuoteStatusConsumerListener(0, ChannelType.CB_ALL_QUOTES, clientListener);
    }

    public void unsubscribeQuoteStatusV2(EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        GUILoggerHome.find().debug(TRANSLATOR_NAME + ": unsubscribeQuoteStatusV2", GUILoggerBusinessProperty.QUOTE, clientListener);

        unsubscribeQuoteStatusConsumerListener(0, ChannelType.CB_ALL_QUOTES, clientListener);
    }

    public void subscribeQuoteStatusForClassV2(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[5];
            argObj[0] = new Integer(classKey);
            argObj[1] = new Boolean(DEFAULT_PUBLISH_ON_SUBSCRIBE_ACTION);
            argObj[2] = new Boolean(DEFAULT_INCLUDE_USER_EVENT_ACTION);
            argObj[3] = clientListener;
            argObj[4] = new Boolean(gmdCallback);
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeQuoteStatusForClassV2", GUILoggerBusinessProperty.QUOTE, argObj);
        }

        // V2 consumer publishes CB_QUOTE_BY_CLASS, not CB_QUOTE_BY_CLASS_V2
        subscribeQuoteStatusConsumerListener(classKey, ChannelType.CB_QUOTE_BY_CLASS, clientListener);
    }

    public void unsubscribeQuoteStatusForClassV2(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = new Integer(classKey);
            argObj[1] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": unsubscribeQuoteStatusForClassV2", GUILoggerBusinessProperty.QUOTE, argObj);
        }

        unsubscribeQuoteStatusConsumerListener(classKey, ChannelType.CB_QUOTE_BY_CLASS, clientListener);
    }


    public void subscribeQuoteStatusForProductV2(int productKey, EventChannelListener clientListener)
    {
        subscribeQuoteStatusConsumerListener(productKey, ChannelType.CB_QUOTE_BY_CLASS, clientListener);
    }

    public void unsubscribeQuoteStatusForProductV2(int productKey, EventChannelListener clientListener)
    {
        unsubscribeQuoteStatusConsumerListener(productKey, ChannelType.CB_QUOTE_BY_CLASS, clientListener);
    }


    public void subscribeQuoteStatusForFirmV2(EventChannelListener clientListener)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = clientListener;
            argObj[1] = new Boolean(gmdCallback);
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": subscribeQuoteStatusForFirmV2", GUILoggerBusinessProperty.QUOTE, argObj);
        }
        ChannelKey key;
        key = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_BY_FIRM, new Integer(0));
        SubscriptionManagerFactory.find().subscribe(key, clientListener, quoteStatusConsumerV2);
        key = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_BY_FIRM, new Integer(0));
        SubscriptionManagerFactory.find().subscribe(key, clientListener, quoteStatusConsumerV2);
        key = new ChannelKey(ChannelType.CB_QUOTE_CANCEL_REPORT_BY_FIRM_V2, new Integer(0));
        SubscriptionManagerFactory.find().subscribe(key, clientListener, quoteStatusConsumerV2);
    }

    public void unsubscribeQuoteStatusForFirmV2(EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        GUILoggerHome.find().debug(TRANSLATOR_NAME + ": unsubscribeQuoteStatusForFirmV2", GUILoggerBusinessProperty.QUOTE, clientListener);
        ChannelKey key;
        key = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_BY_FIRM, new Integer(0));
        SubscriptionManagerFactory.find().unsubscribe(key, clientListener, quoteStatusConsumerV2);
        key = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_BY_FIRM, new Integer(0));
        SubscriptionManagerFactory.find().unsubscribe(key, clientListener, quoteStatusConsumerV2);
        key = new ChannelKey(ChannelType.CB_QUOTE_CANCEL_REPORT_BY_FIRM_V2, new Integer(0));
        SubscriptionManagerFactory.find().unsubscribe(key, clientListener, quoteStatusConsumerV2);
    }


    public void subscribeQuoteStatusForFirmForClassV2(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = new Integer(classKey);
            argObj[1] = clientListener;
            argObj[2] = new Boolean(gmdCallback);
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeQuoteStatusForFirmForClassV2", GUILoggerBusinessProperty.QUOTE, argObj);
        }

        subscribeQuoteStatusConsumerListener(classKey, ChannelType.CB_QUOTE_FILLED_REPORT_BY_FIRM_BY_CLASS_V2, clientListener);
        subscribeQuoteStatusConsumerListener(classKey, ChannelType.CB_QUOTE_BUST_REPORT_BY_FIRM_BY_CLASS_V2, clientListener);
    }


    public void unsubscribeQuoteStatusForFirmForClassV2(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = new Integer (classKey);
            argObj[1] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeQuoteStatusForFirmForClassV2", GUILoggerBusinessProperty.QUOTE, argObj);
        }

        unsubscribeQuoteStatusConsumerListener(classKey, ChannelType.CB_QUOTE_BUST_REPORT_BY_FIRM_BY_CLASS_V2, clientListener);
        unsubscribeQuoteStatusConsumerListener(classKey, ChannelType.CB_QUOTE_FILLED_REPORT_BY_FIRM_BY_CLASS_V2, clientListener);
    }

    public void subscribeRFQV2(String session, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = session;
            argObj[1] = new Integer(classKey);
            argObj[2] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": subscribeRFQV2", GUILoggerBusinessProperty.RFQ, argObj);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_RFQ_V2, new SessionKeyContainer(session, classKey));
        if (SubscriptionManagerFactory.find().subscribe(key, clientListener, rfqConsumerV2) == 1)
        {
            quoteQueryV2.subscribeRFQV2(session, classKey, rfqConsumerV2);
        }
    }

    public void unsubscribeRFQV2(String session, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = session;
            argObj[1] = new Integer(classKey);
            argObj[2] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeRFQV2", GUILoggerBusinessProperty.RFQ, argObj);
        }
        ChannelKey key = new ChannelKey(ChannelType.CB_RFQ_V2, new SessionKeyContainer(session, classKey));

        //unsubscribe the listener

        if (SubscriptionManagerFactory.find().unsubscribe(key, clientListener, rfqConsumer) == 0)
        {
            quoteQueryV2.unsubscribeRFQV2(session, classKey, rfqConsumerV2);
        }
    }

    public void subscribeQuoteDeletedReportV2(EventChannelListener clientListener) throws Exception
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": subscribeQuoteDeletedReportV2", GUILoggerBusinessProperty.QUOTE, clientListener);
        }

        subscribeQuoteStatusConsumerListener(0, ChannelType.CB_QUOTE_CANCEL_REPORT_V2, clientListener);
    }

    public void subscribeQuoteDeletedReportForClassV2(int classKey, EventChannelListener clientListener) throws Exception
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": subscribeQuoteDeletedReportForClassV2", GUILoggerBusinessProperty.QUOTE, clientListener);
        }

        subscribeQuoteStatusConsumerListener(classKey, ChannelType.CB_QUOTE_CANCEL_REPORT_BY_CLASS_V2, clientListener);
    }

    public void subscribeQuoteDeletedReportForProductV2(int productKey, EventChannelListener clientListener) throws Exception
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": subscribeQuoteDeletedReportForProductV2", GUILoggerBusinessProperty.QUOTE, clientListener);
        }

        subscribeQuoteStatusConsumerListener(productKey, ChannelType.CB_QUOTE_CANCEL_REPORT_BY_CLASS_V2, clientListener);
    }

    public void subscribeQuoteFilledReportV2(EventChannelListener clientListener) throws Exception
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": subscribeQuoteFilledReportV2", GUILoggerBusinessProperty.QUOTE);
        }

        subscribeQuoteStatusConsumerListener(0, ChannelType.CB_QUOTE_FILLED_REPORT, clientListener);
    }

    public void subscribeQuoteFilledReportForClassV2(int classKey, EventChannelListener clientListener) throws Exception
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": subscribeQuoteFilledReportForClassV2", GUILoggerBusinessProperty.QUOTE);
        }

        subscribeQuoteStatusConsumerListener(classKey, ChannelType.CB_QUOTE_FILLED_REPORT_BY_CLASS, clientListener);
    }

    public void subscribeQuoteFilledReportForProductV2(int productKey, EventChannelListener clientListener) throws Exception
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": subscribeQuoteFilledReportForProductV2", GUILoggerBusinessProperty.QUOTE);
        }

        subscribeQuoteStatusConsumerListener(productKey, ChannelType.CB_QUOTE_FILLED_REPORT_BY_CLASS, clientListener);
    }
// TODO -- why does this interface throw Exception???
    public void subscribeQuoteBustReportV2(EventChannelListener clientListener) throws Exception
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": subscribeQuoteBustReportV2", GUILoggerBusinessProperty.QUOTE);
        }

        subscribeQuoteStatusConsumerListener(0, ChannelType.CB_QUOTE_BUST_REPORT, clientListener);
    }

    public void subscribeQuoteBustReportForClassV2(int classKey, EventChannelListener clientListener) throws Exception
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": subscribeQuoteBustReportForClassV2", GUILoggerBusinessProperty.QUOTE);
        }

        subscribeQuoteStatusConsumerListener(classKey, ChannelType.CB_QUOTE_BUST_REPORT_BY_CLASS, clientListener);
    }

    public void subscribeQuoteBustReportForProductV2(int productKey, EventChannelListener clientListener) throws Exception
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": subscribeQuoteBustReportForProductV2", GUILoggerBusinessProperty.QUOTE);
        }

        subscribeQuoteStatusConsumerListener(productKey, ChannelType.CB_QUOTE_BUST_REPORT_BY_CLASS, clientListener);
    }

    public void unsubscribeQuoteDeletedReportV2(EventChannelListener clientListener) throws Exception
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": unsubscribeQuoteDeletedReportV2", GUILoggerBusinessProperty.QUOTE);
        }

        unsubscribeQuoteStatusConsumerListener(0, ChannelType.CB_QUOTE_CANCEL_REPORT_V2, clientListener);
    }

    public void unsubscribeQuoteDeletedReportForClassV2(int classKey, EventChannelListener clientListener) throws Exception
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": unsubscribeQuoteDeletedReportForClassV2", GUILoggerBusinessProperty.QUOTE);
        }

        unsubscribeQuoteStatusConsumerListener(classKey, ChannelType.CB_QUOTE_CANCEL_REPORT_BY_CLASS_V2, clientListener);
    }

    public void unsubscribeQuoteDeletedReportForProductV2(int productKey, EventChannelListener clientListener) throws Exception
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": unsubscribeQuoteDeletedReportForProductV2", GUILoggerBusinessProperty.QUOTE);
        }

        unsubscribeQuoteStatusConsumerListener(productKey, ChannelType.CB_QUOTE_CANCEL_REPORT_BY_CLASS_V2, clientListener);
    }

    public void unsubscribeQuoteFilledReportV2(EventChannelListener clientListener) throws Exception
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": unsubscribeQuoteFilledReportV2", GUILoggerBusinessProperty.QUOTE);
        }
        unsubscribeQuoteStatusConsumerListener(0, ChannelType.CB_QUOTE_FILLED_REPORT, clientListener);
    }

    public void unsubscribeQuoteFilledReportForClassV2(int classKey, EventChannelListener clientListener) throws Exception
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": unsubscribeQuoteFilledReportForClassV2", GUILoggerBusinessProperty.QUOTE);
        }
        unsubscribeQuoteStatusConsumerListener(classKey, ChannelType.CB_QUOTE_FILLED_REPORT_BY_CLASS, clientListener);
    }

    public void unsubscribeQuoteFilledReportForProductV2(int productKey, EventChannelListener clientListener) throws Exception
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": unsubscribeQuoteFilledReportForProductV2", GUILoggerBusinessProperty.QUOTE);
        }
        unsubscribeQuoteStatusConsumerListener(productKey, ChannelType.CB_QUOTE_FILLED_REPORT_BY_CLASS, clientListener);
    }


    public void unsubscribeQuoteBustReportV2(EventChannelListener clientListener) throws Exception
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": unsubscribeQuoteBustReportV2", GUILoggerBusinessProperty.QUOTE);
        }
        unsubscribeQuoteStatusConsumerListener(0, ChannelType.CB_QUOTE_BUST_REPORT_V2, clientListener);
    }

    public void unsubscribeQuoteBustReportForClassV2(int classKey, EventChannelListener clientListener) throws Exception
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": unsubscribeQuoteBustReportForClassV2", GUILoggerBusinessProperty.QUOTE);
        }
        unsubscribeQuoteStatusConsumerListener(classKey, ChannelType.CB_QUOTE_BUST_REPORT_BY_CLASS_V2, clientListener);
    }

    public void unsubscribeQuoteBustReportForProductV2(int productKey, EventChannelListener clientListener) throws Exception
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": unsubscribeQuoteBustReportForProductV2", GUILoggerBusinessProperty.QUOTE);
        }
        unsubscribeQuoteStatusConsumerListener(productKey, ChannelType.CB_QUOTE_BUST_REPORT_BY_CLASS_V2,  clientListener);
    }

    private int subscribeQuoteStatusConsumerListener(int theKey, int channelType, EventChannelListener listener)
    {
        ChannelKey key = new ChannelKey(channelType, new Integer(theKey));
        return SubscriptionManagerFactory.find().subscribe(key, listener, quoteStatusConsumerV2);
    }

    private int unsubscribeQuoteStatusConsumerListener(int theKey, int channelType, EventChannelListener listener)
    {
        ChannelKey key = new ChannelKey(channelType, new Integer(theKey));
        return SubscriptionManagerFactory.find().unsubscribe(key, listener, quoteStatusConsumerV2);
    }


    public void subscribeAllEventsQuoteStatusV2(EventChannelListener clientListener) throws Exception
    {
        subscribeQuoteStatusV2(clientListener);
        subscribeQuoteFilledReportV2(clientListener);
        subscribeQuoteBustReportV2(clientListener);
        subscribeQuoteDeletedReportV2(clientListener);
    }

    public void subscribeAllEventsQuoteStatusForClassV2(int classKey, EventChannelListener clientListener) throws Exception
    {
        subscribeQuoteStatusForClassV2(classKey, clientListener);
        subscribeQuoteFilledReportForClassV2(classKey, clientListener);
        subscribeQuoteBustReportForClassV2(classKey, clientListener);
        subscribeQuoteDeletedReportForClassV2(classKey, clientListener);
    }

    public void subscribeAllEventsQuoteStatusForProductV2(int productKey, EventChannelListener clientListener) throws Exception
    {
        subscribeQuoteStatusForProductV2(productKey, clientListener);
        subscribeQuoteFilledReportForProductV2(productKey, clientListener);
        subscribeQuoteBustReportForProductV2(productKey, clientListener);
        subscribeQuoteDeletedReportForProductV2(productKey, clientListener);
    }
    public void unsubscribeAllEventsQuoteStatusV2(EventChannelListener clientListener) throws Exception
    {
        unsubscribeQuoteStatusV2(clientListener);
        unsubscribeQuoteFilledReportV2(clientListener);
        unsubscribeQuoteBustReportV2(clientListener);
        unsubscribeQuoteDeletedReportV2(clientListener);
    }

    public void unsubscribeAllEventsQuoteStatusForClassV2(int classKey, EventChannelListener clientListener) throws Exception
    {
        unsubscribeQuoteStatusForClassV2(classKey, clientListener);
        unsubscribeQuoteFilledReportForClassV2(classKey, clientListener);
        unsubscribeQuoteBustReportForClassV2(classKey, clientListener);
        unsubscribeQuoteDeletedReportForClassV2(classKey, clientListener);
    }

    public void unsubscribeAllEventsQuoteStatusForProductV2(int productKey, EventChannelListener clientListener) throws Exception
    {
        unsubscribeQuoteStatusForProductV2(productKey, clientListener);
        unsubscribeQuoteFilledReportForProductV2(productKey, clientListener);
        unsubscribeQuoteBustReportForProductV2(productKey, clientListener);
        unsubscribeQuoteDeletedReportForProductV2(productKey, clientListener);
    }

    // QuoteV3API
    public ClassQuoteResultStructV3[] acceptQuotesForClassV3(int classKey, QuoteEntryStructV3[] quoteEntryStructs)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = new Integer(classKey);
            argObj[1] = quoteEntryStructs;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": acceptQuotesForClassV3", GUILoggerBusinessProperty.QUOTE, argObj);
        }
        ClassQuoteResultStructV3[] quoteResults = quoteQueryV3.acceptQuotesForClassV3(classKey, quoteEntryStructs);
        if(GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": acceptQuotesForClassV3 results:",GUILoggerBusinessProperty.QUOTE,
                    quoteResults);
        }
        return quoteResults;
    }
    
    public synchronized void addQuoteFilledReport(QuoteFilledReportStruct quoteFilledReportStruct)
    {
    	filledReportCache.addQuoteFilledReport(quoteFilledReportStruct);
    }

    public synchronized void removeQuoteFilledReport(QuoteFilledReportStruct quoteFilledReportStruct)
    {
    	filledReportCache.removeQuoteFilledReport(quoteFilledReportStruct);
    }
    // end QuoteV3API
    
    
    // QouteV7API
    public ClassQuoteResultStructV3[] acceptQuotesForClassV7(int classKey,QuoteEntryStructV4[] quotes)
    	throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
    		   NotAcceptedException,TransactionFailedException
    {
    	if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = new Integer(classKey);
            argObj[1] = quotes;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": acceptQuotesForClass", GUILoggerBusinessProperty.QUOTE, argObj);
        }
        return quoteQueryV7.acceptQuotesForClassV7(classKey, quotes);
    }
    
	public void acceptQuoteV7(QuoteEntryStructV4 quote)
    	throws SystemException,CommunicationException,AuthorizationException,DataValidationException,
           		NotAcceptedException,TransactionFailedException
    {
		if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": acceptQuote", GUILoggerBusinessProperty.QUOTE, quote);
        }
        quoteQueryV7.acceptQuoteV7(quote);
    }
}