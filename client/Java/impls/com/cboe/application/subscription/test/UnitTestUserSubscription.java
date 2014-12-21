package com.cboe.application.subscription.test;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.interfaces.application.subscription.SubscriptionService;
import com.cboe.domain.util.SessionKeyContainer;
import junit.framework.Test;
import junit.framework.TestSuite;

public class UnitTestUserSubscription extends SetupSubscriptionServiceTest
{
    public void testAddUserInterest()
            throws Exception
    {
        Object listener = new Object();
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getOrderSubscription().isDefaultSubscription());
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteSubscription().isDefaultSubscription());
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteLockSubscription().isDefaultSubscription());
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getPropertySubscription().isDefaultSubscription());
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getTextMessagingSubscription().isDefaultSubscription());
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getUserTimeoutWarningSubscription().isDefaultSubscription());
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getAuctionSubscription().isDefaultSubscription());
        subscriptionService.addUserInterest(listener);
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getTextMessagingSubscription().channelSubscribed());
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getOrderSubscription().channelSubscribed());
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteSubscription().channelSubscribed());
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteLockSubscription().channelSubscribed());
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getPropertySubscription().channelSubscribed());
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getUserTimeoutWarningSubscription().channelSubscribed());
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getAuctionSubscription().channelSubscribed());
        subscriptionService.removeUserInterest(listener);
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getTextMessagingSubscription().channelSubscribed());
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getOrderSubscription().channelSubscribed());
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteSubscription().channelSubscribed());
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteLockSubscription().channelSubscribed());
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getPropertySubscription().channelSubscribed());
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getUserTimeoutWarningSubscription().channelSubscribed());
        subscriptionService.addUserInterest(listener);
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getTextMessagingSubscription().channelSubscribed());
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getOrderSubscription().channelSubscribed());
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteSubscription().channelSubscribed());
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteLockSubscription().channelSubscribed());
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getPropertySubscription().channelSubscribed());
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getUserTimeoutWarningSubscription().channelSubscribed());
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getAuctionSubscription().channelSubscribed());
        ServicesHelper.getSubscriptionServiceHome().remove(sessionManager);
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getTextMessagingSubscription().channelSubscribed());
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getOrderSubscription().channelSubscribed());
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteSubscription().channelSubscribed());
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteLockSubscription().channelSubscribed());
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getPropertySubscription().channelSubscribed());
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getUserTimeoutWarningSubscription().channelSubscribed());
    }

    public void testMultipleUserInterestSubscriptionsFromSameUserId()
            throws Exception
    {
        Object listener = new Object();
        SubscriptionService subscriptionService2 = getNewSubscriptionService();
        subscriptionService.addUserInterest(listener);
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getOrderSubscription().channelSubscribed());
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteSubscription().channelSubscribed());
        Object listener2 = new Object();
        subscriptionService2.addUserInterest(listener2);
        subscriptionService.removeUserInterest(listener);
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getOrderSubscription().channelSubscribed());
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteSubscription().channelSubscribed());
        subscriptionService2.removeUserInterest(listener2);
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getOrderSubscription().channelSubscribed());
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteSubscription().channelSubscribed());
    }


    public void testCleanUp()
            throws Exception
    {
        Object listener = new Object();
        subscriptionService.addUserInterest(listener);
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getTextMessagingSubscription().channelSubscribed());
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getOrderSubscription().channelSubscribed());
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteSubscription().channelSubscribed());
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteLockSubscription().channelSubscribed());
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getPropertySubscription().channelSubscribed());
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getUserTimeoutWarningSubscription().channelSubscribed());
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getAuctionSubscription().channelSubscribed());
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

    public void testUserSubscriptionMutliThreaded()
            throws Exception
    {
        TestCaseRunnable runnables [] = new TestCaseRunnable [2];
        runnables[0] = new TestCaseRunnable()
        {
            public void runTestCase()
            {
                try
                {
                    Object listener = new Object();
                    SubscriptionService subscriptionService1 = getNewSubscriptionService();
                    subscriptionService1.addUserInterest(listener);
                    assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getOrderSubscription().channelSubscribed());
                    Thread.currentThread().sleep(50);
                    subscriptionService1.removeUserInterest(listener);
                    assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getOrderSubscription().channelSubscribed());
                    long startTime = System.currentTimeMillis();
                    while(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getOrderSubscription().channelSubscribed())
                    {
                    }
                    System.out.println("waiting time:"+(System.currentTimeMillis()-startTime));
                    assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getOrderSubscription().channelSubscribed());
                    System.out.println("testUserSubscriptionMultiThreaded thread1 finished.");
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    fail("testUserSubscriptionMutliThreaded failed:"+e.getMessage());
                }
            }
        };

        runnables[1] = new TestCaseRunnable()
        {
            public void runTestCase()
            {
                try
                {
                    Object listener2 = new Object();
                    SubscriptionService subscriptionService2 = getNewSubscriptionService();
                    subscriptionService2.addUserInterest(listener2);
                    assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getOrderSubscription().channelSubscribed());
                    Thread.currentThread().sleep(100);
                    subscriptionService2.removeUserInterest(listener2);
                    assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getOrderSubscription().channelSubscribed());
                    System.out.println("testUserSubscriptionMultiThreaded thread2 finished.");
                  }
                catch(Exception e)
                {
                    e.printStackTrace();
                    fail("testUserSubscriptionMutliThreaded failed:"+e.getMessage());
                }
            }
        };
        runTestCaseRunnables(runnables);
    }


    public static Test suite()
    {
        return new TestSuite(UnitTestUserSubscription.class);
    }
}
