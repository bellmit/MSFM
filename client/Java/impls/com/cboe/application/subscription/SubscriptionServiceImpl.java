package com.cboe.application.subscription;

import com.cboe.application.order.FixOrderQueryCacheFactory;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.order.OrderQueryCacheFactory;
import com.cboe.application.quote.QuoteCacheFactory;
import com.cboe.domain.util.ExchangeFirmStructContainer;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.domain.util.TradingFirmGroupContainer;
import com.cboe.domain.util.fixUtil.FixUtilConstants;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.interfaces.application.subscription.Subscription;
import com.cboe.interfaces.application.subscription.SubscriptionCollectionService;
import com.cboe.interfaces.application.subscription.SubscriptionService;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.interfaces.domain.TradingFirmGroupWrapper;
import com.cboe.util.ExceptionBuilder;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * @author Jing Chen
 */
public class SubscriptionServiceImpl extends SubscriptionGroupImpl implements SubscriptionService
{
    protected Map subscriptionCollections;
    protected BaseSessionManager sessionManager;
    protected SubscriptionCollectionService subscriptionCollectionService;
    public static final String LOGIN_ORDER_PUBLISH_DELAY = "LoginOrderPublishDelayTime";
    public static final String LOGIN_ORDER_PUBLISH_DELAY_DEFAULT = "4000";
    private long loginOrderPublishDelayTime=4000;


    public SubscriptionServiceImpl(BaseSessionManager sessionManager)
            throws Exception
    {
        super();
        subscriptionCollections = new HashMap();
        this.sessionManager = sessionManager;
        subscriptionCollectionService = ServicesHelper.getSubscriptionCollectionService();
        try
        {
            String value = System.getProperty(LOGIN_ORDER_PUBLISH_DELAY, LOGIN_ORDER_PUBLISH_DELAY_DEFAULT);
            if(value!=null && value.trim().length()>0)
            {
                loginOrderPublishDelayTime = Long.parseLong(value);
            }
        }
        catch (Exception e)
        {
            Log.exception(e);
        }


    }

   public void addUserInterest(Object source)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        String userId = sessionManager.getUserId();
        if (FixUtilConstants.CLIENT_TYPES.FIXCAS.equals(System.getenv(FixUtilConstants.CLIENT_TYPES.CLIENT_TYPE))){
            FixOrderQueryCacheFactory.create(userId);
        } else {
            OrderQueryCacheFactory.create(userId);
        }
        QuoteCacheFactory.create(userId);
        subscriptionCollectionService.getUserSubscriptionCollection(sessionManager.getValidSessionProfileUserV2())
                .addDefaultSubscriptions(this, source);
        StringBuilder delay = new StringBuilder(userId.length()+90);
        delay.append("PITS 9106 Delay ").append(loginOrderPublishDelayTime)
             .append(" ms before calling publishOrdersForUser:").append(userId)
             .append(" sessionKey:").append(sessionManager.getSessionKey());
        Log.information(delay.toString());
        try
        {
            Thread.sleep(loginOrderPublishDelayTime);
        }
        catch (java.lang.InterruptedException ex)
        {
            Log.information("PITS 9106 Delay was interrupted for user:" + userId + " sessionKey:" + sessionManager.getSessionKey());
        }
        ServicesHelper.getOrderHandlingService().publishOrdersForUser(userId);
    }

    public void addTradingFirmInterest(Object source, List<String> users)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        String userId = sessionManager.getUserId();
        if (FixUtilConstants.CLIENT_TYPES.FIXCAS.equals(System.getenv(FixUtilConstants.CLIENT_TYPES.CLIENT_TYPE))){
            FixOrderQueryCacheFactory.find(userId).setFirmGroupMembers(users);
        } else {
            OrderQueryCacheFactory.find(userId).setFirmGroupMembers(users);
        }
        QuoteCacheFactory.find(userId).setFirmGroupMembers(users);
        TradingFirmGroupWrapper tradingFirm = new TradingFirmGroupContainer(userId, users);
        subscriptionCollectionService.getTradingFirmSubscriptionCollection(tradingFirm)
                .addDefaultSubscriptions(this, source);
        ServicesHelper.getOrderHandlingService().publishOrdersForUser(userId);
        ServicesHelper.getOrderStatusAdminPublisher().subscribeOrderStatus(userId);
        ServicesHelper.getQuoteStatusAdminPublisher().subscribeQuoteStatus(userId);
    }

    public void addFirmInterest(Object source)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        String userId = sessionManager.getUserId();
        ExchangeFirmStruct exchangeFirm = sessionManager.getValidSessionProfileUserV2().userInfo.firm;
        if (FixUtilConstants.CLIENT_TYPES.FIXCAS.equals(System.getenv(FixUtilConstants.CLIENT_TYPES.CLIENT_TYPE))){
            FixOrderQueryCacheFactory.find(userId).setFirmKey(exchangeFirm);
        } else {
            OrderQueryCacheFactory.find(userId).setFirmKey(exchangeFirm);
        }
        QuoteCacheFactory.find(userId).setFirmKey(exchangeFirm);
        subscriptionCollectionService.getFirmSubscriptionCollection(new ExchangeFirmStructContainer(exchangeFirm)).addDefaultSubscriptions(this, source);
        ServicesHelper.getOrderHandlingService().publishOrdersForFirm(exchangeFirm);
        ServicesHelper.getOrderStatusAdminPublisher().subscribeOrderStatus(userId);
        ServicesHelper.getQuoteStatusAdminPublisher().subscribeQuoteStatus(userId);
    }

    public void addProductTypeInterest(Object source, short productType)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        subscriptionCollectionService.getProductTypeSubscriptionCollection(productType).addDefaultSubscriptions(this, source);
    }
    public void addSessionClassInterest(Object source, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).addDefaultSubscriptions(this, source);
    }
    public void removeFirmInterest(Object source)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ExchangeFirmStruct exchangeFirm = sessionManager.getValidSessionProfileUserV2().userInfo.firm;
        subscriptionCollectionService.getFirmSubscriptionCollection(new ExchangeFirmStructContainer(exchangeFirm)).removeDefaultSubscriptions(this, source);
    }
    public void removeUserInterest(Object source)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        subscriptionCollectionService.getUserSubscriptionCollection(sessionManager.getValidSessionProfileUserV2()).removeDefaultSubscriptions(this, source);
    }
    public void removeProductTypeInterest(Object source, short productType)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        subscriptionCollectionService.getProductTypeSubscriptionCollection(productType).removeDefaultSubscriptions(this, source);
    }
    public void removeSessionClassInterest(Object source, String sessionName, int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).removeDefaultSubscriptions(this, source);
    }
    public void addCurrentMarketClassInterest(Object listener, String sessionName, int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classKey);
        Subscription currentMarketSubscription = subscriptionCollectionService.getSessionClassSubscriptionCollection(sessionKey).
                getCurrentMarketSubscription();
        checkProductSubscription(currentMarketSubscription, listener, sessionKey);
        currentMarketSubscription.subscribe(this, listener);
    }
    public void addNBBOClassInterest(Object listener, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classKey);
        Subscription currentMarketSubscription = subscriptionCollectionService.getSessionClassSubscriptionCollection(sessionKey).
                getCurrentMarketSubscription();
        checkProductSubscription(currentMarketSubscription, listener, sessionKey);
        currentMarketSubscription.subscribe(this, listener);
    }

    public void addRecapClassInterest(Object listener, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classKey);
        Subscription recapSubscription = subscriptionCollectionService.getSessionClassSubscriptionCollection(sessionKey).
                getRecapSubscription();
        checkProductSubscription(recapSubscription, listener, sessionKey);
        recapSubscription.subscribe(this, listener);
    }

    public void addTickerClassInterest(Object listener, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classKey);
        Subscription tickerSubscription = subscriptionCollectionService.getSessionClassSubscriptionCollection(sessionKey).
                getTickerSubscription();
        checkProductSubscription(tickerSubscription, listener, sessionKey);
        tickerSubscription.subscribe(this, listener);
    }

    public void addOpeningPriceClassInterest(Object listener, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classKey);
        Subscription expectedOpeningPriceSubscription = subscriptionCollectionService.getSessionClassSubscriptionCollection(sessionKey).
                getExpectedOpeningPriceSubscription();
        checkProductSubscription(expectedOpeningPriceSubscription, listener, sessionKey);
        expectedOpeningPriceSubscription.subscribe(this, listener);
    }

    public void addAuctionClassInterest(Object listener, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classKey);
        subscriptionCollectionService.getSessionClassSubscriptionCollection(sessionKey).
                getAuctionSubscription().subscribe(this, listener, sessionKey);
    }

    public void addRFQClassInterest(Object listener, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classKey);
        subscriptionCollectionService.getSessionClassSubscriptionCollection(sessionKey).
                getRFQSubscription().subscribe(this, listener, sessionKey);
    }

    public void addBookDepthClassInterest(Object listener, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classKey);
        Subscription bookDepthSubscription = subscriptionCollectionService.getSessionClassSubscriptionCollection(sessionKey).
                getBookDepthSubscription();
        checkProductSubscription(bookDepthSubscription, listener, sessionKey);
        bookDepthSubscription.subscribe(this, listener);
    }

    public void removeCurrentMarketClassInterest(Object listener, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classKey);
        subscriptionCollectionService.getSessionClassSubscriptionCollection(sessionKey).
                getCurrentMarketSubscription().unsubscribe(this, listener);
    }

    public void removeNBBOClassInterest(Object listener, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classKey);
        subscriptionCollectionService.getSessionClassSubscriptionCollection(sessionKey).
                getCurrentMarketSubscription().unsubscribe(this, listener);
    }

    public void removeTickerClassInterest(Object listener, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classKey);
        subscriptionCollectionService.getSessionClassSubscriptionCollection(sessionKey).
                getTickerSubscription().unsubscribe(this, listener);
    }

    public void removeRecapClassInterest(Object listener, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classKey);

        subscriptionCollectionService.getSessionClassSubscriptionCollection(sessionKey).
                getRecapSubscription().unsubscribe(this, listener);
    }

    public void removeRFQClassInterest(Object listener, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classKey);

        subscriptionCollectionService.getSessionClassSubscriptionCollection(sessionKey).
                getRFQSubscription().unsubscribe(this, listener);
    }

    public void removeOpeningPriceClassInterest(Object listener, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classKey);
        subscriptionCollectionService.getSessionClassSubscriptionCollection(sessionKey).
                getExpectedOpeningPriceSubscription().unsubscribe(this, listener);
    }

    public void removeBookDepthClassInterest(Object listener, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classKey);
        subscriptionCollectionService.getSessionClassSubscriptionCollection(sessionKey).
                getBookDepthSubscription().unsubscribe(this, listener);
    }

    public void removeAuctionClassInterest(Object listener, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classKey);
        subscriptionCollectionService.getSessionClassSubscriptionCollection(sessionKey).
                getAuctionSubscription().unsubscribe(this, listener);
    }

    public void addQuoteLockedNotificationUserInterest(Object listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionProfileUserStructV2 userStruct = sessionManager.getValidSessionProfileUserV2();
        subscriptionCollectionService.getUserSubscriptionCollection(userStruct).
                getQuoteLockSubscription().subscribe(this, listener, userStruct);
    }

    public void removeQuoteLockedNotificationUserInterest(Object listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionProfileUserStructV2 userStruct = sessionManager.getValidSessionProfileUserV2();
        subscriptionCollectionService.getUserSubscriptionCollection(userStruct).
                getQuoteLockSubscription().unsubscribe(this, listener, userStruct);
    }

    public void addAuctionUserInterest(Object listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionProfileUserStructV2 userStruct = sessionManager.getValidSessionProfileUserV2();
        subscriptionCollectionService.getUserSubscriptionCollection(userStruct).
                getAuctionSubscription().subscribe(this, listener, userStruct);
    }
    public void removeAuctionUserInterest(Object listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionProfileUserStructV2 userStruct = sessionManager.getValidSessionProfileUserV2();
        subscriptionCollectionService.getUserSubscriptionCollection(userStruct).
                getAuctionSubscription().unsubscribe(this, listener, userStruct);
    }

    public void addCurrentMarketProductInterest(Object listener, String sessionName, int classKey, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classKey);
        SessionKeyContainer sessionProduct = new SessionKeyContainer(sessionName, productKey);
        Subscription currentMarketSubscription = subscriptionCollectionService.getSessionClassSubscriptionCollection(sessionKey).
                getCurrentMarketSubscription();
        checkClassKeySubscription(currentMarketSubscription, listener, sessionProduct);
        currentMarketSubscription.subscribe(this, listener, sessionProduct);
    }

    public void addNBBOProductInterest(Object listener, String sessionName, int classKey, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classKey);
        SessionKeyContainer sessionProduct = new SessionKeyContainer(sessionName, productKey);
        Subscription currentMarketSubscription = subscriptionCollectionService.getSessionClassSubscriptionCollection(sessionKey).
                getCurrentMarketSubscription();
        checkClassKeySubscription(currentMarketSubscription, listener, sessionProduct);
        currentMarketSubscription.subscribe(this, listener, sessionProduct);
    }

    public void addRecapProductInterest(Object listener, String sessionName, int classKey, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classKey);
        SessionKeyContainer sessionProduct = new SessionKeyContainer(sessionName, productKey);
        Subscription recapSubscription = subscriptionCollectionService.getSessionClassSubscriptionCollection(sessionKey).
                getRecapSubscription();
        checkClassKeySubscription(recapSubscription, listener, sessionProduct);
        recapSubscription.subscribe(this, listener, sessionProduct);
    }

    public void addTickerProductInterest(Object listener, String sessionName, int classKey, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classKey);
        SessionKeyContainer sessionProduct = new SessionKeyContainer(sessionName, productKey);
        Subscription tickerSubscription = subscriptionCollectionService.getSessionClassSubscriptionCollection(sessionKey).
                getTickerSubscription();
        checkClassKeySubscription(tickerSubscription, listener, sessionProduct);
        tickerSubscription.subscribe(this, listener, sessionProduct);
    }

    public void addOpeningPriceProductInterest(Object listener, String sessionName, int classKey, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classKey);
        SessionKeyContainer sessionProduct = new SessionKeyContainer(sessionName, productKey);
        Subscription expectedOpeningPriceSubscription = subscriptionCollectionService.getSessionClassSubscriptionCollection(sessionKey).
                getExpectedOpeningPriceSubscription();
        checkClassKeySubscription(expectedOpeningPriceSubscription, listener, sessionProduct);
        expectedOpeningPriceSubscription.subscribe(this, listener, sessionProduct);
    }

    public void addBookDepthProductInterest(Object listener, String sessionName, int classKey, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classKey);
        SessionKeyContainer sessionProduct = new SessionKeyContainer(sessionName, productKey);
        Subscription bookDepthSubscription = subscriptionCollectionService.getSessionClassSubscriptionCollection(sessionKey).
                getBookDepthSubscription();
        checkClassKeySubscription(bookDepthSubscription, listener, sessionProduct);
        bookDepthSubscription.subscribe(this, listener, sessionProduct);
    }

    public void removeOpeningPriceProductInterest(Object listener, String sessionName, int classKey, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName, classKey)).
                getExpectedOpeningPriceSubscription().unsubscribe(this, listener, new SessionKeyContainer(sessionName,productKey));
    }

    public void removeBookDepthProductInterest(Object listener, String sessionName, int classKey, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName, classKey)).
                getBookDepthSubscription().unsubscribe(this, listener, new SessionKeyContainer(sessionName,productKey));
    }

    public void removeCurrentMarketProductInterest(Object listener, String sessionName, int classKey, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName, classKey)).
                getCurrentMarketSubscription().unsubscribe(this, listener, new SessionKeyContainer(sessionName,productKey));
    }

    public void removeNBBOProductInterest(Object listener, String sessionName, int classKey, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName, classKey)).
                getCurrentMarketSubscription().unsubscribe(this, listener, new SessionKeyContainer(sessionName,productKey));
    }

    public void removeTickerProductInterest(Object listener, String sessionName, int classKey, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName, classKey)).
                getTickerSubscription().unsubscribe(this, listener, new SessionKeyContainer(sessionName,productKey));
    }

    public void removeRecapProductInterest(Object listener, String sessionName, int classKey, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName, classKey)).
                getRecapSubscription().unsubscribe(this, listener, new SessionKeyContainer(sessionName,productKey));
    }

    public void addLargeTradeLastSaleClassInterest(Object listener, String sessionName, int classKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
        SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classKey);
        Subscription ltlsSubscription = subscriptionCollectionService.
                                          getSessionClassSubscriptionCollection(sessionKey).
                                          getLargeTradeLastSaleSubscription();
        checkProductSubscription(ltlsSubscription, listener, sessionKey);
        ltlsSubscription.subscribe(this, listener);

    }

    public void removeLargeTradeLastSaleClassInterest(Object listener, String sessionName, int classKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
        SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classKey);
        subscriptionCollectionService.getSessionClassSubscriptionCollection(sessionKey).
            getLargeTradeLastSaleSubscription().unsubscribe(this, listener);
    }

    protected void checkProductSubscription(Subscription subscription, Object listener, SessionKeyContainer sessionKey)
            throws DataValidationException
    {
        if(subscription.containsSubscriptionsForListener(this, listener) && !subscription.containsDefaultKeySubscriptionForListener(this, listener))
        {
            // assume a product subscription exists.
            throw ExceptionBuilder.dataValidationException("Consumer already exists. Invalid class subscription for "+sessionKey, DataValidationCodes.LISTENER_ALREADY_REGISTERED);
        }
    }

    protected void checkClassKeySubscription(Subscription subscription, Object listener, SessionKeyContainer sessionKey)
            throws DataValidationException
    {
        if(subscription.containsDefaultKeySubscriptionForListener(this, listener))
        {
            // assume a class subscription exists.
            throw ExceptionBuilder.dataValidationException("Consumer already exists. Invalid product subscription for "+sessionKey, DataValidationCodes.LISTENER_ALREADY_REGISTERED);
        }
    }


    public void cleanUp()
    {
        super.cleanUp();
        sessionManager = null;
    }



}
