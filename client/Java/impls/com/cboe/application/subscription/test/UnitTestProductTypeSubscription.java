package com.cboe.application.subscription.test;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.interfaces.application.subscription.SubscriptionService;
import junit.framework.Test;
import junit.framework.TestSuite;

public class UnitTestProductTypeSubscription extends SetupSubscriptionServiceTest
{
    public void testAddProductTypeInterest()
            throws Exception
    {
        Object listener = new Object();
        assertTrue(subscriptionCollectionService.getProductTypeSubscriptionCollection(productType).getTextMessagingSubscription().isDefaultSubscription());
        subscriptionService.addProductTypeInterest(listener, productType);
        assertTrue(subscriptionCollectionService.getProductTypeSubscriptionCollection(productType).getTextMessagingSubscription().channelSubscribed());
        subscriptionService.removeProductTypeInterest(listener, productType);
        assertFalse(subscriptionCollectionService.getProductTypeSubscriptionCollection(productType).getTextMessagingSubscription().channelSubscribed());
    }

    public void testMultipleProductTypeInterestSubscriptionsFromSameProductTypeId()
            throws Exception
    {
        Object listener = new Object();
        SubscriptionService subscriptionService2 = getNewSubscriptionService();
        subscriptionService.addProductTypeInterest(listener, productType);
        assertTrue(subscriptionCollectionService.getProductTypeSubscriptionCollection(productType).getTextMessagingSubscription().channelSubscribed());
        subscriptionService2.addProductTypeInterest(listener, productType);
        subscriptionService.removeProductTypeInterest(listener, productType);
        assertTrue(subscriptionCollectionService.getProductTypeSubscriptionCollection(productType).getTextMessagingSubscription().channelSubscribed());
        subscriptionService2.removeProductTypeInterest(listener, productType);
        assertFalse(subscriptionCollectionService.getProductTypeSubscriptionCollection(productType).getTextMessagingSubscription().channelSubscribed());
    }

    public void testCleanUp()
            throws Exception
    {
        Object listener = new Object();
        subscriptionService.addProductTypeInterest(listener, productType);
        assertTrue(subscriptionCollectionService.getProductTypeSubscriptionCollection(productType).getTextMessagingSubscription().channelSubscribed());
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
        return new TestSuite(UnitTestProductTypeSubscription.class);
    }
}
