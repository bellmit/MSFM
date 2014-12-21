package com.cboe.cfix.fix.fix42.session;

import com.cboe.cfix.interfaces.*;
import com.cboe.cfix.fix.session.FixSessionInstrumentation;
import com.cboe.cfix.fix.util.FixMessageBuilder;
import com.cboe.cfix.fix.util.FixChecksumHelper;
import com.cboe.cfix.fix.util.FixMarketDataRejectStruct;
import com.cboe.cfix.fix.fix42.generated.fields.*;
import com.cboe.cfix.fix.fix42.generated.messages.FixMarketDataRequestRejectMessage;
import com.cboe.cfix.cas.fix42.CfixMarketDataIncrementalMapper;
import com.cboe.cfix.cas.fix42.CfixMDXMarketDataFullRefreshMapper;
import com.cboe.cfix.util.OverlayPolicyMarketDataHolder;
import com.cboe.interfaces.cfix.CfixMarketDataMDReqIDHelper;
import com.cboe.interfaces.cfix.OverlayPolicyMarketDataHolderIF;
import com.cboe.interfaces.cfix.OverlayPolicyMarketDataListIF;
import com.cboe.client.util.queue.DoublePriorityEventChannelIF;
import com.cboe.client.util.queue.DoublePriorityEventChannel;
import com.cboe.client.util.*;
import com.cboe.client.util.collections.CalculateSizeVisitor;
import com.cboe.instrumentationService.instrumentors.QueueInstrumentor;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

import java.net.SocketException;

/**
 * Created by IntelliJ IDEA.
 * User: beniwalv
 * Date: Jun 28, 2010
 * Time: 11:44:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class FixMDXSessionWriter implements Runnable, FixSessionWriterIF, CfixMarketDataMDReqIDHelper
{
    public    FixMDXSession                         fixSession;
    protected FixSessionInstrumentation             fixSessionInstrumentation;
    public    DoublePriorityEventChannelIF          fixEventChannel;
    protected boolean                               justDequeueHighPriority                     = true; // we expect to dequeue Logon first, then move on to drain the queue
    protected int                                   debugFlags                                  = FixSessionDebugIF.DEBUG_OFF;
    protected boolean                               terminateInProgress;
    protected CfixMarketDataIncrementalMapper       cfixMarketDataIncrementalMapper             = new CfixMarketDataIncrementalMapper();
    protected CfixMDXMarketDataFullRefreshMapper    cfixMarketDataFullRefreshMapper             = new CfixMDXMarketDataFullRefreshMapper();
    protected FixMessageBuilder                     cachedFixMessageBuilder                     = new FixMessageBuilder();
    protected FastCharacterWriter                   fixSessionWriterScratchFastCharacterWriter  = new FastCharacterWriter();
    protected boolean                               askedToTerminate;

    private static final boolean SENT_SUCCESSFULLY = true;
    private static final boolean SEND_FAILED       = false;

    private static final String TIMEDOUT = "FixSessionWriter.TIMEDOUT";

    public FixMDXSessionWriter(FixMDXSession fixSession)
    {
        this.fixSession = fixSession;

        fixEventChannel = new DoublePriorityEventChannel(2048);

        QueueInstrumentor queueInstrumentor = null;

        try
        {
            queueInstrumentor = FoundationFramework.getInstance().getInstrumentationService().getQueueInstrumentorFactory().create(null, null);
        }
        catch (InstrumentorAlreadyCreatedException ex)
        {

        }

        fixEventChannel.setQueueInstrumentor(queueInstrumentor);
    }

    public void setFixSessionInstrumentation(FixSessionInstrumentation fixSessionInstrumentation)
    {
        this.fixSessionInstrumentation = fixSessionInstrumentation;
    }

    public DoublePriorityEventChannelIF getFixEventChannel()
    {
        return fixEventChannel;
    }

    public boolean getJustDequeueHighPriority()
    {
        return justDequeueHighPriority;
    }

    public void setDebugFlags(int debugFlags)
    {
        this.debugFlags = debugFlags;
    }

    public boolean isTerminating()
    {
        return terminateInProgress;
    }

    public void terminate(boolean immediate)
    {
        if (terminateInProgress)
        {
            return;
        }

        terminateInProgress = true;

        Log.information(Thread.currentThread().getName() + " REQUESTED TO TERMINATE REASON = " + fixSession.disconnectReasonText);

        if (immediate)
        {
            fixEventChannel.enqueueHighPriorityFront(FixSessionWriterCommand.createHighPriorityTerminate());
        }
        else
        {
            fixEventChannel.enqueueHighPriority(FixSessionWriterCommand.createHighPriorityTerminate());
        }
    }

    protected void internalBuildHeader(FixMessageBuilder fixMessageBuilder, char[] msgType, int seq) throws Exception
    {
        fixMessageBuilder.append(FixBeginStringField.TagIDAsChars,   fixSession.fixMessageFactory.getFixVersionAsChars());

        fixMessageBuilder.append(FixBodyLengthField.TagIDAsChars,    StringHelper.zeroesChars[FixFieldIF.FIX_TAG_9_OUTGOING_DIGITS]);

        fixMessageBuilder.append(FixMsgTypeField.TagIDAsChars,       msgType);

        fixMessageBuilder.append(FixSendingTimeField.TagIDAsChars,   DateHelper.stringizeDateInFixUTCTimeStampFormat());
        fixMessageBuilder.append(FixMsgSeqNumField.TagIDAsChars,     seq);

        fixMessageBuilder.append(FixSenderCompIDField.TagIDAsChars,  fixSession.fixSessionInformation.getTargetCompID()); // flip sender/target when sending
        fixMessageBuilder.append(FixTargetCompIDField.TagIDAsChars,  fixSession.fixSessionInformation.getSenderCompID()); // flip sender/target when sending
    }

    protected void internalBuildTrailer(FixMessageBuilder fixMessageBuilder) throws Exception
    {
        int bodyLength = fixMessageBuilder.size() - FixFieldIF.FIX_TAG_35_OUTGOING_OFFSET;

        //IF FIX_TAG_9_OUTGOING_DIGITS ever changes from 4 to 5, then have to change the second line in all of these accordingly, and add another if (<100000)
        if (bodyLength < 100)
        {
            fixMessageBuilder.replace(FixFieldIF.FIX_TAG_8_LENGTH + 2 + FixFieldIF.FIX_TAG_9_OUTGOING_DIGITS - 2,
                           FixFieldIF.FIX_TAG_9_OUTGOING_DIGITS - 2,
                           StringHelper.numbersChars(bodyLength));
        }
        else if (bodyLength < 1000)
        {
            fixMessageBuilder.replace(FixFieldIF.FIX_TAG_8_LENGTH + 2 + FixFieldIF.FIX_TAG_9_OUTGOING_DIGITS - 3,
                           FixFieldIF.FIX_TAG_9_OUTGOING_DIGITS - 1,
                           StringHelper.numbersChars(bodyLength));
        }
        else
        {
            fixMessageBuilder.replace(FixFieldIF.FIX_TAG_8_LENGTH + 2 + FixFieldIF.FIX_TAG_9_OUTGOING_DIGITS - 4,
                           FixFieldIF.FIX_TAG_9_OUTGOING_DIGITS,
                           StringHelper.numbersChars(bodyLength));
        }

        fixMessageBuilder.append(FixCheckSumField.TagIDAsChars, FixChecksumHelper.calculateFixChecksumToString(fixMessageBuilder.getFastCharacterWriter()));

        if (BitHelper.isBitMaskSet(fixSession.debugFlags, FixSessionDebugIF.SESSION_DECODE_SENT_MESSAGES))
        {
            FixSessionDebugger.dumpFixMessage(Thread.currentThread().getName() + " FIXDECODE SEND ", fixMessageBuilder.getFastCharacterWriter(), fixSession.fixMessageFactory, FixSessionDebugIF.SESSION_DECODE_SENT_MESSAGES | FixSessionDebugIF.SESSION_SHOW_PREDECODE_MESSAGE_DATA);
        }
    }

    protected boolean internalSend(char[] msgTypeAsChars, FastCharacterWriter fastCharacterWriter, boolean storeInResendList)
    {
        String msgType = fixSession.fixMessageFactory.getMsgType(msgTypeAsChars);

        try
        {
            if (fixSession.fixSessionInformation.isSuppressedSentFixMsgType(msgType))
            {
                fixSession.fixSocketAdapter.writeNoDebug(fastCharacterWriter);
            }
            else
            {
                fixSession.fixSocketAdapter.write(fastCharacterWriter);
            }

            fixSessionInstrumentation.incNetworkPacketsSent();
            fixSessionInstrumentation.incSentMsgType(msgType);
            fixSessionInstrumentation.addBytesSent(fastCharacterWriter.size());

            if (storeInResendList)
            {
                fixSession.fixSessionInformation.fixResendList.storeSentMessage(fixSession.fixSessionInformation.incSendMsgSeqNum(), msgType, fastCharacterWriter);
            }
        }
        catch (SocketException ex)
        {
            if (fixSession.disconnectReasonText == null)
            {
                fixSession.disconnectReasonText = "Could Not Send To Sender";
            }
            terminate(true);
            return SEND_FAILED;
        }
        catch (Exception ex)
        {
            Log.exception(ex);
            if (fixSession.disconnectReasonText == null)
            {
                fixSession.disconnectReasonText = "Could Not Send To Sender";
            }
            terminate(true);
            return SEND_FAILED;
        }

        return SENT_SUCCESSFULLY;
    }

    protected boolean buildAndSendRaw(char[] msgType, FastCharacterWriter fastCharacterWriter) throws Exception
    {
        return internalSend(msgType, fastCharacterWriter, true);
    }

    protected boolean buildAndSend(char[] msgType, FastCharacterWriter fastCharacterWriter) throws Exception
    {
        cachedFixMessageBuilder.clear();

        internalBuildHeader(cachedFixMessageBuilder, msgType, fixSession.fixSessionInformation.getNextSendMsgSeqNum());

        cachedFixMessageBuilder.getFastCharacterWriter().write(fastCharacterWriter);

        internalBuildTrailer(cachedFixMessageBuilder);

        return internalSend(msgType, cachedFixMessageBuilder.getFastCharacterWriter(), true);
    }

    protected boolean buildAndSend(int seq, char[] msgType, FastCharacterWriter fastCharacterWriter) throws Exception
    {
        cachedFixMessageBuilder.clear();

        internalBuildHeader(cachedFixMessageBuilder, msgType, seq);

        cachedFixMessageBuilder.getFastCharacterWriter().write(fastCharacterWriter);

        internalBuildTrailer(cachedFixMessageBuilder);

        return internalSend(msgType, cachedFixMessageBuilder.getFastCharacterWriter(), false);
    }

    protected boolean rebuildAndResend(FastCharacterWriter fastCharacterWriter) throws Exception
    {
        return false; //TODO
/*
        FastCharacterWriter messageBody = new FastCharacterWriter();
        FastCharacterWriter prefix      = new FastCharacterWriter();
        int sohIndex = -1;

//DV -- have to retest since changes 03/03/03

        String body = buffer.toString();

        sohIndex = body.indexOf(FixFieldIF.SOH, sohIndex + 1); // SOH before BodyLength

        int beforeMsgType = sohIndex + 1;

        sohIndex = body.indexOf(FixFieldIF.SOH, sohIndex + 1); // SOH before MsgType

        int afterBodyLength = sohIndex + 1;

        String msgType = body.substring(beforeMsgType, afterBodyLength);

        sohIndex = body.indexOf(FixFieldIF.SOH, sohIndex + 1); // SOH before SendingTime

        int beforeSendingTime = sohIndex + 1;

        sohIndex = body.indexOf(FixFieldIF.SOH, sohIndex + 1); // SOH before MsgSeqNum

        int afterSendingTime = sohIndex;

        messageBody.append(body.substring(afterBodyLength, beforeSendingTime));

        FixMessageBuilder.appendFixString(messageBody, FixSendingTimeField.TagIDAsChars,     DateHelper.stringizeDateInFixUTCTimeStampFormat());
        FixMessageBuilder.appendFixString(messageBody, FixPossDupFlagField.TagIDAsChars,     FixPossDupFlagField.string_PossibleDuplicate);
        FixMessageBuilder.appendFixString(messageBody, FixOrigSendingTimeField.TagIDAsChars, body.substring(beforeSendingTime+3, afterSendingTime));

        messageBody.append(body.substring(afterSendingTime + 1, body.length() - FixFieldIF.FIX_TAG_10_LENGTH));

        FixMessageBuilder.appendFixString(prefix, FixBeginStringField.TagIDAsChars,      fixSession.fixMessageFactory.getFixVersionAsString());
        FixMessageBuilder.appendFixString(prefix, FixBodyLengthField.TagIDAsChars,       messageBody.length());

        messageBody.linkToFront(prefix);

        FixMessageBuilder.appendFixString(messageBody, FixCheckSumField.TagIDAsChars, StringHelper.numbers(fixChecksumFastCharacterWriterVisitor.calculateChecksum(messageBody)));

        if (BitHelper.isBitMaskSet(fixSession.debugFlags, FixSessionDebugIF.SESSION_DECODE_SENT_MESSAGES))
        {
            try
            {
                FixSessionDebugger.dumpFixMessage(Thread.currentThread().getName() + " FIXDECODE SEND ", messageBody, fixSession.fixMessageFactory, FixSessionDebugIF.SESSION_DECODE_SENT_MESSAGES | FixSessionDebugIF.SESSION_SHOW_PREDECODE_MESSAGE_DATA);
            }
            catch (Exception ex)
            {

            }
        }

        return internalSend(msgType, messageBody, false);
*/
    }

    public boolean isValidMDReqID(String mdReqID)
    {
        return fixSession.marketDataFutureExecutionMap.containsKey(mdReqID);
    }

    public void incSentMDReqID(String mdReqID)
    {
        fixSession.fixSessionInstrumentation.incSentMDReqID(mdReqID);
    }

    public void run()
    {
        FixSessionWriterCommand         heartBeatFixSessionWriterCommand = null;
        FixSessionWriterCommand         scratchFixSessionWriterCommand   = FixSessionWriterCommand.createSend(null, null);
        FixMessageBuilder               scratchFixMessageBuilder         = new FixMessageBuilder(fixSessionWriterScratchFastCharacterWriter);
        Object                          object;
        long                            millisUntilNextHeartBeat;
        long                            lastSentTime;
        long                            heartBeatIntervalInMillis;
        FixSessionWriterCommand         fixSessionWriterCommand;
        CfixMarketDataMapperIF          cfixMarketDataMapper;
        int                             dequeueProblems = 0;
        String                          mdReqID;
        OverlayPolicyMarketDataHolderIF cfixOverlayPolicyMarketDataHolder = new OverlayPolicyMarketDataHolder();
        BitArrayIF                      bitArray;
        int                             i;
        int                             overlaid = 0;
        CalculateSizeVisitor            calculateSizeVisitor = new CalculateSizeVisitor();

        try
        {
//            cfixMarketDataIncrementalMapper.initialize(fixSession.cfixSessionManager, fixSession.propertyPrefix, fixSession.sessionProperties);
            cfixMarketDataFullRefreshMapper.initialize(fixSession.cfixSessionManager, fixSession.propertyPrefix, fixSession.sessionProperties);
        }
        catch (Exception ex)
        {
            Log.exception(ex);
        }

        while (fixSession.fixSocketAdapter.isConnected() && !askedToTerminate)
        {
            object                            = null;
            fixSessionWriterCommand           = null;

            calculateSizeVisitor.size = 0;

            try
            {
                if (justDequeueHighPriority)
                {
                    if (BitHelper.isBitMaskSet(fixSession.debugFlags, FixSessionDebugIF.WRITER_SHOW_EVENT_CHANNEL))
                    {
                        Log.information(Thread.currentThread().getName() + " DequeueHP(~)");
                    }

                    object = fixEventChannel.dequeueHighPriority();
                }
                else if (fixSession.fixSessionInformation.getHeartBeatInterval() > 0)
                {
                    heartBeatIntervalInMillis = fixSession.fixSessionInformation.getHeartBeatInterval() * 1000L;

                    lastSentTime = fixSession.fixSocketAdapter.getLastSentTime();

                    millisUntilNextHeartBeat = heartBeatIntervalInMillis - (System.currentTimeMillis() - lastSentTime);

                    if (millisUntilNextHeartBeat < 0)
                    {
                        if (lastSentTime == 0)
                        {
                            millisUntilNextHeartBeat = heartBeatIntervalInMillis;
                        }
                        else
                        {
                            millisUntilNextHeartBeat = 1; // this should timeout and cause us to send a heartbeat or a valid message
                        }
                    }
                    else if (millisUntilNextHeartBeat > heartBeatIntervalInMillis)
                    {
                        millisUntilNextHeartBeat = heartBeatIntervalInMillis;
                    }

                    if (BitHelper.isBitMaskSet(fixSession.debugFlags, FixSessionDebugIF.WRITER_SHOW_EVENT_CHANNEL))
                    {
                        Log.information(Thread.currentThread().getName() + " DequeueNP(" + millisUntilNextHeartBeat + " ms)");
                    }

                    object = fixEventChannel.dequeue(millisUntilNextHeartBeat, TIMEDOUT);
                }
                else
                {
                    if (BitHelper.isBitMaskSet(fixSession.debugFlags, FixSessionDebugIF.WRITER_SHOW_EVENT_CHANNEL))
                    {
                        Log.information(Thread.currentThread().getName() + " DequeueNP(~)");
                    }

                    object = fixEventChannel.dequeue();
                }
            }
            catch (Exception ex)
            {
                Log.exception(ex);

                if (++dequeueProblems > 5)
                {
                    if (fixSession.disconnectReasonText == null)
                    {
                        fixSession.disconnectReasonText = "INTERNAL QUEUEING PROBLEM";
                    }

                    terminate(true);

                    fixSession.cleanup();

                    break;
                }

                continue;
            }

            dequeueProblems = 0;

            if (object == null)
            {
                continue;
            }

            try
            {
                scratchFixMessageBuilder.clear();

                calculateSizeVisitor.clear();

                if (BitHelper.isBitMaskSet(fixSession.debugFlags, FixSessionDebugIF.WRITER_SHOW_EVENT_CHANNEL))
                {
                    if (!calculateSizeVisitor.wasRun()) fixEventChannel.acceptVisitor(calculateSizeVisitor);
                    Log.information(Thread.currentThread().getName() + " Dequeued QueueSize(" + calculateSizeVisitor.size + ") " + object);
                }

                if (object == TIMEDOUT)
                {
                    if ((System.currentTimeMillis() - fixSession.fixSocketAdapter.getLastSentTime()) >= (fixSession.fixSessionInformation.getHeartBeatInterval() * 1000L))
                    {
                        if (heartBeatFixSessionWriterCommand == null)
                        {
                            heartBeatFixSessionWriterCommand = fixSession.buildFixSessionWriterCommandForHeartBeatMessage();
                        }

                        processCommand(heartBeatFixSessionWriterCommand);

                        continue;
                    }
                }

                if (object instanceof FixMarketDataRejectStruct)
                {
                    FixMarketDataRejectStruct fixMarketDataRejectStruct = (FixMarketDataRejectStruct) object;

                    scratchFixSessionWriterCommand.command             = FixSessionWriterCommand.SEND;
                    scratchFixSessionWriterCommand.msgSeqNum           = FixSessionWriterCommand.USE_CURRENT_SEQ_NUM;
                    scratchFixSessionWriterCommand.fastCharacterWriter = fixSessionWriterScratchFastCharacterWriter;
                    scratchFixSessionWriterCommand.msgType             = FixMarketDataRequestRejectMessage.MsgTypeAsChars;

                    scratchFixMessageBuilder.clear();

                    mdReqID = fixMarketDataRejectStruct.getMdReqID();

                    fixSession.fixSessionInstrumentation.incRejectedMDReqID(mdReqID);

                    if (fixMarketDataRejectStruct.getTargetCompID() != null)
                    {
                        scratchFixMessageBuilder.append(FixOnBehalfOfCompIDField.TagIDAsChars, fixMarketDataRejectStruct.getTargetCompID());
                    }

                    scratchFixMessageBuilder.append(FixMDReqIDField.TagIDAsChars,        mdReqID);
                    scratchFixMessageBuilder.append(FixMDReqRejReasonField.TagIDAsChars, fixMarketDataRejectStruct.getRejectReason());
                    scratchFixMessageBuilder.append(FixTextField.TagIDAsChars,           fixMarketDataRejectStruct.getText());

                    if (fixMarketDataRejectStruct.getRejectReason() != FixMarketDataRejectStruct.DuplicateMdReqId)
                    {
                        fixSession.marketDataFutureExecutionMap.remove(mdReqID);
                    }

                    if (processCommand(scratchFixSessionWriterCommand) == SEND_FAILED)
                    {
                        break;
                    }

                    fixSession.fixSessionInstrumentation.incSentMDReqID(mdReqID);

                    continue;
                }

                if (object instanceof OverlayPolicyMarketDataListIF)
                {
                    if (!calculateSizeVisitor.wasRun()) fixEventChannel.acceptVisitor(calculateSizeVisitor);

                    OverlayPolicyMarketDataListIF cfixOverlayPolicyMarketDataList = (OverlayPolicyMarketDataListIF) object;

                    cfixOverlayPolicyMarketDataList.remove(cfixOverlayPolicyMarketDataHolder);

                    cfixMarketDataMapper                               = cfixMarketDataFullRefreshMapper;

                    scratchFixSessionWriterCommand.command             = FixSessionWriterCommand.SEND;
                    scratchFixSessionWriterCommand.msgSeqNum           = FixSessionWriterCommand.USE_CURRENT_SEQ_NUM;
                    scratchFixSessionWriterCommand.fastCharacterWriter = fixSessionWriterScratchFastCharacterWriter;
                    scratchFixSessionWriterCommand.msgType             = cfixMarketDataMapper.getMsgTypeAsChars();

                    cfixMarketDataMapper.reset(cfixOverlayPolicyMarketDataHolder, this);

                    if (true)
                    {
                        overlaid = 0;
                        bitArray = cfixOverlayPolicyMarketDataHolder.getOverlaid();
                        for (i = bitArray.size(); --i >= 0; )
                        {
                            if (bitArray.timesChanged(i) > 1)
                            {
                                overlaid++;
                            }
                        }

                        if (overlaid > 0)
                        {
                            fixEventChannel.incOverlaid(overlaid);
                            Log.information(Thread.currentThread().getName() + " OVERLAID: mdReqID(" + cfixOverlayPolicyMarketDataHolder.getMdReqID() + ") = " + overlaid);
                        }
                    }

                    while (true)
                    {
                        scratchFixMessageBuilder.clear();

                        if (cfixMarketDataMapper.build(scratchFixMessageBuilder) == false)
                        {
                            break;
                        }

                        fixSession.fixSessionInstrumentation.incSentMDReqID(cfixOverlayPolicyMarketDataHolder.getMdReqID());

                        if (calculateSizeVisitor.size > 0) // TODO: Figure out how to do this for Incremental
                        {
                            scratchFixMessageBuilder.append(FixCboeApplicationQueueDepthField.TagIDAsChars, calculateSizeVisitor.size);
                        }

                        if (processCommand(scratchFixSessionWriterCommand) == SEND_FAILED)
                        {
                            break;
                        }
                    }

                    continue;
                }

                if (object instanceof FixSessionWriterCommand)
                {
                    fixSessionWriterCommand = (FixSessionWriterCommand) object;

                    processCommand(fixSessionWriterCommand);

                    continue;
                }

                if (object instanceof FixMessageIF)
                {
                    FixMessageIF fixMessage = (FixMessageIF) object;

                    scratchFixSessionWriterCommand.command             = FixSessionWriterCommand.SEND;
                    scratchFixSessionWriterCommand.msgSeqNum           = FixSessionWriterCommand.USE_CURRENT_SEQ_NUM;
                    scratchFixSessionWriterCommand.fastCharacterWriter = fixSessionWriterScratchFastCharacterWriter;
                    scratchFixSessionWriterCommand.msgType             = fixMessage.getMsgTypeAsChars();

                    scratchFixMessageBuilder.clear();

                    fixMessage.accept(scratchFixMessageBuilder);

                    processCommand(scratchFixSessionWriterCommand);

                    continue;
                }

                Log.alarm(Thread.currentThread().getName() + " Can't Process Class='" + ClassHelper.getClassName(object) + "' object=" + object);
            }
            catch (Exception ex)
            {
                Log.exception(ex);
            }
        }

        fixSession.cleanup();

        fixSession.sessionWriterTerminatedLatch.release();
    }

    public boolean processCommand(FixSessionWriterCommand fixSessionWriterCommand) throws Exception
    {
        switch (fixSessionWriterCommand.command)
        {
            case FixSessionWriterCommand.RESEND:
                return rebuildAndResend(fixSessionWriterCommand.fastCharacterWriter == null ? FastCharacterWriter.EMPTY_FAST_CHARACTER_WRITER : fixSessionWriterCommand.fastCharacterWriter);

            case FixSessionWriterCommand.SEND:
                if (fixSessionWriterCommand.msgSeqNum == FixSessionWriterCommand.USE_CURRENT_SEQ_NUM)
                {
                    return buildAndSend(fixSessionWriterCommand.msgType, fixSessionWriterCommand.fastCharacterWriter == null ? FastCharacterWriter.EMPTY_FAST_CHARACTER_WRITER : fixSessionWriterCommand.fastCharacterWriter);
                }
                return buildAndSend(fixSessionWriterCommand.msgSeqNum, fixSessionWriterCommand.msgType, fixSessionWriterCommand.fastCharacterWriter == null ? FastCharacterWriter.EMPTY_FAST_CHARACTER_WRITER : fixSessionWriterCommand.fastCharacterWriter);

            case FixSessionWriterCommand.HIGH_PRIORITY_BEGIN:
                justDequeueHighPriority = true;
                break;

            case FixSessionWriterCommand.HIGH_PRIORITY_END:
                justDequeueHighPriority = false;
                break;

            case FixSessionWriterCommand.HIGH_PRIORITY_TERMINATE:
                askedToTerminate = true;
                break;
        }

        return SENT_SUCCESSFULLY;
    }
}
