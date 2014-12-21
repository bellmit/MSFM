package com.cboe.application.subscription.test;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.subscription.SubscriptionGroupImpl;
import com.cboe.domain.util.ExchangeFirmStructContainer;
import com.cboe.domain.util.MultiThreadedTestCase;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiUser.SessionProfileUserStruct;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationServiceFileImpl;
import com.cboe.interfaces.application.subscription.*;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.interfaces.domain.session.CallbackDeregistrationInfo;
import com.cboe.interfaces.events.*;
import com.cboe.util.channel.ChannelListener;
import junit.framework.TestCase;

public class SetupSubscriptionServiceTest extends MultiThreadedTestCase
{
    protected FoundationFramework ff;
    protected HomeFactory hf;
    protected BaseSessionManager sessionManager;
    protected SessionProfileUserStructV2 userStructV2;
    protected ExchangeFirmStructContainer exchangeFirmStructContainer;
    protected SubscriptionGroup subscriptionGroup;
    protected SubscriptionService subscriptionService;
    protected SubscriptionCollectionService subscriptionCollectionService;
    protected String sessionName;
    protected int classKey;
    protected int productKey;
    protected short productType;

    protected static ConfigurationService getConfigurationService(final java.util.Properties props)
    {
        return new ConfigurationServiceFileImpl()
        {
            java.util.Properties prop = props;
            /**
             * Override initialize.
             */
            public boolean initialize(String[] parameters, int firstConfigurationParameter )
            {
                properties = prop;
                return true;
            }
        };
    }

    protected void setUpFoundationFramework()
    {
        ff = FoundationFramework.getInstance();
        ff.setName("TestSubscription");

        hf = HomeFactory.getInstance();
        java.util.Properties defaultProps = new java.util.Properties();
        ConfigurationService cof = getConfigurationService(defaultProps);
        ff.setConfigService(cof);
        cof.initialize(null, 0);

        BOContainer container = new BOContainer();
        container.setName("TestContainer");
        container.initialize();
        BOContainerDescriptor boContainerDes = new BOContainerDescriptor();
        container.setBOContainerDescriptor(boContainerDes);
        ContainerFactory.getInstance().addBOContainer(container);

        BOHomeDescriptor desc = new BOHomeDescriptor();
        desc.setBOHomeImplClassName("com.cboe.application.subscription.SubscriptionCollectionServiceHomeImpl");
        desc.setBOHomeName(SubscriptionCollectionServiceHome.HOME_NAME);
        desc.setBOContainerDescriptorName("TestContainer");
        hf.defineHome(SubscriptionCollectionServiceHome.HOME_NAME, desc);

        desc = new BOHomeDescriptor();
        desc.setBOHomeImplClassName("com.cboe.application.subscription.SubscriptionServiceHomeImpl");
        desc.setBOHomeName(SubscriptionServiceHome.HOME_NAME);
        desc.setBOContainerDescriptorName("TestContainer");
        hf.defineHome(SubscriptionServiceHome.HOME_NAME, desc);

        desc = new BOHomeDescriptor();
        desc.setBOHomeImplClassName("com.cboe.consumers.eventChannel.CurrentMarketConsumerHomeInProcessImpl");
        desc.setBOHomeName(IECCurrentMarketConsumerHome.HOME_NAME);
        desc.setBOContainerDescriptorName("TestContainer");
        hf.defineHome(IECCurrentMarketConsumerHome.HOME_NAME, desc);

        desc = new BOHomeDescriptor();
        desc.setBOHomeImplClassName("com.cboe.consumers.eventChannel.RecapConsumerHomeInProcessImpl");
        desc.setBOHomeName(IECRecapConsumerHome.HOME_NAME);
        desc.setBOContainerDescriptorName("TestContainer");
        hf.defineHome(IECRecapConsumerHome.HOME_NAME, desc);

        desc = new BOHomeDescriptor();
        desc.setBOHomeImplClassName("com.cboe.consumers.eventChannel.BookDepthConsumerHomeInProcessImpl");
        desc.setBOHomeName(IECBookDepthConsumerHome.HOME_NAME);
        desc.setBOContainerDescriptorName("TestContainer");
        hf.defineHome(IECBookDepthConsumerHome.HOME_NAME, desc);

        desc = new BOHomeDescriptor();
        desc.setBOHomeImplClassName("com.cboe.consumers.eventChannel.TickerConsumerHomeInProcessImpl");
        desc.setBOHomeName(IECTickerConsumerHome.HOME_NAME);
        desc.setBOContainerDescriptorName("TestContainer");
        hf.defineHome(IECTickerConsumerHome.HOME_NAME, desc);

        desc = new BOHomeDescriptor();
        desc.setBOHomeImplClassName("com.cboe.consumers.eventChannel.RFQConsumerHomeInProcessImpl");
        desc.setBOHomeName(IECRFQConsumerHome.HOME_NAME);
        desc.setBOContainerDescriptorName("TestContainer");
        hf.defineHome(IECRFQConsumerHome.HOME_NAME, desc);

        desc = new BOHomeDescriptor();
        desc.setBOHomeImplClassName("com.cboe.consumers.eventChannel.QuoteNotificationConsumerHomeInProcessImpl");
        desc.setBOHomeName(IECQuoteNotificationConsumerHome.HOME_NAME);
        desc.setBOContainerDescriptorName("TestContainer");
        hf.defineHome(IECQuoteNotificationConsumerHome.HOME_NAME, desc);

        desc = new BOHomeDescriptor();
        desc.setBOHomeImplClassName("com.cboe.consumers.eventChannel.AuctionConsumerHomeInProcessImpl");
        desc.setBOHomeName(IECAuctionConsumerHome.HOME_NAME);
        desc.setBOContainerDescriptorName("TestContainer");
        hf.defineHome(IECAuctionConsumerHome.HOME_NAME, desc);

        desc = new BOHomeDescriptor();
        desc.setBOHomeImplClassName("com.cboe.consumers.eventChannel.TextMessageConsumerHomeInProcessImpl");
        desc.setBOHomeName(IECTextMessageConsumerHome.HOME_NAME);
        desc.setBOContainerDescriptorName("TestContainer");
        hf.defineHome(IECTextMessageConsumerHome.HOME_NAME, desc);

        desc = new BOHomeDescriptor();
        desc.setBOHomeImplClassName("com.cboe.consumers.eventChannel.OrderStatusConsumerHomeInProcessImpl");
        desc.setBOHomeName(IECOrderStatusConsumerHome.HOME_NAME);
        desc.setBOContainerDescriptorName("TestContainer");
        hf.defineHome(IECOrderStatusConsumerHome.HOME_NAME, desc);

        desc = new BOHomeDescriptor();
        desc.setBOHomeImplClassName("com.cboe.consumers.eventChannel.QuoteStatusConsumerHomeInProcessImpl");
        desc.setBOHomeName(IECQuoteStatusConsumerHome.HOME_NAME);
        desc.setBOContainerDescriptorName("TestContainer");
        hf.defineHome(IECQuoteStatusConsumerHome.HOME_NAME, desc);

        desc = new BOHomeDescriptor();
        desc.setBOHomeImplClassName("com.cboe.consumers.eventChannel.QuoteNotificationConsumerHomeInProcessImpl");
        desc.setBOHomeName(IECQuoteNotificationConsumerHome.HOME_NAME);
        desc.setBOContainerDescriptorName("TestContainer");
        hf.defineHome(IECQuoteNotificationConsumerHome.HOME_NAME, desc);

        desc = new BOHomeDescriptor();
        desc.setBOHomeImplClassName("com.cboe.consumers.eventChannel.PropertyConsumerHomeInProcessImpl");
        desc.setBOHomeName(IECPropertyConsumerHome.HOME_NAME);
        desc.setBOContainerDescriptorName("TestContainer");
        hf.defineHome(IECPropertyConsumerHome.HOME_NAME, desc);

        desc = new BOHomeDescriptor();
        desc.setBOHomeImplClassName("com.cboe.consumers.eventChannel.TextMessageConsumerHomeInProcessImpl");
        desc.setBOHomeName(IECTextMessageConsumerHome.HOME_NAME);
        desc.setBOContainerDescriptorName("TestContainer");
        hf.defineHome(IECTextMessageConsumerHome.HOME_NAME, desc);

        desc = new BOHomeDescriptor();
        desc.setBOHomeImplClassName("com.cboe.consumers.eventChannel.UserTimeoutWarningConsumerHomeInProcessImpl");
        desc.setBOHomeName(IECUserTimeoutWarningConsumerHome.HOME_NAME);
        desc.setBOContainerDescriptorName("TestContainer");
        hf.defineHome(IECUserTimeoutWarningConsumerHome.HOME_NAME, desc);
    }

    public void setUp()
            throws Exception
    {
        setUpFoundationFramework();
        String userId = "TST";
        ExchangeAcronymStruct exchangeAcro = new ExchangeAcronymStruct("CBOE", "TST");
        ExchangeFirmStruct exchangeFirm = new ExchangeFirmStruct("CBOE", "HULL");
        SessionProfileUserStruct userStruct = new SessionProfileUserStruct(exchangeAcro,userId,exchangeFirm,
                "Test",'T',null,null,null,null,null,null,null);
        userStructV2 = new SessionProfileUserStructV2(123, userStruct);
        subscriptionGroup = new SubscriptionGroupImpl();
        sessionName = "W_MAIN";
        classKey = 12345;
        productKey = 54321;
        productType = 7;
        exchangeFirmStructContainer = new ExchangeFirmStructContainer(userStructV2.userInfo.firm);

        sessionManager = new TestSession();
        subscriptionService = ServicesHelper.getSubscriptionService(sessionManager);
        subscriptionCollectionService = ServicesHelper.getSubscriptionCollectionService();
    }

    public void tearDown()
            throws Exception
    {
        ServicesHelper.getSubscriptionServiceHome().remove(sessionManager);
    }

    protected SubscriptionService getNewSubscriptionService()
            throws Exception
    {
        BaseSessionManager sessionManager = new TestSession();
        return ServicesHelper.getSubscriptionService(sessionManager);
    }

    protected class TestSession implements BaseSessionManager
    {
        public void lostConnection(ChannelListener channelListener)
                throws SystemException, CommunicationException, AuthorizationException
        {}

        public void unregisterNotification(CallbackDeregistrationInfo deregistrationInfo)
                throws SystemException, CommunicationException, AuthorizationException
        {}

        public String getUserId()
                throws SystemException, CommunicationException, AuthorizationException
        {
            return userStructV2.userInfo.userId;
        }

        public SessionProfileUserStructV2 getValidSessionProfileUserV2()
                throws SystemException, CommunicationException, AuthorizationException
        {
            return userStructV2;
        }

        public String getExchange()
                throws SystemException, CommunicationException, AuthorizationException
        {
            return userStructV2.userInfo.userAcronym.exchange;
        }

        public String getAcronym()
                throws SystemException, CommunicationException, AuthorizationException
        {
            return userStructV2.userInfo.userAcronym.acronym;
        }

        public String getInstrumentorName()
                throws SystemException, CommunicationException, AuthorizationException
        {
            return "TestSession:"+userStructV2.userInfo.userId;
        }

        public int getSessionKey()
                throws SystemException, CommunicationException, AuthorizationException
        {
            return 1;
        }
    }

}
