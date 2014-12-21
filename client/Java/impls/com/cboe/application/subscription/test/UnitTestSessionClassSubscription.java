package com.cboe.application.subscription.test;

import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.infrastructureServices.foundationFramework.BOHomeDescriptor;
import com.cboe.interfaces.events.IECTextMessageConsumerHome;
import junit.framework.Test;
import junit.framework.TestSuite;

public class UnitTestSessionClassSubscription extends SetupSubscriptionServiceTest
{
    public void testDefaultSubscription()
            throws Exception
    {
        Object listener = new Object();
        assertTrue(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).getTextMessagingSubscription().isDefaultSubscription());
        assertFalse(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).getTextMessagingSubscription().channelSubscribed());
        subscriptionService.addSessionClassInterest(listener, sessionName, classKey);
        assertTrue(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).getTextMessagingSubscription().channelSubscribed());
        subscriptionService.removeSessionClassInterest(listener, sessionName, classKey);
        assertFalse(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).getTextMessagingSubscription().channelSubscribed());
    }

    protected void setUpFoundationFramework()
    {
        super.setUpFoundationFramework();
        BOHomeDescriptor desc = new BOHomeDescriptor();
        desc.setBOHomeImplClassName("com.cboe.consumers.eventChannel.TextMessageConsumerHomeInProcessImpl");
        desc.setBOHomeName(IECTextMessageConsumerHome.HOME_NAME);
        desc.setBOContainerDescriptorName("TestContainer");
        hf.defineHome(IECTextMessageConsumerHome.HOME_NAME, desc);
    }

    public static Test suite() {
        return new TestSuite(UnitTestSessionClassSubscription.class);
    }
}
