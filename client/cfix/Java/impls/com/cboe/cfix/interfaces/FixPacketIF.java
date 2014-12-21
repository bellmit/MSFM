package com.cboe.cfix.interfaces;

/**
 * FixPacketIF.java
 *
 * @author Dmitry Volpyansky
 *
 */

public interface FixPacketIF
{
    public final static int OK                          = 0;
    public final static int TIMED_OUT                   = 1  << 1 ;
    public final static int DISCONNECTED                = 1  << 2 ;
    public final static int NOTCONNECTED                = 1  << 3 ;
    public final static int WRONG_CHECKSUM              = 1  << 4 ;
    public final static int WRONG_LENGTH                = 1  << 5 ;
    public final static int EXCEEDED_LENGTH             = 1  << 6 ;
    public final static int INCOMPLETE                  = 1  << 7 ;
    public final static int MALFORMED_TAG               = 1  << 8 ;
    public final static int MALFORMED_VALUE             = 1  << 9 ;
    public final static int MALFORMED_TAG_8             = 1  << 10;
    public final static int MALFORMED_TAG_9             = 1  << 11;
    public final static int MALFORMED_TAG_35            = 1  << 12;
    public final static int MALFORMED_TAG_10            = 1  << 13;
    public final static int COLLISION                   = 1  << 14;
    public final static int ALL_GARBAGE                 = 1  << 15;
    public final static int NON_FOLLOWING_RLE_TAG       = 1  << 16;
    public final static int BAD_LENGTH_RLE_TAG          = 1  << 17;
    public final static int UNINITIALIZED               = 1  << 31;

    public boolean  isGoodFixMessage();
    public boolean  isBadFixMessage();
    public boolean  isGarbageMessage();
    public boolean  isMessageDisconnected();
    public boolean  isMessageTimedOut();

    public void     startTagPosition(int tag);
    public void     addTagPositionValueStart(int position);
    public void     addTagPositionValueEnd(int position);
    public String   dumpTagPosition(int index);
    public void     allocateArray(int neededLength, int copyLength);
    public void     setResult(int result, int readLength);
    public char     charAt(int index);
    public char     setCharAt(int index, char ch);
    public void     reset();
    public void     reset(int neededLength);
    public int      getExpectedLength();
    public int      getTagPositionLength();
    public long[]   getTagPositionArray();
    public int      getTag(int index);
    public int      getValueOffset(int index);
    public int      getValueLength(int index);
    public char[]   getArray();
    public String   getArrayAsString();
    public int      getReadLength();
}