package com.cboe.cfix.fix.fix42.test;

/**
 * testSessionLevelMatrix.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * tester file to help junit FIX 4.2 session level matrix
 *
 */

import java.io.*;
import java.net.*;
import java.util.*;

import com.cboe.cfix.fix.fix42.generated.fields.*;
import com.cboe.cfix.fix.fix42.generated.messages.*;
import com.cboe.cfix.fix.fix42.session.*;
import com.cboe.cfix.fix.net.*;
import com.cboe.cfix.fix.parser.*;
import com.cboe.cfix.fix.session.*;
import com.cboe.cfix.fix.util.*;
import com.cboe.cfix.interfaces.*;
import com.cboe.cfix.util.*;
import com.cboe.client.util.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.instrumentationService.factories.*;
import com.cboe.instrumentationService.instrumentors.*;

public class testSessionLevelMatrix
{
    FixSocketAdapter  fixSocketAdapter;
    FixPacketIF       fixPacket;
    FixMessage        fixMessage;
    PackedIntArrayIF  foundErrors;
    Socket            socket;
    FixMessageFactory fixMessageFactory;
    String            message;
    public static int globalInt     = 100;
    public String     senderCompID  = "SL1";
    public String     targetCompID  = "CFIX01";
    public String     userID        = "JIM:JIM";
    String            host          = "localhost";
    int               port          = 50000;

    public final testSessionSettings normalSessionSettings = new testSessionSettings();

    public class testSessionSettings
    {
        public String getSenderCompID()     {return senderCompID;}
        public String getTargetCompID()     {return targetCompID;}
        public int    getSequenceNumber()   {return 1;}
        public Date   getSendingTime()      {return new Date();}
        public String getPossDupFlag()      {return "N";}
        public String getResetSeqNumFlag()  {return "N";}
        public int    getHeartBeatInterval(){return 10;}
    }

    public void setDescription(String description)
    {
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("TEST DESCRIPTION: " + description);
    }

    public void close()
    {
        if (fixSocketAdapter != null)
        {
            fixSocketAdapter.close();
            fixSocketAdapter = null;
            System.out.println("CLOSED CONNECTION");
        }
    }

    public void reset()
    {
        close();

        fixSocketAdapter  = new FixSocketAdapter();
        foundErrors       = new GrowableIntArray();
        fixMessageFactory = new FixMessageFactory();
        fixPacket         = null;
        fixMessage        = null;
        message           = null;
        socket            = null;

        fixSocketAdapter.setDebugFlags(FixSessionDebugIF.DEBUG_OFF);
        fixSocketAdapter.setFixPacketParser(new FixPacketParser());
    }

    public void logon(testSessionSettings sessionSettings) throws Exception
    {
        reset();
        connect();

        FixMessageBuilder fixMessageBuilder = new FixMessageBuilder();
        FixLogonMessage  fixLogonMessage  = new FixLogonMessage();

        fixLogonMessage.fieldEncryptMethod   = FixEncryptMethodField.flyweightNone();
        fixLogonMessage.fieldHeartBtInt      = FixHeartBtIntField.create(sessionSettings.getHeartBeatInterval());

        if ("Y".equals(sessionSettings.getResetSeqNumFlag()))
        {
            fixLogonMessage.fieldResetSeqNumFlag = FixResetSeqNumFlagField.flyweightYesResetSequenceNumbers();
        }

        fixLogonMessage.fieldMsgType         = FixMsgTypeField.create(FixLogonMessage.MsgType);
        fixLogonMessage.fieldMsgSeqNum       = FixMsgSeqNumField.create(sessionSettings.getSequenceNumber());
        fixLogonMessage.fieldSenderCompID    = FixSenderCompIDField.create(sessionSettings.getSenderCompID());
        fixLogonMessage.fieldTargetCompID    = FixTargetCompIDField.create(sessionSettings.getTargetCompID());
        fixLogonMessage.fieldSendingTime     = FixSendingTimeField.create(sessionSettings.getSendingTime());
        fixLogonMessage.fieldSenderSubID     = FixSenderSubIDField.create(userID);

        if ("Y".equals(sessionSettings.getPossDupFlag()))
        {
            fixLogonMessage.fieldPossDupFlag = FixPossDupFlagField.flyweightPossibleDuplicate();
        }

        fixLogonMessage.accept(fixMessageBuilder);

        FastCharacterWriter body = fixMessageBuilder.getFastCharacterWriter();

        fixMessageBuilder.insert(FixBodyLengthField.TagIDAsChars,    body.size());
        fixMessageBuilder.insert(FixBeginStringField.TagIDAsChars,   fixMessageFactory.getFixVersionAsString());
        fixMessageBuilder.append(FixCheckSumField.TagIDAsChars,      FixChecksumHelper.calculateFixChecksumToString(body));

        FixSessionDebugger.dumpFixMessage("FIXDECODE SEND ", body, fixMessageFactory);

        fixSocketAdapter.write(body);
    }

    public void sendHeartBeat(testSessionSettings sessionSettings, int seq) throws Exception
    {
        FixMessageBuilder    fixMessageBuilder    = new FixMessageBuilder();
        FixHeartBeatMessage fixHeartBeatMessage = new FixHeartBeatMessage();

        fixHeartBeatMessage.fieldMsgType         = FixMsgTypeField.create(FixHeartBeatMessage.MsgType);
        fixHeartBeatMessage.fieldMsgSeqNum       = FixMsgSeqNumField.create(seq);
        fixHeartBeatMessage.fieldSenderCompID    = FixSenderCompIDField.create(sessionSettings.getSenderCompID());
        fixHeartBeatMessage.fieldTargetCompID    = FixTargetCompIDField.create(sessionSettings.getTargetCompID());
        fixHeartBeatMessage.fieldSendingTime     = FixSendingTimeField.create(sessionSettings.getSendingTime());

        fixHeartBeatMessage.accept(fixMessageBuilder);

        FastCharacterWriter body = fixMessageBuilder.getFastCharacterWriter();

        fixMessageBuilder.insert(FixBodyLengthField.TagIDAsChars,    body.size());
        fixMessageBuilder.insert(FixBeginStringField.TagIDAsChars,   fixMessageFactory.getFixVersionAsString());
        fixMessageBuilder.append(FixCheckSumField.TagIDAsChars,      FixChecksumHelper.calculateFixChecksumToString(body));

        FixSessionDebugger.dumpFixMessage("FIXDECODE SEND ", body, fixMessageFactory);

        fixSocketAdapter.write(body);
    }

    public void sendTestRequest(testSessionSettings sessionSettings, int seq) throws Exception
    {
        FixMessageBuilder    fixMessageBuilder        = new FixMessageBuilder();
        FixTestRequestMessage fixTestRequestMessage = new FixTestRequestMessage();

        fixTestRequestMessage.fieldMsgType         = FixMsgTypeField.create(FixTestRequestMessage.MsgType);
        fixTestRequestMessage.fieldMsgSeqNum       = FixMsgSeqNumField.create(seq);
        fixTestRequestMessage.fieldSenderCompID    = FixSenderCompIDField.create(sessionSettings.getSenderCompID());
        fixTestRequestMessage.fieldTargetCompID    = FixTargetCompIDField.create(sessionSettings.getTargetCompID());
        fixTestRequestMessage.fieldSendingTime     = FixSendingTimeField.create(sessionSettings.getSendingTime());

        fixTestRequestMessage.fieldTestReqID       = FixTestReqIDField.create("(" + (++globalInt) + ") " + DateHelper.stringizeDateInFixUTCTimeStampFormat());

        fixTestRequestMessage.accept(fixMessageBuilder);

        FastCharacterWriter body = fixMessageBuilder.getFastCharacterWriter();

        fixMessageBuilder.insert(FixBodyLengthField.TagIDAsChars,    body.size());
        fixMessageBuilder.insert(FixBeginStringField.TagIDAsChars,   fixMessageFactory.getFixVersionAsString());
        fixMessageBuilder.append(FixCheckSumField.TagIDAsChars,      FixChecksumHelper.calculateFixChecksumToString(body));

        FixSessionDebugger.dumpFixMessage("FIXDECODE SEND ", body, fixMessageFactory);

        fixSocketAdapter.write(body);
    }

    public void sendResendRequest(testSessionSettings sessionSettings, int seq, int startingNumber, int endingNumber) throws Exception
    {
        FixMessageBuilder    fixMessageBuilder        = new FixMessageBuilder();
        FixResendRequestMessage fixResendRequestMessage = new FixResendRequestMessage();

        fixResendRequestMessage.fieldMsgType         = FixMsgTypeField.create(FixResendRequestMessage.MsgType);
        fixResendRequestMessage.fieldMsgSeqNum       = FixMsgSeqNumField.create(seq);
        fixResendRequestMessage.fieldSenderCompID    = FixSenderCompIDField.create(sessionSettings.getSenderCompID());
        fixResendRequestMessage.fieldTargetCompID    = FixTargetCompIDField.create(sessionSettings.getTargetCompID());
        fixResendRequestMessage.fieldSendingTime     = FixSendingTimeField.create(sessionSettings.getSendingTime());

        fixResendRequestMessage.fieldBeginSeqNo      = FixBeginSeqNoField.create(startingNumber);
        fixResendRequestMessage.fieldEndSeqNo        = FixEndSeqNoField.create(endingNumber);

        fixResendRequestMessage.accept(fixMessageBuilder);

        FastCharacterWriter body = fixMessageBuilder.getFastCharacterWriter();

        fixMessageBuilder.insert(FixBodyLengthField.TagIDAsChars,    body.size());
        fixMessageBuilder.insert(FixBeginStringField.TagIDAsChars,   fixMessageFactory.getFixVersionAsString());
        fixMessageBuilder.append(FixCheckSumField.TagIDAsChars,      FixChecksumHelper.calculateFixChecksumToString(body));

        FixSessionDebugger.dumpFixMessage("FIXDECODE SEND ", body, fixMessageFactory);

        fixSocketAdapter.write(body);
    }

    public void sendLogout(testSessionSettings sessionSettings, int seq) throws Exception
    {
        FixMessageBuilder fixMessageBuilder = new FixMessageBuilder();
        FixLogoutMessage fixLogoutMessage = new FixLogoutMessage();

        fixLogoutMessage.fieldMsgType         = FixMsgTypeField.create(FixLogoutMessage.MsgType);
        fixLogoutMessage.fieldMsgSeqNum       = FixMsgSeqNumField.create(seq);
        fixLogoutMessage.fieldSenderCompID    = FixSenderCompIDField.create(sessionSettings.getSenderCompID());
        fixLogoutMessage.fieldTargetCompID    = FixTargetCompIDField.create(sessionSettings.getTargetCompID());
        fixLogoutMessage.fieldSendingTime     = FixSendingTimeField.create(sessionSettings.getSendingTime());

        fixLogoutMessage.accept(fixMessageBuilder);

        FastCharacterWriter body = fixMessageBuilder.getFastCharacterWriter();

        fixMessageBuilder.insert(FixBodyLengthField.TagIDAsChars,    body.size());
        fixMessageBuilder.insert(FixBeginStringField.TagIDAsChars,   fixMessageFactory.getFixVersionAsString());
        fixMessageBuilder.append(FixCheckSumField.TagIDAsChars,      FixChecksumHelper.calculateFixChecksumToString(body));

        FixSessionDebugger.dumpFixMessage("FIXDECODE SEND ", body, fixMessageFactory);

        fixSocketAdapter.write(body);
    }

    public void dumpFixPacket()
    {
        System.out.println("RCVD [" + fixPacket.getArrayAsString() + "]");
        if (fixPacket.isMessageDisconnected())
        {
            System.out.println("DISCONNECTED");
            return;
        }
        else if (fixPacket.isMessageTimedOut())
        {
            System.out.println("TIMED_OUT");
            return;
        }
        else if (fixPacket.isBadFixMessage())
        {
            System.out.println("BAD PACKET " + fixPacket);
            return;
        }
        else if (fixPacket.isGarbageMessage())
        {
            System.out.println("GARBAGE PACKET " + fixPacket);
            return;
        }

        try
        {
            fixMessage = (FixMessage) fixMessageFactory.createFixMessageFromMsgType(fixPacket.charAt(fixPacket.getValueOffset(2)));
            if (fixMessage == null)
            {
                System.out.println("NULL MESSAGE");
                return;
            }

            foundErrors = fixMessage.build(fixPacket, foundErrors, FixMessageIF.VALIDATE_UNUSED_FIELDS, FixSessionDebugIF.DEBUG_ALL);

            System.out.println(fixMessage.toString());

            if (!foundErrors.isEmpty())
            {
                System.out.println("ERRORS[" + fixPacket.getArrayAsString() + "]");
                for (int err = 0; err < foundErrors.length(); err++)
                {
                    byte error    = BitHelper.unpackHighByte(foundErrors.get(err));
                    int  position = BitHelper.unpackLowShortAsInt(foundErrors.get(err));
                    System.out.println("   " + FixException.toString(error, position) + "  " + fixPacket.getTag(position) + "=" + new String(fixPacket.getArray(), fixPacket.getValueOffset(position), fixPacket.getValueLength(position)));
                }
            }
        }
        catch (Exception ex)
        {
            System.out.println("Exception: " + ExceptionHelper.getStackTrace(ex));
        }
    }

    public void connect()
    {
        while (true)
        {
             try
             {
                  socket = new Socket(host, port);
                  break;
             }
             catch (IOException ex)
             {
                  System.out.println("Exception: " + ex);
                  ThreadHelper.sleepSeconds(1);
             }
        }

        fixSocketAdapter.resetSocket(socket);
    }

    public void testLogonSuccess() throws Exception
    {
        setDescription("logon -- server should reply with a valid logon, then drop connection");

        logon(normalSessionSettings);

        fixPacket = fixSocketAdapter.read(); dumpFixPacket();

        close();
    }

    public void testLogonTwice() throws Exception
    {
        setDescription("logon -- server should reply with a valid logon -- then logon again");

        logon(normalSessionSettings);

        fixPacket = fixSocketAdapter.read(); dumpFixPacket();

        FixSessionDebugger.dumpFixMessage("FIXDECODE SEND ", message, fixMessageFactory);

        fixSocketAdapter.write(message);

        fixPacket = fixSocketAdapter.read(); dumpFixPacket();

        close();
    }

    public void testLogonNoMessageSent() throws Exception
    {
        setDescription("don't logon -- server should drop connection within a few seconds");

        reset();
        connect();

        fixPacket = fixSocketAdapter.read(); dumpFixPacket();

        close();
    }

    public void testLogonGarbageMessageSent() throws Exception
    {
        setDescription("send garbage message instead of logon -- server should drop connection immediately");

        reset();
        connect();

        fixSocketAdapter.write("GARBAGE GARBAGE GARBAGE GARBAGE GARBAGE GARBAGE GARBAGE GARBAGE GARBAGE GARBAGE GARBAGE");

        fixPacket = fixSocketAdapter.read(); dumpFixPacket();

        close();
    }

    public void testLogonAndImmediatelyDropConnection() throws Exception
    {
        setDescription("logon and immediately drop connection");

        logon(normalSessionSettings);

        close();
    }

    public void testLogonIncorrectSenderCompID() throws Exception
    {
        setDescription("logon with invalid SenderCompID -- server should reply with a reject");

        logon(new testSessionSettings() {public String getSenderCompID() {return "WRONG_SENDER_COMP_ID";}});

        fixPacket = fixSocketAdapter.read(); dumpFixPacket();

        close();
    }

    public void testLogonIncorrectTargetCompID() throws Exception
    {
        setDescription("logon with invalid TargetCompID -- server should reply with a reject");

        logon(new testSessionSettings() {public String getTargetCompID() {return "WRONG_TARGET_COMP_ID";}});

        fixPacket = fixSocketAdapter.read(); dumpFixPacket();

        close();
    }

    public void testLogonIncorrectSendingTime() throws Exception
    {
        setDescription("logon with invalid SendingTime -- server should reply with a reject");

        logon(new testSessionSettings() {public Date getSendingTime() {return new Date(System.currentTimeMillis() - DateHelper.convertDaysToMilliseconds(1));}});

        fixPacket = fixSocketAdapter.read(); dumpFixPacket();

        close();
    }

    public void testLogonHighSequenceNumber() throws Exception
    {
        setDescription("logon with SeqNum=2 -- server should reply with a valid logon, followed by ResendRequest");

        logon(new testSessionSettings() {public int getSequenceNumber() {return 5;}});

        for (int i = 0; i < 2; i++)
        {
            fixPacket = fixSocketAdapter.read(); dumpFixPacket();
        }

        close();
    }

    public void testLogonLowSequenceNumber() throws Exception
    {
        setDescription("logon with SeqNum=0 -- server should drop");

        logon(new testSessionSettings() {public int getSequenceNumber() {return 0;}});

        fixPacket = fixSocketAdapter.read(); dumpFixPacket();

        close();
    }

    public void testLogonWithPossDupFlagEqualYes() throws Exception
    {
        setDescription("logon with PossDupFlag=Y -- server should reply with a valid logon");

        logon(new testSessionSettings() {public String getPossDupFlag() {return "Y";}});

        fixPacket = fixSocketAdapter.read(); dumpFixPacket();

        close();
    }

    public void testLogonAndCheckForTestRequest() throws Exception
    {
        setDescription("logon -- server should reply with a valid logon.  Then don't send anything more -- server should request TestRequest, and drop connection after 3 TestRequests");

        logon(normalSessionSettings);

        while (true)
        {
            fixPacket = fixSocketAdapter.read(); dumpFixPacket();

            if (fixPacket.isMessageDisconnected())
            {
                break;
            }
        }

        close();
    }

    public void testLogonAndResetSeqNum() throws Exception
    {
        setDescription("logon with SeqNum=5 and ResetSeqNum=Y -- server should allow the following HeartBeat with new SeqNums=2.");

        logon(new testSessionSettings() {public String getResetSeqNumFlag() {return "Y";} public int getSequenceNumber() {return 5;}});

        fixPacket = fixSocketAdapter.read(); dumpFixPacket();

        sendHeartBeat(normalSessionSettings, 2);

        fixPacket = fixSocketAdapter.read(); dumpFixPacket();

        close();
    }

    public void testLogoutSuccess() throws Exception
    {
        setDescription("Establish a connection, logon, receive logon, send logout, receive logout");

        logon(normalSessionSettings);

        fixPacket = fixSocketAdapter.read(); dumpFixPacket();

        sendLogout(normalSessionSettings, 2);

        fixPacket = fixSocketAdapter.read(); dumpFixPacket();

        close();
    }

    public void testLogoutInMiddleOfResendRequest() throws Exception
    {
        setDescription("Establish a connection, logon, receive logon, send OOS heartbeat, receive ResendRequest, send OOS logout, receive logout");

        logon(normalSessionSettings);

        fixPacket = fixSocketAdapter.read(); dumpFixPacket();

        sendHeartBeat(normalSessionSettings, 3);

        fixPacket = fixSocketAdapter.read(); dumpFixPacket();

        sendLogout(normalSessionSettings, 4);

        fixPacket = fixSocketAdapter.read(); dumpFixPacket();

        close();
    }

    public void testResendRequest() throws Exception
    {
        setDescription("Establish a connection, logon, send some TestRequests, then send a ResendRequest from 1-infinity");

        logon(new testSessionSettings() {public int getHeartBeatInterval() {return 10;}});

        fixPacket = fixSocketAdapter.read(); dumpFixPacket();

        int i;
        for (i = 2; i < 6; i++)
        {
            sendTestRequest(normalSessionSettings, i);
            fixPacket = fixSocketAdapter.read(); dumpFixPacket();
        }

        for (i = 0; i < 2; i++)
        {
            fixPacket = fixSocketAdapter.read(); dumpFixPacket(); // should drain some heartbeats and testrequests
        }

        sendResendRequest(normalSessionSettings, i, 1, 0);

        for (i = 0; ; i++)
        {
            fixPacket = fixSocketAdapter.read(); dumpFixPacket();

            if (i == 4)
            {
                sendResendRequest(normalSessionSettings, i, 5, 9);
            }

            if (fixPacket.isMessageTimedOut())
            {
                break;
            }

            if (fixPacket.isMessageDisconnected())
            {
                break;
            }
        }

        close();
    }

    public void testGarbageMessageSent() throws Exception
    {
        setDescription("logon and send a garbage message -- server should ignore it, and drop connection after some TestRequests");

        logon(normalSessionSettings);

        fixSocketAdapter.write("GARBAGE GARBAGE GARBAGE GARBAGE GARBAGE GARBAGE GARBAGE GARBAGE GARBAGE GARBAGE GARBAGE");

        while (true)
        {
            fixPacket = fixSocketAdapter.read(); dumpFixPacket();

            if (fixPacket.isMessageDisconnected())
            {
                break;
            }
        }

        close();
    }

    public void testResendSameMessageDenialOfService() throws Exception
    {
        setDescription("force server to request resend, then send the same message many times");

    }

    public void testResendMalformedMessageDenialOfService() throws Exception
    {
        setDescription("force server to request resend, off by 1, then send the same malformed message for every request -- server should stop asking after 2nd receipt");

    }

    public void testLogonWithNoHeartbeats(int maxMessages, boolean dump) throws Exception
    {
        setDescription("Establish a connection, logon with heartbeat=0, DON'T show display message, show summary every 1000 messages");

        logon(new testSessionSettings() {public int getHeartBeatInterval() {return 0;}});

        fixPacket = fixSocketAdapter.read(); dumpFixPacket();

        NetworkConnectionInstrumentor networkConnectionInstrumentor = null;

        try
        {
            networkConnectionInstrumentor = FoundationFramework.getInstance().getInstrumentationService().getNetworkConnectionInstrumentorFactory().create(null, null);
        }
        catch (InstrumentorAlreadyCreatedException ex)
        {

        }

        FixSessionInstrumentation fixSessionInstrumentation = new FixSessionInstrumentation(networkConnectionInstrumentor);

        fixSessionInstrumentation.start();

        for (int i = 1; ; i++)
        {
            fixPacket = fixSocketAdapter.read();
            if (dump)
            {
                dumpFixPacket();
            }

            if (i == maxMessages)
            {
                break;
            }

            if ((i % 1000) == 0)
            {
                fixSessionInstrumentation.stop();
                //fixSessionInstrumentation.dumpToScreen();
            }

            if (fixPacket.isMessageTimedOut())
            {
                break;
            }

            if (fixPacket.isMessageDisconnected())
            {
                break;
            }

            fixSessionInstrumentation.incValidNetworkPacketsReceived();
        }

        fixSessionInstrumentation.stop();
        //fixSessionInstrumentation.dumpToScreen();

        close();
    }

    public static void main(String[] args)
    {
        if (args.length == 0)
        {
            Usage();
            return;
        }

        System.setOut(new PrintTimeStampedStream(System.out, true));

        testSessionLevelMatrix testSessionLevelMatrix = new testSessionLevelMatrix();

        for (int i = 0; i < args.length; i++)
        {
            try
            {
                if (args[i].equals("-junit"))
                {
                    i++;

                    if (i == args.length)
                    {
                        Usage();
                    }

                         if (args[i].equals("0"))  testSessionLevelMatrix.testLogonWithNoHeartbeats(0, false);
                    else if (args[i].equals("1"))  testSessionLevelMatrix.testLogonWithNoHeartbeats(25, false);
                    else if (args[i].equals("2"))  testSessionLevelMatrix.testLogonWithNoHeartbeats(0, true);
                    else if (args[i].equals("3"))  testSessionLevelMatrix.testLogonSuccess();
                    else if (args[i].equals("4"))  testSessionLevelMatrix.testLogonTwice(); //TODO -- 1. verify if this is a business level reject, 2. allow Logon message if it has the ResetFlag
                    else if (args[i].equals("5"))  testSessionLevelMatrix.testLogonNoMessageSent();
                    else if (args[i].equals("6"))  testSessionLevelMatrix.testLogonAndCheckForTestRequest();
                    else if (args[i].equals("7"))  testSessionLevelMatrix.testLogonGarbageMessageSent();
                    else if (args[i].equals("8"))  testSessionLevelMatrix.testLogonAndImmediatelyDropConnection();
                    else if (args[i].equals("9"))  testSessionLevelMatrix.testLogonIncorrectSenderCompID();
                    else if (args[i].equals("10")) testSessionLevelMatrix.testLogonIncorrectTargetCompID();
                    else if (args[i].equals("11")) testSessionLevelMatrix.testLogonIncorrectSendingTime();
                    else if (args[i].equals("12")) testSessionLevelMatrix.testLogonHighSequenceNumber();
                    else if (args[i].equals("13")) testSessionLevelMatrix.testLogonLowSequenceNumber();
                    else if (args[i].equals("14")) testSessionLevelMatrix.testLogonWithPossDupFlagEqualYes();
                    else if (args[i].equals("15")) testSessionLevelMatrix.testLogonAndResetSeqNum();
                    else if (args[i].equals("16")) testSessionLevelMatrix.testLogoutSuccess();
                    else if (args[i].equals("17")) testSessionLevelMatrix.testGarbageMessageSent();
                    else if (args[i].equals("18")) testSessionLevelMatrix.testLogoutInMiddleOfResendRequest();
                    else if (args[i].equals("19")) testSessionLevelMatrix.testResendRequest();
                    else Usage();
                }
                else if (args[i].equals("-sender"))
                {
                    i++;
                    testSessionLevelMatrix.senderCompID = args[i];
                }
                else if (args[i].equals("-target"))
                {
                    i++;
                    testSessionLevelMatrix.targetCompID = args[i];
                }
                else if (args[i].equals("-user"))
                {
                    i++;
                    testSessionLevelMatrix.userID = args[i];
                }
                else if (args[i].equals("-host"))
                {
                    i++;
                    testSessionLevelMatrix.host = args[i];
                }
                else if (args[i].equals("-port"))
                {
                    i++;
                    testSessionLevelMatrix.port = Integer.parseInt(args[i]);
                }
                else
                {
                    Usage();
                }
            }
            catch (Exception ex)
            {
                System.out.println("Exception: " + ExceptionHelper.getStackTrace(ex));
            }
        }
    }

    public static void Usage()
    {
        System.out.println("Usage: [-junit #] [-sender COMPID] [-target COMPID] [-user USER:PWD] [-host 'host'] [-port #]");

        System.out.println("TEST");
        System.out.println("0  testLogonWithNoHeartbeats()");
        System.out.println("1  testLogonWithNoHeartbeats(25 messages only)");
        System.out.println("2  testLogonWithNoHeartbeats(and display messages)");
        System.out.println("3  testLogonSuccess");
        System.out.println("4  testLogonTwice");
        System.out.println("5  testLogonNoMessageSent");
        System.out.println("6  testLogonAndCheckForTestRequest");
        System.out.println("7  testLogonGarbageMessageSent");
        System.out.println("8  testLogonAndImmediatelyDropConnection");
        System.out.println("9  testLogonIncorrectSenderCompID");
        System.out.println("10 testLogonIncorrectTargetCompID");
        System.out.println("11 testLogonIncorrectSendingTime");
        System.out.println("12 testLogonHighSequenceNumber");
        System.out.println("13 testLogonLowSequenceNumber");
        System.out.println("14 testLogonWithPossDupFlagEqualYes");
        System.out.println("15 testLogonAndResetSeqNum");
        System.out.println("16 testLogoutSuccess");
        System.out.println("17 testGarbageMessageSent");
        System.out.println("18 testLogoutInMiddleOfResendRequest");
        System.out.println("19 testResendRequest");

        System.exit(1);
    }
}