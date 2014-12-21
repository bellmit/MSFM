package com.cboe.util.collections;

/**
 * ControllableVisitor.java
 *
 * @author Dmitry Volpyansky
 *
 */

public interface ControllableVisitor
{
    public static final int ABORT    = 0;
    public static final int CONTINUE = 1;
    public static final int SKIP     = 2;
}

