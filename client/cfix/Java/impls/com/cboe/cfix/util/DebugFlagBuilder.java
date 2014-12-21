package com.cboe.cfix.util;

/**
 * DebugFlagBuilder.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.util.*;

import com.cboe.cfix.interfaces.*;
import com.cboe.client.util.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.interfaces.cfix.*;

public class DebugFlagBuilder
{
    public static final int buildFixSessionDebugFlags(String str)
    {
        if (str == null)
        {
            return IntegerHelper.INVALID_VALUE;
        }

        int    debugFlags = FixSessionDebugIF.DEBUG_OFF;
        int    temp;
        String flag;

        for (StringTokenizer tokenizer = new StringTokenizer(str, ",|"); tokenizer.hasMoreTokens(); )
        {
            flag = tokenizer.nextToken().trim();

            if (flag.length() == 0)
            {
                continue;
            }

            temp = IntegerHelper.parseInt(flag);

            if (temp != IntegerHelper.INVALID_VALUE)
            {
                debugFlags |= temp;
            }
            else if (flag.equalsIgnoreCase(FixSessionDebugIF.strDEBUG_OFF))
            {
                debugFlags = FixSessionDebugIF.DEBUG_OFF;
                break;
            }
            else if (flag.equalsIgnoreCase(FixSessionDebugIF.strDEBUG_ALL))
            {
                debugFlags |= FixSessionDebugIF.DEBUG_ALL;
            }
            else if (flag.equalsIgnoreCase(FixSessionDebugIF.strSESSION_SHOW_RAW_SENT_MESSAGES))
            {
                debugFlags |= FixSessionDebugIF.SESSION_SHOW_RAW_SENT_MESSAGES;
            }
            else if (flag.equalsIgnoreCase(FixSessionDebugIF.strSESSION_SHOW_RAW_RECEIVED_MESSAGES))
            {
                debugFlags |= FixSessionDebugIF.SESSION_SHOW_RAW_RECEIVED_MESSAGES;
            }
            else if (flag.equalsIgnoreCase(FixSessionDebugIF.strSESSION_DECODE_RECEIVED_MESSAGES))
            {
                debugFlags |= FixSessionDebugIF.SESSION_DECODE_RECEIVED_MESSAGES;
            }
            else if (flag.equalsIgnoreCase(FixSessionDebugIF.strSESSION_DECODE_SENT_MESSAGES))
            {
                debugFlags |= FixSessionDebugIF.SESSION_DECODE_SENT_MESSAGES;
            }
            else if (flag.equalsIgnoreCase(FixSessionDebugIF.strSESSION_SHOW_PREDECODE_MESSAGE_DATA))
            {
                debugFlags |= FixSessionDebugIF.SESSION_SHOW_PREDECODE_MESSAGE_DATA;
            }
            else if (flag.equalsIgnoreCase(FixSessionDebugIF.strSESSION_SHOW_STATE_TRANSITIONS))
            {
                debugFlags |= FixSessionDebugIF.SESSION_SHOW_STATE_TRANSITIONS;
            }
            else if (flag.equalsIgnoreCase(FixSessionDebugIF.strMESSAGE_SHOW_BUILD_TRANSITIONS))
            {
                debugFlags |= FixSessionDebugIF.MESSAGE_SHOW_BUILD_TRANSITIONS;
            }
            else if (flag.equalsIgnoreCase(FixSessionDebugIF.strMESSAGE_SHOW_CHECKSUM_BUILDING))
            {
                debugFlags |= FixSessionDebugIF.MESSAGE_SHOW_CHECKSUM_BUILDING;
            }
            else if (flag.equalsIgnoreCase(FixSessionDebugIF.strPARSER_SHOW_STATE_TRANSITIONS))
            {
                debugFlags |= FixSessionDebugIF.PARSER_SHOW_STATE_TRANSITIONS;
            }
            else if (flag.equalsIgnoreCase(FixSessionDebugIF.strPARSER_SHOW_GARBAGE_BYTES))
            {
                debugFlags |= FixSessionDebugIF.PARSER_SHOW_GARBAGE_BYTES;
            }
            else if (flag.equalsIgnoreCase(FixSessionDebugIF.strRESEND_LIST_SHOW_INSERT))
            {
                debugFlags |= FixSessionDebugIF.RESEND_LIST_SHOW_INSERT;
            }
            else if (flag.equalsIgnoreCase(FixSessionDebugIF.strRESEND_LIST_SHOW_SEARCH))
            {
                debugFlags |= FixSessionDebugIF.RESEND_LIST_SHOW_SEARCH;
            }
            else if (flag.equalsIgnoreCase(FixSessionDebugIF.strMARKET_DATA_DECODER))
            {
                debugFlags |= FixSessionDebugIF.MARKET_DATA_DECODER;
            }
            else if (flag.equalsIgnoreCase(FixSessionDebugIF.strMARKET_DATA_CONSUMER_ACCEPT))
            {
                debugFlags |= FixSessionDebugIF.MARKET_DATA_CONSUMER_ACCEPT;
            }
            else if (flag.equalsIgnoreCase(FixSessionDebugIF.strWRITER_SHOW_EVENT_CHANNEL))
            {
                debugFlags |= FixSessionDebugIF.WRITER_SHOW_EVENT_CHANNEL;
            }
            else
            {
                Log.alarm("INVALID debugFlag specified(" + flag + ") within line(" + str + ")");
            }
        }

        return debugFlags;
    }

    public static final String stringizeFixSessionDebugFlags(int debugFlags)
    {
        if (debugFlags == FixSessionDebugIF.DEBUG_OFF)
        {
            return "";
        }

        StringBuilder buffer = new StringBuilder(128);
        boolean needComma = false;

        if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.SESSION_SHOW_RAW_SENT_MESSAGES))
        {
            if (needComma) buffer.append(","); else needComma = true;
            buffer.append(FixSessionDebugIF.strSESSION_SHOW_RAW_SENT_MESSAGES);
        }
        if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.SESSION_SHOW_RAW_RECEIVED_MESSAGES))
        {
            if (needComma) buffer.append(","); else needComma = true;
            buffer.append(FixSessionDebugIF.strSESSION_SHOW_RAW_RECEIVED_MESSAGES);
        }
        if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.SESSION_DECODE_RECEIVED_MESSAGES))
        {
            if (needComma) buffer.append(","); else needComma = true;
            buffer.append(FixSessionDebugIF.strSESSION_DECODE_RECEIVED_MESSAGES);
        }
        if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.SESSION_DECODE_SENT_MESSAGES))
        {
            if (needComma) buffer.append(","); else needComma = true;
            buffer.append(FixSessionDebugIF.strSESSION_DECODE_SENT_MESSAGES);
        }
        if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.SESSION_SHOW_PREDECODE_MESSAGE_DATA))
        {
            if (needComma) buffer.append(","); else needComma = true;
            buffer.append(FixSessionDebugIF.strSESSION_SHOW_PREDECODE_MESSAGE_DATA);
        }
        if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.SESSION_SHOW_STATE_TRANSITIONS))
        {
            if (needComma) buffer.append(","); else needComma = true;
            buffer.append(FixSessionDebugIF.strSESSION_SHOW_STATE_TRANSITIONS);
        }
        if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MESSAGE_SHOW_BUILD_TRANSITIONS))
        {
            if (needComma) buffer.append(","); else needComma = true;
            buffer.append(FixSessionDebugIF.strMESSAGE_SHOW_BUILD_TRANSITIONS);
        }
        if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MESSAGE_SHOW_CHECKSUM_BUILDING))
        {
            if (needComma) buffer.append(","); else needComma = true;
            buffer.append(FixSessionDebugIF.strMESSAGE_SHOW_CHECKSUM_BUILDING);
        }
        if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.PARSER_SHOW_STATE_TRANSITIONS))
        {
            if (needComma) buffer.append(","); else needComma = true;
            buffer.append(FixSessionDebugIF.strPARSER_SHOW_STATE_TRANSITIONS);
        }
        if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.PARSER_SHOW_GARBAGE_BYTES))
        {
            if (needComma) buffer.append(","); else needComma = true;
            buffer.append(FixSessionDebugIF.strPARSER_SHOW_GARBAGE_BYTES);
        }
        if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.RESEND_LIST_SHOW_INSERT))
        {
            if (needComma) buffer.append(","); else needComma = true;
            buffer.append(FixSessionDebugIF.strRESEND_LIST_SHOW_INSERT);
        }
        if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.RESEND_LIST_SHOW_SEARCH))
        {
            if (needComma) buffer.append(","); else needComma = true;
            buffer.append(FixSessionDebugIF.strRESEND_LIST_SHOW_SEARCH);
        }
        if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_DECODER))
        {
            if (needComma) buffer.append(","); else needComma = true;
            buffer.append(FixSessionDebugIF.strMARKET_DATA_DECODER);
        }
        if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_CONSUMER_ACCEPT))
        {
            if (needComma) buffer.append(","); else needComma = true;
            buffer.append(FixSessionDebugIF.strMARKET_DATA_CONSUMER_ACCEPT);
        }
        if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.WRITER_SHOW_EVENT_CHANNEL))
        {
            if (needComma) buffer.append(","); else needComma = true;
            buffer.append(FixSessionDebugIF.strWRITER_SHOW_EVENT_CHANNEL);
        }

        return buffer.toString();
    }

    public static final int buildDispatcherDebugFlags(String str)
    {
        if (str == null)
        {
            return IntegerHelper.INVALID_VALUE;
        }

        int    debugFlags = CfixMarketDataDispatcherIF.DEBUG_OFF;
        int    temp;
        String flag;

        for (StringTokenizer tokenizer = new StringTokenizer(str, ",|"); tokenizer.hasMoreTokens(); )
        {
            flag = tokenizer.nextToken().trim();

            if (flag.length() == 0)
            {
                continue;
            }

            temp = IntegerHelper.parseInt(flag);

            if (temp != IntegerHelper.INVALID_VALUE)
            {
                debugFlags |= temp;
            }
            else if (flag.equalsIgnoreCase(CfixMarketDataDispatcherIF.strDEBUG_OFF))
            {
                debugFlags = CfixMarketDataDispatcherIF.DEBUG_OFF;
                break;
            }
            else if (flag.equalsIgnoreCase(CfixMarketDataDispatcherIF.strDEBUG_ALL))
            {
                debugFlags |= CfixMarketDataDispatcherIF.DEBUG_ALL;
            }
            else if (flag.equalsIgnoreCase(CfixMarketDataDispatcherIF.strDEBUG_Subscribe))
            {
                debugFlags |= CfixMarketDataDispatcherIF.DEBUG_Subscribe;
            }
            else if (flag.equalsIgnoreCase(CfixMarketDataDispatcherIF.strDEBUG_Accept))
            {
                debugFlags |= CfixMarketDataDispatcherIF.DEBUG_Accept;
            }
            else if (flag.equalsIgnoreCase(CfixMarketDataDispatcherIF.strDEBUG_ChannelUpdate))
            {
                debugFlags |= CfixMarketDataDispatcherIF.DEBUG_ChannelUpdate;
            }
            else if (flag.equalsIgnoreCase(CfixMarketDataDispatcherIF.strDEBUG_ChannelUpdateDecode))
            {
                debugFlags |= CfixMarketDataDispatcherIF.DEBUG_ChannelUpdateDecode;
            }
            else
            {
                Log.alarm("INVALID debugFlag specified(" + flag + ") within line(" + str + ")");
            }
        }

        return debugFlags;
    }

    public static final String stringizeDispatcherDebugFlags(int debugFlags)
    {
        if (debugFlags == CfixMarketDataDispatcherIF.DEBUG_OFF)
        {
            return "";
        }

        StringBuilder buffer = new StringBuilder(128);
        boolean needComma = false;

        if (BitHelper.isBitMaskSet(debugFlags, CfixMarketDataDispatcherIF.DEBUG_Subscribe))
        {
            if (needComma) buffer.append(","); else needComma = true;
            buffer.append(CfixMarketDataDispatcherIF.strDEBUG_Subscribe);
        }
        if (BitHelper.isBitMaskSet(debugFlags, CfixMarketDataDispatcherIF.DEBUG_Accept))
        {
            if (needComma) buffer.append(","); else needComma = true;
            buffer.append(CfixMarketDataDispatcherIF.strDEBUG_Accept);
        }

        if (BitHelper.isBitMaskSet(debugFlags, CfixMarketDataDispatcherIF.DEBUG_ChannelUpdate))
        {
            if (needComma) buffer.append(","); else needComma = true;
            buffer.append(CfixMarketDataDispatcherIF.strDEBUG_ChannelUpdate);
        }

        if (BitHelper.isBitMaskSet(debugFlags, CfixMarketDataDispatcherIF.DEBUG_ChannelUpdateDecode))
        {
            if (needComma) buffer.append(","); else needComma = true;
            buffer.append(CfixMarketDataDispatcherIF.strDEBUG_ChannelUpdateDecode);
        }

        return buffer.toString();
    }
}
