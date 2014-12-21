package com.cboe.util.collections;

/**
 * LongLongMapVisitorImpl.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapVisitorImpl.template (Long/long, Long/long)
 *
 */

public class LongLongMapVisitorImpl implements LongLongMapVisitor
{
    public LongLongMapVisitor beforeVisit(Object arg)
    {
        return this;
    }

    public LongLongMapVisitor afterVisit(Object arg)
    {
        return this;
    }

    public int visit(long key, long value)
    {
        return ABORT;
    }
}

