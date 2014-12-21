package com.cboe.application.subscription.user;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.subscription.SubscriptionAbstractCollection;
import com.cboe.application.userServices.UserEnablementImpl;
import com.cboe.domain.rateMonitor.RateLimitsFactory;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.interfaces.events.IECPropertyConsumerHome;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

public class PropertyUpdateSubscriptionImpl extends UserSubscriptionImpl
{
    protected IECPropertyConsumerHome propertyConsumerHome;

    public PropertyUpdateSubscriptionImpl(SessionProfileUserStructV2 userStruct, SubscriptionAbstractCollection subscriptionCollection)
    {
        super(userStruct, subscriptionCollection);
        propertyConsumerHome = ServicesHelper.getPropertyConsumerHome();
    }

    public void subscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.UPDATE_PROPERTY_ENABLEMENT, userStruct.userInfo.userId);
        propertyConsumerHome.addFilter(channelKey);

        // use static getter() method here so as not to instantiate the enablement object at this time
        String enablementKey = UserEnablementImpl.getUserEnablementKey(userStruct.userInfo.userId, userStruct.userInfo.userAcronym.exchange, userStruct.userInfo.userAcronym.acronym);
        ChannelKey enableChannelKey = new ChannelKey(ChannelType.UPDATE_PROPERTY_ENABLEMENT, enablementKey);
        propertyConsumerHome.addFilter(enableChannelKey);

        // use static getter() method here so as not to instantiate the enablement object at this time
        String testClassesKey = UserEnablementImpl.getUserTestClassesKey(userStruct.userInfo.userId, userStruct.userInfo.userAcronym.exchange, userStruct.userInfo.userAcronym.acronym);
        ChannelKey testClassesChannelKey = new ChannelKey(ChannelType.UPDATE_PROPERTY_ENABLEMENT, testClassesKey);
        propertyConsumerHome.addFilter(testClassesChannelKey);

        // use static getter() method here so as not to instantiate the enablement object at this time
        String mdxKey = UserEnablementImpl.getMDXKey(userStruct.userInfo.userAcronym.exchange, userStruct.userInfo.userAcronym.acronym);
        ChannelKey mdxChannelKey = new ChannelKey(ChannelType.UPDATE_PROPERTY_ENABLEMENT, mdxKey);
        propertyConsumerHome.addFilter(mdxChannelKey);

        // use static getter() method here so as not to instantiate the enablement object at this time
        String tradingFirmKey = UserEnablementImpl.getTradingFirmKey(userStruct.userInfo.userAcronym.exchange, userStruct.userInfo.userAcronym.acronym);
        ChannelKey tradingFirmChannelKey = new ChannelKey(ChannelType.UPDATE_PROPERTY_ENABLEMENT, tradingFirmKey);
        propertyConsumerHome.addFilter(tradingFirmChannelKey);

        // new support for rate limites
        String rateLimitKey = RateLimitsFactory.getRateMonitorKey(userStruct.userInfo.userId, userStruct.userInfo.userAcronym.exchange, userStruct.userInfo.userAcronym.acronym);
        ChannelKey rateLimitChannelKey = new ChannelKey(ChannelType.UPDATE_PROPERTY_RATELIMIT, rateLimitKey);
        propertyConsumerHome.addFilter(rateLimitChannelKey);
        rateLimitChannelKey = new ChannelKey(ChannelType.REMOVE_PROPERTY_RATELIMIT, rateLimitKey);
        propertyConsumerHome.addFilter(rateLimitChannelKey);
    }

    public void unsubscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.UPDATE_PROPERTY_ENABLEMENT, userStruct.userInfo.userId);
        propertyConsumerHome.removeFilter(channelKey);

        // use static getter() method here so as not to instantiate the enablement object at this time
        String enablementKey = UserEnablementImpl.getUserEnablementKey(userStruct.userInfo.userId, userStruct.userInfo.userAcronym.exchange, userStruct.userInfo.userAcronym.acronym);
        ChannelKey enableChannelKey = new ChannelKey(ChannelType.UPDATE_PROPERTY_ENABLEMENT, enablementKey);
        propertyConsumerHome.removeFilter(enableChannelKey);

        // use static getter() method here so as not to instantiate the enablement object at this time
        String testClassesKey = UserEnablementImpl.getUserTestClassesKey(userStruct.userInfo.userId, userStruct.userInfo.userAcronym.exchange, userStruct.userInfo.userAcronym.acronym);
        ChannelKey testClassesChannelKey = new ChannelKey(ChannelType.UPDATE_PROPERTY_ENABLEMENT, testClassesKey);
        propertyConsumerHome.removeFilter(testClassesChannelKey);

        // use static getter() method here so as not to instantiate the enablement object at this time
        String mdxKey = UserEnablementImpl.getMDXKey(userStruct.userInfo.userAcronym.exchange, userStruct.userInfo.userAcronym.acronym);
        ChannelKey mdxChannelKey = new ChannelKey(ChannelType.UPDATE_PROPERTY_ENABLEMENT, mdxKey);
        propertyConsumerHome.removeFilter(mdxChannelKey);

        // use static getter() method here so as not to instantiate the enablement object at this time
        String tradingFirmKey = UserEnablementImpl.getTradingFirmKey(userStruct.userInfo.userAcronym.exchange, userStruct.userInfo.userAcronym.acronym);
        ChannelKey tradingFirmChannelKey = new ChannelKey(ChannelType.UPDATE_PROPERTY_ENABLEMENT, tradingFirmKey);
        propertyConsumerHome.removeFilter(tradingFirmChannelKey);

        // new support for rate limites
        String rateLimitKey = RateLimitsFactory.getRateMonitorKey(userStruct.userInfo.userId, userStruct.userInfo.userAcronym.exchange, userStruct.userInfo.userAcronym.acronym);
        ChannelKey rateLimitChannelKey = new ChannelKey(ChannelType.UPDATE_PROPERTY_RATELIMIT, rateLimitKey);
        propertyConsumerHome.removeFilter(rateLimitChannelKey);
        rateLimitChannelKey = new ChannelKey(ChannelType.REMOVE_PROPERTY_RATELIMIT, rateLimitKey);
        propertyConsumerHome.removeFilter(rateLimitChannelKey);
    }
}
