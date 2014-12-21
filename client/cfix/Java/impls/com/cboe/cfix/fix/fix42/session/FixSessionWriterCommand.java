package com.cboe.cfix.fix.fix42.session;

/**
 * FixSessionWriterCommand.java
 *
 * @author Dmitry Volpyansky
 *
 */

import com.cboe.client.util.*;

public class FixSessionWriterCommand
{
    protected int                 command;
    protected FastCharacterWriter fastCharacterWriter;
    protected char[]              msgType;
    protected int                 msgSeqNum;

    public static final int USE_CURRENT_SEQ_NUM     = 0;

    public static final int HIGH_PRIORITY_BEGIN     = 1;
    public static final int HIGH_PRIORITY_END       = 2;
    public static final int HIGH_PRIORITY_TERMINATE = 3;
    public static final int SEND                    = 4;
    public static final int RESEND                  = 5;

    public final static FixSessionWriterCommand highPriorityBegin     = new FixSessionWriterCommand().buildHighPriorityBegin();
    public final static FixSessionWriterCommand highPriorityEnd       = new FixSessionWriterCommand().buildHighPriorityEnd();
    public final static FixSessionWriterCommand highPriorityTerminate = new FixSessionWriterCommand().buildHighPriorityTerminate();

    public static FixSessionWriterCommand createHighPriorityBegin()
    {
        return highPriorityBegin;
    }

    public static FixSessionWriterCommand createHighPriorityEnd()
    {
        return highPriorityEnd;
    }

    public static FixSessionWriterCommand createHighPriorityTerminate()
    {
        return highPriorityTerminate;
    }

    public static FixSessionWriterCommand createSend(FastCharacterWriter fastCharacterWriter, char[] msgType)
    {
        return new FixSessionWriterCommand().buildSend(fastCharacterWriter, msgType);
    }

    public static FixSessionWriterCommand createSend(FastCharacterWriter fastCharacterWriter, char[] msgType, int msgSeqNum)
    {
        return new FixSessionWriterCommand().buildSend(fastCharacterWriter, msgType, msgSeqNum);
    }

    public static FixSessionWriterCommand createResend(FastCharacterWriter fastCharacterWriter)
    {
        return new FixSessionWriterCommand().buildResend(fastCharacterWriter);
    }

    public FixSessionWriterCommand buildHighPriorityTerminate()
    {
        this.command = HIGH_PRIORITY_TERMINATE;

        return this;
    }

    public FixSessionWriterCommand buildHighPriorityBegin()
    {
        this.command = HIGH_PRIORITY_BEGIN;

        return this;
    }

    public FixSessionWriterCommand buildHighPriorityEnd()
    {
        this.command = HIGH_PRIORITY_END;

        return this;
    }

    public FixSessionWriterCommand buildSend(FastCharacterWriter fastCharacterWriter, char[] msgType)
    {
        this.command             = SEND;
        this.fastCharacterWriter = fastCharacterWriter;
        this.msgType             = msgType;

        return this;
    }

    public FixSessionWriterCommand buildSend(FastCharacterWriter fastCharacterWriter, char[] msgType, int msgSeqNum)
    {
        this.command             = SEND;
        this.fastCharacterWriter = fastCharacterWriter;
        this.msgType             = msgType;
        this.msgSeqNum           = msgSeqNum;

        return this;
    }

    public FixSessionWriterCommand buildResend(FastCharacterWriter fastCharacterWriter)
    {
        this.command             = RESEND;
        this.fastCharacterWriter = fastCharacterWriter;

        return this;
    }

    public char[] getMsgType()
    {
        return msgType;
    }

    public FixSessionWriterCommand setMsgType(char[] msgType)
    {
        this.msgType = msgType;

        return this;
    }

    public int getMsgSeqNum()
    {
        return msgSeqNum;
    }

    public FixSessionWriterCommand setMsgSeqNum(int msgSeqNum)
    {
        this.msgSeqNum = msgSeqNum;

        return this;
    }

    public FastCharacterWriter getFastCharacterWriter()
    {
        return fastCharacterWriter;
    }

    public int getCommand()
    {
        return command;
    }

    public String toString()
    {
        switch (command)
        {
            case HIGH_PRIORITY_BEGIN:       return "Command[HIGH_PRIORITY_BEGIN]";
            case HIGH_PRIORITY_END:         return "Command[HIGH_PRIORITY_END]";
            case SEND:                      return "Command[SEND]";
            case HIGH_PRIORITY_TERMINATE:   return "Command[HIGH_PRIORITY_TERMINATE]";
            case RESEND:                    return "Command[RESEND]";
        }

        return "Command[" + command + "] " + fastCharacterWriter;
    }
}
