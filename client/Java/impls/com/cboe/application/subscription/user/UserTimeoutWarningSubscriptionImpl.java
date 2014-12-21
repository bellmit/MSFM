package com.cboe.application.subscription.user;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.subscription.SubscriptionAbstractCollection;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.interfaces.events.IECUserTimeoutWarningConsumerHome;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

public class UserTimeoutWarningSubscriptionImpl extends UserSubscriptionImpl
{
    protected IECUserTimeoutWarningConsumerHome userTimeoutWarningConsumerHome;
    public UserTimeoutWarningSubscriptionImpl(SessionProfileUserStructV2 userStruct, SubscriptionAbstractCollection subscriptionCollection)
    {
        super(userStruct, subscriptionCollection);
        userTimeoutWarningConsumerHome = ServicesHelper.getUserTimeoutWarningConsumerHome();
    }

    public void subscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.USER_SECURITY_TIMEOUT, userStruct.userInfo.userId);
        userTimeoutWarningConsumerHome.addFilter(channelKey);
    }

    public void unsubscribeChannel()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.USER_SECURITY_TIMEOUT, userStruct.userInfo.userId);
        userTimeoutWarningConsumerHome.removeFilter(channelKey);
    }
}
