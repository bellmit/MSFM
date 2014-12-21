//
// -----------------------------------------------------------------------------------
// Source file: MessageElement.java
//
// PACKAGE: com.cboe.interfaces.presentation.omt;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.omt;

import com.cboe.idl.cmiUtil.DateTimeStruct;

import com.cboe.interfaces.presentation.util.CBOEId;

/**
 * A common interface for all order-related and non-order-related (informational) OMT messages.
 */
@SuppressWarnings({"BooleanMethodNameMustStartWithQuestion"})
public interface MessageElement
{
    //public constants
    short NO_ROUTING_REASON_AVAILABLE = -1;
    String NO_ROUTING_DESCRIPTION_AVAILABLE = "";

    short NO_SOURCE_AVAILABLE = -1;
    String NO_SOURCE_DESCRIPTION_AVAILABLE = "";

    String NO_SESSION_NAME_AVAILABLE = "";
    long NO_MESSAGE_ID_AVAILABLE = -1;
    DateTimeStruct NO_ROUTE_TIME_AVAILABLE = new DateTimeStruct();
    int NO_PRODUCT_KEY_AVAILABLE = -1;

    String NO_CLASS_POST_STATION_AVAILABLE = "";
    String NOT_AVAILABLE = "";

    enum MessageType
    {
		ORDER_ACCEPTED,
		ORDER_CANCELED,
		ORDER_CANCEL_REPLACED,
		ORDER_FILL_REPORT_REJECT,
		LINKAGE_CANCEL_REPORT,
		LINKAGE_FILL_REPORT,
		TEXT,
		ORDER_QUERY,
		SINGLE_ORDER_QUERY,
		LIGHT_ORDER,
		TRADE_NOTIFICATION,
		FILL_REPORT_DROP_COPY,
		CANCEL_REPORT_DROP_COPY,
		MANUAL_ORDER_TIMEOUT,
		MANUAL_FILL_TIMEOUT;

        @SuppressWarnings({"MethodWithMultipleReturnPoints"})
        @Override
        public String toString()
        {
            switch(this)
            {
                case ORDER_ACCEPTED: return "Order";
                case ORDER_CANCELED: return "Cancel Request";
                case ORDER_CANCEL_REPLACED: return "Cancel Replace Request";
                case ORDER_FILL_REPORT_REJECT: return "Fill Reject";
                case LINKAGE_CANCEL_REPORT: return "Linkage Cxl Report";
                case LINKAGE_FILL_REPORT: return "Linkage Fill Report";
                case TEXT: return "Text";
                case ORDER_QUERY: return "Order Query";
                case SINGLE_ORDER_QUERY: return "Single Order Query";
                case LIGHT_ORDER: return "Light Order";
                case TRADE_NOTIFICATION: return "Trade Notification";
                case FILL_REPORT_DROP_COPY: return "Fill Report Drop Copy";
                case CANCEL_REPORT_DROP_COPY: return "Cancel Report Drop Copy";
                case MANUAL_ORDER_TIMEOUT: return "Manual Order Timeout";
                case MANUAL_FILL_TIMEOUT: return "Manual Fill Timeout";
                default: return "Unknown Message Type";
            }
        }
    }

    FormattableDateTime getReceivedDate();
    MessageType getType();
    String getRestAsString();
    short getRouteReason();
    String getRouteDescription();
    short getRouteSourceType();
    String getRouteSource();
    String getSessionName();
    DateTimeStruct getRouteTime();
    long getMessageId();
    int getProductKeyValue();
    CBOEId getCboeId();
    public String getLogString();
    String getGiveUpFirm();
    String getCorrespondentFirm();
    String getBranchSeqNum();
    String getOrsId();
    String getDisplayOrsId();
    String getClassPostStation();
    String getProductName();
    int getMessageNumber();
    void setMessageNumber(int msgNumber);
    String getExpiration(boolean fullFormat);
}
