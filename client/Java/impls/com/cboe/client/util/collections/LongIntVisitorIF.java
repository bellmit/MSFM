package com.cboe.client.util.collections;

/**
 * LongIntVisitorIF
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED BY VELOCITY TEMPLATE ENGINE FROM /vobs/dte/client/generator/DV_XYZVisitor.java (KEY_TYPE=long, VALUE_TYPE=int)
 *
 */

public interface LongIntVisitorIF
{
    public static final int ABORT    = 0;
    public static final int CONTINUE = 1;
    public static final int SKIP     = 2;

    public int visit(long key, int value);
}
