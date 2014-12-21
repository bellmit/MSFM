/*
 * Created on Oct 20, 2008
 */
package com.cboe.externalIntegrationServices.msgCodec;

import com.cboe.externalIntegrationServices.msgCodec.useCase.tests.CatchingExceptions;
import com.cboe.externalIntegrationServices.msgCodec.useCase.tests.PingTest;
import com.cboe.externalIntegrationServices.msgCodec.useCase.tests.ReadingBuffers;
import com.cboe.externalIntegrationServices.msgCodec.useCase.tests.SetCodecFields;
import com.cboe.externalIntegrationServices.msgCodec.useCase.tests.blocking.DataBufferBlockTests;
import com.cboe.externalIntegrationServices.msgCodec.useCase.tests.rollout.RolloutTests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllFieldCodecTests extends TestCase 
{
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("AllFieldCodecTests");
        suite.addTest(AsciiStringFieldCodecTest.suite());
        suite.addTest(ByteArrayFieldCodecTest.suite());
        suite.addTest(ByteFieldCodecTest.suite());
        suite.addTest(CompressedIntFieldCodecTest.suite());
        suite.addTest(IntFieldCodecTest.suite());
        suite.addTest(LongFieldCodecTest.suite());
        suite.addTest(SequenceFieldCodecTest.suite());
        suite.addTest(ShortFieldCodecTest.suite());
        
        suite.addTest(DataBufferBlockTest.suite());
        /*
         * use case tests
         */
        suite.addTestSuite(SetCodecFields.class);
        suite.addTestSuite(ReadingBuffers.class);
        suite.addTestSuite(CatchingExceptions.class);
        suite.addTestSuite(RolloutTests.class);
        suite.addTestSuite(PingTest.class);
        suite.addTestSuite(DataBufferBlockTests.class);
        
        return suite;
    }
    
    protected void setUp() throws Exception {
        super.setUp();
    }
}
