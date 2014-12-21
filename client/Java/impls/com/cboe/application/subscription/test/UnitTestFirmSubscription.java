package com.cboe.application.subscription.test;

import com.cboe.interfaces.application.subscription.SubscriptionService;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.application.shared.ServicesHelper;
import junit.framework.Test;
import junit.framework.TestSuite;
/**
 * @author Jing Chen
 */
public class UnitTestFirmSubscription extends SetupSubscriptionServiceTest
{
    public void testAddFirmInterest()
            throws Exception
    {
        Object listener = new Object();
        assertTrue(subscriptionCollectionService.getFirmSubscriptionCollection(exchangeFirmStructContainer).getOrderSubscription().isDefaultSubscription());
        assertTrue(subscriptionCollectionService.getFirmSubscriptionCollection(exchangeFirmStructContainer).getQuoteSubscription().isDefaultSubscription());
        subscriptionService.addFirmInterest(listener);
        assertTrue(subscriptionCollectionService.getFirmSubscriptionCollection(exchangeFirmStructContainer).getOrderSubscription().channelSubscribed());
        assertTrue(subscriptionCollectionService.getFirmSubscriptionCollection(exchangeFirmStructContainer).getQuoteSubscription().channelSubscribed());
        subscriptionService.removeFirmInterest(listener);
        assertFalse(subscriptionCollectionService.getFirmSubscriptionCollection(exchangeFirmStructContainer).getOrderSubscription().channelSubscribed());
        assertFalse(subscriptionCollectionService.getFirmSubscriptionCollection(exchangeFirmStructContainer).getQuoteSubscription().channelSubscribed());
    }

    public void testMultipleFirmInterestSubscriptionsFromSameFirmId()
            throws Exception
    {
        Object listener = new Object();
        SubscriptionService subscriptionService2 = getNewSubscriptionService();
        subscriptionService.addFirmInterest(listener);
        assertTrue(subscriptionCollectionService.getFirmSubscriptionCollection(exchangeFirmStructContainer).getOrderSubscription().channelSubscribed());
        assertTrue(subscriptionCollectionService.getFirmSubscriptionCollection(exchangeFirmStructContainer).getQuoteSubscription().channelSubscribed());
        Object listener2 = new Object();
        subscriptionService2.addFirmInterest(listener2);
        subscriptionService.removeFirmInterest(listener);
        assertTrue(subscriptionCollectionService.getFirmSubscriptionCollection(exchangeFirmStructContainer).getOrderSubscription().channelSubscribed());
        assertTrue(subscriptionCollectionService.getFirmSubscriptionCollection(exchangeFirmStructContainer).getQuoteSubscription().channelSubscribed());
        subscriptionService2.removeFirmInterest(listener2);
        assertFalse(subscriptionCollectionService.getFirmSubscriptionCollection(exchangeFirmStructContainer).getOrderSubscription().channelSubscribed());
        assertFalse(subscriptionCollectionService.getFirmSubscriptionCollection(exchangeFirmStructContainer).getQuoteSubscription().channelSubscribed());
    }

    public void testCleanUp()
            throws Exception
    {
        Object listener = new Object();
        subscriptionService.addFirmInterest(listener);
        assertTrue(subscriptionCollectionService.getFirmSubscriptionCollection(exchangeFirmStructContainer).getOrderSubscription().channelSubscribed());
        assertTrue(subscriptionCollectionService.getFirmSubscriptionCollection(exchangeFirmStructContainer).getQuoteSubscription().channelSubscribed());
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
        return new TestSuite(UnitTestFirmSubscription.class);
    }
}
