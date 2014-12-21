package com.cboe.cfix.interfaces;

/**
 * FixPacketParserIF
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * Can parse and validate a FIX line
 *
 */

import java.io.*;

public interface FixPacketParserIF
{
    public static final int NO_OPTIONS                         = 0;
    public static final int SKIP_GARBAGE_PREFIX                = 1 << 1;
    public static final int RETURN_GARBAGE_PREFIX              = 1 << 2;
    public static final int MAXIMUM_MATCH                      = 1 << 3;

    public void         setFixPacket(FixPacketIF fixPacket);
    public FixPacketIF  parse(InputStream istream, int parseOptions, int debugFlags);
    public void         reset();
}
