package com.cboe.cfix.util;

/**
 * FutureExecutionIF.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 *  Meant to be invoked some time in the future via its run() method
 *
 */

public interface FutureExecutionIF extends Runnable
{
    public static final int MAIN_STATE_READY        = 1 << 1;
    public static final int MAIN_STATE_FINISHED     = 1 << 2;
    public static final int MAIN_STATE_RUNNING      = 1 << 3;

    public static final int CURRENT_STATE_RUNNING   = 1 << 6;
    public static final int CURRENT_STATE_SUCCEEDED = 1 << 7;
    public static final int CURRENT_STATE_ABORTED   = 1 << 8;
    public static final int CURRENT_STATE_UNDO      = 1 << 9;

    public int  getStatusBits();
    public int  cancelAndUndo(long millisToWaitForAcknowlegement);
}
