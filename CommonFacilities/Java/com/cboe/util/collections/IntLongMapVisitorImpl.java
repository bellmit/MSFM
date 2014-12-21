package com.cboe.util.collections;

/**
 * IntLongMapVisitorImpl.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapVisitorImpl.template (Int/int, Long/long)
 *
 */

public class IntLongMapVisitorImpl implements IntLongMapVisitor
{
    public IntLongMapVisitor beforeVisit(Object arg)
    {
        return this;
    }

    public IntLongMapVisitor afterVisit(Object arg)
    {
        return this;
    }

    public int visit(int key, long value)
    {
        return ABORT;
    }
}

