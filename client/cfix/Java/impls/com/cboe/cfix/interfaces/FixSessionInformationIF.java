package com.cboe.cfix.interfaces;

/**
 * FixSessionInformationIF.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.util.*;

public interface FixSessionInformationIF
{
    public FixSessionInformationIF initialize(FixSessionIF fixSession) throws Exception;
    public int                     getNextReceiveMsgSeqNum();
    public int                     setNextReceiveMsgSeqNum(int receiveMsgSeqNum);
    public int                     getNextSendMsgSeqNum();
    public int                     getSendMsgSeqNum();
    public int                     setSendMsgSeqNum(int sendMsgSeqNum);
    public Collection              copyConnectionInformation(Collection collection);
    public int                     getPort();
    public boolean                 isCurrentlyConnected();
    public Collection              getSuppressedRecvFixMsgTypes();
    public boolean                 isSuppressedRecvFixMsgType(String msgType);
    public Collection              getSuppressedSentFixMsgTypes();
    public boolean                 isSuppressedSentFixMsgType(String msgType);
}