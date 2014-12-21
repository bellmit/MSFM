package com.cboe.application.subscription.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestAllSubscriptionsInService
{
    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        suite.addTest(UnitTestRFQSubscription.suite());
        suite.addTest(UnitTestCurrentMarketSubscription.suite());
        suite.addTest(UnitTestNBBOSubscription.suite());
        suite.addTest(UnitTestRecapSubscription.suite());
        suite.addTest(UnitTestTickerSubscription.suite());
        suite.addTest(UnitTestExpectedOpeningPriceSubscription.suite());
        suite.addTest(UnitTestBookDepthSubscription.suite());
        suite.addTest(UnitTestAuctionClassSubscription.suite());
        suite.addTest(UnitTestAuctionUserSubscription.suite());
        suite.addTest(UnitTestQuoteLockSubscription.suite());
        suite.addTest(UnitTestUserSubscription.suite());
        suite.addTest(UnitTestSessionClassSubscription.suite());
        suite.addTest(UnitTestProductTypeSubscription.suite());
        suite.addTest(UnitTestFirmSubscription.suite());
        return suite;
    }

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(suite());
    }
}
