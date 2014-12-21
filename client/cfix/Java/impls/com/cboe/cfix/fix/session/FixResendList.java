package com.cboe.cfix.fix.session;

/**
 * FixResendList.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.util.*;

import com.cboe.cfix.interfaces.*;
import com.cboe.cfix.util.*;
import com.cboe.client.util.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;

public final class FixResendList
{
    protected List       sentList;
    protected Set        msgTypeSet = new HashSet();
    protected IntIntPair intIntPair = new IntIntPair();
    protected int        debugFlags = FixSessionDebugIF.DEBUG_OFF;

    public void initialize(PropertiesHelper propertiesHelper) throws Exception
    {
        String s = propertiesHelper.getProperty("cfix.fixSession.resendList.storedMsgTypes");
        if (s != null)
        {
            for (StringTokenizer tokenizer = new StringTokenizer(s); tokenizer.hasMoreTokens();)
            {
                msgTypeSet.add(tokenizer.nextToken());
            }
        }

        reset();
    }

    public int setDebugFlags(int debugFlags)
    {
        int oldDebugFlags = this.debugFlags;

        this.debugFlags = debugFlags;

        return oldDebugFlags;
    }

    public void reset()
    {
        sentList = new ArrayList();
    }

    public void storeSentMessage(int messageMsgSeqNum, String messageMsgType, FastCharacterWriter fastCharacterWriter)
    {
        if (!msgTypeSet.isEmpty() && msgTypeSet.contains(messageMsgType))
        {
            IntFastCharacterWriterPair pair = new IntFastCharacterWriterPair(messageMsgSeqNum, (FastCharacterWriter) fastCharacterWriter.clone());

            if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.RESEND_LIST_SHOW_INSERT)) {Log.information("RESENDLIST<" + messageMsgSeqNum + ">: New Message(" + messageMsgSeqNum + "," + fastCharacterWriter + ")");}

            sentList.add(pair);
        }
    }

    /**
     *  INVARIANT: atLeastMsgSeqNum < highestSentMsgSeqNum
     */
    public FirstIntIF getStoredMessage(int atLeastMsgSeqNum, int atMostMsgSeqNum)
    {
        if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.RESEND_LIST_SHOW_SEARCH)) {Log.information("getStoredMessage ENTER(" + atLeastMsgSeqNum + "," + atMostMsgSeqNum + ")");}

        FirstIntIF firstInt;

        int nextHighestMsgSeqNum = 0;

        for (int i = sentList.size() - 1; i >= 0; i--)
        {
            firstInt = (FirstIntIF) sentList.get(i);

            // if stored message's sequence number is greater than our minimum, keep descending
            if (firstInt.getFirst() > atLeastMsgSeqNum)
            {
                //if (BitHelper.isBitMaskSet(debugFlags, DEBUG_SHOW_RESEND_LIST_SEARCH)) {Log.information("getStoredMessage DESCENDING(" + firstInt.getFirst() + " > " + atLeastMsgSeqNum + ")");}
                nextHighestMsgSeqNum = firstInt.getFirst();
                continue;
            }

            // if stored message's sequence number is equal to our minimum, then return this message to be resent
            if (firstInt.getFirst() == atLeastMsgSeqNum)
            {
                if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.RESEND_LIST_SHOW_SEARCH))
                {
                    Log.information("getStoredMessage MATCHED(" + atLeastMsgSeqNum + ")");
                }

                return firstInt;
            }

            break;
        }

        if (sentList.size() > 0)
        {
            // if stored message's sequence number is less than our minimum, then we need to gap fill

            //   gapfill to next message
            if (atMostMsgSeqNum >= nextHighestMsgSeqNum)
            {
                if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.RESEND_LIST_SHOW_SEARCH)) {Log.information("getStoredMessage GAPFILL(" + atLeastMsgSeqNum + "," + nextHighestMsgSeqNum + ")");}

                return intIntPair.reset(atLeastMsgSeqNum, nextHighestMsgSeqNum);
            }
        }

        if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.RESEND_LIST_SHOW_SEARCH)) {Log.information("getStoredMessage EMPTYLIST(" + atLeastMsgSeqNum + "," + atMostMsgSeqNum + ")");}

        return intIntPair.reset(atLeastMsgSeqNum, atMostMsgSeqNum);
    }

    public String toString()
    {
        StringBuilder buffer = new StringBuilder(sentList.size() * 32);
        FirstIntIF   firstInt;

        for (int i = 0; i < sentList.size(); i++)
        {
            firstInt = (FirstIntIF) sentList.get(i);

            if (firstInt instanceof IntIntPair)
            {
                buffer.append("RESENDLIST[").append(i).append("/").append(sentList.size())
                      .append("] GapFill(").append((firstInt).getFirst()).append(",")
                      .append(((IntIntPair) firstInt).getSecond()).append(")");
            }
            else if (firstInt instanceof IntFastCharacterWriterPair)
            {
                buffer.append("RESENDLIST[").append(i).append("/").append(sentList.size())
                      .append("] Message(").append((firstInt).getFirst()).append(",")
                      .append(((IntFastCharacterWriterPair) firstInt).getFastCharacterWriter()).append(")");
            }

            buffer.append("\n");
        }

        return buffer.toString();
    }
}