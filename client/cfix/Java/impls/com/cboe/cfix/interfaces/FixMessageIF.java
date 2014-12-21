package com.cboe.cfix.interfaces;

/**
 * FixMessageIF.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.util.*;

import com.cboe.cfix.util.*;
import com.cboe.client.util.*;

public interface FixMessageIF extends FixMessageWriterVisitorIF, PrettyPrintWriterVisitorIF, Cloneable
{
    public static final int USER_DEFINED_TAGS_START   = 5000;
    public static final int VALIDATE_UNUSED_FIELDS    = BitHelper.INT_SET_BIT_1;
    public static final int VALIDATE_ONLY_USED_FIELDS = BitHelper.INT_SET_BIT_2;
    public static final int STOP_ON_FIRST_ERROR       = BitHelper.INT_SET_BIT_3;

    public String           getMsgType();
    public char[]           getMsgTypeAsChars();
    public String           getMsgTypeName();
    public PackedIntArrayIF build(FixPacketIF fixPacket, PackedIntArrayIF foundErrors, int validationFlags, int debugFlags);
    public PackedIntArrayIF validate(FixPacketIF fixPacket, PackedIntArrayIF foundErrors, int validationFlags, int debugFlags);
    public int              getMsgSeqNum();
    public String           getSenderCompID();
    public String           getTargetCompID();
    public Date             getSendingTime();
    public boolean          isPossDup();
    public Object           clone() throws CloneNotSupportedException;
}