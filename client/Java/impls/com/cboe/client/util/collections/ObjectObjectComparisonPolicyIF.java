package com.cboe.client.util.collections;

/**
 * ObjectObjectComparisonPolicyIF.java
 *
 * @author Dmitry Volpyansky
 *
 */

public interface ObjectObjectComparisonPolicyIF
{
    public static final int CONTINUE = 0;
    public static final int ACCEPT   = 1;
    public static final int REJECT   = 2;

    public int compare(Object valueA, Object valueB);
}

