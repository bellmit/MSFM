package com.cboe.application.quote;

import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.*;
import com.cboe.interfaces.application.subscription.SubscriptionService;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.application.shared.consumer.*;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.supplier.*;
import com.cboe.domain.util.*;
import com.cboe.domain.iec.ClientIECFactory;
import com.cboe.domain.iec.ConcurrentEventChannelAdapter;
import com.cboe.domain.logout.LogoutServiceFactory;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.ChannelKey;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.channel.ChannelListener;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.quote.QuoteInfoStruct;
import com.cboe.idl.cmiOrder.BustReportStruct;
import com.cboe.idl.cmiOrder.FilledReportStruct;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiConstants.StatusUpdateReasons;
import com.cboe.idl.cmiUser.SessionProfileUserStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.exceptions.*;
import com.cboe.domain.iec.ConcurrentEventChannelAdapter;
import com.cboe.domain.iec.ClientIECFactory;


/**
 * User: huange
 * Date: May 4, 2004
 */
public class UserQuoteBasicCollectorImpl extends BObject implements QuoteNotificationCollector, RFQCollector, QuoteStatusCollector, UserSessionLogoutCollector {
    ////////////////// member variables /////////////////////////////////
    protected SessionManager                    currentSession;
    protected SessionProfileUserStruct          userStruct;
    protected RFQProcessor                      rfqProcessor;
    protected RFQSupplier                       rfqSupplier;
    protected RFQV2Supplier                     rfqV2Supplier;
    protected QuoteStatusSupplier               quoteStatusSupplier = null;
    protected QuoteStatusV2Supplier             quoteStatusV2Supplier = null;
    private UserSessionLogoutProcessor          logoutProcessor;
    private ProductQueryServiceAdapter          pqAdapter;
    protected ExchangeFirmStruct                thisFirmKeyStruct;
    protected ExchangeFirmStructContainer       thisFirmKeyContainer;
    private SessionProfileUserStructV2          userStructV2;
    protected QuoteNotificationSupplier         quoteNotificationSupplier = null;
    protected QuoteNotificationProcessor        quoteNotificationProcessor = null;
    protected String                            thisUserId;
    protected int                               thisUserKey;
    protected ChannelListener                   quoteStatusProxy                = null;
    protected ChannelListener                   quoteNotificationProxy          = null;
    protected QuoteStatusCollectorSupplier      quoteStatusCollectorSupplier    = null;
    protected QuoteNotificationCollectorSupplier  quoteNotificationCollectorSupplier    = null;
    protected QuoteCache                        quoteCache;
    protected UserQuoteService                  userQuoteService;
    protected SubscriptionService               subscriptionService;
    protected ConcurrentEventChannelAdapter internalEventChannel;

    /**
     * Initialize the services. This has to be called after current session is set.
     */
    public void create(String name)
    {
        super.create(name);
        try
        {
        	internalEventChannel = ClientIECFactory.findConcurrentEventChannelAdapter(ClientIECFactory.CAS_INSTRUMENTED_IEC);
        }
        catch (Exception e)
        {
        	Log.exception("Exception getting CAS_INSTRUMENTED_IEC!", e);
        }        

        logoutProcessor = UserSessionLogoutProcessorFactory.create(this);
        EventChannelAdapterFactory.find().addChannelListener(this, logoutProcessor, currentSession);
        LogoutServiceFactory.find().addLogoutListener(currentSession, this);

        quoteStatusProxy = ServicesHelper.getQuoteStatusCollectorProxy(this, currentSession);

        userQuoteService = ServicesHelper.getUserQuoteService(currentSession);

        rfqProcessor = RFQProcessorFactory.create(this);
        quoteNotificationProcessor = QuoteNotificationProcessorFactory.create(this);

        rfqSupplier = RFQSupplierFactory.create();
        rfqV2Supplier = RFQV2SupplierFactory.create();
        quoteStatusSupplier = QuoteStatusSupplierFactory.create(currentSession);
        quoteStatusV2Supplier = QuoteStatusV2SupplierFactory.create(currentSession);
        quoteNotificationSupplier = QuoteNotificationSupplierFactory.create();

        // Make Sure all suppliers are dynamic channels
        rfqSupplier.setDynamicChannels(true);
        rfqV2Supplier.setDynamicChannels(true);
        quoteStatusSupplier.setDynamicChannels(true);
        quoteStatusV2Supplier.setDynamicChannels(true);
        quoteNotificationSupplier.setDynamicChannels(true);

        try
        {
            userStruct = currentSession.getValidSessionProfileUser();
            thisUserId = userStruct.userId;
            thisFirmKeyStruct = userStruct.defaultProfile.executingGiveupFirm;
            thisFirmKeyContainer = new ExchangeFirmStructContainer(thisFirmKeyStruct);

            userStructV2 = currentSession.getValidSessionProfileUserV2();
            thisUserKey = userStructV2.userKey;

            quoteCache = QuoteCacheFactory.find(thisUserId);
            quoteStatusCollectorSupplier = quoteCache.getQuoteStatusCollectorSupplier();
            quoteStatusCollectorSupplier.setDynamicChannels(true);

            subscribeForQuoteStatus(thisUserId);             // subscribe for quote status updates

        }
        catch(org.omg.CORBA.UserException e)
        {
            Log.exception(this, "session : " + currentSession, e);
            Log.alarm(this, ": error in servcie initialization");
        }
    }
    /**
     * sets the current session manager.
     * This needs to be called after the creation of this object in the home impl
     *
     * @param theSession the current session manager
     */
    public void setSessionManager(SessionManager theSession)
    {
        currentSession = theSession;
        subscriptionService = ServicesHelper.getSubscriptionService(currentSession);
    }

    /** Get the current session manager. Used by SessionBasedCollector. */ 
    public BaseSessionManager getSessionManager()
    {
        return currentSession;
    }

    /**
      * Helper method to subscribe to the cache supplier
      * @param channelKey The hashable index key.
      */
    protected void addListenerForSupplier(ChannelKey channelKey)
    {
        quoteStatusCollectorSupplier.addChannelListener(this, quoteStatusProxy, channelKey);
    }

    /**
     * Helper method to subscribe to the cache supplier
     * @param channelKey The hashable index key.
     */
   protected void removeListenerForSupplier(ChannelKey channelKey)
   {
       quoteStatusCollectorSupplier.removeChannelListener(this, quoteStatusProxy, channelKey);
   }

    /**
     * dispatches the CB_RFQ event to the event listener callbacks
     *
     * @param rfq the prodcut keys
     */
    public void acceptRFQ(com.cboe.idl.cmiQuote.RFQStruct rfq)
    {
        if (Log.isDebugOn()) {Log.debug(this, "calling acceptRFQ for " + currentSession + " productKey:" + rfq.productKeys.productKey);}
        ChannelKey key = new ChannelKey(ChannelType.CB_RFQ, new UserSessionKey(currentSession, new SessionKeyContainer(rfq.sessionName, rfq.productKeys.classKey)));
        ChannelEvent event = internalEventChannel.getChannelEvent(this, key, rfq);
        rfqSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_RFQ_V2, new UserSessionKey(currentSession, new SessionKeyContainer(rfq.sessionName, rfq.productKeys.classKey)));
        event = internalEventChannel.getChannelEvent(this, key, rfq);
        rfqV2Supplier.dispatch(event);
    }

    /**
    * dispatches the quote busted event to the event listener callbacks
    * including the all CB_QUOTE and CB_ALL_QUOTES
    * listeners
    */
    public void acceptQuoteBustReport(QuoteInfoStruct quoteInfo, BustReportStruct[] bustedData, short statusChange )
    {
       if (Log.isDebugOn()) {Log.debug(this, "calling acceptQuoteBustReport for " + currentSession + " quoteKey:" + quoteInfo.quoteKey);}
       QuoteBustReportStruct bustedQuote;
       try
       {
            bustedQuote = buildBustReport(quoteInfo, bustedData, statusChange);

       }
       catch(org.omg.CORBA.UserException e)
       {
           // if error on getting product info, return.
           Log.exception(this, "session : " + currentSession, e);
           return;
       }

       QuoteInfoBustReportContainer container= new QuoteInfoBustReportContainer(quoteInfo, bustedQuote);

       publishBustedReport(quoteInfo.userId, container);
       publishBustedReportByUserByClass(quoteInfo.userId, container, bustedQuote.productKeys.classKey);
       publishQuoteReportStatusChange(statusChange, quoteCache.getQuoteByQuoteKey(quoteInfo.quoteKey), StatusUpdateReasons.BUST);

       if (!thisUserId.equals(quoteInfo.userId))
       {
           publishBustedReportByFirm(quoteInfo.firm, container);
           publishBustedReportByFirmByClass(quoteInfo.firm, container, bustedQuote.productKeys.classKey);
           publishBustedReportByTradingFirm(quoteInfo.userId, container);
       }

    }

    /**
     * dispatches the quote cancelled event to the event listener callbacks
     * including the all CB_QUOTE and CB_ALL_QUOTES
     * listeners
     *
     * @param deletedQuotes quotes to be cancelled
     */
    public void acceptQuoteDeleteReports(QuoteDeleteReportWrapper[] deletedQuotes)
    {
        int numQuotes = deletedQuotes.length;
        if ( numQuotes == 0)
            return;

        // log the event received message
        if (Log.isDebugOn()) {Log.debug(this, "calling acceptQuoteDeleteReports for " + numQuotes + " quotes for " + currentSession);}

        publishCancleReport(userStruct.userId, deletedQuotes);
        publishCancleReportByUserByClass(userStruct.userId, deletedQuotes, deletedQuotes[0].getQuoteDetailStruct().productKeys.classKey);

        // Extract an array of QuoteDetailStruct objects that we can pass to the
        QuoteDetailStruct[] details = new QuoteDetailStruct[numQuotes];
        for (int i = 0; i < numQuotes; ++i)
        {
            details[i] = deletedQuotes[i].getQuoteDetailStruct();
        }
        publishQuoteStatusOnlyToV1(details);
    }

    /**
     * @param quoteDetails The quotes that are being accepted.
     */
    public void acceptAddQuotes(QuoteDetailStruct[] quoteDetails)
    {
        if (Log.isDebugOn()) {Log.debug(this, "calling acceptAddQuotes for " + currentSession + " (" + quoteDetails.length + " quotes)");}

        if (quoteDetails[0].statusChange == StatusUpdateReasons.NEW )
        {
            publishQuoteStatusOnlyToV1(quoteDetails);
            publishBookedStatusOnlyToV2(quoteDetails, true);
        }
        else
        {
            publishQuoteStatus(quoteDetails, true); // true == "also publish by class"
        }
    }

    public void acceptQuoteUpdate(QuoteDetailStruct[] quoteDetails)
    {
        publishQuoteStatus(quoteDetails, true);
    }

    /**
     * dispatches the quote filled event to the event listener callbacks
     * including the all CB_FILLED_REPORT, CB_FILLED_REPORTBYUSER, CB_QUOTE and CB_ALL_QUOTES
     * listeners
     */
    public void acceptQuoteFillReport(QuoteInfoStruct quoteInfo, FilledReportStruct[] filledData, short statusChange )
    {
        if (Log.isDebugOn()) {Log.debug(this, "calling acceptQuoteFillReport for " + currentSession + " quoteKey:" + quoteInfo.quoteKey);}

        QuoteFilledReportStruct filledQuote;
        try
        {
            filledQuote = buildFillReport(quoteInfo, filledData, statusChange);

        }
        catch(org.omg.CORBA.UserException e)
        {
            // if error on getting product info, return.
            Log.exception(this, "session : " + currentSession, e);
            return;
        }

        QuoteInfoFilledReportContainer container= new QuoteInfoFilledReportContainer(quoteInfo, filledQuote);

        // quoeKey==0 => MMTN Quotefill report.
        if(quoteInfo.quoteKey==0)
        {
            publishMMTNFilledReport(quoteInfo.userId,container,filledQuote.productKeys.classKey);
        }
        else
        {

            publishFilledReport(quoteInfo.userId, container);
            publishFilledReportByUserByClass(quoteInfo.userId, container, filledQuote.productKeys.classKey);
            publishQuoteReportStatusChange(statusChange, quoteCache.getQuoteByQuoteKey(quoteInfo.quoteKey), StatusUpdateReasons.FILL);
            if (!thisUserId.equals(quoteInfo.userId))
            {
                publishFilledReportByFirm(quoteInfo.firm, container);
                publishFilledReportByFirmByClass(quoteInfo.firm, container, filledQuote.productKeys.classKey);
                publishFilledReportByTradingFirm(quoteInfo.userId, container);
            }
        }
    }

   protected ProductStruct getProductStruct(int productKey)
            throws SystemException, CommunicationException, DataValidationException, NotFoundException, AuthorizationException
    {
//PQRefactor: this used to be synchronized
//        ProductStruct product = ProductQueryManagerImpl.getProduct(productKey);
        ProductStruct product = getProductQueryServiceAdapter().getProductByKey(productKey);
        return product;
    }


    /**
     * Publishes the  quote booked status change event only to V2 booked status channel.
     * @param quoteDetails The quotes to be published.
     */
    protected void publishBookedStatusOnlyToV2(QuoteDetailStruct[] quoteDetails, boolean publishByClass )
    {
        if ((quoteDetails != null) && (quoteDetails.length > 0))
        {
            ChannelKey key;
            ChannelEvent event;

            //!! now notify the callbacks (getAllQuotes)on the new quote detail info
            key = new ChannelKey(ChannelType.CB_QUOTES_BOOKED_V2, quoteDetails[0].quote.userId);
            event = internalEventChannel.getChannelEvent(this, key, quoteDetails);
            quoteStatusV2Supplier.dispatch(event);

            if (publishByClass)
            {
                //!! now notify the callbacks (subscribeQuoteStatus on classes)on the new quote detail info
                key = new ChannelKey(ChannelType.CB_QUOTES_BOOKED_BY_CLASS_V2, Integer.valueOf( quoteDetails[0].productKeys.classKey));
                event = internalEventChannel.getChannelEvent(this, key, quoteDetails);
                quoteStatusV2Supplier.dispatch(event);
            }
        }
    } // end of publishQuoteStatusOnlyToV1

    /**
     * Publishes the quote status change event with the Internal Event Channel.
     * This method will always publish the given quotes on the CB_ALL_QUOTES
     * channel.  If requested (via the 'publishByClass' parameter), this method
     * will also publish the given quotes on the CB_QUOTE_BY_CLASS channel.
     *
     * NOTE: If you request that this method also publish the quotes on the
     * CB_QUOTE_BY_CLASS channel, it is assumed that *ALL* quotes in the given
     * array contain the same class key!!!
     *
     * @param quoteDetails The quote detail struct array to be published.
     *
     * @param publishByClass The flag that tells this method whether or not it
     * should publish the given quotes on the CB_QUOTE_BY_CLASS channel.
     */
    protected void publishQuoteStatus(QuoteDetailStruct[] quoteDetails, boolean publishByClass )
    {
        publishAllQuotes(quoteDetails);
        if (publishByClass)
        {
            publishQuoteStatusByClass(quoteDetails);
        }
    }

    protected void subscribeForQuoteStatus(String userId)
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.QUOTE_FILL_REPORT, userId);
        addListenerForSupplier(channelKey);

        channelKey = new ChannelKey(ChannelType.QUOTE_STATUS, userId);
        addListenerForSupplier(channelKey);

        channelKey = new ChannelKey(ChannelType.QUOTE_STATUS_UPDATE, userId);
        addListenerForSupplier(channelKey);

        channelKey = new ChannelKey(ChannelType.QUOTE_DELETE_REPORT, userId);
        addListenerForSupplier(channelKey);

        channelKey = new ChannelKey(ChannelType.QUOTE_BUST_REPORT, userId);
        addListenerForSupplier(channelKey);
    }

    protected void unsubscribeForQuoteStatus(String userId)
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.QUOTE_FILL_REPORT, userId);
        removeListenerForSupplier(channelKey);

        channelKey = new ChannelKey(ChannelType.QUOTE_STATUS, userId);
        removeListenerForSupplier(channelKey);

        channelKey = new ChannelKey(ChannelType.QUOTE_STATUS_UPDATE, userId);
        removeListenerForSupplier(channelKey);

        channelKey = new ChannelKey(ChannelType.QUOTE_DELETE_REPORT, userId);
        removeListenerForSupplier(channelKey);

        channelKey = new ChannelKey(ChannelType.QUOTE_BUST_REPORT, userId);
        removeListenerForSupplier(channelKey);
    }

    public void acceptUserSessionLogout() {
        if (Log.isDebugOn()) {Log.debug(this, "calling acceptUserSessionLogout for " + currentSession);}
        try
        {
        	unsubscribeForQuoteStatus(thisUserId);
        }
        catch (Exception e)
        {        
        }
        try
        {
        	EventChannelAdapterFactory.find().removeListenerGroup(this);
        }
        catch (Exception e)
        {        
        }

        // clean up the suppliers
        rfqSupplier.removeListenerGroup(this);
        rfqV2Supplier.removeListenerGroup(this);
        quoteStatusCollectorSupplier.removeListenerGroup(this);
        quoteStatusSupplier.removeListenerGroup(this);
        QuoteStatusSupplierFactory.remove(currentSession);
        quoteStatusV2Supplier.removeListenerGroup(this);
        QuoteStatusV2SupplierFactory.remove(currentSession);
        quoteNotificationSupplier.removeListenerGroup(this);
        try
        {
        	LogoutServiceFactory.find().logoutComplete(currentSession,this);
        }
        catch (Exception e)
        {        	
        }

        quoteStatusSupplier = null;
        rfqSupplier = null;
        rfqV2Supplier = null;
        quoteStatusCollectorSupplier = null;
        quoteStatusV2Supplier = null;
        quoteStatusProxy = null;
        quoteNotificationSupplier = null;
        quoteNotificationProxy = null;
        quoteNotificationProcessor.setParent(null);
        quoteNotificationProcessor = null;

        // Clean up the processors
        rfqProcessor.setParent(null);
        rfqProcessor = null;
        logoutProcessor.setParent(null);
        logoutProcessor = null;

        // Clean up instance variables.
        userQuoteService = null;
        currentSession = null;
        userStruct = null;
        pqAdapter = null;
    }

    public void acceptQuoteNotification(LockNotificationStruct [] quoteLocks)
    {
        if (Log.isDebugOn()) {Log.debug(this, "calling acceptQuoteNotification for " + currentSession + " quoteLocks.length:" + quoteLocks.length);}

        // channel key should be the same as in subscribeQuteLockNotification
        ChannelKey key = new ChannelKey(ChannelType.CB_QUOTE_LOCKED_NOTIFICATION, userStruct.userId);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, key, quoteLocks);
        quoteNotificationSupplier.dispatch(event);

        UserClassContainer userClassKey;

        userClassKey = new UserClassContainer(thisUserId, quoteLocks[0].classKey);

        key = new ChannelKey(ChannelType.CB_QUOTE_LOCKED_NOTIFICATION_BY_CLASS, userClassKey);
        event = internalEventChannel.getChannelEvent(this, key, quoteLocks);
        quoteNotificationSupplier.dispatch(event);

    }

    protected ProductQueryServiceAdapter getProductQueryServiceAdapter()
    {
        if(pqAdapter == null)
        {
            pqAdapter = ServicesHelper.getProductQueryServiceAdapter();
        }
        return pqAdapter;
    }

    protected void queryQuoteStatus(boolean publish)
    {
        if ( publish )
        {
            QuoteDetailStruct[] quoteDetails = quoteCache.getAllQuotes();
            for (int i = 0; i < quoteDetails.length; ++i)
            {
                quoteDetails[i].statusChange = StatusUpdateReasons.QUERY;
            }
            publishQuoteStatus(quoteDetails, false);
        }

    }

    protected void queryQuoteStatusByClass(int classKey, boolean publish)
    {
        if ( publish )
        {
            QuoteDetailStruct[] quoteDetails = quoteCache.getQuotesForClass(classKey);
            for (int i = 0; i < quoteDetails.length; ++i)
            {
                 quoteDetails[i].statusChange = StatusUpdateReasons.QUERY;
            }
            publishQuoteStatusByClass(quoteDetails);
        }
    }

    protected void publishQuoteStatusByClass(QuoteDetailStruct[] quoteDetails )
    {
        if ((quoteDetails != null) && (quoteDetails.length > 0))
        {
            ChannelKey key = new ChannelKey(ChannelType.CB_QUOTE_BY_CLASS_V2, Integer.valueOf(quoteDetails[0].productKeys.classKey));
            ChannelEvent event = internalEventChannel.getChannelEvent(this, key, quoteDetails);
            quoteStatusV2Supplier.dispatch(event);
        }
    }

    protected void publishAllQuotes(QuoteDetailStruct[] quoteDetails )
    {
        if ((quoteDetails != null) && (quoteDetails.length > 0))
        {

            ChannelKey key = null;
            ChannelEvent event = null;

            //!! now notify the callbacks (getAllQuotes)on the new quote detail info
            key = new ChannelKey(ChannelType.CB_ALL_QUOTES, quoteDetails[0].quote.userId);
            event = internalEventChannel.getChannelEvent(this, key, quoteDetails);
            quoteStatusSupplier.dispatch(event);

            //!! now notify the callbacks (getAllQuotes)on the new quote detail info
            key = new ChannelKey(ChannelType.CB_ALL_QUOTES_V2, quoteDetails[0].quote.userId);
            event = internalEventChannel.getChannelEvent(this, key, quoteDetails);
            quoteStatusV2Supplier.dispatch(event);
        }
    }

    protected  QuoteBustReportStruct buildBustReport(QuoteInfoStruct quoteInfo, BustReportStruct[] bustedData, short statusChange )
            throws SystemException, CommunicationException, DataValidationException, NotFoundException, AuthorizationException

    {
        QuoteBustReportStruct bustedQuote = new QuoteBustReportStruct();
        ProductStruct productStruct = getProductStruct(quoteInfo.productKey);
        bustedQuote.bustedReport = bustedData;
        bustedQuote.productKeys = productStruct.productKeys;
        bustedQuote.productName = productStruct.productName;
        bustedQuote.quoteKey = quoteInfo.quoteKey;
        bustedQuote.statusChange = statusChange;
        return bustedQuote;
    }

    protected void publishBustedReport(Object channekKey, Object channelData)
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT, channekKey);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, key, channelData);
        quoteStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_V2, channekKey);
        event = internalEventChannel.getChannelEvent(this, key, channelData);
        quoteStatusV2Supplier.dispatch(event);
    }

    protected void publishBustedReportByFirm(ExchangeFirmStruct firm, Object channelData)
    {
            ChannelKey key = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_BY_FIRM,
                                                        new ExchangeFirmStructContainer(firm));
            ChannelEvent event = internalEventChannel.getChannelEvent(this, key, channelData);
            quoteStatusSupplier.dispatch(event);

            key = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_BY_FIRM_V2,
                                                        new ExchangeFirmStructContainer(firm));
            event = internalEventChannel.getChannelEvent(this, key, channelData);
            quoteStatusV2Supplier.dispatch(event);

    }
    protected void publishBustedReportByFirmByClass(ExchangeFirmStruct firm, Object channelData, int classKey)
    {

            ChannelKey key = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_BY_FIRM_BY_CLASS_V2, new FirmClassContainer(firm, classKey));
            ChannelEvent event = internalEventChannel.getChannelEvent(this, key, channelData);
            quoteStatusV2Supplier.dispatch(event);

    }

    protected void publishBustedReportByTradingFirm(String userId, Object channelData)
    {
            ChannelKey key = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_BY_TRADING_FIRM, userId);
            ChannelEvent event = internalEventChannel.getChannelEvent(this, key, channelData);
            quoteStatusSupplier.dispatch(event);

            key = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_BY_TRADING_FIRM, userId);
            event = internalEventChannel.getChannelEvent(this, key, channelData);
            quoteStatusV2Supplier.dispatch(event);
    }

    protected void publishBustedReportByUserByClass(String userId, Object channelData, int classKey)
    {
            ChannelKey key = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_BY_CLASS_V2, Integer.valueOf(classKey));
            ChannelEvent event = internalEventChannel.getChannelEvent(this, key, channelData);
            quoteStatusV2Supplier.dispatch(event);
    }


    protected void publishQuoteReportStatusChange(short statusChange, QuoteDetailStruct theQuote, short reportType)
    {
        if ( theQuote!= null )
        {
            theQuote.statusChange =
                    (statusChange == StatusUpdateReasons.POSSIBLE_RESEND) ? StatusUpdateReasons.POSSIBLE_RESEND :reportType;
             publishQuoteStatus(theQuote);
        }
    }

    protected void publishQuoteStatus(QuoteDetailStruct theQuote)
    {
        if ( theQuote!= null )
        {
            ChannelKey key = new ChannelKey(ChannelType.CB_ALL_QUOTES, theQuote.quote.userId);
            QuoteDetailStruct[] quoteDetails = new QuoteDetailStruct[1];
            quoteDetails[0] = theQuote;
            ChannelEvent event = internalEventChannel.getChannelEvent(this, key, quoteDetails);
            quoteStatusSupplier.dispatch(event);
        }
    }

    protected void publishQuoteStatusOnlyToV1(QuoteDetailStruct[] theQuotes)
    {
        if ( theQuotes.length!= 0 )
        {
            ChannelKey key = new ChannelKey(ChannelType.CB_ALL_QUOTES, theQuotes[0].quote.userId);
            ChannelEvent event = internalEventChannel.getChannelEvent(this, key, theQuotes);
            quoteStatusSupplier.dispatch(event);
        }
    }

    protected void publishCancleReport(Object channekKey, Object channelData)
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_QUOTE_CANCEL_REPORT, userStruct.userId);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, key, channelData);
        quoteStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_QUOTE_CANCEL_REPORT_V2, userStruct.userId);
        event = internalEventChannel.getChannelEvent(this, key, channelData);
        quoteStatusV2Supplier.dispatch(event);
    }

    protected void publishCancleReportByUserByClass(String userId, Object channelData, int classKey)
    {
        ChannelKey key ;
        ChannelEvent event ;
        key = new ChannelKey(ChannelType.CB_QUOTE_CANCEL_REPORT_BY_CLASS, Integer.valueOf(classKey));
        event = internalEventChannel.getChannelEvent(this, key, channelData);
        quoteStatusSupplier.dispatch(event);

        //!! now notify the callbacks (subscribeQuoteStatus on classes)on the new quote detail info
        key = new ChannelKey(ChannelType.CB_QUOTE_CANCEL_REPORT_BY_CLASS_V2, Integer.valueOf(classKey));
        event = internalEventChannel.getChannelEvent(this, key, channelData);
        quoteStatusV2Supplier.dispatch(event);

    }


    protected  QuoteFilledReportStruct buildFillReport(QuoteInfoStruct quoteInfo, FilledReportStruct[] filledData, short statusChange )
            throws SystemException, CommunicationException, DataValidationException, NotFoundException, AuthorizationException

    {
        QuoteFilledReportStruct filledQuote = new QuoteFilledReportStruct();
        ProductStruct productStruct = getProductStruct(quoteInfo.productKey);
        filledQuote.filledReport = filledData;
        filledQuote.productKeys = productStruct.productKeys;
        filledQuote.productName = productStruct.productName;
        filledQuote.quoteKey = quoteInfo.quoteKey;
        filledQuote.statusChange = statusChange;
        return filledQuote;
    }

    protected void publishFilledReport(Object channekKey, Object channelData)
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT, channekKey);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, key, channelData);
        quoteStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_V2, channekKey);
        event = internalEventChannel.getChannelEvent(this, key, channelData);
        quoteStatusV2Supplier.dispatch(event);


    }

    protected void publishMMTNFilledReport(String userId, QuoteInfoFilledReportContainer channelData,int classKey)
    {

        //dispatch event for class subscription
        ChannelKey key = new ChannelKey(ChannelType.CB_MMHH_QUOTE_FILL_REPORT_V2, classKey);
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, channelData);
        quoteStatusV2Supplier.dispatch(event);
        //dispatch event for catch-all subscription.
        key = new ChannelKey(ChannelType.CB_MMHH_QUOTE_FILL_REPORT_V2,0);
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, channelData);
        quoteStatusV2Supplier.dispatch(event);

    }

    protected void publishFilledReportByFirm(ExchangeFirmStruct firm, Object channelData)
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_BY_FIRM, new ExchangeFirmStructContainer(firm));
        ChannelEvent event = internalEventChannel.getChannelEvent(this, key, channelData);
        quoteStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_BY_FIRM_V2, new ExchangeFirmStructContainer(firm));
        event = internalEventChannel.getChannelEvent(this, key, channelData);
        quoteStatusV2Supplier.dispatch(event);

    }
    protected void publishFilledReportByFirmByClass(ExchangeFirmStruct firm, Object channelData, int classKey)
    {

            ChannelKey key = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_BY_FIRM_BY_CLASS_V2, new FirmClassContainer(firm, classKey));
            ChannelEvent event = internalEventChannel.getChannelEvent(this, key, channelData);
            quoteStatusV2Supplier.dispatch(event);

    }

    protected void publishFilledReportByTradingFirm(String userId, Object channelData)
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_BY_TRADING_FIRM, userId);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, key, channelData);
        quoteStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_BY_TRADING_FIRM, userId);
        event = internalEventChannel.getChannelEvent(this, key, channelData);
        quoteStatusV2Supplier.dispatch(event);
    }

    protected void publishFilledReportByUserByClass(String userId, Object channelData, int classKey)
    {
            ChannelKey key = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_BY_CLASS_V2, Integer.valueOf(classKey));
            ChannelEvent event = internalEventChannel.getChannelEvent(this, key, channelData);
            quoteStatusV2Supplier.dispatch(event);
    }

    protected int getClassKey(int productKey)
             throws SystemException, CommunicationException, AuthorizationException, DataValidationException

     {
         try{
         return getProductQueryServiceAdapter().getProductByKey(productKey).productKeys.classKey;
         } catch ( NotFoundException nofe)
         {
             throw ExceptionBuilder.dataValidationException("invalid product", DataValidationCodes.INVALID_PRODUCT);
         }
     }


}
