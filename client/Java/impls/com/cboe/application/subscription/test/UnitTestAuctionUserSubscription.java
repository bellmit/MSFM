package com.cboe.application.subscription.test;

import com.cboe.interfaces.application.subscription.SubscriptionService;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.application.shared.ServicesHelper;
import junit.framework.Test;
import junit.framework.TestSuite;

public class UnitTestAuctionUserSubscription extends UnitTestUserSubscription
{
    public void testSingleListenerAuctionInterest()
            throws Exception
    {
        Object listener = new Object();
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getAuctionSubscription().isDefaultSubscription());
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getAuctionSubscription().channelSubscribed());
        subscriptionService.addAuctionUserInterest(listener);
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getAuctionSubscription().channelSubscribed());
        subscriptionService.removeAuctionUserInterest(listener);
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getAuctionSubscription().channelSubscribed());
    }

    public void testMultipleInterestAuctionInterest()
            throws Exception
    {
        Object listener = new Object();
        subscriptionService.addAuctionUserInterest(listener);
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getAuctionSubscription().channelSubscribed());
        Object listener2 = new Object();
        subscriptionService.addAuctionUserInterest(listener2);
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getAuctionSubscription().channelSubscribed());
        subscriptionService.removeAuctionUserInterest(listener);
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getAuctionSubscription().channelSubscribed());
        subscriptionService.removeAuctionUserInterest(listener2);
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getAuctionSubscription().channelSubscribed());
    }

    public void testAuctionInterestFromMultipleSessionsUsingSameUserId()
            throws Exception
    {
        Object listener = new Object();
        subscriptionService.addAuctionUserInterest(listener);
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getAuctionSubscription().channelSubscribed());
        SubscriptionService subscriptionService2 = getNewSubscriptionService();
        Object listener2 = new Object();
        subscriptionService2.addAuctionUserInterest(listener2);
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getAuctionSubscription().channelSubscribed());
        subscriptionService.removeAuctionUserInterest(listener);
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getAuctionSubscription().channelSubscribed());
        subscriptionService2.removeAuctionUserInterest(listener2);
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getAuctionSubscription().channelSubscribed());
    }

    public void testCleanUp()
            throws Exception
    {
        Object listener = new Object();
        subscriptionService.addAuctionUserInterest(listener);
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getAuctionSubscription().channelSubscribed());
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getOrderSubscription().channelSubscribed());
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

    public static Test suite() {
        return new TestSuite(UnitTestAuctionUserSubscription.class);
    }
}
