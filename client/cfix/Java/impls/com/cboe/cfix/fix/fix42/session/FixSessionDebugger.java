package com.cboe.cfix.fix.fix42.session;

/**
 * FixSessionDebugger.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.io.*;

import com.cboe.cfix.fix.parser.*;
import com.cboe.cfix.fix.util.*;
import com.cboe.cfix.interfaces.*;
import com.cboe.cfix.util.*;
import com.cboe.client.util.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;

public class FixSessionDebugger
{
    public static void dumpFixMessage(String prefix, String message, FixMessageFactoryIF debugFixMessageFactory)
    {
        dumpFixMessage(prefix, message, debugFixMessageFactory, FixSessionDebugIF.SESSION_DECODE_SENT_MESSAGES);
    }

    public static void dumpFixMessage(String prefix, FastCharacterWriter writer, FixMessageFactoryIF debugFixMessageFactory)
    {
        dumpFixMessage(prefix, writer.toString(), debugFixMessageFactory, FixSessionDebugIF.SESSION_DECODE_SENT_MESSAGES);
    }

    public static void dumpFixMessage(String prefix, FastCharacterWriter writer, FixMessageFactoryIF debugFixMessageFactory, int debugFlags)
    {
        dumpFixMessage(prefix, writer.toString(), debugFixMessageFactory, debugFlags);
    }

    public static void dumpFixMessage(String prefix, FastCharacterWriter[] writers, FixMessageFactoryIF debugFixMessageFactory)
    {
        dumpFixMessage(prefix, writers, debugFixMessageFactory, FixSessionDebugIF.SESSION_DECODE_SENT_MESSAGES);
    }

    public static void dumpFixMessage(String prefix, FastCharacterWriter[] writers, FixMessageFactoryIF debugFixMessageFactory, int debugFlags)
    {
        StringBuilder writer = new StringBuilder(512);

        for (int i = 0; i < writers.length; i++)
        {
            writer.append(writers[i].toString());
        }

        dumpFixMessage(prefix, writer.toString(), debugFixMessageFactory, debugFlags);
    }

    public static void dumpFixMessage(String prefix, String message, FixMessageFactoryIF debugFixMessageFactory, int debugFlags)
    {
        PackedIntArrayIF  debugFoundErrors     = new GrowableIntArray();
        FixPacketParserIF debugFixPacketParser = new FixPacketParser();
        StringBuffer      writer               = new StringBuffer(256);
        boolean           endsInNL             = false;
        FixPacketIF       debugFixPacket;
        FixMessageIF      debugFixMessage;

        try
        {
            writer.append(prefix);

            if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.SESSION_SHOW_PREDECODE_MESSAGE_DATA))
            {
                writer.append("FIXMESSAGE [" + message + "]\n");
                endsInNL = true;
            }

            debugFixPacket = debugFixPacketParser.parse(new StringBufferInputStream(message), FixMessageIF.VALIDATE_ONLY_USED_FIELDS, debugFlags);
            if (debugFixPacket.isBadFixMessage())
            {
                writer.append("\nPARSE ERRORS [" + debugFixPacket.toString() + "]\n");
                endsInNL = true;
            }

            debugFixMessage = debugFixMessageFactory.createFixMessageFromMsgType(debugFixPacket.charAt(debugFixPacket.getValueOffset(2)));

            debugFoundErrors = debugFixMessage.build(debugFixPacket, debugFoundErrors, FixMessageIF.VALIDATE_ONLY_USED_FIELDS | FixMessageIF.VALIDATE_UNUSED_FIELDS, debugFlags);

            if (!debugFoundErrors.isEmpty())
            {
                writer.append("\nBUILD ERRORS\n");
                for (int err = 0; err < debugFoundErrors.length(); err++)
                {
                    byte error    = BitHelper.unpackHighByte(debugFoundErrors.get(err));
                    int  position = BitHelper.unpackLowShortAsInt(debugFoundErrors.get(err));

                    if (FixException.isPositionATag(error, position))
                    {
                        writer.append("   " + FixException.toString(error, position) + "\n");
                    }
                    else
                    {
                        writer.append("   " + FixException.toString(error, position) + "  " + debugFixPacket.getTag(position) + "=" + new String(debugFixPacket.getArray(), debugFixPacket.getValueOffset(position), debugFixPacket.getValueLength(position)) + "\n");
                    }
                }
                endsInNL = true;
            }

            if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.SESSION_DECODE_SENT_MESSAGES))
            {
                if (!endsInNL)
                {
                    writer.append("\n");
                }

                debugFixMessage.accept(new PrettyPrintWriter(writer));
            }
        }
        catch (Exception ex)
        {
            writer.append("Exception: " + ExceptionHelper.getStackTrace(ex) + "\n");
        }

        Log.information(writer.toString());
    }

    public static void dumpFixMessage(String prefix, FixMessageIF debugFixMessage, FixPacketIF debugFixPacket, PackedIntArrayIF debugFoundErrors)
    {
        StringBuffer writer = new StringBuffer();

        writer.append(prefix);

        writer.append("\n");

        debugFixMessage.accept(new PrettyPrintWriter(writer));

        if (!debugFoundErrors.isEmpty())
        {
            writer.append("ERRORS[" + debugFixPacket.getArrayAsString() + "]");
            for (int err = 0; err < debugFoundErrors.length(); err++)
            {
                byte error    = BitHelper.unpackHighByte(debugFoundErrors.get(err));
                int  position = BitHelper.unpackLowShortAsInt(debugFoundErrors.get(err));

                if (FixException.isPositionATag(error, position))
                {
                    writer.append("   " + FixException.toString(error, position));
                }
                else
                {
                    writer.append("   " + FixException.toString(error, position) + "  " + debugFixPacket.getTag(position) + "=" + new String(debugFixPacket.getArray(), debugFixPacket.getValueOffset(position), debugFixPacket.getValueLength(position)));
                }
            }
        }

        Log.information(writer.toString());
    }
}
