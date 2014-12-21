package com.cboe.application.subscription.test;

import com.cboe.interfaces.application.subscription.SubscriptionService;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.application.shared.ServicesHelper;
import junit.framework.Test;
import junit.framework.TestSuite;

public class UnitTestQuoteLockSubscription extends UnitTestUserSubscription
{
    public void testSingleListenerQuoteLockInterest()
            throws Exception
    {
        Object listener = new Object();
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteLockSubscription().isDefaultSubscription());
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteLockSubscription().channelSubscribed());
        subscriptionService.addQuoteLockedNotificationUserInterest(listener);
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteLockSubscription().channelSubscribed());
        subscriptionService.removeQuoteLockedNotificationUserInterest(listener);
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteLockSubscription().channelSubscribed());
        subscriptionService.addQuoteLockedNotificationUserInterest(listener);
        subscriptionService.addUserInterest(listener);
        subscriptionService.removeQuoteLockedNotificationUserInterest(listener);
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getOrderSubscription().channelSubscribed());
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteLockSubscription().channelSubscribed());
        subscriptionService.removeUserInterest(listener);
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getOrderSubscription().channelSubscribed());
    }

    public void testMultipleInterestQuoteLockInterest()
            throws Exception
    {
        Object listener = new Object();
        subscriptionService.addQuoteLockedNotificationUserInterest(listener);
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteLockSubscription().channelSubscribed());
        Object listener2 = new Object();
        subscriptionService.addQuoteLockedNotificationUserInterest(listener2);
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteLockSubscription().channelSubscribed());
        subscriptionService.removeQuoteLockedNotificationUserInterest(listener);
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteLockSubscription().channelSubscribed());
        subscriptionService.removeQuoteLockedNotificationUserInterest(listener2);
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteLockSubscription().channelSubscribed());
    }

    public void testQuoteLockInterestFromMultipleSessionsUsingSameUserId()
            throws Exception
    {
        Object listener = new Object();
        subscriptionService.addQuoteLockedNotificationUserInterest(listener);
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteLockSubscription().channelSubscribed());
        SubscriptionService subscriptionService2 = getNewSubscriptionService();
        Object listener2 = new Object();
        subscriptionService2.addQuoteLockedNotificationUserInterest(listener2);
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteLockSubscription().channelSubscribed());
        subscriptionService.removeQuoteLockedNotificationUserInterest(listener);
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteLockSubscription().channelSubscribed());
        subscriptionService2.removeQuoteLockedNotificationUserInterest(listener2);
        assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteLockSubscription().channelSubscribed());
    }

    public void testCleanUp()
            throws Exception
    {
        Object listener = new Object();
        subscriptionService.addQuoteLockedNotificationUserInterest(listener);
        assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteLockSubscription().channelSubscribed());
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

    public void testQuoteLockSubscriptionMutliThreaded()
            throws Exception
    {
        TestCaseRunnable runnables [] = new TestCaseRunnable [2];
        runnables[0] = new TestCaseRunnable()
        {
            public void runTestCase()
            {
                try
                {
                    Object listener1 = new Object();
                    SubscriptionService subscriptionService1 = getNewSubscriptionService();
                    subscriptionService1.addQuoteLockedNotificationUserInterest(listener1);
                    assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteLockSubscription().channelSubscribed());
                    assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getOrderSubscription().channelSubscribed());
                    Thread.currentThread().sleep(50);
                    subscriptionService1.removeQuoteLockedNotificationUserInterest(listener1);
                    assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteLockSubscription().channelSubscribed());
                    assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getOrderSubscription().channelSubscribed());
                    long startTime = System.currentTimeMillis();
                    while(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getOrderSubscription().channelSubscribed())
                    {
                    }
                    System.out.println("waiting time:"+(System.currentTimeMillis()-startTime));
                    assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getOrderSubscription().channelSubscribed());
                    System.out.println("testQuoteLockSubscriptionMutliThreaded thread1 finished.");
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
                    subscriptionService2.addQuoteLockedNotificationUserInterest(listener2);
                    assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteLockSubscription().channelSubscribed());
                    assertTrue(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getOrderSubscription().channelSubscribed());
                    Thread.currentThread().sleep(100);
                    subscriptionService2.removeQuoteLockedNotificationUserInterest(listener2);
                    assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getQuoteLockSubscription().channelSubscribed());
                    assertFalse(subscriptionCollectionService.getUserSubscriptionCollection(userStructV2).getOrderSubscription().channelSubscribed());
                    System.out.println("testQuoteLockSubscriptionMutliThreaded thread2 finished.");
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
        return new TestSuite(UnitTestQuoteLockSubscription.class);
    }
}
