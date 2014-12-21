package com.cboe.application.subscription.test;

import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.exceptions.DataValidationException;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.interfaces.application.subscription.SubscriptionService;
import com.cboe.application.shared.ServicesHelper;
import junit.framework.Test;
import junit.framework.TestSuite;
/**
 * @author Jing Chen
 */
public class UnitTestCurrentMarketSubscription extends UnitTestSessionClassSubscription
{
    public void testSingleListenerForProductSubscriptionReject()
            throws Exception
    {
        Object listener = new Object();
        subscriptionService.addCurrentMarketClassInterest(listener,sessionName,classKey);
        assertChannelSubscriptionTrue();
        try
        {
            subscriptionService.addCurrentMarketProductInterest(listener,sessionName,classKey,productKey);
            fail("Should raise an DataValidationException");
        }
        catch(DataValidationException expected)
        {
            if(expected.details.error != DataValidationCodes.LISTENER_ALREADY_REGISTERED)
            {
                fail("Should contain error code:"+DataValidationCodes.LISTENER_ALREADY_REGISTERED);
            }
            else
            {
                assertTrue(true);
            }
        }
        subscriptionService.removeCurrentMarketProductInterest(listener,sessionName,classKey,productKey);
        assertChannelSubscriptionTrue();
        subscriptionService.removeCurrentMarketClassInterest(listener,sessionName,classKey);
        assertChannelSubscriptionFalse();
    }

    public void testSingleListenerForClassSubscriptionReject()
            throws Exception
    {
        Object listener = new Object();
        subscriptionService.addCurrentMarketProductInterest(listener,sessionName,classKey,productKey);
        assertChannelSubscriptionTrue();
        try
        {
            subscriptionService.addCurrentMarketClassInterest(listener,sessionName,classKey);
            fail("Should raise an DataValidationException");
        }
        catch(DataValidationException expected)
        {
            if(expected.details.error != DataValidationCodes.LISTENER_ALREADY_REGISTERED)
            {
                fail("Should contain error code:"+DataValidationCodes.LISTENER_ALREADY_REGISTERED);
            }
            else
            {
                assertTrue(true);
            }
        }
        subscriptionService.removeCurrentMarketProductInterest(listener,sessionName,classKey,productKey);
        assertChannelSubscriptionFalse();
    }

    public void testSingleListenerForProductSubAndClassUnSub()
            throws Exception
    {
        Object listener = new Object();
        subscriptionService.addCurrentMarketProductInterest(listener,sessionName,classKey,productKey);
        assertChannelSubscriptionTrue();
        subscriptionService.addCurrentMarketProductInterest(listener,sessionName,classKey,productKey+2);
        assertChannelSubscriptionTrue();
        subscriptionService.removeCurrentMarketClassInterest(listener,sessionName,classKey);
        assertChannelSubscriptionFalse();
    }

    public void testMultipleProductSubscriptions()
            throws Exception
    {
        Object listener = new Object();
        subscriptionService.addCurrentMarketProductInterest(listener,sessionName,classKey,productKey);
        assertChannelSubscriptionTrue();
        subscriptionService.addCurrentMarketProductInterest(listener,sessionName,classKey,productKey+2);
        assertChannelSubscriptionTrue();
        subscriptionService.removeCurrentMarketProductInterest(listener,sessionName,classKey,productKey);
        assertChannelSubscriptionTrue();
        subscriptionService.removeCurrentMarketProductInterest(listener,sessionName,classKey,productKey+2);
        assertChannelSubscriptionFalse();
        subscriptionService.addCurrentMarketProductInterest(listener,sessionName,classKey,productKey);
        assertChannelSubscriptionTrue();
        subscriptionService.addCurrentMarketProductInterest(listener,sessionName,classKey,productKey+2);
        assertChannelSubscriptionTrue();
        subscriptionService.removeCurrentMarketClassInterest(listener,sessionName,classKey);
        assertChannelSubscriptionFalse();
    }

    public void testMultipleListenersForClassSubscription()
            throws Exception
    {
        Object listener1 = new Object();
        subscriptionService.addCurrentMarketClassInterest(listener1,sessionName,classKey);
        assertChannelSubscriptionTrue();
        Object listener2 = new Object();
        subscriptionService.addCurrentMarketClassInterest(listener2,sessionName,classKey);
        assertChannelSubscriptionTrue();
        subscriptionService.removeCurrentMarketClassInterest(listener1, sessionName, classKey);
        assertChannelSubscriptionTrue();
        subscriptionService.removeCurrentMarketClassInterest(listener2, sessionName, classKey);
        assertChannelSubscriptionFalse();
    }

    public void testMultipleGroupSubscription()
            throws Exception
    {
        SubscriptionService subscriptionService2 = getNewSubscriptionService();
        Object listener1 = new Object();
        subscriptionService.addCurrentMarketClassInterest(listener1,sessionName,classKey);
        assertChannelSubscriptionTrue();
        Object listener2 = new Object();
        subscriptionService2.addCurrentMarketClassInterest(listener2,sessionName,classKey);
        assertChannelSubscriptionTrue();
        subscriptionService.removeCurrentMarketClassInterest(listener1,sessionName,classKey);
        assertChannelSubscriptionTrue();
        subscriptionService2.removeCurrentMarketClassInterest(listener2,sessionName,classKey);
        assertChannelSubscriptionFalse();
    }

    public void testDefaultSubscription()
            throws Exception
    {
        Object listener = new Object();
        assertTrue(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).getTextMessagingSubscription().isDefaultSubscription());
        subscriptionService.addCurrentMarketClassInterest(listener,sessionName,classKey);
        assertTrue(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).getTextMessagingSubscription().channelSubscribed());
        subscriptionService.removeCurrentMarketClassInterest(listener,sessionName,classKey);
        assertFalse(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).getTextMessagingSubscription().channelSubscribed());
        subscriptionService.addSessionClassInterest(listener, sessionName, classKey);
        assertTrue(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).getTextMessagingSubscription().channelSubscribed());
        subscriptionService.addCurrentMarketClassInterest(listener,sessionName,classKey);
        assertTrue(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).getTextMessagingSubscription().channelSubscribed());
        subscriptionService.removeCurrentMarketClassInterest(listener,sessionName,classKey);
        assertTrue(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).getTextMessagingSubscription().channelSubscribed());
        subscriptionService.removeSessionClassInterest(listener, sessionName, classKey);
        assertFalse(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).getTextMessagingSubscription().channelSubscribed());
        SubscriptionService subscriptionService2 = getNewSubscriptionService();
        Object listener2 = new Object();
        subscriptionService2.addCurrentMarketClassInterest(listener2,sessionName,classKey);
        assertTrue(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).getTextMessagingSubscription().channelSubscribed());
        subscriptionService.addCurrentMarketClassInterest(listener, sessionName, classKey);
        subscriptionService2.removeCurrentMarketClassInterest(listener2, sessionName, classKey);
        assertTrue(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).getTextMessagingSubscription().channelSubscribed());
        subscriptionService.removeCurrentMarketClassInterest(listener, sessionName, classKey);
        assertFalse(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).getTextMessagingSubscription().channelSubscribed());
    }

    public void testCleanUp()
            throws Exception
    {
        Object listener = new Object();
        subscriptionService.addCurrentMarketClassInterest(listener, sessionName, classKey);
        assertChannelSubscriptionTrue();
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

    public void testCurrentMarketSubscriptionMutliThreaded()
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
                    subscriptionService1.addCurrentMarketClassInterest(listener1, sessionName, classKey);
                    assertTrue(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName, classKey)).getCurrentMarketSubscription().channelSubscribed());
                    assertTrue(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName, classKey)).getTextMessagingSubscription().channelSubscribed());
                    Thread.currentThread().sleep(50);
                    subscriptionService1.removeCurrentMarketClassInterest(listener1, sessionName, classKey);
                    assertTrue(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName, classKey)).getCurrentMarketSubscription().channelSubscribed());
                    assertTrue(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName, classKey)).getTextMessagingSubscription().channelSubscribed());
                    long startTime = System.currentTimeMillis();
                    while(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName, classKey)).getCurrentMarketSubscription().channelSubscribed())
                    {
                    }
                    System.out.println("waiting time:"+(System.currentTimeMillis()-startTime));
                    assertFalse(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName, classKey)).getCurrentMarketSubscription().channelSubscribed());
                    assertFalse(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName, classKey)).getTextMessagingSubscription().channelSubscribed());
                    System.out.println("testCurrentMarketSubscriptionMutliThreaded thread1 finished.");
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
                    subscriptionService2.addCurrentMarketClassInterest(listener2, sessionName, classKey);
                    assertTrue(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName, classKey)).getCurrentMarketSubscription().channelSubscribed());
                    assertTrue(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName, classKey)).getTextMessagingSubscription().channelSubscribed());
                    Thread.currentThread().sleep(100);
                    subscriptionService2.removeCurrentMarketClassInterest(listener2, sessionName, classKey);
                    assertFalse(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName, classKey)).getCurrentMarketSubscription().channelSubscribed());
                    assertFalse(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName, classKey)).getTextMessagingSubscription().channelSubscribed());
                    System.out.println("testCurrentMarketSubscriptionMutliThreaded thread2 finished.");
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

    protected void assertChannelSubscriptionTrue()
    {
        assertTrue(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).getCurrentMarketSubscription().channelSubscribed());
    }

    protected void assertChannelSubscriptionFalse()
    {
        assertFalse(subscriptionCollectionService.getSessionClassSubscriptionCollection(new SessionKeyContainer(sessionName,classKey)).getCurrentMarketSubscription().channelSubscribed());
    }

    public static Test suite() {
        return new TestSuite(UnitTestCurrentMarketSubscription.class);
    }
}
