package com.cboe.application.subscription.test;

import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.interfaces.application.subscription.SubscriptionService;
import com.cboe.application.shared.ServicesHelper;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Jing Chen
 */
public class UnitTestAuctionClassSubscription extends UnitTestSessionClassSubscription
{

    public void testSingleListenerForClassSubAndUnSub()
            throws Exception
    {
        Object listener = new Object();
        subscriptionService.addAuctionClassInterest(listener,sessionName,classKey);
        assertChannelSubscriptionTrue();
        subscriptionService.removeAuctionClassInterest(listener,sessionName,classKey);
        assertChannelSubscriptionFalse();
    }

    public void testMultipleListenersForClassSubscription()
            throws Exception
    {
        Object listener1 = new Object();
        subscriptionService.addAuctionClassInterest(listener1,sessionName,classKey);
        assertChannelSubscriptionTrue();
        Object listener2 = new Object();
        subscriptionService.addAuctionClassInterest(listener2,sessionName,classKey);
        assertChannelSubscriptionTrue();
        subscriptionService.removeAuctionClassInterest(listener1, sessionName, classKey);
        assertChannelSubscriptionTrue();
        subscriptionService.removeAuctionClassInterest(listener2, sessionName, classKey);
        assertChannelSubscriptionFalse();
    }

    public void testMultipleGroupSubscription()
            throws Exception
    {
        SubscriptionService subscriptionService2 = getNewSubscriptionService();
        Object listener1 = new Object();
        subscriptionService.addAuctionClassInterest(listener1,sessionName,classKey);
        assertChannelSubscriptionTrue();
        Object listener2 = new Object();
        subscriptionService2.addAuctionClassInterest(listener2,sessionName,classKey);
        assertChannelSubscriptionTrue();
        subscriptionService.removeAuctionClassInterest(listener1,sessionName,classKey);
        assertChannelSubscriptionTrue();
        subscriptionService2.removeAuctionClassInterest(listener2,sessionName,classKey);
        assertChannelSubscriptionFalse();
    }

    public void testDefaultSubscription()
            throws Exception
    {
        Object listener = new Object();
        assertTrue(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).getTextMessagingSubscription().isDefaultSubscription());
        subscriptionService.addAuctionClassInterest(listener,sessionName,classKey);
        assertTrue(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).getTextMessagingSubscription().channelSubscribed());
        subscriptionService.removeAuctionClassInterest(listener,sessionName,classKey);
        assertFalse(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).getTextMessagingSubscription().channelSubscribed());
        subscriptionService.addSessionClassInterest(listener, sessionName, classKey);
        assertTrue(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).getTextMessagingSubscription().channelSubscribed());
        subscriptionService.addAuctionClassInterest(listener,sessionName,classKey);
        assertTrue(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).getTextMessagingSubscription().channelSubscribed());
        subscriptionService.removeAuctionClassInterest(listener,sessionName,classKey);
        assertTrue(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).getTextMessagingSubscription().channelSubscribed());
        subscriptionService.removeSessionClassInterest(listener, sessionName, classKey);
        assertFalse(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).getTextMessagingSubscription().channelSubscribed());
        SubscriptionService subscriptionService2 = getNewSubscriptionService();
        Object listener2 = new Object();
        subscriptionService2.addAuctionClassInterest(listener2,sessionName,classKey);
        assertTrue(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).getTextMessagingSubscription().channelSubscribed());
        subscriptionService.addAuctionClassInterest(listener, sessionName, classKey);
        subscriptionService2.removeAuctionClassInterest(listener2, sessionName, classKey);
        assertTrue(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).getTextMessagingSubscription().channelSubscribed());
        subscriptionService.removeAuctionClassInterest(listener, sessionName, classKey);
        assertFalse(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).getTextMessagingSubscription().channelSubscribed());
    }

    public void testCleanUp()
            throws Exception
    {
        Object listener = new Object();
        subscriptionService.addAuctionClassInterest(listener, sessionName, classKey);
        assertTrue(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).getAuctionSubscription().channelSubscribed());
        assertTrue(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).getTextMessagingSubscription().channelSubscribed());
        ServicesHelper.getSubscriptionServiceHome().remove(sessionManager);
        assertFalse(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).getAuctionSubscription().channelSubscribed());
        assertFalse(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).getTextMessagingSubscription().channelSubscribed());
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getOrderSubscription().channelSubscribed());
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteSubscription().channelSubscribed());
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getUserTimeoutWarningSubscription().channelSubscribed());
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getPropertySubscription().channelSubscribed());
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getTextMessagingSubscription().channelSubscribed());
        assertFalse(subscriptionCollectionService.getProductTypeSubscriptionCollection(productType).getTextMessagingSubscription().channelSubscribed());
        assertFalse(subscriptionCollectionService.getFirmSubscriptionCollection(exchangeFirmStructContainer).getOrderSubscription().channelSubscribed());
        assertFalse(subscriptionCollectionService.getFirmSubscriptionCollection(exchangeFirmStructContainer).getQuoteSubscription().channelSubscribed());
    }

    protected void assertChannelSubscriptionTrue()
    {
        assertTrue(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).getAuctionSubscription().channelSubscribed());
    }

    protected void assertChannelSubscriptionFalse()
    {
        assertFalse(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).getAuctionSubscription().channelSubscribed());
    }

    public static Test suite() {
        return new TestSuite(UnitTestAuctionClassSubscription.class);
    }

}
