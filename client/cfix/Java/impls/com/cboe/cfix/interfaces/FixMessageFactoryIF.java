package com.cboe.cfix.interfaces;

/**
 * FixMessageFactoryIF.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.util.*;

public interface FixMessageFactoryIF
{
    public void         initialize(String propertyPrefix, Properties properties) throws Exception;
    public FixMessageIF createFixMessageFromMsgType(char msgType);
    public FixMessageIF createFixMessageFromMsgType(char[] msgType);
    public FixMessageIF createFixMessageFromMsgType(String msgType);
    public String       getMsgType(char[] msgType);
    public String       getFixVersionAsString();
    public char[]       getFixVersionAsChars();
    public FixMessageIF createFixHeartBeatMessage();
    public FixMessageIF createFixLogonMessage();
    public FixMessageIF createFixTestRequestMessage();
    public FixMessageIF createFixResendRequestMessage();
    public FixMessageIF createFixRejectMessage();
    public FixMessageIF createFixSequenceResetMessage();
    public FixMessageIF createFixLogoutMessage();
    public FixMessageIF createFixAdvertisementMessage();
    public FixMessageIF createFixIndicationOfInterestMessage();
    public FixMessageIF createFixNewsMessage();
    public FixMessageIF createFixEmailMessage();
    public FixMessageIF createFixQuoteRequestMessage();
    public FixMessageIF createFixQuoteMessage();
    public FixMessageIF createFixMassQuoteMessage();
    public FixMessageIF createFixQuoteCancelMessage();
    public FixMessageIF createFixQuoteStatusRequestMessage();
    public FixMessageIF createFixQuoteAcknowledgementMessage();
    public FixMessageIF createFixMarketDataRequestMessage();
    public FixMessageIF createFixMarketDataSnapshotFullRefreshMessage();
    public FixMessageIF createFixMarketDataIncrementalRefreshMessage();
    public FixMessageIF createFixMarketDataRequestRejectMessage();
    public FixMessageIF createFixSecurityDefinitionRequestMessage();
    public FixMessageIF createFixSecurityDefinitionMessage();
    public FixMessageIF createFixSecurityStatusRequestMessage();
    public FixMessageIF createFixSecurityStatusMessage();
    public FixMessageIF createFixTradingSessionStatusRequestMessage();
    public FixMessageIF createFixTradingSessionStatusMessage();
    public FixMessageIF createFixNewOrderSingleMessage();
    public FixMessageIF createFixExecutionReportMessage();
    public FixMessageIF createFixDontKnowTradeMessage();
    public FixMessageIF createFixOrderCancelReplaceRequestMessage();
    public FixMessageIF createFixOrderCancelRequestMessage();
    public FixMessageIF createFixOrderCancelRejectMessage();
    public FixMessageIF createFixOrderStatusRequestMessage();
    public FixMessageIF createFixAllocationMessage();
    public FixMessageIF createFixAllocationAckMessage();
    public FixMessageIF createFixSettlementInstructionsMessage();
    public FixMessageIF createFixNewOrderListMessage();
    public FixMessageIF createFixListStatusMessage();
    public FixMessageIF createFixListExecuteMessage();
    public FixMessageIF createFixListCancelRequestMessage();
    public FixMessageIF createFixListStatusRequestMessage();
    public FixMessageIF createFixBusinessMessageRejectMessage();
}
