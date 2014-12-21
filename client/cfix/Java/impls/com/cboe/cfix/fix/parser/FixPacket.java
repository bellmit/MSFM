package com.cboe.cfix.fix.parser;

/**
 * FixPacket.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 *
 * This object contains a decoded image of the FIX string, split into a character array, and a tokenized array<br>
 * <br>
 * ASSUMPTIONS:<br>
 *  - Since we pack a Tag (short) + ValueStart (int) + ValueLength (short) into a Long,<br>
 *       we can only handle value-length's shorter than 64K unless we use non-base2 bit masks<br>
 *
 */

import com.cboe.cfix.interfaces.*;

public class FixPacket implements FixPacketIF
{
    public int    result;
    public char[] array;
    public int    expectedLength;
    public int    readLength;
    public long[] tagPositionArray;
    public int    tagPositionLength;
    public int[]  errorPositions;

    private final static int DEFAULT_ARRAY_SIZE         = 64;
    private final static int DEFAULT_ARRAY_SOFT_LIMIT   = 64 * 1024;
    private final static int DEFAULT_ARRAY_GROWTH       = 64;

    public void reset()
    {
        result            = UNINITIALIZED;
        expectedLength    = 0;
        readLength        = 0;
        tagPositionArray  = new long[DEFAULT_ARRAY_SIZE];
        tagPositionLength = 0;
        errorPositions    = null;
        array             = new char[DEFAULT_ARRAY_SIZE];
    }

    public void reset(int neededLength)
    {
        result            = UNINITIALIZED;
        readLength        = 0;
        tagPositionArray  = new long[DEFAULT_ARRAY_SIZE];
        tagPositionLength = 0;
        errorPositions    = null;
        array             = new char[DEFAULT_ARRAY_SIZE];
        allocateArray(neededLength);
    }

    public int getExpectedLength()
    {
        return expectedLength;
    }

    public void allocateArray(int neededLength)
    {
        expectedLength = neededLength;

        if (array.length < neededLength ||
             (array.length > DEFAULT_ARRAY_SOFT_LIMIT && // try to shrink arrays back to below 64K if possible
              neededLength < DEFAULT_ARRAY_SOFT_LIMIT))
        {
            array = new char[neededLength];
        }
    }

    public void startTagPosition(int tag)
    {
        if (tagPositionLength >= tagPositionArray.length)
        {
            long[] old = tagPositionArray;

            tagPositionArray = new long[tagPositionArray.length + DEFAULT_ARRAY_GROWTH];

            System.arraycopy(old, 0, tagPositionArray, 0, old.length);
        }

        tagPositionArray[tagPositionLength] = ((long) tag << 48);
    }

    public void addTagPositionValueStart(int position)
    {
        tagPositionArray[tagPositionLength] |= (long) (position - 1);
    }

    public void addTagPositionValueEnd(int position)
    {
        int valueLength = position - (int) (tagPositionArray[tagPositionLength]) - 1;

        tagPositionArray[tagPositionLength] |= ((long) valueLength << 32);

//          Log.information(dumpTagPosition(tagPositionLength));

        tagPositionLength++;
    }

    public int getTagPositionLength()
    {
        return tagPositionLength;
    }

    public long[] getTagPositionArray()
    {
        return tagPositionArray;
    }

    public char[] getArray()
    {
        return array;
    }

    public String dumpTagPosition(int index)
    {
        return "Tag[" + getTag(index) + "] ValueOffset[" + getValueOffset(index) + "] ValueLength[" + getValueLength(index) + "] {" + new String(array, ((int) ((tagPositionArray[index]))), ((short) ((tagPositionArray[index]) >> 32))) + "}";
    }

    public void allocateArray(int neededLength, int copyLength)
    {
        expectedLength = neededLength;

        if (array == null)
        {
            array = new char[neededLength];
        }
        else if (array.length < neededLength)
        {
            char[] old = array;

            array = new char[neededLength];

            System.arraycopy(old, 0, array, 0, copyLength);
        }
    }

    public void setResult(int result, int readLength)
    {
        this.result     = (this.result & ~UNINITIALIZED) | result;
        this.readLength = readLength;

        if (result != OK && result != ALL_GARBAGE)
        {
            if (errorPositions == null)
            {
                errorPositions = new int[2];
            }
            else
            {
                int[] old = errorPositions;

                errorPositions = new int[errorPositions.length + 2];

                System.arraycopy(old, 0, errorPositions, 0, old.length);
            }

            errorPositions[errorPositions.length - 2] = result;
            errorPositions[errorPositions.length - 1] = readLength;
        }
    }

    public int getTag(int index)
    {
        return (int) ((short) (tagPositionArray[index] >> 48));
    }

    public int getValueOffset(int index)
    {
        return (int) tagPositionArray[index];
    }

    public int getValueLength(int index)
    {
        return (int) ((short) (tagPositionArray[index] >> 32));
    }

    public boolean isGoodFixMessage()
    {
        return result == OK;
    }

    public boolean isBadFixMessage()
    {
        return result != OK &&
               result != ALL_GARBAGE;
    }

    public boolean isGarbageMessage()
    {
        return result == ALL_GARBAGE;
    }

    public boolean isMessageDisconnected()
    {
        return 0 != (result & DISCONNECTED);
    }

    public boolean isMessageTimedOut()
    {
        return 0 != (result & TIMED_OUT);
    }

    public char setCharAt(int index, char ch)
    {
        array[index] = ch;

        return ch;
    }

    public char charAt(int index)
    {
        return array[index];
    }

    public String getArrayAsString()
    {
        return new String(array, 0, readLength);
    }

    public int getReadLength()
    {
        return readLength;
    }

    public String toString()
    {
        if (result == UNINITIALIZED)
        {
            return "result[UNINITIALIZED]";
        }

        if (result == ALL_GARBAGE)
        {
            return "result[ALL_GARBAGE]";
        }

        if (result == OK)
        {
            return "result[OK]";
        }

        StringBuilder buffer = new StringBuilder(20*(errorPositions.length/2));

        for (int errorCount = 0; errorCount < errorPositions.length; errorCount += 2)
        {
            if (errorPositions[errorCount] == TIMED_OUT)
            {
                buffer.append("TIMED_OUT@").append(errorPositions[errorCount+1]);
            }
            else if (errorPositions[errorCount] == DISCONNECTED)
            {
                buffer.append("DISCONNECTED@").append(errorPositions[errorCount+1]);
            }
            else if (errorPositions[errorCount] == NOTCONNECTED)
            {
                buffer.append("NOTCONNECTED@").append(errorPositions[errorCount+1]);
            }
            else if (errorPositions[errorCount] == INCOMPLETE)
            {
                buffer.append("INCOMPLETE@").append(errorPositions[errorCount+1]);
            }
            else if (errorPositions[errorCount] == WRONG_CHECKSUM)
            {
                buffer.append("WRONG_CHECKSUM@").append(errorPositions[errorCount+1]);
            }
            else if (errorPositions[errorCount] == WRONG_LENGTH)
            {
                buffer.append("WRONG_LENGTH@").append(errorPositions[errorCount+1]);
            }
            else if (errorPositions[errorCount] == EXCEEDED_LENGTH)
            {
                buffer.append("EXCEEDED_LENGTH@").append(errorPositions[errorCount+1]);
            }
            else if (errorPositions[errorCount] == MALFORMED_TAG)
            {
                buffer.append("MALFORMED_TAG@").append(errorPositions[errorCount+1]);
            }
            else if (errorPositions[errorCount] == MALFORMED_VALUE)
            {
                buffer.append("MALFORMED_VALUE@").append(errorPositions[errorCount+1]);
            }
            else if (errorPositions[errorCount] == MALFORMED_TAG_8)
            {
                buffer.append("MALFORMED_TAG_8@").append(errorPositions[errorCount+1]);
            }
            else if (errorPositions[errorCount] == MALFORMED_TAG_9)
            {
                buffer.append("MALFORMED_TAG_9@").append(errorPositions[errorCount+1]);
            }
            else if (errorPositions[errorCount] == MALFORMED_TAG_35)
            {
                buffer.append("MALFORMED_TAG_35@").append(errorPositions[errorCount+1]);
            }
            else if (errorPositions[errorCount] == MALFORMED_TAG_10)
            {
                buffer.append("MALFORMED_TAG_10@").append(errorPositions[errorCount+1]);
            }
            else if (errorPositions[errorCount] == COLLISION)
            {
                buffer.append("COLLISION@").append(errorPositions[errorCount+1]);
            }
            else if (errorPositions[errorCount] == NON_FOLLOWING_RLE_TAG)
            {
                buffer.append("NON_FOLLOWING_RLE_TAG@").append(errorPositions[errorCount+1]);
            }
            else if (errorPositions[errorCount] == BAD_LENGTH_RLE_TAG)
            {
                buffer.append("BAD_LENGTH_RLE_TAG@").append(errorPositions[errorCount+1]);
            }

            if (errorCount + 2 < errorPositions.length)
            {
                buffer.append(" ");
            }
        }

        return buffer.toString();
    }
}