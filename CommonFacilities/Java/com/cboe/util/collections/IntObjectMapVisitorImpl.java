package com.cboe.util.collections;

/**
 * IntObjectMapVisitorImpl.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapVisitorImpl.template (Int/int, Object/Object)
 *
 */

public class IntObjectMapVisitorImpl implements IntObjectMapVisitor
{
    public IntObjectMapVisitor beforeVisit(Object arg)
    {
        return this;
    }

    public IntObjectMapVisitor afterVisit(Object arg)
    {
        return this;
    }

    public int visit(int key, Object value)
    {
        return ABORT;
    }
}

