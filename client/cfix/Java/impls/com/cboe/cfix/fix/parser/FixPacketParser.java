package com.cboe.cfix.fix.parser;

/**
 * FixPacketParser.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * Can parse and validate a FIX line
 *
 */

import java.io.*;
import java.net.*;

import com.cboe.cfix.fix.util.*;
import com.cboe.cfix.interfaces.*;
import com.cboe.client.util.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;

public class FixPacketParser implements FixPacketParserIF
{
    protected FixPacketIF        fixPacket;

    private FixStateContext      stateContext = new FixStateContext();
    private int                  prevCharRead;
    private int                  charRead;
    private int                  parseOptions;
    private int                  debugFlags;
    private int                  fixVersionToParse = FIX_VERSION_4_2;

    private static final int     RETURN_FIX_PACKET                  = 0;
    private static final int     STORE_CHAR                         = 1;
    private static final int     STORE_CHAR_AND_EXIT                = 2;

    public static final int      FIX_VERSION_4_2                    = '2';
    public static final int      FIX_VERSION_4_3                    = '3';
    public static final int      FIX_VERSION_4_4                    = '4';

    private final FixStateIF     fixStateFindTag8                   = new FixStateFindTag8();
    private final FixStateIF     fixStateTag8                       = new FixStateTag8();
    private final FixStateIF     fixStateTag9                       = new FixStateTag9();
    private final FixStateIF     fixStateTag35                      = new FixStateTag35();
    private final FixStateIF     fixStateValue10                    = new FixStateValue10();
    private final FixStateIF     fixStateTag                        = new FixStateTag();
    private final FixStateIF     fixStateValue                      = new FixStateValue();
    private final FixStateIF     fixStateRLEExpectedTag             = new FixStateRLEExpectedTag();
    private final FixStateIF     fixStateRLETag                     = new FixStateRLETag();
    private final FixStateIF     fixStateRLEValue                   = new FixStateRLEValue();

    private class FixStateContext
    {
        public FixStateIF currentState;
        public int        expectedLength;
        public int        readLength;
        public int        tag9_size;
        public int        expectedChecksum;
        public int        currentTag;
        public int        offset;
        public int        rleExpectedTag;
        public int        rleExpectedLength;

        public void reset()
        {
            currentState     = fixStateTag8;
            expectedLength   = 0;
            readLength       = 0;
            expectedChecksum = 0;
            currentTag       = 0;
        }
    }

    private interface FixStateIF
    {
        public int processChar();
    }

    private class FixStateFindTag8 implements FixStateIF
    {
        public int processChar()
        {
            switch (stateContext.readLength - 1)
            {
                case 0:
                    if (charRead == '8')
                    {
                        fixPacket.startTagPosition(8);
                        return STORE_CHAR;
                    }
                    break;
                case 1:
                    if (charRead == '=')
                    {
                        return STORE_CHAR;
                    }
                    break;
                case 2:
                    if (charRead == 'F')
                    {
                        fixPacket.addTagPositionValueStart(stateContext.readLength);
                        return STORE_CHAR;
                    }
                    break;
                case 3:
                    if (charRead == 'I')
                    {
                        return STORE_CHAR;
                    }
                    break;
                case 4:
                    if (charRead == 'X')
                    {
                        return STORE_CHAR;
                    }
                    break;
                case 5:
                    if (charRead == '.')
                    {
                        return STORE_CHAR;
                    }
                    break;
                case 6:
                    if (charRead == '4')
                    {
                        return STORE_CHAR;
                    }
                    break;
                case 7:
                    if (charRead == '.')
                    {
                        return STORE_CHAR;
                    }
                    break;
                case 8:
                    if (charRead == fixVersionToParse)
                    {
                        return STORE_CHAR;
                    }
                    break;
                case 9:
                    if (charRead == FixFieldIF.SOHchar)
                    {
                        stateContext.currentState = fixStateTag9;
                        stateContext.offset       = stateContext.readLength;

                        fixPacket.addTagPositionValueEnd(stateContext.readLength);

                        return STORE_CHAR;
                    }
                    break;
            }

            if (BitHelper.isBitMaskSet(parseOptions, FixSessionDebugIF.PARSER_SHOW_GARBAGE_BYTES)) {Log.information("Skipping Garbage Character(" + charRead + ")");}

            stateContext.readLength = 0;
            return STORE_CHAR;
        }
    }

    private class FixStateTag8 implements FixStateIF
    {
        public int processChar()
        {
            switch (stateContext.readLength - 1)
            {
                case 0:
                    if (charRead == '8')
                    {
                        fixPacket.startTagPosition(8);
                        return STORE_CHAR;
                    }
                    break;
                case 1:
                    if (charRead == '=')
                    {
                        return STORE_CHAR;
                    }
                    break;
                case 2:
                    if (charRead == 'F')
                    {
                        fixPacket.addTagPositionValueStart(stateContext.readLength);
                        return STORE_CHAR;
                    }
                    break;
                case 3:
                    if (charRead == 'I')
                    {
                        return STORE_CHAR;
                    }
                    break;
                case 4:
                    if (charRead == 'X')
                    {
                        return STORE_CHAR;
                    }
                    break;
                case 5:
                    if (charRead == '.')
                    {
                        return STORE_CHAR;
                    }
                    break;
                case 6:
                    if (charRead == '4')
                    {
                        return STORE_CHAR;
                    }
                    break;
                case 7:
                    if (charRead == '.')
                    {
                        return STORE_CHAR;
                    }
                    break;
                case 8:
                    if (charRead == fixVersionToParse)
                    {
                        return STORE_CHAR;
                    }
                    break;
                case 9:
                    if (charRead == FixFieldIF.SOHchar)
                    {
                        stateContext.currentState = fixStateTag9;
                        stateContext.offset       = stateContext.readLength;

                        fixPacket.addTagPositionValueEnd(stateContext.readLength);

                        return STORE_CHAR;
                    }
                    break;
            }

            fixPacket.setResult(FixPacketIF.MALFORMED_TAG_8, stateContext.readLength);
            return RETURN_FIX_PACKET;
        }
    }

    private class FixStateTag9 implements FixStateIF
    {
        public int processChar()
        {
            switch (stateContext.readLength - stateContext.offset - 1)
            {
                case 0:
                    if (charRead == '9')
                    {
                        fixPacket.startTagPosition(9);
                        return STORE_CHAR;
                    }
                    break;
                case 1:
                    if (charRead == '=')
                    {
                        fixPacket.addTagPositionValueStart(stateContext.readLength + 1);
                        return STORE_CHAR;
                    }
                    break;
                default:
                    if (charRead >= '0' && charRead <= '9')
                    {
                        stateContext.expectedLength = stateContext.expectedLength * 10 + (charRead - '0');
                        return STORE_CHAR;
                    }

                    if (stateContext.readLength - stateContext.offset - 1 >= 4 && charRead == FixFieldIF.SOHchar)
                    {
                        stateContext.currentState = fixStateTag35;
                        stateContext.offset       = stateContext.readLength;

                        if (stateContext.expectedLength < 10)
                        {
                            fixPacket.setResult(FixPacketIF.MALFORMED_TAG_9, stateContext.readLength);
                            return RETURN_FIX_PACKET;
                        }

                        stateContext.tag9_size = stateContext.readLength - FixFieldIF.FIX_TAG_8_LENGTH - FixFieldIF.FIX_TAG_9_EMPTY_LENGTH;

                        fixPacket.allocateArray(stateContext.expectedLength +
                                                FixFieldIF.FIX_TAG_8_LENGTH +
                                                FixFieldIF.FIX_TAG_9_EMPTY_LENGTH +
                                                stateContext.tag9_size +
                                                FixFieldIF.FIX_TAG_10_LENGTH,
                                                FixFieldIF.FIX_TAG_8_LENGTH +
                                                FixFieldIF.FIX_TAG_9_EMPTY_LENGTH +
                                                stateContext.tag9_size);

                        fixPacket.addTagPositionValueEnd(stateContext.readLength);

                        return STORE_CHAR;
                    }
                    break;
            }

            fixPacket.setResult(FixPacketIF.MALFORMED_TAG_9, stateContext.readLength);
            return RETURN_FIX_PACKET;
        }
    }

    private class FixStateTag35 implements FixStateIF
    {
        public int processChar()
        {
            switch (stateContext.readLength - stateContext.offset - 1)
            {
                case 0:
                    if (charRead == '3')
                    {
                        fixPacket.startTagPosition(35);
                        return STORE_CHAR;
                    }
                    break;
                case 1:
                    if (charRead == '5')
                    {
                        return STORE_CHAR;
                    }
                    break;
                case 2:
                    if (charRead == '=')
                    {
                        return STORE_CHAR;
                    }
                    break;
                case 3:
                    if ((charRead >= 'a' && charRead <= 'z') ||
                        (charRead >= 'A' && charRead <= 'Z') ||
                        (charRead >= '0' && charRead <= '9'))
                    {
                        fixPacket.addTagPositionValueStart(stateContext.readLength);

                        return STORE_CHAR;
                    }
                    break;
                case 4:
                    if (charRead == FixFieldIF.SOHchar)
                    {
                        stateContext.currentState = fixStateTag;
                        stateContext.currentTag   = 0;

                        fixPacket.addTagPositionValueEnd(stateContext.readLength);

                        return STORE_CHAR;
                    }

                    if (prevCharRead == 'U' &&
                        (charRead >= 'a' && charRead <= 'z') ||
                        (charRead >= 'A' && charRead <= 'Z') ||
                        (charRead >= '0' && charRead <= '9'))
                    {
                        return STORE_CHAR;
                    }

                    if (fixVersionToParse == FIX_VERSION_4_4 && prevCharRead == 'A' &&
                        (charRead >= 'a' && charRead <= 'z') ||
                        (charRead >= 'A' && charRead <= 'Z') ||
                        (charRead >= '0' && charRead <= '9'))
                    {
                        return STORE_CHAR;
                    }
                    break;
                case 5:
                    if (charRead == FixFieldIF.SOHchar)
                    {
                        stateContext.currentState = fixStateTag;
                        stateContext.currentTag   = 0;

                        fixPacket.addTagPositionValueEnd(stateContext.readLength);

                        return STORE_CHAR;
                    }
                    break;
            }

            fixPacket.setResult(FixPacketIF.MALFORMED_TAG_35, stateContext.readLength);
            return RETURN_FIX_PACKET;
        }
    }

    private class FixStateValue10 implements FixStateIF
    {
        public int processChar()
        {
            switch (stateContext.readLength - stateContext.offset - 1)
            {
                case 0:
                case 1:
                case 2:
                    if (charRead >= '0' && charRead <= '9')
                    {
                        stateContext.expectedChecksum = stateContext.expectedChecksum * 10 + (charRead - '0');
                        return STORE_CHAR;
                    }
                    break;
                case 3:
                    if (charRead == FixFieldIF.SOHchar)
                    {
                        boolean bError = false;

                        fixPacket.addTagPositionValueEnd(stateContext.readLength);

                        if (stateContext.expectedLength + FixFieldIF.FIX_TAG_8_LENGTH + FixFieldIF.FIX_TAG_9_EMPTY_LENGTH + stateContext.tag9_size + FixFieldIF.FIX_TAG_10_LENGTH != stateContext.readLength)
                        {
                            fixPacket.setResult(FixPacketIF.WRONG_LENGTH, stateContext.readLength);
                            bError = true;
                        }

                        int readChecksum = FixChecksumHelper.calculateFixChecksum(fixPacket.getArray(), 0, stateContext.readLength - FixFieldIF.FIX_TAG_10_LENGTH);

                        if (stateContext.expectedChecksum != readChecksum)
                        {
                            fixPacket.setResult(FixPacketIF.WRONG_CHECKSUM, stateContext.readLength);
                            bError = true;
                        }

                        return bError ? RETURN_FIX_PACKET : STORE_CHAR_AND_EXIT;
                    }
                    break;
            }

            fixPacket.setResult(FixPacketIF.MALFORMED_TAG_10, stateContext.readLength);
            return RETURN_FIX_PACKET;
        }
    }

    private class FixStateRLETag implements FixStateIF
    {
        public int processChar()
        {
            if (stateContext.readLength > fixPacket.getExpectedLength())
            {
                fixPacket.setResult(FixPacketIF.INCOMPLETE, stateContext.readLength);
                return RETURN_FIX_PACKET;
            }

            if (charRead >= '0' && charRead <= '9')
            {
                stateContext.rleExpectedLength = stateContext.rleExpectedLength * 10 + (charRead - '0');
                return STORE_CHAR;
            }

            if (charRead == FixFieldIF.SOHchar)
            {
                fixPacket.addTagPositionValueEnd(stateContext.readLength);

                stateContext.currentTag = 0;

                if (stateContext.rleExpectedLength > 0)
                {
                    stateContext.currentState = fixStateRLEExpectedTag;
                }
                else
                {
                    stateContext.currentState = fixStateTag;
                }
                return STORE_CHAR;
            }

            fixPacket.setResult(FixPacketIF.MALFORMED_TAG, stateContext.readLength);
            return RETURN_FIX_PACKET;
        }
    }

    private class FixStateRLEExpectedTag implements FixStateIF
    {
        public int processChar()
        {
            if (stateContext.readLength > fixPacket.getExpectedLength())
            {
                fixPacket.setResult(FixPacketIF.INCOMPLETE, stateContext.readLength);
                return RETURN_FIX_PACKET;
            }

            if (charRead >= '0' && charRead <= '9')
            {
                stateContext.currentTag = stateContext.currentTag * 10 + (charRead - '0');
                return STORE_CHAR;
            }

            if (charRead == '=')
            {
                fixPacket.startTagPosition(stateContext.currentTag);
                fixPacket.addTagPositionValueStart(stateContext.readLength + 1);

                if (stateContext.currentTag != stateContext.rleExpectedTag)
                {
                    fixPacket.setResult(FixPacketIF.NON_FOLLOWING_RLE_TAG, stateContext.readLength);
                    return RETURN_FIX_PACKET;
                }

                stateContext.currentState = fixStateRLEValue;
                return STORE_CHAR;
            }

            fixPacket.setResult(FixPacketIF.MALFORMED_TAG, stateContext.readLength);
            return RETURN_FIX_PACKET;
        }
    }

    private class FixStateRLEValue implements FixStateIF
    {
        public int processChar()
        {
            if (stateContext.readLength > fixPacket.getExpectedLength())
            {
                fixPacket.setResult(FixPacketIF.INCOMPLETE, stateContext.readLength);
                return RETURN_FIX_PACKET;
            }

            if (stateContext.rleExpectedLength-- > 0)
            {
                return STORE_CHAR;
            }

            if (charRead == FixFieldIF.SOHchar)
            {
                fixPacket.addTagPositionValueEnd(stateContext.readLength);

                stateContext.currentState = fixStateTag;
                stateContext.currentTag   = 0;
                return STORE_CHAR;
            }

            fixPacket.setResult(FixPacketIF.BAD_LENGTH_RLE_TAG, stateContext.readLength);
            return RETURN_FIX_PACKET;
        }
    }

    private class FixStateTag implements FixStateIF
    {
        public int processChar()
        {
            if (stateContext.readLength > fixPacket.getExpectedLength())
            {
                fixPacket.setResult(FixPacketIF.INCOMPLETE, stateContext.readLength);
                return RETURN_FIX_PACKET;
            }

            if (charRead >= '0' && charRead <= '9')
            {
                stateContext.currentTag = stateContext.currentTag * 10 + (charRead - '0');
                return STORE_CHAR;
            }

            if (charRead == '=')
            {
                fixPacket.startTagPosition(stateContext.currentTag);
                fixPacket.addTagPositionValueStart(stateContext.readLength + 1);

                switch (stateContext.currentTag)
                {
                    case 10:
                    {
                        stateContext.currentState = fixStateValue10;
                        stateContext.offset       = stateContext.readLength;
                        return STORE_CHAR;
                    }

                    case 90:
                    case 95:
                    case 212:
                    case 348:
                    case 350:
                    case 354:
                    case 356:
                    {
                        stateContext.currentState       = fixStateRLETag;
                        stateContext.offset             = stateContext.readLength;
                        stateContext.rleExpectedLength  = 0;
                        stateContext.rleExpectedTag     = stateContext.currentTag + 1;
                        return STORE_CHAR;
                    }
                }

                stateContext.currentState = fixStateValue;
                return STORE_CHAR;
            }

            fixPacket.setResult(FixPacketIF.MALFORMED_TAG, stateContext.readLength);
            return RETURN_FIX_PACKET;
        }
    }

    private class FixStateValue implements FixStateIF
    {
        public int processChar()
        {
            if (stateContext.readLength > fixPacket.getExpectedLength())
            {
                fixPacket.setResult(FixPacketIF.INCOMPLETE, stateContext.readLength);
                return RETURN_FIX_PACKET;
            }

            if (stateContext.currentTag == 8 || stateContext.currentTag == 9 || stateContext.currentTag == 35)
            {
                fixPacket.setResult(FixPacketIF.COLLISION, stateContext.readLength);
                return RETURN_FIX_PACKET;
            }

            if (charRead == FixFieldIF.SOHchar)
            {
                fixPacket.addTagPositionValueEnd(stateContext.readLength);
                stateContext.currentState = fixStateTag;
                stateContext.currentTag   = 0;
                return STORE_CHAR;
            }

            return STORE_CHAR;
        }
    }

    public FixPacketParser()
    {
        this.fixPacket = new FixPacket();
    }

    public FixPacketParser(FixPacketIF fixPacket)
    {
        this.fixPacket = fixPacket;
    }

    public FixPacketParser(int fixVersionToParse)
    {
        this();
        setParserVersion(fixVersionToParse);
    }

    public FixPacketParser(FixPacketIF fixPacket, int fixVersionToParse)
    {
        this(fixPacket);
        setParserVersion(fixVersionToParse);
    }

    public FixPacketParserIF setParserVersion(int fixVersionToParse)
    {
        switch (fixVersionToParse)
        {
            case FIX_VERSION_4_2:
                this.fixVersionToParse = fixVersionToParse;
                break;
            default:
                Log.alarm(Thread.currentThread().getName() + " FixPacketParser.setParserVersion() INVALID FIX VERSION: DEFAULTING TO 4." + this.fixVersionToParse);
                break;
        }

        return this;
    }

    public int getParserVersion()
    {
        return fixVersionToParse;
    }

    public void setFixPacket(FixPacketIF fixPacket)
    {
        this.fixPacket = fixPacket;
    }

    public void reset()
    {
        stateContext.reset();
        fixPacket.reset(21);
    }

    public FixPacketIF parse(InputStream istream, int parseOptions, int debugFlags)
    {
        this.parseOptions = parseOptions;
        this.debugFlags   = debugFlags;

        reset();

        if (BitHelper.isBitMaskSet(this.parseOptions, SKIP_GARBAGE_PREFIX))
        {
            stateContext.currentState = fixStateFindTag8;
        }

        FixStateIF debugLastState = stateContext.currentState;
        int rc;

        try
        {
            while (true)
            {
                prevCharRead = charRead;

                try
                {
                    charRead = istream.read();
                }
                catch (SocketException ex)
                {
                    charRead = -1;
                }
                catch (NullPointerException ex)
                {
                    charRead = -1;
                }

                if (charRead == -1)
                {
                    if (stateContext.currentState == fixStateFindTag8)
                    {
                        fixPacket.setResult(FixPacketIF.ALL_GARBAGE, stateContext.readLength);
                    }
                    else
                    {
                        fixPacket.setResult(FixPacketIF.DISCONNECTED, stateContext.readLength);
                    }
                    return fixPacket;
                }

                if (charRead == 0)
                {
                    continue;
                }

                if (stateContext.readLength == fixPacket.getArray().length)
                {
                    fixPacket.allocateArray(stateContext.readLength + 1, fixPacket.getArray().length);
                    fixPacket.setResult(FixPacketIF.EXCEEDED_LENGTH, stateContext.readLength);
                }

                fixPacket.setCharAt(stateContext.readLength++, (char) charRead);

                if (BitHelper.isBitMaskSet(this.debugFlags, FixSessionDebugIF.PARSER_SHOW_STATE_TRANSITIONS)) {Log.information(StringHelper.rightPad(ClassHelper.getClassNameFinalPortion(stateContext.currentState), 30, ' ') + "[" + stateContext.readLength + "] '" + charRead + "'");}

                rc = stateContext.currentState.processChar();

                if (BitHelper.isBitMaskSet(this.debugFlags, FixSessionDebugIF.PARSER_SHOW_STATE_TRANSITIONS))
                {
                    if (stateContext.currentState != debugLastState)
                    {
                        debugLastState = stateContext.currentState;
                    }
                }

                if (rc == STORE_CHAR)
                {
                    continue;
                }
                else if (rc == STORE_CHAR_AND_EXIT)
                {
                    break;
                }
                else if (rc == RETURN_FIX_PACKET)
                {
                    return fixPacket;
                }
            }
        }
        catch (InterruptedIOException ex)
        {
            fixPacket.setResult(FixPacketIF.TIMED_OUT, stateContext.readLength);
            return fixPacket;
        }
        catch (SocketException ex)
        {
            Log.exception(ex);

            fixPacket.setResult(FixPacketIF.DISCONNECTED, stateContext.readLength);
            return fixPacket;
        }
        catch (Exception ex)
        {
            Log.exception(ex);
        }

        if (stateContext.readLength == 0)
        {
            fixPacket.setResult(FixPacketIF.DISCONNECTED, stateContext.readLength);
            return fixPacket;
        }

        fixPacket.setResult(FixPacketIF.OK, stateContext.readLength);
        return fixPacket;
    }
}
