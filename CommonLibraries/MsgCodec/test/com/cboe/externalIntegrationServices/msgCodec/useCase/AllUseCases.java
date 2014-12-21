package com.cboe.externalIntegrationServices.msgCodec.useCase;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.cboe.externalIntegrationServices.msgCodec.useCase.tests.CatchingExceptions;
import com.cboe.externalIntegrationServices.msgCodec.useCase.tests.CorruptData;
import com.cboe.externalIntegrationServices.msgCodec.useCase.tests.PingTest;
import com.cboe.externalIntegrationServices.msgCodec.useCase.tests.ReadingBuffers;
import com.cboe.externalIntegrationServices.msgCodec.useCase.tests.SetCodecFields;
import com.cboe.externalIntegrationServices.msgCodec.useCase.tests.blocking.DataBufferBlockTests;
import com.cboe.externalIntegrationServices.msgCodec.useCase.tests.rollout.RolloutTests;

public class AllUseCases {

    public static Test suite() {
        TestSuite suite = new TestSuite(
                "Test for com.cboe.externalIntegrationServices.msgCodec.useCase");
        // $JUnit-BEGIN$
        suite.addTestSuite(SetCodecFields.class);
        suite.addTestSuite(ReadingBuffers.class);
        suite.addTestSuite(CatchingExceptions.class);
        suite.addTestSuite(RolloutTests.class);
        suite.addTestSuite(PingTest.class);
        suite.addTestSuite(DataBufferBlockTests.class);
        suite.addTestSuite(CorruptData.class);
        // $JUnit-END$
        return suite;
    }

}
