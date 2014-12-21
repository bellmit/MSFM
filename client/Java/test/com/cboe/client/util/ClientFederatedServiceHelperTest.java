package com.cboe.client.util;

import com.cboe.domain.groupService.BulkActionRequestImpl;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.businessServices.TradingSessionServiceHelper;
import com.cboe.idl.cmiConstants.TimesInForce;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiUtil.KeyValueStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.idl.constants.FederatedOperationType;
import com.cboe.idl.constants.TradingSessionDestinationCodes;
import com.cboe.idl.constants.PropertyFederatedBulkOperation;
import com.cboe.idl.session.TradingSessionStruct;
import com.cboe.idl.util.ServerResponseStruct;
import com.cboe.idl.util.SummaryStruct;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.utilities.AbstractTransaction;
import com.cboe.infrastructureServices.foundationFramework.utilities.Transaction;
import com.cboe.interfaces.businessServices.TradingSessionService;
import com.cboe.interfaces.businessServices.TradingSessionServiceHome;
import com.cboe.interfaces.domain.groupService.BulkActionRequest;
import com.cboe.interfaces.domain.groupService.BulkActionRequestHome;
import com.cboe.proxy.businessServices.ProxyHomeBase;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;  // annotation
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClientFederatedServiceHelperTest
{
    // Allow this JUnit 4 test to run under JUnit3
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(ClientFederatedServiceHelperTest.class);
    }

    @Test public void testGetValueFromKeyValueStruct()
    {
        KeyValueStruct nolist[] = null;
        KeyValueStruct emptylist[] = new KeyValueStruct[0];
        KeyValueStruct kvsseq[] = new KeyValueStruct[3];
        kvsseq[0] = new KeyValueStruct("key0", "value0");
        kvsseq[1] = new KeyValueStruct("key1", "value1");
        kvsseq[2] = new KeyValueStruct("key2", "value2");

        assertEquals("", ClientFederatedServiceHelper
                .getValueFromKeyValueStruct(nolist, "key0"));
        assertEquals("", ClientFederatedServiceHelper
                .getValueFromKeyValueStruct(emptylist, "key0"));
        assertEquals("value0", ClientFederatedServiceHelper
                .getValueFromKeyValueStruct(kvsseq, "key0"));
        assertEquals("value1", ClientFederatedServiceHelper
                .getValueFromKeyValueStruct(kvsseq, "key1"));
        assertEquals("value2", ClientFederatedServiceHelper
                .getValueFromKeyValueStruct(kvsseq, "key2"));
        assertEquals("", ClientFederatedServiceHelper
                .getValueFromKeyValueStruct(kvsseq, "key3"));
        assertEquals("", ClientFederatedServiceHelper
                .getValueFromKeyValueStruct(kvsseq, null));
    }

    @Test public void testPrepareFederatedResponseSummaryStruct()
    {
        final int SUCCESSFUL = 1, NOT_SUCCESSFUL = 0,
                  FAILED = 1, NOT_FAILED = 0;
        KeyValueStruct noprops[] = null;
        KeyValueStruct properties[] = new KeyValueStruct[1];
        properties[0] = new KeyValueStruct(
                PropertyFederatedBulkOperation.GROUP_KEY, "42");
        KeyValueStruct malaprop[] = new KeyValueStruct[1];
        malaprop[0] = new KeyValueStruct(
                PropertyFederatedBulkOperation.GROUP_KEY, "NonNumeric");

        SummaryStruct s = ClientFederatedServiceHelper
                .prepareFederatedResponseSummaryStruct(
                        NOT_SUCCESSFUL, 20, NOT_FAILED, "transId1", "server1",
                        noprops, (short)1);
        assertEquals("transId1", s.transactionId);
        assertEquals("server1", s.serverId);
        assertEquals(-1, s.groupKey);
        assertEquals(NOT_SUCCESSFUL, s.successfull);
        assertEquals(20, s.activeQuantity);
        assertEquals(NOT_FAILED, s.failed);
        assertEquals(1, s.operationType);

        s = ClientFederatedServiceHelper
                .prepareFederatedResponseSummaryStruct(
                        SUCCESSFUL, 15, FAILED, "transId2", "server2",
                        properties, (short)2);
        assertEquals("transId2", s.transactionId);
        assertEquals("server2", s.serverId);
        assertEquals(42, s.groupKey);
        assertEquals(SUCCESSFUL, s.successfull);
        assertEquals(15, s.activeQuantity);
        assertEquals(FAILED, s.failed);
        assertEquals(2, s.operationType);

        // Use of malaprop casues a call to Log.exception, which
        // creates a lot of messy output in the middle of our test run.
        // TODO: If Log ever provides method setInstanceForTesting, use it!
        s = ClientFederatedServiceHelper
                .prepareFederatedResponseSummaryStruct(
                        SUCCESSFUL, 23, NOT_FAILED, "transId3", "server3",
                        malaprop, (short)3);
        assertEquals("transId3", s.transactionId);
        assertEquals("server3", s.serverId);
        assertEquals(-1, s.groupKey);
        assertEquals(SUCCESSFUL, s.successfull);
        assertEquals(23, s.activeQuantity);
        assertEquals(NOT_FAILED, s.failed);
        assertEquals(3, s.operationType);
    }

    @Test public void testPersistBulkActionRequest()
    {
        KeyValueStruct noprops[] = null;
        KeyValueStruct twoprops[] = new KeyValueStruct[2];
        twoprops[0] = new KeyValueStruct("yksi", "unu");
        twoprops[1] = new KeyValueStruct("kaksi", "du");

        BulkActionRequestHome barHome = mock(BulkActionRequestHome.class);
        AbstractTransaction trans = mock(AbstractTransaction.class);
        Transaction.setInstanceForTesting(trans);
        
        // persistBulkActionRequest ("method") calls
        // 1. trans.startTransaction
        // 2. barHome.create
        // 3. trans.commit
        // 4. trans.rollback if exception or if trans.commit returns false

        // test1: use noprops. trans.commit returns true -> object
        // test2: use twoprops. trans.commit returns true -> object
        // test3: use noprops, trans.commit returns false -> null
        // test4: use noprops, barHome.create throws SystemException -> null
        // all tests: barHome.create returns reasonable structure.
        doNothing()
        .when(trans).startTransaction();

        when(trans.commit())
        .thenReturn(true, true, false, true);

        try
        {
            when(barHome.create(anyString(), anyString(), anyString(),
                    anyString(), anyString(), anyLong()))
                .thenReturn(new BulkActionRequestImpl())
                .thenReturn(new BulkActionRequestImpl())
                .thenReturn(new BulkActionRequestImpl())
                .thenThrow(new SystemException());
        }
        catch (SystemException se)
        {
            // This is a setup, not a call, so I don't understand how it can
            // throw an exception; but the compiler insists that an exception
            // is possible, so I have to create this exception handler.
            fail("SystemException thrown during setup of mock BulkActionRequestHome");
        }

        DateTimeStruct dt = new DateTimeStruct();
        dt.date = new DateStruct((byte)10, (byte)20, (short)10);
        dt.time = new TimeStruct((byte)8, (byte)35, (byte)42, (byte)0);

        // TODO: If Log ever provides method setInstanceForTesting, use it!
        BulkActionRequest request;
        request = ClientFederatedServiceHelper.persistBulkActionRequest(
                barHome, dt, "user1", "type1", "trans1", "server1", noprops);
        assertNotNull(request);

        request = ClientFederatedServiceHelper.persistBulkActionRequest(
                barHome, dt, "user2", "type2", "trans2", "server2", twoprops);
        assertNotNull(request);

        request = ClientFederatedServiceHelper.persistBulkActionRequest(
                barHome, dt, "user3", "type3", "trans3", "server3", noprops);
        assertNotNull(request);

        request = ClientFederatedServiceHelper.persistBulkActionRequest(
                barHome, dt, "user4", "type4", "trans4", "server4", noprops);
        assertNull(request);
    }

    // for testGetServiceRoutes
    private static class TssHome extends ProxyHomeBase
            implements TradingSessionServiceHome
    {
        private static TradingSessionStruct sessions[];
        static
        {
            DateStruct someDay = new DateStruct((byte)1,(byte)2,(short)10);
            TimeStruct aStart = new TimeStruct((byte)8,(byte)0,(byte)0,(byte)0);
            TimeStruct anEnd = new TimeStruct((byte)15,(byte)0,(byte)0,(byte)0);
            sessions = new TradingSessionStruct[3];
            sessions[0] = new TradingSessionStruct();
            sessions[1] = new TradingSessionStruct();
            sessions[2] = new TradingSessionStruct();
            for (TradingSessionStruct s : sessions)
            {
                s.exchangeAcronym = "CBOE";
                s.sessionState = 0;
                s.endOfSessionStrategy = "punt";
                s.isLastSessionForBusinessDay = false;
                s.autoStartEndOfSession = false;
                s.abortEndOfSession = false;
                s.endOfSessionEventCompleted = 0;
                s.endOfSessionEventInProgress = 0;
                s.businessDay = someDay;
                s.startTime = aStart;
                s.endTime = anEnd;
            }
            sessions[0].sessionName = "W_MAIN";
            sessions[0].sessionDestinationCode =
                    TradingSessionDestinationCodes.SBT;
            sessions[1].sessionName = "FLOOR";
            sessions[1].sessionDestinationCode =
                    TradingSessionDestinationCodes.OPEN_OUTCRY;
            sessions[2].sessionName = "Underlying";
            sessions[2].sessionDestinationCode =
                    TradingSessionDestinationCodes.UNDERLYING;
        }
        public TradingSessionService find()
            { return create(); }
        protected String getHelperClassName()
            { return TradingSessionServiceHelper.class.getName(); }
        public TradingSessionService create()
        {
            TradingSessionService tss = mock(TradingSessionService.class);
            try
            {
                when(tss.getTradingSessions()).thenReturn(sessions);
            }
            catch (CommunicationException ce)
            {
                // Don't understand how this can happen, but compiler needs it.
                fail("CommunicationException thrown during setup of mock TradingSessionService");
            }
            catch (SystemException se)
            {
                // Don't understand how this can happen, but compiler needs it.
                fail("SystemException thrown during setup of mock TradingSessionService");
            }
            catch (AuthorizationException ae)
            {
                // Don't understand how this can happen, but compiler needs it.
                fail("AuthorizationException thrown during setup of mock TradingSessionService");
            }
            return tss;
        }
    }

    @Test public void testGetServiceRoutes()
    {
        HomeFactory.getInstance().addHomeForTesting(
                TradingSessionServiceHome.HOME_NAME, new TssHome());

        Map<String,String> one = new HashMap<String,String>();
        one.put("W_MAIN:something", "Hybrid");
        one.put("Underlying:other", "equities");
        one.put("FLOOR:both", "allele");

        List<String> routes =
                ClientFederatedServiceHelper.getServiceRoutes(one);
        assertEquals("W_MAIN:something", routes.get(0));
        assertEquals("FLOOR:both", routes.get(1));
        assertEquals(2, routes.size());
    }

    @Test public void testIsStringEmpty()
    {
        assertTrue(ClientFederatedServiceHelper.isStringEmpty(""));
        assertTrue(ClientFederatedServiceHelper.isStringEmpty(null));
        assertTrue(ClientFederatedServiceHelper.isStringEmpty("  "));
        assertTrue(ClientFederatedServiceHelper.isStringEmpty("\t\n"));
        assertFalse(ClientFederatedServiceHelper.isStringEmpty("A"));
        assertFalse(ClientFederatedServiceHelper.isStringEmpty(" B "));
        assertFalse(ClientFederatedServiceHelper.isStringEmpty(" C"));
        assertFalse(ClientFederatedServiceHelper.isStringEmpty("D "));
    }

    @Test public void testGetServerResponseStruct()
    {
        ServerResponseStruct response;
        response = ClientFederatedServiceHelper.getServerResponseStruct(
                "server1", (short) 1, "description1");
        assertEquals("server1", response.serverId);
        assertEquals(1, response.errorCode);
        assertEquals("description1", response.description);
    }

    @Test public void testValidateUserIdRequestingCancel1()
            throws DataValidationException
    {
        ClientFederatedServiceHelper.validateUserIdRequestingCancel("ABC");
    }

    @Test(expected=DataValidationException.class)
    public void testValidateUserIdRequestingCancel2()
            throws DataValidationException
    {
        ClientFederatedServiceHelper.validateUserIdRequestingCancel("");
    } 

    @Test(expected=DataValidationException.class)
    public void testValidateUserIdRequestingCancel3()
            throws DataValidationException
    {
        ClientFederatedServiceHelper.validateUserIdRequestingCancel(null);
    }

    @Test(expected=DataValidationException.class)
    public void testValidateUserIds1() throws DataValidationException
    {
        ClientFederatedServiceHelper.validateUserIds(null);
    }

    @Test(expected=DataValidationException.class)
    public void testValidateUserIds2() throws DataValidationException
    {
        String names[] = new String[0];
        ClientFederatedServiceHelper.validateUserIds(names);
    }

    @Test public void testValidateUserIds3() throws DataValidationException
    {
        String names[] = new String[1];
        ClientFederatedServiceHelper.validateUserIds(names);
    }

    @Test public void testValidateUserIds4() throws DataValidationException
    {
        String names[] = new String[1];
        names[0] = "VVV";
        ClientFederatedServiceHelper.validateUserIds(names);
    }

    private KeyValueStruct[] workstationIds()
    {
        KeyValueStruct seq[] = new KeyValueStruct[3];
        seq[0] = new KeyValueStruct("", "");
        seq[1] = new KeyValueStruct("key1", "val1");
        seq[2] = new KeyValueStruct("key2", "val2");
        return seq;
    }

    @Test(expected=DataValidationException.class)
    public void testValidateWorkstationID1() throws DataValidationException
    {
        KeyValueStruct properties[] = workstationIds();
        ClientFederatedServiceHelper.validateWorkstationID(properties, "");
    }

    @Test(expected=DataValidationException.class)
    public void testValidateWorkstationID2() throws DataValidationException
    {
        KeyValueStruct properties[] = workstationIds();
        ClientFederatedServiceHelper.validateWorkstationID(properties, "foo");
    }

    @Test public void testValidateWorkstationID3()
            throws DataValidationException
    {
        KeyValueStruct properties[] = workstationIds();
        ClientFederatedServiceHelper.validateWorkstationID(properties, "key1");
    }

    @Test(expected=DataValidationException.class)
    public void testValidateOperationType1() throws DataValidationException
    {
        ClientFederatedServiceHelper.validateOperationType(
                FederatedOperationType.QUOTES);
    }

    @Test public void testValidateOperationType2()
            throws DataValidationException
    {
        ClientFederatedServiceHelper.validateOperationType(
                FederatedOperationType.ORDERS);
    }

    @Test public void testValidateOperationType3()
            throws DataValidationException
    {
        ClientFederatedServiceHelper.validateOperationType(
                FederatedOperationType.IORDERS);
    }

    @Test(expected=DataValidationException.class)
    public void testValidateOrderTypes1() throws DataValidationException
    {
        ClientFederatedServiceHelper.validateOrderTypes(null);
    }

    @Test(expected=DataValidationException.class)
    public void testValidateOrderTypes2() throws DataValidationException
    {
        char types[] = new char[0];
        ClientFederatedServiceHelper.validateOrderTypes(types);
    }

    @Test public void testValidateOrderTypes3() throws DataValidationException
    {
        char types[] = { TimesInForce.DAY, TimesInForce.GTC, TimesInForce.GTD };
        ClientFederatedServiceHelper.validateOrderTypes(types);
    }

    @Test public void testValidateOrderTypes4() throws DataValidationException
    {
        char types[] = { TimesInForce.DAY };
        ClientFederatedServiceHelper.validateOrderTypes(types);
    }

    @Test(expected=DataValidationException.class)
    public void testValidateOrderTypes5() throws DataValidationException
    {
        char types[] = { TimesInForce.DAY, TimesInForce.ALL };
        ClientFederatedServiceHelper.validateOrderTypes(types);
    }

    @Test(expected=DataValidationException.class)
    public void testValidateCorrespondentFirm1() throws DataValidationException
    {
        ClientFederatedServiceHelper.validateCorrespondentFirm(null);
    }

    @Test public void testValidateCorrespondentFirm2()
            throws DataValidationException
    {
        String names[] = new String[0];
        ClientFederatedServiceHelper.validateCorrespondentFirm(names);
    }

    @Test public void testValidateCorrespondentFirm3()
            throws DataValidationException
    {
        String names[] = new String[1];
        names[0] = "UMU";
        ClientFederatedServiceHelper.validateCorrespondentFirm(names);
    }

    @Test(expected=DataValidationException.class)
    public void testValidateClassKeys1() throws DataValidationException
    {
        ClientFederatedServiceHelper.validateClassKeys(null);
    }

    @Test public void testValidateClassKeys2() throws DataValidationException
    {
        int keys[] = new int[0];
        ClientFederatedServiceHelper.validateClassKeys(keys);
    }

    @Test public void testValidateClassKeys3() throws DataValidationException
    {
        int keys[] = new int[1];
        keys[0] = 42;
        ClientFederatedServiceHelper.validateClassKeys(keys);
    }
}
