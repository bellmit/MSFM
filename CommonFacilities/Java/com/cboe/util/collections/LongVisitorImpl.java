package com.cboe.util.collections;

/**
 * LongVisitorImpl.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM TypedVisitorImpl.template (Long/long)
 *
 */

public class LongVisitorImpl implements LongVisitor
{
    public LongVisitor beforeVisit(Object arg)
    {
        return this;
    }

    public LongVisitor afterVisit(Object arg)
    {
        return this;
    }

    public int visit(long key)
    {
        return ABORT;
    }
}

