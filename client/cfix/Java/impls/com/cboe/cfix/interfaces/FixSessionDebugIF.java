package com.cboe.cfix.interfaces;

/**
 * FixSessionDebugIF.java
 *
 * @author Dmitry Volpyansky
 *
 */

public interface FixSessionDebugIF
{
    public static final int DEBUG_OFF                           = 0;       public static final String strDEBUG_OFF                           = "DEBUG_OFF";
    public static final int DEBUG_ALL                           = ~0;      public static final String strDEBUG_ALL                           = "DEBUG_ALL";

    public static final int SESSION_SHOW_RAW_SENT_MESSAGES      = 1 << 1;  public static final String strSESSION_SHOW_RAW_SENT_MESSAGES      = "SESSION_SHOW_RAW_SENT_MESSAGES";
    public static final int SESSION_SHOW_RAW_RECEIVED_MESSAGES  = 1 << 2;  public static final String strSESSION_SHOW_RAW_RECEIVED_MESSAGES  = "SESSION_SHOW_RAW_RECEIVED_MESSAGES";

    public static final int SESSION_DECODE_RECEIVED_MESSAGES    = 1 << 3;  public static final String strSESSION_DECODE_RECEIVED_MESSAGES    = "SESSION_DECODE_RECEIVED_MESSAGES";
    public static final int SESSION_DECODE_SENT_MESSAGES        = 1 << 4;  public static final String strSESSION_DECODE_SENT_MESSAGES        = "SESSION_DECODE_SENT_MESSAGES";

    public static final int SESSION_SHOW_PREDECODE_MESSAGE_DATA = 1 << 5;  public static final String strSESSION_SHOW_PREDECODE_MESSAGE_DATA = "SESSION_SHOW_PREDECODE_MESSAGE_DATA";
    public static final int SESSION_SHOW_STATE_TRANSITIONS      = 1 << 6;  public static final String strSESSION_SHOW_STATE_TRANSITIONS      = "SESSION_SHOW_STATE_TRANSITIONS";

    public static final int MESSAGE_SHOW_BUILD_TRANSITIONS      = 1 << 7;  public static final String strMESSAGE_SHOW_BUILD_TRANSITIONS      = "MESSAGE_SHOW_BUILD_TRANSITIONS";
    public static final int MESSAGE_SHOW_CHECKSUM_BUILDING      = 1 << 8;  public static final String strMESSAGE_SHOW_CHECKSUM_BUILDING      = "MESSAGE_SHOW_CHECKSUM_BUILDING";

    public static final int PARSER_SHOW_STATE_TRANSITIONS       = 1 << 9;  public static final String strPARSER_SHOW_STATE_TRANSITIONS       = "PARSER_SHOW_STATE_TRANSITIONS";
    public static final int PARSER_SHOW_GARBAGE_BYTES           = 1 << 10; public static final String strPARSER_SHOW_GARBAGE_BYTES           = "PARSER_SHOW_GARBAGE_BYTES";

    public static final int RESEND_LIST_SHOW_INSERT             = 1 << 11; public static final String strRESEND_LIST_SHOW_INSERT             = "RESEND_LIST_SHOW_INSERT";
    public static final int RESEND_LIST_SHOW_SEARCH             = 1 << 12; public static final String strRESEND_LIST_SHOW_SEARCH             = "RESEND_LIST_SHOW_SEARCH";

    public static final int MARKET_DATA_CONSUMER_ACCEPT         = 1 << 13; public static final String strMARKET_DATA_CONSUMER_ACCEPT         = "MARKET_DATA_CONSUMER_ACCEPT";
    public static final int MARKET_DATA_DECODER                 = 1 << 14; public static final String strMARKET_DATA_DECODER                 = "MARKET_DATA_DECODER";

    public static final int WRITER_SHOW_EVENT_CHANNEL           = 1 << 15; public static final String strWRITER_SHOW_EVENT_CHANNEL           = "WRITER_SHOW_EVENT_CHANNEL";

    public static final int AVAILABLE_FROM                      = 16;
}