package com.cboe.cfix.fix.fix42.session;

import com.cboe.cfix.interfaces.*;
import com.cboe.cfix.fix.net.FixSocketAdapter;
import com.cboe.cfix.fix.fix42.generated.messages.*;
import com.cboe.cfix.fix.fix42.generated.fields.*;
import com.cboe.cfix.fix.session.FixSessionInstrumentation;
import com.cboe.cfix.fix.session.FixConnectionInformationHolder;
import com.cboe.cfix.fix.session.FixSessionManager;
import com.cboe.cfix.fix.parser.FixPacketParser;
import com.cboe.cfix.fix.util.FixMessageBuilder;
import com.cboe.cfix.fix.util.FixException;
import com.cboe.cfix.fix.util.FixMarketDataRejectStruct;
import com.cboe.cfix.util.*;
import com.cboe.interfaces.cfix.*;
import com.cboe.client.util.threadpool.AdaptiveThreadPool;
import com.cboe.client.util.*;
import com.cboe.client.util.collections.ObjectObjectComparisonPolicy;
import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiConstants.LoginSessionTypes;
import com.cboe.idl.cmiConstants.LoginSessionModes;
import com.cboe.idl.cmi.VersionPOA;
import com.cboe.idl.cmiAdmin.MessageStruct;
import com.cboe.instrumentationService.instrumentors.NetworkConnectionInstrumentor;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.exceptions.*;
import com.cboe.util.ExceptionBuilder;

import java.util.*;
import java.net.Socket;

/**
 * FixMDXSession.java
 * FixMDXSession replaces FixSession in the MDX enabled CFIX
 * @author Dmitry Volpyansky / Vivek Beniwal
 *
 */

public class FixMDXSession implements FixSessionIF, CfixUserSessionAdminConsumer, CfixMarketDataConsumer, Comparable
{
    protected long                              sessionCreationTime;
    protected FixMessageFactoryIF               fixMessageFactory;
    protected FixSessionManagerIF               fixSessionManager;
    protected FixSocketAdapter                  fixSocketAdapter;
    protected FixPacketIF                       fixJustReceivedPacket;
    protected FixMessage                        fixJustReceivedMessage;
    protected FixMDXSession.FixMDXSessionState  fixSessionState;
    protected String                            sessionName;
    protected int                               port;
    protected Properties                        sessionProperties;
    protected String                            propertyPrefix;
    protected FixSessionInformation             fixSessionInformation;
    protected FixSessionInstrumentation         fixSessionInstrumentation;
    protected PackedIntArrayIF                  foundErrors                  = new GrowableIntArray();
    protected FixMDXSessionWriter               fixSessionWriter;
    protected List                              fixSessionListenerList       = new ArrayList();
    protected int                               disconnectReason             = FixSessionListenerIF.SESSION_CONNECTED;
    protected String                            disconnectReasonText;
    protected AdaptiveThreadPool                threadPool;
    protected CfixMDXMarketDataQueryIF          cfixMDXMarketDataQuery;
    protected Map                               marketDataFutureExecutionMap = Collections.synchronizedMap(new HashMap());
    protected CfixSessionManager                cfixSessionManager;
    protected CfixUserAccess                    cfixUserAccess;
    protected UserLogonStruct                   userLogonStruct;
    protected int                               debugFlags                   = FixSessionDebugIF.DEBUG_OFF;
    protected Latch                             sessionWriterTerminatedLatch;
    protected Latch                             sessionTerminatedLatch       = new Latch();
    protected boolean                           markedAsUnsuccessful;
    protected OverlayPolicyFactory              overlayPolicyFactory         = new OverlayPolicyFactory();
    protected boolean                           inCleanup;
    protected PropertiesHelper                  propertiesHelper;
    protected String                            targetCompID;
    protected String                            senderCompID;
    protected char                              udfSupportIndicator = FixCboeUDFSupportIndicatorField.NoUDFSupport;

    protected final FixMDXSession.FixMDXSessionStatePreLogon         fixSessionStatePreLogon         = new FixMDXSession.FixMDXSessionStatePreLogon();
    protected final FixMDXSession.FixMDXSessionStateNormalProcessing fixSessionStateNormalProcessing = new FixMDXSession.FixMDXSessionStateNormalProcessing();
    protected final FixMDXSession.FixMDXSessionStateDisconnect       fixSessionStateDisconnect       = new FixMDXSession.FixMDXSessionStateDisconnect();

    private static final boolean IGNORE_ALL_SEQUENCE_NUMBER_PROBLEMS_ON_LOGON = true; //KEEP IT AS TRUE FOR MarketData CFIX

    protected static final String NO_BODY   = "";
    protected static final String PRIMARY   = "PRI";
    protected static final String SECONDARY = "SEC";

    private int maxQueueSize;

    public FixMDXSession()
    {
        sessionCreationTime = System.currentTimeMillis();

        NetworkConnectionInstrumentor networkConnectionInstrumentor = null;

        try
        {
            networkConnectionInstrumentor = FoundationFramework.getInstance().getInstrumentationService().getNetworkConnectionInstrumentorFactory().create(null, null);
        }
        catch (InstrumentorAlreadyCreatedException ex)
        {

        }

        fixSessionInstrumentation = new FixSessionInstrumentation(networkConnectionInstrumentor);
    }

    public String toString()
    {
        return getSessionName();
    }

    public void setTargetCompID(String targetCompID)
    {
        this.targetCompID = targetCompID;
    }

    public String getTargetCompID()
    {
        return targetCompID;
    }

    public void setSenderCompID(String senderCompID)
    {
        this.senderCompID = senderCompID;
    }

    public String getSenderCompID()
    {
        return senderCompID;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public int getPort()
    {
        return port;
    }

    public PropertiesHelper getPropertiesHelper()
    {
        return propertiesHelper;
    }

    public FixSessionIF initialize(FixSessionManagerIF fixSessionManager, String propertyPrefix, Properties sessionProperties)
    {
        if(Log.isDebugOn())
        {
            Log.debug("Initialized FixMDXSession. CFIX is MDX Enabled.");
        }

        this.propertyPrefix    = propertyPrefix.trim();

        this.sessionProperties = sessionProperties;

        this.fixSessionManager = fixSessionManager;

        this.fixMessageFactory = fixSessionManager.getFixMessageFactory();

        propertiesHelper = new PropertiesHelper(sessionProperties, propertyPrefix, PropertiesHelper.STRIP_ONE_FRONT);

        setDebugFlags(DebugFlagBuilder.buildFixSessionDebugFlags(propertiesHelper.getProperty("cfix.fixSession.debugFlags", FixSessionDebugIF.strDEBUG_OFF)));

        return this;
    }

    public Properties getSessionProperties()
    {
        return sessionProperties;
    }

    public FixSessionInformationIF getFixSessionInformation()
    {
        return fixSessionInformation;
    }

    public void setFixSessionInformation(FixSessionInformationIF fixSessionInformation)
    {
        this.fixSessionInformation = (FixSessionInformation) fixSessionInformation; // Yes, for now it MUST be FixSessionInformation from com.cboe.cfix.fix.fix42.session
    }

    public void setThreadPool(AdaptiveThreadPool threadPool)
    {
        this.threadPool = threadPool;
    }

    public AdaptiveThreadPool getThreadPool()
    {
        return threadPool;
    }

    public OverlayPolicyFactory getOverlayPolicyFactory()
    {
        return overlayPolicyFactory;
    }

    public CfixSessionManager getCfixSessionManager()
    {
        return cfixSessionManager;
    }

    public FixSessionWriterIF getFixSessionWriter()
    {
        return fixSessionWriter;
    }

    public String getSessionName()
    {
        return sessionName;
    }

    public FixSessionInstrumentationIF getFixSessionInstrumentation()
    {
        return fixSessionInstrumentation;
    }

    public boolean enqueueOutboundFixMessage(Object object)
    {
        if (!terminated() && fixSessionWriter != null)
        {
            fixSessionWriter.fixEventChannel.enqueue(object);
            return true;
        }

        return false;
    }

    public boolean enqueueOutboundUniqueFixMessage(Object object)
    {
        if (!terminated() && fixSessionWriter != null)
        {
            fixSessionWriter.fixEventChannel.enqueue(object, ObjectObjectComparisonPolicy.RejectEqualsObjectComparisonPolicy);
            return true;
        }

        return false;
    }

    public int setDebugFlags(int debugFlags)
    {
        int oldDebugFlags = this.debugFlags;

        this.debugFlags = debugFlags;

        if (fixSessionInformation != null)
        {
            fixSessionInformation.setDebugFlags(debugFlags);
        }

        if (fixSessionWriter != null)
        {
            fixSessionWriter.setDebugFlags(debugFlags);
        }

        if (fixSocketAdapter != null)
        {
            fixSocketAdapter.setDebugFlags(debugFlags);
        }

        return oldDebugFlags;
    }

    public int getDebugFlags()
    {
        return debugFlags;
    }

    public int getQueueDepth()
    {
        return fixSessionWriter.fixEventChannel.size();
    }

    public FixMessageFactoryIF getFixMessageFactory()
    {
        return fixMessageFactory;
    }

    public void resetSocket(Socket socket)
    {
        fixSessionManager.sessionReset(this);

        disconnectReason = FixSessionListenerIF.SESSION_CONNECTED;

        setDebugFlags(DebugFlagBuilder.buildFixSessionDebugFlags(propertiesHelper.getProperty("cfix.fixSession.debugFlags", FixSessionDebugIF.strDEBUG_OFF)));

        if (fixSocketAdapter == null)
        {
            fixSocketAdapter = new FixSocketAdapter();
            fixSocketAdapter.setFixPacketParser(new FixPacketParser());
        }

        fixSocketAdapter.setDebugFlags(debugFlags);

        fixSocketAdapter.resetSocket(socket);
    }

    public boolean terminated()
    {
        return fixSessionWriter.isTerminating();
    }

    public void terminate(boolean immediate)
    {
        fixSessionWriter.terminate(immediate);
    }

    public void externallyGenerateFixHeartBeatMessage()
    {
        fixSessionWriter.fixEventChannel.enqueueHighPriority(FixSessionWriterCommand.createSend(null, FixHeartBeatMessage.MsgTypeAsChars));
    }

    public void externallyGenerateFixTestRequestMessage()
    {
        FastCharacterWriter fastCharacterWriter = new FastCharacterWriter();

        FixMessageBuilder.writeFixString(fastCharacterWriter, FixTestReqIDField.TagIDAsChars, "CBOE_HELP_DESK_TEST_REQUEST");

        fixSessionWriter.fixEventChannel.enqueueHighPriority(FixSessionWriterCommand.createSend(fastCharacterWriter, FixTestRequestMessage.MsgTypeAsChars));
    }

    protected FixSessionWriterCommand buildFixSessionWriterCommandForRejectMessage(String bytesRejectReason, String text)
    {
        return buildFixSessionWriterCommandForRejectMessage(bytesRejectReason, fixJustReceivedMessage.getMsgTypeAsChars(), text);
    }

    protected FixSessionWriterCommand buildFixSessionWriterCommandForRejectMessage(String bytesRejectReason, char[] msgType, String text)
    {
        FastCharacterWriter fastCharacterWriter = new FastCharacterWriter();

        FixMessageBuilder.writeFixString(fastCharacterWriter, FixRefSeqNumField.TagIDAsChars, fixSessionInformation.getNextReceiveMsgSeqNum());
        if (msgType != null)
        {
            FixMessageBuilder.writeFixString(fastCharacterWriter, FixRefMsgTypeField.TagIDAsChars, msgType);
        }
        FixMessageBuilder.writeFixString(fastCharacterWriter, FixSessionRejectReasonField.TagIDAsChars, bytesRejectReason);
        FixMessageBuilder.writeFixString(fastCharacterWriter, FixTextField.TagIDAsChars,                text);

        return FixSessionWriterCommand.createSend(fastCharacterWriter, FixRejectMessage.MsgTypeAsChars);
    }

    protected FixSessionWriterCommand buildFixSessionWriterCommandForEmailMessage(String sender, int messageId, String subject, String text, int originalMessageId, DateTimeStruct dateTimeStruct)
    {
        int allowedLines = propertiesHelper.getPropertyInt("cfix.fixSession.email.lines", "1");
        if (allowedLines == 0)
        {
            return null;
        }

        FastCharacterWriter fastCharacterWriter = new FastCharacterWriter();

        if (sender != null)
        {
            FixMessageBuilder.writeFixString(fastCharacterWriter, FixSenderSubIDField.TagIDAsChars, sender);
        }

        FixMessageBuilder.writeFixString(fastCharacterWriter, FixEmailThreadIDField.TagIDAsChars, messageId);

        if (originalMessageId == 0)
        {
            fastCharacterWriter.write(FixEmailTypeField.taggedchars_New);
        }
        else
        {
            fastCharacterWriter.write(FixEmailTypeField.taggedchars_AdminReply);
        }

        if (dateTimeStruct == null)
        {
            FixMessageBuilder.writeFixString(fastCharacterWriter, FixOrigTimeField.TagIDAsChars, DateHelper.stringizeDateInFixUTCTimeStampFormat());
        }
        else
        {
            fastCharacterWriter.write(FixOrigTimeField.TagIDAsChars);
            fastCharacterWriter.write(FixFieldIF.EQUALSchar);
            DateHelper.appendDateInFixUTCTimeStampFormat(fastCharacterWriter, dateTimeStruct.date, dateTimeStruct.time);
            fastCharacterWriter.write(FixFieldIF.SOHchar);
        }

        FixMessageBuilder.writeFixString(fastCharacterWriter, FixSubjectField.TagIDAsChars, subject == null ? "" : subject);

        int lines = 1;

        if (text.length() == 0)
        {
            FixMessageBuilder.writeFixString(fastCharacterWriter, FixTextField.TagIDAsChars, ' ');
        }
        else
        {
            char[] chars = text.toCharArray();
            char ch;
            int j = 0;

            int len = chars.length;

            for (int i = len - 1; i >= 0; i--)
            {
                ch = chars[i];
                if (ch == '\r' || ch == '\n')
                {
                    len--;
                }
                else
                {
                    break;
                }
            }

            FastCharacterWriter tempFastCharacterWriter = new FastCharacterWriter(chars.length + 64);

            tempFastCharacterWriter.write(FixTextField.TagIDAsChars);
            tempFastCharacterWriter.write(FixFieldIF.EQUALSchar);

            for (int i = 0; i < len; i++)
            {
                ch = chars[i];

                if (ch == '\r' || ch == '\n')
                {
                    if (allowedLines > 1)
                    {
                        if (j != 0)
                        {
                            tempFastCharacterWriter.write(FixFieldIF.SOHchar);
                            tempFastCharacterWriter.write(FixTextField.TagIDAsChars);
                            tempFastCharacterWriter.write(FixFieldIF.EQUALSchar);
                            lines++;
                            j = 0;
                        }

                        continue;
                    }
                    else
                    {
                        ch = ' ';
                    }

                }
                else if (Character.isISOControl(ch))
                {
                    continue;
                }

                j++;
                tempFastCharacterWriter.write(ch);
            }

            tempFastCharacterWriter.write(FixFieldIF.SOHchar);

            fastCharacterWriter.write(tempFastCharacterWriter);
        }

        FixMessageBuilder.writeFixString(fastCharacterWriter, FixLinesOfTextField.TagIDAsChars, lines);

        return FixSessionWriterCommand.createSend(fastCharacterWriter, FixEmailMessage.MsgTypeAsChars);
    }

    protected FixSessionWriterCommand buildFixSessionWriterCommandForBusinessMessageRejectMessage(String bytesBusinessMessageRejectReason, String text)
    {
        FastCharacterWriter fastCharacterWriter = new FastCharacterWriter();

        FixMessageBuilder.writeFixString(fastCharacterWriter, FixRefSeqNumField.TagIDAsChars,            fixSessionInformation.getNextReceiveMsgSeqNum());
        FixMessageBuilder.writeFixString(fastCharacterWriter, FixRefMsgTypeField.TagIDAsChars,           fixJustReceivedMessage.getMsgTypeAsChars());
        FixMessageBuilder.writeFixString(fastCharacterWriter, FixBusinessRejectReasonField.TagIDAsChars, bytesBusinessMessageRejectReason);
        FixMessageBuilder.writeFixString(fastCharacterWriter, FixTextField.TagIDAsChars,                 text);

        return FixSessionWriterCommand.createSend(fastCharacterWriter, FixBusinessMessageRejectMessage.MsgTypeAsChars);
    }

    protected FixSessionWriterCommand buildFixSessionWriterCommandForLogonResponseMessage(boolean shouldResetSeqNumFlag)
    {
        FastCharacterWriter fastCharacterWriter = new FastCharacterWriter();

        FixMessageBuilder.writeFixString(fastCharacterWriter, FixEncryptMethodField.TagIDAsChars,   FixEncryptMethodField.flyweightNone().getValue());
        FixMessageBuilder.writeFixString(fastCharacterWriter, FixHeartBtIntField.TagIDAsChars,      fixSessionInformation.getHeartBeatInterval());
        if (shouldResetSeqNumFlag)
        {
            FixMessageBuilder.writeFixString(fastCharacterWriter, FixResetSeqNumFlagField.TagIDAsChars, shouldResetSeqNumFlag);
        }

        return FixSessionWriterCommand.createSend(fastCharacterWriter, FixLogonMessage.MsgTypeAsChars);
    }

    protected FixSessionWriterCommand buildFixSessionWriterCommandForLogonRequestMessage(boolean shouldResetSeqNumFlag)
    {
        FastCharacterWriter fastCharacterWriter = new FastCharacterWriter();

        FixMessageBuilder.writeFixString(fastCharacterWriter, FixEncryptMethodField.TagIDAsChars,   FixEncryptMethodField.flyweightNone().getValue());
        FixMessageBuilder.writeFixString(fastCharacterWriter, FixHeartBtIntField.TagIDAsChars,      fixSessionInformation.getHeartBeatInterval());
        if (shouldResetSeqNumFlag)
        {
            FixMessageBuilder.writeFixString(fastCharacterWriter, FixResetSeqNumFlagField.TagIDAsChars, shouldResetSeqNumFlag);
        }

        return FixSessionWriterCommand.createSend(fastCharacterWriter, FixLogonMessage.MsgTypeAsChars);
    }

    protected FixSessionWriterCommand buildFixSessionWriterCommandForLogoutMessage(String text)
    {
        FastCharacterWriter fastCharacterWriter = new FastCharacterWriter();

        FixMessageBuilder.writeFixString(fastCharacterWriter, FixTextField.TagIDAsChars, text);

        return FixSessionWriterCommand.createSend(fastCharacterWriter, FixLogoutMessage.MsgTypeAsChars);
    }

    protected FixSessionWriterCommand buildFixSessionWriterCommandForHeartBeatMessage()
    {
        return FixSessionWriterCommand.createSend(null, FixHeartBeatMessage.MsgTypeAsChars);
    }

    protected FixSessionWriterCommand buildFixSessionWriterCommandForTestRequestResponseMessage(String text)
    {
        FastCharacterWriter fastCharacterWriter = new FastCharacterWriter();

        FixMessageBuilder.writeFixString(fastCharacterWriter, FixTestReqIDField.TagIDAsChars, text);

        return FixSessionWriterCommand.createSend(fastCharacterWriter, FixHeartBeatMessage.MsgTypeAsChars);
    }

    protected FixSessionWriterCommand buildFixSessionWriterCommandForTestRequestMessage()
    {
        String testID = "(" + (fixSessionInformation.getOutstandingTestRequestList().size() + 1) + "/" + fixSessionInformation.maxOutstandingTestRequests + ") " + DateHelper.stringizeDateInFixUTCTimeStampFormat();

        fixSessionInformation.addOutstandingTestRequest(testID);

        FastCharacterWriter fastCharacterWriter = new FastCharacterWriter();

        FixMessageBuilder.writeFixString(fastCharacterWriter, FixTestReqIDField.TagIDAsChars, testID);

        return FixSessionWriterCommand.createSend(fastCharacterWriter, FixTestRequestMessage.MsgTypeAsChars);
    }

    protected FixSessionWriterCommand buildFixSessionWriterCommandForResendRequestMessage(int startingNumber, int endingNumber)
    {
        FastCharacterWriter fastCharacterWriter = new FastCharacterWriter();

        FixMessageBuilder.writeFixString(fastCharacterWriter, FixBeginSeqNoField.TagIDAsChars, startingNumber);
        FixMessageBuilder.writeFixString(fastCharacterWriter, FixEndSeqNoField.TagIDAsChars,   endingNumber);

        return FixSessionWriterCommand.createSend(fastCharacterWriter, FixResendRequestMessage.MsgTypeAsChars);
    }

    protected FixSessionWriterCommand buildFixSessionWriterCommandForSequenceResetMessage(int oldSeqNum, int newSeqNum, boolean gapFillFlag)
    {
        FastCharacterWriter fastCharacterWriter = new FastCharacterWriter();

        FixMessageBuilder.writeFixString(fastCharacterWriter, FixNewSeqNoField.TagIDAsChars,    newSeqNum);
        FixMessageBuilder.writeFixString(fastCharacterWriter, FixGapFillFlagField.TagIDAsChars, gapFillFlag);

        return FixSessionWriterCommand.createSend(fastCharacterWriter, FixSequenceResetMessage.MsgTypeAsChars, oldSeqNum);
    }

    protected FixSessionWriterCommand buildFixSessionWriterCommandForResendMessage(String msgBody)
    {
        FixMessageBuilder fixMessageBuilder = new FixMessageBuilder();

        fixMessageBuilder.append(msgBody);

        return FixSessionWriterCommand.createResend(fixMessageBuilder.getFastCharacterWriter());
    }

    protected boolean checkIfInvalidCompIDs()
    {
        if (!fixSessionInformation.isValidSenderCompID(fixJustReceivedMessage.getSenderCompID()))
        {
            disconnectReasonText = "Wrong SenderCompID(" + fixJustReceivedMessage.getSenderCompID() + ")";
            fixSessionWriter.fixEventChannel.enqueueHighPriority(buildFixSessionWriterCommandForRejectMessage(FixSessionRejectReasonField.string_CompIdProblem, disconnectReasonText));
            fixSessionState = fixSessionStateDisconnect;
            return true;
        }

        if (!fixSessionInformation.isValidTargetCompID(fixJustReceivedMessage.getTargetCompID()))
        {
            disconnectReasonText = "Wrong TargetCompID(" + fixJustReceivedMessage.getTargetCompID() + ")";
            fixSessionWriter.fixEventChannel.enqueueHighPriority(buildFixSessionWriterCommandForRejectMessage(FixSessionRejectReasonField.string_CompIdProblem, disconnectReasonText));
            fixSessionState = fixSessionStateDisconnect;
            return true;
        }

        return false;
    }

    protected boolean checkIfInvalidSendingTime()
    {
        int differSeconds = DateHelper.convertMillisecondsToSeconds(fixJustReceivedMessage.getSendingTime().getTime() - System.currentTimeMillis());

        if (differSeconds < 0)
        {
            differSeconds = -differSeconds;
        }

        if (differSeconds > fixSessionInformation.maxSendingTimeDifferenceInSeconds)
        {
            fixSessionWriter.fixEventChannel.enqueueHighPriority(buildFixSessionWriterCommandForRejectMessage(FixSessionRejectReasonField.string_SendingTimeAccuracyProblem, "SendingTime differs from Server Time by (" + differSeconds + ") seconds; maximum difference allowed is (" + fixSessionInformation.maxSendingTimeDifferenceInSeconds + ") seconds."));
            disconnectReasonText = "Sending Time Problems";
            fixSessionState = fixSessionStateDisconnect;

            return true;
        }

        return false;
    }

    public void renameSession(String name, boolean isNameSession)
    {
        if (isNameSession)
        {
            if (name != null && name.endsWith("."))
            {
                name = name.substring(0, name.length() - 1);
            }
        }

        Thread.currentThread().setName("[FixSessionReader<" + name + ">]");

        threadPool.setName("adaptiveThreadPool(" + name + ")");

        setDebugFlags(DebugFlagBuilder.buildFixSessionDebugFlags(propertiesHelper.getProperty("cfix.fixSession.debugFlags", FixSessionDebugIF.strDEBUG_OFF)));

        overlayPolicyFactory.setOverlayPolicy(propertiesHelper.getProperty("cfix.fixSession.overlayPolicy"));

        maxQueueSize = propertiesHelper.getPropertyInt("cfix.fixSession.maxQueueSize", "5000");

        this.sessionName = name;
    }

    public void prepareToRun()
    {
        fixSessionState = fixSessionStatePreLogon;

        if (threadPool == null)
        {
            threadPool = AdaptiveThreadPool.createThreadPool(propertyPrefix, sessionProperties);
        }

        if (fixSessionWriter != null)
        {
            Log.alarm(Thread.currentThread().getName() + " CRITICAL ERROR - FixSessionWriter is ALREADY RUNNING");
            return;
        }

        fixSessionWriter = new FixMDXSessionWriter(this);

        fixSessionWriter.setFixSessionInstrumentation(fixSessionInstrumentation);
        fixSessionWriter.setDebugFlags(debugFlags);

        renameSession(fixSocketAdapter.getSocket().toString(), false);

        notifySessionStarting();
    }

    public void blockUntilSessionTerminated()
    {
        sessionTerminatedLatch.acquire();
    }

    public void addFixSessionListener(FixSessionListenerIF fixSessionListener)
    {
        if (!fixSessionListenerList.contains(fixSessionListener))
        {
            fixSessionListenerList.add(fixSessionListener);
        }
    }

    public void removeFixSessionListener(FixSessionListenerIF fixSessionListener)
    {
        fixSessionListenerList.remove(fixSessionListener);
    }

    protected void notifySessionStarting()
    {
        for (Iterator iterator = fixSessionListenerList.iterator(); iterator.hasNext(); )
        {
            try
            {
                ((FixSessionListenerIF) iterator.next()).sessionStarting(this);
            }
            catch (Exception ex)
            {

            }
        }
    }

    protected void notifySessionEnded()
    {
        for (Iterator iterator = fixSessionListenerList.iterator(); iterator.hasNext(); )
        {
            try
            {
                ((FixSessionListenerIF) iterator.next()).sessionEnded(this, disconnectReason);
            }
            catch (Exception ex)
            {

            }
        }
    }

    protected void notifySessionTargetLoggedIn()
    {
        for (Iterator iterator = fixSessionListenerList.iterator(); iterator.hasNext(); )
        {
            try
            {
                ((FixSessionListenerIF) iterator.next()).sessionTargetLoggedIn(this);
            }
            catch (Exception ex)
            {

            }
        }
    }

    protected void notifySessionTerminating()
    {
        for (Iterator iterator = fixSessionListenerList.iterator(); iterator.hasNext(); )
        {
            try
            {
                ((FixSessionListenerIF) iterator.next()).sessionTerminating(this, disconnectReason);
            }
            catch (Exception ex)
            {

            }
        }
    }

    protected void logonToCas(FixLogonMessage fixLogonMessage, short userLogonType) throws DataValidationException
    {
        if(Log.isDebugOn())
        {
            Log.debug("FixMDXSession : Logging onto CAS ");
        }

        try
        {
            cfixUserAccess = ((CfixUserAccessHome) HomeFactory.getInstance().findHome(CfixUserAccessHome.HOME_NAME)).find();
        }
        catch (Exception ex)
        {
            throw ExceptionBuilder.dataValidationException("Internal Error: Can't validate user information", 0);
        }

        Log.information(Thread.currentThread().getName() + " BEGIN LOGIN(" + userLogonStruct.userId + ")");

        try
        {
            cfixSessionManager = cfixUserAccess.logon(userLogonStruct, userLogonType, this, false);
        }
        catch (SystemException ex)          {throw ExceptionBuilder.dataValidationException("ABORT LOGIN(" + userLogonStruct.userId + ") " + ex.details.message, 0);}
        catch (CommunicationException ex)   {throw ExceptionBuilder.dataValidationException("ABORT LOGIN(" + userLogonStruct.userId + ") " + ex.details.message, 0);}
        catch (AuthorizationException ex)   {throw ExceptionBuilder.dataValidationException("ABORT LOGIN(" + userLogonStruct.userId + ") " + ex.details.message, 0);}
        catch (AuthenticationException ex)  {throw ExceptionBuilder.dataValidationException("ABORT LOGIN(" + userLogonStruct.userId + ") " + ex.details.message, 0);}
        catch (DataValidationException ex)  {throw ExceptionBuilder.dataValidationException("ABORT LOGIN(" + userLogonStruct.userId + ") " + ex.details.message, 0);}
        catch (Exception ex)
        {
            Log.exception(ex);
            throw ExceptionBuilder.dataValidationException("ABORT LOGIN(" + userLogonStruct.userId + ") " + ex.getClass().getName() + ": " + ex.getMessage(),    0);
        }
        catch (Throwable ex)
        {
            //Exception exception = new Exception(ex);
            //exception.setStackTrace(ex.getStackTrace());
            //Log.exception(exception);
            throw ExceptionBuilder.dataValidationException("ABORT LOGIN(" + userLogonStruct.userId + ") " + ex.getClass().getName() + ": " + ex.getMessage(),    0);
        }

        if (cfixSessionManager == null)     {throw ExceptionBuilder.dataValidationException("ABORT LOGIN(" + userLogonStruct.userId + ") Can't Get CfixSessionManager", 0);}

        Log.information(Thread.currentThread().getName() + " COMPLETE LOGIN(" + userLogonStruct.userId + ")");
    }

    public void run()
    {
        try
        {
            prepareToRun();
        }
        catch (Exception ex)
        {
            Log.exception(ex);
            if (disconnectReasonText == null)
            {
                disconnectReasonText = "Session Is Misconfigured";
            }
            terminate(true);
        }

        while (!terminated() && fixSessionState != null)
        {
            try
            {
                if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.SESSION_SHOW_STATE_TRANSITIONS)) {Log.information(Thread.currentThread().getName() + " Entering: " + fixSessionState);}

                fixJustReceivedMessage = null;

                foundErrors.clear();

                fixSessionState.processMessage();
            }
            catch (Exception ex)
            {
                try
                {
                    Log.exception(ex);
                }
                catch (Throwable th)
                {
                    System.out.println("CAUGHT EXCEPTION FROM Log.exception: " + ex);
                }

                if (fixSessionState == fixSessionStatePreLogon ||
                    fixSessionState == fixSessionStateDisconnect)
                {
                    break;
                }
            }
        }

        try
        {
            terminate(true);

            if (sessionWriterTerminatedLatch != null)
            {
                sessionWriterTerminatedLatch.acquire();
            }
        }
        catch (Exception ex)
        {
            try
            {
                Log.exception(ex);
            }
            catch (Throwable th)
            {
                System.out.println("CAUGHT EXCEPTION FROM Log.exception: " + ex);
            }
        }

        notifySessionEnded();

        sessionTerminatedLatch.release();

        Log.information(Thread.currentThread().getName() + " Session Terminated For Reason(" + disconnectReasonText + ")");

        if (!markedAsUnsuccessful)
        {
            FixConnectionInformationHolder fixConnectionInformationHolder = (FixConnectionInformationHolder) fixSessionInformation.connectionInformation.get(fixSessionInformation.connectionInformation.size() - 1);
            fixConnectionInformationHolder.disconnectTime = System.currentTimeMillis();
            fixConnectionInformationHolder.disconnectReason = disconnectReasonText;
        }
    }

    protected abstract class FixMDXSessionState
    {
        public abstract void processMessage() throws Exception;

        public String toString()
        {
            return "{" + ClassHelper.getClassNameFinalPortion(getClass()) + "(" + getSenderCompID() + ")}";
        }

        public boolean receiveGoodFixMessage()
        {
            fixJustReceivedPacket = fixSocketAdapter.read();

            if (!fixSessionInstrumentation.isStarted())
            {
                fixSessionInstrumentation.start();
            }

            fixSessionInstrumentation.incTotalNetworkPacketsReceived();

            fixSessionInstrumentation.addBytesReceived(fixJustReceivedPacket.getReadLength());

            if (!fixJustReceivedPacket.isGoodFixMessage())
            {
                if (fixJustReceivedPacket.isMessageDisconnected())
                {
                    fixSessionState = fixSessionStateDisconnect;

                    if (disconnectReasonText == null)
                    {
                        disconnectReasonText = "Sender Disconnected";
                    }

                    if (disconnectReason == 0)
                    {
                        disconnectReason = FixSessionListenerIF.SESSION_DISCONNECTED_BY_TARGET;
                    }

                    if (Log.isDebugOn()) Log.debug(Thread.currentThread().getName() + " SENDER_DISCONNECTED [" + fixJustReceivedPacket.getArrayAsString() + "]");
                }
                else if (fixJustReceivedPacket.isMessageTimedOut())
                {
                    if (Log.isDebugOn()) Log.debug(Thread.currentThread().getName() + " TIMEDOUT [" + fixJustReceivedPacket.getArrayAsString() + "]");
                }
                else if (fixJustReceivedPacket.isBadFixMessage())
                {
                    fixSessionInstrumentation.incInvalidNetworkPacketsReceived();
                    if (Log.isDebugOn())
                    {
                        Log.debug(Thread.currentThread().getName() + " BADPACKET_BEGIN");
                        Log.debug(Thread.currentThread().getName() + " [" + fixJustReceivedPacket.getArrayAsString() + "]");
                        Log.debug(Thread.currentThread().getName() + " " + fixJustReceivedPacket.toString());
                        Log.debug(Thread.currentThread().getName() + " BADPACKET_END");
                    }
                }
                else if (fixJustReceivedPacket.isGarbageMessage())
                {
                    fixSessionInstrumentation.incGarbageNetworkPacketsReceived();
                    if (Log.isDebugOn()) Log.debug(Thread.currentThread().getName() + " GARBAGE [" + fixJustReceivedPacket.getArrayAsString() + "]: " + fixJustReceivedPacket);
                }

                return false;
            }

            fixSessionInstrumentation.incValidNetworkPacketsReceived();

            try
            {
                char msgType = fixJustReceivedPacket.charAt(fixJustReceivedPacket.getValueOffset(2)); //BUGBUG -- will not work for multi-char msgTypes

                if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.SESSION_SHOW_RAW_RECEIVED_MESSAGES) &&
                   (fixSessionInformation == null || !fixSessionInformation.isSuppressedRecvFixMsgType(FixSessionInformation.MsgTypesAsString[msgType])))
                {
                    Log.information(Thread.currentThread().getName() + " RCVD [" + fixJustReceivedPacket.getArrayAsString() + "]");
                }

                fixJustReceivedMessage = (FixMessage) fixMessageFactory.createFixMessageFromMsgType(msgType);

                foundErrors = fixJustReceivedMessage.build(fixJustReceivedPacket, foundErrors, FixMessageIF.VALIDATE_ONLY_USED_FIELDS | FixMessageIF.STOP_ON_FIRST_ERROR, debugFlags);
                if (!foundErrors.isEmpty())
                {
                    byte   error    = BitHelper.unpackHighByte(foundErrors.get(0));
                    int    position = BitHelper.unpackLowShortAsInt(foundErrors.get(0));
                    String s;

                    if (FixException.isPositionATag(error, position))
                    {
                        s = FixException.toString(error, position);
                    }
                    else
                    {
                        s = FixException.toString(error, position) + "{" + fixJustReceivedPacket.getTag(position) + "=" + new String(fixJustReceivedPacket.getArray(), fixJustReceivedPacket.getValueOffset(position), fixJustReceivedPacket.getValueLength(position)) + "}";
                    }

                    if (fixSessionState == fixSessionStatePreLogon)
                    {
                        disconnectReasonText = "During Logon: Invalid Sender Data During Build(" + s + ")";
                    }
                    else
                    {
                        disconnectReasonText = "Invalid Sender Data During Build(" + s + ")";
                    }
                    return false;
                }

                foundErrors.clear();

                foundErrors = fixJustReceivedMessage.validate(fixJustReceivedPacket, foundErrors, FixMessageIF.VALIDATE_ONLY_USED_FIELDS | FixMessageIF.STOP_ON_FIRST_ERROR, debugFlags);
                if (!foundErrors.isEmpty())
                {
                    byte   error    = BitHelper.unpackHighByte(foundErrors.get(0));
                    int    position = BitHelper.unpackLowShortAsInt(foundErrors.get(0));
                    String s;

                    if (FixException.isPositionATag(error, position))
                    {
                        s = FixException.toString(error, position);
                    }
                    else
                    {
                        s = FixException.toString(error, position) + "{" + fixJustReceivedPacket.getTag(position) + "=" + new String(fixJustReceivedPacket.getArray(), fixJustReceivedPacket.getValueOffset(position), fixJustReceivedPacket.getValueLength(position)) + "}";
                    }

                    if (fixSessionState == fixSessionStatePreLogon)
                    {
                        disconnectReasonText = "During Logon: Invalid Sender Data During Validate(" + s + ")";
                    }
                    else
                    {
                        disconnectReasonText = "Invalid Sender Data During Validate(" + s + ")";
                    }
                    return false;
                }
            }
            catch (Exception ex)
            {
                Log.exception(ex);

                return false;
            }
            finally
            {
                try
                {
                    if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.SESSION_DECODE_RECEIVED_MESSAGES)) {FixSessionDebugger.dumpFixMessage(Thread.currentThread().getName() + " FIXDECODE RCVD ", fixJustReceivedMessage, fixJustReceivedPacket, foundErrors);}
                }
                catch (Exception ex)
                {

                }
            }

            fixSessionInstrumentation.incRecvMsgType(fixJustReceivedMessage.getMsgType());

            return true; //TODO: need better return logic
        }
    }

    protected class FixMDXSessionStatePreLogon extends FixMDXSession.FixMDXSessionState
    {
        public void processMessage() throws Exception
        {
            boolean haveWeReset = false;

            FixConnectionInformationHolder fixConnectionInformationHolder = new FixConnectionInformationHolder();
            fixConnectionInformationHolder.connectTime    = System.currentTimeMillis();
            fixConnectionInformationHolder.connectAddress = fixSocketAdapter.getSocket().getInetAddress().getHostAddress() + ":" + fixSocketAdapter.getSocket().getPort();

            fixSocketAdapter.setTimeout(propertiesHelper.getPropertyInt("cfix.fixNetworkAcceptor.preLogonSocketTimeout", "15") * 1000);

            // get the next message from the socket -- if it has problems, then kick it upstairs,
            // and see what the Session wants to do with it
            // we don't want to consume a sequence number here
            if (!receiveGoodFixMessage())
            {
                fixSessionState = null;

                fixSessionInstrumentation.start();

                if (fixJustReceivedPacket.isMessageDisconnected())
                {
                    disconnectReasonText = "During Logon: Sender Disconnected";
                }
                else if (fixJustReceivedPacket.isMessageTimedOut())
                {
                    disconnectReasonText = "During Logon: Timed Out Waiting For Sender";
                }
                else if (fixJustReceivedPacket.isBadFixMessage())
                {
                    disconnectReasonText = "During Logon: Malformed Request From Sender";
                }
                else if (fixJustReceivedPacket.isGarbageMessage())
                {
                    disconnectReasonText = "During Logon: Garbage Data From Sender";
                }
                else
                {
                    if (disconnectReasonText == null)
                    {
                        disconnectReasonText = "During Logon: System Exception On Sender Data";
                    }
                }

                Log.information(Thread.currentThread().getName() + " " + disconnectReasonText);

                markUnsuccessfulConnectionAttempt(fixConnectionInformationHolder);

                try
                {
                    cleanup();
                }
                catch (Exception ex3)
                {

                }

                return;
            }

            // we only allow logon message as the first message && don't want to consume a sequence number here
            if (!fixJustReceivedMessage.isFixLogonMessage())
            {
                fixSessionState = null;

                fixSessionInstrumentation.start();

                disconnectReasonText = "First Message Not Logon: " + ClassHelper.getClassNameFinalPortion(fixJustReceivedMessage);

                Log.alarm(Thread.currentThread().getName() + " " + disconnectReasonText);

                markUnsuccessfulConnectionAttempt(fixConnectionInformationHolder);

                try
                {
                    cleanup();
                }
                catch (Exception ex3)
                {

                }

                return;
            }

            FixLogonMessage fixLogonMessage = (FixLogonMessage) fixJustReceivedMessage;

            try
            {
                fixSessionManager.sessionLogonRequest(FixMDXSession.this, fixLogonMessage);
            }
            catch (Exception ex)
            {
                // we don't want to consume a sequence number here

                fixSessionState = null;

                if (ex instanceof DataValidationException)
                {
                    disconnectReasonText = "LOGIN REJECTED BY FixSessionManager for SenderCompID(" + fixJustReceivedMessage.getSenderCompID() + ") TargetCompID(" + fixJustReceivedMessage.getTargetCompID() + ") Reason(" + ((DataValidationException) ex).details.message + ")";
                }
                else if (ex.getMessage() == null)
                {
                    disconnectReasonText = "LOGIN REJECTED BY FixSessionManager for SenderCompID(" + fixJustReceivedMessage.getSenderCompID() + ") TargetCompID(" + fixJustReceivedMessage.getTargetCompID() + ") Reason(" + ClassHelper.getClassNameFinalPortion(ex) + ")";
                }
                else
                {
                    disconnectReasonText = "LOGIN REJECTED BY FixSessionManager for SenderCompID(" + fixJustReceivedMessage.getSenderCompID() + ") TargetCompID(" + fixJustReceivedMessage.getTargetCompID() + ") Reason(" + ex.getMessage() + ")";
                }

                try
                {
                    Log.alarm(Thread.currentThread().getName() + " " + disconnectReasonText);
                }
                catch (Exception ex2)
                {

                }

                try
                {
                    Log.exception(ex);
                }
                catch (Exception ex2)
                {

                }

                markUnsuccessfulConnectionAttempt(fixConnectionInformationHolder);

                try
                {
                    cleanup();
                }
                catch (Exception ex3)
                {

                }

                return;
            }

            short userLogonType = LoginSessionTypes.SECONDARY; // I default this to secondary login, since we use COMPID's as duplicate logon detections

            try
            {
                // first, we need to extract the userid:password from the SenderSubID
                if (fixLogonMessage.fieldSenderSubID == null)
                {
                    throw ExceptionBuilder.dataValidationException("No userid:password specified in the SenderSubID field", 0);
                }

                if (fixLogonMessage.fieldTargetSubID == null)
                {
                    throw ExceptionBuilder.dataValidationException("No loginMode specified in the TargetSubID field", 0);
                }

                StringTokenizer tokenizer = new StringTokenizer(fixLogonMessage.fieldSenderSubID.getValue(), ":");

                // The SenderSubID field should be in the format: USERNAME:PASSWORD[:USERLOGINTYPE]
                // At least two tokens are required: USERNAME:PASSWORD
                if (tokenizer.countTokens() < 2)
                {
                    throw ExceptionBuilder.dataValidationException("SenderSubID tag is improperly formatted: " + fixLogonMessage.fieldSenderSubID.getValue(), 0);
                }

                userLogonStruct = new UserLogonStruct();

                userLogonStruct.version = VersionPOA.CMI_VERSION;

                userLogonStruct.loginMode = LoginSessionModes.PRODUCTION;
                if (fixLogonMessage.fieldTargetSubID.isProduction())
                {
                    userLogonStruct.loginMode = LoginSessionModes.PRODUCTION;
                }
                else if (fixLogonMessage.fieldTargetSubID.isTest())
                {
                    userLogonStruct.loginMode = LoginSessionModes.NETWORK_TEST;
                }
                else if (fixLogonMessage.fieldTargetSubID.isSimulator())
                {
                    userLogonStruct.loginMode = LoginSessionModes.STAND_ALONE_TEST;
                }

                userLogonStruct.userId   = tokenizer.nextToken();
                userLogonStruct.password = tokenizer.nextToken();

                if (tokenizer.countTokens() > 0)
                {
                    String loginSessionType = tokenizer.nextToken();
                    if (!loginSessionType.equals(PRIMARY))
                    {
                        if (loginSessionType.equals(SECONDARY))
                        {
                            userLogonType = LoginSessionTypes.SECONDARY;
                        }
                        else
                        {
                            throw ExceptionBuilder.dataValidationException("Improperly formatted SenderSubID or invalid UserLoginType: " + fixLogonMessage.fieldSenderSubID.getValue(), 0);
                        }
                    }
                }

                boolean bDefinedUser = true;

                String definedUsers = propertiesHelper.getProperty("cfix.fixSession.users");
                if (definedUsers != null && definedUsers.trim().length() > 0)
                {
                    bDefinedUser = false;

                    for (tokenizer = new StringTokenizer(definedUsers, ", "); tokenizer.hasMoreTokens();)
                    {
                        if (userLogonStruct.userId.equals(tokenizer.nextToken()))
                        {
                            bDefinedUser = true;
                            break;
                        }
                    }

                    tokenizer = null;
                }

                if (!bDefinedUser)
                {
                    throw ExceptionBuilder.dataValidationException("User(" + userLogonStruct.userId + ") Not Defined For FixSession", 0);
                }
            }
            catch (DataValidationException ex)
            {
                try
                {
                    disconnectReasonText = "LOGIN REJECTED: " + ex.details.message;
                    Log.alarm(Thread.currentThread().getName() + " " + disconnectReasonText);
                }
                catch (Exception ex2)
                {

                }

                try
                {
                    Log.exception(ex);
                }
                catch (Exception ex2)
                {

                }

                markUnsuccessfulConnectionAttempt(fixConnectionInformationHolder);

                try
                {
                    cleanup();
                }
                catch (Exception ex3)
                {

                }

                return;
            }
            catch (Exception ex)
            {
                try
                {
                    disconnectReasonText = "LOGIN REJECTED: " + ex.getMessage();
                    Log.alarm(Thread.currentThread().getName() + " " + disconnectReasonText);
                }
                catch (Exception ex2)
                {

                }

                try
                {
                    Log.exception(ex);
                }
                catch (Exception ex2)
                {

                }

                markUnsuccessfulConnectionAttempt(fixConnectionInformationHolder);

                try
                {
                    cleanup();
                }
                catch (Exception ex3)
                {

                }

                return;
            }

            sessionWriterTerminatedLatch = new Latch();

            if (fixLogonMessage.fieldCboeUDFSupportIndicator != null)
            {
                udfSupportIndicator = fixLogonMessage.fieldCboeUDFSupportIndicator.charValue();
                fixSessionWriter.cfixMarketDataFullRefreshMapper.setUDFSupportIndicator(udfSupportIndicator);
            }

            renameSession(fixJustReceivedMessage.getSenderCompID(), true);

            fixSessionInstrumentation.getNetworkConnectionInstrumentor().rename(targetCompID + "/" + sessionName + "/NetworkInstrumentor");
            fixSessionWriter.fixEventChannel.getQueueInstrumentor().rename(targetCompID + "/" + sessionName + "/QueueInstrumentor");
            FoundationFramework.getInstance().getInstrumentationService().getNetworkConnectionInstrumentorFactory().register(fixSessionInstrumentation.getNetworkConnectionInstrumentor());
            FoundationFramework.getInstance().getInstrumentationService().getQueueInstrumentorFactory().register(fixSessionWriter.fixEventChannel.getQueueInstrumentor());

            threadPool.execute(fixSessionWriter, "FixSessionWriter{" + sessionName + "}");

            try
            {
                fixSessionInformation.initialize(FixMDXSession.this);
                fixSessionInformation.connectionInformation.add(fixConnectionInformationHolder);
            }
            catch (Exception ex)
            {
                disconnectReasonText = "Engine Misconfigured";
                fixSessionWriter.fixEventChannel.enqueueHighPriority(buildFixSessionWriterCommandForRejectMessage(FixSessionRejectReasonField.string_InvalidMsgType, disconnectReasonText)); // should never happen
                fixSessionState = fixSessionStateDisconnect;
                return;
            }

            if (checkIfInvalidSendingTime())
            {
                return;
            }

            if (checkIfInvalidCompIDs())
            {
                return;
            }

            if (!IGNORE_ALL_SEQUENCE_NUMBER_PROBLEMS_ON_LOGON)
            {
                // the logon message's sequence number must be equalTo or greaterThan to ours, meaning that WE missed some of THEIR messages.
                // if it is less than what we already have, that means that THEY have "lost" some of their messages, so we have to drop them.
                if (fixJustReceivedMessage.getMsgSeqNum() < fixSessionInformation.getNextReceiveMsgSeqNum())
                {
                    disconnectReasonText = "Logon is OutOfSequence (OOS) - Expected #" + fixSessionInformation.getNextReceiveMsgSeqNum() + " but got #" + fixJustReceivedMessage.getMsgSeqNum();
                    fixSessionWriter.fixEventChannel.enqueueHighPriority(buildFixSessionWriterCommandForRejectMessage(FixSessionRejectReasonField.string_ValueIsIncorrect, disconnectReasonText));
                    fixSessionState = fixSessionStateDisconnect;

                    return;
                }
            }
            else
            {
                fixSessionInformation.setNextReceiveMsgSeqNum(fixJustReceivedMessage.getMsgSeqNum());

                if (fixLogonMessage.fieldLastMsgSeqNumProcessed != null)
                {
                    try
                    {
                        fixSessionInformation.setSendMsgSeqNum(fixLogonMessage.fieldLastMsgSeqNumProcessed.intValue());
                    }
                    catch (Exception ex)
                    {

                    }
                }
            }

            fixSessionInformation.incReceiveMsgSeqNum();

            disconnectReasonText = null;

            try
            {
                logonToCas(fixLogonMessage, userLogonType);

                cfixMDXMarketDataQuery = cfixSessionManager.getCfixMDXMarketDataQuery();
                cfixMDXMarketDataQuery.setCfixMarketDataConsumer(FixMDXSession.this);
            }
            catch (SystemException ex)
            {
                disconnectReasonText = ex.details.message;
                Log.exception(ex);
            }
            catch (CommunicationException ex)
            {
                disconnectReasonText = ex.details.message;
                Log.exception(ex);
            }
            catch (AuthorizationException ex)
            {
                disconnectReasonText = ex.details.message;
                Log.exception(ex);
            }
            catch (NotFoundException ex)
            {
                disconnectReasonText = ex.details.message;
                Log.exception(ex);
            }
            catch (DataValidationException ex)
            {
                disconnectReasonText = ex.details.message;
                Log.exception(ex);
            }
            catch (Exception ex)
            {
                disconnectReasonText = ex.getMessage();
                Log.exception(ex);
            }
            catch (Throwable ex)
            {
                disconnectReasonText = ex.getMessage();
                //Exception exception = new Exception(ex);
                //exception.setStackTrace(ex.getStackTrace());
                //Log.exception(exception);
            }

            if (disconnectReasonText != null)
            {
                fixSessionWriter.fixEventChannel.enqueueHighPriority(buildFixSessionWriterCommandForRejectMessage(FixSessionRejectReasonField.string_InvalidMsgType, disconnectReasonText));
                fixSessionState = fixSessionStateDisconnect;
                return;
            }

            notifySessionTargetLoggedIn();

            // we use their heart beat -- or default it if they don't care
            fixSessionInformation.setHeartBeatInterval(fixLogonMessage.fieldHeartBtInt);

            fixSocketAdapter.setTimeout((int) (fixSessionInformation.getHeartBeatInterval() * 1.15) * 1000);

            // we have accepted their login, so now start processing
            fixSessionWriter.fixEventChannel.enqueueHighPriority(buildFixSessionWriterCommandForLogonResponseMessage(false));
            fixSessionWriter.fixEventChannel.enqueueHighPriority(FixSessionWriterCommand.createHighPriorityEnd());

            if (!IGNORE_ALL_SEQUENCE_NUMBER_PROBLEMS_ON_LOGON)
            {
                // if WE missed some of their messages, then we want them to RESEND them to us
                if (!haveWeReset && fixJustReceivedMessage.getMsgSeqNum() > fixSessionInformation.getNextReceiveMsgSeqNum())
                {
                    fixSessionWriter.fixEventChannel.enqueueHighPriority(buildFixSessionWriterCommandForResendRequestMessage(fixSessionInformation.getNextReceiveMsgSeqNum(), 0));
                }
            }

            fixSessionState = fixSessionStateNormalProcessing;

            Log.information(Thread.currentThread().getName() + " Session Logged In");
        }

        public void markUnsuccessfulConnectionAttempt(FixConnectionInformationHolder fixConnectionInformationHolder)
        {
            markedAsUnsuccessful = true;

            try
            {
                fixConnectionInformationHolder.disconnectTime = System.currentTimeMillis();
                fixConnectionInformationHolder.disconnectReason = disconnectReasonText;

                FixSessionInformation info = (FixSessionInformation) fixSessionManager.getFixSessionInformationByName(FixSessionManager.UNSUCCESSFUL_CONNECTION_SESSION_INFORMATION_NAME);

                info.connectionInformation.add(fixConnectionInformationHolder);
            }
            catch (Exception ex)
            {

            }
        }

        public String toString()
        {
            return "{" + ClassHelper.getClassNameFinalPortion(getClass()) + "(???)}";
        }
    }

    protected class FixMDXSessionStateNormalProcessing extends FixMDXSession.FixMDXSessionState
    {
        public void processMessage() throws Exception
        {
            // get the next message from the socket -- if it has problems, then kick it upstairs,
            // and see what the Session wants to do with it
            if (!receiveGoodFixMessage())
            {
                if (fixJustReceivedPacket.isMessageTimedOut())
                {
                    if (fixSessionInformation.getOutstandingTestRequestList().size() >= fixSessionInformation.maxOutstandingTestRequests)
                    {
                        disconnectReasonText = "No HeartBeat Responses after #" + fixSessionInformation.maxOutstandingTestRequests + " TestRequests";
                        fixSessionState = fixSessionStateDisconnect;
                        return;
                    }

                    fixSessionWriter.fixEventChannel.enqueueHighPriority(buildFixSessionWriterCommandForTestRequestMessage());
                }
                else if (fixJustReceivedPacket.isMessageDisconnected())
                {
                    return;
                }
                else if (fixJustReceivedPacket.isGarbageMessage())
                {

                }
                else
                {
                    if (!foundErrors.isEmpty())
                    {
                        byte   error    = BitHelper.unpackHighByte(foundErrors.get(0));
                        int    position = BitHelper.unpackLowShortAsInt(foundErrors.get(0));
                        String s;

                        if (FixException.isPositionATag(error, position))
                        {
                            s = FixException.toString(error, position);
                        }
                        else
                        {
                            s = FixException.toString(error, position) + "{" + fixJustReceivedPacket.getTag(position) + "=" + new String(fixJustReceivedPacket.getArray(), fixJustReceivedPacket.getValueOffset(position), fixJustReceivedPacket.getValueLength(position)) + "}";
                        }

                        fixSessionWriter.fixEventChannel.enqueueHighPriority(buildFixSessionWriterCommandForRejectMessage(FixSessionRejectReasonField.string_ValueIsIncorrect, fixJustReceivedMessage.getMsgTypeAsChars(), s));
                    }
                    else if (fixJustReceivedPacket.isBadFixMessage())
                    {
                        fixSessionWriter.fixEventChannel.enqueueHighPriority(buildFixSessionWriterCommandForRejectMessage(FixSessionRejectReasonField.string_ValueIsIncorrect, null, fixJustReceivedPacket.toString()));
                    }

                    fixSessionInformation.incReceiveMsgSeqNum();
                }

                return;
            }

            if (checkIfInvalidCompIDs())
            {
                 return;
            }

            if (checkIfInvalidSendingTime())
            {
                return;
            }

            if (fixJustReceivedMessage.isFixSequenceResetMessage())
            {
                FixSequenceResetMessage fixSequenceResetMessage = (FixSequenceResetMessage) fixJustReceivedMessage;
                int newSeqNum = fixSequenceResetMessage.fieldNewSeqNo.intValue();

                if (fixSequenceResetMessage.fieldGapFillFlag == null || fixSequenceResetMessage.fieldGapFillFlag.isMsgSeqNumReset())
                {
                    Log.information(Thread.currentThread().getName() + " RECEIVED SequenceReset-Reset from #" + fixSessionInformation.getNextReceiveMsgSeqNum() + " to #" + newSeqNum);
                    fixSessionInformation.setNextReceiveMsgSeqNum(newSeqNum);

                    return;
                }

                if (fixJustReceivedMessage.getMsgSeqNum() == fixSessionInformation.getNextReceiveMsgSeqNum())
                {
                    if (newSeqNum > fixSessionInformation.getNextReceiveMsgSeqNum())
                    {
                        fixSessionInformation.setNextReceiveMsgSeqNum(newSeqNum);

                        return;
                    }
                }
            }

            // if it is less than what we already have, that means that THEY have "lost" some of their messages, so we have to drop them.
            if (fixJustReceivedMessage.getMsgSeqNum() < fixSessionInformation.getNextReceiveMsgSeqNum())
            {
                if (fixJustReceivedMessage.isPossDup())
                {
                    if (Log.isDebugOn()) Log.debug(Thread.currentThread().getName() + " DISCARDING RECEIVED DUPLICATE MESSAGE");
                    return;
                }

                disconnectReasonText = "Message OutOfSequence (OOS) - Expected #" + fixSessionInformation.getNextReceiveMsgSeqNum() + " but got #" + fixJustReceivedMessage.getMsgSeqNum();
                fixSessionWriter.fixEventChannel.enqueueHighPriority(buildFixSessionWriterCommandForRejectMessage(FixSessionRejectReasonField.string_ValueIsIncorrect, disconnectReasonText));
                fixSessionState = fixSessionStateDisconnect;

                return;
            }

            if (fixJustReceivedMessage.getMsgSeqNum() > fixSessionInformation.getNextReceiveMsgSeqNum())
            {
                String s = Thread.currentThread().getName() + " IGNORING Higher MsgSeqNum(" + fixJustReceivedMessage.getMsgSeqNum() + ") Than Expected(" + fixSessionInformation.getNextReceiveMsgSeqNum() +")";

                if (fixSessionInformation.isResendRequestCurrentlyOutstandingFor(fixJustReceivedMessage.getMsgSeqNum()))
                {
                    Log.information(s);
                }
                else
                {
                    Log.information(s + " And Requesting Resend From(" + fixSessionInformation.getNextReceiveMsgSeqNum() + ")");
                    fixSessionInformation.setResendRequestOutstandingFor(fixSessionInformation.getNextReceiveMsgSeqNum());
                    fixSessionWriter.fixEventChannel.enqueueHighPriority(buildFixSessionWriterCommandForResendRequestMessage(fixSessionInformation.getNextReceiveMsgSeqNum(), 0));
                }

                return;
            }

            fixSessionInformation.setResendRequestOutstandingFor(0);

            fixSessionInformation.incReceiveMsgSeqNum();

            if (fixJustReceivedMessage.isFixLogonMessage())
            {
                disconnectReasonText = "Received Logon when already Logged On";
                fixSessionWriter.fixEventChannel.enqueueHighPriority(buildFixSessionWriterCommandForRejectMessage(FixSessionRejectReasonField.string_InvalidMsgType, disconnectReasonText));
                fixSessionState = fixSessionStateDisconnect;
                return;
            }

            processInboundMessage();

            return;
        }
    }

    protected class FixMDXSessionStateDisconnect extends FixMDXSession.FixMDXSessionState
    {
        public void processMessage() throws Exception
        {
            if (disconnectReason == 0)
            {
                disconnectReason = FixSessionListenerIF.SESSION_DISCONNECTED_BY_SENDER;
            }

            terminate(false);

            fixSessionState = null;
        }
    }

    public boolean containsMDReqID(String mdReqID)
    {
        return marketDataFutureExecutionMap.containsKey(mdReqID);
    }

    protected void processRequestResendMessage(int startingResendRequestMsgSeqNum, int endingResendRequestMsgSeqNum) throws Exception
    {
        fixSessionWriter.fixEventChannel.enqueueHighPriority(FixSessionWriterCommand.createHighPriorityBegin());

        fixSessionState = fixSessionStateNormalProcessing;

        try
        {
            int end = endingResendRequestMsgSeqNum == 0 ? fixSessionInformation.getNextSendMsgSeqNum(): endingResendRequestMsgSeqNum + 1;

            FirstIntIF firstInt;

            for (int i = startingResendRequestMsgSeqNum; i < end;)
            {
                firstInt = fixSessionInformation.fixResendList.getStoredMessage(i, end);

                if (firstInt instanceof IntIntPair)
                {
                    IntIntPair intIntPair = (IntIntPair) firstInt;

                    if (endingResendRequestMsgSeqNum != 0 && (intIntPair.getSecond() == endingResendRequestMsgSeqNum + 1))
                    {
                        fixSessionWriter.fixEventChannel.enqueueHighPriority(buildFixSessionWriterCommandForSequenceResetMessage(intIntPair.getFirst(), fixSessionInformation.getNextSendMsgSeqNum(), true));

                        break;
                    }
                    else
                    {
                        fixSessionWriter.fixEventChannel.enqueueHighPriority(buildFixSessionWriterCommandForSequenceResetMessage(intIntPair.getFirst(), ((IntIntPair) firstInt).getSecond(), true));

                        i = ((IntIntPair) firstInt).getSecond();
                    }
                }
                else
                {
                    fixSessionWriter.fixEventChannel.enqueueHighPriority(buildFixSessionWriterCommandForResendMessage(((IntStringPair) firstInt).getSecond()));

                    i++;
                }
            }
        }
        finally
        {
            fixSessionWriter.fixEventChannel.enqueueHighPriority(FixSessionWriterCommand.createHighPriorityEnd());
        }
    }

    protected void processInboundMessage() throws Exception
    {
        if (fixJustReceivedMessage.isFixHeartBeatMessage())
        {
            if (((FixHeartBeatMessage) fixJustReceivedMessage).fieldTestReqID != null)
            {
                fixSessionInformation.removeOutstandingTestRequest(((FixHeartBeatMessage) fixJustReceivedMessage).fieldTestReqID.getValue());
            }

            return;
        }

        if (fixJustReceivedMessage.isFixTestRequestMessage())
        {
            fixSessionWriter.fixEventChannel.enqueueHighPriorityFront(buildFixSessionWriterCommandForTestRequestResponseMessage(((FixTestRequestMessage) fixJustReceivedMessage).fieldTestReqID.getValue()));
            return;
        }


        if (fixJustReceivedMessage.isFixResendRequestMessage())
        {
            processRequestResendMessage(((FixResendRequestMessage) fixJustReceivedMessage).fieldBeginSeqNo.intValue(), ((FixResendRequestMessage) fixJustReceivedMessage).fieldEndSeqNo.intValue());
            return;
        }

        if (fixJustReceivedMessage.isFixRejectMessage())
        {
            return;
        }

        if (fixJustReceivedMessage.isFixBusinessMessageRejectMessage())
        {
            return;
        }

        if (fixJustReceivedMessage.isFixSequenceResetMessage())
        {
            return;
        }

        if (fixJustReceivedMessage.isFixLogoutMessage())
        {
            FixLogoutMessage fixLogoutMessage = (FixLogoutMessage) fixJustReceivedMessage;
            if (fixLogoutMessage.fieldText.hasValue())
            {
                String logoutText =  fixLogoutMessage.fieldText.getValue();
                /*
                    Sample reject message from client - MsgSeqNum too low, expecting 3406847 but received 41
                    This is a generic processing algo that should take care of any space delimited Text.
                    It will get the highest int in the message and use that as the basis for the next sequence no.
                */
                String[] result = logoutText.split("\\s");
                // make sure that the highest sequence number is greater than the current outbound sequence number
                int highestSeqNum = fixSessionInformation.getNextSendMsgSeqNum();
                for (int i=0; i <result.length; i++)
                {
                    try
                    {
                        int temp = Integer.parseInt(result[i]);
                        if (temp > highestSeqNum)
                            highestSeqNum = temp;
                    } catch (NumberFormatException nfe)
                    {
                        // This is expected - we are trying to get and and every int in the text field.
                    }
                }
                if(highestSeqNum > fixSessionInformation.getNextSendMsgSeqNum())
                {
                    Log.information("Setting next outbound sequence number to: " + highestSeqNum);
                    fixSessionInformation.setSendMsgSeqNum(highestSeqNum);
                }else
                {
                    Log.information("Logoff message processed. Sequence Number reset not required.");
                }

            }

            disconnectReasonText = "Sender Logout Request Accepted";
            fixSessionWriter.fixEventChannel.enqueueHighPriority(buildFixSessionWriterCommandForLogoutMessage(disconnectReasonText));
            fixSessionState = fixSessionStateDisconnect;
            return;
        }

        // todo - VivekB: this is where things need to be taken care of for MDX subscription -
        // this does not seem in need of a change - this is only looking at MDReqID as a basis of accepting or rejecting subscriptions -
        // this logic should not change with MDX enabled CFIX
        if (fixJustReceivedMessage.isFixMarketDataRequestMessage())
        {
            FixMarketDataRequestMessage     fixMarketDataRequestMessage = (FixMarketDataRequestMessage) fixJustReceivedMessage;
            String                          mdReqID                     = fixMarketDataRequestMessage.fieldMDReqID.getValue();
            FixMDXMarketDataFutureExecution marketDataFutureExecution;

            if (inCleanup)
            {
                return;
            }

            // Check if the user is trying to unsubscribe to some existing subscription -
            if (fixMarketDataRequestMessage.fieldSubscriptionRequestType.isDisablePrevious())
            {
                // first, see if we have FutureExecution
                marketDataFutureExecution = (FixMDXMarketDataFutureExecution) marketDataFutureExecutionMap.remove(mdReqID);

                // nope
                if (marketDataFutureExecution == null)
                {
                    acceptMarketDataReject(new FixMarketDataRejectStruct(FixMarketDataRejectStruct.DuplicateMdReqId, "No Subscription Found For This MDReqID", mdReqID));
                    return;
                }

                marketDataFutureExecution.cancelAndUndo(1);

                // if already finished, then see if we need to undo
                if (BitHelper.isBitMaskSet(marketDataFutureExecution.getStatusBits(), FutureExecutionIF.MAIN_STATE_FINISHED))
                {
                    if (BitHelper.isBitMaskSet(marketDataFutureExecution.getStatusBits(), FutureExecutionIF.CURRENT_STATE_UNDO))
                    {
                        // nothing to do -- it successfully cleaned up
                        return;
                    }

                    if (BitHelper.isBitMaskSet(marketDataFutureExecution.getStatusBits(), FutureExecutionIF.CURRENT_STATE_SUCCEEDED))
                    {
                        // we have to cleanup this up
                        marketDataFutureExecution = new FixMDXMarketDataFutureExecution(this, marketDataFutureExecution);
                        threadPool.execute(marketDataFutureExecution, "FixMarketDataFutureExecutionUnsubscriber(" + mdReqID + ")");
                        return;
                    }
                }

                // it will clean itself up because of the cancelAndUndo()
                return;
            }

            // Refuse IncrementalRefresh
            if (fixMarketDataRequestMessage.fieldMDUpdateType != null && fixMarketDataRequestMessage.fieldMDUpdateType.isIncrementalRefresh())
            {
                acceptMarketDataReject(new FixMarketDataRejectStruct(FixMarketDataRejectStruct.InsufficientPermissions, "Engine Does Not Handle IncrementalRefresh Subscriptions", mdReqID));
                return;
            }

            marketDataFutureExecution = (FixMDXMarketDataFutureExecution) marketDataFutureExecutionMap.get(mdReqID);
            if (marketDataFutureExecution != null)
            {
                acceptMarketDataReject(new FixMarketDataRejectStruct(FixMarketDataRejectStruct.DuplicateMdReqId, "Already Subscribed By This MDReqID", mdReqID));
                return;
            }

            marketDataFutureExecution = new FixMDXMarketDataFutureExecution(this, fixMarketDataRequestMessage);

            if (inCleanup)
            {
                return;
            }

            marketDataFutureExecutionMap.put(mdReqID, marketDataFutureExecution);

            threadPool.execute(marketDataFutureExecution, "FixMarketDataFutureExecution(" + mdReqID + ")");

            return;
        }

        fixSessionWriter.fixEventChannel.enqueueHighPriority(buildFixSessionWriterCommandForBusinessMessageRejectMessage(FixBusinessRejectReasonField.string_UnsupportedMessageType, "Unsupported MsgType (" + fixJustReceivedMessage.getMsgTypeAsChars() + ")"));

        Log.alarm(Thread.currentThread().getName() + " RECEIVED AND REJECTED AN APPLICATION LEVEL MESSAGE!!!!");
    }

    public void acceptLogout(String reason)
    {
        Log.information(Thread.currentThread().getName() + " acceptLogout(" + reason + ")");

        if (disconnectReasonText == null)
        {
            disconnectReasonText = "Sender Logged Out By CAS (" + reason + ")";
        }

        terminate(false);
    }

    public void acceptTextMessage(MessageStruct messageStruct)
    {
        if (messageStruct == null)
        {
            return;
        }

        FixSessionWriterCommand fixSessionWriterCommand = buildFixSessionWriterCommandForEmailMessage(messageStruct.sender, messageStruct.messageKey, messageStruct.subject, messageStruct.messageText, messageStruct.originalMessageKey, messageStruct.timeStamp);

        if (fixSessionWriterCommand != null)
        {
            fixSessionWriter.fixEventChannel.enqueueHighPriority(fixSessionWriterCommand);
        }
    }

    private void enqueueMarketData(OverlayPolicyMarketDataListIF cfixOverlayPolicyMarketDataList, String mdType)
    {
        if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_CONSUMER_ACCEPT)) {Log.information(Thread.currentThread().getName() + " " + sessionName + ":acceptMarketData" + mdType + "(" + cfixOverlayPolicyMarketDataList + ")");}

        if (cfixOverlayPolicyMarketDataList.getPolicyType() == OverlayPolicyMarketDataListIF.ALWAYS_OVERLAY_POLICY)
        {
            enqueueOutboundUniqueFixMessage(cfixOverlayPolicyMarketDataList);
        }
        else
        {
            if (fixSessionWriter.fixEventChannel.size() > maxQueueSize)
            {
                this.acceptLogout("Queue Buildup of size : " + fixSessionWriter.fixEventChannel.size()
                        + " for session : " + this.sessionName + ". Logging out user!");
                try
                {
                    cfixSessionManager.logout();
                } catch(Exception e){
                    Log.exception("FixSession : " + this + " : Logout Failure", e);
                }
            }

            enqueueOutboundFixMessage(cfixOverlayPolicyMarketDataList);
        }
    }

    public void acceptMarketDataCurrentMarket(OverlayPolicyMarketDataListIF cfixOverlayPolicyMarketDataList)
    {
        enqueueMarketData(cfixOverlayPolicyMarketDataList, "CurrentMarket");
    }

    public void acceptMarketDataBookDepth(OverlayPolicyMarketDataListIF cfixOverlayPolicyMarketDataList)
    {
        enqueueMarketData(cfixOverlayPolicyMarketDataList, "BookDepth");
    }

    public void acceptMarketDataBookDepthUpdate(OverlayPolicyMarketDataListIF cfixOverlayPolicyMarketDataList)
    {
        enqueueMarketData(cfixOverlayPolicyMarketDataList, "BookDepthUpdate");
    }

    public void acceptMarketDataExpectedOpeningPrice(OverlayPolicyMarketDataListIF cfixOverlayPolicyMarketDataList)
    {
        enqueueMarketData(cfixOverlayPolicyMarketDataList, "ExpectedOpeningPrice");
    }

    public void acceptMarketDataNbbo(OverlayPolicyMarketDataListIF cfixOverlayPolicyMarketDataList)
    {
        enqueueMarketData(cfixOverlayPolicyMarketDataList, "Nbbo");
    }

    public void acceptMarketDataRecap(OverlayPolicyMarketDataListIF cfixOverlayPolicyMarketDataList)
    {
        enqueueMarketData(cfixOverlayPolicyMarketDataList, "Recap");
    }

    public void acceptMarketDataTicker(OverlayPolicyMarketDataListIF cfixOverlayPolicyMarketDataList)
    {
        enqueueMarketData(cfixOverlayPolicyMarketDataList, "Ticker");
    }

    public void acceptMarketDataReject(CfixMarketDataRejectStruct cfixFixMarketDataRejectStruct)
    {
        if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_CONSUMER_ACCEPT)) {Log.information(Thread.currentThread().getName() + " " + sessionName + ":acceptMarketDataReject(" + cfixFixMarketDataRejectStruct + ")");}

        enqueueOutboundFixMessage(cfixFixMarketDataRejectStruct);
    }

    public boolean isAcceptingMarketData()
    {
        return fixSessionWriter.terminateInProgress == false;
    }

    /* package protected */ void cleanup()
    {
        synchronized(fixSessionStatePreLogon) // any lock will do
        {
            if (inCleanup)
            {
                return;
            }
        }

        inCleanup = true;

        Log.information(Thread.currentThread().getName() + " IN SESSION CLEANUP");

        try
        {
            FoundationFramework.getInstance().getInstrumentationService().getNetworkConnectionInstrumentorFactory().unregister(fixSessionInstrumentation.getNetworkConnectionInstrumentor());
        }
        catch (Exception ex)
        {
            Log.exception(Thread.currentThread().getName() + " NCI unregister failed!", ex);
        }

        try
        {
            FoundationFramework.getInstance().getInstrumentationService().getQueueInstrumentorFactory().unregister(fixSessionWriter.fixEventChannel.getQueueInstrumentor());
        }
        catch (Exception ex)
        {
            Log.exception(Thread.currentThread().getName() + " QI unregister failed!", ex);
        }

        try
        {
            threadPool.stopPool();
        }
        catch (Exception ex)
        {

        }

        try
        {
            Socket socket = fixSocketAdapter.getSocket();
            socket.setSoLinger(true, 5);
            socket.setTcpNoDelay(true);
        }
        catch (Exception ex)
        {

        }

        try
        {
            fixSocketAdapter.close();
        }
        catch (Exception ex)
        {
            Log.exception(ex);
        }

        try
        {
            notifySessionTerminating();
        }
        catch (Exception ex)
        {
            Log.exception(ex);
        }

        Map.Entry entry;

        Map tempMap = new HashMap(marketDataFutureExecutionMap.size() + 10);

        marketDataFutureExecutionMap.putAll(tempMap);

        for (Iterator iterator = tempMap.entrySet().iterator(); iterator.hasNext(); )
        {
            try
            {
                entry = (Map.Entry) iterator.next();
                ((FutureExecutionIF) entry.getValue()).cancelAndUndo(1);
            }
            catch (Exception ex)
            {
                Log.exception(ex);
            }
        }

        try
        {
            if (cfixMDXMarketDataQuery != null)
            {
                Log.information(Thread.currentThread().getName() + " UNSUBSCRIBING LISTENER");
                cfixMDXMarketDataQuery.unsubscribeListener();
            }
        }
        catch (Exception ex)
        {
            Log.exception(ex);
        }

        try
        {
            if (cfixSessionManager != null)
            {
                cfixSessionManager.logout();
            }
        }
        catch (Exception ex)
        {
            Log.exception(ex);
        }
    }

    public int compareTo(Object object)
    {
        if (object == this)
        {
            return 0;
        }

        if (!(object instanceof FixSession) || sessionName == null)
        {
            return 1;
        }

        final FixSession other = (FixSession) object;

        if (sessionCreationTime != other.sessionCreationTime)
        {
            return (int) (sessionCreationTime - other.sessionCreationTime);
        }

        return sessionName.compareTo(other.sessionName);
    }

    public boolean equals(Object object)
    {
        if (object == this)
        {
            return true;
        }

        if (!(object instanceof FixSession) || object == null)
        {
            return false;
        }

        final FixSession other = (FixSession) object;

        return sessionCreationTime == other.sessionCreationTime && sessionName.equals(other.sessionName);
    }

    public int hashCode()
    {
        return (int) sessionCreationTime;
    }

    //private void debugSendEmail()
    //{
        //MessageStruct messageStruct = new MessageStruct();
        //messageStruct.originalMessageKey = 0;
        //messageStruct.messageKey = 136782034;
        //messageStruct.messageText = "ATTENTION TRADERS: SOME USERS ARE NOT RECEIVING TRADES INTO THEIR POSITION MANAGER.\nWE ARE CURRENTLY LOOKING INTO THIS PROBLEM.\nIF YOU NEED A COPY OF YOUR TRADES CONTACT THE HELPDESK AT 4100 OR GO TO TFL IN ROW 20.\n";
        //messageStruct.sender = "Help Desk";
        //messageStruct.subject = "Position Manager";
        //messageStruct.timeStamp = new DateTimeStruct(new DateStruct((byte) 2, (byte) 11,(short) 2004), new TimeStruct((byte) 19, (byte) 34, (byte) 27, (byte) 34));
        //acceptTextMessage(messageStruct);
    //}
}
