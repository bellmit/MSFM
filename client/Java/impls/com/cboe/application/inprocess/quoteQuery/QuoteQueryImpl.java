package com.cboe.application.inprocess.quoteQuery;

import com.cboe.application.inprocess.shared.InProcessServicesHelper;
import com.cboe.application.quote.QuoteCache;
import com.cboe.application.quote.QuoteCacheFactory;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessor;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessorFactory;
import com.cboe.application.supplier.QuoteStatusCollectorSupplier;
import com.cboe.domain.logout.LogoutServiceFactory;
import com.cboe.domain.util.ExchangeFirmStructContainer;
import com.cboe.domain.util.SessionKeyContainer;
//import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapterFactory;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiConstants.StatusUpdateReasons;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiQuote.QuoteDetailStruct;
import com.cboe.idl.cmiQuote.RFQStruct;
import com.cboe.idl.cmiUser.UserStruct;
import com.cboe.idl.user.UserStructV2;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.ProductQueryServiceAdapter;
import com.cboe.interfaces.application.UserQuoteService;
import com.cboe.interfaces.application.UserSessionLogoutCollector;
import com.cboe.interfaces.application.inprocess.*;
import com.cboe.interfaces.application.subscription.SubscriptionService;
import com.cboe.interfaces.domain.instrumentedChannel.InstrumentedEventChannelListener;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.channel.ChannelListener;
import com.cboe.util.event.EventChannelAdapterFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import com.cboe.domain.iec.ClientIECFactory;
import com.cboe.domain.iec.ConcurrentEventChannelAdapter;

/**
 * @author Jing Chen
 */
public class QuoteQueryImpl extends BObject implements QuoteQuery, UserSessionLogoutCollector
{
    InProcessSessionManager sessionManager;
    ////////////////// member variables /////////////////////////////////
    private ProductQueryServiceAdapter pqAdapter;
    protected UserStruct                  userStruct;
    /** the current user structure */
    protected UserSessionLogoutProcessor  logoutProcessor;
    protected ExchangeFirmStructContainer       thisFirmKeyContainer;
    protected UserStructV2                userStructV2;
    protected String                      thisUserId;
    protected static final int            DEBUG_ERROR_CODE = 1;
    protected QuoteStatusCollectorSupplier  quoteStatusCollectorSupplier    = null;
    protected QuoteCache quoteCache;
    protected SubscriptionService subscriptionService;

    protected UserQuoteService userQuoteService;
    private ConcurrentEventChannelAdapter internalEventChannel; 
    
    public QuoteQueryImpl()
    {
        try
        {
        	internalEventChannel = ClientIECFactory.findConcurrentEventChannelAdapter(ClientIECFactory.CAS_INSTRUMENTED_IEC);
        }
        catch (Exception e)
        {
        	Log.exception(this, "Exception getting CAS_INSTRUMENTED_IEC!", e);
        }
    }

    public void create(String name)
    {
        super.create(name);
        try
        {
            userStruct = sessionManager.getValidUser();
            thisFirmKeyContainer = new ExchangeFirmStructContainer(userStruct.defaultProfile.executingGiveupFirm);
            thisUserId = userStruct.userId;
            quoteCache = QuoteCacheFactory.find(thisUserId);
            quoteStatusCollectorSupplier = quoteCache.getQuoteStatusCollectorSupplier();
            quoteStatusCollectorSupplier.setDynamicChannels(true);
        }
        catch(org.omg.CORBA.UserException e)
        {
            Log.exception(this, "session : " + sessionManager, e);
        }

    }// end of create

    public void setInProcessSessionManager(InProcessSessionManager session)
    {
        sessionManager = session;
        logoutProcessor = UserSessionLogoutProcessorFactory.create(this);
        EventChannelAdapterFactory.find().addChannelListener(this, logoutProcessor, session);
        LogoutServiceFactory.find().addLogoutListener(session, this);
        userQuoteService = ServicesHelper.getUserQuoteService(session);
        subscriptionService = ServicesHelper.getSubscriptionService(sessionManager);
    }

    public void subscribeQuoteStatus(QuoteStatusConsumer quoteStatusConsumer, boolean includeBookedStatus)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeQuoteStatus for " + sessionManager);
        }

        if(quoteStatusConsumer != null)
        {
            ChannelListener quoteStatusProxy =
                InProcessServicesHelper.getQuoteStatusConsumerProxy(quoteStatusConsumer, sessionManager);

            ChannelKey channelKey = new ChannelKey(ChannelType.QUOTE_FILL_REPORT, thisUserId);
            quoteStatusCollectorSupplier.addChannelListener(this, quoteStatusProxy, channelKey);

            if(includeBookedStatus)
            {
                channelKey = new ChannelKey(ChannelType.QUOTE_STATUS, thisUserId);
                quoteStatusCollectorSupplier.addChannelListener(this, quoteStatusProxy, channelKey);
            }

            channelKey = new ChannelKey(ChannelType.QUOTE_DELETE_REPORT, thisUserId);
            quoteStatusCollectorSupplier.addChannelListener(this, quoteStatusProxy, channelKey);

            channelKey = new ChannelKey(ChannelType.QUOTE_BUST_REPORT, thisUserId);
            quoteStatusCollectorSupplier.addChannelListener(this, quoteStatusProxy, channelKey);

            channelKey = new ChannelKey(ChannelType.QUOTE_STATUS_UPDATE, thisUserId);
            quoteStatusCollectorSupplier.addChannelListener(this, quoteStatusProxy, channelKey);

            userQuoteService.publishUnAckedQuotes();

            try
            {
                QuoteDetailStruct[] quoteDetails = quoteCache.getAllQuotes();
                for (int i = 0; i < quoteDetails.length; ++i)
                {
                    quoteDetails[i].statusChange = StatusUpdateReasons.QUERY;
                    quoteStatusConsumer.acceptQuoteStatus(quoteDetails[i], 0);
                }
            }
            catch (Exception e)
            {
                Log.exception("Error sending the initial quote status", e);
            }
        }
        else
        {
            Log.alarm(this, "null quoteStatusConsumer found in subscribeQuoteStatus for session:"+sessionManager);
        }
    }

    public void subscribeQuoteStatusForFirm(QuoteStatusConsumer quoteStatusConsumer)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "fix calling subscribeQuoteStatusForFirm for " + sessionManager);
        }

        if ( quoteStatusConsumer != null)
        {
            ChannelListener quoteStatusProxy = InProcessServicesHelper.getQuoteStatusConsumerProxy(quoteStatusConsumer, sessionManager);

            if(sessionManager.isTradingFirmEnabled())
            {
                subscribeQuoteStatusForTradingFirm(quoteStatusProxy);
                return;
            }

            //now subscribe to CBOE events
            ChannelKey channelKey = new ChannelKey(ChannelType.QUOTE_FILL_REPORT_BY_FIRM, thisFirmKeyContainer);
            quoteStatusCollectorSupplier.addChannelListener(this, quoteStatusProxy, channelKey);

            channelKey = new ChannelKey(ChannelType.QUOTE_BUST_REPORT_BY_FIRM, thisFirmKeyContainer);
            quoteStatusCollectorSupplier.addChannelListener(this, quoteStatusProxy, channelKey);

            subscriptionService.addFirmInterest(quoteStatusProxy);
        }
        else
        {
            Log.alarm(this, "null quoteStatusConsumer in subscribeQuoteStatusForFirm for session:"+sessionManager);
        }
    }

    private void subscribeQuoteStatusForTradingFirm(ChannelListener quoteStatusProxy)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "fix calling subscribeQuoteStatusForTradingFirm for " + sessionManager);
        }

        List<String> users = new ArrayList<String>();
        users.add(sessionManager.getUserId());
        users.addAll(sessionManager.getTradingFirmGroup());
        for(String user : users)
        {
            //now subscribe to CBOE events
            ChannelKey channelKey = new ChannelKey(ChannelType.QUOTE_FILL_REPORT_BY_TRADING_FIRM, user);
            quoteStatusCollectorSupplier.addChannelListener(this, quoteStatusProxy, channelKey);

            channelKey = new ChannelKey(ChannelType.QUOTE_BUST_REPORT_BY_TRADING_FIRM, user);
            quoteStatusCollectorSupplier.addChannelListener(this, quoteStatusProxy, channelKey);
        }

        subscriptionService.addTradingFirmInterest(quoteStatusProxy, users);
    }

    public void subscribeQuoteLockedNotification(LockedQuoteStatusConsumer lockedQuoteStatusConsumer)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        ChannelListener proxyListener = null;
        if ( lockedQuoteStatusConsumer != null)
        {
            proxyListener = InProcessServicesHelper.getQuoteNotificationConsumerProxy(lockedQuoteStatusConsumer, sessionManager);
            UserStructV2 userStructV2 = getUserStructV2();
            int userKey = userStructV2.userKey;
            ChannelKey channelKey = new ChannelKey(ChannelType.QUOTE_LOCKED_NOTIFICATION, Integer.valueOf(userKey));
            internalEventChannel.addChannelListener(this, proxyListener, channelKey);
            subscriptionService.addQuoteLockedNotificationUserInterest(proxyListener);
        }
        else
        {
            Log.alarm(this, "null quoteLockedNotification in subscribeQuoteLockedNotification for session:"+sessionManager);
        }
    }

    public void subscribeRFQ(String sessionName, int classKey, RFQConsumer rfqConsumer)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeRFQ for " + sessionManager + " classKey: " + classKey);
        }
        //verify if it is enabled
        userQuoteService.verifyUserRFQEnablementForClass(sessionName, classKey);
        if ( rfqConsumer != null)
        {
            try
            {
                ///////// add the call back consumer to the consumer list/////
                SessionKeyContainer key = new SessionKeyContainer(sessionName, classKey);
                ChannelListener proxyListener = InProcessServicesHelper.getRFQConsumerProxy(rfqConsumer, sessionManager);
                ChannelKey channelKey = new ChannelKey(ChannelType.RFQ, key);
                internalEventChannel.addChannelListener(this, proxyListener, channelKey);

                subscriptionService.addRFQClassInterest(proxyListener, sessionName, classKey);
                ProductStruct product = null;
                synchronized(rfqConsumer)
                {
                    RFQStruct[] rfqs = userQuoteService.getRFQ(sessionName, classKey);
                    for (int i = 0; i < rfqs.length; i++)
                    {
                        //PQRefactor: used to be ProductQueryManagerImpl.getProduct (synchronized)
                        //product = ProductQueryManagerImpl.getProduct(rfqs[i].productKeys.productKey);
                        product = getProductQueryServiceAdapter().getProductByKey(rfqs[i].productKeys.productKey);
                        rfqConsumer.acceptRFQ(rfqs[i], product, 0);
                    }
                }
            }
            catch( NotFoundException e )
            {
                throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
            }
        }
        else
        {
            Log.alarm(this, "null rfqConsumer in subscribeRFQ for session:"+sessionManager);
        }
    }
    public void unsubscribeRFQ(String sessionName, int classKey, RFQConsumer rfqConsumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeRFQ for " + sessionManager + " classKey: " + classKey);
        }
        userQuoteService.verifyUserRFQEnablementForClass(sessionName, classKey);
        if ( rfqConsumer != null)
        {
            ///////// add the call back consumer to the consumer list/////
            SessionKeyContainer key = new SessionKeyContainer(sessionName, classKey);
            ChannelListener proxyListener = InProcessServicesHelper.getRFQConsumerProxy(rfqConsumer, sessionManager);
            subscriptionService.removeRFQClassInterest(proxyListener, sessionName, classKey);
            ChannelKey channelKey = new ChannelKey(ChannelType.RFQ, key);
            EventChannelAdapterFactory.find().removeChannelListener(this, proxyListener, channelKey);
        }
        else
        {
            Log.alarm(this, "null rfqConsumer in unsubscribeRFQ for session:"+sessionManager);
        }
    }

    public short getQuoteStatus(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, NotFoundException
    {
        short status;
        try
        {
            status = userQuoteService.getQuote(sessionName, productKey).statusChange;
        }
        catch(DataValidationException e)
        {
            throw ExceptionBuilder.notFoundException("quote not found for sessionName:"+sessionName + " productKey:"+productKey, 1);
        }
        return status;
    }

    protected UserStructV2 getUserStructV2()
            throws DataValidationException, SystemException, CommunicationException, AuthorizationException
    {
        if (userStructV2 == null)
        {
            try {
                userStructV2 = ServicesHelper.getUserService().getUserInformationV2(thisUserId);
            } catch (NotFoundException e)
            {
                Log.exception(this, e);
                throw ExceptionBuilder.dataValidationException("Could not find user key", DataValidationCodes.INVALID_USER);
            }
        }
        return userStructV2;
    }

    public void acceptUserSessionLogout()
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptUserSessionLogout for " + sessionManager);
        }
        internalEventChannel.removeListenerGroup(this);
        EventChannelAdapterFactory.find().removeListenerGroup(this);
        LogoutServiceFactory.find().logoutComplete(sessionManager,this);
        quoteStatusCollectorSupplier.removeListenerGroup(this);
        quoteStatusCollectorSupplier = null;

        logoutProcessor.setParent(null);
        logoutProcessor = null;
        // Clean up instance variables.
        userQuoteService = null;
        pqAdapter = null;
        sessionManager = null;
        userStruct = null;
    }

    protected ProductQueryServiceAdapter getProductQueryServiceAdapter()
    {
        if(pqAdapter == null)
        {
            pqAdapter = ServicesHelper.getProductQueryServiceAdapter();
        }
        return pqAdapter;
    }
/* @todo SANTHAN
    public void subscribeMarketAlertForClass(String session, int classKey, MarketAlertConsumer marketAlertConsumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        Log.debug(this, "calling subscribeMarketAlertForClass for " + classKey);
//        userQuoteService.verifyMarketAlertEnablementForClass(session,classKey);
        if(marketAlertConsumer != null)
        {
            SessionKeyContainer sessionKey = new SessionKeyContainer(session, classKey);
            ChannelListener listener = InProcessServicesHelper.getMarketAlertConsumerProxy(marketAlertConsumer,sessionManager);
            ChannelKey channelKey = new ChannelKey(ChannelKey.ALL_MARKET_ALERT, sessionKey);
            EventChannelAdapterFactory.find().addChannelListener(this, listener, channelKey);
            ServicesHelper.getSubscriptionManager().addMarketAlertClassInterest(listener, thisUserId, session, classKey);
        }
        else
        {
            Log.alarm(this, "null marketAlertConsumer found in subscribeMarketAlertForClass for session:");
        }
    }

    public void unsubscribeMarketAlertForClass(String session, int classKey, MarketAlertConsumer marketAlertConsumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        Log.debug(this, "calling unsubscribeMarketAlertForClass for " + sessionManager + " classKey: " + classKey);
        userQuoteService.verifyUserRFQEnablementForClass(session, classKey);
        if ( marketAlertConsumer != null)
        {
            ///////// add the call back consumer to the consumer list/////
            SessionKeyContainer key = new SessionKeyContainer(session, classKey);
            ChannelListener proxyListener = InProcessServicesHelper.getMarketAlertConsumerProxy(marketAlertConsumer, sessionManager);
            ServicesHelper.getSubscriptionManager().removeMarketAlertClassInterest(proxyListener, thisUserId, session, classKey);
            ChannelKey channelKey = new ChannelKey(ChannelType.MARKET_ALERT_BY_CLASS, key);
            EventChannelAdapterFactory.find().removeChannelListener(this, proxyListener, channelKey);
        }
        else
        {
            Log.alarm(this, "null rfqConsumer in unsubscribeRFQ for session:"+sessionManager);
        }
    }
 */
}
