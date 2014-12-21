package com.cboe.client.util;

import com.cboe.idl.cmiCallback.CMIRFQConsumer;
import com.cboe.idl.cmiCallback.CMIRFQConsumerPOA;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.loggingService.LogService;
import com.cboe.infrastructureServices.orbService.OrbServiceBaseImpl;
import com.cboe.ORBInfra.ORB.AnyImpl;
import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import org.junit.Before;    // annotation
import org.junit.Test;      // annotation
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class ClientObjectResolverTest
{
    // Allow this JUnit 4 test to run under JUnit3
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(ClientObjectResolverTest.class);
    }

    private org.omg.CORBA.ORB orb;
    private OrbServiceBaseImpl orbService;
    private FoundationFramework ff;
    private LogService logService;
    private CMIRFQConsumerPOA rfqConsumer;
    private CMIRFQConsumer rfqConsumer_this;

    @Before public void setUpMocks()
    {
        // mock this call in ClientObjectResolver:
        // FoundationFramework.getInstance().getOrbService().getOrb()
        orb = mock(org.omg.CORBA.ORB.class);

        orbService = mock(OrbServiceBaseImpl.class);
        when(orbService.getOrb()).thenReturn(orb);

        ff = mock(FoundationFramework.class);
        when(ff.getOrbService()).thenReturn(orbService);

        FoundationFramework.setInstanceForTesting(ff);

        // Mocking FoundationFramework interferes with Log. Set up mocks.
        logService = mock(LogService.class);
        when(ff.getDefaultLogService()).thenReturn(logService);

        // Fake CORBA object to pass to clientObjectResolver.resolveObject
        rfqConsumer_this = mock(CMIRFQConsumer.class);
        rfqConsumer = mock(CMIRFQConsumerPOA.class);
        when(rfqConsumer._this()).thenReturn(rfqConsumer_this);
    }

    @Test public void testResolveObject()
    {
        // ClientObjectResolver.resolveObject calls orb.object_to_string()
        when(orb.object_to_string(any(org.omg.CORBA.Object.class)))
                .thenReturn("ior:dummy");

        // Now, the test
        org.omg.CORBA.Object obj = ClientObjectResolver.resolveObject(
                rfqConsumer._this(),
                "com.cboe.idl.cmiCallback.CMIRFQConsumerHelper");
        assertNull(obj);    // result is null because we supplied mock objects

        // We can at least verify that resolveObject made the expected calls. 
        verify(ff).getOrbService();
        verify(orbService).getOrb();
        verify(orb).object_to_string(rfqConsumer._this());
    }

    @Test public void testResolveObjectWithTimeout()
    {
        final int timeout = 100;
        org.omg.CORBA.Any anyLong = mock(AnyImpl.class);
        when(anyLong.extract_long()).thenReturn(timeout);
        when(orb.create_any()).thenReturn(anyLong);

        /* TODO: Can't make this test work with mock objects
        try
        {
            org.omg.CORBA.Object obj = ClientObjectResolver.resolveObject(
                    rfqConsumer._this(),
                    "com.cboe.idl.cmiCallback.CMIFRQConsumerHelper",
                    timeout);
        }
        catch (Exception e)
        {
            System.out.println("\n* Exception in testResolveObjectWithTimeout");
            e.printStackTrace();
            fail("Exception thrown, not expected");
        }
        */
    }
}
