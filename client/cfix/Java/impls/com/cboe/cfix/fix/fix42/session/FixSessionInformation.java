package com.cboe.cfix.fix.fix42.session;

/**
 * FixSessionInformation.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.util.*;

import com.cboe.cfix.fix.fix42.generated.fields.*;
import com.cboe.cfix.fix.session.*;
import com.cboe.cfix.interfaces.*;
import com.cboe.client.util.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.util.*;

public class FixSessionInformation implements FixSessionInformationIF
{
    public int                  port;
    public boolean              currentlyConnected;
    public int                  heartBeatInterval                 = 30;
    public int                  maxOutstandingTestRequests        = 3;
    public int                  maxSendingTimeDifferenceInSeconds = 120;
    public boolean              resetOnReconnection;
    public int                  outstandingResendRequestMsgSeqNum;
    public int                  msgSeqNumThatMovesUsBackIntoSequence;
    public List                 outstandingTestRequestList = new ArrayList();
    public boolean              skipCompIdValidation;
    public FixSenderCompIDField fieldSenderCompID;
    public FixTargetCompIDField fieldTargetCompID;
    public int                  sendMsgSeqNum;
    public int                  receiveMsgSeqNum;
    public int                  debugFlags = FixSessionDebugIF.DEBUG_OFF;
    public FixResendList        fixResendList;
    public Set                  suppressSentFixMsgTypeLoggingSet = new HashSet();
    public Set                  suppressRecvFixMsgTypeLoggingSet = new HashSet();
    public boolean              notInitialized = true;
    public List                 connectionInformation = new ArrayList();
    public PropertiesHelper     propertiesHelper;

    public static final String MsgTypesAsString[] = new String[256];

    static
    {
        for (int i = 0; i < MsgTypesAsString.length; i++)
        {
            MsgTypesAsString[i] = String.valueOf((char) i);
        }
    }

    public FixSessionInformationIF initialize(FixSessionIF fixSession) throws Exception
    {
        this.port = fixSession.getPort();

        this.currentlyConnected = true;

        if (notInitialized)
        {
            propertiesHelper = fixSession.getPropertiesHelper();

            notInitialized                    = false;

            fieldSenderCompID                 = FixSenderCompIDField.create(fixSession.getSenderCompID());
            fieldTargetCompID                 = FixTargetCompIDField.create(fixSession.getTargetCompID());
            resetOnReconnection               = propertiesHelper.getPropertyBoolean("cfix.fixSession.resetOnReconnection",               "false");
            skipCompIdValidation              = propertiesHelper.getPropertyBoolean("cfix.fixSession.skipCompIdValidation",              "false");
            heartBeatInterval                 = propertiesHelper.getPropertyInt(    "cfix.fixSession.heartBeatInterval",                 "30");
            maxOutstandingTestRequests        = propertiesHelper.getPropertyInt(    "cfix.fixSession.maxOutstandingTestRequests",        "5");
            maxSendingTimeDifferenceInSeconds = propertiesHelper.getPropertyInt(    "cfix.fixSession.maxSendingTimeDifferenceInSeconds", "1200000000");

            if (fieldSenderCompID == null)
            {
                Log.alarm(Thread.currentThread().getName() + " Misconfigured CFIX Property: NULL SenderCompID");
            }

            if (fieldTargetCompID == null)
            {
                Log.alarm(Thread.currentThread().getName() + " Misconfigured CFIX Property: NULL TargetCompID");
            }

            fixResendList = new FixResendList();

            try
            {
                fixResendList.initialize(propertiesHelper);
            }
            catch (Exception ex)
            {

            }

            String debugSuppressMsgType = propertiesHelper.getProperty("cfix.fixSession.suppressSentFixMsgTypes");
            if (debugSuppressMsgType != null && debugSuppressMsgType.length() > 0)
            {
                for (StringTokenizer tokenizer = new StringTokenizer(debugSuppressMsgType, ", "); tokenizer.hasMoreTokens();)
                {
                    suppressSentFixMsgTypeLoggingSet.add(tokenizer.nextToken());
                }
            }

            debugSuppressMsgType = propertiesHelper.getProperty("cfix.fixSession.suppressRecvFixMsgTypes");
            if (debugSuppressMsgType != null && debugSuppressMsgType.length() > 0)
            {
                for (StringTokenizer tokenizer = new StringTokenizer(debugSuppressMsgType, ", "); tokenizer.hasMoreTokens();)
                {
                    suppressRecvFixMsgTypeLoggingSet.add(tokenizer.nextToken());
                }
            }
        }
        else
        {
            if (resetOnReconnection)
            {
                reset(true);
                fixResendList.reset();
            }
            else
            {
                reset(false);
            }
        }

        return this;
    }

    public Collection getSuppressedRecvFixMsgTypes()
    {
        return suppressRecvFixMsgTypeLoggingSet;
    }

    public boolean isSuppressedRecvFixMsgType(String msgType)
    {
        return suppressRecvFixMsgTypeLoggingSet.contains(msgType);
    }

    public Collection getSuppressedSentFixMsgTypes()
    {
        return suppressSentFixMsgTypeLoggingSet;
    }

    public boolean isSuppressedSentFixMsgType(String msgType)
    {
        return suppressSentFixMsgTypeLoggingSet.contains(msgType);
    }

    public void reset(boolean full)
    {
        if (full)
        {
            sendMsgSeqNum    = 0;
            receiveMsgSeqNum = 1;
        }

        msgSeqNumThatMovesUsBackIntoSequence = 0;
        outstandingResendRequestMsgSeqNum    = 0;
        outstandingTestRequestList           = new ArrayList();
    }

    public int getPort()
    {
        return port;
    }

    public boolean isCurrentlyConnected()
    {
        return currentlyConnected;
    }

    public int setDebugFlags(int debugFlags)
    {
        int oldDebugFlags = this.debugFlags;

        this.debugFlags = debugFlags;

        fixResendList.setDebugFlags(debugFlags);

        return oldDebugFlags;
    }

    public FixResendList getFixResendList()
    {
        return fixResendList;
    }

    public void addOutstandingTestRequest(String testID)
    {
        outstandingTestRequestList.add(testID);
    }

    public boolean removeOutstandingTestRequest(String testID)
    {
        return outstandingTestRequestList.remove(testID);
    }

    public List getOutstandingTestRequestList()
    {
        return outstandingTestRequestList;
    }

    public int getMsgSeqNumThatMovesUsBackIntoSequence()
    {
        return msgSeqNumThatMovesUsBackIntoSequence;
    }

    public int setMsgSeqNumThatMovesUsBackIntoSequence(int msgSeqNumThatMovesUsBackIntoSequence)
    {
        return this.msgSeqNumThatMovesUsBackIntoSequence = msgSeqNumThatMovesUsBackIntoSequence;
    }

    public int getNextReceiveMsgSeqNum()
    {
        return receiveMsgSeqNum;
    }

    public int setNextReceiveMsgSeqNum(int receiveMsgSeqNum)
    {
        return this.receiveMsgSeqNum = receiveMsgSeqNum;
    }

    public int incReceiveMsgSeqNum()
    {
        return ++receiveMsgSeqNum;
    }

    public int getNextSendMsgSeqNum()
    {
        return sendMsgSeqNum + 1;
    }

    public int getSendMsgSeqNum()
    {
        return sendMsgSeqNum;
    }

    public int setSendMsgSeqNum(int sendMsgSeqNum)
    {
        return this.sendMsgSeqNum = sendMsgSeqNum;
    }

    public int incSendMsgSeqNum()
    {
        return ++sendMsgSeqNum;
    }

    public boolean isValidSenderCompID(String senderCompID)
    {
        if (skipCompIdValidation)
        {
            return true;
        }

        try
        {
            return fieldSenderCompID.getValue().equals(senderCompID);
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    public FixSenderCompIDField getSenderCompIDField()
    {
        return fieldSenderCompID;
    }

    public String getSenderCompID()
    {
        try
        {
            return fieldSenderCompID.getValue();
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public char[] getSenderCompIDAsChars()
    {
        try
        {
            return fieldSenderCompID.getValueAsChars();
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public boolean isValidTargetCompID(String targetCompID)
    {
        if (skipCompIdValidation)
        {
            return true;
        }

        try
        {
            return fieldTargetCompID.getValue().equals(targetCompID);
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    public FixTargetCompIDField getTargetCompIDField()
    {
        return fieldTargetCompID;
    }

    public String getTargetCompID()
    {
        try
        {
            return fieldTargetCompID.getValue();
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public char[] getTargetCompIDAsChars()
    {
        try
        {
            return fieldTargetCompID.getValueAsChars();
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public void setHeartBeatInterval(int heartBeatInterval)
    {
        this.heartBeatInterval = heartBeatInterval;
    }

    public void setHeartBeatInterval(FixHeartBtIntField fieldHeartBtInt)
    {
        if (fieldHeartBtInt != null && propertiesHelper.getPropertyBoolean("cfix.fixSession.unchangeableHeartBeatInterval", "false") == false)
        {
            try
            {
                heartBeatInterval = fieldHeartBtInt.intValue();
            }
            catch (Exception ex)
            {

            }
        }
    }

    public int getHeartBeatInterval()
    {
        return heartBeatInterval;
    }

    public boolean isResendRequestCurrentlyOutstandingFor(int msgSeqNum)
    {
        return outstandingResendRequestMsgSeqNum > 0 && outstandingResendRequestMsgSeqNum < msgSeqNum;
    }

    public void setResendRequestOutstandingFor(int msgSeqNum)
    {
        outstandingResendRequestMsgSeqNum = msgSeqNum;
    }

    public int getResendRequestOutstandingFor()
    {
        return outstandingResendRequestMsgSeqNum;
    }

    public synchronized Collection copyConnectionInformation(Collection collection)
    {
        collection.addAll(connectionInformation);

        return collection;
    }
}
